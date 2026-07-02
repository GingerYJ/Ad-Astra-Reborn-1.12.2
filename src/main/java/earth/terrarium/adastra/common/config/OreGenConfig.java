package earth.terrarium.adastra.common.config;

import net.minecraftforge.common.config.Configuration;

/**
 * 矿石生成配置
 * 控制各行星矿石的矿脉大小、生成频率和Y范围
 * Ore generation configuration controlled by AdAstraConfig.oreGenerationMultiplier
 */
public final class OreGenConfig {

    // Moon (月球)
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

    // Mars (火星)
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

    // Mercury (水星)
    public static int mercuryIronVeinSize;
    public static int mercuryIronCount;
    public static int mercuryIronMinY;
    public static int mercuryIronMaxY;

    // Venus (金星)
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

    // Glacio (冰川星)
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

    // 世界生成调试
    public static boolean debugWorldgen;

    private OreGenConfig() {
    }

    public static void sync(Configuration config) {
        String category = "worldgen";

        debugWorldgen = config.getBoolean(
            "debugWorldgen",
            category,
            false,
            "启用矿物生成调试日志。"
        );

        // Moon ores
        moonCheeseVeinSize = config.getInt("moonCheeseVeinSize", category, 8, 1, 64,
            veinSizeComment("月球奶酪矿"));
        moonCheeseCount = config.getInt("moonCheeseCount", category, 9, 0, 100,
            veinCountComment("月球奶酪矿"));
        moonCheeseMinY = config.getInt("moonCheeseMinY", category, 6, -80, 320,
            minYComment("月球奶酪矿"));
        moonCheeseMaxY = config.getInt("moonCheeseMaxY", category, 192, -80, 320,
            maxYComment("月球奶酪矿"));

        moonDeshVeinSize = config.getInt("moonDeshVeinSize", category, 9, 1, 64,
            veinSizeComment("月球戴斯矿"));
        moonDeshCount = config.getInt("moonDeshCount", category, 9, 0, 100,
            veinCountComment("月球戴斯矿"));
        moonDeshMinY = config.getInt("moonDeshMinY", category, -80, -80, 320,
            minYComment("月球戴斯矿"));
        moonDeshMaxY = config.getInt("moonDeshMaxY", category, 80, -80, 320,
            maxYComment("月球戴斯矿"));

        moonIceShardVeinSize = config.getInt("moonIceShardVeinSize", category, 10, 1, 64,
            veinSizeComment("月球冰晶矿"));
        moonIceShardCount = config.getInt("moonIceShardCount", category, 8, 0, 100,
            veinCountComment("月球冰晶矿"));
        moonIceShardMinY = config.getInt("moonIceShardMinY", category, -32, -80, 320,
            minYComment("月球冰晶矿"));
        moonIceShardMaxY = config.getInt("moonIceShardMaxY", category, 32, -80, 320,
            maxYComment("月球冰晶矿"));

        moonIronVeinSize = config.getInt("moonIronVeinSize", category, 11, 1, 64,
            veinSizeComment("月球铁矿"));
        moonIronCount = config.getInt("moonIronCount", category, 10, 0, 100,
            veinCountComment("月球铁矿"));
        moonIronMinY = config.getInt("moonIronMinY", category, -24, -80, 320,
            minYComment("月球铁矿"));
        moonIronMaxY = config.getInt("moonIronMaxY", category, 56, -80, 320,
            maxYComment("月球铁矿"));

        // Mars ores
        marsDiamondVeinSize = config.getInt("marsDiamondVeinSize", category, 7, 1, 64,
            veinSizeComment("火星钻石矿"));
        marsDiamondCount = config.getInt("marsDiamondCount", category, 5, 0, 100,
            veinCountComment("火星钻石矿"));
        marsDiamondMinY = config.getInt("marsDiamondMinY", category, -80, -80, 320,
            minYComment("火星钻石矿"));
        marsDiamondMaxY = config.getInt("marsDiamondMaxY", category, 80, -80, 320,
            maxYComment("火星钻石矿"));

        marsIceShardVeinSize = config.getInt("marsIceShardVeinSize", category, 10, 1, 64,
            veinSizeComment("火星冰晶矿"));
        marsIceShardCount = config.getInt("marsIceShardCount", category, 8, 0, 100,
            veinCountComment("火星冰晶矿"));
        marsIceShardMinY = config.getInt("marsIceShardMinY", category, -32, -80, 320,
            minYComment("火星冰晶矿"));
        marsIceShardMaxY = config.getInt("marsIceShardMaxY", category, 32, -80, 320,
            maxYComment("火星冰晶矿"));

        marsIronVeinSize = config.getInt("marsIronVeinSize", category, 11, 1, 64,
            veinSizeComment("火星铁矿"));
        marsIronCount = config.getInt("marsIronCount", category, 10, 0, 100,
            veinCountComment("火星铁矿"));
        marsIronMinY = config.getInt("marsIronMinY", category, -24, -80, 320,
            minYComment("火星铁矿"));
        marsIronMaxY = config.getInt("marsIronMaxY", category, 56, -80, 320,
            maxYComment("火星铁矿"));

        marsOstrumVeinSize = config.getInt("marsOstrumVeinSize", category, 8, 1, 64,
            veinSizeComment("火星紫金矿"));
        marsOstrumCount = config.getInt("marsOstrumCount", category, 8, 0, 100,
            veinCountComment("火星紫金矿"));
        marsOstrumMinY = config.getInt("marsOstrumMinY", category, -80, -80, 320,
            minYComment("火星紫金矿"));
        marsOstrumMaxY = config.getInt("marsOstrumMaxY", category, 80, -80, 320,
            maxYComment("火星紫金矿"));

        // Mercury ores
        mercuryIronVeinSize = config.getInt("mercuryIronVeinSize", category, 8, 1, 64,
            veinSizeComment("水星铁矿"));
        mercuryIronCount = config.getInt("mercuryIronCount", category, 20, 0, 100,
            veinCountComment("水星铁矿"));
        mercuryIronMinY = config.getInt("mercuryIronMinY", category, -80, -80, 320,
            minYComment("水星铁矿"));
        mercuryIronMaxY = config.getInt("mercuryIronMaxY", category, 192, -80, 320,
            maxYComment("水星铁矿"));

        // Venus ores
        venusCaloriteVeinSize = config.getInt("venusCaloriteVeinSize", category, 8, 1, 64,
            veinSizeComment("金星耐热金属矿"));
        venusCaloriteCount = config.getInt("venusCaloriteCount", category, 8, 0, 100,
            veinCountComment("金星耐热金属矿"));
        venusCaloriteMinY = config.getInt("venusCaloriteMinY", category, -80, -80, 320,
            minYComment("金星耐热金属矿"));
        venusCaloriteMaxY = config.getInt("venusCaloriteMaxY", category, 80, -80, 320,
            maxYComment("金星耐热金属矿"));

        venusCoalVeinSize = config.getInt("venusCoalVeinSize", category, 17, 1, 64,
            veinSizeComment("金星煤矿"));
        venusCoalCount = config.getInt("venusCoalCount", category, 20, 0, 100,
            veinCountComment("金星煤矿"));
        venusCoalMinY = config.getInt("venusCoalMinY", category, -80, -80, 320,
            minYComment("金星煤矿"));
        venusCoalMaxY = config.getInt("venusCoalMaxY", category, 192, -80, 320,
            maxYComment("金星煤矿"));

        venusDiamondVeinSize = config.getInt("venusDiamondVeinSize", category, 9, 1, 64,
            veinSizeComment("金星钻石矿"));
        venusDiamondCount = config.getInt("venusDiamondCount", category, 5, 0, 100,
            veinCountComment("金星钻石矿"));
        venusDiamondMinY = config.getInt("venusDiamondMinY", category, -80, -80, 320,
            minYComment("金星钻石矿"));
        venusDiamondMaxY = config.getInt("venusDiamondMaxY", category, 80, -80, 320,
            maxYComment("金星钻石矿"));

        venusGoldVeinSize = config.getInt("venusGoldVeinSize", category, 10, 1, 64,
            veinSizeComment("金星金矿"));
        venusGoldCount = config.getInt("venusGoldCount", category, 4, 0, 100,
            veinCountComment("金星金矿"));
        venusGoldMinY = config.getInt("venusGoldMinY", category, -64, -80, 320,
            minYComment("金星金矿"));
        venusGoldMaxY = config.getInt("venusGoldMaxY", category, 32, -80, 320,
            maxYComment("金星金矿"));

        // Glacio ores
        glacioCoalVeinSize = config.getInt("glacioCoalVeinSize", category, 17, 1, 64,
            veinSizeComment("冰川星煤矿"));
        glacioCoalCount = config.getInt("glacioCoalCount", category, 20, 0, 100,
            veinCountComment("冰川星煤矿"));
        glacioCoalMinY = config.getInt("glacioCoalMinY", category, -80, -80, 320,
            minYComment("冰川星煤矿"));
        glacioCoalMaxY = config.getInt("glacioCoalMaxY", category, 192, -80, 320,
            maxYComment("冰川星煤矿"));

        glacioIceShardVeinSize = config.getInt("glacioIceShardVeinSize", category, 17, 1, 64,
            veinSizeComment("冰川星冰晶矿"));
        glacioIceShardCount = config.getInt("glacioIceShardCount", category, 8, 0, 100,
            veinCountComment("冰川星冰晶矿"));
        glacioIceShardMinY = config.getInt("glacioIceShardMinY", category, -32, -80, 320,
            minYComment("冰川星冰晶矿"));
        glacioIceShardMaxY = config.getInt("glacioIceShardMaxY", category, 32, -80, 320,
            maxYComment("冰川星冰晶矿"));

        glacioIronVeinSize = config.getInt("glacioIronVeinSize", category, 11, 1, 64,
            veinSizeComment("冰川星铁矿"));
        glacioIronCount = config.getInt("glacioIronCount", category, 10, 0, 100,
            veinCountComment("冰川星铁矿"));
        glacioIronMinY = config.getInt("glacioIronMinY", category, -24, -80, 320,
            minYComment("冰川星铁矿"));
        glacioIronMaxY = config.getInt("glacioIronMaxY", category, 56, -80, 320,
            maxYComment("冰川星铁矿"));

        glacioLapisVeinSize = config.getInt("glacioLapisVeinSize", category, 9, 1, 64,
            veinSizeComment("冰川星青金石矿"));
        glacioLapisCount = config.getInt("glacioLapisCount", category, 2, 0, 100,
            veinCountComment("冰川星青金石矿"));
        glacioLapisMinY = config.getInt("glacioLapisMinY", category, -32, -80, 320,
            minYComment("冰川星青金石矿"));
        glacioLapisMaxY = config.getInt("glacioLapisMaxY", category, 32, -80, 320,
            maxYComment("冰川星青金石矿"));
    }

    private static String veinSizeComment(String oreName) {
        return oreName + "每个矿脉包含的最大矿石数量。";
    }

    private static String veinCountComment(String oreName) {
        return oreName + "每个区块尝试生成的矿脉数量。";
    }

    private static String minYComment(String oreName) {
        return oreName + "允许生成的最低 Y 坐标。";
    }

    private static String maxYComment(String oreName) {
        return oreName + "允许生成的最高 Y 坐标。";
    }

    /**
     * Get the modified ore generation count based on the global multiplier.
     * This applies AdAstraConfig.oreGenerationMultiplier to the base count.
     * @param baseCount Base number of veins per chunk
     * @return Modified count (0 if generation is disabled)
     */
    public static int getModifiedOreCount(int baseCount) {
        return AdAstraConfig.getModifiedOreGeneration(baseCount);
    }
}
