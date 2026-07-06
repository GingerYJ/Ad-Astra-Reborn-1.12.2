package earth.terrarium.adastra.common.tile;

import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TileEntityTritonGeyser extends TileEntity implements ITickable {

    private int tickCounter;

    @Override
    public void update() {
        if (world == null || world.isRemote) return;
        tickCounter++;
        if (tickCounter % 50 == 0) {
            world.scheduleBlockUpdate(pos, blockType, 1, 0);
        }
    }

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
        return oldState.getBlock() != newState.getBlock();
    }
}
