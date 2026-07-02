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

public class OxygenLoaderRecipeCategory implements IRecipeCategory<OxygenLoaderRecipeWrapper> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(Reference.MOD_ID,
        "textures/gui/container/oxygen_loader.png");
    private static final ResourceLocation ENERGY_BAR = new ResourceLocation(Reference.MOD_ID,
        "textures/gui/sprites/energy_bar.png");
    private static final ResourceLocation FLUID_BAR = new ResourceLocation(Reference.MOD_ID,
        "textures/gui/sprites/fluid_bar.png");

    private final IDrawable background;
    private final IDrawable icon;
    private final IDrawable energyBar;
    private final IDrawable fluidBar;
    private final String localizedName;

    public OxygenLoaderRecipeCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createDrawable(TEXTURE, 0, 0, 177, 100, 177, 184);
        this.icon = guiHelper.createDrawableIngredient(new ItemStack(ModBlocks.OXYGEN_LOADER));
        this.energyBar = guiHelper.createDrawable(ENERGY_BAR, 0, 0, 13, 46, 13, 46);
        this.fluidBar = guiHelper.createDrawable(FLUID_BAR, 0, 0, 12, 46, 12, 46);
        this.localizedName = I18n.format("tile.ad_astra.oxygen_loader.name");
    }

    @Nonnull
    @Override
    public String getUid() {
        return AdAstraJEIPlugin.OXYGEN_LOADER_CATEGORY;
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
        fluidBar.draw(minecraft, 43, 22);
        fluidBar.draw(minecraft, 100, 22);
        energyBar.draw(minecraft, 150, 22);
    }

    @Override
    public void setRecipe(@Nonnull IRecipeLayout recipeLayout, @Nonnull OxygenLoaderRecipeWrapper recipeWrapper,
                          @Nonnull IIngredients ingredients) {
        recipeLayout.getItemStacks().init(0, true, 77, 52);
        recipeLayout.getFluidStacks().init(1, true, 43, 22, 12, 46, 10000, false, null);
        recipeLayout.getItemStacks().init(2, false, 127, 52);

        recipeLayout.getItemStacks().set(ingredients);
        recipeLayout.getFluidStacks().set(ingredients);
    }
}
