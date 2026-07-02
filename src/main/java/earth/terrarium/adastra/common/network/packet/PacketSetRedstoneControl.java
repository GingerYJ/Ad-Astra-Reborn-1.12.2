package earth.terrarium.adastra.common.network.packet;

import earth.terrarium.adastra.common.tile.AdAstraMachineTileEntity;
import earth.terrarium.adastra.common.tile.AdAstraRedstoneControl;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Network packet for setting machine redstone control mode.
 * Allows players to configure when a machine operates based on redstone signal.
 */
public class PacketSetRedstoneControl implements IMessage {

    private static final double MAX_DISTANCE_SQ = 64.0D;

    private BlockPos pos;
    private AdAstraRedstoneControl mode;

    public PacketSetRedstoneControl() {
    }

    /**
     * Creates a packet to set redstone control mode.
     *
     * @param pos Machine position
     * @param mode The redstone control mode to set
     */
    public PacketSetRedstoneControl(BlockPos pos, AdAstraRedstoneControl mode) {
        this.pos = pos;
        this.mode = mode;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        pos = BlockPos.fromLong(buf.readLong());
        mode = AdAstraRedstoneControl.values()[buf.readByte()];
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeLong(pos.toLong());
        buf.writeByte(mode.ordinal());
    }

    public static class Handler implements IMessageHandler<PacketSetRedstoneControl, IMessage> {

        @Override
        public IMessage onMessage(PacketSetRedstoneControl message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().player;
            player.getServerWorld().addScheduledTask(() -> handle(message, player));
            return null;
        }

        private void handle(PacketSetRedstoneControl message, EntityPlayerMP player) {
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

            // Validate mode
            if (message.mode == null) {
                return;
            }

            // Apply the redstone control mode
            machine.setRedstoneControl(message.mode);
        }
    }
}
