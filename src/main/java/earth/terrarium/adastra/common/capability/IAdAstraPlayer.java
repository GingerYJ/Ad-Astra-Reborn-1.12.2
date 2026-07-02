package earth.terrarium.adastra.common.capability;

import net.minecraft.util.math.BlockPos;

import java.util.Map;
import java.util.Set;

public interface IAdAstraPlayer {

    /**
     * Gets the player's oxygen tick counter.
     * Used for tracking oxygen consumption timing.
     *
     * @return oxygen ticks remaining
     */
    int getOxygenTicks();

    /**
     * Sets the player's oxygen tick counter.
     *
     * @param ticks oxygen ticks to set
     */
    void setOxygenTicks(int ticks);

    /**
     * Gets the player's current temperature exposure.
     *
     * @return temperature in degrees
     */
    float getTemperature();

    /**
     * Sets the player's temperature exposure.
     *
     * @param temperature temperature to set
     */
    void setTemperature(float temperature);

    /**
     * Gets the player's current gravity multiplier.
     *
     * @return gravity multiplier (1.0 = Earth normal)
     */
    float getGravity();

    /**
     * Sets the player's gravity multiplier.
     *
     * @param gravity gravity multiplier to set
     */
    void setGravity(float gravity);

    /**
     * Gets the map of launch positions per dimension.
     * Key: dimension ID
     * Value: launch position
     *
     * @return map of launch positions
     */
    Map<Integer, BlockPos> getLaunchPositions();

    /**
     * Sets a launch position for a specific dimension.
     *
     * @param dimension dimension ID
     * @param pos launch position
     */
    void setLaunchPosition(int dimension, BlockPos pos);

    /**
     * Gets the launch position for a specific dimension.
     *
     * @param dimension dimension ID
     * @return launch position, or null if none set
     */
    BlockPos getLaunchPosition(int dimension);

    /**
     * Gets the set of space stations owned by this player.
     *
     * @return set of owned space stations
     */
    Set<SpaceStation> getSpaceStations();

    /**
     * Adds a space station to the player's ownership.
     *
     * @param station space station to add
     */
    void addSpaceStation(SpaceStation station);

    /**
     * Removes a space station from the player's ownership.
     *
     * @param station space station to remove
     */
    void removeSpaceStation(SpaceStation station);

    /**
     * Checks if the player owns a specific space station.
     *
     * @param station space station to check
     * @return true if owned, false otherwise
     */
    boolean hasSpaceStation(SpaceStation station);
}
