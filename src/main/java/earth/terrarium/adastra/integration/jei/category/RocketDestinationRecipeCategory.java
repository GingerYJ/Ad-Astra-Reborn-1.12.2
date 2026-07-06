package earth.terrarium.adastra.integration.jei.category;

import earth.terrarium.adastra.Reference;
import earth.terrarium.adastra.common.registry.ModItems;
import earth.terrarium.adastra.integration.jei.AdAstraJEIPlugin;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class RocketDestinationRecipeCategory implements IRecipeCategory<RocketDestinationRecipeWrapper> {

    private final IDrawable background;
    private final IDrawable icon;
    private final String localizedName;

    public RocketDestinationRecipeCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createBlankDrawable(176, 160);
        this.icon = guiHelper.createDrawableIngredient(new ItemStack(ModItems.TIER_1_ROCKET));
        this.localizedName = I18n.format("jei.ad_astra.rocket_destinations.title");
    }

    @Nonnull
    @Override
    public String getUid() {
        return AdAstraJEIPlugin.ROCKET_DESTINATIONS_CATEGORY;
    }

    @Nonnull
    @Override
    public String getTitle() {
        return localizedName;
    }

    @Nonnull
    @Override
    public String getModName() {
        return Reference.MOD_NAME;
    }

    @Nonnull
    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(@Nonnull IRecipeLayout recipeLayout, @Nonnull RocketDestinationRecipeWrapper recipeWrapper,
                          @Nonnull IIngredients ingredients) {
        recipeLayout.getItemStacks().init(0, true, 8, 7);
        recipeLayout.getItemStacks().set(ingredients);
    }
}
