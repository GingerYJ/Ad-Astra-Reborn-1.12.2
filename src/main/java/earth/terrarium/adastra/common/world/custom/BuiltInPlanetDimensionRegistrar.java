package earth.terrarium.adastra.common.world.custom;

import earth.terrarium.adastra.AdAstraReborn;
import earth.terrarium.adastra.Reference;
import net.minecraft.world.DimensionType;
import net.minecraftforge.common.DimensionManager;

import java.util.Locale;

/** Registers code-owned custom-shaped planet dimensions during pre-init. */
public final class BuiltInPlanetDimensionRegistrar {

    private BuiltInPlanetDimensionRegistrar() {
    }

    public static int register() {
        int registered = 0;
        for (CustomPlanetDefinition definition : BuiltInPlanetRegistry.getDefinitions()) {
            registered += registerDimension(definition);
        }
        return registered;
    }

    private static int registerDimension(CustomPlanetDefinition definition) {
        int dimensionId = definition.getDimensionId();
        if (BuiltInPlanetRegistry.getDimensionType(dimensionId) != null) {
            return 0;
        }
        if (DimensionManager.isDimensionRegistered(dimensionId)) {
            AdAstraReborn.LOGGER.warn(
                "Built-in planet {} cannot register surface dimension id {}; the id is already registered.",
                definition.getId(), dimensionId);
            return 0;
        }

        String suffix = sanitize(definition.getId().getNamespace() + "_" + definition.getId().getPath());
        DimensionType type = DimensionType.register(
            Reference.MOD_ID + "_builtin_" + suffix,
            "_" + Reference.MOD_ID + "_builtin_" + suffix,
            dimensionId,
            WorldProviderCustomPlanet.class,
            false);
        DimensionManager.registerDimension(dimensionId, type);
        BuiltInPlanetRegistry.registerDimensionType(dimensionId, type);
        AdAstraReborn.LOGGER.info(
            "Registered built-in planet dimension {} for {}.", dimensionId, definition.getId());
        return 1;
    }

    private static String sanitize(String value) {
        String lower = value.toLowerCase(Locale.ROOT);
        StringBuilder builder = new StringBuilder(lower.length());
        for (int i = 0; i < lower.length(); i++) {
            char c = lower.charAt(i);
            if ((c >= 'a' && c <= 'z') || (c >= '0' && c <= '9') || c == '_') {
                builder.append(c);
            } else {
                builder.append('_');
            }
        }
        return builder.length() == 0 ? "planet" : builder.toString();
    }
}
