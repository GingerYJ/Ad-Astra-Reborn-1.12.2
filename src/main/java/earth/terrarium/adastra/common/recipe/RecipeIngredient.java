package earth.terrarium.adastra.common.recipe;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a recipe ingredient that can match items or ore dictionary entries.
 * Provides flexible matching for both specific items and ore dictionary tags.
 */
public class RecipeIngredient {

    private final ItemStack itemStack;
    private final String oreName;
    private final boolean useOreDictionary;

    /**
     * Creates an ingredient from a specific ItemStack.
     */
    public RecipeIngredient(ItemStack itemStack) {
        this.itemStack = itemStack.copy();
        this.oreName = null;
        this.useOreDictionary = false;
    }

    /**
     * Creates an ingredient from an ore dictionary name.
     */
    public RecipeIngredient(String oreName) {
        this.itemStack = ItemStack.EMPTY;
        this.oreName = oreName;
        this.useOreDictionary = true;
    }

    /**
     * Creates an ingredient from an item.
     */
    public RecipeIngredient(Item item) {
        this(new ItemStack(item));
    }

    /**
     * Creates an ingredient from an item with metadata.
     */
    public RecipeIngredient(Item item, int meta) {
        this(new ItemStack(item, 1, meta));
    }

    /**
     * Checks if the given ItemStack matches this ingredient.
     */
    public boolean matches(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }

        if (useOreDictionary) {
            return matchesOreDict(stack);
        } else {
            return RecipeHelper.itemsMatch(stack, itemStack);
        }
    }

    /**
     * Checks if the given ItemStack matches this ingredient's ore dictionary entry.
     */
    private boolean matchesOreDict(ItemStack stack) {
        if (oreName == null) {
            return false;
        }

        int[] oreIds = OreDictionary.getOreIDs(stack);
        for (int id : oreIds) {
            if (oreName.equals(OreDictionary.getOreName(id))) {
                return true;
            }
        }
        return false;
    }

    /**
     * Gets the ItemStack representation of this ingredient.
     * For ore dictionary ingredients, returns the first matching ore.
     */
    public ItemStack getExampleStack() {
        if (useOreDictionary) {
            List<ItemStack> ores = OreDictionary.getOres(oreName);
            if (!ores.isEmpty()) {
                return ores.get(0).copy();
            }
            return ItemStack.EMPTY;
        } else {
            return itemStack.copy();
        }
    }

    /**
     * Gets all possible matching ItemStacks for this ingredient.
     * For item ingredients, returns a single-element list.
     * For ore dictionary ingredients, returns all matching ores.
     */
    public List<ItemStack> getMatchingStacks() {
        List<ItemStack> stacks = new ArrayList<>();
        if (useOreDictionary) {
            stacks.addAll(OreDictionary.getOres(oreName));
        } else {
            stacks.add(itemStack.copy());
        }
        return stacks;
    }

    /**
     * Returns whether this ingredient uses ore dictionary matching.
     */
    public boolean isOreDictionary() {
        return useOreDictionary;
    }

    /**
     * Gets the ore dictionary name if this ingredient uses ore dictionary.
     */
    public String getOreName() {
        return oreName;
    }

    /**
     * Gets the item stack if this ingredient uses a specific item.
     */
    public ItemStack getItemStack() {
        return itemStack.copy();
    }
}
