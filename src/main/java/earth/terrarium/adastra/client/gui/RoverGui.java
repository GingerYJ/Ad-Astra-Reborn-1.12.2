package earth.terrarium.adastra.client.gui;

import earth.terrarium.adastra.Reference;
import earth.terrarium.adastra.common.menus.vehicles.RoverMenu;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

import java.io.IOException;

/**
 * GUI for Rover inventory and fuel display.
 */
public class RoverGui extends GuiContainer {

    private static final int RADIO_BUTTON = 20;
    private static final ResourceLocation TEXTURE = new ResourceLocation(Reference.MOD_ID, "textures/gui/container/rover.png");

    private final RoverMenu container;

    public RoverGui(InventoryPlayer playerInventory, RoverMenu container) {
        super(container);
        this.container = container;
        this.xSize = 177;
        this.ySize = 181;
    }

    @Override
    public void initGui() {
        super.initGui();
        this.buttonList.add(new GuiButton(RADIO_BUTTON, this.guiLeft + 126, this.guiTop + 4, 42, 18,
            I18n.format("gui.ad_astra.rover.radio_short")));
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == RADIO_BUTTON) {
            this.mc.displayGuiScreen(new RoverRadioGui(container.getRover(), this));
            return;
        }
        super.actionPerformed(button);
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
        String title = I18n.format("entity.ad_astra.tier_1_rover");
        this.fontRenderer.drawString(title, -3, 6, 0x2a262b);
        this.fontRenderer.drawString(I18n.format("container.inventory"), -3, this.ySize - 94, 0x2a262b);

        // Fuel bar hover tooltip
        if (mouseX >= 43 && mouseX <= 55 && mouseY >= 26 && mouseY <= 72) {
            int fuel = container.getRover().getFluidFuelAmount();
            int maxFuel = container.getRover().getFluidFuelCapacity();
            this.drawHoveringText("Fuel: " + fuel + " / " + maxFuel + " mB", mouseX, mouseY);
        }
    }

    /**
     * Draw the fuel bar indicator.
     */
    private void drawFuelBar(int left, int top) {
        int fuelAmount = container.getRover().getFluidFuelAmount();
        int fuelCapacity = container.getRover().getFluidFuelCapacity();

        if (fuelCapacity > 0) {
            int fuelHeight = (int) (46.0F * fuelAmount / fuelCapacity);
            int x = left + 43;
            int y = top + 26 + (46 - fuelHeight);

            // Draw fuel (simple colored rectangle)
            drawRect(x, y, x + 12, y + fuelHeight, 0xFFFFAA00); // Orange fuel color
        }
    }
}
