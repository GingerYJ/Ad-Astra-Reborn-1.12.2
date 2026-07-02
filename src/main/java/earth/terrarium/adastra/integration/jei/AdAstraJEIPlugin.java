package earth.terrarium.adastra.integration.jei;

import earth.terrarium.adastra.Reference;
import earth.terrarium.adastra.common.recipe.RecipeRegistry;
import earth.terrarium.adastra.common.registry.ModBlocks;
import earth.terrarium.adastra.common.registry.ModGuiIds;
import earth.terrarium.adastra.integration.jei.category.*;
// TODO: Implement transfer handlers
// import earth.terrarium.adastra.integration.jei.transfer.*;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import net.minecraft.item.ItemStack;

/**
 * JEI Integration Plugin for Ad Astra
 * Provides recipe viewing and transfer support for all Ad Astra machines
 */
@JEIPlugin
public class AdAstraJEIPlugin implements IModPlugin {

    public static final String COMPRESSOR_CATEGORY = Reference.MOD_ID + ":compressor";
    public static final String BLAST_FURNACE_CATEGORY = Reference.MOD_ID + ":etrionic_blast_furnace";
    public static final String CRYO_FREEZER_CATEGORY = Reference.MOD_ID + ":cryo_freezer";
    public static final String FUEL_REFINERY_CATEGORY = Reference.MOD_ID + ":fuel_refinery";
    public static final String OXYGEN_LOADER_CATEGORY = Reference.MOD_ID + ":oxygen_loader";
    public static final String NASA_WORKBENCH_CATEGORY = Reference.MOD_ID + ":nasa_workbench";

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        IJeiHelpers jeiHelpers = registration.getJeiHelpers();
        IGuiHelper guiHelper = jeiHelpers.getGuiHelper();

        registration.addRecipeCategories(
            new CompressorRecipeCategory(guiHelper),
            new BlastFurnaceRecipeCategory(guiHelper),
            new CryoFreezerRecipeCategory(guiHelper),
            new FuelRefineryRecipeCategory(guiHelper),
            new OxygenLoaderRecipeCategory(guiHelper),
            new NASAWorkbenchRecipeCategory(guiHelper)
        );
    }

    @Override
    public void register(IModRegistry registry) {
        // Register recipe handlers
        registry.handleRecipes(earth.terrarium.adastra.common.recipe.CompressingRecipe.class,
            recipe -> new CompressorRecipeWrapper(recipe), COMPRESSOR_CATEGORY);
        registry.handleRecipes(earth.terrarium.adastra.common.recipe.AlloySmeltingRecipe.class,
            recipe -> new BlastFurnaceRecipeWrapper(recipe), BLAST_FURNACE_CATEGORY);
        registry.handleRecipes(earth.terrarium.adastra.common.recipe.CryoFreezingRecipe.class,
            recipe -> new CryoFreezerRecipeWrapper(recipe), CRYO_FREEZER_CATEGORY);
        registry.handleRecipes(earth.terrarium.adastra.common.recipe.FuelRefiningRecipe.class,
            recipe -> new FuelRefineryRecipeWrapper(recipe), FUEL_REFINERY_CATEGORY);
        registry.handleRecipes(earth.terrarium.adastra.common.recipe.OxygenLoadingRecipe.class,
            recipe -> new OxygenLoaderRecipeWrapper(recipe), OXYGEN_LOADER_CATEGORY);
        registry.handleRecipes(earth.terrarium.adastra.common.recipe.NASAWorkbenchRecipe.class,
            recipe -> new NASAWorkbenchRecipeWrapper(recipe), NASA_WORKBENCH_CATEGORY);

        // Add recipes to JEI
        registry.addRecipes(RecipeRegistry.getAllCompressingRecipes(), COMPRESSOR_CATEGORY);
        registry.addRecipes(RecipeRegistry.getAllAlloyingRecipes(), BLAST_FURNACE_CATEGORY);
        registry.addRecipes(RecipeRegistry.getAllCryoFreezingRecipes(), CRYO_FREEZER_CATEGORY);
        registry.addRecipes(RecipeRegistry.getAllRefiningRecipes(), FUEL_REFINERY_CATEGORY);
        registry.addRecipes(RecipeRegistry.getAllOxygenLoadingRecipes(), OXYGEN_LOADER_CATEGORY);
        registry.addRecipes(RecipeRegistry.getAllNASAWorkbenchRecipes(), NASA_WORKBENCH_CATEGORY);

        // Add recipe catalysts (machines that perform these recipes)
        registry.addRecipeCatalyst(new ItemStack(ModBlocks.COMPRESSOR), COMPRESSOR_CATEGORY);
        registry.addRecipeCatalyst(new ItemStack(ModBlocks.ETRIONIC_BLAST_FURNACE), BLAST_FURNACE_CATEGORY);
        registry.addRecipeCatalyst(new ItemStack(ModBlocks.CRYO_FREEZER), CRYO_FREEZER_CATEGORY);
        registry.addRecipeCatalyst(new ItemStack(ModBlocks.FUEL_REFINERY), FUEL_REFINERY_CATEGORY);
        registry.addRecipeCatalyst(new ItemStack(ModBlocks.OXYGEN_LOADER), OXYGEN_LOADER_CATEGORY);
        registry.addRecipeCatalyst(new ItemStack(ModBlocks.NASA_WORKBENCH), NASA_WORKBENCH_CATEGORY);

        // TODO: Add recipe transfer handlers for GUI interaction
        // registry.getRecipeTransferRegistry().addRecipeTransferHandler(
        //     new CompressorRecipeTransferHandler(), COMPRESSOR_CATEGORY);
        // registry.getRecipeTransferRegistry().addRecipeTransferHandler(
        //     new BlastFurnaceRecipeTransferHandler(), BLAST_FURNACE_CATEGORY);
        // registry.getRecipeTransferRegistry().addRecipeTransferHandler(
        //     new CryoFreezerRecipeTransferHandler(), CRYO_FREEZER_CATEGORY);
        // registry.getRecipeTransferRegistry().addRecipeTransferHandler(
        //     new FuelRefineryRecipeTransferHandler(), FUEL_REFINERY_CATEGORY);
        // registry.getRecipeTransferRegistry().addRecipeTransferHandler(
        //     new OxygenLoaderRecipeTransferHandler(), OXYGEN_LOADER_CATEGORY);
        // registry.getRecipeTransferRegistry().addRecipeTransferHandler(
        //     new NASAWorkbenchRecipeTransferHandler(), NASA_WORKBENCH_CATEGORY);

        // Add click areas in GUIs to open recipe categories
        registry.addRecipeClickArea(earth.terrarium.adastra.client.gui.AdAstraMachineGui.class,
            72, 59, 15, 16, COMPRESSOR_CATEGORY);
        registry.addRecipeClickArea(earth.terrarium.adastra.client.gui.AdAstraMachineGui.class,
            75, 50, 20, 12, BLAST_FURNACE_CATEGORY);
        registry.addRecipeClickArea(earth.terrarium.adastra.client.gui.AdAstraMachineGui.class,
            54, 71, 13, 13, CRYO_FREEZER_CATEGORY);
    }
}
