package earth.terrarium.adastra.common.tile;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.energy.EnergyStorage;

public class AdAstraEnergyStorage extends EnergyStorage {

    public AdAstraEnergyStorage(int capacity, int maxReceive, int maxExtract) {
        super(capacity, maxReceive, maxExtract);
    }

    public void readFromNBT(NBTTagCompound compound) {
        this.energy = Math.max(0, Math.min(capacity, compound.getInteger("Energy")));
    }

    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound.setInteger("Energy", energy);
        return compound;
    }

    public void setEnergyStored(int energy) {
        this.energy = Math.max(0, Math.min(capacity, energy));
    }

    public int getMaxExtract() {
        return maxExtract;
    }

    public int getMaxReceive() {
        return maxReceive;
    }

    public int internalReceiveEnergy(int maxReceive, boolean simulate) {
        if (maxReceive <= 0) {
            return 0;
        }
        int energyReceived = Math.min(capacity - energy, maxReceive);
        if (!simulate) {
            energy += energyReceived;
        }
        return energyReceived;
    }
}
