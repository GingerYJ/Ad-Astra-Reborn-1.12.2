package earth.terrarium.adastra.common.config;

import earth.terrarium.adastra.common.world.custom.CustomPlanetDefinition;
import earth.terrarium.adastra.common.world.custom.CustomPlanetRegistry;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * \u5185\u7f6e\u884c\u661f\u77ff\u8109\u751f\u6210\u5668\u4e0e\u81ea\u5b9a\u4e49\u751f\u6210\u5668\u7684\u914d\u7f6e\u3002
 * Configuration for built-in planet ore generators and custom generators.
 */
public final class OreGenConfig {

    private static final int MIN_Y_LIMIT = -80;
    private static final int MAX_Y_LIMIT = 320;
    private static final String CUSTOM_BLOCK_CATEGORY = "worldgen_custom_blocks";
    private static final Map<String, OreSettings> ORE_SETTINGS = new HashMap<>();
    private static final Map<CustomPlanetDefinition.OreDefinition, OreSettings> CUSTOM_PLANET_ORE_SETTINGS = new HashMap<>();
    private static final Map<String, List<CustomBlockSettings>> CUSTOM_BLOCK_SETTINGS = new HashMap<>();
    private static final String CUSTOM_BLOCK_CATEGORY_COMMENT =
        "\u4e3a\u5185\u7f6e\u884c\u661f\u6216\u5df2\u6ce8\u518c\u7684\u81ea\u5b9a\u4e49\u884c\u661f\u6dfb\u52a0\u7531 AdAstraChunkGenerator \u5904\u7406\u7684\u65b9\u5757\u6216\u77ff\u8109\u751f\u6210\u5668\u3002\u6bcf\u884c\u914d\u7f6e\u4e00\u4e2a\u751f\u6210\u5668\uff1b\u7b2c 2 \u5b57\u6bb5\u662f\u76ee\u6807\u65b9\u5757\uff0c\u7b2c 7 \u5b57\u6bb5\u662f\u5141\u8bb8\u66ff\u6362\u7684\u65b9\u5757\u3002\u884c\u661f\u540d\u5fc5\u987b\u4e0e\u5df2\u6ce8\u518c\u884c\u661f\u751f\u6210\u5668\u7684\u5185\u90e8\u540d\u4e00\u81f4\uff1b\u65e0\u6548\u884c\u661f\u540d\u7684\u884c\u4e0d\u4f1a\u5728\u4efb\u4f55\u884c\u661f\u4e2d\u751f\u6548\u3002CustomPlanetDefinition \u4e2d\u76f4\u63a5\u58f0\u660e\u7684\u77ff\u8109\u53ef\u5728\u5bf9\u5e94 worldgen_<\u884c\u661f> \u7c7b\u522b\u8c03\u6574\u56db\u4e2a\u6570\u503c\uff0c\u4f46\u4e0d\u53d7 oreGenerationMultiplier \u5f71\u54cd\u3002\u4fee\u6539\u540e\u9700\u8981\u91cd\u542f\u6e38\u620f\u6216\u670d\u52a1\u5668\uff0c\u53ea\u5bf9\u65b0\u751f\u6210\u7684\u533a\u5757\u751f\u6548\u3002\n"
            + "Add block or ore generators handled by AdAstraChunkGenerator for built-in or registered custom planets. Configure one generator per line; the second field is the target block and the seventh field lists replaceable blocks. The planet name must match a registered planet generator's internal name; a row for an unknown planet has no effect. Ores declared directly in CustomPlanetDefinition can be adjusted with four properties in the matching worldgen_<planet> category, but are not affected by oreGenerationMultiplier. A game or server restart is required after changes; changes affect newly generated chunks only.";
    private static final String CUSTOM_BLOCK_GENERATORS_COMMENT =
        "\u6bcf\u884c\u914d\u7f6e\u4e00\u4e2a\u7531 AdAstraChunkGenerator \u5904\u7406\u7684\u751f\u6210\u5668\uff1b\u7a7a\u884c\u548c\u4ee5 # \u5f00\u5934\u7684\u884c\u4f1a\u88ab\u5ffd\u7565\u3002\u683c\u5f0f\uff1a\u884c\u661f\u5185\u90e8\u540d|\u76ee\u6807\u65b9\u5757[@\u5143\u6570\u636e]|\u77ff\u8109\u5927\u5c0f|\u6bcf\u533a\u5757\u5c1d\u8bd5\u6b21\u6570|\u6700\u4f4e Y|\u6700\u9ad8 Y|\u53ef\u66ff\u6362\u65b9\u5757\u3002\u81f3\u5c11\u9700\u8981 7 \u4e2a\u5b57\u6bb5\uff1b\u7b2c 8 \u4e2a\u53ca\u4e4b\u540e\u7684\u5b57\u6bb5\u4f1a\u88ab\u5ffd\u7565\u3002\n"
            + "\u7b2c 1 \u4e2a\u5b57\u6bb5\u5fc5\u987b\u4e0e\u884c\u661f\u751f\u6210\u5668\u7684\u5185\u90e8\u540d\u5b8c\u5168\u4e00\u81f4\uff08\u4f8b\u5982 ceres\u3001jupiter\uff09\uff1b\u884c\u661f\u3001\u76ee\u6807\u65b9\u5757\u6216\u66ff\u6362\u5b57\u6bb5\u4e3a\u7a7a\u65f6\u6574\u884c\u88ab\u5ffd\u7565\u3002\u7b2c 2 \u4e2a\u5b57\u6bb5\u662f\u8981\u751f\u6210\u7684\u76ee\u6807\u65b9\u5757\uff0c\u672a\u5199 namespace \u65f6\u6309 minecraft \u89e3\u6790\uff1b\u7b2c 7 \u4e2a\u5b57\u6bb5\u4e3a\u5141\u8bb8\u88ab\u66ff\u6362\u7684\u65b9\u5757\uff0c\u53ef\u586b default\uff08\u5f53\u524d\u884c\u661f\u7684 surface/filler/cave top/cave floor \u65b9\u5757\uff09\u6216\u4ee5\u9017\u53f7\u5206\u9694\u7684\u65b9\u5757 ID\uff0c\u53ef\u9644 @\u5143\u6570\u636e\u3002\u76ee\u6807\u65b9\u5757\u65e0\u6548\u65f6\u8be5\u884c\u4e0d\u4f1a\u751f\u6210\uff1b\u66ff\u6362\u5217\u8868\u4e2d\u7684\u65e0\u6548\u65b9\u5757\u4f1a\u88ab\u5ffd\u7565\uff0c\u5982\u679c\u6ca1\u6709\u4efb\u4f55\u6709\u6548\u76ee\u6807\u5219\u56de\u9000\u4e3a\u5f53\u524d\u884c\u661f\u7684\u9ed8\u8ba4\u65b9\u5757\u3002\n"
            + "\u6570\u5b57\u89e3\u6790\u5931\u8d25\u65f6\u77ff\u8109\u5927\u5c0f\u56de\u9000\u4e3a 1\uff0c\u6bcf\u533a\u5757\u5c1d\u8bd5\u6b21\u6570\u56de\u9000\u4e3a 0\uff0c\u6700\u4f4e/\u6700\u9ad8 Y \u56de\u9000\u4e3a 0\uff1b\u4e4b\u540e\u5206\u522b\u9650\u5236\u5728 1-64\u30010-100 \u4e0e -80 \u81f3 320\u3002\u5143\u6570\u636e\u65e0\u6cd5\u89e3\u6790\u65f6\u56de\u9000\u4e3a 0\u3002\n"
            + "\u6700\u7ec8\u5c1d\u8bd5\u6b21\u6570\u8fd8\u4f1a\u53d7 oreGenerationMultiplier \u5f71\u54cd\uff1b\u6bcf\u6b21\u5c1d\u8bd5\u5728\u533a\u5757\u4e2d\u968f\u673a\u9009\u62e9\u4f4d\u7f6e\uff0c\u53ef\u80fd\u65e0\u6cd5\u653e\u7f6e\u77ff\u8109\u3002\u6700\u4f4e Y \u4e0d\u4f1a\u4f4e\u4e8e 1\uff0c\u6700\u9ad8 Y \u4e0d\u4f1a\u8d85\u8fc7\u5730\u5f62\u751f\u6210\u5668\u7684\u6700\u5927\u9ad8\u5ea6\u51cf 3\uff1b\u6700\u7ec8\u8303\u56f4\u4e0d\u5408\u6cd5\u65f6\u4e0d\u751f\u6210\u3002\u4fee\u6539\u540e\u9700\u8981\u91cd\u542f\u6e38\u620f\u6216\u670d\u52a1\u5668\uff1b\u53ea\u5bf9\u65b0\u751f\u6210\u7684\u533a\u5757\u751f\u6548\u3002\n"
            + "Configure one generator per line; blank rows and lines beginning with # are ignored. Format: planet internal name|target block[@meta]|vein size|attempts per chunk|minimum Y|maximum Y|replaceable blocks. The planet name must exactly match the generator's internal name, such as ceres or jupiter. At least seven fields are required; field 8 and later fields are ignored.\n"
            + "An empty planet, target-block, or replaceable-block field rejects the row. The target block uses the minecraft namespace when omitted. Replaceable blocks can be default (the current planet's surface, filler, cave-top, and cave-floor blocks) or comma-separated block IDs with optional @meta. An invalid target block rejects the generator. Invalid replaceable entries are skipped; if none remain valid, the generator falls back to the current planet's default replaceable blocks.\n"
            + "If numeric parsing fails, vein size falls back to 1, attempts to 0, and minimum/maximum Y to 0; values are then clamped to vein size 1-64, attempts 0-100, and Y -80 to 320. Invalid metadata falls back to 0. Final attempts are also affected by oreGenerationMultiplier, each attempt may fail to place a vein, the effective Y range is limited to 1 through the terrain generator's maximum height minus 3, and no generator is added when the final range is invalid. A game or server restart is required after changes; changes affect newly generated chunks only.";
    private static final String[] DEFAULT_CUSTOM_BLOCK_GENERATORS = new String[] {
        "# \u683c\u5f0f\uff1a\u661f\u7403|\u65b9\u5757[@\u5143\u6570\u636e]|\u77ff\u8109\u5927\u5c0f|\u6bcf\u533a\u5757\u5c1d\u8bd5\u6b21\u6570|\u6700\u4f4e Y|\u6700\u9ad8 Y|\u66ff\u6362\u76ee\u6807\u65b9\u5757\uff08default \u6216\u4ee5\u9017\u53f7\u5206\u9694\u7684\u5b8c\u6574\u65b9\u5757 ID\uff09\u3002\u77ff\u8109\u5927\u5c0f 1-64\uff0c\u6bcf\u533a\u5757\u5c1d\u8bd5\u6b21\u6570 0-100\uff0cY \u8303\u56f4 -80~320\u3002 / Format: planet|block[@meta]|vein size|attempts per chunk|minimum Y|maximum Y|replaceable blocks (default or comma-separated full block IDs). Vein size: 1-64; attempts: 0-100; Y: -80 to 320.",
        "# \u76ee\u6807\u65b9\u5757\u53ef\u4ee5\u662f\u65b0\u77ff\u77f3\u6216\u666e\u901a\u65b9\u5757\uff0c\u4e0d\u8981\u6c42\u65b9\u5757 ID \u5305\u542b ore\u3002 / Target blocks may be new ores or ordinary blocks; the ID does not need to contain ore.",
        "# \u793a\u4f8b\uff1a\u4e3a Ceres \u6dfb\u52a0\u65b0\u77ff\u77f3 / Example: add a new ore to Ceres: ceres|minecraft:gold_ore|6|2|4|60|default",
        "# \u793a\u4f8b\uff1a\u4e3a Jupiter \u6dfb\u52a0\u666e\u901a\u65b9\u5757 / Example: add an ordinary block to Jupiter: jupiter|minecraft:iron_block|1|1|20|40|default",
        "# \u793a\u4f8b / Example: moon|minecraft:iron_ore|1|1|45|70|ad_astra:moon_stone,ad_astra:moon_deepslate",
        "# \u793a\u4f8b / Example: mars|minecraft:gold_ore|8|4|8|60|default"
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
        CUSTOM_PLANET_ORE_SETTINGS.clear();
        CUSTOM_BLOCK_SETTINGS.clear();
        debugWorldgen = getBoolean(config, legacyConfig, "debugWorldgen", "worldgen", false,
            "\u662f\u5426\u542f\u7528\u77ff\u8109\u751f\u6210\u8c03\u8bd5\u65e5\u5fd7\u3002\n"
                + "Whether to enable debug logging for ore generation.");

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

        syncCustomPlanetOres(config, legacyConfig);
        registerCustomBlockGenerators(config, legacyConfig);
        removeLegacyWorldgenOreEntries(config);
    }

    private static void syncCustomPlanetOres(Configuration config, Configuration legacyConfig) {
        for (CustomPlanetDefinition planet : CustomPlanetRegistry.getDefinitions()) {
            String category = "worldgen_" + planet.getPlanetName();
            String displayName = getPlanetDisplayName(planet);
            String categoryComment = bilingual(
                displayName + " \u81ea\u5b9a\u4e49\u884c\u661f\u77ff\u8109\u751f\u6210\u8bbe\u7f6e\u3002\u6bcf\u4e2a\u5df2\u58f0\u660e\u77ff\u8109\u4f7f\u7528 ore_<configKey> \u914d\u7f6e\u7ec4\uff0c\u5e76\u5305\u542b veinSize\u3001count\u3001minY \u4e0e maxY \u56db\u4e2a\u914d\u7f6e\u9879\uff1bconfigKey \u4f1a\u8f6c\u4e3a\u5c0f\u5199\uff0c\u975e\u5b57\u6bcd\u3001\u6570\u5b57\u548c\u4e0b\u5212\u7ebf\u7684\u5b57\u7b26\u4f1a\u66ff\u6362\u4e3a\u4e0b\u5212\u7ebf\uff1b\u91cd\u540d\u65f6\u4f1a\u9644\u52a0 _2\u3001_3 \u7b49\u540e\u7f00\u3002\u76f4\u63a5\u58f0\u660e\u7684\u77ff\u8109\u76ee\u6807\u4e0e\u66ff\u6362\u65b9\u5757\u4fdd\u6301\u4e0d\u53d8\uff1b\u9700\u8981\u65b0\u589e\u5176\u4ed6\u77ff\u7269\u6216\u65b9\u5757\u65f6\u8bf7\u4f7f\u7528 worldgen_custom_blocks \u7c7b\u522b\u3002\u4fee\u6539\u540e\u9700\u8981\u91cd\u542f\u6e38\u620f\u6216\u670d\u52a1\u5668\uff0c\u53ea\u5bf9\u65b0\u751f\u6210\u7684\u533a\u5757\u751f\u6548\u3002",
                "Custom planet ore-generation settings for " + displayName + ". Each declared ore uses an ore_<configKey> group containing veinSize, count, minY, and maxY. The configKey is lower-cased, characters other than letters, digits, and underscores become underscores, and duplicate keys receive _2, _3, and later suffixes. The target and replaceable blocks of a declared ore remain unchanged; use the worldgen_custom_blocks category to add other ores or blocks. A game or server restart is required after changes; changes affect newly generated chunks only.");
            config.setCategoryComment(category, categoryComment);
            if (legacyConfig != null && legacyConfig != config) {
                legacyConfig.setCategoryComment(category, categoryComment);
            }

            Map<String, Integer> keyOccurrences = new HashMap<>();
            for (CustomPlanetDefinition.OreDefinition ore : planet.getOres()) {
                String baseKey = "ore_" + sanitizePropertyKey(ore.getConfigKey());
                int occurrence = keyOccurrences.containsKey(baseKey) ? keyOccurrences.get(baseKey) + 1 : 1;
                keyOccurrences.put(baseKey, occurrence);
                String propertyPrefix = occurrence == 1 ? baseKey : baseKey + "_" + occurrence;
                String oreDisplayName = displayName + " / " + ore.getConfigKey();

                int veinSize = getInt(config, legacyConfig, propertyPrefix + "_veinSize", category,
                    ore.getVeinSize(), 1, 64,
                    bilingual(oreDisplayName + " \u77ff\u8109\u5927\u5c0f\uff0c\u8303\u56f4\uff1a1-64\u3002",
                        oreDisplayName + " vein size, range: 1-64."));
                int count = getInt(config, legacyConfig, propertyPrefix + "_count", category,
                    ore.getCountPerChunk(), 0, 100,
                    bilingual(oreDisplayName + " \u6bcf\u533a\u5757\u751f\u6210\u5c1d\u8bd5\u6b21\u6570\uff0c\u8303\u56f4\uff1a0-100\u3002",
                        oreDisplayName + " generation attempts per chunk, range: 0-100."));
                int minY = getInt(config, legacyConfig, propertyPrefix + "_minY", category,
                    ore.getMinY(), 0, 255,
                    bilingual(oreDisplayName + " \u6700\u4f4e\u914d\u7f6e Y \u5750\u6807\uff0c\u914d\u7f6e\u8303\u56f4\uff1a0-255\uff1b\u5b9e\u9645\u751f\u6210\u4e0d\u4f1a\u4f4e\u4e8e Y=1\u3002",
                        oreDisplayName + " configured minimum Y, configuration range: 0-255; effective generation never goes below Y=1."));
                int maxY = getInt(config, legacyConfig, propertyPrefix + "_maxY", category,
                    ore.getMaxY(), 0, 255,
                    bilingual(oreDisplayName + " \u6700\u9ad8\u914d\u7f6e Y \u5750\u6807\uff0c\u914d\u7f6e\u8303\u56f4\uff1a0-255\uff1b\u5b9e\u9645\u751f\u6210\u4e0a\u9650\u4e3a Y=252\u3002\u5f53\u6700\u4f4e Y \u5927\u4e8e\u5b9e\u9645\u6700\u9ad8 Y \u65f6\u4e0d\u751f\u6210\u3002",
                        oreDisplayName + " configured maximum Y, configuration range: 0-255; effective generation is capped at Y=252. No generation occurs when the minimum Y exceeds the effective maximum Y."));
                OreSettings configured = new OreSettings(veinSize, count, minY, maxY);
                CUSTOM_PLANET_ORE_SETTINGS.put(ore, configured);
                ORE_SETTINGS.put(planet.getPlanetName() + "." + propertyPrefix, configured);
            }
        }
    }

    private static String sanitizePropertyKey(String value) {
        String source = value == null ? "ore" : value.trim().toLowerCase(Locale.ROOT);
        StringBuilder builder = new StringBuilder(source.length());
        for (int i = 0; i < source.length(); i++) {
            char c = source.charAt(i);
            if ((c >= 'a' && c <= 'z') || (c >= '0' && c <= '9') || c == '_') {
                builder.append(c);
            } else {
                builder.append('_');
            }
        }
        return builder.length() == 0 ? "ore" : builder.toString();
    }

    private static String getPlanetDisplayName(CustomPlanetDefinition planet) {
        String displayName = planet.getDisplayName();
        return displayName == null || displayName.trim().isEmpty() ? planet.getPlanetName() : displayName.trim();
    }

    private static void registerCustomBlockGenerators(Configuration config, Configuration legacyConfig) {
        config.setCategoryComment(CUSTOM_BLOCK_CATEGORY, CUSTOM_BLOCK_CATEGORY_COMMENT);
        if (legacyConfig != null && legacyConfig != config) {
            legacyConfig.setCategoryComment(CUSTOM_BLOCK_CATEGORY, CUSTOM_BLOCK_CATEGORY_COMMENT);
        }
        String[] rows = getStringList(config, legacyConfig,
            "customPlanetBlockGenerators",
            CUSTOM_BLOCK_CATEGORY,
            DEFAULT_CUSTOM_BLOCK_GENERATORS,
            CUSTOM_BLOCK_GENERATORS_COMMENT);
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
        Property property = config.get(category, key, value, comment);
        String[] existingRows = property.getStringList();
        String[] updatedRows = mergeDefaultCommentRows(existingRows, defaultValue);
        if (!sameRows(existingRows, updatedRows)) {
            property.set(updatedRows);
        }
        if (legacyHas(legacyConfig, category, key)) {
            Property legacyProperty = legacyConfig.get(category, key, defaultValue, comment);
            String[] legacyRows = legacyProperty.getStringList();
            String[] updatedLegacyRows = mergeDefaultCommentRows(legacyRows, defaultValue);
            if (!sameRows(legacyRows, updatedLegacyRows)) {
                legacyProperty.set(updatedLegacyRows);
            }
        }
        return property.getStringList();
    }

    private static String[] mergeDefaultCommentRows(String[] rows, String[] defaultValue) {
        List<String> merged = new ArrayList<>();
        if (rows != null) {
            for (String row : rows) {
                if (!isLegacyCustomBlockComment(row)) {
                    merged.add(row);
                }
            }
        }
        for (String defaultRow : defaultValue) {
            if (!containsRow(merged, defaultRow)) {
                merged.add(defaultRow);
            }
        }
        return merged.toArray(new String[merged.size()]);
    }

    private static boolean isLegacyCustomBlockComment(String row) {
        if (row == null) {
            return false;
        }
        String normalized = row.trim().toLowerCase(Locale.ROOT);
        if (!normalized.startsWith("#")) {
            return false;
        }
        return normalized.startsWith("# format: planet|block[@meta]")
            || (normalized.startsWith("# \u683c\u5f0f\uff1a\u661f\u7403|") && normalized.contains("\u6bcf\u533a\u5757\u6b21\u6570"))
            || (normalized.startsWith("# \u683c\u5f0f / format:") && normalized.contains("|count|"))
            || normalized.contains("moon|minecraft:chest|")
            || normalized.contains("mars|minecraft:gold_ore|")
            || normalized.contains("ceres|minecraft:diamond_ore|");
    }

    private static boolean containsRow(List<String> rows, String candidate) {
        for (String row : rows) {
            if (candidate == null ? row == null : candidate.equals(row)) {
                return true;
            }
        }
        return false;
    }

    private static boolean sameRows(String[] left, String[] right) {
        if (left == right) {
            return true;
        }
        if (left == null || right == null || left.length != right.length) {
            return false;
        }
        for (int i = 0; i < left.length; i++) {
            if (left[i] == null ? right[i] != null : !left[i].equals(right[i])) {
                return false;
            }
        }
        return true;
    }

    private static OreSettings registerOre(Configuration config, Configuration legacyConfig, String planetName,
                                           String oreKey, String oreName, int defaultVeinSize, int defaultCount,
                                           int defaultMinY, int defaultMaxY) {
        String category = "worldgen_" + planetName;
        String planetDisplayName = getPlanetDisplayName(planetName);
        String categoryComment =
            planetDisplayName + " \u77ff\u8109\u751f\u6210\u8bbe\u7f6e\u3002\u4e0b\u65b9\u53c2\u6570\u5206\u522b\u63a7\u5236\u77ff\u8109\u5927\u5c0f\u3001\u6bcf\u533a\u5757\u5c1d\u8bd5\u6b21\u6570\u4e0e Y \u5750\u6807\u8303\u56f4\u3002\u201cCount\u201d \u662f\u6bcf\u4e2a\u533a\u5757\u7684\u751f\u6210\u5c1d\u8bd5\u6b21\u6570\uff0c\u4e0d\u662f\u4fdd\u8bc1\u751f\u6210\u7684\u77ff\u8109\u6570\u91cf\u3002\u4fee\u6539\u540e\u9700\u8981\u91cd\u542f\u6e38\u620f\u6216\u670d\u52a1\u5668\uff0c\u53ea\u5bf9\u65b0\u751f\u6210\u7684\u533a\u5757\u751f\u6548\u3002\n"
            + "Ore-generation settings for " + getPlanetEnglishName(planetName) + ". The properties below control vein size, attempts per chunk, and the configured Y-level range. Count is the number of generation attempts per chunk, not a guaranteed number of veins. The effective range is also limited by the generator to Y 1 through the terrain generator's maximum height minus 3; no vein is generated when the effective range is invalid. A game or server restart is required after changes; changes affect newly generated chunks only.";
        config.setCategoryComment(category, categoryComment);
        if (legacyConfig != null && legacyConfig != config) {
            legacyConfig.setCategoryComment(category, categoryComment);
        }
        String displayName = getOreDisplayName(planetName, oreKey, oreName);
        int veinSize = getInt(config, legacyConfig, oreKey + "VeinSize", category, defaultVeinSize, 1, 64,
            displayName + " \u77ff\u8109\u5927\u5c0f\uff08\u6bcf\u6b21\u5c1d\u8bd5\u653e\u7f6e\u7684\u77ff\u8109\u957f\u5ea6\uff09\uff0c\u8303\u56f4\uff1a1-64\u3002\n" + oreName + " vein size (length of one placement attempt), range: 1-64.");
        int count = getInt(config, legacyConfig, oreKey + "Count", category, defaultCount, 0, 100,
            displayName + " \u6bcf\u533a\u5757\u751f\u6210\u5c1d\u8bd5\u6b21\u6570\uff08\u4e0d\u4fdd\u8bc1\u6bcf\u6b21\u6210\u529f\uff09\uff0c\u8303\u56f4\uff1a0-100\u3002\n" + oreName + " generation attempts per chunk (not guaranteed veins), range: 0-100.");
        int minY = getInt(config, legacyConfig, oreKey + "MinY", category, defaultMinY, MIN_Y_LIMIT, MAX_Y_LIMIT,
            displayName + " \u6700\u4f4e\u914d\u7f6e Y \u5750\u6807\uff0c\u914d\u7f6e\u8303\u56f4\uff1a-80 \u81f3 320\uff1b\u5b9e\u9645\u751f\u6210\u4e0d\u4f1a\u4f4e\u4e8e Y=1\u3002\n" + oreName + " configured minimum Y, configuration range: -80 to 320; effective generation never goes below Y=1.");
        int maxY = getInt(config, legacyConfig, oreKey + "MaxY", category, defaultMaxY, MIN_Y_LIMIT, MAX_Y_LIMIT,
            displayName + " \u6700\u9ad8\u914d\u7f6e Y \u5750\u6807\uff0c\u914d\u7f6e\u8303\u56f4\uff1a-80 \u81f3 320\uff1b\u5b9e\u9645\u4e0a\u9650\u4e3a\u5730\u5f62\u751f\u6210\u5668\u6700\u5927\u9ad8\u5ea6\u51cf 3\u3002\u5f53\u6700\u4f4e Y \u5927\u4e8e\u5b9e\u9645\u6700\u9ad8 Y \u65f6\u4e0d\u751f\u6210\u3002\n" + oreName + " configured maximum Y, configuration range: -80 to 320; the effective upper bound is the terrain generator's maximum height minus 3. No vein is generated when the minimum Y exceeds the effective maximum Y.");
        OreSettings settings = new OreSettings(veinSize, count, minY, maxY);
        ORE_SETTINGS.put(planetName + "." + oreKey, settings);
        return settings;
    }

    private static String getOreDisplayName(String planetName, String oreKey, String englishName) {
        String planetDisplayName = getPlanetDisplayName(planetName);
        String materialDisplayName;
        switch (oreKey) {
            case "cheese":
                materialDisplayName = "\u5976\u916a";
                break;
            case "desh":
                materialDisplayName = "Desh";
                break;
            case "iceShard":
                materialDisplayName = "\u51b0\u6676\u788e\u7247";
                break;
            case "iron":
                materialDisplayName = "\u94c1";
                break;
            case "diamond":
                materialDisplayName = "\u94bb\u77f3";
                break;
            case "ostrum":
                materialDisplayName = "Ostrum";
                break;
            case "calorite":
                materialDisplayName = "Calorite";
                break;
            case "coal":
                materialDisplayName = "\u7164\u70ad";
                break;
            case "gold":
                materialDisplayName = "\u91d1";
                break;
            case "lapis":
                materialDisplayName = "\u9752\u91d1\u77f3";
                break;
            default:
                materialDisplayName = englishName;
                break;
        }
        return planetDisplayName + " " + materialDisplayName;
    }

    private static String getPlanetDisplayName(String planetName) {
        switch (planetName) {
            case "moon":
                return "\u6708\u7403";
            case "mars":
                return "\u706b\u661f";
            case "mercury":
                return "\u6c34\u661f";
            case "venus":
                return "\u91d1\u661f";
            case "glacio":
                return "\u51b0\u5ddd\u661f";
            default:
                return planetName;
        }
    }

    private static String getPlanetEnglishName(String planetName) {
        switch (planetName) {
            case "moon":
                return "Moon";
            case "mars":
                return "Mars";
            case "mercury":
                return "Mercury";
            case "venus":
                return "Venus";
            case "glacio":
                return "Glacio";
            default:
                return planetName;
        }
    }

    private static String bilingual(String chinese, String english) {
        return chinese + "\n" + english;
    }

    public static OreSettings getOreSettings(String planetName, String oreKey) {
        OreSettings settings = ORE_SETTINGS.get(planetName + "." + oreKey);
        return settings == null ? OreSettings.DISABLED : settings;
    }

    public static OreSettings getCustomPlanetOreSettings(CustomPlanetDefinition planet,
                                                         CustomPlanetDefinition.OreDefinition ore) {
        if (ore == null) {
            return OreSettings.DISABLED;
        }
        OreSettings settings = CUSTOM_PLANET_ORE_SETTINGS.get(ore);
        return settings == null
            ? new OreSettings(ore.getVeinSize(), ore.getCountPerChunk(), ore.getMinY(), ore.getMaxY())
            : settings;
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
