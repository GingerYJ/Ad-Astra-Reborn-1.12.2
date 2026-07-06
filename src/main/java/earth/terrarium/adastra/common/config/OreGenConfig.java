package earth.terrarium.adastra.common.config;

import net.minecraftforge.common.config.Configuration;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 矿脉生成配置。
 *
 * <p>全局开关与倍率仍保留在 worldgen 分类中；每个行星的矿脉参数拆到独立分类，
 * 例如 worldgen_moon、worldgen_ceres，避免所有矿脉配置混在同一个 worldgen 分类里。</p>
 */
public final class OreGenConfig {

    private static final int MIN_Y_LIMIT = -80;
    private static final int MAX_Y_LIMIT = 320;
    private static final Map<String, OreSettings> ORE_SETTINGS = new HashMap<>();
    private static final Map<String, java.util.List<CustomBlockSettings>> CUSTOM_BLOCK_SETTINGS = new HashMap<>();

    private static final String CUSTOM_BLOCK_CATEGORY = "worldgen_custom_blocks";
    private static final String[] DEFAULT_CUSTOM_BLOCK_GENERATORS = new String[] {
        "# 格式：星球|生成方块[@meta]|矿脉大小|每区块尝试次数|最小Y|最大Y|可替换方块列表",
        "# 可替换方块列表填写 default 时，会自动替换该星球的表层/填充方块；也可以写 ad_astra:moon_stone,ad_astra:moon_deepslate。",
        "# 示例：moon|minecraft:chest|1|1|45|70|ad_astra:moon_stone,ad_astra:moon_deepslate",
        "# 示例：mars|minecraft:gold_ore|8|4|8|60|default",
        "# 示例：ceres|minecraft:diamond_ore|5|2|5|35|ad_astra:ceres_blocks@0,ad_astra:ceres_blocks@1"
    };

    // Moon（月球）
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

    // Mars（火星）
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

    // Mercury（水星）
    public static int mercuryIronVeinSize;
    public static int mercuryIronCount;
    public static int mercuryIronMinY;
    public static int mercuryIronMaxY;

    // Venus（金星）
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

    // Glacio（冰川星）
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
        sync(config, null);
    }

    public static void sync(Configuration config, Configuration legacyConfig) {
        ORE_SETTINGS.clear();
        CUSTOM_BLOCK_SETTINGS.clear();

        debugWorldgen = getBoolean(config, legacyConfig,
            "debugWorldgen",
            "worldgen",
            false,
            "启用矿物生成调试日志。"
        );

        config.setCategoryComment("worldgen_moon", "月球矿脉配置：只控制月球矿物的矿脉大小、生成次数和 Y 坐标范围。全局倍率仍由 worldgen.oreGenerationMultiplier 控制。");
        OreSettings moonCheese = registerOre(config, legacyConfig, "moon", "cheese", "月球奶酪矿", 8, 9, 6, 192);
        moonCheeseVeinSize = moonCheese.veinSize;
        moonCheeseCount = moonCheese.countPerChunk;
        moonCheeseMinY = moonCheese.minY;
        moonCheeseMaxY = moonCheese.maxY;
        OreSettings moonDesh = registerOre(config, legacyConfig, "moon", "desh", "月球戴斯矿", 9, 9, -80, 80);
        moonDeshVeinSize = moonDesh.veinSize;
        moonDeshCount = moonDesh.countPerChunk;
        moonDeshMinY = moonDesh.minY;
        moonDeshMaxY = moonDesh.maxY;
        OreSettings moonIceShard = registerOre(config, legacyConfig, "moon", "iceShard", "月球冰晶矿", 10, 8, -32, 32);
        moonIceShardVeinSize = moonIceShard.veinSize;
        moonIceShardCount = moonIceShard.countPerChunk;
        moonIceShardMinY = moonIceShard.minY;
        moonIceShardMaxY = moonIceShard.maxY;
        OreSettings moonIron = registerOre(config, legacyConfig, "moon", "iron", "月球铁矿", 11, 10, -24, 56);
        moonIronVeinSize = moonIron.veinSize;
        moonIronCount = moonIron.countPerChunk;
        moonIronMinY = moonIron.minY;
        moonIronMaxY = moonIron.maxY;

        config.setCategoryComment("worldgen_mars", "火星矿脉配置：只控制火星矿物的矿脉大小、生成次数和 Y 坐标范围。全局倍率仍由 worldgen.oreGenerationMultiplier 控制。");
        OreSettings marsDiamond = registerOre(config, legacyConfig, "mars", "diamond", "火星钻石矿", 7, 5, -80, 80);
        marsDiamondVeinSize = marsDiamond.veinSize;
        marsDiamondCount = marsDiamond.countPerChunk;
        marsDiamondMinY = marsDiamond.minY;
        marsDiamondMaxY = marsDiamond.maxY;
        OreSettings marsIceShard = registerOre(config, legacyConfig, "mars", "iceShard", "火星冰晶矿", 10, 8, -32, 32);
        marsIceShardVeinSize = marsIceShard.veinSize;
        marsIceShardCount = marsIceShard.countPerChunk;
        marsIceShardMinY = marsIceShard.minY;
        marsIceShardMaxY = marsIceShard.maxY;
        OreSettings marsIron = registerOre(config, legacyConfig, "mars", "iron", "火星铁矿", 11, 10, -24, 56);
        marsIronVeinSize = marsIron.veinSize;
        marsIronCount = marsIron.countPerChunk;
        marsIronMinY = marsIron.minY;
        marsIronMaxY = marsIron.maxY;
        OreSettings marsOstrum = registerOre(config, legacyConfig, "mars", "ostrum", "火星紫金矿", 8, 8, -80, 80);
        marsOstrumVeinSize = marsOstrum.veinSize;
        marsOstrumCount = marsOstrum.countPerChunk;
        marsOstrumMinY = marsOstrum.minY;
        marsOstrumMaxY = marsOstrum.maxY;

        config.setCategoryComment("worldgen_mercury", "水星矿脉配置：只控制水星矿物的矿脉大小、生成次数和 Y 坐标范围。全局倍率仍由 worldgen.oreGenerationMultiplier 控制。");
        OreSettings mercuryIron = registerOre(config, legacyConfig, "mercury", "iron", "水星铁矿", 8, 20, -80, 192);
        mercuryIronVeinSize = mercuryIron.veinSize;
        mercuryIronCount = mercuryIron.countPerChunk;
        mercuryIronMinY = mercuryIron.minY;
        mercuryIronMaxY = mercuryIron.maxY;

        config.setCategoryComment("worldgen_venus", "金星矿脉配置：只控制金星矿物的矿脉大小、生成次数和 Y 坐标范围。全局倍率仍由 worldgen.oreGenerationMultiplier 控制。");
        OreSettings venusCalorite = registerOre(config, legacyConfig, "venus", "calorite", "金星耐热金属矿", 8, 8, -80, 80);
        venusCaloriteVeinSize = venusCalorite.veinSize;
        venusCaloriteCount = venusCalorite.countPerChunk;
        venusCaloriteMinY = venusCalorite.minY;
        venusCaloriteMaxY = venusCalorite.maxY;
        OreSettings venusCoal = registerOre(config, legacyConfig, "venus", "coal", "金星煤矿", 17, 20, -80, 192);
        venusCoalVeinSize = venusCoal.veinSize;
        venusCoalCount = venusCoal.countPerChunk;
        venusCoalMinY = venusCoal.minY;
        venusCoalMaxY = venusCoal.maxY;
        OreSettings venusDiamond = registerOre(config, legacyConfig, "venus", "diamond", "金星钻石矿", 9, 5, -80, 80);
        venusDiamondVeinSize = venusDiamond.veinSize;
        venusDiamondCount = venusDiamond.countPerChunk;
        venusDiamondMinY = venusDiamond.minY;
        venusDiamondMaxY = venusDiamond.maxY;
        OreSettings venusGold = registerOre(config, legacyConfig, "venus", "gold", "金星金矿", 10, 4, -64, 32);
        venusGoldVeinSize = venusGold.veinSize;
        venusGoldCount = venusGold.countPerChunk;
        venusGoldMinY = venusGold.minY;
        venusGoldMaxY = venusGold.maxY;

        config.setCategoryComment("worldgen_glacio", "冰川星矿脉配置：只控制冰川星矿物的矿脉大小、生成次数和 Y 坐标范围。全局倍率仍由 worldgen.oreGenerationMultiplier 控制。");
        OreSettings glacioCoal = registerOre(config, legacyConfig, "glacio", "coal", "冰川星煤矿", 17, 20, -80, 192);
        glacioCoalVeinSize = glacioCoal.veinSize;
        glacioCoalCount = glacioCoal.countPerChunk;
        glacioCoalMinY = glacioCoal.minY;
        glacioCoalMaxY = glacioCoal.maxY;
        OreSettings glacioIceShard = registerOre(config, legacyConfig, "glacio", "iceShard", "冰川星冰晶矿", 17, 8, -32, 32);
        glacioIceShardVeinSize = glacioIceShard.veinSize;
        glacioIceShardCount = glacioIceShard.countPerChunk;
        glacioIceShardMinY = glacioIceShard.minY;
        glacioIceShardMaxY = glacioIceShard.maxY;
        OreSettings glacioIron = registerOre(config, legacyConfig, "glacio", "iron", "冰川星铁矿", 11, 10, -24, 56);
        glacioIronVeinSize = glacioIron.veinSize;
        glacioIronCount = glacioIron.countPerChunk;
        glacioIronMinY = glacioIron.minY;
        glacioIronMaxY = glacioIron.maxY;
        OreSettings glacioLapis = registerOre(config, legacyConfig, "glacio", "lapis", "冰川星青金石矿", 9, 2, -32, 32);
        glacioLapisVeinSize = glacioLapis.veinSize;
        glacioLapisCount = glacioLapis.countPerChunk;
        glacioLapisMinY = glacioLapis.minY;
        glacioLapisMaxY = glacioLapis.maxY;

        registerNewPlanetOres(config, legacyConfig);
        registerPlanetaryExclusiveResourceOres(config, legacyConfig);
        registerCustomBlockGenerators(config, legacyConfig);
        removeLegacyWorldgenOreEntries(config);
    }

    private static void registerNewPlanetOres(Configuration config, Configuration legacyConfig) {
        setPlanetComment(config, "ceres", "谷神星");
        registerOre(config, legacyConfig, "ceres", "dolomite", "谷神星白云石矿", 10, 16, 8, 72);
        registerOre(config, legacyConfig, "ceres", "meteoricIron", "谷神星陨铁矿", 8, 12, 4, 56);

        setPlanetComment(config, "pluto", "冥王星");
        registerOre(config, legacyConfig, "pluto", "iron", "冥王星铁矿", 9, 14, 4, 80);
        registerOre(config, legacyConfig, "pluto", "sulfur", "冥王星硫磺矿", 8, 12, 8, 72);
        registerOre(config, legacyConfig, "pluto", "uranium", "冥王星铀矿", 6, 7, 4, 36);

        setPlanetComment(config, "haumea", "妊神星");
        registerOre(config, legacyConfig, "haumea", "dolomite", "妊神星白云石矿", 9, 14, 8, 80);

        setPlanetComment(config, "io", "伊奥 / 木卫一");
        registerOre(config, legacyConfig, "io", "sulfur", "伊奥硫磺矿", 10, 18, 8, 96);
        registerOre(config, legacyConfig, "io", "volcanic", "伊奥火山矿", 8, 14, 4, 72);

        setPlanetComment(config, "europa", "欧罗巴 / 木卫二");
        registerOre(config, legacyConfig, "europa", "silicon", "欧罗巴硅矿", 9, 14, 8, 72);
        registerOre(config, legacyConfig, "europa", "iron", "欧罗巴铁矿", 10, 16, 4, 80);

        setPlanetComment(config, "ganymede", "盖尼米德 / 木卫三");
        registerOre(config, legacyConfig, "ganymede", "magnesium", "盖尼米德镁矿", 9, 16, 6, 80);
        registerOre(config, legacyConfig, "ganymede", "titanium", "盖尼米德钛铁矿", 7, 10, 4, 56);

        setPlanetComment(config, "enceladus", "恩克拉多斯 / 土卫二");
        registerOre(config, legacyConfig, "enceladus", "coal", "恩克拉多斯煤矿", 12, 18, 8, 96);

        setPlanetComment(config, "titan", "泰坦 / 土卫六");
        registerOre(config, legacyConfig, "titan", "sapphire", "泰坦蓝宝石矿", 6, 7, 4, 36);
        registerOre(config, legacyConfig, "titan", "emerald", "泰坦绿宝石矿", 4, 5, 4, 32);
        registerOre(config, legacyConfig, "titan", "diamond", "泰坦钻石矿", 5, 5, 4, 28);
        registerOre(config, legacyConfig, "titan", "coal", "泰坦煤矿", 13, 18, 16, 96);
        registerOre(config, legacyConfig, "titan", "lapis", "泰坦青金石矿", 7, 8, 4, 40);
        registerOre(config, legacyConfig, "titan", "redstone", "泰坦红石矿", 8, 10, 4, 48);

        setPlanetComment(config, "miranda", "米兰达 / 天卫五");
        registerOre(config, legacyConfig, "miranda", "iron", "米兰达铁矿", 10, 16, 4, 80);
        registerOre(config, legacyConfig, "miranda", "dolomite", "米兰达白云石矿", 9, 13, 8, 72);
        registerOre(config, legacyConfig, "miranda", "diamond", "米兰达钻石矿", 5, 5, 4, 28);
        registerOre(config, legacyConfig, "miranda", "quartz", "米兰达石英矿", 8, 10, 4, 52);
        registerOre(config, legacyConfig, "miranda", "cobalt", "米兰达钴矿", 7, 8, 4, 48);
        registerOre(config, legacyConfig, "miranda", "nickel", "米兰达镍矿", 7, 8, 4, 48);

        setPlanetComment(config, "phobos", "火卫一");
        registerOre(config, legacyConfig, "phobos", "iron", "火卫一铁矿", 10, 18, 4, 80);
        registerOre(config, legacyConfig, "phobos", "meteoricIron", "火卫一陨铁矿", 8, 12, 4, 56);
        registerOre(config, legacyConfig, "phobos", "nickel", "火卫一镍矿", 7, 9, 4, 48);
        registerOre(config, legacyConfig, "phobos", "desh", "火卫一戴斯矿", 7, 8, 4, 44);

        setPlanetComment(config, "barnarda_c", "巴纳德 C");
        registerOre(config, legacyConfig, "barnarda_c", "iron", "巴纳德 C铁矿", 10, 16, 4, 80);
        registerOre(config, legacyConfig, "barnarda_c", "gold", "巴纳德 C金矿", 8, 10, 4, 48);
        registerOre(config, legacyConfig, "barnarda_c", "coal", "巴纳德 C煤矿", 12, 18, 12, 96);

        setPlanetComment(config, "barnarda_c1", "巴纳德 C1");
        registerOre(config, legacyConfig, "barnarda_c1", "iron", "巴纳德 C1铁矿", 10, 16, 4, 80);

        setPlanetComment(config, "tauceti_f", "天仓五 F");
        registerOre(config, legacyConfig, "tauceti_f", "iron", "天仓五 F铁矿", 10, 16, 4, 80);
        registerOre(config, legacyConfig, "tauceti_f", "coal", "天仓五 F煤矿", 12, 18, 12, 96);
        registerOre(config, legacyConfig, "tauceti_f", "gold", "天仓五 F金矿", 8, 10, 4, 48);
        registerOre(config, legacyConfig, "tauceti_f", "diamond", "天仓五 F钻石矿", 5, 5, 4, 28);
        registerOre(config, legacyConfig, "tauceti_f", "lapis", "天仓五 F青金石矿", 7, 8, 4, 40);
    }


    private static void registerPlanetaryExclusiveResourceOres(Configuration config, Configuration legacyConfig) {
        registerExclusiveResourceOre(config, legacyConfig, "mercury", "hermium", "水星赫尔姆金属矿", 7, 8, 4, 56);
        registerExclusiveResourceOre(config, legacyConfig, "glacio", "cryonite", "霜原星冰凝金属矿", 7, 8, 4, 56);
        registerExclusiveResourceOre(config, legacyConfig, "ceres", "cerium", "谷神星谷神金属矿", 7, 8, 4, 56);
        registerExclusiveResourceOre(config, legacyConfig, "pluto", "plutonium", "冥王星冥晶金属矿", 7, 8, 4, 56);
        registerExclusiveResourceOre(config, legacyConfig, "haumea", "haumeite", "妊神星妊神石矿", 7, 8, 4, 56);
        registerExclusiveResourceOre(config, legacyConfig, "kuiper_belt", "kuiperite", "柯伊伯带柯伊伯石矿", 7, 8, 4, 56);
        registerExclusiveResourceOre(config, legacyConfig, "io", "ionite", "木卫一木卫一辉石矿", 7, 8, 4, 56);
        registerExclusiveResourceOre(config, legacyConfig, "europa", "europium", "木卫二欧罗巴金属矿", 7, 8, 4, 56);
        registerExclusiveResourceOre(config, legacyConfig, "ganymede", "ganymedite", "木卫三盖尼米德金属矿", 7, 8, 4, 56);
        registerExclusiveResourceOre(config, legacyConfig, "callisto", "callistite", "木卫四卡利斯托石矿", 7, 8, 4, 56);
        registerExclusiveResourceOre(config, legacyConfig, "enceladus", "enceladite", "土卫二恩克拉多斯晶金矿", 7, 8, 4, 56);
        registerExclusiveResourceOre(config, legacyConfig, "titan", "titanite", "土卫六泰坦金属矿", 7, 8, 4, 56);
        registerExclusiveResourceOre(config, legacyConfig, "miranda", "mirandium", "米兰达米兰达金属矿", 7, 8, 4, 56);
        registerExclusiveResourceOre(config, legacyConfig, "triton", "tritonium", "海卫一海卫一金属矿", 7, 8, 4, 56);
        registerExclusiveResourceOre(config, legacyConfig, "phobos", "phobium", "火卫一火卫金属矿", 7, 8, 4, 56);
        registerExclusiveResourceOre(config, legacyConfig, "barnarda_c", "barnardium", "巴纳德 C巴纳德金属矿", 7, 8, 4, 56);
        registerExclusiveResourceOre(config, legacyConfig, "barnarda_c1", "c1_barnardium", "巴纳德 C1C1 巴纳德金属矿", 7, 8, 4, 56);
        registerExclusiveResourceOre(config, legacyConfig, "tauceti_f", "taucetite", "天仓五 F天仓五金属矿", 7, 8, 4, 56);
        registerExclusiveResourceOre(config, legacyConfig, "proxima_b", "proximite", "比邻星 b比邻金属矿", 7, 8, 4, 56);
    }

    private static void registerExclusiveResourceOre(Configuration config, Configuration legacyConfig, String planet, String resource, String displayName,
                                                     int veinSize, int count, int minY, int maxY) {
        registerOre(config, legacyConfig, planet, resource, displayName, veinSize, count, minY, maxY);
    }

    private static void registerCustomBlockGenerators(Configuration config, Configuration legacyConfig) {
        config.setCategoryComment(CUSTOM_BLOCK_CATEGORY,
            "自定义星球方块生成：可以为指定星球额外添加矿物方块、装饰方块或带 TileEntity 的方块（如箱子）。\n"
                + "每行格式：星球|生成方块[@meta]|矿脉大小|每区块尝试次数|最小Y|最大Y|可替换方块列表。\n"
                + "星球名使用维度注册名，如 moon、mars、ceres、phobos、mineral_world。\n"
                + "方块格式示例：minecraft:chest、minecraft:gold_ore、ad_astra:ceres_blocks@3。\n"
                + "可替换方块列表可写 default，表示自动使用该星球地表/填充方块；也可以逗号分隔多个方块，例如 ad_astra:moon_stone,ad_astra:moon_deepslate。\n"
                + "修改后需要重新生成区块或前往新区块才会看到变化。"
        );
        String[] rows = getStringList(config, legacyConfig,
            "customPlanetBlockGenerators",
            CUSTOM_BLOCK_CATEGORY,
            DEFAULT_CUSTOM_BLOCK_GENERATORS,
            "为指定星球追加自定义方块/矿脉生成。以 # 开头的行会被忽略。示例：moon|minecraft:chest|1|1|45|70|ad_astra:moon_stone,ad_astra:moon_deepslate"
        );
        for (String row : rows) {
            CustomBlockSettings settings = parseCustomBlockSettings(row);
            if (settings != null) {
                java.util.List<CustomBlockSettings> list = CUSTOM_BLOCK_SETTINGS.get(settings.planetName);
                if (list == null) {
                    list = new java.util.ArrayList<>();
                    CUSTOM_BLOCK_SETTINGS.put(settings.planetName, list);
                }
                list.add(settings);
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

    public static java.util.List<CustomBlockSettings> getCustomBlockSettings(String planetName) {
        java.util.List<CustomBlockSettings> settings = CUSTOM_BLOCK_SETTINGS.get(planetName);
        return settings == null ? Collections.emptyList() : Collections.unmodifiableList(settings);
    }

    private static boolean legacyHas(Configuration legacyConfig, String category, String key) {
        return legacyConfig != null && legacyConfig.hasKey(category, key);
    }

    private static boolean getBoolean(Configuration config, Configuration legacyConfig,
                                      String key, String category, boolean defaultValue, String comment) {
        boolean value = defaultValue;
        if (config != null && !config.hasKey(category, key) && legacyHas(legacyConfig, category, key)) {
            value = legacyConfig.getBoolean(key, category, defaultValue, comment);
        }
        return config.getBoolean(key, category, value, comment);
    }

    private static int getInt(Configuration config, Configuration legacyConfig,
                              String key, String category, int defaultValue, int minValue, int maxValue, String comment) {
        int value = defaultValue;
        if (config != null && !config.hasKey(category, key) && legacyHas(legacyConfig, category, key)) {
            value = legacyConfig.getInt(key, category, defaultValue, minValue, maxValue, comment);
        }
        return config.getInt(key, category, value, minValue, maxValue, comment);
    }

    private static String[] getStringList(Configuration config, Configuration legacyConfig,
                                          String key, String category, String[] defaultValue, String comment) {
        String[] value = defaultValue;
        if (config != null && !config.hasKey(category, key) && legacyHas(legacyConfig, category, key)) {
            value = legacyConfig.getStringList(key, category, defaultValue, comment);
        }
        return config.getStringList(key, category, value, comment);
    }

    private static void setPlanetComment(Configuration config, String planetName, String displayName) {
        config.setCategoryComment(category(planetName), displayName + "矿脉配置：只控制该行星矿物的矿脉大小、生成次数和 Y 坐标范围。全局倍率仍由 worldgen.oreGenerationMultiplier 控制。");
    }

    private static OreSettings registerOre(Configuration config, Configuration legacyConfig, String planetName, String oreKey, String oreName,
                                           int defaultVeinSize, int defaultCountPerChunk, int defaultMinY, int defaultMaxY) {
        String category = category(planetName);
        int veinSize = getInt(config, legacyConfig,oreKey + "VeinSize", category, defaultVeinSize, 1, 64, veinSizeComment(oreName));
        int countPerChunk = getInt(config, legacyConfig,oreKey + "Count", category, defaultCountPerChunk, 0, 100, veinCountComment(oreName));
        int minY = getInt(config, legacyConfig,oreKey + "MinY", category, defaultMinY, MIN_Y_LIMIT, MAX_Y_LIMIT, minYComment(oreName));
        int maxY = getInt(config, legacyConfig,oreKey + "MaxY", category, defaultMaxY, MIN_Y_LIMIT, MAX_Y_LIMIT, maxYComment(oreName));
        OreSettings settings = new OreSettings(veinSize, countPerChunk, minY, maxY);
        ORE_SETTINGS.put(key(planetName, oreKey), settings);
        return settings;
    }

    public static OreSettings getOreSettings(String planetName, String oreKey) {
        OreSettings settings = ORE_SETTINGS.get(key(planetName, oreKey));
        return settings == null ? OreSettings.DISABLED : settings;
    }

    public static Map<String, OreSettings> getOreSettingsView() {
        return Collections.unmodifiableMap(ORE_SETTINGS);
    }

    private static String category(String planetName) {
        return "worldgen_" + planetName;
    }

    private static String key(String planetName, String oreKey) {
        return planetName + "." + oreKey;
    }

    private static String veinSizeComment(String oreName) {
        return oreName + "每个矿脉包含的最大矿石数量。";
    }

    private static String veinCountComment(String oreName) {
        return oreName + "每个区块尝试生成的矿脉数量。会继续受到 worldgen.oreGenerationMultiplier 全局倍率影响。";
    }

    private static String minYComment(String oreName) {
        return oreName + "允许生成的最低 Y 坐标。";
    }

    private static String maxYComment(String oreName) {
        return oreName + "允许生成的最高 Y 坐标。";
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

    /**
     * Get the modified ore generation count based on the global multiplier.
     * This applies AdAstraConfig.oreGenerationMultiplier to the base count.
     *
     * @param baseCount Base number of veins per chunk
     * @return Modified count (0 if generation is disabled)
     */
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