package earth.terrarium.adastra.common.registry;

public final class ModParticles {

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
        ACID_RAIN = 0;
        LARGE_FLAME = 1;
        LARGE_SMOKE = 2;
        OXYGEN_BUBBLE = 3;
        OXYGEN_VENT = 4;
        CRYO_FREEZE = 5;
        WIND = 6;
    }
}
