package earth.terrarium.adastra.client.gui.widget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class EnergyBarWidget extends Gui {

    public static final int WIDTH = 13;
    public static final int HEIGHT = 46;

    private static final int TOP_COLOR = 0xFF5BD4E8;
    private static final int BOTTOM_COLOR = 0xFF1D5776;

    private final ResourceLocation texture;

    public EnergyBarWidget(ResourceLocation texture) {
        this.texture = texture;
    }

    public void render(Minecraft minecraft, int x, int y, int energy, int capacity) {
        int fill = getScaled(energy, capacity, HEIGHT);
        if (fill > 0) {
            drawGradientRect(x + 3, y + HEIGHT - fill, x + WIDTH - 3, y + HEIGHT, TOP_COLOR, BOTTOM_COLOR);
        }

        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        minecraft.getTextureManager().bindTexture(texture);
        drawModalRectWithCustomSizedTexture(x, y, 0.0f, 0.0f, WIDTH, HEIGHT, WIDTH, HEIGHT);
    }

    public boolean isMouseOver(int mouseX, int mouseY, int x, int y) {
        return mouseX >= x && mouseX < x + WIDTH && mouseY >= y && mouseY < y + HEIGHT;
    }

    private int getScaled(int value, int max, int size) {
        if (max <= 0 || value <= 0) {
            return 0;
        }
        return Math.max(1, Math.min(size, value * size / max));
    }
}
