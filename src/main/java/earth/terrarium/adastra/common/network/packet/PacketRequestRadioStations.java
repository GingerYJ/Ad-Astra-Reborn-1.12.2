package earth.terrarium.adastra.common.network.packet;

import earth.terrarium.adastra.common.network.NetworkHandler;
import earth.terrarium.adastra.common.util.radio.StationLoader;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketRequestRadioStations implements IMessage {

    @Override
    public void fromBytes(ByteBuf buf) {
    }

    @Override
    public void toBytes(ByteBuf buf) {
    }

    public static class Handler implements IMessageHandler<PacketRequestRadioStations, IMessage> {

        @Override
        public IMessage onMessage(PacketRequestRadioStations message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().player;
            player.getServerWorld().addScheduledTask(() ->
                NetworkHandler.CHANNEL.sendTo(new PacketSyncRadioStations(StationLoader.stations()), player));
            return null;
        }
    }
}
