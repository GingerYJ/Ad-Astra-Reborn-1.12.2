package earth.terrarium.adastra.common.config;

import earth.terrarium.adastra.AdAstraReborn;
import earth.terrarium.adastra.Reference;
import earth.terrarium.adastra.common.registry.ModDimensions;
import earth.terrarium.adastra.common.registry.ModResourceIds;
import earth.terrarium.adastra.common.rocket.ConfigurableRocketRegistry;
import earth.terrarium.adastra.common.util.PlanetTierOverrideRegistry;
import earth.terrarium.adastra.common.world.PlanetDimensionProperties;
import earth.terrarium.adastra.common.world.custom.BuiltInPlanetRegistry;
import earth.terrarium.adastra.common.world.custom.CustomPlanetDefinition;
import earth.terrarium.adastra.common.world.custom.CustomPlanetRegistry;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraft.util.ResourceLocation;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public final class AdAstraConfig {

    private static final int CONFIG_FORMAT_VERSION = 6;
    private static final String CONFIG_MIGRATION_MARKER = ".config_format_v" + CONFIG_FORMAT_VERSION;
    private static final String REMOVED_CONFIG_FORMAT_VERSION_KEY = "configFormatVersion";
    private static final String CATEGORY_PLANET_PREFIX = "planet_";
    private static final String CATEGORY_PLANET_ORE_PREFIX = "planet_ore_";
    private static final String[] CONFIG_FILE_NAMES = new String[] {
        "ad_astra.cfg", "core.cfg", "client.cfg", "machines.cfg", "dimensions.cfg",
        "mobs.cfg", "worldgen.cfg", "debug.cfg", "external_dimensions.cfg", "rockets.cfg"
    };
    private static final int MAX_PLANET_ROCKET_TIER = 15;
    private static final String DEFAULT_DEDICATED_DIMENSION_SAVE_FOLDER_NAME = "AdAstraDimensions";
    private static final String GENERIC_COMMENT =
        "\u914d\u7f6e\u9879\u3002\u4fee\u6539\u540e\u53ef\u80fd\u9700\u8981\u91cd\u542f\u6e38\u620f/\u670d\u52a1\u5668\u3002\n"
            + "Configuration option. A game or server restart may be required after changes.";
    private static final String GENERAL_COMMENT =
        "\u5e38\u89c4\u6e38\u620f\u4e0e\u73af\u5883\u8bbe\u7f6e\u3002\u4fee\u6539\u540e\u53ef\u80fd\u9700\u8981\u91cd\u542f\u6e38\u620f\u6216\u670d\u52a1\u5668\u3002\n"
            + "General gameplay and environment settings. A game or server restart may be required after changes.";
    private static final String PERFORMANCE_COMMENT =
        "\u6027\u80fd\u4e0e\u7f13\u5b58\u8bbe\u7f6e\u3002\u8c03\u6574\u540e\u53ef\u80fd\u6539\u53d8\u670d\u52a1\u5668\u8d1f\u8f7d\u3002\n"
            + "Performance and cache settings. Changes may affect server load.";
    private static final String CLIENT_COMMENT =
        "\u5ba2\u6237\u7aef\u754c\u9762\u4e0e\u663e\u793a\u8bbe\u7f6e\u3002\u4ec5\u5f71\u54cd\u5ba2\u6237\u7aef\u3002\n"
            + "Client interface and display settings. These options affect the client only.";
    private static final String BALANCE_COMMENT =
        "\u6e38\u620f\u5e73\u8861\u8bbe\u7f6e\u3002\u8c03\u6574\u540e\u53ef\u80fd\u6539\u53d8\u673a\u5668\u548c\u73af\u5883\u7684\u6d88\u8017\u3002\n"
            + "Gameplay balance settings. Changes may alter machine and environment costs.";
    private static final String MACHINES_COMMENT =
        "\u673a\u5668\u5904\u7406\u3001\u80fd\u91cf\u3001\u6d41\u4f53\u548c\u7ba1\u9053\u8bbe\u7f6e\u3002\n"
            + "Machine processing, energy, fluid, and pipe settings.";
    private static final String DIMENSIONS_COMMENT =
        "\u884c\u661f\u7ef4\u5ea6\u4e0e\u706b\u7bad\u9650\u5236\u8bbe\u7f6e\u3002\u6ce8\u518c\u5143\u6570\u636e\u7531\u4ee3\u7801\u7ef4\u62a4\uff0c\u4ec5\u706b\u7bad\u7b49\u7ea7\u53ef\u914d\u7f6e\u3002\u4fee\u6539\u540e\u9700\u8981\u91cd\u542f\u3002\n"
            + "Planet dimensions and rocket restrictions. Registration metadata is code-owned; only rocket tiers are configurable. Restart after changes.";
    private static final String PLANET_COMMENT =
        "\u884c\u661f\u8868\u9762\u7ef4\u5ea6 ID \u7531\u4ee3\u7801\u7ef4\u62a4\uff1b\u706b\u7bad\u7b49\u7ea7\u8303\u56f4 0-15\uff0c0 \u8868\u793a\u4e0d\u9650\u5236\u3002\n"
            + "Numeric surface-dimension IDs are code-owned; rocket tier range: 0-15, with 0 meaning no restriction.";
    private static final String MOBS_COMMENT =
        "\u884c\u661f\u751f\u7269\u751f\u6210\u3001\u751f\u6210\u767d\u540d\u5355\u3001\u6c27\u6c14\u8c41\u514d\u4e0e\u6570\u91cf\u4e0a\u9650\u3002\n"
            + "Planet mob spawning, spawn whitelists, oxygen immunity, and entity caps.";
    private static final String WORLDGEN_COMMENT =
        "\u884c\u661f\u5730\u5f62\u3001\u77ff\u8109\u5217\u8868\u4e0e\u751f\u6210\u500d\u7387\uff1b\u77ff\u8109\u6309\u884c\u661f\u5206\u533a\u3002\u4ec5\u65b0\u533a\u5757\u751f\u6548\u3002\n"
            + "Planet terrain, ore rows, and generation multiplier; ore rows are grouped by planet. New chunks only.";
    private static final String UNKNOWN_COMMENT =
        "\u672a\u5206\u7c7b\u914d\u7f6e\u9879\u3002\u8bf7\u53c2\u8003\u914d\u7f6e\u9879\u540d\u79f0\u4e0e\u6570\u503c\u8303\u56f4\u3002\n"
            + "Unclassified configuration option. Refer to the property name and value range.";
    private static final String LEGACY_CATEGORY_COMMENT =
        "\u65e7\u914d\u7f6e\u5206\u7c7b\uff0c\u5f53\u524d\u7248\u672c\u4e0d\u8bfb\u53d6\u3002\n"
            + "Legacy category; not read by the current version.";
    private static final String LEGACY_PROPERTY_COMMENT =
        "\u65e7\u914d\u7f6e\u9879\uff0c\u5f53\u524d\u7248\u672c\u4e0d\u8bfb\u53d6\u3002\n"
            + "Legacy option; not read by the current version.";
    private static final Map<String, String> PROPERTY_COMMENTS = createPropertyComments();

    private static Configuration configuration;
    private static Configuration coreConfiguration;
    private static Configuration clientConfiguration;
    private static Configuration machinesConfiguration;
    private static Configuration dimensionsConfiguration;
    private static Configuration mobsConfiguration;
    private static Configuration worldgenConfiguration;
    private static Configuration[] splitConfigurations = new Configuration[0];
    private static File configurationFolder;
    private static final Map<Integer, Boolean> PLANET_DIMENSION_ENABLED = new HashMap<>();
    private static final Map<Integer, Float> PLANET_GRAVITY_MULTIPLIERS = new HashMap<>();
    private static final Map<Integer, Boolean> PLANET_HOSTILE_MOB_OVERRIDES = new HashMap<>();
    private static final Map<Integer, Integer> PLANET_ROCKET_TIERS = new HashMap<>();
    private static final Map<Integer, String> PLANET_KEYS_BY_DIMENSION = new HashMap<>();
    private static final Set<Integer> PLANET_DIMENSIONS = new HashSet<>();
    private static final Map<String, List<PlanetMobSpawnConfig>> PLANET_MOB_SPAWN_WHITELIST = new HashMap<>();

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
    public static float oxygenDamageAmount;
    public static int oxygenDamageInterval;
    public static int oxygenConsumptionInterval;
    public static int oxygenConsumptionAmount;
    public static boolean enableSpaceEnvironmentEffects;
    public static int planetRandomTickSpeed;

    // Performance Configuration
    public static boolean enableMachineIdleOptimization;
    public static int machineTransferInterval;

    // Balance Configuration
    public static float gravityNormalizerEnergyMultiplier;

    // Machine Configuration
    public static float machineProcessingSpeedMultiplier;
    public static float machineEnergyConsumptionMultiplier;
    public static float coalGeneratorEnergyMultiplier;
    public static float solarPanelEnergyMultiplier;
    public static int ironMaxEnergyInOut = 100;
    public static int ironEnergyCapacity = 10_000;
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
    public static float temperatureDamageMultiplier = 1.0f;
    public static float gravityMultiplier = 1.0f;

    // Dimension Configuration
    public static boolean launchFromAnywhere = false;
    public static boolean useDedicatedDimensionSaveFolder = true;
    public static String dedicatedDimensionSaveFolderName = DEFAULT_DEDICATED_DIMENSION_SAVE_FOLDER_NAME;

    // Mob Configuration
    public static float planetMobSpawnRateMultiplier;
    public static int planetEntityCapPerType;
    public static int planetMobCountRescanIntervalTicks;
    public static int planetMobRespawnIntervalTicks;
    public static String[] noOxygenEntityWhitelist;

    // World Generation Configuration
    public static float oreGenerationMultiplier;

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

        configurationFolder = folder;
        upgradeConfigFormat(folder, legacyFile, file);
        coreConfiguration = new Configuration(new File(folder, "core.cfg"));
        clientConfiguration = new Configuration(new File(folder, "client.cfg"));
        machinesConfiguration = new Configuration(new File(folder, "machines.cfg"));
        dimensionsConfiguration = new Configuration(new File(folder, "dimensions.cfg"));
        mobsConfiguration = new Configuration(new File(folder, "mobs.cfg"));
        worldgenConfiguration = new Configuration(new File(folder, "worldgen.cfg"));
        ExternalDimensionConfig.init(new File(folder, "external_dimensions.cfg"));
        ConfigurableRocketRegistry.init(new File(folder, "rockets.cfg"));
        splitConfigurations = new Configuration[] {
            coreConfiguration,
            clientConfiguration,
            machinesConfiguration,
            dimensionsConfiguration,
            mobsConfiguration,
            worldgenConfiguration
        };
        configuration = coreConfiguration;
        sync();
    }

    private static void upgradeConfigFormat(File folder, File resolvedLegacyFile, File suggestedFile) {
        File migrationMarker = new File(folder, CONFIG_MIGRATION_MARKER);
        boolean hasConfigFiles = (resolvedLegacyFile != null && resolvedLegacyFile.exists())
            || (suggestedFile != null && suggestedFile.exists());
        for (String configName : CONFIG_FILE_NAMES) {
            hasConfigFiles |= new File(folder, configName).exists();
        }
        if (!hasConfigFiles || migrationMarker.exists()) {
            return;
        }

        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ROOT).format(new Date());
        File backupFolder = new File(folder, "backup_" + timestamp);
        int suffix = 1;
        while (backupFolder.exists()) {
            backupFolder = new File(folder, "backup_" + timestamp + "_" + suffix++);
        }
        try {
            Files.createDirectories(backupFolder.toPath());
            for (String configName : CONFIG_FILE_NAMES) {
                moveToBackup(new File(folder, configName), backupFolder, configName);
            }
            if (suggestedFile != null && suggestedFile.exists()
                && !sameFile(suggestedFile, new File(folder, suggestedFile.getName()))) {
                moveToBackup(suggestedFile, backupFolder, "legacy_" + suggestedFile.getName());
            }
            AdAstraReborn.LOGGER.info("Backed up old {} configuration files to {}.", Reference.MOD_NAME, backupFolder);
        } catch (IOException exception) {
            AdAstraReborn.LOGGER.warn("Could not back up old {} configuration files; defaults will be used where possible.",
                Reference.MOD_NAME, exception);
        }
    }

    private static void moveToBackup(File source, File backupFolder, String targetName) throws IOException {
        if (!source.exists()) {
            return;
        }
        Files.move(source.toPath(), new File(backupFolder, targetName).toPath(), StandardCopyOption.REPLACE_EXISTING);
    }

    private static boolean sameFile(File first, File second) {
        try {
            return first.getCanonicalFile().equals(second.getCanonicalFile());
        } catch (IOException ignored) {
            return first.getAbsoluteFile().equals(second.getAbsoluteFile());
        }
    }

    private static void writeMigrationMarker() {
        if (configurationFolder == null) {
            return;
        }
        File marker = new File(configurationFolder, CONFIG_MIGRATION_MARKER);
        if (marker.exists()) {
            return;
        }
        try {
            marker.createNewFile();
        } catch (IOException exception) {
            AdAstraReborn.LOGGER.warn("Could not write the {} configuration migration marker.", Reference.MOD_NAME,
                exception);
        }
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

        removeLegacyConfigurationPaths();
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

        // Balance Configuration
        gravityNormalizerEnergyMultiplier = getFloat(
            "gravityNormalizerEnergyMultiplier",
            "balance",
            1.0f,
            0.1f,
            10.0f,
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
        ironMaxEnergyInOut = getInt("ironMaxEnergyInOut", "machines", 100, 0, Integer.MAX_VALUE,
            bilingual("\u94c1\u7ea7\u6bcf\u523b\u6700\u5927 FE \u8f93\u5165/\u8f93\u51fa\u91cf\u3002", "Iron tier max FE input/output per tick."));
        ironEnergyCapacity = getInt("ironEnergyCapacity", "machines", 10000, 0, Integer.MAX_VALUE,
            bilingual("\u94c1\u7ea7\u5185\u90e8 FE \u5bb9\u91cf\u3002", "Iron tier internal FE capacity."));
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

        // Dimension Configuration
        launchFromAnywhere = false;
        useDedicatedDimensionSaveFolder = true;
        dedicatedDimensionSaveFolderName = DEFAULT_DEDICATED_DIMENSION_SAVE_FOLDER_NAME;
        syncPlanetDimensionSettings();

        // Mob Configuration
        planetMobSpawnRateMultiplier = getFloat(
            "planetMobSpawnRateMultiplier",
            "mobs",
            1.0f,
            0.0f,
            10.0f,
            "\u914d\u7f6e\u9879\u3002\u4fee\u6539\u540e\u53ef\u80fd\u9700\u8981\u91cd\u542f\u6e38\u620f/\u670d\u52a1\u5668\u3002");
        syncPlanetHostileMobOverrides();
        planetEntityCapPerType = getInt(
            "planetEntityCapPerType",
            "mobs",
            10,
            1,
            1000,
            bilingual("\u6bcf\u79cd\u5b9e\u4f53\u5728\u6bcf\u4e2a\u884c\u661f\u7684\u6570\u91cf\u4e0a\u9650\u3002", "Maximum count of each entity type on each planet."));
        planetMobCountRescanIntervalTicks = getInt(
            "planetMobCountRescanIntervalTicks",
            "mobs",
            6000,
            0,
            120000,
            bilingual("\u884c\u661f\u751f\u7269\u6570\u91cf\u5b8c\u6574\u6821\u51c6\u95f4\u9694\uff0c\u5355\u4f4d\u4e3a tick\uff08\u6bcf\u79d2 20 tick\uff09\u3002\u4ec5\u5728\u6709\u73a9\u5bb6\u65f6\u6821\u51c6\uff0c0 \u8868\u793a\u4ec5\u9996\u6b21\u6821\u51c6\u3002", "Full planet-mob count rescan interval in ticks (20 ticks per second). Rescans run only while players are present; 0 means the initial rescan only."));
        planetMobRespawnIntervalTicks = getInt(
            "planetMobRespawnIntervalTicks",
            "mobs",
            0,
            0,
            120000,
            bilingual("\u81ea\u7136\u751f\u6210\u5b9e\u4f53\u6b7b\u4ea1\u540e\u7684\u91cd\u65b0\u751f\u6210\u51b7\u5374\u65f6\u95f4\uff0c\u5355\u4f4d\u4e3a tick\u3002\u4ec5\u5f71\u54cd\u81ea\u7136\u751f\u6210\uff0c0 \u8868\u793a\u4e0d\u989d\u5916\u9650\u5236\u3002", "Natural-spawn cooldown after an entity dies, in ticks. Affects natural spawning only; 0 adds no cooldown."));
        noOxygenEntityWhitelist = getStringList(
            "noOxygenEntityWhitelist",
            "mobs",
            new String[0],
            bilingual("\u7f3a\u6c27\u65f6\u53ef\u751f\u5b58\u7684\u989d\u5916\u5b9e\u4f53 ID\uff0c\u6bcf\u884c\u4e00\u9879\u3002planetMobSpawnWhitelist \u4e2d\u7684\u5b9e\u4f53\u4f1a\u81ea\u52a8\u5305\u542b\u5728\u6b64\u89c4\u5219\u4e2d\u3002",
                "Additional entity IDs allowed to live without oxygen, one per line. Entities in planetMobSpawnWhitelist are included automatically."));
        syncPlanetMobSpawnWhitelist();

        // World Generation Configuration
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
        syncPlanetTierOverrides();

        // Validation
        if (gravityNormalizerEnergyMultiplier < 0.1f) gravityNormalizerEnergyMultiplier = 0.1f;
        if (gravityNormalizerEnergyMultiplier > 10.0f) gravityNormalizerEnergyMultiplier = 10.0f;
        // Machine validation
        if (machineProcessingSpeedMultiplier < 0.1f) machineProcessingSpeedMultiplier = 0.1f;
        if (machineProcessingSpeedMultiplier > 10.0f) machineProcessingSpeedMultiplier = 10.0f;
        if (machineEnergyConsumptionMultiplier < 0.1f) machineEnergyConsumptionMultiplier = 0.1f;
        if (machineEnergyConsumptionMultiplier > 10.0f) machineEnergyConsumptionMultiplier = 10.0f;
        if (coalGeneratorEnergyMultiplier < 0.1f) coalGeneratorEnergyMultiplier = 0.1f;
        if (solarPanelEnergyMultiplier < 0.1f) solarPanelEnergyMultiplier = 0.1f;
        dedicatedDimensionSaveFolderName = sanitizeSaveFolderSegment(
            dedicatedDimensionSaveFolderName, DEFAULT_DEDICATED_DIMENSION_SAVE_FOLDER_NAME);

        // Mob validation
        if (planetMobSpawnRateMultiplier < 0.0f) planetMobSpawnRateMultiplier = 0.0f;
        if (planetMobSpawnRateMultiplier > 10.0f) planetMobSpawnRateMultiplier = 10.0f;
        if (planetEntityCapPerType < 1) planetEntityCapPerType = 1;

        // Worldgen validation
        if (oreGenerationMultiplier < 0.0f) oreGenerationMultiplier = 0.0f;
        if (oreGenerationMultiplier > 10.0f) oreGenerationMultiplier = 10.0f;

        // Sync ore generation configuration
        OreGenConfig.sync(worldgenConfiguration);
        earth.terrarium.adastra.common.world.AdAstraChunkGenerator.clearCache();

        // Existing configs can contain entries from removed planets. Keep those values for compatibility,
        // but ensure every retained category and property has an explicit canonical bilingual comment.
        for (Configuration splitConfiguration : splitConfigurations) {
            ensureCanonicalComments(splitConfiguration);
        }

        // Configuration comments are metadata and Forge does not mark a property dirty when only its comment changes.
        // Save all split files after synchronization so existing installations receive updated bilingual comments.
        saveAllConfigurations();
        writeMigrationMarker();
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
        return sanitizeSaveFolderSegment(
            dedicatedDimensionSaveFolderName, DEFAULT_DEDICATED_DIMENSION_SAVE_FOLDER_NAME) + "/" + dimensionFolder;
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
            || "balance".equals(category)) {
            return coreConfiguration;
        }
        if ("client".equals(category)) {
            return clientConfiguration;
        }
        if ("machines".equals(category)) {
            return machinesConfiguration;
        }
        if ("dimensions".equals(category) || category.startsWith(CATEGORY_PLANET_PREFIX)) {
            return dimensionsConfiguration;
        }
        if ("mobs".equals(category)) {
            return mobsConfiguration;
        }
        if ("worldgen".equals(category)) {
            return worldgenConfiguration;
        }
        return configuration == null ? coreConfiguration : configuration;
    }

    private static void removeLegacyConfigurationPaths() {
        removeProperties(coreConfiguration, Configuration.CATEGORY_GENERAL,
            REMOVED_CONFIG_FORMAT_VERSION_KEY);
        removeCategory(coreConfiguration, "environment");
        removeProperties(coreConfiguration, "performance",
            "maxOxygenDistributorRadius", "oxygenScanRadius", "sealedRoomCacheLifetime", "sealedRoomMaxBlocks");
        removeProperties(clientConfiguration, "client", "energyBarX", "energyBarY", "energyBarScale");
        removeProperties(machinesConfiguration, "machines",
            "compressorBaseTime", "cryoFreezerBaseTime", "etrionicBlastFurnaceBaseTime",
            "fuelRefineryBaseTime", "ironFluidCapacity");
        removeProperties(worldgenConfiguration, "worldgen",
            "enableStructureGeneration", "enableLunarVillages", "enableMarsOutposts", "oreVeins");
        removeCategory(dimensionsConfiguration, "dimensions");
        removeCategory(dimensionsConfiguration, "planet_tiers");
        removeCategory(worldgenConfiguration, "worldgen_custom_blocks");

        if (worldgenConfiguration != null) {
            ConfigCategory worldgen = worldgenConfiguration.getCategory("worldgen");
            if (worldgen != null) {
                String[] legacyOreProperties = new String[] {
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
                for (String property : legacyOreProperties) {
                    worldgen.remove(property);
                }
            }
            for (String categoryName : new HashSet<>(worldgenConfiguration.getCategoryNames())) {
                if (categoryName.startsWith("worldgen_")) {
                    removeCategory(worldgenConfiguration, categoryName);
                }
            }
        }

        if (dimensionsConfiguration != null) {
            for (String categoryName : new HashSet<>(dimensionsConfiguration.getCategoryNames())) {
                if (categoryName.startsWith(CATEGORY_PLANET_PREFIX)) {
                    removeProperties(dimensionsConfiguration, categoryName,
                        "enabled", "gravityMultiplier", "dimensionId", "registryId");
                }
            }
        }
    }

    private static void removeCategory(Configuration target, String categoryName) {
        if (target == null) {
            return;
        }
        ConfigCategory category = target.getCategory(categoryName);
        if (category != null) {
            target.removeCategory(category);
        }
    }

    private static void removeProperties(Configuration target, String categoryName, String... propertyNames) {
        if (target == null) {
            return;
        }
        ConfigCategory category = target.getCategory(categoryName);
        if (category == null) {
            return;
        }
        for (String propertyName : propertyNames) {
            category.remove(propertyName);
        }
    }

    private static boolean getBoolean(String key, String category, boolean defaultValue, String comment) {
        Configuration target = configForCategory(category);
        String resolvedComment = resolveComment(category, key, comment);
        return target.getBoolean(key, category, defaultValue, resolvedComment);
    }

    private static int getInt(String key, String category, int defaultValue, int minValue, int maxValue, String comment) {
        Configuration target = configForCategory(category);
        String resolvedComment = resolveComment(category, key, comment);
        return target.getInt(key, category, defaultValue, minValue, maxValue, resolvedComment);
    }

    private static float getFloat(String key, String category, float defaultValue, float minValue, float maxValue, String comment) {
        Configuration target = configForCategory(category);
        String resolvedComment = resolveComment(category, key, comment);
        return target.getFloat(key, category, defaultValue, minValue, maxValue, resolvedComment);
    }

    private static String getString(String key, String category, String defaultValue, String comment) {
        Configuration target = configForCategory(category);
        String resolvedComment = resolveComment(category, key, comment);
        return target.getString(key, category, defaultValue, resolvedComment);
    }

    private static String[] getStringList(String key, String category, String[] defaultValue, String comment) {
        Configuration target = configForCategory(category);
        String resolvedComment = resolveComment(category, key, comment);
        return target.getStringList(key, category, defaultValue, resolvedComment);
    }

    private static Map<String, String> createPropertyComments() {
        Map<String, String> comments = new HashMap<>();
        addPropertyComment(comments, "debugLogging",
            "\u662f\u5426\u8f93\u51fa Ad Astra \u8c03\u8bd5\u65e5\u5fd7\u3002",
            "Whether to enable Ad Astra debug logging.");
        addPropertyComment(comments, "disableOxygen",
            "\u662f\u5426\u8df3\u8fc7\u5b9e\u4f53\u6c27\u6c14\u68c0\u67e5\u3001\u6d88\u8017\u548c\u7f3a\u6c27\u4f24\u5bb3\u3002\u4e0d\u5f71\u54cd\u65e0\u6c27\u65b9\u5757\u6548\u679c\u3002",
            "Whether to skip entity oxygen checks, consumption, and suffocation damage. Does not affect airless block effects.");
        addPropertyComment(comments, "disableTemperature",
            "\u662f\u5426\u8df3\u8fc7\u5b9e\u4f53\u6e29\u5ea6\u4f24\u5bb3\u3002\u4e0d\u5f71\u54cd\u6e29\u5ea6\u8ba1\u7b97\u6216\u65e0\u6c27\u65b9\u5757\u6548\u679c\u3002",
            "Whether to skip entity temperature damage. Does not affect temperature calculations or airless block effects.");
        addPropertyComment(comments, "disableGravity",
            "\u662f\u5426\u8df3\u8fc7\u5b9e\u4f53\u91cd\u529b\u8fd0\u52a8\u3001\u8df3\u8dc3\u548c\u6454\u843d\u4f24\u5bb3\u3002\u4e0d\u6539\u53d8\u884c\u661f\u91cd\u529b\u5c5e\u6027\u3002",
            "Whether to skip entity gravity movement, jumping, and fall damage. Does not change planet gravity properties.");
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
            "\u662f\u5426\u542f\u7528\u65e0\u6c27\u884c\u661f\u7684\u65b9\u5757\u6548\u679c\uff0c\u5982\u6c34\u84b8\u53d1\u3001\u7ed3\u51b0\u548c\u6d41\u4f53\u9650\u5236\u3002\u4e0d\u63a7\u5236\u5b9e\u4f53\u6c27\u6c14\u6216\u6e29\u5ea6\u4f24\u5bb3\u3002",
            "Whether to enable airless-planet block effects such as evaporation, freezing, and fluid restrictions. Does not control entity oxygen or temperature damage.");
        addPropertyComment(comments, "planetRandomTickSpeed",
            "\u65e0\u6c27\u73af\u5883\u6bcf\u533a\u5757\u7684\u968f\u673a\u65b9\u5757\u68c0\u67e5\u6b21\u6570\uff0c\u4e0d\u662f\u539f\u7248 randomTickSpeed\u3002\u8bbe\u4e3a 0 \u7981\u7528\u3002",
            "Random block checks per chunk in airless environments; not vanilla randomTickSpeed. Set 0 to disable.");
        addPropertyComment(comments, "enableMachineIdleOptimization",
            "\u662f\u5426\u4f18\u5316\u95f2\u7f6e\u673a\u5668\u3002\u542f\u7528\u540e\u53ef\u51cf\u5c11\u4e0d\u5fc5\u8981\u7684\u5904\u7406\u3002",
            "Whether to optimize idle machines to reduce unnecessary processing.");
        addPropertyComment(comments, "machineTransferInterval",
            "\u673a\u5668\u68c0\u67e5\u7269\u54c1\u3001\u80fd\u91cf\u548c\u6d41\u4f53\u8f93\u5165\u8f93\u51fa\u7684\u95f4\u9694\uff0c\u5355\u4f4d\u4e3a\u523b\u3002",
            "Interval between machine item, energy, and fluid transfer checks, in ticks.");
        addPropertyComment(comments, "gravityNormalizerEnergyMultiplier",
            "\u91cd\u529b\u6b63\u5219\u5316\u5668\u80fd\u91cf\u6d88\u8017\u500d\u7387\u3002\u503c\u8d8a\u5927\u6d88\u8017\u8d8a\u9ad8\u3002",
            "Energy-consumption multiplier for gravity normalizers. Higher values consume more energy.");
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
        addPropertyComment(comments, "ironMaxEnergyInOut",
            "\u94c1\u7ea7\u6bcf\u523b\u6700\u5927 FE \u8f93\u5165/\u8f93\u51fa\u91cf\u3002",
            "Iron tier max FE input/output per tick.");
        addPropertyComment(comments, "ironEnergyCapacity",
            "\u94c1\u7ea7\u5185\u90e8 FE \u5bb9\u91cf\u3002",
            "Iron tier internal FE capacity.");
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
            "Etrionic \u7535\u529b\u9ad8\u7089\u5185\u7f6e\u51b6\u70bc\u914d\u65b9\u7684\u57fa\u7840\u6bcf\u523b FE \u6d88\u8017\u91cf\uff0c\u518d\u5e94\u7528\u673a\u5668\u80fd\u8017\u500d\u7387\u3002",
            "Base FE per tick for built-in Etrionic blasting recipes; the machine energy multiplier is applied.");
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
        addPropertyComment(comments, "planetMobSpawnRateMultiplier",
            "\u654c\u5bf9\u751f\u7269\u5168\u5c40\u5f00\u5173\uff1a\u22640 \u7981\u7528\uff1b>0 \u4fdd\u6301\u9ed8\u8ba4\u751f\u6210\u7387\u3002\n\u8303\u56f4\uff1a0.0-10.0\u3002",
            "Global hostile-mob switch: <=0 disables spawning; >0 keeps the default rate.\nRange: 0.0-10.0.");
        addPropertyComment(comments, "planetEntityCapPerType",
            "\u672a\u914d\u7f6e\u767d\u540d\u5355\u6570\u91cf\u4e0a\u9650\u65f6\u7684\u9ed8\u8ba4\u503c\u3002\u767d\u540d\u5355\u884c\u7684 maxCount \u4f18\u5148\u3002",
            "Default cap when a whitelist row does not set an entity limit. A row's maxCount takes priority.");
        addPropertyComment(comments, "planetMobCountRescanIntervalTicks",
            "\u884c\u661f\u751f\u7269\u6570\u91cf\u5b8c\u6574\u6821\u51c6\u95f4\u9694\uff0c\u5355\u4f4d\u4e3a tick\u3002\u4ec5\u5728\u6709\u73a9\u5bb6\u65f6\u6267\u884c\uff0c0 \u8868\u793a\u4ec5\u9996\u6b21\u6821\u51c6\u3002",
            "Full planet-mob count rescan interval in ticks. Runs only while players are present; 0 means the initial rescan only.");
        addPropertyComment(comments, "planetMobRespawnIntervalTicks",
            "\u81ea\u7136\u751f\u6210\u5b9e\u4f53\u6b7b\u4ea1\u540e\u7684\u91cd\u65b0\u751f\u6210\u51b7\u5374\u65f6\u95f4\uff0c\u5355\u4f4d\u4e3a tick\u3002\u4ec5\u5f71\u54cd\u81ea\u7136\u751f\u6210\uff0c0 \u8868\u793a\u4e0d\u989d\u5916\u9650\u5236\u3002",
            "Natural-spawn cooldown after an entity dies, in ticks. Affects natural spawning only; 0 adds no cooldown.");
        addPropertyComment(comments, "noOxygenEntityWhitelist",
            "\u7f3a\u6c27\u65f6\u53ef\u751f\u5b58\u7684\u989d\u5916\u5b9e\u4f53 ID\uff0c\u6bcf\u884c\u4e00\u9879\u3002\nplanetMobSpawnWhitelist \u4e2d\u7684\u5b9e\u4f53\u4f1a\u81ea\u52a8\u5305\u542b\u5728\u6b64\u89c4\u5219\u4e2d\u3002\n\u652f\u6301\uff1amodid:entity_id\u3001modid:*\u3001\u5b8c\u6574 Java \u7c7b\u540d\u6216\u7b80\u5355\u7c7b\u540d\u3002",
            "Additional entity IDs allowed to live without oxygen, one per line.\nEntities in planetMobSpawnWhitelist are included automatically.\nSupported forms: modid:entity_id, modid:*, a full Java class name, or a simple class name.");
        addPropertyComment(comments, "oreGenerationMultiplier",
            "\u6240\u6709\u884c\u661f\u77ff\u8109\u5217\u8868\u7684\u751f\u6210\u5c1d\u8bd5\u6b21\u6570\u500d\u7387\uff0c\u503c\u8d8a\u9ad8\u6bcf\u533a\u5757\u5c1d\u8bd5\u6b21\u6570\u8d8a\u591a\u3002",
            "Generation-attempt multiplier for all planet ore lists; higher values mean more attempts per chunk.");
        addPropertyComment(comments, "debugWorldgen",
            "\u662f\u5426\u542f\u7528\u77ff\u8109\u751f\u6210\u8c03\u8bd5\u65e5\u5fd7\u3002",
            "Whether to enable debug logging for ore generation.");
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
        if ("dimensions".equals(category)) {
            return DIMENSIONS_COMMENT;
        }
        if (category.startsWith(CATEGORY_PLANET_ORE_PREFIX)) {
            String key = category.substring(CATEGORY_PLANET_ORE_PREFIX.length());
            for (PlanetConfigEntry planet : getPlanetConfigs()) {
                if (planet.key.equals(key)) {
                    return planetOreCategoryComment(planet);
                }
            }
            return WORLDGEN_COMMENT;
        }
        if (category.startsWith(CATEGORY_PLANET_PREFIX)) {
            for (PlanetConfigEntry planet : getPlanetConfigs()) {
                if (planet.category.equals(category)) {
                    return planetCategoryComment(planet);
                }
            }
            return PLANET_COMMENT;
        }
        if ("mobs".equals(category)) {
            return MOBS_COMMENT;
        }
        if ("worldgen".equals(category)) {
            return WORLDGEN_COMMENT;
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
        setCategoryComment("mobs",
            MOBS_COMMENT);
        setCategoryComment("worldgen",
            WORLDGEN_COMMENT);
    }

    private static void setCategoryComment(String category, String comment) {
        Configuration target = configForCategory(category);
        if (target != null) {
            target.setCategoryComment(category, comment);
        }
    }

    private static void syncPlanetTierOverrides() {
        PlanetTierOverrideRegistry.clear();
        for (PlanetConfigEntry planet : getPlanetConfigs()) {
            Integer tier = PLANET_ROCKET_TIERS.get(planet.dimensionId);
            PlanetTierOverrideRegistry.setPlanetTier(planet.dimensionId,
                tier == null ? planet.defaultTier : tier);
        }
        for (ExternalDimensionConfig.ExternalDimensionEntry external : ExternalDimensionConfig.getEntries()) {
            PlanetTierOverrideRegistry.setPlanetTier(external.getDimensionId(), external.getTier());
        }
    }

    private static void syncPlanetDimensionSettings() {
        PLANET_DIMENSIONS.clear();
        PLANET_DIMENSION_ENABLED.clear();
        PLANET_GRAVITY_MULTIPLIERS.clear();
        PLANET_ROCKET_TIERS.clear();
        PLANET_KEYS_BY_DIMENSION.clear();
        for (PlanetConfigEntry planet : getPlanetConfigs()) {
            String category = planet.category;
            int tier = getInt("rocketTier", category, planet.defaultTier, 0, MAX_PLANET_ROCKET_TIER,
                bilingual("\u6700\u4f4e\u706b\u7bad\u7b49\u7ea7\uff0c0 \u8868\u793a\u4e0d\u9650\u5236\u3002", "Minimum rocket tier; 0 means no restriction."));
            setCategoryComment(category, planetCategoryComment(planet));

            PLANET_DIMENSIONS.add(planet.dimensionId);
            PLANET_DIMENSION_ENABLED.put(planet.dimensionId, true);
            PLANET_GRAVITY_MULTIPLIERS.put(planet.dimensionId, 1.0F);
            PLANET_ROCKET_TIERS.put(planet.dimensionId, tier);
            PLANET_KEYS_BY_DIMENSION.put(planet.dimensionId, planet.key);
        }
    }

    private static void syncPlanetHostileMobOverrides() {
        PLANET_HOSTILE_MOB_OVERRIDES.clear();
        for (PlanetConfigEntry planet : getPlanetConfigs()) {
            PLANET_HOSTILE_MOB_OVERRIDES.put(planet.dimensionId, true);
        }
    }

    private static void syncPlanetMobSpawnWhitelist() {
        PLANET_MOB_SPAWN_WHITELIST.clear();
        String comment = bilingual(
            "\u67d0\u884c\u661f\u6ca1\u6709\u767d\u540d\u5355\u884c\uff1a\u4f7f\u7528\u751f\u7269\u7fa4\u7cfb\u9ed8\u8ba4\u751f\u6210\u3002\n"
                + "\u67d0\u884c\u661f\u5b58\u5728\u767d\u540d\u5355\u884c\uff1a\u5b8c\u5168\u63a5\u7ba1\u8be5\u884c\u661f\u7684\u81ea\u7136\u751f\u6210\uff0c\u672a\u5217\u51fa\u7684\u5b9e\u4f53\u4e0d\u4f1a\u751f\u6210\u3002\n"
                + "\u683c\u5f0f\uff1a\u884c\u661f\u952e|\u5b9e\u4f53 ID|\u751f\u6210\u7c7b\u578b|\u751f\u6210\u6743\u91cd|\u6700\u5c0f\u7fa4\u7ec4\u6570\u91cf|\u6700\u5927\u7fa4\u7ec4\u6570\u91cf|\u5b9e\u4f53\u6570\u91cf\u4e0a\u9650\u3002\n"
                + "\u5b57\u6bb5\uff1a\u884c\u661f\u952e=moon \u7b49\uff1b\u5b9e\u4f53 ID=modid:entity_id\uff1b\u751f\u6210\u7c7b\u578b=monster/creature/water/ambient/cave\uff1b\u6743\u91cd=\u751f\u6210\u6743\u91cd\uff1b\u6700\u5c0f/\u6700\u5927\u7fa4\u7ec4=\u5355\u6b21\u751f\u6210\u6570\u91cf\uff1b\u6570\u91cf\u4e0a\u9650=\u8be5\u884c\u661f\u8be5\u5b9e\u4f53\u7684\u6700\u5927\u603b\u6570\uff0c0 \u8868\u793a\u7981\u6b62\u751f\u6210\u3002\n"
                + "\u793a\u4f8b\uff1amoon|minecraft:ender_dragon|monster|1|1|1|1\u3002\n"
                + "\u767d\u540d\u5355\u4e2d\u7684\u6709\u6548\u5b9e\u4f53\u4f1a\u81ea\u52a8\u65e0\u89c6\u7f3a\u6c27\uff0cnoOxygenEntityWhitelist \u53ef\u989d\u5916\u6dfb\u52a0\u5b9e\u4f53\u3002",
            "A planet without whitelist rows uses biome defaults.\n"
                + "A planet with whitelist rows is fully controlled by them; omitted entities do not spawn.\n"
                + "Format: planet key|entity ID|spawn type|spawn weight|min group size|max group size|entity count cap.\n"
                + "Fields: planet key=moon etc.; entity ID=modid:entity_id; spawn type=monster/creature/water/ambient/cave; weight=spawn weight; min/max group=entities per spawn; max count=per-planet per-entity cap, 0 disables spawning.\n"
                + "Example: moon|minecraft:ender_dragon|monster|1|1|1|1.\n"
                + "Valid entities in this whitelist automatically ignore oxygen; noOxygenEntityWhitelist can add more entities.");
        Configuration target = configForCategory("mobs");
        Property whitelist = target.get("mobs", "planetMobSpawnWhitelist", new String[0], comment);
        whitelist.setComment(comment);
        String[] rows = whitelist.getStringList();
        List<String> cleanedRows = new ArrayList<>();
        boolean removedDocumentation = false;
        for (String row : rows) {
            if (isGeneratedMobWhitelistDocumentation(row)) {
                removedDocumentation = true;
            } else {
                cleanedRows.add(row);
            }
        }
        if (removedDocumentation) {
            whitelist.set(cleanedRows.toArray(new String[0]));
        }
        rows = whitelist.getStringList();
        Set<String> surfaceKeys = new HashSet<>();
        for (PlanetConfigEntry planet : getPlanetConfigs()) surfaceKeys.add(planet.key);
        for (String row : rows) {
            PlanetMobSpawnConfig parsed = PlanetMobSpawnConfig.parse(row);
            if (parsed == null) continue;
            if (!surfaceKeys.contains(parsed.planetKey)) {
                AdAstraReborn.LOGGER.warn("Ignored mob whitelist row for unknown surface planet '{}'.", parsed.planetKey);
                continue;
            }
            PLANET_MOB_SPAWN_WHITELIST.computeIfAbsent(parsed.planetKey, key -> new ArrayList<>()).add(parsed);
        }
    }

    private static boolean isGeneratedMobWhitelistDocumentation(String row) {
        if (row == null) {
            return false;
        }
        String value = row.trim();
        return value.startsWith("# \u683c\u5f0f\uff1aplanet|entityId|spawnType|weight|minGroup|maxGroup|maxCount")
            || value.startsWith("# \u793a\u4f8b\uff1amoon|minecraft:ender_dragon|monster|1|1|1|1")
            || value.startsWith("# Format: planet|entityId|spawnType|weight|minGroup|maxGroup|maxCount")
            || value.startsWith("# Example: moon|minecraft:ender_dragon|monster|1|1|1|1");
    }

    private static List<PlanetConfigEntry> getPlanetConfigs() {
        Map<Integer, PlanetConfigEntry> entries = new java.util.LinkedHashMap<>();
        for (PlanetDimensionProperties properties : ModDimensions.getPlanetProperties()) {
            String key = normalizePlanetKey(properties.getName());
            entries.put(properties.getDimensionId(), new PlanetConfigEntry(
                key,
                CATEGORY_PLANET_PREFIX + key,
                properties.getDimensionId(),
                properties.getTier(),
                chinesePlanetName(key),
                englishPlanetName(key)));
        }
        for (CustomPlanetDefinition definition : BuiltInPlanetRegistry.getDefinitions()) {
            addPlanetConfig(entries, definition);
        }
        for (CustomPlanetDefinition definition : CustomPlanetRegistry.getDefinitions()) {
            addPlanetConfig(entries, definition);
        }
        return new ArrayList<>(entries.values());
    }

    private static void addPlanetConfig(Map<Integer, PlanetConfigEntry> entries, CustomPlanetDefinition definition) {
            String key = normalizePlanetKey(definition.getPlanetName());
            String englishName = definition.getDisplayName();
            if (englishName == null || englishName.trim().isEmpty()) {
                englishName = englishPlanetName(key);
            }
            entries.put(definition.getDimensionId(), new PlanetConfigEntry(
                key,
                CATEGORY_PLANET_PREFIX + key,
                definition.getDimensionId(),
                definition.getTier(),
                chinesePlanetName(key),
                englishName));
    }

    private static String planetCategoryComment(PlanetConfigEntry planet) {
        return planet.chineseName + "\uff08" + planet.englishName + "\uff09\uff1a"
            + "\u884c\u661f\u8868\u9762\u7ef4\u5ea6 ID \u7531\u4ee3\u7801\u7ef4\u62a4\uff1b\u706b\u7bad\u7b49\u7ea7\u8303\u56f4 0-15\uff0c0 \u8868\u793a\u4e0d\u9650\u5236\u3002\n"
            + planet.englishName + " (" + planet.category + "): Numeric surface-dimension IDs are code-owned; rocket tier range: 0-15, with 0 meaning no restriction.";
    }

    private static String planetOreCategoryComment(PlanetConfigEntry planet) {
        return planet.chineseName + "\uff08" + planet.englishName + "\uff09\u7684\u77ff\u8109\u914d\u7f6e\uff0c\u53ea\u5f71\u54cd\u65b0\u751f\u6210\u533a\u5757\u3002\n"
            + "Ore settings for " + planet.englishName + "; new chunks only.";
    }

    private static String chinesePlanetName(String key) {
        switch (key) {
            case "moon": return "\u6708\u7403";
            case "mars": return "\u706b\u661f";
            case "mercury": return "\u6c34\u661f";
            case "venus": return "\u91d1\u661f";
            case "glacio": return "\u971c\u539f\u661f";
            case "ceres": return "\u8c37\u795e\u661f";
            case "jupiter": return "\u6728\u661f";
            case "saturn": return "\u571f\u661f";
            case "uranus": return "\u5929\u738b\u661f";
            case "neptune": return "\u6d77\u738b\u661f";
            case "orcus": return "\u5965\u5e93\u65af";
            case "pluto": return "\u51a5\u738b\u661f";
            case "haumea": return "\u598a\u795e\u661f";
            case "quaoar": return "\u521b\u795e\u661f";
            case "makemake": return "\u9e1f\u795e\u661f";
            case "gonggong": return "\u5171\u5de5\u661f";
            case "eris": return "\u960b\u795e\u661f";
            case "sedna": return "\u585e\u5fb7\u5a1c";
            case "proxima_centauri_b": return "\u6bd4\u90bb\u661f b";
            default: return key;
        }
    }

    private static String englishPlanetName(String key) {
        switch (key) {
            case "moon": return "Moon";
            case "mars": return "Mars";
            case "mercury": return "Mercury";
            case "venus": return "Venus";
            case "glacio": return "Glacio";
            case "ceres": return "Ceres";
            case "jupiter": return "Jupiter";
            case "saturn": return "Saturn";
            case "uranus": return "Uranus";
            case "neptune": return "Neptune";
            case "orcus": return "Orcus";
            case "pluto": return "Pluto";
            case "haumea": return "Haumea";
            case "quaoar": return "Quaoar";
            case "makemake": return "Makemake";
            case "gonggong": return "Gonggong";
            case "eris": return "Eris";
            case "sedna": return "Sedna";
            case "proxima_centauri_b": return "Proxima Centauri b";
            default: return key;
        }
    }

    private static String normalizePlanetKey(String value) {
        String source = value == null ? "planet" : value.trim().toLowerCase(Locale.ROOT);
        StringBuilder result = new StringBuilder(source.length());
        for (int i = 0; i < source.length(); i++) {
            char c = source.charAt(i);
            result.append((c >= 'a' && c <= 'z') || (c >= '0' && c <= '9') || c == '_' ? c : '_');
        }
        return result.length() == 0 ? "planet" : result.toString();
    }
    private static final class PlanetConfigEntry {
        private final String key;
        private final String category;
        private final int dimensionId;
        private final int defaultTier;
        private final String chineseName;
        private final String englishName;

        private PlanetConfigEntry(String key, String category, int dimensionId, int defaultTier,
                                  String chineseName, String englishName) {
            this.key = key;
            this.category = category;
            this.dimensionId = dimensionId;
            this.defaultTier = defaultTier;
            this.chineseName = chineseName;
            this.englishName = englishName;
        }
    }

    public static final class PlanetMobSpawnConfig {
        private final String planetKey;
        private final String entityId;
        private final String spawnType;
        private final int weight;
        private final int minGroup;
        private final int maxGroup;
        private final int maxCount;

        private PlanetMobSpawnConfig(String planetKey, String entityId, String spawnType,
                                     int weight, int minGroup, int maxGroup, int maxCount) {
            this.planetKey = planetKey;
            this.entityId = entityId;
            this.spawnType = spawnType;
            this.weight = weight;
            this.minGroup = minGroup;
            this.maxGroup = maxGroup;
            this.maxCount = maxCount;
        }

        private static PlanetMobSpawnConfig parse(String row) {
            if (row == null || row.trim().isEmpty() || row.trim().startsWith("#")) return null;
            String[] parts = row.trim().split("\\|", -1);
            if (parts.length != 7) {
                AdAstraReborn.LOGGER.warn("Ignored invalid mob whitelist row '{}': expected 7 fields.", row);
                return null;
            }
            String planet = normalizePlanetKey(parts[0]);
            String entity = parts[1].trim().toLowerCase(Locale.ROOT);
            String type = parts[2].trim().toLowerCase(Locale.ROOT);
            if (planet.isEmpty() || entity.isEmpty()
                || !("monster".equals(type) || "creature".equals(type) || "water".equals(type)
                    || "ambient".equals(type) || "cave".equals(type))) {
                AdAstraReborn.LOGGER.warn("Ignored invalid mob whitelist row '{}': invalid planet, entity, or type.", row);
                return null;
            }
            try {
                ResourceLocation id = new ResourceLocation(entity);
                int weight = parseRange(parts[3], 1, 1000);
                int minGroup = parseRange(parts[4], 1, 128);
                int maxGroup = parseRange(parts[5], minGroup, 128);
                int maxCount = parseRange(parts[6], 0, 100000);
                return new PlanetMobSpawnConfig(planet, id.toString(), type, weight, minGroup, maxGroup, maxCount);
            } catch (RuntimeException exception) {
                AdAstraReborn.LOGGER.warn("Ignored invalid mob whitelist row '{}': {}", row, exception.getMessage());
                return null;
            }
        }

        private static int parseRange(String value, int min, int max) {
            int parsed = Integer.parseInt(value.trim());
            if (parsed < min || parsed > max) throw new IllegalArgumentException("value out of range");
            return parsed;
        }

        public String getPlanetKey() { return planetKey; }
        public String getEntityId() { return entityId; }
        public String getSpawnType() { return spawnType; }
        public int getWeight() { return weight; }
        public int getMinGroup() { return minGroup; }
        public int getMaxGroup() { return maxGroup; }
        public int getMaxCount() { return maxCount; }
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
        return PLANET_DIMENSIONS.contains(dimensionId);
    }

    public static String getPlanetKeyForDimension(int dimensionId) {
        return PLANET_KEYS_BY_DIMENSION.get(dimensionId);
    }

    public static List<PlanetMobSpawnConfig> getPlanetMobSpawnWhitelist(String planetKey) {
        List<PlanetMobSpawnConfig> entries = PLANET_MOB_SPAWN_WHITELIST.get(normalizePlanetKey(planetKey));
        return entries == null ? Collections.emptyList() : Collections.unmodifiableList(entries);
    }

    public static boolean hasPlanetMobSpawnWhitelist(String planetKey) {
        return !getPlanetMobSpawnWhitelist(planetKey).isEmpty();
    }

    /** Returns true when an entity ID is included in any valid planet spawn row. */
    public static boolean isPlanetMobSpawnEntity(String entityId) {
        if (entityId == null || entityId.trim().isEmpty()) {
            return false;
        }
        String normalized = entityId.trim().toLowerCase(Locale.ROOT);
        for (List<PlanetMobSpawnConfig> entries : PLANET_MOB_SPAWN_WHITELIST.values()) {
            for (PlanetMobSpawnConfig entry : entries) {
                if (entry.getEntityId().equals(normalized)) {
                    return true;
                }
            }
        }
        return false;
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
