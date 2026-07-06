package earth.terrarium.adastra.common.world;

import earth.terrarium.adastra.common.registry.ModDimensions;
import net.minecraft.world.DimensionType;

public class WorldProviderPhobosOrbit extends AdAstraOrbitWorldProvider {

    @Override
    protected PlanetDimensionProperties getProperties() {
        return ModDimensions.PHOBOS_ORBIT_PROPERTIES;
    }

    @Override
    public DimensionType getDimensionType() {
        return ModDimensions.PHOBOS_ORBIT;
    }
}
