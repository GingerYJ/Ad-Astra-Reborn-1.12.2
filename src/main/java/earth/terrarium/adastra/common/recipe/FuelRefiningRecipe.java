package earth.terrarium.adastra.common.recipe;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

/**
 * Recipe implementation for the Fuel Refinery machine.
 * Refines a single input item into a fuel fluid output.
 */
public class FuelRefiningRecipe {

    private final String id;
    private final ItemStack input;
    private final Fluid outputFluid;
    private final int outputAmount;
    private final int processingTime;
    private final int energyPerTick;

    /**
     * Creates a fuel refining recipe.
     *
     * @param id Recipe identifier
     * @param input Input ItemStack
     * @param outputFluid Output fuel fluid type
     * @param outputAmount Amount of fuel produced in millibuckets
     * @param processingTime Total time in ticks to process this recipe
     * @param energyPerTick Energy consumed per tick during processing
     */
    public FuelRefiningRecipe(String id, ItemStack input, Fluid outputFluid, int outputAmount, int processingTime, int energyPerTick) {
        this.id = id;
        this.input = input.copy();
        this.outputFluid = outputFluid;
        this.outputAmount = outputAmount;
        this.processingTime = processingTime;
        this.energyPerTick = energyPerTick;
    }

    /**
     * Checks if a single ItemStack matches this recipe's input.
     *
     * @param stack ItemStack to check
     * @return true if the stack matches
     */
    public boolean matches(ItemStack stack) {
        if (stack.isEmpty() || input.isEmpty()) {
            return false;
        }
        return RecipeHelper.itemsMatch(stack, input);
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
     * Gets the input ItemStack for this recipe.
     *
     * @return Input ItemStack
     */
    public ItemStack getInput() {
        return input.copy();
    }

    /**
     * Gets the output fluid type.
     *
     * @return Output Fluid
     */
    public Fluid getOutputFluid() {
        return outputFluid;
    }

    /**
     * Gets the output fluid amount in millibuckets.
     *
     * @return Fluid amount in mB
     */
    public int getOutputAmount() {
        return outputAmount;
    }

    /**
     * Gets the output as a FluidStack.
     *
     * @return FluidStack with the output fluid and amount
     */
    public FluidStack getOutputFluidStack() {
        return outputFluid != null ? new FluidStack(outputFluid, outputAmount) : null;
    }

    /**
     * Gets the processing time in ticks.
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
     * Gets the energy cost per tick.
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
}
