package earth.terrarium.adastra.common.container;

import earth.terrarium.adastra.common.registry.ModFluids;
import net.minecraftforge.fluids.FluidStack;

import java.util.function.Predicate;

/**
 * Factory class for creating standardized energy and fluid containers
 * with common configurations used in Ad Astra machines.
 */
public class ContainerFactory {

    // ==================== ENERGY CONTAINERS ====================

    /**
     * Create a small energy container (100,000 FE capacity).
     * Used for: Solar Panel, Basic machines
     */
    public static AdAstraEnergyContainer createSmallEnergy() {
        return new AdAstraEnergyContainer(100_000, 1_000);
    }

    /**
     * Create a medium energy container (500,000 FE capacity).
     * Used for: Most processing machines
     */
    public static AdAstraEnergyContainer createMediumEnergy() {
        return new AdAstraEnergyContainer(500_000, 5_000);
    }

    /**
     * Create a large energy container (1,000,000 FE capacity).
     * Used for: Advanced machines, multi-block structures
     */
    public static AdAstraEnergyContainer createLargeEnergy() {
        return new AdAstraEnergyContainer(1_000_000, 10_000);
    }

    /**
     * Create a custom energy container.
     * @param capacity Total capacity in FE
     * @param maxTransfer Max transfer rate in FE/t
     */
    public static AdAstraEnergyContainer createEnergy(int capacity, int maxTransfer) {
        return new AdAstraEnergyContainer(capacity, maxTransfer);
    }

    /**
     * Create a custom energy container with separate I/O rates.
     * @param capacity Total capacity in FE
     * @param maxReceive Max input rate in FE/t
     * @param maxExtract Max output rate in FE/t
     */
    public static AdAstraEnergyContainer createEnergy(int capacity, int maxReceive, int maxExtract) {
        return new AdAstraEnergyContainer(capacity, maxReceive, maxExtract);
    }

    /**
     * Create a sided energy container (for machines with side configuration).
     */
    public static SidedEnergyContainer createSidedEnergy(AdAstraEnergyContainer container) {
        return new SidedEnergyContainer(container);
    }

    // ==================== FLUID CONTAINERS ====================

    /**
     * Create a small fluid tank (4000 mB = 4 buckets).
     * Used for: Basic fluid machines
     */
    public static AdAstraFluidTank createSmallFluid() {
        return new AdAstraFluidTank(4_000);
    }

    /**
     * Create a medium fluid tank (8000 mB = 8 buckets).
     * Used for: Most fluid processing machines
     */
    public static AdAstraFluidTank createMediumFluid() {
        return new AdAstraFluidTank(8_000);
    }

    /**
     * Create a large fluid tank (16000 mB = 16 buckets).
     * Used for: Advanced fluid machines, storage
     */
    public static AdAstraFluidTank createLargeFluid() {
        return new AdAstraFluidTank(16_000);
    }

    /**
     * Create a custom fluid tank.
     * @param capacity Capacity in mB (1000 mB = 1 bucket)
     */
    public static AdAstraFluidTank createFluid(int capacity) {
        return new AdAstraFluidTank(capacity);
    }

    /**
     * Create a custom fluid tank with a filter.
     * @param capacity Capacity in mB
     * @param filter Predicate to validate fluids
     */
    public static AdAstraFluidTank createFluid(int capacity, Predicate<FluidStack> filter) {
        return new AdAstraFluidTank(capacity, filter);
    }

    /**
     * Create a bi-fluid tank (input/output separation).
     * @param capacity Capacity per tank in mB
     */
    public static BiFluidTank createBiFluid(int capacity) {
        return new BiFluidTank(capacity);
    }

    /**
     * Create a bi-fluid tank with different capacities.
     * @param inputCapacity Input tank capacity in mB
     * @param outputCapacity Output tank capacity in mB
     */
    public static BiFluidTank createBiFluid(int inputCapacity, int outputCapacity) {
        return new BiFluidTank(inputCapacity, outputCapacity);
    }

    /**
     * Create a bi-fluid tank with filters.
     * @param inputCapacity Input tank capacity in mB
     * @param outputCapacity Output tank capacity in mB
     * @param inputFilter Filter for input tank
     * @param outputFilter Filter for output tank
     */
    public static BiFluidTank createBiFluid(int inputCapacity, int outputCapacity,
                                            Predicate<FluidStack> inputFilter,
                                            Predicate<FluidStack> outputFilter) {
        return new BiFluidTank(inputCapacity, outputCapacity, inputFilter, outputFilter);
    }

    /**
     * Create a sided fluid container (for machines with side configuration).
     */
    public static SidedFluidContainer createSidedFluid(AdAstraFluidTank tank) {
        return new SidedFluidContainer(tank);
    }

    // ==================== COMMON FLUID FILTERS ====================

    /**
     * Filter that only accepts oxygen fluid.
     */
    public static Predicate<FluidStack> oxygenOnly() {
        return fluid -> {
            if (fluid == null || fluid.getFluid() == null) return false;
            return fluid.getFluid() == ModFluids.OXYGEN;
        };
    }

    /**
     * Filter that only accepts fuel fluids.
     */
    public static Predicate<FluidStack> fuelOnly() {
        return fluid -> {
            if (fluid == null || fluid.getFluid() == null) return false;
            String name = fluid.getFluid().getName();
            return name.equals("fuel") || name.contains("fuel") || name.contains("gasoline");
        };
    }

    /**
     * Filter that only accepts oil/petroleum fluids.
     */
    public static Predicate<FluidStack> oilOnly() {
        return fluid -> {
            if (fluid == null || fluid.getFluid() == null) return false;
            String name = fluid.getFluid().getName();
            return name.equals("oil") || name.contains("oil") || name.contains("petroleum");
        };
    }

    /**
     * Filter that only accepts cryo fuel fluids.
     */
    public static Predicate<FluidStack> cryoFuelOnly() {
        return fluid -> {
            if (fluid == null || fluid.getFluid() == null) return false;
            String name = fluid.getFluid().getName();
            return name.equals("cryo_fuel") || name.contains("cryo");
        };
    }

    // ==================== MACHINE-SPECIFIC CONTAINERS ====================

    /**
     * Create containers for Coal Generator.
     * Energy: 100k capacity, 1k FE/t output
     */
    public static AdAstraEnergyContainer createCoalGeneratorEnergy() {
        AdAstraEnergyContainer container = new AdAstraEnergyContainer(100_000, 0, 1_000);
        container.setCanAutoReceive(false);
        return container;
    }

    /**
     * Create containers for Solar Panel.
     * Energy: 100k capacity, 500 FE/t output
     */
    public static AdAstraEnergyContainer createSolarPanelEnergy() {
        AdAstraEnergyContainer container = new AdAstraEnergyContainer(100_000, 0, 500);
        container.setCanAutoReceive(false);
        return container;
    }

    /**
     * Create containers for Compressor.
     * Energy: 500k capacity, 5k FE/t input
     */
    public static AdAstraEnergyContainer createCompressorEnergy() {
        return new AdAstraEnergyContainer(500_000, 5_000, 0);
    }

    /**
     * Create containers for Fuel Refinery.
     * Energy: 500k capacity
     * Input fluid: 8 buckets (oil)
     * Output fluid: 8 buckets (fuel)
     */
    public static BiFluidTank createFuelRefineryFluid() {
        return new BiFluidTank(8_000, 8_000, oilOnly(), fuelOnly());
    }

    /**
     * Create containers for Oxygen Loader.
     * Input fluid: 8 buckets
     * Output fluid: 8 buckets (oxygen)
     */
    public static BiFluidTank createOxygenLoaderFluid() {
        return new BiFluidTank(8_000, 8_000, null, oxygenOnly());
    }

    /**
     * Create containers for Water Pump.
     * Fluid: 8 buckets (water output)
     */
    public static AdAstraFluidTank createWaterPumpFluid() {
        return new AdAstraFluidTank(8_000, fluid -> {
            if (fluid == null || fluid.getFluid() == null) return false;
            return fluid.getFluid().getName().equals("water");
        });
    }

    /**
     * Create containers for Cryo Freezer.
     * Input fluid: 4 buckets
     * Output fluid: 4 buckets (cryo fuel)
     */
    public static BiFluidTank createCryoFreezerFluid() {
        return new BiFluidTank(4_000, 4_000, null, cryoFuelOnly());
    }
}
