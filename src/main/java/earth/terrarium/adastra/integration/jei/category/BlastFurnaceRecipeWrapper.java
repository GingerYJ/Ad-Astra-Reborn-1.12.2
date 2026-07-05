package earth.terrarium.adastra.integration.jei.category;

import earth.terrarium.adastra.common.recipe.AlloySmeltingRecipe;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;

public class BlastFurnaceRecipeWrapper implements IRecipeWrapper {

    private final AlloySmeltingRecipe recipe;

    public BlastFurnaceRecipeWrapper(AlloySmeltingRecipe recipe) {
        this.recipe = recipe;
    }

    @Override
    public void getIngredients(@Nonnull IIngredients ingredients) {
        List<List<ItemStack>> inputs = java.util.Arrays.asList(
            Collections.singletonList(recipe.getInput1()),
            Collections.singletonList(recipe.getInput2())
        );
        ingredients.setInputLists(VanillaTypes.ITEM, inputs);
        ingredients.setOutput(VanillaTypes.ITEM, recipe.getResult());
    }

    public ItemStack getInput1() {
        return recipe.getInput1();
    }

    public ItemStack getInput2() {
        return recipe.getInput2();
    }

    public ItemStack getResult() {
        return recipe.getResult();
    }

    @Nonnull
    @Override
    public java.util.List<String> getTooltipStrings(int mouseX, int mouseY) {
        return Collections.emptyList();
    }
}
