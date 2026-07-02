package com.wantedxnn.adastra.common.blockentities.machines;

import earth.terrarium.adastra.common.tile.AdAstraMachineTileEntity;

import earth.terrarium.adastra.common.registry.ModBlocks;
import earth.terrarium.adastra.common.registry.ModItems;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.oredict.OreDictionary;

public class NasaWorkbenchTileEntity extends AdAstraMachineTileEntity {

    private static final int FIRST_INPUT_SLOT = 0;
    private static final int LAST_INPUT_SLOT = 13;
    private static final int OUTPUT_SLOT = 14;
    private static final int[] SLOTS_FOR_FACE = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14};

    private NasaWorkbenchRecipe activeRecipe;

    public NasaWorkbenchTileEntity() {
        super("nasa_workbench", 15, 0, 0, 0, 0);
    }

    @Override
    protected void tickMachine() {
        NasaWorkbenchRecipe recipe = getRecipe();
        setActiveRecipe(recipe);
        setLit(recipe != null);

        if (recipe != null && canCraft(recipe)) {
            craft(recipe);
            setActiveRecipe(getRecipe());
            setLit(activeRecipe != null);
        }
    }

    private NasaWorkbenchRecipe getRecipe() {
        for (NasaWorkbenchRecipe recipe : NasaWorkbenchRecipe.values()) {
            if (recipe.matches(this)) {
                return recipe;
            }
        }
        return null;
    }

    private void setActiveRecipe(NasaWorkbenchRecipe recipe) {
        if (activeRecipe != recipe) {
            activeRecipe = recipe;
            markDirty();
        }
    }

    private boolean canCraft(NasaWorkbenchRecipe recipe) {
        return recipe.matches(this) && canStoreResult(recipe.result);
    }

    private void craft(NasaWorkbenchRecipe recipe) {
        for (int slot = FIRST_INPUT_SLOT; slot <= LAST_INPUT_SLOT; slot++) {
            ItemStack stack = items.getStackInSlot(slot);
            stack.shrink(1);
            if (stack.isEmpty()) {
                items.setStackInSlot(slot, ItemStack.EMPTY);
            }
        }

        addResult(recipe.result);
        markDirty();
    }

    private boolean canStoreResult(ItemStack result) {
        ItemStack output = items.getStackInSlot(OUTPUT_SLOT);
        if (output.isEmpty()) {
            return true;
        }
        return ItemHandlerHelper.canItemStacksStack(output, result)
            && output.getCount() + result.getCount() <= Math.min(output.getMaxStackSize(), items.getSlotLimit(OUTPUT_SLOT));
    }

    private void addResult(ItemStack result) {
        ItemStack output = items.getStackInSlot(OUTPUT_SLOT);
        if (output.isEmpty()) {
            items.setStackInSlot(OUTPUT_SLOT, result.copy());
        } else {
            output.grow(result.getCount());
        }
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        if (stack.isEmpty() || index < FIRST_INPUT_SLOT || index > LAST_INPUT_SLOT) {
            return false;
        }
        for (NasaWorkbenchRecipe recipe : NasaWorkbenchRecipe.values()) {
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
        return index == OUTPUT_SLOT && !stack.isEmpty();
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        activeRecipe = compound.hasKey("ActiveRecipe") ? NasaWorkbenchRecipe.byName(compound.getString("ActiveRecipe")) : null;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        if (activeRecipe != null) {
            compound.setString("ActiveRecipe", activeRecipe.name());
        }
        return compound;
    }

    private enum NasaWorkbenchRecipe {
        TIER_1("blockSteel", ModBlocks.STEEL_BLOCK, ModItems.STEEL_TANK, ModItems.STEEL_ENGINE, ModItems.TIER_1_ROCKET),
        TIER_2("blockDesh", ModBlocks.DESH_BLOCK, ModItems.DESH_TANK, ModItems.DESH_ENGINE, ModItems.TIER_2_ROCKET),
        TIER_3("blockOstrum", ModBlocks.OSTRUM_BLOCK, ModItems.OSTRUM_TANK, ModItems.OSTRUM_ENGINE, ModItems.TIER_3_ROCKET),
        TIER_4("blockCalorite", ModBlocks.CALORITE_BLOCK, ModItems.CALORITE_TANK, ModItems.CALORITE_ENGINE, ModItems.TIER_4_ROCKET);

        private final String bodyOre;
        private final Block bodyBlock;
        private final Item tank;
        private final Item engine;
        private final ItemStack result;

        NasaWorkbenchRecipe(String bodyOre, Block bodyBlock, Item tank, Item engine, Item result) {
            this.bodyOre = bodyOre;
            this.bodyBlock = bodyBlock;
            this.tank = tank;
            this.engine = engine;
            this.result = new ItemStack(result);
        }

        private boolean matches(NasaWorkbenchTileEntity tile) {
            return isItem(tile.items.getStackInSlot(0), ModItems.ROCKET_NOSE_CONE)
                && matchesBody(tile.items.getStackInSlot(1))
                && matchesBody(tile.items.getStackInSlot(2))
                && matchesBody(tile.items.getStackInSlot(3))
                && matchesBody(tile.items.getStackInSlot(4))
                && matchesBody(tile.items.getStackInSlot(5))
                && matchesBody(tile.items.getStackInSlot(6))
                && isItem(tile.items.getStackInSlot(7), ModItems.ROCKET_FIN)
                && isItem(tile.items.getStackInSlot(8), tank)
                && isItem(tile.items.getStackInSlot(9), tank)
                && isItem(tile.items.getStackInSlot(10), ModItems.ROCKET_FIN)
                && isItem(tile.items.getStackInSlot(11), ModItems.ROCKET_FIN)
                && isItem(tile.items.getStackInSlot(12), engine)
                && isItem(tile.items.getStackInSlot(13), ModItems.ROCKET_FIN);
        }

        private boolean isValidForSlot(int slot, ItemStack stack) {
            switch (slot) {
                case 0:
                    return isItem(stack, ModItems.ROCKET_NOSE_CONE);
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                    return matchesBody(stack);
                case 7:
                case 10:
                case 11:
                case 13:
                    return isItem(stack, ModItems.ROCKET_FIN);
                case 8:
                case 9:
                    return isItem(stack, tank);
                case 12:
                    return isItem(stack, engine);
                default:
                    return false;
            }
        }

        private boolean matchesBody(ItemStack stack) {
            return isBlock(stack, bodyBlock) || isOre(stack, bodyOre);
        }

        private static boolean isItem(ItemStack stack, Item item) {
            return !stack.isEmpty() && stack.getItem() == item;
        }

        private static boolean isBlock(ItemStack stack, Block block) {
            return isItem(stack, Item.getItemFromBlock(block));
        }

        private static boolean isOre(ItemStack stack, String oreName) {
            if (stack.isEmpty()) {
                return false;
            }
            int oreId = OreDictionary.getOreID(oreName);
            for (int stackOreId : OreDictionary.getOreIDs(stack)) {
                if (stackOreId == oreId) {
                    return true;
                }
            }
            return false;
        }

        private static NasaWorkbenchRecipe byName(String name) {
            for (NasaWorkbenchRecipe recipe : values()) {
                if (recipe.name().equals(name)) {
                    return recipe;
                }
            }
            return null;
        }
    }
}
