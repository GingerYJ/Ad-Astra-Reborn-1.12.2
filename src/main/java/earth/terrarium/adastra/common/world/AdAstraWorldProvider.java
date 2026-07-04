package earth.terrarium.adastra.common.world;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.IChunkGenerator;

public abstract class AdAstraWorldProvider extends WorldProvider {

    protected abstract PlanetDimensionProperties getProperties();

    @Override
    protected void init() {
        this.biomeProvider = new AdAstraBiomeProvider(getProperties().getBiome());
        this.hasSkyLight = getProperties().hasSkyLight();
        this.nether = false;
        this.doesWaterVaporize = false;
    }

    @Override
    public IChunkGenerator createChunkGenerator() {
        return new AdAstraChunkGenerator(world, getProperties());
    }

    @Override
    public boolean canCoordinateBeSpawn(int x, int z) {
        return true;
    }

    @Override
    public BlockPos getSpawnCoordinate() {
        return new BlockPos(0, AdAstraChunkGenerator.SPAWN_Y, 0);
    }

    @Override
    public int getAverageGroundLevel() {
        return AdAstraChunkGenerator.SPAWN_Y;
    }

    @Override
    public boolean canRespawnHere() {
        return getProperties().canRespawn();
    }

    @Override
    public boolean isSurfaceWorld() {
        return true;
    }

    @Override
    public boolean hasSkyLight() {
        return getProperties().hasSkyLight();
    }

    @Override
    public float calculateCelestialAngle(long worldTime, float partialTicks) {
        int dayLength = getProperties().getDayLength();
        int dayTime = (int) (worldTime % dayLength);
        float angle = ((float) dayTime + partialTicks) / (float) dayLength - 0.25F;

        if (angle < 0.0F) {
            angle += 1.0F;
        }

        if (angle > 1.0F) {
            angle -= 1.0F;
        }

        float previousAngle = angle;
        angle = 1.0F - (float) ((Math.cos((double) angle * Math.PI) + 1.0D) / 2.0D);
        return previousAngle + (angle - previousAngle) / 3.0F;
    }

    @Override
    public Vec3d getFogColor(float celestialAngle, float partialTicks) {
        return getProperties().getFogColor();
    }

    @Override
    public Vec3d getSkyColor(Entity cameraEntity, float partialTicks) {
        return getProperties().getSkyColor();
    }

    @Override
    public boolean isSkyColored() {
        return false;
    }

    @Override
    public float getCloudHeight() {
        return 192.0F;
    }

    @Override
    public String getSaveFolder() {
        return getProperties().getSaveFolder();
    }

    @Override
    public boolean canDoLightning(Chunk chunk) {
        return false;
    }

    @Override
    public boolean canDoRainSnowIce(Chunk chunk) {
        return false;
    }

    public String getPlanetName() {
        return getProperties().getName();
    }

    public boolean hasOxygen() {
        return getProperties().hasOxygen();
    }

    public short getTemperature() {
        return getProperties().getTemperature();
    }

    public float getGravity() {
        return getProperties().getGravity();
    }

    public int getSolarPower() {
        return getProperties().getSolarPower();
    }

    public int getTier() {
        return getProperties().getTier();
    }
}
