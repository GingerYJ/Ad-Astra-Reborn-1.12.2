package earth.terrarium.adastra.common.rocket;

import earth.terrarium.adastra.Reference;
import earth.terrarium.adastra.common.items.ConfigurableRocketItem;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import javax.annotation.Nullable;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Reads and exposes config-defined rocket variants.
 *
 * Format per row:
 * id|displayName|tier|fuelCapacity|modelTier|texture
 */
public final class ConfigurableRocketRegistry {

    private static final String CATEGORY = "rockets";
    private static final String DEFAULT_EXTERNAL_TEXTURE = "custom_tier_8_rocket.png";
    private static final String DEFAULT_TEXTURE_RESOURCE = "assets/ad_astra/textures/entity/rocket/tier_7_rocket.png";
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
        copyDefaultRocketTexture();

        Configuration config = new Configuration(file);
        config.setCategoryComment(CATEGORY,
            "\u914d\u7f6e\u706b\u7bad\uff1a\u901a\u8fc7\u8d34\u56fe\u8def\u5f84\u589e\u52a0\u989d\u5916\u706b\u7bad\u3002\n" +
            "\u6bcf\u884c\u683c\u5f0f\uff1aid|\u663e\u793a\u540d\u79f0|\u706b\u7bad\u7b49\u7ea7|\u71c3\u6599\u5bb9\u91cfmB|\u6a21\u578b\u7b49\u7ea7|\u8d34\u56fe\u3002\n" +
            "\u81ea\u5b9a\u4e49 PNG \u8bf7\u653e\u5165 config/ad_astra/rocket_png\uff0c\u8d34\u56fe\u5b57\u6bb5\u586b\u5199\u6587\u4ef6\u540d\u3002\n" +
            "\u542f\u52a8\u65f6\u4f1a\u81ea\u52a8\u751f\u6210 7 \u9636\u706b\u7bad\u8d34\u56fe\u6a21\u677f\uff1a" + DEFAULT_EXTERNAL_TEXTURE + "\uff0c\u53ef\u76f4\u63a5\u6539\u8272\u4f7f\u7528\u3002\n" +
            "\u793a\u4f8b\uff1acustom_tier_8_rocket|\u516b\u9636\u706b\u7bad|8|10000|7|" + DEFAULT_EXTERNAL_TEXTURE + "\n" +
            "\u4ecd\u517c\u5bb9\u8d44\u6e90\u5305\u8def\u5f84\uff0c\u4f8b\u5982 ad_astra:textures/entity/rocket/tier_7_rocket.png\u3002\n" +
            "\u9ed8\u8ba4\u4e3a\u7a7a\uff0c\u4e0d\u751f\u6210\u989d\u5916\u706b\u7bad\uff1b\u4fee\u6539\u540e\u9700\u8981\u91cd\u542f\u6e38\u620f/\u670d\u52a1\u5668\u3002");

        Property customRocketsProperty = config.get(
            CATEGORY,
            "customRockets",
            new String[0],
            "\u81ea\u5b9a\u4e49\u706b\u7bad\u5217\u8868\u3002\u6bcf\u884c\u683c\u5f0f\uff1aid|\u663e\u793a\u540d\u79f0|\u706b\u7bad\u7b49\u7ea7|\u71c3\u6599\u5bb9\u91cfmB|\u6a21\u578b\u7b49\u7ea7|\u8d34\u56fe\u3002\n" +
                "\u5c06 PNG \u653e\u5230 config/ad_astra/rocket_png \u540e\uff0c\u8d34\u56fe\u5b57\u6bb5\u586b\u5199\u6587\u4ef6\u540d\u3002\n" +
                "\u542f\u52a8\u540e\u6587\u4ef6\u5939\u4e2d\u4f1a\u81ea\u52a8\u751f\u6210 " + DEFAULT_EXTERNAL_TEXTURE + " \u4f5c\u4e3a\u6539\u8272\u6a21\u677f\u3002\n" +
                "\u4f8b\uff1acustom_tier_8_rocket|\u516b\u9636\u706b\u7bad|8|10000|7|" + DEFAULT_EXTERNAL_TEXTURE);
        String[] rows = customRocketsProperty.getStringList();

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

    private static void copyDefaultRocketTexture() {
        if (rocketPngFolder == null) {
            return;
        }
        File output = new File(rocketPngFolder, DEFAULT_EXTERNAL_TEXTURE);
        if (output.isFile()) {
            return;
        }
        try (InputStream input = ConfigurableRocketRegistry.class.getClassLoader().getResourceAsStream(DEFAULT_TEXTURE_RESOURCE)) {
            if (input == null) {
                return;
            }
            Files.copy(input, output.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception ignored) {
            // The template is a convenience file only; startup should not fail if copying it is blocked.
        }
    }

    public static boolean isLoaded() {
        return loaded;
    }

    public static List<ConfigurableRocketSpec> getRockets() {
        return Collections.unmodifiableList(ROCKET_LIST);
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

        int tier = parseInt(parts[2], 1, 64, 7);
        int fuelCapacity = parseInt(parts[3], 1000, 64000, Math.max(1000, tier * 1000 + 2000));
        int modelTier = parseInt(parts[4], 1, 7, Math.min(Math.max(tier, 1), 7));
        ParsedTexture texture = parseTexture(id, parts[5], modelTier);

        return new ConfigurableRocketSpec(id, displayName, tier, fuelCapacity, modelTier,
            texture.location, texture.externalFile, texture.displayName);
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
