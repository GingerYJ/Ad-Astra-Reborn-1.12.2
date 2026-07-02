package earth.terrarium.adastra.common.network.packet;

import earth.terrarium.adastra.common.tile.GravityNormalizerTileEntity;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Network packet for setting gravity normalizer target gravity.
 * Allows players to configure the target gravity value for a gravity normalizer.
 */
public class PacketSetGravityNormalizerTarget implements IMessage {

    private static final double MAX_DISTANCE_SQ = 64.0D;

    private BlockPos pos;
    private float targetGravity;

    public PacketSetGravityNormalizerTarget() {
    }

    /**
     * Creates a packet to set gravity normalizer target.
     *
     * @param pos Machine position
     * @param targetGravity Target gravity value (0.0 to 1.0)
     */
    public PacketSetGravityNormalizerTarget(BlockPos pos, float targetGravity) {
        this.pos = pos;
        this.targetGravity = targetGravity;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        pos = BlockPos.fromLong(buf.readLong());
        targetGravity = buf.readFloat();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeLong(pos.toLong());
        buf.writeFloat(targetGravity);
    }

    public static class Handler implements IMessageHandler<PacketSetGravityNormalizerTarget, IMessage> {

        @Override
        public IMessage onMessage(PacketSetGravityNormalizerTarget message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().player;
            player.getServerWorld().addScheduledTask(() -> handle(message, player));
            return null;
        }

        private void handle(PacketSetGravityNormalizerTarget message, EntityPlayerMP player) {
            World world = player.world;
            if (message.pos == null || world == null || !world.isBlockLoaded(message.pos)) {
                return;
            }

            // Validate distance to prevent cheating
            if (player.getDistanceSq(message.pos.getX() + 0.5D, message.pos.getY() + 0.5D, message.pos.getZ() + 0.5D) > MAX_DISTANCE_SQ) {
                return;
            }

            TileEntity tile = world.getTileEntity(message.pos);
            if (!(tile instanceof GravityNormalizerTileEntity)) {
                return;
            }

            GravityNormalizerTileEntity normalizer = (GravityNormalizerTileEntity) tile;

            // Clamp target gravity to valid range (0.0 to 1.0)
            float clampedGravity = Math.max(0.0f, Math.min(1.0f, message.targetGravity));

            // Set target gravity
            normalizer.setTargetGravity(clampedGravity);
            normalizer.markDirty();
        }
    }
}
