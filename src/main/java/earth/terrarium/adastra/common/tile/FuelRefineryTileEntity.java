package earth.terrarium.adastra.common.tile;

import earth.terrarium.adastra.common.config.AdAstraConfig;
import earth.terrarium.adastra.common.registry.ModFluids;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.FluidTankProperties;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.items.ItemHandlerHelper;

public class FuelRefineryTileEntity extends AdAstraMachineTileEntity {

    private static final int INPUT_CONTAINER_SLOT = 1;
    private static final int EMPTY_CONTAINER_SLOT = 2;
    private static final int OUTPUT_CONTAINER_SLOT = 3;
    private static final int FILLED_CONTAINER_SLOT = 4;
    private static final int ENERGY_PER_OPERATION = 30;
    private static final int INPUT_PER_OPERATION = 5;
    private static final int OUTPUT_PER_OPERATION = 5;

    private final FluidTank inputTank = new RestrictedTank(STEEL_FLUID, ModFluids.OIL, true, false);
    private final FluidTank outputTank = new RestrictedTank(STEEL_FLUID, ModFluids.FUEL, false, true);
    private final IFluidHandler fluidHandler = new RefineryFluidHandler();

    public FuelRefineryTileEntity() {
        super("fuel_refinery", 5, STEEL_ENERGY, STEEL_IO, 0, 0);
        setAllSideModes(SideConfigType.ENERGY, AdAstraSideMode.PULL);
        setAllSideModes(SideConfigType.FLUID, AdAstraSideMode.PUSH_PULL);
    }

    @Override
    public FluidTank getFluidTank() {
        return inputTank;
    }

    public FluidTank getInputTank() {
        return inputTank;
    }

    public FluidTank getOutputTank() {
        return outputTank;
    }

    @Override
    protected void tickMachine() {
        if (energy == null || !canFunction()) {
            setLit(false);
            return;
        }

        moveInputContainerToTank();
        moveTankToOutputContainer();

        boolean refined = refineFuel();
        setLit(refined);
        if (refined) {
            markDirty();
        }
    }

    private boolean refineFuel() {
        int modifiedEnergy = AdAstraConfig.getModifiedEnergyConsumption(ENERGY_PER_OPERATION);
        if (energy.internalExtractEnergy(modifiedEnergy, true) < modifiedEnergy) {
            return false;
        }
        FluidStack input = inputTank.getFluid();
        if (input == null || input.getFluid() != ModFluids.OIL || input.amount < INPUT_PER_OPERATION) {
            return false;
        }
        if (outputTank.fillInternal(new FluidStack(ModFluids.FUEL, OUTPUT_PER_OPERATION), false) < OUTPUT_PER_OPERATION) {
            return false;
        }

        energy.internalExtractEnergy(modifiedEnergy, false);
        inputTank.drainInternal(INPUT_PER_OPERATION, true);
        outputTank.fillInternal(new FluidStack(ModFluids.FUEL, OUTPUT_PER_OPERATION), true);
        return true;
    }

    private void moveInputContainerToTank() {
        ItemStack stack = items.getStackInSlot(INPUT_CONTAINER_SLOT);
        if (stack.isEmpty()) {
            return;
        }

        ItemStack single = stack.copy();
        single.setCount(1);
        FluidActionResult simulated = FluidUtil.tryEmptyContainer(single, inputTank, inputTank.getCapacity(), null, false);
        if (simulated.isSuccess() && storeContainerResult(EMPTY_CONTAINER_SLOT, simulated.getResult())) {
            FluidActionResult result = FluidUtil.tryEmptyContainer(single, inputTank, inputTank.getCapacity(), null, true);
            if (!result.isSuccess()) {
                return;
            }
            stack.shrink(1);
            if (stack.isEmpty()) {
                items.setStackInSlot(INPUT_CONTAINER_SLOT, ItemStack.EMPTY);
            }
            insertContainerResult(EMPTY_CONTAINER_SLOT, result.getResult());
            markDirty();
        }
    }

    private void moveTankToOutputContainer() {
        ItemStack stack = items.getStackInSlot(OUTPUT_CONTAINER_SLOT);
        if (stack.isEmpty()) {
            return;
        }

        ItemStack single = stack.copy();
        single.setCount(1);
        FluidActionResult simulated = FluidUtil.tryFillContainer(single, outputTank, outputTank.getCapacity(), null, false);
        if (simulated.isSuccess() && storeContainerResult(FILLED_CONTAINER_SLOT, simulated.getResult())) {
            FluidActionResult result = FluidUtil.tryFillContainer(single, outputTank, outputTank.getCapacity(), null, true);
            if (!result.isSuccess()) {
                return;
            }
            stack.shrink(1);
            if (stack.isEmpty()) {
                items.setStackInSlot(OUTPUT_CONTAINER_SLOT, ItemStack.EMPTY);
            }
            insertContainerResult(FILLED_CONTAINER_SLOT, result.getResult());
            markDirty();
        }
    }

    private boolean storeContainerResult(int slot, ItemStack result) {
        if (result.isEmpty()) {
            return true;
        }

        ItemStack stored = items.getStackInSlot(slot);
        if (stored.isEmpty()) {
            return true;
        }
        return ItemHandlerHelper.canItemStacksStack(stored, result)
            && stored.getCount() + result.getCount() <= Math.min(stored.getMaxStackSize(), items.getSlotLimit(slot));
    }

    private void insertContainerResult(int slot, ItemStack result) {
        if (result.isEmpty()) {
            return;
        }

        ItemStack stored = items.getStackInSlot(slot);
        if (stored.isEmpty()) {
            items.setStackInSlot(slot, result.copy());
        } else {
            stored.grow(result.getCount());
        }
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }
        if (isValidBatterySlotItem(index, stack)) {
            return true;
        }
        if (index == INPUT_CONTAINER_SLOT) {
            FluidStack contained = FluidUtil.getFluidContained(stack);
            return contained != null && contained.getFluid() == ModFluids.OIL;
        }
        if (index == OUTPUT_CONTAINER_SLOT) {
            return FluidUtil.getFluidHandler(stack) != null;
        }
        return false;
    }

    @Override
    public int[] getSlotsForFace(EnumFacing side) {
        return new int[]{getBatterySlot(), INPUT_CONTAINER_SLOT, EMPTY_CONTAINER_SLOT, OUTPUT_CONTAINER_SLOT, FILLED_CONTAINER_SLOT};
    }

    @Override
    public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
        return (isBatterySlot(index) || index == INPUT_CONTAINER_SLOT || index == OUTPUT_CONTAINER_SLOT) && isItemValidForSlot(index, itemStackIn);
    }

    @Override
    public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
        return index == EMPTY_CONTAINER_SLOT || index == FILLED_CONTAINER_SLOT;
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            return true;
        }
        return super.hasCapability(capability, facing);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            return (T) fluidHandler;
        }
        return super.getCapability(capability, facing);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        if (compound.hasKey("InputTank")) {
            inputTank.readFromNBT(compound.getCompoundTag("InputTank"));
        }
        if (compound.hasKey("OutputTank")) {
            outputTank.readFromNBT(compound.getCompoundTag("OutputTank"));
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setTag("InputTank", inputTank.writeToNBT(new NBTTagCompound()));
        compound.setTag("OutputTank", outputTank.writeToNBT(new NBTTagCompound()));
        return compound;
    }

    @Override
    public int getField(int id) {
        if (id == 2) {
            return inputTank.getFluidAmount();
        }
        if (id == 3) {
            return inputTank.getCapacity();
        }
        if (id == 4) {
            return outputTank.getFluidAmount();
        }
        if (id == 5) {
            return outputTank.getCapacity();
        }
        return super.getField(id);
    }

    @Override
    public int getFieldCount() {
        return 6;
    }

    private final class RestrictedTank extends FluidTank {

        private final Fluid allowedFluid;

        private RestrictedTank(int capacity, Fluid allowedFluid, boolean canFill, boolean canDrain) {
            super(capacity);
            this.allowedFluid = allowedFluid;
            setCanFill(canFill);
            setCanDrain(canDrain);
        }

        @Override
        public boolean canFillFluidType(FluidStack fluid) {
            return fluid != null && fluid.getFluid() == allowedFluid;
        }

        @Override
        public boolean canDrainFluidType(FluidStack fluid) {
            return fluid != null && fluid.getFluid() == allowedFluid;
        }

        @Override
        protected void onContentsChanged() {
            markDirty();
        }
    }

    private final class RefineryFluidHandler implements IFluidHandler {

        @Override
        public IFluidTankProperties[] getTankProperties() {
            return new IFluidTankProperties[]{
                new FluidTankProperties(inputTank.getFluid(), inputTank.getCapacity(), true, false),
                new FluidTankProperties(outputTank.getFluid(), outputTank.getCapacity(), false, true)
            };
        }

        @Override
        public int fill(FluidStack resource, boolean doFill) {
            if (resource == null || resource.getFluid() != ModFluids.OIL) {
                return 0;
            }
            return inputTank.fill(resource, doFill);
        }

        @Override
        public FluidStack drain(FluidStack resource, boolean doDrain) {
            if (resource == null || resource.getFluid() != ModFluids.FUEL) {
                return null;
            }
            return outputTank.drain(resource, doDrain);
        }

        @Override
        public FluidStack drain(int maxDrain, boolean doDrain) {
            return outputTank.drain(maxDrain, doDrain);
        }
    }
}
