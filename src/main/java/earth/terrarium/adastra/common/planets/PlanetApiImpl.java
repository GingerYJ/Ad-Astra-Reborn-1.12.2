package earth.terrarium.adastra.common.planets;

import earth.terrarium.adastra.api.planets.Planet;
import earth.terrarium.adastra.api.planets.PlanetApi;
import earth.terrarium.adastra.common.config.ExternalDimensionConfig;
import earth.terrarium.adastra.common.constants.PlanetConstants;
import earth.terrarium.adastra.common.registry.ModDimensions;
import earth.terrarium.adastra.common.world.PlanetDimensionProperties;
import earth.terrarium.adastra.common.world.custom.CustomPlanetDefinition;
import earth.terrarium.adastra.common.world.custom.CustomPlanetRegistry;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class PlanetApiImpl implements PlanetApi {

    private static final ResourceLocation SOLAR_SYSTEM = PlanetConstants.SOLAR_SYSTEM;
    private static final Map<Integer, Planet> PLANETS = new HashMap<>();
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
    @Override public void registerPlanet(Planet planet) { if (planet != null) { ensureDefaultsRegistered(); PLANETS.put(planet.getDimensionId(), planet); } }

    public static Map<Integer, Planet> snapshotPlanets() { ensureDefaultsRegistered(); synchronized (PLANETS) { return new LinkedHashMap<>(PLANETS); } }
    public static void replacePlanets(Map<Integer, Planet> planets) { synchronized (PLANETS) { PLANETS.clear(); if (planets != null) PLANETS.putAll(planets); defaultsRegistered = true; } }

    private static void ensureDefaultsRegistered() {
        if (!defaultsRegistered) {
            synchronized (PLANETS) {
                if (!defaultsRegistered) { registerDefaultPlanets(); defaultsRegistered = true; }
            }
        }
    }

    private static void registerDefaultPlanets() {
        // Existing planets
        registerPlanet(ModDimensions.MOON_PROPERTIES, ModDimensions.MOON_ORBIT_ID, 1, SOLAR_SYSTEM);
        registerPlanet(ModDimensions.MARS_PROPERTIES, ModDimensions.MARS_ORBIT_ID, 2, SOLAR_SYSTEM);
        registerPlanet(ModDimensions.MERCURY_PROPERTIES, ModDimensions.MERCURY_ORBIT_ID, 3, SOLAR_SYSTEM);
        registerPlanet(ModDimensions.VENUS_PROPERTIES, ModDimensions.VENUS_ORBIT_ID, 3, SOLAR_SYSTEM);
        registerPlanet(ModDimensions.GLACIO_PROPERTIES, ModDimensions.GLACIO_ORBIT_ID, 4, SOLAR_SYSTEM);

        // Built-in planet orbits
        registerOrbit(ModDimensions.EARTH_ORBIT_PROPERTIES, SOLAR_SYSTEM);
        registerOrbit(ModDimensions.MOON_ORBIT_PROPERTIES, SOLAR_SYSTEM);
        registerOrbit(ModDimensions.MARS_ORBIT_PROPERTIES, SOLAR_SYSTEM);
        registerOrbit(ModDimensions.MERCURY_ORBIT_PROPERTIES, SOLAR_SYSTEM);
        registerOrbit(ModDimensions.VENUS_ORBIT_PROPERTIES, SOLAR_SYSTEM);
        registerOrbit(ModDimensions.GLACIO_ORBIT_PROPERTIES, SOLAR_SYSTEM);

        for (CustomPlanetDefinition definition : CustomPlanetRegistry.getDefinitions()) {
            registerCustomPlanet(definition);
        }
        for (ExternalDimensionConfig.ExternalDimensionEntry entry : ExternalDimensionConfig.getEntries()) {
            registerExternalDimension(entry.toDimensionProperties());
        }
    }

    private static void registerPlanet(PlanetDimensionProperties properties, int orbitDimensionId, int tier,
                                       ResourceLocation solarSystem) {
        Planet planet = Planet.builder(properties.getDimensionId())
            .oxygen(properties.hasOxygen()).temperature(properties.getTemperature())
            .gravity(properties.getGravity()).solarPower(properties.getSolarPower())
            .solarSystem(solarSystem).orbitDimensionId(orbitDimensionId).tier(tier)
            .additionalLaunchDimensions(Collections.emptyList()).build();
        PLANETS.put(planet.getDimensionId(), planet);
    }

    private static void registerOrbit(PlanetDimensionProperties properties, ResourceLocation solarSystem) {
        Planet planet = Planet.builder(properties.getDimensionId())
            .oxygen(properties.hasOxygen()).temperature(properties.getTemperature())
            .gravity(properties.getGravity()).solarPower(properties.getSolarPower())
            .solarSystem(solarSystem).orbitDimensionId(null).tier(properties.getTier())
            .additionalLaunchDimensions(Collections.emptyList()).build();
        PLANETS.put(planet.getDimensionId(), planet);
    }

    private static void registerCustomPlanet(CustomPlanetDefinition definition) {
        PlanetDimensionProperties surface = definition.toDimensionProperties();
        Planet planet = Planet.builder(surface.getDimensionId())
            .oxygen(surface.hasOxygen()).temperature(surface.getTemperature())
            .gravity(surface.getGravity()).solarPower(surface.getSolarPower())
            .solarSystem(definition.getSolarSystem())
            .orbitDimensionId(definition.getOrbitDimensionId()).tier(surface.getTier())
            .additionalLaunchDimensions(Collections.emptyList()).build();
        PLANETS.put(planet.getDimensionId(), planet);

        PlanetDimensionProperties orbit = definition.toOrbitDimensionProperties();
        Planet orbitPlanet = Planet.builder(orbit.getDimensionId())
            .oxygen(orbit.hasOxygen()).temperature(orbit.getTemperature())
            .gravity(orbit.getGravity()).solarPower(orbit.getSolarPower())
            .solarSystem(definition.getSolarSystem())
            .orbitDimensionId(null).tier(orbit.getTier())
            .additionalLaunchDimensions(new ArrayList<>()).build();
        PLANETS.put(orbitPlanet.getDimensionId(), orbitPlanet);
    }

    private static void registerExternalDimension(PlanetDimensionProperties properties) {
        Planet planet = Planet.builder(properties.getDimensionId())
            .oxygen(properties.hasOxygen()).temperature(properties.getTemperature())
            .gravity(properties.getGravity()).solarPower(properties.getSolarPower())
            .solarSystem(SOLAR_SYSTEM).orbitDimensionId(Integer.MIN_VALUE).tier(properties.getTier())
            .additionalLaunchDimensions(Collections.emptyList()).build();
        PLANETS.put(planet.getDimensionId(), planet);
    }
}
