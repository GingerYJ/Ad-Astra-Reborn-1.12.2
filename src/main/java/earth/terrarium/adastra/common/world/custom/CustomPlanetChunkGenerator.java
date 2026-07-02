package earth.terrarium.adastra.common.world.custom;

import earth.terrarium.adastra.common.world.AdAstraChunkGenerator;
import net.minecraft.world.World;

public class CustomPlanetChunkGenerator extends AdAstraChunkGenerator {

    private final CustomPlanetDefinition definition;

    public CustomPlanetChunkGenerator(World world, CustomPlanetDefinition definition) {
        super(world, definition.toDimensionProperties());
        this.definition = definition;
    }

    public CustomPlanetDefinition getDefinition() {
        return definition;
    }

    @Override
    public void populate(int chunkX, int chunkZ) {
        super.populate(chunkX, chunkZ);
        // TODO: consume definition.getOres() and definition.getFluidLakes() here once the final
        // worldgen contract is chosen. The first CRT layer deliberately only stores the data.
    }
}
