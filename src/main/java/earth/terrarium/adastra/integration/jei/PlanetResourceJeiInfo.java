package earth.terrarium.adastra.integration.jei;

import earth.terrarium.adastra.common.registry.ModBlocks;
import mezz.jei.api.IModRegistry;
import net.minecraft.block.Block;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

/** Adds JEI source information for the built-in planet blocks and ores. */
public final class PlanetResourceJeiInfo {

    private PlanetResourceJeiInfo() {
    }

    public static void register(IModRegistry registry) {
        for (Block block : ModBlocks.BLOCKS) {
            if (block.getRegistryName() == null) {
                continue;
            }
            String path = block.getRegistryName().getPath();
            if (path.endsWith("_globe") || path.contains("flag")) {
                continue;
            }
            String planet = planetKey(path);
            if (planet != null) {
                add(registry, planet, new ItemStack(block), isResourceOre(path));
            }
        }
    }

    private static String planetKey(String path) {
        if (path.startsWith("moon_") || path.startsWith("deepslate_desh") || path.startsWith("deepslate_ice_shard")) {
            return "moon";
        }
        if (path.startsWith("mars_") || path.equals("conglomerate") || path.equals("polished_conglomerate")
            || path.startsWith("deepslate_ostrum")) {
            return "mars";
        }
        if (path.startsWith("mercury_")) {
            return "mercury";
        }
        if (path.startsWith("venus_") || path.startsWith("deepslate_calorite") || path.equals("infernal_spire_block")) {
            return "venus";
        }
        if (path.startsWith("glacio_") || path.startsWith("permafrost") || path.startsWith("cracked_permafrost")
            || path.startsWith("chiseled_permafrost") || path.startsWith("polished_permafrost")
            || path.startsWith("glacian_") || path.startsWith("aeronos_") || path.startsWith("strophar_")) {
            return "glacio";
        }
        return null;
    }

    private static boolean isResourceOre(String path) {
        return path.contains("ore");
    }

    private static void add(IModRegistry registry, String planetKey, ItemStack stack, boolean ore) {
        if (stack.isEmpty() || stack.getItem() == Items.AIR) {
            return;
        }
        registry.addIngredientInfo(stack, ItemStack.class,
            I18n.format("jei.ad_astra.resource_source", I18n.format("jei.ad_astra.planet." + planetKey)),
            I18n.format(ore ? "jei.ad_astra.resource_ore_hint" : "jei.ad_astra.resource_block_hint"),
            I18n.format("jei.ad_astra.resource_config_hint"));
    }
}
