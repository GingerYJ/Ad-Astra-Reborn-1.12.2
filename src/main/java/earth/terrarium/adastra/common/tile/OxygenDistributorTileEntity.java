package earth.terrarium.adastra.common.tile;

import earth.terrarium.adastra.common.registry.ModFluids;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.FluidTankProperties;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.items.ItemHandlerHelper;

public class OxygenDistributorTileEntity extends AdAstraMachineTileEntity {

    private static final int INPUT_CONTAINER_SLOT = 1;
    private static final int EMPTY_CONTAINER_SLOT = 2;
    private static final int MAX_DISTRIBUTION_BLOCKS = 6000;
    private static final int DISTRIBUTION_REFRESH_RATE = 100;
    private static final int WATER_INPUT_PER_OPERATION = 100;
    private static final int WATER_OXYGEN_OUTPUT_PER_OPERATION = 4;
    private static final int MAX_WORKING_RADIUS = calculateMaxRadius(MAX_DISTRIBUTION_BLOCKS);
    private static final int DEFAULT_WORKING_RADIUS = MAX_WORKING_RADIUS;

    private final IFluidHandler fluidHandler = new DistributorFluidHandler();

    private boolean providingOxygen;
    private int workingRadius = DEFAULT_WORKING_RADIUS;
    private int plannedBlocksCount = countBlocksInRadius(DEFAULT_WORKING_RADIUS);
    private int distributedBlocksCount;
    private int energyPerTick;
    private int oxygenPerTick;
    private int ticksUntilRefresh;

    public OxygenDistributorTileEntity() {
        super("oxygen_distributor", 3, DESH_ENERGY, DESH_IO, 0, DESH_FLUID);
        setAllSideModes(SideConfigType.ENERGY, AdAstraSideMode.PULL);
        setAllSideModes(SideConfigType.FLUID, AdAstraSideMode.PULL);
    }

    @Override
    protected void tickMachine() {
        if (energy == null || fluidTank == null || !canFunction()) {
            stopProvidingOxygen();
            return;
        }

        moveInputContainerToTank();
        refreshDistributionEstimateIfNeeded();

        int requiredEnergy = calculateEnergyPerTick(plannedBlocksCount);
        int requiredOxygen = calculateOxygenPerTick(plannedBlocksCount);
        if (!canMaintainDistribution(requiredEnergy, requiredOxygen)) {
            stopProvidingOxygen();
            return;
        }

        energy.extractEnergy(requiredEnergy, false);
        drainDistributionFluid(requiredOxygen);
        providingOxygen = true;
        distributedBlocksCount = plannedBlocksCount;
        energyPerTick = requiredEnergy;
        oxygenPerTick = requiredOxygen;
        setLit(true);
        markDirty();
    }

    private void moveInputContainerToTank() {
        ItemStack stack = items.getStackInSlot(INPUT_CONTAINER_SLOT);
        if (stack.isEmpty()) {
            return;
        }

        FluidStack contained = FluidUtil.getFluidContained(stack);
        if (!isValidInputFluid(contained)) {
            return;
        }

        ItemStack single = stack.copy();
        single.setCount(1);
        FluidActionResult simulated = FluidUtil.tryEmptyContainer(single, fluidHandler, fluidTank.getCapacity(), null, false);
        if (simulated.isSuccess() && storeContainerResult(EMPTY_CONTAINER_SLOT, simulated.getResult())) {
            FluidActionResult result = FluidUtil.tryEmptyContainer(single, fluidHandler, fluidTank.getCapacity(), null, true);
            if (!result.isSuccess()) {
                return;
            }
            stack.shrink(1);
            if (stack.isEmpty()) {
                items.setStackInSlot(INPUT_CONTAINER_SLOT, ItemStack.EMPTY);
            }
            insertContainerResult(EMPTY_CONTAINER_SLOT, result.getResult());
            markDirty();
        }
    }

    private boolean storeContainerResult(int slot, ItemStack result) {
        if (result.isEmpty()) {
            return true;
        }

        ItemStack stored = items.getStackInSlot(slot);
        if (stored.isEmpty()) {
            return true;
        }
        return ItemHandlerHelper.canItemStacksStack(stored, result)
            && stored.getCount() + result.getCount() <= Math.min(stored.getMaxStackSize(), items.getSlotLimit(slot));
    }

    private void insertContainerResult(int slot, ItemStack result) {
        if (result.isEmpty()) {
            return;
        }

        ItemStack stored = items.getStackInSlot(slot);
        if (stored.isEmpty()) {
            items.setStackInSlot(slot, result.copy());
        } else {
            stored.grow(result.getCount());
        }
    }

    private void refreshDistributionEstimateIfNeeded() {
        if (ticksUntilRefresh > 0) {
            ticksUntilRefresh--;
            return;
        }
        plannedBlocksCount = Math.min(countBlocksInRadius(workingRadius), MAX_DISTRIBUTION_BLOCKS);
        ticksUntilRefresh = DISTRIBUTION_REFRESH_RATE;
    }

    private boolean canMaintainDistribution(int requiredEnergy, int requiredOxygen) {
        if (plannedBlocksCount <= 0) {
            return false;
        }
        if (energy.extractEnergy(requiredEnergy, true) < requiredEnergy) {
            return false;
        }
        return canConsumeOxygen(requiredOxygen);
    }

    private boolean canConsumeOxygen(int oxygenAmount) {
        FluidStack stored = fluidTank.getFluid();
        if (!isValidInputFluid(stored)) {
            return false;
        }
        return stored.amount >= getStoredFluidCostForOxygen(stored.getFluid(), oxygenAmount);
    }

    private void drainDistributionFluid(int oxygenAmount) {
        FluidStack stored = fluidTank.getFluid();
        if (!isValidInputFluid(stored)) {
            return;
        }
        fluidTank.drainInternal(getStoredFluidCostForOxygen(stored.getFluid(), oxygenAmount), true);
    }

    private int getStoredFluidCostForOxygen(Fluid fluid, int oxygenAmount) {
        if (fluid == FluidRegistry.WATER) {
            return Math.max(1, (oxygenAmount * WATER_INPUT_PER_OPERATION + WATER_OXYGEN_OUTPUT_PER_OPERATION - 1) / WATER_OXYGEN_OUTPUT_PER_OPERATION);
        }
        return oxygenAmount;
    }

    private void stopProvidingOxygen() {
        if (providingOxygen || distributedBlocksCount != 0 || energyPerTick != 0 || oxygenPerTick != 0) {
            providingOxygen = false;
            distributedBlocksCount = 0;
            energyPerTick = 0;
            oxygenPerTick = 0;
            markDirty();
        }
        setLit(false);
    }

    private int calculateEnergyPerTick(int blocksCount) {
        return Math.max(1, blocksCount / 50);
    }

    private int calculateOxygenPerTick(int blocksCount) {
        return Math.max(1, blocksCount / 1500);
    }

    private boolean isValidInputFluid(FluidStack fluid) {
        return fluid != null && (fluid.getFluid() == ModFluids.OXYGEN || fluid.getFluid() == FluidRegistry.WATER);
    }

    public boolean isProvidingOxygen() {
        return providingOxygen && canFunction();
    }

    public boolean isProvidingOxygen(BlockPos target) {
        if (!isProvidingOxygen() || pos == null || target == null) {
            return false;
        }

        long dx = target.getX() - pos.getX();
        long dy = target.getY() - pos.getY();
        long dz = target.getZ() - pos.getZ();
        long radius = workingRadius;
        return dx * dx + dy * dy + dz * dz <= radius * radius;
    }

    public int getWorkingRadius() {
        return workingRadius;
    }

    public void setWorkingRadius(int radius) {
        int clamped = Math.max(1, Math.min(MAX_WORKING_RADIUS, radius));
        if (workingRadius != clamped) {
            workingRadius = clamped;
            plannedBlocksCount = Math.min(countBlocksInRadius(workingRadius), MAX_DISTRIBUTION_BLOCKS);
            ticksUntilRefresh = DISTRIBUTION_REFRESH_RATE;
            markDirty();
        }
    }

    public int getDistributedBlocksCount() {
        return isProvidingOxygen() ? distributedBlocksCount : 0;
    }

    public int distributedBlocksCount() {
        return getDistributedBlocksCount();
    }

    public int getDistributedBlocksLimit() {
        return MAX_DISTRIBUTION_BLOCKS;
    }

    public int distributedBlocksLimit() {
        return getDistributedBlocksLimit();
    }

    public int getEnergyPerTick() {
        return isProvidingOxygen() ? energyPerTick : 0;
    }

    public int energyPerTick() {
        return getEnergyPerTick();
    }

    public int getOxygenPerTick() {
        return isProvidingOxygen() ? oxygenPerTick : 0;
    }

    public int oxygenPerTick() {
        return getOxygenPerTick();
    }

    public int getStoredOxygenEquivalent() {
        FluidStack stored = fluidTank == null ? null : fluidTank.getFluid();
        if (!isValidInputFluid(stored)) {
            return 0;
        }
        if (stored.getFluid() == FluidRegistry.WATER) {
            return stored.amount * WATER_OXYGEN_OUTPUT_PER_OPERATION / WATER_INPUT_PER_OPERATION;
        }
        return stored.amount;
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }
        if (isValidBatterySlotItem(index, stack)) {
            return true;
        }
        if (index == INPUT_CONTAINER_SLOT) {
            return isValidInputFluid(FluidUtil.getFluidContained(stack));
        }
        return false;
    }

    @Override
    public int[] getSlotsForFace(EnumFacing side) {
        return new int[]{getBatterySlot(), INPUT_CONTAINER_SLOT, EMPTY_CONTAINER_SLOT};
    }

    @Override
    public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
        return (isBatterySlot(index) || index == INPUT_CONTAINER_SLOT) && isItemValidForSlot(index, itemStackIn);
    }

    @Override
    public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
        return index == EMPTY_CONTAINER_SLOT;
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            return true;
        }
        return super.hasCapability(capability, facing);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            return (T) fluidHandler;
        }
        return super.getCapability(capability, facing);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        providingOxygen = compound.getBoolean("ProvidingOxygen");
        workingRadius = compound.hasKey("WorkingRadius") ? clamp(compound.getInteger("WorkingRadius"), 1, MAX_WORKING_RADIUS) : DEFAULT_WORKING_RADIUS;
        plannedBlocksCount = compound.hasKey("PlannedBlocksCount") ? clamp(compound.getInteger("PlannedBlocksCount"), 0, MAX_DISTRIBUTION_BLOCKS) : countBlocksInRadius(workingRadius);
        distributedBlocksCount = compound.hasKey("DistributedBlocksCount") ? clamp(compound.getInteger("DistributedBlocksCount"), 0, MAX_DISTRIBUTION_BLOCKS) : 0;
        energyPerTick = Math.max(0, compound.getInteger("EnergyPerTick"));
        oxygenPerTick = Math.max(0, compound.getInteger("OxygenPerTick"));
        ticksUntilRefresh = Math.max(0, compound.getInteger("DistributionRefreshTicks"));
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setBoolean("ProvidingOxygen", providingOxygen);
        compound.setInteger("WorkingRadius", workingRadius);
        compound.setInteger("PlannedBlocksCount", plannedBlocksCount);
        compound.setInteger("DistributedBlocksCount", distributedBlocksCount);
        compound.setInteger("EnergyPerTick", energyPerTick);
        compound.setInteger("OxygenPerTick", oxygenPerTick);
        compound.setInteger("DistributionRefreshTicks", ticksUntilRefresh);
        return compound;
    }

    @Override
    public int getField(int id) {
        if (id == 4) {
            return isProvidingOxygen() ? 1 : 0;
        }
        if (id == 5) {
            return workingRadius;
        }
        if (id == 6) {
            return getDistributedBlocksCount();
        }
        if (id == 7) {
            return MAX_DISTRIBUTION_BLOCKS;
        }
        if (id == 8) {
            return getEnergyPerTick();
        }
        if (id == 9) {
            return getOxygenPerTick();
        }
        if (id == 10) {
            return getStoredOxygenEquivalent();
        }
        return super.getField(id);
    }

    @Override
    public void setField(int id, int value) {
        if (id == 5) {
            setWorkingRadius(value);
            return;
        }
        super.setField(id, value);
    }

    @Override
    public int getFieldCount() {
        return 11;
    }

    private static int calculateMaxRadius(int blockLimit) {
        int radius = 1;
        while (countBlocksInRadius(radius + 1) <= blockLimit) {
            radius++;
        }
        return radius;
    }

    private static int countBlocksInRadius(int radius) {
        int radiusSq = radius * radius;
        int count = 0;
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    if (x * x + y * y + z * z <= radiusSq) {
                        count++;
                    }
                }
            }
        }
        return count;
    }

    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    private final class DistributorFluidHandler implements IFluidHandler {

        @Override
        public IFluidTankProperties[] getTankProperties() {
            return new IFluidTankProperties[]{
                new FluidTankProperties(fluidTank.getFluid(), fluidTank.getCapacity(), true, false)
            };
        }

        @Override
        public int fill(FluidStack resource, boolean doFill) {
            if (!isValidInputFluid(resource)) {
                return 0;
            }
            return fluidTank.fill(resource, doFill);
        }

        @Override
        public FluidStack drain(FluidStack resource, boolean doDrain) {
            return null;
        }

        @Override
        public FluidStack drain(int maxDrain, boolean doDrain) {
            return null;
        }
    }
}
