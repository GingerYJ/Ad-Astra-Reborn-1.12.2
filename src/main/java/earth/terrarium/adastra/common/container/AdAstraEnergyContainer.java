package earth.terrarium.adastra.common.container;

import earth.terrarium.adastra.common.tile.AdAstraEnergyStorage;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.energy.IEnergyStorage;

/**
 * Enhanced energy container with additional features for Ad Astra machines.
 * Provides compatibility layer between 1.20's Botarium EnergyContainer and 1.12.2's Forge Energy.
 *
 * Features:
 * - Change listener support
 * - Side configuration awareness
 * - Auto-extraction/insertion control
 * - Enhanced serialization
 */
public class AdAstraEnergyContainer extends AdAstraEnergyStorage {

    private Runnable changeListener;
    private boolean canAutoExtract = true;
    private boolean canAutoReceive = true;

    public AdAstraEnergyContainer(int capacity) {
        this(capacity, capacity, capacity);
    }

    public AdAstraEnergyContainer(int capacity, int maxTransfer) {
        this(capacity, maxTransfer, maxTransfer);
    }

    public AdAstraEnergyContainer(int capacity, int maxReceive, int maxExtract) {
        super(capacity, maxReceive, maxExtract);
    }

    /**
     * Set a callback to be invoked when energy changes.
     */
    public void setChangeListener(Runnable listener) {
        this.changeListener = listener;
    }

    /**
     * Called when energy content changes.
     */
    protected void onEnergyChanged() {
        if (changeListener != null) {
            changeListener.run();
        }
    }

    /**
     * Set whether this container can automatically extract energy to adjacent blocks.
     */
    public void setCanAutoExtract(boolean canAutoExtract) {
        this.canAutoExtract = canAutoExtract;
    }

    /**
     * Set whether this container can automatically receive energy from adjacent blocks.
     */
    public void setCanAutoReceive(boolean canAutoReceive) {
        this.canAutoReceive = canAutoReceive;
    }

    /**
     * Check if this container can automatically extract energy.
     */
    public boolean canAutoExtract() {
        return canAutoExtract && maxExtract > 0;
    }

    /**
     * Check if this container can automatically receive energy.
     */
    public boolean canAutoReceive() {
        return canAutoReceive && maxReceive > 0;
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        int received = super.receiveEnergy(maxReceive, simulate);
        if (received > 0 && !simulate) {
            onEnergyChanged();
        }
        return received;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        int extracted = super.extractEnergy(maxExtract, simulate);
        if (extracted > 0 && !simulate) {
            onEnergyChanged();
        }
        return extracted;
    }

    @Override
    public void setEnergyStored(int energy) {
        int oldEnergy = this.energy;
        super.setEnergyStored(energy);
        if (oldEnergy != this.energy) {
            onEnergyChanged();
        }
    }

    /**
     * Get remaining capacity.
     */
    public int getSpace() {
        return capacity - energy;
    }

    /**
     * Check if the container is empty.
     */
    public boolean isEmpty() {
        return energy == 0;
    }

    /**
     * Check if the container is full.
     */
    public boolean isFull() {
        return energy >= capacity;
    }

    /**
     * Get the fill percentage (0.0 to 1.0).
     */
    public float getFillPercentage() {
        if (capacity == 0) return 0f;
        return (float) energy / capacity;
    }

    /**
     * Consume energy (no rate limit).
     */
    public boolean consumeEnergy(int amount) {
        if (energy >= amount) {
            setEnergyStored(energy - amount);
            return true;
        }
        return false;
    }

    /**
     * Add energy (no rate limit).
     */
    public void addEnergy(int amount) {
        setEnergyStored(Math.min(capacity, energy + amount));
    }

    /**
     * Transfer energy from this container to another.
     * @return Amount actually transferred
     */
    public int transferTo(IEnergyStorage target, int maxAmount) {
        if (target == null || !target.canReceive()) {
            return 0;
        }
        int extractable = extractEnergy(maxAmount, true);
        if (extractable <= 0) {
            return 0;
        }
        int accepted = target.receiveEnergy(extractable, false);
        if (accepted > 0) {
            extractEnergy(accepted, false);
        }
        return accepted;
    }

    /**
     * Transfer energy from another container to this.
     * @return Amount actually transferred
     */
    public int transferFrom(IEnergyStorage source, int maxAmount) {
        if (source == null || !source.canExtract()) {
            return 0;
        }
        int receivable = receiveEnergy(maxAmount, true);
        if (receivable <= 0) {
            return 0;
        }
        int extracted = source.extractEnergy(receivable, false);
        if (extracted > 0) {
            receiveEnergy(extracted, false);
        }
        return extracted;
    }

    /**
     * Create a copy of this container.
     */
    public AdAstraEnergyContainer copy() {
        AdAstraEnergyContainer copy = new AdAstraEnergyContainer(capacity, maxReceive, maxExtract);
        copy.setEnergyStored(energy);
        copy.setCanAutoExtract(canAutoExtract);
        copy.setCanAutoReceive(canAutoReceive);
        return copy;
    }

    /**
     * Serialize to NBT.
     */
    public NBTTagCompound serializeNBT() {
        NBTTagCompound nbt = new NBTTagCompound();
        writeToNBT(nbt);
        nbt.setBoolean("CanAutoExtract", canAutoExtract);
        nbt.setBoolean("CanAutoReceive", canAutoReceive);
        return nbt;
    }

    /**
     * Deserialize from NBT.
     */
    public void deserializeNBT(NBTTagCompound nbt) {
        readFromNBT(nbt);
        if (nbt.hasKey("CanAutoExtract")) {
            canAutoExtract = nbt.getBoolean("CanAutoExtract");
        }
        if (nbt.hasKey("CanAutoReceive")) {
            canAutoReceive = nbt.getBoolean("CanAutoReceive");
        }
    }

    /**
     * Clear energy.
     */
    public void clear() {
        setEnergyStored(0);
    }
}
