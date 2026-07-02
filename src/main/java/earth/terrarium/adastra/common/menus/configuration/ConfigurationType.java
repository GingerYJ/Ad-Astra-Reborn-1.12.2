package earth.terrarium.adastra.common.menus.configuration;

import java.util.Locale;

/**
 * Types of menu configurations for side configuration support.
 * Ported from Ad-Astra 1.20.x to 1.12.2.
 */
public enum ConfigurationType {
    SLOT,
    ENERGY,
    FLUID;

    /**
     * Get the translation key for this configuration type.
     * Used for GUI display.
     */
    public String getTranslationKey() {
        return "side_config.ad_astra.type." + name().toLowerCase(Locale.ROOT);
    }
}
