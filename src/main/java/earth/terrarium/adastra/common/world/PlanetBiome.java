package earth.terrarium.adastra.common.world;

import net.minecraft.block.state.IBlockState;
import net.minecraft.world.biome.Biome;

public class PlanetBiome extends Biome {

    public PlanetBiome(String name, IBlockState topBlock, IBlockState fillerBlock, float temperature, float rainfall) {
        super(properties(name, temperature, rainfall));
        this.topBlock = topBlock;
        this.fillerBlock = fillerBlock;
        this.decorator.treesPerChunk = -999;
        this.decorator.flowersPerChunk = 0;
        this.decorator.grassPerChunk = 0;
        this.decorator.deadBushPerChunk = 0;
        this.decorator.mushroomsPerChunk = 0;
        this.decorator.reedsPerChunk = 0;
        this.decorator.cactiPerChunk = 0;
        this.spawnableMonsterList.clear();
        this.spawnableCreatureList.clear();
        this.spawnableWaterCreatureList.clear();
        this.spawnableCaveCreatureList.clear();
    }

    public PlanetBiome addMonsterSpawn(Class<? extends net.minecraft.entity.EntityLiving> entityClass, int weight, int min, int max) {
        this.spawnableMonsterList.add(new SpawnListEntry(entityClass, weight, min, max));
        return this;
    }

    public PlanetBiome addCreatureSpawn(Class<? extends net.minecraft.entity.EntityLiving> entityClass, int weight, int min, int max) {
        this.spawnableCreatureList.add(new SpawnListEntry(entityClass, weight, min, max));
        return this;
    }

    @Override
    public float getSpawningChance() {
        return 0.1F;
    }

    private static BiomeProperties properties(String name, float temperature, float rainfall) {
        BiomeProperties properties = new BiomeProperties(name)
            .setBaseHeight(0.125F)
            .setHeightVariation(0.05F)
            .setTemperature(temperature)
            .setRainfall(rainfall);

        if (rainfall <= 0.0F) {
            properties.setRainDisabled();
        }

        if (temperature < 0.15F && rainfall > 0.0F) {
            properties.setSnowEnabled();
        }

        return properties;
    }
}
