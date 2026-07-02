package com.wantedxnn.adastra.common.blockentities.machines;

import earth.terrarium.adastra.common.tile.AdAstraMachineTileEntity;
import earth.terrarium.adastra.common.tile.AdAstraSideMode;
import earth.terrarium.adastra.common.config.AdAstraConfig;
import earth.terrarium.adastra.common.recipe.CryoFreezingRecipe;
import earth.terrarium.adastra.common.recipe.CryoFreezingRecipes;
import earth.terrarium.adastra.common.recipe.RecipeRegistry;
import earth.terrarium.adastra.common.registry.ModFluids;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.ItemHandlerHelper;

public class CryoFreezerTileEntity extends AdAstraMachineTileEntity {

    private static final int INPUT_SLOT = 1;
    private static final int EMPTY_CONTAINER_SLOT = 2;
    private static final int FILLED_CONTAINER_SLOT = 3;

    private final FluidTank outputTank = new CryoFuelTank(OSTRUM_FLUID);
    private int cookTime;
    private int cookTimeTotal;
    private CryoFreezingRecipe activeRecipe;

    public CryoFreezerTileEntity() {
        super("cryo_freezer", 4, OSTRUM_ENERGY, OSTRUM_IO, 0, 0);
        setAllSideModes(SideConfigType.ENERGY, AdAstraSideMode.PULL);
        setAllSideModes(SideConfigType.FLUID, AdAstraSideMode.PUSH);
    }

    @Override
    public FluidTank getFluidTank() {
        return outputTank;
    }

    @Override
    protected void tickMachine() {
        if (energy == null) {
            setLit(false);
            return;
        }

        moveTankToOutputContainer();

        if (!canFunction()) {
            setLit(false);
            return;
        }

        CryoFreezingRecipe recipe = getRecipe(items.getStackInSlot(INPUT_SLOT));
        if (recipe == null) {
            cookTime = 0;
            cookTimeTotal = 0;
            activeRecipe = null;
            setLit(false);
            return;
        }

        if (activeRecipe != recipe) {
            cookTime = 0;
            activeRecipe = recipe;
        }
        // Apply config speed multiplier to processing time
        cookTimeTotal = AdAstraConfig.getModifiedProcessingTime(recipe.getProcessingTime());
        if (!canProcess(recipe)) {
            setLit(false);
            return;
        }

        // Apply config energy multiplier to energy consumption
        int modifiedEnergy = AdAstraConfig.getModifiedEnergyConsumption(recipe.getEnergyPerTick());
        energy.extractEnergy(modifiedEnergy, false);
        cookTime++;
        setLit(true);
        if (cookTime >= cookTimeTotal) {
            craft(recipe);
        }
        markDirty();
    }

    private boolean canProcess(CryoFreezingRecipe recipe) {
        int modifiedEnergy = AdAstraConfig.getModifiedEnergyConsumption(recipe.getEnergyPerTick());
        if (energy.extractEnergy(modifiedEnergy, true) < modifiedEnergy) {
            return false;
        }
        return outputTank.fillInternal(new FluidStack(recipe.getOutputFluid(), recipe.getOutputAmount()), false) == recipe.getOutputAmount();
    }

    private void craft(CryoFreezingRecipe recipe) {
        ItemStack input = items.getStackInSlot(INPUT_SLOT);
        input.shrink(1);
        if (input.isEmpty()) {
            items.setStackInSlot(INPUT_SLOT, ItemStack.EMPTY);
        }

        outputTank.fillInternal(new FluidStack(recipe.getOutputFluid(), recipe.getOutputAmount()), true);
        cookTime = 0;
    }

    private CryoFreezingRecipe getRecipe(ItemStack stack) {
        // Try new recipe system first
        CryoFreezingRecipe newRecipe = RecipeRegistry.findCryoFreezingRecipe(stack);
        if (newRecipe != null) {
            return newRecipe;
        }
        // Fallback to legacy system
        CryoFreezingRecipes.Recipe legacyRecipe = CryoFreezingRecipes.find(stack);
        if (legacyRecipe != null) {
            // Wrap legacy recipe in new format
            return new CryoFreezingRecipe(
                legacyRecipe.getId(),
                stack.copy(),
                legacyRecipe.getOutputFluid(),
                legacyRecipe.getOutputAmount(),
                legacyRecipe.getCookingTime(),
                legacyRecipe.getEnergyPerTick()
            );
        }
        return null;
    }

    private void moveTankToOutputContainer() {
        ItemStack stack = items.getStackInSlot(EMPTY_CONTAINER_SLOT);
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
                items.setStackInSlot(EMPTY_CONTAINER_SLOT, ItemStack.EMPTY);
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
        if (index == INPUT_SLOT) {
            return getRecipe(stack) != null;
        }
        if (index == EMPTY_CONTAINER_SLOT) {
            return FluidUtil.getFluidHandler(stack) != null;
        }
        return false;
    }

    @Override
    public int[] getSlotsForFace(EnumFacing side) {
        return new int[]{getBatterySlot(), INPUT_SLOT, EMPTY_CONTAINER_SLOT, FILLED_CONTAINER_SLOT};
    }

    @Override
    public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
        return (isBatterySlot(index) || index == INPUT_SLOT || index == EMPTY_CONTAINER_SLOT) && isItemValidForSlot(index, itemStackIn);
    }

    @Override
    public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
        return index == FILLED_CONTAINER_SLOT;
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
            return (T) outputTank;
        }
        return super.getCapability(capability, facing);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        if (compound.hasKey("FluidTank")) {
            outputTank.readFromNBT(compound.getCompoundTag("FluidTank"));
        }
        cookTime = compound.getInteger("CookTime");
        cookTimeTotal = compound.getInteger("CookTimeTotal");
        if (compound.hasKey("ActiveRecipe")) {
            // The activeRecipe will be set properly on next tick
            activeRecipe = null;
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setTag("FluidTank", outputTank.writeToNBT(new NBTTagCompound()));
        compound.setInteger("CookTime", cookTime);
        compound.setInteger("CookTimeTotal", cookTimeTotal);
        if (activeRecipe != null) {
            compound.setString("ActiveRecipe", activeRecipe.getId());
        }
        return compound;
    }

    @Override
    public int getField(int id) {
        if (id == 2) {
            return outputTank.getFluidAmount();
        }
        if (id == 3) {
            return outputTank.getCapacity();
        }
        if (id == 4) {
            return cookTime;
        }
        if (id == 5) {
            return cookTimeTotal;
        }
        return super.getField(id);
    }

    @Override
    public void setField(int id, int value) {
        if (id == 4) {
            cookTime = value;
        } else if (id == 5) {
            cookTimeTotal = value;
        } else {
            super.setField(id, value);
        }
    }

    @Override
    public int getFieldCount() {
        return 6;
    }

    private final class CryoFuelTank extends FluidTank {

        private CryoFuelTank(int capacity) {
            super(capacity);
            setCanFill(false);
            setCanDrain(true);
        }

        @Override
        public boolean canFillFluidType(FluidStack fluid) {
            return fluid != null && fluid.getFluid() == ModFluids.CRYO_FUEL;
        }

        @Override
        public boolean canDrainFluidType(FluidStack fluid) {
            return fluid != null && fluid.getFluid() == ModFluids.CRYO_FUEL;
        }

        @Override
        protected void onContentsChanged() {
            markDirty();
        }
    }

}
