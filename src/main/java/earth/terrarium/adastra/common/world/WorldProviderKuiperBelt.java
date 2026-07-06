package earth.terrarium.adastra.common.world;

import earth.terrarium.adastra.common.registry.ModDimensions;
import net.minecraft.world.DimensionType;

public class WorldProviderKuiperBelt extends AdAstraWorldProvider {

    @Override
    protected PlanetDimensionProperties getProperties() {
        return ModDimensions.KUIPER_BELT_PROPERTIES;
    }

    @Override
    public DimensionType getDimensionType() {
        return ModDimensions.KUIPER_BELT;
    }
}
