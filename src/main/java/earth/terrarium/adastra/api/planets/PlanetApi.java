package earth.terrarium.adastra.api.planets;

import earth.terrarium.adastra.api.ApiHelper;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * API for querying planet and space dimension information in the Ad Astra mod.
 * <p>
 * This interface provides methods to:
 * <ul>
 *   <li>Query planet metadata for dimensions</li>
 *   <li>Check if dimensions are planets, space, or extraterrestrial</li>
 *   <li>Get solar power generation values for dimensions</li>
 *   <li>Register custom planets for third-party integration</li>
 * </ul>
 * <p>
 * Third-party mods can use {@link #API} to access the implementation.
 *
 * @since 1.12.2
 */
public interface PlanetApi {

    /**
     * The singleton API instance. Loaded via ServiceLoader.
     */
    PlanetApi API = ApiHelper.load(PlanetApi.class);

    /**
     * Gets the planet data for the given world, or null if the world is not a planet.
     * This includes all metadata such as oxygen, temperature, gravity, and solar power.
     *
     * @param world The world to get planet data for
     * @return The Planet data, or null if the world is not a registered planet
     */
    @Nullable
    Planet getPlanet(World world);

    /**
     * Gets the planet data for the given dimension ID, or null if it's not a planet.
     *
     * @param dimensionId The dimension ID to get planet data for
     * @return The Planet data, or null if the dimension is not a registered planet
     */
    @Nullable
    Planet getPlanet(int dimensionId);

    /**
     * Returns true if the given world is a planet (has a surface).
     * This excludes the shared space station dimension.
     *
     * @param world The world to check
     * @return true if the world is a planet, false otherwise
     */
    boolean isPlanet(World world);

    /**
     * Returns true if the given dimension ID is a planet.
     *
     * @param dimensionId The dimension ID to check
     * @return true if the dimension is a planet, false otherwise
     */
    boolean isPlanet(int dimensionId);

    /**
     * Returns true if the given world is the shared space station dimension.
     *
     * @param world The world to check
     * @return true if the world is the shared space station, false otherwise
     */
    boolean isSpace(World world);

    /**
     * Returns true if the given dimension ID is the shared space station dimension.
     *
     * @param dimensionId The dimension ID to check
     * @return true if the dimension is the shared space station, false otherwise
     */
    boolean isSpace(int dimensionId);

    /**
     * Returns true if the given world is extraterrestrial (either a planet or space).
     * This is true for all Ad Astra dimensions except Earth (dimension 0).
     *
     * @param world The world to check
     * @return true if the world is extraterrestrial, false if it's Earth
     */
    boolean isExtraterrestrial(World world);

    /**
     * Returns true if the given dimension ID is extraterrestrial.
     *
     * @param dimensionId The dimension ID to check
     * @return true if the dimension is extraterrestrial, false if it's Earth
     */
    boolean isExtraterrestrial(int dimensionId);

    /**
     * Returns the solar power generation multiplier for the given world.
     * Higher values mean solar panels generate more energy in this dimension.
     * <p>
     * Typical values:
     * <ul>
     *   <li>Earth: 10 (baseline)</li>
     *   <li>Moon: 24 (closer to sun, no atmosphere)</li>
     *   <li>Mercury: 64 (very close to sun)</li>
     *   <li>Mars: 12 (farther from sun)</li>
     * </ul>
     *
     * @param world The world to check
     * @return The solar power multiplier
     */
    long getSolarPower(World world);

    /**
     * Returns the solar power generation multiplier for the given dimension ID.
     *
     * @param dimensionId The dimension ID to check
     * @return The solar power multiplier
     */
    long getSolarPower(int dimensionId);

    /**
     * Registers a custom planet for third-party mod integration.
     * This should be called during pre-initialization before dimensions are registered.
     * <p>
     * Example usage:
     * <pre>{@code
     * Planet customPlanet = Planet.builder(CUSTOM_DIM_ID)
     *     .oxygen(false)
     *     .temperature((short) -100)
     *     .gravity(0.5f)
     *     .solarPower(15)
     *     .tier(3)
     *     .build();
     * PlanetApi.API.registerPlanet(customPlanet);
     * }</pre>
     *
     * @param planet The planet to register
     */
    void registerPlanet(Planet planet);
}
