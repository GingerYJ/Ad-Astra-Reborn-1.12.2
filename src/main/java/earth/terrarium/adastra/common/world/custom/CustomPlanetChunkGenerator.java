package earth.terrarium.adastra.common.world.custom;

import earth.terrarium.adastra.common.config.OreGenConfig;
import earth.terrarium.adastra.common.world.AdAstraChunkGenerator;
import earth.terrarium.adastra.common.blocks.ExtendraIcicleBlock;
import earth.terrarium.adastra.common.registry.ModBlocks;
import earth.terrarium.adastra.common.registry.ExtendraBlocks;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
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

        for (CustomPlanetDefinition.OreDefinition ore : definition.getOres()) {
            OreGenConfig.OreSettings settings = OreGenConfig.getCustomPlanetOreSettings(definition, ore);
            generateCustomOre(ore, settings, chunkOrigin, random);
        }

        for (CustomPlanetDefinition.FluidLakeDefinition lake : definition.getFluidLakes()) {
            generateCustomFluidLake(lake, chunkOrigin, random);
        }

        if ("b".equals(definition.getPlanetName())) {
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

    private void generateCustomOre(CustomPlanetDefinition.OreDefinition ore, OreGenConfig.OreSettings settings,
                                   BlockPos chunkOrigin, Random random) {
        int minGenerationY = Math.max(1, settings.minY);
        int maxGenerationY = Math.min(255 - 3, settings.maxY);
        if (settings.countPerChunk <= 0 || settings.veinSize <= 0 || minGenerationY > maxGenerationY) {
            return;
        }

        for (int i = 0; i < settings.countPerChunk; i++) {
            int x = chunkOrigin.getX() + random.nextInt(16);
            int y = minGenerationY + random.nextInt(maxGenerationY - minGenerationY + 1);
            int z = chunkOrigin.getZ() + random.nextInt(16);
            generateCustomVein(ore, settings.veinSize, random, new BlockPos(x, y, z));
        }
    }

    private void generateCustomVein(CustomPlanetDefinition.OreDefinition ore, int veinSize,
                                    Random random, BlockPos origin) {
        float angle = random.nextFloat() * (float) Math.PI;
        double startX = (double) origin.getX() + 8.0D + (double) (MathHelper.sin(angle) * (float) veinSize) / 8.0D;
        double endX = (double) origin.getX() + 8.0D - (double) (MathHelper.sin(angle) * (float) veinSize) / 8.0D;
        double startZ = (double) origin.getZ() + 8.0D + (double) (MathHelper.cos(angle) * (float) veinSize) / 8.0D;
        double endZ = (double) origin.getZ() + 8.0D - (double) (MathHelper.cos(angle) * (float) veinSize) / 8.0D;
        double startY = (double) origin.getY() + random.nextInt(3) - 2;
        double endY = (double) origin.getY() + random.nextInt(3) - 2;

        int maxGenerationY = 255 - 2;

        for (int step = 0; step < veinSize; step++) {
            float progress = (float) step / (float) veinSize;
            double centerX = startX + (endX - startX) * (double) progress;
            double centerY = startY + (endY - startY) * (double) progress;
            double centerZ = startZ + (endZ - startZ) * (double) progress;
            double diameter = random.nextDouble() * (double) veinSize / 16.0D;
            double horizontalRadius = (double) (MathHelper.sin((float) Math.PI * progress) + 1.0F) * diameter + 1.0D;
            double verticalRadius = (double) (MathHelper.sin((float) Math.PI * progress) + 1.0F) * diameter + 1.0D;

            int minX = MathHelper.floor(centerX - horizontalRadius / 2.0D);
            int minY = Math.max(1, MathHelper.floor(centerY - verticalRadius / 2.0D));
            int minZ = MathHelper.floor(centerZ - horizontalRadius / 2.0D);
            int maxX = MathHelper.floor(centerX + horizontalRadius / 2.0D);
            int maxY = Math.min(maxGenerationY, MathHelper.floor(centerY + verticalRadius / 2.0D));
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
                        double zDistance = ((double) z + 0.5D - centerZ) / (verticalRadius / 2.0D);
                        if (xDistance * xDistance + yDistance * yDistance + zDistance * zDistance < 1.0D) {
                            replaceOreBlock(ore, new BlockPos(x, y, z));
                        }
                    }
                }
            }
        }
    }

    private void replaceOreBlock(CustomPlanetDefinition.OreDefinition ore, BlockPos pos) {
        IBlockState current = world.getBlockState(pos);
        if (current.getBlock() == ore.getReplaceBlock().getBlock()) {
            world.setBlockState(pos, ore.getOreBlock(), 2);
        }
    }

    private void generateCustomFluidLake(CustomPlanetDefinition.FluidLakeDefinition lake, BlockPos chunkOrigin, Random random) {
        IBlockState fluidBlock = lake.getFluidBlock();
        IBlockState replaceBlock = lake.getReplaceBlock();
        int maxGenerationY = 255 - 3;

        for (int i = 0; i < lake.getCountPerChunk(); i++) {
            int x = chunkOrigin.getX() + random.nextInt(16);
            int y = lake.getMinY() + random.nextInt(Math.min(lake.getMaxY(), maxGenerationY) - lake.getMinY() + 1);
            int z = chunkOrigin.getZ() + random.nextInt(16);
            BlockPos center = new BlockPos(x, y, z);

            int radius = lake.getLakeSize();
            for (int dx = -radius; dx <= radius; dx++) {
                for (int dy = -radius / 2; dy <= radius / 2; dy++) {
                    for (int dz = -radius; dz <= radius; dz++) {
                        if (dx * dx + dz * dz + dy * dy * 4 <= radius * radius) {
                            BlockPos pos = center.add(dx, dy, dz);
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
        Block sapling = ExtendraBlocks.get("centaurian_oak_sapling");
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
        if (!(ExtendraBlocks.ICICLE instanceof ExtendraIcicleBlock)) {
            return;
        }
        ExtendraIcicleBlock icicle = (ExtendraIcicleBlock) ExtendraBlocks.ICICLE;
        for (int attempt = 0; attempt < 8; attempt++) {
            int x = chunkOrigin.getX() + random.nextInt(16);
            int z = chunkOrigin.getZ() + random.nextInt(16);
            BlockPos surface = world.getHeight(new BlockPos(x, 0, z));
            BlockPos base = surface.down();
            if (world.getBlockState(base).getBlock() != ExtendraBlocks.getPlanetStone("uranus")) {
                continue;
            }

            int height = 1 + random.nextInt(5);
            ExtendraIcicleBlock.grow(world, icicle, base.up(), net.minecraft.util.EnumFacing.UP, height, false);

            // The source feature spreads the base stone and grows nearby points.
            for (net.minecraft.util.EnumFacing horizontal : net.minecraft.util.EnumFacing.HORIZONTALS) {
                if (random.nextFloat() > 0.8F) {
                    continue;
                }
                BlockPos nearbyBase = base.offset(horizontal);
                if (world.getBlockState(nearbyBase).getBlock() == ExtendraBlocks.getPlanetStone("uranus")) {
                    ExtendraIcicleBlock.grow(world, icicle, nearbyBase.up(), net.minecraft.util.EnumFacing.UP,
                        1 + random.nextInt(3), false);
                }
            }
        }
    }

    /** A small 1.12.2 equivalent of Extendra's rare packed-ice icicle geode feature. */
    private void generateCustomIcicleGeode(BlockPos chunkOrigin, Random random) {
        if (random.nextInt(24) != 0) {
            return;
        }

        BlockPos center = new BlockPos(
            chunkOrigin.getX() + random.nextInt(16),
            6 + random.nextInt(25),
            chunkOrigin.getZ() + random.nextInt(16));
        Block stone = ExtendraBlocks.getPlanetStone("uranus");
        Block outer = ModBlocks.PERMAFROST;
        if (stone == null || outer == null) {
            return;
        }

        int outerRadius = 4 + random.nextInt(3);
        boolean alternateInner = random.nextFloat() < 0.083F;
        for (int dx = -outerRadius; dx <= outerRadius; dx++) {
            for (int dy = -outerRadius; dy <= outerRadius; dy++) {
                for (int dz = -outerRadius; dz <= outerRadius; dz++) {
                    double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
                    if (distance > 4.2D || distance > outerRadius + 0.5D) {
                        continue;
                    }

                    BlockPos pos = center.add(dx, dy, dz);
                    if (!world.isBlockLoaded(pos)) {
                        continue;
                    }
                    IBlockState current = world.getBlockState(pos);
                    if (current.getBlock() != stone && current.getBlock() != outer) {
                        continue;
                    }

                    IBlockState replacement;
                    if (distance <= 1.7D) {
                        replacement = Blocks.AIR.getDefaultState();
                    } else if (distance <= 2.2D) {
                        replacement = alternateInner
                            ? ExtendraBlocks.BLUE_SLUSHY_ICE.getDefaultState()
                            : Blocks.PACKED_ICE.getDefaultState();
                    } else if (distance <= 3.2D) {
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
