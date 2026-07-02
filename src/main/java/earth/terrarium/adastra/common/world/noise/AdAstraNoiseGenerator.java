package earth.terrarium.adastra.common.world.noise;

import net.minecraft.util.math.MathHelper;
import net.minecraft.world.gen.NoiseGeneratorPerlin;

import java.util.Random;

/**
 * Noise generator for Ad Astra terrain generation.
 * Wraps Minecraft 1.12.2's NoiseGeneratorPerlin with multi-octave support.
 */
public class AdAstraNoiseGenerator {

    private final NoiseGeneratorPerlin[] octaves;
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
        this.octaves = new NoiseGeneratorPerlin[octaveCount];

        for (int i = 0; i < octaveCount; i++) {
            this.octaves[i] = new NoiseGeneratorPerlin(random, 1);
        }
    }

    /**
     * Generates 2D noise value at given coordinates.
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

            double noise = octaves[i].getValue(sampleX, sampleZ);
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

            // Use XZ plane noise combined with Y offset for 3D effect
            double noise = octaves[i].getValue(sampleX + sampleY, sampleZ);
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

    private double smoothstep(double x) {
        return x * x * (3.0 - 2.0 * x);
    }
}
