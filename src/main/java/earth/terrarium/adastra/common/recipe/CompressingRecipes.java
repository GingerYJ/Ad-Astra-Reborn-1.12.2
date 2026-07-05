package earth.terrarium.adastra.common.recipe;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import earth.terrarium.adastra.AdAstraReborn;
import earth.terrarium.adastra.Reference;
import earth.terrarium.adastra.common.registry.ModBlocks;
import earth.terrarium.adastra.common.registry.ModItems;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.OreDictionary;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class CompressingRecipes {

    private static final String[] GENERATED_RECIPE_PATHS = {
        "data/ad_astra/machine_recipes/compressing/calorite_plate_from_compressing_calorite_blocks.json",
        "data/ad_astra/machine_recipes/compressing/calorite_plate_from_compressing_calorite_ingots.json",
        "data/ad_astra/machine_recipes/compressing/desh_plate_from_compressing_desh_blocks.json",
        "data/ad_astra/machine_recipes/compressing/desh_plate_from_compressing_desh_ingots.json",
        "data/ad_astra/machine_recipes/compressing/iron_plate_from_compressing_iron_block.json",
        "data/ad_astra/machine_recipes/compressing/iron_plate_from_compressing_iron_ingot.json",
        "data/ad_astra/machine_recipes/compressing/ostrum_plate_from_compressing_ostrum_blocks.json",
        "data/ad_astra/machine_recipes/compressing/ostrum_plate_from_compressing_ostrum_ingots.json",
        "data/ad_astra/machine_recipes/compressing/steel_plate_from_compressing_steel_blocks.json",
        "data/ad_astra/machine_recipes/compressing/steel_plate_from_compressing_steel_ingots.json"
    };

    private static final List<Recipe> RECIPES = new ArrayList<>();

    private CompressingRecipes() {
    }

    public static void register() {
        RECIPES.clear();
        for (String path : GENERATED_RECIPE_PATHS) {
            Recipe recipe = readGeneratedRecipe(path);
            if (recipe != null) {
                RECIPES.add(recipe);
            }
        }
        registerFallbacks();
        AdAstraReborn.LOGGER.info("Loaded {} compressor recipes.", RECIPES.size());
    }

    public static Recipe find(ItemStack stack) {
        if (stack.isEmpty()) {
            return null;
        }
        for (Recipe recipe : RECIPES) {
            if (recipe.matches(stack)) {
                return recipe;
            }
        }
        return null;
    }

    public static Recipe getById(String id) {
        for (Recipe recipe : RECIPES) {
            if (recipe.getId().equals(id)) {
                return recipe;
            }
        }
        return null;
    }

    public static List<Recipe> all() {
        return Collections.unmodifiableList(RECIPES);
    }

    private static Recipe readGeneratedRecipe(String path) {
        try (InputStream stream = CompressingRecipes.class.getClassLoader().getResourceAsStream(path)) {
            if (stream == null) {
                AdAstraReborn.LOGGER.warn("Missing generated compressor recipe {}.", path);
                return null;
            }
            JsonObject json = new JsonParser().parse(new InputStreamReader(stream, StandardCharsets.UTF_8)).getAsJsonObject();
            JsonObject ingredientJson = json.getAsJsonObject("ingredient");
            Ingredient ingredient = readIngredient(ingredientJson);
            ItemStack result = readResult(json.getAsJsonObject("result"));
            if (ingredient == null || result.isEmpty()) {
                AdAstraReborn.LOGGER.warn("Skipping invalid compressor recipe {}.", path);
                return null;
            }
            String id = path.substring(path.lastIndexOf('/') + 1, path.length() - ".json".length());
            return new Recipe(Reference.MOD_ID + ":" + id, ingredient, result, json.get("cookingtime").getAsInt(), json.get("energy").getAsInt());
        } catch (RuntimeException e) {
            AdAstraReborn.LOGGER.warn("Failed to load compressor recipe {}.", path, e);
            return null;
        } catch (java.io.IOException e) {
            AdAstraReborn.LOGGER.warn("Failed to close compressor recipe {}.", path, e);
            return null;
        }
    }

    private static Ingredient readIngredient(JsonObject json) {
        if (json.has("item")) {
            Item item = getItem(json.get("item").getAsString());
            return item == null ? null : new ItemIngredient(item);
        }
        if (json.has("ore")) {
            return new OreIngredient(json.get("ore").getAsString());
        }
        return null;
    }

    private static ItemStack readResult(JsonObject json) {
        Item item = getItem(json.get("id").getAsString());
        if (item == null) {
            return ItemStack.EMPTY;
        }
        int count = json.has("count") ? json.get("count").getAsInt() : 1;
        return new ItemStack(item, count);
    }

    private static Item getItem(String id) {
        Item item = Item.REGISTRY.getObject(new ResourceLocation(id));
        return item == Items.AIR ? null : item;
    }

    private static void registerFallbacks() {
        fallback("ad_astra:iron_plate_from_compressing_iron_ingot", new ItemIngredient(Items.IRON_INGOT), new ItemStack(ModItems.IRON_PLATE), 100);
        fallback("ad_astra:iron_plate_from_compressing_iron_block", new ItemIngredient(Item.getItemFromBlock(Blocks.IRON_BLOCK)), new ItemStack(ModItems.IRON_PLATE, 9), 800);
        fallback("ad_astra:steel_plate_from_compressing_steel_ingots", new ItemIngredient(ModItems.STEEL_INGOT), new ItemStack(ModItems.STEEL_PLATE), 100);
        fallback("ad_astra:steel_plate_from_compressing_steel_blocks", new ItemIngredient(Item.getItemFromBlock(ModBlocks.STEEL_BLOCK)), new ItemStack(ModItems.STEEL_PLATE, 9), 800);
        fallback("ad_astra:desh_plate_from_compressing_desh_ingots", new ItemIngredient(ModItems.DESH_INGOT), new ItemStack(ModItems.DESH_PLATE), 100);
        fallback("ad_astra:desh_plate_from_compressing_desh_blocks", new ItemIngredient(Item.getItemFromBlock(ModBlocks.DESH_BLOCK)), new ItemStack(ModItems.DESH_PLATE, 9), 800);
        fallback("ad_astra:ostrum_plate_from_compressing_ostrum_ingots", new ItemIngredient(ModItems.OSTRUM_INGOT), new ItemStack(ModItems.OSTRUM_PLATE), 100);
        fallback("ad_astra:ostrum_plate_from_compressing_ostrum_blocks", new ItemIngredient(Item.getItemFromBlock(ModBlocks.OSTRUM_BLOCK)), new ItemStack(ModItems.OSTRUM_PLATE, 9), 800);
        fallback("ad_astra:calorite_plate_from_compressing_calorite_ingots", new ItemIngredient(ModItems.CALORITE_INGOT), new ItemStack(ModItems.CALORITE_PLATE), 100);
        fallback("ad_astra:calorite_plate_from_compressing_calorite_blocks", new ItemIngredient(Item.getItemFromBlock(ModBlocks.CALORITE_BLOCK)), new ItemStack(ModItems.CALORITE_PLATE, 9), 800);
    }

    private static void fallback(String id, Ingredient ingredient, ItemStack result, int cookingTime) {
        if (getById(id) == null) {
            RECIPES.add(new Recipe(id, ingredient, result, cookingTime, 20));
        }
    }

    private interface Ingredient {
        boolean matches(ItemStack stack);
    }

    private static final class ItemIngredient implements Ingredient {
        private final Item item;

        private ItemIngredient(Item item) {
            this.item = item;
        }

        @Override
        public boolean matches(ItemStack stack) {
            return !stack.isEmpty() && stack.getItem() == item;
        }
    }

    private static final class OreIngredient implements Ingredient {
        private final String oreName;

        private OreIngredient(String oreName) {
            this.oreName = oreName;
        }

        @Override
        public boolean matches(ItemStack stack) {
            if (stack.isEmpty()) {
                return false;
            }
            for (int id : OreDictionary.getOreIDs(stack)) {
                if (oreName.equals(OreDictionary.getOreName(id))) {
                    return true;
                }
            }
            return false;
        }
    }

    public static final class Recipe {
        private final String id;
        private final Ingredient ingredient;
        private final ItemStack result;
        private final int cookingTime;
        private final int energyPerTick;

        private Recipe(String id, Ingredient ingredient, ItemStack result, int cookingTime, int energyPerTick) {
            this.id = id;
            this.ingredient = ingredient;
            this.result = result;
            this.cookingTime = cookingTime;
            this.energyPerTick = energyPerTick;
        }

        public String getId() {
            return id;
        }

        public ItemStack getResult() {
            return result;
        }

        public int getCookingTime() {
            return cookingTime;
        }

        public int getEnergyPerTick() {
            return energyPerTick;
        }

        private boolean matches(ItemStack stack) {
            return ingredient.matches(stack);
        }
    }
}
