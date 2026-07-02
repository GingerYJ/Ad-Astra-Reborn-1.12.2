package earth.terrarium.adastra.common.network.packet;

import earth.terrarium.adastra.client.systems.ClientEnvironmentCache;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Network packet for syncing environment systems to clients.
 * Synchronizes oxygen distributor coverage, gravity normalizer areas, and temperature zones.
 */
public class PacketSyncEnvironment implements IMessage {

    private EnvironmentType type;
    private BlockPos sourcePos;
    private int radius;
    private boolean active;
    private float value; // For gravity scale or temperature value

    public PacketSyncEnvironment() {
    }

    /**
     * Creates a packet to sync environment data.
     *
     * @param type Type of environment system
     * @param sourcePos Position of the source block
     * @param radius Coverage radius
     * @param active Whether the system is active
     * @param value Additional value (gravity scale, temperature, etc.)
     */
    public PacketSyncEnvironment(EnvironmentType type, BlockPos sourcePos,
                                int radius, boolean active, float value) {
        this.type = type;
        this.sourcePos = sourcePos;
        this.radius = radius;
        this.active = active;
        this.value = value;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        type = EnvironmentType.values()[buf.readByte()];
        sourcePos = BlockPos.fromLong(buf.readLong());
        radius = buf.readInt();
        active = buf.readBoolean();
        value = buf.readFloat();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeByte(type.ordinal());
        buf.writeLong(sourcePos.toLong());
        buf.writeInt(radius);
        buf.writeBoolean(active);
        buf.writeFloat(value);
    }

    public static class Handler implements IMessageHandler<PacketSyncEnvironment, IMessage> {

        @Override
        public IMessage onMessage(PacketSyncEnvironment message, MessageContext ctx) {
            // Handle on client side
            Minecraft.getMinecraft().addScheduledTask(() -> handleClientSide(message));
            return null;
        }

        @SideOnly(Side.CLIENT)
        private static void handleClientSide(PacketSyncEnvironment message) {
            if (message.sourcePos == null || message.type == null) {
                return;
            }

            // Update client-side environment cache
            switch (message.type) {
                case OXYGEN_DISTRIBUTOR:
                    if (message.active) {
                        ClientEnvironmentCache.addOxygenZone(message.sourcePos, message.radius);
                    } else {
                        ClientEnvironmentCache.removeOxygenZone(message.sourcePos);
                    }
                    break;

                case GRAVITY_NORMALIZER:
                    if (message.active) {
                        ClientEnvironmentCache.addGravityZone(message.sourcePos, message.radius, message.value);
                    } else {
                        ClientEnvironmentCache.removeGravityZone(message.sourcePos);
                    }
                    break;

                case TEMPERATURE_ZONE:
                    if (message.active) {
                        ClientEnvironmentCache.addTemperatureZone(message.sourcePos, message.radius, message.value);
                    } else {
                        ClientEnvironmentCache.removeTemperatureZone(message.sourcePos);
                    }
                    break;
            }

            // Trigger HUD update if needed
            ClientEnvironmentCache.markDirty();
        }
    }

    public enum EnvironmentType {
        OXYGEN_DISTRIBUTOR,
        GRAVITY_NORMALIZER,
        TEMPERATURE_ZONE
    }
}
