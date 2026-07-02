package earth.terrarium.adastra.integration.jei.category;

import earth.terrarium.adastra.common.recipe.AlloySmeltingRecipe;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeWrapper;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Collections;

public class BlastFurnaceRecipeWrapper implements IRecipeWrapper {

    private final AlloySmeltingRecipe recipe;

    public BlastFurnaceRecipeWrapper(AlloySmeltingRecipe recipe) {
        this.recipe = recipe;
    }

    @Override
    public void getIngredients(@Nonnull IIngredients ingredients) {
        ingredients.setInputs(VanillaTypes.ITEM, Arrays.asList(
            recipe.getInput1(),
            recipe.getInput2()
        ));
        ingredients.setOutput(VanillaTypes.ITEM, recipe.getResult());
    }

    @Nonnull
    @Override
    public java.util.List<String> getTooltipStrings(int mouseX, int mouseY) {
        return Collections.emptyList();
    }
}
