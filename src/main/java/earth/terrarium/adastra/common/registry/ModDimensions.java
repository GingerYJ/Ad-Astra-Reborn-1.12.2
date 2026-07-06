package earth.terrarium.adastra.common.registry;

import earth.terrarium.adastra.AdAstraReborn;
import earth.terrarium.adastra.Reference;
import earth.terrarium.adastra.common.world.PlanetDimensionProperties;
import earth.terrarium.adastra.common.world.*;
import net.minecraft.block.Block;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldProvider;
import net.minecraftforge.common.DimensionManager;

public final class ModDimensions {

    // ===== Existing dimensions =====
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

    // ===== New solar system dwarf planets =====
    public static final int CERES_ID = 1220;
    public static final int CERES_ORBIT_ID = 1221;
    public static final int PLUTO_ID = 1222;
    public static final int PLUTO_ORBIT_ID = 1223;
    public static final int HAUMEA_ID = 1224;
    public static final int HAUMEA_ORBIT_ID = 1225;
    public static final int KUIPER_BELT_ID = 1226;

    // ===== Jupiter moons =====
    public static final int IO_ID = 1230;
    public static final int IO_ORBIT_ID = 1231;
    public static final int EUROPA_ID = 1232;
    public static final int EUROPA_ORBIT_ID = 1233;
    public static final int GANYMEDE_ID = 1234;
    public static final int GANYMEDE_ORBIT_ID = 1235;
    public static final int CALLISTO_ID = 1236;
    public static final int CALLISTO_ORBIT_ID = 1237;

    // ===== Saturn moons =====
    public static final int ENCELADUS_ID = 1240;
    public static final int ENCELADUS_ORBIT_ID = 1241;
    public static final int TITAN_ID = 1242;
    public static final int TITAN_ORBIT_ID = 1243;

    // ===== Other moons =====
    public static final int MIRANDA_ID = 1250;
    public static final int MIRANDA_ORBIT_ID = 1251;
    public static final int TRITON_ID = 1252;
    public static final int TRITON_ORBIT_ID = 1253;
    public static final int PHOBOS_ID = 1254;
    public static final int PHOBOS_ORBIT_ID = 1255;
    public static final int JUPITER_ORBIT_ID = 1260;

    // ===== Exoplanet systems =====
    public static final int BARNARDA_C_ID = 1301;
    public static final int BARNARDA_C_ORBIT_ID = 1302;
    public static final int BARNARDA_C1_ID = 1303;
    public static final int BARNARDA_C1_ORBIT_ID = 1304;
    public static final int TAUCETI_F_ID = 1311;
    public static final int TAUCETI_F_ORBIT_ID = 1312;
    public static final int PROXIMA_B_ID = 1321;
    public static final int PROXIMA_B_ORBIT_ID = 1322;

    private static final int DAY = 24000;

    // ===== Existing Properties =====
    public static final PlanetDimensionProperties MOON_PROPERTIES = new PlanetDimensionProperties(
        "moon", MOON_ID, "DIM_AD_ASTRA_MOON", ModBiomes.LUNAR_WASTELANDS,
        state(ModBlocks.MOON_SAND), state(ModBlocks.MOON_STONE),
        true, true, false, (short)-173, 0.166F, 24, 1, DAY,
        new Vec3d(0.035D, 0.035D, 0.045D), new Vec3d(0.0D, 0.0D, 0.0D));

    public static final PlanetDimensionProperties MARS_PROPERTIES = new PlanetDimensionProperties(
        "mars", MARS_ID, "DIM_AD_ASTRA_MARS", ModBiomes.MARTIAN_WASTELANDS,
        state(ModBlocks.MARS_SAND), state(ModBlocks.MARS_STONE),
        true, true, false, (short)-65, 0.38F, 12, 2, DAY,
        new Vec3d(0.72D, 0.33D, 0.2D), new Vec3d(0.38D, 0.12D, 0.08D));

    public static final PlanetDimensionProperties MERCURY_PROPERTIES = new PlanetDimensionProperties(
        "mercury", MERCURY_ID, "DIM_AD_ASTRA_MERCURY", ModBiomes.MERCURY_DELTAS,
        state(ModBlocks.MERCURY_STONE), state(ModBlocks.MERCURY_STONE),
        true, true, false, (short)167, 0.38F, 64, 3, DAY,
        new Vec3d(0.45D, 0.39D, 0.32D), new Vec3d(0.25D, 0.22D, 0.2D));

    public static final PlanetDimensionProperties VENUS_PROPERTIES = new PlanetDimensionProperties(
        "venus", VENUS_ID, "DIM_AD_ASTRA_VENUS", ModBiomes.VENUS_WASTELANDS,
        state(ModBlocks.VENUS_SAND), state(ModBlocks.VENUS_STONE),
        true, true, false, (short)464, 0.9F, 8, 3, DAY,
        new Vec3d(0.95D, 0.62D, 0.25D), new Vec3d(0.55D, 0.32D, 0.12D));

    public static final PlanetDimensionProperties GLACIO_PROPERTIES = new PlanetDimensionProperties(
        "glacio", GLACIO_ID, "DIM_AD_ASTRA_GLACIO", ModBiomes.GLACIO_SNOWY_BARRENS,
        state(ModBlocks.PERMAFROST), state(ModBlocks.GLACIO_STONE),
        true, true, true, (short)-20, 0.25F, 14, 4, DAY,
        new Vec3d(0.58D, 0.72D, 0.9D), new Vec3d(0.34D, 0.48D, 0.68D));

    // ===== New Planet Properties =====
    // Ceres - Dwarf planet, Tier 3
    public static final PlanetDimensionProperties CERES_PROPERTIES = new PlanetDimensionProperties(
        "ceres", CERES_ID, "DIM_AD_ASTRA_CERES", ModBiomes.CERES_WASTELANDS,
        state(ModBlocks.CERES_BLOCKS), state(ModBlocks.CERES_BLOCKS),
        true, true, false, (short)-105, 0.029F, 8, 3, DAY,
        new Vec3d(0.36D, 0.35D, 0.33D), new Vec3d(0.18D, 0.17D, 0.16D));

    // Pluto - Dwarf planet, Tier 4
    public static final PlanetDimensionProperties PLUTO_PROPERTIES = new PlanetDimensionProperties(
        "pluto", PLUTO_ID, "DIM_AD_ASTRA_PLUTO", ModBiomes.PLUTO_WASTELANDS,
        state(ModBlocks.PLUTO_BLOCKS), state(ModBlocks.PLUTO_BLOCKS),
        true, true, false, (short)-229, 0.063F, 4, 4, DAY,
        new Vec3d(0.22D, 0.2D, 0.27D), new Vec3d(0.12D, 0.11D, 0.14D));

    // Haumea - Dwarf planet, Tier 4
    public static final PlanetDimensionProperties HAUMEA_PROPERTIES = new PlanetDimensionProperties(
        "haumea", HAUMEA_ID, "DIM_AD_ASTRA_HAUMEA", ModBiomes.HAUMEA_WASTELANDS,
        state(ModBlocks.HAUMEA_BLOCKS), state(ModBlocks.HAUMEA_BLOCKS),
        true, true, false, (short)-241, 0.044F, 4, 4, DAY,
        new Vec3d(0.28D, 0.24D, 0.29D), new Vec3d(0.14D, 0.12D, 0.15D));

    // Kuiper Belt - Dimensionless zone, Tier 5
    public static final PlanetDimensionProperties KUIPER_BELT_PROPERTIES = new PlanetDimensionProperties(
        "kuiper_belt", KUIPER_BELT_ID, "DIM_AD_ASTRA_KUIPER_BELT", ModBiomes.KUIPER_BELT,
        state(Blocks.STONE), state(Blocks.STONE),
        true, true, false, (short)-250, 0.01F, 2, 5, DAY,
        new Vec3d(0.05D, 0.05D, 0.08D), new Vec3d(0.02D, 0.02D, 0.04D));

    // Io - Jupiter moon, Tier 4
    public static final PlanetDimensionProperties IO_PROPERTIES = new PlanetDimensionProperties(
        "io", IO_ID, "DIM_AD_ASTRA_IO", ModBiomes.IO_VOLCANIC,
        state(ModBlocks.IO_BLOCKS), state(ModBlocks.IO_BLOCKS),
        true, true, false, (short)-143, 0.183F, 20, 4, DAY,
        new Vec3d(0.55D, 0.42D, 0.13D), new Vec3d(0.32D, 0.22D, 0.06D));

    // Europa - Jupiter moon, Tier 4
    public static final PlanetDimensionProperties EUROPA_PROPERTIES = new PlanetDimensionProperties(
        "europa", EUROPA_ID, "DIM_AD_ASTRA_EUROPA", ModBiomes.EUROPA_ICE_PLAINS,
        state(ModBlocks.EUROPA_BLOCKS), state(ModBlocks.EUROPA_BLOCKS),
        true, true, false, (short)-160, 0.134F, 18, 4, DAY,
        new Vec3d(0.65D, 0.72D, 0.82D), new Vec3d(0.38D, 0.44D, 0.52D));

    // Ganymede - Jupiter moon, Tier 4
    public static final PlanetDimensionProperties GANYMEDE_PROPERTIES = new PlanetDimensionProperties(
        "ganymede", GANYMEDE_ID, "DIM_AD_ASTRA_GANYMEDE", ModBiomes.GANYMEDE_WASTELANDS,
        state(ModBlocks.GANYMEDE_BLOCKS), state(ModBlocks.GANYMEDE_BLOCKS),
        true, true, false, (short)-160, 0.146F, 14, 4, DAY,
        new Vec3d(0.45D, 0.42D, 0.48D), new Vec3d(0.22D, 0.2D, 0.25D));

    // Callisto - Jupiter moon, Tier 4
    public static final PlanetDimensionProperties CALLISTO_PROPERTIES = new PlanetDimensionProperties(
        "callisto", CALLISTO_ID, "DIM_AD_ASTRA_CALLISTO", ModBiomes.CALLISTO_WASTELANDS,
        state(ModBlocks.CALLISTO_BLOCKS), state(ModBlocks.CALLISTO_BLOCKS),
        true, true, false, (short)-139, 0.126F, 13, 4, DAY,
        new Vec3d(0.35D, 0.33D, 0.36D), new Vec3d(0.18D, 0.17D, 0.18D));

    // Enceladus - Saturn moon, Tier 5
    public static final PlanetDimensionProperties ENCELADUS_PROPERTIES = new PlanetDimensionProperties(
        "enceladus", ENCELADUS_ID, "DIM_AD_ASTRA_ENCELADUS", ModBiomes.ENCELADUS_PLAINS,
        state(ModBlocks.ENCELADUS_BLOCKS), state(ModBlocks.ENCELADUS_BLOCKS),
        true, true, false, (short)-198, 0.011F, 4, 5, DAY,
        new Vec3d(0.58D, 0.75D, 0.92D), new Vec3d(0.3D, 0.42D, 0.58D));

    // Titan - Saturn moon, Tier 5
    public static final PlanetDimensionProperties TITAN_PROPERTIES = new PlanetDimensionProperties(
        "titan", TITAN_ID, "DIM_AD_ASTRA_TITAN", ModBiomes.TITAN_WASTELANDS,
        state(ModBlocks.TITAN_BLOCKS), state(ModBlocks.TITAN_BLOCKS),
        true, true, false, (short)-179, 0.138F, 3, 5, DAY,
        new Vec3d(0.62D, 0.48D, 0.28D), new Vec3d(0.42D, 0.28D, 0.12D));

    // Miranda - Uranus moon, Tier 5
    public static final PlanetDimensionProperties MIRANDA_PROPERTIES = new PlanetDimensionProperties(
        "miranda", MIRANDA_ID, "DIM_AD_ASTRA_MIRANDA", ModBiomes.MIRANDA_WASTELANDS,
        state(ModBlocks.MIRANDA_BLOCKS), state(ModBlocks.MIRANDA_BLOCKS),
        true, true, false, (short)-187, 0.008F, 2, 5, DAY,
        new Vec3d(0.42D, 0.5D, 0.55D), new Vec3d(0.22D, 0.28D, 0.3D));

    // Triton - Neptune moon, Tier 5
    public static final PlanetDimensionProperties TRITON_PROPERTIES = new PlanetDimensionProperties(
        "triton", TRITON_ID, "DIM_AD_ASTRA_TRITON", ModBiomes.TRITON_PLAINS,
        state(ModBlocks.TRITON_BLOCKS), state(ModBlocks.TRITON_BLOCKS),
        true, true, false, (short)-235, 0.078F, 2, 5, DAY,
        new Vec3d(0.38D, 0.45D, 0.58D), new Vec3d(0.18D, 0.25D, 0.35D));

    // Phobos - Mars moon, Tier 3
    public static final PlanetDimensionProperties PHOBOS_PROPERTIES = new PlanetDimensionProperties(
        "phobos", PHOBOS_ID, "DIM_AD_ASTRA_PHOBOS", ModBiomes.PHOBOS_WASTELANDS,
        state(ModBlocks.PHOBOS_BLOCKS), state(ModBlocks.PHOBOS_BLOCKS),
        true, true, false, (short)-40, 0.006F, 14, 3, DAY,
        new Vec3d(0.62D, 0.38D, 0.22D), new Vec3d(0.35D, 0.18D, 0.08D));

    // Barnarda C - Exoplanet, Tier 6
    public static final PlanetDimensionProperties BARNARDA_C_PROPERTIES = new PlanetDimensionProperties(
        "barnarda_c", BARNARDA_C_ID, "DIM_AD_ASTRA_BARNARDA_C", ModBiomes.BARNARDA_C_PLAINS,
        state(ModBlocks.BARNARDA_C_BLOCKS), state(ModBlocks.BARNARDA_C_BLOCKS),
        true, true, true, (short)15, 0.9F, 24, 6, DAY,
        new Vec3d(0.35D, 0.55D, 0.72D), new Vec3d(0.15D, 0.28D, 0.42D));

    // Barnarda C1 - Barnarda moon, Tier 6
    public static final PlanetDimensionProperties BARNARDA_C1_PROPERTIES = new PlanetDimensionProperties(
        "barnarda_c1", BARNARDA_C1_ID, "DIM_AD_ASTRA_BARNARDA_C1", ModBiomes.BARNARDA_C1_WASTELANDS,
        state(ModBlocks.BARNARDA_C1_BLOCKS), state(ModBlocks.BARNARDA_C1_BLOCKS),
        true, true, false, (short)-25, 0.3F, 12, 6, DAY,
        new Vec3d(0.45D, 0.4D, 0.38D), new Vec3d(0.22D, 0.2D, 0.18D));

    // Tau Ceti F - Exoplanet, Tier 6
    public static final PlanetDimensionProperties TAUCETI_F_PROPERTIES = new PlanetDimensionProperties(
        "tauceti_f", TAUCETI_F_ID, "DIM_AD_ASTRA_TAUCETI_F", ModBiomes.TAUCETI_F_WASTELANDS,
        state(ModBlocks.TAUCETI_F_BLOCKS), state(ModBlocks.TAUCETI_F_BLOCKS),
        true, true, true, (short)20, 1.1F, 18, 6, DAY,
        new Vec3d(0.22D, 0.48D, 0.65D), new Vec3d(0.1D, 0.25D, 0.4D));

    // Proxima B - Exoplanet, Tier 6
    public static final PlanetDimensionProperties PROXIMA_B_PROPERTIES = new PlanetDimensionProperties(
        "proxima_b", PROXIMA_B_ID, "DIM_AD_ASTRA_PROXIMA_B", ModBiomes.PROXIMA_B_WASTELANDS,
        state(ModBlocks.PROXIMA_B_BLOCKS), state(ModBlocks.PROXIMA_B_BLOCKS),
        true, true, true, (short)18, 1.0F, 16, 6, DAY,
        new Vec3d(0.42D, 0.55D, 0.38D), new Vec3d(0.2D, 0.32D, 0.18D));

    // ===== Existing Orbit Properties =====
    public static final PlanetDimensionProperties EARTH_ORBIT_PROPERTIES = orbit("earth_orbit", EARTH_ORBIT_ID, "DIM_AD_ASTRA_EARTH_ORBIT", 1, new Vec3d(0.1D,0.2D,0.4D), new Vec3d(0.05D,0.1D,0.2D));
    public static final PlanetDimensionProperties MOON_ORBIT_PROPERTIES = orbit("moon_orbit", MOON_ORBIT_ID, "DIM_AD_ASTRA_MOON_ORBIT", 1, MOON_PROPERTIES.getFogColor(), MOON_PROPERTIES.getSkyColor());
    public static final PlanetDimensionProperties MARS_ORBIT_PROPERTIES = orbit("mars_orbit", MARS_ORBIT_ID, "DIM_AD_ASTRA_MARS_ORBIT", 2, MARS_PROPERTIES.getFogColor(), MARS_PROPERTIES.getSkyColor());
    public static final PlanetDimensionProperties MERCURY_ORBIT_PROPERTIES = orbit("mercury_orbit", MERCURY_ORBIT_ID, "DIM_AD_ASTRA_MERCURY_ORBIT", 3, MERCURY_PROPERTIES.getFogColor(), MERCURY_PROPERTIES.getSkyColor());
    public static final PlanetDimensionProperties VENUS_ORBIT_PROPERTIES = orbit("venus_orbit", VENUS_ORBIT_ID, "DIM_AD_ASTRA_VENUS_ORBIT", 3, VENUS_PROPERTIES.getFogColor(), VENUS_PROPERTIES.getSkyColor());
    public static final PlanetDimensionProperties GLACIO_ORBIT_PROPERTIES = orbit("glacio_orbit", GLACIO_ORBIT_ID, "DIM_AD_ASTRA_GLACIO_ORBIT", 4, GLACIO_PROPERTIES.getFogColor(), GLACIO_PROPERTIES.getSkyColor());
    public static final PlanetDimensionProperties NETHER_ORBIT_PROPERTIES = orbit("nether_orbit", NETHER_ORBIT_ID, "DIM_AD_ASTRA_NETHER_ORBIT", 3, new Vec3d(0.35D,0.02D,0.01D), new Vec3d(0.16D,0.01D,0.01D));
    public static final PlanetDimensionProperties END_ORBIT_PROPERTIES = orbit("the_end_orbit", END_ORBIT_ID, "DIM_AD_ASTRA_THE_END_ORBIT", 4, new Vec3d(0.05D,0.03D,0.09D), new Vec3d(0.02D,0.02D,0.04D));

    // ===== New Orbit Properties =====
    public static final PlanetDimensionProperties CERES_ORBIT_PROPERTIES = orbit("ceres_orbit", CERES_ORBIT_ID, "DIM_AD_ASTRA_CERES_ORBIT", 3, CERES_PROPERTIES.getFogColor(), CERES_PROPERTIES.getSkyColor());
    public static final PlanetDimensionProperties PLUTO_ORBIT_PROPERTIES = orbit("pluto_orbit", PLUTO_ORBIT_ID, "DIM_AD_ASTRA_PLUTO_ORBIT", 4, PLUTO_PROPERTIES.getFogColor(), PLUTO_PROPERTIES.getSkyColor());
    public static final PlanetDimensionProperties HAUMEA_ORBIT_PROPERTIES = orbit("haumea_orbit", HAUMEA_ORBIT_ID, "DIM_AD_ASTRA_HAUMEA_ORBIT", 4, HAUMEA_PROPERTIES.getFogColor(), HAUMEA_PROPERTIES.getSkyColor());
    public static final PlanetDimensionProperties IO_ORBIT_PROPERTIES = orbit("io_orbit", IO_ORBIT_ID, "DIM_AD_ASTRA_IO_ORBIT", 4, IO_PROPERTIES.getFogColor(), IO_PROPERTIES.getSkyColor());
    public static final PlanetDimensionProperties EUROPA_ORBIT_PROPERTIES = orbit("europa_orbit", EUROPA_ORBIT_ID, "DIM_AD_ASTRA_EUROPA_ORBIT", 4, EUROPA_PROPERTIES.getFogColor(), EUROPA_PROPERTIES.getSkyColor());
    public static final PlanetDimensionProperties GANYMEDE_ORBIT_PROPERTIES = orbit("ganymede_orbit", GANYMEDE_ORBIT_ID, "DIM_AD_ASTRA_GANYMEDE_ORBIT", 4, GANYMEDE_PROPERTIES.getFogColor(), GANYMEDE_PROPERTIES.getSkyColor());
    public static final PlanetDimensionProperties CALLISTO_ORBIT_PROPERTIES = orbit("callisto_orbit", CALLISTO_ORBIT_ID, "DIM_AD_ASTRA_CALLISTO_ORBIT", 4, CALLISTO_PROPERTIES.getFogColor(), CALLISTO_PROPERTIES.getSkyColor());
    public static final PlanetDimensionProperties ENCELADUS_ORBIT_PROPERTIES = orbit("enceladus_orbit", ENCELADUS_ORBIT_ID, "DIM_AD_ASTRA_ENCELADUS_ORBIT", 5, ENCELADUS_PROPERTIES.getFogColor(), ENCELADUS_PROPERTIES.getSkyColor());
    public static final PlanetDimensionProperties TITAN_ORBIT_PROPERTIES = orbit("titan_orbit", TITAN_ORBIT_ID, "DIM_AD_ASTRA_TITAN_ORBIT", 5, TITAN_PROPERTIES.getFogColor(), TITAN_PROPERTIES.getSkyColor());
    public static final PlanetDimensionProperties MIRANDA_ORBIT_PROPERTIES = orbit("miranda_orbit", MIRANDA_ORBIT_ID, "DIM_AD_ASTRA_MIRANDA_ORBIT", 5, MIRANDA_PROPERTIES.getFogColor(), MIRANDA_PROPERTIES.getSkyColor());
    public static final PlanetDimensionProperties TRITON_ORBIT_PROPERTIES = orbit("triton_orbit", TRITON_ORBIT_ID, "DIM_AD_ASTRA_TRITON_ORBIT", 5, TRITON_PROPERTIES.getFogColor(), TRITON_PROPERTIES.getSkyColor());
    public static final PlanetDimensionProperties PHOBOS_ORBIT_PROPERTIES = orbit("phobos_orbit", PHOBOS_ORBIT_ID, "DIM_AD_ASTRA_PHOBOS_ORBIT", 3, PHOBOS_PROPERTIES.getFogColor(), PHOBOS_PROPERTIES.getSkyColor());
    public static final PlanetDimensionProperties JUPITER_ORBIT_PROPERTIES = orbit("jupiter_orbit", JUPITER_ORBIT_ID, "DIM_AD_ASTRA_JUPITER_ORBIT", 4, new Vec3d(0.6D,0.5D,0.3D), new Vec3d(0.35D,0.28D,0.15D));
    public static final PlanetDimensionProperties BARNARDA_C_ORBIT_PROPERTIES = orbit("barnarda_c_orbit", BARNARDA_C_ORBIT_ID, "DIM_AD_ASTRA_BARNARDA_C_ORBIT", 6, BARNARDA_C_PROPERTIES.getFogColor(), BARNARDA_C_PROPERTIES.getSkyColor());
    public static final PlanetDimensionProperties BARNARDA_C1_ORBIT_PROPERTIES = orbit("barnarda_c1_orbit", BARNARDA_C1_ORBIT_ID, "DIM_AD_ASTRA_BARNARDA_C1_ORBIT", 6, BARNARDA_C1_PROPERTIES.getFogColor(), BARNARDA_C1_PROPERTIES.getSkyColor());
    public static final PlanetDimensionProperties TAUCETI_F_ORBIT_PROPERTIES = orbit("tauceti_f_orbit", TAUCETI_F_ORBIT_ID, "DIM_AD_ASTRA_TAUCETI_F_ORBIT", 6, TAUCETI_F_PROPERTIES.getFogColor(), TAUCETI_F_PROPERTIES.getSkyColor());
    public static final PlanetDimensionProperties PROXIMA_B_ORBIT_PROPERTIES = orbit("proxima_b_orbit", PROXIMA_B_ORBIT_ID, "DIM_AD_ASTRA_PROXIMA_B_ORBIT", 6, PROXIMA_B_PROPERTIES.getFogColor(), PROXIMA_B_PROPERTIES.getSkyColor());

    // ===== DimensionType instances =====
    public static DimensionType MOON, MARS, MERCURY, VENUS, GLACIO;
    public static DimensionType EARTH_ORBIT, MOON_ORBIT, MARS_ORBIT, MERCURY_ORBIT, VENUS_ORBIT, GLACIO_ORBIT, NETHER_ORBIT, END_ORBIT;
    public static DimensionType CERES, CERES_ORBIT, PLUTO, PLUTO_ORBIT, HAUMEA, HAUMEA_ORBIT, KUIPER_BELT;
    public static DimensionType IO, IO_ORBIT, EUROPA, EUROPA_ORBIT, GANYMEDE, GANYMEDE_ORBIT, CALLISTO, CALLISTO_ORBIT;
    public static DimensionType ENCELADUS, ENCELADUS_ORBIT, TITAN, TITAN_ORBIT;
    public static DimensionType MIRANDA, MIRANDA_ORBIT, TRITON, TRITON_ORBIT, PHOBOS, PHOBOS_ORBIT, JUPITER_ORBIT;
    public static DimensionType BARNARDA_C, BARNARDA_C_ORBIT, BARNARDA_C1, BARNARDA_C1_ORBIT;
    public static DimensionType TAUCETI_F, TAUCETI_F_ORBIT, PROXIMA_B, PROXIMA_B_ORBIT;

    private static boolean registered;

    private ModDimensions() {}

    public static void register() {
        if (registered) return;

        MOON = reg("moon", MOON_PROPERTIES, WorldProviderMoon.class);
        MARS = reg("mars", MARS_PROPERTIES, WorldProviderMars.class);
        MERCURY = reg("mercury", MERCURY_PROPERTIES, WorldProviderMercury.class);
        VENUS = reg("venus", VENUS_PROPERTIES, WorldProviderVenus.class);
        GLACIO = reg("glacio", GLACIO_PROPERTIES, WorldProviderGlacio.class);
        EARTH_ORBIT = reg("earth_orbit", EARTH_ORBIT_PROPERTIES, WorldProviderEarthOrbit.class);
        MOON_ORBIT = reg("moon_orbit", MOON_ORBIT_PROPERTIES, WorldProviderMoonOrbit.class);
        MARS_ORBIT = reg("mars_orbit", MARS_ORBIT_PROPERTIES, WorldProviderMarsOrbit.class);
        MERCURY_ORBIT = reg("mercury_orbit", MERCURY_ORBIT_PROPERTIES, WorldProviderMercuryOrbit.class);
        VENUS_ORBIT = reg("venus_orbit", VENUS_ORBIT_PROPERTIES, WorldProviderVenusOrbit.class);
        GLACIO_ORBIT = reg("glacio_orbit", GLACIO_ORBIT_PROPERTIES, WorldProviderGlacioOrbit.class);
        NETHER_ORBIT = reg("nether_orbit", NETHER_ORBIT_PROPERTIES, WorldProviderNetherOrbit.class);
        END_ORBIT = reg("the_end_orbit", END_ORBIT_PROPERTIES, WorldProviderEndOrbit.class);

        CERES = reg("ceres", CERES_PROPERTIES, WorldProviderCeres.class);
        PLUTO = reg("pluto", PLUTO_PROPERTIES, WorldProviderPluto.class);
        HAUMEA = reg("haumea", HAUMEA_PROPERTIES, WorldProviderHaumea.class);
        KUIPER_BELT = reg("kuiper_belt", KUIPER_BELT_PROPERTIES, WorldProviderKuiperBelt.class);
        IO = reg("io", IO_PROPERTIES, WorldProviderIo.class);
        EUROPA = reg("europa", EUROPA_PROPERTIES, WorldProviderEuropa.class);
        GANYMEDE = reg("ganymede", GANYMEDE_PROPERTIES, WorldProviderGanymede.class);
        CALLISTO = reg("callisto", CALLISTO_PROPERTIES, WorldProviderCallisto.class);
        ENCELADUS = reg("enceladus", ENCELADUS_PROPERTIES, WorldProviderEnceladus.class);
        TITAN = reg("titan", TITAN_PROPERTIES, WorldProviderTitan.class);
        MIRANDA = reg("miranda", MIRANDA_PROPERTIES, WorldProviderMiranda.class);
        TRITON = reg("triton", TRITON_PROPERTIES, WorldProviderTriton.class);
        PHOBOS = reg("phobos", PHOBOS_PROPERTIES, WorldProviderPhobos.class);
        BARNARDA_C = reg("barnarda_c", BARNARDA_C_PROPERTIES, WorldProviderBarnardaC.class);
        BARNARDA_C1 = reg("barnarda_c1", BARNARDA_C1_PROPERTIES, WorldProviderBarnardaC1.class);
        TAUCETI_F = reg("tauceti_f", TAUCETI_F_PROPERTIES, WorldProviderTauCetiF.class);
        PROXIMA_B = reg("proxima_b", PROXIMA_B_PROPERTIES, WorldProviderProximaB.class);

        CERES_ORBIT = reg("ceres_orbit", CERES_ORBIT_PROPERTIES, WorldProviderCeresOrbit.class);
        PLUTO_ORBIT = reg("pluto_orbit", PLUTO_ORBIT_PROPERTIES, WorldProviderPlutoOrbit.class);
        HAUMEA_ORBIT = reg("haumea_orbit", HAUMEA_ORBIT_PROPERTIES, WorldProviderHaumeaOrbit.class);
        IO_ORBIT = reg("io_orbit", IO_ORBIT_PROPERTIES, WorldProviderIoOrbit.class);
        EUROPA_ORBIT = reg("europa_orbit", EUROPA_ORBIT_PROPERTIES, WorldProviderEuropaOrbit.class);
        GANYMEDE_ORBIT = reg("ganymede_orbit", GANYMEDE_ORBIT_PROPERTIES, WorldProviderGanymedeOrbit.class);
        CALLISTO_ORBIT = reg("callisto_orbit", CALLISTO_ORBIT_PROPERTIES, WorldProviderCallistoOrbit.class);
        ENCELADUS_ORBIT = reg("enceladus_orbit", ENCELADUS_ORBIT_PROPERTIES, WorldProviderEnceladusOrbit.class);
        TITAN_ORBIT = reg("titan_orbit", TITAN_ORBIT_PROPERTIES, WorldProviderTitanOrbit.class);
        MIRANDA_ORBIT = reg("miranda_orbit", MIRANDA_ORBIT_PROPERTIES, WorldProviderMirandaOrbit.class);
        TRITON_ORBIT = reg("triton_orbit", TRITON_ORBIT_PROPERTIES, WorldProviderTritonOrbit.class);
        PHOBOS_ORBIT = reg("phobos_orbit", PHOBOS_ORBIT_PROPERTIES, WorldProviderPhobosOrbit.class);
        JUPITER_ORBIT = reg("jupiter_orbit", JUPITER_ORBIT_PROPERTIES, WorldProviderJupiterOrbit.class);
        BARNARDA_C_ORBIT = reg("barnarda_c_orbit", BARNARDA_C_ORBIT_PROPERTIES, WorldProviderBarnardaCOrbit.class);
        BARNARDA_C1_ORBIT = reg("barnarda_c1_orbit", BARNARDA_C1_ORBIT_PROPERTIES, WorldProviderBarnardaC1Orbit.class);
        TAUCETI_F_ORBIT = reg("tauceti_f_orbit", TAUCETI_F_ORBIT_PROPERTIES, WorldProviderTauCetiFOrbit.class);
        PROXIMA_B_ORBIT = reg("proxima_b_orbit", PROXIMA_B_ORBIT_PROPERTIES, WorldProviderProximaBOrbit.class);

        registered = true;
    }

    private static DimensionType reg(String name, PlanetDimensionProperties p, Class<? extends WorldProvider> cls) {
        DimensionType type = DimensionType.register(Reference.MOD_ID + "_" + name, "_" + Reference.MOD_ID + "_" + name, p.getDimensionId(), cls, false);
        if (!DimensionManager.isDimensionRegistered(p.getDimensionId())) {
            DimensionManager.registerDimension(p.getDimensionId(), type);
            AdAstraReborn.LOGGER.info("Registered {} dimension {} with id {}.", Reference.MOD_NAME, name, p.getDimensionId());
        }
        return type;
    }

    private static net.minecraft.block.state.IBlockState state(Block block) { return block.getDefaultState(); }

    private static PlanetDimensionProperties orbit(String name, int id, String folder, int tier, Vec3d fog, Vec3d sky) {
        return new PlanetDimensionProperties(name, id, folder, Biomes.SKY, state(Blocks.AIR), state(Blocks.AIR), true, true, false, (short)-270, 0.05F, 64, tier, DAY, fog, sky);
    }
}
