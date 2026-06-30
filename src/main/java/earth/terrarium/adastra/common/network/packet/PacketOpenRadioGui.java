package earth.terrarium.adastra.common.network.packet;

import earth.terrarium.adastra.AdAstraReborn;
import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketOpenRadioGui implements IMessage {

    private BlockPos pos;
    private String station;
    private boolean playing;

    public PacketOpenRadioGui() {
    }

    public PacketOpenRadioGui(BlockPos pos, String station, boolean playing) {
        this.pos = pos;
        this.station = station == null ? "" : station;
        this.playing = playing;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        pos = BlockPos.fromLong(buf.readLong());
        station = ByteBufUtils.readUTF8String(buf);
        playing = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeLong(pos.toLong());
        ByteBufUtils.writeUTF8String(buf, station);
        buf.writeBoolean(playing);
    }

    public static class Handler implements IMessageHandler<PacketOpenRadioGui, IMessage> {

        @Override
        public IMessage onMessage(PacketOpenRadioGui message, MessageContext ctx) {
            AdAstraReborn.proxy.openRadio(message.pos, message.station, message.playing);
            return null;
        }
    }
}
