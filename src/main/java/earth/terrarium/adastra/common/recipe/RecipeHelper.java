package earth.terrarium.adastra.common.recipe;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.oredict.OreDictionary;

/**
 * Utility class for recipe-related operations.
 * Provides helper methods for ItemStack comparison, recipe matching, and input consumption.
 */
public final class RecipeHelper {

    private RecipeHelper() {
        // Utility class
    }

    /**
     * Checks if two ItemStacks match, considering item type and metadata.
     * Does not compare stack size or NBT data.
     *
     * @param stack1 First ItemStack
     * @param stack2 Second ItemStack
     * @return true if items match (same item and metadata)
     */
    public static boolean itemsMatch(ItemStack stack1, ItemStack stack2) {
        if (stack1.isEmpty() || stack2.isEmpty()) {
            return stack1.isEmpty() && stack2.isEmpty();
        }
        return stack1.getItem() == stack2.getItem() && stack1.getMetadata() == stack2.getMetadata();
    }

    /**
     * Checks if two ItemStacks match exactly, including NBT data.
     * Does not compare stack size.
     *
     * @param stack1 First ItemStack
     * @param stack2 Second ItemStack
     * @return true if items match including NBT
     */
    public static boolean itemsMatchWithNBT(ItemStack stack1, ItemStack stack2) {
        if (!itemsMatch(stack1, stack2)) {
            return false;
        }
        return ItemStack.areItemStackTagsEqual(stack1, stack2);
    }

    /**
     * Checks if two ItemStacks are exactly equal, including size and NBT.
     *
     * @param stack1 First ItemStack
     * @param stack2 Second ItemStack
     * @return true if stacks are identical
     */
    public static boolean stacksEqual(ItemStack stack1, ItemStack stack2) {
        return ItemStack.areItemStacksEqual(stack1, stack2);
    }

    /**
     * Checks if the first stack can fit into the second stack.
     * Useful for checking if a recipe output can be inserted into an output slot.
     *
     * @param toInsert ItemStack to insert
     * @param destination Destination ItemStack slot
     * @param maxStackSize Maximum stack size for the slot
     * @return true if the stack can fit
     */
    public static boolean canInsertStack(ItemStack toInsert, ItemStack destination, int maxStackSize) {
        if (toInsert.isEmpty()) {
            return true;
        }
        if (destination.isEmpty()) {
            return toInsert.getCount() <= maxStackSize;
        }
        if (!itemsMatchWithNBT(toInsert, destination)) {
            return false;
        }
        return destination.getCount() + toInsert.getCount() <= Math.min(maxStackSize, destination.getMaxStackSize());
    }

    /**
     * Checks if a recipe output can be inserted into an output slot.
     *
     * @param output Recipe output ItemStack
     * @param outputSlot Current ItemStack in output slot
     * @return true if the output can be inserted
     */
    public static boolean canInsertOutput(ItemStack output, ItemStack outputSlot) {
        return canInsertStack(output, outputSlot, output.getMaxStackSize());
    }

    /**
     * Consumes items from an input ItemStack for recipe processing.
     *
     * @param stack ItemStack to consume from
     * @param amount Amount to consume
     * @return true if consumption was successful
     */
    public static boolean consumeInput(ItemStack stack, int amount) {
        if (stack.isEmpty() || stack.getCount() < amount) {
            return false;
        }
        stack.shrink(amount);
        return true;
    }

    /**
     * Inserts an ItemStack into a destination slot.
     * If destination is empty, places the stack there.
     * If destination matches, increases the stack size.
     *
     * @param toInsert ItemStack to insert
     * @param destination Destination ItemStack slot
     * @return true if insertion was successful
     */
    public static boolean insertStack(ItemStack toInsert, ItemStack destination) {
        if (toInsert.isEmpty()) {
            return true;
        }
        if (destination.isEmpty()) {
            return false;
        }
        if (!itemsMatchWithNBT(toInsert, destination)) {
            return false;
        }
        int newCount = destination.getCount() + toInsert.getCount();
        if (newCount > destination.getMaxStackSize()) {
            return false;
        }
        destination.setCount(newCount);
        return true;
    }

    /**
     * Checks if an ItemStack has enough items for a recipe.
     *
     * @param stack ItemStack to check
     * @param required Required amount
     * @return true if stack has enough items
     */
    public static boolean hasEnough(ItemStack stack, int required) {
        return !stack.isEmpty() && stack.getCount() >= required;
    }

    /**
     * Compares two NBT tags for equality.
     *
     * @param nbt1 First NBT tag
     * @param nbt2 Second NBT tag
     * @return true if NBT tags are equal
     */
    public static boolean nbtEquals(NBTTagCompound nbt1, NBTTagCompound nbt2) {
        if (nbt1 == null && nbt2 == null) {
            return true;
        }
        if (nbt1 == null || nbt2 == null) {
            return false;
        }
        return nbt1.equals(nbt2);
    }

    /**
     * Creates a copy of an ItemStack with a specified count.
     *
     * @param stack ItemStack to copy
     * @param count New stack size
     * @return Copy of ItemStack with new count
     */
    public static ItemStack copyWithCount(ItemStack stack, int count) {
        if (stack.isEmpty()) {
            return ItemStack.EMPTY;
        }
        ItemStack copy = stack.copy();
        copy.setCount(count);
        return copy;
    }

    /**
     * Checks if an ItemStack matches an ore dictionary name.
     *
     * @param stack ItemStack to check
     * @param oreName Ore dictionary name
     * @return true if the stack matches the ore dictionary entry
     */
    public static boolean matchesOreDict(ItemStack stack, String oreName) {
        if (stack.isEmpty() || oreName == null || oreName.isEmpty()) {
            return false;
        }
        for (int oreId : OreDictionary.getOreIDs(stack)) {
            if (oreName.equals(OreDictionary.getOreName(oreId))) {
                return true;
            }
        }
        return false;
    }
}
