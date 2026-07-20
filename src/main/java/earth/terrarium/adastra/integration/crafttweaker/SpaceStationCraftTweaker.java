package earth.terrarium.adastra.integration.crafttweaker;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.IAction;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.oredict.IOreDictEntry;
import earth.terrarium.adastra.Reference;
import earth.terrarium.adastra.common.recipe.RecipeRegistry;
import earth.terrarium.adastra.common.recipe.SpaceStationRecipe;
import net.minecraft.item.ItemStack;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@ZenRegister
@ZenClass("mods.ad_astra.SpaceStation")
public final class SpaceStationCraftTweaker {

    private static final String DEFAULT_STRUCTURE = Reference.MOD_ID + ":space_station";

    private SpaceStationCraftTweaker() {
    }

    @ZenMethod
    public static void setRecipe(IIngredient[] ingredients, int[] counts) {
        CraftTweakerAPI.apply(new SetRecipeAction(ingredients, counts));
    }

    @ZenMethod
    public static void setRecipe(IIngredient[] ingredients) {
        setRecipe(ingredients, getIngredientAmounts(ingredients));
    }

    @ZenMethod
    public static void replaceRecipe(IIngredient[] ingredients, int[] counts) {
        setRecipe(ingredients, counts);
    }

    @ZenMethod
    public static void replaceRecipe(IIngredient[] ingredients) {
        setRecipe(ingredients);
    }

    @ZenMethod
    public static void addRecipe(IIngredient[] ingredients, int[] counts) {
        setRecipe(ingredients, counts);
    }

    @ZenMethod
    public static void addRecipe(IIngredient[] ingredients) {
        setRecipe(ingredients);
    }

    @ZenMethod
    public static void removeRecipe() {
        CraftTweakerAPI.apply(new RemoveRecipeAction());
    }

    @ZenMethod
    public static void removeRecipeById(String id) {
        CraftTweakerAPI.apply(new RemoveRecipeByIdAction(id));
    }

    private static int[] getIngredientAmounts(IIngredient[] ingredients) {
        if (ingredients == null) {
            return null;
        }
        int[] counts = new int[ingredients.length];
        for (int i = 0; i < ingredients.length; i++) {
            counts[i] = ingredients[i] == null ? 0 : ingredients[i].getAmount();
        }
        return counts;
    }

    private static List<SpaceStationRecipe.IngredientRequirement> buildRequirements(
        IIngredient[] ingredients, int[] counts) {
        if (ingredients == null || ingredients.length == 0) {
            throw new IllegalArgumentException("Space station recipe must have at least one ingredient");
        }
        if (counts == null || counts.length != ingredients.length) {
            throw new IllegalArgumentException("Ingredient and count arrays must have the same length");
        }
        List<SpaceStationRecipe.IngredientRequirement> requirements = new ArrayList<>();
        for (int i = 0; i < ingredients.length; i++) {
            requirements.add(toRequirement(ingredients[i], counts[i]));
        }
        return requirements;
    }

    private static SpaceStationRecipe.IngredientRequirement toRequirement(IIngredient ingredient, int count) {
        if (ingredient == null) {
            throw new IllegalArgumentException("Ingredient must not be null");
        }
        if (count <= 0) {
            throw new IllegalArgumentException("Ingredient count must be greater than 0");
        }
        IIngredient current = unwrapIngredient(ingredient);
        if (current instanceof IOreDictEntry) {
            return oreRequirement(((IOreDictEntry) current).getName(), count);
        }
        Object internal = current.getInternal();
        if (internal instanceof IIngredient && internal != current) {
            return toRequirement((IIngredient) internal, count);
        }
        if (internal instanceof String) {
            return oreRequirement((String) internal, count);
        }
        if (internal instanceof ItemStack) {
            return stackRequirement((ItemStack) internal, count);
        }
        if (current instanceof IItemStack) {
            return stackRequirement((IItemStack) current, count);
        }
        throw new IllegalArgumentException("Unsupported ingredient: " + current.toCommandString());
    }

    private static IIngredient unwrapIngredient(IIngredient ingredient) {
        IIngredient current = ingredient;
        Object internal = current.getInternal();
        while (internal instanceof IIngredient && internal != current) {
            current = (IIngredient) internal;
            internal = current.getInternal();
        }
        return current;
    }

    private static SpaceStationRecipe.IngredientRequirement oreRequirement(String oreDictName, int count) {
        if (oreDictName == null || oreDictName.trim().isEmpty()) {
            throw new IllegalArgumentException("Ore dictionary ingredient must have a name");
        }
        return new SpaceStationRecipe.IngredientRequirement(oreDictName, count);
    }

    private static SpaceStationRecipe.IngredientRequirement stackRequirement(IItemStack stack, int count) {
        Object internal = stack.getInternal();
        if (!(internal instanceof ItemStack)) {
            throw new IllegalArgumentException("Item ingredient is not a Minecraft ItemStack");
        }
        return stackRequirement((ItemStack) internal, count);
    }

    private static SpaceStationRecipe.IngredientRequirement stackRequirement(ItemStack stack, int count) {
        if (stack == null || stack.isEmpty()) {
            throw new IllegalArgumentException("Item ingredient must not be empty");
        }
        ItemStack copy = stack.copy();
        copy.setCount(1);
        return new SpaceStationRecipe.IngredientRequirement(copy, count);
    }

    private static final class SetRecipeAction implements IAction {
        private final IIngredient[] ingredients;
        private final int[] counts;
        private List<SpaceStationRecipe.IngredientRequirement> requirements;
        private String invalidReason;
        private boolean prepared;

        private SetRecipeAction(IIngredient[] ingredients, int[] counts) {
            this.ingredients = ingredients == null ? null : Arrays.copyOf(ingredients, ingredients.length);
            this.counts = counts == null ? null : Arrays.copyOf(counts, counts.length);
        }

        @Override
        public void apply() {
            if (!prepare()) {
                return;
            }
            SpaceStationRecipe existing = RecipeRegistry.findSpaceStationRecipe();
            String id = existing == null ? Reference.MOD_ID + ":space_station" : existing.getId();
            String structure = existing == null ? DEFAULT_STRUCTURE : existing.getStructure();
            RecipeRegistry.replaceSpaceStationRecipe(new SpaceStationRecipe(id, structure, requirements));
        }

        @Override
        public String describe() {
            return "Replacing the Ad Astra global space station recipe";
        }

        @Override
        public boolean validate() {
            return prepare();
        }

        @Override
        public String describeInvalid() {
            prepare();
            return invalidReason == null ? "Invalid Ad Astra space station recipe" : invalidReason;
        }

        private boolean prepare() {
            if (prepared) {
                return invalidReason == null;
            }
            prepared = true;
            try {
                requirements = buildRequirements(ingredients, counts);
            } catch (RuntimeException exception) {
                invalidReason = exception.getMessage();
            }
            return invalidReason == null;
        }
    }

    private static final class RemoveRecipeAction implements IAction {

        @Override
        public void apply() {
            RecipeRegistry.removeSpaceStationRecipe();
        }

        @Override
        public String describe() {
            return "Removing the Ad Astra global space station recipe";
        }

        @Override
        public boolean validate() {
            return true;
        }

        @Override
        public String describeInvalid() {
            return "Invalid Ad Astra global space station recipe";
        }
    }

    private static final class RemoveRecipeByIdAction implements IAction {
        private final String id;
        private String invalidReason;

        private RemoveRecipeByIdAction(String id) {
            this.id = id;
        }

        @Override
        public void apply() {
            RecipeRegistry.removeSpaceStationRecipeById(id);
        }

        @Override
        public String describe() {
            return "Removing Ad Astra space station recipe " + id;
        }

        @Override
        public boolean validate() {
            if (id == null || id.trim().isEmpty()) {
                invalidReason = "Space station recipe id must not be empty";
                return false;
            }
            return true;
        }

        @Override
        public String describeInvalid() {
            return invalidReason == null ? "Invalid Ad Astra space station recipe id" : invalidReason;
        }
    }
}
