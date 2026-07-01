package earth.terrarium.adastra.common.registry;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

public final class ModFluids {

    private static final ResourceLocation STILL = new ResourceLocation("minecraft", "blocks/water_still");
    private static final ResourceLocation FLOWING = new ResourceLocation("minecraft", "blocks/water_flow");

    public static final Fluid OXYGEN = register("oxygen", 0xffdae6f0, -1, 0, 300, true);
    public static final Fluid HYDROGEN = register("hydrogen", 0xff89cff0, -1, 0, 300, true);
    public static final Fluid OIL = register("oil", 0xff373a36, 2000, 2000, 300, false);
    public static final Fluid FUEL = register("fuel", 0xffe5292b, 1500, 1500, 300, false);
    public static final Fluid CRYO_FUEL = register("cryo_fuel", 0xff6cfffa, 71, 71, 77, false);

    private ModFluids() {
    }

    public static void init() {
        // Triggers static registration for callers that want an explicit lifecycle hook.
    }

    private static Fluid register(String name, int color, int density, int viscosity, int temperature, boolean gaseous) {
        Fluid fluid = new Fluid(name, STILL, FLOWING)
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
