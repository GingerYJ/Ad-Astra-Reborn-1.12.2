package earth.terrarium.adastra.common.registry;

import earth.terrarium.adastra.Reference;
import earth.terrarium.adastra.common.entities.mob.CorruptedLunarianEntity;
import earth.terrarium.adastra.common.entities.mob.GlacianRamEntity;
import earth.terrarium.adastra.common.entities.mob.MartianRaptorEntity;
import earth.terrarium.adastra.common.entities.mob.MoglerEntity;
import earth.terrarium.adastra.common.entities.mob.StarCrawlerEntity;
import earth.terrarium.adastra.common.entities.mob.SulfurCreeperEntity;
import earth.terrarium.adastra.common.entities.mob.ZombifiedPygroEntity;
import earth.terrarium.adastra.common.world.PlanetBiome;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.monster.EntityMagmaCube;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID)
public final class ModBiomes {

    // Existing biomes
    public static final PlanetBiome LUNAR_WASTELANDS = biome(
        "lunar_wastelands", "Lunar Wastelands",
        ModBlocks.MOON_SAND.getDefaultState(), ModBlocks.MOON_STONE.getDefaultState(), 0.7F, 0.0F)
        .addMonsterSpawn(CorruptedLunarianEntity.class, 100, 1, 3)
        .addMonsterSpawn(StarCrawlerEntity.class, 100, 1, 3);

    public static final PlanetBiome MARTIAN_WASTELANDS = biome(
        "martian_wastelands", "Martian Wastelands",
        ModBlocks.MARS_SAND.getDefaultState(), ModBlocks.MARS_STONE.getDefaultState(), 0.8F, 0.0F)
        .addMonsterSpawn(MartianRaptorEntity.class, 100, 1, 3);

    public static final PlanetBiome MERCURY_DELTAS = biome(
        "mercury_deltas", "Mercury Deltas",
        ModBlocks.MERCURY_STONE.getDefaultState(), ModBlocks.MERCURY_STONE.getDefaultState(), 1.6F, 0.0F)
        .addMonsterSpawn(EntityMagmaCube.class, 100, 1, 3);

    public static final PlanetBiome VENUS_WASTELANDS = biome(
        "venus_wastelands", "Venus Wastelands",
        ModBlocks.VENUS_SAND.getDefaultState(), ModBlocks.VENUS_STONE.getDefaultState(), 1.6F, 1.0F)
        .addMonsterSpawn(MoglerEntity.class, 100, 1, 3)
        .addMonsterSpawn(SulfurCreeperEntity.class, 100, 1, 3)
        .addMonsterSpawn(ZombifiedPygroEntity.class, 100, 1, 2);

    public static final PlanetBiome GLACIO_SNOWY_BARRENS = biome(
        "glacio_snowy_barrens", "Glacio Snowy Barrens",
        ModBlocks.PERMAFROST.getDefaultState(), ModBlocks.GLACIO_STONE.getDefaultState(), -0.7F, 1.0F)
        .addCreatureSpawn(GlacianRamEntity.class, 12, 2, 4);

    // New celestial biomes - Solar System Dwarf Planets
    public static final PlanetBiome CERES_WASTELANDS = biome(
        "ceres_wastelands", "Ceres Wastelands",
        ModBlocks.CERES_BLOCKS.getDefaultState(), ModBlocks.CERES_BLOCKS.getDefaultState(), -0.7F, 0.0F);

    public static final PlanetBiome PLUTO_WASTELANDS = biome(
        "pluto_wastelands", "Pluto Wastelands",
        ModBlocks.PLUTO_BLOCKS.getDefaultState(), ModBlocks.PLUTO_BLOCKS.getDefaultState(), -1.5F, 0.0F);

    public static final PlanetBiome HAUMEA_WASTELANDS = biome(
        "haumea_wastelands", "Haumea Wastelands",
        ModBlocks.HAUMEA_BLOCKS.getDefaultState(), ModBlocks.HAUMEA_BLOCKS.getDefaultState(), -1.3F, 0.0F);

    public static final PlanetBiome KUIPER_BELT = biome(
        "kuiper_belt", "Kuiper Belt",
        net.minecraft.init.Blocks.STONE.getDefaultState(), net.minecraft.init.Blocks.STONE.getDefaultState(), -2.0F, 0.0F);

    // Jupiter moons
    public static final PlanetBiome IO_VOLCANIC = biome(
        "io_volcanic", "Io Volcanic Plains",
        ModBlocks.IO_BLOCKS.getDefaultState(), ModBlocks.IO_BLOCKS.getDefaultState(), 1.2F, 0.0F);

    public static final PlanetBiome IO_SULFUR = biome(
        "io_sulfur", "Io Sulfur Fields",
        ModBlocks.IO_BLOCKS.getDefaultState(), ModBlocks.IO_BLOCKS.getDefaultState(), 1.0F, 0.0F);

    public static final PlanetBiome IO_MAGMA = biome(
        "io_magma", "Io Magma Flows",
        ModBlocks.IO_BLOCKS.getDefaultState(), ModBlocks.IO_BLOCKS.getDefaultState(), 2.0F, 0.0F);

    public static final PlanetBiome EUROPA_ICE_PLAINS = biome(
        "europa_ice_plains", "Europa Ice Plains",
        ModBlocks.EUROPA_BLOCKS.getDefaultState(), ModBlocks.EUROPA_BLOCKS.getDefaultState(), -1.0F, 0.0F);

    public static final PlanetBiome EUROPA_OCEAN = biome(
        "europa_ocean", "Europa Subsurface Ocean",
        ModBlocks.EUROPA_BLOCKS.getDefaultState(), ModBlocks.EUROPA_BLOCKS.getDefaultState(), -0.8F, 0.5F);

    public static final PlanetBiome EUROPA_MOUNTAINS = biome(
        "europa_mountains", "Europa Mountains",
        ModBlocks.EUROPA_BLOCKS.getDefaultState(), ModBlocks.EUROPA_BLOCKS.getDefaultState(), -1.2F, 0.0F);

    public static final PlanetBiome EUROPA_GEYSER = biome(
        "europa_geyser", "Europa Geyser Field",
        ModBlocks.EUROPA_BLOCKS.getDefaultState(), ModBlocks.EUROPA_BLOCKS.getDefaultState(), -0.9F, 0.0F);

    public static final PlanetBiome GANYMEDE_WASTELANDS = biome(
        "ganymede_wastelands", "Ganymede Wastelands",
        ModBlocks.GANYMEDE_BLOCKS.getDefaultState(), ModBlocks.GANYMEDE_BLOCKS.getDefaultState(), -1.1F, 0.0F);

    public static final PlanetBiome CALLISTO_WASTELANDS = biome(
        "callisto_wastelands", "Callisto Wastelands",
        ModBlocks.CALLISTO_BLOCKS.getDefaultState(), ModBlocks.CALLISTO_BLOCKS.getDefaultState(), -0.9F, 0.0F);

    // Saturn moons
    public static final PlanetBiome ENCELADUS_MOUNTAINS = biome(
        "enceladus_mountains", "Enceladus Mountains",
        ModBlocks.ENCELADUS_BLOCKS.getDefaultState(), ModBlocks.ENCELADUS_BLOCKS.getDefaultState(), -1.2F, 0.0F);

    public static final PlanetBiome ENCELADUS_PLAINS = biome(
        "enceladus_plains", "Enceladus Plains",
        ModBlocks.ENCELADUS_BLOCKS.getDefaultState(), ModBlocks.ENCELADUS_BLOCKS.getDefaultState(), -1.0F, 0.0F);

    public static final PlanetBiome ENCELADUS_RAVINE = biome(
        "enceladus_ravine", "Enceladus Ravine",
        ModBlocks.ENCELADUS_BLOCKS.getDefaultState(), ModBlocks.ENCELADUS_BLOCKS.getDefaultState(), -0.8F, 0.0F);

    public static final PlanetBiome TITAN_WASTELANDS = biome(
        "titan_wastelands", "Titan Wastelands",
        ModBlocks.TITAN_BLOCKS.getDefaultState(), ModBlocks.TITAN_BLOCKS.getDefaultState(), -1.0F, 0.3F);

    // Other moons
    public static final PlanetBiome MIRANDA_WASTELANDS = biome(
        "miranda_wastelands", "Miranda Wastelands",
        ModBlocks.MIRANDA_BLOCKS.getDefaultState(), ModBlocks.MIRANDA_BLOCKS.getDefaultState(), -1.3F, 0.0F);

    public static final PlanetBiome TRITON_MOUNTAINS = biome(
        "triton_mountains", "Triton Mountains",
        ModBlocks.TRITON_BLOCKS.getDefaultState(), ModBlocks.TRITON_BLOCKS.getDefaultState(), -2.0F, 0.0F);

    public static final PlanetBiome TRITON_PLAINS = biome(
        "triton_plains", "Triton Plains",
        ModBlocks.TRITON_BLOCKS.getDefaultState(), ModBlocks.TRITON_BLOCKS.getDefaultState(), -1.8F, 0.0F);

    public static final PlanetBiome PHOBOS_WASTELANDS = biome(
        "phobos_wastelands", "Phobos Wastelands",
        ModBlocks.PHOBOS_BLOCKS.getDefaultState(), ModBlocks.PHOBOS_BLOCKS.getDefaultState(), -0.2F, 0.0F);

    // Exoplanet systems
    public static final PlanetBiome BARNARDA_C_PLAINS = biome(
        "barnarda_c_plains", "Barnarda C Plains",
        ModBlocks.BARNARDA_C_BLOCKS.getDefaultState(), ModBlocks.BARNARDA_C_BLOCKS.getDefaultState(), 0.6F, 0.3F);

    public static final PlanetBiome BARNARDA_C_FOREST = biome(
        "barnarda_c_forest", "Barnarda C Forest",
        ModBlocks.BARNARDA_C_BLOCKS.getDefaultState(), ModBlocks.BARNARDA_C_BLOCKS.getDefaultState(), 0.5F, 0.5F);

    public static final PlanetBiome BARNARDA_C_DESERT = biome(
        "barnarda_c_desert", "Barnarda C Desert",
        ModBlocks.BARNARDA_C_BLOCKS.getDefaultState(), ModBlocks.BARNARDA_C_BLOCKS.getDefaultState(), 1.0F, 0.0F);

    public static final PlanetBiome BARNARDA_C1_WASTELANDS = biome(
        "barnarda_c1_wastelands", "Barnarda C1 Wastelands",
        ModBlocks.BARNARDA_C1_BLOCKS.getDefaultState(), ModBlocks.BARNARDA_C1_BLOCKS.getDefaultState(), -0.5F, 0.0F);

    public static final PlanetBiome TAUCETI_F_WASTELANDS = biome(
        "tauceti_f_wastelands", "Tau Ceti F Wastelands",
        ModBlocks.TAUCETI_F_BLOCKS.getDefaultState(), ModBlocks.TAUCETI_F_BLOCKS.getDefaultState(), 0.3F, 0.2F);

    public static final PlanetBiome PROXIMA_B_WASTELANDS = biome(
        "proxima_b_wastelands", "Proxima B Wastelands",
        ModBlocks.PROXIMA_B_BLOCKS.getDefaultState(), ModBlocks.PROXIMA_B_BLOCKS.getDefaultState(), 0.5F, 0.2F);

    private ModBiomes() {
    }

    @SubscribeEvent
    public static void register(RegistryEvent.Register<Biome> event) {
        event.getRegistry().registerAll(
            LUNAR_WASTELANDS, MARTIAN_WASTELANDS, MERCURY_DELTAS,
            VENUS_WASTELANDS, GLACIO_SNOWY_BARRENS,
            CERES_WASTELANDS, PLUTO_WASTELANDS, HAUMEA_WASTELANDS, KUIPER_BELT,
            IO_VOLCANIC, IO_SULFUR, IO_MAGMA,
            EUROPA_ICE_PLAINS, EUROPA_OCEAN, EUROPA_MOUNTAINS, EUROPA_GEYSER,
            GANYMEDE_WASTELANDS, CALLISTO_WASTELANDS,
            ENCELADUS_MOUNTAINS, ENCELADUS_PLAINS, ENCELADUS_RAVINE,
            TITAN_WASTELANDS, MIRANDA_WASTELANDS,
            TRITON_MOUNTAINS, TRITON_PLAINS, PHOBOS_WASTELANDS,
            BARNARDA_C_PLAINS, BARNARDA_C_FOREST, BARNARDA_C_DESERT,
            BARNARDA_C1_WASTELANDS, TAUCETI_F_WASTELANDS, PROXIMA_B_WASTELANDS);
        registerBiomeTypes();
    }

    private static void registerBiomeTypes() {
        BiomeDictionary.addTypes(LUNAR_WASTELANDS, BiomeDictionary.Type.DRY, BiomeDictionary.Type.SPOOKY);
        BiomeDictionary.addTypes(MARTIAN_WASTELANDS, BiomeDictionary.Type.DRY, BiomeDictionary.Type.SANDY);
        BiomeDictionary.addTypes(MERCURY_DELTAS, BiomeDictionary.Type.DRY, BiomeDictionary.Type.HOT, BiomeDictionary.Type.WASTELAND);
        BiomeDictionary.addTypes(VENUS_WASTELANDS, BiomeDictionary.Type.HOT, BiomeDictionary.Type.WET, BiomeDictionary.Type.WASTELAND);
        BiomeDictionary.addTypes(GLACIO_SNOWY_BARRENS, BiomeDictionary.Type.COLD, BiomeDictionary.Type.SNOWY, BiomeDictionary.Type.WASTELAND);
        // New biomes
        BiomeDictionary.addTypes(CERES_WASTELANDS, BiomeDictionary.Type.COLD, BiomeDictionary.Type.DRY, BiomeDictionary.Type.WASTELAND);
        BiomeDictionary.addTypes(PLUTO_WASTELANDS, BiomeDictionary.Type.COLD, BiomeDictionary.Type.DRY, BiomeDictionary.Type.WASTELAND);
        BiomeDictionary.addTypes(HAUMEA_WASTELANDS, BiomeDictionary.Type.COLD, BiomeDictionary.Type.DRY, BiomeDictionary.Type.WASTELAND);
        BiomeDictionary.addTypes(KUIPER_BELT, BiomeDictionary.Type.COLD, BiomeDictionary.Type.DRY, BiomeDictionary.Type.WASTELAND);
        BiomeDictionary.addTypes(IO_VOLCANIC, BiomeDictionary.Type.HOT, BiomeDictionary.Type.DRY, BiomeDictionary.Type.WASTELAND);
        BiomeDictionary.addTypes(IO_SULFUR, BiomeDictionary.Type.HOT, BiomeDictionary.Type.DRY);
        BiomeDictionary.addTypes(IO_MAGMA, BiomeDictionary.Type.HOT, BiomeDictionary.Type.WASTELAND);
        BiomeDictionary.addTypes(EUROPA_ICE_PLAINS, BiomeDictionary.Type.COLD, BiomeDictionary.Type.SNOWY);
        BiomeDictionary.addTypes(EUROPA_OCEAN, BiomeDictionary.Type.COLD, BiomeDictionary.Type.WET, BiomeDictionary.Type.OCEAN);
        BiomeDictionary.addTypes(EUROPA_MOUNTAINS, BiomeDictionary.Type.COLD, BiomeDictionary.Type.MOUNTAIN);
        BiomeDictionary.addTypes(EUROPA_GEYSER, BiomeDictionary.Type.COLD, BiomeDictionary.Type.WET);
        BiomeDictionary.addTypes(GANYMEDE_WASTELANDS, BiomeDictionary.Type.COLD, BiomeDictionary.Type.DRY, BiomeDictionary.Type.WASTELAND);
        BiomeDictionary.addTypes(CALLISTO_WASTELANDS, BiomeDictionary.Type.COLD, BiomeDictionary.Type.DRY, BiomeDictionary.Type.WASTELAND);
        BiomeDictionary.addTypes(ENCELADUS_MOUNTAINS, BiomeDictionary.Type.COLD, BiomeDictionary.Type.MOUNTAIN);
        BiomeDictionary.addTypes(ENCELADUS_PLAINS, BiomeDictionary.Type.COLD, BiomeDictionary.Type.PLAINS);
        BiomeDictionary.addTypes(ENCELADUS_RAVINE, BiomeDictionary.Type.COLD, BiomeDictionary.Type.DRY);
        BiomeDictionary.addTypes(TITAN_WASTELANDS, BiomeDictionary.Type.COLD, BiomeDictionary.Type.WET, BiomeDictionary.Type.WASTELAND);
        BiomeDictionary.addTypes(MIRANDA_WASTELANDS, BiomeDictionary.Type.COLD, BiomeDictionary.Type.DRY, BiomeDictionary.Type.WASTELAND);
        BiomeDictionary.addTypes(TRITON_MOUNTAINS, BiomeDictionary.Type.COLD, BiomeDictionary.Type.MOUNTAIN);
        BiomeDictionary.addTypes(TRITON_PLAINS, BiomeDictionary.Type.COLD, BiomeDictionary.Type.PLAINS);
        BiomeDictionary.addTypes(PHOBOS_WASTELANDS, BiomeDictionary.Type.DRY, BiomeDictionary.Type.WASTELAND);
        BiomeDictionary.addTypes(BARNARDA_C_PLAINS, BiomeDictionary.Type.PLAINS, BiomeDictionary.Type.LUSH);
        BiomeDictionary.addTypes(BARNARDA_C_FOREST, BiomeDictionary.Type.FOREST, BiomeDictionary.Type.LUSH);
        BiomeDictionary.addTypes(BARNARDA_C_DESERT, BiomeDictionary.Type.HOT, BiomeDictionary.Type.DRY, BiomeDictionary.Type.SANDY);
        BiomeDictionary.addTypes(BARNARDA_C1_WASTELANDS, BiomeDictionary.Type.COLD, BiomeDictionary.Type.DRY, BiomeDictionary.Type.WASTELAND);
        BiomeDictionary.addTypes(TAUCETI_F_WASTELANDS, BiomeDictionary.Type.WET, BiomeDictionary.Type.OCEAN);
        BiomeDictionary.addTypes(PROXIMA_B_WASTELANDS, BiomeDictionary.Type.PLAINS, BiomeDictionary.Type.LUSH);
    }

    private static PlanetBiome biome(
        String registryName, String displayName,
        IBlockState surface, IBlockState filler,
        float temperature, float rainfall) {
        PlanetBiome biome = new PlanetBiome(displayName, surface, filler, temperature, rainfall);
        biome.setRegistryName(new ResourceLocation(Reference.MOD_ID, registryName));
        return biome;
    }
}
