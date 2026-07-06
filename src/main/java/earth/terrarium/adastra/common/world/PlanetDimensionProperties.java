package earth.terrarium.adastra.common.world;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
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
    private final IBlockState oceanBlock;
    private final int oceanLevel;
    private final IBlockState caveTopBlock;
    private final IBlockState caveFloorBlock;

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
        this(name, dimensionId, saveFolder, biome, surfaceBlock, fillerBlock,
            hasSkyLight, canRespawn, oxygen, temperature, gravity, solarPower,
            tier, dayLength, fogColor, skyColor,
            Blocks.WATER.getDefaultState(), 63, null, null);
    }

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
        Vec3d skyColor,
        IBlockState oceanBlock,
        int oceanLevel,
        IBlockState caveTopBlock,
        IBlockState caveFloorBlock) {
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
        this.oceanBlock = oceanBlock;
        this.oceanLevel = oceanLevel;
        this.caveTopBlock = caveTopBlock;
        this.caveFloorBlock = caveFloorBlock;
    }

    public String getName() { return name; }
    public int getDimensionId() { return dimensionId; }
    public String getSaveFolder() { return saveFolder; }
    public Biome getBiome() { return biome; }
    public IBlockState getSurfaceBlock() { return surfaceBlock; }
    public IBlockState getFillerBlock() { return fillerBlock; }
    public boolean hasSkyLight() { return hasSkyLight; }
    public boolean canRespawn() { return canRespawn; }
    public boolean hasOxygen() { return oxygen; }
    public short getTemperature() { return temperature; }
    public float getGravity() { return gravity; }
    public int getSolarPower() { return solarPower; }
    public int getTier() { return tier; }
    public int getDayLength() { return dayLength; }
    public Vec3d getFogColor() { return fogColor; }
    public Vec3d getSkyColor() { return skyColor; }
    public IBlockState getOceanBlock() { return oceanBlock; }
    public int getOceanLevel() { return oceanLevel; }
    public IBlockState getCaveTopBlock() { return caveTopBlock; }
    public IBlockState getCaveFloorBlock() { return caveFloorBlock; }
}
