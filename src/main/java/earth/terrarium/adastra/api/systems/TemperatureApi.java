package earth.terrarium.adastra.api.systems;

import earth.terrarium.adastra.api.ApiHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import java.util.Collection;

/**
 * API for querying and manipulating temperature in the Ad Astra mod.
 * <p>
 * This interface provides methods to:
 * <ul>
 *   <li>Query temperature values for dimensions, positions, and entities</li>
 *   <li>Override temperature at specific positions (e.g., for climate control systems)</li>
 *   <li>Check if temperatures are liveable or extreme</li>
 *   <li>Apply temperature effects to living entities</li>
 * </ul>
 * <p>
 * Temperatures are measured in Celsius. Earth's average is around 15°C.
 * <p>
 * Third-party mods can use {@link #API} to access the implementation.
 *
 * @since 1.12.2
 */
public interface TemperatureApi {

    /**
     * The singleton API instance. Loaded via ServiceLoader.
     */
    TemperatureApi API = ApiHelper.load(TemperatureApi.class);

    /**
     * Returns the temperature of the given world/dimension in Celsius.
     * Different planets have different base temperatures.
     *
     * @param world The world to check
     * @return The temperature in Celsius
     */
    short getTemperature(World world);

    /**
     * Returns the temperature of the given world/dimension by dimension ID.
     *
     * @param dimensionId The dimension ID to check
     * @return The temperature in Celsius
     */
    short getTemperature(int dimensionId);

    /**
     * Returns the temperature at the given position in Celsius.
     * This accounts for local temperature modifiers such as climate control systems.
     *
     * @param world The world to check
     * @param pos The position to check
     * @return The temperature in Celsius
     */
    short getTemperature(World world, BlockPos pos);

    /**
     * Returns the temperature affecting the given entity.
     * This accounts for the entity's current position and any temperature protection
     * from armor or suits.
     *
     * @param entity The entity to check
     * @return The effective temperature for the entity in Celsius
     */
    short getTemperature(Entity entity);

    /**
     * Sets the temperature at a specific position to override the dimension's default temperature.
     * This is useful for implementing climate control systems or heating/cooling devices.
     *
     * @param world The world to modify
     * @param pos The position to set temperature at
     * @param temperature The temperature value in Celsius
     */
    void setTemperature(World world, BlockPos pos, short temperature);

    /**
     * Sets the temperature at multiple positions to the given value.
     * This is a bulk operation for efficiency when modifying large areas.
     *
     * @param world The world to modify
     * @param positions The positions to set temperature at
     * @param temperature The temperature value in Celsius
     */
    void setTemperature(World world, Collection<BlockPos> positions, short temperature);

    /**
     * Removes the temperature override at the given position, reverting to the dimension's default.
     *
     * @param world The world to modify
     * @param pos The position to remove the override from
     */
    void removeTemperature(World world, BlockPos pos);

    /**
     * Removes temperature overrides at multiple positions, reverting to the dimension's default.
     *
     * @param world The world to modify
     * @param positions The positions to remove overrides from
     */
    void removeTemperature(World world, Collection<BlockPos> positions);

    /**
     * Returns whether the temperature at the given position is within a liveable range.
     * Liveable temperatures typically range from about -20°C to 50°C.
     *
     * @param world The world to check
     * @param pos The position to check
     * @return true if the temperature is liveable, false if it's too hot or too cold
     */
    boolean isLiveable(World world, BlockPos pos);

    /**
     * Returns whether the temperature at the given position is dangerously hot.
     * Hot temperatures can cause burning damage to unprotected entities.
     *
     * @param world The world to check
     * @param pos The position to check
     * @return true if the temperature is too hot, false otherwise
     */
    boolean isHot(World world, BlockPos pos);

    /**
     * Returns whether the temperature at the given position is dangerously cold.
     * Cold temperatures can cause freezing damage to unprotected entities.
     *
     * @param world The world to check
     * @param pos The position to check
     * @return true if the temperature is too cold, false otherwise
     */
    boolean isCold(World world, BlockPos pos);

    /**
     * Internal method called to apply temperature effects to a living entity.
     * Third-party mods should generally not call this method directly.
     *
     * @param world The world the entity is in
     * @param entity The entity to apply temperature effects to
     */
    void entityTick(WorldServer world, EntityLivingBase entity);
}
