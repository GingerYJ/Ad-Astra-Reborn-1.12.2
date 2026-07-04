package earth.terrarium.adastra.common.tile;

import earth.terrarium.adastra.api.planets.PlanetApi;
import earth.terrarium.adastra.common.config.AdAstraConfig;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

public class SolarPanelTileEntity extends AdAstraMachineTileEntity {

    private static final int CHARGE_SLOT = 0;

    public SolarPanelTileEntity() {
        super("solar_panel", 1, DESH_ENERGY, 0, DESH_IO, 0);
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
            int solarPower = (int) Math.min(Integer.MAX_VALUE, PlanetApi.API.getSolarPower(world));
            int modifiedPower = AdAstraConfig.getModifiedEnergyGeneration(solarPower, "solar");
            int generated = energy.internalReceiveEnergy(modifiedPower, false);
            if (generated > 0) {
                generating = true;
                markDirty();
            }
        }

        boolean chargedItem = chargeItem();
        pushEnergyToSides();
        setLit(generating || chargedItem);
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
        if (index != CHARGE_SLOT || stack.isEmpty() || !stack.hasCapability(CapabilityEnergy.ENERGY, null)) {
            return false;
        }
        IEnergyStorage itemEnergy = stack.getCapability(CapabilityEnergy.ENERGY, null);
        return itemEnergy != null && itemEnergy.canReceive();
    }

    @Override
    public int[] getSlotsForFace(EnumFacing side) {
        return new int[0];
    }

    @Override
    public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
        return false;
    }

    private boolean chargeItem() {
        ItemStack stack = items.getStackInSlot(CHARGE_SLOT);
        if (!isItemValidForSlot(CHARGE_SLOT, stack)) {
            return false;
        }

        IEnergyStorage itemEnergy = stack.getCapability(CapabilityEnergy.ENERGY, null);
        if (itemEnergy == null) {
            return false;
        }

        int available = energy.extractEnergy(energy.getMaxExtract(), true);
        if (available <= 0) {
            return false;
        }

        int accepted = itemEnergy.receiveEnergy(available, false);
        if (accepted <= 0) {
            return false;
        }

        energy.extractEnergy(accepted, false);
        markDirty();
        return true;
    }
}
