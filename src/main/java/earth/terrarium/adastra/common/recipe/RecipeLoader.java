package earth.terrarium.adastra.common.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import earth.terrarium.adastra.AdAstraReborn;
import earth.terrarium.adastra.Reference;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Central recipe loader for all Ad Astra machine recipes.
 * Loads recipes from JSON files in the resources folder.
 */
public final class RecipeLoader {

    private RecipeLoader() {
        // Utility class
    }

    /**
     * Loads all machine recipes from JSON files.
     * Should be called during mod initialization.
     */
    public static void loadAllRecipes() {
        AdAstraReborn.LOGGER.info("Loading Ad Astra machine recipes...");

        // Clear existing recipes
        RecipeRegistry.clearAll();
        ensureOreDictionaryFallbacks();

        // Load each recipe type
        int totalLoaded = 0;
        totalLoaded += loadCompressingRecipes();
        totalLoaded += loadAlloyingRecipes();
        totalLoaded += loadCryoFreezingRecipes();
        totalLoaded += loadOxygenLoadingRecipes();
        totalLoaded += loadRefiningRecipes();
        totalLoaded += loadNASAWorkbenchRecipes();
        totalLoaded += loadSpaceStationRecipes();

        AdAstraReborn.LOGGER.info("Finished loading {} Ad Astra machine recipes.", totalLoaded);
    }

    private static void ensureOreDictionaryFallbacks() {
        ensureOreDictionaryEntry("coal", new ItemStack(Items.COAL, 1, 0));
        ensureOreDictionaryEntry("coal", new ItemStack(Items.COAL, 1, 1));
    }

    private static void ensureOreDictionaryEntry(String oreName, ItemStack stack) {
        for (ItemStack existing : OreDictionary.getOres(oreName)) {
            if (ItemStack.areItemsEqual(existing, stack)) {
                return;
            }
        }
        OreDictionary.registerOre(oreName, stack);
    }

    // ==================== Recipe Type Loaders ====================

    private static int loadCompressingRecipes() {
        String[] recipePaths = {
            "data/ad_astra/recipes/compressing/calorite_plate_from_compressing_calorite_blocks.json",
            "data/ad_astra/recipes/compressing/calorite_plate_from_compressing_calorite_ingots.json",
            "data/ad_astra/recipes/compressing/desh_plate_from_compressing_desh_blocks.json",
            "data/ad_astra/recipes/compressing/desh_plate_from_compressing_desh_ingots.json",
            "data/ad_astra/recipes/compressing/iron_plate_from_compressing_iron_block.json",
            "data/ad_astra/recipes/compressing/iron_plate_from_compressing_iron_ingot.json",
            "data/ad_astra/recipes/compressing/ostrum_plate_from_compressing_ostrum_blocks.json",
            "data/ad_astra/recipes/compressing/ostrum_plate_from_compressing_ostrum_ingots.json",
            "data/ad_astra/recipes/compressing/steel_plate_from_compressing_steel_blocks.json",
            "data/ad_astra/recipes/compressing/steel_plate_from_compressing_steel_ingots.json"
        };

        int count = 0;
        for (String path : recipePaths) {
            if (loadCompressingRecipe(path)) {
                count++;
            }
        }
        AdAstraReborn.LOGGER.info("Loaded {} compressing recipes", count);
        return count;
    }

    private static int loadAlloyingRecipes() {
        String[] recipePaths = {
            "data/ad_astra/recipes/alloying/steel_ingot_from_alloying_iron_ingot_and_coals.json"
        };

        int count = 0;
        for (String path : recipePaths) {
            if (loadAlloyingRecipe(path)) {
                count++;
            }
        }
        AdAstraReborn.LOGGER.info("Loaded {} alloying recipes", count);
        return count;
    }

    private static int loadCryoFreezingRecipes() {
        String[] recipePaths = {
            "data/ad_astra/recipes/cryo_freezing/cryo_fuel_from_cryo_freezing_ice.json",
            "data/ad_astra/recipes/cryo_freezing/cryo_fuel_from_cryo_freezing_packed_ice.json",
            "data/ad_astra/recipes/cryo_freezing/cryo_fuel_from_cryo_freezing_ice_shard.json",
            "data/ad_astra/recipes/cryo_freezing/cryo_fuel_from_cryo_freezing_blue_ice.json"
        };

        int count = 0;
        for (String path : recipePaths) {
            if (loadCryoFreezingRecipe(path)) {
                count++;
            }
        }
        AdAstraReborn.LOGGER.info("Loaded {} cryo freezing recipes", count);
        return count;
    }

    private static int loadOxygenLoadingRecipes() {
        String[] recipePaths = {
            "data/ad_astra/recipes/oxygen_loading/oxygen_from_oxygen_loading_water.json",
            "data/ad_astra/recipes/oxygen_loading/oxygen_from_oxygen_loading_oxygen.json"
        };

        int count = 0;
        for (String path : recipePaths) {
            if (loadOxygenLoadingRecipe(path)) {
                count++;
            }
        }
        AdAstraReborn.LOGGER.info("Loaded {} oxygen loading recipes", count);
        return count;
    }

    private static int loadRefiningRecipes() {
        String[] recipePaths = {
            "data/ad_astra/recipes/refining/fuel_from_refining_oil.json"
        };

        int count = 0;
        for (String path : recipePaths) {
            if (loadRefiningRecipe(path)) {
                count++;
            }
        }
        AdAstraReborn.LOGGER.info("Loaded {} refining recipes", count);
        return count;
    }

    private static int loadNASAWorkbenchRecipes() {
        String[] recipePaths = {
            "data/ad_astra/recipes/nasa_workbench/tier_1_rocket_from_nasa_workbench.json",
            "data/ad_astra/recipes/nasa_workbench/tier_2_rocket_from_nasa_workbench.json",
            "data/ad_astra/recipes/nasa_workbench/tier_3_rocket_from_nasa_workbench.json",
            "data/ad_astra/recipes/nasa_workbench/tier_4_rocket_from_nasa_workbench.json"
        };

        int count = 0;
        for (String path : recipePaths) {
            if (loadNASAWorkbenchRecipe(path)) {
                count++;
            }
        }
        AdAstraReborn.LOGGER.info("Loaded {} NASA Workbench recipes", count);
        return count;
    }

    private static int loadSpaceStationRecipes() {
        String[] recipePaths = {
            "data/ad_astra/recipes/space_station/earth_orbit_space_station.json",
            "data/ad_astra/recipes/space_station/moon_orbit_space_station.json",
            "data/ad_astra/recipes/space_station/mars_orbit_space_station.json",
            "data/ad_astra/recipes/space_station/venus_orbit_space_station.json",
            "data/ad_astra/recipes/space_station/mercury_orbit_space_station.json",
            "data/ad_astra/recipes/space_station/glacio_orbit_space_station.json"
        };

        int count = 0;
        for (String path : recipePaths) {
            if (loadSpaceStationRecipe(path)) {
                count++;
            }
        }
        AdAstraReborn.LOGGER.info("Loaded {} Space Station recipes", count);
        return count;
    }

    // ==================== Individual Recipe Loaders ====================

    private static boolean loadCompressingRecipe(String path) {
        try (InputStream stream = RecipeLoader.class.getClassLoader().getResourceAsStream(path)) {
            if (stream == null) {
                AdAstraReborn.LOGGER.warn("Recipe file not found: {}", path);
                return false;
            }

            JsonObject json = new JsonParser().parse(
                new InputStreamReader(stream, StandardCharsets.UTF_8)
            ).getAsJsonObject();

            // Parse ingredient
            JsonObject ingredientJson = json.getAsJsonObject("ingredient");
            IngredientInfo ingredient = parseIngredient(ingredientJson);

            // Parse result
            ItemStack result = parseItemStack(json.getAsJsonObject("result"));

            // Parse processing parameters
            int cookingTime = json.get("cookingtime").getAsInt();
            int energy = json.get("energy").getAsInt();

            if (ingredient == null || result.isEmpty()) {
                AdAstraReborn.LOGGER.warn("Invalid compressing recipe: {}", path);
                return false;
            }

            // Extract ID from path
            String id = extractRecipeId(path);

            // Create recipe with ore dict support
            CompressingRecipe recipe = new CompressingRecipe(
                id,
                ingredient.toItemStack(),
                result,
                cookingTime,
                energy / cookingTime
            );

            RecipeRegistry.registerCompressingRecipe(recipe);
            return true;

        } catch (Exception e) {
            AdAstraReborn.LOGGER.error("Failed to load compressing recipe: {}", path, e);
            return false;
        }
    }

    private static boolean loadAlloyingRecipe(String path) {
        try (InputStream stream = RecipeLoader.class.getClassLoader().getResourceAsStream(path)) {
            if (stream == null) {
                return false;
            }

            JsonObject json = new JsonParser().parse(
                new InputStreamReader(stream, StandardCharsets.UTF_8)
            ).getAsJsonObject();

            // Parse ingredients
            JsonArray ingredientsJson = json.getAsJsonArray("ingredients");
            List<IngredientInfo> ingredients = new ArrayList<>();
            for (int i = 0; i < ingredientsJson.size(); i++) {
                IngredientInfo ingredient = parseIngredient(ingredientsJson.get(i).getAsJsonObject());
                if (ingredient != null) {
                    ingredients.add(ingredient);
                }
            }

            // Parse result
            ItemStack result = parseItemStack(json.getAsJsonObject("result"));

            // Parse processing parameters
            int cookingTime = json.get("cookingtime").getAsInt();
            int energy = json.get("energy").getAsInt();

            if (ingredients.size() < 2 || result.isEmpty()) {
                AdAstraReborn.LOGGER.warn("Invalid alloying recipe: {}", path);
                return false;
            }

            String id = extractRecipeId(path);

            // Create recipe with ore dict support
            ItemStack input1 = ingredients.get(0).toItemStack();
            ItemStack input2 = ingredients.get(1).toItemStack();
            String oreName1 = ingredients.get(0).isOreDict() ? ingredients.get(0).getOreName() : null;
            String oreName2 = ingredients.get(1).isOreDict() ? ingredients.get(1).getOreName() : null;

            AlloySmeltingRecipe recipe = new AlloySmeltingRecipe(
                id,
                input1,
                input2,
                result,
                cookingTime,
                Math.max(1, energy),
                oreName1,
                oreName2
            );

            RecipeRegistry.registerAlloyingRecipe(recipe);
            return true;

        } catch (Exception e) {
            AdAstraReborn.LOGGER.error("Failed to load alloying recipe: {}", path, e);
            return false;
        }
    }

    private static boolean loadCryoFreezingRecipe(String path) {
        try (InputStream stream = RecipeLoader.class.getClassLoader().getResourceAsStream(path)) {
            if (stream == null) {
                return false;
            }

            JsonObject json = new JsonParser().parse(
                new InputStreamReader(stream, StandardCharsets.UTF_8)
            ).getAsJsonObject();

            // Parse ingredient
            IngredientInfo ingredient = parseIngredient(json.getAsJsonObject("ingredient"));

            // Parse result (fluid)
            FluidStack result = parseFluidStack(json.getAsJsonObject("result"));

            // Parse processing parameters
            int cookingTime = json.get("cookingtime").getAsInt();
            int energy = json.get("energy").getAsInt();

            if (ingredient == null || result == null) {
                AdAstraReborn.LOGGER.warn("Invalid cryo freezing recipe: {}", path);
                return false;
            }

            String id = extractRecipeId(path);

            CryoFreezingRecipe recipe = new CryoFreezingRecipe(
                id,
                ingredient.toItemStack(),
                result.getFluid(),
                result.amount,
                cookingTime,
                energy / cookingTime
            );

            RecipeRegistry.registerCryoFreezingRecipe(recipe);
            return true;

        } catch (Exception e) {
            AdAstraReborn.LOGGER.error("Failed to load cryo freezing recipe: {}", path, e);
            return false;
        }
    }

    private static boolean loadOxygenLoadingRecipe(String path) {
        try (InputStream stream = RecipeLoader.class.getClassLoader().getResourceAsStream(path)) {
            if (stream == null) {
                return false;
            }

            JsonObject json = new JsonParser().parse(
                new InputStreamReader(stream, StandardCharsets.UTF_8)
            ).getAsJsonObject();

            // Parse input fluid from nested structure
            JsonObject inputJson = json.getAsJsonObject("input");
            FluidStack inputFluid = null;
            if (inputJson.has("ingredient")) {
                JsonObject ingredientJson = inputJson.getAsJsonObject("ingredient");
                // Get fluid from tag (e.g., "tag": "minecraft:water")
                String fluidId = null;
                if (ingredientJson.has("tag")) {
                    String tag = ingredientJson.get("tag").getAsString();
                    // Extract fluid name from tag (e.g., "minecraft:water" -> "water")
                    fluidId = tag.contains(":") ? tag.substring(tag.indexOf(':') + 1) : tag;
                } else if (ingredientJson.has("fluid")) {
                    fluidId = ingredientJson.get("fluid").getAsString();
                }

                if (fluidId != null) {
                    Fluid fluid = FluidRegistry.getFluid(fluidId);
                    if (fluid != null) {
                        int amount = inputJson.has("millibuckets") ? inputJson.get("millibuckets").getAsInt() : 1000;
                        inputFluid = new FluidStack(fluid, amount);
                    }
                }
            }

            // Parse result fluid
            FluidStack resultFluid = parseFluidStack(json.getAsJsonObject("result"));

            // Parse processing parameters
            int cookingTime = json.get("cookingtime").getAsInt();
            int energy = json.get("energy").getAsInt();

            if (inputFluid == null || resultFluid == null) {
                AdAstraReborn.LOGGER.warn("Invalid oxygen loading recipe: {}", path);
                return false;
            }

            String id = extractRecipeId(path);

            // Create fluid-to-fluid conversion recipe
            OxygenLoadingRecipe recipe = new OxygenLoadingRecipe(
                id,
                ItemStack.EMPTY,
                inputFluid.getFluid(),
                inputFluid.amount,
                ItemStack.EMPTY,
                cookingTime,
                energy / cookingTime
            );

            RecipeRegistry.registerOxygenLoadingRecipe(recipe);
            return true;

        } catch (Exception e) {
            AdAstraReborn.LOGGER.error("Failed to load oxygen loading recipe: {}", path, e);
            return false;
        }
    }

    private static boolean loadRefiningRecipe(String path) {
        try (InputStream stream = RecipeLoader.class.getClassLoader().getResourceAsStream(path)) {
            if (stream == null) {
                return false;
            }

            JsonObject json = new JsonParser().parse(
                new InputStreamReader(stream, StandardCharsets.UTF_8)
            ).getAsJsonObject();

            // Parse input fluid from nested structure
            FluidStack inputFluid = null;
            if (json.has("input")) {
                JsonObject inputJson = json.getAsJsonObject("input");
                if (inputJson.has("ingredient")) {
                    JsonObject ingredientJson = inputJson.getAsJsonObject("ingredient");
                    // Get fluid from tag (e.g., "tag": "ad_astra:oil")
                    String fluidId = null;
                    if (ingredientJson.has("tag")) {
                        String tag = ingredientJson.get("tag").getAsString();
                        // Extract fluid name from tag
                        fluidId = tag.contains(":") ? tag.substring(tag.indexOf(':') + 1) : tag;
                    } else if (ingredientJson.has("fluid")) {
                        fluidId = ingredientJson.get("fluid").getAsString();
                    }

                    if (fluidId != null) {
                        Fluid fluid = FluidRegistry.getFluid(fluidId);
                        if (fluid != null) {
                            int amount = inputJson.has("millibuckets") ? inputJson.get("millibuckets").getAsInt() : 1000;
                            inputFluid = new FluidStack(fluid, amount);
                        }
                    }
                }
            }

            // Parse result fluid
            FluidStack resultFluid = parseFluidStack(json.getAsJsonObject("result"));

            // Parse processing parameters
            int cookingTime = json.get("cookingtime").getAsInt();
            int energy = json.get("energy").getAsInt();

            if (inputFluid == null || resultFluid == null) {
                AdAstraReborn.LOGGER.warn("Invalid refining recipe: {}", path);
                return false;
            }

            String id = extractRecipeId(path);

            // Create recipe with input fluid info (stored in ItemStack as empty placeholder)
            // The actual fluid processing is handled by the FuelRefineryTileEntity
            FuelRefiningRecipe recipe = new FuelRefiningRecipe(
                id,
                ItemStack.EMPTY,
                resultFluid.getFluid(),
                resultFluid.amount,
                cookingTime,
                energy / cookingTime
            );

            RecipeRegistry.registerRefiningRecipe(recipe);
            return true;

        } catch (Exception e) {
            AdAstraReborn.LOGGER.error("Failed to load refining recipe: {}", path, e);
            return false;
        }
    }

    private static boolean loadNASAWorkbenchRecipe(String path) {
        try (InputStream stream = RecipeLoader.class.getClassLoader().getResourceAsStream(path)) {
            if (stream == null) {
                AdAstraReborn.LOGGER.warn("Recipe file not found: {}", path);
                return false;
            }

            JsonObject json = new JsonParser().parse(
                new InputStreamReader(stream, StandardCharsets.UTF_8)
            ).getAsJsonObject();

            // Parse ingredients array (14 slots for NASA Workbench)
            JsonArray ingredientsJson = json.getAsJsonArray("ingredients");
            List<ItemStack> ingredients = new ArrayList<>();

            for (int i = 0; i < ingredientsJson.size(); i++) {
                IngredientInfo ingredient = parseIngredient(ingredientsJson.get(i).getAsJsonObject());
                if (ingredient != null) {
                    ingredients.add(ingredient.toItemStack());
                } else {
                    ingredients.add(ItemStack.EMPTY);
                }
            }

            // Parse result
            ItemStack result = parseItemStack(json.getAsJsonObject("result"));

            if (ingredients.isEmpty() || result.isEmpty()) {
                AdAstraReborn.LOGGER.warn("Invalid NASA Workbench recipe: {}", path);
                return false;
            }

            String id = extractRecipeId(path);

            // NASA Workbench uses a 14-slot pattern (special layout)
            // Default processing parameters
            int processingTime = 200; // 10 seconds
            int energyPerTick = 10;

            NASAWorkbenchRecipe recipe = new NASAWorkbenchRecipe(
                id,
                ingredients,
                result,
                3, // width
                5, // height (14 slots total in special layout)
                processingTime,
                energyPerTick
            );

            RecipeRegistry.registerNASAWorkbenchRecipe(recipe);
            return true;

        } catch (Exception e) {
            AdAstraReborn.LOGGER.error("Failed to load NASA Workbench recipe: {}", path, e);
            return false;
        }
    }

    private static boolean loadSpaceStationRecipe(String path) {
        try (InputStream stream = RecipeLoader.class.getClassLoader().getResourceAsStream(path)) {
            if (stream == null) {
                AdAstraReborn.LOGGER.warn("Recipe file not found: {}", path);
                return false;
            }

            JsonObject json = new JsonParser().parse(
                new InputStreamReader(stream, StandardCharsets.UTF_8)
            ).getAsJsonObject();

            // Parse dimension
            String dimensionString = json.get("dimension").getAsString();
            ResourceLocation dimension = new ResourceLocation(dimensionString);

            // Parse structure
            String structure = json.get("structure").getAsString();

            // Parse ingredient requirements
            JsonArray ingredientsJson = json.getAsJsonArray("ingredients");
            List<SpaceStationRecipe.IngredientRequirement> requirements = new ArrayList<>();

            for (int i = 0; i < ingredientsJson.size(); i++) {
                JsonObject reqJson = ingredientsJson.get(i).getAsJsonObject();
                int count = reqJson.get("count").getAsInt();

                JsonObject ingredientJson = reqJson.getAsJsonObject("ingredient");

                // Parse the ingredient (item or tag-based)
                if (ingredientJson.has("tag")) {
                    String tag = ingredientJson.get("tag").getAsString();
                    String oreName = convertTagToOreName(tag);
                    requirements.add(new SpaceStationRecipe.IngredientRequirement(oreName, count));
                } else if (ingredientJson.has("item")) {
                    String itemId = ingredientJson.get("item").getAsString();
                    Item item = Item.REGISTRY.getObject(new ResourceLocation(itemId));
                    if (item != null) {
                        ItemStack stack = new ItemStack(item, 1);
                        requirements.add(new SpaceStationRecipe.IngredientRequirement(stack, count));
                    }
                }
            }

            if (requirements.isEmpty()) {
                AdAstraReborn.LOGGER.warn("Invalid Space Station recipe: {}", path);
                return false;
            }

            String id = extractRecipeId(path);

            SpaceStationRecipe recipe = new SpaceStationRecipe(
                id,
                dimension,
                structure,
                requirements
            );

            RecipeRegistry.registerSpaceStationRecipe(recipe);
            return true;

        } catch (Exception e) {
            AdAstraReborn.LOGGER.error("Failed to load Space Station recipe: {}", path, e);
            return false;
        }
    }

    // ==================== JSON Parsing Utilities ====================

    private static String extractRecipeId(String path) {
        String fileName = path.substring(path.lastIndexOf('/') + 1);
        if (fileName.endsWith(".json")) {
            fileName = fileName.substring(0, fileName.length() - 5);
        }
        return Reference.MOD_ID + ":" + fileName;
    }

    /**
     * Parses an ingredient from JSON.
     * Supports item, ore dictionary, and tag-based ingredients.
     */
    private static IngredientInfo parseIngredient(JsonObject json) {
        if (json.has("item")) {
            String itemId = json.get("item").getAsString();
            Item item = Item.REGISTRY.getObject(new ResourceLocation(itemId));
            if (item == null) {
                AdAstraReborn.LOGGER.warn("Unknown item: {}", itemId);
                return null;
            }
            int count = json.has("count") ? json.get("count").getAsInt() : 1;
            int meta = json.has("data") ? json.get("data").getAsInt() : 0;
            return new IngredientInfo(new ItemStack(item, count, meta), null);
        } else if (json.has("ore")) {
            String oreName = json.get("ore").getAsString();
            int count = json.has("count") ? json.get("count").getAsInt() : 1;
            return new IngredientInfo(null, oreName, count);
        } else if (json.has("tag")) {
            // Support for tag-based ingredients (treat as ore dictionary)
            String tag = json.get("tag").getAsString();
            int count = json.has("count") ? json.get("count").getAsInt() : 1;
            if (isCoalTag(tag)) {
                ensureOreDictionaryFallbacks();
                return new IngredientInfo(new ItemStack(Items.COAL, count, 0), "coal", count);
            }
            // Convert tag format to ore dictionary format
            // "minecraft:coals" -> "coals", "ad_astra:steel_blocks" -> "blockSteel"
            String oreName = convertTagToOreName(tag);
            return new IngredientInfo(null, oreName, count);
        }
        return null;
    }

    private static boolean isCoalTag(String tag) {
        return "minecraft:coals".equals(tag) || "coals".equals(tag);
    }

    /**
     * Converts a tag string to an ore dictionary name.
     * Examples:
     * - "minecraft:coals" -> "coals"
     * - "ad_astra:steel_blocks" -> "blockSteel"
     */
    private static String convertTagToOreName(String tag) {
        // Remove namespace
        String key = tag.contains(":") ? tag.substring(tag.indexOf(':') + 1) : tag;

        // Handle common conversions
        if (key.equals("coals")) {
            return "coal"; // OreDictionary uses "coal" not "coals"
        }
        if (key.endsWith("_blocks")) {
            // Convert "steel_blocks" to "blockSteel"
            String material = key.substring(0, key.length() - "_blocks".length());
            return "block" + capitalize(material);
        }
        if (key.endsWith("_ingots")) {
            // Convert "steel_ingots" to "ingotSteel"
            String material = key.substring(0, key.length() - "_ingots".length());
            return "ingot" + capitalize(material);
        }
        if (key.endsWith("_plates")) {
            // Convert "steel_plates" to "plateSteel"
            String material = key.substring(0, key.length() - "_plates".length());
            return "plate" + capitalize(material);
        }

        return key;
    }

    private static String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    /**
     * Parses an ItemStack from JSON.
     */
    private static ItemStack parseItemStack(JsonObject json) {
        String itemId = json.has("item") ? json.get("item").getAsString()
            : json.has("id") ? json.get("id").getAsString() : null;

        if (itemId == null) {
            return ItemStack.EMPTY;
        }

        Item item = Item.REGISTRY.getObject(new ResourceLocation(itemId));
        if (item == null) {
            AdAstraReborn.LOGGER.warn("Unknown item: {}", itemId);
            return ItemStack.EMPTY;
        }
        int count = json.has("count") ? json.get("count").getAsInt() : 1;
        int meta = json.has("data") ? json.get("data").getAsInt() : 0;
        return new ItemStack(item, count, meta);
    }

    /**
     * Parses a FluidStack from JSON.
     */
    private static FluidStack parseFluidStack(JsonObject json) {
        String fluidId = json.has("fluid") ? json.get("fluid").getAsString()
            : json.has("id") ? json.get("id").getAsString() : null;

        if (fluidId == null) {
            return null;
        }

        // Remove namespace if present (e.g., "ad_astra:oxygen" -> "oxygen")
        if (fluidId.contains(":")) {
            fluidId = fluidId.substring(fluidId.indexOf(':') + 1);
        }

        Fluid fluid = FluidRegistry.getFluid(fluidId);
        if (fluid == null) {
            AdAstraReborn.LOGGER.warn("Unknown fluid: {}", fluidId);
            return null;
        }
        int amount = json.has("amount") ? json.get("amount").getAsInt() : 1000;
        return new FluidStack(fluid, amount);
    }

    // ==================== Helper Classes ====================

    /**
     * Helper class to hold ingredient information from JSON.
     */
    private static class IngredientInfo {
        private final ItemStack itemStack;
        private final String oreName;
        private final int count;

        public IngredientInfo(ItemStack itemStack, String oreName) {
            this(itemStack, oreName, itemStack != null ? itemStack.getCount() : 1);
        }

        public IngredientInfo(ItemStack itemStack, String oreName, int count) {
            this.itemStack = itemStack;
            this.oreName = oreName;
            this.count = count;
        }

        public ItemStack toItemStack() {
            if (itemStack != null) {
                return itemStack.copy();
            } else if (oreName != null) {
                List<ItemStack> ores = OreDictionary.getOres(oreName);
                if (!ores.isEmpty()) {
                    ItemStack stack = ores.get(0).copy();
                    stack.setCount(count);
                    return stack;
                }
            }
            return ItemStack.EMPTY;
        }

        public boolean isOreDict() {
            return oreName != null;
        }

        public String getOreName() {
            return oreName;
        }
    }
}
