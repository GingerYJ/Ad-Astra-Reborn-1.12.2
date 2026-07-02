package earth.terrarium.adastra.common.menus.configuration;

/**
 * Configuration for a slot in the menu.
 *
 * Ported from Ad-Astra 1.20.x to 1.12.2.
 * Note: 1.20.x uses record, but 1.12.2 uses regular class.
 */
public class SlotConfiguration implements MenuConfiguration {

    private final int index;
    private final int x;
    private final int y;
    private final int width;
    private final int height;

    /**
     * Create a slot configuration with default dimensions (16x16).
     */
    public SlotConfiguration(int index, int x, int y) {
        this(index, x, y, 16, 16);
    }

    /**
     * Create a slot configuration with custom dimensions.
     */
    public SlotConfiguration(int index, int x, int y, int width, int height) {
        this.index = index;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    @Override
    public int index() {
        return index;
    }

    @Override
    public ConfigurationType type() {
        return ConfigurationType.SLOT;
    }

    @Override
    public int x() {
        return x;
    }

    @Override
    public int y() {
        return y;
    }

    public int width() {
        return width;
    }

    public int height() {
        return height;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof SlotConfiguration)) return false;
        SlotConfiguration other = (SlotConfiguration) obj;
        return index == other.index && x == other.x && y == other.y
            && width == other.width && height == other.height;
    }

    @Override
    public int hashCode() {
        int result = index;
        result = 31 * result + x;
        result = 31 * result + y;
        result = 31 * result + width;
        result = 31 * result + height;
        return result;
    }

    @Override
    public String toString() {
        return "SlotConfiguration{index=" + index + ", x=" + x + ", y=" + y
            + ", width=" + width + ", height=" + height + "}";
    }
}
