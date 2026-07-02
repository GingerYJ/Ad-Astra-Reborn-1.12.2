package com.wantedxnn.adastra.common.blockentities.machines;

import earth.terrarium.adastra.common.tile.AdAstraMachineTileEntity;
import earth.terrarium.adastra.common.tile.AdAstraSideMode;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

public class WaterPumpTileEntity extends AdAstraMachineTileEntity {

    private static final int ENERGY_CAPACITY = 10_000;
    private static final int FLUID_CAPACITY = 16_000;
    private static final int ENERGY_PER_TICK = 20;
    private static final int WATER_GENERATED_PER_TICK = 50;

    public WaterPumpTileEntity() {
        super("water_pump", 1, ENERGY_CAPACITY, DESH_IO, 0, FLUID_CAPACITY);
        setAllSideModes(SideConfigType.ENERGY, AdAstraSideMode.PULL);
        setAllSideModes(SideConfigType.FLUID, AdAstraSideMode.PUSH);
    }

    @Override
    protected void tickMachine() {
        if (energy == null || fluidTank == null || !canFunction()) {
            setLit(false);
            pushFluidToSides();
            return;
        }

        boolean pumped = false;

        // Check if we can pump water
        if (canPump()) {
            energy.extractEnergy(ENERGY_PER_TICK, false);
            fluidTank.fill(new FluidStack(FluidRegistry.WATER, WATER_GENERATED_PER_TICK), true);
            pumped = true;
            markDirty();
        }

        pushFluidToSides();
        setLit(pumped);
    }

    private boolean canPump() {
        if (world == null || pos == null) {
            return false;
        }

        // Check for water block below
        if (world.getBlockState(pos.down()).getBlock() != Blocks.WATER) {
            return false;
        }

        // Check if we have enough energy
        if (energy.extractEnergy(ENERGY_PER_TICK, true) < ENERGY_PER_TICK) {
            return false;
        }

        // Check if we have space for water
        return fluidTank.fill(new FluidStack(FluidRegistry.WATER, WATER_GENERATED_PER_TICK), false) > 0;
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return false;
    }

    @Override
    public int[] getSlotsForFace(EnumFacing side) {
        return new int[0];
    }

    @Override
    public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
        return false;
    }

    @Override
    public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
        return false;
    }
}
