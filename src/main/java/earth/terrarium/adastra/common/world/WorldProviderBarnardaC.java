package earth.terrarium.adastra.common.world;

import earth.terrarium.adastra.common.registry.ModDimensions;
import net.minecraft.world.DimensionType;

public class WorldProviderBarnardaC extends AdAstraWorldProvider {

    @Override
    protected PlanetDimensionProperties getProperties() {
        return ModDimensions.BARNARDA_C_PROPERTIES;
    }

    @Override
    public DimensionType getDimensionType() {
        return ModDimensions.BARNARDA_C;
    }
}
