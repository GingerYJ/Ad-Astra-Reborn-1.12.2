package earth.terrarium.adastra.common.world;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.IChunkGenerator;

import javax.annotation.Nullable;
import java.util.List;

public class AdAstraChunkGenerator implements IChunkGenerator {

    public static final int SURFACE_Y = 63;
    public static final int SPAWN_Y = SURFACE_Y + 1;

    private final World world;
    private final PlanetDimensionProperties properties;

    public AdAstraChunkGenerator(World world, PlanetDimensionProperties properties) {
        this.world = world;
        this.properties = properties;
    }

    @Override
    public Chunk generateChunk(int chunkX, int chunkZ) {
        ChunkPrimer primer = new ChunkPrimer();
        IBlockState bedrock = Blocks.BEDROCK.getDefaultState();
        IBlockState surface = properties.getSurfaceBlock();
        IBlockState filler = properties.getFillerBlock();

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                primer.setBlockState(x, 0, z, bedrock);

                for (int y = 1; y < SURFACE_Y; y++) {
                    primer.setBlockState(x, y, z, filler);
                }

                primer.setBlockState(x, SURFACE_Y, z, surface);
            }
        }

        Chunk chunk = new Chunk(world, primer, chunkX, chunkZ);
        byte biomeId = (byte) Biome.getIdForBiome(properties.getBiome());
        byte[] biomeArray = chunk.getBiomeArray();
        for (int i = 0; i < biomeArray.length; i++) {
            biomeArray[i] = biomeId;
        }
        chunk.generateSkylightMap();
        return chunk;
    }

    @Override
    public void populate(int chunkX, int chunkZ) {
    }

    @Override
    public boolean generateStructures(Chunk chunkIn, int x, int z) {
        return false;
    }

    @Override
    public List<Biome.SpawnListEntry> getPossibleCreatures(EnumCreatureType creatureType, BlockPos pos) {
        return PlanetMobSpawns.getPossibleCreatures(properties, creatureType);
    }

    @Nullable
    @Override
    public BlockPos getNearestStructurePos(World worldIn, String structureName, BlockPos position, boolean findUnexplored) {
        return null;
    }

    @Override
    public void recreateStructures(Chunk chunkIn, int x, int z) {
    }

    @Override
    public boolean isInsideStructure(World worldIn, String structureName, BlockPos pos) {
        return false;
    }
}
