package earth.terrarium.adastra.common.network.packet;

import earth.terrarium.adastra.common.entities.vehicles.RoverEntity;
import earth.terrarium.adastra.common.tile.RadioTileEntity;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketSetRoverRadioStation implements IMessage {

    private static final double MAX_DISTANCE_SQ = 64.0D;

    private int entityId;
    private String station;

    public PacketSetRoverRadioStation() {
    }

    public PacketSetRoverRadioStation(int entityId, String station) {
        this.entityId = entityId;
        this.station = RadioTileEntity.normalizeStation(station);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        entityId = buf.readInt();
        station = RadioTileEntity.normalizeStation(ByteBufUtils.readUTF8String(buf));
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(entityId);
        ByteBufUtils.writeUTF8String(buf, station);
    }

    public static class Handler implements IMessageHandler<PacketSetRoverRadioStation, IMessage> {

        @Override
        public IMessage onMessage(PacketSetRoverRadioStation message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().player;
            player.getServerWorld().addScheduledTask(() -> handle(message, player));
            return null;
        }

        private void handle(PacketSetRoverRadioStation message, EntityPlayerMP player) {
            Entity entity = player.world.getEntityByID(message.entityId);
            if (!(entity instanceof RoverEntity)) {
                return;
            }

            RoverEntity rover = (RoverEntity) entity;
            if (!rover.isPassenger(player) && player.getDistanceSq(rover) > MAX_DISTANCE_SQ) {
                return;
            }

            rover.setRadioUrl(message.station);
        }
    }
}
