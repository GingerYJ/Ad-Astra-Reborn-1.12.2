package earth.terrarium.adastra.common.config;

import earth.terrarium.adastra.common.registry.ModDimensions;
import earth.terrarium.adastra.common.rocket.ConfigurableRocketRegistry;
import earth.terrarium.adastra.common.util.PlanetTierOverrideRegistry;
import net.minecraftforge.common.config.Configuration;

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
        new PlanetTierConfig("moon", ModDimensions.MOON_ID, 1, "\u6708\u7403"),
        new PlanetTierConfig("mars", ModDimensions.MARS_ID, 2, "\u706b\u661f"),
        new PlanetTierConfig("mercury", ModDimensions.MERCURY_ID, 3, "\u6c34\u661f"),
        new PlanetTierConfig("venus", ModDimensions.VENUS_ID, 3, "\u91d1\u661f"),
        new PlanetTierConfig("glacio", ModDimensions.GLACIO_ID, 4, "\u51b0\u5ddd\u661f"),
        new PlanetTierConfig("ceres", ModDimensions.CERES_ID, 3, "\u8c37\u795e\u661f"),
        new PlanetTierConfig("pluto", ModDimensions.PLUTO_ID, 4, "\u51a5\u738b\u661f"),
        new PlanetTierConfig("haumea", ModDimensions.HAUMEA_ID, 4, "\u598a\u795e\u661f"),
        new PlanetTierConfig("kuiper_belt", ModDimensions.KUIPER_BELT_ID, 5, "\u67ef\u4f0a\u4f2f\u5e26"),
        new PlanetTierConfig("io", ModDimensions.IO_ID, 4, "\u6728\u536b\u4e00"),
        new PlanetTierConfig("europa", ModDimensions.EUROPA_ID, 4, "\u6728\u536b\u4e8c"),
        new PlanetTierConfig("ganymede", ModDimensions.GANYMEDE_ID, 4, "\u6728\u536b\u4e09"),
        new PlanetTierConfig("callisto", ModDimensions.CALLISTO_ID, 4, "\u6728\u536b\u56db"),
        new PlanetTierConfig("enceladus", ModDimensions.ENCELADUS_ID, 5, "\u571f\u536b\u4e8c"),
        new PlanetTierConfig("titan", ModDimensions.TITAN_ID, 5, "\u571f\u536b\u516d"),
        new PlanetTierConfig("miranda", ModDimensions.MIRANDA_ID, 5, "\u5929\u536b\u4e94"),
        new PlanetTierConfig("triton", ModDimensions.TRITON_ID, 5, "\u6d77\u536b\u4e00"),
        new PlanetTierConfig("phobos", ModDimensions.PHOBOS_ID, 3, "\u706b\u536b\u4e00"),
        new PlanetTierConfig("jupiter_orbit", ModDimensions.JUPITER_ORBIT_ID, 4, "\u6728\u661f\u8f68\u9053"),
        new PlanetTierConfig("barnarda_c", ModDimensions.BARNARDA_C_ID, 6, "\u5df4\u7eb3\u5fb7C"),
        new PlanetTierConfig("barnarda_c1", ModDimensions.BARNARDA_C1_ID, 6, "\u5df4\u7eb3\u5fb7C1"),
        new PlanetTierConfig("tauceti_f", ModDimensions.TAUCETI_F_ID, 6, "\u5929\u4ed3\u4e94F"),
        new PlanetTierConfig("proxima_b", ModDimensions.PROXIMA_B_ID, 6, "\u6bd4\u90bb\u661fb")
    };

    private static final PlanetMobConfig[] PLANET_MOB_CONFIGS = new PlanetMobConfig[] {
        new PlanetMobConfig("enableHostileMobsOnMoon", ModDimensions.MOON_ID, "\u6708\u7403"),
        new PlanetMobConfig("enableHostileMobsOnMars", ModDimensions.MARS_ID, "\u706b\u661f"),
        new PlanetMobConfig("enableHostileMobsOnMercury", ModDimensions.MERCURY_ID, "\u6c34\u661f"),
        new PlanetMobConfig("enableHostileMobsOnVenus", ModDimensions.VENUS_ID, "\u91d1\u661f"),
        new PlanetMobConfig("enableHostileMobsOnGlacio", ModDimensions.GLACIO_ID, "\u51b0\u5ddd\u661f"),
        new PlanetMobConfig("enableHostileMobsOnCeres", ModDimensions.CERES_ID, "\u8c37\u795e\u661f"),
        new PlanetMobConfig("enableHostileMobsOnPluto", ModDimensions.PLUTO_ID, "\u51a5\u738b\u661f"),
        new PlanetMobConfig("enableHostileMobsOnHaumea", ModDimensions.HAUMEA_ID, "\u598a\u795e\u661f"),
        new PlanetMobConfig("enableHostileMobsOnKuiperBelt", ModDimensions.KUIPER_BELT_ID, "\u67ef\u4f0a\u4f2f\u5e26"),
        new PlanetMobConfig("enableHostileMobsOnIo", ModDimensions.IO_ID, "\u6728\u536b\u4e00"),
        new PlanetMobConfig("enableHostileMobsOnEuropa", ModDimensions.EUROPA_ID, "\u6728\u536b\u4e8c"),
        new PlanetMobConfig("enableHostileMobsOnGanymede", ModDimensions.GANYMEDE_ID, "\u6728\u536b\u4e09"),
        new PlanetMobConfig("enableHostileMobsOnCallisto", ModDimensions.CALLISTO_ID, "\u6728\u536b\u56db"),
        new PlanetMobConfig("enableHostileMobsOnEnceladus", ModDimensions.ENCELADUS_ID, "\u571f\u536b\u4e8c"),
        new PlanetMobConfig("enableHostileMobsOnTitan", ModDimensions.TITAN_ID, "\u571f\u536b\u516d"),
        new PlanetMobConfig("enableHostileMobsOnMiranda", ModDimensions.MIRANDA_ID, "\u5929\u536b\u4e94"),
        new PlanetMobConfig("enableHostileMobsOnTriton", ModDimensions.TRITON_ID, "\u6d77\u536b\u4e00"),
        new PlanetMobConfig("enableHostileMobsOnPhobos", ModDimensions.PHOBOS_ID, "\u706b\u536b\u4e00"),
        new PlanetMobConfig("enableHostileMobsOnBarnardaC", ModDimensions.BARNARDA_C_ID, "\u5df4\u7eb3\u5fb7C"),
        new PlanetMobConfig("enableHostileMobsOnBarnardaC1", ModDimensions.BARNARDA_C1_ID, "\u5df4\u7eb3\u5fb7C1"),
        new PlanetMobConfig("enableHostileMobsOnTauCetiF", ModDimensions.TAUCETI_F_ID, "\u5929\u4ed3\u4e94F"),
        new PlanetMobConfig("enableHostileMobsOnProximaB", ModDimensions.PROXIMA_B_ID, "\u6bd4\u90bb\u661fb")
    };

    private static final PlanetDimensionConfig[] PLANET_DIMENSION_CONFIGS = new PlanetDimensionConfig[] {
        new PlanetDimensionConfig("enableMoonDimension", "moonGravityMultiplier", ModDimensions.MOON_ID, "\u6708\u7403"),
        new PlanetDimensionConfig("enableMarsDimension", "marsGravityMultiplier", ModDimensions.MARS_ID, "\u706b\u661f"),
        new PlanetDimensionConfig("enableMercuryDimension", "mercuryGravityMultiplier", ModDimensions.MERCURY_ID, "\u6c34\u661f"),
        new PlanetDimensionConfig("enableVenusDimension", "venusGravityMultiplier", ModDimensions.VENUS_ID, "\u91d1\u661f"),
        new PlanetDimensionConfig("enableGlacioDimension", "glacioGravityMultiplier", ModDimensions.GLACIO_ID, "\u51b0\u5ddd\u661f"),
        new PlanetDimensionConfig("enableCeresDimension", "ceresGravityMultiplier", ModDimensions.CERES_ID, "\u8c37\u795e\u661f"),
        new PlanetDimensionConfig("enablePlutoDimension", "plutoGravityMultiplier", ModDimensions.PLUTO_ID, "\u51a5\u738b\u661f"),
        new PlanetDimensionConfig("enableHaumeaDimension", "haumeaGravityMultiplier", ModDimensions.HAUMEA_ID, "\u598a\u795e\u661f"),
        new PlanetDimensionConfig("enableKuiperBeltDimension", "kuiperBeltGravityMultiplier", ModDimensions.KUIPER_BELT_ID, "\u67ef\u4f0a\u4f2f\u5e26"),
        new PlanetDimensionConfig("enableIoDimension", "ioGravityMultiplier", ModDimensions.IO_ID, "\u6728\u536b\u4e00"),
        new PlanetDimensionConfig("enableEuropaDimension", "europaGravityMultiplier", ModDimensions.EUROPA_ID, "\u6728\u536b\u4e8c"),
        new PlanetDimensionConfig("enableGanymedeDimension", "ganymedeGravityMultiplier", ModDimensions.GANYMEDE_ID, "\u6728\u536b\u4e09"),
        new PlanetDimensionConfig("enableCallistoDimension", "callistoGravityMultiplier", ModDimensions.CALLISTO_ID, "\u6728\u536b\u56db"),
        new PlanetDimensionConfig("enableEnceladusDimension", "enceladusGravityMultiplier", ModDimensions.ENCELADUS_ID, "\u571f\u536b\u4e8c"),
        new PlanetDimensionConfig("enableTitanDimension", "titanGravityMultiplier", ModDimensions.TITAN_ID, "\u571f\u536b\u516d"),
        new PlanetDimensionConfig("enableMirandaDimension", "mirandaGravityMultiplier", ModDimensions.MIRANDA_ID, "\u5929\u536b\u4e94"),
        new PlanetDimensionConfig("enableTritonDimension", "tritonGravityMultiplier", ModDimensions.TRITON_ID, "\u6d77\u536b\u4e00"),
        new PlanetDimensionConfig("enablePhobosDimension", "phobosGravityMultiplier", ModDimensions.PHOBOS_ID, "\u706b\u536b\u4e00"),
        new PlanetDimensionConfig("enableBarnardaCDimension", "barnardaCGravityMultiplier", ModDimensions.BARNARDA_C_ID, "\u5df4\u7eb3\u5fb7C"),
        new PlanetDimensionConfig("enableBarnardaC1Dimension", "barnardaC1GravityMultiplier", ModDimensions.BARNARDA_C1_ID, "\u5df4\u7eb3\u5fb7C1"),
        new PlanetDimensionConfig("enableTauCetiFDimension", "tauCetiFGravityMultiplier", ModDimensions.TAUCETI_F_ID, "\u5929\u4ed3\u4e94F"),
        new PlanetDimensionConfig("enableProximaBDimension", "proximaBGravityMultiplier", ModDimensions.PROXIMA_B_ID, "\u6bd4\u90bb\u661fb")
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
    public static boolean enableNetherAsPlanet;
    public static int netherRocketTier;
    public static boolean blockVanillaNetherTravelWhenPlanet;
    public static boolean enableEndAsPlanet;
    public static int endRocketTier;
    public static boolean blockVanillaEndTravelWhenPlanet;
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

        ironMaxEnergyInOut = getInt("ironMaxEnergyInOut", "machines", 100, 0, Integer.MAX_VALUE, "Iron tier max FE input/output per tick.");
        ironEnergyCapacity = getInt("ironEnergyCapacity", "machines", 10000, 0, Integer.MAX_VALUE, "Iron tier internal FE capacity.");
        ironFluidCapacity = getInt("ironFluidCapacity", "machines", 0, 0, Integer.MAX_VALUE, "Iron tier internal fluid capacity in mB.");
        steelMaxEnergyInOut = getInt("steelMaxEnergyInOut", "machines", 150, 0, Integer.MAX_VALUE, "Steel tier max FE input/output per tick.");
        steelEnergyCapacity = getInt("steelEnergyCapacity", "machines", 20000, 0, Integer.MAX_VALUE, "Steel tier internal FE capacity.");
        steelFluidCapacity = getInt("steelFluidCapacity", "machines", 3000, 0, Integer.MAX_VALUE, "Steel tier internal fluid capacity in mB.");
        deshMaxEnergyInOut = getInt("deshMaxEnergyInOut", "machines", 250, 0, Integer.MAX_VALUE, "Desh tier max FE input/output per tick.");
        deshEnergyCapacity = getInt("deshEnergyCapacity", "machines", 50000, 0, Integer.MAX_VALUE, "Desh tier internal FE capacity.");
        deshFluidCapacity = getInt("deshFluidCapacity", "machines", 5000, 0, Integer.MAX_VALUE, "Desh tier internal fluid capacity in mB.");
        ostrumMaxEnergyInOut = getInt("ostrumMaxEnergyInOut", "machines", 500, 0, Integer.MAX_VALUE, "Ostrum tier max FE input/output per tick.");
        ostrumEnergyCapacity = getInt("ostrumEnergyCapacity", "machines", 100000, 0, Integer.MAX_VALUE, "Ostrum tier internal FE capacity.");
        ostrumFluidCapacity = getInt("ostrumFluidCapacity", "machines", 10000, 0, Integer.MAX_VALUE, "Ostrum tier internal fluid capacity in mB.");
        coalGeneratorEnergyGenerationPerTick = getInt("coalGeneratorEnergyGenerationPerTick", "machines", 20, 0, Integer.MAX_VALUE, "Coal generator FE generated per tick before multipliers.");
        etrionicBlastFurnaceBlastingEnergyPerItem = getInt("etrionicBlastFurnaceBlastingEnergyPerItem", "machines", 10, 0, Integer.MAX_VALUE, "Etrionic blast furnace FE used per blasting tick before multipliers.");
        waterPumpEnergyPerTick = getInt("waterPumpEnergyPerTick", "machines", 20, 0, Integer.MAX_VALUE, "Water pump FE used per tick before multipliers.");
        waterPumpFluidGenerationPerTick = getInt("waterPumpFluidGenerationPerTick", "machines", 50, 0, Integer.MAX_VALUE, "Water generated by the water pump per tick in mB.");
        energizerEnergyCapacity = getInt("energizerEnergyCapacity", "machines", 2000000, 0, Integer.MAX_VALUE, "Energizer internal FE capacity.");
        maxDistributionBlocks = getInt("maxDistributionBlocks", "machines", 6000, 1, Integer.MAX_VALUE, "Maximum blocks oxygen distributors and gravity normalizers can distribute to.");
        distributionRefreshRate = getInt("distributionRefreshRate", "machines", 100, 1, Integer.MAX_VALUE, "Refresh interval in ticks for distributor and gravity normalizer coverage.");
        pipeRefreshRate = getInt("pipeRefreshRate", "machines", 50, 1, Integer.MAX_VALUE, "Pipe refresh interval in ticks for compatibility with the source config.");

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
            "\u914d\u7f6e\u9879\u3002\u4fee\u6539\u540e\u53ef\u80fd\u9700\u8981\u91cd\u542f\u6e38\u620f/\u670d\u52a1\u5668\u3002");
        enableMoonDimension = getBoolean(
            "enableMoonDimension",
            "dimensions",
            true,
            "\u914d\u7f6e\u9879\u3002\u4fee\u6539\u540e\u53ef\u80fd\u9700\u8981\u91cd\u542f\u6e38\u620f/\u670d\u52a1\u5668\u3002");
        enableMarsDimension = getBoolean(
            "enableMarsDimension",
            "dimensions",
            true,
            "\u914d\u7f6e\u9879\u3002\u4fee\u6539\u540e\u53ef\u80fd\u9700\u8981\u91cd\u542f\u6e38\u620f/\u670d\u52a1\u5668\u3002");
        enableMercuryDimension = getBoolean(
            "enableMercuryDimension",
            "dimensions",
            true,
            "\u914d\u7f6e\u9879\u3002\u4fee\u6539\u540e\u53ef\u80fd\u9700\u8981\u91cd\u542f\u6e38\u620f/\u670d\u52a1\u5668\u3002");
        enableVenusDimension = getBoolean(
            "enableVenusDimension",
            "dimensions",
            true,
            "\u914d\u7f6e\u9879\u3002\u4fee\u6539\u540e\u53ef\u80fd\u9700\u8981\u91cd\u542f\u6e38\u620f/\u670d\u52a1\u5668\u3002");
        enableGlacioDimension = getBoolean(
            "enableGlacioDimension",
            "dimensions",
            true,
            "\u914d\u7f6e\u9879\u3002\u4fee\u6539\u540e\u53ef\u80fd\u9700\u8981\u91cd\u542f\u6e38\u620f/\u670d\u52a1\u5668\u3002");
        enableNetherAsPlanet = getBoolean(
            "enableNetherAsPlanet",
            "dimensions",
            false,
            "\u914d\u7f6e\u9879\u3002\u4fee\u6539\u540e\u53ef\u80fd\u9700\u8981\u91cd\u542f\u6e38\u620f/\u670d\u52a1\u5668\u3002");
        netherRocketTier = getInt(
            "netherRocketTier",
            "dimensions",
            1,
            0,
            7,
            "\u914d\u7f6e\u9879\u3002\u4fee\u6539\u540e\u53ef\u80fd\u9700\u8981\u91cd\u542f\u6e38\u620f/\u670d\u52a1\u5668\u3002");
        blockVanillaNetherTravelWhenPlanet = getBoolean(
            "blockVanillaNetherTravelWhenPlanet",
            "dimensions",
            true,
            "\u914d\u7f6e\u9879\u3002\u4fee\u6539\u540e\u53ef\u80fd\u9700\u8981\u91cd\u542f\u6e38\u620f/\u670d\u52a1\u5668\u3002");
        enableEndAsPlanet = getBoolean(
            "enableEndAsPlanet",
            "dimensions",
            false,
            "\u914d\u7f6e\u9879\u3002\u4fee\u6539\u540e\u53ef\u80fd\u9700\u8981\u91cd\u542f\u6e38\u620f/\u670d\u52a1\u5668\u3002");
        endRocketTier = getInt(
            "endRocketTier",
            "dimensions",
            4,
            0,
            7,
            "\u914d\u7f6e\u9879\u3002\u4fee\u6539\u540e\u53ef\u80fd\u9700\u8981\u91cd\u542f\u6e38\u620f/\u670d\u52a1\u5668\u3002");
        blockVanillaEndTravelWhenPlanet = getBoolean(
            "blockVanillaEndTravelWhenPlanet",
            "dimensions",
            true,
            "\u914d\u7f6e\u9879\u3002\u4fee\u6539\u540e\u53ef\u80fd\u9700\u8981\u91cd\u542f\u6e38\u620f/\u670d\u52a1\u5668\u3002");
        useDedicatedDimensionSaveFolder = getBoolean(
            "useDedicatedDimensionSaveFolder",
            "dimensions",
            true,
            "\u914d\u7f6e\u9879\u3002\u4fee\u6539\u540e\u53ef\u80fd\u9700\u8981\u91cd\u542f\u6e38\u620f/\u670d\u52a1\u5668\u3002");
        dedicatedDimensionSaveFolderName = getString(
            "dedicatedDimensionSaveFolderName",
            "dimensions",
            "AdAstraDimensions",
            "\u914d\u7f6e\u9879\u3002\u4fee\u6539\u540e\u53ef\u80fd\u9700\u8981\u91cd\u542f\u6e38\u620f/\u670d\u52a1\u5668\u3002");
        moonGravityMultiplier = getFloat(
            "moonGravityMultiplier",
            "dimensions",
            1.0f,
            0.0f,
            5.0f,
            "\u914d\u7f6e\u9879\u3002\u4fee\u6539\u540e\u53ef\u80fd\u9700\u8981\u91cd\u542f\u6e38\u620f/\u670d\u52a1\u5668\u3002");
        marsGravityMultiplier = getFloat(
            "marsGravityMultiplier",
            "dimensions",
            1.0f,
            0.0f,
            5.0f,
            "\u914d\u7f6e\u9879\u3002\u4fee\u6539\u540e\u53ef\u80fd\u9700\u8981\u91cd\u542f\u6e38\u620f/\u670d\u52a1\u5668\u3002");
        mercuryGravityMultiplier = getFloat(
            "mercuryGravityMultiplier",
            "dimensions",
            1.0f,
            0.0f,
            5.0f,
            "\u914d\u7f6e\u9879\u3002\u4fee\u6539\u540e\u53ef\u80fd\u9700\u8981\u91cd\u542f\u6e38\u620f/\u670d\u52a1\u5668\u3002");
        venusGravityMultiplier = getFloat(
            "venusGravityMultiplier",
            "dimensions",
            1.0f,
            0.0f,
            5.0f,
            "\u914d\u7f6e\u9879\u3002\u4fee\u6539\u540e\u53ef\u80fd\u9700\u8981\u91cd\u542f\u6e38\u620f/\u670d\u52a1\u5668\u3002");
        glacioGravityMultiplier = getFloat(
            "glacioGravityMultiplier",
            "dimensions",
            1.0f,
            0.0f,
            5.0f,
            "\u914d\u7f6e\u9879\u3002\u4fee\u6539\u540e\u53ef\u80fd\u9700\u8981\u91cd\u542f\u6e38\u620f/\u670d\u52a1\u5668\u3002");
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

        // Planet Tier Configuration
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
        if (netherRocketTier < 0) netherRocketTier = 0;
        if (netherRocketTier > 7) netherRocketTier = 7;
        if (endRocketTier < 0) endRocketTier = 0;
        if (endRocketTier > 7) endRocketTier = 7;
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

        saveChangedConfigurations();
    }

    public static void setRadioVolume(int volume) {
        radioVolume = clampInt(volume, 0, 100);
        Configuration target = configForCategory(Configuration.CATEGORY_GENERAL);
        if (target != null) {
            target.get(Configuration.CATEGORY_GENERAL, "radioVolume", 50, "\u914d\u7f6e\u9879\u3002\u4fee\u6539\u540e\u53ef\u80fd\u9700\u8981\u91cd\u542f\u6e38\u620f/\u670d\u52a1\u5668\u3002").set(radioVolume);
            saveChangedConfigurations();
        }
    }

    public static void setJetSuitEnabled(boolean enabled) {
        jetSuitEnabled = enabled;
        Configuration target = configForCategory("client");
        if (target != null) {
            target.get("client", "jetSuitEnabled", true, "\u914d\u7f6e\u9879\u3002\u4fee\u6539\u540e\u53ef\u80fd\u9700\u8981\u91cd\u542f\u6e38\u620f/\u670d\u52a1\u5668\u3002").set(jetSuitEnabled);
            saveChangedConfigurations();
        }
    }

    public static void setShowOxygenDistributorArea(boolean enabled) {
        showOxygenDistributorArea = enabled;
        Configuration target = configForCategory("client");
        if (target != null) {
            target.get("client", "showOxygenDistributorArea", false, "\u914d\u7f6e\u9879\u3002\u4fee\u6539\u540e\u53ef\u80fd\u9700\u8981\u91cd\u542f\u6e38\u620f/\u670d\u52a1\u5668\u3002").set(showOxygenDistributorArea);
            saveChangedConfigurations();
        }
    }

    public static void setShowGravityNormalizerArea(boolean enabled) {
        showGravityNormalizerArea = enabled;
        Configuration target = configForCategory("client");
        if (target != null) {
            target.get("client", "showGravityNormalizerArea", false, "\u914d\u7f6e\u9879\u3002\u4fee\u6539\u540e\u53ef\u80fd\u9700\u8981\u91cd\u542f\u6e38\u620f/\u670d\u52a1\u5668\u3002").set(showGravityNormalizerArea);
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
        boolean value = defaultValue;
        if (target != null && !target.hasKey(category, key) && legacyHas(category, key)) {
            value = legacyConfiguration.getBoolean(key, category, defaultValue, comment);
        }
        return target.getBoolean(key, category, value, comment);
    }

    private static int getInt(String key, String category, int defaultValue, int minValue, int maxValue, String comment) {
        Configuration target = configForCategory(category);
        int value = defaultValue;
        if (target != null && !target.hasKey(category, key) && legacyHas(category, key)) {
            value = legacyConfiguration.getInt(key, category, defaultValue, minValue, maxValue, comment);
        }
        return target.getInt(key, category, value, minValue, maxValue, comment);
    }

    private static float getFloat(String key, String category, float defaultValue, float minValue, float maxValue, String comment) {
        Configuration target = configForCategory(category);
        float value = defaultValue;
        if (target != null && !target.hasKey(category, key) && legacyHas(category, key)) {
            value = legacyConfiguration.getFloat(key, category, defaultValue, minValue, maxValue, comment);
        }
        return target.getFloat(key, category, value, minValue, maxValue, comment);
    }

    private static String getString(String key, String category, String defaultValue, String comment) {
        Configuration target = configForCategory(category);
        String value = defaultValue;
        if (target != null && !target.hasKey(category, key) && legacyHas(category, key)) {
            value = legacyConfiguration.getString(key, category, defaultValue, comment);
        }
        return target.getString(key, category, value, comment);
    }

    private static String[] getStringList(String key, String category, String[] defaultValue, String comment) {
        Configuration target = configForCategory(category);
        String[] value = defaultValue;
        if (target != null && !target.hasKey(category, key) && legacyHas(category, key)) {
            value = legacyConfiguration.getStringList(key, category, defaultValue, comment);
        }
        return target.getStringList(key, category, value, comment);
    }

    private static String[] getStringListNoLegacy(String key, String category, String[] defaultValue, String comment) {
        Configuration target = configForCategory(category);
        return target.getStringList(key, category, defaultValue, comment);
    }

    private static void saveChangedConfigurations() {
        for (Configuration splitConfiguration : splitConfigurations) {
            if (splitConfiguration != null && splitConfiguration.hasChanged()) {
                splitConfiguration.save();
            }
        }
    }

    private static void setCategoryComments() {
        setCategoryComment(Configuration.CATEGORY_GENERAL,
            "\u914d\u7f6e\u9879\u3002\u4fee\u6539\u540e\u53ef\u80fd\u9700\u8981\u91cd\u542f\u6e38\u620f/\u670d\u52a1\u5668\u3002");
        setCategoryComment("performance",
            "\u914d\u7f6e\u9879\u3002\u4fee\u6539\u540e\u53ef\u80fd\u9700\u8981\u91cd\u542f\u6e38\u620f/\u670d\u52a1\u5668\u3002");
        setCategoryComment("client",
            "\u914d\u7f6e\u9879\u3002\u4fee\u6539\u540e\u53ef\u80fd\u9700\u8981\u91cd\u542f\u6e38\u620f/\u670d\u52a1\u5668\u3002");
        setCategoryComment("balance",
            "\u914d\u7f6e\u9879\u3002\u4fee\u6539\u540e\u53ef\u80fd\u9700\u8981\u91cd\u542f\u6e38\u620f/\u670d\u52a1\u5668\u3002");
        setCategoryComment("machines",
            "\u914d\u7f6e\u9879\u3002\u4fee\u6539\u540e\u53ef\u80fd\u9700\u8981\u91cd\u542f\u6e38\u620f/\u670d\u52a1\u5668\u3002");
        setCategoryComment("environment",
            "\u914d\u7f6e\u9879\u3002\u4fee\u6539\u540e\u53ef\u80fd\u9700\u8981\u91cd\u542f\u6e38\u620f/\u670d\u52a1\u5668\u3002");
        setCategoryComment("dimensions",
            "\u914d\u7f6e\u9879\u3002\u4fee\u6539\u540e\u53ef\u80fd\u9700\u8981\u91cd\u542f\u6e38\u620f/\u670d\u52a1\u5668\u3002");
        setCategoryComment(CATEGORY_PLANET_TIERS,
            "\u914d\u7f6e\u9879\u3002\u4fee\u6539\u540e\u53ef\u80fd\u9700\u8981\u91cd\u542f\u6e38\u620f/\u670d\u52a1\u5668\u3002");
        setCategoryComment("mobs",
            "\u914d\u7f6e\u9879\u3002\u4fee\u6539\u540e\u53ef\u80fd\u9700\u8981\u91cd\u542f\u6e38\u620f/\u670d\u52a1\u5668\u3002");
        setCategoryComment("worldgen",
            "\u914d\u7f6e\u9879\u3002\u4fee\u6539\u540e\u53ef\u80fd\u9700\u8981\u91cd\u542f\u6e38\u620f/\u670d\u52a1\u5668\u3002");
        setCategoryComment("debug",
            "\u8c03\u8bd5\u914d\u7f6e\uff1a\u5f00\u542f\u5bf9\u5e94\u7cfb\u7edf\u7684\u989d\u5916\u65e5\u5fd7\uff0c\u4fbf\u4e8e\u6392\u67e5\u95ee\u9898\u3002");
    }

    private static void setCategoryComment(String category, String comment) {
        Configuration target = configForCategory(category);
        if (target != null) {
            target.setCategoryComment(category, comment);
        }
    }

    private static void syncPlanetTierOverrides(Configuration configuration) {
        for (PlanetTierConfig planet : PLANET_TIER_CONFIGS) {
            int tier = getInt(
                planet.key,
                CATEGORY_PLANET_TIERS,
                planet.defaultTier,
                0,
                64,
                planet.displayName + "\u9700\u8981\u7684\u6700\u4f4e\u706b\u7bad\u7b49\u7ea7\u3002\u9ed8\u8ba4\uff1a" + planet.defaultTier + "\u30020 \u8868\u793a\u4e0d\u9650\u5236\uff1b\u4fee\u6539\u540e\u9700\u8981\u91cd\u542f\u6e38\u620f/\u670d\u52a1\u5668\u3002");
            PlanetTierOverrideRegistry.setPlanetTier(planet.dimensionId, tier);
        }

        PlanetTierOverrideRegistry.setPlanetTier(PlanetTravelDimensionIds.NETHER, netherRocketTier);
        PlanetTierOverrideRegistry.setPlanetTier(PlanetTravelDimensionIds.END, endRocketTier);

        String[] overrides = getStringListNoLegacy(
            "customDimensionTierOverrides",
            CATEGORY_PLANET_TIERS,
            new String[0],
            "\u914d\u7f6e\u9879\u3002\u4fee\u6539\u540e\u53ef\u80fd\u9700\u8981\u91cd\u542f\u6e38\u620f/\u670d\u52a1\u5668\u3002");
        for (String override : overrides) {
            applyCustomPlanetTierOverride(override);
        }
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
            int tier = Integer.parseInt(trimmed.substring(separator + 1).trim());
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
                "\u914d\u7f6e\u9879\u3002\u4fee\u6539\u540e\u53ef\u80fd\u9700\u8981\u91cd\u542f\u6e38\u620f/\u670d\u52a1\u5668\u3002");
            float gravity = getFloat(
                planet.gravityKey,
                "dimensions",
                1.0f,
                0.0f,
                5.0f,
                planet.displayName + "\u91cd\u529b\u500d\u7387\u3002\u6b64\u9879\u4f1a\u4e58\u5728\u8be5\u884c\u661f\u57fa\u7840\u91cd\u529b\u4e0a\uff1b1.0 \u8868\u793a\u4f7f\u7528\u9ed8\u8ba4\u57fa\u7840\u91cd\u529b\u3002");
            PLANET_DIMENSION_ENABLED.put(planet.dimensionId, enabled);
            PLANET_GRAVITY_MULTIPLIERS.put(planet.dimensionId, gravity);
        }

        String[] enabledOverrides = getStringListNoLegacy(
            "customDimensionEnabledOverrides",
            "dimensions",
            new String[0],
            "\u914d\u7f6e\u9879\u3002\u4fee\u6539\u540e\u53ef\u80fd\u9700\u8981\u91cd\u542f\u6e38\u620f/\u670d\u52a1\u5668\u3002");
        for (String override : enabledOverrides) {
            applyCustomDimensionEnabledOverride(override);
        }

        String[] gravityOverrides = getStringListNoLegacy(
            "customDimensionGravityOverrides",
            "dimensions",
            new String[0],
            "\u914d\u7f6e\u9879\u3002\u4fee\u6539\u540e\u53ef\u80fd\u9700\u8981\u91cd\u542f\u6e38\u620f/\u670d\u52a1\u5668\u3002");
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
                "\u914d\u7f6e\u9879\u3002\u4fee\u6539\u540e\u53ef\u80fd\u9700\u8981\u91cd\u542f\u6e38\u620f/\u670d\u52a1\u5668\u3002");
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

        private PlanetTierConfig(String key, int dimensionId, int defaultTier, String displayName) {
            this.key = key;
            this.dimensionId = dimensionId;
            this.defaultTier = defaultTier;
            this.displayName = displayName;
        }
    }

    private static final class PlanetMobConfig {
        private final String key;
        private final int dimensionId;
        private final String displayName;

        private PlanetMobConfig(String key, int dimensionId, String displayName) {
            this.key = key;
            this.dimensionId = dimensionId;
            this.displayName = displayName;
        }
    }

    private static final class PlanetDimensionConfig {
        private final String enableKey;
        private final String gravityKey;
        private final int dimensionId;
        private final String displayName;

        private PlanetDimensionConfig(String enableKey, String gravityKey, int dimensionId, String displayName) {
            this.enableKey = enableKey;
            this.gravityKey = gravityKey;
            this.dimensionId = dimensionId;
            this.displayName = displayName;
        }
    }

    private static final class PlanetTravelDimensionIds {
        private static final int NETHER = -1;
        private static final int END = 1;
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
