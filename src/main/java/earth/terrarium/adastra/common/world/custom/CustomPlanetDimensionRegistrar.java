package earth.terrarium.adastra.common.world.custom;

import earth.terrarium.adastra.AdAstraReborn;
import earth.terrarium.adastra.Reference;
import net.minecraft.world.DimensionType;
import net.minecraftforge.common.DimensionManager;

import java.util.Locale;

public final class CustomPlanetDimensionRegistrar {

    private CustomPlanetDimensionRegistrar() {
    }

    public static int registerQueuedDimensions() {
        int registered = 0;
        for (CustomPlanetDefinition definition : CustomPlanetRegistry.getDefinitions()) {
            if (!definition.shouldRegisterDimension()) {
                continue;
            }
            int dimensionId = definition.getDimensionId();

            // Register planet dimension
            if (CustomPlanetRegistry.getDimensionType(dimensionId) == null
                && !DimensionManager.isDimensionRegistered(dimensionId)) {
                String suffix = sanitize(definition.getId().getNamespace() + "_" + definition.getId().getPath());
                DimensionType type = DimensionType.register(
                    Reference.MOD_ID + "_custom_" + suffix,
                    "_" + Reference.MOD_ID + "_custom_" + suffix,
                    dimensionId,
                    WorldProviderCustomPlanet.class,
                    false);
                DimensionManager.registerDimension(dimensionId, type);
                CustomPlanetRegistry.registerDimensionType(dimensionId, type);
                registered++;
                AdAstraReborn.LOGGER.info("Registered custom planet dimension {} for {}.", dimensionId, definition.getId());
            } else if (DimensionManager.isDimensionRegistered(dimensionId)) {
                AdAstraReborn.LOGGER.warn(
                    "Custom planet {} requested dimension id {}, but that id is already registered.",
                    definition.getId(),
                    dimensionId);
            }

        }
        return registered;
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
