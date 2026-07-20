package earth.terrarium.adastra.common.planets;

import earth.terrarium.adastra.api.planets.Planet;
import earth.terrarium.adastra.api.planets.PlanetApi;
import earth.terrarium.adastra.common.config.ExternalDimensionConfig;
import earth.terrarium.adastra.common.constants.PlanetConstants;
import earth.terrarium.adastra.common.registry.ModDimensions;
import earth.terrarium.adastra.common.world.PlanetDimensionProperties;
import earth.terrarium.adastra.common.world.custom.CustomPlanetDefinition;
import earth.terrarium.adastra.common.world.custom.CustomPlanetRegistry;
import earth.terrarium.adastra.common.world.custom.BuiltInPlanetRegistry;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class PlanetApiImpl implements PlanetApi {

    private static final ResourceLocation SOLAR_SYSTEM = PlanetConstants.SOLAR_SYSTEM;
    private static final Map<Integer, Planet> PLANETS = new LinkedHashMap<>();
    private static volatile boolean defaultsRegistered = false;

    @Override @Nullable public Planet getPlanet(World world) { return world == null ? null : getPlanet(world.provider.getDimension()); }
    @Override @Nullable public Planet getPlanet(int dimensionId) { ensureDefaultsRegistered(); return PLANETS.get(dimensionId); }
    @Override public boolean isPlanet(World world) { return world != null && isPlanet(world.provider.getDimension()); }
    @Override public boolean isPlanet(int dimensionId) { ensureDefaultsRegistered(); Planet p = PLANETS.get(dimensionId); return p != null && !p.isSpace(); }
    @Override public boolean isSpace(World world) { return world != null && isSpace(world.provider.getDimension()); }
    @Override public boolean isSpace(int dimensionId) { ensureDefaultsRegistered(); Planet p = PLANETS.get(dimensionId); return p != null && p.isSpace(); }
    @Override public boolean isExtraterrestrial(World world) { return world != null && isExtraterrestrial(world.provider.getDimension()); }
    @Override public boolean isExtraterrestrial(int dimensionId) { return dimensionId != 0 && (isPlanet(dimensionId) || isSpace(dimensionId)); }
    @Override public long getSolarPower(World world) { return world == null ? 10 : getSolarPower(world.provider.getDimension()); }
    @Override public long getSolarPower(int dimensionId) { ensureDefaultsRegistered(); Planet p = PLANETS.get(dimensionId); return p != null ? p.getSolarPower() : 10; }
    @Override public void registerPlanet(Planet planet) {
        if (planet == null) {
            return;
        }
        if (planet.isSpace()) {
            throw new IllegalArgumentException("Third-party registrations must describe surface planets; the space station is built in.");
        }
        ensureDefaultsRegistered();
        if (ModDimensions.isBuiltInDimension(planet.getDimensionId())
            || BuiltInPlanetRegistry.containsDimension(planet.getDimensionId())) {
            throw new IllegalArgumentException("Cannot replace built-in planet dimension " + planet.getDimensionId() + ".");
        }
        synchronized (PLANETS) {
            PLANETS.put(planet.getDimensionId(), planet);
        }
    }

    public static Map<Integer, Planet> snapshotPlanets() { ensureDefaultsRegistered(); synchronized (PLANETS) { return new LinkedHashMap<>(PLANETS); } }
    public static void replacePlanets(Map<Integer, Planet> planets) {
        ensureDefaultsRegistered();
        synchronized (PLANETS) {
            if (planets == null) {
                return;
            }
            PLANETS.entrySet().removeIf(entry -> !isBuiltInDimension(entry.getKey()));
            for (Map.Entry<Integer, Planet> entry : planets.entrySet()) {
                if (!isBuiltInDimension(entry.getKey())) {
                    PLANETS.put(entry.getKey(), entry.getValue());
                }
            }
        }
    }

    private static void ensureDefaultsRegistered() {
        if (!defaultsRegistered) {
            synchronized (PLANETS) {
                if (!defaultsRegistered) { registerDefaultPlanets(); defaultsRegistered = true; }
            }
        }
    }

    private static void registerDefaultPlanets() {
        // Existing planets
        registerPlanet(ModDimensions.MOON_PROPERTIES, 1, SOLAR_SYSTEM);
        registerPlanet(ModDimensions.MARS_PROPERTIES, 2, SOLAR_SYSTEM);
        registerPlanet(ModDimensions.MERCURY_PROPERTIES, 3, SOLAR_SYSTEM);
        registerPlanet(ModDimensions.VENUS_PROPERTIES, 3, SOLAR_SYSTEM);
        registerPlanet(ModDimensions.GLACIO_PROPERTIES, 4, SOLAR_SYSTEM);

        registerSpace(ModDimensions.SPACE_STATION_PROPERTIES, SOLAR_SYSTEM);

        for (CustomPlanetDefinition definition : BuiltInPlanetRegistry.getDefinitions()) {
            registerCustomPlanet(definition);
        }
        for (CustomPlanetDefinition definition : CustomPlanetRegistry.getDefinitions()) {
            registerCustomPlanet(definition);
        }
        for (ExternalDimensionConfig.ExternalDimensionEntry entry : ExternalDimensionConfig.getEntries()) {
            registerExternalDimension(entry.toDimensionProperties());
        }
    }

    private static void registerPlanet(PlanetDimensionProperties properties, int tier,
                                       ResourceLocation solarSystem) {
        Planet planet = Planet.builder(properties.getDimensionId())
            .oxygen(properties.hasOxygen()).temperature(properties.getTemperature())
            .gravity(properties.getGravity()).solarPower(properties.getSolarPower())
            .solarSystem(solarSystem).space(false).tier(tier)
            .additionalLaunchDimensions(Collections.emptyList()).build();
        PLANETS.put(planet.getDimensionId(), planet);
    }

    private static void registerSpace(PlanetDimensionProperties properties, ResourceLocation solarSystem) {
        Planet planet = Planet.builder(properties.getDimensionId())
            .oxygen(properties.hasOxygen()).temperature(properties.getTemperature())
            .gravity(properties.getGravity()).solarPower(properties.getSolarPower())
            .solarSystem(solarSystem).space(true).tier(properties.getTier())
            .additionalLaunchDimensions(Collections.emptyList()).build();
        PLANETS.put(planet.getDimensionId(), planet);
    }

    private static void registerCustomPlanet(CustomPlanetDefinition definition) {
        PlanetDimensionProperties surface = definition.toDimensionProperties();
        Planet planet = Planet.builder(surface.getDimensionId())
            .oxygen(surface.hasOxygen()).temperature(surface.getTemperature())
            .gravity(surface.getGravity()).solarPower(surface.getSolarPower())
            .solarSystem(definition.getSolarSystem())
            .space(false).tier(surface.getTier())
            .additionalLaunchDimensions(Collections.emptyList()).build();
        PLANETS.put(planet.getDimensionId(), planet);
    }

    private static void registerExternalDimension(PlanetDimensionProperties properties) {
        Planet planet = Planet.builder(properties.getDimensionId())
            .oxygen(properties.hasOxygen()).temperature(properties.getTemperature())
            .gravity(properties.getGravity()).solarPower(properties.getSolarPower())
            .solarSystem(SOLAR_SYSTEM).space(false).tier(properties.getTier())
            .additionalLaunchDimensions(Collections.emptyList()).build();
        PLANETS.put(planet.getDimensionId(), planet);
    }

    private static boolean isBuiltInDimension(int dimensionId) {
        return ModDimensions.isBuiltInDimension(dimensionId)
            || BuiltInPlanetRegistry.containsDimension(dimensionId);
    }
}
