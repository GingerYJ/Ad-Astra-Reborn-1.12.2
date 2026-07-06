package earth.terrarium.adastra.integration.jei;

import earth.terrarium.adastra.common.blocks.celestial.BarnardaC1Blocks;
import earth.terrarium.adastra.common.blocks.celestial.BarnardaCBlocks;
import earth.terrarium.adastra.common.blocks.celestial.CallistoBlocks;
import earth.terrarium.adastra.common.blocks.celestial.CeresBlocks;
import earth.terrarium.adastra.common.blocks.celestial.EnceladusBlocks;
import earth.terrarium.adastra.common.blocks.celestial.EuropaBlocks;
import earth.terrarium.adastra.common.blocks.celestial.GanymedeBlocks;
import earth.terrarium.adastra.common.blocks.celestial.HaumeaBlocks;
import earth.terrarium.adastra.common.blocks.celestial.IoBlocks;
import earth.terrarium.adastra.common.blocks.celestial.MirandaBlocks;
import earth.terrarium.adastra.common.blocks.celestial.PhobosBlocks;
import earth.terrarium.adastra.common.blocks.celestial.PlutoBlocks;
import earth.terrarium.adastra.common.blocks.celestial.ProximaBBlocks;
import earth.terrarium.adastra.common.blocks.celestial.TauCetiFBlocks;
import earth.terrarium.adastra.common.blocks.celestial.TitanBlocks;
import earth.terrarium.adastra.common.blocks.celestial.TritonBlocks;
import earth.terrarium.adastra.common.registry.ModBlocks;
import mezz.jei.api.IModRegistry;
import net.minecraft.block.Block;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.IStringSerializable;

/**
 * Adds lightweight JEI/HEI information pages for planet surface blocks and ores.
 * HEI exposes the JEI 1.12 API, so addIngredientInfo works for both viewers.
 */
public final class PlanetResourceJeiInfo {

    private PlanetResourceJeiInfo() {
    }

    public static void register(IModRegistry registry) {
        registerLegacyPlanetBlocks(registry);
        registerCelestial(registry, "ceres", ModBlocks.CERES_BLOCKS, CeresBlocks.EnumCeresBlocks.values());
        registerCelestial(registry, "pluto", ModBlocks.PLUTO_BLOCKS, PlutoBlocks.EnumPlutoBlocks.values());
        registerCelestial(registry, "haumea", ModBlocks.HAUMEA_BLOCKS, HaumeaBlocks.EnumHaumeaBlocks.values());
        registerCelestial(registry, "io", ModBlocks.IO_BLOCKS, IoBlocks.EnumIoBlocks.values());
        registerCelestial(registry, "europa", ModBlocks.EUROPA_BLOCKS, EuropaBlocks.EnumEuropaBlocks.values());
        registerCelestial(registry, "ganymede", ModBlocks.GANYMEDE_BLOCKS, GanymedeBlocks.EnumGanymedeBlocks.values());
        registerCelestial(registry, "callisto", ModBlocks.CALLISTO_BLOCKS, CallistoBlocks.EnumCallistoBlocks.values());
        registerCelestial(registry, "enceladus", ModBlocks.ENCELADUS_BLOCKS, EnceladusBlocks.EnumEnceladusBlocks.values());
        registerCelestial(registry, "titan", ModBlocks.TITAN_BLOCKS, TitanBlocks.EnumTitanBlocks.values());
        registerCelestial(registry, "miranda", ModBlocks.MIRANDA_BLOCKS, MirandaBlocks.EnumMirandaBlocks.values());
        registerCelestial(registry, "triton", ModBlocks.TRITON_BLOCKS, TritonBlocks.EnumTritonBlocks.values());
        registerCelestial(registry, "phobos", ModBlocks.PHOBOS_BLOCKS, PhobosBlocks.EnumPhobosBlocks.values());
        registerCelestial(registry, "barnarda_c", ModBlocks.BARNARDA_C_BLOCKS, BarnardaCBlocks.EnumBarnardaCBlocks.values());
        registerCelestial(registry, "barnarda_c1", ModBlocks.BARNARDA_C1_BLOCKS, BarnardaC1Blocks.EnumBarnardaC1Blocks.values());
        registerCelestial(registry, "tau_ceti_f", ModBlocks.TAUCETI_F_BLOCKS, TauCetiFBlocks.EnumTauCetiFBlocks.values());
        registerCelestial(registry, "proxima_b", ModBlocks.PROXIMA_B_BLOCKS, ProximaBBlocks.EnumProximaBBlocks.values());

        add(registry, "io", new ItemStack(ModBlocks.IO_GEYSER), true);
        add(registry, "europa", new ItemStack(ModBlocks.EUROPA_GEYSER), true);
        add(registry, "enceladus", new ItemStack(ModBlocks.ENCELADUS_CRYSTAL), true);
        registerPlanetaryExclusiveResources(registry);
    }

    private static void registerPlanetaryExclusiveResources(IModRegistry registry) {
        addPlanetaryExclusiveResource(registry, "mercury", "hermium");
        addPlanetaryExclusiveResource(registry, "glacio", "cryonite");
        addPlanetaryExclusiveResource(registry, "ceres", "cerium");
        addPlanetaryExclusiveResource(registry, "pluto", "plutonium");
        addPlanetaryExclusiveResource(registry, "haumea", "haumeite");
        addPlanetaryExclusiveResource(registry, "kuiper_belt", "kuiperite");
        addPlanetaryExclusiveResource(registry, "io", "ionite");
        addPlanetaryExclusiveResource(registry, "europa", "europium");
        addPlanetaryExclusiveResource(registry, "ganymede", "ganymedite");
        addPlanetaryExclusiveResource(registry, "callisto", "callistite");
        addPlanetaryExclusiveResource(registry, "enceladus", "enceladite");
        addPlanetaryExclusiveResource(registry, "titan", "titanite");
        addPlanetaryExclusiveResource(registry, "miranda", "mirandium");
        addPlanetaryExclusiveResource(registry, "triton", "tritonium");
        addPlanetaryExclusiveResource(registry, "phobos", "phobium");
        addPlanetaryExclusiveResource(registry, "barnarda_c", "barnardium");
        addPlanetaryExclusiveResource(registry, "barnarda_c1", "c1_barnardium");
        addPlanetaryExclusiveResource(registry, "tauceti_f", "taucetite");
        addPlanetaryExclusiveResource(registry, "proxima_b", "proximite");
    }

    private static void addPlanetaryExclusiveResource(IModRegistry registry, String planetKey, String resource) {
        ItemStack ore = blockStack(planetKey + "_" + resource + "_ore");
        ItemStack block = blockStack(resource + "_block");
        ItemStack raw = itemStack("raw_" + resource);
        ItemStack ingot = itemStack(resource + "_ingot");
        add(registry, planetKey, ore, true);
        add(registry, planetKey, raw, true);
        add(registry, planetKey, ingot, true);
        add(registry, planetKey, block, false);
    }

    private static ItemStack itemStack(String path) {
        net.minecraft.item.Item item = net.minecraft.item.Item.REGISTRY.getObject(new ResourceLocation("ad_astra", path));
        return item == null || item == Items.AIR ? ItemStack.EMPTY : new ItemStack(item);
    }

    private static ItemStack blockStack(String path) {
        Block block = Block.REGISTRY.getObject(new ResourceLocation("ad_astra", path));
        return block == null || block == net.minecraft.init.Blocks.AIR ? ItemStack.EMPTY : new ItemStack(block);
    }

    private static void registerLegacyPlanetBlocks(IModRegistry registry) {
        for (Block block : ModBlocks.BLOCKS) {
            if (block.getRegistryName() == null) {
                continue;
            }
            String path = block.getRegistryName().getPath();
            if (path.endsWith("_globe") || path.contains("flag")) {
                continue;
            }
            String planet = legacyPlanetKey(path);
            if (planet != null) {
                add(registry, planet, new ItemStack(block), isResourceOre(path));
            }
        }
    }

    private static String legacyPlanetKey(String path) {
        if (path.startsWith("moon_") || path.startsWith("deepslate_desh") || path.startsWith("deepslate_ice_shard")) {
            return "moon";
        }
        if (path.startsWith("mars_") || path.equals("conglomerate") || path.equals("polished_conglomerate") || path.startsWith("deepslate_ostrum")) {
            return "mars";
        }
        if (path.startsWith("mercury_")) {
            return "mercury";
        }
        if (path.startsWith("venus_") || path.startsWith("deepslate_calorite") || path.equals("infernal_spire_block")) {
            return "venus";
        }
        if (path.startsWith("glacio_") || path.startsWith("permafrost") || path.startsWith("cracked_permafrost")
            || path.startsWith("chiseled_permafrost") || path.startsWith("polished_permafrost") || path.startsWith("glacian_")
            || path.startsWith("aeronos_") || path.startsWith("strophar_")) {
            return "glacio";
        }
        return null;
    }

    private static <T extends Enum<T> & IStringSerializable> void registerCelestial(IModRegistry registry, String planetKey, Block block, T[] values) {
        for (T value : values) {
            int meta = ((Enum<?>) value).ordinal();
            if (value instanceof CeresBlocks.EnumCeresBlocks) {
                meta = ((CeresBlocks.EnumCeresBlocks) value).getMeta();
            } else if (value instanceof PlutoBlocks.EnumPlutoBlocks) {
                meta = ((PlutoBlocks.EnumPlutoBlocks) value).getMeta();
            } else if (value instanceof HaumeaBlocks.EnumHaumeaBlocks) {
                meta = ((HaumeaBlocks.EnumHaumeaBlocks) value).getMeta();
            } else if (value instanceof IoBlocks.EnumIoBlocks) {
                meta = ((IoBlocks.EnumIoBlocks) value).getMeta();
            } else if (value instanceof EuropaBlocks.EnumEuropaBlocks) {
                meta = ((EuropaBlocks.EnumEuropaBlocks) value).getMeta();
            } else if (value instanceof GanymedeBlocks.EnumGanymedeBlocks) {
                meta = ((GanymedeBlocks.EnumGanymedeBlocks) value).getMeta();
            } else if (value instanceof CallistoBlocks.EnumCallistoBlocks) {
                meta = ((CallistoBlocks.EnumCallistoBlocks) value).getMeta();
            } else if (value instanceof EnceladusBlocks.EnumEnceladusBlocks) {
                meta = ((EnceladusBlocks.EnumEnceladusBlocks) value).getMeta();
            } else if (value instanceof TitanBlocks.EnumTitanBlocks) {
                meta = ((TitanBlocks.EnumTitanBlocks) value).getMeta();
            } else if (value instanceof MirandaBlocks.EnumMirandaBlocks) {
                meta = ((MirandaBlocks.EnumMirandaBlocks) value).getMeta();
            } else if (value instanceof TritonBlocks.EnumTritonBlocks) {
                meta = ((TritonBlocks.EnumTritonBlocks) value).getMeta();
            } else if (value instanceof PhobosBlocks.EnumPhobosBlocks) {
                meta = ((PhobosBlocks.EnumPhobosBlocks) value).getMeta();
            } else if (value instanceof BarnardaCBlocks.EnumBarnardaCBlocks) {
                meta = ((BarnardaCBlocks.EnumBarnardaCBlocks) value).getMeta();
            } else if (value instanceof BarnardaC1Blocks.EnumBarnardaC1Blocks) {
                meta = ((BarnardaC1Blocks.EnumBarnardaC1Blocks) value).getMeta();
            } else if (value instanceof TauCetiFBlocks.EnumTauCetiFBlocks) {
                meta = ((TauCetiFBlocks.EnumTauCetiFBlocks) value).getMeta();
            } else if (value instanceof ProximaBBlocks.EnumProximaBBlocks) {
                meta = ((ProximaBBlocks.EnumProximaBBlocks) value).getMeta();
            }
            add(registry, planetKey, new ItemStack(block, 1, meta), isResourceOre(value.getName()));
        }
    }

    private static boolean isResourceOre(String path) {
        return path.contains("ore") || path.contains("geyser") || path.contains("crystal");
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
