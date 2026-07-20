package earth.terrarium.adastra.common.capability;

import net.minecraft.util.math.BlockPos;

import java.util.Map;

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

}
