package earth.terrarium.adastra.common.container;

import earth.terrarium.adastra.common.util.AdAstraFluidHelper;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;

import javax.annotation.Nullable;
import java.util.function.Predicate;

/**
 * Enhanced FluidTank with additional features for Ad Astra machines.
 * Provides compatibility layer between 1.20's Botarium and 1.12.2's FluidTank.
 */
public class AdAstraFluidTank extends FluidTank {

    private final Predicate<FluidStack> fluidFilter;
    private Runnable changeListener;

    public AdAstraFluidTank(int capacity) {
        this(capacity, null);
    }

    public AdAstraFluidTank(int capacity, @Nullable Predicate<FluidStack> fluidFilter) {
        super(capacity);
        this.fluidFilter = fluidFilter;
    }

    /**
     * Set a callback to be invoked when the tank contents change.
     */
    public void setChangeListener(Runnable listener) {
        this.changeListener = listener;
    }

    @Override
    protected void onContentsChanged() {
        if (changeListener != null) {
            changeListener.run();
        }
    }

    @Override
    public FluidTank readFromNBT(NBTTagCompound nbt) {
        setFluid(AdAstraFluidHelper.loadFluidStackFromNBT(nbt));
        return this;
    }

    @Override
    public void setFluid(FluidStack fluid) {
        super.setFluid(AdAstraFluidHelper.normalizeFluidStack(fluid));
    }

    @Override
    public boolean canFillFluidType(FluidStack fluid) {
        if (fluid == null || fluid.amount <= 0) {
            return false;
        }
        if (fluidFilter != null && !fluidFilter.test(fluid)) {
            return false;
        }
        return super.canFillFluidType(fluid);
    }

    /**
     * Check if this tank can accept a specific fluid.
     */
    public boolean isFluidValid(FluidStack stack) {
        if (stack == null || stack.amount <= 0) {
            return false;
        }
        if (fluidFilter != null) {
            return fluidFilter.test(stack);
        }
        return true;
    }

    /**
     * Get the remaining capacity of this tank.
     */
    public int getSpace() {
        return capacity - getFluidAmount();
    }

    /**
     * Check if the tank is empty.
     */
    public boolean isEmpty() {
        return getFluidAmount() == 0;
    }

    /**
     * Check if the tank is full.
     */
    public boolean isFull() {
        return getFluidAmount() >= capacity;
    }

    /**
     * Get the fill percentage (0.0 to 1.0).
     */
    public float getFillPercentage() {
        if (capacity == 0) return 0f;
        return (float) getFluidAmount() / capacity;
    }

    /**
     * Serialize to NBT with a custom tag name.
     */
    public NBTTagCompound serializeNBT() {
        return writeToNBT(new NBTTagCompound());
    }

    /**
     * Deserialize from NBT with a custom tag name.
     */
    public void deserializeNBT(NBTTagCompound nbt) {
        readFromNBT(nbt);
    }

    /**
     * Create a copy of this tank.
     */
    public AdAstraFluidTank copy() {
        AdAstraFluidTank copy = new AdAstraFluidTank(capacity, fluidFilter);
        if (getFluid() != null) {
            copy.setFluid(getFluid().copy());
        }
        return copy;
    }

    /**
     * Set the fluid directly (bypasses validation).
     */
    public void setFluidDirect(FluidStack stack) {
        this.fluid = stack;
        onContentsChanged();
    }

    /**
     * Clear the tank contents.
     */
    public void clear() {
        setFluid(null);
    }
}
