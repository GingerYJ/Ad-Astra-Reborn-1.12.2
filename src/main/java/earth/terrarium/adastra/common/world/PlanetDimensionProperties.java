package earth.terrarium.adastra.common.world;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.biome.Biome;

public final class PlanetDimensionProperties {

    private final String name;
    private final int dimensionId;
    private final String saveFolder;
    private final Biome biome;
    private final IBlockState surfaceBlock;
    private final IBlockState fillerBlock;
    private final boolean hasSkyLight;
    private final boolean canRespawn;
    private final boolean oxygen;
    private final short temperature;
    private final float gravity;
    private final int solarPower;
    private final int tier;
    private final int dayLength;
    private final Vec3d fogColor;
    private final Vec3d skyColor;

    public PlanetDimensionProperties(
        String name,
        int dimensionId,
        String saveFolder,
        Biome biome,
        IBlockState surfaceBlock,
        IBlockState fillerBlock,
        boolean hasSkyLight,
        boolean canRespawn,
        boolean oxygen,
        short temperature,
        float gravity,
        int solarPower,
        int tier,
        int dayLength,
        Vec3d fogColor,
        Vec3d skyColor) {
        this.name = name;
        this.dimensionId = dimensionId;
        this.saveFolder = saveFolder;
        this.biome = biome;
        this.surfaceBlock = surfaceBlock;
        this.fillerBlock = fillerBlock;
        this.hasSkyLight = hasSkyLight;
        this.canRespawn = canRespawn;
        this.oxygen = oxygen;
        this.temperature = temperature;
        this.gravity = gravity;
        this.solarPower = solarPower;
        this.tier = tier;
        this.dayLength = dayLength;
        this.fogColor = fogColor;
        this.skyColor = skyColor;
    }

    public String getName() {
        return name;
    }

    public int getDimensionId() {
        return dimensionId;
    }

    public String getSaveFolder() {
        return saveFolder;
    }

    public Biome getBiome() {
        return biome;
    }

    public IBlockState getSurfaceBlock() {
        return surfaceBlock;
    }

    public IBlockState getFillerBlock() {
        return fillerBlock;
    }

    public boolean hasSkyLight() {
        return hasSkyLight;
    }

    public boolean canRespawn() {
        return canRespawn;
    }

    public boolean hasOxygen() {
        return oxygen;
    }

    public short getTemperature() {
        return temperature;
    }

    public float getGravity() {
        return gravity;
    }

    public int getSolarPower() {
        return solarPower;
    }

    public int getTier() {
        return tier;
    }

    public int getDayLength() {
        return dayLength;
    }

    public Vec3d getFogColor() {
        return fogColor;
    }

    public Vec3d getSkyColor() {
        return skyColor;
    }
}
