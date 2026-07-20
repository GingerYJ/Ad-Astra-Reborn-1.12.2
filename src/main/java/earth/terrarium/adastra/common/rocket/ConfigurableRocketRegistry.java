package earth.terrarium.adastra.common.rocket;

import earth.terrarium.adastra.AdAstraReborn;
import earth.terrarium.adastra.Reference;
import earth.terrarium.adastra.common.items.ConfigurableRocketItem;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import javax.annotation.Nullable;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * \u8bfb\u53d6\u5e76\u63d0\u4f9b\u914d\u7f6e\u6587\u4ef6\u4e2d\u5b9a\u4e49\u7684\u706b\u7bad\u53d8\u4f53\u3002
 * Reads and exposes rocket variants defined in the configuration file.
 *
 * \u6bcf\u884c\u683c\u5f0f / Format per row:
 * id|displayName|rocketTier|fuelCapacity|modelTier|texture
 */
public final class ConfigurableRocketRegistry {

    private static final String CATEGORY = "rockets";
    public static final int MIN_ROCKET_TIER = 16;
    public static final int MAX_ROCKET_TIER = 255;
    public static final int MIN_MODEL_TIER = 1;
    public static final int MAX_MODEL_TIER = 15;
    private static final String CATEGORY_COMMENT =
        "\u81ea\u5b9a\u4e49\u706b\u7bad\u3002\nCustom rockets.";
    private static final String CUSTOM_ROCKETS_PROPERTY_COMMENT =
        "\u683c\u5f0f\uff1aID|\u663e\u793a\u540d\u79f0|\u706b\u7bad\u7b49\u7ea7|\u71c3\u6599\u5bb9\u91cf|\u6a21\u578b\u7b49\u7ea7|\u8d34\u56fe\u3002\n"
            + "\u5b57\u6bb5\uff1aID=\u914d\u7f6e ID\uff1b\u663e\u793a\u540d\u79f0=\u754c\u9762\u540d\u79f0\uff1b\u706b\u7bad\u7b49\u7ea7=\u53d1\u5c04\u7b49\u7ea7\uff1b\u71c3\u6599\u5bb9\u91cf\u5355\u4f4d\u4e3a mB\uff1b\u6a21\u578b\u7b49\u7ea7=\u6a21\u578b\u7f16\u53f7\u3002\n"
            + "\u8303\u56f4\uff1a\u706b\u7bad\u7b49\u7ea7 16-255\uff1b\u71c3\u6599\u5bb9\u91cf 1000-64000 mB\uff1b\u6a21\u578b\u7b49\u7ea7 1-15\u3002\n"
            + "\u8d34\u56fe\uff1a\u7559\u7a7a\u4f7f\u7528\u5185\u7f6e\u8d34\u56fe\uff1b\u76f8\u5bf9 PNG \u653e\u5728 rocket_png \u6587\u4ef6\u5939\u3002\n"
            + "Format: id|displayName|rocketTier|fuelCapacity|modelTier|texture.\n"
            + "Fields: id=config ID; displayName=UI name; rocketTier=launch tier; fuelCapacity=maximum fuel in mB; modelTier=model number.\n"
            + "Ranges: rocket tier 16-255; fuel capacity 1000-64000 mB; model tier 1-15.\n"
            + "Texture: leave empty for the built-in texture; put relative PNG files in rocket_png.";
    private static final String[] DEFAULT_CUSTOM_ROCKETS = new String[] {
        "# \u4e2d\u6587\u793a\u4f8b\uff1aexample_rocket|\u793a\u4f8b\u9ad8\u7ea7\u706b\u7bad|16|18000|8|",
        "# Example: example_rocket|Example Advanced Rocket|16|18000|8|"
    };
    private static final Map<String, ConfigurableRocketSpec> ROCKETS = new LinkedHashMap<>();
    private static final List<ConfigurableRocketSpec> ROCKET_LIST = new ArrayList<>();
    private static File rocketPngFolder;
    private static boolean loaded;

    private ConfigurableRocketRegistry() {
    }

    public static void init(File file) {
        ROCKETS.clear();
        ROCKET_LIST.clear();
        File parent = file.getParentFile();
        if (parent == null) {
            parent = new File("config/ad_astra");
        }
        rocketPngFolder = new File(parent, "rocket_png");
        if (!rocketPngFolder.exists()) {
            rocketPngFolder.mkdirs();
        }
        Configuration config = new Configuration(file);
        config.setCategoryComment(CATEGORY, CATEGORY_COMMENT);

        Property customRocketsProperty = config.get(
            CATEGORY,
            "customRockets",
            DEFAULT_CUSTOM_ROCKETS,
            CUSTOM_ROCKETS_PROPERTY_COMMENT);
        customRocketsProperty.setComment(CUSTOM_ROCKETS_PROPERTY_COMMENT);
        String[] existingRows = customRocketsProperty.getStringList();
        String[] rows = mergeDefaultCommentRows(existingRows);
        rows = migrateLegacyRows(rows);
        if (!sameRows(rows, existingRows)) {
            customRocketsProperty.set(rows);
        }

        for (String row : rows) {
            ConfigurableRocketSpec spec = parse(row);
            if (spec != null) {
                ROCKETS.put(spec.getId(), spec);
                ROCKET_LIST.add(spec);
            }
        }

        config.save();
        loaded = true;
    }

    private static String[] mergeDefaultCommentRows(String[] rows) {
        List<String> merged = new ArrayList<>();
        if (rows != null) {
            for (String row : rows) {
                if (!isLegacyRocketComment(row)) {
                    merged.add(row);
                }
            }
        }
        for (String defaultRow : DEFAULT_CUSTOM_ROCKETS) {
            if (!containsRow(merged, defaultRow)) {
                merged.add(defaultRow);
            }
        }
        return merged.toArray(new String[merged.size()]);
    }

    private static String[] migrateLegacyRows(String[] rows) {
        if (rows == null || rows.length == 0) {
            return rows;
        }
        String[] migrated = rows.clone();
        for (int i = 0; i < migrated.length; i++) {
            String row = migrated[i];
            if (row == null || row.trim().isEmpty() || row.trim().startsWith("#")) {
                continue;
            }
            String[] parts = row.trim().split("\\|", -1);
            if (parts.length < 6) {
                continue;
            }
            int tier;
            try {
                tier = Integer.parseInt(parts[2].trim());
            } catch (Exception ignored) {
                continue;
            }
            if (tier < MIN_ROCKET_TIER) {
                String id = sanitizeId(parts[0]);
                AdAstraReborn.LOGGER.warn(
                    "Configurable rocket '{}' uses legacy rocket tier {}; rewriting it to tier {}.",
                    id, tier, MIN_ROCKET_TIER);
                parts[2] = Integer.toString(MIN_ROCKET_TIER);
                migrated[i] = joinRow(parts);
            }
        }
        return migrated;
    }

    private static String joinRow(String[] parts) {
        StringBuilder row = new StringBuilder();
        for (int i = 0; i < parts.length; i++) {
            if (i > 0) {
                row.append('|');
            }
            row.append(parts[i]);
        }
        return row.toString();
    }

    private static boolean isLegacyRocketComment(String row) {
        if (row == null) {
            return false;
        }
        String normalized = row.trim().toLowerCase(Locale.ROOT);
        if (!normalized.startsWith("#")) {
            return false;
        }
        return normalized.startsWith("# configure custom rockets")
            || normalized.startsWith("# format: id|display name|rocket tier")
            || normalized.startsWith("# rocket tier:")
            || normalized.startsWith("# put a relative texture")
            || normalized.startsWith("# an empty texture")
            || normalized.contains("id|displayname|rockettier|fuelcapacity|modeltier|texture")
            || normalized.contains("example_rocket|example advanced rocket|16|18000|8|")
            || normalized.contains("custom_tier_8_rocket|")
            || normalized.contains("example_tier_");
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

    public static boolean isLoaded() {
        return loaded;
    }

    public static List<ConfigurableRocketSpec> getRockets() {
        return Collections.unmodifiableList(ROCKET_LIST);
    }

    /** Registers a code-defined rocket without exposing it as a user config row. */
    public static synchronized void registerBuiltIn(ConfigurableRocketSpec spec) {
        if (spec == null || spec.getId() == null || spec.getId().isEmpty()) {
            return;
        }
        if (isReservedId(spec.getId())
            || spec.getTier() < MIN_ROCKET_TIER
            || spec.getTier() > MAX_ROCKET_TIER
            || spec.getModelTier() < MIN_MODEL_TIER
            || spec.getModelTier() > MAX_MODEL_TIER) {
            return;
        }
        // A user-defined row owns its ID. Built-in content must never replace it.
        if (ROCKETS.containsKey(spec.getId())) {
            return;
        }
        ROCKETS.put(spec.getId(), spec);
        ROCKET_LIST.add(spec);
    }

    @Nullable
    public static ConfigurableRocketSpec get(String id) {
        if (id == null) {
            return null;
        }
        return ROCKETS.get(id);
    }

    @Nullable
    public static ConfigurableRocketItem getItem(String id) {
        ConfigurableRocketSpec spec = get(id);
        return spec == null ? null : spec.getItem();
    }

    public static ConfigurableRocketSpec fallback() {
        ConfigurableRocketSpec fallback = ROCKET_LIST.isEmpty() ? null : ROCKET_LIST.get(0);
        if (fallback != null) {
            return fallback;
        }
        return ConfigurableRocketSpec.fallback();
    }

    @Nullable
    private static ConfigurableRocketSpec parse(String row) {
        if (row == null) {
            return null;
        }
        String trimmed = row.trim();
        if (trimmed.isEmpty() || trimmed.startsWith("#")) {
            return null;
        }

        String[] parts = trimmed.split("\\|", -1);
        if (parts.length < 6) {
            return null;
        }

        String id = sanitizeId(parts[0]);
        if (id.isEmpty() || isReservedId(id) || ROCKETS.containsKey(id)) {
            return null;
        }

        String displayName = parts[1].trim();
        if (displayName.isEmpty()) {
            displayName = id;
        }

        int tier = parseRocketTier(id, parts[2]);
        int fuelCapacity = parseInt(parts[3], 1000, 64000, 18000);
        int modelTier = parseInt(parts[4], MIN_MODEL_TIER, MAX_MODEL_TIER, MAX_MODEL_TIER);
        ParsedTexture texture = parseTexture(id, parts[5], modelTier);

        return new ConfigurableRocketSpec(id, displayName, tier, fuelCapacity, modelTier,
            texture.location, texture.externalFile, texture.displayName, false, modelTier >= 8);
    }

    private static int parseRocketTier(String id, String value) {
        int parsed;
        try {
            parsed = Integer.parseInt(value.trim());
        } catch (Exception exception) {
            AdAstraReborn.LOGGER.warn(
                "Configurable rocket '{}' has an invalid rocket tier '{}'; using tier {}.",
                id, value, MIN_ROCKET_TIER);
            return MIN_ROCKET_TIER;
        }
        if (parsed < MIN_ROCKET_TIER) {
            AdAstraReborn.LOGGER.warn(
                "Configurable rocket '{}' uses legacy rocket tier {}; migrating it to tier {}.",
                id, parsed, MIN_ROCKET_TIER);
            return MIN_ROCKET_TIER;
        }
        if (parsed > MAX_ROCKET_TIER) {
            AdAstraReborn.LOGGER.warn(
                "Configurable rocket '{}' has rocket tier {}; clamping it to {}.",
                id, parsed, MAX_ROCKET_TIER);
            return MAX_ROCKET_TIER;
        }
        return parsed;
    }

    private static int parseInt(String value, int min, int max, int fallback) {
        try {
            int parsed = Integer.parseInt(value.trim());
            if (parsed < min) parsed = min;
            if (parsed > max) parsed = max;
            return parsed;
        } catch (Exception ignored) {
            return fallback;
        }
    }

    private static ParsedTexture parseTexture(String id, String raw, int modelTier) {
        String value = raw == null ? "" : raw.trim();
        if (value.isEmpty()) {
            value = "rocket/tier_" + modelTier + "_rocket";
        }

        boolean resourcePath = value.indexOf(':') >= 0
            || value.startsWith("textures/")
            || value.startsWith("entity/")
            || value.startsWith("rocket/");
        if (!resourcePath) {
            String relative = value.replace('\\', '/');
            if (relative.startsWith("/") || new File(relative).isAbsolute() || relative.contains("..")) {
                AdAstraReborn.LOGGER.warn(
                    "Configurable rocket '{}' has an unsafe external texture path '{}'; using its ID as the file name.",
                    id, value);
                relative = id + ".png";
            }
            if (!relative.toLowerCase(Locale.ROOT).endsWith(".png")) {
                relative = relative + ".png";
            }
            File file = new File(rocketPngFolder == null ? new File("config/ad_astra/rocket_png") : rocketPngFolder, relative);
            ResourceLocation dynamicLocation = new ResourceLocation(Reference.MOD_ID, "textures/config_rocket_png/" + id + ".png");
            return new ParsedTexture(dynamicLocation, file, relative);
        }

        String namespace = Reference.MOD_ID;
        String path = value;
        int colon = value.indexOf(':');
        if (colon >= 0) {
            namespace = value.substring(0, colon).trim();
            path = value.substring(colon + 1).trim();
        }
        if (namespace.isEmpty()) {
            namespace = Reference.MOD_ID;
        }
        path = path.replace('\\', '/');
        if (!path.startsWith("textures/")) {
            if (!path.startsWith("entity/rocket/") && !path.startsWith("rocket/")) {
                path = "rocket/" + path;
            }
            if (path.startsWith("rocket/")) {
                path = "entity/" + path;
            }
            path = "textures/" + path;
        }
        if (!path.endsWith(".png")) {
            path = path + ".png";
        }
        ResourceLocation location = new ResourceLocation(namespace, path);
        return new ParsedTexture(location, null, location.toString());
    }

    private static String sanitizeId(String raw) {
        if (raw == null) {
            return "";
        }
        String lower = raw.trim().toLowerCase(Locale.ROOT);
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < lower.length(); i++) {
            char c = lower.charAt(i);
            if ((c >= 'a' && c <= 'z') || (c >= '0' && c <= '9') || c == '_' || c == '-') {
                builder.append(c == '-' ? '_' : c);
            } else {
                builder.append('_');
            }
        }
        while (builder.length() > 0 && builder.charAt(0) == '_') {
            builder.deleteCharAt(0);
        }
        return builder.toString();
    }

    private static boolean isReservedId(String id) {
        return isBuiltInRocketItemId(id)
            || "tier_1_rocket".equals(id)
            || "tier_2_rocket".equals(id)
            || "tier_3_rocket".equals(id)
            || "tier_4_rocket".equals(id)
            || "tier_5_rocket".equals(id)
            || "tier_6_rocket".equals(id)
            || "tier_7_rocket".equals(id)
            || "tier_8_rocket".equals(id)
            || "tier_9_rocket".equals(id)
            || "tier_10_rocket".equals(id)
            || "tier_11_rocket".equals(id)
            || "tier_12_rocket".equals(id)
            || "tier_13_rocket".equals(id)
            || "tier_14_rocket".equals(id)
            || "tier_15_rocket".equals(id)
            || "tier_1_rover".equals(id)
            || "configurable_rocket".equals(id);
    }

    private static boolean isBuiltInRocketItemId(String id) {
        if (id == null || !id.startsWith("item_tier_") || !id.endsWith("_rocket")) {
            return false;
        }
        try {
            int tier = Integer.parseInt(id.substring("item_tier_".length(), id.length() - "_rocket".length()));
            return tier >= 1 && tier <= 15;
        } catch (NumberFormatException ignored) {
            return false;
        }
    }

    private static final class ParsedTexture {
        private final ResourceLocation location;
        private final File externalFile;
        private final String displayName;

        private ParsedTexture(ResourceLocation location, @Nullable File externalFile, String displayName) {
            this.location = location;
            this.externalFile = externalFile;
            this.displayName = displayName;
        }
    }
}
