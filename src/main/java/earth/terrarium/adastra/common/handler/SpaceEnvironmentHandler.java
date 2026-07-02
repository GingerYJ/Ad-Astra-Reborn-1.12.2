package earth.terrarium.adastra.common.handler;

import earth.terrarium.adastra.common.config.AdAstraConfig;
import earth.terrarium.adastra.common.util.EnvironmentUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFarmland;
import net.minecraft.block.BlockGrass;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.Iterator;

/**
 * Applies slow environmental block effects in airless planet dimensions.
 */
public class SpaceEnvironmentHandler {

    private static final short FREEZE_TEMPERATURE = -50;
    private static final short EVAPORATE_TEMPERATURE = 70;
    private static final int CHUNK_TICK_PHASES = 20;

    @SubscribeEvent
    public void onWorldTick(TickEvent.WorldTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        World world = event.world;
        if (world.isRemote || !(world instanceof WorldServer)) {
            return;
        }
        if (!AdAstraConfig.enableSpaceEnvironmentEffects) {
            return;
        }
        if (EnvironmentUtils.worldProviderHasOxygen(world)) {
            return;
        }

        int tickSpeed = AdAstraConfig.planetRandomTickSpeed;
        if (tickSpeed <= 0) {
            return;
        }

        WorldServer server = (WorldServer) world;
        long worldTime = world.getTotalWorldTime();
        Iterator<Chunk> chunks = server.getPersistentChunkIterable(server.getPlayerChunkMap().getChunkIterator());
        while (chunks.hasNext()) {
            Chunk chunk = chunks.next();
            if (shouldTickChunkThisPhase(chunk.getPos(), worldTime)) {
                tickChunk(world, chunk, tickSpeed);
            }
        }
    }

    private boolean shouldTickChunkThisPhase(ChunkPos chunkPos, long worldTime) {
        int phase = (int) (worldTime % CHUNK_TICK_PHASES);
        int chunkPhase = Math.floorMod(chunkPos.x * 31 + chunkPos.z * 17, CHUNK_TICK_PHASES);
        return chunkPhase == phase;
    }

    private void tickChunk(World world, Chunk chunk, int tickSpeed) {
        ChunkPos chunkPos = chunk.getPos();
        int baseX = chunkPos.getXStart();
        int baseZ = chunkPos.getZStart();
        int topY = Math.max(1, chunk.getTopFilledSegment() + 16);
        short temperature = EnvironmentUtils.getTemperature(world);

        for (int i = 0; i < tickSpeed; i++) {
            int x = baseX + world.rand.nextInt(16);
            int z = baseZ + world.rand.nextInt(16);
            int y = world.rand.nextInt(topY);
            BlockPos pos = new BlockPos(x, y, z);

            IBlockState state = chunk.getBlockState(pos);
            if (state.getBlock() == Blocks.AIR) {
                continue;
            }

            if (isWaterSource(state)) {
                if (temperature > EVAPORATE_TEMPERATURE) {
                    tickHot(world, pos);
                } else if (temperature < FREEZE_TEMPERATURE) {
                    tickCold(world, pos);
                }
            }

            if (needsOxygenEnvironmentTick(state)
                && !EnvironmentUtils.hasOxygen(world, pos, EnvironmentUtils.DEFAULT_ENVIRONMENT_SCAN_RADIUS)) {
                tickBlock(world, pos, state);
            }
        }
    }

    private void tickCold(World world, BlockPos pos) {
        world.setBlockState(pos, Blocks.ICE.getDefaultState());
    }

    private void tickHot(World world, BlockPos pos) {
        world.setBlockToAir(pos);
        world.playSound(null, pos, net.minecraft.init.SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS,
            0.5f, 2.6f + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.8f);
    }

    private void tickBlock(World world, BlockPos pos, IBlockState state) {
        Block block = state.getBlock();
        if (block instanceof BlockGrass || block instanceof BlockFarmland) {
            world.setBlockState(pos, Blocks.DIRT.getDefaultState());
        } else if (block == Blocks.FIRE) {
            world.setBlockToAir(pos);
        }
    }

    private boolean needsOxygenEnvironmentTick(IBlockState state) {
        Block block = state.getBlock();
        return block instanceof BlockGrass || block instanceof BlockFarmland || block == Blocks.FIRE;
    }

    private static boolean isWaterSource(IBlockState state) {
        Block block = state.getBlock();
        return block.getMaterial(state) == Material.WATER
            && block == Blocks.WATER
            && state.getValue(net.minecraft.block.BlockLiquid.LEVEL) == 0;
    }
}
