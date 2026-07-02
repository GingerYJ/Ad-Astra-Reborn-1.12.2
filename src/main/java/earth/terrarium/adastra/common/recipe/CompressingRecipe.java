package earth.terrarium.adastra.common.recipe;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import java.util.Collections;

/**
 * Recipe implementation for the Compressor machine.
 * Compresses a single input item into a single output item.
 */
public class CompressingRecipe extends MachineRecipe {

    private final String oreDictName;

    /**
     * Creates a compressing recipe.
     *
     * @param id Recipe identifier
     * @param input Input ItemStack
     * @param output Output ItemStack
     * @param processingTime Total time in ticks to process this recipe
     * @param energyPerTick Energy consumed per tick during processing
     */
    public CompressingRecipe(String id, ItemStack input, ItemStack output, int processingTime, int energyPerTick) {
        this(id, input, output, processingTime, energyPerTick, null);
    }

    /**
     * Creates a compressing recipe with ore dictionary support.
     *
     * @param id Recipe identifier
     * @param input Input ItemStack (representative)
     * @param output Output ItemStack
     * @param processingTime Total time in ticks to process this recipe
     * @param energyPerTick Energy consumed per tick during processing
     * @param oreDictName Ore dictionary name for matching, or null for exact match
     */
    public CompressingRecipe(String id, ItemStack input, ItemStack output, int processingTime, int energyPerTick, String oreDictName) {
        super(id, Collections.singletonList(input), Collections.singletonList(output), processingTime, energyPerTick);
        this.oreDictName = oreDictName;
    }

    @Override
    public boolean matches(ItemStack[] stacks) {
        if (stacks == null || stacks.length == 0) {
            return false;
        }
        return matches(stacks[0]);
    }

    /**
     * Checks if a single ItemStack matches this recipe's input.
     *
     * @param stack ItemStack to check
     * @return true if the stack matches
     */
    public boolean matches(ItemStack stack) {
        if (stack.isEmpty() || inputs.isEmpty()) {
            return false;
        }

        // If ore dictionary name is specified, use ore dict matching
        if (oreDictName != null && !oreDictName.isEmpty()) {
            return matchesOreDict(stack, oreDictName);
        }

        // Otherwise use exact item matching
        return RecipeHelper.itemsMatch(stack, inputs.get(0));
    }

    /**
     * Checks if a stack matches an ore dictionary name.
     */
    private boolean matchesOreDict(ItemStack stack, String oreName) {
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

    /**
     * Gets the input ItemStack for this recipe.
     *
     * @return Input ItemStack
     */
    public ItemStack getInput() {
        return inputs.isEmpty() ? ItemStack.EMPTY : inputs.get(0).copy();
    }

    /**
     * Gets the output ItemStack for this recipe.
     *
     * @return Output ItemStack
     */
    public ItemStack getResult() {
        return getOutput();
    }

    /**
     * Gets the ore dictionary name if this recipe uses ore dict matching.
     *
     * @return Ore dictionary name or null
     */
    public String getOreDictName() {
        return oreDictName;
    }
}
