package earth.terrarium.adastra.common.world;

import earth.terrarium.adastra.common.registry.ModDimensions;
import net.minecraft.world.DimensionType;

public class WorldProviderProximaB extends AdAstraWorldProvider {

    @Override
    protected PlanetDimensionProperties getProperties() {
        return ModDimensions.PROXIMA_B_PROPERTIES;
    }

    @Override
    public DimensionType getDimensionType() {
        return ModDimensions.PROXIMA_B;
    }
}
