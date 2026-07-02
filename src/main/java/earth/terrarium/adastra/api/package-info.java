/**
 * Public API for the Ad Astra mod (1.12.2 Edition).
 * <p>
 * This package provides stable interfaces for third-party mods to integrate with Ad Astra's
 * space exploration mechanics. The API is organized into several sub-packages:
 * <p>
 * <h2>Systems API ({@link earth.terrarium.adastra.api.systems})</h2>
 * <ul>
 *   <li>{@link earth.terrarium.adastra.api.systems.OxygenApi} - Query and modify oxygen availability</li>
 *   <li>{@link earth.terrarium.adastra.api.systems.GravityApi} - Query and modify gravity effects</li>
 *   <li>{@link earth.terrarium.adastra.api.systems.TemperatureApi} - Query and modify temperature</li>
 *   <li>{@link earth.terrarium.adastra.api.systems.PlanetData} - Efficient data packing for network sync</li>
 * </ul>
 * <p>
 * <h2>Planets API ({@link earth.terrarium.adastra.api.planets})</h2>
 * <ul>
 *   <li>{@link earth.terrarium.adastra.api.planets.PlanetApi} - Query planet information and register custom planets</li>
 *   <li>{@link earth.terrarium.adastra.api.planets.Planet} - Planet metadata including atmosphere and solar power</li>
 * </ul>
 * <p>
 * <h2>Events API ({@link earth.terrarium.adastra.api.events})</h2>
 * <ul>
 *   <li>{@link earth.terrarium.adastra.api.events.AdAstraEvents} - Hook into oxygen, gravity, temperature, and environmental systems</li>
 * </ul>
 * <p>
 * <h2>Recipes API ({@link earth.terrarium.adastra.api.recipes})</h2>
 * <ul>
 *   <li>{@link earth.terrarium.adastra.api.recipes.RecipeApi} - Register custom machine recipes</li>
 * </ul>
 * <p>
 * <h2>Usage Examples</h2>
 * <p>
 * <b>Check if a position has oxygen:</b>
 * <pre>{@code
 * boolean hasOxygen = OxygenApi.API.hasOxygen(world, pos);
 * }</pre>
 * <p>
 * <b>Get gravity for a dimension:</b>
 * <pre>{@code
 * float gravity = GravityApi.API.getGravity(world);
 * }</pre>
 * <p>
 * <b>Register a custom planet:</b>
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
 * <p>
 * <b>Listen to oxygen events:</b>
 * <pre>{@code
 * AdAstraEvents.OxygenTickEvent.register((world, entity) -> {
 *     // Custom logic
 *     return true; // Return false to cancel oxygen deprivation
 * });
 * }</pre>
 * <p>
 * <b>Register a custom machine recipe:</b>
 * <pre>{@code
 * RecipeApi.API.registerCompressingRecipe(
 *     "modid:custom_recipe",
 *     Collections.singletonList(new ItemStack(Items.DIAMOND)),
 *     Collections.singletonList(new ItemStack(ModItems.COMPRESSED_DIAMOND)),
 *     200,  // 10 seconds
 *     20    // 20 FE/tick
 * );
 * }</pre>
 * <p>
 * <h2>Compatibility Notes</h2>
 * <p>
 * This API is designed for Minecraft 1.12.2 with Forge. It uses:
 * <ul>
 *   <li>Java ServiceLoader for API implementation loading</li>
 *   <li>Forge's Capability system for entity data storage</li>
 *   <li>Forge Energy (FE) for power systems</li>
 *   <li>Standard Forge dimension registration</li>
 * </ul>
 * <p>
 * <h2>Stability</h2>
 * <p>
 * All public interfaces in this package are considered stable API.
 * Methods may be added in future versions, but existing methods will not
 * be removed or have their signatures changed without deprecation warnings
 * in at least one prior release.
 * <p>
 * Classes and methods marked with {@code @Deprecated} may be removed in
 * future major versions. Check the deprecation notice for recommended alternatives.
 *
 * @since 1.12.2
 * @version 1.0.0
 */
package earth.terrarium.adastra.api;
