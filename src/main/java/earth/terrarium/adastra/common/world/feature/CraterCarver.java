package earth.terrarium.adastra.common.world.feature;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;

import java.util.Random;

/**
 * Generates spherical impact craters on planet surfaces.
 * Features bowl-shaped depressions with raised rims.
 */
public class CraterCarver {

    private final int centerX;
    private final int centerZ;
    private final int centerY;
    private final int radius;
    private final int depth;
    private final int rimHeight;

    public CraterCarver(int centerX, int centerZ, int centerY, int radius, int depth, int rimHeight) {
        this.centerX = centerX;
        this.centerZ = centerZ;
        this.centerY = centerY;
        this.radius = radius;
        this.depth = depth;
        this.rimHeight = rimHeight;
    }

    /**
     * Carve crater into ChunkPrimer during terrain generation.
     * This is the preferred method for performance.
     */
    public void carveInPrimer(ChunkPrimer primer, int chunkX, int chunkZ, IBlockState airState) {
        int chunkStartX = chunkX * 16;
        int chunkStartZ = chunkZ * 16;

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int worldX = chunkStartX + x;
                int worldZ = chunkStartZ + z;

                int targetY = calculateCraterY(worldX, worldZ);
                if (targetY < 0) {
                    continue; // Outside crater influence
                }

                // Carve from target Y upward to surface
                for (int y = targetY; y < 255; y++) {
                    IBlockState current = primer.getBlockState(x, y, z);
                    if (current.getBlock() == Blocks.AIR || current.getBlock() == Blocks.BEDROCK) {
                        break;
                    }
                    primer.setBlockState(x, y, z, airState);
                }
            }
        }
    }

    /**
     * Carve crater into existing world blocks.
     * Used during populate phase if needed.
     */
    public void carveInWorld(World world, IBlockState airState) {
        for (int x = centerX - radius - rimHeight; x <= centerX + radius + rimHeight; x++) {
            for (int z = centerZ - radius - rimHeight; z <= centerZ + radius + rimHeight; z++) {
                int targetY = calculateCraterY(x, z);
                if (targetY < 0) {
                    continue;
                }

                // Carve from target Y upward
                for (int y = targetY; y <= Math.min(centerY + rimHeight + 5, 255); y++) {
                    BlockPos pos = new BlockPos(x, y, z);
                    IBlockState current = world.getBlockState(pos);
                    if (current.getBlock() == Blocks.AIR || current.getBlock() == Blocks.BEDROCK) {
                        break;
                    }
                    if (!shouldPreserveBlock(current)) {
                        world.setBlockState(pos, airState, 2);
                    }
                }
            }
        }
    }

    /**
     * Calculate the target Y level for crater carving at given position.
     * Returns -1 if position is outside crater influence.
     */
    private int calculateCraterY(int worldX, int worldZ) {
        double dx = worldX - centerX;
        double dz = worldZ - centerZ;
        double distance = Math.sqrt(dx * dx + dz * dz);

        if (distance > radius + rimHeight) {
            return -1; // Outside crater
        }

        if (distance <= radius) {
            // Bowl interior: deeper at center
            double depthFactor = 1.0 - (distance / radius);
            // Smooth parabolic curve
            depthFactor = depthFactor * depthFactor;
            int carveDepth = (int) (depth * depthFactor);
            return centerY - carveDepth;
        } else {
            // Rim zone: raised edge
            double rimDistance = distance - radius;
            double rimFactor = 1.0 - (rimDistance / rimHeight);
            // Smooth falloff
            rimFactor = Math.max(0, rimFactor);
            int elevation = (int) (rimHeight * rimFactor);
            return centerY + elevation;
        }
    }

    /**
     * Check if block should be preserved (not carved).
     */
    private boolean shouldPreserveBlock(IBlockState state) {
        return state.getBlock() == Blocks.BEDROCK;
    }

    /**
     * Check if this crater affects a given chunk.
     */
    public boolean affectsChunk(int chunkX, int chunkZ) {
        int chunkStartX = chunkX * 16;
        int chunkStartZ = chunkZ * 16;
        int chunkEndX = chunkStartX + 15;
        int chunkEndZ = chunkStartZ + 15;

        int influenceRadius = radius + rimHeight + 2;

        // Check if crater center is within influence distance of chunk bounds
        boolean xOverlap = centerX + influenceRadius >= chunkStartX && centerX - influenceRadius <= chunkEndX;
        boolean zOverlap = centerZ + influenceRadius >= chunkStartZ && centerZ - influenceRadius <= chunkEndZ;

        return xOverlap && zOverlap;
    }

    public int getCenterX() {
        return centerX;
    }

    public int getCenterZ() {
        return centerZ;
    }

    public int getRadius() {
        return radius;
    }

    /**
     * Generate a random crater with typical parameters.
     */
    public static CraterCarver createRandom(Random random, int centerX, int centerZ, int surfaceY) {
        int radius = 5 + random.nextInt(20); // 5-25 blocks
        int depth = Math.max(3, radius / 3 + random.nextInt(5)); // 3-15 blocks typically
        int rimHeight = 1 + random.nextInt(3); // 1-3 blocks
        return new CraterCarver(centerX, centerZ, surfaceY, radius, depth, rimHeight);
    }

    /**
     * Generate a small crater.
     */
    public static CraterCarver createSmall(Random random, int centerX, int centerZ, int surfaceY) {
        int radius = 5 + random.nextInt(10); // 5-15 blocks
        int depth = Math.max(3, radius / 3); // 2-5 blocks
        int rimHeight = 1;
        return new CraterCarver(centerX, centerZ, surfaceY, radius, depth, rimHeight);
    }

    /**
     * Generate a large crater.
     */
    public static CraterCarver createLarge(Random random, int centerX, int centerZ, int surfaceY) {
        int radius = 20 + random.nextInt(30); // 20-50 blocks
        int depth = Math.max(8, radius / 3 + random.nextInt(8)); // 10-25 blocks
        int rimHeight = 2 + random.nextInt(2); // 2-3 blocks
        return new CraterCarver(centerX, centerZ, surfaceY, radius, depth, rimHeight);
    }
}
