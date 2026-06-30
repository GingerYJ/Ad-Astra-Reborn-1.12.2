package earth.terrarium.adastra.common.tile;

import earth.terrarium.adastra.common.blocks.AdAstraMachineBlock;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.block.state.IBlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public class AdAstraMachineTileEntity extends AdAstraTileEntity implements ISidedInventory, ITickable {

    protected static final int IRON_ENERGY = 10_000;
    protected static final int IRON_IO = 100;
    protected static final int STEEL_ENERGY = 20_000;
    protected static final int STEEL_IO = 150;
    protected static final int STEEL_FLUID = 3_000;
    protected static final int DESH_ENERGY = 50_000;
    protected static final int DESH_IO = 250;
    protected static final int DESH_FLUID = 5_000;
    protected static final int OSTRUM_ENERGY = 100_000;
    protected static final int OSTRUM_IO = 500;
    protected static final int OSTRUM_FLUID = 10_000;

    private final String machineName;
    protected final ItemStackHandler items;
    protected final AdAstraEnergyStorage energy;
    protected final FluidTank fluidTank;
    protected AdAstraRedstoneControl redstoneControl = AdAstraRedstoneControl.ALWAYS_ON;
    protected final AdAstraSideMode[][] sideModes = new AdAstraSideMode[EnumFacing.values().length][SideConfigType.values().length];

    public AdAstraMachineTileEntity(String machineName, int slots, int energyCapacity, int maxReceive, int maxExtract, int fluidCapacity) {
        this.machineName = machineName;
        for (EnumFacing facing : EnumFacing.values()) {
            for (SideConfigType type : SideConfigType.values()) {
                sideModes[facing.getIndex()][type.ordinal()] = AdAstraSideMode.NONE;
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
        this.energy = energyCapacity > 0 ? new AdAstraEnergyStorage(energyCapacity, maxReceive, maxExtract) : null;
        this.fluidTank = fluidCapacity > 0 ? new FluidTank(fluidCapacity) {
            @Override
            protected void onContentsChanged() {
                markDirty();
            }
        } : null;
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
            pullEnergyFromBatterySlot();
            tickMachine();
        }
    }

    protected void tickMachine() {
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
        if (state.getBlock() instanceof AdAstraMachineBlock && state.getValue(AdAstraMachineBlock.LIT) != lit) {
            world.setBlockState(pos, state.withProperty(AdAstraMachineBlock.LIT, lit), 3);
        }
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
        }
    }

    protected void pushEnergyToSides() {
        if (energy == null || energy.getEnergyStored() <= 0 || energy.getMaxExtract() <= 0 || world == null || pos == null) {
            return;
        }

        for (EnumFacing facing : EnumFacing.values()) {
            if (!getSideMode(facing, SideConfigType.ENERGY).canPush()) {
                continue;
            }
            TileEntity target = world.getTileEntity(pos.offset(facing));
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
            }
        }
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
            return (T) items;
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
    }
}
