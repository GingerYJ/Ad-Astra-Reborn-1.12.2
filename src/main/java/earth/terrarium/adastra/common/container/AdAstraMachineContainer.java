package earth.terrarium.adastra.common.container;

import earth.terrarium.adastra.common.container.slots.NasaWorkbenchOutputSlot;
import earth.terrarium.adastra.common.container.slots.PredicateSlot;
import earth.terrarium.adastra.common.menus.base.MachineMenu;
import earth.terrarium.adastra.common.registry.ModGuiIds;
import earth.terrarium.adastra.common.tile.AdAstraMachineTileEntity;
import earth.terrarium.adastra.common.tile.CoalGeneratorTileEntity;
import earth.terrarium.adastra.common.tile.CompressorTileEntity;
import earth.terrarium.adastra.common.tile.CryoFreezerTileEntity;
import earth.terrarium.adastra.common.tile.EnergizerTileEntity;
import earth.terrarium.adastra.common.tile.EtrionicBlastFurnaceTileEntity;
import earth.terrarium.adastra.common.tile.FuelRefineryTileEntity;
import earth.terrarium.adastra.common.tile.GravityNormalizerTileEntity;
import earth.terrarium.adastra.common.tile.NasaWorkbenchTileEntity;
import earth.terrarium.adastra.common.tile.OxygenDistributorTileEntity;
import earth.terrarium.adastra.common.tile.OxygenLoaderTileEntity;
import earth.terrarium.adastra.common.tile.SolarPanelTileEntity;
import earth.terrarium.adastra.common.tile.WaterPumpTileEntity;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;

import java.util.ArrayList;
import java.util.List;

public class AdAstraMachineContainer extends MachineMenu<AdAstraMachineTileEntity> {

    private final AdAstraMachineTileEntity machine;
    private final Layout layout;
    private final int machineSlotCount;

    public AdAstraMachineContainer(InventoryPlayer playerInventory, AdAstraMachineTileEntity machine, Layout layout) {
        super(playerInventory, machine, layout);
        this.machine = machine;
        this.layout = layout;
        for (SlotSpec slot : layout.machineSlots) {
            addSlotToContainer(createMachineSlot(machine, slot));
        }
        this.machineSlotCount = inventorySlots.size();
        addPlayerInvSlots();
    }

    private Slot createMachineSlot(AdAstraMachineTileEntity machine, SlotSpec slot) {
        if (slot.battery) {
            return createBatterySlot(machine, slot.index, slot.x, slot.y);
        }
        if (machine instanceof NasaWorkbenchTileEntity) {
            NasaWorkbenchTileEntity nasaWorkbench = (NasaWorkbenchTileEntity) machine;
            if (nasaWorkbench.isOutputSlot(slot.index)) {
                return new NasaWorkbenchOutputSlot(nasaWorkbench, slot.index, slot.x, slot.y);
            }
        }
        return new PredicateSlot(machine, slot.index, slot.x, slot.y,
            stack -> slot.canInsert && machine.isItemValidForSlot(slot.index, stack));
    }

    public AdAstraMachineTileEntity getMachine() {
        return machine;
    }

    public Layout getLayout() {
        return layout;
    }

    public int getMachineSlotCount() {
        return machineSlotCount;
    }

    @Override
    protected int getContainerInputEnd() {
        return machineSlotCount;
    }

    @Override
    protected int getInventoryStart() {
        return machineSlotCount;
    }

    @Override
    public int getPlayerInvXOffset() {
        return layout.playerInventoryX;
    }

    @Override
    public int getPlayerInvYOffset() {
        return layout.playerInventoryY;
    }

    public static Layout layoutFor(int id, AdAstraMachineTileEntity machine) {
        switch (id) {
            case ModGuiIds.COAL_GENERATOR:
                return new Layout("coal_generator", 176, 189, 8, 107)
                    .slot(1, 77, 71, true);
            case ModGuiIds.COMPRESSOR:
                return new Layout("compressor", 184, 201, 12, 114).batterySlot(161, -25)
                    .slot(1, 47, 58, true)
                    .slot(2, 95, 58, false);
            case ModGuiIds.ETRIONIC_BLAST_FURNACE:
                return new Layout("etrionic_blast_furnace", 184, 201, 12, 114).batterySlot(161, -25)
                    .slot(1, 29, 38, true)
                    .slot(2, 47, 38, true)
                    .slot(3, 29, 58, true)
                    .slot(4, 47, 58, true)
                    .slot(5, 101, 38, false)
                    .slot(6, 119, 38, false)
                    .slot(7, 101, 58, false)
                    .slot(8, 119, 58, false);
            case ModGuiIds.FUEL_REFINERY:
                return new Layout("fuel_refinery", 177, 184, 8, 102).batterySlot(154, -25)
                    .slot(1, 12, 22, true)
                    .slot(2, 12, 52, false)
                    .slot(3, 127, 22, true)
                    .slot(4, 127, 52, false);
            case ModGuiIds.OXYGEN_LOADER:
                return new Layout("oxygen_loader", 177, 184, 8, 102).batterySlot(154, -25)
                    .slot(1, 12, 22, true)
                    .slot(2, 12, 52, false)
                    .slot(3, 127, 22, true)
                    .slot(4, 127, 52, false);
            case ModGuiIds.SOLAR_PANEL:
                return new Layout("solar_panel", 177, 230, 8, 148).batterySlot(154, -25);
            case ModGuiIds.WATER_PUMP:
                return new Layout("water_pump", 177, 191, 8, 109).batterySlot(154, -25);
            case ModGuiIds.ENERGIZER:
                return new Layout("energizer", 184, 201, 12, 114)
                    .slot(0, 95, 58, true);
            case ModGuiIds.CRYO_FREEZER:
                return new Layout("cryo_freezer", 177, 184, 8, 102).batterySlot(154, -25)
                    .slot(1, 26, 70, true)
                    .slot(2, 113, 42, true)
                    .slot(3, 113, 70, false);
            case ModGuiIds.NASA_WORKBENCH:
                return new Layout("nasa_workbench", 177, 224, 8, 142)
                    .slot(0, 56, 20, true)
                    .slot(1, 47, 38, true)
                    .slot(2, 65, 38, true)
                    .slot(3, 47, 56, true)
                    .slot(4, 65, 56, true)
                    .slot(5, 47, 74, true)
                    .slot(6, 65, 74, true)
                    .slot(7, 29, 92, true)
                    .slot(8, 47, 92, true)
                    .slot(9, 65, 92, true)
                    .slot(10, 83, 92, true)
                    .slot(11, 29, 110, true)
                    .slot(12, 56, 110, true)
                    .slot(13, 83, 110, true)
                    .slot(14, 129, 56, false);
            case ModGuiIds.GRAVITY_NORMALIZER:
                return new Layout("gravity_normalizer", 184, 215, 12, 130).batterySlot(161, -25);
            case ModGuiIds.OXYGEN_DISTRIBUTOR:
                return new Layout("oxygen_distributor", 177, 244, 8, 162).batterySlot(168, 7)
                    .slot(1, 17, 82, true)
                    .slot(2, 17, 112, false);
            default:
                return null;
        }
    }

    public static int idFor(AdAstraMachineTileEntity machine) {
        if (machine instanceof CoalGeneratorTileEntity) {
            return ModGuiIds.COAL_GENERATOR;
        }
        if (machine instanceof CompressorTileEntity) {
            return ModGuiIds.COMPRESSOR;
        }
        if (machine instanceof EtrionicBlastFurnaceTileEntity) {
            return ModGuiIds.ETRIONIC_BLAST_FURNACE;
        }
        if (machine instanceof FuelRefineryTileEntity) {
            return ModGuiIds.FUEL_REFINERY;
        }
        if (machine instanceof OxygenLoaderTileEntity) {
            return ModGuiIds.OXYGEN_LOADER;
        }
        if (machine instanceof OxygenDistributorTileEntity) {
            return ModGuiIds.OXYGEN_DISTRIBUTOR;
        }
        if (machine instanceof SolarPanelTileEntity) {
            return ModGuiIds.SOLAR_PANEL;
        }
        if (machine instanceof WaterPumpTileEntity) {
            return ModGuiIds.WATER_PUMP;
        }
        if (machine instanceof EnergizerTileEntity) {
            return ModGuiIds.ENERGIZER;
        }
        if (machine instanceof CryoFreezerTileEntity) {
            return ModGuiIds.CRYO_FREEZER;
        }
        if (machine instanceof NasaWorkbenchTileEntity) {
            return ModGuiIds.NASA_WORKBENCH;
        }
        if (machine instanceof GravityNormalizerTileEntity) {
            return ModGuiIds.GRAVITY_NORMALIZER;
        }
        return -1;
    }

    public static final class Layout {
        private final String textureName;
        private final int width;
        private final int height;
        private final int playerInventoryX;
        private final int playerInventoryY;
        private final List<SlotSpec> machineSlots = new ArrayList<>();
        private boolean hasBatterySlot;

        private Layout(String textureName, int width, int height, int playerInventoryX, int playerInventoryY) {
            this.textureName = textureName;
            this.width = width;
            this.height = height;
            this.playerInventoryX = playerInventoryX;
            this.playerInventoryY = playerInventoryY;
        }

        private Layout slot(int index, int x, int y, boolean canInsert) {
            machineSlots.add(new SlotSpec(index, x, y, canInsert));
            return this;
        }

        private Layout batterySlot(int x, int y) {
            hasBatterySlot = true;
            machineSlots.add(new SlotSpec(0, x, y, true, true));
            return this;
        }

        public String getTextureName() {
            return textureName;
        }

        public int getWidth() {
            return width;
        }

        public int getHeight() {
            return height;
        }

        public boolean hasBatterySlot() {
            return hasBatterySlot;
        }
    }

    private static final class SlotSpec {
        private final int index;
        private final int x;
        private final int y;
        private final boolean canInsert;
        private final boolean battery;

        private SlotSpec(int index, int x, int y, boolean canInsert) {
            this(index, x, y, canInsert, false);
        }

        private SlotSpec(int index, int x, int y, boolean canInsert, boolean battery) {
            this.index = index;
            this.x = x;
            this.y = y;
            this.canInsert = canInsert;
            this.battery = battery;
        }
    }

}
