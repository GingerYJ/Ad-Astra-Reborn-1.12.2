package earth.terrarium.adastra.common.config;

import net.minecraftforge.common.config.Configuration;

import java.io.File;

public final class AdAstraConfig {

    private static Configuration configuration;

    public static boolean debugLogging;
    public static boolean enableAirVortexes;

    private AdAstraConfig() {
    }

    public static void init(File file) {
        configuration = new Configuration(file);
        sync();
    }

    public static void sync() {
        if (configuration == null) {
            return;
        }

        debugLogging = configuration.getBoolean(
            "debugLogging",
            Configuration.CATEGORY_GENERAL,
            false,
            "Enable extra logging while the 1.12.2 port is being rebuilt.");
        enableAirVortexes = configuration.getBoolean(
            "enableAirVortexes",
            Configuration.CATEGORY_GENERAL,
            true,
            "Spawn air vortex entities when an oxygen distributor is pushing oxygen over its maximum area.");

        if (configuration.hasChanged()) {
            configuration.save();
        }
    }
}
