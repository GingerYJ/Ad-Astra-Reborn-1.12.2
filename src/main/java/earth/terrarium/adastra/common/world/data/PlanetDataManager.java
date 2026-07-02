package earth.terrarium.adastra.common.world.data;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * Helper class for accessing planet environmental data.
 * Provides static methods and caching for performance.
 */
public class PlanetDataManager {

    // Cache storage per world (weak reference to allow GC)
    private static final WeakHashMap<World, PlanetDataStorage> storageCache = new WeakHashMap<>();

    // Thread-local cache for repeated queries in the same tick
    private static final ThreadLocal<Map<Long, CachedData>> tickCache = ThreadLocal.withInitial(HashMap::new);
    private static long lastCacheClearTick = 0;

    /**
     * Gets the PlanetDataStorage for the given world.
     * Uses caching to avoid repeated lookups.
     */
    @Nonnull
    public static PlanetDataStorage getStorage(World world) {
        if (world.isRemote) {
            throw new RuntimeException("PlanetDataManager should only be used server-side");
        }

        return storageCache.computeIfAbsent(world, PlanetDataStorage::get);
    }

    /**
     * Gets complete planet data at the given position.
     * Returns a new PlanetData with dimension defaults if no custom data exists.
     */
    @Nonnull
    public static PlanetData getData(World world, BlockPos pos) {
        if (world.isRemote) {
            // Client-side should use synced data from packets
            return getDefaultData(world);
        }

        PlanetDataStorage storage = getStorage(world);
        PlanetData data = storage.getData(pos);

        if (data != null) {
            return data;
        }

        // Return dimension defaults
        return getDefaultData(world);
    }

    /**
     * Sets complete planet data at the given position.
     */
    public static void setData(World world, BlockPos pos, PlanetData data) {
        if (world.isRemote) return;

        PlanetDataStorage storage = getStorage(world);
        storage.setData(pos, data);
        clearTickCache();
    }

    /**
     * Gets oxygen presence at the given position.
     */
    public static boolean hasOxygen(World world, BlockPos pos) {
        if (world.isRemote) {
            return getDefaultOxygen(world);
        }

        // Check tick cache first
        CachedData cached = getCachedData(world, pos);
        if (cached != null && cached.hasOxygen != null) {
            return cached.hasOxygen;
        }

        PlanetDataStorage storage = getStorage(world);
        boolean oxygen = storage.getOxygen(pos);

        // Cache result
        if (cached == null) {
            cached = new CachedData();
            putCachedData(world, pos, cached);
        }
        cached.hasOxygen = oxygen;

        return oxygen;
    }

    /**
     * Sets oxygen presence at the given position.
     */
    public static void setOxygen(World world, BlockPos pos, boolean hasOxygen) {
        if (world.isRemote) return;

        PlanetDataStorage storage = getStorage(world);
        storage.setOxygen(pos, hasOxygen);
        clearTickCache();
    }

    /**
     * Gets temperature at the given position.
     */
    public static short getTemperature(World world, BlockPos pos) {
        if (world.isRemote) {
            return getDefaultTemperature(world);
        }

        // Check tick cache first
        CachedData cached = getCachedData(world, pos);
        if (cached != null && cached.temperature != null) {
            return cached.temperature;
        }

        PlanetDataStorage storage = getStorage(world);
        short temperature = storage.getTemperature(pos);

        // Cache result
        if (cached == null) {
            cached = new CachedData();
            putCachedData(world, pos, cached);
        }
        cached.temperature = temperature;

        return temperature;
    }

    /**
     * Sets temperature at the given position.
     */
    public static void setTemperature(World world, BlockPos pos, short temperature) {
        if (world.isRemote) return;

        PlanetDataStorage storage = getStorage(world);
        storage.setTemperature(pos, temperature);
        clearTickCache();
    }

    /**
     * Gets gravity multiplier at the given position.
     */
    public static float getGravity(World world, BlockPos pos) {
        if (world.isRemote) {
            return getDefaultGravity(world);
        }

        // Check tick cache first
        CachedData cached = getCachedData(world, pos);
        if (cached != null && cached.gravity != null) {
            return cached.gravity;
        }

        PlanetDataStorage storage = getStorage(world);
        float gravity = storage.getGravity(pos);

        // Cache result
        if (cached == null) {
            cached = new CachedData();
            putCachedData(world, pos, cached);
        }
        cached.gravity = gravity;

        return gravity;
    }

    /**
     * Sets gravity multiplier at the given position.
     */
    public static void setGravity(World world, BlockPos pos, float gravity) {
        if (world.isRemote) return;

        PlanetDataStorage storage = getStorage(world);
        storage.setGravity(pos, gravity);
        clearTickCache();
    }

    /**
     * Clears data in a region. Useful for Oxygen/Gravity distributor removal.
     */
    public static void clearRegion(World world, Iterable<BlockPos> positions) {
        if (world.isRemote) return;

        PlanetDataStorage storage = getStorage(world);
        storage.clearRegion(positions);
        clearTickCache();
    }

    /**
     * Clears a single position's data.
     */
    public static void clearPosition(World world, BlockPos pos) {
        if (world.isRemote) return;

        PlanetDataStorage storage = getStorage(world);
        storage.clearPosition(pos);
        clearTickCache();
    }

    /**
     * Gets dimension default oxygen setting.
     */
    public static boolean getDefaultOxygen(World world) {
        if (world.provider instanceof earth.terrarium.adastra.common.world.AdAstraWorldProvider) {
            earth.terrarium.adastra.common.world.AdAstraWorldProvider provider =
                (earth.terrarium.adastra.common.world.AdAstraWorldProvider) world.provider;
            return provider.hasOxygen();
        }
        // Overworld has oxygen by default
        return world.provider.getDimension() == 0;
    }

    /**
     * Gets dimension default temperature.
     */
    public static short getDefaultTemperature(World world) {
        if (world.provider instanceof earth.terrarium.adastra.common.world.AdAstraWorldProvider) {
            earth.terrarium.adastra.common.world.AdAstraWorldProvider provider =
                (earth.terrarium.adastra.common.world.AdAstraWorldProvider) world.provider;
            return provider.getTemperature();
        }
        // Earth default: 20°C
        return 20;
    }

    /**
     * Gets dimension default gravity multiplier.
     */
    public static float getDefaultGravity(World world) {
        if (world.provider instanceof earth.terrarium.adastra.common.world.AdAstraWorldProvider) {
            earth.terrarium.adastra.common.world.AdAstraWorldProvider provider =
                (earth.terrarium.adastra.common.world.AdAstraWorldProvider) world.provider;
            return provider.getGravity();
        }
        // Earth default: 1.0
        return 1.0f;
    }

    /**
     * Gets a PlanetData object with dimension defaults.
     */
    @Nonnull
    public static PlanetData getDefaultData(World world) {
        return new PlanetData(
            getDefaultOxygen(world),
            getDefaultTemperature(world),
            getDefaultGravity(world)
        );
    }

    // Caching implementation

    @Nullable
    private static CachedData getCachedData(World world, BlockPos pos) {
        clearTickCacheIfNeeded(world);
        long key = makeCacheKey(world, pos);
        return tickCache.get().get(key);
    }

    private static void putCachedData(World world, BlockPos pos, CachedData data) {
        clearTickCacheIfNeeded(world);
        long key = makeCacheKey(world, pos);
        tickCache.get().put(key, data);
    }

    private static long makeCacheKey(World world, BlockPos pos) {
        // Combine world dimension ID and position for unique key
        // This assumes dimension IDs fit in 16 bits (safe for Minecraft 1.12.2)
        long dimId = world.provider.getDimension() & 0xFFFF;
        long posLong = pos.toLong();
        return (dimId << 48) | (posLong & 0xFFFFFFFFFFFFL);
    }

    private static void clearTickCacheIfNeeded(World world) {
        long currentTick = world.getTotalWorldTime();
        if (currentTick != lastCacheClearTick) {
            tickCache.get().clear();
            lastCacheClearTick = currentTick;
        }
    }

    private static void clearTickCache() {
        tickCache.get().clear();
    }

    /**
     * Clears all caches. Should be called when worlds are unloaded.
     */
    public static void clearAllCaches() {
        storageCache.clear();
        tickCache.get().clear();
    }

    /**
     * Internal cached data structure.
     */
    private static class CachedData {
        Boolean hasOxygen;
        Short temperature;
        Float gravity;
    }
}
