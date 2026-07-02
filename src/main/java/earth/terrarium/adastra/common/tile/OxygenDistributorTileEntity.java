package earth.terrarium.adastra.common.tile;

import earth.terrarium.adastra.common.config.AdAstraConfig;
import earth.terrarium.adastra.common.entities.misc.AirVortexEntity;
import earth.terrarium.adastra.common.registry.ModFluids;
import earth.terrarium.adastra.common.registry.ModSounds;
import earth.terrarium.adastra.common.systems.OxygenSystem;
import earth.terrarium.adastra.common.systems.OxygenSystemExtended;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class OxygenDistributorTileEntity extends AdAstraMachineTileEntity {

    private static final int INPUT_CONTAINER_SLOT = 1;
    private static final int EMPTY_CONTAINER_SLOT = 2;
    private static final int MAX_DISTRIBUTION_BLOCKS = 6000;
    private static final int DISTRIBUTION_REFRESH_RATE = 100;
    private static final int WATER_INPUT_PER_OPERATION = 100;
    private static final int WATER_OXYGEN_OUTPUT_PER_OPERATION = 4;
    private static final int MAX_WORKING_RADIUS = calculateMaxRadius(MAX_DISTRIBUTION_BLOCKS);
    private static final int DEFAULT_WORKING_RADIUS = MAX_WORKING_RADIUS;
    private static final int AIR_VORTEX_SPAWN_INTERVAL = 200;
    private static final int SOUND_INTERVAL = 100;

    private final IFluidHandler fluidHandler = new DistributorFluidHandler();

    private boolean providingOxygen;
    private int workingRadius = DEFAULT_WORKING_RADIUS;
    private int plannedBlocksCount = countBlocksInRadius(DEFAULT_WORKING_RADIUS);
    private int distributedBlocksCount;
    private int energyPerTick;
    private int oxygenPerTick;
    private int ticksUntilRefresh;
    private int airVortexCooldown;
    private int soundCooldown;

    // Sealed room tracking for integration with OxygenSystemExtended
    private final Set<BlockPos> lastDistributedBlocks = new HashSet<>();
    private boolean usesSealedRoomDetection = true;
    private int shutDownTicks;

    public OxygenDistributorTileEntity() {
        super("oxygen_distributor", 3, DESH_ENERGY, DESH_IO, 0, DESH_FLUID);
        setAllSideModes(SideConfigType.ENERGY, AdAstraSideMode.PULL);
        setAllSideModes(SideConfigType.FLUID, AdAstraSideMode.PULL);
    }

    @Override
    protected void tickMachine() {
        if (airVortexCooldown > 0) {
            airVortexCooldown--;
        }
        if (soundCooldown > 0) {
            soundCooldown--;
        }
        if (shutDownTicks > 0) {
            shutDownTicks--;
            return;
        }

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
        playWorkSound();
        maybeSpawnAirVortex();
        markDirty();
    }

    private void playWorkSound() {
        if (soundCooldown <= 0) {
            playMachineSound(ModSounds.OXYGEN_INTAKE, 0.45f, 0.9f + world.rand.nextFloat() * 0.2f);
            soundCooldown = SOUND_INTERVAL;
        }
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

        // Try sealed room detection if enabled
        if (usesSealedRoomDetection && world != null && pos != null) {
            Set<BlockPos> sealedRoom = OxygenSystem.findSealedRoom(world, pos.up(), MAX_DISTRIBUTION_BLOCKS);

            if (sealedRoom != null && !sealedRoom.isEmpty()) {
                // Found a sealed room - use exact block count
                plannedBlocksCount = sealedRoom.size();

                // Update oxygen positions via OxygenSystemExtended
                updateOxygenPositions(sealedRoom);

                ticksUntilRefresh = DISTRIBUTION_REFRESH_RATE;
                return;
            }
        }

        // Fallback to spherical distribution
        plannedBlocksCount = Math.min(countBlocksInRadius(workingRadius), MAX_DISTRIBUTION_BLOCKS);

        // Update oxygen positions for spherical area
        if (isProvidingOxygen() && world != null && pos != null) {
            updateOxygenPositionsSpherical();
        }

        ticksUntilRefresh = DISTRIBUTION_REFRESH_RATE;
    }

    /**
     * Update oxygen positions in OxygenSystemExtended for sealed room mode.
     * Adds oxygen to new blocks and removes from old blocks.
     */
    private void updateOxygenPositions(Set<BlockPos> newPositions) {
        if (world == null || world.isRemote) {
            return;
        }

        // Remove oxygen from blocks that are no longer covered
        Set<BlockPos> toRemove = new HashSet<>(lastDistributedBlocks);
        toRemove.removeAll(newPositions);
        if (!toRemove.isEmpty()) {
            OxygenSystemExtended.removeOxygen(world, toRemove);
        }

        // Add oxygen to new blocks
        Set<BlockPos> toAdd = new HashSet<>(newPositions);
        toAdd.removeAll(lastDistributedBlocks);
        if (!toAdd.isEmpty()) {
            OxygenSystemExtended.setOxygen(world, toAdd, true);
        }

        // Update tracked positions
        lastDistributedBlocks.clear();
        lastDistributedBlocks.addAll(newPositions);
    }

    /**
     * Update oxygen positions for spherical distribution mode.
     */
    private void updateOxygenPositionsSpherical() {
        if (world == null || world.isRemote || pos == null) {
            return;
        }

        Set<BlockPos> sphericalPositions = new HashSet<>();
        int radiusSq = workingRadius * workingRadius;

        // Generate all positions within the sphere
        for (int x = -workingRadius; x <= workingRadius; x++) {
            for (int y = -workingRadius; y <= workingRadius; y++) {
                for (int z = -workingRadius; z <= workingRadius; z++) {
                    if (x * x + y * y + z * z <= radiusSq) {
                        BlockPos blockPos = pos.add(x, y, z);
                        sphericalPositions.add(blockPos);

                        if (sphericalPositions.size() >= MAX_DISTRIBUTION_BLOCKS) {
                            break;
                        }
                    }
                }
                if (sphericalPositions.size() >= MAX_DISTRIBUTION_BLOCKS) {
                    break;
                }
            }
            if (sphericalPositions.size() >= MAX_DISTRIBUTION_BLOCKS) {
                break;
            }
        }

        updateOxygenPositions(sphericalPositions);
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

            // Clear oxygen from all distributed blocks
            clearOxygenBlocks();

            // Add shutdown delay to prevent rapid on/off cycles
            shutDownTicks = 60;

            markDirty();
        }
        setLit(false);
    }

    /**
     * Clear oxygen from all tracked positions and reset the tracking set.
     */
    private void clearOxygenBlocks() {
        if (world != null && !world.isRemote && !lastDistributedBlocks.isEmpty()) {
            OxygenSystemExtended.removeOxygen(world, lastDistributedBlocks);
            lastDistributedBlocks.clear();
        }
    }

    /**
     * Called when the tile entity is removed or invalidated.
     * Ensures oxygen is properly cleaned up.
     */
    @Override
    public void invalidate() {
        super.invalidate();
        clearOxygenBlocks();
    }

    /**
     * Called when the chunk is unloaded.
     * Ensures oxygen is properly cleaned up.
     */
    @Override
    public void onChunkUnload() {
        super.onChunkUnload();
        clearOxygenBlocks();
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

    /**
     * Check if this distributor is providing oxygen at all.
     * @return true if actively distributing oxygen
     */
    public boolean isProvidingOxygen() {
        return providingOxygen && canFunction();
    }

    /**
     * Check if this distributor is providing oxygen at the given position.
     * Checks both spherical radius and sealed room membership.
     *
     * @param target Position to check
     * @return true if oxygen is available at this position from this distributor
     */
    public boolean isProvidingOxygen(BlockPos target) {
        if (!isProvidingOxygen() || pos == null || target == null) {
            return false;
        }

        // If using sealed room detection and we have tracked positions, check exact membership
        if (usesSealedRoomDetection && !lastDistributedBlocks.isEmpty()) {
            return lastDistributedBlocks.contains(target);
        }

        // Fallback to spherical distance check
        long dx = target.getX() - pos.getX();
        long dy = target.getY() - pos.getY();
        long dz = target.getZ() - pos.getZ();
        long radius = workingRadius;
        return dx * dx + dy * dy + dz * dz <= radius * radius;
    }

    /**
     * Get all positions currently being provided with oxygen.
     * @return Set of block positions, or empty set if not providing oxygen
     */
    public Set<BlockPos> getDistributedPositions() {
        if (!isProvidingOxygen()) {
            return new HashSet<>();
        }
        return new HashSet<>(lastDistributedBlocks);
    }

    /**
     * Toggle between sealed room detection and spherical distribution modes.
     * @param useSealedRoom true to use sealed room detection, false for spherical
     */
    public void setUsesSealedRoomDetection(boolean useSealedRoom) {
        if (this.usesSealedRoomDetection != useSealedRoom) {
            this.usesSealedRoomDetection = useSealedRoom;
            // Force immediate recalculation
            ticksUntilRefresh = 0;
            markDirty();
        }
    }

    /**
     * Check if using sealed room detection mode.
     * @return true if sealed room detection is enabled
     */
    public boolean usesSealedRoomDetection() {
        return usesSealedRoomDetection;
    }

    private void maybeSpawnAirVortex() {
        if (!AdAstraConfig.enableAirVortexes || world == null || pos == null || airVortexCooldown > 0) {
            return;
        }
        if (distributedBlocksCount < MAX_DISTRIBUTION_BLOCKS || hasSourceAirVortex()) {
            return;
        }

        BlockPos spawnPos = pos.up(Math.max(2, workingRadius / 2));
        AirVortexEntity vortex = new AirVortexEntity(world, pos, workingRadius);
        vortex.setPosition(spawnPos.getX() + 0.5d, spawnPos.getY() + 0.5d, spawnPos.getZ() + 0.5d);
        world.spawnEntity(vortex);
        airVortexCooldown = AIR_VORTEX_SPAWN_INTERVAL;
    }

    private boolean hasSourceAirVortex() {
        AxisAlignedBB bounds = new AxisAlignedBB(pos).grow(workingRadius + 2);
        List<AirVortexEntity> vortexes = world.getEntitiesWithinAABB(AirVortexEntity.class, bounds);
        for (AirVortexEntity vortex : vortexes) {
            if (!vortex.isDead && pos.equals(vortex.getSourcePos())) {
                return true;
            }
        }
        return false;
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
        airVortexCooldown = Math.max(0, compound.getInteger("AirVortexCooldown"));
        usesSealedRoomDetection = compound.hasKey("UsesSealedRoomDetection") ? compound.getBoolean("UsesSealedRoomDetection") : true;
        shutDownTicks = Math.max(0, compound.getInteger("ShutDownTicks"));

        // Load distributed block positions (1.12.2 compatible - use int array instead of long array)
        lastDistributedBlocks.clear();
        if (compound.hasKey("LastDistributedBlocksCount")) {
            int count = compound.getInteger("LastDistributedBlocksCount");
            int[] xArray = compound.getIntArray("LastDistributedBlocksX");
            int[] yArray = compound.getIntArray("LastDistributedBlocksY");
            int[] zArray = compound.getIntArray("LastDistributedBlocksZ");

            if (xArray.length == count && yArray.length == count && zArray.length == count) {
                for (int i = 0; i < count; i++) {
                    lastDistributedBlocks.add(new BlockPos(xArray[i], yArray[i], zArray[i]));
                }
            }
        }
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
        compound.setInteger("AirVortexCooldown", airVortexCooldown);
        compound.setBoolean("UsesSealedRoomDetection", usesSealedRoomDetection);
        compound.setInteger("ShutDownTicks", shutDownTicks);

        // Save distributed block positions (1.12.2 compatible - use int arrays instead of long array)
        if (!lastDistributedBlocks.isEmpty()) {
            compound.setInteger("LastDistributedBlocksCount", lastDistributedBlocks.size());
            int[] xArray = new int[lastDistributedBlocks.size()];
            int[] yArray = new int[lastDistributedBlocks.size()];
            int[] zArray = new int[lastDistributedBlocks.size()];

            int i = 0;
            for (BlockPos blockPos : lastDistributedBlocks) {
                xArray[i] = blockPos.getX();
                yArray[i] = blockPos.getY();
                zArray[i] = blockPos.getZ();
                i++;
            }

            compound.setIntArray("LastDistributedBlocksX", xArray);
            compound.setIntArray("LastDistributedBlocksY", yArray);
            compound.setIntArray("LastDistributedBlocksZ", zArray);
        }

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
        if (id == 11) {
            return usesSealedRoomDetection ? 1 : 0;
        }
        return super.getField(id);
    }

    @Override
    public void setField(int id, int value) {
        if (id == 5) {
            setWorkingRadius(value);
            return;
        }
        if (id == 11) {
            setUsesSealedRoomDetection(value != 0);
            return;
        }
        super.setField(id, value);
    }

    @Override
    public int getFieldCount() {
        return 12;
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
