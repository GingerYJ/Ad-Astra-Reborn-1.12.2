package com.wantedxnn.adastra.common.blockentities.machines;

import earth.terrarium.adastra.common.tile.AdAstraMachineTileEntity;
import earth.terrarium.adastra.common.tile.AdAstraSideMode;

import earth.terrarium.adastra.common.config.AdAstraConfig;
import earth.terrarium.adastra.common.recipe.AlloySmeltingRecipe;
import earth.terrarium.adastra.common.recipe.RecipeRegistry;
import earth.terrarium.adastra.common.registry.ModBlocks;
import earth.terrarium.adastra.common.registry.ModItems;
import net.minecraft.block.Block;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.oredict.OreDictionary;

public class EtrionicBlastFurnaceTileEntity extends AdAstraMachineTileEntity {

    private static final int FIRST_INPUT_SLOT = 1;
    private static final int LAST_INPUT_SLOT = 4;
    private static final int FIRST_OUTPUT_SLOT = 5;
    private static final int LAST_OUTPUT_SLOT = 8;
    private static final int ALLOYING_ENERGY_PER_TICK = 20;
    private static final int ALLOYING_COOK_TIME = 100;
    private static final int BLASTING_ENERGY_PER_TICK = 20;
    private static final int BLASTING_COOK_TIME = 100;

    private FurnaceMode mode = FurnaceMode.ALLOYING;
    private int cookTime;
    private int cookTimeTotal;
    private BlastingRecipe activeBlastingRecipe;
    private int activeBlastingSlot = -1;

    public EtrionicBlastFurnaceTileEntity() {
        super("etrionic_blast_furnace", 9, STEEL_ENERGY, STEEL_IO, 0, 0);
        setAllSideModes(SideConfigType.ENERGY, AdAstraSideMode.PULL);
    }

    @Override
    protected void tickMachine() {
        if (energy == null || !canFunction()) {
            setLit(false);
            return;
        }

        if (mode == FurnaceMode.BLASTING) {
            tickBlasting();
            return;
        }

        tickAlloying();
    }

    private void tickAlloying() {
        // Try to find a matching recipe from the registry
        AlloySmeltingRecipe recipe = findAlloyingRecipe();
        if (recipe == null) {
            cookTime = 0;
            cookTimeTotal = 0;
            setLit(false);
            return;
        }

        // Check if we can output the result
        if (!canAddOutput(recipe.getResult())) {
            cookTime = 0;
            cookTimeTotal = 0;
            setLit(false);
            return;
        }

        int energyPerTick = recipe.getEnergyPerTick();
        int modifiedEnergy = AdAstraConfig.getModifiedEnergyConsumption(energyPerTick);
        if (energy.extractEnergy(modifiedEnergy, true) < modifiedEnergy) {
            setLit(false);
            return;
        }

        // Apply config speed multiplier to processing time
        cookTimeTotal = AdAstraConfig.getModifiedProcessingTime(recipe.getProcessingTime());
        energy.extractEnergy(modifiedEnergy, false);
        cookTime++;
        setLit(true);

        if (cookTime >= cookTimeTotal) {
            craftAlloying(recipe);
            cookTime = 0;
        }
        markDirty();
    }

    private AlloySmeltingRecipe findAlloyingRecipe() {
        // Try all combinations of input slots
        for (int slot1 = FIRST_INPUT_SLOT; slot1 <= LAST_INPUT_SLOT; slot1++) {
            ItemStack stack1 = items.getStackInSlot(slot1);
            if (stack1.isEmpty()) continue;

            for (int slot2 = slot1 + 1; slot2 <= LAST_INPUT_SLOT; slot2++) {
                ItemStack stack2 = items.getStackInSlot(slot2);
                if (stack2.isEmpty()) continue;

                AlloySmeltingRecipe recipe = RecipeRegistry.findAlloyingRecipe(stack1, stack2);
                if (recipe != null) {
                    return recipe;
                }
            }
        }
        return null;
    }

    private void craftAlloying(AlloySmeltingRecipe recipe) {
        // Find and consume the matching inputs
        boolean consumedFirst = false;
        boolean consumedSecond = false;

        // Try to consume both inputs
        for (int slot = FIRST_INPUT_SLOT; slot <= LAST_INPUT_SLOT && (!consumedFirst || !consumedSecond); slot++) {
            ItemStack stack = items.getStackInSlot(slot);
            if (stack.isEmpty()) continue;

            if (!consumedFirst) {
                // Try to match first input
                ItemStack[] testInputs = {stack, ItemStack.EMPTY};
                if (recipe.matches(testInputs)) {
                    stack.shrink(1);
                    if (stack.isEmpty()) {
                        items.setStackInSlot(slot, ItemStack.EMPTY);
                    }
                    consumedFirst = true;
                    continue;
                }
            }

            if (!consumedSecond) {
                // Try to match second input
                ItemStack[] testInputs = {ItemStack.EMPTY, stack};
                if (recipe.matches(testInputs)) {
                    stack.shrink(1);
                    if (stack.isEmpty()) {
                        items.setStackInSlot(slot, ItemStack.EMPTY);
                    }
                    consumedSecond = true;
                }
            }
        }

        if (consumedFirst && consumedSecond) {
            addOutput(recipe.getResult());
        }
    }

    private void tickBlasting() {
        BlastingTarget target = getBlastingTarget();
        if (target == null) {
            cookTime = 0;
            cookTimeTotal = 0;
            activeBlastingRecipe = null;
            activeBlastingSlot = -1;
            setLit(false);
            return;
        }

        if (target.recipe != activeBlastingRecipe || target.inputSlot != activeBlastingSlot) {
            cookTime = 0;
            activeBlastingRecipe = target.recipe;
            activeBlastingSlot = target.inputSlot;
        }

        cookTimeTotal = AdAstraConfig.getModifiedProcessingTime(target.recipe.cookingTime);
        if (!canProcessBlasting(target.recipe)) {
            setLit(false);
            return;
        }

        int modifiedEnergy = AdAstraConfig.getModifiedEnergyConsumption(target.recipe.energyPerTick);
        energy.extractEnergy(modifiedEnergy, false);
        cookTime++;
        setLit(true);

        if (cookTime >= cookTimeTotal) {
            craftBlasting(target);
            cookTime = 0;
        }
        markDirty();
    }

    private BlastingTarget getBlastingTarget() {
        BlastingTarget activeTarget = null;
        if (activeBlastingRecipe != null && activeBlastingSlot >= FIRST_INPUT_SLOT && activeBlastingSlot <= LAST_INPUT_SLOT) {
            ItemStack stack = items.getStackInSlot(activeBlastingSlot);
            if (activeBlastingRecipe.matches(stack)) {
                activeTarget = new BlastingTarget(activeBlastingSlot, activeBlastingRecipe);
                if (canAddOutput(activeBlastingRecipe.result)) {
                    return activeTarget;
                }
            }
        }

        for (int slot = FIRST_INPUT_SLOT; slot <= LAST_INPUT_SLOT; slot++) {
            BlastingRecipe recipe = getBlastingRecipe(items.getStackInSlot(slot));
            if (recipe != null && canAddOutput(recipe.result)) {
                return new BlastingTarget(slot, recipe);
            }
        }
        return activeTarget;
    }

    private boolean canProcessBlasting(BlastingRecipe recipe) {
        int modifiedEnergy = AdAstraConfig.getModifiedEnergyConsumption(recipe.energyPerTick);
        return energy.extractEnergy(modifiedEnergy, true) >= modifiedEnergy && canAddOutput(recipe.result);
    }

    private void craftBlasting(BlastingTarget target) {
        ItemStack input = items.getStackInSlot(target.inputSlot);
        input.shrink(1);
        if (input.isEmpty()) {
            items.setStackInSlot(target.inputSlot, ItemStack.EMPTY);
        }
        addOutput(target.recipe.result);
    }

    private BlastingRecipe getBlastingRecipe(ItemStack stack) {
        if (stack.isEmpty()) {
            return null;
        }

        for (BlastingRecipe recipe : BlastingRecipe.values()) {
            if (recipe.matches(stack)) {
                return recipe;
            }
        }
        return null;
    }

    private boolean hasIronIngot() {
        for (int slot = FIRST_INPUT_SLOT; slot <= LAST_INPUT_SLOT; slot++) {
            ItemStack stack = items.getStackInSlot(slot);
            if (!stack.isEmpty() && stack.getItem() == Items.IRON_INGOT) {
                return true;
            }
        }
        return false;
    }

    private boolean hasCoal() {
        for (int slot = FIRST_INPUT_SLOT; slot <= LAST_INPUT_SLOT; slot++) {
            ItemStack stack = items.getStackInSlot(slot);
            if (!stack.isEmpty() && stack.getItem() == Items.COAL) {
                return true;
            }
        }
        return false;
    }

    private void craftSteelIngot() {
        consumeOne(stack -> stack.getItem() == Items.IRON_INGOT);
        consumeOne(stack -> stack.getItem() == Items.COAL);
        addOutput(new ItemStack(ModItems.STEEL_INGOT));
    }

    private void consumeOne(StackPredicate predicate) {
        for (int slot = FIRST_INPUT_SLOT; slot <= LAST_INPUT_SLOT; slot++) {
            ItemStack stack = items.getStackInSlot(slot);
            if (!stack.isEmpty() && predicate.test(stack)) {
                stack.shrink(1);
                if (stack.isEmpty()) {
                    items.setStackInSlot(slot, ItemStack.EMPTY);
                }
                return;
            }
        }
    }

    private boolean canAddOutput(ItemStack result) {
        for (int slot = FIRST_OUTPUT_SLOT; slot <= LAST_OUTPUT_SLOT; slot++) {
            ItemStack output = items.getStackInSlot(slot);
            if (output.isEmpty()) {
                return true;
            }
            if (ItemHandlerHelper.canItemStacksStack(output, result)
                && output.getCount() + result.getCount() <= Math.min(output.getMaxStackSize(), items.getSlotLimit(slot))) {
                return true;
            }
        }
        return false;
    }

    private void addOutput(ItemStack result) {
        for (int slot = FIRST_OUTPUT_SLOT; slot <= LAST_OUTPUT_SLOT; slot++) {
            ItemStack output = items.getStackInSlot(slot);
            if (output.isEmpty()) {
                items.setStackInSlot(slot, result.copy());
                return;
            }
            if (ItemHandlerHelper.canItemStacksStack(output, result)
                && output.getCount() + result.getCount() <= Math.min(output.getMaxStackSize(), items.getSlotLimit(slot))) {
                output.grow(result.getCount());
                return;
            }
        }
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        if (isValidBatterySlotItem(index, stack)) {
            return true;
        }
        if (stack.isEmpty() || index < FIRST_INPUT_SLOT || index > LAST_INPUT_SLOT) {
            return false;
        }
        // Check if valid for current mode
        if (mode == FurnaceMode.BLASTING) {
            return getBlastingRecipe(stack) != null;
        } else {
            // For alloying, check if it's part of any recipe
            return isValidAlloyingIngredient(stack) || stack.getItem() == Items.IRON_INGOT || stack.getItem() == Items.COAL;
        }
    }

    private boolean isValidAlloyingIngredient(ItemStack stack) {
        // Check if this item is used in any alloying recipe
        for (AlloySmeltingRecipe recipe : RecipeRegistry.getAllAlloyingRecipes()) {
            if (recipe.matches(new ItemStack[]{stack, ItemStack.EMPTY}) ||
                recipe.matches(new ItemStack[]{ItemStack.EMPTY, stack})) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int[] getSlotsForFace(EnumFacing side) {
        return new int[]{getBatterySlot(), 1, 2, 3, 4, 5, 6, 7, 8};
    }

    @Override
    public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
        return (isBatterySlot(index) || index >= FIRST_INPUT_SLOT && index <= LAST_INPUT_SLOT) && isItemValidForSlot(index, itemStackIn);
    }

    @Override
    public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
        return index >= FIRST_OUTPUT_SLOT && index <= LAST_OUTPUT_SLOT;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        cookTime = compound.getInteger("CookTime");
        cookTimeTotal = compound.getInteger("CookTimeTotal");
        mode = FurnaceMode.byOrdinal(compound.getByte("Mode"));
        activeBlastingSlot = compound.hasKey("ActiveBlastingSlot") ? compound.getInteger("ActiveBlastingSlot") : -1;
        activeBlastingRecipe = null;
        if (compound.hasKey("ActiveBlastingRecipe")) {
            try {
                activeBlastingRecipe = BlastingRecipe.valueOf(compound.getString("ActiveBlastingRecipe"));
            } catch (IllegalArgumentException ignored) {
                activeBlastingRecipe = null;
                activeBlastingSlot = -1;
            }
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setInteger("CookTime", cookTime);
        compound.setInteger("CookTimeTotal", cookTimeTotal);
        compound.setByte("Mode", (byte) mode.ordinal());
        if (activeBlastingRecipe != null) {
            compound.setString("ActiveBlastingRecipe", activeBlastingRecipe.name());
            compound.setInteger("ActiveBlastingSlot", activeBlastingSlot);
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
        if (id == 6) {
            return mode.ordinal();
        }
        return super.getField(id);
    }

    @Override
    public void setField(int id, int value) {
        if (id == 4) {
            cookTime = value;
        } else if (id == 5) {
            cookTimeTotal = value;
        } else if (id == 6) {
            mode = FurnaceMode.byOrdinal(value);
        } else {
            super.setField(id, value);
        }
    }

    @Override
    public int getFieldCount() {
        return 7;
    }

    public FurnaceMode getMode() {
        return mode;
    }

    public void setMode(FurnaceMode mode) {
        this.mode = mode;
        cookTime = 0;
        cookTimeTotal = 0;
        activeBlastingRecipe = null;
        activeBlastingSlot = -1;
        markDirty();
    }

    private static boolean hasOreName(ItemStack stack, String oreName) {
        if (stack.isEmpty()) {
            return false;
        }
        for (int oreId : OreDictionary.getOreIDs(stack)) {
            if (oreName.equals(OreDictionary.getOreName(oreId))) {
                return true;
            }
        }
        return false;
    }

    private enum BlastingRecipe {
        RAW_DESH("rawDesh", ModItems.DESH_INGOT),
        MOON_DESH_ORE(ModBlocks.MOON_DESH_ORE, ModItems.DESH_INGOT),
        DEEPSLATE_DESH_ORE(ModBlocks.DEEPSLATE_DESH_ORE, ModItems.DESH_INGOT),
        RAW_OSTRUM("rawOstrum", ModItems.OSTRUM_INGOT),
        MARS_OSTRUM_ORE(ModBlocks.MARS_OSTRUM_ORE, ModItems.OSTRUM_INGOT),
        DEEPSLATE_OSTRUM_ORE(ModBlocks.DEEPSLATE_OSTRUM_ORE, ModItems.OSTRUM_INGOT),
        RAW_CALORITE("rawCalorite", ModItems.CALORITE_INGOT),
        VENUS_CALORITE_ORE(ModBlocks.VENUS_CALORITE_ORE, ModItems.CALORITE_INGOT),
        DEEPSLATE_CALORITE_ORE(ModBlocks.DEEPSLATE_CALORITE_ORE, ModItems.CALORITE_INGOT),
        MOON_CHEESE_ORE(ModBlocks.MOON_CHEESE_ORE, ModItems.CHEESE),
        MOON_ICE_SHARD_ORE(ModBlocks.MOON_ICE_SHARD_ORE, ModItems.ICE_SHARD),
        DEEPSLATE_ICE_SHARD_ORE(ModBlocks.DEEPSLATE_ICE_SHARD_ORE, ModItems.ICE_SHARD),
        MARS_ICE_SHARD_ORE(ModBlocks.MARS_ICE_SHARD_ORE, ModItems.ICE_SHARD),
        GLACIO_ICE_SHARD_ORE(ModBlocks.GLACIO_ICE_SHARD_ORE, ModItems.ICE_SHARD),
        VENUS_COAL_ORE(ModBlocks.VENUS_COAL_ORE, Items.COAL),
        GLACIO_COAL_ORE(ModBlocks.GLACIO_COAL_ORE, Items.COAL),
        MARS_DIAMOND_ORE(ModBlocks.MARS_DIAMOND_ORE, Items.DIAMOND),
        VENUS_DIAMOND_ORE(ModBlocks.VENUS_DIAMOND_ORE, Items.DIAMOND),
        MOON_IRON_ORE(ModBlocks.MOON_IRON_ORE, Items.IRON_INGOT),
        MARS_IRON_ORE(ModBlocks.MARS_IRON_ORE, Items.IRON_INGOT),
        MERCURY_IRON_ORE(ModBlocks.MERCURY_IRON_ORE, Items.IRON_INGOT),
        GLACIO_IRON_ORE(ModBlocks.GLACIO_IRON_ORE, Items.IRON_INGOT),
        VENUS_GOLD_ORE(ModBlocks.VENUS_GOLD_ORE, Items.GOLD_INGOT),
        GLACIO_LAPIS_ORE(ModBlocks.GLACIO_LAPIS_ORE, new ItemStack(Items.DYE, 1, EnumDyeColor.BLUE.getDyeDamage()));

        private final StackPredicate input;
        private final ItemStack result;
        private final int cookingTime = BLASTING_COOK_TIME;
        private final int energyPerTick = BLASTING_ENERGY_PER_TICK;

        BlastingRecipe(String oreName, Item output) {
            this(stack -> hasOreName(stack, oreName), new ItemStack(output));
        }

        BlastingRecipe(Block input, Item output) {
            this(Item.getItemFromBlock(input), new ItemStack(output));
        }

        BlastingRecipe(Block input, ItemStack result) {
            this(Item.getItemFromBlock(input), result);
        }

        BlastingRecipe(Item input, ItemStack result) {
            this(stack -> stack.getItem() == input, result);
        }

        BlastingRecipe(StackPredicate input, ItemStack result) {
            this.input = input;
            this.result = result;
        }

        private boolean matches(ItemStack stack) {
            return !stack.isEmpty() && input.test(stack);
        }
    }

    private static final class BlastingTarget {
        private final int inputSlot;
        private final BlastingRecipe recipe;

        private BlastingTarget(int inputSlot, BlastingRecipe recipe) {
            this.inputSlot = inputSlot;
            this.recipe = recipe;
        }
    }

    public enum FurnaceMode {
        ALLOYING,
        BLASTING;

        public FurnaceMode next() {
            FurnaceMode[] values = values();
            return values[(ordinal() + 1) % values.length];
        }

        public FurnaceMode previous() {
            FurnaceMode[] values = values();
            return values[(ordinal() - 1 + values.length) % values.length];
        }

        public static FurnaceMode byOrdinal(int ordinal) {
            FurnaceMode[] values = values();
            return ordinal >= 0 && ordinal < values.length ? values[ordinal] : ALLOYING;
        }
    }

    private interface StackPredicate {
        boolean test(ItemStack stack);
    }
}
