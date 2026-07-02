package earth.terrarium.adastra.api.planets;

import earth.terrarium.adastra.Reference;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Represents a planet or space dimension in the Ad Astra mod.
 * <p>
 * This class contains all the metadata for a planet, including:
 * <ul>
 *   <li>Dimension ID for the planet</li>
 *   <li>Atmospheric properties (oxygen, temperature, gravity)</li>
 *   <li>Solar power generation capability</li>
 *   <li>Solar system membership</li>
 *   <li>Orbit dimension (for space stations)</li>
 *   <li>Technology tier requirement</li>
 * </ul>
 * <p>
 * Planets can be registered by third-party mods to add custom celestial bodies.
 *
 * @since 1.12.2
 */
public class Planet {

    // Default Ad Astra planet dimension IDs
    public static final int MOON_DIM = 1201;
    public static final int MARS_DIM = 1202;
    public static final int MERCURY_DIM = 1203;
    public static final int VENUS_DIM = 1204;
    public static final int GLACIO_DIM = 1205;

    private final int dimensionId;
    private final boolean oxygen;
    private final short temperature;
    private final float gravity;
    private final int solarPower;
    private final ResourceLocation solarSystem;
    @Nullable
    private final Integer orbitDimensionId;
    private final int tier;
    private final List<Integer> additionalLaunchDimensions;

    /**
     * Constructs a new Planet definition.
     *
     * @param dimensionId The dimension ID for this planet
     * @param oxygen Whether the planet has breathable oxygen
     * @param temperature The planet's temperature in Celsius
     * @param gravity The gravity multiplier (1.0 = Earth gravity)
     * @param solarPower The solar power generation multiplier
     * @param solarSystem The solar system this planet belongs to
     * @param orbitDimensionId The dimension ID of this planet's orbit, or null if this IS an orbit
     * @param tier The technology tier required to reach this planet (1-4)
     * @param additionalLaunchDimensions Additional dimensions from which rockets can launch to this planet
     */
    public Planet(
        int dimensionId,
        boolean oxygen,
        short temperature,
        float gravity,
        int solarPower,
        ResourceLocation solarSystem,
        @Nullable Integer orbitDimensionId,
        int tier,
        List<Integer> additionalLaunchDimensions
    ) {
        this.dimensionId = dimensionId;
        this.oxygen = oxygen;
        this.temperature = temperature;
        this.gravity = gravity;
        this.solarPower = solarPower;
        this.solarSystem = solarSystem;
        this.orbitDimensionId = orbitDimensionId;
        this.tier = tier;
        this.additionalLaunchDimensions = additionalLaunchDimensions;
    }

    /**
     * Gets the dimension ID for this planet.
     *
     * @return The dimension ID
     */
    public int getDimensionId() {
        return dimensionId;
    }

    /**
     * Gets whether this planet has breathable oxygen.
     *
     * @return true if the planet has oxygen, false otherwise
     */
    public boolean hasOxygen() {
        return oxygen;
    }

    /**
     * Gets the planet's temperature in Celsius.
     *
     * @return The temperature
     */
    public short getTemperature() {
        return temperature;
    }

    /**
     * Gets the planet's gravity multiplier.
     *
     * @return The gravity multiplier (1.0 = Earth gravity)
     */
    public float getGravity() {
        return gravity;
    }

    /**
     * Gets the solar power generation multiplier for this planet.
     * Higher values mean solar panels generate more power.
     *
     * @return The solar power multiplier
     */
    public int getSolarPower() {
        return solarPower;
    }

    /**
     * Gets the solar system this planet belongs to.
     *
     * @return The solar system resource location
     */
    public ResourceLocation getSolarSystem() {
        return solarSystem;
    }

    /**
     * Gets the dimension ID of this planet's orbit.
     * Returns null if this dimension IS an orbit (space station).
     *
     * @return The orbit dimension ID, or null if this is an orbit
     */
    @Nullable
    public Integer getOrbitDimensionId() {
        return orbitDimensionId;
    }

    /**
     * Gets the orbit dimension ID, or returns this planet's dimension ID if it has no orbit.
     *
     * @return The orbit dimension ID, or this planet's ID
     */
    public int getOrbitDimensionIdOrSelf() {
        return orbitDimensionId != null ? orbitDimensionId : dimensionId;
    }

    /**
     * Returns whether this dimension represents space (an orbit) rather than a planet surface.
     *
     * @return true if this is a space/orbit dimension, false if it's a planet surface
     */
    public boolean isSpace() {
        return orbitDimensionId == null;
    }

    /**
     * Gets the technology tier required to reach this planet.
     * Tiers typically range from 1 (Moon) to 4 (Glacio).
     *
     * @return The required technology tier
     */
    public int getTier() {
        return tier;
    }

    /**
     * Gets the list of additional dimensions from which rockets can launch to this planet.
     * This allows custom launch locations beyond the default Earth/planet surfaces.
     *
     * @return The list of additional launch dimension IDs
     */
    public List<Integer> getAdditionalLaunchDimensions() {
        return additionalLaunchDimensions;
    }

    /**
     * Creates a simple planet builder for easier planet registration.
     *
     * @param dimensionId The dimension ID
     * @return A new PlanetBuilder instance
     */
    public static PlanetBuilder builder(int dimensionId) {
        return new PlanetBuilder(dimensionId);
    }

    /**
     * Builder class for easier Planet construction.
     */
    public static class PlanetBuilder {
        private final int dimensionId;
        private boolean oxygen = false;
        private short temperature = 0;
        private float gravity = 1.0f;
        private int solarPower = 10;
        private ResourceLocation solarSystem = new ResourceLocation(Reference.MOD_ID, "solar_system");
        private Integer orbitDimensionId = null;
        private int tier = 1;
        private List<Integer> additionalLaunchDimensions = java.util.Collections.emptyList();

        private PlanetBuilder(int dimensionId) {
            this.dimensionId = dimensionId;
        }

        public PlanetBuilder oxygen(boolean oxygen) {
            this.oxygen = oxygen;
            return this;
        }

        public PlanetBuilder temperature(short temperature) {
            this.temperature = temperature;
            return this;
        }

        public PlanetBuilder gravity(float gravity) {
            this.gravity = gravity;
            return this;
        }

        public PlanetBuilder solarPower(int solarPower) {
            this.solarPower = solarPower;
            return this;
        }

        public PlanetBuilder solarSystem(ResourceLocation solarSystem) {
            this.solarSystem = solarSystem;
            return this;
        }

        public PlanetBuilder orbitDimensionId(Integer orbitDimensionId) {
            this.orbitDimensionId = orbitDimensionId;
            return this;
        }

        public PlanetBuilder tier(int tier) {
            this.tier = tier;
            return this;
        }

        public PlanetBuilder additionalLaunchDimensions(List<Integer> additionalLaunchDimensions) {
            this.additionalLaunchDimensions = additionalLaunchDimensions;
            return this;
        }

        public Planet build() {
            return new Planet(
                dimensionId,
                oxygen,
                temperature,
                gravity,
                solarPower,
                solarSystem,
                orbitDimensionId,
                tier,
                additionalLaunchDimensions
            );
        }
    }
}
