package earth.terrarium.adastra.common.menus.configuration;

import earth.terrarium.adastra.common.container.AdAstraFluidTank;

/**
 * Configuration for fluid tank display/management in the menu.
 *
 * Ported from Ad-Astra 1.20.x to 1.12.2.
 * Note: 1.20.x uses record with Botarium's FluidContainer,
 * 1.12.2 uses regular class with Forge FluidTank (AdAstraFluidTank).
 */
public class FluidConfiguration implements MenuConfiguration {

    private final int index;
    private final int x;
    private final int y;
    private final AdAstraFluidTank container;
    private final int tank;

    /**
     * Create a fluid configuration.
     *
     * @param index The index of this fluid container
     * @param x The X position in the GUI
     * @param y The Y position in the GUI
     * @param container The fluid tank to manage
     * @param tank The tank index (for multi-tank systems)
     */
    public FluidConfiguration(int index, int x, int y, AdAstraFluidTank container, int tank) {
        this.index = index;
        this.x = x;
        this.y = y;
        this.container = container;
        this.tank = tank;
    }

    @Override
    public int index() {
        return index;
    }

    @Override
    public ConfigurationType type() {
        return ConfigurationType.FLUID;
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
     * Get the fluid container.
     */
    public AdAstraFluidTank container() {
        return container;
    }

    /**
     * Get the tank index.
     */
    public int tank() {
        return tank;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof FluidConfiguration)) return false;
        FluidConfiguration other = (FluidConfiguration) obj;
        return index == other.index && x == other.x && y == other.y && tank == other.tank
            && (container == other.container || (container != null && container.equals(other.container)));
    }

    @Override
    public int hashCode() {
        int result = index;
        result = 31 * result + x;
        result = 31 * result + y;
        result = 31 * result + tank;
        result = 31 * result + (container != null ? container.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "FluidConfiguration{index=" + index + ", x=" + x + ", y=" + y
            + ", tank=" + tank + ", container=" + container + "}";
    }
}
