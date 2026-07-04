package earth.terrarium.adastra.common.tile;

import earth.terrarium.adastra.common.config.AdAstraConfig;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

public class WaterPumpTileEntity extends AdAstraMachineTileEntity {

    private long lastBubbleParticleTick = Long.MIN_VALUE;

    public WaterPumpTileEntity() {
        super("water_pump", 1, DESH_ENERGY, DESH_IO, 0, DESH_FLUID);
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
            energy.internalExtractEnergy(AdAstraConfig.waterPumpEnergyPerTick, false);
            fluidTank.fill(new FluidStack(FluidRegistry.WATER, AdAstraConfig.waterPumpFluidGenerationPerTick), true);
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
        if (energy.internalExtractEnergy(AdAstraConfig.waterPumpEnergyPerTick, true) < AdAstraConfig.waterPumpEnergyPerTick) {
            return false;
        }

        // Check if we have space for water
        return fluidTank.fill(new FluidStack(FluidRegistry.WATER, AdAstraConfig.waterPumpFluidGenerationPerTick), false) > 0;
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return isValidBatterySlotItem(index, stack);
    }

    @Override
    public int[] getSlotsForFace(EnumFacing side) {
        return new int[0];
    }

    @Override
    public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
        return isItemValidForSlot(index, itemStackIn);
    }

    @Override
    public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
        return false;
    }

    public boolean consumeBubbleParticleTick() {
        if (world == null || !world.isRemote) {
            return false;
        }
        long tick = world.getTotalWorldTime();
        if (lastBubbleParticleTick == tick) {
            return false;
        }
        lastBubbleParticleTick = tick;
        return true;
    }
}
