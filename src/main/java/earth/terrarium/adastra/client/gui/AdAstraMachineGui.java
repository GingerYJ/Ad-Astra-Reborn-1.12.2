package earth.terrarium.adastra.client.gui;

import earth.terrarium.adastra.Reference;
import earth.terrarium.adastra.common.container.AdAstraMachineContainer;
import earth.terrarium.adastra.common.registry.ModGuiIds;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class AdAstraMachineGui extends GuiContainer {

    private static final ResourceLocation FALLBACK_TEXTURE = new ResourceLocation(Reference.MOD_ID, "textures/gui/container/compressor.png");
    private static final ResourceLocation IRON_SLOT = new ResourceLocation(Reference.MOD_ID, "textures/gui/container/slots/iron.png");
    private static final ResourceLocation STEEL_SLOT = new ResourceLocation(Reference.MOD_ID, "textures/gui/container/slots/steel.png");
    private static final ResourceLocation CRYO_SLOT = new ResourceLocation(Reference.MOD_ID, "textures/gui/container/slots/cryo.png");
    private static final ResourceLocation ENERGY_BAR = new ResourceLocation(Reference.MOD_ID, "textures/gui/sprites/energy_bar.png");
    private static final ResourceLocation FLUID_BAR = new ResourceLocation(Reference.MOD_ID, "textures/gui/sprites/fluid_bar.png");
    private static final ResourceLocation ARROW = new ResourceLocation(Reference.MOD_ID, "textures/gui/sprites/arrow.png");
    private static final ResourceLocation FIRE = new ResourceLocation(Reference.MOD_ID, "textures/gui/sprites/fire.png");
    private static final ResourceLocation HAMMER = new ResourceLocation(Reference.MOD_ID, "textures/gui/sprites/hammer.png");
    private static final ResourceLocation SNOWFLAKE = new ResourceLocation(Reference.MOD_ID, "textures/gui/sprites/snowflake.png");
    private static final ResourceLocation FURNACE_OVERLAY = new ResourceLocation(Reference.MOD_ID, "textures/gui/sprites/etrionic_blast_furnace_overlay.png");
    private static final ResourceLocation SUN = new ResourceLocation(Reference.MOD_ID, "textures/gui/sprites/sun.png");
    private static final ResourceLocation SLIDER = new ResourceLocation(Reference.MOD_ID, "textures/gui/sprites/slider.png");

    private static final int ENERGY_WIDTH = 13;
    private static final int ENERGY_HEIGHT = 46;
    private static final int FLUID_WIDTH = 12;
    private static final int FLUID_HEIGHT = 46;
    private static final int SOLAR_POWER = 16;
    private static final int EARTH_GRAVITY = 10;
    private static final int MAX_GRAVITY = 20;

    private final AdAstraMachineContainer container;
    private final ResourceLocation texture;
    private final ResourceLocation slotTexture;
    private final int guiId;

    public AdAstraMachineGui(InventoryPlayer playerInventory, AdAstraMachineContainer container) {
        super(container);
        this.container = container;
        AdAstraMachineContainer.Layout layout = container.getLayout();
        this.xSize = layout.getWidth();
        this.ySize = layout.getHeight();
        this.guiId = AdAstraMachineContainer.idFor(container.getMachine());
        this.texture = getTexture(layout);
        this.slotTexture = getSlotTexture(guiId);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        renderHoveredTooltips(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        fontRenderer.drawString(container.getMachine().getDisplayName().getUnformattedText(), 8, getTitleY(), 0x2A262B);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        mc.getTextureManager().bindTexture(texture);
        int x = getGuiLeft();
        int y = getGuiTop();
        drawModalRectWithCustomSizedTexture(x, y, 0.0f, 0.0f, xSize, ySize, xSize, ySize);

        drawSlotFrames(x, y);
        drawMachineVisuals(x, y);
    }

    private ResourceLocation getTexture(AdAstraMachineContainer.Layout layout) {
        if (guiId == ModGuiIds.ENERGIZER) {
            return FALLBACK_TEXTURE;
        }
        return new ResourceLocation(Reference.MOD_ID, "textures/gui/container/" + layout.getTextureName() + ".png");
    }

    private ResourceLocation getSlotTexture(int id) {
        if (id == ModGuiIds.COAL_GENERATOR) {
            return IRON_SLOT;
        }
        if (id == ModGuiIds.CRYO_FREEZER) {
            return CRYO_SLOT;
        }
        return STEEL_SLOT;
    }

    private int getTitleY() {
        switch (guiId) {
            case ModGuiIds.COMPRESSOR:
            case ModGuiIds.ETRIONIC_BLAST_FURNACE:
            case ModGuiIds.GRAVITY_NORMALIZER:
                return 9;
            case ModGuiIds.SOLAR_PANEL:
                return 45;
            default:
                return 6;
        }
    }

    @Override
    public int getGuiLeft() {
        return (width - xSize) / 2;
    }

    @Override
    public int getGuiTop() {
        return (height - ySize) / 2;
    }

    private void drawSlotFrames(int left, int top) {
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        for (int i = 0; i < inventorySlots.inventorySlots.size(); i++) {
            Slot slot = inventorySlots.inventorySlots.get(i);
            ResourceLocation frame = getFrameTexture(i);
            if (frame == null) {
                continue;
            }
            mc.getTextureManager().bindTexture(frame);
            drawModalRectWithCustomSizedTexture(left + slot.xPos - 1, top + slot.yPos - 1, 0.0f, 0.0f, 18, 18, 18, 18);
        }
    }

    private ResourceLocation getFrameTexture(int slotIndex) {
        if (slotIndex < container.getMachineSlotCount()) {
            if (guiId == ModGuiIds.CRYO_FREEZER && slotIndex == 1) {
                return CRYO_SLOT;
            }
            return slotTexture;
        }
        return slotTexture;
    }

    private void drawMachineVisuals(int left, int top) {
        drawEnergyForMachine(left, top);
        drawFluidsForMachine(left, top);
        drawProgressForMachine(left, top);
        drawStatusForMachine(left, top);
    }

    private void drawEnergyForMachine(int left, int top) {
        BarSpec energy = getEnergyBarSpec();
        if (energy != null) {
            drawEnergyBar(left + energy.x, top + energy.y, field(0), field(1));
        }
    }

    private BarSpec getEnergyBarSpec() {
        switch (guiId) {
            case ModGuiIds.COAL_GENERATOR:
                return new BarSpec(146, 32);
            case ModGuiIds.COMPRESSOR:
                return new BarSpec(150, 42);
            case ModGuiIds.ETRIONIC_BLAST_FURNACE:
                return new BarSpec(152, 35);
            case ModGuiIds.FUEL_REFINERY:
            case ModGuiIds.OXYGEN_LOADER:
                return new BarSpec(150, 22);
            case ModGuiIds.SOLAR_PANEL:
                return new BarSpec(108, 69);
            case ModGuiIds.WATER_PUMP:
                return new BarSpec(146, 30);
            case ModGuiIds.ENERGIZER:
                return new BarSpec(150, 42);
            case ModGuiIds.CRYO_FREEZER:
                return new BarSpec(149, 27);
            case ModGuiIds.GRAVITY_NORMALIZER:
                return new BarSpec(151, 39);
            default:
                return null;
        }
    }

    private void drawFluidsForMachine(int left, int top) {
        switch (guiId) {
            case ModGuiIds.FUEL_REFINERY:
                drawFluidBar(left + 43, top + 22, field(2), field(3), 0x7A5A36);
                drawFluidBar(left + 100, top + 22, field(4), field(5), 0xD59A2A);
                break;
            case ModGuiIds.OXYGEN_LOADER:
                drawFluidBar(left + 43, top + 22, field(2), field(3), 0x5AA8E8);
                drawFluidBar(left + 100, top + 22, field(4), field(5), 0x7FD7F7);
                break;
            case ModGuiIds.WATER_PUMP:
                drawFluidBar(left + 81, top + 31, field(2), field(3), 0x2F7EEA);
                break;
            case ModGuiIds.CRYO_FREEZER:
                drawFluidBar(left + 86, top + 38, field(2), field(3), 0x8BE7FF);
                break;
            default:
                break;
        }
    }

    private void drawProgressForMachine(int left, int top) {
        switch (guiId) {
            case ModGuiIds.COAL_GENERATOR:
                drawVerticalProgress(left + 78, top + 54, 15, 15, field(4), field(5), FIRE);
                break;
            case ModGuiIds.COMPRESSOR:
                drawHorizontalProgress(left + 72, top + 59, 15, 16, field(4), field(5), HAMMER);
                break;
            case ModGuiIds.ETRIONIC_BLAST_FURNACE:
                drawHorizontalProgress(left + 75, top + 50, 20, 12, field(4), field(5), ARROW);
                if (field(5) > 0) {
                    GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
                    mc.getTextureManager().bindTexture(FURNACE_OVERLAY);
                    drawModalRectWithCustomSizedTexture(left + 30, top + 51, 0.0f, 0.0f, 32, 43, 32, 43);
                }
                break;
            case ModGuiIds.CRYO_FREEZER:
                drawHorizontalProgress(left + 54, top + 71, 13, 13, field(4), field(5), SNOWFLAKE);
                break;
            default:
                break;
        }
    }

    private void drawStatusForMachine(int left, int top) {
        switch (guiId) {
            case ModGuiIds.SOLAR_PANEL:
                drawSolarPanelStatus(left, top);
                break;
            case ModGuiIds.ETRIONIC_BLAST_FURNACE:
                drawModeLabel(left + 27, top + 82);
                break;
            case ModGuiIds.GRAVITY_NORMALIZER:
                drawGravityStatus(left, top);
                break;
            case ModGuiIds.ENERGIZER:
                drawCenteredSmallText(I18n.format("tile.ad_astra.energizer.name"), left + xSize / 2, top + 26, 0x8CF5F5, 0x32506E);
                break;
            default:
                break;
        }
    }

    private void drawSolarPanelStatus(int left, int top) {
        boolean full = field(1) > 0 && field(0) >= field(1);
        int generation = full ? 0 : SOLAR_POWER;
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        mc.getTextureManager().bindTexture(SUN);
        drawModalRectWithCustomSizedTexture(left + 35, top + 59, 0.0f, 0.0f, 21, 21, 21, 21);
        fontRenderer.drawString(I18n.format("tooltip.ad_astra.energy_per_tick", generation), left + 27, top + 9, 0x68D975);
        fontRenderer.drawString(I18n.format("tooltip.ad_astra.max_generation", SOLAR_POWER), left + 27, top + 19, 0x68D975);
    }

    private void drawGravityStatus(int left, int top) {
        float targetGravity = field(9) / 1000.0f;
        String gravity = I18n.format("tooltip.ad_astra.gravity_amount", targetGravity * EARTH_GRAVITY, (float) MAX_GRAVITY);
        drawCenteredSmallText(gravity, left + 78, top + 32, 0x8CF5F5, 0x32506E);
        drawShadowedSmallText(I18n.format("tooltip.ad_astra.energy_per_tick", field(8)), left + 45, top + 82, 0x8CF5F5, 0x32506E);
        drawShadowedSmallText(I18n.format("tooltip.ad_astra.blocks_distributed", field(6), field(7)), left + 45, top + 93, 0x8CF5F5, 0x32506E);
        drawGravitySlider(left + 25, top + 52, targetGravity);
    }

    private void drawGravitySlider(int x, int y, float targetGravity) {
        int sliderX = x + Math.round(clamp(targetGravity, 0.0f, 2.0f) / 2.0f * 103.0f);
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        mc.getTextureManager().bindTexture(SLIDER);
        drawModalRectWithCustomSizedTexture(sliderX, y - 5, 0.0f, 0.0f, 5, 21, 5, 21);
    }

    private void drawModeLabel(int x, int y) {
        String modeKey = field(6) == 1
            ? "tooltip.ad_astra.etrionic_blast_furnace.mode.blasting"
            : "tooltip.ad_astra.etrionic_blast_furnace.mode.alloying";
        drawShadowedSmallText(I18n.format("tooltip.ad_astra.etrionic_blast_furnace.mode", I18n.format(modeKey)), x, y, 0x8CF5F5, 0x32506E);
    }

    private void drawEnergyBar(int x, int y, int energy, int capacity) {
        int fill = getScaled(energy, capacity, ENERGY_HEIGHT);
        drawInsetFill(x + 3, y + ENERGY_HEIGHT - fill, ENERGY_WIDTH - 6, fill, 0xFF5BD4E8, 0xFF1D5776);
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        mc.getTextureManager().bindTexture(ENERGY_BAR);
        drawModalRectWithCustomSizedTexture(x, y, 0.0f, 0.0f, ENERGY_WIDTH, ENERGY_HEIGHT, ENERGY_WIDTH, ENERGY_HEIGHT);
    }

    private void drawFluidBar(int x, int y, int amount, int capacity, int color) {
        int fill = getScaled(amount, capacity, FLUID_HEIGHT);
        int dark = darken(color);
        drawInsetFill(x + 2, y + FLUID_HEIGHT - fill, FLUID_WIDTH - 4, fill, 0xFF000000 | color, 0xFF000000 | dark);
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        mc.getTextureManager().bindTexture(FLUID_BAR);
        drawModalRectWithCustomSizedTexture(x, y, 0.0f, 0.0f, FLUID_WIDTH, FLUID_HEIGHT, FLUID_WIDTH, FLUID_HEIGHT);
    }

    private void drawInsetFill(int x, int y, int width, int height, int topColor, int bottomColor) {
        if (height <= 0) {
            return;
        }
        drawGradientRect(x, y, x + width, y + height, topColor, bottomColor);
    }

    private void drawHorizontalProgress(int x, int y, int barWidth, int barHeight, int progress, int maxProgress, ResourceLocation sprite) {
        int fill = getScaled(progress, maxProgress, barWidth);
        if (fill <= 0) {
            return;
        }
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        mc.getTextureManager().bindTexture(sprite);
        drawModalRectWithCustomSizedTexture(x, y, 0.0f, 0.0f, fill, barHeight, barWidth, barHeight);
    }

    private void drawVerticalProgress(int x, int y, int barWidth, int barHeight, int progress, int maxProgress, ResourceLocation sprite) {
        int fill = getScaled(progress, maxProgress, barHeight);
        if (fill <= 0) {
            return;
        }
        int offset = barHeight - fill;
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        mc.getTextureManager().bindTexture(sprite);
        drawModalRectWithCustomSizedTexture(x, y + offset, 0.0f, offset, barWidth, fill, barWidth, barHeight);
    }

    private void drawCenteredSmallText(String text, int x, int y, int color, int shadowColor) {
        int textX = x - fontRenderer.getStringWidth(text) / 2;
        drawShadowedSmallText(text, textX, y, color, shadowColor);
    }

    private void drawShadowedSmallText(String text, int x, int y, int color, int shadowColor) {
        fontRenderer.drawString(text, x - 1, y + 1, shadowColor);
        fontRenderer.drawString(text, x, y, color);
    }

    private void renderHoveredTooltips(int mouseX, int mouseY) {
        List<String> tooltip = getTooltip(mouseX, mouseY);
        if (!tooltip.isEmpty()) {
            drawHoveringText(tooltip, mouseX, mouseY);
        }
    }

    private List<String> getTooltip(int mouseX, int mouseY) {
        List<String> tooltip = new ArrayList<>();
        int left = getGuiLeft();
        int top = getGuiTop();

        BarSpec energy = getEnergyBarSpec();
        if (energy != null && isMouseIn(mouseX, mouseY, left + energy.x, top + energy.y, ENERGY_WIDTH, ENERGY_HEIGHT)) {
            tooltip.add(formatEnergy(field(0), field(1)));
            return tooltip;
        }

        addFluidTooltip(tooltip, mouseX, mouseY, left, top);
        if (!tooltip.isEmpty()) {
            return tooltip;
        }

        addProgressTooltip(tooltip, mouseX, mouseY, left, top);
        if (!tooltip.isEmpty()) {
            return tooltip;
        }

        addStatusTooltip(tooltip, mouseX, mouseY, left, top);
        return tooltip;
    }

    private void addFluidTooltip(List<String> tooltip, int mouseX, int mouseY, int left, int top) {
        switch (guiId) {
            case ModGuiIds.FUEL_REFINERY:
                addFluidTooltip(tooltip, mouseX, mouseY, left + 43, top + 22, I18n.format("side_config.ad_astra.input_fluid"), field(2), field(3));
                addFluidTooltip(tooltip, mouseX, mouseY, left + 100, top + 22, I18n.format("side_config.ad_astra.output_fluid"), field(4), field(5));
                break;
            case ModGuiIds.OXYGEN_LOADER:
                addFluidTooltip(tooltip, mouseX, mouseY, left + 43, top + 22, I18n.format("side_config.ad_astra.input_fluid"), field(2), field(3));
                addFluidTooltip(tooltip, mouseX, mouseY, left + 100, top + 22, I18n.format("side_config.ad_astra.output_fluid"), field(4), field(5));
                break;
            case ModGuiIds.WATER_PUMP:
                addFluidTooltip(tooltip, mouseX, mouseY, left + 81, top + 31, I18n.format("side_config.ad_astra.fluid"), field(2), field(3));
                break;
            case ModGuiIds.CRYO_FREEZER:
                addFluidTooltip(tooltip, mouseX, mouseY, left + 86, top + 38, I18n.format("side_config.ad_astra.fluid"), field(2), field(3));
                break;
            default:
                break;
        }
    }

    private void addFluidTooltip(List<String> tooltip, int mouseX, int mouseY, int x, int y, String label, int amount, int capacity) {
        if (tooltip.isEmpty() && isMouseIn(mouseX, mouseY, x, y, FLUID_WIDTH, FLUID_HEIGHT)) {
            tooltip.add(label);
            tooltip.add(formatMillibuckets(amount, capacity));
        }
    }

    private void addProgressTooltip(List<String> tooltip, int mouseX, int mouseY, int left, int top) {
        switch (guiId) {
            case ModGuiIds.COAL_GENERATOR:
                addProgressTooltip(tooltip, mouseX, mouseY, left + 78, top + 54, 15, 15, field(4), field(5));
                break;
            case ModGuiIds.COMPRESSOR:
                addProgressTooltip(tooltip, mouseX, mouseY, left + 72, top + 59, 15, 16, field(4), field(5));
                break;
            case ModGuiIds.ETRIONIC_BLAST_FURNACE:
                addProgressTooltip(tooltip, mouseX, mouseY, left + 75, top + 50, 20, 12, field(4), field(5));
                break;
            case ModGuiIds.CRYO_FREEZER:
                addProgressTooltip(tooltip, mouseX, mouseY, left + 54, top + 71, 13, 13, field(4), field(5));
                break;
            default:
                break;
        }
    }

    private void addProgressTooltip(List<String> tooltip, int mouseX, int mouseY, int x, int y, int width, int height, int progress, int maxProgress) {
        if (isMouseIn(mouseX, mouseY, x, y, width, height)) {
            tooltip.add(I18n.format("tooltip.ad_astra.progress", progress, maxProgress));
        }
    }

    private void addStatusTooltip(List<String> tooltip, int mouseX, int mouseY, int left, int top) {
        if (guiId == ModGuiIds.GRAVITY_NORMALIZER && isMouseIn(mouseX, mouseY, left + 25, top + 52, 108, 11)) {
            tooltip.add(I18n.format("tooltip.ad_astra.gravity_amount", field(9) / 1000.0f * EARTH_GRAVITY, (float) MAX_GRAVITY));
        } else if (guiId == ModGuiIds.ETRIONIC_BLAST_FURNACE && isMouseIn(mouseX, mouseY, left + 23, top + 79, 45, 19)) {
            String modeKey = field(6) == 1
                ? "tooltip.ad_astra.etrionic_blast_furnace.mode.blasting"
                : "tooltip.ad_astra.etrionic_blast_furnace.mode.alloying";
            tooltip.add(I18n.format("tooltip.ad_astra.etrionic_blast_furnace.mode", I18n.format(modeKey)));
        }
    }

    private int field(int id) {
        return container.getSyncedField(id);
    }

    private int getScaled(int amount, int capacity, int scale) {
        if (amount <= 0 || capacity <= 0) {
            return 0;
        }
        return Math.max(1, Math.min(scale, (int) ((long) amount * scale / capacity)));
    }

    private int darken(int color) {
        int red = (color >> 16) & 255;
        int green = (color >> 8) & 255;
        int blue = color & 255;
        return (red * 55 / 100) << 16 | (green * 55 / 100) << 8 | blue * 55 / 100;
    }

    private boolean isMouseIn(int mouseX, int mouseY, int x, int y, int width, int height) {
        return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
    }

    private float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }

    private String formatEnergy(int energy, int capacity) {
        return energy + " / " + capacity + " FE";
    }

    private String formatMillibuckets(int amount, int capacity) {
        return amount + " / " + capacity + " mB";
    }

    private static final class BarSpec {
        private final int x;
        private final int y;

        private BarSpec(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }
}
