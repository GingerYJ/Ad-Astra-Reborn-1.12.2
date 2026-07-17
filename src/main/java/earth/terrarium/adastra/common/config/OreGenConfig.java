package earth.terrarium.adastra.common.config;

import net.minecraftforge.common.config.Configuration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Configuration for the built-in planet ore generators and custom generators. */
public final class OreGenConfig {

    private static final int MIN_Y_LIMIT = -80;
    private static final int MAX_Y_LIMIT = 320;
    private static final String CUSTOM_BLOCK_CATEGORY = "worldgen_custom_blocks";
    private static final Map<String, OreSettings> ORE_SETTINGS = new HashMap<>();
    private static final Map<String, List<CustomBlockSettings>> CUSTOM_BLOCK_SETTINGS = new HashMap<>();
    private static final String[] DEFAULT_CUSTOM_BLOCK_GENERATORS = new String[] {
        "# Format: planet|block[@meta]|vein size|count|minimum Y|maximum Y|replaceable blocks",
        "# Example: moon|minecraft:chest|1|1|45|70|ad_astra:moon_stone,ad_astra:moon_deepslate",
        "# Example: mars|minecraft:gold_ore|8|4|8|60|default"
    };

    public static int moonCheeseVeinSize;
    public static int moonCheeseCount;
    public static int moonCheeseMinY;
    public static int moonCheeseMaxY;
    public static int moonDeshVeinSize;
    public static int moonDeshCount;
    public static int moonDeshMinY;
    public static int moonDeshMaxY;
    public static int moonIceShardVeinSize;
    public static int moonIceShardCount;
    public static int moonIceShardMinY;
    public static int moonIceShardMaxY;
    public static int moonIronVeinSize;
    public static int moonIronCount;
    public static int moonIronMinY;
    public static int moonIronMaxY;

    public static int marsDiamondVeinSize;
    public static int marsDiamondCount;
    public static int marsDiamondMinY;
    public static int marsDiamondMaxY;
    public static int marsIceShardVeinSize;
    public static int marsIceShardCount;
    public static int marsIceShardMinY;
    public static int marsIceShardMaxY;
    public static int marsIronVeinSize;
    public static int marsIronCount;
    public static int marsIronMinY;
    public static int marsIronMaxY;
    public static int marsOstrumVeinSize;
    public static int marsOstrumCount;
    public static int marsOstrumMinY;
    public static int marsOstrumMaxY;

    public static int mercuryIronVeinSize;
    public static int mercuryIronCount;
    public static int mercuryIronMinY;
    public static int mercuryIronMaxY;

    public static int venusCaloriteVeinSize;
    public static int venusCaloriteCount;
    public static int venusCaloriteMinY;
    public static int venusCaloriteMaxY;
    public static int venusCoalVeinSize;
    public static int venusCoalCount;
    public static int venusCoalMinY;
    public static int venusCoalMaxY;
    public static int venusDiamondVeinSize;
    public static int venusDiamondCount;
    public static int venusDiamondMinY;
    public static int venusDiamondMaxY;
    public static int venusGoldVeinSize;
    public static int venusGoldCount;
    public static int venusGoldMinY;
    public static int venusGoldMaxY;

    public static int glacioCoalVeinSize;
    public static int glacioCoalCount;
    public static int glacioCoalMinY;
    public static int glacioCoalMaxY;
    public static int glacioIceShardVeinSize;
    public static int glacioIceShardCount;
    public static int glacioIceShardMinY;
    public static int glacioIceShardMaxY;
    public static int glacioIronVeinSize;
    public static int glacioIronCount;
    public static int glacioIronMinY;
    public static int glacioIronMaxY;
    public static int glacioLapisVeinSize;
    public static int glacioLapisCount;
    public static int glacioLapisMinY;
    public static int glacioLapisMaxY;

    public static boolean debugWorldgen;

    private OreGenConfig() {
    }

    public static void sync(Configuration config) {
        sync(config, null);
    }

    public static void sync(Configuration config, Configuration legacyConfig) {
        ORE_SETTINGS.clear();
        CUSTOM_BLOCK_SETTINGS.clear();
        debugWorldgen = getBoolean(config, legacyConfig, "debugWorldgen", "worldgen", false,
            "Enable ore generation debug logging.");

        OreSettings settings = registerOre(config, legacyConfig, "moon", "cheese", "Moon cheese", 8, 9, 6, 192);
        moonCheeseVeinSize = settings.veinSize;
        moonCheeseCount = settings.countPerChunk;
        moonCheeseMinY = settings.minY;
        moonCheeseMaxY = settings.maxY;
        settings = registerOre(config, legacyConfig, "moon", "desh", "Moon desh", 9, 9, -80, 80);
        moonDeshVeinSize = settings.veinSize;
        moonDeshCount = settings.countPerChunk;
        moonDeshMinY = settings.minY;
        moonDeshMaxY = settings.maxY;
        settings = registerOre(config, legacyConfig, "moon", "iceShard", "Moon ice shard", 10, 8, -32, 32);
        moonIceShardVeinSize = settings.veinSize;
        moonIceShardCount = settings.countPerChunk;
        moonIceShardMinY = settings.minY;
        moonIceShardMaxY = settings.maxY;
        settings = registerOre(config, legacyConfig, "moon", "iron", "Moon iron", 11, 10, -24, 56);
        moonIronVeinSize = settings.veinSize;
        moonIronCount = settings.countPerChunk;
        moonIronMinY = settings.minY;
        moonIronMaxY = settings.maxY;

        settings = registerOre(config, legacyConfig, "mars", "diamond", "Mars diamond", 7, 5, -80, 80);
        marsDiamondVeinSize = settings.veinSize;
        marsDiamondCount = settings.countPerChunk;
        marsDiamondMinY = settings.minY;
        marsDiamondMaxY = settings.maxY;
        settings = registerOre(config, legacyConfig, "mars", "iceShard", "Mars ice shard", 10, 8, -32, 32);
        marsIceShardVeinSize = settings.veinSize;
        marsIceShardCount = settings.countPerChunk;
        marsIceShardMinY = settings.minY;
        marsIceShardMaxY = settings.maxY;
        settings = registerOre(config, legacyConfig, "mars", "iron", "Mars iron", 11, 10, -24, 56);
        marsIronVeinSize = settings.veinSize;
        marsIronCount = settings.countPerChunk;
        marsIronMinY = settings.minY;
        marsIronMaxY = settings.maxY;
        settings = registerOre(config, legacyConfig, "mars", "ostrum", "Mars ostrum", 8, 8, -80, 80);
        marsOstrumVeinSize = settings.veinSize;
        marsOstrumCount = settings.countPerChunk;
        marsOstrumMinY = settings.minY;
        marsOstrumMaxY = settings.maxY;

        settings = registerOre(config, legacyConfig, "mercury", "iron", "Mercury iron", 8, 20, -80, 192);
        mercuryIronVeinSize = settings.veinSize;
        mercuryIronCount = settings.countPerChunk;
        mercuryIronMinY = settings.minY;
        mercuryIronMaxY = settings.maxY;

        settings = registerOre(config, legacyConfig, "venus", "calorite", "Venus calorite", 8, 8, -80, 80);
        venusCaloriteVeinSize = settings.veinSize;
        venusCaloriteCount = settings.countPerChunk;
        venusCaloriteMinY = settings.minY;
        venusCaloriteMaxY = settings.maxY;
        settings = registerOre(config, legacyConfig, "venus", "coal", "Venus coal", 17, 20, -80, 192);
        venusCoalVeinSize = settings.veinSize;
        venusCoalCount = settings.countPerChunk;
        venusCoalMinY = settings.minY;
        venusCoalMaxY = settings.maxY;
        settings = registerOre(config, legacyConfig, "venus", "diamond", "Venus diamond", 9, 5, -80, 80);
        venusDiamondVeinSize = settings.veinSize;
        venusDiamondCount = settings.countPerChunk;
        venusDiamondMinY = settings.minY;
        venusDiamondMaxY = settings.maxY;
        settings = registerOre(config, legacyConfig, "venus", "gold", "Venus gold", 10, 4, -64, 32);
        venusGoldVeinSize = settings.veinSize;
        venusGoldCount = settings.countPerChunk;
        venusGoldMinY = settings.minY;
        venusGoldMaxY = settings.maxY;

        settings = registerOre(config, legacyConfig, "glacio", "coal", "Glacio coal", 17, 20, -80, 192);
        glacioCoalVeinSize = settings.veinSize;
        glacioCoalCount = settings.countPerChunk;
        glacioCoalMinY = settings.minY;
        glacioCoalMaxY = settings.maxY;
        settings = registerOre(config, legacyConfig, "glacio", "iceShard", "Glacio ice shard", 17, 8, -32, 32);
        glacioIceShardVeinSize = settings.veinSize;
        glacioIceShardCount = settings.countPerChunk;
        glacioIceShardMinY = settings.minY;
        glacioIceShardMaxY = settings.maxY;
        settings = registerOre(config, legacyConfig, "glacio", "iron", "Glacio iron", 11, 10, -24, 56);
        glacioIronVeinSize = settings.veinSize;
        glacioIronCount = settings.countPerChunk;
        glacioIronMinY = settings.minY;
        glacioIronMaxY = settings.maxY;
        settings = registerOre(config, legacyConfig, "glacio", "lapis", "Glacio lapis", 9, 2, -32, 32);
        glacioLapisVeinSize = settings.veinSize;
        glacioLapisCount = settings.countPerChunk;
        glacioLapisMinY = settings.minY;
        glacioLapisMaxY = settings.maxY;

        registerCustomBlockGenerators(config, legacyConfig);
        removeLegacyWorldgenOreEntries(config);
    }

    private static void registerCustomBlockGenerators(Configuration config, Configuration legacyConfig) {
        config.setCategoryComment(CUSTOM_BLOCK_CATEGORY,
            "Additional configured block or ore generators for built-in or custom planets.");
        String[] rows = getStringList(config, legacyConfig,
            "customPlanetBlockGenerators",
            CUSTOM_BLOCK_CATEGORY,
            DEFAULT_CUSTOM_BLOCK_GENERATORS,
            "One generator per line. Lines beginning with # are ignored.");
        for (String row : rows) {
            CustomBlockSettings settings = parseCustomBlockSettings(row);
            if (settings != null) {
                CUSTOM_BLOCK_SETTINGS.computeIfAbsent(settings.planetName, key -> new ArrayList<>()).add(settings);
            }
        }
    }

    private static CustomBlockSettings parseCustomBlockSettings(String row) {
        if (row == null) {
            return null;
        }
        String trimmed = row.trim();
        if (trimmed.isEmpty() || trimmed.startsWith("#")) {
            return null;
        }
        String[] parts = trimmed.split("\\|", -1);
        if (parts.length < 7) {
            return null;
        }
        try {
            String planetName = parts[0].trim();
            String blockState = parts[1].trim();
            int veinSize = clamp(parseInt(parts[2], 1), 1, 64);
            int countPerChunk = clamp(parseInt(parts[3], 0), 0, 100);
            int minY = clamp(parseInt(parts[4], 0), MIN_Y_LIMIT, MAX_Y_LIMIT);
            int maxY = clamp(parseInt(parts[5], 0), MIN_Y_LIMIT, MAX_Y_LIMIT);
            String replaceTargets = parts[6].trim();
            if (planetName.isEmpty() || blockState.isEmpty() || replaceTargets.isEmpty()) {
                return null;
            }
            return new CustomBlockSettings(planetName, blockState, veinSize, countPerChunk, minY, maxY, replaceTargets);
        } catch (RuntimeException ignored) {
            return null;
        }
    }

    private static int parseInt(String value, int fallback) {
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException ignored) {
            return fallback;
        }
    }

    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    public static List<CustomBlockSettings> getCustomBlockSettings(String planetName) {
        List<CustomBlockSettings> settings = CUSTOM_BLOCK_SETTINGS.get(planetName);
        return settings == null ? Collections.emptyList() : Collections.unmodifiableList(settings);
    }

    private static boolean legacyHas(Configuration legacyConfig, String category, String key) {
        return legacyConfig != null && legacyConfig.hasKey(category, key);
    }

    private static boolean getBoolean(Configuration config, Configuration legacyConfig, String key, String category,
                                      boolean defaultValue, String comment) {
        boolean value = defaultValue;
        if (legacyHas(legacyConfig, category, key)) {
            value = legacyConfig.getBoolean(key, category, defaultValue, comment);
        }
        return config.getBoolean(key, category, value, comment);
    }

    private static int getInt(Configuration config, Configuration legacyConfig, String key, String category,
                              int defaultValue, int minValue, int maxValue, String comment) {
        int value = defaultValue;
        if (legacyHas(legacyConfig, category, key)) {
            value = legacyConfig.getInt(key, category, defaultValue, minValue, maxValue, comment);
        }
        return config.getInt(key, category, value, minValue, maxValue, comment);
    }

    private static String[] getStringList(Configuration config, Configuration legacyConfig, String key, String category,
                                          String[] defaultValue, String comment) {
        String[] value = defaultValue;
        if (legacyHas(legacyConfig, category, key)) {
            value = legacyConfig.getStringList(key, category, defaultValue, comment);
        }
        return config.getStringList(key, category, value, comment);
    }

    private static OreSettings registerOre(Configuration config, Configuration legacyConfig, String planetName,
                                           String oreKey, String oreName, int defaultVeinSize, int defaultCount,
                                           int defaultMinY, int defaultMaxY) {
        String category = "worldgen_" + planetName;
        int veinSize = getInt(config, legacyConfig, oreKey + "VeinSize", category, defaultVeinSize, 1, 64,
            oreName + " vein size.");
        int count = getInt(config, legacyConfig, oreKey + "Count", category, defaultCount, 0, 100,
            oreName + " attempts per chunk.");
        int minY = getInt(config, legacyConfig, oreKey + "MinY", category, defaultMinY, MIN_Y_LIMIT, MAX_Y_LIMIT,
            oreName + " minimum Y.");
        int maxY = getInt(config, legacyConfig, oreKey + "MaxY", category, defaultMaxY, MIN_Y_LIMIT, MAX_Y_LIMIT,
            oreName + " maximum Y.");
        OreSettings settings = new OreSettings(veinSize, count, minY, maxY);
        ORE_SETTINGS.put(planetName + "." + oreKey, settings);
        return settings;
    }

    public static OreSettings getOreSettings(String planetName, String oreKey) {
        OreSettings settings = ORE_SETTINGS.get(planetName + "." + oreKey);
        return settings == null ? OreSettings.DISABLED : settings;
    }

    public static Map<String, OreSettings> getOreSettingsView() {
        return Collections.unmodifiableMap(ORE_SETTINGS);
    }

    private static void removeLegacyWorldgenOreEntries(Configuration config) {
        if (!config.hasCategory("worldgen")) {
            return;
        }
        String[] legacyKeys = new String[] {
            "moonCheeseVeinSize", "moonCheeseCount", "moonCheeseMinY", "moonCheeseMaxY",
            "moonDeshVeinSize", "moonDeshCount", "moonDeshMinY", "moonDeshMaxY",
            "moonIceShardVeinSize", "moonIceShardCount", "moonIceShardMinY", "moonIceShardMaxY",
            "moonIronVeinSize", "moonIronCount", "moonIronMinY", "moonIronMaxY",
            "marsDiamondVeinSize", "marsDiamondCount", "marsDiamondMinY", "marsDiamondMaxY",
            "marsIceShardVeinSize", "marsIceShardCount", "marsIceShardMinY", "marsIceShardMaxY",
            "marsIronVeinSize", "marsIronCount", "marsIronMinY", "marsIronMaxY",
            "marsOstrumVeinSize", "marsOstrumCount", "marsOstrumMinY", "marsOstrumMaxY",
            "mercuryIronVeinSize", "mercuryIronCount", "mercuryIronMinY", "mercuryIronMaxY",
            "venusCaloriteVeinSize", "venusCaloriteCount", "venusCaloriteMinY", "venusCaloriteMaxY",
            "venusCoalVeinSize", "venusCoalCount", "venusCoalMinY", "venusCoalMaxY",
            "venusDiamondVeinSize", "venusDiamondCount", "venusDiamondMinY", "venusDiamondMaxY",
            "venusGoldVeinSize", "venusGoldCount", "venusGoldMinY", "venusGoldMaxY",
            "glacioCoalVeinSize", "glacioCoalCount", "glacioCoalMinY", "glacioCoalMaxY",
            "glacioIceShardVeinSize", "glacioIceShardCount", "glacioIceShardMinY", "glacioIceShardMaxY",
            "glacioIronVeinSize", "glacioIronCount", "glacioIronMinY", "glacioIronMaxY",
            "glacioLapisVeinSize", "glacioLapisCount", "glacioLapisMinY", "glacioLapisMaxY"
        };
        boolean removed = false;
        for (String key : legacyKeys) {
            removed |= config.getCategory("worldgen").remove(key) != null;
        }
        if (removed) {
            config.save();
        }
    }

    public static int getModifiedOreCount(int baseCount) {
        return AdAstraConfig.getModifiedOreGeneration(baseCount);
    }

    public static final class CustomBlockSettings {
        public final String planetName;
        public final String blockState;
        public final int veinSize;
        public final int countPerChunk;
        public final int minY;
        public final int maxY;
        public final String replaceTargets;

        private CustomBlockSettings(String planetName, String blockState, int veinSize, int countPerChunk,
                                    int minY, int maxY, String replaceTargets) {
            this.planetName = planetName;
            this.blockState = blockState;
            this.veinSize = veinSize;
            this.countPerChunk = countPerChunk;
            this.minY = minY;
            this.maxY = maxY;
            this.replaceTargets = replaceTargets;
        }
    }

    public static final class OreSettings {
        public static final OreSettings DISABLED = new OreSettings(0, 0, 0, 0);
        public final int veinSize;
        public final int countPerChunk;
        public final int minY;
        public final int maxY;

        private OreSettings(int veinSize, int countPerChunk, int minY, int maxY) {
            this.veinSize = veinSize;
            this.countPerChunk = countPerChunk;
            this.minY = minY;
            this.maxY = maxY;
        }
    }
}
