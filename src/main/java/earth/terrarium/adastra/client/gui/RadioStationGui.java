package earth.terrarium.adastra.client.gui;

import earth.terrarium.adastra.common.network.NetworkHandler;
import earth.terrarium.adastra.common.network.packet.PacketSetRadioStation;
import earth.terrarium.adastra.common.tile.RadioTileEntity;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.input.Keyboard;

import java.io.IOException;

public class RadioStationGui extends GuiScreen {

    private static final int PANEL_WIDTH = 260;
    private static final int PANEL_HEIGHT = 128;
    private static final int SAVE_BUTTON = 0;
    private static final int CLEAR_BUTTON = 1;
    private static final int TOGGLE_BUTTON = 2;
    private static final int DONE_BUTTON = 3;

    private final BlockPos pos;
    private final String initialStation;
    private boolean playing;

    private GuiTextField stationField;

    public RadioStationGui(BlockPos pos, String station, boolean playing) {
        this.pos = pos;
        this.initialStation = RadioTileEntity.normalizeStation(station);
        this.playing = playing && !this.initialStation.isEmpty();
    }

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);
        buttonList.clear();

        int left = (width - PANEL_WIDTH) / 2;
        int top = (height - PANEL_HEIGHT) / 2;
        stationField = new GuiTextField(0, fontRenderer, left + 14, top + 42, PANEL_WIDTH - 28, 20);
        stationField.setMaxStringLength(RadioTileEntity.MAX_STATION_LENGTH);
        stationField.setText(getCurrentStation());
        stationField.setFocused(true);
        syncPlayingFromTile();

        buttonList.add(new GuiButton(SAVE_BUTTON, left + 14, top + 74, 54, 20, I18n.format("gui.ad_astra.radio.save")));
        buttonList.add(new GuiButton(CLEAR_BUTTON, left + 74, top + 74, 54, 20, I18n.format("gui.ad_astra.radio.clear")));
        buttonList.add(new GuiButton(TOGGLE_BUTTON, left + 134, top + 74, 54, 20, getToggleLabel()));
        buttonList.add(new GuiButton(DONE_BUTTON, left + 194, top + 74, 52, 20, I18n.format("gui.done")));
        updateButtons();
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
    }

    @Override
    public void updateScreen() {
        syncPlayingFromTile();
        stationField.updateCursorCounter();
        updateButtons();
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == SAVE_BUTTON) {
            sendUpdate(stationField.getText(), playing);
        } else if (button.id == CLEAR_BUTTON) {
            stationField.setText("");
            sendUpdate("", false);
        } else if (button.id == TOGGLE_BUTTON) {
            sendUpdate(stationField.getText(), !playing);
        } else if (button.id == DONE_BUTTON) {
            mc.displayGuiScreen(null);
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == Keyboard.KEY_RETURN || keyCode == Keyboard.KEY_NUMPADENTER) {
            sendUpdate(stationField.getText(), playing);
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
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        int left = (width - PANEL_WIDTH) / 2;
        int top = (height - PANEL_HEIGHT) / 2;

        drawRect(left, top, left + PANEL_WIDTH, top + PANEL_HEIGHT, 0xE0202530);
        drawRect(left + 1, top + 1, left + PANEL_WIDTH - 1, top + PANEL_HEIGHT - 1, 0xE03A4252);
        drawCenteredString(fontRenderer, I18n.format("tile.ad_astra.radio.name"), width / 2, top + 10, 0xFFFFFF);
        drawString(fontRenderer, I18n.format("gui.ad_astra.radio.station_url"), left + 14, top + 30, 0xC9E6FF);
        stationField.drawTextBox();

        String station = RadioTileEntity.normalizeStation(stationField.getText());
        String status = station.isEmpty() ? I18n.format("text.ad_astra.radio.none") : getStatusLabel();
        drawString(fontRenderer, I18n.format("gui.ad_astra.radio.status", status), left + 14, top + 104, 0xD8DEE9);

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    private void sendUpdate(String station, boolean playing) {
        String normalized = RadioTileEntity.normalizeStation(station);
        this.playing = playing && !normalized.isEmpty();
        if (!stationField.getText().equals(normalized)) {
            stationField.setText(normalized);
        }
        NetworkHandler.CHANNEL.sendToServer(new PacketSetRadioStation(pos, normalized, this.playing));
        updateButtons();
    }

    private String getCurrentStation() {
        RadioTileEntity radio = getRadioTile();
        if (radio != null) {
            return radio.getStation();
        }
        return initialStation;
    }

    private void syncPlayingFromTile() {
        RadioTileEntity radio = getRadioTile();
        if (radio != null && stationField != null && RadioTileEntity.normalizeStation(stationField.getText()).equals(radio.getStation())) {
            playing = radio.isPlaying();
        }
    }

    private RadioTileEntity getRadioTile() {
        TileEntity tile = mc.world == null ? null : mc.world.getTileEntity(pos);
        return tile instanceof RadioTileEntity ? (RadioTileEntity) tile : null;
    }

    private void updateButtons() {
        String station = stationField == null ? "" : RadioTileEntity.normalizeStation(stationField.getText());
        for (GuiButton button : buttonList) {
            if (button.id == TOGGLE_BUTTON) {
                button.enabled = !station.isEmpty();
                button.displayString = getToggleLabel();
            } else if (button.id == CLEAR_BUTTON) {
                button.enabled = !station.isEmpty() || playing;
            }
        }
    }

    private String getToggleLabel() {
        return I18n.format(playing ? "gui.ad_astra.radio.stop" : "gui.ad_astra.radio.play");
    }

    private String getStatusLabel() {
        return I18n.format(playing ? "gui.ad_astra.radio.playing" : "gui.ad_astra.radio.stopped");
    }
}
