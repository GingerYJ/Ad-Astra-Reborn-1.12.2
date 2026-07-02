package earth.terrarium.adastra.common.network.packet;

import earth.terrarium.adastra.common.entities.vehicles.AdAstraVehicleEntity;
import earth.terrarium.adastra.common.util.SpaceStationHelper;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketConstructSpaceStation implements IMessage {

    private int planetDimensionId;

    public PacketConstructSpaceStation() {
    }

    public PacketConstructSpaceStation(int planetDimensionId) {
        this.planetDimensionId = planetDimensionId;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        planetDimensionId = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(planetDimensionId);
    }

    public static class Handler implements IMessageHandler<PacketConstructSpaceStation, IMessage> {

        @Override
        public IMessage onMessage(PacketConstructSpaceStation message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().player;
            player.getServerWorld().addScheduledTask(
                () -> SpaceStationHelper.constructSpaceStation(player, message.planetDimensionId, getRocketTier(player)));
            return null;
        }

        private int getRocketTier(EntityPlayerMP player) {
            Entity riding = player.getRidingEntity();
            if (riding instanceof AdAstraVehicleEntity) {
                return ((AdAstraVehicleEntity) riding).getRocketTier();
            }
            return 0;
        }
    }
}
