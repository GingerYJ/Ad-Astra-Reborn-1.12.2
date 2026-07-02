package earth.terrarium.adastra.api.systems;

import java.util.Objects;

/**
 * A mutable data class that stores the oxygen, temperature, and gravity of a planet.
 * Data is packed into a single integer for efficient storage and transmission.
 * <p>
 * This class provides methods to pack/unpack atmospheric data for network synchronization
 * and storage. It uses bit packing to efficiently encode three values into a 32-bit integer:
 * <ul>
 *   <li>Oxygen: 1 bit (boolean)</li>
 *   <li>Temperature: 16 bits (signed short)</li>
 *   <li>Gravity: 15 bits (unsigned compact float with 2 decimal places precision)</li>
 * </ul>
 *
 * @since 1.12.2
 */
public final class PlanetData {

    private static final int OXYGEN_BIT_LENGTH = 1; // boolean
    private static final int TEMPERATURE_BIT_LENGTH = Short.SIZE; // 16-bit signed short
    private static final int GRAVITY_BIT_LENGTH = 15; // unsigned compact float

    private static final float GRAVITY_PRECISION = 100.0f; // 2 decimal places of precision

    // 32 bits (i32) total
    private static final int OXYGEN_BIT = 0; // first bit
    private static final int TEMPERATURE_BIT = OXYGEN_BIT + OXYGEN_BIT_LENGTH; // next 16 bits
    private static final int GRAVITY_BIT = TEMPERATURE_BIT + TEMPERATURE_BIT_LENGTH; // next 15 bits

    private boolean oxygen;
    private short temperature;
    private float gravity;

    /**
     * Constructs a new PlanetData instance.
     *
     * @param oxygen Whether the planet has breathable oxygen
     * @param temperature The temperature in Celsius
     * @param gravity The gravity multiplier (1.0 = Earth gravity)
     */
    public PlanetData(boolean oxygen, short temperature, float gravity) {
        this.oxygen = oxygen;
        this.temperature = temperature;
        this.gravity = gravity;
    }

    /**
     * Gets whether the planet has breathable oxygen.
     *
     * @return true if the planet has oxygen, false otherwise
     */
    public boolean oxygen() {
        return oxygen;
    }

    /**
     * Sets whether the planet has breathable oxygen.
     *
     * @param oxygen true if the planet has oxygen, false otherwise
     */
    public void setOxygen(boolean oxygen) {
        this.oxygen = oxygen;
    }

    /**
     * Gets the temperature in Celsius.
     *
     * @return The temperature value
     */
    public short temperature() {
        return temperature;
    }

    /**
     * Sets the temperature in Celsius.
     *
     * @param temperature The temperature value
     */
    public void setTemperature(short temperature) {
        this.temperature = temperature;
    }

    /**
     * Gets the gravity multiplier.
     *
     * @return The gravity multiplier (1.0 = Earth gravity)
     */
    public float gravity() {
        return gravity;
    }

    /**
     * Sets the gravity multiplier.
     *
     * @param gravity The gravity multiplier (1.0 = Earth gravity)
     */
    public void setGravity(float gravity) {
        this.gravity = gravity;
    }

    /**
     * Packs the data into a single integer for efficient network transmission or storage.
     *
     * @return The packed data as a 32-bit integer
     */
    public int pack() {
        int packedData = 0;
        // convert boolean to 1 or 0 and shift to its position.
        packedData |= (this.oxygen ? 1 : 0) << OXYGEN_BIT;
        // mask temperature to 16 bits and shift to its position.
        packedData |= (this.temperature & ((1 << TEMPERATURE_BIT_LENGTH) - 1)) << TEMPERATURE_BIT;
        // compact gravity to 2 decimal places of precision and shift to its position.
        // precision is lost here.
        packedData |= (int) (this.gravity * GRAVITY_PRECISION) << GRAVITY_BIT;

        return packedData;
    }

    /**
     * Unpacks planet data from a single integer.
     *
     * @param packedData The packed data as a 32-bit integer
     * @return A new PlanetData instance with the unpacked values
     */
    public static PlanetData unpack(int packedData) {
        // shift right and mask other bits to get the oxygen boolean.
        boolean oxygen = ((packedData >> OXYGEN_BIT) & 1) == 1;
        // shift right and mask other bits to get the temperature short.
        short temperature = (short) ((packedData >> TEMPERATURE_BIT) & ((1 << TEMPERATURE_BIT_LENGTH) - 1));
        // shift right and mask other bits to get the gravity. convert back to float with precision.
        float gravity = ((packedData >> GRAVITY_BIT) & ((1 << GRAVITY_BIT_LENGTH) - 1)) / GRAVITY_PRECISION;

        return new PlanetData(oxygen, temperature, gravity);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        PlanetData that = (PlanetData) obj;
        return this.oxygen == that.oxygen &&
            this.temperature == that.temperature &&
            Float.floatToIntBits(this.gravity) == Float.floatToIntBits(that.gravity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(oxygen, temperature, gravity);
    }

    @Override
    public String toString() {
        return "PlanetData[" +
            "oxygen=" + oxygen + ", " +
            "temperature=" + temperature + ", " +
            "gravity=" + gravity + ']';
    }
}
