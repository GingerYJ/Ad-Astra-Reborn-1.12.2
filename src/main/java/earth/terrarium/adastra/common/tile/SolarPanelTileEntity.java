package earth.terrarium.adastra.common.tile;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

public class SolarPanelTileEntity extends AdAstraMachineTileEntity {

    private static final int OVERWORLD_SOLAR_POWER = 16;

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
        if (energy == null || !canFunction() || !isDay()) {
            pushEnergyToSides();
            setLit(false);
            return;
        }

        int generated = energy.internalReceiveEnergy(getSolarPower(), false);
        pushEnergyToSides();
        setLit(generated > 0);
        if (generated > 0) {
            markDirty();
        }
    }

    private boolean isDay() {
        if (world == null || pos == null || world.provider == null || !world.provider.hasSkyLight()) {
            return false;
        }
        long dayTime = world.getWorldTime() % 24000L;
        BlockPos skyPos = pos.up();
        return dayTime <= 12000L && world.canBlockSeeSky(skyPos);
    }

    private int getSolarPower() {
        return OVERWORLD_SOLAR_POWER;
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
