package earth.terrarium.adastra.common.recipe;

import net.minecraft.item.ItemStack;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Recipe implementation for Space Station construction.
 * Requires multiple ingredient stacks to build the global space station.
 */
public class SpaceStationRecipe {

    private final String id;
    private final String structure;
    private final List<IngredientRequirement> requirements;

    /**
     * Creates a space station recipe.
     *
     * @param id Recipe identifier
     * @param structure Structure NBT name
     * @param requirements List of ingredient requirements with counts
     */
    public SpaceStationRecipe(String id, String structure, List<IngredientRequirement> requirements) {
        this.id = id;
        this.structure = structure;
        this.requirements = new ArrayList<>(requirements);
    }

    /**
     * Gets the recipe identifier.
     *
     * @return Recipe ID string
     */
    public String getId() {
        return id;
    }

    /**
     * Gets the structure NBT name for this space station.
     *
     * @return Structure name
     */
    public String getStructure() {
        return structure;
    }

    /**
     * Gets the list of ingredient requirements.
     *
     * @return Unmodifiable list of requirements
     */
    public List<IngredientRequirement> getRequirements() {
        return Collections.unmodifiableList(requirements);
    }

    /**
     * Checks if the provided item stacks satisfy all requirements.
     *
     * @param providedStacks Array of ItemStacks provided by player
     * @return true if all requirements are met
     */
    public boolean matches(ItemStack[] providedStacks) {
        if (providedStacks == null || requirements.isEmpty()) {
            return false;
        }

        // Check each requirement
        for (IngredientRequirement req : requirements) {
            int totalCount = 0;

            // Count matching items across all provided stacks
            for (ItemStack provided : providedStacks) {
                if (provided != null && !provided.isEmpty() && req.matches(provided)) {
                    totalCount += provided.getCount();
                }
            }

            // Check if we have enough of this ingredient
            if (totalCount < req.getCount()) {
                return false;
            }
        }

        return true;
    }

    public boolean matchesInventory(IInventory inventory) {
        if (inventory == null || requirements.isEmpty()) {
            return false;
        }
        for (IngredientRequirement req : requirements) {
            int totalCount = 0;
            for (int i = 0; i < inventory.getSizeInventory(); i++) {
                ItemStack stack = inventory.getStackInSlot(i);
                if (!stack.isEmpty() && req.matches(stack)) {
                    totalCount += stack.getCount();
                }
            }
            if (totalCount < req.getCount()) {
                return false;
            }
        }
        return true;
    }

    public boolean canCraft(EntityPlayer player) {
        return player != null && (player.capabilities.isCreativeMode || matchesInventory(player.inventory));
    }

    public boolean consumeIngredients(EntityPlayer player) {
        if (player == null) {
            return false;
        }
        if (player.capabilities.isCreativeMode) {
            return true;
        }
        if (!matchesInventory(player.inventory)) {
            return false;
        }

        for (IngredientRequirement req : requirements) {
            int remaining = req.getCount();
            for (int i = 0; i < player.inventory.getSizeInventory() && remaining > 0; i++) {
                ItemStack stack = player.inventory.getStackInSlot(i);
                if (stack.isEmpty() || !req.matches(stack)) {
                    continue;
                }
                int removed = Math.min(remaining, stack.getCount());
                stack.shrink(removed);
                remaining -= removed;
                if (stack.isEmpty()) {
                    player.inventory.setInventorySlotContents(i, ItemStack.EMPTY);
                }
            }
        }
        player.inventory.markDirty();
        return true;
    }

    /**
     * Represents a single ingredient requirement for a space station recipe.
     */
    public static class IngredientRequirement {
        private final ItemStack representativeStack;
        private final String oreDictName;
        private final int count;

        /**
         * Creates an item-based ingredient requirement.
         *
         * @param stack Representative ItemStack
         * @param count Required count
         */
        public IngredientRequirement(ItemStack stack, int count) {
            this(stack, null, count);
        }

        /**
         * Creates an ore dictionary-based ingredient requirement.
         *
         * @param oreDictName Ore dictionary name
         * @param count Required count
         */
        public IngredientRequirement(String oreDictName, int count) {
            this(ItemStack.EMPTY, oreDictName, count);
        }

        /**
         * Creates an ingredient requirement with optional ore dict support.
         *
         * @param stack Representative ItemStack (can be empty if using ore dict)
         * @param oreDictName Ore dictionary name (null for exact item matching)
         * @param count Required count
         */
        private IngredientRequirement(ItemStack stack, String oreDictName, int count) {
            this.representativeStack = stack;
            this.oreDictName = oreDictName;
            this.count = count;
        }

        /**
         * Checks if a stack matches this requirement's ingredient.
         *
         * @param stack ItemStack to check
         * @return true if the stack matches
         */
        public boolean matches(ItemStack stack) {
            if (stack.isEmpty()) {
                return false;
            }

            // If ore dictionary name is specified, use ore dict matching
            if (oreDictName != null && !oreDictName.isEmpty()) {
                return RecipeHelper.matchesOreDict(stack, oreDictName);
            }

            // Otherwise use exact item matching
            return !representativeStack.isEmpty() && RecipeHelper.itemsMatch(stack, representativeStack);
        }

        public int countMatching(IInventory inventory) {
            if (inventory == null) {
                return 0;
            }
            int count = 0;
            for (int i = 0; i < inventory.getSizeInventory(); i++) {
                ItemStack stack = inventory.getStackInSlot(i);
                if (!stack.isEmpty() && matches(stack)) {
                    count += stack.getCount();
                }
            }
            return count;
        }

        /**
         * Gets the required count for this ingredient.
         *
         * @return Required count
         */
        public int getCount() {
            return count;
        }

        /**
         * Gets the representative ItemStack for display purposes.
         *
         * @return Representative ItemStack
         */
        public ItemStack getRepresentativeStack() {
            return representativeStack.copy();
        }

        public ItemStack getDisplayStack() {
            if (!representativeStack.isEmpty()) {
                ItemStack stack = representativeStack.copy();
                stack.setCount(1);
                return stack;
            }
            if (oreDictName != null && !oreDictName.isEmpty()) {
                List<ItemStack> ores = OreDictionary.getOres(oreDictName);
                if (!ores.isEmpty()) {
                    ItemStack stack = ores.get(0).copy();
                    stack.setCount(1);
                    return stack;
                }
            }
            return ItemStack.EMPTY;
        }

        /**
         * Gets the ore dictionary name if this uses ore dict matching.
         *
         * @return Ore dictionary name or null
         */
        public String getOreDictName() {
            return oreDictName;
        }
    }
}
