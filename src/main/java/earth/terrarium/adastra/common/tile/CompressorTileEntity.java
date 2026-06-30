package earth.terrarium.adastra.common.tile;

import earth.terrarium.adastra.common.registry.ModBlocks;
import earth.terrarium.adastra.common.registry.ModItems;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.items.ItemHandlerHelper;

public class CompressorTileEntity extends AdAstraMachineTileEntity {

    private static final int INPUT_SLOT = 1;
    private static final int OUTPUT_SLOT = 2;
    private static final int ENERGY_PER_TICK = 20;

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
        cookTimeTotal = recipe.cookingTime;
        if (!canProcess(recipe)) {
            setLit(false);
            return;
        }

        energy.extractEnergy(recipe.energyPerTick, false);
        cookTime++;
        setLit(true);
        if (cookTime >= cookTimeTotal) {
            craft(recipe);
        }
        markDirty();
    }

    private boolean canProcess(CompressingRecipe recipe) {
        if (energy.extractEnergy(recipe.energyPerTick, true) < recipe.energyPerTick) {
            return false;
        }

        ItemStack output = items.getStackInSlot(OUTPUT_SLOT);
        if (output.isEmpty()) {
            return true;
        }
        return ItemHandlerHelper.canItemStacksStack(output, recipe.result)
            && output.getCount() + recipe.result.getCount() <= Math.min(output.getMaxStackSize(), items.getSlotLimit(OUTPUT_SLOT));
    }

    private void craft(CompressingRecipe recipe) {
        ItemStack input = items.getStackInSlot(INPUT_SLOT);
        input.shrink(1);
        if (input.isEmpty()) {
            items.setStackInSlot(INPUT_SLOT, ItemStack.EMPTY);
        }

        ItemStack output = items.getStackInSlot(OUTPUT_SLOT);
        if (output.isEmpty()) {
            items.setStackInSlot(OUTPUT_SLOT, recipe.result.copy());
        } else {
            output.grow(recipe.result.getCount());
        }
        cookTime = 0;
    }

    private CompressingRecipe getRecipe(ItemStack stack) {
        if (stack.isEmpty()) {
            return null;
        }

        Item item = stack.getItem();
        if (item == Items.IRON_INGOT) {
            return CompressingRecipe.IRON_INGOT;
        }
        if (item == Item.getItemFromBlock(Blocks.IRON_BLOCK)) {
            return CompressingRecipe.IRON_BLOCK;
        }
        if (item == ModItems.STEEL_INGOT) {
            return CompressingRecipe.STEEL_INGOT;
        }
        if (item == Item.getItemFromBlock(ModBlocks.STEEL_BLOCK)) {
            return CompressingRecipe.STEEL_BLOCK;
        }
        if (item == ModItems.DESH_INGOT) {
            return CompressingRecipe.DESH_INGOT;
        }
        if (item == Item.getItemFromBlock(ModBlocks.DESH_BLOCK)) {
            return CompressingRecipe.DESH_BLOCK;
        }
        if (item == ModItems.OSTRUM_INGOT) {
            return CompressingRecipe.OSTRUM_INGOT;
        }
        if (item == Item.getItemFromBlock(ModBlocks.OSTRUM_BLOCK)) {
            return CompressingRecipe.OSTRUM_BLOCK;
        }
        if (item == ModItems.CALORITE_INGOT) {
            return CompressingRecipe.CALORITE_INGOT;
        }
        if (item == Item.getItemFromBlock(ModBlocks.CALORITE_BLOCK)) {
            return CompressingRecipe.CALORITE_BLOCK;
        }
        return null;
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return index == INPUT_SLOT && getRecipe(stack) != null;
    }

    @Override
    public int[] getSlotsForFace(EnumFacing side) {
        return new int[]{INPUT_SLOT, OUTPUT_SLOT};
    }

    @Override
    public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
        return index == INPUT_SLOT && isItemValidForSlot(index, itemStackIn);
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
            try {
                activeRecipe = CompressingRecipe.valueOf(compound.getString("ActiveRecipe"));
            } catch (IllegalArgumentException ignored) {
                activeRecipe = null;
            }
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setInteger("CookTime", cookTime);
        compound.setInteger("CookTimeTotal", cookTimeTotal);
        if (activeRecipe != null) {
            compound.setString("ActiveRecipe", activeRecipe.name());
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

    private enum CompressingRecipe {
        IRON_INGOT(100, new ItemStack(ModItems.IRON_PLATE)),
        IRON_BLOCK(800, new ItemStack(ModItems.IRON_PLATE, 9)),
        STEEL_INGOT(100, new ItemStack(ModItems.STEEL_PLATE)),
        STEEL_BLOCK(800, new ItemStack(ModItems.STEEL_PLATE, 9)),
        DESH_INGOT(100, new ItemStack(ModItems.DESH_PLATE)),
        DESH_BLOCK(800, new ItemStack(ModItems.DESH_PLATE, 9)),
        OSTRUM_INGOT(100, new ItemStack(ModItems.OSTRUM_PLATE)),
        OSTRUM_BLOCK(800, new ItemStack(ModItems.OSTRUM_PLATE, 9)),
        CALORITE_INGOT(100, new ItemStack(ModItems.CALORITE_PLATE)),
        CALORITE_BLOCK(800, new ItemStack(ModItems.CALORITE_PLATE, 9));

        private final int cookingTime;
        private final ItemStack result;
        private final int energyPerTick = ENERGY_PER_TICK;

        CompressingRecipe(int cookingTime, ItemStack result) {
            this.cookingTime = cookingTime;
            this.result = result;
        }
    }
}
