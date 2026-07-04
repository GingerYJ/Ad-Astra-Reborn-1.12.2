package earth.terrarium.adastra.common.network.packet;

import earth.terrarium.adastra.AdAstraReborn;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketOpenPlanetSelection implements IMessage {

    private int rocketTier;
    private int rocketEntityId;

    public PacketOpenPlanetSelection() {
    }

    public PacketOpenPlanetSelection(int rocketTier) {
        this(rocketTier, -1);
    }

    public PacketOpenPlanetSelection(int rocketTier, int rocketEntityId) {
        this.rocketTier = rocketTier;
        this.rocketEntityId = rocketEntityId;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        rocketTier = buf.readInt();
        rocketEntityId = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(rocketTier);
        buf.writeInt(rocketEntityId);
    }

    public static class Handler implements IMessageHandler<PacketOpenPlanetSelection, IMessage> {

        @Override
        public IMessage onMessage(PacketOpenPlanetSelection message, MessageContext ctx) {
            AdAstraReborn.proxy.openPlanetSelection(message.rocketTier, message.rocketEntityId);
            return null;
        }
    }
}
