package earth.terrarium.adastra.common.network.packet;

import earth.terrarium.adastra.AdAstraReborn;
import earth.terrarium.adastra.ModGuiHandler;
import earth.terrarium.adastra.client.gui.RoverRadioGui;
import earth.terrarium.adastra.common.entities.vehicles.RoverEntity;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
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
        LANDER,
        ROVER_RADIO
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
            Minecraft minecraft = Minecraft.getMinecraft();
            EntityPlayer player = minecraft.player;
            if (player == null) {
                return;
            }

            World world = player.world;
            if (message.guiType == VehicleGuiType.ROVER_RADIO) {
                Entity entity = world.getEntityByID(message.vehicleEntityId);
                if (entity instanceof RoverEntity) {
                    minecraft.displayGuiScreen(new RoverRadioGui((RoverEntity) entity, minecraft.currentScreen));
                }
                return;
            }

            int guiId = guiIdFor(message.guiType);
            if (guiId >= 0) {
                player.openGui(AdAstraReborn.instance, guiId, world, message.vehicleEntityId, 0, 0);
            }
        }

        private static int guiIdFor(VehicleGuiType type) {
            switch (type) {
                case ROCKET:
                    return ModGuiHandler.ROCKET_GUI;
                case ROVER:
                    return ModGuiHandler.ROVER_GUI;
                case LANDER:
                    return ModGuiHandler.LANDER_GUI;
                default:
                    return -1;
            }
        }
    }
}
