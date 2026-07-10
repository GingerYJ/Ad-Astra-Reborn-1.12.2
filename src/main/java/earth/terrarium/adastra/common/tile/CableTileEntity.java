package earth.terrarium.adastra.common.tile;

import earth.terrarium.adastra.common.config.AdAstraConfig;
import earth.terrarium.adastra.common.registry.ModBlocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

public class CableTileEntity extends AdAstraPipeTileEntity implements ITickable {

    private static final int IDLE_THRESHOLD = 20;
    private static final int STEEL_TRANSFER_RATE = 150;
    private static final int DUCT_TRANSFER_RATE = 250;
    private static final int DESH_TRANSFER_RATE = 500;

    private int idleTicks;
    private int pullCursor;
    private int pushCursor;
    private final AdAstraEnergyStorage buffer = new AdAstraEnergyStorage(DESH_TRANSFER_RATE, DESH_TRANSFER_RATE, DESH_TRANSFER_RATE) {
        @Override
        public int receiveEnergy(int maxReceive, boolean simulate) {
            int received = super.receiveEnergy(maxReceive, simulate);
            if (!simulate && received > 0) {
                idleTicks = 0;
            }
            return received;
        }
    };

    @Override
    public void update() {
        if (world == null || world.isRemote || pos == null) {
            return;
        }
        int idleInterval = getConfiguredIdleInterval();
        if (idleTicks >= IDLE_THRESHOLD
            && (world.getTotalWorldTime() + Math.floorMod(pos.toLong(), idleInterval)) % idleInterval != 0) {
            return;
        }
        int attempts = idleTicks >= IDLE_THRESHOLD ? EnumFacing.values().length : 1;
        boolean transferred = pullEnergy(attempts);
        transferred |= pushEnergy(attempts);
        idleTicks = transferred ? 0 : idleTicks + 1;
    }

    private boolean pullEnergy(int attempts) {
        int room = buffer.getMaxEnergyStored() - buffer.getEnergyStored();
        if (room <= 0) {
            return false;
        }

        for (int attempt = 0; attempt < attempts; attempt++) {
            EnumFacing facing = nextTransferFacing(true);
            if (facing != null && pullEnergy(facing, room)) {
                return true;
            }
        }
        return false;
    }

    private boolean pullEnergy(EnumFacing facing, int room) {
        TileEntity sourceTile = getCachedNeighbor(facing);
        if (sourceTile instanceof EnergizerTileEntity
            && getConnection(facing) != earth.terrarium.adastra.common.blocks.AdAstraPipeConnection.EXTRACT) {
            return false;
        }
        IEnergyStorage source = getNeighborEnergy(sourceTile, facing);
        if (source == null || !source.canExtract()) {
            return false;
        }
        int extractable = source.extractEnergy(Math.min(getTransferRate(), room), true);
        int receivable = buffer.internalReceiveEnergy(extractable, true);
        if (receivable <= 0) {
            return false;
        }
        int extracted = source.extractEnergy(receivable, false);
        if (extracted <= 0) {
            return false;
        }
        int received = buffer.internalReceiveEnergy(extracted, false);
        if (received <= 0) {
            return false;
        }
        if (sourceTile instanceof EnergizerTileEntity) {
            ((EnergizerTileEntity) sourceTile).onExternalEnergyChanged();
        }
        markDirty();
        return true;
    }

    private boolean pushEnergy(int attempts) {
        int remaining = Math.min(getTransferRate(), buffer.getEnergyStored());
        if (remaining <= 0) {
            return false;
        }

        for (int attempt = 0; attempt < attempts; attempt++) {
            EnumFacing facing = nextTransferFacing(false);
            if (facing != null && pushEnergy(facing, remaining)) {
                return true;
            }
        }
        return false;
    }

    private boolean pushEnergy(EnumFacing facing, int remaining) {
        TileEntity tile = getCachedNeighbor(facing);
        IEnergyStorage target = getNeighborEnergy(tile, facing);
        if (target == null || !target.canReceive()) {
            return false;
        }
        int acceptable = target.receiveEnergy(remaining, true);
        int extractable = buffer.internalExtractEnergy(acceptable, true);
        if (extractable <= 0) {
            return false;
        }
        int extracted = buffer.internalExtractEnergy(extractable, false);
        int accepted = target.receiveEnergy(extracted, false);
        if (accepted < extracted) {
            buffer.internalReceiveEnergy(extracted - accepted, false);
        }
        if (accepted <= 0) {
            return false;
        }
        markDirty();
        if (tile != null) {
            tile.markDirty();
        }
        if (tile instanceof EnergizerTileEntity) {
            ((EnergizerTileEntity) tile).onExternalEnergyChanged();
        }
        return true;
    }

    private EnumFacing nextTransferFacing(boolean pull) {
        EnumFacing[] facings = EnumFacing.values();
        int cursor = pull ? pullCursor : pushCursor;
        for (int offset = 0; offset < facings.length; offset++) {
            int index = (cursor + offset) % facings.length;
            EnumFacing facing = facings[index];
            if (pull ? canPull(facing) : canPush(facing)) {
                if (pull) {
                    pullCursor = (index + 1) % facings.length;
                } else {
                    pushCursor = (index + 1) % facings.length;
                }
                return facing;
            }
        }
        return null;
    }

    private IEnergyStorage getNeighborEnergy(TileEntity tile, EnumFacing facing) {
        if (tile == null || !tile.hasCapability(CapabilityEnergy.ENERGY, facing.getOpposite())) {
            return null;
        }
        return tile.getCapability(CapabilityEnergy.ENERGY, facing.getOpposite());
    }

    private int getTransferRate() {
        if (world != null && pos != null && world.getBlockState(pos).getBlock() == ModBlocks.CABLE_DUCT) {
            return DUCT_TRANSFER_RATE;
        }
        if (world != null && pos != null && world.getBlockState(pos).getBlock() == ModBlocks.DESH_CABLE) {
            return DESH_TRANSFER_RATE;
        }
        return STEEL_TRANSFER_RATE;
    }

    private static int getConfiguredIdleInterval() {
        return Math.max(1, AdAstraConfig.pipeRefreshRate);
    }

    @Override
    public void invalidateNeighborCache() {
        super.invalidateNeighborCache();
        idleTicks = 0;
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        if (capability == CapabilityEnergy.ENERGY) {
            return true;
        }
        return super.hasCapability(capability, facing);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        if (capability == CapabilityEnergy.ENERGY) {
            return (T) buffer;
        }
        return super.getCapability(capability, facing);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        if (compound.hasKey("Energy")) {
            buffer.readFromNBT(compound.getCompoundTag("Energy"));
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setTag("Energy", buffer.writeToNBT(new NBTTagCompound()));
        return compound;
    }
}
