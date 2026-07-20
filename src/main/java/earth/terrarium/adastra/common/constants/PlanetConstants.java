package earth.terrarium.adastra.common.constants;

import earth.terrarium.adastra.Reference;
import net.minecraft.util.ResourceLocation;

/**
 * Planet physical constants ported from Ad Astra 1.20.x.
 * Source: https://www.windows2universe.org/our_solar_system/planets_table.html
 */
public class PlanetConstants {

    public static final ResourceLocation SOLAR_SYSTEM = new ResourceLocation(Reference.MOD_ID, "solar_system");
    public static final ResourceLocation PROXIMA_CENTAURI = new ResourceLocation(Reference.MOD_ID, "proxima_centauri");

    // Earth
    public static final float EARTH_GRAVITY = 9.807f;
    public static final short EARTH_TEMPERATURE = 15;
    public static final short COMFY_EARTH_TEMPERATURE = 22;
    public static final int EARTH_SOLAR_POWER = 16;

    // Space
    public static final float SPACE_GRAVITY = 0.0f;
    public static final short SPACE_TEMPERATURE = -270;
    public static final int SPACE_SOLAR_POWER = 32;

    // Moon
    public static final float MOON_GRAVITY = 1.622f;
    public static final short MOON_TEMPERATURE = -173;
    public static final int MOON_SOLAR_POWER = 24;

    // Mars
    public static final float MARS_GRAVITY = 3.72076f;
    public static final short MARS_TEMPERATURE = -65;
    public static final int MARS_SOLAR_POWER = 12;

    // Venus
    public static final float VENUS_GRAVITY = 8.87f;
    public static final short VENUS_TEMPERATURE = 464;
    public static final int VENUS_SOLAR_POWER = 8;

    // Mercury
    public static final float MERCURY_GRAVITY = 3.7f;
    public static final short MERCURY_TEMPERATURE = 167;
    public static final int MERCURY_SOLAR_POWER = 64;

    // Glacio
    public static final float GLACIO_GRAVITY = 3.721f;
    public static final short GLACIO_TEMPERATURE = -20;
    public static final int GLACIO_SOLAR_POWER = 14;

    // Temperature thresholds
    public static final int FREEZE_TEMPERATURE = -20;
    public static final int MIN_LIVEABLE_TEMPERATURE = -50;
    public static final int MAX_LIVEABLE_TEMPERATURE = 70;

    // Physics
    public static final float SPACE_FRICTION = 0.999f;
    public static final float ZERO_GRAVITY_THRESHOLD = 0.05f;
}
