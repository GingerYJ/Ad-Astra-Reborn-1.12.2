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
    private static final String DIMENSION_LIST_COMMENT =
        "\u6bcf\u884c\u683c\u5f0f\uff1a\u7ef4\u5ea6ID|\u663e\u793aID|\u6700\u4f4e\u706b\u7bad\u7b49\u7ea7|\u6e29\u5ea6|\u91cd\u529b\u500d\u7387|\u5929\u5149\u500d\u7387|\u663e\u793a\u540d\u79f0\uff08\u53ef\u9009\uff09\u3002\n"
            + "\u793a\u4f8b\uff08\u9ed8\u8ba4\u4e0d\u4f1a\u81ea\u52a8\u6dfb\u52a0\uff09\uff1a-28|galacticraftcore:moon|1|-173|0.166|24|\u661f\u7cfb-\u6708\u7403\n"
            + "\u793a\u4f8b\uff08\u4e0d\u586b\u663e\u793a\u540d\u79f0\uff09\uff1a-1|minecraft:nether|1|80|1.0|0\n"
            + "\u6700\u4f4e\u706b\u7bad\u7b49\u7ea7 0 \u8868\u793a\u4e0d\u9650\u5236\u3002\u5916\u90e8\u7ef4\u5ea6\u4f1a\u6309\u672c\u5217\u8868\u7684\u6dfb\u52a0\u987a\u5e8f\uff0c\u5728\u661f\u56fe\u4e0a\u4ece\u5185\u5411\u5916\u6392\u5217\u3002";
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
            "\u5916\u90e8\u7ef4\u5ea6\u63a5\u5165\u603b\u5f00\u5173\u3002\u8fd9\u91cc\u53ea\u63a7\u5236\u706b\u7bad\u661f\u56fe\u548c\u73af\u5883\u6570\u636e\u63a5\u5165\uff0c\u4e0d\u62e6\u622a\u539f\u7248\u4f20\u9001\u95e8\u6216\u5176\u4ed6 mod \u7684\u8fdb\u5165\u65b9\u5f0f\u3002");
        configuration.setCategoryComment(CATEGORY_DIMENSIONS,
            "\u5916\u90e8\u7ef4\u5ea6\u76ee\u6807\u5217\u8868\u3002\u4fee\u6539\u540e\u9700\u8981\u91cd\u542f\u6e38\u620f/\u670d\u52a1\u5668\u3002\u5916\u90e8\u7ef4\u5ea6\u4f7f\u7528\u81ea\u5df1\u7684\u5730\u5f62\uff0c\u672c\u6587\u4ef6\u4e0d\u914d\u7f6e\u8868\u9762\u65b9\u5757\u6216\u586b\u5145\u65b9\u5757\u3002");

        enabled = configuration.getBoolean("enableExternalDimensionTravel", CATEGORY_GENERAL, true,
            "\u662f\u5426\u542f\u7528\u5916\u90e8\u7ef4\u5ea6\u706b\u7bad\u63a5\u5165\u3002false \u65f6\u5ffd\u7565\u672c\u6587\u4ef6\u4e2d\u7684\u76ee\u6807\u5217\u8868\u3002");

        String[] rows = configuration.getStringList(
            "externalDimensions", CATEGORY_DIMENSIONS, DEFAULT_DIMENSIONS, DIMENSION_LIST_COMMENT);
        Property dimensionList = configuration.get(CATEGORY_DIMENSIONS, "externalDimensions", DEFAULT_DIMENSIONS);
        boolean commentChanged = !DIMENSION_LIST_COMMENT.equals(dimensionList.getComment());
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

        if (configuration.hasChanged() || commentChanged) {
            configuration.save();
        }
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
            int tier = clampInt(Integer.parseInt(parts[2].trim()), 0, 64);
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
