package earth.terrarium.adastra.common.network.packet;

import earth.terrarium.adastra.common.tile.RadioTileEntity;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketSetRadioStation implements IMessage {

    private static final double MAX_DISTANCE_SQ = 64.0D;

    private BlockPos pos;
    private String station;
    private boolean playing;

    public PacketSetRadioStation() {
    }

    public PacketSetRadioStation(BlockPos pos, String station, boolean playing) {
        this.pos = pos;
        this.station = RadioTileEntity.normalizeStation(station);
        this.playing = playing;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        pos = BlockPos.fromLong(buf.readLong());
        station = RadioTileEntity.normalizeStation(ByteBufUtils.readUTF8String(buf));
        playing = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeLong(pos.toLong());
        ByteBufUtils.writeUTF8String(buf, station);
        buf.writeBoolean(playing);
    }

    public static class Handler implements IMessageHandler<PacketSetRadioStation, IMessage> {

        @Override
        public IMessage onMessage(PacketSetRadioStation message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().player;
            player.getServerWorld().addScheduledTask(() -> handle(message, player));
            return null;
        }

        private void handle(PacketSetRadioStation message, EntityPlayerMP player) {
            World world = player.world;
            if (message.pos == null || world == null || !world.isBlockLoaded(message.pos)) {
                return;
            }
            if (player.getDistanceSq(message.pos.getX() + 0.5D, message.pos.getY() + 0.5D, message.pos.getZ() + 0.5D) > MAX_DISTANCE_SQ) {
                return;
            }

            TileEntity tile = world.getTileEntity(message.pos);
            if (tile instanceof RadioTileEntity) {
                ((RadioTileEntity) tile).setStationAndPlaying(message.station, message.playing);
            }
        }
    }
}
