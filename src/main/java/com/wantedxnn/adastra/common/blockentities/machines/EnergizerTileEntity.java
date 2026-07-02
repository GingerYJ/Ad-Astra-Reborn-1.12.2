package com.wantedxnn.adastra.common.blockentities.machines;

import earth.terrarium.adastra.common.tile.AdAstraMachineTileEntity;
import earth.terrarium.adastra.common.tile.AdAstraSideMode;

import net.minecraft.item.ItemStack;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

public class EnergizerTileEntity extends AdAstraMachineTileEntity {

    private static final int CHARGE_SLOT = 0;

    public EnergizerTileEntity() {
        super("energizer", 1, 2_000_000, OSTRUM_IO, OSTRUM_IO, 0);
        setAllSideModes(SideConfigType.ENERGY, AdAstraSideMode.PUSH_PULL);
    }

    @Override
    protected void tickMachine() {
        if (energy == null || !canFunction()) {
            setLit(false);
            return;
        }

        boolean chargedItem = chargeItem();
        pushEnergyToSides();
        setLit(chargedItem || energy.getEnergyStored() > 0);
    }

    @Override
    protected boolean canExtractEnergyFromBatterySlot() {
        return false;
    }

    private boolean chargeItem() {
        ItemStack stack = items.getStackInSlot(CHARGE_SLOT);
        if (stack.isEmpty() || !stack.hasCapability(CapabilityEnergy.ENERGY, null)) {
            return false;
        }

        IEnergyStorage itemEnergy = stack.getCapability(CapabilityEnergy.ENERGY, null);
        if (itemEnergy == null || !itemEnergy.canReceive()) {
            return false;
        }

        int available = energy.extractEnergy(energy.getMaxExtract(), true);
        if (available <= 0) {
            return false;
        }

        int accepted = itemEnergy.receiveEnergy(available, false);
        if (accepted > 0) {
            energy.extractEnergy(accepted, false);
            markDirty();
            return true;
        }
        return false;
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return index == CHARGE_SLOT && !stack.isEmpty() && stack.hasCapability(CapabilityEnergy.ENERGY, null);
    }

    @Override
    public int[] getSlotsForFace(net.minecraft.util.EnumFacing side) {
        return new int[]{CHARGE_SLOT};
    }
}
