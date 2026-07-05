package earth.terrarium.adastra.integration.jei;

import earth.terrarium.adastra.Reference;
import earth.terrarium.adastra.client.gui.AdAstraMachineGui;
import earth.terrarium.adastra.client.gui.machine.CompressorGui;
import earth.terrarium.adastra.client.gui.machine.CryoFreezerGui;
import earth.terrarium.adastra.client.gui.machine.EtrionicBlastFurnaceGui;
import earth.terrarium.adastra.common.container.AdAstraMachineContainer;
import earth.terrarium.adastra.common.recipe.RecipeRegistry;
import earth.terrarium.adastra.common.registry.ModBlocks;
import earth.terrarium.adastra.common.registry.ModFluids;
import earth.terrarium.adastra.common.registry.ModGuiIds;
import earth.terrarium.adastra.integration.jei.category.*;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.ingredients.IModIngredientRegistration;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import mezz.jei.api.recipe.transfer.IRecipeTransferInfo;
import mezz.jei.plugins.vanilla.ingredients.fluid.FluidStackListFactory;
import mezz.jei.plugins.vanilla.ingredients.fluid.FluidStackRenderer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
    public void registerIngredients(IModIngredientRegistration registration) {
        registration.register(
            VanillaTypes.FLUID,
            FluidStackListFactory.create(),
            new AdAstraFluidStackHelper(),
            new FluidStackRenderer());
    }

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
        hideForgeBucketVariants(registry);

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

        // Add recipe transfer handlers for GUI interaction.
        registry.getRecipeTransferRegistry().addRecipeTransferHandler(
            new MachineRecipeTransferInfo(COMPRESSOR_CATEGORY, ModGuiIds.COMPRESSOR, 1, 1));
        registry.getRecipeTransferRegistry().addRecipeTransferHandler(
            new MachineRecipeTransferInfo(BLAST_FURNACE_CATEGORY, ModGuiIds.ETRIONIC_BLAST_FURNACE, 1, 4));
        registry.getRecipeTransferRegistry().addRecipeTransferHandler(
            new MachineRecipeTransferInfo(CRYO_FREEZER_CATEGORY, ModGuiIds.CRYO_FREEZER, 1, 1));
        registry.getRecipeTransferRegistry().addRecipeTransferHandler(
            new MachineRecipeTransferInfo(FUEL_REFINERY_CATEGORY, ModGuiIds.FUEL_REFINERY, 1, 1));
        registry.getRecipeTransferRegistry().addRecipeTransferHandler(
            new MachineRecipeTransferInfo(OXYGEN_LOADER_CATEGORY, ModGuiIds.OXYGEN_LOADER, 1, 1));
        registry.getRecipeTransferRegistry().addRecipeTransferHandler(
            new MachineRecipeTransferInfo(NASA_WORKBENCH_CATEGORY, ModGuiIds.NASA_WORKBENCH, 0, 14));

        // Add click areas in GUIs to open recipe categories
        registry.addRecipeClickArea(CompressorGui.class,
            72, 59, 15, 16, COMPRESSOR_CATEGORY);
        registry.addRecipeClickArea(EtrionicBlastFurnaceGui.class,
            75, 50, 20, 12, BLAST_FURNACE_CATEGORY);
        registry.addRecipeClickArea(CryoFreezerGui.class,
            54, 71, 13, 13, CRYO_FREEZER_CATEGORY);
    }

    private void hideForgeBucketVariants(IModRegistry registry) {
        hideForgeBucketVariant(registry, ModFluids.OXYGEN);
        hideForgeBucketVariant(registry, ModFluids.HYDROGEN);
        hideForgeBucketVariant(registry, ModFluids.OIL);
        hideForgeBucketVariant(registry, ModFluids.FUEL);
        hideForgeBucketVariant(registry, ModFluids.CRYO_FUEL);
    }

    private void hideForgeBucketVariant(IModRegistry registry, Fluid fluid) {
        ItemStack stack = FluidUtil.getFilledBucket(new FluidStack(fluid, Fluid.BUCKET_VOLUME));
        if (!stack.isEmpty()) {
            registry.getJeiHelpers().getIngredientBlacklist().addIngredientToBlacklist(stack);
        }
    }

    private static class MachineRecipeTransferInfo implements IRecipeTransferInfo<AdAstraMachineContainer> {
        private final String categoryUid;
        private final int guiId;
        private final int recipeSlotStart;
        private final int recipeSlotCount;

        private MachineRecipeTransferInfo(String categoryUid, int guiId, int recipeSlotStart, int recipeSlotCount) {
            this.categoryUid = categoryUid;
            this.guiId = guiId;
            this.recipeSlotStart = recipeSlotStart;
            this.recipeSlotCount = recipeSlotCount;
        }

        @Override
        public Class<AdAstraMachineContainer> getContainerClass() {
            return AdAstraMachineContainer.class;
        }

        @Override
        public String getRecipeCategoryUid() {
            return categoryUid;
        }

        @Override
        public boolean canHandle(AdAstraMachineContainer container) {
            if (container == null || AdAstraMachineContainer.idFor(container.getMachine()) != guiId) {
                return false;
            }
            return recipeSlotStart >= 0
                && recipeSlotCount > 0
                && recipeSlotStart + recipeSlotCount <= container.getMachineSlotCount()
                && container.inventorySlots.size() > container.getMachineSlotCount();
        }

        @Override
        public List<Slot> getRecipeSlots(AdAstraMachineContainer container) {
            if (!canHandle(container)) {
                return Collections.emptyList();
            }
            List<Slot> slots = new ArrayList<>();
            for (int i = recipeSlotStart; i < recipeSlotStart + recipeSlotCount; i++) {
                slots.add(container.getSlot(i));
            }
            return slots;
        }

        @Override
        public List<Slot> getInventorySlots(AdAstraMachineContainer container) {
            if (!canHandle(container)) {
                return Collections.emptyList();
            }
            List<Slot> slots = new ArrayList<>();
            for (int i = container.getMachineSlotCount(); i < container.inventorySlots.size(); i++) {
                slots.add(container.getSlot(i));
            }
            return slots;
        }
    }
}
