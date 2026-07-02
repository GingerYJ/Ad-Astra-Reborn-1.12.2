package com.wantedxnn.adastra.common.blockentities.machines;

import earth.terrarium.adastra.common.tile.AdAstraMachineTileEntity;
import earth.terrarium.adastra.common.tile.AdAstraSideMode;

import earth.terrarium.adastra.common.config.AdAstraConfig;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

public class SolarPanelTileEntity extends AdAstraMachineTileEntity {

    private static final int ENERGY_CAPACITY = 10_000;
    private static final int SOLAR_POWER = 10;

    public SolarPanelTileEntity() {
        super("solar_panel", 1, ENERGY_CAPACITY, 0, DESH_IO, 0);
        setAllSideModes(SideConfigType.ENERGY, AdAstraSideMode.PUSH);
    }

    @Override
    protected boolean canExtractEnergyFromBatterySlot() {
        return false;
    }

    @Override
    protected void tickMachine() {
        if (energy == null || !canFunction()) {
            pushEnergyToSides();
            setLit(false);
            return;
        }

        boolean generating = false;

        // Generate energy if it's daytime and we can see the sky
        if (isDay() && energy.getEnergyStored() < energy.getMaxEnergyStored()) {
            int modifiedPower = AdAstraConfig.getModifiedEnergyGeneration(SOLAR_POWER, "solar");
            int generated = energy.internalReceiveEnergy(modifiedPower, false);
            if (generated > 0) {
                generating = true;
                markDirty();
            }
        }

        pushEnergyToSides();
        setLit(generating);
    }

    private boolean isDay() {
        if (world == null || pos == null || world.provider == null || !world.provider.hasSkyLight()) {
            return false;
        }
        long dayTime = world.getWorldTime() % 24000L;
        BlockPos skyPos = pos.up();
        return dayTime <= 12000L && world.canBlockSeeSky(skyPos);
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
    public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
        return false;
    }
}
