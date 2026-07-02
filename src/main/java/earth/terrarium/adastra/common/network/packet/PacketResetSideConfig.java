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
 * Network packet for resetting machine side configuration to defaults.
 * Allows players to reset all side configurations on a machine.
 */
public class PacketResetSideConfig implements IMessage {

    private static final double MAX_DISTANCE_SQ = 64.0D;

    private BlockPos pos;

    public PacketResetSideConfig() {
    }

    /**
     * Creates a packet to reset side configuration.
     *
     * @param pos Machine position
     */
    public PacketResetSideConfig(BlockPos pos) {
        this.pos = pos;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        pos = BlockPos.fromLong(buf.readLong());
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeLong(pos.toLong());
    }

    public static class Handler implements IMessageHandler<PacketResetSideConfig, IMessage> {

        @Override
        public IMessage onMessage(PacketResetSideConfig message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().player;
            player.getServerWorld().addScheduledTask(() -> handle(message, player));
            return null;
        }

        private void handle(PacketResetSideConfig message, EntityPlayerMP player) {
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

            // Reset all side configurations to defaults
            for (EnumFacing side : EnumFacing.values()) {
                // Reset item sides to NONE
                machine.setSideMode(side, AdAstraMachineTileEntity.SideConfigType.ITEM, AdAstraSideMode.NONE);

                // Reset energy sides to NONE (or PUSH for generators)
                machine.setSideMode(side, AdAstraMachineTileEntity.SideConfigType.ENERGY, AdAstraSideMode.NONE);

                // Reset fluid sides to NONE
                machine.setSideMode(side, AdAstraMachineTileEntity.SideConfigType.FLUID, AdAstraSideMode.NONE);
            }

            machine.markDirty();
        }
    }
}
