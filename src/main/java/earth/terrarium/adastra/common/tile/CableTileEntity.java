package earth.terrarium.adastra.common.tile;

import earth.terrarium.adastra.common.registry.ModBlocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

public class CableTileEntity extends AdAstraPipeTileEntity implements ITickable {

    private static final int STEEL_TRANSFER_RATE = 150;
    private static final int DUCT_TRANSFER_RATE = 250;
    private static final int DESH_TRANSFER_RATE = 500;

    private final AdAstraEnergyStorage buffer = new AdAstraEnergyStorage(DESH_TRANSFER_RATE, DESH_TRANSFER_RATE, DESH_TRANSFER_RATE);

    @Override
    public void update() {
        if (world == null || world.isRemote || pos == null) {
            return;
        }
        pullEnergy();
        pushEnergy();
    }

    private void pullEnergy() {
        int room = buffer.getMaxEnergyStored() - buffer.getEnergyStored();
        if (room <= 0) {
            return;
        }

        int remaining = Math.min(getTransferRate(), room);
        for (EnumFacing facing : EnumFacing.values()) {
            if (!canPull(facing)) {
                continue;
            }
            IEnergyStorage source = getNeighborEnergy(facing);
            if (source == null || !source.canExtract()) {
                continue;
            }
            int extracted = source.extractEnergy(remaining, false);
            if (extracted > 0) {
                buffer.internalReceiveEnergy(extracted, false);
                markDirty();
                remaining -= extracted;
                if (remaining <= 0) {
                    return;
                }
            }
        }
    }

    private void pushEnergy() {
        int remaining = Math.min(getTransferRate(), buffer.getEnergyStored());
        if (remaining <= 0) {
            return;
        }

        for (EnumFacing facing : EnumFacing.values()) {
            if (!canPush(facing)) {
                continue;
            }
            IEnergyStorage target = getNeighborEnergy(facing);
            if (target == null || !target.canReceive()) {
                continue;
            }
            int accepted = target.receiveEnergy(remaining, false);
            if (accepted > 0) {
                buffer.extractEnergy(accepted, false);
                markDirty();
                remaining -= accepted;
                TileEntity tile = world.getTileEntity(pos.offset(facing));
                if (tile != null) {
                    tile.markDirty();
                }
                if (remaining <= 0) {
                    return;
                }
            }
        }
    }

    private IEnergyStorage getNeighborEnergy(EnumFacing facing) {
        TileEntity tile = world.getTileEntity(pos.offset(facing));
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
