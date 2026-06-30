package earth.terrarium.adastra.common.network.packet;

import earth.terrarium.adastra.common.entities.vehicles.AdAstraVehicleEntity;
import earth.terrarium.adastra.common.util.PlanetTravelHelper;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketLandPlanet implements IMessage {

    private int dimensionId;

    public PacketLandPlanet() {
    }

    public PacketLandPlanet(int dimensionId) {
        this.dimensionId = dimensionId;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        dimensionId = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(dimensionId);
    }

    public static class Handler implements IMessageHandler<PacketLandPlanet, IMessage> {

        @Override
        public IMessage onMessage(PacketLandPlanet message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().player;
            player.getServerWorld().addScheduledTask(() -> PlanetTravelHelper.landPlayer(player, message.dimensionId, getRocketTier(player)));
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
