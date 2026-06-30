package earth.terrarium.adastra.client.gui;

import earth.terrarium.adastra.Reference;
import earth.terrarium.adastra.common.container.AdAstraMachineContainer;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

public class AdAstraMachineGui extends GuiContainer {

    private final AdAstraMachineContainer container;
    private final ResourceLocation texture;

    public AdAstraMachineGui(InventoryPlayer playerInventory, AdAstraMachineContainer container) {
        super(container);
        this.container = container;
        AdAstraMachineContainer.Layout layout = container.getLayout();
        this.xSize = layout.getWidth();
        this.ySize = layout.getHeight();
        this.texture = new ResourceLocation(Reference.MOD_ID, "textures/gui/container/" + layout.getTextureName() + ".png");
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        fontRenderer.drawString(container.getMachine().getDisplayName().getUnformattedText(), 8, 6, 0x404040);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        mc.getTextureManager().bindTexture(texture);
        int x = (width - xSize) / 2;
        int y = (height - ySize) / 2;
        drawModalRectWithCustomSizedTexture(x, y, 0.0f, 0.0f, xSize, ySize, xSize, ySize);
    }
}
