package earth.terrarium.adastra.client.gui.widget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class ImageButtonWidget extends Gui {

    private final int width;
    private final int height;

    public ImageButtonWidget(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public void render(Minecraft minecraft, ResourceLocation texture, int x, int y) {
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        minecraft.getTextureManager().bindTexture(texture);
        drawModalRectWithCustomSizedTexture(x, y, 0.0f, 0.0f, width, height, width, height);
    }

    public boolean isMouseOver(int mouseX, int mouseY, int x, int y) {
        return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
    }
}
