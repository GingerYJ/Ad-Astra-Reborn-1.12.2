package earth.terrarium.adastra.client.gui;

import earth.terrarium.adastra.Reference;
import earth.terrarium.adastra.common.menus.vehicles.LanderMenu;
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

    private final LanderMenu container;

    public LanderGui(InventoryPlayer playerInventory, LanderMenu container) {
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
        this.fontRenderer.drawString(title, -3, 6, 0x2a262b);
        this.fontRenderer.drawString(I18n.format("container.inventory"), -3, this.ySize - 94, 0x2a262b);

        // Draw descent controls help text
        this.fontRenderer.drawString("W: Thrust", 8, 75, 0x666666);
        this.fontRenderer.drawString("A/D: Steer", 8, 84, 0x666666);

        // Fuel bar hover tooltip
        if (mouseX >= 7 && mouseX <= 20 && mouseY >= 18 && mouseY <= 50) {
            int fuel = container.getLander().getFluidFuelAmount();
            int maxFuel = container.getLander().getFluidFuelCapacity();
            this.drawHoveringText("Fuel: " + fuel + " / " + maxFuel + " mB", mouseX, mouseY);
        }

        // Thruster status hover tooltip
        if (mouseX >= 155 && mouseX <= 165 && mouseY >= 20 && mouseY <= 30) {
            String status = container.getLander().isThrusting() ? "Thrusters: Active" : "Thrusters: Inactive";
            this.drawHoveringText(status, mouseX, mouseY);
        }

        // Descent speed warning
        double landingSpeed = container.getLander().getLastLandingSpeed();
        if (landingSpeed > 0.5D && landingSpeed < 1.5D) {
            this.fontRenderer.drawString("Warning: High Speed!", 55, 75, 0xFFAA00);
        } else if (landingSpeed >= 1.5D) {
            this.fontRenderer.drawString("DANGER: CRASH IMMINENT!", 40, 75, 0xFF0000);
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
