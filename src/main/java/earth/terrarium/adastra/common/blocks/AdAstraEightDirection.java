package earth.terrarium.adastra.common.blocks;

import net.minecraft.util.IStringSerializable;

import java.util.Locale;

public enum AdAstraEightDirection implements IStringSerializable {
    NORTH,
    NORTH_EAST,
    EAST,
    SOUTH_EAST,
    SOUTH,
    SOUTH_WEST,
    WEST,
    NORTH_WEST;

    public static final AdAstraEightDirection[] VALUES = values();

    public static AdAstraEightDirection fromYaw(float yaw) {
        int index = Math.round(yaw * 8.0f / 360.0f) & 7;
        return VALUES[index];
    }

    @Override
    public String getName() {
        return name().toLowerCase(Locale.ROOT);
    }

    @Override
    public String toString() {
        return getName();
    }
}
