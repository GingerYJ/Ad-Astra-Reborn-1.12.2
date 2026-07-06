package earth.terrarium.adastra.common.world;

/**
 * Terrain configuration for each planet.
 * Defines height ranges, noise parameters, and surface characteristics.
 */
public class PlanetTerrainConfig {

    private final int baseHeight;
    private final int minHeight;
    private final int maxHeight;
    private final int surfaceDepth;
    private final int fillerDepth;

    // Noise parameters
    private final int octaves;
    private final double persistence;
    private final double scale;

    // Cave parameters
    private final boolean generateCaves;
    private final double caveThreshold;
    private final int caveMinY;
    private final int caveMaxY;

    private PlanetTerrainConfig(Builder builder) {
        this.baseHeight = builder.baseHeight;
        this.minHeight = builder.minHeight;
        this.maxHeight = builder.maxHeight;
        this.surfaceDepth = builder.surfaceDepth;
        this.fillerDepth = builder.fillerDepth;
        this.octaves = builder.octaves;
        this.persistence = builder.persistence;
        this.scale = builder.scale;
        this.generateCaves = builder.generateCaves;
        this.caveThreshold = builder.caveThreshold;
        this.caveMinY = builder.caveMinY;
        this.caveMaxY = builder.caveMaxY;
    }

    public int getBaseHeight() {
        return baseHeight;
    }

    public int getMinHeight() {
        return minHeight;
    }

    public int getMaxHeight() {
        return maxHeight;
    }

    public int getSurfaceDepth() {
        return surfaceDepth;
    }

    public int getFillerDepth() {
        return fillerDepth;
    }

    public int getOctaves() {
        return octaves;
    }

    public double getPersistence() {
        return persistence;
    }

    public double getScale() {
        return scale;
    }

    public boolean shouldGenerateCaves() {
        return generateCaves;
    }

    public double getCaveThreshold() {
        return caveThreshold;
    }

    public int getCaveMinY() {
        return caveMinY;
    }

    public int getCaveMaxY() {
        return caveMaxY;
    }

    /**
     * Get terrain config for a specific planet.
     */
    public static PlanetTerrainConfig forPlanet(String planetName) {
        switch (planetName.toLowerCase()) {
            case "moon":
                return moon();
            case "mars":
                return mars();
            case "mercury":
                return mercury();
            case "venus":
                return venus();
            case "glacio":
                return glacio();
            default:
                return defaultConfig();
        }
    }

    /**
     * Moon: Low hills with flat plains.
     * Minimal variation to reflect lunar maria.
     */
    private static PlanetTerrainConfig moon() {
        return new Builder()
            .baseHeight(63)
            .minHeight(50)
            .maxHeight(75)
            .surfaceDepth(1)
            .fillerDepth(62)
            .octaves(3)
            .persistence(0.4)
            .scale(120.0)
            .caves(true, 0.6, 5, 55)
            .build();
    }

    /**
     * Mars: Medium variation with canyons and plateaus.
     * More dramatic terrain than Moon.
     */
    private static PlanetTerrainConfig mars() {
        return new Builder()
            .baseHeight(63)
            .minHeight(45)
            .maxHeight(85)
            .surfaceDepth(2)
            .fillerDepth(61)
            .octaves(4)
            .persistence(0.5)
            .scale(100.0)
            .caves(true, 0.55, 5, 60)
            .build();
    }

    /**
     * Mercury: Very rough mountainous terrain.
     * High variation with steep features.
     */
    private static PlanetTerrainConfig mercury() {
        return new Builder()
            .baseHeight(63)
            .minHeight(40)
            .maxHeight(90)
            .surfaceDepth(1)
            .fillerDepth(62)
            .octaves(5)
            .persistence(0.6)
            .scale(80.0)
            .caves(true, 0.5, 5, 55)
            .build();
    }

    /**
     * Venus: Moderate hills with volcanic features.
     * Moderate variation, relatively smooth.
     */
    private static PlanetTerrainConfig venus() {
        return new Builder()
            .baseHeight(63)
            .minHeight(50)
            .maxHeight(80)
            .surfaceDepth(2)
            .fillerDepth(61)
            .octaves(4)
            .persistence(0.45)
            .scale(110.0)
            .caves(true, 0.58, 5, 58)
            .build();
    }

    /**
     * Glacio: Ice mountains with deep valleys.
     * Highest variation for dramatic frozen landscape.
     */
    private static PlanetTerrainConfig glacio() {
        return new Builder()
            .baseHeight(63)
            .minHeight(45)
            .maxHeight(95)
            .surfaceDepth(3)
            .fillerDepth(60)
            .octaves(5)
            .persistence(0.55)
            .scale(90.0)
            .caves(true, 0.52, 5, 62)
            .build();
    }

    /**
     * Default config for custom/unknown planets.
     * Provides varied terrain comparable to Mars.
     */
    private static PlanetTerrainConfig defaultConfig() {
        return new Builder()
            .baseHeight(63)
            .minHeight(45)
            .maxHeight(85)
            .surfaceDepth(2)
            .fillerDepth(61)
            .octaves(4)
            .persistence(0.5)
            .scale(100.0)
            .caves(true, 0.55, 5, 60)
            .build();
    }

    public static class Builder {
        private int baseHeight = 63;
        private int minHeight = 63;
        private int maxHeight = 63;
        private int surfaceDepth = 1;
        private int fillerDepth = 62;
        private int octaves = 3;
        private double persistence = 0.5;
        private double scale = 100.0;
        private boolean generateCaves = false;
        private double caveThreshold = 0.6;
        private int caveMinY = 5;
        private int caveMaxY = 60;

        public Builder baseHeight(int baseHeight) {
            this.baseHeight = baseHeight;
            return this;
        }

        public Builder minHeight(int minHeight) {
            this.minHeight = minHeight;
            return this;
        }

        public Builder maxHeight(int maxHeight) {
            this.maxHeight = maxHeight;
            return this;
        }

        public Builder surfaceDepth(int surfaceDepth) {
            this.surfaceDepth = surfaceDepth;
            return this;
        }

        public Builder fillerDepth(int fillerDepth) {
            this.fillerDepth = fillerDepth;
            return this;
        }

        public Builder octaves(int octaves) {
            this.octaves = octaves;
            return this;
        }

        public Builder persistence(double persistence) {
            this.persistence = persistence;
            return this;
        }

        public Builder scale(double scale) {
            this.scale = scale;
            return this;
        }

        public Builder caves(boolean generate, double threshold, int minY, int maxY) {
            this.generateCaves = generate;
            this.caveThreshold = threshold;
            this.caveMinY = minY;
            this.caveMaxY = maxY;
            return this;
        }

        public PlanetTerrainConfig build() {
            return new PlanetTerrainConfig(this);
        }
    }
}

