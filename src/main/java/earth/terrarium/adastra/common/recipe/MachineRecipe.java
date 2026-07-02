package earth.terrarium.adastra.common.recipe;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Base class for all Ad Astra machine recipes.
 * Provides common functionality for input/output matching, energy cost, and processing time.
 */
public abstract class MachineRecipe {

    protected final String id;
    protected final List<ItemStack> inputs;
    protected final List<ItemStack> outputs;
    protected final int energyPerTick;
    protected final int processingTime;

    /**
     * Creates a machine recipe with item inputs and outputs.
     *
     * @param id Recipe identifier
     * @param inputs List of input ItemStacks
     * @param outputs List of output ItemStacks
     * @param processingTime Total time in ticks to process this recipe
     * @param energyPerTick Energy consumed per tick during processing
     */
    protected MachineRecipe(String id, List<ItemStack> inputs, List<ItemStack> outputs, int processingTime, int energyPerTick) {
        this.id = id;
        this.inputs = new ArrayList<>(inputs);
        this.outputs = new ArrayList<>(outputs);
        this.processingTime = processingTime;
        this.energyPerTick = energyPerTick;
    }

    /**
     * Checks if the provided stacks match this recipe's inputs.
     *
     * @param stacks Array of ItemStacks to check against recipe inputs
     * @return true if the stacks match this recipe
     */
    public abstract boolean matches(ItemStack[] stacks);

    /**
     * Gets the recipe identifier.
     *
     * @return Recipe ID string
     */
    public String getId() {
        return id;
    }

    /**
     * Gets the output ItemStacks for this recipe.
     *
     * @return Unmodifiable list of output ItemStacks
     */
    public List<ItemStack> getOutputs() {
        return Collections.unmodifiableList(outputs);
    }

    /**
     * Gets the primary output ItemStack for this recipe.
     * For recipes with multiple outputs, returns the first output.
     *
     * @return Primary output ItemStack, or ItemStack.EMPTY if no outputs
     */
    public ItemStack getOutput() {
        return outputs.isEmpty() ? ItemStack.EMPTY : outputs.get(0).copy();
    }

    /**
     * Gets the input ItemStacks for this recipe.
     *
     * @return Unmodifiable list of input ItemStacks
     */
    public List<ItemStack> getInputs() {
        return Collections.unmodifiableList(inputs);
    }

    /**
     * Gets the total processing time in ticks for this recipe.
     *
     * @return Processing time in ticks
     */
    public int getCookTime() {
        return processingTime;
    }

    /**
     * Gets the processing time in ticks (alias for getCookTime).
     *
     * @return Processing time in ticks
     */
    public int getProcessingTime() {
        return processingTime;
    }

    /**
     * Gets the energy cost per tick for this recipe.
     *
     * @return Energy consumed per tick
     */
    public int getEnergyPerTick() {
        return energyPerTick;
    }

    /**
     * Gets the total energy cost for this recipe.
     *
     * @return Total energy cost (energyPerTick * processingTime)
     */
    public int getEnergyCost() {
        return energyPerTick * processingTime;
    }

    /**
     * Checks if a given ItemStack matches any of this recipe's inputs.
     *
     * @param stack ItemStack to check
     * @return true if the stack matches any input
     */
    protected boolean matchesAnyInput(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }
        for (ItemStack input : inputs) {
            if (RecipeHelper.itemsMatch(stack, input)) {
                return true;
            }
        }
        return false;
    }
}
