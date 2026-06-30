package earth.terrarium.adastra.common.container;

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
import earth.terrarium.adastra.common.tile.OxygenLoaderTileEntity;
import earth.terrarium.adastra.common.tile.SolarPanelTileEntity;
import earth.terrarium.adastra.common.tile.WaterPumpTileEntity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class AdAstraMachineContainer extends Container {

    private final AdAstraMachineTileEntity machine;
    private final Layout layout;
    private final int machineSlotCount;
    private int[] cachedFields;

    public AdAstraMachineContainer(InventoryPlayer playerInventory, AdAstraMachineTileEntity machine, Layout layout) {
        this.machine = machine;
        this.layout = layout;
        for (SlotSpec slot : layout.machineSlots) {
            addSlotToContainer(new MachineSlot(machine, slot.index, slot.x, slot.y, slot.canInsert));
        }
        this.machineSlotCount = inventorySlots.size();
        addPlayerInventory(playerInventory, layout.playerInventoryX, layout.playerInventoryY);
        this.cachedFields = new int[machine.getFieldCount()];
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

    public int getSyncedField(int id) {
        if (id >= 0 && id < cachedFields.length) {
            return cachedFields[id];
        }
        return machine.getField(id);
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return machine.isUsableByPlayer(playerIn);
    }

    @Override
    public void addListener(IContainerListener listener) {
        super.addListener(listener);
        for (int i = 0; i < machine.getFieldCount(); i++) {
            listener.sendWindowProperty(this, i, machine.getField(i));
            cachedFields[i] = machine.getField(i);
        }
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        for (int i = 0; i < machine.getFieldCount(); i++) {
            int value = machine.getField(i);
            if (i >= cachedFields.length || cachedFields[i] != value) {
                for (IContainerListener listener : listeners) {
                    listener.sendWindowProperty(this, i, value);
                }
                if (i < cachedFields.length) {
                    cachedFields[i] = value;
                }
            }
        }
    }

    @Override
    public void updateProgressBar(int id, int data) {
        if (id >= 0 && id < cachedFields.length) {
            cachedFields[id] = data;
        }
        machine.setField(id, data);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
        ItemStack original = ItemStack.EMPTY;
        Slot slot = inventorySlots.get(index);
        if (slot == null || !slot.getHasStack()) {
            return original;
        }

        ItemStack stack = slot.getStack();
        original = stack.copy();
        if (index < machineSlotCount) {
            if (!mergeItemStack(stack, machineSlotCount, inventorySlots.size(), true)) {
                return ItemStack.EMPTY;
            }
        } else if (!mergeIntoMachineSlots(stack)) {
            return ItemStack.EMPTY;
        }

        if (stack.isEmpty()) {
            slot.putStack(ItemStack.EMPTY);
        } else {
            slot.onSlotChanged();
        }

        if (stack.getCount() == original.getCount()) {
            return ItemStack.EMPTY;
        }
        slot.onTake(playerIn, stack);
        return original;
    }

    private boolean mergeIntoMachineSlots(ItemStack stack) {
        for (int i = 0; i < machineSlotCount; i++) {
            Slot slot = inventorySlots.get(i);
            if (!slot.isItemValid(stack)) {
                continue;
            }
            if (mergeItemStack(stack, i, i + 1, false)) {
                return true;
            }
        }
        return false;
    }

    private void addPlayerInventory(InventoryPlayer playerInventory, int x, int y) {
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                addSlotToContainer(new Slot(playerInventory, column + row * 9 + 9, x + column * 18, y + row * 18));
            }
        }
        for (int column = 0; column < 9; column++) {
            addSlotToContainer(new Slot(playerInventory, column, x + column * 18, y + 58));
        }
    }

    public static Layout layoutFor(int id, AdAstraMachineTileEntity machine) {
        switch (id) {
            case ModGuiIds.COAL_GENERATOR:
                return new Layout("coal_generator", 176, 189, 8, 107)
                    .slot(1, 77, 71, true);
            case ModGuiIds.COMPRESSOR:
                return new Layout("compressor", 184, 201, 12, 114)
                    .slot(0, 24, 58, true)
                    .slot(1, 47, 58, true)
                    .slot(2, 95, 58, false);
            case ModGuiIds.ETRIONIC_BLAST_FURNACE:
                return new Layout("etrionic_blast_furnace", 184, 201, 12, 114)
                    .slot(0, 12, 58, true)
                    .slot(1, 29, 38, true)
                    .slot(2, 47, 38, true)
                    .slot(3, 29, 58, true)
                    .slot(4, 47, 58, true)
                    .slot(5, 101, 38, false)
                    .slot(6, 119, 38, false)
                    .slot(7, 101, 58, false)
                    .slot(8, 119, 58, false);
            case ModGuiIds.FUEL_REFINERY:
                return new Layout("fuel_refinery", 177, 184, 8, 102)
                    .slot(0, 77, 52, true)
                    .slot(1, 12, 22, true)
                    .slot(2, 12, 52, false)
                    .slot(3, 127, 22, true)
                    .slot(4, 127, 52, false);
            case ModGuiIds.OXYGEN_LOADER:
                return new Layout("oxygen_loader", 177, 184, 8, 102)
                    .slot(0, 77, 52, true)
                    .slot(1, 12, 22, true)
                    .slot(2, 12, 52, false)
                    .slot(3, 127, 22, true)
                    .slot(4, 127, 52, false);
            case ModGuiIds.SOLAR_PANEL:
                return new Layout("solar_panel", 177, 230, 8, 148);
            case ModGuiIds.WATER_PUMP:
                return new Layout("water_pump", 177, 191, 8, 109)
                    .slot(0, 26, 70, true);
            case ModGuiIds.ENERGIZER:
                return new Layout("energizer", 184, 201, 12, 114)
                    .slot(0, 80, 58, true);
            case ModGuiIds.CRYO_FREEZER:
                return new Layout("cryo_freezer", 177, 184, 8, 102)
                    .slot(0, 26, 42, true)
                    .slot(1, 26, 70, true)
                    .slot(2, 113, 42, true)
                    .slot(3, 113, 70, false);
            case ModGuiIds.NASA_WORKBENCH:
                return new Layout("nasa_workbench", 177, 224, 8, 142)
                    .slot(0, 80, 15, true)
                    .slot(1, 53, 36, true)
                    .slot(2, 80, 36, true)
                    .slot(3, 107, 36, true)
                    .slot(4, 53, 57, true)
                    .slot(5, 80, 57, true)
                    .slot(6, 107, 57, true)
                    .slot(7, 35, 78, true)
                    .slot(8, 62, 78, true)
                    .slot(9, 98, 78, true)
                    .slot(10, 125, 78, true)
                    .slot(11, 35, 99, true)
                    .slot(12, 80, 99, true)
                    .slot(13, 125, 99, true)
                    .slot(14, 80, 120, false);
            case ModGuiIds.GRAVITY_NORMALIZER:
                return new Layout("gravity_normalizer", 184, 215, 12, 130)
                    .slot(0, 80, 74, true);
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

        public String getTextureName() {
            return textureName;
        }

        public int getWidth() {
            return width;
        }

        public int getHeight() {
            return height;
        }
    }

    private static final class SlotSpec {
        private final int index;
        private final int x;
        private final int y;
        private final boolean canInsert;

        private SlotSpec(int index, int x, int y, boolean canInsert) {
            this.index = index;
            this.x = x;
            this.y = y;
            this.canInsert = canInsert;
        }
    }

    private static final class MachineSlot extends Slot {
        private final boolean canInsert;

        private MachineSlot(AdAstraMachineTileEntity inventory, int index, int xPosition, int yPosition, boolean canInsert) {
            super(inventory, index, xPosition, yPosition);
            this.canInsert = canInsert;
        }

        @Override
        public boolean isItemValid(ItemStack stack) {
            return canInsert && inventory.isItemValidForSlot(getSlotIndex(), stack);
        }
    }
}
