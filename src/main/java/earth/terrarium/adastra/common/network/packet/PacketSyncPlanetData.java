package earth.terrarium.adastra.common.network.packet;

import earth.terrarium.adastra.AdAstraReborn;
import earth.terrarium.adastra.common.systems.PlanetData;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Network packet for syncing planet environmental data to clients.
 * Sends local area data (configurable radius) around the player.
 */
public class PacketSyncPlanetData implements IMessage {

    private BlockPos centerPos;
    private int radius;
    private int[] positions; // Relative positions encoded as ints
    private int[] packedData; // Packed PlanetData

    public PacketSyncPlanetData() {
    }

    /**
     * Creates a packet to sync planet data around a center position.
     *
     * @param centerPos The center position (usually player position)
     * @param radius The radius to sync (in blocks)
     * @param dataMap Map of positions to planet data (relative to centerPos)
     */
    public PacketSyncPlanetData(BlockPos centerPos, int radius, java.util.Map<BlockPos, PlanetData> dataMap) {
        this.centerPos = centerPos;
        this.radius = radius;

        // Convert map to arrays for efficient transmission
        this.positions = new int[dataMap.size()];
        this.packedData = new int[dataMap.size()];

        int index = 0;
        for (java.util.Map.Entry<BlockPos, PlanetData> entry : dataMap.entrySet()) {
            BlockPos pos = entry.getKey();
            // Encode relative position as single int (assumes radius < 128)
            int dx = pos.getX() - centerPos.getX();
            int dy = pos.getY() - centerPos.getY();
            int dz = pos.getZ() - centerPos.getZ();

            // Pack relative coords into int: 8 bits each for x, y, z (signed)
            this.positions[index] = ((dx & 0xFF) << 16) | ((dy & 0xFF) << 8) | (dz & 0xFF);
            this.packedData[index] = entry.getValue().pack();
            index++;
        }
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        // Read center position
        centerPos = BlockPos.fromLong(buf.readLong());
        radius = buf.readInt();

        // Read arrays
        int count = buf.readInt();
        positions = new int[count];
        packedData = new int[count];

        for (int i = 0; i < count; i++) {
            positions[i] = buf.readInt();
            packedData[i] = buf.readInt();
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        // Write center position
        buf.writeLong(centerPos.toLong());
        buf.writeInt(radius);

        // Write arrays
        buf.writeInt(positions.length);
        for (int i = 0; i < positions.length; i++) {
            buf.writeInt(positions[i]);
            buf.writeInt(packedData[i]);
        }
    }

    public static class Handler implements IMessageHandler<PacketSyncPlanetData, IMessage> {

        @Override
        public IMessage onMessage(PacketSyncPlanetData message, MessageContext ctx) {
            // Handle on client side
            Minecraft.getMinecraft().addScheduledTask(() -> handleClientSide(message));
            return null;
        }

        @SideOnly(Side.CLIENT)
        private static void handleClientSide(PacketSyncPlanetData message) {
            EntityPlayer player = Minecraft.getMinecraft().player;
            if (player == null) return;

            // Store in client-side cache
            ClientPlanetDataCache cache = ClientPlanetDataCache.getInstance();
            if (cache == null) return;

            // Clear old data beyond radius
            cache.clearOldData(message.centerPos, message.radius);

            // Update with new data
            for (int i = 0; i < message.positions.length; i++) {
                // Unpack relative position
                int packed = message.positions[i];
                int dx = (byte) ((packed >> 16) & 0xFF); // Cast to byte for sign extension
                int dy = (byte) ((packed >> 8) & 0xFF);
                int dz = (byte) (packed & 0xFF);

                BlockPos pos = message.centerPos.add(dx, dy, dz);
                PlanetData data = PlanetData.unpack(message.packedData[i]);

                cache.setData(pos, data);
            }
        }
    }

    /**
     * Client-side cache for planet data.
     */
    public static class ClientPlanetDataCache {
        private static final ClientPlanetDataCache INSTANCE = new ClientPlanetDataCache();
        private final java.util.Map<Long, PlanetData> cache = new java.util.HashMap<>();
        private BlockPos lastCenter = BlockPos.ORIGIN;

        public static ClientPlanetDataCache getInstance() {
            return INSTANCE;
        }

        public void setData(BlockPos pos, PlanetData data) {
            cache.put(pos.toLong(), data);
        }

        public PlanetData getData(BlockPos pos) {
            return cache.get(pos.toLong());
        }

        public boolean hasOxygen(BlockPos pos, boolean defaultValue) {
            PlanetData data = cache.get(pos.toLong());
            return data != null ? data.oxygen() : defaultValue;
        }

        public short getTemperature(BlockPos pos, short defaultValue) {
            PlanetData data = cache.get(pos.toLong());
            return data != null ? data.temperature() : defaultValue;
        }

        public float getGravity(BlockPos pos, float defaultValue) {
            PlanetData data = cache.get(pos.toLong());
            return data != null ? data.gravity() : defaultValue;
        }

        public void clearOldData(BlockPos center, int radius) {
            lastCenter = center;
            int radiusSq = radius * radius;

            // Remove entries outside the radius
            cache.entrySet().removeIf(entry -> {
                BlockPos pos = BlockPos.fromLong(entry.getKey());
                return center.distanceSq(pos) > radiusSq;
            });
        }

        public void clear() {
            cache.clear();
        }
    }
}
