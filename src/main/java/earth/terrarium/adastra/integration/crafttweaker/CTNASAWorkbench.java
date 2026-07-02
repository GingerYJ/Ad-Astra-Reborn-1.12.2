package earth.terrarium.adastra.integration.crafttweaker;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.IAction;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import earth.terrarium.adastra.common.recipe.NASAWorkbenchRecipe;
import earth.terrarium.adastra.common.recipe.RecipeRegistry;
import net.minecraft.item.ItemStack;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import java.util.ArrayList;
import java.util.List;

@ZenRegister
@ZenClass("mods.ad_astra.NASAWorkbench")
public final class CTNASAWorkbench {

    private CTNASAWorkbench() {
    }

    @ZenMethod("addRecipe")
    public static void addRecipe(String id, IItemStack[] inputs, IItemStack output, int width, int height, int time, int energy) {
        CraftTweakerAPI.apply(new AddRecipeAction(id, toStacks(inputs), toStack(output), width, height, time, energy));
    }

    @ZenMethod("removeRecipe")
    public static void removeRecipe(String id) {
        CraftTweakerAPI.apply(new RemoveRecipeAction(id));
    }

    @ZenMethod("removeByOutput")
    public static void removeByOutput(IItemStack output) {
        CraftTweakerAPI.apply(new RemoveByOutputAction(toStack(output)));
    }

    private static List<ItemStack> toStacks(IItemStack[] inputs) {
        List<ItemStack> stacks = new ArrayList<>();
        if (inputs == null) {
            return stacks;
        }
        for (IItemStack input : inputs) {
            stacks.add(toStack(input));
        }
        return stacks;
    }

    private static ItemStack toStack(IItemStack stack) {
        if (stack == null) {
            return ItemStack.EMPTY;
        }
        ItemStack itemStack = CraftTweakerMC.getItemStack(stack);
        return itemStack == null || itemStack.isEmpty() ? ItemStack.EMPTY : itemStack.copy();
    }

    private static boolean hasId(String id) {
        return id != null && !id.trim().isEmpty();
    }

    private static final class AddRecipeAction implements IAction {

        private final String id;
        private final List<ItemStack> inputs;
        private final ItemStack output;
        private final int width;
        private final int height;
        private final int time;
        private final int energy;

        private AddRecipeAction(String id, List<ItemStack> inputs, ItemStack output, int width, int height, int time, int energy) {
            this.id = id == null ? "" : id.trim();
            this.inputs = inputs;
            this.output = output;
            this.width = width;
            this.height = height;
            this.time = time;
            this.energy = energy;
        }

        @Override
        public void apply() {
            RecipeRegistry.replaceNASAWorkbenchRecipe(new NASAWorkbenchRecipe(
                id,
                inputs,
                output,
                Math.max(1, width),
                Math.max(1, height),
                Math.max(1, time),
                Math.max(0, energy)
            ));
        }

        @Override
        public String describe() {
            return "Adding Ad Astra NASA Workbench recipe " + id;
        }

        @Override
        public boolean validate() {
            return hasId(id) && output != null && !output.isEmpty() && inputs != null && !inputs.isEmpty();
        }

        @Override
        public String describeInvalid() {
            return "Cannot add Ad Astra NASA Workbench recipe " + id + ": missing id, inputs, or output";
        }
    }

    private static final class RemoveRecipeAction implements IAction {

        private final String id;

        private RemoveRecipeAction(String id) {
            this.id = id == null ? "" : id.trim();
        }

        @Override
        public void apply() {
            RecipeRegistry.removeNASAWorkbenchRecipe(id);
        }

        @Override
        public String describe() {
            return "Removing Ad Astra NASA Workbench recipe " + id;
        }

        @Override
        public boolean validate() {
            return hasId(id);
        }

        @Override
        public String describeInvalid() {
            return "Cannot remove Ad Astra NASA Workbench recipe: missing id";
        }
    }

    private static final class RemoveByOutputAction implements IAction {

        private final ItemStack output;

        private RemoveByOutputAction(ItemStack output) {
            this.output = output;
        }

        @Override
        public void apply() {
            RecipeRegistry.removeNASAWorkbenchRecipesByOutput(output);
        }

        @Override
        public String describe() {
            return "Removing Ad Astra NASA Workbench recipes for " + output.getDisplayName();
        }

        @Override
        public boolean validate() {
            return output != null && !output.isEmpty();
        }

        @Override
        public String describeInvalid() {
            return "Cannot remove Ad Astra NASA Workbench recipes by output: missing output";
        }
    }
}
