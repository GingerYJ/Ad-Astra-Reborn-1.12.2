package earth.terrarium.adastra.common.config;

import earth.terrarium.adastra.common.registry.ModDimensions;
import earth.terrarium.adastra.common.rocket.ConfigurableRocketRegistry;
import earth.terrarium.adastra.common.util.PlanetTierOverrideRegistry;
import earth.terrarium.adastra.common.world.custom.CustomPlanetDefinition;
import earth.terrarium.adastra.common.world.custom.CustomPlanetRegistry;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class AdAstraConfig {

    private static final String CATEGORY_PLANET_TIERS = "planet_tiers";
    private static final int MAX_PLANET_ROCKET_TIER = 15;
    private static final String GENERIC_COMMENT =
        "\u914d\u7f6e\u9879\u3002\u4fee\u6539\u540e\u53ef\u80fd\u9700\u8981\u91cd\u542f\u6e38\u620f/\u670d\u52a1\u5668\u3002\n"
            + "Configuration option. A game or server restart may be required after changes.";
    private static final String GENERAL_COMMENT =
        "\u5e38\u89c4\u6e38\u620f\u4e0e\u73af\u5883\u8bbe\u7f6e\u3002\u4fee\u6539\u540e\u53ef\u80fd\u9700\u8981\u91cd\u542f\u6e38\u620f\u6216\u670d\u52a1\u5668\u3002\n"
            + "General gameplay and environment settings. A game or server restart may be required after changes.";
    private static final String PERFORMANCE_COMMENT =
        "\u6027\u80fd\u4e0e\u7f13\u5b58\u8bbe\u7f6e\u3002\u90e8\u5206\u65e7\u914d\u7f6e\u9879\u4ec5\u4e3a\u517c\u5bb9\u4fdd\u7559\uff0c\u8c03\u6574\u6709\u6548\u914d\u7f6e\u540e\u53ef\u80fd\u6539\u53d8\u670d\u52a1\u5668\u8d1f\u8f7d\u3002\n"
            + "Performance and cache settings. Some legacy options are retained for compatibility; changing active options may affect server load.";
    private static final String CLIENT_COMMENT =
        "\u5ba2\u6237\u7aef\u754c\u9762\u4e0e\u663e\u793a\u8bbe\u7f6e\u3002\u4ec5\u5f71\u54cd\u5ba2\u6237\u7aef\u3002\n"
            + "Client interface and display settings. These options affect the client only.";
    private static final String BALANCE_COMMENT =
        "\u6e38\u620f\u5e73\u8861\u8bbe\u7f6e\u3002\u8c03\u6574\u540e\u53ef\u80fd\u6539\u53d8\u673a\u5668\u548c\u73af\u5883\u7684\u6d88\u8017\u3002\n"
            + "Gameplay balance settings. Changes may alter machine and environment costs.";
    private static final String MACHINES_COMMENT =
        "\u673a\u5668\u5904\u7406\u3001\u80fd\u91cf\u3001\u6d41\u4f53\u548c\u7ba1\u9053\u8bbe\u7f6e\u3002\n"
            + "Machine processing, energy, fluid, and pipe settings.";
    private static final String ENVIRONMENT_COMMENT =
        "\u6e29\u5ea6\u3001\u91cd\u529b\u3001\u6c27\u6c14\u4e0e\u4f24\u5bb3\u8bbe\u7f6e\u3002\n"
            + "Temperature, gravity, oxygen, and damage settings.";
    private static final String DIMENSIONS_COMMENT =
        "\u884c\u661f\u7ef4\u5ea6\u3001\u706b\u7bad\u53d1\u5c04\u9650\u5236\u3001\u517c\u5bb9\u6027\u7ef4\u5ea6\u6620\u5c04\u4e0e\u5b58\u6863\u76ee\u5f55\u8bbe\u7f6e\u3002launchAnywhere=true \u53ea\u8ba9\u706b\u7bad\u5728\u4efb\u610f\u7ef4\u5ea6\u901a\u8fc7\u7ef4\u5ea6\u68c0\u67e5\uff0c\u4ecd\u7136\u9700\u8981\u53d1\u5c04\u53f0\u3001\u71c3\u6599\u7b49\u5176\u4ed6\u53d1\u5c04\u6761\u4ef6\uff1b\u5185\u7f6e\u7ef4\u5ea6\u59cb\u7ec8\u6ce8\u518c\u3002enable*Dimension \u5f00\u5173\u4ec5\u4f5c\u4e3a\u517c\u5bb9\u6027/API \u67e5\u8be2\u503c\uff0c\u4e0d\u4f1a\u7981\u7528\u5185\u7f6e\u7ef4\u5ea6\uff1b\u884c\u661f\u91cd\u529b\u500d\u6570\u4ec5\u4f9b getGravityMultiplierForDimension API \u4f7f\u7528\uff0c\u4e0d\u4f1a\u6539\u53d8\u5f53\u524d\u5185\u7f6e\u4e16\u754c\u7684\u5b9e\u9645\u91cd\u529b\u3002\u81ea\u5b9a\u4e49\u7ef4\u5ea6\u5f00\u5173\u4e0e\u91cd\u529b\u8986\u76d6\u4ec5\u66f4\u65b0\u517c\u5bb9\u6027/API \u6620\u5c04\u3002useDedicatedDimensionSaveFolder \u4e0e dedicatedDimensionSaveFolderName \u4f1a\u6539\u53d8\u884c\u661f\u7ef4\u5ea6\u7684\u5b58\u6863\u8def\u5f84\u3002\u4fee\u6539\u540e\u9700\u8981\u91cd\u542f\u6e38\u620f\u6216\u670d\u52a1\u5668\u3002\n"
            + "Planet dimensions, rocket launch restrictions, compatibility dimension mappings, and save-folder settings. launchAnywhere=true only lets a rocket pass the current-dimension check in any dimension; a launch pad, fuel, and the other normal launch conditions are still required. Built-in dimensions are always registered. The enable*Dimension toggles are retained as compatibility/API query values and do not disable built-in dimensions. Planet gravity multipliers are used only by getGravityMultiplierForDimension and do not change effective gravity in the current built-in worlds. Custom dimension enable/gravity overrides update compatibility/API mappings only. useDedicatedDimensionSaveFolder and dedicatedDimensionSaveFolderName change the save path for planet dimensions. A game or server restart is required after changes.";
    private static final String PLANET_TIERS_COMMENT =
"\u5185\u7f6e\u884c\u661f\u3001\u5df2\u6ce8\u518c\u7684\u81ea\u5b9a\u4e49\u884c\u661f\u4e0e\u5df2\u6ce8\u518c\u7684\u5916\u90e8\u7ef4\u5ea6\u7684\u6700\u4f4e\u706b\u7bad\u7b49\u7ea7\uff0c\u6709\u6548\u8303\u56f4\u4e3a 0-15\u3002\u8bbe\u4e3a 0 \u8868\u793a\u4e0d\u9650\u5236\u3002\u81ea\u5b9a\u4e49\u884c\u661f\u4f7f\u7528 custom_<\u884c\u661f\u6ce8\u518c ID \u7684 path \u90e8\u5206> \u914d\u7f6e\u9879\uff0c\u4f8b\u5982 ad_astra:planet_ceres \u5bf9\u5e94 custom_planet_ceres\uff1b\u5916\u90e8\u7ef4\u5ea6\u9ed8\u8ba4\u4f7f\u7528 external_dimensions.cfg \u4e2d\u5df2\u6ce8\u518c\u884c\u7684\u7b49\u7ea7\uff0ccustomDimensionTierOverrides \u53ef\u4ee5\u4f5c\u4e3a\u6700\u540e\u7684\u8986\u76d6\u3002\u4fee\u6539\u540e\u9700\u8981\u91cd\u542f\u6e38\u620f\u6216\u670d\u52a1\u5668\u3002\n"
+ "Minimum rocket tiers for built-in planets, registered custom planets, and registered external dimensions; valid range: 0-15. Set to 0 for no restriction. A custom planet uses custom_<the path part of its registration ID>; for example, ad_astra:planet_ceres uses custom_planet_ceres. External dimensions use the tier from registered rows in external_dimensions.cfg by default, and customDimensionTierOverrides can be the final override. A game or server restart is required after changes.";
    private static final String MOBS_COMMENT =
        "\u654c\u5bf9\u751f\u7269\u751f\u6210\u603b\u5f00\u5173\u3001\u884c\u661f\u7ef4\u5ea6\u6309\u7ef4\u5ea6\u5f00\u5173\u548c\u6bcf\u79cd\u5b9e\u4f53\u6570\u91cf\u4e0a\u9650\u8bbe\u7f6e\u3002\u751f\u6210\u500d\u7387\u4ec5\u5728\u503c\u5c0f\u4e8e\u7b49\u4e8e 0 \u65f6\u7981\u7528\u654c\u5bf9\u751f\u7269\u751f\u6210\uff0c\u6b63\u6570\u4e0d\u4f1a\u6309\u6bd4\u4f8b\u8c03\u6574\u751f\u6210\u7387\u3002\n"
            + "Hostile-mob spawning, per-dimension switches in planet dimensions, and per-type entity caps. The spawn-rate multiplier acts as a global hostile-mob spawn switch: values less than or equal to 0 disable hostile-mob spawning, while positive values do not scale the spawn rate.";
    private static final String WORLDGEN_COMMENT =
        "\u884c\u661f\u5730\u5f62\u4e0e\u77ff\u8109\u751f\u6210\u8bbe\u7f6e\u3002\u5185\u7f6e\u884c\u661f\u77ff\u8109\u548c worldgen_custom_blocks \u4e2d\u7684\u751f\u6210\u5668\u4f7f\u7528 oreGenerationMultiplier \u8c03\u6574\u6bcf\u533a\u5757\u7684\u751f\u6210\u5c1d\u8bd5\u6b21\u6570\uff1bCustomPlanetDefinition \u76f4\u63a5\u58f0\u660e\u7684\u81ea\u5b9a\u4e49\u77ff\u8109\u53ef\u5728\u5bf9\u5e94 worldgen_<\u884c\u661f> \u7c7b\u522b\u4e2d\u8c03\u6574\u56db\u4e2a\u6570\u503c\uff0c\u4f46\u4e0d\u53d7 oreGenerationMultiplier \u5f71\u54cd\u3002enableStructureGeneration\u3001enableLunarVillages \u4e0e enableMarsOutposts \u4ec5\u4e3a\u517c\u5bb9\u4fdd\u7559\uff1bdebugWorldgen \u7528\u4e8e\u8f93\u51fa\u77ff\u8109\u751f\u6210\u8c03\u8bd5\u65e5\u5fd7\u3002\u4fee\u6539\u540e\u9700\u8981\u91cd\u542f\u6e38\u620f\u6216\u670d\u52a1\u5668\uff0c\u53ea\u5bf9\u65b0\u751f\u6210\u7684\u533a\u5757\u751f\u6548\u3002\n"
            + "Planet terrain and ore-generation settings. Built-in planet ores and generators in worldgen_custom_blocks use oreGenerationMultiplier to adjust attempts per chunk. Ores declared directly by CustomPlanetDefinition can be adjusted with four properties in the matching worldgen_<planet> category, but are not affected by oreGenerationMultiplier. enableStructureGeneration, enableLunarVillages, and enableMarsOutposts are retained for compatibility only; debugWorldgen enables ore-generation debug logging. A game or server restart is required after changes; changes affect newly generated chunks only.";
    private static final String DEBUG_COMMENT =
        "\u8c03\u8bd5\u517c\u5bb9\u5f00\u5173\u3002\u5f53\u524d\u7248\u672c\u7684\u6c27\u6c14\u3001\u6e29\u5ea6\u4e0e\u91cd\u529b\u7cfb\u7edf\u4e0d\u8bfb\u53d6\u8fd9\u4e9b\u5c5e\u6027\u3002\n"
            + "Compatibility debug toggles. The current oxygen, temperature, and gravity systems do not read these properties.";
    private static final String UNKNOWN_COMMENT =
        "\u672a\u5206\u7c7b\u914d\u7f6e\u9879\u3002\u8bf7\u53c2\u8003\u914d\u7f6e\u9879\u540d\u79f0\u4e0e\u6570\u503c\u8303\u56f4\u3002\n"
            + "Unclassified configuration option. Refer to the property name and value range.";
    private static final String LEGACY_CATEGORY_COMMENT =
        "\u65e7\u7248\u672c\u9057\u7559\u914d\u7f6e\u5206\u7c7b\u3002\u5f53\u524d\u7248\u672c\u4e0d\u4f1a\u8bfb\u53d6\u6b64\u5206\u7c7b\uff1b\u4fdd\u7559\u5b83\u662f\u4e3a\u4e86\u907f\u514d\u76f4\u63a5\u5220\u9664\u65e7\u914d\u7f6e\u3002\u786e\u8ba4\u4e0d\u518d\u9700\u8981\u540e\u53ef\u624b\u52a8\u5220\u9664\u3002\n"
            + "Legacy configuration category from an older version. The current version does not read this category; it is retained to avoid deleting old settings. It can be removed manually once it is no longer needed.";
    private static final String LEGACY_PROPERTY_COMMENT =
        "\u65e7\u7248\u672c\u9057\u7559\u914d\u7f6e\u9879\u3002\u5f53\u524d\u7248\u672c\u4e0d\u4f1a\u8bfb\u53d6\u6b64\u9879\uff1b\u4fdd\u7559\u5b83\u662f\u4e3a\u4e86\u517c\u5bb9\u65e7\u914d\u7f6e\u3002\u786e\u8ba4\u4e0d\u518d\u9700\u8981\u540e\u53ef\u624b\u52a8\u5220\u9664\u3002\n"
            + "Legacy configuration option from an older version. The current version does not read this option; it is retained for compatibility with old configuration files. It can be removed manually once it is no longer needed.";
    private static final Map<String, String> PROPERTY_COMMENTS = createPropertyComments();

    private static Configuration configuration;
    private static Configuration coreConfiguration;
    private static Configuration clientConfiguration;
    private static Configuration machinesConfiguration;
    private static Configuration dimensionsConfiguration;
    private static Configuration mobsConfiguration;
    private static Configuration worldgenConfiguration;
    private static Configuration debugConfiguration;
    private static Configuration legacyConfiguration;
    private static Configuration[] splitConfigurations = new Configuration[0];
    private static final Map<Integer, Boolean> PLANET_DIMENSION_ENABLED = new HashMap<>();
    private static final Map<Integer, Float> PLANET_GRAVITY_MULTIPLIERS = new HashMap<>();
    private static final Map<Integer, Boolean> PLANET_HOSTILE_MOB_OVERRIDES = new HashMap<>();
    private static final Set<Integer> PLANET_DIMENSIONS = new HashSet<>();

    private static final PlanetTierConfig[] PLANET_TIER_CONFIGS = new PlanetTierConfig[] {
        new PlanetTierConfig("moon", ModDimensions.MOON_ID, 1, "\u6708\u7403", "Moon"),
        new PlanetTierConfig("mars", ModDimensions.MARS_ID, 2, "\u706b\u661f", "Mars"),
        new PlanetTierConfig("mercury", ModDimensions.MERCURY_ID, 3, "\u6c34\u661f", "Mercury"),
        new PlanetTierConfig("venus", ModDimensions.VENUS_ID, 3, "\u91d1\u661f", "Venus"),
        new PlanetTierConfig("glacio", ModDimensions.GLACIO_ID, 4, "\u51b0\u5ddd\u661f", "Glacio"),
    };

    private static final PlanetMobConfig[] PLANET_MOB_CONFIGS = new PlanetMobConfig[] {
        new PlanetMobConfig("enableHostileMobsOnMoon", ModDimensions.MOON_ID, "\u6708\u7403", "Moon"),
        new PlanetMobConfig("enableHostileMobsOnMars", ModDimensions.MARS_ID, "\u706b\u661f", "Mars"),
        new PlanetMobConfig("enableHostileMobsOnMercury", ModDimensions.MERCURY_ID, "\u6c34\u661f", "Mercury"),
        new PlanetMobConfig("enableHostileMobsOnVenus", ModDimensions.VENUS_ID, "\u91d1\u661f", "Venus"),
        new PlanetMobConfig("enableHostileMobsOnGlacio", ModDimensions.GLACIO_ID, "\u51b0\u5ddd\u661f", "Glacio"),
    };

    private static final PlanetDimensionConfig[] PLANET_DIMENSION_CONFIGS = new PlanetDimensionConfig[] {
        new PlanetDimensionConfig("enableMoonDimension", "moonGravityMultiplier", ModDimensions.MOON_ID, "\u6708\u7403", "Moon"),
        new PlanetDimensionConfig("enableMarsDimension", "marsGravityMultiplier", ModDimensions.MARS_ID, "\u706b\u661f", "Mars"),
        new PlanetDimensionConfig("enableMercuryDimension", "mercuryGravityMultiplier", ModDimensions.MERCURY_ID, "\u6c34\u661f", "Mercury"),
        new PlanetDimensionConfig("enableVenusDimension", "venusGravityMultiplier", ModDimensions.VENUS_ID, "\u91d1\u661f", "Venus"),
        new PlanetDimensionConfig("enableGlacioDimension", "glacioGravityMultiplier", ModDimensions.GLACIO_ID, "\u51b0\u5ddd\u661f", "Glacio"),
    };

    // General Configuration
    public static boolean debugLogging;
    public static boolean disableOxygen;
    public static boolean disableTemperature;
    public static boolean disableGravity;
    public static boolean enableAirVortexes;
    public static boolean allowFlagImages;
    public static int radioVolume;
    public static boolean spaceMuffler;
    public static boolean jetSuitEnabled;
    public static boolean showOxygenDistributorArea;
    public static boolean showGravityNormalizerArea;
    public static int oxygenBarX;
    public static int oxygenBarY;
    public static float oxygenBarScale;
    public static int energyBarX;
    public static int energyBarY;
    public static float energyBarScale;
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
    public static int ironMaxEnergyInOut = 100;
    public static int ironEnergyCapacity = 10_000;
    public static int ironFluidCapacity = 0;
    public static int steelMaxEnergyInOut = 150;
    public static int steelEnergyCapacity = 20_000;
    public static int steelFluidCapacity = 3_000;
    public static int deshMaxEnergyInOut = 250;
    public static int deshEnergyCapacity = 50_000;
    public static int deshFluidCapacity = 5_000;
    public static int ostrumMaxEnergyInOut = 500;
    public static int ostrumEnergyCapacity = 100_000;
    public static int ostrumFluidCapacity = 10_000;
    public static int coalGeneratorEnergyGenerationPerTick = 20;
    public static int etrionicBlastFurnaceBlastingEnergyPerItem = 10;
    public static int waterPumpEnergyPerTick = 20;
    public static int waterPumpFluidGenerationPerTick = 50;
    public static int energizerEnergyCapacity = 2_000_000;
    public static int maxDistributionBlocks = 6000;
    public static int distributionRefreshRate = 100;
    public static int pipeRefreshRate = 50;

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
    public static boolean launchFromAnywhere;
    public static boolean enableMoonDimension;
    public static boolean enableMarsDimension;
    public static boolean enableMercuryDimension;
    public static boolean enableVenusDimension;
    public static boolean enableGlacioDimension;
    public static boolean useDedicatedDimensionSaveFolder;
    public static String dedicatedDimensionSaveFolderName;
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
    public static String[] noOxygenEntityWhitelist;

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
        File legacyFile = resolveConfigFile(file);
        File folder = legacyFile.getParentFile();
        if (folder == null) {
            folder = new File("config/ad_astra");
        }
        if (!folder.exists()) {
            folder.mkdirs();
        }

        legacyConfiguration = legacyFile.exists() ? new Configuration(legacyFile) : null;
        coreConfiguration = new Configuration(new File(folder, "core.cfg"));
        clientConfiguration = new Configuration(new File(folder, "client.cfg"));
        machinesConfiguration = new Configuration(new File(folder, "machines.cfg"));
        dimensionsConfiguration = new Configuration(new File(folder, "dimensions.cfg"));
        mobsConfiguration = new Configuration(new File(folder, "mobs.cfg"));
        worldgenConfiguration = new Configuration(new File(folder, "worldgen.cfg"));
        debugConfiguration = new Configuration(new File(folder, "debug.cfg"));
        ExternalDimensionConfig.init(new File(folder, "external_dimensions.cfg"));
        ConfigurableRocketRegistry.init(new File(folder, "rockets.cfg"));
        splitConfigurations = new Configuration[] {
            coreConfiguration,
            clientConfiguration,
            machinesConfiguration,
            dimensionsConfiguration,
            mobsConfiguration,
            worldgenConfiguration,
            debugConfiguration
        };
        configuration = coreConfiguration;
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
        debugLogging = getBoolean(
            "debugLogging",
            Configuration.CATEGORY_GENERAL,
            false,
            "\u914d\u7f6e\u9879\u3002\u4fee\u6539\u540e\u53ef\u80fd\u9700\u8981\u91cd\u542f\u6e38\u620f/\u670d\u52a1\u5668\u3002");
        disableOxygen = getBoolean(
            "disableOxygen",
            Configuration.CATEGORY_GENERAL,
            false,
            "\u914d\u7f6e\u9879\u3002\u4fee\u6539\u540e\u53ef\u80fd\u9700\u8981\u91cd\u542f\u6e38\u620f/\u670d\u52a1\u5668\u3002");
        disableTemperature = getBoolean(
            "disableTemperature",
            Configuration.CATEGORY_GENERAL,
            false,
            "\u914d\u7f6e\u9879\u3002\u4fee\u6539\u540e\u53ef\u80fd\u9700\u8981\u91cd\u542f\u6e38\u620f/\u670d\u52a1\u5668\u3002");
        disableGravity = getBoolean(
            "disableGravity",
            Configuration.CATEGORY_GENERAL,
            false,
            "\u914d\u7f6e\u9879\u3002\u4fee\u6539\u540e\u53ef\u80fd\u9700\u8981\u91cd\u542f\u6e38\u620f/\u670d\u52a1\u5668\u3002");
        enableAirVortexes = getBoolean(
            "enableAirVortexes",
            Configuration.CATEGORY_GENERAL,
            true,
            "\u914d\u7f6e\u9879\u3002\u4fee\u6539\u540e\u53ef\u80fd\u9700\u8981\u91cd\u542f\u6e38\u620f/\u670d\u52a1\u5668\u3002");
        allowFlagImages = getBoolean(
            "allowFlagImages",
            Configuration.CATEGORY_GENERAL,
            true,
            "\u914d\u7f6e\u9879\u3002\u4fee\u6539\u540e\u53ef\u80fd\u9700\u8981\u91cd\u542f\u6e38\u620f/\u670d\u52a1\u5668\u3002");
        radioVolume = getInt(
            "radioVolume",
            Configuration.CATEGORY_GENERAL,
            50,
            0,
            100,
            "\u914d\u7f6e\u9879\u3002\u4fee\u6539\u540e\u53ef\u80fd\u9700\u8981\u91cd\u542f\u6e38\u620f/\u670d\u52a1\u5668\u3002");
        spaceMuffler = getBoolean(
            "spaceMuffler",
            "client",
            true,
            "\u914d\u7f6e\u9879\u3002\u4fee\u6539\u540e\u53ef\u80fd\u9700\u8981\u91cd\u542f\u6e38\u620f/\u670d\u52a1\u5668\u3002");
        jetSuitEnabled = getBoolean(
            "jetSuitEnabled",
            "client",
            true,
            "\u914d\u7f6e\u9879\u3002\u4fee\u6539\u540e\u53ef\u80fd\u9700\u8981\u91cd\u542f\u6e38\u620f/\u670d\u52a1\u5668\u3002");
        showOxygenDistributorArea = getBoolean(
            "showOxygenDistributorArea",
            "client",
            false,
            "\u914d\u7f6e\u9879\u3002\u4fee\u6539\u540e\u53ef\u80fd\u9700\u8981\u91cd\u542f\u6e38\u620f/\u670d\u52a1\u5668\u3002");
        showGravityNormalizerArea = getBoolean(
            "showGravityNormalizerArea",
            "client",
            false,
            "\u914d\u7f6e\u9879\u3002\u4fee\u6539\u540e\u53ef\u80fd\u9700\u8981\u91cd\u542f\u6e38\u620f/\u670d\u52a1\u5668\u3002");
        oxygenBarX = getInt(
            "oxygenBarX",
            "client",
            5,
            -10000,
            10000,
            "\u914d\u7f6e\u9879\u3002\u4fee\u6539\u540e\u53ef\u80fd\u9700\u8981\u91cd\u542f\u6e38\u620f/\u670d\u52a1\u5668\u3002");
        oxygenBarY = getInt(
            "oxygenBarY",
            "client",
            25,
            -10000,
            10000,
            "\u914d\u7f6e\u9879\u3002\u4fee\u6539\u540e\u53ef\u80fd\u9700\u8981\u91cd\u542f\u6e38\u620f/\u670d\u52a1\u5668\u3002");
        oxygenBarScale = getFloat(
            "oxygenBarScale",
            "client",
            1.0f,
            0.25f,
            4.0f,
            "\u914d\u7f6e\u9879\u3002\u4fee\u6539\u540e\u53ef\u80fd\u9700\u8981\u91cd\u542f\u6e38\u620f/\u670d\u52a1\u5668\u3002");
        energyBarX = getInt(
            "energyBarX",
            "client",
            11,
            -10000,
            10000,
            "\u914d\u7f6e\u9879\u3002\u4fee\u6539\u540e\u53ef\u80fd\u9700\u8981\u91cd\u542f\u6e38\u620f/\u670d\u52a1\u5668\u3002");
        energyBarY = getInt(
            "energyBarY",
            "client",
            95,
            -10000,
            10000,
            "\u914d\u7f6e\u9879\u3002\u4fee\u6539\u540e\u53ef\u80fd\u9700\u8981\u91cd\u542f\u6e38\u620f/\u670d\u52a1\u5668\u3002");
        energyBarScale = getFloat(
            "energyBarScale",
            "client",
            1.0f,
            0.25f,
            4.0f,
            "\u914d\u7f6e\u9879\u3002\u4fee\u6539\u540e\u53ef\u80fd\u9700\u8981\u91cd\u542f\u6e38\u620f/\u670d\u52a1\u5668\u3002");
        oxygenDamageAmount = getFloat(
            "oxygenDamageAmount",
            Configuration.CATEGORY_GENERAL,
            2.0f,
            0.0f,
            20.0f,
            "\u914d\u7f6e\u9879\u3002\u4fee\u6539\u540e\u53ef\u80fd\u9700\u8981\u91cd\u542f\u6e38\u620f/\u670d\u52a1\u5668\u3002");
        oxygenDamageInterval = getInt(
            "oxygenDamageInterval",
            Configuration.CATEGORY_GENERAL,
            20,
            1,
            200,
            "\u914d\u7f6e\u9879\u3002\u4fee\u6539\u540e\u53ef\u80fd\u9700\u8981\u91cd\u542f\u6e38\u620f/\u670d\u52a1\u5668\u3002");
        oxygenConsumptionInterval = getInt(
            "oxygenConsumptionInterval",
            Configuration.CATEGORY_GENERAL,
            12,
            1,
            200,
            "\u914d\u7f6e\u9879\u3002\u4fee\u6539\u540e\u53ef\u80fd\u9700\u8981\u91cd\u542f\u6e38\u620f/\u670d\u52a1\u5668\u3002");
        oxygenConsumptionAmount = getInt(
            "oxygenConsumptionAmount",
            Configuration.CATEGORY_GENERAL,
            1,
            1,
            1000,
            "\u914d\u7f6e\u9879\u3002\u4fee\u6539\u540e\u53ef\u80fd\u9700\u8981\u91cd\u542f\u6e38\u620f/\u670d\u52a1\u5668\u3002");
        enableSpaceEnvironmentEffects = getBoolean(
            "enableSpaceEnvironmentEffects",
            Configuration.CATEGORY_GENERAL,
            true,
            "\u914d\u7f6e\u9879\u3002\u4fee\u6539\u540e\u53ef\u80fd\u9700\u8981\u91cd\u542f\u6e38\u620f/\u670d\u52a1\u5668\u3002");
        planetRandomTickSpeed = getInt(
            "planetRandomTickSpeed",
            Configuration.CATEGORY_GENERAL,
            4,
            0,
            64,
            "\u914d\u7f6e\u9879\u3002\u4fee\u6539\u540e\u53ef\u80fd\u9700\u8981\u91cd\u542f\u6e38\u620f/\u670d\u52a1\u5668\u3002");

        // Performance Configuration
        maxOxygenDistributorRadius = getInt(
            "maxOxygenDistributorRadius",
            "performance",
            18,
            1,
            100,
            "\u914d\u7f6e\u9879\u3002\u4fee\u6539\u540e\u53ef\u80fd\u9700\u8981\u91cd\u542f\u6e38\u620f/\u670d\u52a1\u5668\u3002");
        oxygenScanRadius = getInt(
            "oxygenScanRadius",
            "performance",
            16,
            1,
            64,
            "\u914d\u7f6e\u9879\u3002\u4fee\u6539\u540e\u53ef\u80fd\u9700\u8981\u91cd\u542f\u6e38\u620f/\u670d\u52a1\u5668\u3002");
        enableMachineIdleOptimization = getBoolean(
            "enableMachineIdleOptimization",
            "performance",
            true,
            "\u914d\u7f6e\u9879\u3002\u4fee\u6539\u540e\u53ef\u80fd\u9700\u8981\u91cd\u542f\u6e38\u620f/\u670d\u52a1\u5668\u3002");
        machineTransferInterval = getInt(
            "machineTransferInterval",
            "performance",
            10,
            1,
            20,
            "\u914d\u7f6e\u9879\u3002\u4fee\u6539\u540e\u53ef\u80fd\u9700\u8981\u91cd\u542f\u6e38\u620f/\u670d\u52a1\u5668\u3002");
        sealedRoomCacheLifetime = getInt(
            "sealedRoomCacheLifetime",
            "performance",
            60,
            20,
            200,
            "\u914d\u7f6e\u9879\u3002\u4fee\u6539\u540e\u53ef\u80fd\u9700\u8981\u91cd\u542f\u6e38\u620f/\u670d\u52a1\u5668\u3002");

        // Balance Configuration
        gravityNormalizerEnergyMultiplier = getFloat(
            "gravityNormalizerEnergyMultiplier",
            "balance",
            1.0f,
            0.1f,
            10.0f,
            "\u914d\u7f6e\u9879\u3002\u4fee\u6539\u540e\u53ef\u80fd\u9700\u8981\u91cd\u542f\u6e38\u620f/\u670d\u52a1\u5668\u3002");
        sealedRoomMaxBlocks = getInt(
            "sealedRoomMaxBlocks",
            "balance",
            5000,
            100,
            50000,
            "\u914d\u7f6e\u9879\u3002\u4fee\u6539\u540e\u53ef\u80fd\u9700\u8981\u91cd\u542f\u6e38\u620f/\u670d\u52a1\u5668\u3002");

        // Machine Configuration
        machineProcessingSpeedMultiplier = getFloat(
            "machineProcessingSpeedMultiplier",
            "machines",
            1.0f,
            0.1f,
            10.0f,
            "\u914d\u7f6e\u9879\u3002\u4fee\u6539\u540e\u53ef\u80fd\u9700\u8981\u91cd\u542f\u6e38\u620f/\u670d\u52a1\u5668\u3002");
        machineEnergyConsumptionMultiplier = getFloat(
            "machineEnergyConsumptionMultiplier",
            "machines",
            1.0f,
            0.1f,
            10.0f,
            "\u914d\u7f6e\u9879\u3002\u4fee\u6539\u540e\u53ef\u80fd\u9700\u8981\u91cd\u542f\u6e38\u620f/\u670d\u52a1\u5668\u3002");
        coalGeneratorEnergyMultiplier = getFloat(
            "coalGeneratorEnergyMultiplier",
            "machines",
            1.0f,
            0.1f,
            10.0f,
            "\u914d\u7f6e\u9879\u3002\u4fee\u6539\u540e\u53ef\u80fd\u9700\u8981\u91cd\u542f\u6e38\u620f/\u670d\u52a1\u5668\u3002");
        solarPanelEnergyMultiplier = getFloat(
            "solarPanelEnergyMultiplier",
            "machines",
            1.0f,
            0.1f,
            10.0f,
            "\u914d\u7f6e\u9879\u3002\u4fee\u6539\u540e\u53ef\u80fd\u9700\u8981\u91cd\u542f\u6e38\u620f/\u670d\u52a1\u5668\u3002");
        compressorBaseTime = getInt(
            "compressorBaseTime",
            "machines",
            100,
            10,
            1000,
            "\u914d\u7f6e\u9879\u3002\u4fee\u6539\u540e\u53ef\u80fd\u9700\u8981\u91cd\u542f\u6e38\u620f/\u670d\u52a1\u5668\u3002");
        cryoFreezerBaseTime = getInt(
            "cryoFreezerBaseTime",
            "machines",
            200,
            10,
            1000,
            "\u914d\u7f6e\u9879\u3002\u4fee\u6539\u540e\u53ef\u80fd\u9700\u8981\u91cd\u542f\u6e38\u620f/\u670d\u52a1\u5668\u3002");
        etrionicBlastFurnaceBaseTime = getInt(
            "etrionicBlastFurnaceBaseTime",
            "machines",
            200,
            10,
            1000,
            "\u914d\u7f6e\u9879\u3002\u4fee\u6539\u540e\u53ef\u80fd\u9700\u8981\u91cd\u542f\u6e38\u620f/\u670d\u52a1\u5668\u3002");
        fuelRefineryBaseTime = getInt(
            "fuelRefineryBaseTime",
            "machines",
            150,
            10,
            1000,
            "\u914d\u7f6e\u9879\u3002\u4fee\u6539\u540e\u53ef\u80fd\u9700\u8981\u91cd\u542f\u6e38\u620f/\u670d\u52a1\u5668\u3002");

        ironMaxEnergyInOut = getInt("ironMaxEnergyInOut", "machines", 100, 0, Integer.MAX_VALUE,
            bilingual("\u94c1\u7ea7\u6bcf\u523b\u6700\u5927 FE \u8f93\u5165/\u8f93\u51fa\u91cf\u3002", "Iron tier max FE input/output per tick."));
        ironEnergyCapacity = getInt("ironEnergyCapacity", "machines", 10000, 0, Integer.MAX_VALUE,
            bilingual("\u94c1\u7ea7\u5185\u90e8 FE \u5bb9\u91cf\u3002", "Iron tier internal FE capacity."));
        ironFluidCapacity = getInt("ironFluidCapacity", "machines", 0, 0, Integer.MAX_VALUE,
            bilingual("\u94c1\u7ea7\u5185\u90e8\u6d41\u4f53\u5bb9\u91cf\uff0c\u5355\u4f4d\uff1amB\u3002", "Iron tier internal fluid capacity in mB."));
        steelMaxEnergyInOut = getInt("steelMaxEnergyInOut", "machines", 150, 0, Integer.MAX_VALUE,
            bilingual("\u94a2\u7ea7\u6bcf\u523b\u6700\u5927 FE \u8f93\u5165/\u8f93\u51fa\u91cf\u3002", "Steel tier max FE input/output per tick."));
        steelEnergyCapacity = getInt("steelEnergyCapacity", "machines", 20000, 0, Integer.MAX_VALUE,
            bilingual("\u94a2\u7ea7\u5185\u90e8 FE \u5bb9\u91cf\u3002", "Steel tier internal FE capacity."));
        steelFluidCapacity = getInt("steelFluidCapacity", "machines", 3000, 0, Integer.MAX_VALUE,
            bilingual("\u94a2\u7ea7\u5185\u90e8\u6d41\u4f53\u5bb9\u91cf\uff0c\u5355\u4f4d\uff1amB\u3002", "Steel tier internal fluid capacity in mB."));
        deshMaxEnergyInOut = getInt("deshMaxEnergyInOut", "machines", 250, 0, Integer.MAX_VALUE,
            bilingual("Desh \u7ea7\u6bcf\u523b\u6700\u5927 FE \u8f93\u5165/\u8f93\u51fa\u91cf\u3002", "Desh tier max FE input/output per tick."));
        deshEnergyCapacity = getInt("deshEnergyCapacity", "machines", 50000, 0, Integer.MAX_VALUE,
            bilingual("Desh \u7ea7\u5185\u90e8 FE \u5bb9\u91cf\u3002", "Desh tier internal FE capacity."));
        deshFluidCapacity = getInt("deshFluidCapacity", "machines", 5000, 0, Integer.MAX_VALUE,
            bilingual("Desh \u7ea7\u5185\u90e8\u6d41\u4f53\u5bb9\u91cf\uff0c\u5355\u4f4d\uff1amB\u3002", "Desh tier internal fluid capacity in mB."));
        ostrumMaxEnergyInOut = getInt("ostrumMaxEnergyInOut", "machines", 500, 0, Integer.MAX_VALUE,
            bilingual("Ostrum \u7ea7\u6bcf\u523b\u6700\u5927 FE \u8f93\u5165/\u8f93\u51fa\u91cf\u3002", "Ostrum tier max FE input/output per tick."));
        ostrumEnergyCapacity = getInt("ostrumEnergyCapacity", "machines", 100000, 0, Integer.MAX_VALUE,
            bilingual("Ostrum \u7ea7\u5185\u90e8 FE \u5bb9\u91cf\u3002", "Ostrum tier internal FE capacity."));
        ostrumFluidCapacity = getInt("ostrumFluidCapacity", "machines", 10000, 0, Integer.MAX_VALUE,
            bilingual("Ostrum \u7ea7\u5185\u90e8\u6d41\u4f53\u5bb9\u91cf\uff0c\u5355\u4f4d\uff1amB\u3002", "Ostrum tier internal fluid capacity in mB."));
        coalGeneratorEnergyGenerationPerTick = getInt("coalGeneratorEnergyGenerationPerTick", "machines", 20, 0, Integer.MAX_VALUE,
            bilingual("\u7164\u70ad\u53d1\u7535\u673a\u6bcf\u523b\u4ea7\u751f\u7684 FE\uff0c\u672a\u8ba1\u5165\u500d\u7387\u3002", "Coal generator FE generated per tick before multipliers."));
        etrionicBlastFurnaceBlastingEnergyPerItem = getInt("etrionicBlastFurnaceBlastingEnergyPerItem", "machines", 10, 0, Integer.MAX_VALUE,
            bilingual("Etrionic \u7535\u529b\u9ad8\u7089\u5185\u7f6e\u51b6\u70bc\u914d\u65b9\u7684\u57fa\u7840\u6bcf\u523b FE \u6d88\u8017\uff1b\u5b9e\u9645\u6d88\u8017\u8fd8\u4f1a\u5e94\u7528\u673a\u5668\u80fd\u8017\u500d\u7387\u3002", "Base FE consumed per tick by the Etrionic blast furnace's built-in blasting recipes; the machine energy-consumption multiplier is applied to the actual cost."));
        waterPumpEnergyPerTick = getInt("waterPumpEnergyPerTick", "machines", 20, 0, Integer.MAX_VALUE,
            bilingual("\u6c34\u6cf5\u6bcf\u523b\u6d88\u8017\u7684 FE\u3002", "FE consumed by the water pump per tick."));
        waterPumpFluidGenerationPerTick = getInt("waterPumpFluidGenerationPerTick", "machines", 50, 0, Integer.MAX_VALUE,
            bilingual("\u6c34\u6cf5\u6bcf\u523b\u751f\u6210\u7684\u6c34\u91cf\uff0c\u5355\u4f4d\uff1amB\u3002", "Water generated by the water pump per tick in mB."));
        energizerEnergyCapacity = getInt("energizerEnergyCapacity", "machines", 2000000, 0, Integer.MAX_VALUE,
            bilingual("\u80fd\u91cf\u6fc0\u53d1\u5668\u5185\u90e8 FE \u5bb9\u91cf\u3002", "Energizer internal FE capacity."));
        maxDistributionBlocks = getInt("maxDistributionBlocks", "machines", 6000, 1, Integer.MAX_VALUE,
            bilingual("\u6c27\u6c14\u5206\u914d\u5668\u4e0e\u91cd\u529b\u6b63\u5219\u5316\u5668\u6700\u591a\u53ef\u8986\u76d6\u7684\u65b9\u5757\u6570\u3002", "Maximum blocks oxygen distributors and gravity normalizers can distribute to."));
        distributionRefreshRate = getInt("distributionRefreshRate", "machines", 100, 1, Integer.MAX_VALUE,
            bilingual("\u6c27\u6c14\u5206\u914d\u5668\u4e0e\u91cd\u529b\u6b63\u5219\u5316\u5668\u8986\u76d6\u8303\u56f4\u7684\u5237\u65b0\u95f4\u9694\uff0c\u5355\u4f4d\u4e3a\u523b\u3002", "Refresh interval in ticks for distributor and gravity normalizer coverage."));
        pipeRefreshRate = getInt("pipeRefreshRate", "machines", 50, 1, Integer.MAX_VALUE,
            bilingual("\u7ba1\u9053\u68c0\u67e5\u90bb\u8fd1\u7ba1\u9053\u4ee5\u5bfb\u627e\u4f20\u8f93\u673a\u4f1a\u7684\u95f4\u9694\uff0c\u5355\u4f4d\u4e3a\u523b\u3002", "How often idle pipes probe neighbors for new transfer opportunities, in ticks."));

        // Environment Configuration
        temperatureDamageMultiplier = getFloat(
            "temperatureDamageMultiplier",
            "environment",
            1.0f,
            0.0f,
            10.0f,
            "\u914d\u7f6e\u9879\u3002\u4fee\u6539\u540e\u53ef\u80fd\u9700\u8981\u91cd\u542f\u6e38\u620f/\u670d\u52a1\u5668\u3002");
        freezeDamageInterval = getInt(
            "freezeDamageInterval",
            "environment",
            40,
            1,
            200,
            "\u914d\u7f6e\u9879\u3002\u4fee\u6539\u540e\u53ef\u80fd\u9700\u8981\u91cd\u542f\u6e38\u620f/\u670d\u52a1\u5668\u3002");
        burnDamageInterval = getInt(
            "burnDamageInterval",
            "environment",
            40,
            1,
            200,
            "\u914d\u7f6e\u9879\u3002\u4fee\u6539\u540e\u53ef\u80fd\u9700\u8981\u91cd\u542f\u6e38\u620f/\u670d\u52a1\u5668\u3002");
        freezeDamageAmount = getFloat(
            "freezeDamageAmount",
            "environment",
            2.0f,
            0.0f,
            20.0f,
            "\u914d\u7f6e\u9879\u3002\u4fee\u6539\u540e\u53ef\u80fd\u9700\u8981\u91cd\u542f\u6e38\u620f/\u670d\u52a1\u5668\u3002");
        burnDamageAmount = getFloat(
            "burnDamageAmount",
            "environment",
            2.0f,
            0.0f,
            20.0f,
            "\u914d\u7f6e\u9879\u3002\u4fee\u6539\u540e\u53ef\u80fd\u9700\u8981\u91cd\u542f\u6e38\u620f/\u670d\u52a1\u5668\u3002");
        gravityMultiplier = getFloat(
            "gravityMultiplier",
            "environment",
            1.0f,
            0.0f,
            5.0f,
            "\u914d\u7f6e\u9879\u3002\u4fee\u6539\u540e\u53ef\u80fd\u9700\u8981\u91cd\u542f\u6e38\u620f/\u670d\u52a1\u5668\u3002");
        enableFallDamageInLowGravity = getBoolean(
            "enableFallDamageInLowGravity",
            "environment",
            true,
            "\u914d\u7f6e\u9879\u3002\u4fee\u6539\u540e\u53ef\u80fd\u9700\u8981\u91cd\u542f\u6e38\u620f/\u670d\u52a1\u5668\u3002");
        lowGravityFallDamageThreshold = getFloat(
            "lowGravityFallDamageThreshold",
            "environment",
            0.5f,
            0.0f,
            1.0f,
            "\u914d\u7f6e\u9879\u3002\u4fee\u6539\u540e\u53ef\u80fd\u9700\u8981\u91cd\u542f\u6e38\u620f/\u670d\u52a1\u5668\u3002");

        // Dimension Configuration
        launchFromAnywhere = getBoolean(
            "launchAnywhere",
            "dimensions",
            false,
            "\u662f\u5426\u5141\u8bb8\u706b\u7bad\u5728\u4efb\u610f\u7ef4\u5ea6\u901a\u8fc7\u53d1\u5c04\u524d\u7684\u7ef4\u5ea6\u68c0\u67e5\u3002\u8fd9\u4e0d\u4f1a\u53d6\u6d88\u53d1\u5c04\u53f0\u3001\u71c3\u6599\u6216\u5176\u4ed6\u6b63\u5e38\u53d1\u5c04\u6761\u4ef6\u3002\u4fee\u6539\u540e\u9700\u8981\u91cd\u542f\u3002\n"
                + "Whether a rocket may pass the current-dimension launch check in any dimension. This does not remove the launch-pad, fuel, or other normal launch requirements. Restart required after changes.");
        enableMoonDimension = getBoolean(
            "enableMoonDimension",
            "dimensions",
            true,
            "\u517c\u5bb9\u6027\u914d\u7f6e\u9879\uff1b\u5f53\u524d\u5185\u7f6e\u7ef4\u5ea6\u6ce8\u518c\u59cb\u7ec8\u5f00\u542f\uff0c\u8bbe\u4e3a false \u4e0d\u4f1a\u7981\u7528\u6708\u7403\u7ef4\u5ea6\u3002\u4fee\u6539\u540e\u9700\u8981\u91cd\u542f\u3002\n"
                + "Compatibility setting; built-in dimension registration remains enabled, so false does not disable the Moon dimension. Restart required after changes.");
        enableMarsDimension = getBoolean(
            "enableMarsDimension",
            "dimensions",
            true,
            "\u517c\u5bb9\u6027\u914d\u7f6e\u9879\uff1b\u5f53\u524d\u5185\u7f6e\u7ef4\u5ea6\u6ce8\u518c\u59cb\u7ec8\u5f00\u542f\uff0c\u8bbe\u4e3a false \u4e0d\u4f1a\u7981\u7528\u706b\u661f\u7ef4\u5ea6\u3002\u4fee\u6539\u540e\u9700\u8981\u91cd\u542f\u3002\n"
                + "Compatibility setting; built-in dimension registration remains enabled, so false does not disable the Mars dimension. Restart required after changes.");
        enableMercuryDimension = getBoolean(
            "enableMercuryDimension",
            "dimensions",
            true,
            "\u517c\u5bb9\u6027\u914d\u7f6e\u9879\uff1b\u5f53\u524d\u5185\u7f6e\u7ef4\u5ea6\u6ce8\u518c\u59cb\u7ec8\u5f00\u542f\uff0c\u8bbe\u4e3a false \u4e0d\u4f1a\u7981\u7528\u6c34\u661f\u7ef4\u5ea6\u3002\u4fee\u6539\u540e\u9700\u8981\u91cd\u542f\u3002\n"
                + "Compatibility setting; built-in dimension registration remains enabled, so false does not disable the Mercury dimension. Restart required after changes.");
        enableVenusDimension = getBoolean(
            "enableVenusDimension",
            "dimensions",
            true,
            "\u517c\u5bb9\u6027\u914d\u7f6e\u9879\uff1b\u5f53\u524d\u5185\u7f6e\u7ef4\u5ea6\u6ce8\u518c\u59cb\u7ec8\u5f00\u542f\uff0c\u8bbe\u4e3a false \u4e0d\u4f1a\u7981\u7528\u91d1\u661f\u7ef4\u5ea6\u3002\u4fee\u6539\u540e\u9700\u8981\u91cd\u542f\u3002\n"
                + "Compatibility setting; built-in dimension registration remains enabled, so false does not disable the Venus dimension. Restart required after changes.");
        enableGlacioDimension = getBoolean(
            "enableGlacioDimension",
            "dimensions",
            true,
            "\u517c\u5bb9\u6027\u914d\u7f6e\u9879\uff1b\u5f53\u524d\u5185\u7f6e\u7ef4\u5ea6\u6ce8\u518c\u59cb\u7ec8\u5f00\u542f\uff0c\u8bbe\u4e3a false \u4e0d\u4f1a\u7981\u7528\u51b0\u5ddd\u661f\u7ef4\u5ea6\u3002\u4fee\u6539\u540e\u9700\u8981\u91cd\u542f\u3002\n"
                + "Compatibility setting; built-in dimension registration remains enabled, so false does not disable the Glacio dimension. Restart required after changes.");
        useDedicatedDimensionSaveFolder = getBoolean(
            "useDedicatedDimensionSaveFolder",
            "dimensions",
            true,
            "\u662f\u5426\u4e3a\u884c\u661f\u7ef4\u5ea6\u4f7f\u7528\u72ec\u7acb\u7684\u5b58\u6863\u76ee\u5f55\u3002\u4fee\u6539\u540e\u9700\u8981\u91cd\u542f\u3002\n"
                + "Whether planet dimensions use a dedicated save folder. Restart required after changes.");
        dedicatedDimensionSaveFolderName = getString(
            "dedicatedDimensionSaveFolderName",
            "dimensions",
            "AdAstraDimensions",
            "\u72ec\u7acb\u884c\u661f\u7ef4\u5ea6\u5b58\u6863\u76ee\u5f55\u540d\u79f0\u3002\u4fee\u6539\u540e\u9700\u8981\u91cd\u542f\u3002\n"
                + "Name of the dedicated save folder for planet dimensions. Restart required after changes.");
        moonGravityMultiplier = getFloat(
            "moonGravityMultiplier",
            "dimensions",
            1.0f,
            0.0f,
            5.0f,
            "\u517c\u5bb9\u6027/API \u914d\u7f6e\u9879\uff1b\u5f53\u524d\u4e0d\u4f1a\u6539\u53d8\u6708\u7403\u4e16\u754c\u7684\u91cd\u529b\uff0c\u53ea\u4f9b getGravityMultiplierForDimension API \u67e5\u8be2\u4f7f\u7528\u3002\u8303\u56f4\uff1a0.0-5.0\u3002\n"
                + "Compatibility/API setting; it does not change Moon world physics in the current version and is used only by getGravityMultiplierForDimension. Range: 0.0-5.0.");
        marsGravityMultiplier = getFloat(
            "marsGravityMultiplier",
            "dimensions",
            1.0f,
            0.0f,
            5.0f,
            "\u517c\u5bb9\u6027/API \u914d\u7f6e\u9879\uff1b\u5f53\u524d\u4e0d\u4f1a\u6539\u53d8\u706b\u661f\u4e16\u754c\u7684\u91cd\u529b\uff0c\u53ea\u4f9b getGravityMultiplierForDimension API \u67e5\u8be2\u4f7f\u7528\u3002\u8303\u56f4\uff1a0.0-5.0\u3002\n"
                + "Compatibility/API setting; it does not change Mars world physics in the current version and is used only by getGravityMultiplierForDimension. Range: 0.0-5.0.");
        mercuryGravityMultiplier = getFloat(
            "mercuryGravityMultiplier",
            "dimensions",
            1.0f,
            0.0f,
            5.0f,
            "\u517c\u5bb9\u6027/API \u914d\u7f6e\u9879\uff1b\u5f53\u524d\u4e0d\u4f1a\u6539\u53d8\u6c34\u661f\u4e16\u754c\u7684\u91cd\u529b\uff0c\u53ea\u4f9b getGravityMultiplierForDimension API \u67e5\u8be2\u4f7f\u7528\u3002\u8303\u56f4\uff1a0.0-5.0\u3002\n"
                + "Compatibility/API setting; it does not change Mercury world physics in the current version and is used only by getGravityMultiplierForDimension. Range: 0.0-5.0.");
        venusGravityMultiplier = getFloat(
            "venusGravityMultiplier",
            "dimensions",
            1.0f,
            0.0f,
            5.0f,
            "\u517c\u5bb9\u6027/API \u914d\u7f6e\u9879\uff1b\u5f53\u524d\u4e0d\u4f1a\u6539\u53d8\u91d1\u661f\u4e16\u754c\u7684\u91cd\u529b\uff0c\u53ea\u4f9b getGravityMultiplierForDimension API \u67e5\u8be2\u4f7f\u7528\u3002\u8303\u56f4\uff1a0.0-5.0\u3002\n"
                + "Compatibility/API setting; it does not change Venus world physics in the current version and is used only by getGravityMultiplierForDimension. Range: 0.0-5.0.");
        glacioGravityMultiplier = getFloat(
            "glacioGravityMultiplier",
            "dimensions",
            1.0f,
            0.0f,
            5.0f,
            "\u517c\u5bb9\u6027/API \u914d\u7f6e\u9879\uff1b\u5f53\u524d\u4e0d\u4f1a\u6539\u53d8\u51b0\u5ddd\u661f\u4e16\u754c\u7684\u91cd\u529b\uff0c\u53ea\u4f9b getGravityMultiplierForDimension API \u67e5\u8be2\u4f7f\u7528\u3002\u8303\u56f4\uff1a0.0-5.0\u3002\n"
                + "Compatibility/API setting; it does not change Glacio world physics in the current version and is used only by getGravityMultiplierForDimension. Range: 0.0-5.0.");
        syncPlanetDimensionSettings();

        // Mob Configuration
        planetMobSpawnRateMultiplier = getFloat(
            "planetMobSpawnRateMultiplier",
            "mobs",
            1.0f,
            0.0f,
            10.0f,
            "\u914d\u7f6e\u9879\u3002\u4fee\u6539\u540e\u53ef\u80fd\u9700\u8981\u91cd\u542f\u6e38\u620f/\u670d\u52a1\u5668\u3002");
        enableHostileMobsOnMoon = getBoolean(
            "enableHostileMobsOnMoon",
            "mobs",
            true,
            "\u914d\u7f6e\u9879\u3002\u4fee\u6539\u540e\u53ef\u80fd\u9700\u8981\u91cd\u542f\u6e38\u620f/\u670d\u52a1\u5668\u3002");
        enableHostileMobsOnMars = getBoolean(
            "enableHostileMobsOnMars",
            "mobs",
            true,
            "\u914d\u7f6e\u9879\u3002\u4fee\u6539\u540e\u53ef\u80fd\u9700\u8981\u91cd\u542f\u6e38\u620f/\u670d\u52a1\u5668\u3002");
        enableHostileMobsOnMercury = getBoolean(
            "enableHostileMobsOnMercury",
            "mobs",
            true,
            "\u914d\u7f6e\u9879\u3002\u4fee\u6539\u540e\u53ef\u80fd\u9700\u8981\u91cd\u542f\u6e38\u620f/\u670d\u52a1\u5668\u3002");
        enableHostileMobsOnVenus = getBoolean(
            "enableHostileMobsOnVenus",
            "mobs",
            true,
            "\u914d\u7f6e\u9879\u3002\u4fee\u6539\u540e\u53ef\u80fd\u9700\u8981\u91cd\u542f\u6e38\u620f/\u670d\u52a1\u5668\u3002");
        enableHostileMobsOnGlacio = getBoolean(
            "enableHostileMobsOnGlacio",
            "mobs",
            true,
            "\u914d\u7f6e\u9879\u3002\u4fee\u6539\u540e\u53ef\u80fd\u9700\u8981\u91cd\u542f\u6e38\u620f/\u670d\u52a1\u5668\u3002");
        syncPlanetHostileMobOverrides();
        planetEntityCapPerType = getInt(
            "planetEntityCapPerType",
            "mobs",
            10,
            1,
            1000,
            "\u914d\u7f6e\u9879\u3002\u4fee\u6539\u540e\u53ef\u80fd\u9700\u8981\u91cd\u542f\u6e38\u620f/\u670d\u52a1\u5668\u3002");
        noOxygenEntityWhitelist = getStringList(
            "noOxygenEntityWhitelist",
            "mobs",
            new String[0],
            "\u914d\u7f6e\u9879\u3002\u4fee\u6539\u540e\u53ef\u80fd\u9700\u8981\u91cd\u542f\u6e38\u620f/\u670d\u52a1\u5668\u3002");

        // World Generation Configuration
        enableStructureGeneration = getBoolean(
            "enableStructureGeneration",
            "worldgen",
            true,
            "\u914d\u7f6e\u9879\u3002\u4fee\u6539\u540e\u53ef\u80fd\u9700\u8981\u91cd\u542f\u6e38\u620f/\u670d\u52a1\u5668\u3002");
        enableLunarVillages = getBoolean(
            "enableLunarVillages",
            "worldgen",
            true,
            "\u914d\u7f6e\u9879\u3002\u4fee\u6539\u540e\u53ef\u80fd\u9700\u8981\u91cd\u542f\u6e38\u620f/\u670d\u52a1\u5668\u3002");
        enableMarsOutposts = getBoolean(
            "enableMarsOutposts",
            "worldgen",
            true,
            "\u914d\u7f6e\u9879\u3002\u4fee\u6539\u540e\u53ef\u80fd\u9700\u8981\u91cd\u542f\u6e38\u620f/\u670d\u52a1\u5668\u3002");
        oreGenerationMultiplier = getFloat(
            "oreGenerationMultiplier",
            "worldgen",
            2.0f,
            0.0f,
            10.0f,
            "\u914d\u7f6e\u9879\u3002\u4fee\u6539\u540e\u53ef\u80fd\u9700\u8981\u91cd\u542f\u6e38\u620f/\u670d\u52a1\u5668\u3002");

        // Load external rows first so the shared override list has the final say.
        // Planet Tier Configuration
        ExternalDimensionConfig.sync();
        syncPlanetTierOverrides(configuration);

        // Debug Configuration
        debugOxygen = getBoolean(
            "debugOxygen",
            "debug",
            false,
            "\u914d\u7f6e\u9879\u3002\u4fee\u6539\u540e\u53ef\u80fd\u9700\u8981\u91cd\u542f\u6e38\u620f/\u670d\u52a1\u5668\u3002");
        debugTemperature = getBoolean(
            "debugTemperature",
            "debug",
            false,
            "\u914d\u7f6e\u9879\u3002\u4fee\u6539\u540e\u53ef\u80fd\u9700\u8981\u91cd\u542f\u6e38\u620f/\u670d\u52a1\u5668\u3002");
        debugGravity = getBoolean(
            "debugGravity",
            "debug",
            false,
            "\u914d\u7f6e\u9879\u3002\u4fee\u6539\u540e\u53ef\u80fd\u9700\u8981\u91cd\u542f\u6e38\u620f/\u670d\u52a1\u5668\u3002");

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
        dedicatedDimensionSaveFolderName = sanitizeSaveFolderSegment(dedicatedDimensionSaveFolderName, "AdAstraDimensions");

        // Mob validation
        if (planetMobSpawnRateMultiplier < 0.0f) planetMobSpawnRateMultiplier = 0.0f;
        if (planetMobSpawnRateMultiplier > 10.0f) planetMobSpawnRateMultiplier = 10.0f;
        if (planetEntityCapPerType < 1) planetEntityCapPerType = 1;

        // Worldgen validation
        if (oreGenerationMultiplier < 0.0f) oreGenerationMultiplier = 0.0f;
        if (oreGenerationMultiplier > 10.0f) oreGenerationMultiplier = 10.0f;

        // Sync ore generation configuration
        OreGenConfig.sync(worldgenConfiguration, legacyConfiguration);
        earth.terrarium.adastra.common.world.AdAstraChunkGenerator.clearCache();

        // Existing configs can contain entries from removed planets. Keep those values for compatibility,
        // but ensure every retained category and property has an explicit canonical bilingual comment.
        ensureCanonicalComments(legacyConfiguration);
        for (Configuration splitConfiguration : splitConfigurations) {
            ensureCanonicalComments(splitConfiguration);
        }

        // Configuration comments are metadata and Forge does not mark a property dirty when only its comment changes.
        // Save all split files after synchronization so existing installations receive updated bilingual comments.
        saveAllConfigurations();
    }

    public static void setRadioVolume(int volume) {
        radioVolume = clampInt(volume, 0, 100);
        Configuration target = configForCategory(Configuration.CATEGORY_GENERAL);
        if (target != null) {
            target.get(Configuration.CATEGORY_GENERAL, "radioVolume", 50, PROPERTY_COMMENTS.get("radioVolume")).set(radioVolume);
            saveChangedConfigurations();
        }
    }

    public static void setJetSuitEnabled(boolean enabled) {
        jetSuitEnabled = enabled;
        Configuration target = configForCategory("client");
        if (target != null) {
            target.get("client", "jetSuitEnabled", true, PROPERTY_COMMENTS.get("jetSuitEnabled")).set(jetSuitEnabled);
            saveChangedConfigurations();
        }
    }

    public static void setShowOxygenDistributorArea(boolean enabled) {
        showOxygenDistributorArea = enabled;
        Configuration target = configForCategory("client");
        if (target != null) {
            target.get("client", "showOxygenDistributorArea", false, PROPERTY_COMMENTS.get("showOxygenDistributorArea")).set(showOxygenDistributorArea);
            saveChangedConfigurations();
        }
    }

    public static void setShowGravityNormalizerArea(boolean enabled) {
        showGravityNormalizerArea = enabled;
        Configuration target = configForCategory("client");
        if (target != null) {
            target.get("client", "showGravityNormalizerArea", false, PROPERTY_COMMENTS.get("showGravityNormalizerArea")).set(showGravityNormalizerArea);
            saveChangedConfigurations();
        }
    }

    private static int clampInt(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    public static String getDimensionSaveFolder(String originalSaveFolder) {
        String dimensionFolder = sanitizeSaveFolderSegment(originalSaveFolder, "DIM_AD_ASTRA_UNKNOWN");
        if (!useDedicatedDimensionSaveFolder) {
            return dimensionFolder;
        }
        return sanitizeSaveFolderSegment(dedicatedDimensionSaveFolderName, "AdAstraDimensions") + "/" + dimensionFolder;
    }

    private static String sanitizeSaveFolderSegment(String value, String fallback) {
        String source = value == null ? "" : value.trim();
        if (source.isEmpty()) {
            return fallback;
        }
        StringBuilder builder = new StringBuilder(source.length());
        for (int i = 0; i < source.length(); i++) {
            char c = source.charAt(i);
            if ((c >= 'a' && c <= 'z')
                || (c >= 'A' && c <= 'Z')
                || (c >= '0' && c <= '9')
                || c == '_'
                || c == '-') {
                builder.append(c);
            } else {
                builder.append('_');
            }
        }
        String sanitized = builder.toString();
        return sanitized.isEmpty() ? fallback : sanitized;
    }

    private static Configuration configForCategory(String category) {
        if (Configuration.CATEGORY_GENERAL.equals(category)
            || "performance".equals(category)
            || "balance".equals(category)
            || "environment".equals(category)) {
            return coreConfiguration;
        }
        if ("client".equals(category)) {
            return clientConfiguration;
        }
        if ("machines".equals(category)) {
            return machinesConfiguration;
        }
        if ("dimensions".equals(category) || CATEGORY_PLANET_TIERS.equals(category)) {
            return dimensionsConfiguration;
        }
        if ("mobs".equals(category)) {
            return mobsConfiguration;
        }
        if ("worldgen".equals(category)
            || "worldgen_custom_blocks".equals(category)
            || category.startsWith("worldgen_")) {
            return worldgenConfiguration;
        }
        if ("debug".equals(category)) {
            return debugConfiguration;
        }
        return configuration == null ? coreConfiguration : configuration;
    }

    private static boolean legacyHas(String category, String key) {
        return legacyConfiguration != null && legacyConfiguration.hasKey(category, key);
    }

    private static boolean getBoolean(String key, String category, boolean defaultValue, String comment) {
        Configuration target = configForCategory(category);
        String resolvedComment = resolveComment(category, key, comment);
        boolean value = defaultValue;
        if (legacyHas(category, key)) {
            boolean legacyValue = legacyConfiguration.getBoolean(key, category, defaultValue, resolvedComment);
            if (target != null && !target.hasKey(category, key)) {
                value = legacyValue;
            }
        }
        return target.getBoolean(key, category, value, resolvedComment);
    }

    private static int getInt(String key, String category, int defaultValue, int minValue, int maxValue, String comment) {
        Configuration target = configForCategory(category);
        String resolvedComment = resolveComment(category, key, comment);
        int value = defaultValue;
        if (legacyHas(category, key)) {
            int legacyValue = legacyConfiguration.getInt(key, category, defaultValue, minValue, maxValue, resolvedComment);
            if (target != null && !target.hasKey(category, key)) {
                value = legacyValue;
            }
        }
        return target.getInt(key, category, value, minValue, maxValue, resolvedComment);
    }

    private static float getFloat(String key, String category, float defaultValue, float minValue, float maxValue, String comment) {
        Configuration target = configForCategory(category);
        String resolvedComment = resolveComment(category, key, comment);
        float value = defaultValue;
        if (legacyHas(category, key)) {
            float legacyValue = legacyConfiguration.getFloat(key, category, defaultValue, minValue, maxValue, resolvedComment);
            if (target != null && !target.hasKey(category, key)) {
                value = legacyValue;
            }
        }
        return target.getFloat(key, category, value, minValue, maxValue, resolvedComment);
    }

    private static String getString(String key, String category, String defaultValue, String comment) {
        Configuration target = configForCategory(category);
        String resolvedComment = resolveComment(category, key, comment);
        String value = defaultValue;
        if (legacyHas(category, key)) {
            String legacyValue = legacyConfiguration.getString(key, category, defaultValue, resolvedComment);
            if (target != null && !target.hasKey(category, key)) {
                value = legacyValue;
            }
        }
        return target.getString(key, category, value, resolvedComment);
    }

    private static String[] getStringList(String key, String category, String[] defaultValue, String comment) {
        Configuration target = configForCategory(category);
        String resolvedComment = resolveComment(category, key, comment);
        String[] value = defaultValue;
        if (legacyHas(category, key)) {
            String[] legacyValue = legacyConfiguration.getStringList(key, category, defaultValue, resolvedComment);
            if (target != null && !target.hasKey(category, key)) {
                value = legacyValue;
            }
        }
        return target.getStringList(key, category, value, resolvedComment);
    }

    private static String[] getStringListNoLegacy(String key, String category, String[] defaultValue, String comment) {
        Configuration target = configForCategory(category);
        return target.getStringList(key, category, defaultValue, resolveComment(category, key, comment));
    }

    private static Map<String, String> createPropertyComments() {
        Map<String, String> comments = new HashMap<>();
        addPropertyComment(comments, "debugLogging",
            "\u662f\u5426\u8f93\u51fa Ad Astra \u8c03\u8bd5\u65e5\u5fd7\u3002",
            "Whether to enable Ad Astra debug logging.");
        addPropertyComment(comments, "disableOxygen",
            "\u662f\u5426\u8df3\u8fc7\u5b9e\u4f53\u7684\u6c27\u6c14\u68c0\u67e5\u3001\u6c27\u6c14\u6d88\u8017\u548c\u7f3a\u6c27\u4f24\u5bb3\u3002\u542f\u7528\u540e\u8fd9\u4e9b\u5b9e\u4f53\u6c27\u6c14\u903b\u8f91\u4e0d\u4f1a\u6267\u884c\uff1b\u4e0d\u4f1a\u81ea\u52a8\u5173\u95ed\u65e0\u6c27\u73af\u5883\u65b9\u5757\u6548\u679c\u3002",
            "Whether to skip entity oxygen checks, oxygen consumption, and suffocation damage. When enabled, these entity oxygen paths do not run; it does not disable airless-environment block effects.");
        addPropertyComment(comments, "disableTemperature",
            "\u662f\u5426\u8df3\u8fc7\u5b9e\u4f53\u6e29\u5ea6\u4f24\u5bb3\u3002\u542f\u7528\u540e\u4e0d\u518d\u5bf9\u5b9e\u4f53\u5e94\u7528\u8fc7\u51b7\u6216\u8fc7\u70ed\u4f24\u5bb3\uff1b\u6e29\u5ea6\u8ba1\u7b97\u4e0e\u65e0\u6c27\u73af\u5883\u65b9\u5757\u6548\u679c\u4ecd\u53ef\u4f7f\u7528\u6e29\u5ea6\u503c\u3002",
            "Whether to skip temperature damage to entities. When enabled, freezing and burning damage is not applied to entities; temperature calculations and airless-environment block effects may still use the temperature value.");
        addPropertyComment(comments, "disableGravity",
            "\u662f\u5426\u8df3\u8fc7\u5bf9\u5b9e\u4f53\u5e94\u7528\u91cd\u529b\u8fd0\u52a8\u3001\u8df3\u8dc3\u548c\u6454\u843d\u4f24\u5bb3\u5904\u7406\u3002\u542f\u7528\u540e\u91cd\u529b\u4e8b\u4ef6\u5904\u7406\u4f1a\u88ab\u8df3\u8fc7\uff1b\u4e0d\u4f1a\u4fee\u6539\u884c\u661f\u5c5e\u6027\u6216\u5176\u4ed6\u73af\u5883\u903b\u8f91\u4f7f\u7528\u7684\u91cd\u529b\u503c\u3002",
            "Whether to skip applying gravity movement, jumping, and fall-damage handling to entities. When enabled, gravity event handling is skipped; it does not change planet properties or gravity values used by other environment logic.");
        addPropertyComment(comments, "enableAirVortexes",
            "\u662f\u5426\u5141\u8bb8\u6c27\u6c14\u5206\u914d\u5668\u5728\u8fbe\u5230\u8986\u76d6\u4e0a\u9650\u65f6\u751f\u6210\u7a7a\u6c14\u65cb\u6da1\u5b9e\u4f53\u3002",
            "Whether oxygen distributors may spawn air-vortex entities when their coverage reaches the limit.");
        addPropertyComment(comments, "allowFlagImages",
            "\u662f\u5426\u5141\u8bb8\u65d7\u5e1c\u4f7f\u7528\u7f51\u7edc\u56fe\u7247\u3002\u5173\u95ed\u53ef\u907f\u514d\u52a0\u8f7d\u5916\u90e8\u56fe\u7247\u3002",
            "Whether flags may use network images. Disable this to prevent loading external images.");
        addPropertyComment(comments, "radioVolume",
            "\u6536\u97f3\u673a\u97f3\u91cf\uff0c\u8303\u56f4\u4e3a 0-100\u3002",
            "Radio volume, from 0 to 100.");
        addPropertyComment(comments, "spaceMuffler",
            "\u5ba2\u6237\u7aef\u662f\u5426\u964d\u4f4e\u592a\u7a7a\u73af\u5883\u4e2d\u7684\u58f0\u97f3\u3002",
            "Whether the client muffles sounds in space environments.");
        addPropertyComment(comments, "jetSuitEnabled",
            "\u5ba2\u6237\u7aef\u662f\u5426\u542f\u7528\u55b7\u6c14\u80cc\u5305\u3002",
            "Whether the jet suit is enabled on the client.");
        addPropertyComment(comments, "showOxygenDistributorArea",
            "\u662f\u5426\u663e\u793a\u6c27\u6c14\u5206\u914d\u5668\u7684\u8986\u76d6\u8303\u56f4\u3002",
            "Whether to show the oxygen distributor coverage area.");
        addPropertyComment(comments, "showGravityNormalizerArea",
            "\u662f\u5426\u663e\u793a\u91cd\u529b\u6b63\u5219\u5316\u5668\u7684\u8986\u76d6\u8303\u56f4\u3002",
            "Whether to show the gravity normalizer coverage area.");
        addPropertyComment(comments, "oxygenBarX",
            "\u5ba2\u6237\u7aef\u6c27\u6c14\u6761\u7684\u5c4f\u5e55 X \u5750\u6807\u3002",
            "Client screen X position of the oxygen bar.");
        addPropertyComment(comments, "oxygenBarY",
            "\u5ba2\u6237\u7aef\u6c27\u6c14\u6761\u7684\u5c4f\u5e55 Y \u5750\u6807\u3002",
            "Client screen Y position of the oxygen bar.");
        addPropertyComment(comments, "oxygenBarScale",
            "\u5ba2\u6237\u7aef\u6c27\u6c14\u6761\u7684\u663e\u793a\u7f29\u653e\u6bd4\u4f8b\u3002",
            "Client display scale of the oxygen bar.");
        addPropertyComment(comments, "energyBarX",
            "\u517c\u5bb9\u6027\u4fdd\u7559\u914d\u7f6e\u9879\uff1b\u5f53\u524d\u7248\u672c\u6ca1\u6709\u80fd\u91cf\u6761 HUD\uff0c\u4e0d\u8bfb\u53d6\u6b64 X \u5750\u6807\u3002",
            "Compatibility setting retained for existing configs; the current version has no energy-bar HUD and does not read this X position.");
        addPropertyComment(comments, "energyBarY",
            "\u517c\u5bb9\u6027\u4fdd\u7559\u914d\u7f6e\u9879\uff1b\u5f53\u524d\u7248\u672c\u6ca1\u6709\u80fd\u91cf\u6761 HUD\uff0c\u4e0d\u8bfb\u53d6\u6b64 Y \u5750\u6807\u3002",
            "Compatibility setting retained for existing configs; the current version has no energy-bar HUD and does not read this Y position.");
        addPropertyComment(comments, "energyBarScale",
            "\u517c\u5bb9\u6027\u4fdd\u7559\u914d\u7f6e\u9879\uff1b\u5f53\u524d\u7248\u672c\u6ca1\u6709\u80fd\u91cf\u6761 HUD\uff0c\u4e0d\u8bfb\u53d6\u6b64\u7f29\u653e\u6bd4\u4f8b\u3002",
            "Compatibility setting retained for existing configs; the current version has no energy-bar HUD and does not read this scale.");
        addPropertyComment(comments, "oxygenDamageAmount",
            "\u7f3a\u6c27\u4f24\u5bb3\u6bcf\u6b21\u5e94\u7528\u7684\u4f24\u5bb3\u91cf\u3002",
            "Amount of damage applied on each oxygen-damage event.");
        addPropertyComment(comments, "oxygenDamageInterval",
            "\u7f3a\u6c27\u4f24\u5bb3\u7684\u95f4\u9694\u65f6\u95f4\uff0c\u5355\u4f4d\u4e3a\u523b\u3002",
            "Interval between oxygen-damage events, in ticks.");
        addPropertyComment(comments, "oxygenConsumptionInterval",
            "\u6c27\u6c14\u6d88\u8017\u7684\u95f4\u9694\u65f6\u95f4\uff0c\u5355\u4f4d\u4e3a\u523b\u3002",
            "Interval between oxygen-consumption events, in ticks.");
        addPropertyComment(comments, "oxygenConsumptionAmount",
            "\u6bcf\u6b21\u6c27\u6c14\u6d88\u8017\u91cf\uff0c\u5355\u4f4d\u4e3a mB\u3002",
            "Amount of oxygen consumed per event, in mB.");
        addPropertyComment(comments, "enableSpaceEnvironmentEffects",
            "\u662f\u5426\u542f\u7528\u65e0\u6c27\u884c\u661f\u73af\u5883\u7684\u65b9\u5757\u6548\u679c\uff0c\u5305\u62ec\u6c34\u84b8\u53d1\u3001\u7ed3\u51b0\u3001\u6d41\u4f53\u6e90\u9650\u5236\u3001\u51b0\u5757\u7834\u574f\u4ee5\u53ca\u90e8\u5206\u65b9\u5757\u53d8\u5316\u3002\u4e0d\u63a7\u5236\u5b9e\u4f53\u7684\u6c27\u6c14\u6216\u6e29\u5ea6\u4f24\u5bb3\u3002",
            "Whether to enable airless-planet block effects, including water evaporation, freezing, fluid-source restrictions, ice breaking, and selected block changes. This does not control entity oxygen or temperature damage.");
        addPropertyComment(comments, "planetRandomTickSpeed",
            "\u65e0\u6c27\u884c\u661f\u73af\u5883\u5904\u7406\u65f6\uff0c\u6bcf\u4e2a\u88ab\u9009\u4e2d\u533a\u5757\u8fdb\u884c\u7684\u968f\u673a\u65b9\u5757\u68c0\u67e5\u6b21\u6570\uff0c\u4e0d\u662f Minecraft \u539f\u7248 randomTickSpeed\u3002\u8bbe\u4e3a 0 \u53ef\u7981\u7528\u8fd9\u4e9b\u68c0\u67e5\u3002",
            "Number of random block checks per selected chunk during airless-environment processing; this is not Minecraft's vanilla randomTickSpeed. Set to 0 to disable these checks.");
        addPropertyComment(comments, "maxOxygenDistributorRadius",
            "\u517c\u5bb9\u6027\u4fdd\u7559\u914d\u7f6e\u9879\uff1b\u5f53\u524d\u7248\u672c\u4e0d\u8bfb\u53d6\u6b64\u534a\u5f84\u914d\u7f6e\u3002\u6c27\u6c14\u5206\u914d\u5668\u7684\u8986\u76d6\u4e0a\u9650\u7531 maxDistributionBlocks\uff08\u65b9\u5757\u6570\uff09\u63a7\u5236\u3002\u8303\u56f4\uff1a1-100\u3002",
            "Compatibility setting retained for existing configs; the current version does not read this radius. Oxygen-distributor coverage is limited by maxDistributionBlocks (block count). Range: 1-100.");
        addPropertyComment(comments, "oxygenScanRadius",
            "\u517c\u5bb9\u6027\u4fdd\u7559\u914d\u7f6e\u9879\uff1b\u5f53\u524d\u7248\u672c\u4e0d\u8bfb\u53d6\u6b64\u534a\u5f84\u914d\u7f6e\u3002\u5bc6\u5c01\u623f\u95f4 BFS \u626b\u63cf\u7684\u65b9\u5757\u4e0a\u9650\u7531 maxDistributionBlocks \u63a7\u5236\uff0c\u800c\u4e0d\u662f\u534a\u5f84\u3002\u8303\u56f4\uff1a1-64\u3002",
            "Compatibility setting retained for existing configs; the current version does not read this radius. Sealed-room BFS is limited by maxDistributionBlocks, not by a radius. Range: 1-64.");
        addPropertyComment(comments, "enableMachineIdleOptimization",
            "\u662f\u5426\u4f18\u5316\u95f2\u7f6e\u673a\u5668\u3002\u542f\u7528\u540e\u53ef\u51cf\u5c11\u4e0d\u5fc5\u8981\u7684\u5904\u7406\u3002",
            "Whether to optimize idle machines to reduce unnecessary processing.");
        addPropertyComment(comments, "machineTransferInterval",
            "\u673a\u5668\u68c0\u67e5\u7269\u54c1\u3001\u80fd\u91cf\u548c\u6d41\u4f53\u8f93\u5165\u8f93\u51fa\u7684\u95f4\u9694\uff0c\u5355\u4f4d\u4e3a\u523b\u3002",
            "Interval between machine item, energy, and fluid transfer checks, in ticks.");
        addPropertyComment(comments, "sealedRoomCacheLifetime",
            "\u5bc6\u5c01\u623f\u95f4\u7f13\u5b58\u4e0d\u6309\u65f6\u95f4\u8fc7\u671f\uff1b\u5f53\u524d\u7248\u672c\u4ec5\u5728\u65b9\u5757\u653e\u7f6e\u6216\u7834\u574f\u65f6\u4f7f\u7f13\u5b58\u5931\u6548\u3002\u6b64\u914d\u7f6e\u9879\u5f53\u524d\u4e0d\u8d77\u4f5c\u7528\u3002\u8303\u56f4\uff1a20-200 \u523b\u3002",
            "Sealed-room caches do not expire by time in the current version; they are invalidated only when blocks are placed or broken. This option currently has no effect. Range: 20-200 ticks.");
        addPropertyComment(comments, "gravityNormalizerEnergyMultiplier",
            "\u91cd\u529b\u6b63\u5219\u5316\u5668\u80fd\u91cf\u6d88\u8017\u500d\u7387\u3002\u503c\u8d8a\u5927\u6d88\u8017\u8d8a\u9ad8\u3002",
            "Energy-consumption multiplier for gravity normalizers. Higher values consume more energy.");
        addPropertyComment(comments, "sealedRoomMaxBlocks",
            "\u517c\u5bb9\u6027\u4fdd\u7559\u914d\u7f6e\u9879\uff1b\u5f53\u524d\u5bc6\u5c01\u623f\u95f4 BFS \u4f7f\u7528 maxDistributionBlocks \u9650\u5236\u68c0\u67e5\u65b9\u5757\u6570\uff0c\u4e0d\u8bfb\u53d6\u6b64\u503c\u3002\u8303\u56f4\uff1a100-50000\u3002",
            "Compatibility setting retained for existing configs; sealed-room BFS currently uses maxDistributionBlocks as its block limit and does not read this value. Range: 100-50000.");
        addPropertyComment(comments, "machineProcessingSpeedMultiplier",
            "\u673a\u5668\u5904\u7406\u901f\u5ea6\u500d\u7387\u3002\u503c\u8d8a\u5927\u5904\u7406\u8d8a\u5feb\u3002",
            "Machine processing-speed multiplier. Higher values process recipes faster.");
        addPropertyComment(comments, "machineEnergyConsumptionMultiplier",
            "\u673a\u5668\u80fd\u91cf\u6d88\u8017\u500d\u7387\u3002\u503c\u8d8a\u5927\u6d88\u8017\u8d8a\u9ad8\u3002",
            "Machine energy-consumption multiplier. Higher values consume more energy.");
        addPropertyComment(comments, "coalGeneratorEnergyMultiplier",
            "\u7164\u70ad\u53d1\u7535\u673a\u4ea7\u80fd\u500d\u7387\u3002",
            "Coal-generator energy-generation multiplier.");
        addPropertyComment(comments, "solarPanelEnergyMultiplier",
            "\u592a\u9633\u80fd\u7535\u6c60\u4ea7\u80fd\u500d\u7387\u3002",
            "Solar-panel energy-generation multiplier.");
        addPropertyComment(comments, "compressorBaseTime",
            "\u517c\u5bb9\u6027\u4fdd\u7559\u914d\u7f6e\u9879\uff1b\u5f53\u524d\u538b\u7f29\u673a\u4f7f\u7528\u914d\u65b9\u81ea\u8eab\u7684\u5904\u7406\u65f6\u95f4\u4e0e\u7edf\u4e00\u5904\u7406\u901f\u5ea6\u500d\u7387\uff0c\u4e0d\u8bfb\u53d6\u6b64\u503c\u3002\u8303\u56f4\uff1a10-1000 \u523b\u3002",
            "Compatibility setting retained for existing configs; the compressor uses each recipe's processing time and the shared processing-speed multiplier, not this value. Range: 10-1000 ticks.");
        addPropertyComment(comments, "cryoFreezerBaseTime",
            "\u517c\u5bb9\u6027\u4fdd\u7559\u914d\u7f6e\u9879\uff1b\u5f53\u524d\u4f4e\u6e29\u51b7\u51bb\u673a\u4f7f\u7528\u914d\u65b9\u81ea\u8eab\u7684\u5904\u7406\u65f6\u95f4\u4e0e\u7edf\u4e00\u5904\u7406\u901f\u5ea6\u500d\u7387\uff0c\u4e0d\u8bfb\u53d6\u6b64\u503c\u3002\u8303\u56f4\uff1a10-1000 \u523b\u3002",
            "Compatibility setting retained for existing configs; the cryogenic freezer uses each recipe's processing time and the shared processing-speed multiplier, not this value. Range: 10-1000 ticks.");
        addPropertyComment(comments, "etrionicBlastFurnaceBaseTime",
            "\u517c\u5bb9\u6027\u4fdd\u7559\u914d\u7f6e\u9879\uff1b\u5f53\u524d Etrionic \u7535\u529b\u9ad8\u7089\u4f7f\u7528\u914d\u65b9\u5904\u7406\u65f6\u95f4\u6216\u51b6\u70bc\u914d\u65b9\u5904\u7406\u65f6\u95f4\u4e0e\u7edf\u4e00\u5904\u7406\u901f\u5ea6\u500d\u7387\uff0c\u4e0d\u8bfb\u53d6\u6b64\u503c\u3002\u8303\u56f4\uff1a10-1000 \u523b\u3002",
            "Compatibility setting retained for existing configs; the Etrionic blast furnace uses recipe processing times or blasting-recipe processing times with the shared processing-speed multiplier, not this value. Range: 10-1000 ticks.");
        addPropertyComment(comments, "fuelRefineryBaseTime",
            "\u517c\u5bb9\u6027\u4fdd\u7559\u914d\u7f6e\u9879\uff1b\u5f53\u524d\u71c3\u6599\u7cbe\u70bc\u5382\u6309\u6bcf\u6b21\u64cd\u4f5c\u5904\u7406\u6d41\u4f53\uff0c\u4e0d\u8bfb\u53d6\u6b64\u57fa\u7840\u65f6\u95f4\u3002\u8303\u56f4\uff1a10-1000 \u523b\u3002",
            "Compatibility setting retained for existing configs; the fuel refinery processes a fixed amount per operation and does not read this base-time value. Range: 10-1000 ticks.");
        addPropertyComment(comments, "ironMaxEnergyInOut",
            "\u94c1\u7ea7\u6bcf\u523b\u6700\u5927 FE \u8f93\u5165/\u8f93\u51fa\u91cf\u3002",
            "Iron tier max FE input/output per tick.");
        addPropertyComment(comments, "ironEnergyCapacity",
            "\u94c1\u7ea7\u5185\u90e8 FE \u5bb9\u91cf\u3002",
            "Iron tier internal FE capacity.");
        addPropertyComment(comments, "ironFluidCapacity",
            "\u517c\u5bb9\u6027\u4fdd\u7559\u914d\u7f6e\u9879\uff1b\u5f53\u524d\u7248\u672c\u6ca1\u6709\u4f7f\u7528\u94c1\u7ea7\u6d41\u4f53\u7f50\u7684\u673a\u5668\uff0c\u4e0d\u8bfb\u53d6\u6b64\u503c\u3002\u5355\u4f4d\uff1amB\u3002",
            "Compatibility setting retained for existing configs; the current version has no machine using an iron-tier fluid tank and does not read this value. Unit: mB.");
        addPropertyComment(comments, "steelMaxEnergyInOut",
            "\u94a2\u7ea7\u6bcf\u523b\u6700\u5927 FE \u8f93\u5165/\u8f93\u51fa\u91cf\u3002",
            "Steel tier max FE input/output per tick.");
        addPropertyComment(comments, "steelEnergyCapacity",
            "\u94a2\u7ea7\u5185\u90e8 FE \u5bb9\u91cf\u3002",
            "Steel tier internal FE capacity.");
        addPropertyComment(comments, "steelFluidCapacity",
            "\u94a2\u7ea7\u5185\u90e8\u6d41\u4f53\u5bb9\u91cf\uff0c\u5355\u4f4d\uff1amB\u3002",
            "Steel tier internal fluid capacity in mB.");
        addPropertyComment(comments, "deshMaxEnergyInOut",
            "Desh \u7ea7\u6bcf\u523b\u6700\u5927 FE \u8f93\u5165/\u8f93\u51fa\u91cf\u3002",
            "Desh tier max FE input/output per tick.");
        addPropertyComment(comments, "deshEnergyCapacity",
            "Desh \u7ea7\u5185\u90e8 FE \u5bb9\u91cf\u3002",
            "Desh tier internal FE capacity.");
        addPropertyComment(comments, "deshFluidCapacity",
            "Desh \u7ea7\u5185\u90e8\u6d41\u4f53\u5bb9\u91cf\uff0c\u5355\u4f4d\uff1amB\u3002",
            "Desh tier internal fluid capacity in mB.");
        addPropertyComment(comments, "ostrumMaxEnergyInOut",
            "Ostrum \u7ea7\u6bcf\u523b\u6700\u5927 FE \u8f93\u5165/\u8f93\u51fa\u91cf\u3002",
            "Ostrum tier max FE input/output per tick.");
        addPropertyComment(comments, "ostrumEnergyCapacity",
            "Ostrum \u7ea7\u5185\u90e8 FE \u5bb9\u91cf\u3002",
            "Ostrum tier internal FE capacity.");
        addPropertyComment(comments, "ostrumFluidCapacity",
            "Ostrum \u7ea7\u5185\u90e8\u6d41\u4f53\u5bb9\u91cf\uff0c\u5355\u4f4d\uff1amB\u3002",
            "Ostrum tier internal fluid capacity in mB.");
        addPropertyComment(comments, "coalGeneratorEnergyGenerationPerTick",
            "\u7164\u70ad\u53d1\u7535\u673a\u6bcf\u523b\u4ea7\u751f\u7684 FE\uff0c\u672a\u8ba1\u5165\u500d\u7387\u3002",
            "Coal generator FE generated per tick before multipliers.");
        addPropertyComment(comments, "etrionicBlastFurnaceBlastingEnergyPerItem",
            "Etrionic \u7535\u529b\u9ad8\u7089\u5185\u7f6e\u51b6\u70bc\u914d\u65b9\u7684\u57fa\u7840\u6bcf\u523b FE \u6d88\u8017\uff1b\u5b9e\u9645\u6d88\u8017\u8fd8\u4f1a\u5e94\u7528\u673a\u5668\u80fd\u8017\u500d\u7387\u3002",
            "Base FE consumed per tick by the Etrionic blast furnace's built-in blasting recipes; the machine energy-consumption multiplier is applied to the actual cost.");
        addPropertyComment(comments, "waterPumpEnergyPerTick",
            "\u6c34\u6cf5\u6bcf\u523b\u6d88\u8017\u7684 FE\u3002",
            "FE consumed by the water pump per tick.");
        addPropertyComment(comments, "waterPumpFluidGenerationPerTick",
            "\u6c34\u6cf5\u6bcf\u523b\u751f\u6210\u7684\u6c34\u91cf\uff0c\u5355\u4f4d\uff1amB\u3002",
            "Water generated by the water pump per tick in mB.");
        addPropertyComment(comments, "energizerEnergyCapacity",
            "\u80fd\u91cf\u6fc0\u53d1\u5668\u5185\u90e8 FE \u5bb9\u91cf\u3002",
            "Energizer internal FE capacity.");
        addPropertyComment(comments, "maxDistributionBlocks",
            "\u6c27\u6c14\u5206\u914d\u5668\u4e0e\u91cd\u529b\u6b63\u5219\u5316\u5668\u6bcf\u4e2a\u8bbe\u5907\u53ef\u8986\u76d6\u7684\u6700\u5927\u65b9\u5757\u6570\u3002",
            "Maximum number of blocks covered by each oxygen distributor or gravity normalizer.");
        addPropertyComment(comments, "distributionRefreshRate",
            "\u6c27\u6c14\u5206\u914d\u5668\u4e0e\u91cd\u529b\u6b63\u5219\u5316\u5668\u8986\u76d6\u8303\u56f4\u7684\u5237\u65b0\u95f4\u9694\uff0c\u5355\u4f4d\u4e3a\u523b\u3002",
            "Refresh interval in ticks for distributor and gravity normalizer coverage.");
        addPropertyComment(comments, "pipeRefreshRate",
            "\u7ba1\u9053\u68c0\u67e5\u90bb\u8fd1\u7ba1\u9053\u4ee5\u5bfb\u627e\u4f20\u8f93\u673a\u4f1a\u7684\u95f4\u9694\uff0c\u5355\u4f4d\u4e3a\u523b\u3002",
            "How often idle pipes probe neighbors for new transfer opportunities, in ticks.");
        addPropertyComment(comments, "temperatureDamageMultiplier",
            "\u517c\u5bb9\u6027/API \u914d\u7f6e\u9879\uff1b\u5f53\u524d TemperatureSystem \u4f7f\u7528\u56fa\u5b9a\u7684\u51bb\u7ed3\u4e0e\u707c\u70ed\u4f24\u5bb3\u503c\uff0c\u4e0d\u8bfb\u53d6\u6b64\u5b57\u6bb5\u3002\u4ec5\u5728\u8c03\u7528 getModifiedTemperatureDamage \u65f6\u4f5c\u4e3a\u500d\u6570\u4f7f\u7528\u3002\u8303\u56f4\uff1a0.0-10.0\u3002",
            "Compatibility/API setting; the current TemperatureSystem uses fixed freezing and burning damage values and does not read this field. The multiplier is used only when getModifiedTemperatureDamage is called. Range: 0.0-10.0.");
        addPropertyComment(comments, "freezeDamageInterval",
            "\u5f53\u524d\u7248\u672c\u672a\u4f7f\u7528\u6b64\u503c\u3002\u6e29\u5ea6\u4f24\u5bb3\u68c0\u6d4b\u95f4\u9694\u7531 CommonEventHandler \u56fa\u5b9a\u4e3a 40 \u523b\u3002\u8303\u56f4\uff1a1-200 \u523b\u3002",
            "Unused by the current version. Temperature-damage checks are fixed at 40 ticks by CommonEventHandler. Range: 1-200 ticks.");
        addPropertyComment(comments, "burnDamageInterval",
            "\u5f53\u524d\u7248\u672c\u672a\u4f7f\u7528\u6b64\u503c\u3002\u6e29\u5ea6\u4f24\u5bb3\u68c0\u6d4b\u95f4\u9694\u7531 CommonEventHandler \u56fa\u5b9a\u4e3a 40 \u523b\u3002\u8303\u56f4\uff1a1-200 \u523b\u3002",
            "Unused by the current version. Temperature-damage checks are fixed at 40 ticks by CommonEventHandler. Range: 1-200 ticks.");
        addPropertyComment(comments, "freezeDamageAmount",
            "\u5f53\u524d\u7248\u672c\u672a\u4f7f\u7528\u6b64\u503c\uff1b\u5b9e\u9645\u51bb\u7ed3\u4f24\u5bb3\u56fa\u5b9a\u4e3a 3.0 \u70b9 Minecraft \u4f24\u5bb3\u3002\u8303\u56f4\uff1a0.0-20.0\u3002",
            "Unused by the current version; actual freezing damage is fixed at 3.0 Minecraft damage points. Range: 0.0-20.0.");
        addPropertyComment(comments, "burnDamageAmount",
            "\u5f53\u524d\u7248\u672c\u672a\u4f7f\u7528\u6b64\u503c\uff1b\u5b9e\u9645\u707c\u70ed\u4f24\u5bb3\u56fa\u5b9a\u4e3a 6.0 \u70b9 Minecraft \u4f24\u5bb3\u3002\u8303\u56f4\uff1a0.0-20.0\u3002",
            "Unused by the current version; actual burning damage is fixed at 6.0 Minecraft damage points. Range: 0.0-20.0.");
        addPropertyComment(comments, "gravityMultiplier",
            "\u5168\u5c40\u91cd\u529b\u500d\u6570\uff1b\u5f53\u524d\u5185\u7f6e\u4e16\u754c\u7269\u7406\u4e0d\u8bfb\u53d6\u6b64\u5b57\u6bb5\uff0c\u53ea\u6709 getGravityMultiplierForDimension API \u4f1a\u5c06\u5b83\u4f5c\u4e3a\u57fa\u7840\u500d\u6570\u3002\u8303\u56f4\uff1a0.0-5.0\u3002",
            "Global gravity multiplier; current built-in world physics does not read this field. It is used as the base multiplier only by the getGravityMultiplierForDimension API. Range: 0.0-5.0.");
        addPropertyComment(comments, "enableFallDamageInLowGravity",
            "\u5f53\u524d\u6454\u843d\u4f24\u5bb3\u903b\u8f91\u672a\u8bfb\u53d6\u6b64\u5f00\u5173\uff1b\u4f4e\u91cd\u529b\u89c4\u5219\u59cb\u7ec8\u6309\u5b9e\u9645\u91cd\u529b\u503c\u6267\u884c\u3002",
            "The current fall-damage logic does not read this toggle; the low-gravity rule is always based on the effective gravity value.");
        addPropertyComment(comments, "lowGravityFallDamageThreshold",
            "\u5f53\u524d\u6454\u843d\u4f24\u5bb3\u903b\u8f91\u672a\u8bfb\u53d6\u6b64\u9608\u503c\uff1b\u5b9e\u9645\u4f4e\u91cd\u529b\u9608\u503c\u56fa\u5b9a\u4e3a 0.3\uff0c\u4f4e\u4e8e\u9608\u503c\u65f6\u77ed\u6682\u6454\u843d\u4f24\u5bb3\u53ef\u88ab\u53d6\u6d88\u3002\u8303\u56f4\uff1a0.0-1.0\u3002",
            "The current fall-damage logic does not read this threshold; the effective low-gravity threshold is fixed at 0.3, below which short-fall damage may be canceled. Range: 0.0-1.0.");
        addPropertyComment(comments, "planetMobSpawnRateMultiplier",
            "\u5f53\u524d\u7248\u672c\u5c06\u6b64\u503c\u4f5c\u4e3a\u5168\u5c40\u654c\u5bf9\u751f\u7269\u751f\u6210\u5f00\u5173\uff1a\u503c\u5c0f\u4e8e\u7b49\u4e8e 0 \u65f6\u7981\u7528\u654c\u5bf9\u751f\u7269\u751f\u6210\uff0c\u6b63\u6570\u4e0d\u4f1a\u6309\u6bd4\u4f8b\u8c03\u6574\u751f\u6210\u7387\u3002\u8303\u56f4\uff1a0.0-10.0\u3002",
            "The current version uses this value as a global hostile-mob spawn switch: values less than or equal to 0 disable hostile-mob spawning, while positive values do not scale the spawn rate. Range: 0.0-10.0.");
        addPropertyComment(comments, "enableHostileMobsOnMoon",
            "\u662f\u5426\u5728\u6708\u7403\u751f\u6210\u654c\u5bf9\u751f\u7269\u3002",
            "Whether hostile mobs spawn on the Moon.");
        addPropertyComment(comments, "enableHostileMobsOnMars",
            "\u662f\u5426\u5728\u706b\u661f\u751f\u6210\u654c\u5bf9\u751f\u7269\u3002",
            "Whether hostile mobs spawn on Mars.");
        addPropertyComment(comments, "enableHostileMobsOnMercury",
            "\u662f\u5426\u5728\u6c34\u661f\u751f\u6210\u654c\u5bf9\u751f\u7269\u3002",
            "Whether hostile mobs spawn on Mercury.");
        addPropertyComment(comments, "enableHostileMobsOnVenus",
            "\u662f\u5426\u5728\u91d1\u661f\u751f\u6210\u654c\u5bf9\u751f\u7269\u3002",
            "Whether hostile mobs spawn on Venus.");
        addPropertyComment(comments, "enableHostileMobsOnGlacio",
            "\u662f\u5426\u5728\u51b0\u5ddd\u661f\u751f\u6210\u654c\u5bf9\u751f\u7269\u3002",
            "Whether hostile mobs spawn on Glacio.");
        addPropertyComment(comments, "planetEntityCapPerType",
            "\u884c\u661f\u7ef4\u5ea6\u4e2d\u6bcf\u79cd\u5b9e\u4f53\u7684\u6700\u5927\u6570\u91cf\u3002",
            "Maximum number of entities of each type in planet dimensions.");
        addPropertyComment(comments, "noOxygenEntityWhitelist",
            "\u4e0d\u9700\u8981\u6c27\u6c14\u73af\u5883\u7684\u5b9e\u4f53\u767d\u540d\u5355\uff0c\u6bcf\u884c\u4e00\u9879\u3002\u652f\u6301 modid:entity_id\u3001modid:*\u3001\u5b8c\u6574 Java \u7c7b\u540d\u6216\u7b80\u5355\u7c7b\u540d\u3002",
            "Entities that do not require oxygen; enter one entry per line. Supported forms: modid:entity_id, modid:*, a full Java class name, or a simple class name.");
        addPropertyComment(comments, "enableStructureGeneration",
            "\u5f53\u524d\u7248\u672c\u4ec5\u4f7f\u7528\u81ea\u5b9a\u4e49\u884c\u661f\u5730\u5f62\u4e0e\u77ff\u8109\u751f\u6210\uff0c\u672a\u63a5\u5165\u7ed3\u6784\u751f\u6210\u903b\u8f91\uff1b\u6b64\u914d\u7f6e\u9879\u5f53\u524d\u4e0d\u8d77\u4f5c\u7528\u3002",
            "The current version uses custom planet terrain and ore generation but has no structure-generation hook; this option currently has no effect.");
        addPropertyComment(comments, "enableLunarVillages",
            "\u5f53\u524d\u7248\u672c\u672a\u63a5\u5165\u6708\u7403\u6751\u5e84\u751f\u6210\u903b\u8f91\uff1b\u6b64\u914d\u7f6e\u9879\u5f53\u524d\u4e0d\u8d77\u4f5c\u7528\u3002",
            "The current version has no lunar-village generation hook; this option currently has no effect.");
        addPropertyComment(comments, "enableMarsOutposts",
            "\u5f53\u524d\u7248\u672c\u672a\u63a5\u5165\u706b\u661f\u524d\u54e8\u7ad9\u751f\u6210\u903b\u8f91\uff1b\u6b64\u914d\u7f6e\u9879\u5f53\u524d\u4e0d\u8d77\u4f5c\u7528\u3002",
            "The current version has no Mars-outpost generation hook; this option currently has no effect.");
        addPropertyComment(comments, "oreGenerationMultiplier",
            "\u5185\u7f6e\u884c\u661f\u4e0e worldgen_custom_blocks \u4e2d\u751f\u6210\u5668\u7684\u77ff\u8109\u751f\u6210\u5c1d\u8bd5\u6b21\u6570\u500d\u7387\u3002\u503c\u8d8a\u9ad8\u65f6\u6bcf\u533a\u5757\u5c1d\u8bd5\u6b21\u6570\u8d8a\u591a\uff1b\u4e0d\u5f71\u54cd CustomPlanetDefinition \u76f4\u63a5\u58f0\u660e\u7684\u77ff\u8109\uff0c\u4e14\u6bcf\u6b21\u5c1d\u8bd5\u4e0d\u4fdd\u8bc1\u6210\u529f\u653e\u7f6e\u3002",
            "Multiplier for ore-generation attempts from built-in planet generators and worldgen_custom_blocks entries. Higher values create more attempts per chunk; it does not affect ores declared directly by CustomPlanetDefinition, and an attempt is not guaranteed to place a vein.");
        addPropertyComment(comments, "debugWorldgen",
            "\u662f\u5426\u542f\u7528\u77ff\u8109\u751f\u6210\u8c03\u8bd5\u65e5\u5fd7\u3002",
            "Whether to enable debug logging for ore generation.");
        addPropertyComment(comments, "debugOxygen",
            "\u517c\u5bb9\u6027\u4fdd\u7559\u8c03\u8bd5\u5f00\u5173\uff1b\u5f53\u524d\u7248\u672c\u6c27\u6c14\u7cfb\u7edf\u6ca1\u6709\u8bfb\u53d6\u6b64\u503c\uff0c\u4e0d\u4f1a\u8f93\u51fa\u989d\u5916\u65e5\u5fd7\u3002",
            "Compatibility debug toggle; the current oxygen system does not read this value and does not emit extra logs from it.");
        addPropertyComment(comments, "debugTemperature",
            "\u517c\u5bb9\u6027\u4fdd\u7559\u8c03\u8bd5\u5f00\u5173\uff1b\u5f53\u524d\u7248\u672c\u6e29\u5ea6\u7cfb\u7edf\u6ca1\u6709\u8bfb\u53d6\u6b64\u503c\uff0c\u4e0d\u4f1a\u8f93\u51fa\u989d\u5916\u65e5\u5fd7\u3002",
            "Compatibility debug toggle; the current temperature system does not read this value and does not emit extra logs from it.");
        addPropertyComment(comments, "debugGravity",
            "\u517c\u5bb9\u6027\u4fdd\u7559\u8c03\u8bd5\u5f00\u5173\uff1b\u5f53\u524d\u7248\u672c\u91cd\u529b\u7cfb\u7edf\u6ca1\u6709\u8bfb\u53d6\u6b64\u503c\uff0c\u4e0d\u4f1a\u8f93\u51fa\u989d\u5916\u65e5\u5fd7\u3002",
            "Compatibility debug toggle; the current gravity system does not read this value and does not emit extra logs from it.");
        addPropertyComment(comments, "customDimensionEnabledOverrides",
            "\u81ea\u5b9a\u4e49\u7ef4\u5ea6\u5f00\u5173\u8986\u76d6\u3002\u683c\u5f0f\uff1a\u7ef4\u5ea6 ID=true \u6216 false\uff0c\u6bcf\u884c\u4e00\u4e2a\u3002\u53ea\u6709\u4e0d\u533a\u5206\u5927\u5c0f\u5199\u7684 true \u4f1a\u8bfb\u4e3a true\uff0c\u5176\u4ed6\u975e\u7a7a\u6587\u672c\u4f1a\u6309 false \u5904\u7406\uff1b\u7b49\u53f7\u540e\u4e3a\u7a7a\u7684\u884c\u4f1a\u88ab\u5ffd\u7565\u3002\u914d\u7f6e\u503c\u4f1a\u4fdd\u5b58\u5230\u7ef4\u5ea6\u8bbe\u7f6e API\uff0c\u4f46\u5f53\u524d\u5185\u7f6e\u7ef4\u5ea6\u6ce8\u518c\u4e0d\u4f1a\u6839\u636e\u6b64\u503c\u7981\u7528\u7ef4\u5ea6\u3002\u4fee\u6539\u540e\u9700\u8981\u91cd\u542f\u3002",
            "Custom dimension-enable overrides. Format: dimension ID=true or false, one entry per line. Only the case-insensitive value true is read as true; other non-empty text is read as false, while an empty value after = is ignored. Values are stored in the dimension settings API, but built-in dimension registration currently does not disable dimensions from this setting. Restart required after changes.");
        addPropertyComment(comments, "customDimensionGravityOverrides",
            "\u81ea\u5b9a\u4e49\u7ef4\u5ea6\u91cd\u529b\u8986\u76d6\u3002\u683c\u5f0f\uff1a\u7ef4\u5ea6 ID=\u91cd\u529b\u500d\u7387\uff0c\u6570\u503c\u4f1a\u9650\u5236\u5728 0.0-5.0\uff0c\u6bcf\u884c\u4e00\u4e2a\u3002\u65e0\u6cd5\u89e3\u6790\u7684\u884c\u4f1a\u88ab\u5ffd\u7565\u3002\u914d\u7f6e\u503c\u4f1a\u4fdd\u5b58\u5230\u91cd\u529b/\u7ef4\u5ea6\u914d\u7f6e API\uff0c\u4f46\u5f53\u524d\u884c\u661f\u5c5e\u6027\u63d0\u4f9b\u5668\u4e0d\u4f1a\u5e94\u7528\u8be5\u8986\u76d6\u3002\u4fee\u6539\u540e\u9700\u8981\u91cd\u542f\u3002",
            "Custom dimension-gravity overrides. Format: dimension ID=gravity multiplier, clamped to 0.0-5.0, one entry per line. Rows with an unparseable dimension ID or gravity value are ignored. Values are exposed through the gravity settings API, but the current built-in world-physics path does not apply this override. Restart required after changes.");
        addPropertyComment(comments, "customDimensionTierOverrides",
            "\u81ea\u5b9a\u4e49\u7ef4\u5ea6\u6700\u4f4e\u706b\u7bad\u7b49\u7ea7\u8986\u76d6\u3002\u683c\u5f0f\uff1a\u7ef4\u5ea6 ID=\u6700\u4f4e\u706b\u7bad\u7b49\u7ea7\uff0c\u6bcf\u884c\u4e00\u9879\uff1b\u7b49\u7ea7\u4f1a\u9650\u5236\u5728 0-15\uff0c0 \u8868\u793a\u4e0d\u9650\u5236\uff1b\u7ef4\u5ea6 ID \u6216\u7b49\u7ea7\u65e0\u6cd5\u89e3\u6790\u3001\u7b49\u53f7\u540e\u4e3a\u7a7a\u7684\u884c\u4f1a\u88ab\u5ffd\u7565\u3002\u4fee\u6539\u540e\u9700\u8981\u91cd\u542f\u6e38\u620f\u6216\u670d\u52a1\u5668\u3002",
            "Custom minimum rocket-tier overrides for dimensions. Format: dimension ID=minimum rocket tier, one entry per line; the tier is clamped to 0-15, and 0 means no restriction. Rows with an unparseable dimension ID or tier, or an empty value after =, are ignored. A game or server restart is required after changes.");
        addPropertyComment(comments, "customDimensionHostileMobOverrides",
            "\u81ea\u5b9a\u4e49\u7ef4\u5ea6\u654c\u5bf9\u751f\u7269\u5f00\u5173\u8986\u76d6\u3002\u683c\u5f0f\uff1a\u7ef4\u5ea6 ID=true \u6216 false\uff0c\u6bcf\u884c\u4e00\u4e2a\u3002\u53ea\u6709\u4e0d\u533a\u5206\u5927\u5c0f\u5199\u7684 true \u4f1a\u8bfb\u4e3a true\uff0c\u5176\u4ed6\u975e\u7a7a\u6587\u672c\u4f1a\u6309 false \u5904\u7406\uff1b\u7b49\u53f7\u540e\u4e3a\u7a7a\u7684\u884c\u4f1a\u88ab\u5ffd\u7565\u3002\u4fee\u6539\u540e\u9700\u8981\u91cd\u542f\u6e38\u620f\u6216\u670d\u52a1\u5668\u3002",
            "Custom hostile-mob overrides. Format: dimension ID=true or false, one entry per line. Only the case-insensitive value true is read as true; other non-empty text is read as false, while an empty value after = is ignored. A game or server restart is required after changes.");
        addPropertyComment(comments, "launchAnywhere",
            "\u662f\u5426\u8ba9\u706b\u7bad\u5728\u4efb\u610f\u7ef4\u5ea6\u901a\u8fc7\u53d1\u5c04\u524d\u7684\u7ef4\u5ea6\u68c0\u67e5\u3002\u8fd9\u4e0d\u4f1a\u53d6\u6d88\u53d1\u5c04\u53f0\u3001\u71c3\u6599\u6216\u5176\u4ed6\u6b63\u5e38\u53d1\u5c04\u6761\u4ef6\u3002\u4fee\u6539\u540e\u9700\u8981\u91cd\u542f\u3002",
            "Whether a rocket may pass the current-dimension launch check in any dimension. This does not remove the launch-pad, fuel, or other normal launch requirements. Restart required after changes.");
        addPropertyComment(comments, "enableMoonDimension",
            "\u517c\u5bb9\u6027\u4fdd\u7559\u914d\u7f6e\u9879\uff1b\u5f53\u524d\u7248\u672c\u5185\u7f6e\u7ef4\u5ea6\u6ce8\u518c\u4e0d\u8bfb\u53d6\u6b64\u503c\uff0c\u8bbe\u4e3a false \u4e0d\u4f1a\u7981\u7528\u6708\u7403\u7ef4\u5ea6\u3002\u503c\u4ecd\u4f1a\u5199\u5165\u7ef4\u5ea6\u8bbe\u7f6e API\u3002\u4fee\u6539\u540e\u9700\u8981\u91cd\u542f\u3002",
            "Compatibility setting retained for existing configs; the current built-in dimension registration does not read this value, so false does not disable the Moon dimension. The value is still stored in the dimension settings API. Restart required after changes.");
        addPropertyComment(comments, "enableMarsDimension",
            "\u517c\u5bb9\u6027\u4fdd\u7559\u914d\u7f6e\u9879\uff1b\u5f53\u524d\u7248\u672c\u5185\u7f6e\u7ef4\u5ea6\u6ce8\u518c\u4e0d\u8bfb\u53d6\u6b64\u503c\uff0c\u8bbe\u4e3a false \u4e0d\u4f1a\u7981\u7528\u706b\u661f\u7ef4\u5ea6\u3002\u503c\u4ecd\u4f1a\u5199\u5165\u7ef4\u5ea6\u8bbe\u7f6e API\u3002\u4fee\u6539\u540e\u9700\u8981\u91cd\u542f\u3002",
            "Compatibility setting retained for existing configs; the current built-in dimension registration does not read this value, so false does not disable the Mars dimension. The value is still stored in the dimension settings API. Restart required after changes.");
        addPropertyComment(comments, "enableMercuryDimension",
            "\u517c\u5bb9\u6027\u4fdd\u7559\u914d\u7f6e\u9879\uff1b\u5f53\u524d\u7248\u672c\u5185\u7f6e\u7ef4\u5ea6\u6ce8\u518c\u4e0d\u8bfb\u53d6\u6b64\u503c\uff0c\u8bbe\u4e3a false \u4e0d\u4f1a\u7981\u7528\u6c34\u661f\u7ef4\u5ea6\u3002\u503c\u4ecd\u4f1a\u5199\u5165\u7ef4\u5ea6\u8bbe\u7f6e API\u3002\u4fee\u6539\u540e\u9700\u8981\u91cd\u542f\u3002",
            "Compatibility setting retained for existing configs; the current built-in dimension registration does not read this value, so false does not disable the Mercury dimension. The value is still stored in the dimension settings API. Restart required after changes.");
        addPropertyComment(comments, "enableVenusDimension",
            "\u517c\u5bb9\u6027\u4fdd\u7559\u914d\u7f6e\u9879\uff1b\u5f53\u524d\u7248\u672c\u5185\u7f6e\u7ef4\u5ea6\u6ce8\u518c\u4e0d\u8bfb\u53d6\u6b64\u503c\uff0c\u8bbe\u4e3a false \u4e0d\u4f1a\u7981\u7528\u91d1\u661f\u7ef4\u5ea6\u3002\u503c\u4ecd\u4f1a\u5199\u5165\u7ef4\u5ea6\u8bbe\u7f6e API\u3002\u4fee\u6539\u540e\u9700\u8981\u91cd\u542f\u3002",
            "Compatibility setting retained for existing configs; the current built-in dimension registration does not read this value, so false does not disable the Venus dimension. The value is still stored in the dimension settings API. Restart required after changes.");
        addPropertyComment(comments, "enableGlacioDimension",
            "\u517c\u5bb9\u6027\u4fdd\u7559\u914d\u7f6e\u9879\uff1b\u5f53\u524d\u7248\u672c\u5185\u7f6e\u7ef4\u5ea6\u6ce8\u518c\u4e0d\u8bfb\u53d6\u6b64\u503c\uff0c\u8bbe\u4e3a false \u4e0d\u4f1a\u7981\u7528\u51b0\u5ddd\u661f\u7ef4\u5ea6\u3002\u503c\u4ecd\u4f1a\u5199\u5165\u7ef4\u5ea6\u8bbe\u7f6e API\u3002\u4fee\u6539\u540e\u9700\u8981\u91cd\u542f\u3002",
            "Compatibility setting retained for existing configs; the current built-in dimension registration does not read this value, so false does not disable the Glacio dimension. The value is still stored in the dimension settings API. Restart required after changes.");
        addPropertyComment(comments, "useDedicatedDimensionSaveFolder",
            "\u662f\u5426\u4e3a\u884c\u661f\u7ef4\u5ea6\u4f7f\u7528\u72ec\u7acb\u7684\u5b58\u6863\u76ee\u5f55\u3002\u4fee\u6539\u540e\u9700\u8981\u91cd\u542f\u3002",
            "Whether planet dimensions use a dedicated save folder. Restart required after changes.");
        addPropertyComment(comments, "dedicatedDimensionSaveFolderName",
            "\u72ec\u7acb\u884c\u661f\u7ef4\u5ea6\u5b58\u6863\u76ee\u5f55\u540d\u79f0\u3002\u5176\u4ed6\u5b57\u7b26\u4f1a\u88ab\u66ff\u6362\u4e3a\u4e0b\u5212\u7ebf\u3002\u4fee\u6539\u540e\u9700\u8981\u91cd\u542f\u3002",
            "Name of the dedicated save folder for planet dimensions. Other characters are replaced with underscores. Restart required after changes.");
        addPropertyComment(comments, "moonGravityMultiplier",
            "\u517c\u5bb9\u6027/API \u914d\u7f6e\u9879\uff1b\u5f53\u524d\u5185\u7f6e\u884c\u661f\u5c5e\u6027\u63d0\u4f9b\u6708\u7403\u7684\u5b9e\u9645\u91cd\u529b\uff0c\u4e0d\u4f1a\u5c06\u6b64\u503c\u5e94\u7528\u5230\u4e16\u754c\u7269\u7406\uff1b\u4ec5\u4f9b getGravityMultiplierForDimension API \u4f7f\u7528\u3002\u8303\u56f4\uff1a0.0-5.0\u3002",
            "Compatibility/API setting; the current built-in planet properties provide the Moon's effective gravity and do not apply this value to world physics. It is used only by the getGravityMultiplierForDimension API. Range: 0.0-5.0.");
        addPropertyComment(comments, "marsGravityMultiplier",
            "\u517c\u5bb9\u6027/API \u914d\u7f6e\u9879\uff1b\u5f53\u524d\u5185\u7f6e\u884c\u661f\u5c5e\u6027\u63d0\u4f9b\u706b\u661f\u7684\u5b9e\u9645\u91cd\u529b\uff0c\u4e0d\u4f1a\u5c06\u6b64\u503c\u5e94\u7528\u5230\u4e16\u754c\u7269\u7406\uff1b\u4ec5\u4f9b getGravityMultiplierForDimension API \u4f7f\u7528\u3002\u8303\u56f4\uff1a0.0-5.0\u3002",
            "Compatibility/API setting; the current built-in planet properties provide Mars's effective gravity and do not apply this value to world physics. It is used only by the getGravityMultiplierForDimension API. Range: 0.0-5.0.");
        addPropertyComment(comments, "mercuryGravityMultiplier",
            "\u517c\u5bb9\u6027/API \u914d\u7f6e\u9879\uff1b\u5f53\u524d\u5185\u7f6e\u884c\u661f\u5c5e\u6027\u63d0\u4f9b\u6c34\u661f\u7684\u5b9e\u9645\u91cd\u529b\uff0c\u4e0d\u4f1a\u5c06\u6b64\u503c\u5e94\u7528\u5230\u4e16\u754c\u7269\u7406\uff1b\u4ec5\u4f9b getGravityMultiplierForDimension API \u4f7f\u7528\u3002\u8303\u56f4\uff1a0.0-5.0\u3002",
            "Compatibility/API setting; the current built-in planet properties provide Mercury's effective gravity and do not apply this value to world physics. It is used only by the getGravityMultiplierForDimension API. Range: 0.0-5.0.");
        addPropertyComment(comments, "venusGravityMultiplier",
            "\u517c\u5bb9\u6027/API \u914d\u7f6e\u9879\uff1b\u5f53\u524d\u5185\u7f6e\u884c\u661f\u5c5e\u6027\u63d0\u4f9b\u91d1\u661f\u7684\u5b9e\u9645\u91cd\u529b\uff0c\u4e0d\u4f1a\u5c06\u6b64\u503c\u5e94\u7528\u5230\u4e16\u754c\u7269\u7406\uff1b\u4ec5\u4f9b getGravityMultiplierForDimension API \u4f7f\u7528\u3002\u8303\u56f4\uff1a0.0-5.0\u3002",
            "Compatibility/API setting; the current built-in planet properties provide Venus's effective gravity and do not apply this value to world physics. It is used only by the getGravityMultiplierForDimension API. Range: 0.0-5.0.");
        addPropertyComment(comments, "glacioGravityMultiplier",
            "\u517c\u5bb9\u6027/API \u914d\u7f6e\u9879\uff1b\u5f53\u524d\u5185\u7f6e\u884c\u661f\u5c5e\u6027\u63d0\u4f9b\u51b0\u5ddd\u661f\u7684\u5b9e\u9645\u91cd\u529b\uff0c\u4e0d\u4f1a\u5c06\u6b64\u503c\u5e94\u7528\u5230\u4e16\u754c\u7269\u7406\uff1b\u4ec5\u4f9b getGravityMultiplierForDimension API \u4f7f\u7528\u3002\u8303\u56f4\uff1a0.0-5.0\u3002",
            "Compatibility/API setting; the current built-in planet properties provide Glacio's effective gravity and do not apply this value to world physics. It is used only by the getGravityMultiplierForDimension API. Range: 0.0-5.0.");
        return comments;
    }

    private static void addPropertyComment(Map<String, String> comments, String key, String chinese, String english) {
        comments.put(key, bilingual(chinese, english));
    }

    private static String bilingual(String chinese, String english) {
        return chinese + "\n" + english;
    }

    private static String resolveComment(String category, String key, String comment) {
        String propertyComment = PROPERTY_COMMENTS.get(key);
        // The property map is the canonical source for bilingual comments, including existing configs.
        if (propertyComment != null) {
            return propertyComment;
        }
        if (!isGenericComment(comment)) {
            return comment;
        }
        String categoryComment = getCanonicalCategoryComment(category);
        return categoryComment == null ? UNKNOWN_COMMENT : categoryComment;
    }

    private static boolean isGenericComment(String comment) {
        return GENERIC_COMMENT.equals(comment)
            || "\u914d\u7f6e\u9879\u3002\u4fee\u6539\u540e\u53ef\u80fd\u9700\u8981\u91cd\u542f\u6e38\u620f/\u670d\u52a1\u5668\u3002".equals(comment);
    }

    private static void ensureCanonicalComments(Configuration target) {
        if (target == null) {
            return;
        }
        for (String categoryName : new HashSet<>(target.getCategoryNames())) {
            ConfigCategory category = target.getCategory(categoryName);
            if (category == null) {
                continue;
            }
            String canonicalCategoryComment = getCanonicalCategoryComment(categoryName);
            if (canonicalCategoryComment != null) {
                category.setComment(canonicalCategoryComment);
            } else if (!isBilingualComment(category.getComment())) {
                category.setComment(LEGACY_CATEGORY_COMMENT);
            }
            for (Map.Entry<String, Property> entry : category.getValues().entrySet()) {
                Property property = entry.getValue();
                String canonicalPropertyComment = PROPERTY_COMMENTS.get(entry.getKey());
                if (canonicalPropertyComment != null) {
                    property.setComment(canonicalPropertyComment);
                } else if (!isBilingualComment(property.getComment())) {
                    property.setComment(LEGACY_PROPERTY_COMMENT);
                }
            }
        }
    }

    private static String getCanonicalCategoryComment(String category) {
        if (Configuration.CATEGORY_GENERAL.equals(category)) {
            return GENERAL_COMMENT;
        }
        if ("performance".equals(category)) {
            return PERFORMANCE_COMMENT;
        }
        if ("client".equals(category)) {
            return CLIENT_COMMENT;
        }
        if ("balance".equals(category)) {
            return BALANCE_COMMENT;
        }
        if ("machines".equals(category)) {
            return MACHINES_COMMENT;
        }
        if ("environment".equals(category)) {
            return ENVIRONMENT_COMMENT;
        }
        if ("dimensions".equals(category)) {
            return DIMENSIONS_COMMENT;
        }
        if (CATEGORY_PLANET_TIERS.equals(category)) {
            return PLANET_TIERS_COMMENT;
        }
        if ("mobs".equals(category)) {
            return MOBS_COMMENT;
        }
        if ("worldgen".equals(category)) {
            return WORLDGEN_COMMENT;
        }
        if ("debug".equals(category)) {
            return DEBUG_COMMENT;
        }
        return null;
    }

    private static boolean isBilingualComment(String comment) {
        if (comment == null || comment.trim().isEmpty()) {
            return false;
        }
        // Forge usually stores the Chinese and English portions as separate lines,
        // but a manually edited config may keep both languages on one line.
        boolean hasChinese = false;
        boolean hasEnglish = false;
        for (int i = 0; i < comment.length(); i++) {
            char character = comment.charAt(i);
            if (character >= '\u4e00' && character <= '\u9fff') {
                hasChinese = true;
            } else if ((character >= 'A' && character <= 'Z') || (character >= 'a' && character <= 'z')) {
                hasEnglish = true;
            }
            if (hasChinese && hasEnglish) {
                return true;
            }
        }
        return false;
    }

    private static void saveChangedConfigurations() {
        for (Configuration splitConfiguration : splitConfigurations) {
            if (splitConfiguration != null && splitConfiguration.hasChanged()) {
                splitConfiguration.save();
            }
        }
    }

    private static void saveAllConfigurations() {
        if (legacyConfiguration != null) {
            legacyConfiguration.save();
        }
        for (Configuration splitConfiguration : splitConfigurations) {
            if (splitConfiguration != null) {
                splitConfiguration.save();
            }
        }
    }

    private static void setCategoryComments() {
        setCategoryComment(Configuration.CATEGORY_GENERAL,
            GENERAL_COMMENT);
        setCategoryComment("performance",
            PERFORMANCE_COMMENT);
        setCategoryComment("client",
            CLIENT_COMMENT);
        setCategoryComment("balance",
            BALANCE_COMMENT);
        setCategoryComment("machines",
            MACHINES_COMMENT);
        setCategoryComment("environment",
            ENVIRONMENT_COMMENT);
        setCategoryComment("dimensions",
            DIMENSIONS_COMMENT);
        setCategoryComment(CATEGORY_PLANET_TIERS,
            PLANET_TIERS_COMMENT);
        setCategoryComment("mobs",
            MOBS_COMMENT);
        setCategoryComment("worldgen",
            WORLDGEN_COMMENT);
        setCategoryComment("debug",
            DEBUG_COMMENT);
    }

    private static void setCategoryComment(String category, String comment) {
        Configuration target = configForCategory(category);
        if (target != null) {
            target.setCategoryComment(category, comment);
        }
        if (legacyConfiguration != null && legacyConfiguration != target) {
            legacyConfiguration.setCategoryComment(category, comment);
        }
    }

    private static void syncPlanetTierOverrides(Configuration configuration) {
        PlanetTierOverrideRegistry.clear();
        for (PlanetTierConfig planet : PLANET_TIER_CONFIGS) {
            int tier = getInt(
                planet.key,
                CATEGORY_PLANET_TIERS,
                planet.defaultTier,
                0,
                MAX_PLANET_ROCKET_TIER,
                planet.displayName + "\u9700\u8981\u7684\u6700\u4f4e\u706b\u7bad\u7b49\u7ea7\uff08\u8303\u56f4\uff1a0-15\uff09\u3002\u9ed8\u8ba4\uff1a" + planet.defaultTier
                    + "\u3002\u8bbe\u4e3a 0 \u8868\u793a\u4e0d\u9650\u5236\u3002\u4fee\u6539\u540e\u9700\u8981\u91cd\u542f\u6e38\u620f\u6216\u670d\u52a1\u5668\u3002\n"
                    + "Minimum rocket tier required for " + planet.englishName + " (range: 0-15). Default: " + planet.defaultTier
                    + ". Set to 0 for no tier restriction. A game or server restart is required after changes.");
            PlanetTierOverrideRegistry.setPlanetTier(planet.dimensionId, tier);
        }
        for (CustomPlanetDefinition planet : CustomPlanetRegistry.getDefinitions()) {
            String key = customPlanetTierKey(planet);
            String displayName = planet.getDisplayName() == null || planet.getDisplayName().trim().isEmpty()
                ? planet.getPlanetName()
                : planet.getDisplayName();
            int tier = getInt(
                key,
                CATEGORY_PLANET_TIERS,
                planet.getTier(),
                0,
                MAX_PLANET_ROCKET_TIER,
                bilingual(
                    "\u81ea\u5b9a\u4e49\u884c\u661f " + displayName + " \uff08\u6ce8\u518c ID\uff1a" + planet.getId() + "\uff0c\u7ef4\u5ea6 ID\uff1a" + planet.getDimensionId() + "\uff09\u9700\u8981\u7684\u6700\u4f4e\u706b\u7bad\u7b49\u7ea7\uff08\u8303\u56f4\uff1a0-15\uff09\u3002\u9ed8\u8ba4\uff1a" + planet.getTier()
                        + "\u3002\u8bbe\u4e3a 0 \u8868\u793a\u4e0d\u9650\u5236\u3002\u4fee\u6539\u540e\u9700\u8981\u91cd\u542f\u6e38\u620f\u6216\u670d\u52a1\u5668\u3002",
                    "Minimum rocket tier required for custom planet " + displayName + " (registration ID: " + planet.getId() + ", dimension ID: " + planet.getDimensionId() + "; range: 0-15). Default: " + planet.getTier()
                        + ". Set to 0 for no tier restriction. A game or server restart is required after changes."));
            PlanetTierOverrideRegistry.setPlanetTier(planet.getDimensionId(), tier);
        }
        // ExternalDimensionConfig is synced before this method, but the clear above removes its entries.
        // Reapply registered external rows so external_dimensions.cfg remains the default source of their tiers.
        for (ExternalDimensionConfig.ExternalDimensionEntry external : ExternalDimensionConfig.getEntries()) {
            PlanetTierOverrideRegistry.setPlanetTier(external.getDimensionId(), external.getTier());
        }
        String[] overrides = getStringListNoLegacy(
            "customDimensionTierOverrides",
            CATEGORY_PLANET_TIERS,
            new String[0],
            bilingual(
                "\u81ea\u5b9a\u4e49\u7ef4\u5ea6\u706b\u7bad\u7b49\u7ea7\u8986\u76d6\u3002\u683c\u5f0f\uff1a\u7ef4\u5ea6 ID=\u6700\u4f4e\u706b\u7bad\u7b49\u7ea7\uff0c\u6bcf\u884c\u4e00\u9879\uff1b\u7b49\u7ea7\u4f1a\u9650\u5236\u5728 0-15\uff0c0 \u8868\u793a\u4e0d\u9650\u5236\uff1b\u7b49\u53f7\u540e\u4e3a\u7a7a\u6216\u6570\u503c\u65e0\u6cd5\u89e3\u6790\u65f6\u5ffd\u7565\u3002\u4fee\u6539\u540e\u9700\u8981\u91cd\u542f\u6e38\u620f\u6216\u670d\u52a1\u5668\u3002",
                "Custom minimum rocket-tier overrides. Format: dimension ID=minimum rocket tier, one entry per line; the tier is clamped to 0-15, and 0 means no restriction. Empty or unparseable values after = are ignored. A game or server restart is required after changes."));
        for (String override : overrides) {
            applyCustomPlanetTierOverride(override);
        }
    }

    private static String customPlanetTierKey(CustomPlanetDefinition planet) {
        String path = planet.getId().getPath();
        StringBuilder key = new StringBuilder(path.length() + 8);
        key.append("custom_");
        for (int i = 0; i < path.length(); i++) {
            char c = path.charAt(i);
            if ((c >= 'a' && c <= 'z') || (c >= '0' && c <= '9') || c == '_') {
                key.append(c);
            } else {
                key.append('_');
            }
        }
        return key.toString();
    }

    private static void applyCustomPlanetTierOverride(String override) {
        if (override == null) {
            return;
        }
        String trimmed = override.trim();
        if (trimmed.isEmpty() || trimmed.startsWith("#")) {
            return;
        }
        int separator = trimmed.indexOf('=');
        if (separator <= 0 || separator >= trimmed.length() - 1) {
            return;
        }
        try {
            int dimensionId = Integer.parseInt(trimmed.substring(0, separator).trim());
            int tier = clampInt(Integer.parseInt(trimmed.substring(separator + 1).trim()), 0, MAX_PLANET_ROCKET_TIER);
            PlanetTierOverrideRegistry.setPlanetTier(dimensionId, tier);
        } catch (NumberFormatException ignored) {
            // Keep config forgiving: invalid custom rows are ignored instead of blocking startup.
        }
    }

    private static void syncPlanetDimensionSettings() {
        PLANET_DIMENSION_ENABLED.clear();
        PLANET_GRAVITY_MULTIPLIERS.clear();
        for (PlanetDimensionConfig planet : PLANET_DIMENSION_CONFIGS) {
            PLANET_DIMENSIONS.add(planet.dimensionId);
            boolean enabled = getBoolean(
                planet.enableKey,
                "dimensions",
                true,
                bilingual(
                    "\u517c\u5bb9\u6027\u4fdd\u7559\u914d\u7f6e\u9879\uff1b\u5f53\u524d\u5185\u7f6e\u7ef4\u5ea6\u6ce8\u518c\u6d41\u7a0b\u4e0d\u4f7f\u7528\u6b64\u503c\uff0c\u4e0d\u4f1a\u6839\u636e\u5b83\u7981\u7528" + planet.displayName + "\u7ef4\u5ea6\uff1b\u4ec5\u4f9b isDimensionEnabled \u67e5\u8be2\u3002\u4fee\u6539\u540e\u9700\u8981\u91cd\u542f\u3002",
                    "Compatibility setting retained for existing configs; the current built-in dimension registration does not use this value to disable the " + planet.englishName + " dimension. It is only exposed through isDimensionEnabled. Restart required after changes."));
            float gravity = getFloat(
                planet.gravityKey,
                "dimensions",
                1.0f,
                0.0f,
                5.0f,
                bilingual(
                    "\u517c\u5bb9\u6027\u4fdd\u7559\u914d\u7f6e\u9879\uff1b\u5f53\u524d\u5185\u7f6e\u884c\u661f\u5c5e\u6027\u63d0\u4f9b" + planet.displayName + "\u5b9e\u9645\u91cd\u529b\uff0c\u672a\u5c06\u6b64\u503c\u5e94\u7528\u5230\u4e16\u754c\uff1b\u4ec5\u4f9b getGravityMultiplierForDimension \u67e5\u8be2\u3002",
                    "Compatibility setting retained for existing configs; the current built-in planet properties provide the effective gravity for " + planet.englishName + " and do not apply this value to worlds. It is only exposed through getGravityMultiplierForDimension."));
            PLANET_DIMENSION_ENABLED.put(planet.dimensionId, enabled);
            PLANET_GRAVITY_MULTIPLIERS.put(planet.dimensionId, gravity);
        }

        String[] enabledOverrides = getStringListNoLegacy(
            "customDimensionEnabledOverrides",
            "dimensions",
            new String[0],
            bilingual(
                "\u81ea\u5b9a\u4e49\u7ef4\u5ea6\u542f\u7528\u72b6\u6001\u8986\u76d6\u3002\u683c\u5f0f\uff1a\u7ef4\u5ea6 ID=true \u6216 false\uff0c\u6bcf\u884c\u4e00\u9879\u3002\u5f53\u524d\u5185\u7f6e\u7ef4\u5ea6\u6ce8\u518c\u4e0d\u4f1a\u6839\u636e\u6b64\u503c\u7981\u7528\u7ef4\u5ea6\uff1b\u4fee\u6539\u540e\u9700\u8981\u91cd\u542f\u3002",
                "Custom dimension-enable overrides. Format: dimension ID=true or false, one entry per line. Built-in dimension registration does not disable dimensions from this setting. Restart required after changes."));
        for (String override : enabledOverrides) {
            applyCustomDimensionEnabledOverride(override);
        }

        String[] gravityOverrides = getStringListNoLegacy(
            "customDimensionGravityOverrides",
            "dimensions",
            new String[0],
            bilingual(
                "\u81ea\u5b9a\u4e49\u7ef4\u5ea6\u91cd\u529b\u8986\u76d6\u3002\u683c\u5f0f\uff1a\u7ef4\u5ea6 ID=\u91cd\u529b\u500d\u7387\uff0c\u8303\u56f4 0.0-5.0\uff0c\u6bcf\u884c\u4e00\u9879\u3002\u5f53\u524d\u5185\u7f6e\u4e16\u754c\u63d0\u4f9b\u5668\u4e0d\u4f1a\u5c06\u8be5\u503c\u5e94\u7528\u5230\u5b9e\u9645\u91cd\u529b\uff1b\u4fee\u6539\u540e\u9700\u8981\u91cd\u542f\u3002",
                "Custom dimension-gravity overrides. Format: dimension ID=gravity multiplier, range 0.0-5.0, one entry per line. Current built-in world providers do not apply this value to effective gravity. Restart required after changes."));
        for (String override : gravityOverrides) {
            applyCustomDimensionGravityOverride(override);
        }
    }

    private static void applyCustomDimensionEnabledOverride(String override) {
        if (override == null) {
            return;
        }
        String trimmed = override.trim();
        if (trimmed.isEmpty() || trimmed.startsWith("#")) {
            return;
        }
        int separator = trimmed.indexOf('=');
        if (separator <= 0 || separator >= trimmed.length() - 1) {
            return;
        }
        try {
            int dimensionId = Integer.parseInt(trimmed.substring(0, separator).trim());
            boolean enabled = Boolean.parseBoolean(trimmed.substring(separator + 1).trim());
            PLANET_DIMENSIONS.add(dimensionId);
            PLANET_DIMENSION_ENABLED.put(dimensionId, enabled);
        } catch (NumberFormatException ignored) {
            // Keep config forgiving: invalid custom rows are ignored instead of blocking startup.
        }
    }

    private static void applyCustomDimensionGravityOverride(String override) {
        if (override == null) {
            return;
        }
        String trimmed = override.trim();
        if (trimmed.isEmpty() || trimmed.startsWith("#")) {
            return;
        }
        int separator = trimmed.indexOf('=');
        if (separator <= 0 || separator >= trimmed.length() - 1) {
            return;
        }
        try {
            int dimensionId = Integer.parseInt(trimmed.substring(0, separator).trim());
            float gravity = Float.parseFloat(trimmed.substring(separator + 1).trim());
            if (gravity < 0.0f) gravity = 0.0f;
            if (gravity > 5.0f) gravity = 5.0f;
            PLANET_DIMENSIONS.add(dimensionId);
            PLANET_GRAVITY_MULTIPLIERS.put(dimensionId, gravity);
        } catch (NumberFormatException ignored) {
            // Keep config forgiving: invalid custom rows are ignored instead of blocking startup.
        }
    }

    private static void syncPlanetHostileMobOverrides() {
        PLANET_HOSTILE_MOB_OVERRIDES.clear();
        PLANET_DIMENSIONS.clear();
        for (PlanetMobConfig planet : PLANET_MOB_CONFIGS) {
            PLANET_DIMENSIONS.add(planet.dimensionId);
            boolean enabled = getBoolean(
                planet.key,
                "mobs",
                true,
                bilingual(
                    "\u662f\u5426\u5728" + planet.displayName + "\u751f\u6210\u654c\u5bf9\u751f\u7269\u3002",
                    "Whether hostile mobs spawn on " + planet.englishName + "."));
            PLANET_HOSTILE_MOB_OVERRIDES.put(planet.dimensionId, enabled);
        }

        String[] overrides = getStringListNoLegacy(
            "customDimensionHostileMobOverrides",
            "mobs",
            new String[0],
            "\u914d\u7f6e\u9879\u3002\u4fee\u6539\u540e\u53ef\u80fd\u9700\u8981\u91cd\u542f\u6e38\u620f/\u670d\u52a1\u5668\u3002");
        for (String override : overrides) {
            applyCustomHostileMobOverride(override);
        }
    }

    private static void applyCustomHostileMobOverride(String override) {
        if (override == null) {
            return;
        }
        String trimmed = override.trim();
        if (trimmed.isEmpty() || trimmed.startsWith("#")) {
            return;
        }
        int separator = trimmed.indexOf('=');
        if (separator <= 0 || separator >= trimmed.length() - 1) {
            return;
        }
        try {
            int dimensionId = Integer.parseInt(trimmed.substring(0, separator).trim());
            boolean enabled = Boolean.parseBoolean(trimmed.substring(separator + 1).trim());
            PLANET_DIMENSIONS.add(dimensionId);
            PLANET_HOSTILE_MOB_OVERRIDES.put(dimensionId, enabled);
        } catch (NumberFormatException ignored) {
            // Keep config forgiving: invalid custom rows are ignored instead of blocking startup.
        }
    }

    private static final class PlanetTierConfig {
        private final String key;
        private final int dimensionId;
        private final int defaultTier;
        private final String displayName;
        private final String englishName;

        private PlanetTierConfig(String key, int dimensionId, int defaultTier, String displayName, String englishName) {
            this.key = key;
            this.dimensionId = dimensionId;
            this.defaultTier = defaultTier;
            this.displayName = displayName;
            this.englishName = englishName;
        }
    }

    private static final class PlanetMobConfig {
        private final String key;
        private final int dimensionId;
        private final String displayName;
        private final String englishName;

        private PlanetMobConfig(String key, int dimensionId, String displayName, String englishName) {
            this.key = key;
            this.dimensionId = dimensionId;
            this.displayName = displayName;
            this.englishName = englishName;
        }
    }

    private static final class PlanetDimensionConfig {
        private final String enableKey;
        private final String gravityKey;
        private final int dimensionId;
        private final String displayName;
        private final String englishName;

        private PlanetDimensionConfig(String enableKey, String gravityKey, int dimensionId, String displayName, String englishName) {
            this.enableKey = enableKey;
            this.gravityKey = gravityKey;
            this.dimensionId = dimensionId;
            this.displayName = displayName;
            this.englishName = englishName;
        }
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
        Float planetMultiplier = PLANET_GRAVITY_MULTIPLIERS.get(dimensionId);
        return baseMultiplier * (planetMultiplier == null ? 1.0f : planetMultiplier);
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
        Boolean configured = PLANET_HOSTILE_MOB_OVERRIDES.get(dimensionId);
        if (configured != null) {
            return configured;
        }
        return true;
    }

    /**
     * Check if the dimension is one of Ad Astra's planet dimensions.
     * @param dimensionId The dimension ID
     * @return True for Moon, Mars, Mercury, Venus or Glacio
     */
    public static boolean isPlanetDimension(int dimensionId) {
        return PLANET_DIMENSIONS.contains(dimensionId)
            || dimensionId == ModDimensions.MOON_ID
            || dimensionId == ModDimensions.MARS_ID
            || dimensionId == ModDimensions.MERCURY_ID
            || dimensionId == ModDimensions.VENUS_ID
            || dimensionId == ModDimensions.GLACIO_ID;
    }

    /**
     * Check if a dimension is enabled.
     * @param dimensionId The dimension ID
     * @return True if the dimension is enabled
     */
    public static boolean isDimensionEnabled(int dimensionId) {
        Boolean enabled = PLANET_DIMENSION_ENABLED.get(dimensionId);
        if (enabled != null) {
            return enabled;
        }
        return true;
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
