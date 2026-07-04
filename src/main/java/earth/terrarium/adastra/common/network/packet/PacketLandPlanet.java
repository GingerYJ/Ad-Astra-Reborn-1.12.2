package earth.terrarium.adastra.common.network.packet;

import earth.terrarium.adastra.common.entities.vehicles.AdAstraVehicleEntity;
import earth.terrarium.adastra.common.entities.vehicles.RocketEntity;
import earth.terrarium.adastra.common.util.PlanetTravelHelper;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketLandPlanet implements IMessage {

    private int dimensionId;
    private int rocketTier;
    private int rocketEntityId;

    public PacketLandPlanet() {
    }

    public PacketLandPlanet(int dimensionId) {
        this(dimensionId, 0, -1);
    }

    public PacketLandPlanet(int dimensionId, int rocketTier, int rocketEntityId) {
        this.dimensionId = dimensionId;
        this.rocketTier = rocketTier;
        this.rocketEntityId = rocketEntityId;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        dimensionId = buf.readInt();
        rocketTier = buf.readInt();
        rocketEntityId = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(dimensionId);
        buf.writeInt(rocketTier);
        buf.writeInt(rocketEntityId);
    }

    public static class Handler implements IMessageHandler<PacketLandPlanet, IMessage> {

        @Override
        public IMessage onMessage(PacketLandPlanet message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().player;
            player.getServerWorld().addScheduledTask(() -> {
                RocketEntity rocket = getRocket(player, message.rocketEntityId);
                int tier = Math.max(message.rocketTier, getRocketTier(player, rocket));
                ItemStack rocketStack = rocket == null ? ItemStack.EMPTY : rocket.getDropStack();
                PlanetTravelHelper.landPlayer(player, message.dimensionId, tier, rocket, rocketStack);
            });
            return null;
        }

        private RocketEntity getRocket(EntityPlayerMP player, int rocketEntityId) {
            Entity riding = player.getRidingEntity();
            if (riding instanceof RocketEntity) {
                return (RocketEntity) riding;
            }
            if (rocketEntityId >= 0) {
                Entity entity = player.world.getEntityByID(rocketEntityId);
                if (entity instanceof RocketEntity) {
                    return (RocketEntity) entity;
                }
            }
            return null;
        }

        private int getRocketTier(EntityPlayerMP player, RocketEntity rocket) {
            if (rocket != null) {
                return rocket.getRocketTier();
            }
            Entity riding = player.getRidingEntity();
            if (riding instanceof AdAstraVehicleEntity) {
                return ((AdAstraVehicleEntity) riding).getRocketTier();
            }
            return 0;
        }
    }
}
