package earth.terrarium.adastra.common.world.custom;

import earth.terrarium.adastra.common.world.AdAstraChunkGenerator;
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
            generateCustomOre(ore, chunkOrigin, random);
        }

        for (CustomPlanetDefinition.FluidLakeDefinition lake : definition.getFluidLakes()) {
            generateCustomFluidLake(lake, chunkOrigin, random);
        }
    }

    private Random createChunkRandom(int chunkX, int chunkZ) {
        Random random = new Random(world.getSeed());
        long xSeed = random.nextLong() / 2L * 2L + 1L;
        long zSeed = random.nextLong() / 2L * 2L + 1L;
        random.setSeed((long) chunkX * xSeed + (long) chunkZ * zSeed ^ world.getSeed());
        return random;
    }

    private void generateCustomOre(CustomPlanetDefinition.OreDefinition ore, BlockPos chunkOrigin, Random random) {
        int maxGenerationY = 255 - 3;
        for (int i = 0; i < ore.getCountPerChunk(); i++) {
            int x = chunkOrigin.getX() + random.nextInt(16);
            int y = ore.getMinY() + random.nextInt(Math.min(ore.getMaxY(), maxGenerationY) - ore.getMinY() + 1);
            int z = chunkOrigin.getZ() + random.nextInt(16);
            generateCustomVein(ore, random, new BlockPos(x, y, z));
        }
    }

    private void generateCustomVein(CustomPlanetDefinition.OreDefinition ore, Random random, BlockPos origin) {
        float angle = random.nextFloat() * (float) Math.PI;
        double startX = (double) origin.getX() + 8.0D + (double) (MathHelper.sin(angle) * (float) ore.getVeinSize()) / 8.0D;
        double endX = (double) origin.getX() + 8.0D - (double) (MathHelper.sin(angle) * (float) ore.getVeinSize()) / 8.0D;
        double startZ = (double) origin.getZ() + 8.0D + (double) (MathHelper.cos(angle) * (float) ore.getVeinSize()) / 8.0D;
        double endZ = (double) origin.getZ() + 8.0D - (double) (MathHelper.cos(angle) * (float) ore.getVeinSize()) / 8.0D;
        double startY = (double) origin.getY() + random.nextInt(3) - 2;
        double endY = (double) origin.getY() + random.nextInt(3) - 2;

        int maxGenerationY = 255 - 2;

        for (int step = 0; step < ore.getVeinSize(); step++) {
            float progress = (float) step / (float) ore.getVeinSize();
            double centerX = startX + (endX - startX) * (double) progress;
            double centerY = startY + (endY - startY) * (double) progress;
            double centerZ = startZ + (endZ - startZ) * (double) progress;
            double diameter = random.nextDouble() * (double) ore.getVeinSize() / 16.0D;
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
}
