package earth.terrarium.adastra.common.menus.configuration;

/**
 * Base interface for menu configuration elements.
 * Supports slots, energy, and fluid configuration for side configuration system.
 *
 * Ported from Ad-Astra 1.20.x to 1.12.2.
 * Note: 1.20.x uses sealed interface, but 1.12.2 uses regular interface.
 */
public interface MenuConfiguration {

    /**
     * Get the index of this configuration element.
     * For slots: the slot index in the inventory.
     * For energy/fluid: the container index.
     */
    int index();

    /**
     * Get the configuration type.
     */
    ConfigurationType type();

    /**
     * Get the X position in the GUI.
     */
    int x();

    /**
     * Get the Y position in the GUI.
     */
    int y();
}
