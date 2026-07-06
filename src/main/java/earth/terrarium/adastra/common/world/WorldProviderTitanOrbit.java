package earth.terrarium.adastra.common.world;

import earth.terrarium.adastra.common.registry.ModDimensions;
import net.minecraft.world.DimensionType;

public class WorldProviderTitanOrbit extends AdAstraOrbitWorldProvider {

    @Override
    protected PlanetDimensionProperties getProperties() {
        return ModDimensions.TITAN_ORBIT_PROPERTIES;
    }

    @Override
    public DimensionType getDimensionType() {
        return ModDimensions.TITAN_ORBIT;
    }
}
