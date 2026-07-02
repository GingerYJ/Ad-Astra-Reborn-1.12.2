package earth.terrarium.adastra.common.network.packet;

import earth.terrarium.adastra.common.tile.AdAstraMachineTileEntity;
import earth.terrarium.adastra.common.tile.AdAstraSideMode;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Network packet for setting machine side configuration.
 * Allows players to configure how each side of a machine handles items, energy, and fluids.
 */
public class PacketSetSideConfig implements IMessage {

    private static final double MAX_DISTANCE_SQ = 64.0D;

    private BlockPos pos;
    private EnumFacing side;
    private AdAstraMachineTileEntity.SideConfigType configType;
    private AdAstraSideMode mode;

    public PacketSetSideConfig() {
    }

    /**
     * Creates a packet to set side configuration.
     *
     * @param pos Machine position
     * @param side The side to configure
     * @param configType The type of configuration (ITEM, ENERGY, or FLUID)
     * @param mode The mode to set (NONE, PUSH, PULL, or PUSH_PULL)
     */
    public PacketSetSideConfig(BlockPos pos, EnumFacing side,
                              AdAstraMachineTileEntity.SideConfigType configType,
                              AdAstraSideMode mode) {
        this.pos = pos;
        this.side = side;
        this.configType = configType;
        this.mode = mode;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        pos = BlockPos.fromLong(buf.readLong());
        side = EnumFacing.values()[buf.readByte()];
        configType = AdAstraMachineTileEntity.SideConfigType.values()[buf.readByte()];
        mode = AdAstraSideMode.values()[buf.readByte()];
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeLong(pos.toLong());
        buf.writeByte(side.ordinal());
        buf.writeByte(configType.ordinal());
        buf.writeByte(mode.ordinal());
    }

    public static class Handler implements IMessageHandler<PacketSetSideConfig, IMessage> {

        @Override
        public IMessage onMessage(PacketSetSideConfig message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().player;
            player.getServerWorld().addScheduledTask(() -> handle(message, player));
            return null;
        }

        private void handle(PacketSetSideConfig message, EntityPlayerMP player) {
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

            // Validate config type and side
            if (message.side == null || message.configType == null || message.mode == null) {
                return;
            }

            // Apply the configuration
            machine.setSideMode(message.side, message.configType, message.mode);
        }
    }
}
