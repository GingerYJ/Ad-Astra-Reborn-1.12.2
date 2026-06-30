package earth.terrarium.adastra.common.items;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.energy.IEnergyStorage;

public class ItemStackEnergyStorage implements IEnergyStorage {

    private static final String ENERGY_TAG = "Energy";

    private final ItemStack stack;
    private final int capacity;
    private final int maxReceive;
    private final int maxExtract;

    public ItemStackEnergyStorage(ItemStack stack, int capacity, int maxReceive, int maxExtract) {
        this.stack = stack;
        this.capacity = capacity;
        this.maxReceive = maxReceive;
        this.maxExtract = maxExtract;
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        if (!canReceive()) {
            return 0;
        }
        int received = Math.min(capacity - getEnergyStored(), Math.min(this.maxReceive, maxReceive));
        if (!simulate && received > 0) {
            setEnergyStored(getEnergyStored() + received);
        }
        return received;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        if (!canExtract()) {
            return 0;
        }
        int extracted = Math.min(getEnergyStored(), Math.min(this.maxExtract, maxExtract));
        if (!simulate && extracted > 0) {
            setEnergyStored(getEnergyStored() - extracted);
        }
        return extracted;
    }

    @Override
    public int getEnergyStored() {
        NBTTagCompound tag = stack.getTagCompound();
        return tag == null ? 0 : Math.max(0, Math.min(capacity, tag.getInteger(ENERGY_TAG)));
    }

    @Override
    public int getMaxEnergyStored() {
        return capacity;
    }

    @Override
    public boolean canExtract() {
        return maxExtract > 0;
    }

    @Override
    public boolean canReceive() {
        return maxReceive > 0;
    }

    private void setEnergyStored(int energy) {
        NBTTagCompound tag = stack.getTagCompound();
        if (tag == null) {
            tag = new NBTTagCompound();
            stack.setTagCompound(tag);
        }
        tag.setInteger(ENERGY_TAG, Math.max(0, Math.min(capacity, energy)));
    }
}
