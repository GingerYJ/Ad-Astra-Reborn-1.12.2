package earth.terrarium.adastra.client.gui;

import earth.terrarium.adastra.Reference;
import earth.terrarium.adastra.api.planets.PlanetApi;
import earth.terrarium.adastra.client.gui.widget.EnergyBarWidget;
import earth.terrarium.adastra.client.gui.widget.FluidBarWidget;
import earth.terrarium.adastra.client.gui.widget.GravitySliderWidget;
import earth.terrarium.adastra.client.gui.widget.ImageButtonWidget;
import earth.terrarium.adastra.client.gui.widget.OptionsBarWidget;
import earth.terrarium.adastra.client.gui.widget.ProgressWidget;
import earth.terrarium.adastra.client.gui.widget.SidedConfigWidget;
import earth.terrarium.adastra.client.gui.widget.SlotWidget;
import earth.terrarium.adastra.client.gui.machine.CoalGeneratorGui;
import earth.terrarium.adastra.client.gui.machine.CompressorGui;
import earth.terrarium.adastra.client.gui.machine.CryoFreezerGui;
import earth.terrarium.adastra.client.gui.machine.EtrionicBlastFurnaceGui;
import earth.terrarium.adastra.client.gui.machine.FuelRefineryGui;
import earth.terrarium.adastra.client.gui.machine.GravityNormalizerGui;
import earth.terrarium.adastra.client.gui.machine.NasaWorkbenchGui;
import earth.terrarium.adastra.client.gui.machine.OxygenDistributorGui;
import earth.terrarium.adastra.client.gui.machine.OxygenLoaderGui;
import earth.terrarium.adastra.client.gui.machine.SolarPanelGui;
import earth.terrarium.adastra.client.gui.machine.WaterPumpGui;
import earth.terrarium.adastra.client.render.MachineAreaRenderState;
import earth.terrarium.adastra.common.container.AdAstraMachineContainer;
import earth.terrarium.adastra.common.container.slots.ImageSlot;
import earth.terrarium.adastra.common.menus.base.MachineMenu;
import earth.terrarium.adastra.common.network.NetworkHandler;
import earth.terrarium.adastra.common.network.packet.PacketClearFluidTank;
import earth.terrarium.adastra.common.network.packet.PacketResetSideConfig;
import earth.terrarium.adastra.common.network.packet.PacketSetRedstoneControl;
import earth.terrarium.adastra.common.network.packet.PacketSetSideConfig;
import earth.terrarium.adastra.common.registry.ModGuiIds;
import earth.terrarium.adastra.common.tile.AdAstraEnergyStorage;
import earth.terrarium.adastra.common.tile.AdAstraRedstoneControl;
import earth.terrarium.adastra.common.tile.AdAstraSideMode;
import earth.terrarium.adastra.common.tile.AdAstraMachineTileEntity.SideConfigType;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class AdAstraMachineGui extends GuiContainer {

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
    private static final ResourceLocation OPTIONS_BAR = new ResourceLocation(Reference.MOD_ID, "textures/gui/sprites/options_bar.png");
    private static final ResourceLocation SETTINGS_BUTTON = new ResourceLocation(Reference.MOD_ID, "textures/gui/sprites/buttons/settings_button.png");
    private static final ResourceLocation RESET_BUTTON = new ResourceLocation(Reference.MOD_ID, "textures/gui/sprites/buttons/reset_button.png");
    private static final ResourceLocation SHOW_BUTTON = new ResourceLocation(Reference.MOD_ID, "textures/gui/sprites/buttons/show_button.png");
    private static final ResourceLocation HIDE_BUTTON = new ResourceLocation(Reference.MOD_ID, "textures/gui/sprites/buttons/hide_button.png");
    private static final ResourceLocation CRAFTING_BUTTON = new ResourceLocation(Reference.MOD_ID, "textures/gui/sprites/buttons/crafting_button.png");
    private static final ResourceLocation FURNACE_BUTTON = new ResourceLocation(Reference.MOD_ID, "textures/gui/sprites/buttons/furnace_button.png");
    private static final ResourceLocation SIDE_NONE = new ResourceLocation(Reference.MOD_ID, "textures/gui/sprites/buttons/side_config/none.png");
    private static final ResourceLocation SIDE_PUSH = new ResourceLocation(Reference.MOD_ID, "textures/gui/sprites/buttons/side_config/push.png");
    private static final ResourceLocation SIDE_PULL = new ResourceLocation(Reference.MOD_ID, "textures/gui/sprites/buttons/side_config/pull.png");
    private static final ResourceLocation SIDE_PUSH_PULL = new ResourceLocation(Reference.MOD_ID, "textures/gui/sprites/buttons/side_config/push_pull.png");

    // Redstone control button textures
    private static final ResourceLocation REDSTONE_ALWAYS_ON = new ResourceLocation(Reference.MOD_ID, "textures/gui/sprites/buttons/redstone/always_on_button.png");
    private static final ResourceLocation REDSTONE_ALWAYS_ON_HIGHLIGHTED = new ResourceLocation(Reference.MOD_ID, "textures/gui/sprites/buttons/redstone/always_on_button_highlighted.png");
    private static final ResourceLocation REDSTONE_ON_WHEN_POWERED = new ResourceLocation(Reference.MOD_ID, "textures/gui/sprites/buttons/redstone/on_when_powered_button.png");
    private static final ResourceLocation REDSTONE_ON_WHEN_POWERED_HIGHLIGHTED = new ResourceLocation(Reference.MOD_ID, "textures/gui/sprites/buttons/redstone/on_when_powered_button_highlighted.png");
    private static final ResourceLocation REDSTONE_ON_WHEN_NOT_POWERED = new ResourceLocation(Reference.MOD_ID, "textures/gui/sprites/buttons/redstone/on_when_not_powered_button.png");
    private static final ResourceLocation REDSTONE_ON_WHEN_NOT_POWERED_HIGHLIGHTED = new ResourceLocation(Reference.MOD_ID, "textures/gui/sprites/buttons/redstone/on_when_not_powered_button_highlighted.png");
    private static final ResourceLocation REDSTONE_NEVER = new ResourceLocation(Reference.MOD_ID, "textures/gui/sprites/buttons/redstone/never_on_button.png");
    private static final ResourceLocation REDSTONE_NEVER_HIGHLIGHTED = new ResourceLocation(Reference.MOD_ID, "textures/gui/sprites/buttons/redstone/never_on_button_highlighted.png");

    private static final EnergyBarWidget ENERGY_BAR_WIDGET = new EnergyBarWidget(ENERGY_BAR);
    private static final FluidBarWidget FLUID_BAR_WIDGET = new FluidBarWidget(FLUID_BAR);
    private static final ProgressWidget FIRE_PROGRESS_WIDGET = new ProgressWidget(15, 15, FIRE, true);
    private static final ProgressWidget HAMMER_PROGRESS_WIDGET = new ProgressWidget(15, 16, HAMMER, false);
    private static final ProgressWidget FURNACE_PROGRESS_WIDGET = new ProgressWidget(20, 12, ARROW, false);
    private static final ProgressWidget SNOWFLAKE_PROGRESS_WIDGET = new ProgressWidget(13, 13, SNOWFLAKE, false);
    private static final ImageButtonWidget BUTTON_WIDGET = new ImageButtonWidget(18, 18);
    private static final GravitySliderWidget GRAVITY_SLIDER_WIDGET = new GravitySliderWidget(SLIDER);
    private static final OptionsBarWidget OPTIONS_BAR_WIDGET = new OptionsBarWidget(OPTIONS_BAR);
    private static final SidedConfigWidget SIDED_CONFIG_WIDGET = new SidedConfigWidget();
    private static final SlotWidget SLOT_WIDGET = new SlotWidget();
    private static final int EARTH_GRAVITY = 10;
    private static final int MAX_GRAVITY = 20;

    private final MachineMenu<?> container;
    private final ResourceLocation texture;
    private final ResourceLocation slotTexture;
    private final int guiId;
    private boolean sideConfigOpen;
    private int lastEnergy = -1;
    private int energyDifference;

    public AdAstraMachineGui(InventoryPlayer playerInventory, MachineMenu<?> container) {
        super(container);
        this.container = container;
        AdAstraMachineContainer.Layout layout = container.getLayout();
        this.xSize = layout.getWidth();
        this.ySize = layout.getHeight();
        this.guiId = AdAstraMachineContainer.idFor(container.getMachine());
        this.texture = getTexture(layout);
        this.slotTexture = getSlotTexture(guiId);
    }

    public static AdAstraMachineGui create(InventoryPlayer playerInventory, MachineMenu<?> container) {
        switch (AdAstraMachineContainer.idFor(container.getMachine())) {
            case ModGuiIds.COAL_GENERATOR:
                return new CoalGeneratorGui(playerInventory, container);
            case ModGuiIds.COMPRESSOR:
                return new CompressorGui(playerInventory, container);
            case ModGuiIds.ETRIONIC_BLAST_FURNACE:
                return new EtrionicBlastFurnaceGui(playerInventory, container);
            case ModGuiIds.CRYO_FREEZER:
                return new CryoFreezerGui(playerInventory, container);
            case ModGuiIds.FUEL_REFINERY:
                return new FuelRefineryGui(playerInventory, container);
            case ModGuiIds.OXYGEN_LOADER:
                return new OxygenLoaderGui(playerInventory, container);
            case ModGuiIds.WATER_PUMP:
                return new WaterPumpGui(playerInventory, container);
            case ModGuiIds.SOLAR_PANEL:
                return new SolarPanelGui(playerInventory, container);
            case ModGuiIds.GRAVITY_NORMALIZER:
                return new GravityNormalizerGui(playerInventory, container);
            case ModGuiIds.OXYGEN_DISTRIBUTOR:
                return new OxygenDistributorGui(playerInventory, container);
            case ModGuiIds.NASA_WORKBENCH:
                return new NasaWorkbenchGui(playerInventory, container);
            default:
                return new AdAstraMachineGui(playerInventory, container);
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        drawSideConfigPanel(mouseX, mouseY);
        renderHoveredTooltips(mouseX, mouseY);
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        updateEnergyDifference();
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws java.io.IOException {
        int left = getGuiLeft();
        int top = getGuiTop();

        if (sideConfigOpen && handleSideConfigClick(mouseX, mouseY, mouseButton, left, top)) {
            return;
        }

        if (mouseButton == 1 && isShiftKeyDown() && handleClearFluidTankClick(mouseX, mouseY, left, top)) {
            return;
        }

        ButtonSpec leadingButton = getLeadingButtonSpec();
        if (leadingButton != null && BUTTON_WIDGET.isMouseOver(mouseX, mouseY, left + leadingButton.x, top + leadingButton.y)) {
            if (mouseButton == 0) {
                handleLeadingOptionsButton();
            }
            return;
        }

        ButtonSpec settingsButton = getSettingsButtonSpec();
        if (settingsButton != null && BUTTON_WIDGET.isMouseOver(mouseX, mouseY, left + settingsButton.x, top + settingsButton.y)) {
            if (mouseButton == 0) {
                sideConfigOpen = !sideConfigOpen;
            }
            return;
        }

        RedstoneButtonSpec redstoneButton = getRedstoneButtonSpec();
        if (redstoneButton != null && BUTTON_WIDGET.isMouseOver(mouseX, mouseY, left + redstoneButton.x, top + redstoneButton.y)) {
            if (mouseButton == 0) {
                cycleRedstoneControl();
            }
            return;
        }

        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (mouseButton != 0) {
            return;
        }

        if (guiId == ModGuiIds.ETRIONIC_BLAST_FURNACE && isMouseIn(mouseX, mouseY, left + 23, top + 79, 45, 19)) {
            sendFurnaceModeButton();
        } else if (guiId == ModGuiIds.GRAVITY_NORMALIZER) {
            handleGravitySliderClick(mouseX, left);
        }
    }

    private void handleLeadingOptionsButton() {
        if (guiId == ModGuiIds.ETRIONIC_BLAST_FURNACE) {
            sendFurnaceModeButton();
        } else if (guiId == ModGuiIds.GRAVITY_NORMALIZER) {
            MachineAreaRenderState.toggleGravityNormalizer(container.getMachine().getPos());
        } else if (guiId == ModGuiIds.OXYGEN_DISTRIBUTOR) {
            MachineAreaRenderState.toggleOxygenDistributor(container.getMachine().getPos());
        }
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int mouseButton, long timeSinceLastClick) {
        super.mouseClickMove(mouseX, mouseY, mouseButton, timeSinceLastClick);
        if (mouseButton == 0 && guiId == ModGuiIds.GRAVITY_NORMALIZER) {
            handleGravitySliderClick(mouseX, getGuiLeft());
        }
    }

    private void handleGravitySliderClick(int mouseX, int left) {
        int steps = GRAVITY_SLIDER_WIDGET.getStepsFromMouse(mouseX, left + 25);
        if (steps < 0) {
            return;
        }
        sendButton(MachineMenu.GRAVITY_ID_BASE + steps);
    }

    private void updateEnergyDifference() {
        int currentEnergy = field(0);
        if (lastEnergy >= 0) {
            energyDifference = currentEnergy - lastEnergy;
        }
        lastEnergy = currentEnergy;
    }

    private boolean handleClearFluidTankClick(int mouseX, int mouseY, int left, int top) {
        switch (guiId) {
            case ModGuiIds.FUEL_REFINERY:
                return tryClearFluidTank(mouseX, mouseY, left + 43, top + 22, field(2), 0)
                    || tryClearFluidTank(mouseX, mouseY, left + 100, top + 22, field(4), 1);
            case ModGuiIds.OXYGEN_LOADER:
                return tryClearFluidTank(mouseX, mouseY, left + 43, top + 22, field(2), 0)
                    || tryClearFluidTank(mouseX, mouseY, left + 100, top + 22, field(4), 1);
            case ModGuiIds.WATER_PUMP:
                return tryClearFluidTank(mouseX, mouseY, left + 81, top + 31, field(2), 0);
            case ModGuiIds.CRYO_FREEZER:
                return tryClearFluidTank(mouseX, mouseY, left + 86, top + 38, field(2), 0);
            case ModGuiIds.OXYGEN_DISTRIBUTOR:
                return tryClearFluidTank(mouseX, mouseY, left + 51, top + 82, field(2), 0)
                    || tryClearFluidTank(mouseX, mouseY, left + 116, top + 82, field(10), 0);
            default:
                return false;
        }
    }

    private boolean tryClearFluidTank(int mouseX, int mouseY, int x, int y, int amount, int tank) {
        if (amount <= 0 || !FLUID_BAR_WIDGET.isMouseOver(mouseX, mouseY, x, y)) {
            return false;
        }
        NetworkHandler.CHANNEL.sendToServer(new PacketClearFluidTank(container.getMachine().getPos(), tank));
        return true;
    }

    private void sendButton(int id) {
        mc.playerController.sendEnchantPacket(container.windowId, id);
    }

    private void sendFurnaceModeButton() {
        sendButton(isShiftKeyDown()
            ? MachineMenu.PREVIOUS_FURNACE_MODE
            : MachineMenu.TOGGLE_FURNACE_MODE);
    }

    private void cycleRedstoneControl() {
        AdAstraRedstoneControl current = container.getMachine().getRedstoneControl();
        AdAstraRedstoneControl next = getNextRedstoneControl(current, isShiftKeyDown());
        container.getMachine().setRedstoneControl(next);
        NetworkHandler.CHANNEL.sendToServer(new PacketSetRedstoneControl(container.getMachine().getPos(), next));
    }

    private AdAstraRedstoneControl getNextRedstoneControl(AdAstraRedstoneControl current, boolean backwards) {
        switch (current) {
            case ALWAYS_ON:
                return backwards ? AdAstraRedstoneControl.NEVER : AdAstraRedstoneControl.ACTIVE_WITH_SIGNAL;
            case ACTIVE_WITH_SIGNAL:
                return backwards ? AdAstraRedstoneControl.ALWAYS_ON : AdAstraRedstoneControl.ACTIVE_WITHOUT_SIGNAL;
            case ACTIVE_WITHOUT_SIGNAL:
                return backwards ? AdAstraRedstoneControl.ACTIVE_WITH_SIGNAL : AdAstraRedstoneControl.NEVER;
            case NEVER:
            default:
                return backwards ? AdAstraRedstoneControl.ACTIVE_WITHOUT_SIGNAL : AdAstraRedstoneControl.ALWAYS_ON;
        }
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

        drawOptionsBar(x, y, mouseX, mouseY);
        drawSlotFrames(x, y);
        drawSlotIcons(x, y);
        drawMachineVisuals(x, y, mouseX, mouseY);
    }

    private ResourceLocation getTexture(AdAstraMachineContainer.Layout layout) {
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
            case ModGuiIds.OXYGEN_DISTRIBUTOR:
                return 65;
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
        for (int i = 0; i < inventorySlots.inventorySlots.size(); i++) {
            Slot slot = inventorySlots.inventorySlots.get(i);
            ResourceLocation frame = getFrameTexture(i, slot);
            if (frame == null) {
                continue;
            }
            SLOT_WIDGET.renderFrame(mc, frame, left, top, slot);
        }
    }

    private void drawSlotIcons(int left, int top) {
        for (Slot slot : inventorySlots.inventorySlots) {
            if (!(slot instanceof ImageSlot) || slot.getHasStack()) {
                continue;
            }
            SLOT_WIDGET.renderIcon(mc, ((ImageSlot) slot).getIcon(), left, top, slot);
        }
    }

    private void drawOptionsBar(int left, int top, int mouseX, int mouseY) {
        OptionsBarSpec spec = getOptionsBarSpec();
        if (spec == null) {
            return;
        }

        int x = left + spec.x;
        int y = top + spec.y;
        OPTIONS_BAR_WIDGET.render(mc, x, y, spec.width);

        int buttonIndex = 0;
        int buttonY = y + OPTIONS_BAR_WIDGET.getButtonOffsetY();
        if (hasLeadingOptionsButton()) {
            drawOptionsButton(getLeadingOptionsButtonTexture(), x + OPTIONS_BAR_WIDGET.getButtonOffsetX(buttonIndex), buttonY);
            buttonIndex++;
        }

        drawOptionsButton(SETTINGS_BUTTON, x + OPTIONS_BAR_WIDGET.getButtonOffsetX(buttonIndex), buttonY);
    }

    private void drawSideConfigPanel(int mouseX, int mouseY) {
        if (!sideConfigOpen) {
            return;
        }

        int left = SIDED_CONFIG_WIDGET.getLeft(getGuiLeft());
        int top = SIDED_CONFIG_WIDGET.getTop(getGuiTop());
        SIDED_CONFIG_WIDGET.renderBackground(left, top);
        fontRenderer.drawString(I18n.format("side_config.ad_astra.title", ""), left + 6, top + 6, 0xD8D0D8);
        drawOptionsButton(RESET_BUTTON, SIDED_CONFIG_WIDGET.getResetButtonX(left), SIDED_CONFIG_WIDGET.getResetButtonY(top));

        EnumFacing[] sides = getConfigSides();
        for (int i = 0; i < sides.length; i++) {
            fontRenderer.drawString(shortSideName(sides[i]), SIDED_CONFIG_WIDGET.getSideLabelX(left, i), SIDED_CONFIG_WIDGET.getSideLabelY(top), 0xBDB4C2);
        }

        SideConfigType[] types = SideConfigType.values();
        for (int row = 0; row < types.length; row++) {
            int y = SIDED_CONFIG_WIDGET.getModeButtonY(top, row);
            fontRenderer.drawString(getSideConfigTypeLabel(types[row]), SIDED_CONFIG_WIDGET.getTypeLabelX(left), SIDED_CONFIG_WIDGET.getTypeLabelY(top, row), 0xD8D0D8);
            for (int column = 0; column < sides.length; column++) {
                int x = SIDED_CONFIG_WIDGET.getModeButtonX(left, column);
                AdAstraSideMode mode = container.getMachine().getSideMode(sides[column], types[row]);
                drawOptionsButton(getSideModeTexture(mode), x, y);
            }
        }
        SIDED_CONFIG_WIDGET.finishRender();
    }

    private boolean handleSideConfigClick(int mouseX, int mouseY, int mouseButton, int left, int top) {
        if (mouseButton != 0) {
            return false;
        }

        int panelLeft = SIDED_CONFIG_WIDGET.getLeft(left);
        int panelTop = SIDED_CONFIG_WIDGET.getTop(top);
        if (!SIDED_CONFIG_WIDGET.isInside(mouseX, mouseY, panelLeft, panelTop)) {
            sideConfigOpen = false;
            return false;
        }

        if (BUTTON_WIDGET.isMouseOver(mouseX, mouseY, SIDED_CONFIG_WIDGET.getResetButtonX(panelLeft), SIDED_CONFIG_WIDGET.getResetButtonY(panelTop))) {
            resetSideConfig();
            return true;
        }

        EnumFacing[] sides = getConfigSides();
        SideConfigType[] types = SideConfigType.values();
        for (int row = 0; row < types.length; row++) {
            int y = SIDED_CONFIG_WIDGET.getModeButtonY(panelTop, row);
            for (int column = 0; column < sides.length; column++) {
                int x = SIDED_CONFIG_WIDGET.getModeButtonX(panelLeft, column);
                if (BUTTON_WIDGET.isMouseOver(mouseX, mouseY, x, y)) {
                    cycleSideMode(sides[column], types[row]);
                    return true;
                }
            }
        }
        return true;
    }

    private void cycleSideMode(EnumFacing side, SideConfigType type) {
        AdAstraSideMode next = container.getMachine().getSideMode(side, type).next();
        container.getMachine().setSideMode(side, type, next);
        NetworkHandler.CHANNEL.sendToServer(new PacketSetSideConfig(container.getMachine().getPos(), side, type, next));
    }

    private void resetSideConfig() {
        container.getMachine().resetSideModesToDefaults();
        NetworkHandler.CHANNEL.sendToServer(new PacketResetSideConfig(container.getMachine().getPos()));
    }

    private void drawOptionsButton(ResourceLocation texture, int x, int y) {
        BUTTON_WIDGET.render(mc, texture, x, y);
    }

    private ResourceLocation getFrameTexture(int slotIndex, Slot slot) {
        if (slot.xPos >= xSize) {
            return STEEL_SLOT;
        }
        if (slotIndex < container.getMachineSlotCount()) {
            if (guiId == ModGuiIds.CRYO_FREEZER && slotIndex == 1) {
                return CRYO_SLOT;
            }
            return slotTexture;
        }
        return slotTexture;
    }

    private void drawMachineVisuals(int left, int top, int mouseX, int mouseY) {
        drawEnergyForMachine(left, top);
        drawFluidsForMachine(left, top);
        drawProgressForMachine(left, top);
        drawStatusForMachine(left, top);
        drawRedstoneButton(left, top, mouseX, mouseY);
    }

    private void drawEnergyForMachine(int left, int top) {
        BarSpec energy = getEnergyBarSpec();
        if (energy != null) {
            ENERGY_BAR_WIDGET.render(mc, left + energy.x, top + energy.y, field(0), field(1));
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
            case ModGuiIds.OXYGEN_DISTRIBUTOR:
                return new BarSpec(147, 82);
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
            case ModGuiIds.OXYGEN_DISTRIBUTOR:
                drawFluidBar(left + 51, top + 82, field(2), field(3), 0x5AA8E8);
                drawFluidBar(left + 116, top + 82, field(10), field(3), 0x7FD7F7);
                break;
            default:
                break;
        }
    }

    private void drawProgressForMachine(int left, int top) {
        switch (guiId) {
            case ModGuiIds.COAL_GENERATOR:
                FIRE_PROGRESS_WIDGET.render(mc, left + 78, top + 54, field(4), field(5));
                break;
            case ModGuiIds.COMPRESSOR:
                HAMMER_PROGRESS_WIDGET.render(mc, left + 72, top + 59, field(4), field(5));
                break;
            case ModGuiIds.ETRIONIC_BLAST_FURNACE:
                FURNACE_PROGRESS_WIDGET.render(mc, left + 75, top + 50, field(4), field(5));
                if (field(5) > 0) {
                    GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
                    mc.getTextureManager().bindTexture(FURNACE_OVERLAY);
                    drawModalRectWithCustomSizedTexture(left + 30, top + 51, 0.0f, 0.0f, 32, 43, 32, 43);
                }
                break;
            case ModGuiIds.CRYO_FREEZER:
                SNOWFLAKE_PROGRESS_WIDGET.render(mc, left + 54, top + 71, field(4), field(5));
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
                break;
            case ModGuiIds.GRAVITY_NORMALIZER:
                drawGravityStatus(left, top);
                break;
            case ModGuiIds.OXYGEN_DISTRIBUTOR:
                drawOxygenDistributorStatus(left, top);
                break;
            default:
                break;
        }
    }

    private void drawRedstoneButton(int left, int top, int mouseX, int mouseY) {
        RedstoneButtonSpec spec = getRedstoneButtonSpec();
        if (spec == null) {
            return;
        }

        int x = left + spec.x;
        int y = top + spec.y;
        boolean isHovered = BUTTON_WIDGET.isMouseOver(mouseX, mouseY, x, y);

        AdAstraRedstoneControl mode = container.getMachine().getRedstoneControl();
        ResourceLocation texture = getRedstoneButtonTexture(mode, isHovered);

        BUTTON_WIDGET.render(mc, texture, x, y);
    }

    private ResourceLocation getRedstoneButtonTexture(AdAstraRedstoneControl mode, boolean hovered) {
        switch (mode) {
            case ALWAYS_ON:
                return hovered ? REDSTONE_ALWAYS_ON_HIGHLIGHTED : REDSTONE_ALWAYS_ON;
            case ACTIVE_WITH_SIGNAL:
                return hovered ? REDSTONE_ON_WHEN_POWERED_HIGHLIGHTED : REDSTONE_ON_WHEN_POWERED;
            case ACTIVE_WITHOUT_SIGNAL:
                return hovered ? REDSTONE_ON_WHEN_NOT_POWERED_HIGHLIGHTED : REDSTONE_ON_WHEN_NOT_POWERED;
            case NEVER:
            default:
                return hovered ? REDSTONE_NEVER_HIGHLIGHTED : REDSTONE_NEVER;
        }
    }

    private RedstoneButtonSpec getRedstoneButtonSpec() {
        OptionsBarSpec optionsBar = getOptionsBarSpec();
        if (optionsBar == null) {
            return null;
        }

        int index = hasLeadingOptionsButton() ? 2 : 1;
        return new RedstoneButtonSpec(
            optionsBar.x + OPTIONS_BAR_WIDGET.getButtonOffsetX(index),
            optionsBar.y + OPTIONS_BAR_WIDGET.getButtonOffsetY()
        );
    }

    private ButtonSpec getSettingsButtonSpec() {
        OptionsBarSpec optionsBar = getOptionsBarSpec();
        if (optionsBar == null) {
            return null;
        }
        int index = hasLeadingOptionsButton() ? 1 : 0;
        return new ButtonSpec(
            optionsBar.x + OPTIONS_BAR_WIDGET.getButtonOffsetX(index),
            optionsBar.y + OPTIONS_BAR_WIDGET.getButtonOffsetY()
        );
    }

    private ButtonSpec getLeadingButtonSpec() {
        OptionsBarSpec optionsBar = getOptionsBarSpec();
        if (optionsBar == null || !hasLeadingOptionsButton()) {
            return null;
        }
        return new ButtonSpec(optionsBar.x + OPTIONS_BAR_WIDGET.getButtonOffsetX(0), optionsBar.y + OPTIONS_BAR_WIDGET.getButtonOffsetY());
    }

    private OptionsBarSpec getOptionsBarSpec() {
        if (!hasRedstoneOptionsBar()) {
            return null;
        }

        int buttons = 2 + (container.getLayout().hasBatterySlot() ? 1 : 0) + (hasLeadingOptionsButton() ? 1 : 0);
        int width = OPTIONS_BAR_WIDGET.getWidth(buttons);
        if (guiId == ModGuiIds.OXYGEN_DISTRIBUTOR) {
            return new OptionsBarSpec(98, 0, width);
        }
        return new OptionsBarSpec(xSize - width, -OptionsBarWidget.HEIGHT - 2, width);
    }

    private boolean hasRedstoneOptionsBar() {
        return guiId != ModGuiIds.NASA_WORKBENCH;
    }

    private boolean hasLeadingOptionsButton() {
        return guiId == ModGuiIds.ETRIONIC_BLAST_FURNACE
            || guiId == ModGuiIds.GRAVITY_NORMALIZER
            || guiId == ModGuiIds.OXYGEN_DISTRIBUTOR;
    }

    private ResourceLocation getLeadingOptionsButtonTexture() {
        if (guiId == ModGuiIds.ETRIONIC_BLAST_FURNACE) {
            return field(6) == 1 ? FURNACE_BUTTON : CRAFTING_BUTTON;
        }
        if (guiId == ModGuiIds.GRAVITY_NORMALIZER) {
            return MachineAreaRenderState.isShowingGravityNormalizer(container.getMachine().getPos()) ? HIDE_BUTTON : SHOW_BUTTON;
        }
        if (guiId == ModGuiIds.OXYGEN_DISTRIBUTOR) {
            return MachineAreaRenderState.isShowingOxygenDistributor(container.getMachine().getPos()) ? HIDE_BUTTON : SHOW_BUTTON;
        }
        return SHOW_BUTTON;
    }

    private ResourceLocation getSideModeTexture(AdAstraSideMode mode) {
        switch (mode) {
            case PUSH:
                return SIDE_PUSH;
            case PULL:
                return SIDE_PULL;
            case PUSH_PULL:
                return SIDE_PUSH_PULL;
            case NONE:
            default:
                return SIDE_NONE;
        }
    }

    private EnumFacing[] getConfigSides() {
        return new EnumFacing[]{EnumFacing.UP, EnumFacing.DOWN, EnumFacing.NORTH, EnumFacing.SOUTH, EnumFacing.WEST, EnumFacing.EAST};
    }

    private String shortSideName(EnumFacing side) {
        return side.getName().substring(0, 1).toUpperCase();
    }

    private String getSideConfigTypeLabel(SideConfigType type) {
        return I18n.format("side_config.ad_astra.type." + type.getKey().toLowerCase());
    }

    private void drawSolarPanelStatus(int left, int top) {
        boolean full = field(1) > 0 && field(0) >= field(1);
        int solarPower = getSolarPower();
        boolean daytime = isSolarPanelDaytime();
        int generation = daytime && !full ? solarPower : 0;
        if (daytime) {
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            mc.getTextureManager().bindTexture(SUN);
            drawModalRectWithCustomSizedTexture(left + 35, top + 59, 0.0f, 0.0f, 21, 21, 21, 21);
        }
        fontRenderer.drawString(I18n.format("tooltip.ad_astra.energy_per_tick", generation), left + 27, top + 9, 0x68D975);
        fontRenderer.drawString(I18n.format("tooltip.ad_astra.max_generation", solarPower), left + 27, top + 19, 0x68D975);
    }

    private boolean isSolarPanelDaytime() {
        if (mc.world == null || container.getMachine().getPos() == null || !mc.world.provider.hasSkyLight()) {
            return false;
        }
        long dayTime = mc.world.getWorldTime() % 24000L;
        return dayTime <= 12000L && mc.world.canBlockSeeSky(container.getMachine().getPos().up());
    }

    private int getSolarPower() {
        if (mc.world == null) {
            return 10;
        }
        return (int) Math.min(Integer.MAX_VALUE, PlanetApi.API.getSolarPower(mc.world));
    }

    private void drawGravityStatus(int left, int top) {
        float targetGravity = field(9) / 1000.0f;
        String gravity = I18n.format("tooltip.ad_astra.gravity_amount", targetGravity * EARTH_GRAVITY, (float) MAX_GRAVITY);
        drawCenteredSmallText(gravity, left + 78, top + 32, 0x8CF5F5, 0x32506E);
        drawShadowedSmallText(I18n.format("tooltip.ad_astra.energy_per_tick", field(8)), left + 45, top + 82, 0x8CF5F5, 0x32506E);
        drawShadowedSmallText(I18n.format("tooltip.ad_astra.blocks_distributed", field(6), field(7)), left + 45, top + 93, 0x8CF5F5, 0x32506E);
        GRAVITY_SLIDER_WIDGET.render(mc, left + 25, top + 52, targetGravity);
    }

    private void drawOxygenDistributorStatus(int left, int top) {
        fontRenderer.drawString(I18n.format("tooltip.ad_astra.energy_per_tick", field(8)), left + 11, top + 9, 0x68D975);
        fontRenderer.drawString(I18n.format("tooltip.ad_astra.fluid_per_tick", field(9)), left + 11, top + 20, 0x68D975);
        fontRenderer.drawString(I18n.format("tooltip.ad_astra.blocks_distributed", field(6), field(7)), left + 11, top + 31, 0x68D975);
    }

    private void drawModeLabel(int x, int y) {
        String modeKey = field(6) == 1
            ? "tooltip.ad_astra.etrionic_blast_furnace.mode.blasting"
            : "tooltip.ad_astra.etrionic_blast_furnace.mode.alloying";
        drawShadowedSmallText(I18n.format("tooltip.ad_astra.etrionic_blast_furnace.mode", I18n.format(modeKey)), x, y, 0x8CF5F5, 0x32506E);
    }

    private void drawFluidBar(int x, int y, int amount, int capacity, int color) {
        FLUID_BAR_WIDGET.render(mc, x, y, amount, capacity, color);
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
        if (energy != null && ENERGY_BAR_WIDGET.isMouseOver(mouseX, mouseY, left + energy.x, top + energy.y)) {
            addEnergyTooltip(tooltip);
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

    private void addEnergyTooltip(List<String> tooltip) {
        AdAstraEnergyStorage storage = container.getMachine().getEnergyStorage();
        tooltip.add(I18n.format("tooltip.ad_astra.energy_stored", field(0), field(1)));
        tooltip.add(I18n.format("tooltip.ad_astra.energy_transfer_tick", energyDifference));
        if (storage != null) {
            tooltip.add(I18n.format("tooltip.ad_astra.max_energy_in", storage.getMaxReceive()));
            tooltip.add(I18n.format("tooltip.ad_astra.max_energy_out", storage.getMaxExtract()));
        }
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
            case ModGuiIds.OXYGEN_DISTRIBUTOR:
                addFluidTooltip(tooltip, mouseX, mouseY, left + 51, top + 82, I18n.format("side_config.ad_astra.input_fluid"), field(2), field(3));
                addFluidTooltip(tooltip, mouseX, mouseY, left + 116, top + 82, I18n.format("side_config.ad_astra.output_fluid"), field(10), field(3));
                break;
            default:
                break;
        }
    }

    private void addFluidTooltip(List<String> tooltip, int mouseX, int mouseY, int x, int y, String label, int amount, int capacity) {
        if (tooltip.isEmpty() && FLUID_BAR_WIDGET.isMouseOver(mouseX, mouseY, x, y)) {
            tooltip.add(label);
            tooltip.add(formatMillibuckets(amount, capacity));
            if (amount > 0) {
                tooltip.add(I18n.format("tooltip.ad_astra.clear_fluid_tank"));
            }
        }
    }

    private void addProgressTooltip(List<String> tooltip, int mouseX, int mouseY, int left, int top) {
        switch (guiId) {
            case ModGuiIds.COAL_GENERATOR:
                addProgressTooltip(tooltip, mouseX, mouseY, left + 78, top + 54, FIRE_PROGRESS_WIDGET, field(4), field(5));
                break;
            case ModGuiIds.COMPRESSOR:
                break;
            case ModGuiIds.ETRIONIC_BLAST_FURNACE:
                break;
            case ModGuiIds.CRYO_FREEZER:
                break;
            default:
                break;
        }
    }

    private void addProgressTooltip(List<String> tooltip, int mouseX, int mouseY, int x, int y, ProgressWidget widget, int progress, int maxProgress) {
        if (widget.isMouseOver(mouseX, mouseY, x, y)) {
            tooltip.add(I18n.format("tooltip.ad_astra.progress", progress, maxProgress));
        }
    }

    private void addStatusTooltip(List<String> tooltip, int mouseX, int mouseY, int left, int top) {
        addOptionsButtonTooltip(tooltip, mouseX, mouseY, left, top);
        if (!tooltip.isEmpty()) {
            return;
        }

        addSideConfigTooltip(tooltip, mouseX, mouseY, left, top);
        if (!tooltip.isEmpty()) {
            return;
        }

        // Redstone button tooltip
        RedstoneButtonSpec redstoneButton = getRedstoneButtonSpec();
        if (redstoneButton != null && BUTTON_WIDGET.isMouseOver(mouseX, mouseY, left + redstoneButton.x, top + redstoneButton.y)) {
            AdAstraRedstoneControl mode = container.getMachine().getRedstoneControl();
            tooltip.add(I18n.format("tooltip.ad_astra.redstone_control"));
            tooltip.add(I18n.format("tooltip.ad_astra.redstone_control." + mode.name().toLowerCase()));
            return;
        }

        if (guiId == ModGuiIds.GRAVITY_NORMALIZER && GRAVITY_SLIDER_WIDGET.isMouseOver(mouseX, mouseY, left + 25, top + 52)) {
            tooltip.add(I18n.format("tooltip.ad_astra.gravity_amount", field(9) / 1000.0f * EARTH_GRAVITY, (float) MAX_GRAVITY));
        } else if (guiId == ModGuiIds.ETRIONIC_BLAST_FURNACE && isMouseIn(mouseX, mouseY, left + 23, top + 79, 45, 19)) {
            String modeKey = field(6) == 1
                ? "tooltip.ad_astra.etrionic_blast_furnace.mode.blasting"
                : "tooltip.ad_astra.etrionic_blast_furnace.mode.alloying";
            tooltip.add(I18n.format("tooltip.ad_astra.etrionic_blast_furnace.mode", I18n.format(modeKey)));
        }
    }

    private void addOptionsButtonTooltip(List<String> tooltip, int mouseX, int mouseY, int left, int top) {
        ButtonSpec settingsButton = getSettingsButtonSpec();
        if (settingsButton != null && BUTTON_WIDGET.isMouseOver(mouseX, mouseY, left + settingsButton.x, top + settingsButton.y)) {
            tooltip.add(I18n.format("tooltip.ad_astra.side_config"));
            return;
        }

        ButtonSpec leadingButton = getLeadingButtonSpec();
        if (leadingButton != null && BUTTON_WIDGET.isMouseOver(mouseX, mouseY, left + leadingButton.x, top + leadingButton.y)) {
            if (guiId == ModGuiIds.ETRIONIC_BLAST_FURNACE) {
                String modeKey = field(6) == 1
                    ? "tooltip.ad_astra.etrionic_blast_furnace.mode.blasting"
                    : "tooltip.ad_astra.etrionic_blast_furnace.mode.alloying";
                tooltip.add(I18n.format("tooltip.ad_astra.etrionic_blast_furnace.mode", I18n.format(modeKey)));
            } else if (guiId == ModGuiIds.GRAVITY_NORMALIZER) {
                tooltip.add(I18n.format("tooltip.ad_astra.gravity_distribution_area"));
            } else if (guiId == ModGuiIds.OXYGEN_DISTRIBUTOR) {
                tooltip.add(I18n.format("tooltip.ad_astra.oxygen_distribution_area"));
            }
        }
    }

    private void addSideConfigTooltip(List<String> tooltip, int mouseX, int mouseY, int left, int top) {
        if (!sideConfigOpen) {
            return;
        }

        int panelLeft = SIDED_CONFIG_WIDGET.getLeft(left);
        int panelTop = SIDED_CONFIG_WIDGET.getTop(top);
        if (BUTTON_WIDGET.isMouseOver(mouseX, mouseY, SIDED_CONFIG_WIDGET.getResetButtonX(panelLeft), SIDED_CONFIG_WIDGET.getResetButtonY(panelTop))) {
            tooltip.add(I18n.format("tooltip.ad_astra.reset_to_default"));
            return;
        }

        EnumFacing[] sides = getConfigSides();
        SideConfigType[] types = SideConfigType.values();
        for (int row = 0; row < types.length; row++) {
            int y = SIDED_CONFIG_WIDGET.getModeButtonY(panelTop, row);
            for (int column = 0; column < sides.length; column++) {
                int x = SIDED_CONFIG_WIDGET.getModeButtonX(panelLeft, column);
                if (BUTTON_WIDGET.isMouseOver(mouseX, mouseY, x, y)) {
                    AdAstraSideMode mode = container.getMachine().getSideMode(sides[column], types[row]);
                    tooltip.add(I18n.format("side_config.ad_astra.type.type", getSideConfigTypeLabel(types[row])));
                    tooltip.add(I18n.format("side_config.ad_astra.type.direction", sides[column].getName(), I18n.format("side_config.ad_astra.type." + mode.name().toLowerCase())));
                    return;
                }
            }
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

    private static final class RedstoneButtonSpec {
        private final int x;
        private final int y;

        private RedstoneButtonSpec(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    private static final class ButtonSpec {
        private final int x;
        private final int y;

        private ButtonSpec(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    private static final class OptionsBarSpec {
        private final int x;
        private final int y;
        private final int width;

        private OptionsBarSpec(int x, int y, int width) {
            this.x = x;
            this.y = y;
            this.width = width;
        }
    }
}
