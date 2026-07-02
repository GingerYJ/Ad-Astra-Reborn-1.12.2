package earth.terrarium.adastra.api.vehicles;

import earth.terrarium.adastra.api.ApiHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

/**
 * API for managing custom rockets, rovers, and other space vehicles.
 * <p>
 * This interface allows third-party mods to:
 * <ul>
 *   <li>Register custom vehicle types</li>
 *   <li>Query vehicle properties and capabilities</li>
 *   <li>Spawn vehicles programmatically</li>
 *   <li>Check if entities are vehicles</li>
 * </ul>
 * <p>
 * Example usage:
 * <pre>{@code
 * // Register a custom rocket type
 * VehicleApi.API.registerVehicleType(
 *     new ResourceLocation("modid", "super_rocket"),
 *     VehicleType.ROCKET,
 *     4, // tier 4
 *     100000 // fuel capacity
 * );
 *
 * // Check if entity is a vehicle
 * if (VehicleApi.API.isVehicle(entity)) {
 *     VehicleData data = VehicleApi.API.getVehicleData(entity);
 *     // Access vehicle properties
 * }
 * }</pre>
 * <p>
 * Third-party mods can use {@link #API} to access the implementation.
 *
 * @since 1.12.2
 */
public interface VehicleApi {

    /**
     * The singleton API instance. Loaded via ServiceLoader.
     */
    VehicleApi API = ApiHelper.load(VehicleApi.class);

    /**
     * Vehicle type enumeration for different categories of space vehicles.
     */
    enum VehicleType {
        /** Rockets for traveling between planets */
        ROCKET,
        /** Rovers for ground exploration on planets */
        ROVER,
        /** Landers for descending to planet surfaces */
        LANDER,
        /** Custom vehicle types */
        CUSTOM
    }

    /**
     * Registers a custom vehicle type with Ad Astra.
     * <p>
     * This allows the vehicle to be recognized by Ad Astra's systems,
     * including the launch pad, fuel loading, and tier restrictions.
     *
     * @param id Unique vehicle identifier
     * @param type The vehicle type category
     * @param tier The technology tier (1-4)
     * @param fuelCapacity Maximum fuel capacity in mB
     * @return true if registration succeeded, false if a vehicle with this ID already exists
     */
    boolean registerVehicleType(ResourceLocation id, VehicleType type, int tier, int fuelCapacity);

    /**
     * Checks if the given entity is a vehicle.
     *
     * @param entity The entity to check
     * @return true if the entity is a recognized vehicle, false otherwise
     */
    boolean isVehicle(Entity entity);

    /**
     * Checks if the given entity is a rocket.
     *
     * @param entity The entity to check
     * @return true if the entity is a rocket, false otherwise
     */
    boolean isRocket(Entity entity);

    /**
     * Checks if the given entity is a rover.
     *
     * @param entity The entity to check
     * @return true if the entity is a rover, false otherwise
     */
    boolean isRover(Entity entity);

    /**
     * Gets vehicle data for the given entity.
     * Returns null if the entity is not a vehicle.
     *
     * @param entity The entity to get data for
     * @return The vehicle data, or null if not a vehicle
     */
    @Nullable
    VehicleData getVehicleData(Entity entity);

    /**
     * Spawns a vehicle of the specified type at the given position.
     *
     * @param world The world to spawn in
     * @param x X coordinate
     * @param y Y coordinate
     * @param z Z coordinate
     * @param vehicleId The vehicle type identifier
     * @param owner The player who owns the vehicle (nullable)
     * @return The spawned vehicle entity, or null if spawning failed
     */
    @Nullable
    Entity spawnVehicle(World world, double x, double y, double z, ResourceLocation vehicleId, @Nullable EntityPlayer owner);

    /**
     * Creates an item stack for the specified vehicle type.
     * This is useful for crafting recipes or creative tabs.
     *
     * @param vehicleId The vehicle type identifier
     * @return An ItemStack representing the vehicle, or ItemStack.EMPTY if not found
     */
    ItemStack getVehicleItem(ResourceLocation vehicleId);

    /**
     * Gets all registered vehicle types.
     *
     * @return List of all registered vehicle identifiers
     */
    List<ResourceLocation> getAllVehicleTypes();

    /**
     * Gets all registered vehicles of a specific type.
     *
     * @param type The vehicle type to filter by
     * @return List of vehicle identifiers matching the type
     */
    List<ResourceLocation> getVehiclesByType(VehicleType type);

    /**
     * Checks if a vehicle can launch from the current dimension.
     * <p>
     * Some vehicles may have dimension restrictions or require specific infrastructure.
     *
     * @param entity The vehicle entity
     * @param dimensionId The dimension to launch from
     * @return true if the vehicle can launch, false otherwise
     */
    boolean canLaunchFrom(Entity entity, int dimensionId);

    /**
     * Gets the fuel type required for the given vehicle.
     *
     * @param vehicleId The vehicle type identifier
     * @return The resource location of the required fuel type, or null if no specific fuel
     */
    @Nullable
    ResourceLocation getRequiredFuelType(ResourceLocation vehicleId);
}
