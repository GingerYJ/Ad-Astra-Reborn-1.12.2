package earth.terrarium.adastra.common.network.packet;

import earth.terrarium.adastra.client.radio.audio.RadioHandler;
import earth.terrarium.adastra.common.tile.RadioTileEntity;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PacketPlayRadioStation implements IMessage {

    private String url;
    private boolean hasPos;
    private BlockPos pos;

    public PacketPlayRadioStation() {
    }

    public PacketPlayRadioStation(String url, BlockPos pos) {
        this.url = RadioTileEntity.normalizeStation(url);
        this.hasPos = pos != null;
        this.pos = pos;
    }

    public PacketPlayRadioStation(String url) {
        this(url, null);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        url = RadioTileEntity.normalizeStation(ByteBufUtils.readUTF8String(buf));
        hasPos = buf.readBoolean();
        pos = hasPos ? BlockPos.fromLong(buf.readLong()) : null;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, url);
        buf.writeBoolean(hasPos);
        if (hasPos) {
            buf.writeLong(pos.toLong());
        }
    }

    public static class Handler implements IMessageHandler<PacketPlayRadioStation, IMessage> {

        @Override
        public IMessage onMessage(PacketPlayRadioStation message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> handleClientSide(message));
            return null;
        }

        @SideOnly(Side.CLIENT)
        private static void handleClientSide(PacketPlayRadioStation message) {
            if (message.url == null || message.url.isEmpty()) {
                RadioHandler.stop();
            } else if (message.hasPos) {
                RadioHandler.play(message.url, message.pos);
            } else {
                RadioHandler.play(message.url);
            }
        }
    }
}
