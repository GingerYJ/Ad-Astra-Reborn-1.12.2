package earth.terrarium.adastra.common.network.packet;

import earth.terrarium.adastra.common.util.SpaceStationHelper;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/** Requests entry to the server-wide station; no client destination data is trusted. */
public class PacketLandSpaceStation implements IMessage {

    public PacketLandSpaceStation() {
    }

    @Override
    public void fromBytes(ByteBuf buf) {
    }

    @Override
    public void toBytes(ByteBuf buf) {
    }

    public static class Handler implements IMessageHandler<PacketLandSpaceStation, IMessage> {

        @Override
        public IMessage onMessage(PacketLandSpaceStation message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().player;
            player.getServerWorld().addScheduledTask(() -> SpaceStationHelper.landOnSpaceStation(player));
            return null;
        }
    }
}
