package earth.terrarium.adastra.client.gui.widget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class FluidBarWidget extends Gui {

    public static final int WIDTH = 12;
    public static final int HEIGHT = 46;

    private final ResourceLocation texture;

    public FluidBarWidget(ResourceLocation texture) {
        this.texture = texture;
    }

    public void render(Minecraft minecraft, int x, int y, int amount, int capacity, int color) {
        int fill = getScaled(amount, capacity, HEIGHT);
        if (fill > 0) {
            int dark = darken(color);
            drawGradientRect(x + 2, y + HEIGHT - fill, x + WIDTH - 2, y + HEIGHT, 0xFF000000 | color, 0xFF000000 | dark);
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

    private int darken(int color) {
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = color & 0xFF;
        r = Math.max(0, (int) (r * 0.65f));
        g = Math.max(0, (int) (g * 0.65f));
        b = Math.max(0, (int) (b * 0.65f));
        return (r << 16) | (g << 8) | b;
    }
}
