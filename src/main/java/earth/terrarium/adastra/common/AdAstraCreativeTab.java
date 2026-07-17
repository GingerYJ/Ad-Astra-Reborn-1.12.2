package earth.terrarium.adastra.common;

import earth.terrarium.adastra.Reference;
import earth.terrarium.adastra.common.registry.ModItems;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemDoor;
import net.minecraft.item.ItemSlab;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;

import java.util.Collections;
import java.util.Comparator;

public final class AdAstraCreativeTab extends CreativeTabs {

    public static final AdAstraCreativeTab INSTANCE = new AdAstraCreativeTab();

    private AdAstraCreativeTab() {
        super(Reference.MOD_ID);
    }

    @Override
    public ItemStack createIcon() {
        if (ModItems.STEEL_INGOT != null) {
            return new ItemStack(ModItems.STEEL_INGOT);
        }
        return ItemStack.EMPTY;
    }

    public ItemStack getIconItemStack() {
        return createIcon();
    }

    public Item getTabIconItem() {
        return ModItems.STEEL_INGOT;
    }

    @Override
    public void displayAllRelevantItems(NonNullList<ItemStack> itemList) {
        super.displayAllRelevantItems(itemList);
        Collections.sort(itemList, Comparator
            .comparingInt((ItemStack stack) -> categoryOf(stack.getItem()))
            .thenComparingInt(stack -> rocketTierOf(stack.getItem()))
            .thenComparing(stack -> registryPathOf(stack.getItem())));
    }

    private static int categoryOf(Item item) {
        String path = registryPathOf(item);
        if (item instanceof ItemBlock || item instanceof ItemDoor || item instanceof ItemSlab) {
            return blockCategoryOf(path);
        }
        if (path.startsWith("raw_")) {
            return 20;
        }
        if (path.endsWith("_ingot")) {
            return 21;
        }
        if (path.endsWith("_nugget")) {
            return 22;
        }
        if (path.endsWith("_plate")) {
            return 23;
        }
        if (path.endsWith("_rod")) {
            return 24;
        }
        if (path.endsWith("_engine")) {
            return 25;
        }
        if (path.endsWith("_tank")) {
            return 26;
        }
        if (path.endsWith("_bucket")) {
            return 27;
        }
        if (path.endsWith("_helmet") || path.endsWith("_suit")
            || path.endsWith("_pants") || path.endsWith("_boots")) {
            return 28;
        }
        if (path.endsWith("_rocket") || path.endsWith("_rover")) {
            return 29;
        }
        if (path.endsWith("_spawn_egg")) {
            return 30;
        }
        if (path.equals("cheese")) {
            return 31;
        }
        if (path.equals("wrench") || path.equals("zip_gun") || path.equals("ti_69")) {
            return 32;
        }
        if (path.endsWith("_core") || path.endsWith("_cell") || path.endsWith("_gear")
            || path.endsWith("_wheel") || path.endsWith("_fan") || path.endsWith("_frame")
            || path.endsWith("_fin") || path.endsWith("_cone")) {
            return 33;
        }
        return 34;
    }

    private static int blockCategoryOf(String path) {
        if (path.endsWith("_ore")) {
            return 0;
        }
        if (path.endsWith("_sapling") || path.endsWith("_leaves") || path.endsWith("_log")
            || path.endsWith("_wood") || path.endsWith("_mushroom") || path.endsWith("_mycelium")) {
            return 1;
        }
        if (path.endsWith("_stairs")) {
            return 2;
        }
        if (path.endsWith("_slab")) {
            return 3;
        }
        if (path.endsWith("_wall")) {
            return 4;
        }
        if (path.endsWith("_fence") || path.endsWith("_fence_gate")) {
            return 5;
        }
        if (path.endsWith("_door") || path.endsWith("_trapdoor") || path.endsWith("_sliding_door")) {
            return 6;
        }
        if (path.endsWith("_button") || path.endsWith("_pressure_plate")) {
            return 7;
        }
        if (path.endsWith("_globe")) {
            return 8;
        }
        if (path.endsWith("_flag")) {
            return 9;
        }
        if (path.endsWith("_lamp")) {
            return 10;
        }
        if (path.endsWith("_pipe") || path.endsWith("_duct") || path.endsWith("_cable")) {
            return 11;
        }
        if (path.endsWith("_generator") || path.endsWith("_compressor") || path.endsWith("_furnace")
            || path.endsWith("_workbench") || path.endsWith("_refinery") || path.endsWith("_loader")
            || path.endsWith("_freezer") || path.endsWith("_energizer") || path.endsWith("_sensor")
            || path.endsWith("_distributor") || path.endsWith("_normalizer") || path.endsWith("_pump")
            || path.equals("radio") || path.equals("launch_pad")) {
            return 12;
        }
        return 13;
    }

    private static String registryPathOf(Item item) {
        ResourceLocation registryName = item.getRegistryName();
        return registryName == null ? "" : registryName.getPath();
    }

    private static int rocketTierOf(Item item) {
        String path = registryPathOf(item);
        if (!path.endsWith("_rocket")) {
            return Integer.MAX_VALUE;
        }

        int separator = path.indexOf('_');
        int end = path.indexOf('_', separator + 1);
        if (separator < 0 || end < 0) {
            return Integer.MAX_VALUE;
        }

        try {
            return Integer.parseInt(path.substring(separator + 1, end));
        } catch (NumberFormatException ignored) {
            return Integer.MAX_VALUE;
        }
    }
}
