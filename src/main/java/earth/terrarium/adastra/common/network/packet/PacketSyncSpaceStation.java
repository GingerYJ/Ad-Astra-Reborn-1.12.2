package earth.terrarium.adastra.common.network.packet;

import earth.terrarium.adastra.common.util.SpaceStationClientState;
import earth.terrarium.adastra.common.world.data.GlobalSpaceStationData;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/** Synchronizes the one global station to a client. */
public class PacketSyncSpaceStation implements IMessage {

    private boolean constructed;
    private String name;
    private int x;
    private int y;
    private int z;

    public PacketSyncSpaceStation() {
    }

    public PacketSyncSpaceStation(GlobalSpaceStationData data) {
        constructed = data != null && data.isConstructed();
        name = data == null ? "space_station" : data.getName();
        BlockPos position = data == null ? null : data.getPosition();
        if (position != null) {
            x = position.getX();
            y = position.getY();
            z = position.getZ();
        }
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        constructed = buf.readBoolean();
        name = ByteBufUtils.readUTF8String(buf);
        x = buf.readInt();
        y = buf.readInt();
        z = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeBoolean(constructed);
        ByteBufUtils.writeUTF8String(buf, name == null ? "space_station" : name);
        buf.writeInt(x);
        buf.writeInt(y);
        buf.writeInt(z);
    }

    public static class Handler implements IMessageHandler<PacketSyncSpaceStation, IMessage> {

        @Override
        @SideOnly(Side.CLIENT)
        public IMessage onMessage(PacketSyncSpaceStation message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> SpaceStationClientState.update(
                message.constructed,
                message.name,
                new BlockPos(message.x, message.y, message.z)));
            return null;
        }
    }
}
