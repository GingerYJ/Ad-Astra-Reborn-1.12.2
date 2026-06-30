package earth.terrarium.adastra.common.world;

import earth.terrarium.adastra.common.registry.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.IChunkGenerator;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AdAstraChunkGenerator implements IChunkGenerator {

    public static final int SURFACE_Y = 63;
    public static final int SPAWN_Y = SURFACE_Y + 1;

    private static final int MIN_GENERATION_Y = 1;
    private static final int MAX_GENERATION_Y = SURFACE_Y - 1;
    private static final List<PlanetOreSpec> ORE_SPECS = createOreSpecs();

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
        Random random = createChunkRandom(chunkX, chunkZ);
        List<PlanetOreSpec> specs = getOreSpecs(properties.getName());
        BlockPos chunkOrigin = new BlockPos(chunkX * 16, 0, chunkZ * 16);
        for (PlanetOreSpec spec : specs) {
            generateOre(spec, chunkOrigin, random);
        }

        generateSimpleFeatures(chunkOrigin, random);
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

    private Random createChunkRandom(int chunkX, int chunkZ) {
        Random random = new Random(world.getSeed());
        long xSeed = random.nextLong() / 2L * 2L + 1L;
        long zSeed = random.nextLong() / 2L * 2L + 1L;
        random.setSeed((long) chunkX * xSeed + (long) chunkZ * zSeed ^ world.getSeed());
        return random;
    }

    private void generateOre(PlanetOreSpec spec, BlockPos chunkOrigin, Random random) {
        for (int i = 0; i < spec.countPerChunk; i++) {
            int x = chunkOrigin.getX() + random.nextInt(16);
            int y = spec.minY + random.nextInt(spec.maxY - spec.minY + 1);
            int z = chunkOrigin.getZ() + random.nextInt(16);
            generateVein(spec, random, new BlockPos(x, y, z));
        }
    }

    private void generateVein(PlanetOreSpec spec, Random random, BlockPos origin) {
        float angle = random.nextFloat() * (float) Math.PI;
        double startX = (double) origin.getX() + 8.0D + (double) (MathHelper.sin(angle) * (float) spec.veinSize) / 8.0D;
        double endX = (double) origin.getX() + 8.0D - (double) (MathHelper.sin(angle) * (float) spec.veinSize) / 8.0D;
        double startZ = (double) origin.getZ() + 8.0D + (double) (MathHelper.cos(angle) * (float) spec.veinSize) / 8.0D;
        double endZ = (double) origin.getZ() + 8.0D - (double) (MathHelper.cos(angle) * (float) spec.veinSize) / 8.0D;
        double startY = (double) origin.getY() + random.nextInt(3) - 2;
        double endY = (double) origin.getY() + random.nextInt(3) - 2;

        for (int step = 0; step < spec.veinSize; step++) {
            float progress = (float) step / (float) spec.veinSize;
            double centerX = startX + (endX - startX) * (double) progress;
            double centerY = startY + (endY - startY) * (double) progress;
            double centerZ = startZ + (endZ - startZ) * (double) progress;
            double diameter = random.nextDouble() * (double) spec.veinSize / 16.0D;
            double horizontalRadius = (double) (MathHelper.sin((float) Math.PI * progress) + 1.0F) * diameter + 1.0D;
            double verticalRadius = (double) (MathHelper.sin((float) Math.PI * progress) + 1.0F) * diameter + 1.0D;

            int minX = MathHelper.floor(centerX - horizontalRadius / 2.0D);
            int minY = Math.max(MIN_GENERATION_Y, MathHelper.floor(centerY - verticalRadius / 2.0D));
            int minZ = MathHelper.floor(centerZ - horizontalRadius / 2.0D);
            int maxX = MathHelper.floor(centerX + horizontalRadius / 2.0D);
            int maxY = Math.min(MAX_GENERATION_Y, MathHelper.floor(centerY + verticalRadius / 2.0D));
            int maxZ = MathHelper.floor(centerZ + horizontalRadius / 2.0D);

            for (int x = minX; x <= maxX; x++) {
                double xDistance = ((double) x + 0.5D - centerX) / (horizontalRadius / 2.0D);
                if (xDistance * xDistance >= 1.0D) {
                    continue;
                }

                for (int y = minY; y <= maxY; y++) {
                    double yDistance = ((double) y + 0.5D - centerY) / (verticalRadius / 2.0D);
                    if (xDistance * xDistance + yDistance * yDistance >= 1.0D) {
                        continue;
                    }

                    for (int z = minZ; z <= maxZ; z++) {
                        double zDistance = ((double) z + 0.5D - centerZ) / (horizontalRadius / 2.0D);
                        if (xDistance * xDistance + yDistance * yDistance + zDistance * zDistance < 1.0D) {
                            replaceOreBlock(spec, new BlockPos(x, y, z));
                        }
                    }
                }
            }
        }
    }

    private void replaceOreBlock(PlanetOreSpec spec, BlockPos pos) {
        IBlockState current = world.getBlockState(pos);
        if (spec.canReplace(current.getBlock())) {
            world.setBlockState(pos, spec.oreState, 2);
        }
    }

    private void generateSimpleFeatures(BlockPos chunkOrigin, Random random) {
        String planetName = properties.getName();
        if ("moon".equals(planetName)) {
            generateMoonSoulSand(chunkOrigin, random);
        } else if ("mars".equals(planetName)) {
            generateMarsRockBlobs(chunkOrigin, random);
        } else if ("venus".equals(planetName)) {
            generateInfernalSpireColumns(chunkOrigin, random);
        }
    }

    private void generateMoonSoulSand(BlockPos chunkOrigin, Random random) {
        PlanetOreSpec soulSand = new PlanetOreSpec(
            "moon",
            Blocks.SOUL_SAND.getDefaultState(),
            60,
            20,
            MIN_GENERATION_Y,
            MAX_GENERATION_Y,
            new Block[]{ModBlocks.MOON_STONE, ModBlocks.MOON_DEEPSLATE}
        );
        generateOre(soulSand, chunkOrigin, random);
    }

    private void generateMarsRockBlobs(BlockPos chunkOrigin, Random random) {
        for (int i = 0; i < 2; i++) {
            int x = chunkOrigin.getX() + random.nextInt(16);
            int z = chunkOrigin.getZ() + random.nextInt(16);
            BlockPos surface = findSurface(new BlockPos(x, SURFACE_Y + 8, z), ModBlocks.MARS_SAND);
            if (surface != null) {
                generateMarsRockBlob(surface.up(), random);
            }
        }
    }

    @Nullable
    private BlockPos findSurface(BlockPos start, Block groundBlock) {
        BlockPos pos = start;
        while (pos.getY() > MIN_GENERATION_Y + 3) {
            if (!world.isAirBlock(pos.down()) && world.getBlockState(pos.down()).getBlock() == groundBlock) {
                return pos.down();
            }
            pos = pos.down();
        }
        return null;
    }

    private void generateMarsRockBlob(BlockPos origin, Random random) {
        BlockPos center = origin;
        for (int i = 0; i < 3; i++) {
            int radiusX = random.nextInt(2);
            int radiusY = random.nextInt(2);
            int radiusZ = random.nextInt(2);
            float radius = (float) (radiusX + radiusY + radiusZ) * 0.333F + 0.5F;

            for (int x = center.getX() - radiusX; x <= center.getX() + radiusX; x++) {
                for (int y = center.getY() - radiusY; y <= center.getY() + radiusY; y++) {
                    for (int z = center.getZ() - radiusZ; z <= center.getZ() + radiusZ; z++) {
                        BlockPos pos = new BlockPos(x, y, z);
                        if (pos.distanceSq(center) <= (double) (radius * radius)) {
                            world.setBlockState(pos, ModBlocks.CONGLOMERATE.getDefaultState(), 2);
                        }
                    }
                }
            }

            center = center.add(-1 + random.nextInt(2), -random.nextInt(2), -1 + random.nextInt(2));
        }
    }

    private void generateInfernalSpireColumns(BlockPos chunkOrigin, Random random) {
        for (int i = 0; i < 4; i++) {
            generateInfernalSpireColumn(chunkOrigin, random, 1, 4, 1);
        }
        for (int i = 0; i < 2; i++) {
            generateInfernalSpireColumn(chunkOrigin, random, 5, 10, 2 + random.nextInt(2));
        }
    }

    private void generateInfernalSpireColumn(BlockPos chunkOrigin, Random random, int minHeight, int maxHeight, int reach) {
        int x = chunkOrigin.getX() + random.nextInt(16);
        int z = chunkOrigin.getZ() + random.nextInt(16);
        BlockPos surface = findSurface(new BlockPos(x, SURFACE_Y + 8, z), ModBlocks.VENUS_SAND);
        if (surface == null) {
            return;
        }

        int height = minHeight + random.nextInt(maxHeight - minHeight + 1);
        placeInfernalSpireCluster(surface.up(), random, height, reach);
    }

    private void placeInfernalSpireCluster(BlockPos origin, Random random, int height, int reach) {
        boolean dense = random.nextFloat() < 0.9F;
        int spread = Math.min(height, dense ? 5 : 8);
        int attempts = dense ? 50 : 15;

        for (int i = 0; i < attempts; i++) {
            BlockPos columnOrigin = origin.add(randomRange(random, spread), 0, randomRange(random, spread));
            int columnHeight = height - Math.abs(columnOrigin.getX() - origin.getX()) - Math.abs(columnOrigin.getZ() - origin.getZ());
            if (columnHeight >= 0) {
                placeInfernalSpireColumnAt(columnOrigin, columnHeight, reach);
            }
        }
    }

    private int randomRange(Random random, int radius) {
        return random.nextInt(radius * 2 + 1) - radius;
    }

    private void placeInfernalSpireColumnAt(BlockPos origin, int height, int reach) {
        for (int x = origin.getX() - reach; x <= origin.getX() + reach; x++) {
            for (int z = origin.getZ() - reach; z <= origin.getZ() + reach; z++) {
                BlockPos base = new BlockPos(x, origin.getY(), z);
                int distance = Math.abs(x - origin.getX()) + Math.abs(z - origin.getZ());
                int columnHeight = height - distance / 2;
                if (columnHeight < 0 || !canPlaceSpireAt(base)) {
                    continue;
                }

                for (int y = 0; y <= columnHeight && origin.getY() + y < world.getHeight(); y++) {
                    BlockPos pos = base.up(y);
                    if (world.isAirBlock(pos) || world.getBlockState(pos).getBlock() == ModBlocks.INFERNAL_SPIRE_BLOCK) {
                        world.setBlockState(pos, ModBlocks.INFERNAL_SPIRE_BLOCK.getDefaultState(), 2);
                    } else {
                        break;
                    }
                }
            }
        }
    }

    private boolean canPlaceSpireAt(BlockPos pos) {
        Block below = world.getBlockState(pos.down()).getBlock();
        Block current = world.getBlockState(pos).getBlock();
        return (world.isAirBlock(pos) || current == ModBlocks.INFERNAL_SPIRE_BLOCK)
            && below != Blocks.AIR
            && below != Blocks.BEDROCK
            && below != Blocks.LAVA;
    }

    private List<PlanetOreSpec> getOreSpecs(String planetName) {
        List<PlanetOreSpec> specs = new ArrayList<PlanetOreSpec>();
        for (PlanetOreSpec spec : ORE_SPECS) {
            if (spec.planetName.equals(planetName)) {
                specs.add(spec);
            }
        }
        return specs;
    }

    private static List<PlanetOreSpec> createOreSpecs() {
        List<PlanetOreSpec> specs = new ArrayList<PlanetOreSpec>();

        add(specs, "moon", ModBlocks.MOON_CHEESE_ORE, 8, 9, 6, 192, ModBlocks.MOON_STONE, ModBlocks.MOON_DEEPSLATE);
        add(specs, "moon", ModBlocks.MOON_DESH_ORE, 9, 9, -80, 80, ModBlocks.MOON_STONE, ModBlocks.MOON_DEEPSLATE);
        add(specs, "moon", ModBlocks.MOON_ICE_SHARD_ORE, 10, 8, -32, 32, ModBlocks.MOON_STONE, ModBlocks.MOON_DEEPSLATE);
        add(specs, "moon", ModBlocks.MOON_IRON_ORE, 11, 10, -24, 56, ModBlocks.MOON_STONE, ModBlocks.MOON_DEEPSLATE);

        add(specs, "mars", ModBlocks.MARS_DIAMOND_ORE, 7, 5, -80, 80, ModBlocks.MARS_STONE);
        add(specs, "mars", ModBlocks.MARS_ICE_SHARD_ORE, 10, 8, -32, 32, ModBlocks.MARS_STONE);
        add(specs, "mars", ModBlocks.MARS_IRON_ORE, 11, 10, -24, 56, ModBlocks.MARS_STONE);
        add(specs, "mars", ModBlocks.MARS_OSTRUM_ORE, 8, 8, -80, 80, ModBlocks.MARS_STONE);

        add(specs, "mercury", ModBlocks.MERCURY_IRON_ORE, 8, 20, -80, 192, ModBlocks.MERCURY_STONE);

        add(specs, "venus", ModBlocks.VENUS_CALORITE_ORE, 8, 8, -80, 80, ModBlocks.VENUS_STONE);
        add(specs, "venus", ModBlocks.VENUS_COAL_ORE, 17, 20, -80, 192, ModBlocks.VENUS_STONE);
        add(specs, "venus", ModBlocks.VENUS_DIAMOND_ORE, 9, 5, -80, 80, ModBlocks.VENUS_STONE);
        add(specs, "venus", ModBlocks.VENUS_GOLD_ORE, 10, 4, -64, 32, ModBlocks.VENUS_STONE);

        add(specs, "glacio", ModBlocks.GLACIO_COAL_ORE, 17, 20, -80, 192, ModBlocks.GLACIO_STONE);
        add(specs, "glacio", ModBlocks.GLACIO_ICE_SHARD_ORE, 17, 8, -32, 32, ModBlocks.GLACIO_STONE);
        add(specs, "glacio", ModBlocks.GLACIO_IRON_ORE, 11, 10, -24, 56, ModBlocks.GLACIO_STONE);
        add(specs, "glacio", ModBlocks.GLACIO_LAPIS_ORE, 9, 2, -32, 32, ModBlocks.GLACIO_STONE);

        return specs;
    }

    private static void add(List<PlanetOreSpec> specs, String planetName, Block oreBlock, int veinSize, int countPerChunk,
                            int sourceMinY, int sourceMaxY, Block... replaceableBlocks) {
        int minY = Math.max(MIN_GENERATION_Y, sourceMinY);
        int maxY = Math.min(MAX_GENERATION_Y, sourceMaxY);
        if (minY <= maxY) {
            specs.add(new PlanetOreSpec(planetName, oreBlock.getDefaultState(), veinSize, countPerChunk, minY, maxY, replaceableBlocks));
        }
    }

    private static final class PlanetOreSpec {

        private final String planetName;
        private final IBlockState oreState;
        private final int veinSize;
        private final int countPerChunk;
        private final int minY;
        private final int maxY;
        private final Block[] replaceableBlocks;

        private PlanetOreSpec(String planetName, IBlockState oreState, int veinSize, int countPerChunk, int minY, int maxY,
                              Block[] replaceableBlocks) {
            this.planetName = planetName;
            this.oreState = oreState;
            this.veinSize = veinSize;
            this.countPerChunk = countPerChunk;
            this.minY = minY;
            this.maxY = maxY;
            this.replaceableBlocks = replaceableBlocks;
        }

        private boolean canReplace(Block block) {
            for (Block replaceableBlock : replaceableBlocks) {
                if (block == replaceableBlock) {
                    return true;
                }
            }
            return false;
        }
    }
}
