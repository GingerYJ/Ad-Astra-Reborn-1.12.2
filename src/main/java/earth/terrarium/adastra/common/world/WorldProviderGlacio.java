package earth.terrarium.adastra.common.world;

import earth.terrarium.adastra.common.registry.ModDimensions;
import net.minecraft.world.DimensionType;

public class WorldProviderGlacio extends AdAstraWorldProvider {

    @Override
    protected PlanetDimensionProperties getProperties() {
        return ModDimensions.GLACIO_PROPERTIES;
    }

    @Override
    public DimensionType getDimensionType() {
        return ModDimensions.GLACIO;
    }
}
