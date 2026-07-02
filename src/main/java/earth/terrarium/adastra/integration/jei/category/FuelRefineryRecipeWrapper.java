package earth.terrarium.adastra.integration.jei.category;

import earth.terrarium.adastra.common.recipe.FuelRefiningRecipe;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;
import java.util.Collections;

public class FuelRefineryRecipeWrapper implements IRecipeWrapper {

    private final FuelRefiningRecipe recipe;

    public FuelRefineryRecipeWrapper(FuelRefiningRecipe recipe) {
        this.recipe = recipe;
    }

    @Override
    public void getIngredients(@Nonnull IIngredients ingredients) {
        ingredients.setInput(VanillaTypes.ITEM, recipe.getInput());

        FluidStack fluidOutput = recipe.getOutputFluidStack();
        if (fluidOutput != null) {
            ingredients.setOutput(VanillaTypes.FLUID, fluidOutput);
        }
    }

    @Nonnull
    @Override
    public java.util.List<String> getTooltipStrings(int mouseX, int mouseY) {
        return Collections.emptyList();
    }
}
