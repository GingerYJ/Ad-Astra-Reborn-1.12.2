package earth.terrarium.adastra.client.gui;

import earth.terrarium.adastra.Reference;
import earth.terrarium.adastra.common.container.LanderContainer;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

/**
 * GUI for Lander inventory, fuel, and descent controls display.
 */
public class LanderGui extends GuiContainer {

    private static final ResourceLocation TEXTURE = new ResourceLocation(Reference.MOD_ID, "textures/gui/container/lander.png");

    private final LanderContainer container;

    public LanderGui(InventoryPlayer playerInventory, LanderContainer container) {
        super(container);
        this.container = container;
        this.xSize = 176;
        this.ySize = 166;
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
        this.drawTexturedModalRect(left, top, 0, 0, this.xSize, this.ySize);

        // Draw fuel bar
        drawFuelBar(left, top);

        // Draw thruster status indicator
        if (container.getLander().isThrusting()) {
            // Draw thruster active indicator (green circle)
            drawRect(left + 155, top + 20, left + 165, top + 30, 0xFF00FF00);
        } else {
            // Draw thruster inactive indicator (red circle)
            drawRect(left + 155, top + 20, left + 165, top + 30, 0xFFFF0000);
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        String title = I18n.format("entity.ad_astra.lander.name");
        this.fontRenderer.drawString(title, this.xSize / 2 - this.fontRenderer.getStringWidth(title) / 2, 6, 4210752);
        this.fontRenderer.drawString(I18n.format("container.inventory"), 8, this.ySize - 96 + 2, 4210752);

        // Draw descent controls help text
        this.fontRenderer.drawString("W: Thrust", 8, 55, 0x666666);
        this.fontRenderer.drawString("A/D: Steer", 8, 64, 0x666666);

        int left = (this.width - this.xSize) / 2;
        int top = (this.height - this.ySize) / 2;
        int mouseXRel = mouseX - left;
        int mouseYRel = mouseY - top;

        // Fuel bar hover tooltip
        if (mouseXRel >= 7 && mouseXRel <= 20 && mouseYRel >= 18 && mouseYRel <= 50) {
            int fuel = container.getLander().getFluidFuelAmount();
            int maxFuel = container.getLander().getFluidFuelCapacity();
            this.drawHoveringText("Fuel: " + fuel + " / " + maxFuel + " mB", mouseXRel, mouseYRel);
        }

        // Thruster status hover tooltip
        if (mouseXRel >= 155 && mouseXRel <= 165 && mouseYRel >= 20 && mouseYRel <= 30) {
            String status = container.getLander().isThrusting() ? "Thrusters: Active" : "Thrusters: Inactive";
            this.drawHoveringText(status, mouseXRel, mouseYRel);
        }

        // Descent speed warning
        double landingSpeed = container.getLander().getLastLandingSpeed();
        if (landingSpeed > 0.5D && landingSpeed < 1.5D) {
            this.fontRenderer.drawString("Warning: High Speed!", 55, 55, 0xFFAA00);
        } else if (landingSpeed >= 1.5D) {
            this.fontRenderer.drawString("DANGER: CRASH IMMINENT!", 40, 55, 0xFF0000);
        }
    }

    /**
     * Draw the fuel bar indicator.
     */
    private void drawFuelBar(int left, int top) {
        int fuelAmount = container.getLander().getFluidFuelAmount();
        int fuelCapacity = container.getLander().getFluidFuelCapacity();

        if (fuelCapacity > 0) {
            int fuelHeight = (int) (32.0F * fuelAmount / fuelCapacity);
            int x = left + 7;
            int y = top + 18 + (32 - fuelHeight);

            // Draw fuel (simple colored rectangle)
            drawRect(x, y, x + 13, y + fuelHeight, 0xFFFFAA00); // Orange fuel color
        }
    }
}
