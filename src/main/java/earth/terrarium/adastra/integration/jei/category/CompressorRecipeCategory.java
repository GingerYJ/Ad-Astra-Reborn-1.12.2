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

public class CompressorRecipeCategory implements IRecipeCategory<CompressorRecipeWrapper> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(Reference.MOD_ID,
        "textures/gui/container/compressor.png");
    private static final ResourceLocation HAMMER = new ResourceLocation(Reference.MOD_ID,
        "textures/gui/sprites/hammer.png");
    private static final ResourceLocation ENERGY_BAR = new ResourceLocation(Reference.MOD_ID,
        "textures/gui/sprites/energy_bar.png");

    private final IDrawable background;
    private final IDrawable icon;
    private final IDrawable hammer;
    private final IDrawable energyBar;
    private final String localizedName;

    public CompressorRecipeCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createDrawable(TEXTURE, 0, 0, 184, 110, 184, 201);
        this.icon = guiHelper.createDrawableIngredient(new ItemStack(ModBlocks.COMPRESSOR));
        this.hammer = guiHelper.createDrawable(HAMMER, 0, 0, 15, 16, 15, 16);
        this.energyBar = guiHelper.createDrawable(ENERGY_BAR, 0, 0, 13, 46, 13, 46);
        this.localizedName = I18n.format("tile.ad_astra.compressor.name");
    }

    @Nonnull
    @Override
    public String getUid() {
        return AdAstraJEIPlugin.COMPRESSOR_CATEGORY;
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
        hammer.draw(minecraft, 72, 59);
        energyBar.draw(minecraft, 150, 42);
    }

    @Override
    public void setRecipe(@Nonnull IRecipeLayout recipeLayout, @Nonnull CompressorRecipeWrapper recipeWrapper,
                          @Nonnull IIngredients ingredients) {
        recipeLayout.getItemStacks().init(0, true, 47, 58);
        recipeLayout.getItemStacks().init(1, false, 95, 58);

        recipeLayout.getItemStacks().set(ingredients);
    }
}
