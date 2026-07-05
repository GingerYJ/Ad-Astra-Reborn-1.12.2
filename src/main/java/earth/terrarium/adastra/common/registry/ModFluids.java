package earth.terrarium.adastra.common.registry;

import earth.terrarium.adastra.Reference;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

public final class ModFluids {

    public static final String OXYGEN_FLUID_NAME = "oxygen";
    public static final String HYDROGEN_FLUID_NAME = "hydrogen";

    public static final Fluid OXYGEN = register(OXYGEN_FLUID_NAME, 0xffdae6f0, -1, 0, 300, true);
    public static final Fluid HYDROGEN = register(HYDROGEN_FLUID_NAME, 0xff89cff0, -1, 0, 300, true);
    public static final Fluid OIL = register("oil", 0xff373a36, 2000, 2000, 300, false);
    public static final Fluid FUEL = register("fuel", 0xffe5292b, 1500, 1500, 300, false);
    public static final Fluid CRYO_FUEL = register("cryo_fuel", 0xff6cfffa, 71, 71, 77, false);

    private ModFluids() {
    }

    public static void init() {
        // Triggers static registration for callers that want an explicit lifecycle hook.
    }

    private static Fluid register(String name, int color, int density, int viscosity, int temperature, boolean gaseous) {
        Fluid fluid = new Fluid(name,
            new ResourceLocation(Reference.MOD_ID, "blocks/" + name + "_still"),
            new ResourceLocation(Reference.MOD_ID, "blocks/" + name + "_flow"))
            .setUnlocalizedName(Reference.MOD_ID + "." + name)
            .setColor(color)
            .setDensity(density)
            .setViscosity(viscosity)
            .setTemperature(temperature)
            .setGaseous(gaseous);
        if (!FluidRegistry.registerFluid(fluid)) {
            fluid = FluidRegistry.getFluid(name);
        }
        FluidRegistry.addBucketForFluid(fluid);
        return fluid;
    }
}
