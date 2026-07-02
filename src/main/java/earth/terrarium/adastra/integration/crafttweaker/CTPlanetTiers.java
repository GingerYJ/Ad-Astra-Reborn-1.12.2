package earth.terrarium.adastra.integration.crafttweaker;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.IAction;
import crafttweaker.annotations.ZenRegister;
import earth.terrarium.adastra.common.util.PlanetTierOverrideRegistry;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenRegister
@ZenClass("mods.ad_astra.PlanetTiers")
public final class CTPlanetTiers {

    private CTPlanetTiers() {
    }

    @ZenMethod("setPlanetTier")
    public static void setPlanetTier(int dimensionId, int tier) {
        CraftTweakerAPI.apply(new SetPlanetTierAction(dimensionId, tier));
    }

    @ZenMethod("removePlanetTier")
    public static void removePlanetTier(int dimensionId) {
        CraftTweakerAPI.apply(new RemovePlanetTierAction(dimensionId));
    }

    private static final class SetPlanetTierAction implements IAction {

        private final int dimensionId;
        private final int tier;

        private SetPlanetTierAction(int dimensionId, int tier) {
            this.dimensionId = dimensionId;
            this.tier = tier;
        }

        @Override
        public void apply() {
            PlanetTierOverrideRegistry.setPlanetTier(dimensionId, tier);
        }

        @Override
        public String describe() {
            return "Setting Ad Astra planet dimension " + dimensionId + " to rocket tier " + tier;
        }

        @Override
        public boolean validate() {
            return tier >= 0;
        }

        @Override
        public String describeInvalid() {
            return "Cannot set Ad Astra planet tier for dimension " + dimensionId + ": tier must be 0 or higher";
        }
    }

    private static final class RemovePlanetTierAction implements IAction {

        private final int dimensionId;

        private RemovePlanetTierAction(int dimensionId) {
            this.dimensionId = dimensionId;
        }

        @Override
        public void apply() {
            PlanetTierOverrideRegistry.removePlanetTier(dimensionId);
        }

        @Override
        public String describe() {
            return "Removing Ad Astra planet tier override for dimension " + dimensionId;
        }
    }
}
