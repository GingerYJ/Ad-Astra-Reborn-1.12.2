package earth.terrarium.adastra.integration.jei.category;

import earth.terrarium.adastra.common.recipe.NASAWorkbenchRecipe;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeWrapper;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;

public class NASAWorkbenchRecipeWrapper implements IRecipeWrapper {

    private final NASAWorkbenchRecipe recipe;

    public NASAWorkbenchRecipeWrapper(NASAWorkbenchRecipe recipe) {
        this.recipe = recipe;
    }

    @Override
    public void getIngredients(@Nonnull IIngredients ingredients) {
        ingredients.setInputs(VanillaTypes.ITEM, recipe.getInputs());
        ingredients.setOutput(VanillaTypes.ITEM, recipe.getResult());
    }

    @Nonnull
    @Override
    public List<String> getTooltipStrings(int mouseX, int mouseY) {
        return Collections.emptyList();
    }
}
