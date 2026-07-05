package earth.terrarium.adastra.client.gui.widget;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;

public class SidedConfigWidget extends Gui {

    public static final int WIDTH = 162;
    public static final int HEIGHT = 96;
    private static final int X = 8;
    private static final int Y = 18;
    private static final int BUTTON_SIZE = 18;

    public int getLeft(int guiLeft) {
        return guiLeft + X;
    }

    public int getTop(int guiTop) {
        return guiTop + Y;
    }

    public void renderBackground(int left, int top) {
        GlStateManager.disableDepth();
        drawGradientRect(left, top, left + WIDTH, top + HEIGHT, 0xFF2E2A31, 0xFF4F4753);
        drawRect(left, top, left + WIDTH, top + 1, 0xFF8E8290);
        drawRect(left, top + HEIGHT - 1, left + WIDTH, top + HEIGHT, 0xFF1E1A20);
    }

    public void finishRender() {
        GlStateManager.enableDepth();
    }

    public boolean isInside(int mouseX, int mouseY, int left, int top) {
        return mouseX >= left && mouseX < left + WIDTH && mouseY >= top && mouseY < top + HEIGHT;
    }

    public int getResetButtonX(int left) {
        return left + WIDTH - BUTTON_SIZE - 6;
    }

    public int getResetButtonY(int top) {
        return top + 5;
    }

    public int getSideLabelX(int left, int column) {
        return left + 47 + column * BUTTON_SIZE;
    }

    public int getSideLabelY(int top) {
        return top + 21;
    }

    public int getTypeLabelX(int left) {
        return left + 6;
    }

    public int getTypeLabelY(int top, int row) {
        return getModeButtonY(top, row) + 5;
    }

    public int getModeButtonX(int left, int column) {
        return left + 44 + column * BUTTON_SIZE;
    }

    public int getModeButtonY(int top, int row) {
        return top + 34 + row * 20;
    }
}
