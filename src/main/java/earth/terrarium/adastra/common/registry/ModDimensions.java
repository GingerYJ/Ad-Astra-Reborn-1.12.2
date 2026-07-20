package earth.terrarium.adastra.common.registry;

import earth.terrarium.adastra.AdAstraReborn;
import earth.terrarium.adastra.Reference;
import earth.terrarium.adastra.common.world.PlanetDimensionProperties;
import earth.terrarium.adastra.common.world.*;
import earth.terrarium.adastra.common.world.custom.BuiltInPlanetDimensionRegistrar;
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

    // Keep the built-in surface IDs and the single shared station ID in high ranges
    // to reduce mod conflicts.
    public static final int FIRST_PLANET_ID = 108490;
    /** The only non-surface dimension owned by Ad Astra. */
    public static final int SPACE_STATION_ID = 107489;
    public static final int MOON_ID = FIRST_PLANET_ID;
    public static final int MARS_ID = FIRST_PLANET_ID + 1;
    public static final int MERCURY_ID = FIRST_PLANET_ID + 2;
    public static final int VENUS_ID = FIRST_PLANET_ID + 3;
    public static final int GLACIO_ID = FIRST_PLANET_ID + 4;

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

    // ===== Global Space Station Properties =====
    public static final PlanetDimensionProperties SPACE_STATION_PROPERTIES = spaceStation(
        "space_station", SPACE_STATION_ID, "DIM_AD_ASTRA_SPACE_STATION", 1, 32,
        new Vec3d(0.1D, 0.2D, 0.4D), new Vec3d(0.05D, 0.1D, 0.2D));

    // ===== DimensionType instances =====
    public static DimensionType MOON, MARS, MERCURY, VENUS, GLACIO;
    public static DimensionType SPACE_STATION;
    private static boolean registered;

    private ModDimensions() {}

    public static void register() {
        if (registered) return;

        MOON = reg("moon", MOON_PROPERTIES, WorldProviderMoon.class);
        MARS = reg("mars", MARS_PROPERTIES, WorldProviderMars.class);
        MERCURY = reg("mercury", MERCURY_PROPERTIES, WorldProviderMercury.class);
        VENUS = reg("venus", VENUS_PROPERTIES, WorldProviderVenus.class);
        GLACIO = reg("glacio", GLACIO_PROPERTIES, WorldProviderGlacio.class);
        SPACE_STATION = reg("space_station", SPACE_STATION_PROPERTIES, WorldProviderSpaceStation.class);

        ModPlanets.register();
        BuiltInPlanetDimensionRegistrar.register();

        registered = true;
    }

    /** Returns the registered surface-planet properties, excluding the space station. */
    public static List<PlanetDimensionProperties> getPlanetProperties() {
        return Collections.unmodifiableList(Arrays.asList(
            MOON_PROPERTIES,
            MARS_PROPERTIES,
            MERCURY_PROPERTIES,
            VENUS_PROPERTIES,
            GLACIO_PROPERTIES));
    }

    /** Returns whether an ID belongs to a built-in surface planet or the global space station. */
    public static boolean isBuiltInDimension(int dimensionId) {
        return dimensionId == MOON_ID || dimensionId == MARS_ID || dimensionId == MERCURY_ID
            || dimensionId == VENUS_ID || dimensionId == GLACIO_ID
            || dimensionId == SPACE_STATION_ID;
    }

    public static boolean isBuiltInPlanetId(String planetName) {
        return "moon".equals(planetName) || "mars".equals(planetName) || "mercury".equals(planetName)
            || "venus".equals(planetName) || "glacio".equals(planetName);
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

    private static PlanetDimensionProperties spaceStation(String name, int id, String folder, int tier, int solarPower, Vec3d fog, Vec3d sky) {
        return new PlanetDimensionProperties(name, id, folder, Biomes.SKY, state(Blocks.AIR), state(Blocks.AIR), true, true, false, (short)-270, 0.05F, solarPower, tier, DAY, fog, sky);
    }
}
