package earth.terrarium.adastra.common.config;

import earth.terrarium.adastra.AdAstraReborn;
import earth.terrarium.adastra.Reference;
import earth.terrarium.adastra.common.util.PlanetTierOverrideRegistry;
import earth.terrarium.adastra.common.world.PlanetDimensionProperties;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DimensionType;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class ExternalDimensionConfig {

    private static final String CATEGORY_GENERAL = "general";
    private static final String CATEGORY_DIMENSIONS = "dimensions";
    private static final int MAX_ROCKET_TIER = 15;
    private static final String ENABLE_COMMENT =
        "\u662f\u5426\u5c06\u5916\u90e8\u7ef4\u5ea6\u52a0\u5165 Ad Astra \u706b\u7bad\u661f\u56fe\u548c\u73af\u5883\u6570\u636e\u63a5\u5165\u3002false \u65f6\u5ffd\u7565\u672c\u6587\u4ef6\u4e2d\u7684\u76ee\u6807\u5217\u8868\u3002\n"
            + "Whether to include external dimensions in Ad Astra's rocket star map and environment-data integration. When false, the target list in this file is ignored.";
    private static final String DIMENSION_LIST_COMMENT =
        "\u7a7a\u884c\u548c\u4ee5 # \u5f00\u5934\u7684\u884c\u4f1a\u88ab\u5ffd\u7565\u3002\u6bcf\u884c\u5fc5\u987b\u6b63\u597d\u5305\u542b 6 \u6216 7 \u4e2a\u5b57\u6bb5\uff1b\u7f3a\u5c11\u6216\u591a\u4e8e 7 \u4e2a\u5b57\u6bb5\u7684\u884c\u4f1a\u88ab\u5ffd\u7565\uff1a\u7ef4\u5ea6 ID|\u661f\u56fe\u663e\u793a\u8d44\u6e90 ID(namespace:path)|\u6700\u4f4e\u706b\u7bad\u7b49\u7ea7|\u6e29\u5ea6\uff08\u6574\u6570\uff0c-32768 \u81f3 32767\uff09|\u91cd\u529b\u500d\u7387\uff080.0 \u81f3 10.0\uff09|\u5929\u5149/\u592a\u9633\u80fd\u53d1\u7535\u503c\uff080 \u81f3 1024\uff09|\u53ef\u9009\u661f\u56fe\u663e\u793a\u540d\u79f0\u3002\n"
            + "\u7b2c 2 \u4e2a\u5b57\u6bb5\u7701\u7565 namespace \u65f6\u4f7f\u7528 ad_astra\uff1b\u4e3a\u7a7a\u65f6\u4f7f\u7528 ad_astra:external_<\u7ef4\u5ea6 ID>\u3002\u7b2c 3-6 \u4e2a\u5b57\u6bb5\u8d85\u51fa\u8303\u56f4\u4f1a\u88ab\u9650\u5236\u5230\u8fb9\u754c\uff1b\u4efb\u610f\u6570\u503c\u65e0\u6cd5\u89e3\u6790\u3001\u8d44\u6e90 ID \u65e0\u6548\u6216\u5b57\u6bb5\u6570\u4e0d\u6b63\u786e\u65f6\u6574\u884c\u88ab\u5ffd\u7565\u3002\u706b\u7bad\u7b49\u7ea7\u8303\u56f4\u4e3a 0-15\uff0c0 \u8868\u793a\u4e0d\u9650\u5236\uff1b\u5982\u679c dimensions.cfg \u4e2d\u7684 customDimensionTierOverrides \u5305\u542b\u540c\u4e00\u7ef4\u5ea6 ID\uff0c\u8986\u76d6\u503c\u4f1a\u6210\u4e3a\u6700\u7ec8\u706b\u7bad\u7b49\u7ea7\u3002\n"
            + "\u7b2c 6 \u4e2a\u5b57\u6bb5\u4e3a 0 \u65f6\u7981\u7528\u5929\u5149\uff1b\u6b63\u6570\u540c\u65f6\u4f5c\u4e3a\u592a\u9633\u80fd\u677f\u53d1\u7535\u503c\u3002\u7ef4\u5ea6 ID \u5fc5\u987b\u5df2\u88ab Forge \u6ce8\u518c\uff1b\u672a\u6ce8\u518c\u7684\u7ef4\u5ea6\u4e0d\u4f1a\u51fa\u73b0\u5728\u884c\u661f\u5217\u8868\u6216\u661f\u56fe\u4e2d\uff0c\u4f46\u914d\u7f6e\u884c\u4f1a\u4fdd\u7559\u4ee5\u4fbf\u5c06\u6765\u91cd\u65b0\u4f7f\u7528\u3002\n"
            + "\u540c\u4e00\u7ef4\u5ea6 ID \u7684\u540e\u4e00\u884c\u4f1a\u66f4\u65b0\u524d\u4e00\u884c\u7684\u914d\u7f6e\uff1b\u91cd\u590d ID \u7684\u9996\u6b21\u51fa\u73b0\u4f4d\u7f6e\u4fdd\u6301\u4e0d\u53d8\uff0c\u5df2\u6ce8\u518c\u7684\u884c\u6309\u8be5\u987a\u5e8f\u8fdb\u5165\u661f\u56fe\u3002\u793a\u4f8b\uff08\u4e0d\u4f1a\u81ea\u52a8\u6dfb\u52a0\uff09\uff1a-28|galacticraftcore:moon|1|-173|0.166|24|\u661f\u7cfb-\u6708\u7403\uff1b-1|minecraft:nether|1|80|1.0|0\uff08\u4e0d\u586b\u663e\u793a\u540d\u79f0\uff09\u3002\n"
            + "Blank rows and lines beginning with # are ignored. Each row must contain exactly 6 or 7 fields; rows with fewer than 6 or more than 7 fields are ignored: dimension ID|star-map display resource ID (namespace:path)|minimum rocket tier|temperature (integer, -32768 to 32767)|gravity multiplier (0.0 to 10.0)|sky-light/solar generation value (0 to 1024)|optional star-map display name.\n"
            + "If the namespace is omitted from field 2, ad_astra is used; an empty field 2 uses ad_astra:external_<dimension ID>. Fields 3-6 are clamped to their ranges; an unparseable numeric value, invalid resource ID, or invalid field count rejects the whole row. Rocket tier is clamped to 0-15, and 0 means no restriction. If dimensions.cfg contains the same dimension ID in customDimensionTierOverrides, that override becomes the final rocket tier.\n"
            + "Field 6 set to 0 disables skylight; a positive value also becomes the solar-panel generation value. The dimension ID must already be registered by Forge. Unregistered dimensions are kept in the config but are excluded from the planet list/star map. A later row with the same dimension ID replaces the earlier values; the first occurrence keeps its position, and registered rows use that order in the star map. Examples (not added automatically): -28|galacticraftcore:moon|1|-173|0.166|24|System-Moon; -1|minecraft:nether|1|80|1.0|0 (without a display name).";
    private static final Vec3d DEFAULT_FOG_COLOR = new Vec3d(0.18D, 0.22D, 0.32D);
    private static final Vec3d DEFAULT_SKY_COLOR = new Vec3d(0.28D, 0.36D, 0.52D);
    private static final String[] DEFAULT_DIMENSIONS = new String[0];

    private static Configuration configuration;
    private static boolean enabled = true;
    private static final Map<Integer, ExternalDimensionEntry> ENTRIES = new LinkedHashMap<>();

    private ExternalDimensionConfig() {
    }

    public static void init(File file) {
        configuration = new Configuration(file);
        sync();
    }

    public static void sync() {
        for (Integer dimensionId : ENTRIES.keySet()) {
            PlanetTierOverrideRegistry.removePlanetTier(dimensionId);
        }
        ENTRIES.clear();
        if (configuration == null) {
            return;
        }

        configuration.setCategoryComment(CATEGORY_GENERAL,
            "\u5916\u90e8\u7ef4\u5ea6\u63a5\u5165\u603b\u5f00\u5173\u3002\u8fd9\u91cc\u53ea\u63a7\u5236\u706b\u7bad\u661f\u56fe\u548c\u73af\u5883\u6570\u636e\u63a5\u5165\uff0c\u4e0d\u62e6\u622a\u539f\u7248\u4f20\u9001\u95e8\u6216\u5176\u4ed6 mod \u7684\u8fdb\u5165\u65b9\u5f0f\u3002\n"
                + "External-dimension integration settings. This controls rocket-map and environment-data integration only; it does not block vanilla portals or other mods' travel methods.");
        configuration.setCategoryComment(CATEGORY_DIMENSIONS,
            "\u5916\u90e8\u7ef4\u5ea6\u76ee\u6807\u5217\u8868\u3002\u53ea\u6709 Forge \u5df2\u6ce8\u518c\u7684\u7ef4\u5ea6\u624d\u4f1a\u52a0\u5165\u706b\u7bad\u661f\u56fe\uff1b\u672a\u6ce8\u518c\u7684\u914d\u7f6e\u884c\u4f1a\u4fdd\u7559\u4ee5\u4fbf\u5c06\u6765\u91cd\u65b0\u4f7f\u7528\u3002\u672c\u6587\u4ef6\u53ea\u63d0\u4f9b\u706b\u7bad\u661f\u56fe\u548c\u73af\u5883\u6570\u636e\uff0c\u4e0d\u4f1a\u6ce8\u518c\u7ef4\u5ea6\u6216\u914d\u7f6e\u5730\u5f62\u65b9\u5757\u3002\u4fee\u6539\u540e\u9700\u8981\u91cd\u542f\u6e38\u620f\u6216\u670d\u52a1\u5668\u3002\n"
                + "External-dimension target rows. Only dimensions already registered by Forge are added to the rocket star map; unregistered rows are kept for later reuse. This file supplies rocket-map and environment data only; it does not register dimensions or configure terrain blocks. A game or server restart is required after changes.");

        Property enabledProperty = configuration.get(CATEGORY_GENERAL, "enableExternalDimensionTravel", true, ENABLE_COMMENT);
        enabledProperty.setComment(ENABLE_COMMENT);
        enabled = enabledProperty.getBoolean();

        String[] rows = configuration.getStringList(
            "externalDimensions", CATEGORY_DIMENSIONS, DEFAULT_DIMENSIONS, DIMENSION_LIST_COMMENT);
        Property dimensionList = configuration.get(CATEGORY_DIMENSIONS, "externalDimensions", DEFAULT_DIMENSIONS);
        dimensionList.setComment(DIMENSION_LIST_COMMENT);

        if (enabled) {
            int order = 0;
            for (String row : rows) {
                ExternalDimensionEntry entry = parse(row, order);
                if (entry != null) {
                    ENTRIES.put(entry.getDimensionId(), entry);
                    PlanetTierOverrideRegistry.setPlanetTier(entry.getDimensionId(), entry.getTier());
                    order++;
                }
            }
        }

        // Forge does not mark a property dirty when only its comment changes.
        // Save the file on every sync so existing installations receive updated bilingual comments.
        configuration.save();
    }

    public static boolean isEnabled() {
        return enabled;
    }

    public static boolean isExternalDimension(int dimensionId) {
        return ENTRIES.containsKey(dimensionId);
    }

    public static ExternalDimensionEntry getEntry(int dimensionId) {
        return ENTRIES.get(dimensionId);
    }

    public static List<ExternalDimensionEntry> getEntries() {
        return Collections.unmodifiableList(getRegisteredEntries());
    }

    public static List<PlanetDimensionProperties> getPlanetProperties() {
        List<PlanetDimensionProperties> properties = new ArrayList<>();
        for (ExternalDimensionEntry entry : getRegisteredEntries()) {
            properties.add(entry.toDimensionProperties());
        }
        return properties;
    }

    private static List<ExternalDimensionEntry> getRegisteredEntries() {
        List<ExternalDimensionEntry> registeredEntries = new ArrayList<>();
        int order = 0;
        for (ExternalDimensionEntry entry : ENTRIES.values()) {
            if (isDimensionRegistered(entry.getDimensionId())) {
                registeredEntries.add(entry.withOrder(order++));
            }
        }
        return registeredEntries;
    }

    private static ExternalDimensionEntry parse(String row, int order) {
        if (row == null) {
            return null;
        }
        String trimmed = row.trim();
        if (trimmed.isEmpty() || trimmed.startsWith("#")) {
            return null;
        }

        String[] parts = trimmed.split("\\|", -1);
        if (parts.length != 6 && parts.length != 7) {
            warnInvalid(row, "expected 6 or 7 fields");
            return null;
        }

        try {
            int dimensionId = Integer.parseInt(parts[0].trim());
            ResourceLocation displayId = parseResourceLocation(parts[1], "external_" + dimensionId);
            int tier = clampInt(Integer.parseInt(parts[2].trim()), 0, MAX_ROCKET_TIER);
            short temperature = (short) clampInt(Integer.parseInt(parts[3].trim()), Short.MIN_VALUE, Short.MAX_VALUE);
            float gravity = clampFloat(Float.parseFloat(parts[4].trim()), 0.0F, 10.0F);
            int skyLightMultiplier = clampInt(Integer.parseInt(parts[5].trim()), 0, 1024);
            String displayName = parts.length == 7 ? emptyToNull(parts[6]) : null;
            boolean hasSkyLight = skyLightMultiplier > 0;
            boolean canRespawn = false;
            boolean oxygen = false;
            return new ExternalDimensionEntry(dimensionId, displayId, tier, temperature, gravity,
                skyLightMultiplier, hasSkyLight, canRespawn, oxygen, DEFAULT_FOG_COLOR, DEFAULT_SKY_COLOR,
                order, displayName);
        } catch (RuntimeException exception) {
            warnInvalid(row, exception.getMessage());
            return null;
        }
    }

    private static ResourceLocation parseResourceLocation(String value, String fallbackPath) {
        String trimmed = value == null ? "" : value.trim();
        if (trimmed.isEmpty()) {
            return new ResourceLocation(Reference.MOD_ID, fallbackPath);
        }
        return trimmed.indexOf(':') >= 0 ? new ResourceLocation(trimmed) : new ResourceLocation(Reference.MOD_ID, trimmed);
    }

    private static String emptyToNull(String value) {
        String trimmed = value == null ? "" : value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private static int clampInt(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    private static float clampFloat(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }

    private static void warnInvalid(String row, String reason) {
        AdAstraReborn.LOGGER.warn("Ignored invalid external dimension config row '{}': {}", row, reason);
    }

    private static boolean isDimensionRegistered(int dimensionId) {
        if (DimensionManager.isDimensionRegistered(dimensionId)) {
            return true;
        }
        AdAstraReborn.LOGGER.warn(
            "Ignored external dimension config entry for dimension {} because it is not registered. "
                + "The referenced mod may be missing; the configuration row has been kept for later reuse.",
            dimensionId);
        return false;
    }

    public static final class ExternalDimensionEntry {
        private final int dimensionId;
        private final ResourceLocation displayId;
        private final int tier;
        private final short temperature;
        private final float gravity;
        private final int skyLightMultiplier;
        private final boolean hasSkyLight;
        private final boolean canRespawn;
        private final boolean oxygen;
        private final Vec3d fogColor;
        private final Vec3d skyColor;
        private final int order;
        private final String displayName;

        private ExternalDimensionEntry(int dimensionId, ResourceLocation displayId, int tier, short temperature,
                                       float gravity, int skyLightMultiplier, boolean hasSkyLight, boolean canRespawn, boolean oxygen,
                                       Vec3d fogColor, Vec3d skyColor, int order, String displayName) {
            this.dimensionId = dimensionId;
            this.displayId = displayId;
            this.tier = tier;
            this.temperature = temperature;
            this.gravity = gravity;
            this.skyLightMultiplier = skyLightMultiplier;
            this.hasSkyLight = hasSkyLight;
            this.canRespawn = canRespawn;
            this.oxygen = oxygen;
            this.fogColor = fogColor;
            this.skyColor = skyColor;
            this.order = order;
            this.displayName = displayName;
        }

        public int getDimensionId() {
            return dimensionId;
        }

        public ResourceLocation getDisplayId() {
            return displayId;
        }

        public int getTier() {
            return tier;
        }

        public int getOrder() {
            return order;
        }

        private ExternalDimensionEntry withOrder(int order) {
            return new ExternalDimensionEntry(dimensionId, displayId, tier, temperature, gravity,
                skyLightMultiplier, hasSkyLight, canRespawn, oxygen, fogColor, skyColor, order, displayName);
        }

        public String getDisplayName() {
            return displayName;
        }

        public PlanetDimensionProperties toDimensionProperties() {
            Biome biome = dimensionId == -1 ? Biomes.HELL : dimensionId == 1 ? Biomes.SKY : Biomes.PLAINS;
            DimensionType type = DimensionType.getById(dimensionId);
            String saveFolder = type == null ? "DIM" + dimensionId : type.getSuffix();
            IBlockState terrainPlaceholder = defaultTerrainPlaceholder(dimensionId);
            return new PlanetDimensionProperties(
                displayId.toString().toLowerCase(Locale.ROOT),
                dimensionId,
                saveFolder,
                biome,
                terrainPlaceholder,
                terrainPlaceholder,
                hasSkyLight,
                canRespawn,
                oxygen,
                temperature,
                gravity,
                skyLightMultiplier,
                tier,
                24000,
                fogColor,
                skyColor);
        }

        private static IBlockState defaultTerrainPlaceholder(int dimensionId) {
            if (dimensionId == -1) {
                return Blocks.NETHERRACK.getDefaultState();
            }
            if (dimensionId == 1) {
                return Blocks.END_STONE.getDefaultState();
            }
            return Blocks.STONE.getDefaultState();
        }
    }
}
