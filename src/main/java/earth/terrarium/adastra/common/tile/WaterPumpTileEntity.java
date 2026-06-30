package earth.terrarium.adastra.common.tile;

import net.minecraft.init.Blocks;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

public class WaterPumpTileEntity extends AdAstraMachineTileEntity {

    private static final int ENERGY_PER_TICK = 20;
    private static final int WATER_GENERATED_PER_TICK = 50;

    public WaterPumpTileEntity() {
        super("water_pump", 1, DESH_ENERGY, DESH_IO, 0, DESH_FLUID);
        setAllSideModes(SideConfigType.ENERGY, AdAstraSideMode.PULL);
        setAllSideModes(SideConfigType.FLUID, AdAstraSideMode.PUSH);
    }

    @Override
    protected void tickMachine() {
        if (energy == null || fluidTank == null || !canFunction() || !canPump()) {
            setLit(false);
            return;
        }

        energy.extractEnergy(ENERGY_PER_TICK, false);
        fluidTank.fill(new FluidStack(FluidRegistry.WATER, WATER_GENERATED_PER_TICK), true);
        setLit(true);
        markDirty();
    }

    private boolean canPump() {
        if (world == null || pos == null || world.getBlockState(pos.down()).getBlock() != Blocks.WATER) {
            return false;
        }
        if (energy.extractEnergy(ENERGY_PER_TICK, true) < ENERGY_PER_TICK) {
            return false;
        }
        return fluidTank.fill(new FluidStack(FluidRegistry.WATER, WATER_GENERATED_PER_TICK), false) > 0;
    }
}
