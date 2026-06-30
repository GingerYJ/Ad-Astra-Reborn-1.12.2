package earth.terrarium.adastra.common.network.packet;

import earth.terrarium.adastra.common.util.KeybindManager;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketSyncKeybinds implements IMessage {

    private boolean jumping;
    private boolean sprinting;
    private boolean suitFlightEnabled;

    public PacketSyncKeybinds() {
    }

    public PacketSyncKeybinds(boolean jumping, boolean sprinting, boolean suitFlightEnabled) {
        this.jumping = jumping;
        this.sprinting = sprinting;
        this.suitFlightEnabled = suitFlightEnabled;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        jumping = buf.readBoolean();
        sprinting = buf.readBoolean();
        suitFlightEnabled = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeBoolean(jumping);
        buf.writeBoolean(sprinting);
        buf.writeBoolean(suitFlightEnabled);
    }

    public static class Handler implements IMessageHandler<PacketSyncKeybinds, IMessage> {

        @Override
        public IMessage onMessage(PacketSyncKeybinds message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().player;
            player.getServerWorld().addScheduledTask(() -> KeybindManager.set(player, message.jumping, message.sprinting, message.suitFlightEnabled));
            return null;
        }
    }
}
