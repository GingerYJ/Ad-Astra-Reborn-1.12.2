package earth.terrarium.adastra.common.config;

import net.minecraftforge.common.config.Configuration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public final class AdAstraConfig {

    private static Configuration configuration;

    // General Configuration
    public static boolean debugLogging;
    public static boolean disableOxygen;
    public static boolean disableTemperature;
    public static boolean disableGravity;
    public static boolean enableAirVortexes;
    public static float oxygenDamageAmount;
    public static int oxygenDamageInterval;
    public static int oxygenConsumptionInterval;
    public static int oxygenConsumptionAmount;
    public static boolean enableSpaceEnvironmentEffects;
    public static int planetRandomTickSpeed;

    // Performance Configuration
    public static int maxOxygenDistributorRadius;
    public static int oxygenScanRadius;
    public static boolean enableMachineIdleOptimization;
    public static int machineTransferInterval;
    public static int sealedRoomCacheLifetime;

    // Balance Configuration
    public static float gravityNormalizerEnergyMultiplier;
    public static int sealedRoomMaxBlocks;

    // Machine Configuration
    public static float machineProcessingSpeedMultiplier;
    public static float machineEnergyConsumptionMultiplier;
    public static float coalGeneratorEnergyMultiplier;
    public static float solarPanelEnergyMultiplier;
    public static int compressorBaseTime;
    public static int cryoFreezerBaseTime;
    public static int etrionicBlastFurnaceBaseTime;
    public static int fuelRefineryBaseTime;

    // Environment Configuration
    public static float temperatureDamageMultiplier;
    public static int freezeDamageInterval;
    public static int burnDamageInterval;
    public static float freezeDamageAmount;
    public static float burnDamageAmount;
    public static float gravityMultiplier;
    public static boolean enableFallDamageInLowGravity;
    public static float lowGravityFallDamageThreshold;

    // Dimension Configuration
    public static boolean enableMoonDimension;
    public static boolean enableMarsDimension;
    public static boolean enableMercuryDimension;
    public static boolean enableVenusDimension;
    public static boolean enableGlacioDimension;
    public static boolean enableNetherAsPlanet;
    public static int netherRocketTier;
    public static boolean blockVanillaNetherTravelWhenPlanet;
    public static boolean enableEndAsPlanet;
    public static int endRocketTier;
    public static boolean blockVanillaEndTravelWhenPlanet;
    public static float moonGravityMultiplier;
    public static float marsGravityMultiplier;
    public static float mercuryGravityMultiplier;
    public static float venusGravityMultiplier;
    public static float glacioGravityMultiplier;

    // Mob Configuration
    public static float planetMobSpawnRateMultiplier;
    public static boolean enableHostileMobsOnMoon;
    public static boolean enableHostileMobsOnMars;
    public static boolean enableHostileMobsOnMercury;
    public static boolean enableHostileMobsOnVenus;
    public static boolean enableHostileMobsOnGlacio;
    public static int planetEntityCapPerType;

    // World Generation Configuration
    public static boolean enableStructureGeneration;
    public static boolean enableLunarVillages;
    public static boolean enableMarsOutposts;
    public static float oreGenerationMultiplier;

    // Debug Configuration
    public static boolean debugOxygen;
    public static boolean debugTemperature;
    public static boolean debugGravity;

    private AdAstraConfig() {
    }

    public static void init(File file) {
        configuration = new Configuration(resolveConfigFile(file));
        sync();
    }

    private static File resolveConfigFile(File suggestedFile) {
        if (suggestedFile == null) {
            return new File("config/ad_astra/ad_astra.cfg");
        }

        String fileName = suggestedFile.getName();
        String folderName = fileName.endsWith(".cfg") ? fileName.substring(0, fileName.length() - 4) : fileName;
        File parent = suggestedFile.getParentFile();
        if (parent == null) {
            parent = new File("config");
        }

        File folder = folderName.equals(parent.getName()) ? parent : new File(parent, folderName);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        File resolvedFile = new File(folder, fileName);
        if (!resolvedFile.exists() && suggestedFile.exists() && !suggestedFile.equals(resolvedFile)) {
            try {
                Files.copy(suggestedFile.toPath(), resolvedFile.toPath(), StandardCopyOption.COPY_ATTRIBUTES);
            } catch (IOException ignored) {
                // If migration fails, Forge will generate a fresh config in the new folder.
            }
        }
        return resolvedFile;
    }

    public static void sync() {
        if (configuration == null) {
            return;
        }

        setCategoryComments();

        // General Configuration
        debugLogging = configuration.getBoolean(
            "debugLogging",
            Configuration.CATEGORY_GENERAL,
            false,
            "启用额外日志，用于排查 1.12.2 移植版中的问题。");
        disableOxygen = configuration.getBoolean(
            "disableOxygen",
            Configuration.CATEGORY_GENERAL,
            false,
            "禁用缺氧伤害。");
        disableTemperature = configuration.getBoolean(
            "disableTemperature",
            Configuration.CATEGORY_GENERAL,
            false,
            "禁用温度伤害。");
        disableGravity = configuration.getBoolean(
            "disableGravity",
            Configuration.CATEGORY_GENERAL,
            false,
            "禁用非地球维度的重力修改。");
        enableAirVortexes = configuration.getBoolean(
            "enableAirVortexes",
            Configuration.CATEGORY_GENERAL,
            true,
            "当氧气分配器覆盖区域超过上限时，是否生成空气涡流实体。");
        oxygenDamageAmount = configuration.getFloat(
            "oxygenDamageAmount",
            Configuration.CATEGORY_GENERAL,
            2.0f,
            0.0f,
            20.0f,
            "玩家缺氧时每次受到的伤害，单位为半颗心。");
        oxygenDamageInterval = configuration.getInt(
            "oxygenDamageInterval",
            Configuration.CATEGORY_GENERAL,
            20,
            1,
            200,
            "缺氧伤害触发间隔，单位为 tick。20 tick = 1 秒。");
        oxygenConsumptionInterval = configuration.getInt(
            "oxygenConsumptionInterval",
            Configuration.CATEGORY_GENERAL,
            12,
            1,
            200,
            "宇航服消耗氧气的间隔，单位为 tick。12 tick = 0.6 秒。");
        oxygenConsumptionAmount = configuration.getInt(
            "oxygenConsumptionAmount",
            Configuration.CATEGORY_GENERAL,
            1,
            1,
            1000,
            "每次从宇航服中消耗的氧气量，单位为 mB。");
        enableSpaceEnvironmentEffects = configuration.getBoolean(
            "enableSpaceEnvironmentEffects",
            Configuration.CATEGORY_GENERAL,
            true,
            "启用太空环境效果，例如无氧或极端温度维度中的水结冰/蒸发、方块衰变。");
        planetRandomTickSpeed = configuration.getInt(
            "planetRandomTickSpeed",
            Configuration.CATEGORY_GENERAL,
            4,
            0,
            64,
            "太空环境效果每 tick 在每个已加载区块中随机处理的方块数量。数值越高，冻结/蒸发越快，但 CPU 占用也越高。");

        // Performance Configuration
        maxOxygenDistributorRadius = configuration.getInt(
            "maxOxygenDistributorRadius",
            "performance",
            18,
            1,
            100,
            "氧气分配器最大工作半径，单位为方块。数值越高越可能影响性能。");
        oxygenScanRadius = configuration.getInt(
            "oxygenScanRadius",
            "performance",
            16,
            1,
            64,
            "密封房间漏氧扫描半径，单位为方块。数值越高越可能影响性能。");
        enableMachineIdleOptimization = configuration.getBoolean(
            "enableMachineIdleOptimization",
            "performance",
            true,
            "启用机器空闲优化。没有物品、能量或流体可处理的机器会降低 tick 频率。");
        machineTransferInterval = configuration.getInt(
            "machineTransferInterval",
            "performance",
            10,
            1,
            20,
            "机器向相邻方块传输物品、能量或流体的间隔，单位为 tick。数值越高 CPU 占用越低，但传输越慢。");
        sealedRoomCacheLifetime = configuration.getInt(
            "sealedRoomCacheLifetime",
            "performance",
            60,
            20,
            200,
            "密封房间计算结果缓存时间，单位为 tick。数值越高性能越好，但对房间变化的响应越慢。");

        // Balance Configuration
        gravityNormalizerEnergyMultiplier = configuration.getFloat(
            "gravityNormalizerEnergyMultiplier",
            "balance",
            1.0f,
            0.1f,
            10.0f,
            "重力调节器能量消耗倍率。数值越高，耗能越大。");
        sealedRoomMaxBlocks = configuration.getInt(
            "sealedRoomMaxBlocks",
            "balance",
            5000,
            100,
            50000,
            "密封房间允许包含的最大方块数，超过后会被视为过大。数值越高越可能影响性能。");

        // Machine Configuration
        machineProcessingSpeedMultiplier = configuration.getFloat(
            "machineProcessingSpeedMultiplier",
            "machines",
            1.0f,
            0.1f,
            10.0f,
            "所有机器的处理速度倍率。数值越高处理越快，例如 2.0 为两倍速度，0.5 为半速。");
        machineEnergyConsumptionMultiplier = configuration.getFloat(
            "machineEnergyConsumptionMultiplier",
            "machines",
            1.0f,
            0.1f,
            10.0f,
            "所有机器的能量消耗倍率。数值越高耗能越大，例如 2.0 为两倍耗能。");
        coalGeneratorEnergyMultiplier = configuration.getFloat(
            "coalGeneratorEnergyMultiplier",
            "machines",
            1.0f,
            0.1f,
            10.0f,
            "煤炭发电机发电倍率。数值越高每 tick 产生的能量越多。");
        solarPanelEnergyMultiplier = configuration.getFloat(
            "solarPanelEnergyMultiplier",
            "machines",
            1.0f,
            0.1f,
            10.0f,
            "太阳能板发电倍率。数值越高产生的能量越多。");
        compressorBaseTime = configuration.getInt(
            "compressorBaseTime",
            "machines",
            100,
            10,
            1000,
            "压缩机配方基础处理时间，单位为 tick。默认 100 tick，即 5 秒，会受到机器速度倍率影响。");
        cryoFreezerBaseTime = configuration.getInt(
            "cryoFreezerBaseTime",
            "machines",
            200,
            10,
            1000,
            "冷冻机配方基础处理时间，单位为 tick。默认 200 tick，即 10 秒，会受到机器速度倍率影响。");
        etrionicBlastFurnaceBaseTime = configuration.getInt(
            "etrionicBlastFurnaceBaseTime",
            "machines",
            200,
            10,
            1000,
            "高炉配方基础处理时间，单位为 tick。默认 200 tick，即 10 秒，会受到机器速度倍率影响。");
        fuelRefineryBaseTime = configuration.getInt(
            "fuelRefineryBaseTime",
            "machines",
            150,
            10,
            1000,
            "燃油精炼机配方基础处理时间，单位为 tick。默认 150 tick，即 7.5 秒，会受到机器速度倍率影响。");

        // Environment Configuration
        temperatureDamageMultiplier = configuration.getFloat(
            "temperatureDamageMultiplier",
            "environment",
            1.0f,
            0.0f,
            10.0f,
            "极端温度伤害倍率，包括寒冷和灼热。设为 0 可禁用温度伤害。");
        freezeDamageInterval = configuration.getInt(
            "freezeDamageInterval",
            "environment",
            40,
            1,
            200,
            "寒冷伤害触发间隔，单位为 tick。40 tick = 2 秒，数值越低伤害越频繁。");
        burnDamageInterval = configuration.getInt(
            "burnDamageInterval",
            "environment",
            40,
            1,
            200,
            "灼热伤害触发间隔，单位为 tick。40 tick = 2 秒，数值越低伤害越频繁。");
        freezeDamageAmount = configuration.getFloat(
            "freezeDamageAmount",
            "environment",
            2.0f,
            0.0f,
            20.0f,
            "寒冷时每次造成的伤害，单位为半颗心，会受到温度伤害倍率影响。");
        burnDamageAmount = configuration.getFloat(
            "burnDamageAmount",
            "environment",
            2.0f,
            0.0f,
            20.0f,
            "灼热时每次造成的伤害，单位为半颗心，会受到温度伤害倍率影响。");
        gravityMultiplier = configuration.getFloat(
            "gravityMultiplier",
            "environment",
            1.0f,
            0.0f,
            5.0f,
            "全局重力强度倍率。1.0 为正常，0.5 为一半效果，2.0 为双倍效果。");
        enableFallDamageInLowGravity = configuration.getBoolean(
            "enableFallDamageInLowGravity",
            "environment",
            true,
            "低重力维度中是否启用摔落伤害。设为 false 时，玩家在低重力行星上不会受到摔落伤害。");
        lowGravityFallDamageThreshold = configuration.getFloat(
            "lowGravityFallDamageThreshold",
            "environment",
            0.5f,
            0.0f,
            1.0f,
            "低重力判定阈值。重力低于该值的行星会被视为低重力，摔落伤害可能被降低或禁用。");

        // Dimension Configuration
        enableMoonDimension = configuration.getBoolean(
            "enableMoonDimension",
            "dimensions",
            true,
            "启用月球维度。修改后需要重启游戏。");
        enableMarsDimension = configuration.getBoolean(
            "enableMarsDimension",
            "dimensions",
            true,
            "启用火星维度。修改后需要重启游戏。");
        enableMercuryDimension = configuration.getBoolean(
            "enableMercuryDimension",
            "dimensions",
            true,
            "启用水星维度。修改后需要重启游戏。");
        enableVenusDimension = configuration.getBoolean(
            "enableVenusDimension",
            "dimensions",
            true,
            "启用金星维度。修改后需要重启游戏。");
        enableGlacioDimension = configuration.getBoolean(
            "enableGlacioDimension",
            "dimensions",
            true,
            "启用冰川星维度。修改后需要重启游戏。");
        enableNetherAsPlanet = configuration.getBoolean(
            "enableNetherAsPlanet",
            "dimensions",
            false,
            "将下界维度（-1）加入火箭行星选择界面。");
        netherRocketTier = configuration.getInt(
            "netherRocketTier",
            "dimensions",
            1,
            0,
            7,
            "当下界作为行星启用时，进入下界所需的最低火箭等级。");
        blockVanillaNetherTravelWhenPlanet = configuration.getBoolean(
            "blockVanillaNetherTravelWhenPlanet",
            "dimensions",
            true,
            "当下界作为行星启用时，是否阻止非火箭方式进入或离开下界。");
        enableEndAsPlanet = configuration.getBoolean(
            "enableEndAsPlanet",
            "dimensions",
            false,
            "将末地维度（1）加入火箭行星选择界面。");
        endRocketTier = configuration.getInt(
            "endRocketTier",
            "dimensions",
            4,
            0,
            7,
            "当末地作为行星启用时，进入末地所需的最低火箭等级。");
        blockVanillaEndTravelWhenPlanet = configuration.getBoolean(
            "blockVanillaEndTravelWhenPlanet",
            "dimensions",
            true,
            "当末地作为行星启用时，是否阻止非火箭方式进入或离开末地。");
        moonGravityMultiplier = configuration.getFloat(
            "moonGravityMultiplier",
            "dimensions",
            1.0f,
            0.0f,
            5.0f,
            "月球重力倍率。月球基础重力约为地球的 0.166 倍，此项会乘在基础重力上。");
        marsGravityMultiplier = configuration.getFloat(
            "marsGravityMultiplier",
            "dimensions",
            1.0f,
            0.0f,
            5.0f,
            "火星重力倍率。火星基础重力约为地球的 0.38 倍，此项会乘在基础重力上。");
        mercuryGravityMultiplier = configuration.getFloat(
            "mercuryGravityMultiplier",
            "dimensions",
            1.0f,
            0.0f,
            5.0f,
            "水星重力倍率。水星基础重力约为地球的 0.38 倍，此项会乘在基础重力上。");
        venusGravityMultiplier = configuration.getFloat(
            "venusGravityMultiplier",
            "dimensions",
            1.0f,
            0.0f,
            5.0f,
            "金星重力倍率。金星基础重力约为地球的 0.9 倍，此项会乘在基础重力上。");
        glacioGravityMultiplier = configuration.getFloat(
            "glacioGravityMultiplier",
            "dimensions",
            1.0f,
            0.0f,
            5.0f,
            "冰川星重力倍率。冰川星基础重力约为地球的 0.25 倍，此项会乘在基础重力上。");

        // Mob Configuration
        planetMobSpawnRateMultiplier = configuration.getFloat(
            "planetMobSpawnRateMultiplier",
            "mobs",
            1.0f,
            0.0f,
            10.0f,
            "所有行星的生物生成倍率。0 表示不生成生物，1.0 为正常，2.0 为双倍生成。");
        enableHostileMobsOnMoon = configuration.getBoolean(
            "enableHostileMobsOnMoon",
            "mobs",
            true,
            "是否允许敌对生物在月球生成。");
        enableHostileMobsOnMars = configuration.getBoolean(
            "enableHostileMobsOnMars",
            "mobs",
            true,
            "是否允许敌对生物在火星生成。");
        enableHostileMobsOnMercury = configuration.getBoolean(
            "enableHostileMobsOnMercury",
            "mobs",
            true,
            "是否允许敌对生物在水星生成。");
        enableHostileMobsOnVenus = configuration.getBoolean(
            "enableHostileMobsOnVenus",
            "mobs",
            true,
            "是否允许敌对生物在金星生成。");
        enableHostileMobsOnGlacio = configuration.getBoolean(
            "enableHostileMobsOnGlacio",
            "mobs",
            true,
            "是否允许敌对生物在冰川星生成。");
        planetEntityCapPerType = configuration.getInt(
            "planetEntityCapPerType",
            "mobs",
            10,
            1,
            1000,
            "每个 Ad Astra 行星维度中，同一种实体类允许同时加载的最大活体实体数量。");

        // World Generation Configuration
        enableStructureGeneration = configuration.getBoolean(
            "enableStructureGeneration",
            "worldgen",
            true,
            "是否在行星上生成结构。设为 false 后不会生成任何结构。修改后需要重启游戏。");
        enableLunarVillages = configuration.getBoolean(
            "enableLunarVillages",
            "worldgen",
            true,
            "是否在月球生成月球村庄结构。修改后需要重启游戏。");
        enableMarsOutposts = configuration.getBoolean(
            "enableMarsOutposts",
            "worldgen",
            true,
            "是否在火星生成火星哨站结构。修改后需要重启游戏。");
        oreGenerationMultiplier = configuration.getFloat(
            "oreGenerationMultiplier",
            "worldgen",
            1.0f,
            0.0f,
            10.0f,
            "所有矿物的生成倍率。0 表示不生成矿物，1.0 为正常，2.0 为双倍生成。修改后需要重启游戏。");

        // Debug Configuration
        debugOxygen = configuration.getBoolean(
            "debugOxygen",
            "debug",
            false,
            "启用氧气系统调试日志，用于排查氧气相关问题。");
        debugTemperature = configuration.getBoolean(
            "debugTemperature",
            "debug",
            false,
            "启用温度系统调试日志，用于排查温度相关问题。");
        debugGravity = configuration.getBoolean(
            "debugGravity",
            "debug",
            false,
            "启用重力系统调试日志，用于排查重力相关问题。");

        // Validation
        if (maxOxygenDistributorRadius < 1) maxOxygenDistributorRadius = 1;
        if (maxOxygenDistributorRadius > 100) maxOxygenDistributorRadius = 100;
        if (oxygenScanRadius < 1) oxygenScanRadius = 1;
        if (oxygenScanRadius > 64) oxygenScanRadius = 64;
        if (gravityNormalizerEnergyMultiplier < 0.1f) gravityNormalizerEnergyMultiplier = 0.1f;
        if (gravityNormalizerEnergyMultiplier > 10.0f) gravityNormalizerEnergyMultiplier = 10.0f;
        if (sealedRoomMaxBlocks < 100) sealedRoomMaxBlocks = 100;
        if (sealedRoomMaxBlocks > 50000) sealedRoomMaxBlocks = 50000;

        // Machine validation
        if (machineProcessingSpeedMultiplier < 0.1f) machineProcessingSpeedMultiplier = 0.1f;
        if (machineProcessingSpeedMultiplier > 10.0f) machineProcessingSpeedMultiplier = 10.0f;
        if (machineEnergyConsumptionMultiplier < 0.1f) machineEnergyConsumptionMultiplier = 0.1f;
        if (machineEnergyConsumptionMultiplier > 10.0f) machineEnergyConsumptionMultiplier = 10.0f;
        if (coalGeneratorEnergyMultiplier < 0.1f) coalGeneratorEnergyMultiplier = 0.1f;
        if (solarPanelEnergyMultiplier < 0.1f) solarPanelEnergyMultiplier = 0.1f;
        if (compressorBaseTime < 10) compressorBaseTime = 10;
        if (cryoFreezerBaseTime < 10) cryoFreezerBaseTime = 10;
        if (etrionicBlastFurnaceBaseTime < 10) etrionicBlastFurnaceBaseTime = 10;
        if (fuelRefineryBaseTime < 10) fuelRefineryBaseTime = 10;

        // Environment validation
        if (temperatureDamageMultiplier < 0.0f) temperatureDamageMultiplier = 0.0f;
        if (temperatureDamageMultiplier > 10.0f) temperatureDamageMultiplier = 10.0f;
        if (freezeDamageInterval < 1) freezeDamageInterval = 1;
        if (burnDamageInterval < 1) burnDamageInterval = 1;
        if (freezeDamageAmount < 0.0f) freezeDamageAmount = 0.0f;
        if (burnDamageAmount < 0.0f) burnDamageAmount = 0.0f;
        if (gravityMultiplier < 0.0f) gravityMultiplier = 0.0f;
        if (gravityMultiplier > 5.0f) gravityMultiplier = 5.0f;
        if (lowGravityFallDamageThreshold < 0.0f) lowGravityFallDamageThreshold = 0.0f;
        if (lowGravityFallDamageThreshold > 1.0f) lowGravityFallDamageThreshold = 1.0f;

        // Dimension validation
        if (moonGravityMultiplier < 0.0f) moonGravityMultiplier = 0.0f;
        if (marsGravityMultiplier < 0.0f) marsGravityMultiplier = 0.0f;
        if (mercuryGravityMultiplier < 0.0f) mercuryGravityMultiplier = 0.0f;
        if (venusGravityMultiplier < 0.0f) venusGravityMultiplier = 0.0f;
        if (glacioGravityMultiplier < 0.0f) glacioGravityMultiplier = 0.0f;
        if (netherRocketTier < 0) netherRocketTier = 0;
        if (netherRocketTier > 7) netherRocketTier = 7;
        if (endRocketTier < 0) endRocketTier = 0;
        if (endRocketTier > 7) endRocketTier = 7;

        // Mob validation
        if (planetMobSpawnRateMultiplier < 0.0f) planetMobSpawnRateMultiplier = 0.0f;
        if (planetMobSpawnRateMultiplier > 10.0f) planetMobSpawnRateMultiplier = 10.0f;
        if (planetEntityCapPerType < 1) planetEntityCapPerType = 1;

        // Worldgen validation
        if (oreGenerationMultiplier < 0.0f) oreGenerationMultiplier = 0.0f;
        if (oreGenerationMultiplier > 10.0f) oreGenerationMultiplier = 10.0f;

        // Sync ore generation configuration
        OreGenConfig.sync(configuration);

        if (configuration.hasChanged()) {
            configuration.save();
        }
    }

    private static void setCategoryComments() {
        configuration.setCategoryComment(Configuration.CATEGORY_GENERAL,
            "通用配置：控制氧气、温度、重力、太空环境效果等全局规则。");
        configuration.setCategoryComment("performance",
            "性能配置：用于控制氧气扫描、机器空闲 tick、密封房间缓存等开销较高的逻辑。");
        configuration.setCategoryComment("balance",
            "平衡配置：用于调整重力调节器耗能、密封房间大小上限等玩法数值。");
        configuration.setCategoryComment("machines",
            "机器配置：调整机器处理速度、耗能、发电倍率与基础处理时间。");
        configuration.setCategoryComment("environment",
            "环境配置：控制极端温度、低重力摔落伤害与全局重力倍率。");
        configuration.setCategoryComment("dimensions",
            "维度配置：控制行星维度启用状态、下界/末地火箭旅行规则与各行星重力倍率。");
        configuration.setCategoryComment("mobs",
            "生物配置：控制行星生物生成倍率、敌对生物生成开关与每类实体数量上限。");
        configuration.setCategoryComment("worldgen",
            "世界生成配置：控制行星结构、矿物生成倍率以及各行星矿脉参数。");
        configuration.setCategoryComment("debug",
            "调试配置：开启对应系统的额外日志，便于排查问题。");
    }

    /**
     * Helper Methods for Config Integration
     */

    /**
     * Get modified processing time for machines based on config multiplier.
     * @param baseTime Base processing time in ticks
     * @return Modified processing time
     */
    public static int getModifiedProcessingTime(int baseTime) {
        return Math.max(1, (int) (baseTime / machineProcessingSpeedMultiplier));
    }

    /**
     * Get modified energy consumption for machines based on config multiplier.
     * @param baseEnergy Base energy consumption per tick
     * @return Modified energy consumption
     */
    public static int getModifiedEnergyConsumption(int baseEnergy) {
        return Math.max(1, (int) (baseEnergy * machineEnergyConsumptionMultiplier));
    }

    /**
     * Get modified energy generation for power generators.
     * @param baseEnergy Base energy generation per tick
     * @param generatorType Type of generator ("coal" or "solar")
     * @return Modified energy generation
     */
    public static int getModifiedEnergyGeneration(int baseEnergy, String generatorType) {
        float multiplier = 1.0f;
        if ("coal".equalsIgnoreCase(generatorType)) {
            multiplier = coalGeneratorEnergyMultiplier;
        } else if ("solar".equalsIgnoreCase(generatorType)) {
            multiplier = solarPanelEnergyMultiplier;
        }
        return Math.max(1, (int) (baseEnergy * multiplier));
    }

    /**
     * Get modified temperature damage amount.
     * @param baseDamage Base damage amount
     * @return Modified damage amount
     */
    public static float getModifiedTemperatureDamage(float baseDamage) {
        return baseDamage * temperatureDamageMultiplier;
    }

    /**
     * Get gravity multiplier for a specific dimension ID.
     * @param dimensionId The dimension ID
     * @return Gravity multiplier for that dimension
     */
    public static float getGravityMultiplierForDimension(int dimensionId) {
        float baseMultiplier = gravityMultiplier;
        if (dimensionId == 1201) { // Moon
            return baseMultiplier * moonGravityMultiplier;
        } else if (dimensionId == 1202) { // Mars
            return baseMultiplier * marsGravityMultiplier;
        } else if (dimensionId == 1203) { // Mercury
            return baseMultiplier * mercuryGravityMultiplier;
        } else if (dimensionId == 1204) { // Venus
            return baseMultiplier * venusGravityMultiplier;
        } else if (dimensionId == 1205) { // Glacio
            return baseMultiplier * glacioGravityMultiplier;
        }
        return baseMultiplier;
    }

    /**
     * Check if hostile mobs should spawn in a dimension.
     * @param dimensionId The dimension ID
     * @return True if hostile mobs can spawn
     */
    public static boolean canHostileMobsSpawn(int dimensionId) {
        if (planetMobSpawnRateMultiplier <= 0.0f) {
            return false;
        }
        if (dimensionId == 1201) { // Moon
            return enableHostileMobsOnMoon;
        } else if (dimensionId == 1202) { // Mars
            return enableHostileMobsOnMars;
        } else if (dimensionId == 1203) { // Mercury
            return enableHostileMobsOnMercury;
        } else if (dimensionId == 1204) { // Venus
            return enableHostileMobsOnVenus;
        } else if (dimensionId == 1205) { // Glacio
            return enableHostileMobsOnGlacio;
        }
        return true;
    }

    /**
     * Check if the dimension is one of Ad Astra's planet dimensions.
     * @param dimensionId The dimension ID
     * @return True for Moon, Mars, Mercury, Venus or Glacio
     */
    public static boolean isPlanetDimension(int dimensionId) {
        return dimensionId == 1201
            || dimensionId == 1202
            || dimensionId == 1203
            || dimensionId == 1204
            || dimensionId == 1205;
    }

    /**
     * Check if a dimension is enabled.
     * @param dimensionId The dimension ID
     * @return True if the dimension is enabled
     */
    public static boolean isDimensionEnabled(int dimensionId) {
        if (dimensionId == 1201) { // Moon
            return enableMoonDimension;
        } else if (dimensionId == 1202) { // Mars
            return enableMarsDimension;
        } else if (dimensionId == 1203) { // Mercury
            return enableMercuryDimension;
        } else if (dimensionId == 1204) { // Venus
            return enableVenusDimension;
        } else if (dimensionId == 1205) { // Glacio
            return enableGlacioDimension;
        }
        return true;
    }

    public static boolean isNetherPlanetEnabled() {
        return enableNetherAsPlanet;
    }

    public static boolean isEndPlanetEnabled() {
        return enableEndAsPlanet;
    }

    public static boolean shouldBlockVanillaTravelForDimension(int dimensionId) {
        if (dimensionId == -1) {
            return enableNetherAsPlanet && blockVanillaNetherTravelWhenPlanet;
        }
        if (dimensionId == 1) {
            return enableEndAsPlanet && blockVanillaEndTravelWhenPlanet;
        }
        return false;
    }

    /**
     * Get modified ore generation count.
     * @param baseCount Base number of veins per chunk
     * @return Modified count
     */
    public static int getModifiedOreGeneration(int baseCount) {
        return Math.max(0, (int) (baseCount * oreGenerationMultiplier));
    }
}
