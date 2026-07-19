package earth.terrarium.adastra.common.registry;

import earth.terrarium.adastra.Reference;
import net.minecraft.util.ResourceLocation;

import java.util.Locale;

/**
 * Canonical resource IDs for additional content in the 1.12.2 port.
 *
 * <p>The original Ad Astra IDs remain unchanged. Additional content uses a
 * category prefix so its blocks, items, entities, biomes, planets, and data
 * files cannot collide with the original registry.</p>
 */
public final class ModResourceIds {

    public static final String NAMESPACE = Reference.MOD_ID;

    private ModResourceIds() {
    }

    public static ResourceLocation id(String path) {
        return new ResourceLocation(NAMESPACE, normalize(path));
    }

    public static ResourceLocation block(String name) {
        return prefixed("block", name);
    }

    public static ResourceLocation item(String name) {
        return prefixed("item", name);
    }

    public static ResourceLocation entity(String name) {
        return prefixed("entity", name);
    }

    public static ResourceLocation biome(String name) {
        return prefixed("biome", name);
    }

    public static ResourceLocation planet(String name) {
        return prefixed("planet", name);
    }

    public static ResourceLocation orbit(String planetName) {
        return id(orbitPath(planetName));
    }

    public static ResourceLocation structure(String name) {
        return prefixed("structure", name);
    }

    public static ResourceLocation recipe(String name) {
        return prefixed("recipe", name);
    }

    public static ResourceLocation particle(String name) {
        return prefixed("particle", name);
    }

    public static String blockPath(String name) {
        return prefixedPath("block", name);
    }

    public static String itemPath(String name) {
        return prefixedPath("item", name);
    }

    public static String entityPath(String name) {
        return prefixedPath("entity", name);
    }

    public static String biomePath(String name) {
        return prefixedPath("biome", name);
    }

    public static String planetPath(String name) {
        return prefixedPath("planet", name);
    }

    public static String orbitPath(String planetName) {
        String normalized = normalize(planetName);
        if (normalized.startsWith("planet_")) {
            normalized = normalized.substring("planet_".length());
        }
        if (normalized.endsWith("_orbit")) {
            normalized = normalized.substring(0, normalized.length() - "_orbit".length());
        }
        return "planet_" + normalized + "_orbit";
    }

    public static String structurePath(String name) {
        return prefixedPath("structure", name);
    }

    public static String recipePath(String name) {
        return prefixedPath("recipe", name);
    }

    public static String particlePath(String name) {
        return prefixedPath("particle", name);
    }

    private static ResourceLocation prefixed(String prefix, String name) {
        return id(prefixedPath(prefix, name));
    }

    private static String prefixedPath(String prefix, String name) {
        String normalized = normalize(name);
        String fullPrefix = prefix + "_";
        return normalized.startsWith(fullPrefix) ? normalized : fullPrefix + normalized;
    }

    private static String normalize(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Resource path cannot be null or empty.");
        }
        if (!value.equals(value.trim())) {
            throw new IllegalArgumentException("Invalid resource path: " + value);
        }
        String normalized = value.toLowerCase(Locale.ROOT);
        if (normalized.indexOf(':') >= 0 || containsWhitespace(normalized)) {
            throw new IllegalArgumentException("Invalid resource path: " + value);
        }
        return normalized;
    }

    private static boolean containsWhitespace(String value) {
        for (int i = 0; i < value.length(); i++) {
            if (Character.isWhitespace(value.charAt(i))) {
                return true;
            }
        }
        return false;
    }
}
