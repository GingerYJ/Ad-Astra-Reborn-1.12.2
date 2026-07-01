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

    public static final PlanetBiome LUNAR_WASTELANDS = biome(
        "lunar_wastelands",
        "Lunar Wastelands",
        ModBlocks.MOON_SAND.getDefaultState(),
        ModBlocks.MOON_STONE.getDefaultState(),
        0.7F,
        0.0F)
        .addMonsterSpawn(CorruptedLunarianEntity.class, 100, 1, 3)
        .addMonsterSpawn(StarCrawlerEntity.class, 100, 1, 3);

    public static final PlanetBiome MARTIAN_WASTELANDS = biome(
        "martian_wastelands",
        "Martian Wastelands",
        ModBlocks.MARS_SAND.getDefaultState(),
        ModBlocks.MARS_STONE.getDefaultState(),
        0.8F,
        0.0F)
        .addMonsterSpawn(MartianRaptorEntity.class, 100, 1, 3);

    public static final PlanetBiome MERCURY_DELTAS = biome(
        "mercury_deltas",
        "Mercury Deltas",
        ModBlocks.MERCURY_STONE.getDefaultState(),
        ModBlocks.MERCURY_STONE.getDefaultState(),
        1.6F,
        0.0F)
        .addMonsterSpawn(EntityMagmaCube.class, 100, 1, 3);

    public static final PlanetBiome VENUS_WASTELANDS = biome(
        "venus_wastelands",
        "Venus Wastelands",
        ModBlocks.VENUS_SAND.getDefaultState(),
        ModBlocks.VENUS_STONE.getDefaultState(),
        1.6F,
        1.0F)
        .addMonsterSpawn(MoglerEntity.class, 100, 1, 3)
        .addMonsterSpawn(SulfurCreeperEntity.class, 100, 1, 3)
        .addMonsterSpawn(ZombifiedPygroEntity.class, 100, 1, 2);

    public static final PlanetBiome GLACIO_SNOWY_BARRENS = biome(
        "glacio_snowy_barrens",
        "Glacio Snowy Barrens",
        ModBlocks.PERMAFROST.getDefaultState(),
        ModBlocks.GLACIO_STONE.getDefaultState(),
        -0.7F,
        1.0F)
        .addCreatureSpawn(GlacianRamEntity.class, 12, 2, 4);

    private ModBiomes() {
    }

    @SubscribeEvent
    public static void register(RegistryEvent.Register<Biome> event) {
        event.getRegistry().registerAll(
            LUNAR_WASTELANDS,
            MARTIAN_WASTELANDS,
            MERCURY_DELTAS,
            VENUS_WASTELANDS,
            GLACIO_SNOWY_BARRENS);
        registerBiomeTypes();
    }

    private static void registerBiomeTypes() {
        BiomeDictionary.addTypes(LUNAR_WASTELANDS, BiomeDictionary.Type.DRY, BiomeDictionary.Type.SPOOKY);
        BiomeDictionary.addTypes(MARTIAN_WASTELANDS, BiomeDictionary.Type.DRY, BiomeDictionary.Type.SANDY);
        BiomeDictionary.addTypes(MERCURY_DELTAS, BiomeDictionary.Type.DRY, BiomeDictionary.Type.HOT, BiomeDictionary.Type.WASTELAND);
        BiomeDictionary.addTypes(VENUS_WASTELANDS, BiomeDictionary.Type.HOT, BiomeDictionary.Type.WET, BiomeDictionary.Type.WASTELAND);
        BiomeDictionary.addTypes(GLACIO_SNOWY_BARRENS, BiomeDictionary.Type.COLD, BiomeDictionary.Type.SNOWY, BiomeDictionary.Type.WASTELAND);
    }

    private static PlanetBiome biome(
        String registryName,
        String displayName,
        IBlockState surface,
        IBlockState filler,
        float temperature,
        float rainfall) {
        PlanetBiome biome = new PlanetBiome(
            displayName,
            surface,
            filler,
            temperature,
            rainfall);
        biome.setRegistryName(new ResourceLocation(Reference.MOD_ID, registryName));
        return biome;
    }
}
