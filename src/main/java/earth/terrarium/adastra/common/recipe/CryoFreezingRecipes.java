package earth.terrarium.adastra.common.recipe;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import earth.terrarium.adastra.AdAstraReborn;
import earth.terrarium.adastra.Reference;
import earth.terrarium.adastra.common.registry.ModFluids;
import earth.terrarium.adastra.common.registry.ModItems;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class CryoFreezingRecipes {

    private static final String[] GENERATED_RECIPE_PATHS = {
        "data/ad_astra/recipes/cryo_freezing/cryo_fuel_from_cryo_freezing_blue_ice.json",
        "data/ad_astra/recipes/cryo_freezing/cryo_fuel_from_cryo_freezing_ice.json",
        "data/ad_astra/recipes/cryo_freezing/cryo_fuel_from_cryo_freezing_ice_shard.json",
        "data/ad_astra/recipes/cryo_freezing/cryo_fuel_from_cryo_freezing_packed_ice.json"
    };

    private static final List<Recipe> RECIPES = new ArrayList<>();

    private CryoFreezingRecipes() {
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
        AdAstraReborn.LOGGER.info("Loaded {} cryo freezer recipes.", RECIPES.size());
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
        return getLegacyRecipe(id);
    }

    public static List<Recipe> all() {
        return Collections.unmodifiableList(RECIPES);
    }

    private static Recipe readGeneratedRecipe(String path) {
        try (InputStream stream = CryoFreezingRecipes.class.getClassLoader().getResourceAsStream(path)) {
            if (stream == null) {
                AdAstraReborn.LOGGER.warn("Missing generated cryo freezer recipe {}.", path);
                return null;
            }
            JsonObject json = new JsonParser().parse(new InputStreamReader(stream, StandardCharsets.UTF_8)).getAsJsonObject();
            Item ingredient = getItem(json.getAsJsonObject("ingredient").get("item").getAsString());
            JsonObject resultJson = json.getAsJsonObject("result");
            Fluid fluid = getFluid(resultJson.get("fluid").getAsString());
            if (ingredient == null || fluid == null) {
                AdAstraReborn.LOGGER.warn("Skipping cryo freezer recipe {} because an item or fluid is not available in 1.12.2.", path);
                return null;
            }
            String id = path.substring(path.lastIndexOf('/') + 1, path.length() - ".json".length());
            return new Recipe(
                Reference.MOD_ID + ":" + id,
                ingredient,
                fluid,
                resultJson.get("millibuckets").getAsInt(),
                json.get("cookingtime").getAsInt(),
                json.get("energy").getAsInt()
            );
        } catch (RuntimeException e) {
            AdAstraReborn.LOGGER.warn("Failed to load cryo freezer recipe {}.", path, e);
            return null;
        } catch (java.io.IOException e) {
            AdAstraReborn.LOGGER.warn("Failed to close cryo freezer recipe {}.", path, e);
            return null;
        }
    }

    private static Item getItem(String id) {
        Item item = Item.REGISTRY.getObject(new ResourceLocation(id));
        return item == Items.AIR ? null : item;
    }

    private static Fluid getFluid(String id) {
        Fluid fluid = FluidRegistry.getFluid(id);
        if (fluid != null) {
            return fluid;
        }
        ResourceLocation location = new ResourceLocation(id);
        return FluidRegistry.getFluid(location.getPath());
    }

    private static void registerFallbacks() {
        fallback("ad_astra:cryo_fuel_from_cryo_freezing_ice", Item.getItemFromBlock(Blocks.ICE), 1, 120);
        fallback("ad_astra:cryo_fuel_from_cryo_freezing_packed_ice", Item.getItemFromBlock(Blocks.PACKED_ICE), 2, 120);
        fallback("ad_astra:cryo_fuel_from_cryo_freezing_ice_shard", ModItems.ICE_SHARD, 25, 60);
    }

    private static void fallback(String id, Item input, int outputAmount, int cookingTime) {
        if (input != null && getById(id) == null) {
            RECIPES.add(new Recipe(id, input, ModFluids.CRYO_FUEL, outputAmount, cookingTime, 40));
        }
    }

    private static Recipe getLegacyRecipe(String id) {
        if ("ICE".equals(id)) {
            return getById("ad_astra:cryo_fuel_from_cryo_freezing_ice");
        }
        if ("PACKED_ICE".equals(id)) {
            return getById("ad_astra:cryo_fuel_from_cryo_freezing_packed_ice");
        }
        if ("ICE_SHARD".equals(id)) {
            return getById("ad_astra:cryo_fuel_from_cryo_freezing_ice_shard");
        }
        return null;
    }

    public static final class Recipe {
        private final String id;
        private final Item input;
        private final Fluid outputFluid;
        private final int outputAmount;
        private final int cookingTime;
        private final int energyPerTick;

        private Recipe(String id, Item input, Fluid outputFluid, int outputAmount, int cookingTime, int energyPerTick) {
            this.id = id;
            this.input = input;
            this.outputFluid = outputFluid;
            this.outputAmount = outputAmount;
            this.cookingTime = cookingTime;
            this.energyPerTick = energyPerTick;
        }

        public String getId() {
            return id;
        }

        public Fluid getOutputFluid() {
            return outputFluid;
        }

        public int getOutputAmount() {
            return outputAmount;
        }

        public int getCookingTime() {
            return cookingTime;
        }

        public int getEnergyPerTick() {
            return energyPerTick;
        }

        private boolean matches(ItemStack stack) {
            return !stack.isEmpty() && stack.getItem() == input;
        }
    }
}
