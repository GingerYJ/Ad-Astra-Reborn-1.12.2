package earth.terrarium.adastra.common.world.feature;

public class CraterConfig {

    private final boolean enabled;
    private final double craterChance;
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

    public boolean isEnabled() { return enabled; }
    public double getCraterChance() { return craterChance; }
    public int getMinCratersPerChunk() { return minCratersPerChunk; }
    public int getMaxCratersPerChunk() { return maxCratersPerChunk; }
    public boolean allowLargeCraters() { return allowLargeCraters; }

    public static CraterConfig forPlanet(String planetName) {
        switch (planetName.toLowerCase()) {
            case "moon": return new CraterConfig(true, 0.4, 0, 1, true);
            case "mars": return new CraterConfig(true, 0.2, 0, 1, true);
            case "mercury": return new CraterConfig(true, 0.6, 0, 2, true);
            case "venus": return new CraterConfig(true, 0.05, 0, 1, false);
            case "glacio": return new CraterConfig(false, 0.0, 0, 0, false);
            default: return new CraterConfig(false, 0.0, 0, 0, false);
        }
    }

    public static CraterConfig disabled() { return new CraterConfig(false, 0.0, 0, 0, false); }
}
