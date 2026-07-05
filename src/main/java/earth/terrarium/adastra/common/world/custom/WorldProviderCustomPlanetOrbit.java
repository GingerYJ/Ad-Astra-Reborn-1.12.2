package earth.terrarium.adastra.common.world.custom;

import earth.terrarium.adastra.common.world.AdAstraOrbitWorldProvider;
import earth.terrarium.adastra.common.world.PlanetDimensionProperties;
import net.minecraft.world.DimensionType;

public class WorldProviderCustomPlanetOrbit extends AdAstraOrbitWorldProvider {

    @Override
    protected PlanetDimensionProperties getProperties() {
        CustomPlanetDefinition definition = getDefinition();
        return definition == null
            ? CustomPlanetDefinition.fallbackOrbitProperties(getDimension())
            : definition.toOrbitDimensionProperties();
    }

    @Override
    public DimensionType getDimensionType() {
        DimensionType type = CustomPlanetRegistry.getDimensionType(getDimension());
        return type == null ? DimensionType.OVERWORLD : type;
    }

    private CustomPlanetDefinition getDefinition() {
        return CustomPlanetRegistry.getByOrbitDimensionId(getDimension());
    }
}
