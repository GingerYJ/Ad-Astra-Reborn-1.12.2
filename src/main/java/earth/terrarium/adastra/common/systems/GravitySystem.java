package earth.terrarium.adastra.common.systems;

import earth.terrarium.adastra.common.blocks.AdAstraMachineBlock;
import earth.terrarium.adastra.common.registry.ModBlocks;
import earth.terrarium.adastra.common.tile.GravityNormalizerTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.entity.living.LivingEvent.LivingJumpEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Core gravity system managing gravity multipliers at block positions.
 * Handles dimension-based gravity, per-position overrides, and Gravity Normalizer detection.
 * Includes caching for performance optimization.
 */
public class GravitySystem {

    private static final int NORMALIZER_SCAN_RADIUS = 16;
    private static final int CACHE_CLEANUP_INTERVAL = 200;  // 10 seconds
    private static final int CACHE_EXPIRY_TIME = 100;       // 5 seconds

    // Cache for entity gravity calculations (Entity UUID -> CachedGravity)
    private static final Map<Integer, CachedGravity> ENTITY_GRAVITY_CACHE = new ConcurrentHashMap<>();
    private static int ticksSinceCleanup = 0;

    private static class CachedGravity {
        final float gravity;
        final long timestamp;

        CachedGravity(float gravity, long timestamp) {
            this.gravity = gravity;
            this.timestamp = timestamp;
        }

        boolean isExpired(long currentTime) {
            return currentTime - timestamp > CACHE_EXPIRY_TIME;
        }
    }

    public static float getGravityInDimension(World world) {
        if (world == null || world.provider == null) {
            return 1.0f;
        }

        // Try to get gravity from AdAstraWorldProvider
        if (world.provider instanceof earth.terrarium.adastra.common.world.AdAstraWorldProvider) {
            earth.terrarium.adastra.common.world.AdAstraWorldProvider provider =
                (earth.terrarium.adastra.common.world.AdAstraWorldProvider) world.provider;
            return provider.getGravity();
        }

        // Default to Earth gravity for vanilla dimensions
        return 1.0f;
    }

    public static float getGravityAtPos(World world, BlockPos pos) {
        if (world.isRemote) {
            return getGravityInDimension(world);
        }

        if (!(world instanceof WorldServer)) {
            return getGravityInDimension(world);
        }

        // Check for active Gravity Normalizer first (highest priority)
        Float normalizerGravity = getGravityFromNormalizer(world, pos);
        if (normalizerGravity != null) {
            return normalizerGravity;
        }

        // Check per-position overrides
        PlanetDataStorage storage = PlanetDataStorage.get(world);
        PlanetData data = storage.getData(pos);

        if (data == null) {
            return getGravityInDimension(world);
        }

        return data.gravity();
    }

    /**
     * Gets the effective gravity for an entity, checking Gravity Normalizers.
     * Uses caching to reduce performance impact of frequent calls.
     *
     * @param entity The entity to check
     * @return The gravity multiplier (1.0 = Earth gravity)
     */
    public static float getGravityForEntity(Entity entity) {
        if (entity == null || entity.world == null) {
            return 1.0f;
        }

        // Clean up cache periodically
        cleanupCacheIfNeeded(entity.world);

        // Check cache first
        int entityId = entity.getEntityId();
        long currentTime = entity.world.getTotalWorldTime();
        CachedGravity cached = ENTITY_GRAVITY_CACHE.get(entityId);

        if (cached != null && !cached.isExpired(currentTime)) {
            return cached.gravity;
        }

        // Calculate gravity and cache it
        float gravity = getGravityAtPos(entity.world, entity.getPosition());
        ENTITY_GRAVITY_CACHE.put(entityId, new CachedGravity(gravity, currentTime));

        return gravity;
    }

    /**
     * Cleans up expired cache entries periodically to prevent memory leaks.
     */
    private static void cleanupCacheIfNeeded(World world) {
        ticksSinceCleanup++;
        if (ticksSinceCleanup >= CACHE_CLEANUP_INTERVAL) {
            long currentTime = world.getTotalWorldTime();
            ENTITY_GRAVITY_CACHE.entrySet().removeIf(entry -> entry.getValue().isExpired(currentTime));
            ticksSinceCleanup = 0;
        }
    }

    /**
     * Invalidates the gravity cache for a specific entity.
     * Call this when an entity teleports or when you know gravity has changed.
     */
    public static void invalidateEntityCache(Entity entity) {
        if (entity != null) {
            ENTITY_GRAVITY_CACHE.remove(entity.getEntityId());
        }
    }

    /**
     * Clears the entire gravity cache.
     * Call this when Gravity Normalizers are placed/removed or on dimension change.
     */
    public static void clearCache() {
        ENTITY_GRAVITY_CACHE.clear();
        ticksSinceCleanup = 0;
    }

    /**
     * Checks if the position is covered by an active Gravity Normalizer.
     * Returns the target gravity of the normalizer, or null if not covered.
     */
    private static Float getGravityFromNormalizer(World world, BlockPos target) {
        if (world == null || target == null) {
            return null;
        }

        BlockPos min = target.add(-NORMALIZER_SCAN_RADIUS, -NORMALIZER_SCAN_RADIUS, -NORMALIZER_SCAN_RADIUS);
        BlockPos max = target.add(NORMALIZER_SCAN_RADIUS, NORMALIZER_SCAN_RADIUS, NORMALIZER_SCAN_RADIUS);

        for (BlockPos mutablePos : BlockPos.getAllInBoxMutable(min, max)) {
            if (!world.isBlockLoaded(mutablePos)) {
                continue;
            }

            IBlockState state = world.getBlockState(mutablePos);
            Block block = state.getBlock();

            if (block != ModBlocks.GRAVITY_NORMALIZER || !isMachineLit(state)) {
                continue;
            }

            TileEntity tile = world.getTileEntity(mutablePos);
            if (tile instanceof GravityNormalizerTileEntity) {
                GravityNormalizerTileEntity normalizer = (GravityNormalizerTileEntity) tile;
                if (normalizer.isNormalizingGravity(target)) {
                    return normalizer.getTargetGravity();
                }
            }
        }

        return null;
    }

    /**
     * Checks if an entity is within range of an active Gravity Normalizer.
     */
    public static boolean isInNormalizerRange(Entity entity) {
        if (entity == null || entity.world == null || entity.world.isRemote) {
            return false;
        }
        return getGravityFromNormalizer(entity.world, entity.getPosition()) != null;
    }

    private static boolean isMachineLit(IBlockState state) {
        return state.getPropertyKeys().contains(AdAstraMachineBlock.LIT)
            && state.getValue(AdAstraMachineBlock.LIT);
    }

    public static void setGravity(World world, BlockPos pos, float gravity) {
        if (world.isRemote || !(world instanceof WorldServer)) {
            return;
        }

        PlanetDataStorage storage = PlanetDataStorage.get(world);
        PlanetData data = storage.getData(pos);

        if (data == null) {
            boolean oxygen = OxygenSystemExtended.hasOxygenAtPos(world, pos);
            short temperature = TemperatureSystem.getTemperatureAtPos(world, pos);
            data = new PlanetData(oxygen, temperature, gravity);
        } else {
            data.setGravity(gravity);
        }

        storage.setData(pos, data);
    }

    public static void setGravity(World world, Collection<BlockPos> positions, float gravity) {
        if (world.isRemote || !(world instanceof WorldServer)) {
            return;
        }

        for (BlockPos pos : positions) {
            setGravity(world, pos, gravity);
        }
    }

    public static void removeGravity(World world, BlockPos pos) {
        setGravity(world, pos, getGravityInDimension(world));
    }

    public static void removeGravity(World world, Collection<BlockPos> positions) {
        setGravity(world, positions, getGravityInDimension(world));
    }

    /**
     * Gets the effective gravity multiplier at a specific position.
     * Checks for Gravity Normalizers first, then falls back to dimension gravity.
     *
     * @param world The world
     * @param pos The position to check
     * @return The gravity multiplier (1.0 = Earth gravity)
     */
    public static float getGravityMultiplier(World world, BlockPos pos) {
        if (isInNormalizerRange(world, pos)) {
            return 1.0f;
        }
        if (world.provider instanceof earth.terrarium.adastra.common.world.AdAstraWorldProvider) {
            return ((earth.terrarium.adastra.common.world.AdAstraWorldProvider) world.provider).getGravity();
        }
        return 1.0f;
    }

    /**
     * Checks if a position is within range of an active Gravity Normalizer.
     * Overload that accepts World and BlockPos directly.
     */
    public static boolean isInNormalizerRange(World world, BlockPos pos) {
        return getGravityFromNormalizer(world, pos) != null;
    }

    /**
     * Event handler for entity motion updates.
     * Applies gravity multiplier to falling entities.
     */
    @SubscribeEvent
    public void onLivingUpdate(LivingUpdateEvent event) {
        EntityLivingBase entity = event.getEntityLiving();
        if (!entity.world.isRemote && !entity.onGround) {
            float gravity = getGravityMultiplier(entity.world, entity.getPosition());
            if (gravity != 1.0f) {
                entity.motionY *= gravity;
            }
        }
    }

    /**
     * Event handler for entity jumping.
     * Adjusts jump height based on gravity multiplier (lower gravity = higher jumps).
     */
    @SubscribeEvent
    public void onLivingJump(LivingJumpEvent event) {
        float gravity = getGravityMultiplier(event.getEntity().world, event.getEntity().getPosition());
        if (gravity < 1.0f) {
            event.getEntity().motionY /= gravity;
        }
    }

    /**
     * Event handler for fall damage calculation.
     * Adjusts fall distance based on gravity multiplier (lower gravity = less damage).
     */
    @SubscribeEvent
    public void onLivingFall(LivingFallEvent event) {
        float gravity = getGravityMultiplier(event.getEntity().world, event.getEntity().getPosition());
        event.setDistance(event.getDistance() * gravity);
    }
}
