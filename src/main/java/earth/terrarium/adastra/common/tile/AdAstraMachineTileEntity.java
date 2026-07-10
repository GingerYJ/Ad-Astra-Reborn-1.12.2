package earth.terrarium.adastra.common.tile;

import earth.terrarium.adastra.common.config.AdAstraConfig;
import earth.terrarium.adastra.common.container.AdAstraFluidTank;
import earth.terrarium.adastra.common.performance.PerformanceTracker;
import earth.terrarium.adastra.common.util.MachineStateUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.block.state.IBlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.SidedInvWrapper;

public class AdAstraMachineTileEntity extends AdAstraTileEntity implements ISidedInventory, ITickable {

    protected static final int IRON_ENERGY = AdAstraConfig.ironEnergyCapacity;
    protected static final int IRON_IO = AdAstraConfig.ironMaxEnergyInOut;
    protected static final int STEEL_ENERGY = AdAstraConfig.steelEnergyCapacity;
    protected static final int STEEL_IO = AdAstraConfig.steelMaxEnergyInOut;
    protected static final int STEEL_FLUID = AdAstraConfig.steelFluidCapacity;
    protected static final int DESH_ENERGY = AdAstraConfig.deshEnergyCapacity;
    protected static final int DESH_IO = AdAstraConfig.deshMaxEnergyInOut;
    protected static final int DESH_FLUID = AdAstraConfig.deshFluidCapacity;
    protected static final int OSTRUM_ENERGY = AdAstraConfig.ostrumEnergyCapacity;
    protected static final int OSTRUM_IO = AdAstraConfig.ostrumMaxEnergyInOut;
    protected static final int OSTRUM_FLUID = AdAstraConfig.ostrumFluidCapacity;

    private final String machineName;
    protected final ItemStackHandler items;
    protected final AdAstraEnergyStorage energy;
    protected final FluidTank fluidTank;
    protected final IItemHandler[] itemHandlers = new IItemHandler[EnumFacing.values().length];
    protected AdAstraRedstoneControl redstoneControl = AdAstraRedstoneControl.ALWAYS_ON;
    protected final AdAstraSideMode[][] sideModes = new AdAstraSideMode[EnumFacing.values().length][SideConfigType.values().length];
    protected final AdAstraSideMode[][] defaultSideModes = new AdAstraSideMode[EnumFacing.values().length][SideConfigType.values().length];

    // Performance optimization fields
    protected int idleTicks = 0;
    protected int tickCounter = 0;
    protected static final int IDLE_THRESHOLD = 60; // 3 seconds of no activity
    protected boolean wasActive = false;
    private boolean tickingMachine;

    // Cache for adjacent tile entities to reduce world lookups
    protected final TileEntity[] cachedNeighbors = new TileEntity[EnumFacing.values().length];
    protected final boolean[] neighborCached = new boolean[EnumFacing.values().length];
    protected int neighborCacheAge = 0;
    protected static final int NEIGHBOR_CACHE_LIFETIME = 20; // Re-check neighbors every second

    public AdAstraMachineTileEntity(String machineName, int slots, int energyCapacity, int maxReceive, int maxExtract, int fluidCapacity) {
        this.machineName = machineName;
        for (EnumFacing facing : EnumFacing.values()) {
            for (SideConfigType type : SideConfigType.values()) {
                sideModes[facing.getIndex()][type.ordinal()] = AdAstraSideMode.NONE;
                defaultSideModes[facing.getIndex()][type.ordinal()] = AdAstraSideMode.NONE;
            }
        }
        this.items = new ItemStackHandler(slots) {
            @Override
            protected void onContentsChanged(int slot) {
                markDirty();
            }

            @Override
            public boolean isItemValid(int slot, ItemStack stack) {
                return AdAstraMachineTileEntity.this.isItemValidForSlot(slot, stack);
            }
        };
        this.energy = energyCapacity > 0 ? new AdAstraEnergyStorage(energyCapacity, maxReceive, maxExtract) {
            @Override
            public int receiveEnergy(int amount, boolean simulate) {
                int received = super.receiveEnergy(amount, simulate);
                if (!simulate && received > 0) {
                    markDirty();
                }
                return received;
            }

            @Override
            public int extractEnergy(int amount, boolean simulate) {
                int extracted = super.extractEnergy(amount, simulate);
                if (!simulate && extracted > 0) {
                    markDirty();
                }
                return extracted;
            }
        } : null;
        this.fluidTank = fluidCapacity > 0 ? new AdAstraFluidTank(fluidCapacity) {
            @Override
            protected void onContentsChanged() {
                markDirty();
            }
        } : null;
        for (EnumFacing facing : EnumFacing.values()) {
            itemHandlers[facing.getIndex()] = new SidedInvWrapper(this, facing);
        }
    }

    public String getMachineName() {
        return machineName;
    }

    public AdAstraEnergyStorage getEnergyStorage() {
        return energy;
    }

    public FluidTank getFluidTank() {
        return fluidTank;
    }

    public AdAstraRedstoneControl getRedstoneControl() {
        return redstoneControl;
    }

    public void setRedstoneControl(AdAstraRedstoneControl redstoneControl) {
        this.redstoneControl = redstoneControl;
        markDirty();
    }

    public boolean canFunction() {
        return redstoneControl.canRun(world != null && world.isBlockPowered(pos));
    }

    @Override
    public void update() {
        if (world != null && !world.isRemote) {
            PerformanceTracker.startSystemTiming("machines");

            tickCounter++;
            if (neighborCacheAge < NEIGHBOR_CACHE_LIFETIME) {
                neighborCacheAge++;
            }
            if (isIdleOptimizationEnabled() && idleTicks >= IDLE_THRESHOLD && tickCounter % 20 != 0) {
                PerformanceTracker.endSystemTiming("machines");
                return;
            }

            wasActive = false;
            boolean canRun = false;
            tickingMachine = true;
            try {
                canRun = canFunction();
                if (!canRun) {
                    return;
                }

                pullEnergyFromBatterySlot();

                int transferInterval = Math.max(1, AdAstraConfig.machineTransferInterval);
                if (tickCounter % transferInterval == 0) {
                    pullFromSides();
                    pushToSides();
                }

                tickMachine();
            } finally {
                tickingMachine = false;
                if (wasActive || canRun && hasOngoingWork()) {
                    idleTicks = 0;
                } else {
                    idleTicks++;
                }
                PerformanceTracker.endSystemTiming("machines");
            }
        }
    }

    /**
     * Check if the machine should be considered idle.
     * Override this in subclasses for custom idle detection.
     */
    protected boolean isIdleOptimizationEnabled() {
        return AdAstraConfig.enableMachineIdleOptimization;
    }

    protected boolean hasOngoingWork() {
        return false;
    }

    /**
     * Mark machine as active (has done work this tick).
     * Resets idle counter.
     */
    protected void markActive() {
        idleTicks = 0;
        wasActive = true;
    }

    @Override
    public void markDirty() {
        super.markDirty();
        idleTicks = 0;
        if (tickingMachine) {
            wasActive = true;
        }
    }

    protected void tickMachine() {
    }

    protected void playMachineSound(SoundEvent sound, float volume, float pitch) {
        if (world != null && pos != null && !world.isRemote && sound != null) {
            world.playSound(null, pos, sound, SoundCategory.BLOCKS, volume, pitch);
        }
    }

    protected int getBatterySlot() {
        return 0;
    }

    protected boolean hasBatterySlot() {
        int slot = getBatterySlot();
        return slot >= 0 && slot < items.getSlots();
    }

    protected boolean canExtractEnergyFromBatterySlot() {
        return energy != null && energy.getMaxReceive() > 0 && hasBatterySlot();
    }

    protected boolean isBatterySlot(int index) {
        return hasBatterySlot() && index == getBatterySlot();
    }

    protected boolean isBatterySlotItem(ItemStack stack) {
        if (stack.isEmpty() || !stack.hasCapability(CapabilityEnergy.ENERGY, null)) {
            return false;
        }
        IEnergyStorage itemEnergy = stack.getCapability(CapabilityEnergy.ENERGY, null);
        return itemEnergy != null && itemEnergy.canExtract();
    }

    protected boolean isValidBatterySlotItem(int index, ItemStack stack) {
        return canExtractEnergyFromBatterySlot() && isBatterySlot(index) && isBatterySlotItem(stack);
    }

    protected void pullEnergyFromBatterySlot() {
        if (!canExtractEnergyFromBatterySlot()) {
            return;
        }

        ItemStack stack = items.getStackInSlot(getBatterySlot());
        if (!isBatterySlotItem(stack)) {
            return;
        }

        IEnergyStorage itemEnergy = stack.getCapability(CapabilityEnergy.ENERGY, null);
        if (itemEnergy == null) {
            return;
        }

        int extractable = itemEnergy.extractEnergy(energy.getMaxReceive(), true);
        int receivable = energy.receiveEnergy(extractable, true);
        if (receivable <= 0) {
            return;
        }

        int extracted = itemEnergy.extractEnergy(receivable, false);
        int received = energy.receiveEnergy(extracted, false);
        if (received > 0) {
            markDirty();
        }
    }

    protected void setLit(boolean lit) {
        if (world == null || pos == null) {
            return;
        }
        IBlockState state = world.getBlockState(pos);
        if (MachineStateUtils.hasLitProperty(state) && MachineStateUtils.isLit(state) != lit) {
            TileEntity tile = world.getTileEntity(pos);
            world.setBlockState(pos, MachineStateUtils.withLit(state, lit), 3);
            if (tile == this && world.getTileEntity(pos) != this) {
                validate();
                world.setTileEntity(pos, this);
            }
        }
    }

    public boolean isLit() {
        if (world == null || pos == null) {
            return false;
        }
        return MachineStateUtils.isLit(world.getBlockState(pos));
    }

    public AdAstraSideMode getSideMode(EnumFacing facing, SideConfigType type) {
        return sideModes[facing.getIndex()][type.ordinal()];
    }

    public void setSideMode(EnumFacing facing, SideConfigType type, AdAstraSideMode mode) {
        sideModes[facing.getIndex()][type.ordinal()] = mode;
        markDirty();
    }

    protected void setAllSideModes(SideConfigType type, AdAstraSideMode mode) {
        for (EnumFacing facing : EnumFacing.values()) {
            sideModes[facing.getIndex()][type.ordinal()] = mode;
            defaultSideModes[facing.getIndex()][type.ordinal()] = mode;
        }
    }

    public void resetSideModesToDefaults() {
        for (EnumFacing facing : EnumFacing.values()) {
            for (SideConfigType type : SideConfigType.values()) {
                sideModes[facing.getIndex()][type.ordinal()] = defaultSideModes[facing.getIndex()][type.ordinal()];
            }
        }
        markDirty();
    }

    protected void pushEnergyToSides() {
        if (energy == null || energy.getEnergyStored() <= 0 || energy.getMaxExtract() <= 0 || world == null || pos == null) {
            return;
        }

        for (EnumFacing facing : EnumFacing.values()) {
            if (!getSideMode(facing, SideConfigType.ENERGY).canPush()) {
                continue;
            }
            TileEntity target = getCachedNeighbor(facing);
            if (target == null || !target.hasCapability(CapabilityEnergy.ENERGY, facing.getOpposite())) {
                continue;
            }
            IEnergyStorage targetEnergy = target.getCapability(CapabilityEnergy.ENERGY, facing.getOpposite());
            if (targetEnergy == null || !targetEnergy.canReceive()) {
                continue;
            }
            int available = energy.extractEnergy(energy.getMaxExtract(), true);
            if (available <= 0) {
                return;
            }
            int accepted = targetEnergy.receiveEnergy(available, false);
            if (accepted > 0) {
                energy.extractEnergy(accepted, false);
                markDirty();
                target.markDirty();
                markActive();
            }
        }
    }

    /**
     * Get cached neighbor tile entity to reduce world lookups.
     * Cache is invalidated every NEIGHBOR_CACHE_LIFETIME ticks.
     */
    protected TileEntity getCachedNeighbor(EnumFacing facing) {
        if (neighborCacheAge >= NEIGHBOR_CACHE_LIFETIME) {
            for (int i = 0; i < cachedNeighbors.length; i++) {
                cachedNeighbors[i] = null;
                neighborCached[i] = false;
            }
            neighborCacheAge = 0;
        }

        int index = facing.getIndex();
        BlockPos neighborPos = pos.offset(facing);
        if (!world.isBlockLoaded(neighborPos)) {
            cachedNeighbors[index] = null;
            neighborCached[index] = false;
            return null;
        }
        TileEntity cached = cachedNeighbors[index];
        if (!neighborCached[index] || cached != null && cached.isInvalid()) {
            cachedNeighbors[index] = world.getTileEntity(neighborPos);
            neighborCached[index] = true;
        }

        return cachedNeighbors[index];
    }

    /**
     * Invalidate neighbor cache (call when blocks around this machine change).
     */
    public void invalidateNeighborCache() {
        for (int i = 0; i < cachedNeighbors.length; i++) {
            cachedNeighbors[i] = null;
            neighborCached[i] = false;
        }
        neighborCacheAge = 0;
        idleTicks = 0;
    }

    protected void pullFromSides() {
        pullItemsFromSides();
        pullEnergyFromSides();
        pullFluidFromSides();
    }

    protected void pushToSides() {
        pushItemsToSides();
        pushFluidToSides();
    }

    protected int getMaxItemTransferPerSide() {
        return 8;
    }

    protected int getMaxFluidTransferPerSide() {
        FluidTank tank = getFluidTank();
        return tank == null ? 0 : Math.min(250, tank.getCapacity());
    }

    protected void pullItemsFromSides() {
        if (world == null || pos == null || getMaxItemTransferPerSide() <= 0) {
            return;
        }

        for (EnumFacing facing : EnumFacing.values()) {
            if (!getSideMode(facing, SideConfigType.ITEM).canPull()) {
                continue;
            }
            TileEntity sourceTile = getCachedNeighbor(facing);
            if (sourceTile == null || !sourceTile.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, facing.getOpposite())) {
                continue;
            }
            IItemHandler source = sourceTile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, facing.getOpposite());
            if (source != null && pullOneItemStackFrom(source, facing)) {
                sourceTile.markDirty();
                markDirty();
                markActive();
            }
        }
    }

    private boolean pullOneItemStackFrom(IItemHandler source, EnumFacing side) {
        for (int sourceSlot = 0; sourceSlot < source.getSlots(); sourceSlot++) {
            ItemStack simulatedExtract = source.extractItem(sourceSlot, getMaxItemTransferPerSide(), true);
            if (simulatedExtract.isEmpty()) {
                continue;
            }
            ItemStack remainder = insertItemFromSide(simulatedExtract, side, true);
            int accepted = simulatedExtract.getCount() - remainder.getCount();
            if (accepted <= 0) {
                continue;
            }
            ItemStack extracted = source.extractItem(sourceSlot, accepted, false);
            if (!extracted.isEmpty()) {
                insertItemFromSide(extracted, side, false);
                return true;
            }
        }
        return false;
    }

    protected void pushItemsToSides() {
        if (world == null || pos == null || getMaxItemTransferPerSide() <= 0) {
            return;
        }

        for (EnumFacing facing : EnumFacing.values()) {
            if (!getSideMode(facing, SideConfigType.ITEM).canPush()) {
                continue;
            }
            TileEntity targetTile = getCachedNeighbor(facing);
            if (targetTile == null || !targetTile.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, facing.getOpposite())) {
                continue;
            }
            IItemHandler target = targetTile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, facing.getOpposite());
            if (target != null && pushOneItemStackTo(target, facing)) {
                targetTile.markDirty();
                markDirty();
                markActive();
            }
        }
    }

    private boolean pushOneItemStackTo(IItemHandler target, EnumFacing side) {
        int[] slots = getSlotsForFace(side);
        for (int slot : slots) {
            ItemStack stored = items.getStackInSlot(slot);
            if (stored.isEmpty() || !canExtractItem(slot, stored, side)) {
                continue;
            }
            ItemStack offer = stored.copy();
            offer.setCount(Math.min(offer.getCount(), getMaxItemTransferPerSide()));
            ItemStack remainder = insertIntoHandler(target, offer, true);
            int accepted = offer.getCount() - remainder.getCount();
            if (accepted <= 0) {
                continue;
            }
            ItemStack extracted = items.extractItem(slot, accepted, false);
            if (!extracted.isEmpty()) {
                insertIntoHandler(target, extracted, false);
                return true;
            }
        }
        return false;
    }

    private ItemStack insertItemFromSide(ItemStack stack, EnumFacing side, boolean simulate) {
        ItemStack remainder = stack.copy();
        int[] slots = getSlotsForFace(side);
        for (int slot : slots) {
            if (remainder.isEmpty()) {
                return ItemStack.EMPTY;
            }
            if (!canInsertItem(slot, remainder, side)) {
                continue;
            }
            remainder = items.insertItem(slot, remainder, simulate);
        }
        return remainder;
    }

    private ItemStack insertIntoHandler(IItemHandler target, ItemStack stack, boolean simulate) {
        ItemStack remainder = stack.copy();
        for (int slot = 0; slot < target.getSlots(); slot++) {
            if (remainder.isEmpty()) {
                return ItemStack.EMPTY;
            }
            remainder = target.insertItem(slot, remainder, simulate);
        }
        return remainder;
    }

    protected void pullEnergyFromSides() {
        if (energy == null || energy.getMaxReceive() <= 0 || world == null || pos == null) {
            return;
        }

        int room = energy.getMaxEnergyStored() - energy.getEnergyStored();
        if (room <= 0) {
            return;
        }

        for (EnumFacing facing : EnumFacing.values()) {
            if (!getSideMode(facing, SideConfigType.ENERGY).canPull()) {
                continue;
            }
            TileEntity sourceTile = getCachedNeighbor(facing);
            if (sourceTile == null || !sourceTile.hasCapability(CapabilityEnergy.ENERGY, facing.getOpposite())) {
                continue;
            }
            IEnergyStorage source = sourceTile.getCapability(CapabilityEnergy.ENERGY, facing.getOpposite());
            if (source == null || !source.canExtract()) {
                continue;
            }
            int requested = Math.min(energy.getMaxReceive(), energy.getMaxEnergyStored() - energy.getEnergyStored());
            if (requested <= 0) {
                return;
            }
            int extractable = source.extractEnergy(requested, true);
            int receivable = energy.receiveEnergy(extractable, true);
            if (receivable <= 0) {
                continue;
            }
            int extracted = source.extractEnergy(receivable, false);
            int received = energy.receiveEnergy(extracted, false);
            if (received > 0) {
                sourceTile.markDirty();
                markDirty();
                markActive();
            }
        }
    }

    protected void pullFluidFromSides() {
        IFluidHandler self = getSideFluidHandler();
        if (self == null || world == null || pos == null || getMaxFluidTransferPerSide() <= 0) {
            return;
        }

        for (EnumFacing facing : EnumFacing.values()) {
            if (!getSideMode(facing, SideConfigType.FLUID).canPull()) {
                continue;
            }
            TileEntity sourceTile = getCachedNeighbor(facing);
            if (sourceTile == null || !sourceTile.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, facing.getOpposite())) {
                continue;
            }
            IFluidHandler source = sourceTile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, facing.getOpposite());
            if (source == null) {
                continue;
            }
            FluidStack simulated = source.drain(getMaxFluidTransferPerSide(), false);
            if (simulated == null || simulated.amount <= 0) {
                continue;
            }
            int fillable = self.fill(simulated, false);
            if (fillable <= 0) {
                continue;
            }
            FluidStack drained = source.drain(fillable, true);
            if (drained != null && drained.amount > 0 && self.fill(drained, true) > 0) {
                sourceTile.markDirty();
                markDirty();
                markActive();
            }
        }
    }

    protected void pushFluidToSides() {
        IFluidHandler self = getSideFluidHandler();
        if (self == null || world == null || pos == null || getMaxFluidTransferPerSide() <= 0) {
            return;
        }

        for (EnumFacing facing : EnumFacing.values()) {
            if (!getSideMode(facing, SideConfigType.FLUID).canPush()) {
                continue;
            }
            TileEntity targetTile = getCachedNeighbor(facing);
            if (targetTile == null || !targetTile.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, facing.getOpposite())) {
                continue;
            }
            IFluidHandler target = targetTile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, facing.getOpposite());
            if (target == null) {
                continue;
            }
            FluidStack simulated = self.drain(getMaxFluidTransferPerSide(), false);
            if (simulated == null || simulated.amount <= 0) {
                continue;
            }
            int accepted = target.fill(simulated, false);
            if (accepted <= 0) {
                continue;
            }
            FluidStack drained = self.drain(accepted, true);
            if (drained != null && drained.amount > 0 && target.fill(drained, true) > 0) {
                targetTile.markDirty();
                markDirty();
                markActive();
            }
        }
    }

    protected IFluidHandler getSideFluidHandler() {
        if (getFluidTank() == null) {
            return null;
        }
        return getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        if (compound.hasKey("Items")) {
            items.deserializeNBT(compound.getCompoundTag("Items"));
        }
        if (energy != null && compound.hasKey("Energy")) {
            energy.readFromNBT(compound.getCompoundTag("Energy"));
        }
        if (fluidTank != null && compound.hasKey("FluidTank")) {
            fluidTank.readFromNBT(compound.getCompoundTag("FluidTank"));
        }
        redstoneControl = AdAstraRedstoneControl.byOrdinal(compound.getByte("RedstoneControl"));
        if (compound.hasKey("SideModes")) {
            NBTTagCompound sideCompound = compound.getCompoundTag("SideModes");
            for (EnumFacing facing : EnumFacing.values()) {
                NBTTagCompound facingCompound = sideCompound.getCompoundTag(facing.getName());
                for (SideConfigType type : SideConfigType.values()) {
                    sideModes[facing.getIndex()][type.ordinal()] = AdAstraSideMode.byOrdinal(facingCompound.getByte(type.getKey()));
                }
            }
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setTag("Items", items.serializeNBT());
        if (energy != null) {
            compound.setTag("Energy", energy.writeToNBT(new NBTTagCompound()));
        }
        if (fluidTank != null) {
            compound.setTag("FluidTank", fluidTank.writeToNBT(new NBTTagCompound()));
        }
        compound.setByte("RedstoneControl", (byte) redstoneControl.ordinal());
        NBTTagCompound sideCompound = new NBTTagCompound();
        for (EnumFacing facing : EnumFacing.values()) {
            NBTTagCompound facingCompound = new NBTTagCompound();
            for (SideConfigType type : SideConfigType.values()) {
                facingCompound.setByte(type.getKey(), (byte) sideModes[facing.getIndex()][type.ordinal()].ordinal());
            }
            sideCompound.setTag(facing.getName(), facingCompound);
        }
        compound.setTag("SideModes", sideCompound);
        return compound;
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return true;
        }
        if (energy != null && capability == CapabilityEnergy.ENERGY) {
            return true;
        }
        if (fluidTank != null && capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            return true;
        }
        return super.hasCapability(capability, facing);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return (T) (facing == null ? items : itemHandlers[facing.getIndex()]);
        }
        if (energy != null && capability == CapabilityEnergy.ENERGY) {
            return (T) energy;
        }
        if (fluidTank != null && capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            return (T) fluidTank;
        }
        return super.getCapability(capability, facing);
    }

    @Override
    public int getSizeInventory() {
        return items.getSlots();
    }

    @Override
    public boolean isEmpty() {
        for (int i = 0; i < items.getSlots(); i++) {
            if (!items.getStackInSlot(i).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack getStackInSlot(int index) {
        return items.getStackInSlot(index);
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        return items.extractItem(index, count, false);
    }

    @Override
    public ItemStack removeStackFromSlot(int index) {
        ItemStack stack = items.getStackInSlot(index);
        items.setStackInSlot(index, ItemStack.EMPTY);
        return stack;
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        items.setStackInSlot(index, stack);
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public boolean isUsableByPlayer(EntityPlayer player) {
        return world != null && world.getTileEntity(pos) == this && player.getDistanceSq(pos) <= 64.0d;
    }

    @Override
    public void openInventory(EntityPlayer player) {
    }

    @Override
    public void closeInventory(EntityPlayer player) {
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        if (isBatterySlot(index) && canExtractEnergyFromBatterySlot()) {
            return isBatterySlotItem(stack);
        }
        return true;
    }

    @Override
    public int getField(int id) {
        if (id == 0 && energy != null) {
            return energy.getEnergyStored();
        }
        if (id == 1 && energy != null) {
            return energy.getMaxEnergyStored();
        }
        if (id == 2 && fluidTank != null) {
            return fluidTank.getFluidAmount();
        }
        if (id == 3 && fluidTank != null) {
            return fluidTank.getCapacity();
        }
        return 0;
    }

    @Override
    public void setField(int id, int value) {
        if (id == 0 && energy != null) {
            energy.setEnergyStored(value);
        }
    }

    @Override
    public int getFieldCount() {
        return 4;
    }

    @Override
    public void clear() {
        for (int i = 0; i < items.getSlots(); i++) {
            items.setStackInSlot(i, ItemStack.EMPTY);
        }
    }

    @Override
    public String getName() {
        return "tile.ad_astra." + machineName + ".name";
    }

    @Override
    public ITextComponent getDisplayName() {
        return new TextComponentTranslation(getName());
    }

    @Override
    public boolean hasCustomName() {
        return false;
    }

    @Override
    public int[] getSlotsForFace(EnumFacing side) {
        int[] slots = new int[items.getSlots()];
        for (int i = 0; i < slots.length; i++) {
            slots[i] = i;
        }
        return slots;
    }

    @Override
    public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
        return isItemValidForSlot(index, itemStackIn);
    }

    @Override
    public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
        return true;
    }

    public enum SideConfigType {
        ITEM("Item"),
        ENERGY("Energy"),
        FLUID("Fluid");

        private final String key;

        SideConfigType(String key) {
            this.key = key;
        }

        public String getKey() {
            return key;
        }

        public static SideConfigType byOrdinal(int ordinal) {
            SideConfigType[] values = values();
            return ordinal >= 0 && ordinal < values.length ? values[ordinal] : ENERGY;
        }
    }
}
