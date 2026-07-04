package earth.terrarium.adastra.common.network.packet;

import earth.terrarium.adastra.common.tile.AdAstraMachineTileEntity;
import earth.terrarium.adastra.common.tile.FuelRefineryTileEntity;
import earth.terrarium.adastra.common.tile.OxygenLoaderTileEntity;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Network packet for clearing a machine's fluid tank.
 * Allows players to clear fluid tanks through the machine GUI.
 */
public class PacketClearFluidTank implements IMessage {

    private static final double MAX_DISTANCE_SQ = 64.0D;

    private BlockPos pos;
    private int tank;

    public PacketClearFluidTank() {
    }

    /**
     * Creates a packet to clear a fluid tank.
     *
     * @param pos Machine position
     */
    public PacketClearFluidTank(BlockPos pos) {
        this(pos, 0);
    }

    public PacketClearFluidTank(BlockPos pos, int tank) {
        this.pos = pos;
        this.tank = tank;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        pos = BlockPos.fromLong(buf.readLong());
        tank = buf.isReadable(4) ? buf.readInt() : 0;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeLong(pos.toLong());
        buf.writeInt(tank);
    }

    public static class Handler implements IMessageHandler<PacketClearFluidTank, IMessage> {

        @Override
        public IMessage onMessage(PacketClearFluidTank message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().player;
            player.getServerWorld().addScheduledTask(() -> handle(message, player));
            return null;
        }

        private void handle(PacketClearFluidTank message, EntityPlayerMP player) {
            World world = player.world;
            if (message.pos == null || world == null || !world.isBlockLoaded(message.pos)) {
                return;
            }

            // Validate distance to prevent cheating
            if (player.getDistanceSq(message.pos.getX() + 0.5D, message.pos.getY() + 0.5D, message.pos.getZ() + 0.5D) > MAX_DISTANCE_SQ) {
                return;
            }

            TileEntity tile = world.getTileEntity(message.pos);
            if (!(tile instanceof AdAstraMachineTileEntity)) {
                return;
            }

            AdAstraMachineTileEntity machine = (AdAstraMachineTileEntity) tile;
            FluidTank tank = getTank(machine, message.tank);

            if (tank != null && tank.getFluidAmount() > 0) {
                tank.setFluid(null);
                machine.markDirty();
            }
        }

        private FluidTank getTank(AdAstraMachineTileEntity machine, int tank) {
            if (machine instanceof FuelRefineryTileEntity) {
                FuelRefineryTileEntity refinery = (FuelRefineryTileEntity) machine;
                return tank == 1 ? refinery.getOutputTank() : refinery.getInputTank();
            }
            if (machine instanceof OxygenLoaderTileEntity) {
                OxygenLoaderTileEntity loader = (OxygenLoaderTileEntity) machine;
                return tank == 1 ? loader.getOutputTank() : loader.getInputTank();
            }
            return machine.getFluidTank();
        }
    }
}
