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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class ModDimensions {

    // Keep surface and orbit IDs in separate high ranges to reduce mod conflicts.
    public static final int FIRST_PLANET_ID = 108490;
    public static final int FIRST_ORBIT_ID = 107490;
    public static final int MOON_ID = FIRST_PLANET_ID;
    public static final int MARS_ID = FIRST_PLANET_ID + 1;
    public static final int MERCURY_ID = FIRST_PLANET_ID + 2;
    public static final int VENUS_ID = FIRST_PLANET_ID + 3;
    public static final int GLACIO_ID = FIRST_PLANET_ID + 4;
    public static final int EARTH_ORBIT_ID = FIRST_ORBIT_ID - 1;
    public static final int MOON_ORBIT_ID = FIRST_ORBIT_ID;
    public static final int MARS_ORBIT_ID = FIRST_ORBIT_ID + 1;
    public static final int MERCURY_ORBIT_ID = FIRST_ORBIT_ID + 2;
    public static final int VENUS_ORBIT_ID = FIRST_ORBIT_ID + 3;
    public static final int GLACIO_ORBIT_ID = FIRST_ORBIT_ID + 4;

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

    // ===== Existing Orbit Properties =====
    public static final PlanetDimensionProperties EARTH_ORBIT_PROPERTIES = orbit("earth_orbit", EARTH_ORBIT_ID, "DIM_AD_ASTRA_EARTH_ORBIT", 1, 32, new Vec3d(0.1D,0.2D,0.4D), new Vec3d(0.05D,0.1D,0.2D));
    public static final PlanetDimensionProperties MOON_ORBIT_PROPERTIES = orbit("moon_orbit", MOON_ORBIT_ID, "DIM_AD_ASTRA_MOON_ORBIT", 1, 32, MOON_PROPERTIES.getFogColor(), MOON_PROPERTIES.getSkyColor());
    public static final PlanetDimensionProperties MARS_ORBIT_PROPERTIES = orbit("mars_orbit", MARS_ORBIT_ID, "DIM_AD_ASTRA_MARS_ORBIT", 2, 24, MARS_PROPERTIES.getFogColor(), MARS_PROPERTIES.getSkyColor());
    public static final PlanetDimensionProperties MERCURY_ORBIT_PROPERTIES = orbit("mercury_orbit", MERCURY_ORBIT_ID, "DIM_AD_ASTRA_MERCURY_ORBIT", 3, 72, MERCURY_PROPERTIES.getFogColor(), MERCURY_PROPERTIES.getSkyColor());
    public static final PlanetDimensionProperties VENUS_ORBIT_PROPERTIES = orbit("venus_orbit", VENUS_ORBIT_ID, "DIM_AD_ASTRA_VENUS_ORBIT", 3, 48, VENUS_PROPERTIES.getFogColor(), VENUS_PROPERTIES.getSkyColor());
    public static final PlanetDimensionProperties GLACIO_ORBIT_PROPERTIES = orbit("glacio_orbit", GLACIO_ORBIT_ID, "DIM_AD_ASTRA_GLACIO_ORBIT", 4, 36, GLACIO_PROPERTIES.getFogColor(), GLACIO_PROPERTIES.getSkyColor());

    // ===== DimensionType instances =====
    public static DimensionType MOON, MARS, MERCURY, VENUS, GLACIO;
    public static DimensionType EARTH_ORBIT, MOON_ORBIT, MARS_ORBIT, MERCURY_ORBIT, VENUS_ORBIT, GLACIO_ORBIT;
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

        ModPlanets.register();

        registered = true;
    }

    /** Returns the registered surface-planet properties, excluding orbit dimensions. */
    public static List<PlanetDimensionProperties> getPlanetProperties() {
        return Collections.unmodifiableList(Arrays.asList(
            MOON_PROPERTIES,
            MARS_PROPERTIES,
            MERCURY_PROPERTIES,
            VENUS_PROPERTIES,
            GLACIO_PROPERTIES));
    }

    /** Returns the orbit dimension paired with a built-in surface planet. */
    public static int getOrbitDimensionId(String planetName) {
        if ("moon".equals(planetName)) return MOON_ORBIT_ID;
        if ("mars".equals(planetName)) return MARS_ORBIT_ID;
        if ("mercury".equals(planetName)) return MERCURY_ORBIT_ID;
        if ("venus".equals(planetName)) return VENUS_ORBIT_ID;
        if ("glacio".equals(planetName)) return GLACIO_ORBIT_ID;
        return 0;
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

    private static PlanetDimensionProperties orbit(String name, int id, String folder, int tier, int solarPower, Vec3d fog, Vec3d sky) {
        return new PlanetDimensionProperties(name, id, folder, Biomes.SKY, state(Blocks.AIR), state(Blocks.AIR), true, true, false, (short)-270, 0.05F, solarPower, tier, DAY, fog, sky);
    }
}
