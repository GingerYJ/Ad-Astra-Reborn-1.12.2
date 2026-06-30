package earth.terrarium.adastra.common.blocks;

import net.minecraft.util.IStringSerializable;

public enum AdAstraSlidingDoorPart implements IStringSerializable {
    TOP_LEFT("top_left", -1, 2),
    TOP("top", 0, 2),
    TOP_RIGHT("top_right", 1, 2),
    LEFT("left", -1, 1),
    CENTER("center", 0, 1),
    RIGHT("right", 1, 1),
    BOTTOM_LEFT("bottom_left", -1, 0),
    BOTTOM("bottom", 0, 0),
    BOTTOM_RIGHT("bottom_right", 1, 0);

    private final String name;
    private final int xOffset;
    private final int yOffset;

    AdAstraSlidingDoorPart(String name, int xOffset, int yOffset) {
        this.name = name;
        this.xOffset = xOffset;
        this.yOffset = yOffset;
    }

    @Override
    public String getName() {
        return name;
    }

    public int getXOffset() {
        return xOffset;
    }

    public int getYOffset() {
        return yOffset;
    }

    public boolean isController() {
        return this == BOTTOM;
    }

    public static AdAstraSlidingDoorPart byName(String name) {
        for (AdAstraSlidingDoorPart part : values()) {
            if (part.name.equals(name)) {
                return part;
            }
        }
        return BOTTOM;
    }
}
