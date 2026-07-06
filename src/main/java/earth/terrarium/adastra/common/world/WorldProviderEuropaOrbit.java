package earth.terrarium.adastra.common.world;

import earth.terrarium.adastra.common.registry.ModDimensions;
import net.minecraft.world.DimensionType;

public class WorldProviderEuropaOrbit extends AdAstraOrbitWorldProvider {

    @Override
    protected PlanetDimensionProperties getProperties() {
        return ModDimensions.EUROPA_ORBIT_PROPERTIES;
    }

    @Override
    public DimensionType getDimensionType() {
        return ModDimensions.EUROPA_ORBIT;
    }
}
