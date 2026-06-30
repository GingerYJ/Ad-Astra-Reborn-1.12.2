package earth.terrarium.adastra.common.registry;

import earth.terrarium.adastra.AdAstraReborn;
import earth.terrarium.adastra.Reference;
import earth.terrarium.adastra.common.world.PlanetDimensionProperties;
import earth.terrarium.adastra.common.world.WorldProviderGlacio;
import earth.terrarium.adastra.common.world.WorldProviderMars;
import earth.terrarium.adastra.common.world.WorldProviderMercury;
import earth.terrarium.adastra.common.world.WorldProviderMoon;
import earth.terrarium.adastra.common.world.WorldProviderVenus;
import net.minecraft.block.Block;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldProvider;
import net.minecraftforge.common.DimensionManager;

public final class ModDimensions {

    public static final int MOON_ID = 1201;
    public static final int MARS_ID = 1202;
    public static final int MERCURY_ID = 1203;
    public static final int VENUS_ID = 1204;
    public static final int GLACIO_ID = 1205;

    private static final int DEFAULT_DAY_LENGTH = 24000;

    public static final PlanetDimensionProperties MOON_PROPERTIES = new PlanetDimensionProperties(
        "moon",
        MOON_ID,
        "DIM_AD_ASTRA_MOON",
        ModBiomes.LUNAR_WASTELANDS,
        state(ModBlocks.MOON_SAND),
        state(ModBlocks.MOON_STONE),
        true,
        true,
        false,
        (short) -173,
        1.622F,
        24,
        1,
        DEFAULT_DAY_LENGTH,
        new Vec3d(0.035D, 0.035D, 0.045D),
        new Vec3d(0.0D, 0.0D, 0.0D));

    public static final PlanetDimensionProperties MARS_PROPERTIES = new PlanetDimensionProperties(
        "mars",
        MARS_ID,
        "DIM_AD_ASTRA_MARS",
        ModBiomes.MARTIAN_WASTELANDS,
        state(ModBlocks.MARS_SAND),
        state(ModBlocks.MARS_STONE),
        true,
        true,
        false,
        (short) -65,
        3.72076F,
        12,
        2,
        DEFAULT_DAY_LENGTH,
        new Vec3d(0.72D, 0.33D, 0.2D),
        new Vec3d(0.38D, 0.12D, 0.08D));

    public static final PlanetDimensionProperties MERCURY_PROPERTIES = new PlanetDimensionProperties(
        "mercury",
        MERCURY_ID,
        "DIM_AD_ASTRA_MERCURY",
        ModBiomes.MERCURY_DELTAS,
        state(ModBlocks.MERCURY_STONE),
        state(ModBlocks.MERCURY_STONE),
        true,
        true,
        false,
        (short) 167,
        3.7F,
        64,
        3,
        DEFAULT_DAY_LENGTH,
        new Vec3d(0.45D, 0.39D, 0.32D),
        new Vec3d(0.25D, 0.22D, 0.2D));

    public static final PlanetDimensionProperties VENUS_PROPERTIES = new PlanetDimensionProperties(
        "venus",
        VENUS_ID,
        "DIM_AD_ASTRA_VENUS",
        ModBiomes.VENUS_WASTELANDS,
        state(ModBlocks.VENUS_SAND),
        state(ModBlocks.VENUS_STONE),
        true,
        true,
        false,
        (short) 464,
        8.87F,
        8,
        3,
        DEFAULT_DAY_LENGTH,
        new Vec3d(0.95D, 0.62D, 0.25D),
        new Vec3d(0.55D, 0.32D, 0.12D));

    public static final PlanetDimensionProperties GLACIO_PROPERTIES = new PlanetDimensionProperties(
        "glacio",
        GLACIO_ID,
        "DIM_AD_ASTRA_GLACIO",
        ModBiomes.GLACIO_SNOWY_BARRENS,
        state(ModBlocks.PERMAFROST),
        state(ModBlocks.GLACIO_STONE),
        true,
        true,
        true,
        (short) -20,
        3.721F,
        14,
        4,
        DEFAULT_DAY_LENGTH,
        new Vec3d(0.58D, 0.72D, 0.9D),
        new Vec3d(0.34D, 0.48D, 0.68D));

    public static DimensionType MOON;
    public static DimensionType MARS;
    public static DimensionType MERCURY;
    public static DimensionType VENUS;
    public static DimensionType GLACIO;

    private static boolean registered;

    private ModDimensions() {
    }

    public static void register() {
        if (registered) {
            return;
        }

        MOON = register("moon", MOON_PROPERTIES, WorldProviderMoon.class);
        MARS = register("mars", MARS_PROPERTIES, WorldProviderMars.class);
        MERCURY = register("mercury", MERCURY_PROPERTIES, WorldProviderMercury.class);
        VENUS = register("venus", VENUS_PROPERTIES, WorldProviderVenus.class);
        GLACIO = register("glacio", GLACIO_PROPERTIES, WorldProviderGlacio.class);
        registered = true;
    }

    private static DimensionType register(
        String name,
        PlanetDimensionProperties properties,
        Class<? extends WorldProvider> providerClass) {
        DimensionType type = DimensionType.register(
            Reference.MOD_ID + "_" + name,
            "_" + Reference.MOD_ID + "_" + name,
            properties.getDimensionId(),
            providerClass,
            false);

        if (!DimensionManager.isDimensionRegistered(properties.getDimensionId())) {
            DimensionManager.registerDimension(properties.getDimensionId(), type);
            AdAstraReborn.LOGGER.info(
                "Registered {} dimension {} with id {}.",
                Reference.MOD_NAME,
                name,
                properties.getDimensionId());
        }

        return type;
    }

    private static net.minecraft.block.state.IBlockState state(Block block) {
        return block.getDefaultState();
    }
}
