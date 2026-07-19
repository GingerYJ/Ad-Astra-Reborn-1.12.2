package earth.terrarium.adastra.client.render;

import earth.terrarium.adastra.Reference;
import earth.terrarium.adastra.common.registry.ModDimensions;
import earth.terrarium.adastra.common.world.custom.CustomPlanetDefinition;
import earth.terrarium.adastra.common.world.custom.CustomPlanetRegistry;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Registry for planet-specific sky renderers.
 * Ported from 1.20.x AdAstraPlanetRenderers with 1.12.2 adaptations.
 */
@SideOnly(Side.CLIENT)
public class PlanetSkyRenderers {

    // Celestial body textures
    private static final ResourceLocation EARTH_TEXTURE = new ResourceLocation(Reference.MOD_ID, "textures/environment/earth.png");
    private static final ResourceLocation MOON_TEXTURE = new ResourceLocation(Reference.MOD_ID, "textures/environment/moon.png");
    private static final ResourceLocation MARS_TEXTURE = new ResourceLocation(Reference.MOD_ID, "textures/environment/mars.png");
    private static final ResourceLocation VENUS_TEXTURE = new ResourceLocation(Reference.MOD_ID, "textures/environment/venus.png");
    private static final ResourceLocation GLACIO_TEXTURE = new ResourceLocation(Reference.MOD_ID, "textures/environment/glacio.png");
    private static final ResourceLocation MERCURY_TEXTURE = new ResourceLocation(Reference.MOD_ID, "textures/environment/mercury.png");
    private static final ResourceLocation SUN_TEXTURE = new ResourceLocation(Reference.MOD_ID, "textures/environment/sun.png");
    private static final ResourceLocation BLUE_SUN_TEXTURE = new ResourceLocation(Reference.MOD_ID, "textures/environment/blue_sun.png");
    private static final ResourceLocation VENUS_CLOUDS_TEXTURE = new ResourceLocation(Reference.MOD_ID, "textures/environment/venus_clouds.png");
    private static final ResourceLocation JUPITER_TEXTURE = new ResourceLocation(Reference.MOD_ID, "textures/environment/jupiter.png");
    private static final ResourceLocation SATURN_TEXTURE = new ResourceLocation(Reference.MOD_ID, "textures/environment/saturn.png");
    private static final ResourceLocation JUPITER_CLOUDS_TEXTURE = new ResourceLocation(Reference.MOD_ID, "textures/environment/jupiter_clouds.png");
    private static final ResourceLocation SATURN_CLOUDS_TEXTURE = new ResourceLocation(Reference.MOD_ID, "textures/environment/saturn_clouds.png");

    public static void registerSkyRenderers(World world) {
        int dimension = world.provider.getDimension();
        if (dimension == ModDimensions.MOON_ID) {
            world.provider.setSkyRenderer(createMoonSkyRenderer());
        } else if (dimension == ModDimensions.MARS_ID) {
            world.provider.setSkyRenderer(createMarsSkyRenderer());
        } else if (dimension == ModDimensions.VENUS_ID) {
            world.provider.setSkyRenderer(createVenusSkyRenderer());
            world.provider.setCloudRenderer(createVenusCloudRenderer());
        } else if (dimension == ModDimensions.MERCURY_ID) {
            world.provider.setSkyRenderer(createMercurySkyRenderer());
        } else if (dimension == ModDimensions.GLACIO_ID) {
            world.provider.setSkyRenderer(createGlacioSkyRenderer());
        } else if (dimension == ModDimensions.EARTH_ORBIT_ID) {
            world.provider.setSkyRenderer(createEarthOrbitSkyRenderer());
        } else if (dimension == ModDimensions.MOON_ORBIT_ID) {
            world.provider.setSkyRenderer(createMoonOrbitSkyRenderer());
        } else if (dimension == ModDimensions.MARS_ORBIT_ID) {
            world.provider.setSkyRenderer(createMarsOrbitSkyRenderer());
        } else if (dimension == ModDimensions.MERCURY_ORBIT_ID) {
            world.provider.setSkyRenderer(createMercuryOrbitSkyRenderer());
        } else if (dimension == ModDimensions.VENUS_ORBIT_ID) {
            world.provider.setSkyRenderer(createVenusOrbitSkyRenderer());
        } else if (dimension == ModDimensions.GLACIO_ORBIT_ID) {
            world.provider.setSkyRenderer(createGlacioOrbitSkyRenderer());
        } else {
            CustomPlanetDefinition planet = CustomPlanetRegistry.getByDimensionId(dimension);
            if (planet != null) {
                world.provider.setSkyRenderer(createCustomPlanetSkyRenderer(planet));
                registerCustomCloudRenderer(world, planet);
                return;
            }
            CustomPlanetDefinition orbitPlanet = CustomPlanetRegistry.getByOrbitDimensionId(dimension);
            if (orbitPlanet != null) {
                world.provider.setSkyRenderer(createCustomOrbitSkyRenderer(orbitPlanet));
            }
        }
    }

    /**
     * Earth orbit: the station should clearly sit above Earth.
     */
    private static PlanetSkyRenderer createEarthOrbitSkyRenderer() {
        return new PlanetSkyRenderer()
            .addCelestialBody(EARTH_TEXTURE, 38.0f,
                0, 0, 0,
                55, 0, 8,
                false, true)
            .addCelestialBody(MOON_TEXTURE, 8.0f,
                0, 0, 0,
                -35, 0, 110,
                true, true)
            .addCelestialBody(SUN_TEXTURE, 14.0f,
                0, 0, 0,
                0, 0, 230,
                true, true);
    }

    /**
     * Moon orbit: close moon, distant Earth, and the Sun.
     */
    private static PlanetSkyRenderer createMoonOrbitSkyRenderer() {
        return new PlanetSkyRenderer()
            .addCelestialBody(MOON_TEXTURE, 34.0f,
                0, 0, 0,
                55, 0, 12,
                false, true)
            .addCelestialBody(EARTH_TEXTURE, 18.0f,
                0, 0, 0,
                -35, 0, 145,
                true, true)
            .addCelestialBody(SUN_TEXTURE, 13.0f,
                0, 0, 0,
                0, 0, 250,
                true, true);
    }

    /**
     * Mars orbit: Mars dominates the view.
     */
    private static PlanetSkyRenderer createMarsOrbitSkyRenderer() {
        return new PlanetSkyRenderer(false, 0.02f, 0.0f, 0.0f)
            .addCelestialBody(MARS_TEXTURE, 36.0f,
                0, 0, 0,
                55, 0, 12,
                false, true)
            .addCelestialBody(SUN_TEXTURE, 12.0f,
                0, 0, 0,
                0, 0, 245,
                true, true);
    }

    private static PlanetSkyRenderer createMercuryOrbitSkyRenderer() {
        return new PlanetSkyRenderer()
            .addCelestialBody(MERCURY_TEXTURE, 34.0f,
                0, 0, 0,
                55, 0, 12,
                false, true)
            .addCelestialBody(SUN_TEXTURE, 28.0f,
                0, 0, 0,
                -10, 0, 210,
                true, true);
    }

    private static PlanetSkyRenderer createVenusOrbitSkyRenderer() {
        return new PlanetSkyRenderer(false, 0.03f, 0.02f, 0.0f)
            .addCelestialBody(VENUS_TEXTURE, 36.0f,
                0, 0, 0,
                55, 0, 12,
                false, true)
            .addCelestialBody(SUN_TEXTURE, 16.0f,
                0, 0, 0,
                -10, 0, 220,
                true, true);
    }

    private static PlanetSkyRenderer createGlacioOrbitSkyRenderer() {
        return new PlanetSkyRenderer(false, 0.0f, 0.015f, 0.04f)
            .addCelestialBody(GLACIO_TEXTURE, 36.0f,
                0, 0, 0,
                55, 0, 12,
                false, true)
            .addCelestialBody(BLUE_SUN_TEXTURE, 12.0f,
                0, 0, 0,
                -20, 0, 230,
                true, true);
    }

    private static PlanetSkyRenderer createCustomPlanetSkyRenderer(CustomPlanetDefinition definition) {
        Vec3d skyColor = definition.getSkyColor();
        PlanetSkyRenderer renderer = new PlanetSkyRenderer(false, tint(skyColor.x), tint(skyColor.y), tint(skyColor.z));
        String planet = definition.getPlanetName();
        if ("proxima_centauri_b".equals(planet)) {
            addEnvironmentBody(renderer, "sun", 12.0f, 0, 0, 0, true, true);
            addEnvironmentBody(renderer, "glacio", 50.0f, 60, 0, 5, false, false);
        } else if ("ceres".equals(planet)) {
            addEnvironmentBody(renderer, "sun", 4.0f, 0, 0, 0, true, false);
            addEnvironmentBody(renderer, "mars", 0.32f, -40, 0, 160, true, true);
        } else if ("eris".equals(planet)) {
            addEnvironmentBody(renderer, "sun", 0.15f, 0, 0, 0, true, false);
            addEnvironmentBody(renderer, "dysnomia", 1.2f, 20, 20, 180, true, true);
        } else if ("gonggong".equals(planet)) {
            addEnvironmentBody(renderer, "sun", 0.16f, 0, 0, 0, true, false);
            addEnvironmentBody(renderer, "xiangliu", 0.2f, 20, 20, 180, true, true);
        } else if ("haumea".equals(planet)) {
            addEnvironmentBody(renderer, "sun", 0.19f, 0, 0, 0, true, false);
            addEnvironmentBody(renderer, "hiiaka", 0.62f, 0, 10, 180, true, true);
            addEnvironmentBody(renderer, "namaka", 0.34f, 0, -10, 180, true, true);
        } else if ("jupiter".equals(planet)) {
            addEnvironmentBody(renderer, "sun", 2.2f, 0, 0, 0, true, false);
            addEnvironmentBody(renderer, "io", 3.0f, 20, 20, 180, true, true);
            addEnvironmentBody(renderer, "europa", 2.5f, 20, 50, 180, true, true);
            addEnvironmentBody(renderer, "ganymede", 4.2f, 20, -20, 150, true, true);
            addEnvironmentBody(renderer, "callisto", 3.8f, 0, 20, 210, true, true);
            addEnvironmentBody(renderer, "mars", 0.2f, -40, 0, 160, true, true);
            addEnvironmentBody(renderer, "ceres", 0.32f, -50, 0, 160, true, true);
        } else if ("makemake".equals(planet)) {
            addEnvironmentBody(renderer, "sun", 0.175f, 0, 0, 0, true, false);
            addEnvironmentBody(renderer, "mk2", 0.4f, 20, 20, 180, true, true);
        } else if ("neptune".equals(planet)) {
            addEnvironmentBody(renderer, "sun", 0.33f, 0, 0, 0, true, false);
            addEnvironmentBody(renderer, "triton", 2.17f, 20, 20, 180, true, true);
        } else if ("orcus".equals(planet)) {
            addEnvironmentBody(renderer, "sun", 0.23f, 0, 0, 0, true, false);
            addEnvironmentBody(renderer, "vanth", 0.36f, 20, 20, 180, true, true);
        } else if ("pluto".equals(planet)) {
            addEnvironmentBody(renderer, "sun", 0.21f, 0, 0, 0, true, false);
            addEnvironmentBody(renderer, "charon", 1.5f, 20, 20, 180, true, true);
            addEnvironmentBody(renderer, "nix", 0.08f, 20, 10, 180, true, true);
            addEnvironmentBody(renderer, "hydra", 0.1f, 20, 30, 180, true, true);
        } else if ("quaoar".equals(planet)) {
            addEnvironmentBody(renderer, "sun", 0.18f, 0, 0, 0, true, false);
            addEnvironmentBody(renderer, "weywot", 0.33f, 20, 20, 180, true, true);
        } else if ("saturn".equals(planet)) {
            addEnvironmentBody(renderer, "sun", 1.2f, 0, 0, 0, true, false);
            addEnvironmentBody(renderer, "titan", 4.1f, 20, 20, 180, true, true);
            addEnvironmentBody(renderer, "enceladus", 0.4f, 0, 20, 180, true, true);
            addEnvironmentBody(renderer, "mimas", 0.32f, 0, 30, 180, true, true);
            addEnvironmentBody(renderer, "dione", 0.9f, 0, 50, 180, true, true);
            addEnvironmentBody(renderer, "iapetus", 0.74f, 20, -20, 180, true, true);
            addEnvironmentBody(renderer, "tethys", 0.86f, 0, -20, 180, true, true);
            addEnvironmentBody(renderer, "rhea", 1.8f, 40, -20, 180, true, true);
            addEnvironmentBody(renderer, "jupiter", 0.09f, -40, 0, 160, true, true);
        } else if ("sedna".equals(planet)) {
            addEnvironmentBody(renderer, "sun", 0.012f, 0, 0, 0, true, false);
        } else if ("uranus".equals(planet)) {
            addEnvironmentBody(renderer, "sun", 0.6f, 0, 0, 0, true, false);
            addEnvironmentBody(renderer, "titania", 1.25f, 20, 20, 180, true, true);
            addEnvironmentBody(renderer, "oberon", 1.2f, 20, 40, 180, true, true);
            addEnvironmentBody(renderer, "miranda", 0.375f, 20, 30, 180, true, true);
            addEnvironmentBody(renderer, "ariel", 0.932f, 10, 30, 180, true, true);
            addEnvironmentBody(renderer, "umbriel", 0.96f, 30, 30, 180, true, true);
        } else {
            addEnvironmentBody(renderer, "sun", definition.hasOxygen() ? 12.0f : 16.0f,
                0, 0, 0, true, true);
        }
        return renderer;
    }

    private static PlanetSkyRenderer createCustomOrbitSkyRenderer(CustomPlanetDefinition definition) {
        Vec3d skyColor = definition.getSkyColor();
        PlanetSkyRenderer renderer = new PlanetSkyRenderer(false,
            tint(skyColor.x) * 0.5f, tint(skyColor.y) * 0.5f, tint(skyColor.z) * 0.5f);
        String planet = definition.getPlanetName();
        float sunScale = "proxima_centauri_b".equals(planet) ? 10.0f
            : "ceres".equals(planet) ? 4.0f
            : "eris".equals(planet) ? 0.15f
            : "gonggong".equals(planet) ? 0.16f
            : "haumea".equals(planet) ? 0.19f
            : "jupiter".equals(planet) ? 2.2f
            : "makemake".equals(planet) ? 0.175f
            : "neptune".equals(planet) ? 0.33f
            : "orcus".equals(planet) ? 0.23f
            : "pluto".equals(planet) ? 0.21f
            : "quaoar".equals(planet) ? 0.18f
            : "saturn".equals(planet) ? 1.2f
            : "sedna".equals(planet) ? 0.012f
            : "uranus".equals(planet) ? 0.6f : 14.0f;
        addEnvironmentBody(renderer, "sun", sunScale, 0, 0, 0, true, "proxima_centauri_b".equals(planet));
        float planetScale = "saturn".equals(planet) ? 150.0f : "uranus".equals(planet) ? 125.0f : 80.0f;
        addEnvironmentBody(renderer, planet, planetScale, 180, 0, 0, false, false);
        return renderer;
    }

    private static void addEnvironmentBody(PlanetSkyRenderer renderer, String textureName, float scale,
                                            float globalRotX, float globalRotY, float globalRotZ,
                                            boolean rotateWithTime, boolean blend) {
        ResourceLocation texture = "sun".equals(textureName) ? SUN_TEXTURE
            : new ResourceLocation(Reference.MOD_ID, "textures/environment/" + textureName + ".png");
        renderer.addCelestialBody(texture, scale,
            0, 0, 0, globalRotX, globalRotY, globalRotZ, rotateWithTime, blend);
    }

    private static void registerCustomCloudRenderer(World world, CustomPlanetDefinition definition) {
        if ("jupiter".equals(definition.getPlanetName())) {
            world.provider.setCloudRenderer(new PlanetCloudRenderer(
                JUPITER_CLOUDS_TEXTURE, 192.0F, 160.0F, 1.0F, 1.0F, 1.0F, 0.85F));
        } else if ("saturn".equals(definition.getPlanetName())) {
            world.provider.setCloudRenderer(new PlanetCloudRenderer(
                SATURN_CLOUDS_TEXTURE, 192.0F, 160.0F, 1.0F, 1.0F, 1.0F, 0.8F));
        }
    }

    private static float tint(double value) {
        return (float) Math.max(0.0D, Math.min(0.12D, value * 0.12D));
    }

    /**
     * Moon sky: Earth prominently visible, Sun far away
     */
    private static PlanetSkyRenderer createMoonSkyRenderer() {
        return new PlanetSkyRenderer()
            // Earth - large and visible
            .addCelestialBody(EARTH_TEXTURE, 20.0f,
                0, 0, 0,
                0, 0, 0,
                true, true)
            // Sun - smaller, rotates with time
            .addCelestialBody(SUN_TEXTURE, 15.0f,
                0, 0, 0,
                0, 0, 0,
                true, true);
    }

    /**
     * Mars sky: Slightly reddish tint.
     */
    private static PlanetSkyRenderer createMarsSkyRenderer() {
        return new PlanetSkyRenderer(false, 0.05f, 0.02f, 0.0f)
            // Sun - distant
            .addCelestialBody(SUN_TEXTURE, 12.0f,
                0, 0, 0,
                0, 0, 0,
                true, true)
            ;
    }

    /**
     * Venus sky: Thick atmosphere, yellowish tint, no clear celestial bodies
     */
    private static PlanetSkyRenderer createVenusSkyRenderer() {
        return new PlanetSkyRenderer(false, 0.15f, 0.12f, 0.05f)
            // Sun - very faint through thick atmosphere
            .addCelestialBody(SUN_TEXTURE, 18.0f,
                0, 0, 0,
                0, 0, 0,
                true, true);
    }

    private static PlanetCloudRenderer createVenusCloudRenderer() {
        return new PlanetCloudRenderer(VENUS_CLOUDS_TEXTURE, 192.0f, 160.0f, 0.86f, 0.76f, 0.42f, 0.42f);
    }

    /**
     * Mercury sky: Very close sun, appears large and bright
     */
    private static PlanetSkyRenderer createMercurySkyRenderer() {
        return new PlanetSkyRenderer()
            // Sun - very large and prominent
            .addCelestialBody(SUN_TEXTURE, 25.0f,
                0, 0, 0,
                0, 0, 0,
                true, true);
    }

    /**
     * Glacio sky: Ice planet, distant sun, blue-white tint
     */
    private static PlanetSkyRenderer createGlacioSkyRenderer() {
        return new PlanetSkyRenderer(false, 0.02f, 0.05f, 0.1f)
            // Sun - very distant
            .addCelestialBody(SUN_TEXTURE, 10.0f,
                0, 0, 0,
                0, 0, 0,
                true, true);
    }
}
