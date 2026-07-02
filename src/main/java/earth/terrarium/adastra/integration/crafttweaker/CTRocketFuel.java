package earth.terrarium.adastra.integration.crafttweaker;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.IAction;
import crafttweaker.annotations.ZenRegister;
import earth.terrarium.adastra.common.util.RocketFuelHelper;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenRegister
@ZenClass("mods.ad_astra.RocketFuel")
public final class CTRocketFuel {

    private CTRocketFuel() {
    }

    @ZenMethod("addFuel")
    public static void addFuel(String fluidName, int fuelTier) {
        CraftTweakerAPI.apply(new AddFuelAction(fluidName, fuelTier));
    }

    @ZenMethod("removeFuel")
    public static void removeFuel(String fluidName) {
        CraftTweakerAPI.apply(new RemoveFuelAction(fluidName));
    }

    private static boolean hasFluidName(String fluidName) {
        return fluidName != null && !fluidName.trim().isEmpty();
    }

    private static final class AddFuelAction implements IAction {

        private final String fluidName;
        private final int fuelTier;

        private AddFuelAction(String fluidName, int fuelTier) {
            this.fluidName = fluidName == null ? "" : fluidName.trim();
            this.fuelTier = fuelTier;
        }

        @Override
        public void apply() {
            RocketFuelHelper.addFuel(fluidName, fuelTier);
        }

        @Override
        public String describe() {
            return "Adding Ad Astra rocket fuel " + fluidName + " at tier " + fuelTier;
        }

        @Override
        public boolean validate() {
            return hasFluidName(fluidName) && fuelTier > 0 && RocketFuelHelper.resolveFluidName(fluidName) != null;
        }

        @Override
        public String describeInvalid() {
            return "Cannot add Ad Astra rocket fuel " + fluidName + ": unknown fluid or invalid tier";
        }
    }

    private static final class RemoveFuelAction implements IAction {

        private final String fluidName;

        private RemoveFuelAction(String fluidName) {
            this.fluidName = fluidName == null ? "" : fluidName.trim();
        }

        @Override
        public void apply() {
            RocketFuelHelper.removeFuel(fluidName);
        }

        @Override
        public String describe() {
            return "Removing Ad Astra rocket fuel " + fluidName;
        }

        @Override
        public boolean validate() {
            return hasFluidName(fluidName);
        }

        @Override
        public String describeInvalid() {
            return "Cannot remove Ad Astra rocket fuel: missing fluid name";
        }
    }
}
