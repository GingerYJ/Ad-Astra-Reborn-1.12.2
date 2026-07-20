package earth.terrarium.adastra.common.world;

import earth.terrarium.adastra.common.registry.ModDimensions;
import net.minecraft.world.DimensionType;

/** World provider for the single server-wide space station dimension. */
public class WorldProviderSpaceStation extends AdAstraSpaceStationWorldProvider {

    @Override
    protected PlanetDimensionProperties getProperties() {
        return ModDimensions.SPACE_STATION_PROPERTIES;
    }

    @Override
    public DimensionType getDimensionType() {
        return ModDimensions.SPACE_STATION;
    }
}
