package earth.terrarium.adastra.common.world.custom;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.DimensionType;
import earth.terrarium.adastra.common.world.PlanetDimensionProperties;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Code-owned registry for the additional Ad Astra planets.
 *
 * <p>These definitions use the same world-generation data model as custom
 * planets, but are kept in a separate registry so third-party integrations
 * cannot replace built-in content.</p>
 */
public final class BuiltInPlanetRegistry {

    private static final Map<ResourceLocation, CustomPlanetDefinition> DEFINITIONS = new LinkedHashMap<>();
    private static final Map<Integer, ResourceLocation> SURFACE_INDEX = new LinkedHashMap<>();
    private static final Map<Integer, DimensionType> DIMENSION_TYPES = new LinkedHashMap<>();

    private BuiltInPlanetRegistry() {
    }

    public static synchronized CustomPlanetDefinition register(CustomPlanetDefinition definition) {
        if (definition == null) {
            throw new IllegalArgumentException("Built-in planet definition cannot be null.");
        }

        CustomPlanetDefinition previous = DEFINITIONS.get(definition.getId());
        if (previous != null && previous != definition) {
            throw new IllegalArgumentException("Built-in planet id is already registered: " + definition.getId());
        }

        ResourceLocation surfaceOwner = SURFACE_INDEX.get(definition.getDimensionId());
        if (surfaceOwner != null && !surfaceOwner.equals(definition.getId())) {
            throw new IllegalArgumentException("Built-in surface dimension id " + definition.getDimensionId()
                + " is already used by " + surfaceOwner + ".");
        }

        DEFINITIONS.put(definition.getId(), definition);
        SURFACE_INDEX.put(definition.getDimensionId(), definition.getId());
        return definition;
    }

    public static synchronized boolean contains(ResourceLocation id) {
        return id != null && DEFINITIONS.containsKey(id);
    }

    public static synchronized boolean containsDimension(int dimensionId) {
        return SURFACE_INDEX.containsKey(dimensionId);
    }

    @Nullable
    public static synchronized CustomPlanetDefinition get(ResourceLocation id) {
        return id == null ? null : DEFINITIONS.get(id);
    }

    @Nullable
    public static synchronized CustomPlanetDefinition getByDimensionId(int dimensionId) {
        ResourceLocation id = SURFACE_INDEX.get(dimensionId);
        return id == null ? null : DEFINITIONS.get(id);
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

    public static synchronized void registerDimensionType(int dimensionId, DimensionType type) {
        if (!containsDimension(dimensionId)) {
            throw new IllegalArgumentException("Dimension id " + dimensionId + " is not a built-in surface dimension.");
        }
        if (type == null) {
            throw new IllegalArgumentException("dimensionType cannot be null.");
        }
        DIMENSION_TYPES.put(dimensionId, type);
    }

    @Nullable
    public static synchronized DimensionType getDimensionType(int dimensionId) {
        return DIMENSION_TYPES.get(dimensionId);
    }

    public static synchronized int size() {
        return DEFINITIONS.size();
    }
}
