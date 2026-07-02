package earth.terrarium.adastra.common.systems;

import java.util.Objects;

/**
 * Mutable data class storing oxygen, temperature, and gravity for a block position.
 * Data is packed into a single integer for efficient storage and network transmission.
 * <p>
 * Format (32 bits total):
 * - Bit 0: Oxygen (boolean, 1 bit)
 * - Bits 1-16: Temperature (signed short, 16 bits)
 * - Bits 17-31: Gravity (compact float with 2 decimal precision, 15 bits)
 */
public final class PlanetData {

    private static final int OXYGEN_BIT_LENGTH = 1;
    private static final int TEMPERATURE_BIT_LENGTH = Short.SIZE;
    private static final int GRAVITY_BIT_LENGTH = 15;

    private static final float GRAVITY_PRECISION = 100.0f;

    private static final int OXYGEN_BIT = 0;
    private static final int TEMPERATURE_BIT = OXYGEN_BIT + OXYGEN_BIT_LENGTH;
    private static final int GRAVITY_BIT = TEMPERATURE_BIT + TEMPERATURE_BIT_LENGTH;

    private boolean oxygen;
    private short temperature;
    private float gravity;

    public PlanetData(boolean oxygen, short temperature, float gravity) {
        this.oxygen = oxygen;
        this.temperature = temperature;
        this.gravity = gravity;
    }

    public boolean oxygen() {
        return oxygen;
    }

    public void setOxygen(boolean oxygen) {
        this.oxygen = oxygen;
    }

    public short temperature() {
        return temperature;
    }

    public void setTemperature(short temperature) {
        this.temperature = temperature;
    }

    public float gravity() {
        return gravity;
    }

    public void setGravity(float gravity) {
        this.gravity = gravity;
    }

    public int pack() {
        int packedData = 0;
        packedData |= (this.oxygen ? 1 : 0) << OXYGEN_BIT;
        packedData |= (this.temperature & ((1 << TEMPERATURE_BIT_LENGTH) - 1)) << TEMPERATURE_BIT;
        packedData |= (int) (this.gravity * GRAVITY_PRECISION) << GRAVITY_BIT;
        return packedData;
    }

    public static PlanetData unpack(int packedData) {
        boolean oxygen = ((packedData >> OXYGEN_BIT) & 1) == 1;
        short temperature = (short) ((packedData >> TEMPERATURE_BIT) & ((1 << TEMPERATURE_BIT_LENGTH) - 1));
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
        return "PlanetData[oxygen=" + oxygen + ", temperature=" + temperature + ", gravity=" + gravity + ']';
    }
}
