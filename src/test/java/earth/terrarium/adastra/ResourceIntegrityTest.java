package earth.terrarium.adastra;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

class ResourceIntegrityTest {

    private static final Pattern BLOCKSTATE_MODEL_REFERENCE = Pattern.compile(
        "\\\"model\\\"\\s*:\\s*\\\"ad_astra:([^\\\"]+)\\\"");

    private static final Pattern ITEM_REFERENCE = Pattern.compile(
        "\\\"item\\\"\\s*:\\s*\\\"ad_astra:([^\\\"]+)\\\"");

    @Test
    void sourceAndTextResourcesContainNoLegacyNamespace() throws IOException {
        String legacyNamespace = "ad_" + "ex" + "tendra";
        String legacyDisplayName = "Ad " + "Ex" + "tendra";
        String legacyPrefix = "ex" + "tendra_";
        List<String> forbidden = List.of(
            legacyNamespace,
            legacyDisplayName,
            legacyPrefix,
            "ad_astra:" + legacyPrefix);
        List<String> violations = new ArrayList<>();
        for (Path root : List.of(projectRoot().resolve("src/main/java"), projectRoot().resolve("src/main/resources"))) {
            try (Stream<Path> paths = Files.walk(root)) {
                paths.filter(Files::isRegularFile)
                    .filter(ResourceIntegrityTest::isTextFile)
                    .forEach(path -> {
                        String text = read(path);
                        for (String token : forbidden) {
                            if (text.contains(token)) {
                                violations.add(root.relativize(path) + " contains " + token);
                            }
                        }
                    });
            }
        }
        assertTrue(violations.isEmpty(), String.join(System.lineSeparator(), violations));
    }

    @Test
    void everyJsonResourceParses() throws IOException {
        List<String> failures = new ArrayList<>();
        Path root = projectRoot().resolve("src/main/resources");
        try (Stream<Path> paths = Files.walk(root)) {
            paths.filter(path -> Files.isRegularFile(path) && path.toString().endsWith(".json"))
                .forEach(path -> {
                    try {
                        new JsonParser().parse(read(path));
                    } catch (RuntimeException exception) {
                        failures.add(root.relativize(path) + ": " + exception.getMessage());
                    }
                });
        }
        assertTrue(failures.isEmpty(), String.join(System.lineSeparator(), failures));
    }

    @Test
    void importantAssetReferencesResolve() throws IOException {
        Path resources = projectRoot().resolve("src/main/resources");
        Path assets = resources.resolve("assets/ad_astra");
        List<String> failures = new ArrayList<>();

        try (Stream<Path> paths = Files.walk(assets.resolve("models"))) {
            paths.filter(path -> Files.isRegularFile(path) && path.toString().endsWith(".json"))
                .forEach(path -> checkModelReferences(path, assets, failures));
        }

        try (Stream<Path> paths = Files.walk(assets.resolve("blockstates"))) {
            paths.filter(path -> Files.isRegularFile(path) && path.toString().endsWith(".json"))
                .forEach(path -> checkBlockstateReferences(path, assets, failures));
        }

        assertTrue(Files.exists(assets.resolve("models/block/block_vicinus_globe.json")));
        assertTrue(Files.exists(assets.resolve("models/item/item_vicinus_globe.json")));
        assertTrue(Files.exists(assets.resolve("textures/block/globe/block_vicinus_globe.png")));
        assertTrue(Files.exists(resources.resolve("data/ad_astra/planets/planet_ceres.json")));
        assertTrue(Files.exists(resources.resolve("data/ad_astra/planets/planet_proxima_centauri_b.json")));
        assertFalse(Files.exists(resources.resolve("data/ad_astra/worldgen/structure/structure_proxima_centauri_b_hut.json")));
        assertTrue(failures.isEmpty(), String.join(System.lineSeparator(), failures));
    }

    @Test
    void criticalRenamedIdsAreReferencedByResources() throws IOException {
        Path resources = projectRoot().resolve("src/main/resources");
        String freezeImmune = read(resources.resolve("data/minecraft/tags/entity_types/freeze_immune_entity_types.json"));
        String walkable = read(resources.resolve("data/minecraft/tags/entity_types/powder_snow_walkable_mobs.json"));
        String english = read(resources.resolve("assets/ad_astra/lang/en_us.lang"));
        String chinese = read(resources.resolve("assets/ad_astra/lang/zh_cn.lang"));

        assertTrue(freezeImmune.contains("ad_astra:entity_freeze"));
        assertTrue(walkable.contains("ad_astra:entity_freeze"));
        assertTrue(english.contains("entity.ad_astra.entity_freeze=Freeze"));
        assertTrue(english.contains("item.ad_astra.item_ice_charge.name=Ice Charge"));
        assertTrue(chinese.contains("entity.ad_astra.entity_freeze="));
        assertTrue(chinese.contains("item.ad_astra.item_ice_charge.name="));
    }

    @Test
    void recipeItemReferencesUseRegisteredResourceIds() throws IOException {
        Path recipes = projectRoot().resolve("src/main/resources/assets/ad_astra/recipes");
        List<String> failures = new ArrayList<>();
        int slabReferences = 0;

        try (Stream<Path> paths = Files.walk(recipes)) {
            for (Path path : paths.filter(file -> Files.isRegularFile(file) && file.toString().endsWith(".json"))
                .toList()) {
                String text = read(path);
                Matcher matcher = ITEM_REFERENCE.matcher(text);
                while (matcher.find()) {
                    String id = matcher.group(1);
                    if (id.endsWith("_slab")) {
                        slabReferences++;
                        assertTrue(hasDataZeroAfter(text, matcher.end()),
                            path.getFileName() + " must specify data=0 for ItemSlab result/ingredient " + id);
                    }
                    if (id.startsWith("item_")) {
                        Path model = projectRoot().resolve("src/main/resources/assets/ad_astra/models/item/" + id + ".json");
                        if (!Files.exists(model)) {
                            failures.add(path.getFileName() + " references " + id + " without " + model.getFileName());
                        }
                    }
                }
            }
        }

        assertTrue(slabReferences > 0, "Expected migrated slab recipe references.");
        assertTrue(failures.isEmpty(), String.join(System.lineSeparator(), failures));
    }

    private static boolean hasDataZeroAfter(String text, int referenceEnd) {
        int nextObject = text.indexOf('}', referenceEnd);
        int nextResult = text.indexOf("\"result\"", referenceEnd);
        int boundary = nextObject < 0 ? text.length() : nextObject;
        if (nextResult >= 0 && nextResult < boundary) {
            boundary = nextResult;
        }
        String objectTail = text.substring(referenceEnd, boundary);
        return objectTail.matches("(?s).*\\\"data\\\"\\s*:\\s*0.*");
    }

    private static void checkModelReferences(Path model, Path assets, List<String> failures) {
        JsonObject root = JsonParser.parseString(read(model)).getAsJsonObject();
        checkModelAssetReference(model, assets, failures, root.get("parent"), "parent");

        JsonElement textures = root.get("textures");
        if (textures != null && textures.isJsonObject()) {
            for (Map.Entry<String, JsonElement> entry : textures.getAsJsonObject().entrySet()) {
                checkTextureReference(model, assets, failures, entry.getValue());
            }
        }
    }

    private static void checkModelAssetReference(Path model, Path assets, List<String> failures,
                                                 JsonElement reference, String kind) {
        if (reference == null || !reference.isJsonPrimitive() || !reference.getAsJsonPrimitive().isString()) {
            return;
        }
        String value = reference.getAsString();
        String[] parts = splitAdAstraReference(value);
        if (parts == null) {
            return;
        }
        Path target = assets.resolve("models/" + parts[0] + "/" + parts[1] + ".json");
        if (!Files.exists(target)) {
            failures.add(model.getFileName() + " " + kind + " is missing: " + value);
        }
    }

    private static void checkTextureReference(Path model, Path assets, List<String> failures, JsonElement reference) {
        if (reference == null || !reference.isJsonPrimitive() || !reference.getAsJsonPrimitive().isString()) {
            return;
        }
        String value = reference.getAsString();
        String[] parts = splitAdAstraReference(value);
        if (parts == null) {
            return;
        }
        Path target = assets.resolve("textures/" + parts[0] + "/" + parts[1] + ".png");
        if (!Files.exists(target) && !Files.exists(Path.of(target + ".mcmeta"))) {
            failures.add(model.getFileName() + " texture is missing: " + value);
        }
    }

    private static String[] splitAdAstraReference(String value) {
        if (!value.startsWith("ad_astra:")) {
            return null;
        }
        String path = value.substring("ad_astra:".length());
        int separator = path.indexOf('/');
        if (separator <= 0 || separator == path.length() - 1) {
            return null;
        }
        return new String[] {path.substring(0, separator), path.substring(separator + 1)};
    }

    private static void checkBlockstateReferences(Path blockstate, Path assets, List<String> failures) {
        Matcher matcher = BLOCKSTATE_MODEL_REFERENCE.matcher(read(blockstate));
        while (matcher.find()) {
            String modelPath = matcher.group(1);
            Path target = modelPath.contains("/")
                ? assets.resolve("models/" + modelPath + ".json")
                : assets.resolve("models/block/" + modelPath + ".json");
            if (!Files.exists(target)) {
                failures.add(blockstate.getFileName() + " model is missing: " + modelPath);
            }
        }
    }

    private static boolean isTextFile(Path path) {
        String name = path.getFileName().toString().toLowerCase(Locale.ROOT);
        return name.endsWith(".java") || name.endsWith(".json") || name.endsWith(".lang")
            || name.endsWith(".mcmeta") || name.endsWith(".properties") || name.endsWith(".cfg")
            || name.endsWith(".xml") || name.endsWith(".txt") || name.endsWith(".toml");
    }

    private static String read(Path path) {
        try {
            return Files.readString(path, StandardCharsets.UTF_8);
        } catch (IOException exception) {
            fail("Unable to read " + path + ": " + exception.getMessage());
            return "";
        }
    }

    private static Path projectRoot() {
        Path current = Path.of(System.getProperty("user.dir")).toAbsolutePath();
        while (current != null && !Files.isDirectory(current.resolve("src/main/resources"))) {
            current = current.getParent();
        }
        if (current == null) {
            fail("Could not locate project root from " + System.getProperty("user.dir"));
        }
        return current;
    }
}
