package earth.terrarium.adastra.api.systems;

import earth.terrarium.adastra.api.ApiHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Collection;

/**
 * API for querying and manipulating gravity in the Ad Astra mod.
 * <p>
 * This interface provides methods to:
 * <ul>
 *   <li>Query gravity values for dimensions, positions, and entities</li>
 *   <li>Override gravity at specific positions (e.g., for gravity generators)</li>
 *   <li>Apply gravity effects to living entities</li>
 * </ul>
 * <p>
 * Third-party mods can use {@link #API} to access the implementation.
 *
 * @since 1.12.2
 */
public interface GravityApi {

    /**
     * The singleton API instance. Loaded via ServiceLoader.
     */
    GravityApi API = ApiHelper.load(GravityApi.class);

    /**
     * Returns the gravity of the given world/dimension.
     * Default gravity is 1.0 (Earth gravity). Other planets may have different values.
     *
     * @param world The world to check
     * @return The gravity multiplier for the dimension (1.0 = Earth gravity)
     */
    float getGravity(World world);

    /**
     * Returns the gravity of the given world/dimension by dimension ID.
     *
     * @param dimensionId The dimension ID to check
     * @return The gravity multiplier for the dimension (1.0 = Earth gravity)
     */
    float getGravity(int dimensionId);

    /**
     * Returns the gravity at the given position, accounting for local overrides
     * such as gravity generators or anti-gravity fields.
     *
     * @param world The world to check
     * @param pos The position to check
     * @return The effective gravity at the position
     */
    float getGravity(World world, BlockPos pos);

    /**
     * Returns the gravity affecting the given entity.
     * This accounts for the entity's current position and any local gravity modifiers.
     *
     * @param entity The entity to check
     * @return The effective gravity for the entity
     */
    float getGravity(Entity entity);

    /**
     * Sets the gravity at a specific position to override the dimension's default gravity.
     * This is useful for implementing gravity generators or anti-gravity fields.
     *
     * @param world The world to modify
     * @param pos The position to set gravity at
     * @param gravity The gravity value to set (1.0 = Earth gravity)
     */
    void setGravity(World world, BlockPos pos, float gravity);

    /**
     * Sets the gravity at multiple positions to the given value.
     * This is a bulk operation for efficiency when modifying large areas.
     *
     * @param world The world to modify
     * @param positions The positions to set gravity at
     * @param gravity The gravity value to set (1.0 = Earth gravity)
     */
    void setGravity(World world, Collection<BlockPos> positions, float gravity);

    /**
     * Removes the gravity override at the given position, reverting to the dimension's default gravity.
     *
     * @param world The world to modify
     * @param pos The position to remove the override from
     */
    void removeGravity(World world, BlockPos pos);

    /**
     * Removes gravity overrides at multiple positions, reverting to the dimension's default gravity.
     *
     * @param world The world to modify
     * @param positions The positions to remove overrides from
     */
    void removeGravity(World world, Collection<BlockPos> positions);

    /**
     * Internal method called to apply gravity effects to a living entity.
     * Third-party mods should generally not call this method directly.
     *
     * @param world The world the entity is in
     * @param entity The entity to apply gravity to
     * @param travelVector The entity's travel vector
     * @param movementAffectingPos The position affecting the entity's movement
     */
    void entityTick(World world, EntityLivingBase entity, Vec3d travelVector, BlockPos movementAffectingPos);
}
