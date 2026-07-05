package earth.terrarium.adastra.common.recipe;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import java.util.Arrays;

/**
 * Recipe implementation for the Alloy Smelter / Electric Blast Furnace machine.
 * Smelts two input items into a single output item.
 */
public class AlloySmeltingRecipe extends MachineRecipe {

    private final String oreDictName1;
    private final String oreDictName2;

    /**
     * Creates an alloy smelting recipe with two inputs.
     *
     * @param id Recipe identifier
     * @param input1 First input ItemStack
     * @param input2 Second input ItemStack
     * @param output Output ItemStack
     * @param processingTime Total time in ticks to process this recipe
     * @param energyPerTick Energy consumed per tick during processing
     */
    public AlloySmeltingRecipe(String id, ItemStack input1, ItemStack input2, ItemStack output, int processingTime, int energyPerTick) {
        this(id, input1, input2, output, processingTime, energyPerTick, null, null);
    }

    /**
     * Creates an alloy smelting recipe with two inputs and ore dictionary support.
     *
     * @param id Recipe identifier
     * @param input1 First input ItemStack (representative)
     * @param input2 Second input ItemStack (representative)
     * @param output Output ItemStack
     * @param processingTime Total time in ticks to process this recipe
     * @param energyPerTick Energy consumed per tick during processing
     * @param oreDictName1 Ore dictionary name for first input, or null for exact match
     * @param oreDictName2 Ore dictionary name for second input, or null for exact match
     */
    public AlloySmeltingRecipe(String id, ItemStack input1, ItemStack input2, ItemStack output, int processingTime, int energyPerTick, String oreDictName1, String oreDictName2) {
        super(id, Arrays.asList(input1, input2), Arrays.asList(output), processingTime, energyPerTick);
        this.oreDictName1 = oreDictName1;
        this.oreDictName2 = oreDictName2;
    }

    @Override
    public boolean matches(ItemStack[] stacks) {
        if (stacks == null || stacks.length < 2) {
            return false;
        }
        if (inputs.size() < 2) {
            return false;
        }

        ItemStack input1 = inputs.get(0);
        ItemStack input2 = inputs.get(1);
        ItemStack stack1 = stacks[0];
        ItemStack stack2 = stacks[1];

        // Check both orderings since the inputs can be in either slot
        boolean order1 = matchesInput(stack1, input1, oreDictName1) && matchesInput(stack2, input2, oreDictName2);
        boolean order2 = matchesInput(stack1, input2, oreDictName2) && matchesInput(stack2, input1, oreDictName1);

        return order1 || order2;
    }

    public boolean matchesAnyInput(ItemStack stack) {
        if (inputs.size() < 2) {
            return false;
        }
        return matchesInput(stack, inputs.get(0), oreDictName1)
            || matchesInput(stack, inputs.get(1), oreDictName2);
    }

    /**
     * Checks if a stack matches an input, using ore dict if specified.
     */
    private boolean matchesInput(ItemStack stack, ItemStack recipeInput, String oreDictName) {
        if (stack.isEmpty()) {
            return false;
        }

        // If ore dictionary name is specified, use ore dict matching
        if (oreDictName != null && !oreDictName.isEmpty()) {
            return matchesOreDict(stack, oreDictName);
        }

        // Otherwise use exact item matching
        return RecipeHelper.itemsMatch(stack, recipeInput);
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
     * Gets the first input ItemStack for this recipe.
     *
     * @return First input ItemStack
     */
    public ItemStack getInput1() {
        return inputs.size() > 0 ? inputs.get(0).copy() : ItemStack.EMPTY;
    }

    /**
     * Gets the second input ItemStack for this recipe.
     *
     * @return Second input ItemStack
     */
    public ItemStack getInput2() {
        return inputs.size() > 1 ? inputs.get(1).copy() : ItemStack.EMPTY;
    }

    /**
     * Gets the output ItemStack for this recipe.
     *
     * @return Output ItemStack
     */
    public ItemStack getResult() {
        return getOutput();
    }
}
