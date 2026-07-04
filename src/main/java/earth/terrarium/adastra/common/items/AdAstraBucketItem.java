package earth.terrarium.adastra.common.items;

import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class AdAstraBucketItem extends ItemBucket {

    private final Fluid fluid;

    public AdAstraBucketItem(Block fluidBlock, Fluid fluid) {
        super(fluidBlock);
        this.fluid = fluid;
    }

    @Override
    public ICapabilityProvider initCapabilities(@Nonnull ItemStack stack, @Nullable NBTTagCompound nbt) {
        return new FilledBucketHandler(stack, fluid);
    }

    private static class FilledBucketHandler implements IFluidHandlerItem, ICapabilityProvider {

        private final Fluid fluid;
        private ItemStack container;

        private FilledBucketHandler(ItemStack container, Fluid fluid) {
            this.container = container;
            this.fluid = fluid;
        }

        @Nonnull
        @Override
        public ItemStack getContainer() {
            return container;
        }

        @Override
        public IFluidTankProperties[] getTankProperties() {
            return new IFluidTankProperties[] { new BucketTankProperties(fluid) };
        }

        @Override
        public int fill(FluidStack resource, boolean doFill) {
            return 0;
        }

        @Nullable
        @Override
        public FluidStack drain(FluidStack resource, boolean doDrain) {
            if (resource == null || resource.amount < Fluid.BUCKET_VOLUME || !resource.isFluidEqual(getFluid())) {
                return null;
            }
            return drain(Fluid.BUCKET_VOLUME, doDrain);
        }

        @Nullable
        @Override
        public FluidStack drain(int maxDrain, boolean doDrain) {
            if (maxDrain < Fluid.BUCKET_VOLUME || container.getCount() != 1) {
                return null;
            }
            FluidStack drained = getFluid();
            if (doDrain) {
                container = new ItemStack(Items.BUCKET);
            }
            return drained;
        }

        @Override
        public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
            return capability == CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY;
        }

        @Nullable
        @Override
        @SuppressWarnings("unchecked")
        public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
            return capability == CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY ? (T) this : null;
        }

        private FluidStack getFluid() {
            return new FluidStack(fluid, Fluid.BUCKET_VOLUME);
        }
    }

    private static class BucketTankProperties implements IFluidTankProperties {

        private final Fluid fluid;

        private BucketTankProperties(Fluid fluid) {
            this.fluid = fluid;
        }

        @Nullable
        @Override
        public FluidStack getContents() {
            return new FluidStack(fluid, Fluid.BUCKET_VOLUME);
        }

        @Override
        public int getCapacity() {
            return Fluid.BUCKET_VOLUME;
        }

        @Override
        public boolean canFill() {
            return false;
        }

        @Override
        public boolean canDrain() {
            return true;
        }

        @Override
        public boolean canFillFluidType(FluidStack fluidStack) {
            return false;
        }

        @Override
        public boolean canDrainFluidType(FluidStack fluidStack) {
            return fluidStack != null && fluidStack.getFluid() == fluid;
        }
    }
}
