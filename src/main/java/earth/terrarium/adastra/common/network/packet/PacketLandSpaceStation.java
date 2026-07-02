package earth.terrarium.adastra.common.network.packet;

import earth.terrarium.adastra.common.util.SpaceStationHelper;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketLandSpaceStation implements IMessage {

    private int orbitDimensionId;
    private int x;
    private int y;
    private int z;

    public PacketLandSpaceStation() {
    }

    public PacketLandSpaceStation(int orbitDimensionId, BlockPos stationPos) {
        this.orbitDimensionId = orbitDimensionId;
        this.x = stationPos.getX();
        this.y = stationPos.getY();
        this.z = stationPos.getZ();
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        orbitDimensionId = buf.readInt();
        x = buf.readInt();
        y = buf.readInt();
        z = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(orbitDimensionId);
        buf.writeInt(x);
        buf.writeInt(y);
        buf.writeInt(z);
    }

    public static class Handler implements IMessageHandler<PacketLandSpaceStation, IMessage> {

        @Override
        public IMessage onMessage(PacketLandSpaceStation message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().player;
            player.getServerWorld().addScheduledTask(
                () -> SpaceStationHelper.landOnSpaceStation(
                    player,
                    message.orbitDimensionId,
                    new BlockPos(message.x, message.y, message.z)));
            return null;
        }
    }
}
