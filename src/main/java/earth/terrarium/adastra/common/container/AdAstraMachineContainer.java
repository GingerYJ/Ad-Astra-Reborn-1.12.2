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
import earth.terrarium.adastra.common.tile.OxygenDistributorTileEntity;
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
            addSlotToContainer(createMachineSlot(machine, slot));
        }
        this.machineSlotCount = inventorySlots.size();
        addPlayerInventory(playerInventory, layout.playerInventoryX, layout.playerInventoryY);
        this.cachedFields = new int[machine.getFieldCount()];
    }

    private Slot createMachineSlot(AdAstraMachineTileEntity machine, SlotSpec slot) {
        if (machine instanceof NasaWorkbenchTileEntity) {
            NasaWorkbenchTileEntity nasaWorkbench = (NasaWorkbenchTileEntity) machine;
            if (nasaWorkbench.isOutputSlot(slot.index)) {
                return new NasaWorkbenchOutputSlot(nasaWorkbench, slot.index, slot.x, slot.y);
            }
        }
        return new MachineSlot(machine, slot.index, slot.x, slot.y, slot.canInsert);
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

    // Client -> server interaction channel (vanilla Container.enchantItem / sendEnchantPacket).
    // id == 0: toggle Etrionic Blast Furnace mode.
    // id == 1: toggle Etrionic Blast Furnace mode backwards.
    // id in [GRAVITY_ID_BASE, GRAVITY_ID_BASE + 200]: set Gravity Normalizer target gravity (0.0 - 2.0).
    public static final int TOGGLE_FURNACE_MODE = 0;
    public static final int PREVIOUS_FURNACE_MODE = 1;
    public static final int GRAVITY_ID_BASE = 1000;

    @Override
    public boolean enchantItem(EntityPlayer playerIn, int id) {
        if (!canInteractWith(playerIn)) {
            return false;
        }
        if ((id == TOGGLE_FURNACE_MODE || id == PREVIOUS_FURNACE_MODE) && machine instanceof EtrionicBlastFurnaceTileEntity) {
            EtrionicBlastFurnaceTileEntity furnace = (EtrionicBlastFurnaceTileEntity) machine;
            furnace.setMode(id == PREVIOUS_FURNACE_MODE ? furnace.getMode().previous() : furnace.getMode().next());
            furnace.markDirty();
            detectAndSendChanges();
            return true;
        }
        if (id >= GRAVITY_ID_BASE && id <= GRAVITY_ID_BASE + 200 && machine instanceof GravityNormalizerTileEntity) {
            GravityNormalizerTileEntity normalizer = (GravityNormalizerTileEntity) machine;
            normalizer.setTargetGravity((id - GRAVITY_ID_BASE) / 100.0f);
            normalizer.markDirty();
            detectAndSendChanges();
            return true;
        }
        return false;
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
            machineSlots.add(new SlotSpec(0, x, y, true));
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

    private static final class NasaWorkbenchOutputSlot extends Slot {
        private final NasaWorkbenchTileEntity nasaWorkbench;

        private NasaWorkbenchOutputSlot(NasaWorkbenchTileEntity inventory, int index, int xPosition, int yPosition) {
            super(inventory, index, xPosition, yPosition);
            this.nasaWorkbench = inventory;
        }

        @Override
        public boolean isItemValid(ItemStack stack) {
            return false;
        }

        @Override
        public boolean canTakeStack(EntityPlayer playerIn) {
            return nasaWorkbench.hasCraftingResult();
        }

        @Override
        public ItemStack onTake(EntityPlayer playerIn, ItemStack stack) {
            nasaWorkbench.craftActiveRecipe(playerIn);
            return super.onTake(playerIn, stack);
        }
    }
}
