package earth.terrarium.adastra.common.world;

import earth.terrarium.adastra.common.registry.ModDimensions;
import net.minecraft.world.DimensionType;

public class WorldProviderPhobos extends AdAstraWorldProvider {

    @Override
    protected PlanetDimensionProperties getProperties() {
        return ModDimensions.PHOBOS_PROPERTIES;
    }

    @Override
    public DimensionType getDimensionType() {
        return ModDimensions.PHOBOS;
    }
}
