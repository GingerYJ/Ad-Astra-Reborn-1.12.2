package earth.terrarium.adastra.integration.jei;

import earth.terrarium.adastra.common.blocks.AdAstraOreBlock;
import earth.terrarium.adastra.common.registry.ModBlocks;
import earth.terrarium.adastra.common.world.custom.CustomPlanetDefinition;
import earth.terrarium.adastra.common.world.custom.CustomPlanetRegistry;
import mezz.jei.api.IModRegistry;
import net.minecraft.block.Block;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.block.state.IBlockState;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

/** Adds JEI source information for blocks, ore drops, and custom planet definitions. */
public final class PlanetResourceJeiInfo {

    private PlanetResourceJeiInfo() {
    }

    public static void register(IModRegistry registry) {
        Set<String> registered = new HashSet<>();
        for (Block block : ModBlocks.BLOCKS) {
            registerBlock(registry, registered, block);
        }
        // These vanilla blocks are placed by the custom planet generators.
        add(registry, registered, "moon", new ItemStack(Blocks.SOUL_SAND), false, null);
        add(registry, registered, "uranus", new ItemStack(Blocks.PACKED_ICE), false, null);
        add(registry, registered, "uranus", new ItemStack(ModBlocks.BLUE_SLUSHY_ICE), false, null);

        for (CustomPlanetDefinition definition : CustomPlanetRegistry.getDefinitions()) {
            String planet = definition.getPlanetName();
            String displayName = definition.getDisplayName();
            add(registry, registered, planet, stackForState(definition.getSurfaceBlock()), false, displayName);
            add(registry, registered, planet, stackForState(definition.getStoneBlock()), false, displayName);
            add(registry, registered, planet, stackForState(definition.getFillerBlock()), false, displayName);
            for (CustomPlanetDefinition.OreDefinition ore : definition.getOres()) {
                ItemStack oreStack = stackForState(ore.getOreBlock());
                add(registry, registered, planet, oreStack, true, displayName);
                addOreDrop(registry, registered, planet, oreStack, displayName);
            }
            for (CustomPlanetDefinition.FluidLakeDefinition lake : definition.getFluidLakes()) {
                add(registry, registered, planet, stackForState(lake.getFluidBlock()), false, displayName);
            }
        }
    }

    private static void registerBlock(IModRegistry registry, Set<String> registered, Block block) {
        if (block == null || block.getRegistryName() == null) {
            return;
        }
        String path = block.getRegistryName().getPath();
        if (path.startsWith("block_")) {
            path = path.substring("block_".length());
        }
        if (path.endsWith("_globe") || path.contains("flag") || path.startsWith("potted_")) {
            return;
        }

        for (String planet : planetKeys(path)) {
            ItemStack stack = new ItemStack(block);
            boolean ore = isResourceOre(path);
            add(registry, registered, planet, stack, ore, null);
            if (ore) {
                addOreDrop(registry, registered, planet, stack, null);
            }
        }
    }

    private static List<String> planetKeys(String path) {
        List<String> planets = new ArrayList<>();
        addPrefixPlanet(planets, path, "moon");
        if (path.startsWith("deepslate_desh") || path.startsWith("deepslate_ice_shard")) {
            addPlanet(planets, "moon");
        }

        addPrefixPlanet(planets, path, "mars");
        if (path.equals("conglomerate") || path.equals("polished_conglomerate")
            || path.startsWith("deepslate_ostrum")) {
            addPlanet(planets, "mars");
        }

        addPrefixPlanet(planets, path, "mercury");

        addPrefixPlanet(planets, path, "venus");
        if (path.startsWith("deepslate_calorite") || path.equals("infernal_spire_block")) {
            addPlanet(planets, "venus");
        }

        addPrefixPlanet(planets, path, "glacio");
        if (path.startsWith("permafrost") || path.startsWith("cracked_permafrost")
            || path.startsWith("chiseled_permafrost") || path.startsWith("polished_permafrost")
            || path.startsWith("glacian_")) {
            addPlanet(planets, "glacio");
        }

        for (String planet : ModBlocks.PLANETS) {
            addPrefixPlanet(planets, path, planet);
        }

        if (path.equals("moon_mycelium")) {
            addPlanet(planets, "moon");
        } else if (path.startsWith("aeronos_") || path.startsWith("strophar_")) {
            addPlanet(planets, "moon");
        } else if (path.startsWith("centaurian_")) {
            addPlanet(planets, "proxima_centauri_b");
        } else if (path.equals("icicle") || path.equals("slushy_ice")
            || path.equals("packed_slushy_ice") || path.equals("blue_slushy_ice")) {
            addPlanet(planets, "uranus");
        } else if (path.equals("saturn_ice")) {
            addPlanet(planets, "saturn");
        }

        // The base permafrost block also forms Uranus's custom ice geodes.
        if (path.equals("permafrost")) {
            addPlanet(planets, "uranus");
        }
        return planets;
    }

    private static void addPrefixPlanet(List<String> planets, String path, String planet) {
        if (path.startsWith(planet + "_")) {
            addPlanet(planets, planet);
        }
    }

    private static void addPlanet(List<String> planets, String planet) {
        if (!planets.contains(planet)) {
            planets.add(planet);
        }
    }

    private static boolean isResourceOre(String path) {
        return path.contains("ore");
    }

    private static ItemStack stackForState(IBlockState state) {
        if (state == null || state.getBlock() == null) {
            return ItemStack.EMPTY;
        }
        Block block = state.getBlock();
        return new ItemStack(block, 1, block.getMetaFromState(state));
    }

    private static void addOreDrop(IModRegistry registry, Set<String> registered, String planet,
                                   ItemStack oreStack, String displayName) {
        if (oreStack.isEmpty()) {
            return;
        }
        Block block = Block.getBlockFromItem(oreStack.getItem());
        if (block == null) {
            return;
        }

        Item droppedItem;
        int droppedMeta;
        if (block instanceof AdAstraOreBlock) {
            AdAstraOreBlock ore = (AdAstraOreBlock) block;
            droppedItem = ore.getDroppedItem() == null ? Item.getItemFromBlock(block) : ore.getDroppedItem();
            droppedMeta = ore.getDroppedMeta();
        } else {
            IBlockState state = block.getStateFromMeta(oreStack.getMetadata());
            droppedItem = block.getItemDropped(state, new Random(), 0);
            droppedMeta = block.damageDropped(state);
        }
        if (droppedItem != null && droppedItem != Items.AIR) {
            add(registry, registered, planet,
                new ItemStack(droppedItem, 1, droppedMeta), true, displayName);
        }
    }

    private static void add(IModRegistry registry, Set<String> registered, String planetKey,
                            ItemStack stack, boolean ore, String displayName) {
        if (stack.isEmpty() || stack.getItem() == Items.AIR) {
            return;
        }
        String itemKey = stack.getItem().getRegistryName() == null
            ? String.valueOf(stack.getItem())
            : stack.getItem().getRegistryName().toString();
        String registrationKey = planetKey + "|" + itemKey + "|" + stack.getMetadata();
        if (!registered.add(registrationKey)) {
            return;
        }
        String localizedPlanet = displayName == null || displayName.trim().isEmpty()
            ? localizedPlanetName(planetKey)
            : displayName;
        registry.addIngredientInfo(stack, ItemStack.class,
            I18n.format("jei.ad_astra.resource_source", localizedPlanet),
            I18n.format(ore ? "jei.ad_astra.resource_ore_hint" : "jei.ad_astra.resource_block_hint"),
            I18n.format("jei.ad_astra.resource_config_hint"));
    }

    private static String localizedPlanetName(String planetKey) {
        String jeiKey = "jei.ad_astra.planet." + planetKey;
        String value = I18n.format(jeiKey);
        if (!value.equals(jeiKey)) {
            return value;
        }

        String planetTranslationKey = "planet.ad_astra." + planetKey;
        value = I18n.format(planetTranslationKey);
        return value.equals(planetTranslationKey) ? planetKey : value;
    }
}
