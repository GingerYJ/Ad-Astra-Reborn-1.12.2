package earth.terrarium.adastra.common.network.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Packet to open vehicle inventory GUI on client.
 */
public class PacketOpenVehicleGui implements IMessage {

    public enum VehicleGuiType {
        ROCKET,
        ROVER,
        LANDER
    }

    private int vehicleEntityId;
    private VehicleGuiType guiType;

    public PacketOpenVehicleGui() {
    }

    public PacketOpenVehicleGui(int vehicleEntityId, VehicleGuiType guiType) {
        this.vehicleEntityId = vehicleEntityId;
        this.guiType = guiType;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        vehicleEntityId = buf.readInt();
        guiType = VehicleGuiType.values()[buf.readByte()];
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(vehicleEntityId);
        buf.writeByte(guiType.ordinal());
    }

    public static class Handler implements IMessageHandler<PacketOpenVehicleGui, IMessage> {

        @Override
        public IMessage onMessage(PacketOpenVehicleGui message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> handleClient(message));
            return null;
        }

        @SideOnly(Side.CLIENT)
        private void handleClient(PacketOpenVehicleGui message) {
            // TODO: Open appropriate GUI based on guiType
            // This will be implemented when GUI classes are created
        }
    }
}
