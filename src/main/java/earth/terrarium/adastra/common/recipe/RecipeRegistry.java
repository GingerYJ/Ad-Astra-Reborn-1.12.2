package earth.terrarium.adastra.common.recipe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

/**
 * Central registry for all Ad Astra machine recipes.
 * Provides registration and lookup methods for each recipe type.
 */
public final class RecipeRegistry {

    private static final List<CompressingRecipe> COMPRESSING_RECIPES = new ArrayList<>();
    private static final List<AlloySmeltingRecipe> ALLOYING_RECIPES = new ArrayList<>();
    private static final List<CryoFreezingRecipe> CRYO_FREEZING_RECIPES = new ArrayList<>();
    private static final List<OxygenLoadingRecipe> OXYGEN_LOADING_RECIPES = new ArrayList<>();
    private static final List<FuelRefiningRecipe> REFINING_RECIPES = new ArrayList<>();
    private static final List<NASAWorkbenchRecipe> NASA_WORKBENCH_RECIPES = new ArrayList<>();
    private static final Map<String, NASAWorkbenchRecipe> NASA_WORKBENCH_RECIPE_OVERRIDES = new HashMap<>();
    private static final Set<String> NASA_WORKBENCH_RECIPE_REMOVED_IDS = new HashSet<>();
    private static final List<ItemStack> NASA_WORKBENCH_RECIPE_REMOVED_OUTPUTS = new ArrayList<>();
    private static final List<SpaceStationRecipe> SPACE_STATION_RECIPES = new ArrayList<>();
    private static final Map<ResourceLocation, SpaceStationRecipe> SPACE_STATION_RECIPE_OVERRIDES = new HashMap<>();
    private static final Set<ResourceLocation> SPACE_STATION_RECIPE_REMOVED_DIMENSIONS = new HashSet<>();
    private static final Set<String> SPACE_STATION_RECIPE_REMOVED_IDS = new HashSet<>();

    private RecipeRegistry() {
        // Utility class
    }

    // ==================== Compressing Recipes ====================

    public static void registerCompressingRecipe(CompressingRecipe recipe) {
        COMPRESSING_RECIPES.add(recipe);
    }

    public static CompressingRecipe findCompressingRecipe(ItemStack input) {
        if (input.isEmpty()) {
            return null;
        }
        for (CompressingRecipe recipe : COMPRESSING_RECIPES) {
            if (recipe.matches(input)) {
                return recipe;
            }
        }
        return null;
    }

    public static List<CompressingRecipe> getAllCompressingRecipes() {
        return Collections.unmodifiableList(COMPRESSING_RECIPES);
    }

    public static void clearCompressingRecipes() {
        COMPRESSING_RECIPES.clear();
    }

    // ==================== Alloying Recipes ====================

    public static void registerAlloyingRecipe(AlloySmeltingRecipe recipe) {
        ALLOYING_RECIPES.add(recipe);
    }

    public static AlloySmeltingRecipe findAlloyingRecipe(ItemStack input1, ItemStack input2) {
        ItemStack[] inputs = {input1, input2};
        for (AlloySmeltingRecipe recipe : ALLOYING_RECIPES) {
            if (recipe.matches(inputs)) {
                return recipe;
            }
        }
        return null;
    }

    public static List<AlloySmeltingRecipe> getAllAlloyingRecipes() {
        return Collections.unmodifiableList(ALLOYING_RECIPES);
    }

    public static void clearAlloyingRecipes() {
        ALLOYING_RECIPES.clear();
    }

    // ==================== Cryo Freezing Recipes ====================

    public static void registerCryoFreezingRecipe(CryoFreezingRecipe recipe) {
        CRYO_FREEZING_RECIPES.add(recipe);
    }

    public static CryoFreezingRecipe findCryoFreezingRecipe(ItemStack input) {
        if (input.isEmpty()) {
            return null;
        }
        for (CryoFreezingRecipe recipe : CRYO_FREEZING_RECIPES) {
            if (recipe.matches(input)) {
                return recipe;
            }
        }
        return null;
    }

    public static List<CryoFreezingRecipe> getAllCryoFreezingRecipes() {
        return Collections.unmodifiableList(CRYO_FREEZING_RECIPES);
    }

    public static void clearCryoFreezingRecipes() {
        CRYO_FREEZING_RECIPES.clear();
    }

    // ==================== Oxygen Loading Recipes ====================

    public static void registerOxygenLoadingRecipe(OxygenLoadingRecipe recipe) {
        OXYGEN_LOADING_RECIPES.add(recipe);
    }

    public static OxygenLoadingRecipe findOxygenLoadingRecipe(ItemStack input, Fluid fluid) {
        if (input.isEmpty() || fluid == null) {
            return null;
        }
        for (OxygenLoadingRecipe recipe : OXYGEN_LOADING_RECIPES) {
            if (recipe.matches(input, fluid)) {
                return recipe;
            }
        }
        return null;
    }

    public static OxygenLoadingRecipe findOxygenLoadingRecipe(ItemStack input, FluidStack fluidStack) {
        if (fluidStack == null) {
            return null;
        }
        return findOxygenLoadingRecipe(input, fluidStack.getFluid());
    }

    public static List<OxygenLoadingRecipe> getAllOxygenLoadingRecipes() {
        return Collections.unmodifiableList(OXYGEN_LOADING_RECIPES);
    }

    public static void clearOxygenLoadingRecipes() {
        OXYGEN_LOADING_RECIPES.clear();
    }

    // ==================== Refining Recipes ====================

    public static void registerRefiningRecipe(FuelRefiningRecipe recipe) {
        REFINING_RECIPES.add(recipe);
    }

    public static FuelRefiningRecipe findRefiningRecipe(ItemStack input) {
        if (input.isEmpty()) {
            return null;
        }
        for (FuelRefiningRecipe recipe : REFINING_RECIPES) {
            if (recipe.matches(input)) {
                return recipe;
            }
        }
        return null;
    }

    public static List<FuelRefiningRecipe> getAllRefiningRecipes() {
        return Collections.unmodifiableList(REFINING_RECIPES);
    }

    public static void clearRefiningRecipes() {
        REFINING_RECIPES.clear();
    }

    // ==================== NASA Workbench Recipes ====================

    public static void registerNASAWorkbenchRecipe(NASAWorkbenchRecipe recipe) {
        if (recipe == null || isNASAWorkbenchRecipeRemoved(recipe)) {
            return;
        }
        NASAWorkbenchRecipe override = NASA_WORKBENCH_RECIPE_OVERRIDES.get(recipe.getId());
        addNASAWorkbenchRecipe(override != null ? override : recipe);
    }

    public static void replaceNASAWorkbenchRecipe(NASAWorkbenchRecipe recipe) {
        if (recipe == null) {
            return;
        }
        NASA_WORKBENCH_RECIPE_REMOVED_IDS.remove(recipe.getId());
        removeMatchingRemovedOutput(recipe.getResult());
        NASA_WORKBENCH_RECIPE_OVERRIDES.put(recipe.getId(), recipe);
        addNASAWorkbenchRecipe(recipe);
    }

    public static NASAWorkbenchRecipe findNASAWorkbenchRecipe(String id) {
        if (id == null || id.trim().isEmpty()) {
            return null;
        }
        NASAWorkbenchRecipe override = NASA_WORKBENCH_RECIPE_OVERRIDES.get(id);
        if (override != null && !isNASAWorkbenchRecipeRemoved(override)) {
            return override;
        }
        for (NASAWorkbenchRecipe recipe : getEffectiveNASAWorkbenchRecipes()) {
            if (id.equals(recipe.getId())) {
                return recipe;
            }
        }
        return null;
    }

    public static NASAWorkbenchRecipe findNASAWorkbenchRecipe(ItemStack[] inputs) {
        if (inputs == null || inputs.length < 14) {
            return null;
        }
        for (NASAWorkbenchRecipe recipe : getEffectiveNASAWorkbenchRecipes()) {
            if (recipe.matches(inputs)) {
                return recipe;
            }
        }
        return null;
    }

    public static boolean removeNASAWorkbenchRecipe(String id) {
        if (id == null || id.trim().isEmpty()) {
            return false;
        }
        NASA_WORKBENCH_RECIPE_REMOVED_IDS.add(id);
        NASA_WORKBENCH_RECIPE_OVERRIDES.remove(id);
        return removeNASAWorkbenchRecipeFromActive(id);
    }

    private static boolean removeNASAWorkbenchRecipeFromActive(String id) {
        boolean removed = false;
        Iterator<NASAWorkbenchRecipe> iterator = NASA_WORKBENCH_RECIPES.iterator();
        while (iterator.hasNext()) {
            NASAWorkbenchRecipe recipe = iterator.next();
            if (id.equals(recipe.getId())) {
                iterator.remove();
                removed = true;
            }
        }
        return removed;
    }

    public static int removeNASAWorkbenchRecipesByOutput(ItemStack output) {
        if (output == null || output.isEmpty()) {
            return 0;
        }
        ItemStack removedOutput = output.copy();
        removedOutput.setCount(1);
        NASA_WORKBENCH_RECIPE_REMOVED_OUTPUTS.add(removedOutput);
        removeNASAWorkbenchOverrideByOutput(output);
        int removed = 0;
        Iterator<NASAWorkbenchRecipe> iterator = NASA_WORKBENCH_RECIPES.iterator();
        while (iterator.hasNext()) {
            NASAWorkbenchRecipe recipe = iterator.next();
            if (recipe.matchesOutput(output)) {
                iterator.remove();
                removed++;
            }
        }
        return removed;
    }

    public static List<NASAWorkbenchRecipe> getAllNASAWorkbenchRecipes() {
        return Collections.unmodifiableList(getEffectiveNASAWorkbenchRecipes());
    }

    public static void clearNASAWorkbenchRecipes() {
        NASA_WORKBENCH_RECIPES.clear();
    }

    private static void addNASAWorkbenchRecipe(NASAWorkbenchRecipe recipe) {
        removeNASAWorkbenchRecipeFromActive(recipe.getId());
        NASA_WORKBENCH_RECIPES.add(recipe);
    }

    private static List<NASAWorkbenchRecipe> getEffectiveNASAWorkbenchRecipes() {
        List<NASAWorkbenchRecipe> recipes = new ArrayList<>(NASA_WORKBENCH_RECIPES);
        for (NASAWorkbenchRecipe recipe : NASA_WORKBENCH_RECIPE_OVERRIDES.values()) {
            if (!isNASAWorkbenchRecipeRemoved(recipe) && findNASAWorkbenchRecipeInList(recipes, recipe.getId()) == null) {
                recipes.add(recipe);
            }
        }
        return recipes;
    }

    private static NASAWorkbenchRecipe findNASAWorkbenchRecipeInList(List<NASAWorkbenchRecipe> recipes, String id) {
        for (NASAWorkbenchRecipe recipe : recipes) {
            if (id.equals(recipe.getId())) {
                return recipe;
            }
        }
        return null;
    }

    private static boolean isNASAWorkbenchRecipeRemoved(NASAWorkbenchRecipe recipe) {
        if (NASA_WORKBENCH_RECIPE_REMOVED_IDS.contains(recipe.getId())) {
            return true;
        }
        for (ItemStack output : NASA_WORKBENCH_RECIPE_REMOVED_OUTPUTS) {
            if (recipe.matchesOutput(output)) {
                return true;
            }
        }
        return false;
    }

    private static void removeNASAWorkbenchOverrideByOutput(ItemStack output) {
        Iterator<Map.Entry<String, NASAWorkbenchRecipe>> iterator = NASA_WORKBENCH_RECIPE_OVERRIDES.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, NASAWorkbenchRecipe> entry = iterator.next();
            if (entry.getValue().matchesOutput(output)) {
                iterator.remove();
            }
        }
    }

    private static void removeMatchingRemovedOutput(ItemStack output) {
        Iterator<ItemStack> iterator = NASA_WORKBENCH_RECIPE_REMOVED_OUTPUTS.iterator();
        while (iterator.hasNext()) {
            ItemStack removed = iterator.next();
            if (RecipeHelper.itemsMatchWithNBT(output, removed)) {
                iterator.remove();
            }
        }
    }

    // ==================== Space Station Recipes ====================

    public static void registerSpaceStationRecipe(SpaceStationRecipe recipe) {
        if (recipe == null || isSpaceStationRecipeRemoved(recipe)) {
            return;
        }
        SpaceStationRecipe override = SPACE_STATION_RECIPE_OVERRIDES.get(recipe.getDimension());
        addSpaceStationRecipe(override != null ? override : recipe);
    }

    public static SpaceStationRecipe findSpaceStationRecipe(ResourceLocation dimension) {
        if (dimension == null) {
            return null;
        }
        SpaceStationRecipe override = SPACE_STATION_RECIPE_OVERRIDES.get(dimension);
        if (override != null && !isSpaceStationRecipeRemoved(override)) {
            return override;
        }
        for (SpaceStationRecipe recipe : getEffectiveSpaceStationRecipes()) {
            if (dimension.equals(recipe.getDimension())) {
                return recipe;
            }
        }
        return null;
    }

    public static SpaceStationRecipe findSpaceStationRecipeById(String id) {
        if (id == null || id.trim().isEmpty()) {
            return null;
        }
        for (SpaceStationRecipe recipe : getEffectiveSpaceStationRecipes()) {
            if (id.equals(recipe.getId())) {
                return recipe;
            }
        }
        return null;
    }

    public static SpaceStationRecipe replaceSpaceStationRecipe(SpaceStationRecipe recipe) {
        if (recipe == null) {
            return null;
        }
        SPACE_STATION_RECIPE_REMOVED_DIMENSIONS.remove(recipe.getDimension());
        SPACE_STATION_RECIPE_REMOVED_IDS.remove(recipe.getId());
        SpaceStationRecipe previous = removeSpaceStationRecipeFromActive(recipe.getDimension());
        SpaceStationRecipe override = SPACE_STATION_RECIPE_OVERRIDES.put(recipe.getDimension(), recipe);
        SPACE_STATION_RECIPES.add(recipe);
        return previous != null ? previous : override;
    }

    public static SpaceStationRecipe removeSpaceStationRecipe(ResourceLocation dimension) {
        if (dimension == null) {
            return null;
        }
        SPACE_STATION_RECIPE_REMOVED_DIMENSIONS.add(dimension);
        SpaceStationRecipe override = SPACE_STATION_RECIPE_OVERRIDES.remove(dimension);
        SpaceStationRecipe removed = removeSpaceStationRecipeFromActive(dimension);
        return removed != null ? removed : override;
    }

    public static SpaceStationRecipe removeSpaceStationRecipeById(String id) {
        if (id == null || id.trim().isEmpty()) {
            return null;
        }
        SPACE_STATION_RECIPE_REMOVED_IDS.add(id);
        SpaceStationRecipe override = removeSpaceStationRecipeOverrideById(id);
        SpaceStationRecipe removed = removeSpaceStationRecipeByIdFromActive(id);
        return removed != null ? removed : override;
    }

    public static List<SpaceStationRecipe> getAllSpaceStationRecipes() {
        return Collections.unmodifiableList(getEffectiveSpaceStationRecipes());
    }

    public static void clearSpaceStationRecipes() {
        SPACE_STATION_RECIPES.clear();
    }

    private static void addSpaceStationRecipe(SpaceStationRecipe recipe) {
        removeSpaceStationRecipeFromActive(recipe.getDimension());
        SPACE_STATION_RECIPES.add(recipe);
    }

    private static List<SpaceStationRecipe> getEffectiveSpaceStationRecipes() {
        List<SpaceStationRecipe> recipes = new ArrayList<>(SPACE_STATION_RECIPES);
        for (SpaceStationRecipe recipe : SPACE_STATION_RECIPE_OVERRIDES.values()) {
            if (!isSpaceStationRecipeRemoved(recipe) && findSpaceStationRecipeInList(recipes, recipe.getDimension()) == null) {
                recipes.add(recipe);
            }
        }
        return recipes;
    }

    private static SpaceStationRecipe findSpaceStationRecipeInList(List<SpaceStationRecipe> recipes, ResourceLocation dimension) {
        for (SpaceStationRecipe recipe : recipes) {
            if (dimension.equals(recipe.getDimension())) {
                return recipe;
            }
        }
        return null;
    }

    private static boolean isSpaceStationRecipeRemoved(SpaceStationRecipe recipe) {
        return SPACE_STATION_RECIPE_REMOVED_DIMENSIONS.contains(recipe.getDimension())
            || SPACE_STATION_RECIPE_REMOVED_IDS.contains(recipe.getId());
    }

    private static SpaceStationRecipe removeSpaceStationRecipeFromActive(ResourceLocation dimension) {
        Iterator<SpaceStationRecipe> iterator = SPACE_STATION_RECIPES.iterator();
        while (iterator.hasNext()) {
            SpaceStationRecipe recipe = iterator.next();
            if (dimension.equals(recipe.getDimension())) {
                iterator.remove();
                return recipe;
            }
        }
        return null;
    }

    private static SpaceStationRecipe removeSpaceStationRecipeByIdFromActive(String id) {
        Iterator<SpaceStationRecipe> iterator = SPACE_STATION_RECIPES.iterator();
        while (iterator.hasNext()) {
            SpaceStationRecipe recipe = iterator.next();
            if (id.equals(recipe.getId())) {
                iterator.remove();
                return recipe;
            }
        }
        return null;
    }

    private static SpaceStationRecipe removeSpaceStationRecipeOverrideById(String id) {
        Iterator<Map.Entry<ResourceLocation, SpaceStationRecipe>> iterator = SPACE_STATION_RECIPE_OVERRIDES.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<ResourceLocation, SpaceStationRecipe> entry = iterator.next();
            if (id.equals(entry.getValue().getId())) {
                iterator.remove();
                return entry.getValue();
            }
        }
        return null;
    }

    // ==================== Global Operations ====================

    /**
     * Clears all registered recipes. Used for recipe reloading.
     */
    public static void clearAll() {
        clearCompressingRecipes();
        clearAlloyingRecipes();
        clearCryoFreezingRecipes();
        clearOxygenLoadingRecipes();
        clearRefiningRecipes();
        clearNASAWorkbenchRecipes();
        clearSpaceStationRecipes();
    }

    /**
     * Gets the total count of all registered recipes.
     */
    public static int getTotalRecipeCount() {
        return COMPRESSING_RECIPES.size()
            + ALLOYING_RECIPES.size()
            + CRYO_FREEZING_RECIPES.size()
            + OXYGEN_LOADING_RECIPES.size()
            + REFINING_RECIPES.size()
            + NASA_WORKBENCH_RECIPES.size()
            + SPACE_STATION_RECIPES.size();
    }
}
