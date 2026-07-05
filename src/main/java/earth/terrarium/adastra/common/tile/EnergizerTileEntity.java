package earth.terrarium.adastra.common.tile;

import earth.terrarium.adastra.common.config.AdAstraConfig;
import earth.terrarium.adastra.common.blocks.AdAstraEnergizerBlock;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

public class EnergizerTileEntity extends AdAstraMachineTileEntity {

    private static final int CHARGE_SLOT = 0;

    public EnergizerTileEntity() {
        super("energizer", 1, AdAstraConfig.energizerEnergyCapacity, OSTRUM_IO, OSTRUM_IO, 0);
        setAllSideModes(SideConfigType.ENERGY, AdAstraSideMode.PUSH_PULL);
    }

    @Override
    protected boolean isIdleOptimizationEnabled() {
        return false;
    }

    @Override
    protected void tickMachine() {
        if (energy == null || !canFunction()) {
            setLit(false);
            return;
        }

        boolean chargedItem = chargeItem();
        pushEnergyToSides();
        updatePowerState();
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

    public void restoreStoredEnergy(int stored) {
        if (energy == null) {
            return;
        }

        energy.setEnergyStored(Math.max(0, Math.min(energy.getMaxEnergyStored(), stored)));
        updatePowerState();
        setLit(energy.getEnergyStored() > 0);
        markDirty();
    }

    public void onExternalEnergyChanged() {
        updatePowerState();
        setLit(energy != null && (energy.getEnergyStored() > 0 || !items.getStackInSlot(CHARGE_SLOT).isEmpty()));
        markDirty();
    }

    private void updatePowerState() {
        if (world == null || pos == null || energy == null) {
            return;
        }

        IBlockState state = world.getBlockState(pos);
        if (!state.getPropertyKeys().contains(AdAstraEnergizerBlock.POWER)) {
            return;
        }

        int stored = energy.getEnergyStored();
        int charge = energy.getMaxEnergyStored() <= 0 || stored <= 0
            ? 0
            : Math.max(1, Math.round(stored / (float) energy.getMaxEnergyStored() * 5.0f));
        charge = Math.max(0, Math.min(5, charge));
        if (state.getValue(AdAstraEnergizerBlock.POWER) != charge) {
            world.setBlockState(pos, state.withProperty(AdAstraEnergizerBlock.POWER, charge), 3);
        }
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return index == CHARGE_SLOT && !stack.isEmpty() && stack.hasCapability(CapabilityEnergy.ENERGY, null);
    }

    @Override
    public int[] getSlotsForFace(net.minecraft.util.EnumFacing side) {
        return new int[]{CHARGE_SLOT};
    }

    public boolean shouldRenderChargeSparks() {
        if (world == null || !world.isRemote || energy == null || energy.getEnergyStored() <= 0) {
            return false;
        }

        ItemStack stack = items.getStackInSlot(CHARGE_SLOT);
        if (stack.isEmpty() || !stack.hasCapability(CapabilityEnergy.ENERGY, null)) {
            return false;
        }

        IEnergyStorage itemEnergy = stack.getCapability(CapabilityEnergy.ENERGY, null);
        if (itemEnergy == null || !itemEnergy.canReceive() || itemEnergy.receiveEnergy(1, true) <= 0) {
            return false;
        }

        return world.getTotalWorldTime() % 3L == 0L;
    }
}
