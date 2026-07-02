package earth.terrarium.adastra.integration.jei.category;

import earth.terrarium.adastra.Reference;
import earth.terrarium.adastra.common.registry.ModBlocks;
import earth.terrarium.adastra.integration.jei.AdAstraJEIPlugin;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

public class CryoFreezerRecipeCategory implements IRecipeCategory<CryoFreezerRecipeWrapper> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(Reference.MOD_ID,
        "textures/gui/container/cryo_freezer.png");
    private static final ResourceLocation SNOWFLAKE = new ResourceLocation(Reference.MOD_ID,
        "textures/gui/sprites/snowflake.png");
    private static final ResourceLocation ENERGY_BAR = new ResourceLocation(Reference.MOD_ID,
        "textures/gui/sprites/energy_bar.png");
    private static final ResourceLocation FLUID_BAR = new ResourceLocation(Reference.MOD_ID,
        "textures/gui/sprites/fluid_bar.png");

    private final IDrawable background;
    private final IDrawable icon;
    private final IDrawable snowflake;
    private final IDrawable energyBar;
    private final IDrawable fluidBar;
    private final String localizedName;

    public CryoFreezerRecipeCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createDrawable(TEXTURE, 0, 0, 177, 100, 177, 184);
        this.icon = guiHelper.createDrawableIngredient(new ItemStack(ModBlocks.CRYO_FREEZER));
        this.snowflake = guiHelper.createDrawable(SNOWFLAKE, 0, 0, 13, 13, 13, 13);
        this.energyBar = guiHelper.createDrawable(ENERGY_BAR, 0, 0, 13, 46, 13, 46);
        this.fluidBar = guiHelper.createDrawable(FLUID_BAR, 0, 0, 12, 46, 12, 46);
        this.localizedName = I18n.format("tile.ad_astra.cryo_freezer.name");
    }

    @Nonnull
    @Override
    public String getUid() {
        return AdAstraJEIPlugin.CRYO_FREEZER_CATEGORY;
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
    public void drawExtras(@Nonnull Minecraft minecraft) {
        snowflake.draw(minecraft, 54, 71);
        fluidBar.draw(minecraft, 86, 38);
        energyBar.draw(minecraft, 149, 27);
    }

    @Override
    public void setRecipe(@Nonnull IRecipeLayout recipeLayout, @Nonnull CryoFreezerRecipeWrapper recipeWrapper,
                          @Nonnull IIngredients ingredients) {
        recipeLayout.getItemStacks().init(0, true, 26, 70);
        recipeLayout.getFluidStacks().init(1, false, 86, 38, 12, 46, 10000, false, null);

        recipeLayout.getItemStacks().set(ingredients);
        recipeLayout.getFluidStacks().set(ingredients);
    }
}
