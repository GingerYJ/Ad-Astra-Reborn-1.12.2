package earth.terrarium.adastra.common.world;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeProvider;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class AdAstraBiomeProvider extends BiomeProvider {

    private final Biome biome;
    private final List<Biome> biomes;

    public AdAstraBiomeProvider(Biome biome) {
        this.biome = biome;
        this.biomes = Collections.singletonList(biome);
    }

    @Override
    public List<Biome> getBiomesToSpawnIn() {
        return biomes;
    }

    @Override
    public Biome getBiome(BlockPos pos) {
        return biome;
    }

    @Override
    public Biome getBiome(BlockPos pos, Biome defaultBiome) {
        return biome;
    }

    @Override
    public Biome[] getBiomesForGeneration(Biome[] biomes, int x, int z, int width, int height) {
        return fill(biomes, width, height);
    }

    @Override
    public Biome[] getBiomes(Biome[] oldBiomeList, int x, int z, int width, int depth) {
        return fill(oldBiomeList, width, depth);
    }

    @Override
    public Biome[] getBiomes(Biome[] listToReuse, int x, int z, int width, int length, boolean cacheFlag) {
        return fill(listToReuse, width, length);
    }

    @Override
    public boolean areBiomesViable(int x, int z, int radius, List<Biome> allowed) {
        return allowed.contains(biome);
    }

    @Nullable
    @Override
    public BlockPos findBiomePosition(int x, int z, int range, List<Biome> biomes, Random random) {
        return biomes.contains(biome) ? new BlockPos(x, 0, z) : null;
    }

    @Override
    public boolean isFixedBiome() {
        return true;
    }

    @Override
    public Biome getFixedBiome() {
        return biome;
    }

    private Biome[] fill(Biome[] biomes, int width, int height) {
        int size = width * height;
        if (biomes == null || biomes.length < size) {
            biomes = new Biome[size];
        }
        Arrays.fill(biomes, 0, size, biome);
        return biomes;
    }
}
