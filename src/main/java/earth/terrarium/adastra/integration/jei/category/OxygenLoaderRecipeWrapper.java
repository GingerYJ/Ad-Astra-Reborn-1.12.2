package earth.terrarium.adastra.integration.jei.category;

import earth.terrarium.adastra.common.recipe.OxygenLoadingRecipe;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;
import java.util.Collections;

public class OxygenLoaderRecipeWrapper implements IRecipeWrapper {

    private final OxygenLoadingRecipe recipe;

    public OxygenLoaderRecipeWrapper(OxygenLoadingRecipe recipe) {
        this.recipe = recipe;
    }

    @Override
    public void getIngredients(@Nonnull IIngredients ingredients) {
        ingredients.setInput(VanillaTypes.ITEM, recipe.getInput());

        FluidStack fluidInput = recipe.getInputFluidStack();
        if (fluidInput != null) {
            ingredients.setInput(VanillaTypes.FLUID, fluidInput);
        }

        ingredients.setOutput(VanillaTypes.ITEM, recipe.getOutput());
    }

    @Nonnull
    @Override
    public java.util.List<String> getTooltipStrings(int mouseX, int mouseY) {
        return Collections.emptyList();
    }
}
