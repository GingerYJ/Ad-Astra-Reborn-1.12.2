package earth.terrarium.adastra.common.world;

import earth.terrarium.adastra.common.registry.ModDimensions;
import net.minecraft.world.DimensionType;

public class WorldProviderTauCetiF extends AdAstraWorldProvider {

    @Override
    protected PlanetDimensionProperties getProperties() {
        return ModDimensions.TAUCETI_F_PROPERTIES;
    }

    @Override
    public DimensionType getDimensionType() {
        return ModDimensions.TAUCETI_F;
    }
}
