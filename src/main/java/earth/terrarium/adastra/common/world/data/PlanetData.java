package earth.terrarium.adastra.common.world.data;

import java.util.Objects;

/**
 * A mutable data class that stores the oxygen, temperature, and gravity of a planet.
 * Data is packed into a single integer for efficient storage and transmission.
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

    private boolean hasOxygen;
    private short temperature;
    private float gravity;

    public PlanetData(boolean hasOxygen, short temperature, float gravity) {
        this.hasOxygen = hasOxygen;
        this.temperature = temperature;
        this.gravity = gravity;
    }

    public boolean hasOxygen() {
        return hasOxygen;
    }

    public void setOxygen(boolean hasOxygen) {
        this.hasOxygen = hasOxygen;
    }

    public short getTemperature() {
        return temperature;
    }

    public void setTemperature(short temperature) {
        this.temperature = temperature;
    }

    public float getGravity() {
        return gravity;
    }

    public void setGravity(float gravity) {
        this.gravity = gravity;
    }

    /**
     * Packs the data into a single integer.
     *
     * @return The packed data.
     */
    public int pack() {
        int packedData = 0;
        // convert boolean to 1 or 0 and shift to its position.
        packedData |= (this.hasOxygen ? 1 : 0) << OXYGEN_BIT;
        // mask temperature to 16 bits and shift to its position.
        packedData |= (this.temperature & ((1 << TEMPERATURE_BIT_LENGTH) - 1)) << TEMPERATURE_BIT;
        // compact gravity to 2 decimal places of precision and shift to its position.
        // precision is lost here.
        packedData |= (int) (this.gravity * GRAVITY_PRECISION) << GRAVITY_BIT;

        return packedData;
    }

    /**
     * Unpacks the data from a single integer.
     *
     * @param packedData The packed data.
     * @return The unpacked data.
     */
    public static PlanetData unpack(int packedData) {
        // shift right and mask other bits to get the oxygen boolean.
        boolean hasOxygen = ((packedData >> OXYGEN_BIT) & 1) == 1;
        // shift right and mask other bits to get the temperature short.
        short temperature = (short) ((packedData >> TEMPERATURE_BIT) & ((1 << TEMPERATURE_BIT_LENGTH) - 1));
        // shift right and mask other bits to get the gravity. convert back to float with precision.
        float gravity = ((packedData >> GRAVITY_BIT) & ((1 << GRAVITY_BIT_LENGTH) - 1)) / GRAVITY_PRECISION;

        return new PlanetData(hasOxygen, temperature, gravity);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        PlanetData that = (PlanetData) obj;
        return this.hasOxygen == that.hasOxygen &&
            this.temperature == that.temperature &&
            Float.floatToIntBits(this.gravity) == Float.floatToIntBits(that.gravity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(hasOxygen, temperature, gravity);
    }

    @Override
    public String toString() {
        return "PlanetData[" +
            "hasOxygen=" + hasOxygen + ", " +
            "temperature=" + temperature + ", " +
            "gravity=" + gravity + ']';
    }
}
