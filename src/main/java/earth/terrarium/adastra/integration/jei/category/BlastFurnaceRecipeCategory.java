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

public class BlastFurnaceRecipeCategory implements IRecipeCategory<BlastFurnaceRecipeWrapper> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(Reference.MOD_ID,
        "textures/gui/container/etrionic_blast_furnace.png");
    private static final ResourceLocation ARROW = new ResourceLocation(Reference.MOD_ID,
        "textures/gui/sprites/arrow.png");
    private static final ResourceLocation ENERGY_BAR = new ResourceLocation(Reference.MOD_ID,
        "textures/gui/sprites/energy_bar.png");
    private static final ResourceLocation FURNACE_OVERLAY = new ResourceLocation(Reference.MOD_ID,
        "textures/gui/sprites/etrionic_blast_furnace_overlay.png");

    private final IDrawable background;
    private final IDrawable icon;
    private final IDrawable arrow;
    private final IDrawable energyBar;
    private final IDrawable furnaceOverlay;
    private final String localizedName;

    public BlastFurnaceRecipeCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createDrawable(TEXTURE, 0, 0, 184, 110, 184, 201);
        this.icon = guiHelper.createDrawableIngredient(new ItemStack(ModBlocks.ETRIONIC_BLAST_FURNACE));
        this.arrow = guiHelper.createDrawable(ARROW, 0, 0, 20, 12, 20, 12);
        this.energyBar = guiHelper.createDrawable(ENERGY_BAR, 0, 0, 13, 46, 13, 46);
        this.furnaceOverlay = guiHelper.createDrawable(FURNACE_OVERLAY, 0, 0, 32, 43, 32, 43);
        this.localizedName = I18n.format("tile.ad_astra.etrionic_blast_furnace.name");
    }

    @Nonnull
    @Override
    public String getUid() {
        return AdAstraJEIPlugin.BLAST_FURNACE_CATEGORY;
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
        furnaceOverlay.draw(minecraft, 30, 51);
        arrow.draw(minecraft, 75, 50);
        energyBar.draw(minecraft, 152, 35);
    }

    @Override
    public void setRecipe(@Nonnull IRecipeLayout recipeLayout, @Nonnull BlastFurnaceRecipeWrapper recipeWrapper,
                          @Nonnull IIngredients ingredients) {
        recipeLayout.getItemStacks().init(0, true, 29, 38);
        recipeLayout.getItemStacks().init(1, true, 47, 38);
        recipeLayout.getItemStacks().init(2, false, 101, 38);

        recipeLayout.getItemStacks().set(ingredients);
    }
}
