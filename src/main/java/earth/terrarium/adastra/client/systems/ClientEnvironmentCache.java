package earth.terrarium.adastra.client.systems;

import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.HashMap;
import java.util.Map;

/**
 * Client-side cache for environment system data.
 * Stores oxygen zones, gravity zones, and temperature zones synced from server.
 */
@SideOnly(Side.CLIENT)
public class ClientEnvironmentCache {

    private static final Map<BlockPos, OxygenZone> oxygenZones = new HashMap<>();
    private static final Map<BlockPos, GravityZone> gravityZones = new HashMap<>();
    private static final Map<BlockPos, TemperatureZone> temperatureZones = new HashMap<>();
    private static boolean dirty = false;

    public static void addOxygenZone(BlockPos pos, int radius) {
        oxygenZones.put(pos, new OxygenZone(pos, radius));
        dirty = true;
    }

    public static void removeOxygenZone(BlockPos pos) {
        oxygenZones.remove(pos);
        dirty = true;
    }

    public static void addGravityZone(BlockPos pos, int radius, float gravityScale) {
        gravityZones.put(pos, new GravityZone(pos, radius, gravityScale));
        dirty = true;
    }

    public static void removeGravityZone(BlockPos pos) {
        gravityZones.remove(pos);
        dirty = true;
    }

    public static void addTemperatureZone(BlockPos pos, int radius, float temperature) {
        temperatureZones.put(pos, new TemperatureZone(pos, radius, temperature));
        dirty = true;
    }

    public static void removeTemperatureZone(BlockPos pos) {
        temperatureZones.remove(pos);
        dirty = true;
    }

    public static boolean hasOxygen(BlockPos pos) {
        for (OxygenZone zone : oxygenZones.values()) {
            if (zone.contains(pos)) {
                return true;
            }
        }
        return false;
    }

    public static Float getGravityScale(BlockPos pos) {
        for (GravityZone zone : gravityZones.values()) {
            if (zone.contains(pos)) {
                return zone.gravityScale;
            }
        }
        return null;
    }

    public static Float getTemperature(BlockPos pos) {
        for (TemperatureZone zone : temperatureZones.values()) {
            if (zone.contains(pos)) {
                return zone.temperature;
            }
        }
        return null;
    }

    public static void clear() {
        oxygenZones.clear();
        gravityZones.clear();
        temperatureZones.clear();
        dirty = false;
    }

    public static void markDirty() {
        dirty = true;
    }

    public static boolean isDirty() {
        return dirty;
    }

    public static void clearDirty() {
        dirty = false;
    }

    private static class OxygenZone {
        final BlockPos pos;
        final int radius;
        final int radiusSq;

        OxygenZone(BlockPos pos, int radius) {
            this.pos = pos;
            this.radius = radius;
            this.radiusSq = radius * radius;
        }

        boolean contains(BlockPos target) {
            long dx = target.getX() - pos.getX();
            long dy = target.getY() - pos.getY();
            long dz = target.getZ() - pos.getZ();
            return dx * dx + dy * dy + dz * dz <= radiusSq;
        }
    }

    private static class GravityZone {
        final BlockPos pos;
        final int radius;
        final int radiusSq;
        final float gravityScale;

        GravityZone(BlockPos pos, int radius, float gravityScale) {
            this.pos = pos;
            this.radius = radius;
            this.radiusSq = radius * radius;
            this.gravityScale = gravityScale;
        }

        boolean contains(BlockPos target) {
            long dx = target.getX() - pos.getX();
            long dy = target.getY() - pos.getY();
            long dz = target.getZ() - pos.getZ();
            return dx * dx + dy * dy + dz * dz <= radiusSq;
        }
    }

    private static class TemperatureZone {
        final BlockPos pos;
        final int radius;
        final int radiusSq;
        final float temperature;

        TemperatureZone(BlockPos pos, int radius, float temperature) {
            this.pos = pos;
            this.radius = radius;
            this.radiusSq = radius * radius;
            this.temperature = temperature;
        }

        boolean contains(BlockPos target) {
            long dx = target.getX() - pos.getX();
            long dy = target.getY() - pos.getY();
            long dz = target.getZ() - pos.getZ();
            return dx * dx + dy * dy + dz * dz <= radiusSq;
        }
    }
}
