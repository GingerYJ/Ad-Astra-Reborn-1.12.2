package earth.terrarium.adastra.integration.jei.category;

import earth.terrarium.adastra.Reference;
import earth.terrarium.adastra.common.registry.ModBlocks;
import earth.terrarium.adastra.integration.jei.AdAstraJEIPlugin;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

public class NASAWorkbenchRecipeCategory implements IRecipeCategory<NASAWorkbenchRecipeWrapper> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(Reference.MOD_ID,
        "textures/gui/container/nasa_workbench.png");

    private static final int[][] INPUT_SLOTS = {
        {56, 20}, {47, 38}, {65, 38}, {47, 56}, {65, 56}, {47, 74}, {65, 74},
        {29, 92}, {47, 92}, {65, 92}, {83, 92}, {29, 110}, {56, 110}, {83, 110}
    };

    private final IDrawable background;
    private final IDrawable icon;
    private final String localizedName;

    public NASAWorkbenchRecipeCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createDrawable(TEXTURE, 0, 0, 177, 142, 177, 224);
        this.icon = guiHelper.createDrawableIngredient(new ItemStack(ModBlocks.NASA_WORKBENCH));
        this.localizedName = I18n.format("tile.ad_astra.nasa_workbench.name");
    }

    @Nonnull
    @Override
    public String getUid() {
        return AdAstraJEIPlugin.NASA_WORKBENCH_CATEGORY;
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
    public void setRecipe(@Nonnull IRecipeLayout recipeLayout, @Nonnull NASAWorkbenchRecipeWrapper recipeWrapper,
                          @Nonnull IIngredients ingredients) {
        for (int i = 0; i < INPUT_SLOTS.length; i++) {
            recipeLayout.getItemStacks().init(i, true, INPUT_SLOTS[i][0], INPUT_SLOTS[i][1]);
        }
        recipeLayout.getItemStacks().init(INPUT_SLOTS.length, false, 129, 56);

        recipeLayout.getItemStacks().set(ingredients);
    }
}
