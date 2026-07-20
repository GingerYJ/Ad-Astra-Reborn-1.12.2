package earth.terrarium.adastra.common.world.custom;

import earth.terrarium.adastra.common.world.AdAstraChunkGenerator;
import earth.terrarium.adastra.common.world.AdAstraWorldProvider;
import earth.terrarium.adastra.common.world.PlanetDimensionProperties;
import net.minecraft.world.DimensionType;
import net.minecraft.world.gen.IChunkGenerator;

public class WorldProviderCustomPlanet extends AdAstraWorldProvider {

    @Override
    protected PlanetDimensionProperties getProperties() {
        CustomPlanetDefinition definition = getDefinition();
        return definition == null
            ? CustomPlanetDefinition.fallbackProperties(getDimension())
            : definition.toDimensionProperties();
    }

    @Override
    public IChunkGenerator createChunkGenerator() {
        CustomPlanetDefinition definition = getDefinition();
        return definition == null
            ? new AdAstraChunkGenerator(world, getProperties())
            : new CustomPlanetChunkGenerator(world, definition);
    }

    @Override
    public DimensionType getDimensionType() {
        DimensionType builtInType = BuiltInPlanetRegistry.getDimensionType(getDimension());
        if (builtInType != null) {
            return builtInType;
        }
        DimensionType type = CustomPlanetRegistry.getDimensionType(getDimension());
        return type == null ? DimensionType.OVERWORLD : type;
    }

    private CustomPlanetDefinition getDefinition() {
        CustomPlanetDefinition builtIn = BuiltInPlanetRegistry.getByDimensionId(getDimension());
        return builtIn == null ? CustomPlanetRegistry.getByDimensionId(getDimension()) : builtIn;
    }
}
