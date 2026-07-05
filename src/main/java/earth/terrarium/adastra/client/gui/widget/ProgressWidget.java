package earth.terrarium.adastra.client.gui.widget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class ProgressWidget extends Gui {

    private final int width;
    private final int height;
    private final ResourceLocation texture;
    private final boolean vertical;

    public ProgressWidget(int width, int height, ResourceLocation texture, boolean vertical) {
        this.width = width;
        this.height = height;
        this.texture = texture;
        this.vertical = vertical;
    }

    public void render(Minecraft minecraft, int x, int y, int progress, int maxProgress) {
        int fill = getScaled(progress, maxProgress, vertical ? height : width);
        if (fill <= 0) {
            return;
        }

        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        minecraft.getTextureManager().bindTexture(texture);
        if (vertical) {
            int offset = height - fill;
            drawModalRectWithCustomSizedTexture(x, y + offset, 0.0f, offset, width, fill, width, height);
        } else {
            drawModalRectWithCustomSizedTexture(x, y, 0.0f, 0.0f, fill, height, width, height);
        }
    }

    public boolean isMouseOver(int mouseX, int mouseY, int x, int y) {
        return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
    }

    private int getScaled(int value, int max, int size) {
        if (max <= 0 || value <= 0) {
            return 0;
        }
        return Math.max(1, Math.min(size, value * size / max));
    }
}
