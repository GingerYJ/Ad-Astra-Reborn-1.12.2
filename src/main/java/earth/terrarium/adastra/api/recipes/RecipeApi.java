package earth.terrarium.adastra.api.recipes;

import earth.terrarium.adastra.api.ApiHelper;
import net.minecraft.item.ItemStack;

import java.util.List;

/**
 * API for registering and querying custom machine recipes in the Ad Astra mod.
 * <p>
 * This interface allows third-party mods to:
 * <ul>
 *   <li>Register custom recipes for Ad Astra machines</li>
 *   <li>Query existing recipes by type</li>
 *   <li>Create recipes for Compressors, Alloy Smelters, Fuel Refiners, etc.</li>
 * </ul>
 * <p>
 * Example usage:
 * <pre>{@code
 * // Register a custom compressing recipe
 * RecipeApi.API.registerCompressingRecipe(
 *     "custom_compressed_iron",
 *     Collections.singletonList(new ItemStack(Items.IRON_INGOT, 9)),
 *     Collections.singletonList(new ItemStack(ModItems.COMPRESSED_IRON)),
 *     200, // 10 seconds (200 ticks)
 *     10   // 10 FE/tick
 * );
 * }</pre>
 * <p>
 * Third-party mods can use {@link #API} to access the implementation.
 *
 * @since 1.12.2
 */
public interface RecipeApi {

    /**
     * The singleton API instance. Loaded via ServiceLoader.
     */
    RecipeApi API = ApiHelper.load(RecipeApi.class);

    /**
     * Recipe type identifiers for different Ad Astra machines.
     */
    enum RecipeType {
        /** NASA Workbench recipes for crafting rockets and rovers */
        NASA_WORKBENCH,
        /** Compressor recipes for compressing materials */
        COMPRESSING,
        /** Alloy Smelter recipes for creating alloys */
        ALLOY_SMELTING,
        /** Fuel Refiner recipes for creating rocket fuel */
        FUEL_REFINING,
        /** Cryo Freezer recipes for freezing items */
        CRYO_FREEZING,
        /** Oxygen Loader recipes for filling oxygen tanks */
        OXYGEN_LOADING
    }

    /**
     * Registers a custom compressing recipe for the Compressor machine.
     * <p>
     * The Compressor is used to compress materials into denser forms.
     *
     * @param id Unique recipe identifier (e.g., "modid:compressed_copper")
     * @param inputs List of input ItemStacks required
     * @param outputs List of output ItemStacks produced
     * @param processingTime Total processing time in ticks (20 ticks = 1 second)
     * @param energyPerTick Energy consumed per tick (in Forge Energy units)
     * @return true if the recipe was registered successfully, false otherwise
     */
    boolean registerCompressingRecipe(String id, List<ItemStack> inputs, List<ItemStack> outputs, int processingTime, int energyPerTick);

    /**
     * Registers a custom alloy smelting recipe for the Alloy Smelter machine.
     * <p>
     * The Alloy Smelter is used to combine metals into alloys like Steel or Desh.
     *
     * @param id Unique recipe identifier
     * @param inputs List of input ItemStacks required (typically 2-3 metals)
     * @param outputs List of output ItemStacks produced
     * @param processingTime Total processing time in ticks
     * @param energyPerTick Energy consumed per tick
     * @return true if the recipe was registered successfully, false otherwise
     */
    boolean registerAlloySmeltingRecipe(String id, List<ItemStack> inputs, List<ItemStack> outputs, int processingTime, int energyPerTick);

    /**
     * Registers a custom cryo freezing recipe for the Cryo Freezer machine.
     * <p>
     * The Cryo Freezer is used to freeze items at extremely low temperatures.
     *
     * @param id Unique recipe identifier
     * @param inputs List of input ItemStacks required
     * @param outputs List of output ItemStacks produced
     * @param processingTime Total processing time in ticks
     * @param energyPerTick Energy consumed per tick
     * @return true if the recipe was registered successfully, false otherwise
     */
    boolean registerCryoFreezingRecipe(String id, List<ItemStack> inputs, List<ItemStack> outputs, int processingTime, int energyPerTick);

    /**
     * Gets all registered recipes of a specific type.
     * <p>
     * This is useful for JEI integration or recipe book implementations.
     *
     * @param type The recipe type to query
     * @return List of recipe objects (type depends on RecipeType)
     */
    List<?> getRecipes(RecipeType type);

    /**
     * Finds a matching recipe for the given inputs and recipe type.
     * <p>
     * This method checks if the provided inputs match any registered recipe
     * of the specified type.
     *
     * @param type The recipe type to search
     * @param inputs Array of ItemStacks to match against recipes
     * @return The matching recipe object, or null if no match found
     */
    Object findRecipe(RecipeType type, ItemStack[] inputs);

    /**
     * Removes a recipe by its identifier.
     * <p>
     * This is useful for removing conflicting recipes or disabling default recipes.
     *
     * @param type The recipe type
     * @param id The recipe identifier to remove
     * @return true if a recipe was removed, false if not found
     */
    boolean removeRecipe(RecipeType type, String id);
}
