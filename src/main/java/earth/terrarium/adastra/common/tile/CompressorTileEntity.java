package earth.terrarium.adastra.common.tile;

import earth.terrarium.adastra.common.recipe.CompressingRecipes;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.items.ItemHandlerHelper;

public class CompressorTileEntity extends AdAstraMachineTileEntity {

    private static final int INPUT_SLOT = 1;
    private static final int OUTPUT_SLOT = 2;

    private int cookTime;
    private int cookTimeTotal;
    private CompressingRecipes.Recipe activeRecipe;

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

        CompressingRecipes.Recipe recipe = getRecipe(items.getStackInSlot(INPUT_SLOT));
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
        cookTimeTotal = recipe.getCookingTime();
        if (!canProcess(recipe)) {
            setLit(false);
            return;
        }

        energy.extractEnergy(recipe.getEnergyPerTick(), false);
        cookTime++;
        setLit(true);
        if (cookTime >= cookTimeTotal) {
            craft(recipe);
        }
        markDirty();
    }

    private boolean canProcess(CompressingRecipes.Recipe recipe) {
        if (energy.extractEnergy(recipe.getEnergyPerTick(), true) < recipe.getEnergyPerTick()) {
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

    private void craft(CompressingRecipes.Recipe recipe) {
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

    private CompressingRecipes.Recipe getRecipe(ItemStack stack) {
        return CompressingRecipes.find(stack);
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
            activeRecipe = CompressingRecipes.getById(compound.getString("ActiveRecipe"));
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
