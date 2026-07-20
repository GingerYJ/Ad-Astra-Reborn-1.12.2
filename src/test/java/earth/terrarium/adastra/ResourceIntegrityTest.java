package earth.terrarium.adastra;

import com.google.gson.JsonElement;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

class ResourceIntegrityTest {

    private static final Pattern BLOCKSTATE_MODEL_REFERENCE = Pattern.compile(
        "\\\"model\\\"\\s*:\\s*\\\"ad_astra:([^\\\"]+)\\\"");

    private static final Pattern ITEM_REFERENCE = Pattern.compile(
        "\\\"item\\\"\\s*:\\s*\\\"ad_astra:([^\\\"]+)\\\"");

    private static final List<String> EXTENSION_LOOT_TABLES = List.of(
        "building/eris/eris_building.json",
        "building/haumea/haumea_building.json",
        "building/makemake/makemake_building.json",
        "fallen_ship/sedna/sedna_fallen_ship.json",
        "maze/neptune/neptune_maze.json",
        "meteor/ceres/ceres_meteor.json",
        "temple/gonggong/gonggong_temple.json",
        "temple/jupiter/jupiter_temple.json",
        "temple/orcus/orcus_temple.json",
        "temple/pluto/pluto_temple.json",
        "temple/quaoar/quaoar_temple.json",
        "tower/saturn/saturn_tower.json",
        "tower/uranus/uranus_tower.json",
        "volcano/mercury/mercury_volcano.json",
        "volcano/venus/venus_volcano.json");

    private static final Set<String> REMOVED_MODERN_ITEMS = Set.of(
        "minecraft:soul_lantern", "minecraft:glow_berries", "minecraft:suspicious_stew",
        "minecraft:warped_fungus", "minecraft:magma_block", "minecraft:raw_iron", "minecraft:raw_gold",
        "minecraft:soul_torch", "minecraft:music_disc_wait", "minecraft:enchanted_golden_apple");

    private static final Set<String> CORE_AD_ASTRA_LOOT_ITEMS = Set.of(
        "space_painting", "sky_stone", "moon_sand", "mars_sand", "venus_sand",
        "mercury_cobblestone", "mercury_globe", "cheese", "ice_shard", "raw_desh",
        "raw_ostrum", "raw_calorite", "desh_ingot", "desh_nugget", "steel_ingot",
        "steel_nugget", "launch_pad", "fuel_bucket", "gas_tank", "space_helmet",
        "space_suit", "space_pants", "space_boots");

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
        assertTrue(Files.exists(resources.resolve(
            "data/ad_astra/worldgen/structure/structure_proxima_centauri_b_hut.json")));
        assertTrue(Files.exists(resources.resolve(
            "data/ad_astra/worldgen/structure_set/structure_proxima_centauri_b_hut.json")));
        assertTrue(Files.exists(resources.resolve(
            "data/ad_astra/worldgen/template_pool/structure_proxima_centauri_b_hut/start_pool.json")));
        assertTrue(Files.exists(resources.resolve(
            "data/ad_astra/structures/structure_proxima_centauri_b_hut.nbt")));
        assertTrue(Files.exists(assets.resolve(
            "loot_tables/chests/hut/proxima_centauri_b/proxima_centauri_b_hut.json")));
        assertTrue(failures.isEmpty(), String.join(System.lineSeparator(), failures));
    }

    @Test
    void extensionLootTablesUseLegacyAssetsAndIds() throws IOException {
        Path resources = projectRoot().resolve("src/main/resources");
        Path assets = resources.resolve("assets/ad_astra/loot_tables/chests");
        Path data = resources.resolve("data/ad_astra/loot_tables/chests");

        for (String relative : EXTENSION_LOOT_TABLES) {
            Path asset = assets.resolve(relative);
            assertTrue(Files.exists(asset), "Missing converted loot table " + relative);
            assertFalse(Files.exists(data.resolve(relative)), "Duplicate data loot table " + relative);

            JsonObject root = JsonParser.parseString(read(asset)).getAsJsonObject();
            assertFalse(root.has("type"), relative + " still has a modern root type");
            for (JsonElement poolElement : root.getAsJsonArray("pools")) {
                JsonObject pool = poolElement.getAsJsonObject();
                assertFalse(pool.has("bonus_rolls"), relative + " still has bonus_rolls");
                for (JsonElement entryElement : pool.getAsJsonArray("entries")) {
                    JsonObject entry = entryElement.getAsJsonObject();
                    assertEquals("item", entry.get("type").getAsString(), relative);
                    String name = entry.get("name").getAsString();
                    assertFalse(name.startsWith("ad_extendra:"), relative + " has a legacy namespace");
                    assertFalse(REMOVED_MODERN_ITEMS.contains(name), relative + " has " + name);
                    if (name.startsWith("ad_astra:")) {
                        String path = name.substring("ad_astra:".length());
                        boolean allowedUnprefixed = CORE_AD_ASTRA_LOOT_ITEMS.contains(path);
                        assertTrue(allowedUnprefixed || path.startsWith("item_"),
                            relative + " uses an unprefixed migrated item " + name);
                        assertTrue(Files.exists(resources.resolve("assets/ad_astra/models/item/" + path + ".json")),
                            relative + " references an unregistered item model " + name);
                    }

                    JsonArray functions = entry.has("functions") ? entry.getAsJsonArray("functions") : null;
                    if (functions != null) {
                        for (JsonElement functionElement : functions) {
                            JsonObject function = functionElement.getAsJsonObject();
                            String functionName = function.get("function").getAsString();
                            assertFalse(functionName.startsWith("minecraft:"),
                                relative + " has a modern function " + functionName);
                            assertFalse("set_potion".equals(functionName), relative);
                        }
                    }

                    if ("minecraft:golden_apple".equals(name) && entry.get("weight").getAsInt() == 2) {
                        assertTrue(hasSetData(functions), relative + " lost enchanted golden apple metadata");
                    }
                    if ("minecraft:potion".equals(name)) {
                        assertTrue(hasSetNbt(functions), relative + " has an unconverted potion function");
                        String tag = findSetNbtTag(functions);
                        if (relative.contains("jupiter")) {
                            assertTrue(tag.contains("CustomPotionEffects"), relative);
                            assertTrue(tag.contains("CustomPotionColor"), relative);
                        } else {
                            assertTrue(tag.contains("Potion:"), relative);
                        }
                    }
                }
            }
        }
    }

    @Test
    void legacyLootTableUsesLegacyAssetsAndIds() throws IOException {
        Path resources = projectRoot().resolve("src/main/resources");
        Path table = resources.resolve("assets/minecraft/loot_tables/loot.json");
        assertTrue(Files.exists(table), "Missing legacy minecraft:loot table");

        JsonObject root = JsonParser.parseString(read(table)).getAsJsonObject();
        assertFalse(root.has("type"), "Legacy loot table still has a modern root type");
        for (JsonElement poolElement : root.getAsJsonArray("pools")) {
            JsonObject pool = poolElement.getAsJsonObject();
            assertFalse(pool.has("bonus_rolls"), "Legacy loot table still has bonus_rolls");
            for (JsonElement entryElement : pool.getAsJsonArray("entries")) {
                JsonObject entry = entryElement.getAsJsonObject();
                assertEquals("item", entry.get("type").getAsString());
                assertFalse("minecraft:cobweb".equals(entry.get("name").getAsString()));
                JsonArray functions = entry.has("functions") ? entry.getAsJsonArray("functions") : null;
                if (functions != null) {
                    for (JsonElement functionElement : functions) {
                        assertFalse(functionElement.getAsJsonObject().get("function").getAsString()
                            .startsWith("minecraft:"));
                    }
                }
            }
        }
    }

    @Test
    void allStructureTemplatesReferenceExistingLootTables() throws IOException {
        Path resources = projectRoot().resolve("src/main/resources");
        Path structureRoot = resources.resolve("data/ad_astra/structures");
        Path assets = resources.resolve("assets");
        int legacyReferences = 0;
        try (Stream<Path> paths = Files.walk(structureRoot)) {
            for (Path structure : paths.filter(path -> Files.isRegularFile(path)
                && path.toString().endsWith(".nbt")).toList()) {
                try (var input = Files.newInputStream(structure)) {
                    NBTTagCompound root = CompressedStreamTools.readCompressed(input);
                    NBTTagList blocks = root.getTagList("blocks", 10);
                    for (int i = 0; i < blocks.tagCount(); i++) {
                        NBTTagCompound block = blocks.getCompoundTagAt(i);
                        if (!block.hasKey("nbt", 10)) {
                            continue;
                        }
                        NBTTagCompound blockEntity = block.getCompoundTag("nbt");
                        if (!blockEntity.hasKey("LootTable", 8)) {
                            continue;
                        }
                        String lootTable = blockEntity.getString("LootTable");
                        if ("minecraft:loot".equals(lootTable)) {
                            legacyReferences++;
                        }
                        Path table = lootTablePath(assets, lootTable);
                        assertTrue(Files.exists(table), structure.getFileName() + " references missing " + lootTable);
                    }
                }
            }
        }
        assertTrue(legacyReferences > 0, "Expected legacy Venus/Pygro or meteor loot references.");

        Path structure = structureRoot.resolve("structure_proxima_centauri_b_hut.nbt");
        try (var input = Files.newInputStream(structure)) {
            NBTTagCompound hut = CompressedStreamTools.readCompressed(input);
            NBTTagList size = hut.getTagList("size", 3);
            assertEquals(3, size.tagCount());
            assertEquals(9, size.getIntAt(0));
            assertEquals(7, size.getIntAt(1));
            assertEquals(9, size.getIntAt(2));
        }
    }

    private static Path lootTablePath(Path assets, String id) {
        int separator = id.indexOf(':');
        assertTrue(separator > 0 && separator < id.length() - 1, "Invalid loot table id " + id);
        String namespace = id.substring(0, separator);
        String path = id.substring(separator + 1);
        assertFalse(path.contains(".."), "Unsafe loot table path " + id);
        return assets.resolve(namespace).resolve("loot_tables").resolve(path + ".json");
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

    private static boolean hasSetData(JsonArray functions) {
        if (functions == null) {
            return false;
        }
        for (JsonElement element : functions) {
            JsonObject function = element.getAsJsonObject();
            if ("set_data".equals(function.get("function").getAsString())
                && function.get("data").getAsInt() == 1) {
                return true;
            }
        }
        return false;
    }

    private static boolean hasSetNbt(JsonArray functions) {
        return findSetNbtTag(functions) != null;
    }

    private static String findSetNbtTag(JsonArray functions) {
        if (functions == null) {
            return null;
        }
        for (JsonElement element : functions) {
            JsonObject function = element.getAsJsonObject();
            if ("set_nbt".equals(function.get("function").getAsString())) {
                return function.get("tag").getAsString();
            }
        }
        return null;
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
