package earth.terrarium.adastra.integration.crafttweaker;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.IAction;
import crafttweaker.annotations.ZenRegister;
import earth.terrarium.adastra.common.world.custom.CustomPlanetDefinition;
import earth.terrarium.adastra.common.world.custom.CustomPlanetRegistry;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenRegister
@ZenClass("mods.ad_astra.CustomPlanets")
public final class CTCustomPlanets {

    private CTCustomPlanets() {
    }

    @ZenMethod("create")
    public static CTCustomPlanetBuilder create(String id, int dimensionId) {
        return new CTCustomPlanetBuilder(CustomPlanetDefinition.builder(id, dimensionId));
    }

    @ZenMethod("getRegisteredCount")
    public static int getRegisteredCount() {
        return CustomPlanetRegistry.size();
    }

    @ZenMethod("hasPlanet")
    public static boolean hasPlanet(String id) {
        return CustomPlanetRegistry.contains(CustomPlanetDefinition.parseId(id));
    }

    static void apply(CustomPlanetDefinition definition) {
        CraftTweakerAPI.apply(new RegisterCustomPlanetAction(definition));
    }

    private static final class RegisterCustomPlanetAction implements IAction {

        private final CustomPlanetDefinition definition;

        private RegisterCustomPlanetAction(CustomPlanetDefinition definition) {
            this.definition = definition;
        }

        @Override
        public void apply() {
            CustomPlanetRegistry.register(definition);
        }

        @Override
        public String describe() {
            return "Registering Ad Astra custom planet definition " + definition.getId();
        }

        @Override
        public boolean validate() {
            CustomPlanetDefinition existing = CustomPlanetRegistry.getByDimensionId(definition.getDimensionId());
            return existing == null || existing.getId().equals(definition.getId());
        }

        @Override
        public String describeInvalid() {
            return "Cannot register Ad Astra custom planet " + definition.getId()
                + ": dimension id " + definition.getDimensionId() + " is already used by another custom planet";
        }
    }
}
