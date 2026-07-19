package earth.terrarium.adastra.common.registry;

import earth.terrarium.adastra.Reference;
import earth.terrarium.adastra.common.entities.mob.CorruptedLunarianEntity;
import earth.terrarium.adastra.common.entities.mob.FreezeEntity;
import earth.terrarium.adastra.common.entities.mob.GlacianRamEntity;
import earth.terrarium.adastra.common.entities.mob.MartianRaptorEntity;
import earth.terrarium.adastra.common.entities.mob.MoglerEntity;
import earth.terrarium.adastra.common.entities.mob.StarCrawlerEntity;
import earth.terrarium.adastra.common.entities.mob.SulfurCreeperEntity;
import earth.terrarium.adastra.common.entities.mob.ZombifiedPygroEntity;
import earth.terrarium.adastra.common.world.PlanetBiome;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.monster.EntityMagmaCube;
import net.minecraft.entity.passive.EntitySheep;
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

    public static final PlanetBiome INTRIGUING_WASTELANDS = planetBiome(
        "intriguing_wastelands", "Intriguing Wastelands", "ceres", 0.6F, 0.0F);
    public static final PlanetBiome COSMIC_GASLANDS = planetBiome(
        "cosmic_gaslands", "Cosmic Gaslands", "jupiter", 0.6F, 0.0F);
    public static final PlanetBiome CRONIAN_GASLANDS = planetBiome(
        "cronian_gaslands", "Cronian Gaslands", "saturn", 0.2F, 0.0F);
    public static final PlanetBiome URANUS_ICE_PEAKS = planetBiome(
        "uranus_ice_peaks", "Uranus Ice Peaks", "uranus", -0.7F, 0.0F);
    public static final PlanetBiome WINDY_WASTELANDS = planetBiome(
        "windy_wastelands", "Windy Wastelands", "neptune", -0.4F, 0.0F);
    public static final PlanetBiome ORCEAN_WASTELANDS = planetBiome(
        "orcean_wastelands", "Orcean Wastelands", "orcus", 0.6F, 0.0F);
    public static final PlanetBiome PLUTONIAN_BARRENS = planetBiome(
        "plutonian_barrens", "Plutonian Barrens", "pluto", -0.5F, 0.0F);
    public static final PlanetBiome HAUMEAN_WASTELANDS = planetBiome(
        "haumean_wastelands", "Haumean Wastelands", "haumea", 0.6F, 0.0F);
    public static final PlanetBiome QUAOARIAN_BARRENS = planetBiome(
        "quaoarian_barrens", "Quaoarian Barrens", "quaoar", 0.6F, 0.0F);
    public static final PlanetBiome MAKEMAKEAN_BARRENS = planetBiome(
        "makemakean_barrens", "Makemakean Barrens", "makemake", 0.6F, 0.0F);
    public static final PlanetBiome CRYOVOLCANIC_WASTELANDS = planetBiome(
        "cryovolcanic_wastelands", "Cryovolcanic Wastelands", "gonggong", 0.4F, 0.0F);
    public static final PlanetBiome ERIDIAN_DELTAS = planetBiome(
        "eridian_deltas", "Eridian Deltas", "eris", -0.6F, 0.0F);
    public static final PlanetBiome SEDNIAN_BARRENS = planetBiome(
        "sednian_barrens", "Sednian Barrens", "sedna", 0.2F, 0.0F);
    public static final PlanetBiome CENTAURIAN_PLAINS = planetBiome(
        "centaurian_plains", "Centaurian Plains", "proxima_centauri_b", 0.4F, 0.2F);
    public static final PlanetBiome CENTAURIAN_BEACH = planetBiome(
        "centaurian_beach", "Centaurian Beach", "proxima_centauri_b", 0.4F, 0.0F);

    static {
        URANUS_ICE_PEAKS.addMonsterSpawn(FreezeEntity.class, 100, 1, 3);
        CENTAURIAN_PLAINS.addCreatureSpawn(EntitySheep.class, 12, 2, 4);
    }

    private ModBiomes() {
    }

    @SubscribeEvent
    public static void register(RegistryEvent.Register<Biome> event) {
        event.getRegistry().registerAll(
            LUNAR_WASTELANDS, MARTIAN_WASTELANDS, MERCURY_DELTAS,
            VENUS_WASTELANDS, GLACIO_SNOWY_BARRENS,
            INTRIGUING_WASTELANDS, COSMIC_GASLANDS, CRONIAN_GASLANDS,
            URANUS_ICE_PEAKS, WINDY_WASTELANDS, ORCEAN_WASTELANDS,
            PLUTONIAN_BARRENS, HAUMEAN_WASTELANDS, QUAOARIAN_BARRENS,
            MAKEMAKEAN_BARRENS, CRYOVOLCANIC_WASTELANDS, ERIDIAN_DELTAS,
            SEDNIAN_BARRENS, CENTAURIAN_PLAINS, CENTAURIAN_BEACH);
        registerBiomeTypes();
    }

    private static void registerBiomeTypes() {
        BiomeDictionary.addTypes(LUNAR_WASTELANDS, BiomeDictionary.Type.DRY, BiomeDictionary.Type.SPOOKY);
        BiomeDictionary.addTypes(MARTIAN_WASTELANDS, BiomeDictionary.Type.DRY, BiomeDictionary.Type.SANDY);
        BiomeDictionary.addTypes(MERCURY_DELTAS, BiomeDictionary.Type.DRY, BiomeDictionary.Type.HOT, BiomeDictionary.Type.WASTELAND);
        BiomeDictionary.addTypes(VENUS_WASTELANDS, BiomeDictionary.Type.HOT, BiomeDictionary.Type.WET, BiomeDictionary.Type.WASTELAND);
        BiomeDictionary.addTypes(GLACIO_SNOWY_BARRENS, BiomeDictionary.Type.COLD, BiomeDictionary.Type.SNOWY, BiomeDictionary.Type.WASTELAND);
        BiomeDictionary.addTypes(INTRIGUING_WASTELANDS, BiomeDictionary.Type.DRY, BiomeDictionary.Type.WASTELAND);
        BiomeDictionary.addTypes(COSMIC_GASLANDS, BiomeDictionary.Type.HOT, BiomeDictionary.Type.WASTELAND);
        BiomeDictionary.addTypes(CRONIAN_GASLANDS, BiomeDictionary.Type.COLD, BiomeDictionary.Type.WASTELAND);
        BiomeDictionary.addTypes(URANUS_ICE_PEAKS, BiomeDictionary.Type.COLD, BiomeDictionary.Type.SNOWY);
        BiomeDictionary.addTypes(WINDY_WASTELANDS, BiomeDictionary.Type.COLD, BiomeDictionary.Type.WASTELAND);
        BiomeDictionary.addTypes(ORCEAN_WASTELANDS, BiomeDictionary.Type.DRY, BiomeDictionary.Type.WASTELAND);
        BiomeDictionary.addTypes(PLUTONIAN_BARRENS, BiomeDictionary.Type.COLD, BiomeDictionary.Type.WASTELAND);
        BiomeDictionary.addTypes(HAUMEAN_WASTELANDS, BiomeDictionary.Type.DRY, BiomeDictionary.Type.WASTELAND);
        BiomeDictionary.addTypes(QUAOARIAN_BARRENS, BiomeDictionary.Type.DRY, BiomeDictionary.Type.WASTELAND);
        BiomeDictionary.addTypes(MAKEMAKEAN_BARRENS, BiomeDictionary.Type.DRY, BiomeDictionary.Type.WASTELAND);
        BiomeDictionary.addTypes(CRYOVOLCANIC_WASTELANDS, BiomeDictionary.Type.COLD, BiomeDictionary.Type.WASTELAND);
        BiomeDictionary.addTypes(ERIDIAN_DELTAS, BiomeDictionary.Type.COLD, BiomeDictionary.Type.WASTELAND);
        BiomeDictionary.addTypes(SEDNIAN_BARRENS, BiomeDictionary.Type.COLD, BiomeDictionary.Type.WASTELAND);
        BiomeDictionary.addTypes(CENTAURIAN_PLAINS, BiomeDictionary.Type.PLAINS);
        BiomeDictionary.addTypes(CENTAURIAN_BEACH, BiomeDictionary.Type.BEACH);
    }

    private static PlanetBiome biome(
        String registryName, String displayName,
        IBlockState surface, IBlockState filler,
        float temperature, float rainfall) {
        PlanetBiome biome = new PlanetBiome(displayName, surface, filler, temperature, rainfall);
        biome.setRegistryName(new ResourceLocation(Reference.MOD_ID, registryName));
        return biome;
    }

    private static PlanetBiome planetBiome(String registryName, String displayName, String planet,
                                             float temperature, float rainfall) {
        PlanetBiome biome = new PlanetBiome(displayName,
            ModBlocks.getPlanetSurface(planet).getDefaultState(),
            ModBlocks.getPlanetStone(planet).getDefaultState(),
            temperature, rainfall);
        biome.setRegistryName(ModResourceIds.biome(registryName));
        return biome;
    }
}
