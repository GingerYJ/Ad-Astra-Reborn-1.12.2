package earth.terrarium.adastra.common.world;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.IChunkGenerator;

public abstract class AdAstraOrbitWorldProvider extends AdAstraWorldProvider {

    public static final int STATION_Y = 100;
    public static final int SPAWN_Y = STATION_Y + 4;

    @Override
    public IChunkGenerator createChunkGenerator() {
        return new EmptyOrbitChunkGenerator(world, getProperties().getBiome());
    }

    @Override
    public BlockPos getSpawnCoordinate() {
        return new BlockPos(0, SPAWN_Y, 0);
    }

    @Override
    public int getAverageGroundLevel() {
        return SPAWN_Y;
    }

    @Override
    public boolean isSurfaceWorld() {
        return false;
    }

    @Override
    public float getCloudHeight() {
        return -8.0F;
    }

    @Override
    public boolean canDoLightning(Chunk chunk) {
        return false;
    }

    @Override
    public boolean canDoRainSnowIce(Chunk chunk) {
        return false;
    }
}
