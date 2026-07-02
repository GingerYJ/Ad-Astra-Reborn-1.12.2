package earth.terrarium.adastra.common.registry;

import earth.terrarium.adastra.AdAstraReborn;
import earth.terrarium.adastra.Reference;
import earth.terrarium.adastra.common.world.PlanetDimensionProperties;
import earth.terrarium.adastra.common.world.WorldProviderGlacio;
import earth.terrarium.adastra.common.world.WorldProviderGlacioOrbit;
import earth.terrarium.adastra.common.world.WorldProviderEndOrbit;
import earth.terrarium.adastra.common.world.WorldProviderMars;
import earth.terrarium.adastra.common.world.WorldProviderMarsOrbit;
import earth.terrarium.adastra.common.world.WorldProviderMercury;
import earth.terrarium.adastra.common.world.WorldProviderMercuryOrbit;
import earth.terrarium.adastra.common.world.WorldProviderMoon;
import earth.terrarium.adastra.common.world.WorldProviderMoonOrbit;
import earth.terrarium.adastra.common.world.WorldProviderNetherOrbit;
import earth.terrarium.adastra.common.world.WorldProviderVenus;
import earth.terrarium.adastra.common.world.WorldProviderVenusOrbit;
import earth.terrarium.adastra.common.world.WorldProviderEarthOrbit;
import net.minecraft.block.Block;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
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
    public static final int EARTH_ORBIT_ID = 1210;
    public static final int MOON_ORBIT_ID = 1211;
    public static final int MARS_ORBIT_ID = 1212;
    public static final int MERCURY_ORBIT_ID = 1213;
    public static final int VENUS_ORBIT_ID = 1214;
    public static final int GLACIO_ORBIT_ID = 1215;
    public static final int NETHER_ORBIT_ID = 1216;
    public static final int END_ORBIT_ID = 1217;

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
        0.166F,  // Moon gravity: 1.622 m/s² ÷ 9.81 m/s² (Earth) = 0.166x
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
        0.38F,  // Mars gravity: 3.72 m/s² ÷ 9.81 m/s² = 0.38x
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
        0.38F,  // Mercury gravity: 3.7 m/s² ÷ 9.81 m/s² = 0.38x
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
        0.9F,  // Venus gravity: 8.87 m/s² ÷ 9.81 m/s² = 0.9x
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
        0.25F,  // Glacio gravity: ~2.5 m/s² ÷ 9.81 m/s² = 0.25x (fictional planet)
        14,
        4,
        DEFAULT_DAY_LENGTH,
        new Vec3d(0.58D, 0.72D, 0.9D),
        new Vec3d(0.34D, 0.48D, 0.68D));

    public static final PlanetDimensionProperties EARTH_ORBIT_PROPERTIES = orbit(
        "earth_orbit",
        EARTH_ORBIT_ID,
        "DIM_AD_ASTRA_EARTH_ORBIT",
        0,
        new Vec3d(0.0D, 0.0D, 0.0D),
        new Vec3d(0.02D, 0.02D, 0.04D));

    public static final PlanetDimensionProperties MOON_ORBIT_PROPERTIES = orbit(
        "moon_orbit",
        MOON_ORBIT_ID,
        "DIM_AD_ASTRA_MOON_ORBIT",
        1,
        MOON_PROPERTIES.getFogColor(),
        MOON_PROPERTIES.getSkyColor());

    public static final PlanetDimensionProperties MARS_ORBIT_PROPERTIES = orbit(
        "mars_orbit",
        MARS_ORBIT_ID,
        "DIM_AD_ASTRA_MARS_ORBIT",
        2,
        MARS_PROPERTIES.getFogColor(),
        MARS_PROPERTIES.getSkyColor());

    public static final PlanetDimensionProperties MERCURY_ORBIT_PROPERTIES = orbit(
        "mercury_orbit",
        MERCURY_ORBIT_ID,
        "DIM_AD_ASTRA_MERCURY_ORBIT",
        3,
        MERCURY_PROPERTIES.getFogColor(),
        MERCURY_PROPERTIES.getSkyColor());

    public static final PlanetDimensionProperties VENUS_ORBIT_PROPERTIES = orbit(
        "venus_orbit",
        VENUS_ORBIT_ID,
        "DIM_AD_ASTRA_VENUS_ORBIT",
        3,
        VENUS_PROPERTIES.getFogColor(),
        VENUS_PROPERTIES.getSkyColor());

    public static final PlanetDimensionProperties GLACIO_ORBIT_PROPERTIES = orbit(
        "glacio_orbit",
        GLACIO_ORBIT_ID,
        "DIM_AD_ASTRA_GLACIO_ORBIT",
        4,
        GLACIO_PROPERTIES.getFogColor(),
        GLACIO_PROPERTIES.getSkyColor());

    public static final PlanetDimensionProperties NETHER_ORBIT_PROPERTIES = orbit(
        "nether_orbit",
        NETHER_ORBIT_ID,
        "DIM_AD_ASTRA_NETHER_ORBIT",
        3,
        new Vec3d(0.35D, 0.02D, 0.01D),
        new Vec3d(0.16D, 0.01D, 0.01D));

    public static final PlanetDimensionProperties END_ORBIT_PROPERTIES = orbit(
        "the_end_orbit",
        END_ORBIT_ID,
        "DIM_AD_ASTRA_THE_END_ORBIT",
        4,
        new Vec3d(0.05D, 0.03D, 0.09D),
        new Vec3d(0.02D, 0.02D, 0.04D));

    public static DimensionType MOON;
    public static DimensionType MARS;
    public static DimensionType MERCURY;
    public static DimensionType VENUS;
    public static DimensionType GLACIO;
    public static DimensionType EARTH_ORBIT;
    public static DimensionType MOON_ORBIT;
    public static DimensionType MARS_ORBIT;
    public static DimensionType MERCURY_ORBIT;
    public static DimensionType VENUS_ORBIT;
    public static DimensionType GLACIO_ORBIT;
    public static DimensionType NETHER_ORBIT;
    public static DimensionType END_ORBIT;

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
        EARTH_ORBIT = register("earth_orbit", EARTH_ORBIT_PROPERTIES, WorldProviderEarthOrbit.class);
        MOON_ORBIT = register("moon_orbit", MOON_ORBIT_PROPERTIES, WorldProviderMoonOrbit.class);
        MARS_ORBIT = register("mars_orbit", MARS_ORBIT_PROPERTIES, WorldProviderMarsOrbit.class);
        MERCURY_ORBIT = register("mercury_orbit", MERCURY_ORBIT_PROPERTIES, WorldProviderMercuryOrbit.class);
        VENUS_ORBIT = register("venus_orbit", VENUS_ORBIT_PROPERTIES, WorldProviderVenusOrbit.class);
        GLACIO_ORBIT = register("glacio_orbit", GLACIO_ORBIT_PROPERTIES, WorldProviderGlacioOrbit.class);
        NETHER_ORBIT = register("nether_orbit", NETHER_ORBIT_PROPERTIES, WorldProviderNetherOrbit.class);
        END_ORBIT = register("the_end_orbit", END_ORBIT_PROPERTIES, WorldProviderEndOrbit.class);
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

    private static PlanetDimensionProperties orbit(
        String name,
        int dimensionId,
        String saveFolder,
        int tier,
        Vec3d fogColor,
        Vec3d skyColor) {
        return new PlanetDimensionProperties(
            name,
            dimensionId,
            saveFolder,
            Biomes.SKY,
            state(Blocks.AIR),
            state(Blocks.AIR),
            true,
            true,
            false,
            (short) -270,
            0.05F,
            64,
            tier,
            DEFAULT_DAY_LENGTH,
            fogColor,
            skyColor);
    }
}
