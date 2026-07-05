package earth.terrarium.adastra.common.world.custom;

import earth.terrarium.adastra.common.world.PlanetDimensionProperties;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.DimensionType;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class CustomPlanetRegistry {

    private static final Map<ResourceLocation, CustomPlanetDefinition> DEFINITIONS = new LinkedHashMap<>();
    private static final Map<Integer, ResourceLocation> DIMENSION_INDEX = new LinkedHashMap<>();
    private static final Map<Integer, DimensionType> DIMENSION_TYPES = new LinkedHashMap<>();

    private CustomPlanetRegistry() {
    }

    public static synchronized CustomPlanetDefinition register(CustomPlanetDefinition definition) {
        if (definition == null) {
            throw new IllegalArgumentException("Custom planet definition cannot be null.");
        }

        ResourceLocation indexedId = DIMENSION_INDEX.get(definition.getDimensionId());
        if (indexedId != null && !indexedId.equals(definition.getId())) {
            throw new IllegalArgumentException("Dimension id " + definition.getDimensionId()
                + " is already used by custom planet " + indexedId + ".");
        }

        CustomPlanetDefinition previous = DEFINITIONS.put(definition.getId(), definition);
        if (previous != null) {
            DIMENSION_INDEX.remove(previous.getDimensionId());
        }
        DIMENSION_INDEX.put(definition.getDimensionId(), definition.getId());
        return definition;
    }

    public static synchronized boolean contains(ResourceLocation id) {
        return DEFINITIONS.containsKey(id);
    }

    public static synchronized boolean containsDimension(int dimensionId) {
        return DIMENSION_INDEX.containsKey(dimensionId);
    }

    @Nullable
    public static synchronized CustomPlanetDefinition get(ResourceLocation id) {
        return DEFINITIONS.get(id);
    }

    @Nullable
    public static synchronized CustomPlanetDefinition getByDimensionId(int dimensionId) {
        ResourceLocation id = DIMENSION_INDEX.get(dimensionId);
        return id == null ? null : DEFINITIONS.get(id);
    }

    @Nullable
    public static synchronized CustomPlanetDefinition getByOrbitDimensionId(int orbitDimensionId) {
        for (CustomPlanetDefinition definition : DEFINITIONS.values()) {
            if (definition.getOrbitDimensionId() == orbitDimensionId) {
                return definition;
            }
        }
        return null;
    }

    public static synchronized List<CustomPlanetDefinition> getDefinitions() {
        return Collections.unmodifiableList(new ArrayList<>(DEFINITIONS.values()));
    }

    public static synchronized List<PlanetDimensionProperties> getPlanetProperties() {
        List<PlanetDimensionProperties> properties = new ArrayList<>();
        for (CustomPlanetDefinition definition : DEFINITIONS.values()) {
            properties.add(definition.toDimensionProperties());
        }
        return Collections.unmodifiableList(properties);
    }

    @Nullable
    public static synchronized PlanetDimensionProperties getPlanetProperties(int dimensionId) {
        CustomPlanetDefinition definition = getByDimensionId(dimensionId);
        return definition == null ? null : definition.toDimensionProperties();
    }

    public static synchronized void registerDimensionType(int dimensionId, DimensionType dimensionType) {
        if (dimensionType == null) {
            throw new IllegalArgumentException("dimensionType cannot be null.");
        }
        DIMENSION_TYPES.put(dimensionId, dimensionType);
    }

    @Nullable
    public static synchronized DimensionType getDimensionType(int dimensionId) {
        return DIMENSION_TYPES.get(dimensionId);
    }

    public static synchronized int size() {
        return DEFINITIONS.size();
    }
}
