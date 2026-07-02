package earth.terrarium.adastra.common.util;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class PlanetTierOverrideRegistry {

    private static final Map<Integer, Integer> PLANET_TIERS = new ConcurrentHashMap<>();

    private PlanetTierOverrideRegistry() {
    }

    public static void setPlanetTier(int dimensionId, int tier) {
        PLANET_TIERS.put(dimensionId, Math.max(0, tier));
    }

    public static boolean removePlanetTier(int dimensionId) {
        return PLANET_TIERS.remove(dimensionId) != null;
    }

    public static boolean hasPlanetTier(int dimensionId) {
        return PLANET_TIERS.containsKey(dimensionId);
    }

    public static int getPlanetTier(int dimensionId, int fallbackTier) {
        Integer tier = PLANET_TIERS.get(dimensionId);
        return tier == null ? fallbackTier : tier;
    }

    public static Map<Integer, Integer> getPlanetTiers() {
        return Collections.unmodifiableMap(PLANET_TIERS);
    }
}
