package earth.terrarium.adastra.common.network.packet;

import earth.terrarium.adastra.api.systems.PlanetData;
import earth.terrarium.adastra.client.systems.ClientData;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PacketSyncLocalPlanetData implements IMessage {

    private PlanetData data;

    public PacketSyncLocalPlanetData() {
    }

    public PacketSyncLocalPlanetData(PlanetData data) {
        this.data = data;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        data = PlanetData.unpack(buf.readInt());
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(data.pack());
    }

    public static class Handler implements IMessageHandler<PacketSyncLocalPlanetData, IMessage> {

        @Override
        public IMessage onMessage(PacketSyncLocalPlanetData message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> handleClientSide(message));
            return null;
        }

        @SideOnly(Side.CLIENT)
        private static void handleClientSide(PacketSyncLocalPlanetData message) {
            ClientData.updateLocalData(message.data);
        }
    }
}
