package earth.terrarium.adastra.common.container;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

import javax.annotation.Nullable;

/**
 * Side-aware fluid container for machines that need different I/O behavior per side.
 * Wraps an AdAstraFluidTank and provides side-based access control.
 */
public class SidedFluidContainer implements IFluidHandler {

    private final AdAstraFluidTank tank;
    private final SideMode[] sideModes = new SideMode[6]; // One per EnumFacing

    public SidedFluidContainer(AdAstraFluidTank tank) {
        this.tank = tank;
        // Default: all sides are disabled
        for (int i = 0; i < 6; i++) {
            sideModes[i] = SideMode.NONE;
        }
    }

    /**
     * Set the mode for a specific side.
     */
    public void setSideMode(EnumFacing side, SideMode mode) {
        if (side != null) {
            sideModes[side.getIndex()] = mode;
        }
    }

    /**
     * Get the mode for a specific side.
     */
    public SideMode getSideMode(@Nullable EnumFacing side) {
        if (side == null) {
            return SideMode.BOTH; // Internal access
        }
        return sideModes[side.getIndex()];
    }

    /**
     * Get the underlying fluid tank (for internal use).
     */
    public AdAstraFluidTank getTank() {
        return tank;
    }

    /**
     * Get an IFluidHandler view for a specific side.
     */
    public IFluidHandler forSide(@Nullable EnumFacing side) {
        return new SidedView(side);
    }

    // IFluidHandler implementation (for internal/null side access)

    @Override
    public IFluidTankProperties[] getTankProperties() {
        return tank.getTankProperties();
    }

    @Override
    public int fill(FluidStack resource, boolean doFill) {
        return tank.fill(resource, doFill);
    }

    @Nullable
    @Override
    public FluidStack drain(FluidStack resource, boolean doDrain) {
        return tank.drain(resource, doDrain);
    }

    @Nullable
    @Override
    public FluidStack drain(int maxDrain, boolean doDrain) {
        return tank.drain(maxDrain, doDrain);
    }

    // Serialization

    public NBTTagCompound serializeNBT() {
        NBTTagCompound nbt = tank.serializeNBT();
        int[] modes = new int[6];
        for (int i = 0; i < 6; i++) {
            modes[i] = sideModes[i].ordinal();
        }
        nbt.setIntArray("SideModes", modes);
        return nbt;
    }

    public void deserializeNBT(NBTTagCompound nbt) {
        tank.deserializeNBT(nbt);
        if (nbt.hasKey("SideModes")) {
            int[] modes = nbt.getIntArray("SideModes");
            for (int i = 0; i < Math.min(6, modes.length); i++) {
                sideModes[i] = SideMode.values()[modes[i]];
            }
        }
    }

    /**
     * Side-specific view of the fluid container.
     */
    private class SidedView implements IFluidHandler {
        private final EnumFacing side;

        public SidedView(@Nullable EnumFacing side) {
            this.side = side;
        }

        @Override
        public IFluidTankProperties[] getTankProperties() {
            SideMode mode = getSideMode(side);
            boolean canFill = mode == SideMode.INPUT || mode == SideMode.BOTH;
            boolean canDrain = mode == SideMode.OUTPUT || mode == SideMode.BOTH;

            return new IFluidTankProperties[] {
                new SidedProperties(tank, canFill, canDrain)
            };
        }

        @Override
        public int fill(FluidStack resource, boolean doFill) {
            SideMode mode = getSideMode(side);
            if (mode == SideMode.NONE || mode == SideMode.OUTPUT) {
                return 0;
            }
            return tank.fill(resource, doFill);
        }

        @Nullable
        @Override
        public FluidStack drain(FluidStack resource, boolean doDrain) {
            SideMode mode = getSideMode(side);
            if (mode == SideMode.NONE || mode == SideMode.INPUT) {
                return null;
            }
            return tank.drain(resource, doDrain);
        }

        @Nullable
        @Override
        public FluidStack drain(int maxDrain, boolean doDrain) {
            SideMode mode = getSideMode(side);
            if (mode == SideMode.NONE || mode == SideMode.INPUT) {
                return null;
            }
            return tank.drain(maxDrain, doDrain);
        }
    }

    /**
     * Side-specific tank properties.
     */
    private static class SidedProperties implements IFluidTankProperties {
        private final AdAstraFluidTank tank;
        private final boolean canFill;
        private final boolean canDrain;

        public SidedProperties(AdAstraFluidTank tank, boolean canFill, boolean canDrain) {
            this.tank = tank;
            this.canFill = canFill;
            this.canDrain = canDrain;
        }

        @Nullable
        @Override
        public FluidStack getContents() {
            return tank.getFluid();
        }

        @Override
        public int getCapacity() {
            return tank.getCapacity();
        }

        @Override
        public boolean canFill() {
            return canFill;
        }

        @Override
        public boolean canDrain() {
            return canDrain;
        }

        @Override
        public boolean canFillFluidType(FluidStack fluidStack) {
            return canFill && tank.canFillFluidType(fluidStack);
        }

        @Override
        public boolean canDrainFluidType(FluidStack fluidStack) {
            return canDrain;
        }
    }

    /**
     * Mode for each side of the machine.
     */
    public enum SideMode {
        NONE,   // No fluid transfer
        INPUT,  // Can fill
        OUTPUT, // Can drain
        BOTH    // Can both fill and drain
    }
}
