package earth.terrarium.adastra.common.menus.base;

import earth.terrarium.adastra.common.menus.PlanetsMenu;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public final class PlanetsMenuProvider {

    private PlanetsMenuProvider() {
    }

    public static PlanetsMenu createClientMenu(EntityPlayer player, int rocketTier, int rocketEntityId) {
        return new PlanetsMenu(player.inventory, rocketTier, rocketEntityId, Collections.emptySet());
    }

    public static Set<ResourceLocation> createDisabledPlanets(String disabledPlanets) {
        if (disabledPlanets == null || disabledPlanets.trim().isEmpty()) {
            return Collections.emptySet();
        }
        Set<ResourceLocation> disabled = new HashSet<>();
        for (String entry : disabledPlanets.split(",")) {
            String trimmed = entry.trim();
            if (!trimmed.isEmpty()) {
                disabled.add(new ResourceLocation(trimmed));
            }
        }
        return Collections.unmodifiableSet(disabled);
    }
}
