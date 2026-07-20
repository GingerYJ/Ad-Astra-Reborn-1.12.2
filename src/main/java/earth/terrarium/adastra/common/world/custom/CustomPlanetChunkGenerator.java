package earth.terrarium.adastra.common.world.custom;

import earth.terrarium.adastra.common.world.AdAstraChunkGenerator;
import earth.terrarium.adastra.common.blocks.AdAstraIcicleBlock;
import earth.terrarium.adastra.common.registry.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

public class CustomPlanetChunkGenerator extends AdAstraChunkGenerator {

    private final CustomPlanetDefinition definition;
    // Shadowing the private field in AdAstraChunkGenerator so we can access the world directly.
    private final World world;

    public CustomPlanetChunkGenerator(World world, CustomPlanetDefinition definition) {
        super(world, definition.toDimensionProperties());
        this.definition = definition;
        this.world = world;
    }

    public CustomPlanetDefinition getDefinition() {
        return definition;
    }

    @Override
    public void populate(int chunkX, int chunkZ) {
        super.populate(chunkX, chunkZ);

        Random random = createChunkRandom(chunkX, chunkZ);
        BlockPos chunkOrigin = new BlockPos(chunkX * 16, 0, chunkZ * 16);

        for (CustomPlanetDefinition.FluidLakeDefinition lake : definition.getFluidLakes()) {
            generateCustomFluidLake(lake, chunkOrigin, random);
        }

        if ("proxima_centauri_b".equals(definition.getPlanetName())) {
            generateCentaurianOakSaplings(chunkOrigin, random);
        }

        if ("uranus".equals(definition.getPlanetName())) {
            generateCustomIcicles(chunkOrigin, random);
            generateCustomIcicleGeode(chunkOrigin, random);
        }
    }

    private Random createChunkRandom(int chunkX, int chunkZ) {
        Random random = new Random(world.getSeed());
        long xSeed = random.nextLong() / 2L * 2L + 1L;
        long zSeed = random.nextLong() / 2L * 2L + 1L;
        random.setSeed((long) chunkX * xSeed + (long) chunkZ * zSeed ^ world.getSeed());
        return random;
    }

    private void generateCustomFluidLake(CustomPlanetDefinition.FluidLakeDefinition lake, BlockPos chunkOrigin, Random random) {
        IBlockState fluidBlock = lake.getFluidBlock();
        IBlockState replaceBlock = lake.getReplaceBlock();
        int maxGenerationY = Math.min(lake.getMaxY(), world.getHeight() - 3);
        if (lake.getMinY() > maxGenerationY) {
            return;
        }
        int chunkX = chunkOrigin.getX() >> 4;
        int chunkZ = chunkOrigin.getZ() >> 4;
        int chunkMinX = chunkX << 4;
        int chunkMinZ = chunkZ << 4;
        int chunkMaxX = chunkMinX + 15;
        int chunkMaxZ = chunkMinZ + 15;

        for (int i = 0; i < lake.getCountPerChunk(); i++) {
            int x = chunkOrigin.getX() + random.nextInt(16);
            int y = lake.getMinY() + random.nextInt(maxGenerationY - lake.getMinY() + 1);
            int z = chunkOrigin.getZ() + random.nextInt(16);
            BlockPos center = new BlockPos(x, y, z);

            int radius = lake.getLakeSize();
            int minDx = Math.max(-radius, chunkMinX - center.getX());
            int maxDx = Math.min(radius, chunkMaxX - center.getX());
            int minDz = Math.max(-radius, chunkMinZ - center.getZ());
            int maxDz = Math.min(radius, chunkMaxZ - center.getZ());
            int minDy = Math.max(-radius / 2, -center.getY());
            int maxDy = Math.min(radius / 2, world.getHeight() - 1 - center.getY());
            for (int dx = minDx; dx <= maxDx; dx++) {
                for (int dy = minDy; dy <= maxDy; dy++) {
                    for (int dz = minDz; dz <= maxDz; dz++) {
                        if (dx * dx + dz * dz + dy * dy * 4 <= radius * radius) {
                            BlockPos pos = center.add(dx, dy, dz);
                            if (!isInChunk(pos, chunkX, chunkZ)) {
                                continue;
                            }
                            IBlockState current = world.getBlockState(pos);
                            if (current.getBlock() == replaceBlock.getBlock()) {
                                world.setBlockState(pos, fluidBlock, 2);
                            }
                        }
                    }
                }
            }
        }
    }

    private void generateCentaurianOakSaplings(BlockPos chunkOrigin, Random random) {
        Block sapling = ModBlocks.get("centaurian_oak_sapling");
        if (sapling == null) {
            return;
        }

        int count = random.nextInt(10) == 0 ? 5 : 3;
        for (int attempt = 0; attempt < count; attempt++) {
            int x = chunkOrigin.getX() + random.nextInt(16);
            int z = chunkOrigin.getZ() + random.nextInt(16);
            BlockPos position = world.getHeight(new BlockPos(x, 0, z));
            if (world.isAirBlock(position) && sapling.canPlaceBlockAt(world, position)) {
                world.setBlockState(position, sapling.getDefaultState(), 2);
            }
        }
    }

    private void generateCustomIcicles(BlockPos chunkOrigin, Random random) {
        if (!(ModBlocks.ICICLE instanceof AdAstraIcicleBlock)) {
            return;
        }
        AdAstraIcicleBlock icicle = (AdAstraIcicleBlock) ModBlocks.ICICLE;
        Block uranusStone = ModBlocks.getPlanetStone("uranus");
        if (uranusStone == null) {
            return;
        }
        int chunkX = chunkOrigin.getX() >> 4;
        int chunkZ = chunkOrigin.getZ() >> 4;
        for (int attempt = 0; attempt < 8; attempt++) {
            int x = chunkOrigin.getX() + random.nextInt(16);
            int z = chunkOrigin.getZ() + random.nextInt(16);
            BlockPos surface = world.getHeight(new BlockPos(x, 0, z));
            BlockPos base = surface.down();
            if (world.getBlockState(base).getBlock() != uranusStone) {
                continue;
            }

            int height = 1 + random.nextInt(5);
            AdAstraIcicleBlock.grow(world, icicle, base.up(), net.minecraft.util.EnumFacing.UP, height, false);

            // The source feature spreads the base stone and grows nearby points.
            for (net.minecraft.util.EnumFacing horizontal : net.minecraft.util.EnumFacing.HORIZONTALS) {
                if (random.nextFloat() > 0.8F) {
                    continue;
                }
                BlockPos nearbyBase = base.offset(horizontal);
                if (!isInChunk(nearbyBase, chunkX, chunkZ)) {
                    continue;
                }
                if (world.getBlockState(nearbyBase).getBlock() == uranusStone) {
                    AdAstraIcicleBlock.grow(world, icicle, nearbyBase.up(), net.minecraft.util.EnumFacing.UP,
                        1 + random.nextInt(3), false);
                }
            }
        }
    }

    /** A small 1.12.2 equivalent of the rare packed-ice icicle geode feature. */
    private void generateCustomIcicleGeode(BlockPos chunkOrigin, Random random) {
        if (random.nextInt(24) != 0) {
            return;
        }

        BlockPos center = new BlockPos(
            chunkOrigin.getX() + random.nextInt(16),
            6 + random.nextInt(25),
            chunkOrigin.getZ() + random.nextInt(16));
        Block stone = ModBlocks.getPlanetStone("uranus");
        Block outer = ModBlocks.PERMAFROST;
        if (stone == null || outer == null) {
            return;
        }

        int outerRadius = 4 + random.nextInt(3);
        boolean alternateInner = random.nextFloat() < 0.083F;
        int chunkX = chunkOrigin.getX() >> 4;
        int chunkZ = chunkOrigin.getZ() >> 4;
        int chunkMinX = chunkX << 4;
        int chunkMinZ = chunkZ << 4;
        int chunkMaxX = chunkMinX + 15;
        int chunkMaxZ = chunkMinZ + 15;
        int minDx = Math.max(-outerRadius, chunkMinX - center.getX());
        int maxDx = Math.min(outerRadius, chunkMaxX - center.getX());
        int minDz = Math.max(-outerRadius, chunkMinZ - center.getZ());
        int maxDz = Math.min(outerRadius, chunkMaxZ - center.getZ());
        int minDy = Math.max(-outerRadius, -center.getY());
        int maxDy = Math.min(outerRadius, world.getHeight() - 1 - center.getY());
        double maxDistanceSq = Math.min(4.2D, outerRadius + 0.5D);
        maxDistanceSq *= maxDistanceSq;

        for (int dx = minDx; dx <= maxDx; dx++) {
            for (int dy = minDy; dy <= maxDy; dy++) {
                for (int dz = minDz; dz <= maxDz; dz++) {
                    double distanceSq = dx * dx + dy * dy + dz * dz;
                    if (distanceSq > maxDistanceSq) {
                        continue;
                    }

                    BlockPos pos = center.add(dx, dy, dz);
                    if (!isInChunk(pos, chunkX, chunkZ)) {
                        continue;
                    }
                    IBlockState current = world.getBlockState(pos);
                    if (current.getBlock() != stone && current.getBlock() != outer) {
                        continue;
                    }

                    IBlockState replacement;
                    if (distanceSq <= 1.7D * 1.7D) {
                        replacement = Blocks.AIR.getDefaultState();
                    } else if (distanceSq <= 2.2D * 2.2D) {
                        replacement = alternateInner
                            ? ModBlocks.BLUE_SLUSHY_ICE.getDefaultState()
                            : Blocks.PACKED_ICE.getDefaultState();
                    } else if (distanceSq <= 3.2D * 3.2D) {
                        replacement = stone.getDefaultState();
                    } else {
                        replacement = outer.getDefaultState();
                    }
                    world.setBlockState(pos, replacement, 2);
                }
            }
        }
    }
}
