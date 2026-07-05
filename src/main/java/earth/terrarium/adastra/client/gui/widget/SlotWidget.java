package earth.terrarium.adastra.client.gui.widget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.inventory.Slot;
import net.minecraft.util.ResourceLocation;

public class SlotWidget extends Gui {

    private static final int FRAME_SIZE = 18;
    private static final int ICON_SIZE = 16;

    public void renderFrame(Minecraft minecraft, ResourceLocation texture, int left, int top, Slot slot) {
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        minecraft.getTextureManager().bindTexture(texture);
        drawModalRectWithCustomSizedTexture(left + slot.xPos - 1, top + slot.yPos - 1, 0.0f, 0.0f, FRAME_SIZE, FRAME_SIZE, FRAME_SIZE, FRAME_SIZE);
    }

    public void renderIcon(Minecraft minecraft, ResourceLocation texture, int left, int top, Slot slot) {
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        minecraft.getTextureManager().bindTexture(texture);
        drawModalRectWithCustomSizedTexture(left + slot.xPos, top + slot.yPos, 0.0f, 0.0f, ICON_SIZE, ICON_SIZE, ICON_SIZE, ICON_SIZE);
    }
}
