package earth.terrarium.adastra.common.world.noise;

import net.minecraft.util.math.MathHelper;

import java.util.Random;

/**
 * Noise generator for Ad Astra terrain generation.
 * Uses a custom hash-based noise implementation that works reliably in Minecraft 1.12.2,
 * replacing the problematic NoiseGeneratorPerlin which can produce flat/constant output.
 */
public class AdAstraNoiseGenerator {

    private final long seed;
    private final int octaveCount;
    private final double persistence;
    private final double scale;

    /**
     * Creates a noise generator with specified parameters.
     *
     * @param random Random instance for seeding
     * @param octaveCount Number of octaves (layers of detail)
     * @param persistence How much each octave contributes (typically 0.5)
     * @param scale Base frequency scale
     */
    public AdAstraNoiseGenerator(Random random, int octaveCount, double persistence, double scale) {
        this.octaveCount = octaveCount;
        this.persistence = persistence;
        this.scale = scale;
        this.seed = random.nextLong();
    }

    /**
     * Generates 2D noise value at given coordinates.
     * Uses a hash-based pseudo-random noise that varies smoothly with position.
     *
     * @param x X coordinate
     * @param z Z coordinate
     * @return Noise value typically in range [-1, 1]
     */
    public double sample(double x, double z) {
        double total = 0.0;
        double amplitude = 1.0;
        double frequency = 1.0;
        double maxValue = 0.0;

        for (int i = 0; i < octaveCount; i++) {
            double sampleX = x * frequency / scale;
            double sampleZ = z * frequency / scale;

            double noise = valueNoise(sampleX, sampleZ, i);
            total += noise * amplitude;

            maxValue += amplitude;
            amplitude *= persistence;
            frequency *= 2.0;
        }

        return total / maxValue;
    }

    /**
     * Generates 3D noise value at given coordinates.
     * Useful for cave generation.
     *
     * @param x X coordinate
     * @param y Y coordinate
     * @param z Z coordinate
     * @return Noise value typically in range [-1, 1]
     */
    public double sample3D(double x, double y, double z) {
        double total = 0.0;
        double amplitude = 1.0;
        double frequency = 1.0;
        double maxValue = 0.0;

        for (int i = 0; i < octaveCount; i++) {
            double sampleX = x * frequency / scale;
            double sampleY = y * frequency / scale;
            double sampleZ = z * frequency / scale;

            // Combine Y into the noise sampling for 3D variation
            double noise = valueNoise(sampleX + sampleY, sampleZ + sampleY, i);
            total += noise * amplitude;

            maxValue += amplitude;
            amplitude *= persistence;
            frequency *= 2.0;
        }

        return total / maxValue;
    }

    /**
     * Sample with normalized output [0, 1].
     */
    public double sampleNormalized(double x, double z) {
        return (sample(x, z) + 1.0) * 0.5;
    }

    /**
     * Sample with custom range.
     */
    public double sampleRange(double x, double z, double min, double max) {
        double normalized = sampleNormalized(x, z);
        return min + normalized * (max - min);
    }

    /**
     * Sample height with smoothing for terrain.
     * Applies ease-in-out curve for smoother transitions.
     */
    public int sampleHeight(double x, double z, int minY, int maxY) {
        double raw = sampleNormalized(x, z);
        // Apply smoothstep for more natural terrain
        double smoothed = smoothstep(raw);
        return (int) MathHelper.clamp(minY + smoothed * (maxY - minY), minY, maxY);
    }

    /**
     * Custom value noise that uses bilinear interpolation of hash-based corner values.
     * This avoids the issues with NoiseGeneratorPerlin in MC 1.12.2.
     */
    private double valueNoise(double x, double z, int octave) {
        int floorX = (int) Math.floor(x);
        int floorZ = (int) Math.floor(z);

        double fracX = x - floorX;
        double fracZ = z - floorZ;

        // Smooth interpolation using fade curve (6t^5 - 15t^4 + 10t^3)
        double u = fade(fracX);
        double v = fade(fracZ);

        // Get hash values at the four corners
        double n00 = hashValue(floorX, floorZ, octave);
        double n10 = hashValue(floorX + 1, floorZ, octave);
        double n01 = hashValue(floorX, floorZ + 1, octave);
        double n11 = hashValue(floorX + 1, floorZ + 1, octave);

        // Bilinear interpolation
        double nx0 = n00 + (n10 - n00) * u;
        double nx1 = n01 + (n11 - n01) * u;

        return nx0 + (nx1 - nx0) * v;
    }

    /**
     * Hash function that produces a deterministic pseudo-random value for a given coordinate.
     */
    private double hashValue(int x, int z, int octave) {
        long h = seed;
        h ^= x * 374761393L + h * 668265263L;
        h ^= z * 1274126177L + h * 668265263L;
        h ^= octave * 2912359L + h * 668265263L;
        h = (h ^ (h >> 13)) * 1274126177L;
        h = h ^ (h >> 16);
        // Convert to double in range [-1, 1]
        return (h & 0x7FFFFFFFFFFFFFFFL) / (double) 0x7FFFFFFFFFFFFFFFL * 2.0 - 1.0;
    }

    /**
     * Fade curve for smooth interpolation (Perlin's improved fade function).
     * 6t^5 - 15t^4 + 10t^3
     */
    private double fade(double t) {
        return t * t * t * (t * (t * 6.0 - 15.0) + 10.0);
    }

    private double smoothstep(double x) {
        return x * x * (3.0 - 2.0 * x);
    }
}
