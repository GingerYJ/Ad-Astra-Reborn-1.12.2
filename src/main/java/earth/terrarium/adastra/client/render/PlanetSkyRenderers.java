package earth.terrarium.adastra.client.render;

import earth.terrarium.adastra.Reference;
import earth.terrarium.adastra.common.registry.ModDimensions;
import net.minecraft.util.ResourceLocation;
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
    private static final ResourceLocation PHOBOS_TEXTURE = new ResourceLocation(Reference.MOD_ID, "textures/environment/phobos.png");
    private static final ResourceLocation DEIMOS_TEXTURE = new ResourceLocation(Reference.MOD_ID, "textures/environment/deimos.png");
    private static final ResourceLocation SUN_TEXTURE = new ResourceLocation(Reference.MOD_ID, "textures/environment/sun.png");
    private static final ResourceLocation BLUE_SUN_TEXTURE = new ResourceLocation(Reference.MOD_ID, "textures/environment/blue_sun.png");
    private static final ResourceLocation VENUS_CLOUDS_TEXTURE = new ResourceLocation(Reference.MOD_ID, "textures/environment/venus_clouds.png");

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
     * Mars orbit: Mars dominates the view with its two small moons nearby.
     */
    private static PlanetSkyRenderer createMarsOrbitSkyRenderer() {
        return new PlanetSkyRenderer(false, 0.02f, 0.0f, 0.0f)
            .addCelestialBody(MARS_TEXTURE, 36.0f,
                0, 0, 0,
                55, 0, 12,
                false, true)
            .addCelestialBody(PHOBOS_TEXTURE, 6.0f,
                0, 0, 0,
                -45, 0, 105,
                true, true)
            .addCelestialBody(DEIMOS_TEXTURE, 4.0f,
                0, 0, 0,
                -20, 0, 155,
                true, true)
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
     * Mars sky: Slightly reddish tint, two moons (Phobos and Deimos)
     */
    private static PlanetSkyRenderer createMarsSkyRenderer() {
        return new PlanetSkyRenderer(false, 0.05f, 0.02f, 0.0f)
            // Sun - distant
            .addCelestialBody(SUN_TEXTURE, 12.0f,
                0, 0, 0,
                0, 0, 0,
                true, true)
            // Phobos - larger moon
            .addCelestialBody(MOON_TEXTURE, 4.0f,
                0, 0, 0,
                45, 0, 0,
                true, true)
            // Deimos - smaller moon
            .addCelestialBody(MOON_TEXTURE, 2.5f,
                0, 0, 0,
                -60, 0, 0,
                true, true);
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
