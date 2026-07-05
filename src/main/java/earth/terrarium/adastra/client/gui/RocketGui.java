package earth.terrarium.adastra.client.gui;

import earth.terrarium.adastra.Reference;
import earth.terrarium.adastra.common.menus.vehicles.RocketMenu;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

/**
 * GUI for Rocket inventory and fuel display.
 */
public class RocketGui extends GuiContainer {

    private static final ResourceLocation TEXTURE = new ResourceLocation(Reference.MOD_ID, "textures/gui/container/rocket.png");

    private final RocketMenu container;

    public RocketGui(InventoryPlayer playerInventory, RocketMenu container) {
        super(container);
        this.container = container;
        this.xSize = 177;
        this.ySize = 174;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(TEXTURE);
        int left = (this.width - this.xSize) / 2;
        int top = (this.height - this.ySize) / 2;
        this.drawModalRectWithCustomSizedTexture(left - 8, top, 0.0f, 0.0f, this.xSize, this.ySize, this.xSize, this.ySize);

        // Draw fuel bar
        drawFuelBar(left, top);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        String title = I18n.format("entity.ad_astra.tier_" + container.getRocket().getRocketTier() + "_rocket");
        this.fontRenderer.drawString(title, -3, 6, 0x2a262b);
        this.fontRenderer.drawString(I18n.format("container.inventory"), -3, this.ySize - 94, 0x2a262b);

        // Draw fuel info
        // Fuel bar hover tooltip
        if (mouseX >= 43 && mouseX <= 55 && mouseY >= 24 && mouseY <= 70) {
            int fuel = container.getRocket().getFluidFuelAmount();
            int maxFuel = container.getRocket().getFluidFuelCapacity();
            this.drawHoveringText("Fuel: " + fuel + " / " + maxFuel + " mB", mouseX, mouseY);
        }
    }

    /**
     * Draw the fuel bar indicator.
     */
    private void drawFuelBar(int left, int top) {
        int fuelAmount = container.getRocket().getFluidFuelAmount();
        int fuelCapacity = container.getRocket().getFluidFuelCapacity();

        if (fuelCapacity > 0) {
            int fuelHeight = (int) (46.0F * fuelAmount / fuelCapacity);
            int x = left + 43;
            int y = top + 24 + (46 - fuelHeight);

            // Draw fuel (simple colored rectangle)
            drawRect(x, y, x + 12, y + fuelHeight, 0xFFFFAA00); // Orange fuel color
        }
    }
}
