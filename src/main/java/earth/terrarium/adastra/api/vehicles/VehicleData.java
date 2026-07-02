package earth.terrarium.adastra.api.vehicles;

import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

/**
 * Represents the runtime data for a vehicle entity.
 * <p>
 * This class provides access to vehicle properties such as fuel level,
 * passengers, damage state, and inventory contents.
 *
 * @since 1.12.2
 */
public interface VehicleData {

    /**
     * Gets the vehicle's unique identifier/type.
     *
     * @return The vehicle type resource location
     */
    ResourceLocation getVehicleId();

    /**
     * Gets the vehicle's type category.
     *
     * @return The vehicle type
     */
    VehicleApi.VehicleType getVehicleType();

    /**
     * Gets the technology tier of this vehicle.
     *
     * @return The tier (1-4)
     */
    int getTier();

    /**
     * Gets the current fuel amount in the vehicle's tank.
     *
     * @return Current fuel in mB (millibuckets)
     */
    int getFuel();

    /**
     * Sets the fuel amount in the vehicle's tank.
     *
     * @param fuel Fuel amount in mB
     */
    void setFuel(int fuel);

    /**
     * Gets the maximum fuel capacity of the vehicle.
     *
     * @return Maximum fuel in mB
     */
    int getFuelCapacity();

    /**
     * Gets the fuel type required by this vehicle.
     *
     * @return The fuel type resource location, or null if any fuel is accepted
     */
    @Nullable
    ResourceLocation getFuelType();

    /**
     * Checks if the vehicle has enough fuel for operation.
     *
     * @return true if fuel > 0, false otherwise
     */
    boolean hasFuel();

    /**
     * Consumes fuel from the vehicle's tank.
     *
     * @param amount Amount of fuel to consume in mB
     * @return The amount actually consumed (may be less if insufficient fuel)
     */
    int consumeFuel(int amount);

    /**
     * Adds fuel to the vehicle's tank.
     *
     * @param amount Amount of fuel to add in mB
     * @return The amount actually added (may be less if tank is full)
     */
    int addFuel(int amount);

    /**
     * Gets the number of passenger seats in this vehicle.
     *
     * @return Number of seats (including pilot)
     */
    int getSeatCount();

    /**
     * Checks if the vehicle is currently in flight/motion.
     *
     * @return true if the vehicle is active, false otherwise
     */
    boolean isActive();

    /**
     * Gets the vehicle's current health/durability.
     *
     * @return Health points
     */
    float getHealth();

    /**
     * Gets the vehicle's maximum health.
     *
     * @return Maximum health points
     */
    float getMaxHealth();

    /**
     * Checks if the vehicle is damaged.
     *
     * @return true if health < maxHealth, false otherwise
     */
    boolean isDamaged();

    /**
     * Repairs the vehicle by the specified amount.
     *
     * @param amount Health to restore
     */
    void repair(float amount);

    /**
     * Gets the size of the vehicle's inventory (for cargo storage).
     * Returns 0 if the vehicle has no inventory.
     *
     * @return Number of inventory slots
     */
    int getInventorySize();

    /**
     * Checks if the vehicle has an oxygen supply system.
     * Some vehicles provide oxygen to passengers without suits.
     *
     * @return true if the vehicle provides oxygen, false otherwise
     */
    boolean providesOxygen();

    /**
     * Checks if the vehicle has temperature protection.
     * Some vehicles protect passengers from extreme temperatures.
     *
     * @return true if the vehicle provides temperature protection, false otherwise
     */
    boolean providesTemperatureProtection();

    /**
     * Gets custom NBT data stored on the vehicle.
     * This can be used by third-party mods to store additional vehicle data.
     *
     * @param key The data key
     * @return The data value, or null if not set
     */
    @Nullable
    String getCustomData(String key);

    /**
     * Sets custom NBT data on the vehicle.
     *
     * @param key The data key
     * @param value The data value
     */
    void setCustomData(String key, String value);
}
