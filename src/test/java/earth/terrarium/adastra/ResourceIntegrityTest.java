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

    @Test
    void highTierRocketsHaveDedicatedRegistrationsAndAssets() throws IOException {
        Path root = projectRoot();
        String entities = read(root.resolve("src/main/java/earth/terrarium/adastra/common/registry/ModEntities.java"));
        String items = read(root.resolve("src/main/java/earth/terrarium/adastra/common/registry/ModItems.java"));

        for (int tier = 8; tier <= 15; tier++) {
            String name = "tier_" + tier + "_rocket";
            String entityClass = "Tier" + tier + "RocketEntity";
            assertTrue(entities.contains(
                "TIER_" + tier + "_ROCKET = entity(\"" + name + "\", " + entityClass + ".class"));
            assertTrue(items.contains(
                "TIER_" + tier + "_ROCKET = prefixedVehicle(\"" + name + "\", " + entityClass + "::new)"));
            assertTrue(Files.exists(root.resolve("src/main/resources/assets/ad_astra/models/item/item_" + name + ".json")));
            assertTrue(Files.exists(root.resolve("src/main/resources/assets/ad_astra/textures/entity/rocket/" + name + ".png")));

            Path entitySource = root.resolve(
                "src/main/java/earth/terrarium/adastra/common/entities/vehicles/" + entityClass + ".java");
            String source = read(entitySource);
            assertTrue(source.contains("super(world, " + tier + ", TIER_" + tier + "_FUEL_CAPACITY"));
            assertTrue(source.contains("RocketFuelHelper.canFuelRocket(stack, " + tier + ")"));
        }
    }

    @Test
    void additionalPlanetsAreCodeOwnedAndDimensionRegistrationIsSeparate() throws IOException {
        Path root = projectRoot();
        String planets = read(root.resolve("src/main/java/earth/terrarium/adastra/common/registry/ModPlanets.java"));
        String dimensions = read(root.resolve("src/main/java/earth/terrarium/adastra/common/registry/ModDimensions.java"));
        String builtInRegistry = read(root.resolve(
            "src/main/java/earth/terrarium/adastra/common/world/custom/BuiltInPlanetRegistry.java"));
        String[] additionalPlanets = {
            "ceres", "jupiter", "saturn", "uranus", "neptune", "orcus", "pluto", "haumea",
            "quaoar", "makemake", "gonggong", "eris", "sedna", "proxima_centauri_b"
        };

        assertTrue(planets.contains("BuiltInPlanetRegistry.register(definition)"));
        assertTrue(dimensions.contains("BuiltInPlanetDimensionRegistrar.register()"));
        assertTrue(builtInRegistry.contains("SURFACE_INDEX"));
        assertTrue(dimensions.contains("FIRST_PLANET_ID = 108490"));
        assertTrue(dimensions.contains("SPACE_STATION_ID = 107489"));
        assertFalse(dimensions.contains("FIRST_ORBIT_ID"));
        for (String planet : additionalPlanets) {
            assertTrue(planets.contains("planet(\"" + planet + "\""), "Missing built-in planet " + planet);
        }

        int codeOwnedSurfaceIds = 0;
        for (String field : List.of("MOON_PROPERTIES", "MARS_PROPERTIES", "MERCURY_PROPERTIES", "VENUS_PROPERTIES", "GLACIO_PROPERTIES")) {
            if (dimensions.contains("PlanetDimensionProperties " + field)) {
                codeOwnedSurfaceIds++;
            }
        }
        assertTrue(codeOwnedSurfaceIds == 5, "Expected the five original built-in planet definitions.");
    }

    @Test
    void onlyTheGlobalSpaceStationResourcesRemain() throws IOException {
        Path resources = projectRoot().resolve("src/main/resources");
        List<String> legacy = new ArrayList<>();
        for (Path root : List.of(
            resources.resolve("assets/ad_astra/planet_renderers"),
            resources.resolve("data/ad_astra/dimension"),
            resources.resolve("data/ad_astra/dimension_type"),
            resources.resolve("data/ad_astra/planets"),
            resources.resolve("data/ad_astra/machine_recipes/space_station"),
            resources.resolve("data/ad_astra/recipes/space_station"),
            resources.resolve("data/ad_astra/advancements/recipes/space_stations/space_station"),
            resources.resolve("data/ad_astra/worldgen/biome"),
            resources.resolve("data/ad_astra/worldgen/noise_settings"))) {
            if (!Files.exists(root)) {
                continue;
            }
            try (Stream<Path> paths = Files.walk(root)) {
                paths.filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString().toLowerCase(Locale.ROOT).contains("orbit"))
                    .forEach(path -> legacy.add(resources.relativize(path).toString()));
            }
        }

        assertTrue(Files.exists(resources.resolve("data/ad_astra/dimension/space_station.json")));
        assertTrue(Files.exists(resources.resolve("data/ad_astra/dimension_type/space_station.json")));
        assertTrue(Files.exists(resources.resolve("data/ad_astra/machine_recipes/space_station/space_station.json")));
        assertTrue(Files.exists(resources.resolve("data/ad_astra/advancements/recipes/space_stations/space_station.json")));
        assertTrue(legacy.isEmpty(), String.join(System.lineSeparator(), legacy));
    }

    @Test
    void configurableRocketsOnlyExposeAdvancedConfigurationRules() throws IOException {
        Path root = projectRoot();
        String registry = read(root.resolve(
            "src/main/java/earth/terrarium/adastra/common/rocket/ConfigurableRocketRegistry.java"));
        String spec = read(root.resolve(
            "src/main/java/earth/terrarium/adastra/common/rocket/ConfigurableRocketSpec.java"));
        String textureManager = read(root.resolve(
            "src/main/java/earth/terrarium/adastra/client/render/ConfigurableRocketTextureManager.java"));

        assertTrue(registry.contains("MIN_ROCKET_TIER = 16"));
        assertTrue(registry.contains("MAX_ROCKET_TIER = 255"));
        assertTrue(registry.contains("MAX_MODEL_TIER = 15"));
        assertTrue(registry.contains("migrateLegacyRows"));
        assertTrue(registry.contains("tier_8_rocket"));
        assertFalse(registry.contains("Ignore the old example row"));
        assertTrue(registry.contains("rocket_png"));
        assertTrue(spec.contains("getModelDefinitionTier"));
        assertTrue(spec.contains("builtInTextureForModelTier"));
        assertTrue(textureManager.contains("spec.getBuiltInTexture()"));
        assertTrue(textureManager.contains("return fallback;"));
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
