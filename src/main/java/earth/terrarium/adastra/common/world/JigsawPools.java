package earth.terrarium.adastra.common.world;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import earth.terrarium.adastra.Reference;
import net.minecraft.util.ResourceLocation;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Loads Ad Astra's 1.20 worldgen template pools straight from the packaged JSON
 * ({@code data/ad_astra/worldgen/template_pool/<name>.json}). 1.12.2 has no data-driven jigsaw
 * system, so {@link JigsawStructureGenerator} consumes these pool definitions (element locations +
 * weights + fallback) itself at generation time.
 *
 * <p>Pool names match the {@code pool} field stored inside each jigsaw block's tile-entity data,
 * with the {@code ad_astra:} namespace stripped (e.g. {@code lunarian_village/room_or_hallway},
 * {@code dungeon/moon/walkway}). Element locations are the full structure path relative to
 * {@code structures/} (e.g. {@code village/moon/lunarian_village/entrance}).
 */
public final class JigsawPools {

    public static final class Element {
        public final ResourceLocation location; // structure nbt id relative to structures/
        public final int weight;

        Element(ResourceLocation location, int weight) {
            this.location = location;
            this.weight = weight;
        }
    }

    public static final class Pool {
        public final List<Element> elements = new ArrayList<Element>();
        public final String fallback; // pool name used when the piece budget is exhausted, or null

        Pool(String fallback) {
            this.fallback = fallback;
        }

        int totalWeight() {
            int total = 0;
            for (Element element : elements) {
                total += element.weight;
            }
            return total;
        }

        Element pick(Random random) {
            int total = totalWeight();
            if (total <= 0) {
                return null;
            }
            int roll = random.nextInt(total);
            for (Element element : elements) {
                roll -= element.weight;
                if (roll < 0) {
                    return element;
                }
            }
            return elements.get(elements.size() - 1);
        }
    }

    /** Sentinel so we can cache "no such pool" lookups without re-hitting the classpath. */
    private static final Pool MISSING = new Pool(null);

    private static final Map<String, Pool> CACHE = new HashMap<String, Pool>();
    private static final JsonParser PARSER = new JsonParser();

    public static synchronized Pool get(String poolName) {
        if (poolName == null || poolName.isEmpty()) {
            return null;
        }
        Pool cached = CACHE.get(poolName);
        if (cached != null) {
            return cached == MISSING ? null : cached;
        }
        Pool loaded = load(poolName);
        CACHE.put(poolName, loaded == null ? MISSING : loaded);
        return loaded;
    }

    private static Pool load(String poolName) {
        ResourceLocation poolId = parseId(poolName, Reference.MOD_ID);
        String path = "/data/" + poolId.getNamespace() + "/worldgen/template_pool/" + poolId.getPath() + ".json";
        try (InputStream stream = JigsawPools.class.getResourceAsStream(path)) {
            if (stream == null) {
                return null;
            }
            JsonObject root = PARSER.parse(
                new InputStreamReader(stream, StandardCharsets.UTF_8)).getAsJsonObject();

            String fallback = null;
            if (root.has("fallback")) {
                fallback = root.get("fallback").getAsString();
                if (fallback.isEmpty() || "empty".equals(fallback)) {
                    fallback = null; // minecraft:empty terminates the chain
                }
            }

            Pool pool = new Pool(fallback);
            JsonArray elements = root.getAsJsonArray("elements");
            if (elements != null) {
                for (JsonElement raw : elements) {
                    JsonObject entry = raw.getAsJsonObject();
                    int weight = entry.has("weight") ? entry.get("weight").getAsInt() : 1;
                    JsonObject element = entry.getAsJsonObject("element");
                    if (element == null || !element.has("location")) {
                        continue;
                    }
                    ResourceLocation location = parseId(element.get("location").getAsString(), poolId.getNamespace());
                    pool.elements.add(new Element(location, weight));
                }
            }
            return pool.elements.isEmpty() ? null : pool;
        } catch (Exception exception) {
            return null;
        }
    }

    private static ResourceLocation parseId(String id, String defaultNamespace) {
        if (id == null || id.isEmpty()) {
            return new ResourceLocation(defaultNamespace, "empty");
        }
        int colon = id.indexOf(':');
        return colon >= 0
            ? new ResourceLocation(id.substring(0, colon), id.substring(colon + 1))
            : new ResourceLocation(defaultNamespace, id);
    }

    private JigsawPools() {
    }
}
