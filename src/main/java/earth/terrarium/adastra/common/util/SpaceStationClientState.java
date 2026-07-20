package earth.terrarium.adastra.common.util;

import earth.terrarium.adastra.common.capability.SpaceStation;
import earth.terrarium.adastra.common.registry.ModDimensions;
import net.minecraft.util.math.BlockPos;

/** Client-side mirror of the server-wide space station state. */
public final class SpaceStationClientState {

    private static SpaceStation station;

    private SpaceStationClientState() {
    }

    public static void update(boolean constructed, String name, BlockPos position) {
        station = constructed && position != null
            ? new SpaceStation(name, ModDimensions.SPACE_STATION_ID, position)
            : null;
    }

    public static SpaceStation getStation() {
        return station;
    }

    public static boolean isConstructed() {
        return station != null;
    }
}
