package earth.terrarium.adastra.client.gui.widget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class OptionsBarWidget extends Gui {

    public static final int HEIGHT = 30;
    private static final int BUTTON_SIZE = 18;
    private static final int PADDING = 6;
    private static final int SPACING = 3;

    private final ResourceLocation texture;

    public OptionsBarWidget(ResourceLocation texture) {
        this.texture = texture;
    }

    public void render(Minecraft minecraft, int x, int y, int width) {
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        minecraft.getTextureManager().bindTexture(texture);
        drawScaledCustomSizeModalRect(x, y, 0.0f, 0.0f, HEIGHT, HEIGHT, width, HEIGHT, HEIGHT, HEIGHT);
    }

    public int getWidth(int buttons) {
        return PADDING * 2 + buttons * BUTTON_SIZE + Math.max(0, buttons - 1) * SPACING;
    }

    public int getButtonOffsetX(int index) {
        return PADDING + index * (BUTTON_SIZE + SPACING);
    }

    public int getButtonOffsetY() {
        return PADDING;
    }
}
