package earth.terrarium.adastra.common.network.packet;

import earth.terrarium.adastra.common.entities.vehicles.VehicleBase;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Packet to sync vehicle fuel and inventory data to client.
 */
public class PacketSyncVehicle implements IMessage {

    private int entityId;
    private int fuelAmount;
    private int fuelCapacity;
    private boolean isLaunching;
    private int launchCountdown;
    private boolean hasLaunched;

    public PacketSyncVehicle() {
    }

    public PacketSyncVehicle(VehicleBase vehicle) {
        this.entityId = vehicle.getEntityId();
        this.fuelAmount = vehicle.getFluidFuelAmount();
        this.fuelCapacity = vehicle.getFluidFuelCapacity();
        this.isLaunching = vehicle.isLaunching();
        this.launchCountdown = vehicle.getLaunchCountdown();
        this.hasLaunched = vehicle.hasLaunched();
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        entityId = buf.readInt();
        fuelAmount = buf.readInt();
        fuelCapacity = buf.readInt();
        isLaunching = buf.readBoolean();
        launchCountdown = buf.readInt();
        hasLaunched = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(entityId);
        buf.writeInt(fuelAmount);
        buf.writeInt(fuelCapacity);
        buf.writeBoolean(isLaunching);
        buf.writeInt(launchCountdown);
        buf.writeBoolean(hasLaunched);
    }

    public static class Handler implements IMessageHandler<PacketSyncVehicle, IMessage> {

        @Override
        public IMessage onMessage(PacketSyncVehicle message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> handleClient(message));
            return null;
        }

        @SideOnly(Side.CLIENT)
        private void handleClient(PacketSyncVehicle message) {
            Entity entity = Minecraft.getMinecraft().world.getEntityByID(message.entityId);
            if (entity instanceof VehicleBase) {
                VehicleBase vehicle = (VehicleBase) entity;
                // Sync data to client
                // Note: Since fields are protected/private, this would need getters/setters
                // For now, this serves as a template
            }
        }
    }
}
