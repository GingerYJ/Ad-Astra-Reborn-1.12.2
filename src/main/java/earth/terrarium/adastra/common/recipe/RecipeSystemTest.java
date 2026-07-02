package earth.terrarium.adastra.common.recipe;

import earth.terrarium.adastra.AdAstraReborn;
import net.minecraft.item.ItemStack;

/**
 * Simple test class to verify recipe system functionality.
 * This can be called during mod initialization to verify recipes are loaded correctly.
 */
public class RecipeSystemTest {

    /**
     * Runs basic tests on the recipe system.
     * Logs results to the console.
     */
    public static void runTests() {
        AdAstraReborn.LOGGER.info("Running recipe system tests...");

        testRecipeRegistry();
        testRecipeIngredient();

        AdAstraReborn.LOGGER.info("Recipe system tests completed.");
    }

    private static void testRecipeRegistry() {
        // Test that registry methods don't throw exceptions
        try {
            int totalRecipes = RecipeRegistry.getTotalRecipeCount();
            AdAstraReborn.LOGGER.info("Total recipes registered: {}", totalRecipes);

            int compressingCount = RecipeRegistry.getAllCompressingRecipes().size();
            int alloyingCount = RecipeRegistry.getAllAlloyingRecipes().size();
            int cryoCount = RecipeRegistry.getAllCryoFreezingRecipes().size();
            int oxygenCount = RecipeRegistry.getAllOxygenLoadingRecipes().size();
            int refiningCount = RecipeRegistry.getAllRefiningRecipes().size();
            int nasaWorkbenchCount = RecipeRegistry.getAllNASAWorkbenchRecipes().size();
            int spaceStationCount = RecipeRegistry.getAllSpaceStationRecipes().size();

            AdAstraReborn.LOGGER.info("Recipe counts - Compressing: {}, Alloying: {}, Cryo: {}, Oxygen: {}, Refining: {}, NASA Workbench: {}, Space Station: {}",
                compressingCount, alloyingCount, cryoCount, oxygenCount, refiningCount, nasaWorkbenchCount, spaceStationCount);

            // Verify total matches sum
            int sum = compressingCount + alloyingCount + cryoCount + oxygenCount + refiningCount + nasaWorkbenchCount + spaceStationCount;
            if (sum != totalRecipes) {
                AdAstraReborn.LOGGER.warn("Recipe count mismatch! Total: {}, Sum: {}", totalRecipes, sum);
            } else {
                AdAstraReborn.LOGGER.info("Recipe counts verified successfully.");
            }

        } catch (Exception e) {
            AdAstraReborn.LOGGER.error("RecipeRegistry test failed!", e);
        }
    }

    private static void testRecipeIngredient() {
        try {
            // Test item-based ingredient
            RecipeIngredient itemIngredient = new RecipeIngredient(
                new ItemStack(net.minecraft.init.Items.IRON_INGOT)
            );
            if (!itemIngredient.isOreDictionary()) {
                AdAstraReborn.LOGGER.info("Item ingredient test passed.");
            } else {
                AdAstraReborn.LOGGER.warn("Item ingredient incorrectly marked as ore dictionary!");
            }

            // Test ore dictionary ingredient
            RecipeIngredient oreIngredient = new RecipeIngredient("ingotIron");
            if (oreIngredient.isOreDictionary()) {
                AdAstraReborn.LOGGER.info("Ore dictionary ingredient test passed.");
            } else {
                AdAstraReborn.LOGGER.warn("Ore ingredient incorrectly marked as item!");
            }

            // Test matching
            ItemStack ironIngot = new ItemStack(net.minecraft.init.Items.IRON_INGOT);
            if (itemIngredient.matches(ironIngot) && oreIngredient.matches(ironIngot)) {
                AdAstraReborn.LOGGER.info("Ingredient matching test passed.");
            } else {
                AdAstraReborn.LOGGER.warn("Ingredient matching test failed!");
            }

        } catch (Exception e) {
            AdAstraReborn.LOGGER.error("RecipeIngredient test failed!", e);
        }
    }

    /**
     * Prints a summary of all registered recipes.
     * Useful for debugging.
     */
    public static void printRecipeSummary() {
        AdAstraReborn.LOGGER.info("=== Recipe System Summary ===");

        AdAstraReborn.LOGGER.info("Compressing Recipes: {}", RecipeRegistry.getAllCompressingRecipes().size());
        for (CompressingRecipe recipe : RecipeRegistry.getAllCompressingRecipes()) {
            AdAstraReborn.LOGGER.info("  - {} ({}t, {}FE/t)",
                recipe.getId(), recipe.getCookTime(), recipe.getEnergyPerTick());
        }

        AdAstraReborn.LOGGER.info("Alloying Recipes: {}", RecipeRegistry.getAllAlloyingRecipes().size());
        for (AlloySmeltingRecipe recipe : RecipeRegistry.getAllAlloyingRecipes()) {
            AdAstraReborn.LOGGER.info("  - {} ({}t, {}FE/t)",
                recipe.getId(), recipe.getCookTime(), recipe.getEnergyPerTick());
        }

        AdAstraReborn.LOGGER.info("Cryo Freezing Recipes: {}", RecipeRegistry.getAllCryoFreezingRecipes().size());
        for (CryoFreezingRecipe recipe : RecipeRegistry.getAllCryoFreezingRecipes()) {
            AdAstraReborn.LOGGER.info("  - {} ({}t, {}FE/t)",
                recipe.getId(), recipe.getCookTime(), recipe.getEnergyPerTick());
        }

        AdAstraReborn.LOGGER.info("Oxygen Loading Recipes: {}", RecipeRegistry.getAllOxygenLoadingRecipes().size());
        for (OxygenLoadingRecipe recipe : RecipeRegistry.getAllOxygenLoadingRecipes()) {
            AdAstraReborn.LOGGER.info("  - {} ({}t, {}FE/t)",
                recipe.getId(), recipe.getCookTime(), recipe.getEnergyPerTick());
        }

        AdAstraReborn.LOGGER.info("Refining Recipes: {}", RecipeRegistry.getAllRefiningRecipes().size());
        for (FuelRefiningRecipe recipe : RecipeRegistry.getAllRefiningRecipes()) {
            AdAstraReborn.LOGGER.info("  - {} ({}t, {}FE/t)",
                recipe.getId(), recipe.getCookTime(), recipe.getEnergyPerTick());
        }

        AdAstraReborn.LOGGER.info("NASA Workbench Recipes: {}", RecipeRegistry.getAllNASAWorkbenchRecipes().size());
        for (NASAWorkbenchRecipe recipe : RecipeRegistry.getAllNASAWorkbenchRecipes()) {
            AdAstraReborn.LOGGER.info("  - {} ({}t, {}FE/t)",
                recipe.getId(), recipe.getCookTime(), recipe.getEnergyPerTick());
        }

        AdAstraReborn.LOGGER.info("Space Station Recipes: {}", RecipeRegistry.getAllSpaceStationRecipes().size());
        for (SpaceStationRecipe recipe : RecipeRegistry.getAllSpaceStationRecipes()) {
            AdAstraReborn.LOGGER.info("  - {} for dimension {}",
                recipe.getId(), recipe.getDimension());
        }

        AdAstraReborn.LOGGER.info("=== End Summary ===");
    }
}
