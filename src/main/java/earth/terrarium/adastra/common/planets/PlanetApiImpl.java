package earth.terrarium.adastra.common.planets;

import earth.terrarium.adastra.Reference;
import earth.terrarium.adastra.api.planets.Planet;
import earth.terrarium.adastra.api.planets.PlanetApi;
import earth.terrarium.adastra.common.config.ExternalDimensionConfig;
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

    private static final ResourceLocation SOLAR_SYSTEM = new ResourceLocation(Reference.MOD_ID, "solar_system");
    private static final ResourceLocation BARNARDS_SYSTEM = new ResourceLocation(Reference.MOD_ID, "barnards_system");
    private static final ResourceLocation TAUCETI_SYSTEM = new ResourceLocation(Reference.MOD_ID, "tauceti_system");
    private static final ResourceLocation PROXIMA_SYSTEM = new ResourceLocation(Reference.MOD_ID, "proxima_system");
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
        registerPlanet(ModDimensions.MOON_PROPERTIES, ModDimensions.MOON_ORBIT_ID, 1);
        registerPlanet(ModDimensions.MARS_PROPERTIES, ModDimensions.MARS_ORBIT_ID, 2);
        registerPlanet(ModDimensions.MERCURY_PROPERTIES, ModDimensions.MERCURY_ORBIT_ID, 3);
        registerPlanet(ModDimensions.VENUS_PROPERTIES, ModDimensions.VENUS_ORBIT_ID, 3);
        registerPlanet(ModDimensions.GLACIO_PROPERTIES, ModDimensions.GLACIO_ORBIT_ID, 4);

        // New Solar System dwarf planets
        registerPlanet(ModDimensions.CERES_PROPERTIES, ModDimensions.CERES_ORBIT_ID, 3);
        registerPlanet(ModDimensions.PLUTO_PROPERTIES, ModDimensions.PLUTO_ORBIT_ID, 4);
        registerPlanet(ModDimensions.HAUMEA_PROPERTIES, ModDimensions.HAUMEA_ORBIT_ID, 4);
        registerPlanet(ModDimensions.KUIPER_BELT_PROPERTIES, ModDimensions.KUIPER_BELT_PROPERTIES.getDimensionId(), 5);

        // Jupiter moons
        registerPlanet(ModDimensions.IO_PROPERTIES, ModDimensions.IO_ORBIT_ID, 4);
        registerPlanet(ModDimensions.EUROPA_PROPERTIES, ModDimensions.EUROPA_ORBIT_ID, 4);
        registerPlanet(ModDimensions.GANYMEDE_PROPERTIES, ModDimensions.GANYMEDE_ORBIT_ID, 4);
        registerPlanet(ModDimensions.CALLISTO_PROPERTIES, ModDimensions.CALLISTO_ORBIT_ID, 4);

        // Saturn moons
        registerPlanet(ModDimensions.ENCELADUS_PROPERTIES, ModDimensions.ENCELADUS_ORBIT_ID, 5);
        registerPlanet(ModDimensions.TITAN_PROPERTIES, ModDimensions.TITAN_ORBIT_ID, 5);

        // Other moons
        registerPlanet(ModDimensions.MIRANDA_PROPERTIES, ModDimensions.MIRANDA_ORBIT_ID, 5);
        registerPlanet(ModDimensions.TRITON_PROPERTIES, ModDimensions.TRITON_ORBIT_ID, 5);
        registerPlanet(ModDimensions.PHOBOS_PROPERTIES, ModDimensions.PHOBOS_ORBIT_ID, 3);

        // Exoplanets
        registerPlanet(ModDimensions.BARNARDA_C_PROPERTIES, ModDimensions.BARNARDA_C_ORBIT_ID, 6);
        registerPlanet(ModDimensions.BARNARDA_C1_PROPERTIES, ModDimensions.BARNARDA_C1_ORBIT_ID, 6);
        registerPlanet(ModDimensions.TAUCETI_F_PROPERTIES, ModDimensions.TAUCETI_F_ORBIT_ID, 6);
        registerPlanet(ModDimensions.PROXIMA_B_PROPERTIES, ModDimensions.PROXIMA_B_ORBIT_ID, 6);

        // All orbits
        registerOrbit(ModDimensions.EARTH_ORBIT_PROPERTIES);
        registerOrbit(ModDimensions.MOON_ORBIT_PROPERTIES);
        registerOrbit(ModDimensions.MARS_ORBIT_PROPERTIES);
        registerOrbit(ModDimensions.MERCURY_ORBIT_PROPERTIES);
        registerOrbit(ModDimensions.VENUS_ORBIT_PROPERTIES);
        registerOrbit(ModDimensions.GLACIO_ORBIT_PROPERTIES);
        registerOrbit(ModDimensions.CERES_ORBIT_PROPERTIES);
        registerOrbit(ModDimensions.PLUTO_ORBIT_PROPERTIES);
        registerOrbit(ModDimensions.HAUMEA_ORBIT_PROPERTIES);
        registerOrbit(ModDimensions.IO_ORBIT_PROPERTIES);
        registerOrbit(ModDimensions.EUROPA_ORBIT_PROPERTIES);
        registerOrbit(ModDimensions.GANYMEDE_ORBIT_PROPERTIES);
        registerOrbit(ModDimensions.CALLISTO_ORBIT_PROPERTIES);
        registerOrbit(ModDimensions.ENCELADUS_ORBIT_PROPERTIES);
        registerOrbit(ModDimensions.TITAN_ORBIT_PROPERTIES);
        registerOrbit(ModDimensions.MIRANDA_ORBIT_PROPERTIES);
        registerOrbit(ModDimensions.TRITON_ORBIT_PROPERTIES);
        registerOrbit(ModDimensions.PHOBOS_ORBIT_PROPERTIES);
        registerOrbit(ModDimensions.JUPITER_ORBIT_PROPERTIES);
        registerOrbit(ModDimensions.BARNARDA_C_ORBIT_PROPERTIES);
        registerOrbit(ModDimensions.BARNARDA_C1_ORBIT_PROPERTIES);
        registerOrbit(ModDimensions.TAUCETI_F_ORBIT_PROPERTIES);
        registerOrbit(ModDimensions.PROXIMA_B_ORBIT_PROPERTIES);

        for (CustomPlanetDefinition definition : CustomPlanetRegistry.getDefinitions()) {
            registerCustomPlanet(definition);
        }
        for (ExternalDimensionConfig.ExternalDimensionEntry entry : ExternalDimensionConfig.getEntries()) {
            registerExternalDimension(entry.toDimensionProperties());
        }
    }

    private static void registerPlanet(PlanetDimensionProperties properties, int orbitDimensionId, int tier) {
        Planet planet = Planet.builder(properties.getDimensionId())
            .oxygen(properties.hasOxygen()).temperature(properties.getTemperature())
            .gravity(properties.getGravity()).solarPower(properties.getSolarPower())
            .solarSystem(SOLAR_SYSTEM).orbitDimensionId(orbitDimensionId).tier(tier)
            .additionalLaunchDimensions(Collections.emptyList()).build();
        PLANETS.put(planet.getDimensionId(), planet);
    }

    private static void registerOrbit(PlanetDimensionProperties properties) {
        Planet planet = Planet.builder(properties.getDimensionId())
            .oxygen(properties.hasOxygen()).temperature(properties.getTemperature())
            .gravity(properties.getGravity()).solarPower(properties.getSolarPower())
            .solarSystem(SOLAR_SYSTEM).orbitDimensionId(null).tier(properties.getTier())
            .additionalLaunchDimensions(Collections.emptyList()).build();
        PLANETS.put(planet.getDimensionId(), planet);
    }

    private static void registerCustomPlanet(CustomPlanetDefinition definition) {
        PlanetDimensionProperties surface = definition.toDimensionProperties();
        Planet planet = Planet.builder(surface.getDimensionId())
            .oxygen(surface.hasOxygen()).temperature(surface.getTemperature())
            .gravity(surface.getGravity()).solarPower(surface.getSolarPower())
            .solarSystem(new ResourceLocation(definition.getId().getNamespace(), "solar_system"))
            .orbitDimensionId(definition.getOrbitDimensionId()).tier(surface.getTier())
            .additionalLaunchDimensions(Collections.emptyList()).build();
        PLANETS.put(planet.getDimensionId(), planet);

        PlanetDimensionProperties orbit = definition.toOrbitDimensionProperties();
        Planet orbitPlanet = Planet.builder(orbit.getDimensionId())
            .oxygen(orbit.hasOxygen()).temperature(orbit.getTemperature())
            .gravity(orbit.getGravity()).solarPower(orbit.getSolarPower())
            .solarSystem(new ResourceLocation(definition.getId().getNamespace(), "solar_system"))
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
