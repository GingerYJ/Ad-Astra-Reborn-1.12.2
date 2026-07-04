package earth.terrarium.adastra.client.gui;

import earth.terrarium.adastra.common.network.NetworkHandler;
import earth.terrarium.adastra.common.network.packet.PacketSetFlagUrl;
import earth.terrarium.adastra.common.tile.FlagTileEntity;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.input.Keyboard;

import java.io.IOException;
import java.util.regex.Pattern;

public class FlagUrlGui extends GuiScreen {

    private static final Pattern URL_REGEX = Pattern.compile("^https://i\\.imgur\\.com/(\\w+)\\.(png|jpeg|jpg|webp)$");
    private static final int PANEL_WIDTH = 260;
    private static final int PANEL_HEIGHT = 106;
    private static final int CONFIRM_BUTTON = 0;
    private static final int CLEAR_BUTTON = 1;
    private static final int DONE_BUTTON = 2;

    private final BlockPos pos;
    private GuiTextField urlField;

    public FlagUrlGui(BlockPos pos) {
        this.pos = pos;
    }

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);
        buttonList.clear();

        int left = (width - PANEL_WIDTH) / 2;
        int top = (height - PANEL_HEIGHT) / 2;
        urlField = new GuiTextField(0, fontRenderer, left + 12, top + 36, PANEL_WIDTH - 24, 20);
        urlField.setMaxStringLength(128);
        urlField.setText(getCurrentUrl());
        urlField.setFocused(true);

        buttonList.add(new GuiButton(CONFIRM_BUTTON, left + 12, top + 68, 74, 20, I18n.format("text.ad_astra.text.confirm")));
        buttonList.add(new GuiButton(CLEAR_BUTTON, left + 93, top + 68, 74, 20, I18n.format("gui.ad_astra.radio.clear")));
        buttonList.add(new GuiButton(DONE_BUTTON, left + 174, top + 68, 74, 20, I18n.format("gui.done")));
        updateButtons();
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
    }

    @Override
    public void updateScreen() {
        urlField.updateCursorCounter();
        updateButtons();
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == CONFIRM_BUTTON) {
            sendUpdate(urlField.getText().trim());
            mc.displayGuiScreen(null);
        } else if (button.id == CLEAR_BUTTON) {
            sendUpdate("");
            mc.displayGuiScreen(null);
        } else if (button.id == DONE_BUTTON) {
            mc.displayGuiScreen(null);
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == Keyboard.KEY_RETURN || keyCode == Keyboard.KEY_NUMPADENTER) {
            if (isValidUrl(urlField.getText())) {
                sendUpdate(urlField.getText().trim());
                mc.displayGuiScreen(null);
            }
            return;
        }
        if (urlField.textboxKeyTyped(typedChar, keyCode)) {
            updateButtons();
            return;
        }
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        urlField.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        int left = (width - PANEL_WIDTH) / 2;
        int top = (height - PANEL_HEIGHT) / 2;

        drawRect(left, top, left + PANEL_WIDTH, top + PANEL_HEIGHT, 0xE0202530);
        drawRect(left + 1, top + 1, left + PANEL_WIDTH - 1, top + PANEL_HEIGHT - 1, 0xE03A4252);
        drawString(fontRenderer, I18n.format("text.ad_astra.text.flag_url"), left + 12, top + 18, 0xC9E6FF);
        urlField.drawTextBox();
        if (!urlField.getText().trim().isEmpty() && !isValidUrl(urlField.getText())) {
            drawString(fontRenderer, "https://i.imgur.com/name.png", left + 12, top + 58, 0xFF7777);
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    private void updateButtons() {
        if (urlField == null) {
            return;
        }
        String text = urlField.getText().trim();
        for (GuiButton button : buttonList) {
            if (button.id == CONFIRM_BUTTON) {
                button.enabled = isValidUrl(text);
            } else if (button.id == CLEAR_BUTTON) {
                button.enabled = !getCurrentUrl().isEmpty() || !text.isEmpty();
            }
        }
    }

    private void sendUpdate(String url) {
        NetworkHandler.CHANNEL.sendToServer(new PacketSetFlagUrl(pos, url));
    }

    private boolean isValidUrl(String url) {
        return URL_REGEX.matcher(url.trim()).matches();
    }

    private String getCurrentUrl() {
        TileEntity tile = mc.world == null ? null : mc.world.getTileEntity(pos);
        return tile instanceof FlagTileEntity ? ((FlagTileEntity) tile).getFlagUrl() : "";
    }
}
