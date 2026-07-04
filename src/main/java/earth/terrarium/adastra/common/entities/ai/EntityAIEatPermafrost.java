package earth.terrarium.adastra.common.entities.ai;

import earth.terrarium.adastra.common.entities.mob.GlacianRamEntity;
import earth.terrarium.adastra.common.registry.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EntityAIEatPermafrost extends EntityAIBase {

    private final GlacianRamEntity ram;
    private final World world;
    private int timer;

    public EntityAIEatPermafrost(GlacianRamEntity ram) {
        this.ram = ram;
        this.world = ram.world;
        setMutexBits(7);
    }

    @Override
    public boolean shouldExecute() {
        if (ram.getRNG().nextInt(ram.isChild() ? 50 : 1000) != 0) {
            return false;
        }

        BlockPos pos = new BlockPos(ram);
        return isPermafrost(pos) || isPermafrost(pos.down());
    }

    @Override
    public void startExecuting() {
        timer = 40;
        ram.setEatTimer(timer);
        world.setEntityState(ram, (byte) 10);
        ram.getNavigator().clearPath();
    }

    @Override
    public void resetTask() {
        timer = 0;
        ram.setEatTimer(0);
    }

    @Override
    public boolean shouldContinueExecuting() {
        return timer > 0;
    }

    @Override
    public void updateTask() {
        timer = Math.max(0, timer - 1);
        ram.setEatTimer(timer);

        if (timer == 4) {
            BlockPos pos = new BlockPos(ram);
            if (eatAt(pos)) {
                return;
            }
            eatAt(pos.down());
        }
    }

    public int getTimer() {
        return timer;
    }

    private boolean eatAt(BlockPos pos) {
        IBlockState state = world.getBlockState(pos);
        if (state.getBlock() != ModBlocks.PERMAFROST) {
            return false;
        }

        if (world.getGameRules().getBoolean("mobGriefing")) {
            world.playEvent(2001, pos, Block.getStateId(state));
            world.setBlockToAir(pos);
        }

        ram.onEatPermafrost();
        return true;
    }

    private boolean isPermafrost(BlockPos pos) {
        return world.getBlockState(pos).getBlock() == ModBlocks.PERMAFROST;
    }
}
