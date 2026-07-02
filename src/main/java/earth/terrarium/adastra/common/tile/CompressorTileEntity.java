package earth.terrarium.adastra.common.tile;

import earth.terrarium.adastra.common.config.AdAstraConfig;
import earth.terrarium.adastra.common.recipe.CompressingRecipe;
import earth.terrarium.adastra.common.recipe.CompressingRecipes;
import earth.terrarium.adastra.common.recipe.RecipeRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.items.ItemHandlerHelper;

public class CompressorTileEntity extends AdAstraMachineTileEntity {

    private static final int INPUT_SLOT = 1;
    private static final int OUTPUT_SLOT = 2;

    private int cookTime;
    private int cookTimeTotal;
    private CompressingRecipe activeRecipe;

    public CompressorTileEntity() {
        super("compressor", 3, IRON_ENERGY, IRON_IO, 0, 0);
        setAllSideModes(SideConfigType.ENERGY, AdAstraSideMode.PULL);
    }

    @Override
    protected void tickMachine() {
        if (energy == null || !canFunction()) {
            setLit(false);
            return;
        }

        CompressingRecipe recipe = getRecipe(items.getStackInSlot(INPUT_SLOT));
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

    private boolean canProcess(CompressingRecipe recipe) {
        int modifiedEnergy = AdAstraConfig.getModifiedEnergyConsumption(recipe.getEnergyPerTick());
        if (energy.extractEnergy(modifiedEnergy, true) < modifiedEnergy) {
            return false;
        }

        ItemStack output = items.getStackInSlot(OUTPUT_SLOT);
        if (output.isEmpty()) {
            return true;
        }
        ItemStack result = recipe.getResult();
        return ItemHandlerHelper.canItemStacksStack(output, result)
            && output.getCount() + result.getCount() <= Math.min(output.getMaxStackSize(), items.getSlotLimit(OUTPUT_SLOT));
    }

    private void craft(CompressingRecipe recipe) {
        ItemStack input = items.getStackInSlot(INPUT_SLOT);
        input.shrink(1);
        if (input.isEmpty()) {
            items.setStackInSlot(INPUT_SLOT, ItemStack.EMPTY);
        }

        ItemStack output = items.getStackInSlot(OUTPUT_SLOT);
        if (output.isEmpty()) {
            items.setStackInSlot(OUTPUT_SLOT, recipe.getResult().copy());
        } else {
            output.grow(recipe.getResult().getCount());
        }
        cookTime = 0;
    }

    private CompressingRecipe getRecipe(ItemStack stack) {
        // Try new recipe system first
        CompressingRecipe newRecipe = RecipeRegistry.findCompressingRecipe(stack);
        if (newRecipe != null) {
            return newRecipe;
        }
        // Fallback to legacy system
        CompressingRecipes.Recipe legacyRecipe = CompressingRecipes.find(stack);
        if (legacyRecipe != null) {
            // Wrap legacy recipe in new format
            return new CompressingRecipe(
                legacyRecipe.getId(),
                stack.copy(),
                legacyRecipe.getResult(),
                legacyRecipe.getCookingTime(),
                legacyRecipe.getEnergyPerTick()
            );
        }
        return null;
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        if (isValidBatterySlotItem(index, stack)) {
            return true;
        }
        return index == INPUT_SLOT && getRecipe(stack) != null;
    }

    @Override
    public int[] getSlotsForFace(EnumFacing side) {
        return new int[]{getBatterySlot(), INPUT_SLOT, OUTPUT_SLOT};
    }

    @Override
    public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
        return (isBatterySlot(index) || index == INPUT_SLOT) && isItemValidForSlot(index, itemStackIn);
    }

    @Override
    public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
        return index == OUTPUT_SLOT;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        cookTime = compound.getInteger("CookTime");
        cookTimeTotal = compound.getInteger("CookTimeTotal");
        if (compound.hasKey("ActiveRecipe")) {
            String recipeId = compound.getString("ActiveRecipe");
            // Try to find in new registry
            activeRecipe = RecipeRegistry.findCompressingRecipe(ItemStack.EMPTY);
            // The activeRecipe will be set properly on next tick
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setInteger("CookTime", cookTime);
        compound.setInteger("CookTimeTotal", cookTimeTotal);
        if (activeRecipe != null) {
            compound.setString("ActiveRecipe", activeRecipe.getId());
        }
        return compound;
    }

    @Override
    public int getField(int id) {
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
}
