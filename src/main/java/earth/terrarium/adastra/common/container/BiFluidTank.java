package earth.terrarium.adastra.common.container;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

import javax.annotation.Nullable;
import java.util.function.Predicate;

/**
 * A dual-tank fluid container with separate input and output tanks.
 * Mimics 1.20's BiFluidContainer from Botarium.
 *
 * Input tank: Insert-only (for machines consuming fluids)
 * Output tank: Extract-only (for machines producing fluids)
 */
public class BiFluidTank implements IFluidHandler {

    private final AdAstraFluidTank inputTank;
    private final AdAstraFluidTank outputTank;

    public BiFluidTank(int capacity) {
        this(capacity, capacity, null, null);
    }

    public BiFluidTank(int inputCapacity, int outputCapacity) {
        this(inputCapacity, outputCapacity, null, null);
    }

    public BiFluidTank(int inputCapacity, int outputCapacity,
                       @Nullable Predicate<FluidStack> inputFilter,
                       @Nullable Predicate<FluidStack> outputFilter) {
        this.inputTank = new AdAstraFluidTank(inputCapacity, inputFilter);
        this.outputTank = new AdAstraFluidTank(outputCapacity, outputFilter);
    }

    /**
     * Get the input tank (tank 0).
     */
    public AdAstraFluidTank getInputTank() {
        return inputTank;
    }

    /**
     * Get the output tank (tank 1).
     */
    public AdAstraFluidTank getOutputTank() {
        return outputTank;
    }

    /**
     * Set change listeners for both tanks.
     */
    public void setChangeListener(Runnable listener) {
        inputTank.setChangeListener(listener);
        outputTank.setChangeListener(listener);
    }

    // IFluidHandler implementation

    @Override
    public IFluidTankProperties[] getTankProperties() {
        return new IFluidTankProperties[] {
            new Properties(inputTank, true, false),  // Input: can fill, cannot drain
            new Properties(outputTank, false, true)  // Output: cannot fill, can drain
        };
    }

    @Override
    public int fill(FluidStack resource, boolean doFill) {
        if (resource == null || resource.amount <= 0) {
            return 0;
        }
        // Only fill input tank
        return inputTank.fill(resource, doFill);
    }

    @Nullable
    @Override
    public FluidStack drain(FluidStack resource, boolean doDrain) {
        if (resource == null || resource.amount <= 0) {
            return null;
        }
        // Only drain from output tank
        return outputTank.drain(resource, doDrain);
    }

    @Nullable
    @Override
    public FluidStack drain(int maxDrain, boolean doDrain) {
        // Only drain from output tank
        return outputTank.drain(maxDrain, doDrain);
    }

    /**
     * Internal method to transfer fluid from input to output (for processing).
     */
    public boolean transferInputToOutput(int amount) {
        FluidStack drained = inputTank.drain(amount, false);
        if (drained == null || drained.amount <= 0) {
            return false;
        }
        int filled = outputTank.fill(drained, false);
        if (filled <= 0) {
            return false;
        }
        inputTank.drain(filled, true);
        outputTank.fill(drained.copy(), true);
        return true;
    }

    /**
     * Internal method to insert fluid directly into output tank (for recipe results).
     */
    public int fillOutput(FluidStack resource, boolean doFill) {
        if (resource == null || resource.amount <= 0) {
            return 0;
        }
        return outputTank.fill(resource, doFill);
    }

    /**
     * Internal method to drain fluid directly from input tank (for recipe consumption).
     */
    @Nullable
    public FluidStack drainInput(int maxDrain, boolean doDrain) {
        return inputTank.drain(maxDrain, doDrain);
    }

    /**
     * Check if both tanks are empty.
     */
    public boolean isEmpty() {
        return inputTank.isEmpty() && outputTank.isEmpty();
    }

    /**
     * Clear both tanks.
     */
    public void clear() {
        inputTank.clear();
        outputTank.clear();
    }

    // NBT Serialization

    public NBTTagCompound serializeNBT() {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setTag("Input", inputTank.serializeNBT());
        nbt.setTag("Output", outputTank.serializeNBT());
        return nbt;
    }

    public void deserializeNBT(NBTTagCompound nbt) {
        if (nbt.hasKey("Input")) {
            inputTank.deserializeNBT(nbt.getCompoundTag("Input"));
        }
        if (nbt.hasKey("Output")) {
            outputTank.deserializeNBT(nbt.getCompoundTag("Output"));
        }
    }

    /**
     * Helper class for IFluidTankProperties implementation.
     */
    private static class Properties implements IFluidTankProperties {
        private final AdAstraFluidTank tank;
        private final boolean canFill;
        private final boolean canDrain;

        public Properties(AdAstraFluidTank tank, boolean canFill, boolean canDrain) {
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
}
