package earth.terrarium.adastra.common.menus.configuration;

import earth.terrarium.adastra.common.container.AdAstraEnergyContainer;

/**
 * Configuration for energy display/management in the menu.
 *
 * Ported from Ad-Astra 1.20.x to 1.12.2.
 * Note: 1.20.x uses record with Botarium's EnergyContainer,
 * 1.12.2 uses regular class with Forge Energy (AdAstraEnergyContainer).
 */
public class EnergyConfiguration implements MenuConfiguration {

    private final int index;
    private final int x;
    private final int y;
    private final AdAstraEnergyContainer container;

    /**
     * Create an energy configuration.
     *
     * @param index The index of this energy container
     * @param x The X position in the GUI
     * @param y The Y position in the GUI
     * @param container The energy container to manage
     */
    public EnergyConfiguration(int index, int x, int y, AdAstraEnergyContainer container) {
        this.index = index;
        this.x = x;
        this.y = y;
        this.container = container;
    }

    @Override
    public int index() {
        return index;
    }

    @Override
    public ConfigurationType type() {
        return ConfigurationType.ENERGY;
    }

    @Override
    public int x() {
        return x;
    }

    @Override
    public int y() {
        return y;
    }

    /**
     * Get the energy container.
     */
    public AdAstraEnergyContainer container() {
        return container;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof EnergyConfiguration)) return false;
        EnergyConfiguration other = (EnergyConfiguration) obj;
        return index == other.index && x == other.x && y == other.y
            && (container == other.container || (container != null && container.equals(other.container)));
    }

    @Override
    public int hashCode() {
        int result = index;
        result = 31 * result + x;
        result = 31 * result + y;
        result = 31 * result + (container != null ? container.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "EnergyConfiguration{index=" + index + ", x=" + x + ", y=" + y + ", container=" + container + "}";
    }
}
