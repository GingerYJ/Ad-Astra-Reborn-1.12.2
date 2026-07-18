package earth.terrarium.adastra.common.rocket;

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
    private static final int MAX_ROCKET_TIER = 15;
    private static final int MAX_MODEL_TIER = 12;
    private static final String CATEGORY_COMMENT =
        "\u914d\u7f6e\u989d\u5916\u81ea\u5b9a\u4e49\u706b\u7bad\u3002\u8fd9\u4e9b\u706b\u7bad\u4f1a\u8ffd\u52a0\u5230\u5185\u7f6e\u706b\u7bad\u5217\u8868\uff1b\u706b\u7bad\u7b49\u7ea7\u51b3\u5b9a\u53ef\u5230\u8fbe\u7684\u884c\u661f\uff0c\u6a21\u578b\u7b49\u7ea7\u53ea\u5f71\u54cd\u5916\u89c2\u3002\u8be6\u7ec6\u5b57\u6bb5\u683c\u5f0f\u89c1 customRockets \u5c5e\u6027\u6ce8\u91ca\u3002\n"
            + "Additional custom rockets defined in this file. They are appended to the built-in rocket list; rocket tier determines reachable planets and model tier changes appearance only. See the customRockets property comment for the full field format.";
    private static final String CUSTOM_ROCKETS_PROPERTY_COMMENT =
        "\u989d\u5916\u81ea\u5b9a\u4e49\u706b\u7bad\u5217\u8868\uff1b\u6761\u76ee\u4f1a\u8ffd\u52a0\u5230\u5185\u7f6e\u706b\u7bad\uff0c\u4e0d\u4f1a\u66ff\u6362 tier_1_rocket \u81f3 tier_15_rocket\u3002\u6bcf\u884c\u683c\u5f0f\uff1aid|\u663e\u793a\u540d\u79f0|\u706b\u7bad\u7b49\u7ea7|\u71c3\u6599\u5bb9\u91cf(mB)|\u6a21\u578b\u7b49\u7ea7|\u8d34\u56fe\uff1b\u81f3\u5c11 6 \u4e2a\u5b57\u6bb5\uff0c\u591a\u4f59\u5b57\u6bb5\u5ffd\u7565\uff0c\u7a7a\u884c\u548c\u4ee5 # \u5f00\u5934\u7684\u884c\u5ffd\u7565\u3002\n"
            + "Additional custom rocket list; rows are appended to the built-in rockets and do not replace tier_1_rocket through tier_15_rocket. Format: id|display name|rocket tier|fuel capacity (mB)|model tier|texture. At least six fields are required; field 7 and later fields are ignored. Blank rows and lines beginning with # are ignored.\n"
            + "\u706b\u7bad\u7b49\u7ea7\u8303\u56f4 1-15\uff0c\u71c3\u6599\u5bb9\u91cf\u8303\u56f4 1000-64000 mB\uff0c\u6a21\u578b\u7b49\u7ea7\u8303\u56f4 1-12\uff1b\u6a21\u578b\u7b49\u7ea7 8-12 \u4f7f\u7528\u6269\u5c55\u706b\u7bad\u6a21\u578b\u3002\u5408\u6cd5\u4f46\u8d85\u8303\u56f4\u503c\u4f1a\u88ab\u9650\u5236\uff1b\u89e3\u6790\u5931\u8d25\u65f6\u4f7f\u7528\u5b57\u6bb5\u9ed8\u8ba4\u503c\uff1a\u706b\u7bad\u7b49\u7ea7 7\u3001\u71c3\u6599 max(1000, \u706b\u7bad\u7b49\u7ea7*1000+2000)\u3001\u6a21\u578b\u7b49\u7ea7\u4e3a\u5c06\u706b\u7bad\u7b49\u7ea7\u9650\u5236\u5230 1-12\u3002\n"
            + "Rocket tier: 1-15; fuel capacity: 1000-64000 mB; model tier: 1-12. Model tiers 8-12 use the extended rocket models. Valid numeric values outside these ranges are clamped. Invalid values use field defaults: rocket tier 7, fuel capacity max(1000, rocket tier*1000+2000) after rocket-tier parsing, and model tier clamped from the rocket tier.\n"
            + "\u76f8\u5bf9 PNG \u8d34\u56fe\u8def\u5f84\u653e\u5728 config/ad_astra/rocket_png \u4e0b\uff1b\u6a21\u7ec4\u6216\u6a21\u7ec4\u5305\u5185\u7f6e\u8d34\u56fe\u8bf7\u586b\u5199 namespace:path \u683c\u5f0f\u7684\u5b8c\u6574\u8d44\u6e90\u4f4d\u7f6e\uff1b\u8d34\u56fe\u7559\u7a7a\u65f6\u4f7f\u7528\u4e0e\u6a21\u578b\u7b49\u7ea7\u5339\u914d\u7684\u5185\u7f6e\u8d34\u56fe\u3002\u5185\u7f6e tier_1_rocket \u81f3 tier_15_rocket\u3001tier_1_rover \u548c configurable_rocket \u662f\u4fdd\u7559 ID\uff0c\u4e0d\u80fd\u8986\u76d6\uff1b\u91cd\u590d ID\u3001\u65e7\u793a\u4f8b ID custom_tier_8_rocket \u6216\u5b57\u6bb5\u4e0d\u8db3\u7684\u884c\u4f1a\u88ab\u5ffd\u7565\u3002\n"
            + "Put relative PNG paths under config/ad_astra/rocket_png, or enter a complete namespace:path resource location for a bundled texture. An empty texture uses the built-in model-tier texture. Built-in tier_1_rocket through tier_15_rocket, tier_1_rover, and configurable_rocket are reserved IDs and cannot be overridden here. Duplicate IDs, the legacy example ID custom_tier_8_rocket, and rows with fewer than six fields are ignored.";
    private static final String[] DEFAULT_CUSTOM_ROCKETS = new String[] {
        "# \u793a\u4f8b\uff1a\u53d6\u6d88\u884c\u9996\u7684 # \u5373\u53ef\u6ce8\u518c\u81ea\u5b9a\u4e49\u706b\u7bad\uff1b\u4f7f\u7528\u4e0e\u5185\u7f6e\u706b\u7bad\u4e0d\u540c\u7684 id\u3002 / Example: remove # to register a custom rocket; use an id different from built-in rockets.",
        "# \u793a\u4f8b\uff1a1 \u7ea7\u706b\u7bad / Example: tier 1 rocket: example_tier_1_rocket|Example Tier 1 Rocket|1|3000|1|ad_astra:textures/entity/rocket/tier_1_rocket.png",
        "# \u793a\u4f8b\uff1a2 \u7ea7\u706b\u7bad / Example: tier 2 rocket: example_tier_2_rocket|Example Tier 2 Rocket|2|4000|2|ad_astra:textures/entity/rocket/tier_2_rocket.png",
        "# \u793a\u4f8b\uff1a3 \u7ea7\u706b\u7bad / Example: tier 3 rocket: example_tier_3_rocket|Example Tier 3 Rocket|3|5000|3|ad_astra:textures/entity/rocket/tier_3_rocket.png",
        "# \u793a\u4f8b\uff1a4 \u7ea7\u706b\u7bad / Example: tier 4 rocket: example_tier_4_rocket|Example Tier 4 Rocket|4|6000|4|ad_astra:textures/entity/rocket/tier_4_rocket.png",
        "# \u793a\u4f8b\uff1a5 \u7ea7\u706b\u7bad / Example: tier 5 rocket: example_tier_5_rocket|Example Tier 5 Rocket|5|7000|5|ad_astra:textures/entity/rocket/tier_5_rocket.png",
        "# \u793a\u4f8b\uff1a6 \u7ea7\u706b\u7bad / Example: tier 6 rocket: example_tier_6_rocket|Example Tier 6 Rocket|6|8000|6|ad_astra:textures/entity/rocket/tier_6_rocket.png",
        "# \u793a\u4f8b\uff1a7 \u7ea7\u706b\u7bad / Example: tier 7 rocket: example_tier_7_rocket|Example Tier 7 Rocket|7|9000|7|ad_astra:textures/entity/rocket/tier_7_rocket.png",
        "# \u793a\u4f8b\uff1a8 \u7ea7\u706b\u7bad / Example: tier 8 rocket: example_tier_8_rocket|Example Tier 8 Rocket|8|18000|8|ad_astra:textures/entity/rocket/tier_8_rocket.png",
        "# \u793a\u4f8b\uff1a9 \u7ea7\u706b\u7bad / Example: tier 9 rocket: example_tier_9_rocket|Example Tier 9 Rocket|9|19000|9|ad_astra:textures/entity/rocket/tier_9_rocket.png",
        "# \u793a\u4f8b\uff1a10 \u7ea7\u706b\u7bad / Example: tier 10 rocket: example_tier_10_rocket|Example Tier 10 Rocket|10|20000|10|ad_astra:textures/entity/rocket/tier_10_rocket.png",
        "# \u793a\u4f8b\uff1a11 \u7ea7\u706b\u7bad / Example: tier 11 rocket: example_tier_11_rocket|Example Tier 11 Rocket|11|21000|11|ad_astra:textures/entity/rocket/tier_11_rocket.png",
        "# \u793a\u4f8b\uff1a12 \u7ea7\u706b\u7bad / Example: tier 12 rocket: example_tier_12_rocket|Example Tier 12 Rocket|12|22000|12|ad_astra:textures/entity/rocket/tier_12_rocket.png",
        "# \u793a\u4f8b\uff1a13 \u7ea7\u706b\u7bad / Example: tier 13 rocket: example_tier_13_rocket|Example Tier 13 Rocket|13|23000|12|ad_astra:textures/entity/rocket/tier_13_rocket.png",
        "# \u793a\u4f8b\uff1a14 \u7ea7\u706b\u7bad / Example: tier 14 rocket: example_tier_14_rocket|Example Tier 14 Rocket|14|24000|12|ad_astra:textures/entity/rocket/tier_14_rocket.png",
        "# \u793a\u4f8b\uff1a15 \u7ea7\u706b\u7bad / Example: tier 15 rocket: example_tier_15_rocket|Example Tier 15 Rocket|15|25000|12|ad_astra:textures/entity/rocket/tier_15_rocket.png"
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
        // Ignore the old example row that may still exist in a user's config.
        if ("custom_tier_8_rocket".equals(id)) {
            return null;
        }
        if (id.isEmpty() || isReservedId(id) || ROCKETS.containsKey(id)) {
            return null;
        }

        String displayName = parts[1].trim();
        if (displayName.isEmpty()) {
            displayName = id;
        }

        int tier = parseInt(parts[2], 1, MAX_ROCKET_TIER, 7);
        int fuelCapacity = parseInt(parts[3], 1000, 64000, Math.max(1000, tier * 1000 + 2000));
        int modelTier = parseInt(parts[4], 1, MAX_MODEL_TIER, Math.min(Math.max(tier, 1), MAX_MODEL_TIER));
        ParsedTexture texture = parseTexture(id, parts[5], modelTier);

        return new ConfigurableRocketSpec(id, displayName, tier, fuelCapacity, modelTier,
            texture.location, texture.externalFile, texture.displayName, false, modelTier >= 8);
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
            while (relative.startsWith("/")) {
                relative = relative.substring(1);
            }
            if (relative.contains("..")) {
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
        return "tier_1_rocket".equals(id)
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
