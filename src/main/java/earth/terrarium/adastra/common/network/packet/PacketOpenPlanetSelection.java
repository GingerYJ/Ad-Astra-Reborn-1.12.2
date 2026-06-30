package earth.terrarium.adastra.common.network.packet;

import earth.terrarium.adastra.AdAstraReborn;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketOpenPlanetSelection implements IMessage {

    private int rocketTier;

    public PacketOpenPlanetSelection() {
    }

    public PacketOpenPlanetSelection(int rocketTier) {
        this.rocketTier = rocketTier;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        rocketTier = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(rocketTier);
    }

    public static class Handler implements IMessageHandler<PacketOpenPlanetSelection, IMessage> {

        @Override
        public IMessage onMessage(PacketOpenPlanetSelection message, MessageContext ctx) {
            AdAstraReborn.proxy.openPlanetSelection(message.rocketTier);
            return null;
        }
    }
}
