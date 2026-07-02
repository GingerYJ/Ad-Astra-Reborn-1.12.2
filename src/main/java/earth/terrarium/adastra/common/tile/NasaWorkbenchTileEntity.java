package earth.terrarium.adastra.common.tile;

import earth.terrarium.adastra.common.recipe.NASAWorkbenchRecipe;
import earth.terrarium.adastra.common.recipe.RecipeRegistry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;

public class NasaWorkbenchTileEntity extends AdAstraMachineTileEntity {

    private static final int FIRST_INPUT_SLOT = 0;
    private static final int LAST_INPUT_SLOT = 13;
    private static final int OUTPUT_SLOT = 14;
    private static final int[] SLOTS_FOR_FACE = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14};

    private NASAWorkbenchRecipe activeRecipe;

    public NasaWorkbenchTileEntity() {
        super("nasa_workbench", 15, 0, 0, 0, 0);
    }

    @Override
    protected void tickMachine() {
        NASAWorkbenchRecipe recipe = getRecipe();
        setActiveRecipe(recipe);
    }

    private NASAWorkbenchRecipe getRecipe() {
        return RecipeRegistry.findNASAWorkbenchRecipe(getInputStacks());
    }

    private void setActiveRecipe(NASAWorkbenchRecipe recipe) {
        if (activeRecipe != recipe) {
            activeRecipe = recipe;
            markDirty();
        }
    }

    public boolean isOutputSlot(int index) {
        return index == OUTPUT_SLOT;
    }

    public boolean hasCraftingResult() {
        return getRecipe() != null;
    }

    public ItemStack getCraftingResult() {
        NASAWorkbenchRecipe recipe = getRecipe();
        return recipe == null ? ItemStack.EMPTY : recipe.getResult();
    }

    public boolean craftActiveRecipe(EntityPlayer player) {
        NASAWorkbenchRecipe recipe = getRecipe();
        if (recipe == null) {
            setActiveRecipe(null);
            return false;
        }

        ItemStack[] inputs = getInputStacks();
        if (!recipe.consumeInputs(inputs)) {
            setActiveRecipe(null);
            return false;
        }

        for (int slot = FIRST_INPUT_SLOT; slot <= LAST_INPUT_SLOT; slot++) {
            items.setStackInSlot(slot, inputs[slot].isEmpty() ? ItemStack.EMPTY : inputs[slot]);
        }

        items.setStackInSlot(OUTPUT_SLOT, ItemStack.EMPTY);
        setActiveRecipe(getRecipe());
        markDirty();
        return true;
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        if (stack.isEmpty() || index < FIRST_INPUT_SLOT || index > LAST_INPUT_SLOT) {
            return false;
        }
        for (NASAWorkbenchRecipe recipe : RecipeRegistry.getAllNASAWorkbenchRecipes()) {
            if (recipe.isValidForSlot(index, stack)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int[] getSlotsForFace(EnumFacing side) {
        return SLOTS_FOR_FACE.clone();
    }

    @Override
    public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
        return index >= FIRST_INPUT_SLOT && index <= LAST_INPUT_SLOT && isItemValidForSlot(index, itemStackIn);
    }

    @Override
    public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
        return false;
    }

    @Override
    public ItemStack getStackInSlot(int index) {
        if (index == OUTPUT_SLOT) {
            return getCraftingResult();
        }
        return super.getStackInSlot(index);
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        if (index == OUTPUT_SLOT) {
            ItemStack result = getCraftingResult();
            if (!result.isEmpty() && count < result.getCount()) {
                result.setCount(count);
            }
            return result;
        }
        return super.decrStackSize(index, count);
    }

    @Override
    public ItemStack removeStackFromSlot(int index) {
        if (index == OUTPUT_SLOT) {
            return getCraftingResult();
        }
        return super.removeStackFromSlot(index);
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        if (index == OUTPUT_SLOT) {
            items.setStackInSlot(OUTPUT_SLOT, ItemStack.EMPTY);
            return;
        }
        super.setInventorySlotContents(index, stack);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        items.setStackInSlot(OUTPUT_SLOT, ItemStack.EMPTY);
        activeRecipe = compound.hasKey("ActiveRecipe") ? RecipeRegistry.findNASAWorkbenchRecipe(compound.getString("ActiveRecipe")) : null;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        if (activeRecipe != null) {
            compound.setString("ActiveRecipe", activeRecipe.getId());
        }
        return compound;
    }

    private ItemStack[] getInputStacks() {
        ItemStack[] inputs = new ItemStack[LAST_INPUT_SLOT - FIRST_INPUT_SLOT + 1];
        for (int slot = FIRST_INPUT_SLOT; slot <= LAST_INPUT_SLOT; slot++) {
            inputs[slot - FIRST_INPUT_SLOT] = items.getStackInSlot(slot);
        }
        return inputs;
    }
}

