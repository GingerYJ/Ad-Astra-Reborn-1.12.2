package earth.terrarium.adastra.common.registry;

import earth.terrarium.adastra.Reference;
import earth.terrarium.adastra.common.constants.PlanetConstants;
import earth.terrarium.adastra.common.world.custom.CustomPlanetDefinition;
import earth.terrarium.adastra.common.world.custom.CustomPlanetRegistry;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.biome.Biome;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Registers custom Ad Astra planets and their orbit dimensions. */
public final class ModPlanets {

    private static final int FIRST_DIMENSION_ID = 1400;
    private static final List<CustomPlanetDefinition> DEFINITIONS = new ArrayList<>();
    private static boolean registered;

    private ModPlanets() {
    }

    public static synchronized void register() {
        if (registered) {
            return;
        }
        DEFINITIONS.add(planet("ceres", "Ceres", ModBiomes.INTRIGUING_WASTELANDS, -113, 0.114F, 3,
            ModBlocks.getPlanetSurface("ceres"), ores("ceres_copper_ore", 16, 17, "ceres_iron_ore", 10, 11)));
        DEFINITIONS.add(planet("jupiter", "Jupiter", ModBiomes.COSMIC_GASLANDS, -126, 2.53F, 4,
            ModBlocks.getPlanetStone("jupiter"), ores("jupiter_juperium_ore", 8, 9, "jupiter_coal_ore", 20, 17,
                "jupiter_gold_ore", 4, 10, "jupiter_diamond_ore", 5, 9)));
        DEFINITIONS.add(planet("saturn", "Saturn", ModBiomes.CRONIAN_GASLANDS, -154, 1.065F, 5,
            ModBlocks.getPlanetSurface("saturn"), ores("saturn_saturlyte_ore", 8, 9, "saturn_coal_ore", 20, 17,
                "saturn_gold_ore", 4, 10, "saturn_diamond_ore", 5, 9)));
        DEFINITIONS.add(planet("uranus", "Uranus", ModBiomes.URANUS_ICE_PEAKS, -224, 0.886F, 6,
            ModBlocks.getPlanetStone("uranus"), ores("uranus_uranium_ore", 8, 9, "uranus_ice_shard_ore", 12, 11,
                "uranus_iron_ore", 10, 11, "uranus_lapis_ore", 5, 9, "uranus_diamond_ore", 6, 9)));
        DEFINITIONS.add(planet("neptune", "Neptune", ModBiomes.WINDY_WASTELANDS, -218, 1.138F, 7,
            ModBlocks.getPlanetStone("neptune"), ores("neptune_neptunium_ore", 8, 9, "neptune_ice_shard_ore", 11, 11,
                "neptune_iron_ore", 10, 11, "neptune_copper_ore", 10, 17, "neptune_coal_ore", 9, 17)));
        DEFINITIONS.add(planet("orcus", "Orcus", ModBiomes.ORCEAN_WASTELANDS, -240, 0.028F, 8,
            ModBlocks.getPlanetStone("orcus"), ores("orcus_radium_ore", 6, 9, "orcus_copper_ore", 16, 17,
                "orcus_iron_ore", 10, 11)));
        DEFINITIONS.add(planet("pluto", "Pluto", ModBiomes.PLUTONIAN_BARRENS, -229, 0.063F, 9,
            ModBlocks.getPlanetSurface("pluto"), ores("pluto_plutonium_ore", 6, 9, "pluto_ice_shard_ore", 11, 9,
                "pluto_gold_ore", 10, 10, "pluto_diamond_ore", 8, 9)));
        DEFINITIONS.add(planet("haumea", "Haumea", ModBiomes.HAUMEAN_WASTELANDS, -240, 0.045F, 8,
            ModBlocks.getPlanetStone("haumea"), ores("haumea_copper_ore", 16, 17, "haumea_iron_ore", 10, 11)));
        DEFINITIONS.add(planet("quaoar", "Quaoar", ModBiomes.QUAOARIAN_BARRENS, -220, 0.029F, 8,
            ModBlocks.getPlanetStone("quaoar"), ores("quaoar_copper_ore", 16, 17, "quaoar_iron_ore", 10, 11)));
        DEFINITIONS.add(planet("makemake", "Makemake", ModBiomes.MAKEMAKEAN_BARRENS, -240, 0.031F, 8,
            ModBlocks.getPlanetStone("makemake"), ores("makemake_copper_ore", 16, 17, "makemake_iron_ore", 10, 11)));
        DEFINITIONS.add(planet("gonggong", "Gonggong", ModBiomes.CRYOVOLCANIC_WASTELANDS, -250, 0.020F, 9,
            ModBlocks.getPlanetStone("gonggong"), ores("gonggong_copper_ore", 16, 17, "gonggong_iron_ore", 10, 11)));
        DEFINITIONS.add(planet("eris", "Eris", ModBiomes.ERIDIAN_DELTAS, -240, 0.028F, 9,
            ModBlocks.getPlanetStone("eris"), ores("eris_copper_ore", 16, 17, "eris_iron_ore", 10, 11)));
        DEFINITIONS.add(planet("sedna", "Sedna", ModBiomes.SEDNIAN_BARRENS, -240, 0.019F, 10,
            ModBlocks.getPlanetStone("sedna"), ores("sedna_electrolyte_ore", 5, 9, "sedna_copper_ore", 16, 17,
                "sedna_iron_ore", 10, 11)));
        DEFINITIONS.add(planet("proxima_centauri_b", "Proxima Centauri b", ModBiomes.CENTAURIAN_PLAINS, -39, 1.0F, 11,
            ModBlocks.getPlanetSurface("proxima_centauri_b"), ores("proxima_centauri_b_iron_ore", 10, 11,
                "proxima_centauri_b_redstone_ore", 8, 9, "proxima_centauri_b_emerald_ore", 6, 9,
                "proxima_centauri_b_diamond_ore", 5, 9)));

        for (CustomPlanetDefinition definition : DEFINITIONS) {
            CustomPlanetRegistry.register(definition);
        }
        registered = true;
    }

    public static List<CustomPlanetDefinition> getDefinitions() {
        return Collections.unmodifiableList(DEFINITIONS);
    }

    private static CustomPlanetDefinition planet(
        String name,
        String displayName,
        Biome biome,
        int temperature,
        float gravity,
        int tier,
        net.minecraft.block.Block surface,
        List<OreSpec> ores) {
        int index = ModBlocks.PLANETS.indexOf(name);
        int dimensionId = FIRST_DIMENSION_ID + index * 2;
        CustomPlanetDefinition.Builder builder = CustomPlanetDefinition.builder(
                ModResourceIds.planet(name), dimensionId)
            .planetName(name)
            .displayName(displayName)
            .orbitDimensionId(dimensionId + 1)
            .biome(biome.getRegistryName())
            .surfaceBlock(surface.getDefaultState())
            .stoneBlock(ModBlocks.getPlanetStone(name).getDefaultState())
            .iconStack(new ItemStack(Items.IRON_INGOT))
            .skyLight(true)
            .canRespawn(true)
            .environment("proxima_centauri_b".equals(name), (short) temperature, gravity, 11)
            .orbitSolarPower(13)
            .tier(tier)
            .solarSystem(PlanetConstants.SOLAR_SYSTEM)
            .dayLength(24000)
            .fogColor(0.12D, 0.16D, 0.24D)
            .skyColor(0.28D, 0.42D, 0.62D)
            .registerDimension(true);
        for (OreSpec ore : ores) {
            builder.addOre(
                ore.blockName,
                ModBlocks.getOre(ore.blockName) == null
                    ? ModBlocks.get(ore.blockName).getDefaultState()
                    : ModBlocks.getOre(ore.blockName).getDefaultState(),
                ModBlocks.getPlanetStone(name).getDefaultState(),
                ore.veinSize,
                ore.countPerChunk,
                4,
                60);
        }
        return builder.build();
    }

    private static List<OreSpec> ores(Object... values) {
        List<OreSpec> result = new ArrayList<>();
        for (int i = 0; i < values.length; i += 3) {
            result.add(new OreSpec((String) values[i], (Integer) values[i + 1], (Integer) values[i + 2]));
        }
        return result;
    }

    private static final class OreSpec {
        private final String blockName;
        private final int countPerChunk;
        private final int veinSize;

        private OreSpec(String blockName, int countPerChunk, int veinSize) {
            this.blockName = blockName;
            this.countPerChunk = countPerChunk;
            this.veinSize = veinSize;
        }
    }

}
