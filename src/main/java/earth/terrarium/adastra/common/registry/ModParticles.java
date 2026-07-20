package earth.terrarium.adastra.common.registry;

public final class ModParticles {

    /**
     * EffectRenderer reserves the low particle ids for EnumParticleTypes.
     * Keep mod-only factories outside that range so vanilla particles such as
     * water bubbles and splashes cannot be replaced by our factories.
     */
    private static final int FIRST_CUSTOM_PARTICLE_ID = 128;

    private ModParticles() {
    }

    public static int ACID_RAIN;
    public static int LARGE_FLAME;
    public static int LARGE_SMOKE;
    public static int OXYGEN_BUBBLE;
    public static int OXYGEN_VENT;
    public static int CRYO_FREEZE;
    public static int WIND;

    public static void register() {
        ACID_RAIN = FIRST_CUSTOM_PARTICLE_ID;
        LARGE_FLAME = FIRST_CUSTOM_PARTICLE_ID + 1;
        LARGE_SMOKE = FIRST_CUSTOM_PARTICLE_ID + 2;
        OXYGEN_BUBBLE = FIRST_CUSTOM_PARTICLE_ID + 3;
        OXYGEN_VENT = FIRST_CUSTOM_PARTICLE_ID + 4;
        CRYO_FREEZE = FIRST_CUSTOM_PARTICLE_ID + 5;
        WIND = FIRST_CUSTOM_PARTICLE_ID + 6;
    }
}
