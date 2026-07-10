package earth.terrarium.adastra.common.systems;

import earth.terrarium.adastra.api.events.AdAstraEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.entity.living.LivingEvent.LivingJumpEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Collection;

/**
 * Core gravity system managing gravity multipliers at block positions.
 * Handles dimension-based gravity, per-position overrides, and Gravity Normalizer detection.
 * Position overrides are indexed by packed block position for constant-time lookups.
 */
public class GravitySystem {

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

        // Check per-position overrides
        PlanetDataStorage storage = PlanetDataStorage.get(world);
        Float gravity = storage.getGravityOverride(pos);
        if (gravity != null) {
            return gravity;
        }

        return getGravityInDimension(world);
    }

    /**
     * Gets the effective gravity for an entity, checking indexed position overrides.
     *
     * @param entity The entity to check
     * @return The gravity multiplier (1.0 = Earth gravity)
     */
    public static float getGravityForEntity(Entity entity) {
        if (entity == null || entity.world == null) {
            return 1.0f;
        }

        float gravity = getGravityAtPos(entity.world, entity.getPosition());
        return AdAstraEvents.EntityGravityEvent.fire(entity, gravity);
    }

    /**
     * Invalidates the gravity cache for a specific entity.
     * Call this when an entity teleports or when you know gravity has changed.
     */
    public static void invalidateEntityCache(Entity entity) {
        // Retained for API compatibility; position lookups are already O(1).
    }

    /**
     * Clears the entire gravity cache.
     * Call this when Gravity Normalizers are placed/removed or on dimension change.
     */
    public static void clearCache() {
        // Retained for existing machine calls; there is no entity cache.
    }

    public static void clearCache(World world) {
        // Retained for event-handler compatibility; there is no entity cache.
    }

    /**
     * Checks if an entity is within range of an active Gravity Normalizer.
     */
    public static boolean isInNormalizerRange(Entity entity) {
        if (entity == null || entity.world == null || entity.world.isRemote) {
            return false;
        }
        return isInNormalizerRange(entity.world, entity.getPosition());
    }

    public static void setGravity(World world, BlockPos pos, float gravity) {
        if (world.isRemote || !(world instanceof WorldServer)) {
            return;
        }

        PlanetDataStorage storage = PlanetDataStorage.get(world);
        if (setGravityWithoutMarking(world, storage, pos, gravity)) {
            storage.markChanged();
        }
    }

    public static void setGravity(World world, Collection<BlockPos> positions, float gravity) {
        if (world.isRemote || !(world instanceof WorldServer)) {
            return;
        }

        PlanetDataStorage storage = PlanetDataStorage.get(world);
        boolean changed = false;
        for (BlockPos pos : positions) {
            changed |= setGravityWithoutMarking(world, storage, pos, gravity);
        }
        if (changed) {
            storage.markChanged();
        }
    }

    private static boolean setGravityWithoutMarking(World world, PlanetDataStorage storage,
                                                     BlockPos pos, float gravity) {
        Float previous = storage.getGravityOverride(pos);
        return previous == null || Float.compare(previous, gravity) != 0
            ? storage.setGravityOverrideWithoutMarking(pos, gravity)
            : false;
    }

    public static void removeGravity(World world, BlockPos pos) {
        if (world.isRemote || !(world instanceof WorldServer)) {
            return;
        }
        PlanetDataStorage storage = PlanetDataStorage.get(world);
        if (storage.clearGravityOverrideWithoutMarking(pos)) {
            storage.markChanged();
        }
    }

    public static void removeGravity(World world, Collection<BlockPos> positions) {
        if (world.isRemote || !(world instanceof WorldServer)) {
            return;
        }
        PlanetDataStorage storage = PlanetDataStorage.get(world);
        boolean changed = false;
        for (BlockPos pos : positions) {
            changed |= storage.clearGravityOverrideWithoutMarking(pos);
        }
        if (changed) {
            storage.markChanged();
        }
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
        return getGravityAtPos(world, pos);
    }

    /**
     * Checks if a position is within range of an active Gravity Normalizer.
     * Overload that accepts World and BlockPos directly.
     */
    public static boolean isInNormalizerRange(World world, BlockPos pos) {
        if (world == null || pos == null || world.isRemote || !(world instanceof WorldServer)) {
            return false;
        }
        return PlanetDataStorage.get(world).hasGravityOverride(pos);
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
            gravity = AdAstraEvents.EntityGravityEvent.fire(entity, gravity);

            Vec3d travelVector = new Vec3d(entity.motionX, entity.motionY, entity.motionZ);
            if (gravity == 0.0f) {
                if (!AdAstraEvents.ZeroGravityTickEvent.fire(entity.world, entity, travelVector, entity.getPosition())) {
                    return;
                }
            } else if (!AdAstraEvents.GravityTickEvent.fire(entity.world, entity, travelVector, entity.getPosition())) {
                return;
            }

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
        gravity = AdAstraEvents.EntityGravityEvent.fire(event.getEntity(), gravity);
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
        gravity = AdAstraEvents.EntityGravityEvent.fire(event.getEntity(), gravity);
        event.setDistance(event.getDistance() * gravity);
    }
}
