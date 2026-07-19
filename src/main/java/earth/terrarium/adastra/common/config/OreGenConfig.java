package earth.terrarium.adastra.common.config;

import earth.terrarium.adastra.AdAstraReborn;
import earth.terrarium.adastra.common.registry.ModDimensions;
import earth.terrarium.adastra.common.world.custom.CustomPlanetDefinition;
import earth.terrarium.adastra.common.world.custom.CustomPlanetRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/** Configurable ore rows grouped by registered surface planet. */
public final class OreGenConfig {

    private static final String CATEGORY = "worldgen";
    private static final String PLANET_CATEGORY_PREFIX = "planet_ore_";
    private static final String ORE_ROWS_KEY = "oreVeins";
    private static final int MIN_Y = -80;
    private static final int MAX_Y = 320;
    private static final String CATEGORY_COMMENT =
        "\u5168\u5c40\u5730\u5f62\u4e0e\u77ff\u8108\u751f\u6210\u8bbe\u7f6e\uff1b\u77ff\u8109\u6309\u884c\u661f\u5206\u533a\u3002\u4fee\u6539\u540e\u53ea\u5f71\u54cd\u65b0\u533a\u5757\u3002\n"
            + "Global terrain and ore settings; ore rows are grouped by planet. Changes affect new chunks only.";
    private static final String ORE_ROWS_COMMENT =
        "\u683c\u5f0f\uff1a\u884c\u661f\u952e|\u65b9\u5757 ID[@\u5143\u6570\u636e]|\u77ff\u8109\u5927\u5c0f|\u6bcf\u533a\u5757\u6b21\u6570|\u6700\u4f4e Y|\u6700\u9ad8 Y|\u66ff\u6362\u76ee\u6807\u3002\n"
            + "Format: planet key|block ID[@meta]|vein size|count per chunk|min Y|max Y|replace targets.\n"
            + "\u5b57\u6bb5\uff1adefault \u8868\u793a\u4f7f\u7528\u8be5\u884c\u661f\u9ed8\u8ba4\u65b9\u5757\uff1b\u4e5f\u53ef\u586b\u5199\u9017\u53f7\u5206\u9694\u7684\u65b9\u5757 ID\u3002\u8303\u56f4\uff1a\u77ff\u8109\u5927\u5c0f 1-64\uff0c\u6bcf\u533a\u5757\u6b21\u6570 0-100\uff0cY -80~320\u3002\n"
            + "Fields: default uses the planet's default blocks; comma-separated block IDs are also supported. Ranges: vein size 1-64, count 0-100, Y -80 to 320.\n"
            + "\u793a\u4f8b\uff08\u4e3a\u6708\u7403\u6dfb\u52a0\u94c1\u77ff\uff09\uff1amoon|minecraft:iron_ore|8|4|20|60|default\u3002\n"
            + "Example (add iron ore to the Moon): moon|minecraft:iron_ore|8|4|20|60|default.";
    private static final String DEBUG_COMMENT =
        "\u662f\u5426\u8f93\u51fa\u77ff\u8109\u751f\u6210\u65e5\u5fd7\u3002\nWhether to log ore generation.";
    private static final Map<String, List<OreEntry>> ORE_ROWS = new HashMap<>();

    public static boolean debugWorldgen;

    private OreGenConfig() {
    }

    public static void sync(Configuration config) {
        if (config == null) {
            return;
        }
        ORE_ROWS.clear();
        config.setCategoryComment(CATEGORY, CATEGORY_COMMENT);
        Property debug = config.get(CATEGORY, "debugWorldgen", false, DEBUG_COMMENT);
        debug.setComment(DEBUG_COMMENT);
        debugWorldgen = debug.getBoolean();

        removeLegacyRows(config);
        for (Map.Entry<String, List<String>> planet : defaultRowsByPlanet().entrySet()) {
            String category = planetCategory(planet.getKey());
            config.setCategoryComment(category, planetCategoryComment(planet.getKey()));
            String[] defaults = planet.getValue().toArray(new String[0]);
            boolean missingRows = !config.hasKey(category, ORE_ROWS_KEY);
            Property rowsProperty = config.get(category, ORE_ROWS_KEY, defaults,
                planetOreRowsComment(planet.getKey()));
            rowsProperty.setComment(planetOreRowsComment(planet.getKey()));
            if (missingRows) {
                rowsProperty.set(defaults);
            }

            for (String row : rowsProperty.getStringList()) {
                OreEntry entry = parse(row, planet.getKey());
                if (entry != null) {
                    ORE_ROWS.computeIfAbsent(entry.planet, key -> new ArrayList<>()).add(entry);
                }
            }
        }
        config.save();
    }

    /** Compatibility overload; legacy configuration values are intentionally ignored. */
    public static void sync(Configuration config, Configuration ignoredLegacyConfig) {
        sync(config);
    }

    public static List<OreEntry> getOreEntries(String planetName) {
        String key = normalizePlanetKey(planetName);
        if (key.endsWith("_orbit") || "earth_orbit".equals(key)) {
            return Collections.emptyList();
        }
        List<OreEntry> entries = ORE_ROWS.get(key);
        return entries == null ? Collections.emptyList() : Collections.unmodifiableList(entries);
    }

    public static int getModifiedOreCount(int baseCount) {
        return AdAstraConfig.getModifiedOreGeneration(baseCount);
    }

    /** Resolves a configured block state when the world generator first needs it. */
    public static IBlockState resolveBlockState(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        if (trimmed.isEmpty() || "default".equalsIgnoreCase(trimmed)) {
            return null;
        }
        int meta = 0;
        int separator = trimmed.lastIndexOf('@');
        if (separator >= 0) {
            String metaText = trimmed.substring(separator + 1).trim();
            trimmed = trimmed.substring(0, separator).trim();
            try {
                meta = Integer.parseInt(metaText);
            } catch (NumberFormatException ignored) {
                return null;
            }
            if (meta < 0 || meta > 15) {
                return null;
            }
        }
        if (trimmed.indexOf(':') < 0) {
            trimmed = "minecraft:" + trimmed;
        }
        try {
            Block block = Block.REGISTRY.getObject(new ResourceLocation(trimmed));
            return block == null || block == Blocks.AIR ? null : block.getStateFromMeta(meta);
        } catch (RuntimeException ignored) {
            return null;
        }
    }

    private static OreEntry parse(String row, String expectedPlanet) {
        if (row == null || row.trim().isEmpty() || row.trim().startsWith("#")) {
            return null;
        }
        String[] parts = row.trim().split("\\|", -1);
        if (parts.length != 7) {
            warn(row, "expected 7 fields");
            return null;
        }
        String planet = normalizePlanetKey(parts[0]);
        String target = parts[1].trim();
        String replaceTargets = parts[6].trim();
        if (planet.isEmpty() || !planet.equals(expectedPlanet) || target.isEmpty() || replaceTargets.isEmpty()) {
            if (!planet.equals(expectedPlanet)) {
                warn(row, "planet key does not match its category");
            } else {
                warn(row, "empty planet, target, or replaceTargets");
            }
            return null;
        }
        try {
            int veinSize = parseRange(parts[2], 1, 64);
            int count = parseRange(parts[3], 0, 100);
            int minY = parseRange(parts[4], MIN_Y, MAX_Y);
            int maxY = parseRange(parts[5], MIN_Y, MAX_Y);
            if (minY > maxY) {
                warn(row, "minY is greater than maxY");
                return null;
            }
            return new OreEntry(planet, target, veinSize, count, minY, maxY, replaceTargets);
        } catch (RuntimeException exception) {
            warn(row, exception.getMessage());
            return null;
        }
    }

    private static int parseRange(String value, int min, int max) {
        int parsed = Integer.parseInt(value.trim());
        if (parsed < min || parsed > max) {
            throw new IllegalArgumentException("value out of range");
        }
        return parsed;
    }

    private static String normalizePlanetKey(String value) {
        String source = value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
        StringBuilder result = new StringBuilder(source.length());
        for (int i = 0; i < source.length(); i++) {
            char c = source.charAt(i);
            result.append((c >= 'a' && c <= 'z') || (c >= '0' && c <= '9') || c == '_' ? c : '_');
        }
        return result.toString();
    }

    private static void warn(String row, String reason) {
        AdAstraReborn.LOGGER.warn("Ignored invalid ore config row '{}': {}", row, reason);
    }

    private static Map<String, List<String>> defaultRowsByPlanet() {
        Map<String, List<String>> rows = new LinkedHashMap<>();
        for (earth.terrarium.adastra.common.world.PlanetDimensionProperties properties : ModDimensions.getPlanetProperties()) {
            rows.computeIfAbsent(normalizePlanetKey(properties.getName()), key -> new ArrayList<>());
        }
        for (CustomPlanetDefinition definition : CustomPlanetRegistry.getDefinitions()) {
            rows.computeIfAbsent(normalizePlanetKey(definition.getPlanetName()), key -> new ArrayList<>());
        }

        addBuiltin(rows, "moon", "moon_cheese_ore", 8, 9, 6, 192, "ad_astra:moon_stone,ad_astra:moon_deepslate");
        addBuiltin(rows, "moon", "moon_desh_ore", 9, 9, -80, 80, "ad_astra:moon_stone,ad_astra:moon_deepslate");
        addBuiltin(rows, "moon", "moon_ice_shard_ore", 10, 8, -32, 32, "ad_astra:moon_stone,ad_astra:moon_deepslate");
        addBuiltin(rows, "moon", "moon_iron_ore", 11, 10, -24, 56, "ad_astra:moon_stone,ad_astra:moon_deepslate");
        addBuiltin(rows, "mars", "mars_diamond_ore", 7, 5, -80, 80, "ad_astra:mars_stone");
        addBuiltin(rows, "mars", "mars_ice_shard_ore", 10, 8, -32, 32, "ad_astra:mars_stone");
        addBuiltin(rows, "mars", "mars_iron_ore", 11, 10, -24, 56, "ad_astra:mars_stone");
        addBuiltin(rows, "mars", "mars_ostrum_ore", 8, 8, -80, 80, "ad_astra:mars_stone");
        addBuiltin(rows, "mercury", "mercury_iron_ore", 8, 20, -80, 192, "ad_astra:mercury_stone");
        addBuiltin(rows, "venus", "venus_calorite_ore", 8, 8, -80, 80, "ad_astra:venus_stone");
        addBuiltin(rows, "venus", "venus_coal_ore", 17, 20, -80, 192, "ad_astra:venus_stone");
        addBuiltin(rows, "venus", "venus_diamond_ore", 9, 5, -80, 80, "ad_astra:venus_stone");
        addBuiltin(rows, "venus", "venus_gold_ore", 10, 4, -64, 32, "ad_astra:venus_stone");
        addBuiltin(rows, "glacio", "glacio_coal_ore", 17, 20, -80, 192, "ad_astra:glacio_stone");
        addBuiltin(rows, "glacio", "glacio_ice_shard_ore", 17, 8, -32, 32, "ad_astra:glacio_stone");
        addBuiltin(rows, "glacio", "glacio_iron_ore", 11, 10, -24, 56, "ad_astra:glacio_stone");
        addBuiltin(rows, "glacio", "glacio_lapis_ore", 9, 2, -32, 32, "ad_astra:glacio_stone");

        addCustomDefaults(rows);
        return rows;
    }

    private static void removeLegacyRows(Configuration config) {
        ConfigCategory worldgen = config.getCategory(CATEGORY);
        if (worldgen != null) {
            worldgen.remove(ORE_ROWS_KEY);
        }
        for (String categoryName : new ArrayList<>(config.getCategoryNames())) {
            if (categoryName.startsWith("worldgen_")) {
                config.removeCategory(config.getCategory(categoryName));
            }
        }
    }

    private static String planetCategory(String planet) {
        return PLANET_CATEGORY_PREFIX + planet;
    }

    private static String planetCategoryComment(String planet) {
        String chineseName = chinesePlanetName(planet);
        String englishName = englishPlanetName(planet);
        return chineseName + "（" + englishName + "）\u7684\u77ff\u8109\u914d\u7f6e\uff0c\u53ea\u5f71\u54cd\u65b0\u751f\u6210\u533a\u5757\u3002\n"
            + "Ore settings for " + englishName + "; new chunks only.";
    }

    private static String planetOreRowsComment(String planet) {
        String example = planet + "|minecraft:iron_ore|8|4|20|60|default";
        return ORE_ROWS_COMMENT.replace(
            "moon|minecraft:iron_ore|8|4|20|60|default",
            example).replace(
            "\u4e3a\u6708\u7403\u6dfb\u52a0\u94c1\u77ff",
            "\u4e3a" + chinesePlanetName(planet) + "\u6dfb\u52a0\u94c1\u77ff").replace(
            "add iron ore to the Moon",
            "add iron ore to " + englishPlanetName(planet));
    }

    private static String chinesePlanetName(String key) {
        switch (key) {
            case "moon": return "\u6708\u7403";
            case "mars": return "\u706b\u661f";
            case "mercury": return "\u6c34\u661f";
            case "venus": return "\u91d1\u661f";
            case "glacio": return "\u971c\u539f\u661f";
            case "ceres": return "\u8c37\u795e\u661f";
            case "jupiter": return "\u6728\u661f";
            case "saturn": return "\u571f\u661f";
            case "uranus": return "\u5929\u738b\u661f";
            case "neptune": return "\u6d77\u738b\u661f";
            case "orcus": return "\u5965\u5e93\u65af";
            case "pluto": return "\u51a5\u738b\u661f";
            case "haumea": return "\u598a\u795e\u661f";
            case "quaoar": return "\u521b\u795e\u661f";
            case "makemake": return "\u9e1f\u795e\u661f";
            case "gonggong": return "\u5171\u5de5\u661f";
            case "eris": return "\u960b\u795e\u661f";
            case "sedna": return "\u585e\u5fb7\u5a1c";
            case "proxima_centauri_b": return "\u6bd4\u90bb\u661f b";
            default: return key;
        }
    }

    private static String englishPlanetName(String key) {
        switch (key) {
            case "moon": return "Moon";
            case "mars": return "Mars";
            case "mercury": return "Mercury";
            case "venus": return "Venus";
            case "glacio": return "Glacio";
            case "ceres": return "Ceres";
            case "jupiter": return "Jupiter";
            case "saturn": return "Saturn";
            case "uranus": return "Uranus";
            case "neptune": return "Neptune";
            case "orcus": return "Orcus";
            case "pluto": return "Pluto";
            case "haumea": return "Haumea";
            case "quaoar": return "Quaoar";
            case "makemake": return "Makemake";
            case "gonggong": return "Gonggong";
            case "eris": return "Eris";
            case "sedna": return "Sedna";
            case "proxima_centauri_b": return "Proxima Centauri b";
            default: return key;
        }
    }

    private static void addCustomDefaults(Map<String, List<String>> rows) {
        List<CustomPlanetDefinition> definitions = CustomPlanetRegistry.getDefinitions();
        if (!definitions.isEmpty()) {
            for (CustomPlanetDefinition definition : definitions) {
                String planet = normalizePlanetKey(definition.getPlanetName());
                List<String> planetRows = rows.computeIfAbsent(planet, key -> new ArrayList<>());
                for (CustomPlanetDefinition.OreDefinition ore : definition.getOres()) {
                    String oreState = stateId(ore.getOreBlock());
                    String replaceState = stateId(ore.getReplaceBlock());
                    if (oreState == null || replaceState == null) {
                        AdAstraReborn.LOGGER.warn("Skipped default ore row for custom planet '{}': missing block registry ID.", planet);
                        continue;
                    }
                    planetRows.add(planet + "|" + oreState + "|" + ore.getVeinSize() + "|" + ore.getCountPerChunk() + "|"
                        + ore.getMinY() + "|" + ore.getMaxY() + "|" + replaceState);
                }
            }
            return;
        }

        // Keep a bootstrap fallback for callers that sync before planet registration.
        addFallbackCustomDefaults(rows);
    }

    private static String stateId(IBlockState state) {
        if (state == null || state.getBlock().getRegistryName() == null) {
            return null;
        }
        String id = state.getBlock().getRegistryName().toString();
        int meta = state.getBlock().getMetaFromState(state);
        return meta == 0 ? id : id + "@" + meta;
    }

    private static void addFallbackCustomDefaults(Map<String, List<String>> rows) {
        addCustom(rows, "ceres", "ceres_copper_ore", 16, 17);
        addCustom(rows, "ceres", "ceres_iron_ore", 10, 11);
        addCustom(rows, "jupiter", "jupiter_juperium_ore", 8, 9);
        addCustom(rows, "jupiter", "jupiter_coal_ore", 20, 17);
        addCustom(rows, "jupiter", "jupiter_gold_ore", 4, 10);
        addCustom(rows, "jupiter", "jupiter_diamond_ore", 5, 9);
        addCustom(rows, "saturn", "saturn_saturlyte_ore", 8, 9);
        addCustom(rows, "saturn", "saturn_coal_ore", 20, 17);
        addCustom(rows, "saturn", "saturn_gold_ore", 4, 10);
        addCustom(rows, "saturn", "saturn_diamond_ore", 5, 9);
        addCustom(rows, "uranus", "uranus_uranium_ore", 8, 9);
        addCustom(rows, "uranus", "uranus_ice_shard_ore", 12, 11);
        addCustom(rows, "uranus", "uranus_iron_ore", 10, 11);
        addCustom(rows, "uranus", "uranus_lapis_ore", 5, 9);
        addCustom(rows, "uranus", "uranus_diamond_ore", 6, 9);
        addCustom(rows, "neptune", "neptune_neptunium_ore", 8, 9);
        addCustom(rows, "neptune", "neptune_ice_shard_ore", 11, 11);
        addCustom(rows, "neptune", "neptune_iron_ore", 10, 11);
        addCustom(rows, "neptune", "neptune_copper_ore", 10, 17);
        addCustom(rows, "neptune", "neptune_coal_ore", 9, 17);
        addCustom(rows, "orcus", "orcus_radium_ore", 6, 9);
        addCustom(rows, "orcus", "orcus_copper_ore", 16, 17);
        addCustom(rows, "orcus", "orcus_iron_ore", 10, 11);
        addCustom(rows, "pluto", "pluto_plutonium_ore", 6, 9);
        addCustom(rows, "pluto", "pluto_ice_shard_ore", 11, 9);
        addCustom(rows, "pluto", "pluto_gold_ore", 10, 10);
        addCustom(rows, "pluto", "pluto_diamond_ore", 8, 9);
        for (String planet : new String[] {"haumea", "quaoar", "makemake", "gonggong", "eris"}) {
            addCustom(rows, planet, planet + "_copper_ore", 16, 17);
            addCustom(rows, planet, planet + "_iron_ore", 10, 11);
        }
        addCustom(rows, "sedna", "sedna_electrolyte_ore", 5, 9);
        addCustom(rows, "sedna", "sedna_copper_ore", 16, 17);
        addCustom(rows, "sedna", "sedna_iron_ore", 10, 11);
        addCustom(rows, "proxima_centauri_b", "proxima_centauri_b_iron_ore", 10, 11);
        addCustom(rows, "proxima_centauri_b", "proxima_centauri_b_redstone_ore", 8, 9);
        addCustom(rows, "proxima_centauri_b", "proxima_centauri_b_emerald_ore", 6, 9);
        addCustom(rows, "proxima_centauri_b", "proxima_centauri_b_diamond_ore", 5, 9);
    }

    private static void addCustom(Map<String, List<String>> rows, String planet, String ore, int count, int veinSize) {
        rows.computeIfAbsent(planet, key -> new ArrayList<>())
            .add(planet + "|ad_astra:block_" + ore + "|" + veinSize + "|" + count + "|4|60|ad_astra:block_" + planet + "_stone");
    }

    private static void addBuiltin(Map<String, List<String>> rows, String planet, String ore, int veinSize, int count,
                                   int minY, int maxY, String replaceTargets) {
        rows.computeIfAbsent(planet, key -> new ArrayList<>())
            .add(planet + "|ad_astra:" + ore + "|" + veinSize + "|" + count + "|"
                + minY + "|" + maxY + "|" + replaceTargets);
    }

    public static final class OreEntry {
        public final String planet;
        public final String blockState;
        public final int veinSize;
        public final int countPerChunk;
        public final int minY;
        public final int maxY;
        public final String replaceTargets;

        private OreEntry(String planet, String blockState, int veinSize, int countPerChunk,
                         int minY, int maxY, String replaceTargets) {
            this.planet = planet;
            this.blockState = blockState;
            this.veinSize = veinSize;
            this.countPerChunk = countPerChunk;
            this.minY = minY;
            this.maxY = maxY;
            this.replaceTargets = replaceTargets;
        }
    }
}
