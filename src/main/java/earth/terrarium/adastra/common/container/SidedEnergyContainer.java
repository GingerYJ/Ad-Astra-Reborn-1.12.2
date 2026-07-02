package earth.terrarium.adastra.common.container;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.energy.IEnergyStorage;

import javax.annotation.Nullable;

/**
 * Side-aware energy container for machines that need different I/O behavior per side.
 * Wraps an AdAstraEnergyContainer and provides side-based access control.
 */
public class SidedEnergyContainer implements IEnergyStorage {

    private final AdAstraEnergyContainer container;
    private final SideMode[] sideModes = new SideMode[6]; // One per EnumFacing

    public SidedEnergyContainer(AdAstraEnergyContainer container) {
        this.container = container;
        // Default: all sides are disabled
        for (int i = 0; i < 6; i++) {
            sideModes[i] = SideMode.NONE;
        }
    }

    /**
     * Set the mode for a specific side.
     */
    public void setSideMode(EnumFacing side, SideMode mode) {
        if (side != null) {
            sideModes[side.getIndex()] = mode;
        }
    }

    /**
     * Get the mode for a specific side.
     */
    public SideMode getSideMode(@Nullable EnumFacing side) {
        if (side == null) {
            return SideMode.BOTH; // Internal access
        }
        return sideModes[side.getIndex()];
    }

    /**
     * Get the underlying energy container (for internal use).
     */
    public AdAstraEnergyContainer getContainer() {
        return container;
    }

    /**
     * Get an IEnergyStorage view for a specific side.
     */
    public IEnergyStorage forSide(@Nullable EnumFacing side) {
        return new SidedView(side);
    }

    // IEnergyStorage implementation (for internal/null side access)

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        return container.receiveEnergy(maxReceive, simulate);
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        return container.extractEnergy(maxExtract, simulate);
    }

    @Override
    public int getEnergyStored() {
        return container.getEnergyStored();
    }

    @Override
    public int getMaxEnergyStored() {
        return container.getMaxEnergyStored();
    }

    @Override
    public boolean canExtract() {
        return container.canExtract();
    }

    @Override
    public boolean canReceive() {
        return container.canReceive();
    }

    // Serialization

    public NBTTagCompound serializeNBT() {
        NBTTagCompound nbt = container.serializeNBT();
        int[] modes = new int[6];
        for (int i = 0; i < 6; i++) {
            modes[i] = sideModes[i].ordinal();
        }
        nbt.setIntArray("SideModes", modes);
        return nbt;
    }

    public void deserializeNBT(NBTTagCompound nbt) {
        container.deserializeNBT(nbt);
        if (nbt.hasKey("SideModes")) {
            int[] modes = nbt.getIntArray("SideModes");
            for (int i = 0; i < Math.min(6, modes.length); i++) {
                sideModes[i] = SideMode.values()[modes[i]];
            }
        }
    }

    /**
     * Side-specific view of the energy container.
     */
    private class SidedView implements IEnergyStorage {
        private final EnumFacing side;

        public SidedView(@Nullable EnumFacing side) {
            this.side = side;
        }

        @Override
        public int receiveEnergy(int maxReceive, boolean simulate) {
            SideMode mode = getSideMode(side);
            if (mode == SideMode.NONE || mode == SideMode.OUTPUT) {
                return 0;
            }
            return container.receiveEnergy(maxReceive, simulate);
        }

        @Override
        public int extractEnergy(int maxExtract, boolean simulate) {
            SideMode mode = getSideMode(side);
            if (mode == SideMode.NONE || mode == SideMode.INPUT) {
                return 0;
            }
            return container.extractEnergy(maxExtract, simulate);
        }

        @Override
        public int getEnergyStored() {
            return container.getEnergyStored();
        }

        @Override
        public int getMaxEnergyStored() {
            return container.getMaxEnergyStored();
        }

        @Override
        public boolean canExtract() {
            SideMode mode = getSideMode(side);
            return (mode == SideMode.OUTPUT || mode == SideMode.BOTH) && container.canExtract();
        }

        @Override
        public boolean canReceive() {
            SideMode mode = getSideMode(side);
            return (mode == SideMode.INPUT || mode == SideMode.BOTH) && container.canReceive();
        }
    }

    /**
     * Mode for each side of the machine.
     */
    public enum SideMode {
        NONE,   // No energy transfer
        INPUT,  // Can receive energy
        OUTPUT, // Can extract energy
        BOTH    // Can both receive and extract
    }
}
