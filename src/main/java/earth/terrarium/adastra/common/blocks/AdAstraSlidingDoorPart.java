package earth.terrarium.adastra.common.blocks;

import net.minecraft.util.IStringSerializable;

public enum AdAstraSlidingDoorPart implements IStringSerializable {
    BOTTOM("bottom"),
    BOTTOM_LEFT("bottom_left"),
    BOTTOM_RIGHT("bottom_right"),
    CENTER("center"),
    LEFT("left"),
    RIGHT("right"),
    TOP("top"),
    TOP_LEFT("top_left"),
    TOP_RIGHT("top_right");

    private final String name;

    AdAstraSlidingDoorPart(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }
}
