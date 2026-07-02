package com.wantedxnn.adastra.common.blockentities.machines;

import earth.terrarium.adastra.common.tile.AdAstraMachineTileEntity;
import earth.terrarium.adastra.common.tile.AdAstraSideMode;
import earth.terrarium.adastra.common.config.AdAstraConfig;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumFacing;

public class CoalGeneratorTileEntity extends AdAstraMachineTileEntity {

    private static final int FUEL_SLOT = 1;
    private static final int ENERGY_CAPACITY = 50_000;
    private static final int ENERGY_GENERATED_PER_TICK = 30;
    private static final int MAX_BURN_TIME = 20_000;

    protected int burnTime;
    protected int maxBurnTime;

    public CoalGeneratorTileEntity() {
        super("coal_generator", 2, ENERGY_CAPACITY, 0, IRON_IO, 0);
        setAllSideModes(SideConfigType.ENERGY, AdAstraSideMode.PUSH);
    }

    @Override
    protected boolean canExtractEnergyFromBatterySlot() {
        return false;
    }

    @Override
    protected void tickMachine() {
        if (energy == null || !canFunction()) {
            setLit(false);
            pushEnergyToSides();
            return;
        }

        boolean wasBurning = burnTime > 0;

        // Continue burning if we have burn time remaining
        if (burnTime > 0) {
            burnTime--;
            // Generate energy if there's space (respecting config multiplier)
            if (energy.getEnergyStored() < energy.getMaxEnergyStored()) {
                int modifiedEnergy = AdAstraConfig.getModifiedEnergyGeneration(ENERGY_GENERATED_PER_TICK, "coal");
                energy.internalReceiveEnergy(modifiedEnergy, false);
                markDirty();
            }
        }

        // Try to consume fuel if not burning and there's space for energy
        if (burnTime <= 0 && energy.getEnergyStored() < energy.getMaxEnergyStored()) {
            if (consumeFuel()) {
                markDirty();
            } else {
                maxBurnTime = 0;
            }
        }

        // Update lit state
        boolean isBurning = burnTime > 0;
        if (wasBurning != isBurning) {
            setLit(isBurning);
            markDirty();
        }

        // Push energy to adjacent machines
        pushEnergyToSides();
    }

    private boolean consumeFuel() {
        ItemStack fuel = items.getStackInSlot(FUEL_SLOT);
        if (fuel.isEmpty() || fuel.getItem() instanceof ItemBucket) {
            return false;
        }

        int fuelBurnTime = Math.min(MAX_BURN_TIME, TileEntityFurnace.getItemBurnTime(fuel));
        if (fuelBurnTime <= 0) {
            return false;
        }

        Item fuelItem = fuel.getItem();
        ItemStack container = fuelItem.getContainerItem(fuel);
        fuel.shrink(1);
        if (fuel.isEmpty()) {
            items.setStackInSlot(FUEL_SLOT, container);
        }

        maxBurnTime = fuelBurnTime;
        burnTime = fuelBurnTime;
        return true;
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return index == FUEL_SLOT && !stack.isEmpty() && !(stack.getItem() instanceof ItemBucket) && TileEntityFurnace.getItemBurnTime(stack) > 0;
    }

    @Override
    public int[] getSlotsForFace(net.minecraft.util.EnumFacing side) {
        return new int[]{FUEL_SLOT};
    }

    @Override
    public boolean canExtractItem(int index, ItemStack stack, net.minecraft.util.EnumFacing direction) {
        return false;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        burnTime = compound.getInteger("BurnTime");
        maxBurnTime = compound.getInteger("MaxBurnTime");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setInteger("BurnTime", burnTime);
        compound.setInteger("MaxBurnTime", maxBurnTime);
        return compound;
    }

    @Override
    public int getField(int id) {
        if (id == 4) {
            return burnTime;
        }
        if (id == 5) {
            return maxBurnTime;
        }
        return super.getField(id);
    }

    @Override
    public void setField(int id, int value) {
        if (id == 4) {
            burnTime = value;
        } else if (id == 5) {
            maxBurnTime = value;
        } else {
            super.setField(id, value);
        }
    }

    @Override
    public int getFieldCount() {
        return 6;
    }
}

