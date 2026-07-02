package earth.terrarium.adastra.common.recipe;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

/**
 * Recipe implementation for Oxygen Loading machines.
 * Loads oxygen or other gases into items like tanks or suits.
 */
public class OxygenLoadingRecipe {

    private final String id;
    private final ItemStack input;
    private final Fluid inputFluid;
    private final int fluidAmount;
    private final ItemStack output;
    private final int processingTime;
    private final int energyPerTick;

    /**
     * Creates an oxygen loading recipe.
     *
     * @param id Recipe identifier
     * @param input Input ItemStack (empty tank/suit)
     * @param inputFluid Input fluid (oxygen or other gas)
     * @param fluidAmount Amount of fluid consumed in millibuckets
     * @param output Output ItemStack (filled tank/suit)
     * @param processingTime Total time in ticks to process this recipe
     * @param energyPerTick Energy consumed per tick during processing
     */
    public OxygenLoadingRecipe(String id, ItemStack input, Fluid inputFluid, int fluidAmount, ItemStack output, int processingTime, int energyPerTick) {
        this.id = id;
        this.input = input.copy();
        this.inputFluid = inputFluid;
        this.fluidAmount = fluidAmount;
        this.output = output.copy();
        this.processingTime = processingTime;
        this.energyPerTick = energyPerTick;
    }

    /**
     * Checks if the given item and fluid match this recipe's requirements.
     *
     * @param stack ItemStack to check
     * @param fluid Fluid to check
     * @return true if both match
     */
    public boolean matches(ItemStack stack, Fluid fluid) {
        if (stack.isEmpty() || fluid == null) {
            return false;
        }
        return RecipeHelper.itemsMatch(stack, input) && fluid == inputFluid;
    }

    /**
     * Checks if the given item and fluid stack match this recipe's requirements.
     *
     * @param stack ItemStack to check
     * @param fluidStack FluidStack to check
     * @return true if both match and fluid amount is sufficient
     */
    public boolean matches(ItemStack stack, FluidStack fluidStack) {
        if (fluidStack == null || fluidStack.amount < fluidAmount) {
            return false;
        }
        return matches(stack, fluidStack.getFluid());
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
     * Gets the input fluid type.
     *
     * @return Input Fluid
     */
    public Fluid getInputFluid() {
        return inputFluid;
    }

    /**
     * Gets the input fluid amount in millibuckets.
     *
     * @return Fluid amount in mB
     */
    public int getFluidAmount() {
        return fluidAmount;
    }

    /**
     * Gets the input as a FluidStack.
     *
     * @return FluidStack with the input fluid and amount
     */
    public FluidStack getInputFluidStack() {
        return inputFluid != null ? new FluidStack(inputFluid, fluidAmount) : null;
    }

    /**
     * Gets the output ItemStack for this recipe.
     *
     * @return Output ItemStack
     */
    public ItemStack getOutput() {
        return output.copy();
    }

    /**
     * Gets the output ItemStack for this recipe (alias).
     *
     * @return Output ItemStack
     */
    public ItemStack getResult() {
        return getOutput();
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
