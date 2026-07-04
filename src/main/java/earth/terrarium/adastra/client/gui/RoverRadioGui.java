package earth.terrarium.adastra.client.gui;

import earth.terrarium.adastra.common.entities.vehicles.RoverEntity;
import earth.terrarium.adastra.common.network.NetworkHandler;
import earth.terrarium.adastra.common.network.packet.PacketRequestRadioStations;
import earth.terrarium.adastra.common.network.packet.PacketSetRoverRadioStation;
import earth.terrarium.adastra.common.tile.RadioTileEntity;
import earth.terrarium.adastra.common.util.radio.StationInfo;
import earth.terrarium.adastra.client.radio.audio.RadioHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RoverRadioGui extends GuiScreen {

    private static final int PANEL_WIDTH = 320;
    private static final int PANEL_HEIGHT = 236;
    private static final int LIST_ROW_HEIGHT = 18;
    private static final int LIST_HEIGHT = 90;
    private static final int SAVE_BUTTON = 0;
    private static final int STOP_BUTTON = 1;
    private static final int DONE_BUTTON = 2;
    private static final int VOLUME_DOWN_BUTTON = 3;
    private static final int VOLUME_UP_BUTTON = 4;
    private static List<StationInfo> cachedStations = Collections.emptyList();

    private final RoverEntity rover;
    private final GuiScreen parent;
    private GuiTextField stationField;
    private int selectedStationIndex = -1;
    private int listScroll;

    public RoverRadioGui(RoverEntity rover, GuiScreen parent) {
        this.rover = rover;
        this.parent = parent;
    }

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);
        buttonList.clear();

        int left = (width - PANEL_WIDTH) / 2;
        int top = (height - PANEL_HEIGHT) / 2;
        stationField = new GuiTextField(0, fontRenderer, left + 14, top + 42, PANEL_WIDTH - 28, 20);
        stationField.setMaxStringLength(RadioTileEntity.MAX_STATION_LENGTH);
        stationField.setText(rover.getRadioUrl());
        stationField.setFocused(true);
        selectedStationIndex = findStationIndex(stationField.getText());
        clampScroll();

        buttonList.add(new GuiButton(SAVE_BUTTON, left + 14, top + 74, 58, 20, I18n.format("gui.ad_astra.radio.play")));
        buttonList.add(new GuiButton(STOP_BUTTON, left + 78, top + 74, 58, 20, I18n.format("gui.ad_astra.radio.stop")));
        buttonList.add(new GuiButton(VOLUME_DOWN_BUTTON, left + 142, top + 74, 28, 20, "-"));
        buttonList.add(new GuiButton(VOLUME_UP_BUTTON, left + 176, top + 74, 28, 20, "+"));
        buttonList.add(new GuiButton(DONE_BUTTON, left + 210, top + 74, 96, 20, I18n.format("gui.done")));
        updateButtons();
        NetworkHandler.CHANNEL.sendToServer(new PacketRequestRadioStations());
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
    }

    @Override
    public void updateScreen() {
        stationField.updateCursorCounter();
        updateButtons();
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == SAVE_BUTTON) {
            sendUpdate(stationField.getText());
        } else if (button.id == STOP_BUTTON) {
            stationField.setText("");
            sendUpdate("");
        } else if (button.id == VOLUME_DOWN_BUTTON) {
            RadioHandler.adjustRadioVolume(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT) ? -10 : -1);
        } else if (button.id == VOLUME_UP_BUTTON) {
            RadioHandler.adjustRadioVolume(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT) ? 10 : 1);
        } else if (button.id == DONE_BUTTON) {
            mc.displayGuiScreen(parent);
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == Keyboard.KEY_RETURN || keyCode == Keyboard.KEY_NUMPADENTER) {
            sendUpdate(stationField.getText());
            return;
        }
        if (stationField.textboxKeyTyped(typedChar, keyCode)) {
            updateButtons();
            return;
        }
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        stationField.mouseClicked(mouseX, mouseY, mouseButton);
        if (mouseButton == 0 && handleStationClick(mouseX, mouseY)) {
            updateButtons();
        }
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        int wheel = Mouse.getEventDWheel();
        if (wheel != 0) {
            listScroll += wheel < 0 ? 1 : -1;
            clampScroll();
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        int left = (width - PANEL_WIDTH) / 2;
        int top = (height - PANEL_HEIGHT) / 2;

        drawRect(left, top, left + PANEL_WIDTH, top + PANEL_HEIGHT, 0xE0202530);
        drawRect(left + 1, top + 1, left + PANEL_WIDTH - 1, top + PANEL_HEIGHT - 1, 0xE03A4252);
        drawCenteredString(fontRenderer, I18n.format("gui.ad_astra.rover.radio"), width / 2, top + 10, 0xFFFFFF);
        drawString(fontRenderer, I18n.format("gui.ad_astra.radio.station_url"), left + 14, top + 30, 0xC9E6FF);
        stationField.drawTextBox();
        drawStationList(left, top);

        String current = RadioTileEntity.normalizeStation(rover.getRadioUrl());
        String status = current.isEmpty() ? I18n.format("text.ad_astra.radio.none") : current;
        status = status + "  " + I18n.format("gui.ad_astra.radio.volume", RadioHandler.getRadioVolume());
        drawString(fontRenderer, fontRenderer.trimStringToWidth(status, PANEL_WIDTH - 28), left + 14, top + 214, 0xD8DEE9);

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    private void sendUpdate(String station) {
        String normalized = RadioTileEntity.normalizeStation(station);
        if (!stationField.getText().equals(normalized)) {
            stationField.setText(normalized);
        }
        NetworkHandler.CHANNEL.sendToServer(new PacketSetRoverRadioStation(rover.getEntityId(), normalized));
        updateButtons();
    }

    private void updateButtons() {
        if (stationField == null) {
            return;
        }
        String station = RadioTileEntity.normalizeStation(stationField.getText());
        for (GuiButton button : buttonList) {
            if (button.id == SAVE_BUTTON) {
                button.enabled = !station.isEmpty();
            } else if (button.id == STOP_BUTTON) {
                button.enabled = !station.isEmpty() || !RadioTileEntity.normalizeStation(rover.getRadioUrl()).isEmpty();
            } else if (button.id == VOLUME_DOWN_BUTTON) {
                button.enabled = RadioHandler.getRadioVolume() > 0;
            } else if (button.id == VOLUME_UP_BUTTON) {
                button.enabled = RadioHandler.getRadioVolume() < 100;
            }
        }
    }

    private void drawStationList(int left, int top) {
        int listLeft = left + 14;
        int listTop = getListTop(top);
        int listRight = left + PANEL_WIDTH - 14;
        int listBottom = listTop + LIST_HEIGHT;

        drawString(fontRenderer, I18n.format("gui.ad_astra.radio.presets"), listLeft, top + 104, 0xC9E6FF);
        drawRect(listLeft, listTop, listRight, listBottom, 0x80202530);
        drawRect(listLeft + 1, listTop + 1, listRight - 1, listBottom - 1, 0x80303846);

        if (cachedStations.isEmpty()) {
            drawCenteredString(fontRenderer, I18n.format("gui.ad_astra.radio.no_presets"),
                (listLeft + listRight) / 2, listTop + 40, 0xAAB6C4);
            return;
        }

        int rows = visibleRows();
        int max = Math.min(cachedStations.size(), listScroll + rows);
        for (int i = listScroll; i < max; i++) {
            StationInfo station = cachedStations.get(i);
            int rowTop = listTop + (i - listScroll) * LIST_ROW_HEIGHT;
            int color = i == selectedStationIndex ? 0xA0446A8C : 0x403A4252;
            drawRect(listLeft + 2, rowTop + 1, listRight - 2, rowTop + LIST_ROW_HEIGHT - 1, color);
            drawString(fontRenderer, fontRenderer.trimStringToWidth(station.getTitle(), listRight - listLeft - 10),
                listLeft + 6, rowTop + 2, 0xFFFFFF);
            drawString(fontRenderer, fontRenderer.trimStringToWidth(station.getName(), listRight - listLeft - 10),
                listLeft + 6, rowTop + 11, 0xAAB6C4);
        }
    }

    private boolean handleStationClick(int mouseX, int mouseY) {
        int left = (width - PANEL_WIDTH) / 2;
        int top = (height - PANEL_HEIGHT) / 2;
        int listLeft = left + 14;
        int listTop = getListTop(top);
        int listRight = left + PANEL_WIDTH - 14;
        int listBottom = listTop + LIST_HEIGHT;
        if (mouseX < listLeft || mouseX > listRight || mouseY < listTop || mouseY > listBottom) {
            return false;
        }

        int index = listScroll + (mouseY - listTop) / LIST_ROW_HEIGHT;
        if (index < 0 || index >= cachedStations.size()) {
            return false;
        }

        selectedStationIndex = index;
        stationField.setText(cachedStations.get(index).getUrl());
        return true;
    }

    private int getListTop(int top) {
        return top + 116;
    }

    private int visibleRows() {
        return LIST_HEIGHT / LIST_ROW_HEIGHT;
    }

    private void clampScroll() {
        int maxScroll = Math.max(0, cachedStations.size() - visibleRows());
        if (listScroll < 0) {
            listScroll = 0;
        } else if (listScroll > maxScroll) {
            listScroll = maxScroll;
        }
    }

    private int findStationIndex(String url) {
        String normalized = RadioTileEntity.normalizeStation(url);
        for (int i = 0; i < cachedStations.size(); i++) {
            if (cachedStations.get(i).getUrl().equals(normalized)) {
                return i;
            }
        }
        return -1;
    }

    public static void handleStationUpdates(List<StationInfo> stations) {
        cachedStations = stations == null ? Collections.emptyList() : new ArrayList<>(stations);
        Minecraft minecraft = Minecraft.getMinecraft();
        if (minecraft.currentScreen instanceof RoverRadioGui) {
            RoverRadioGui gui = (RoverRadioGui) minecraft.currentScreen;
            gui.selectedStationIndex = gui.findStationIndex(gui.stationField == null ? "" : gui.stationField.getText());
            gui.clampScroll();
        }
    }
}
