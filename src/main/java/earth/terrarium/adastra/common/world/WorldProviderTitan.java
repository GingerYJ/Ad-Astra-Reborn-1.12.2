package earth.terrarium.adastra.common.world;

import earth.terrarium.adastra.common.registry.ModDimensions;
import net.minecraft.world.DimensionType;

public class WorldProviderTitan extends AdAstraWorldProvider {

    @Override
    protected PlanetDimensionProperties getProperties() {
        return ModDimensions.TITAN_PROPERTIES;
    }

    @Override
    public DimensionType getDimensionType() {
        return ModDimensions.TITAN;
    }
}
