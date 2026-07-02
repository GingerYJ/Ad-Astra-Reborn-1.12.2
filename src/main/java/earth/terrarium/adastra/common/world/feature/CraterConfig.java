package earth.terrarium.adastra.common.world.feature;

/**
 * Configuration for crater generation per planet.
 */
public class CraterConfig {

    private final boolean enabled;
    private final double craterChance; // Chance per chunk (0.0 - 1.0)
    private final int minCratersPerChunk;
    private final int maxCratersPerChunk;
    private final boolean allowLargeCraters;

    public CraterConfig(boolean enabled, double craterChance, int minCratersPerChunk, int maxCratersPerChunk, boolean allowLargeCraters) {
        this.enabled = enabled;
        this.craterChance = craterChance;
        this.minCratersPerChunk = minCratersPerChunk;
        this.maxCratersPerChunk = maxCratersPerChunk;
        this.allowLargeCraters = allowLargeCraters;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public double getCraterChance() {
        return craterChance;
    }

    public int getMinCratersPerChunk() {
        return minCratersPerChunk;
    }

    public int getMaxCratersPerChunk() {
        return maxCratersPerChunk;
    }

    public boolean allowLargeCraters() {
        return allowLargeCraters;
    }

    /**
     * Get crater configuration for a specific planet.
     */
    public static CraterConfig forPlanet(String planetName) {
        switch (planetName.toLowerCase()) {
            case "moon":
                // Dense craters: ~40% chance per chunk, 0-1 craters
                return new CraterConfig(true, 0.4, 0, 1, true);

            case "mars":
                // Moderate craters: ~20% chance per chunk, 0-1 craters
                return new CraterConfig(true, 0.2, 0, 1, true);

            case "mercury":
                // Very dense craters: ~60% chance per chunk, 0-2 craters
                return new CraterConfig(true, 0.6, 0, 2, true);

            case "venus":
                // Rare craters: ~5% chance per chunk
                return new CraterConfig(true, 0.05, 0, 1, false);

            case "glacio":
                // No craters on ice planet
                return new CraterConfig(false, 0.0, 0, 0, false);

            default:
                return new CraterConfig(false, 0.0, 0, 0, false);
        }
    }

    /**
     * Disabled crater config.
     */
    public static CraterConfig disabled() {
        return new CraterConfig(false, 0.0, 0, 0, false);
    }
}
