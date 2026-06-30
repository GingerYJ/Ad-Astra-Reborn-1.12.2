package earth.terrarium.adastra.common.blocks;

import net.minecraft.util.IStringSerializable;

public enum AdAstraPipeConnection implements IStringSerializable {
    NONE("none"),
    NORMAL("normal"),
    INSERT("insert"),
    EXTRACT("extract");

    private final String name;

    AdAstraPipeConnection(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    public AdAstraPipeConnection next() {
        AdAstraPipeConnection[] values = values();
        return values[(ordinal() + 1) % values.length];
    }

    public AdAstraPipeConnection previous() {
        AdAstraPipeConnection[] values = values();
        return values[(ordinal() - 1 + values.length) % values.length];
    }

    public static AdAstraPipeConnection byOrdinal(int ordinal) {
        AdAstraPipeConnection[] values = values();
        return ordinal >= 0 && ordinal < values.length ? values[ordinal] : NORMAL;
    }
}
