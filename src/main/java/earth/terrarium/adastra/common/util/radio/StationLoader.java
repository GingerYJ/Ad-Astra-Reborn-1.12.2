package earth.terrarium.adastra.common.util.radio;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import earth.terrarium.adastra.AdAstraReborn;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class StationLoader {

    private static final JsonParser PARSER = new JsonParser();
    private static final List<StationInfo> STATIONS = new ArrayList<>();
    private static boolean loaded;

    private StationLoader() {
    }

    public static synchronized List<StationInfo> stations() {
        if (!loaded) {
            load();
        }
        return Collections.unmodifiableList(new ArrayList<>(STATIONS));
    }

    public static synchronized boolean hasStation(String url) {
        if (!loaded) {
            load();
        }
        return STATIONS.stream().anyMatch(station -> station.getUrl().equals(url));
    }

    public static synchronized void reload() {
        loaded = false;
        load();
    }

    private static void load() {
        STATIONS.clear();
        try (BufferedReader reader = openStationsReader()) {
            if (reader == null) {
                loaded = true;
                return;
            }
            JsonObject root = PARSER.parse(reader).getAsJsonObject();
            JsonArray stations = root.getAsJsonArray("stations");
            if (stations != null) {
                for (JsonElement element : stations) {
                    if (element != null && element.isJsonObject()) {
                        StationInfo station = parseStation(element.getAsJsonObject());
                        if (!station.getUrl().isEmpty()) {
                            STATIONS.add(station);
                        }
                    }
                }
            }
        } catch (Exception e) {
            AdAstraReborn.LOGGER.warn("Failed to load radio stations.", e);
        } finally {
            loaded = true;
        }
    }

    private static BufferedReader openStationsReader() throws Exception {
        String stationsFile = System.getProperty("adastra.stations");
        if (stationsFile != null && !stationsFile.trim().isEmpty()) {
            AdAstraReborn.LOGGER.info("Loading radio stations from {}.", stationsFile);
            return Files.newBufferedReader(Paths.get(stationsFile), StandardCharsets.UTF_8);
        }

        InputStream stream = StationLoader.class.getClassLoader().getResourceAsStream("stations.json");
        return stream == null ? null : new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
    }

    private static StationInfo parseStation(JsonObject object) {
        String url = getString(object, "url", "");
        String title = getString(object, "title", "0.00 N/A");
        String name = getString(object, "name", "Unknown Station");
        StationLocation location = parseLocation(getString(object, "location", "UNKNOWN"));
        return new StationInfo(url, title, name, location);
    }

    private static String getString(JsonObject object, String key, String fallback) {
        JsonElement element = object.get(key);
        return element == null || element.isJsonNull() ? fallback : element.getAsString();
    }

    private static StationLocation parseLocation(String value) {
        try {
            return StationLocation.valueOf(value.trim().toUpperCase(java.util.Locale.ROOT));
        } catch (Exception ignored) {
            return StationLocation.UNKNOWN;
        }
    }
}
