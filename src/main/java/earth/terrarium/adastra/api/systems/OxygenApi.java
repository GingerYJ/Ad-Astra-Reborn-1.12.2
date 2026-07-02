package earth.terrarium.adastra.api.systems;

import earth.terrarium.adastra.api.ApiHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import java.util.Collection;

/**
 * API for querying and manipulating oxygen availability in the Ad Astra mod.
 * <p>
 * This interface provides methods to:
 * <ul>
 *   <li>Check if dimensions, positions, or entities have oxygen</li>
 *   <li>Create oxygen bubbles or sealed rooms (e.g., via oxygen distributors)</li>
 *   <li>Apply oxygen deprivation effects to entities</li>
 * </ul>
 * <p>
 * Third-party mods can use {@link #API} to access the implementation.
 *
 * @since 1.12.2
 */
public interface OxygenApi {

    /**
     * The singleton API instance. Loaded via ServiceLoader.
     */
    OxygenApi API = ApiHelper.load(OxygenApi.class);

    /**
     * Returns whether the given world/dimension has oxygen by default.
     * Earth has oxygen, but most space dimensions do not.
     *
     * @param world The world to check
     * @return true if the dimension has oxygen, false otherwise
     */
    boolean hasOxygen(World world);

    /**
     * Returns whether the given world/dimension has oxygen by dimension ID.
     *
     * @param dimensionId The dimension ID to check
     * @return true if the dimension has oxygen, false otherwise
     */
    boolean hasOxygen(int dimensionId);

    /**
     * Returns whether the given position has oxygen, accounting for local modifications
     * such as oxygen distributors, sealed rooms, or oxygen bubbles.
     *
     * @param world The world to check
     * @param pos The position to check
     * @return true if the position has oxygen, false otherwise
     */
    boolean hasOxygen(World world, BlockPos pos);

    /**
     * Returns whether the given entity has oxygen at its current location.
     * This checks the entity's position and accounts for space suits with oxygen tanks.
     *
     * @param entity The entity to check
     * @return true if the entity has access to oxygen, false otherwise
     */
    boolean hasOxygen(Entity entity);

    /**
     * Sets the oxygen availability at a specific position.
     * This is useful for implementing oxygen distributors or sealed rooms.
     *
     * @param world The world to modify
     * @param pos The position to set oxygen at
     * @param oxygen true to add oxygen, false to remove it
     */
    void setOxygen(World world, BlockPos pos, boolean oxygen);

    /**
     * Sets the oxygen availability at multiple positions.
     * This is a bulk operation for efficiency when modifying large areas.
     *
     * @param world The world to modify
     * @param positions The positions to set oxygen at
     * @param oxygen true to add oxygen, false to remove it
     */
    void setOxygen(World world, Collection<BlockPos> positions, boolean oxygen);

    /**
     * Removes the oxygen override at the given position, reverting to the dimension's default.
     *
     * @param world The world to modify
     * @param pos The position to remove the override from
     */
    void removeOxygen(World world, BlockPos pos);

    /**
     * Removes oxygen overrides at multiple positions, reverting to the dimension's default.
     *
     * @param world The world to modify
     * @param positions The positions to remove overrides from
     */
    void removeOxygen(World world, Collection<BlockPos> positions);

    /**
     * Internal method called to apply oxygen deprivation effects to a living entity.
     * Third-party mods should generally not call this method directly.
     *
     * @param world The world the entity is in
     * @param entity The entity to check oxygen for
     */
    void entityTick(WorldServer world, EntityLivingBase entity);
}
