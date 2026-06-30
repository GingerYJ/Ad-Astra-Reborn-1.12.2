package earth.terrarium.adastra.common.tile;

import earth.terrarium.adastra.common.registry.ModBlocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

public class FluidPipeTileEntity extends AdAstraPipeTileEntity implements ITickable {

    private static final int DESH_TRANSFER_RATE = 150;
    private static final int OSTRUM_TRANSFER_RATE = 500;

    private final FluidTank buffer = new FluidTank(OSTRUM_TRANSFER_RATE) {
        @Override
        protected void onContentsChanged() {
            markDirty();
        }
    };

    @Override
    public void update() {
        if (world == null || world.isRemote || pos == null) {
            return;
        }
        pullFluid();
        pushFluid();
    }

    private void pullFluid() {
        int room = buffer.getCapacity() - buffer.getFluidAmount();
        if (room <= 0) {
            return;
        }

        int remaining = Math.min(getTransferRate(), room);
        for (EnumFacing facing : EnumFacing.values()) {
            if (!canPull(facing)) {
                continue;
            }
            IFluidHandler source = getNeighborFluidHandler(facing);
            if (source == null) {
                continue;
            }
            FluidStack simulated = source.drain(remaining, false);
            if (simulated == null || simulated.amount <= 0 || buffer.fill(simulated, false) <= 0) {
                continue;
            }
            int fillable = buffer.fill(simulated, false);
            FluidStack drained = source.drain(fillable, true);
            if (drained != null && drained.amount > 0) {
                buffer.fill(drained, true);
                markDirty();
                remaining -= drained.amount;
                if (remaining <= 0) {
                    return;
                }
            }
        }
    }

    private void pushFluid() {
        FluidStack stored = buffer.getFluid();
        if (stored == null || stored.amount <= 0) {
            return;
        }

        int remaining = Math.min(getTransferRate(), stored.amount);
        for (EnumFacing facing : EnumFacing.values()) {
            if (!canPush(facing)) {
                continue;
            }
            IFluidHandler target = getNeighborFluidHandler(facing);
            if (target == null) {
                continue;
            }
            FluidStack offered = stored.copy();
            offered.amount = remaining;
            int accepted = target.fill(offered, true);
            if (accepted > 0) {
                buffer.drain(accepted, true);
                markDirty();
                TileEntity tile = world.getTileEntity(pos.offset(facing));
                if (tile != null) {
                    tile.markDirty();
                }
                remaining -= accepted;
                stored = buffer.getFluid();
                if (remaining <= 0 || stored == null || stored.amount <= 0) {
                    return;
                }
            }
        }
    }

    private IFluidHandler getNeighborFluidHandler(EnumFacing facing) {
        TileEntity tile = world.getTileEntity(pos.offset(facing));
        if (tile == null || !tile.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, facing.getOpposite())) {
            return null;
        }
        return tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, facing.getOpposite());
    }

    private int getTransferRate() {
        if (world != null && pos != null && world.getBlockState(pos).getBlock() == ModBlocks.OSTRUM_FLUID_PIPE) {
            return OSTRUM_TRANSFER_RATE;
        }
        return DESH_TRANSFER_RATE;
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
            return (T) buffer;
        }
        return super.getCapability(capability, facing);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        if (compound.hasKey("FluidTank")) {
            buffer.readFromNBT(compound.getCompoundTag("FluidTank"));
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setTag("FluidTank", buffer.writeToNBT(new NBTTagCompound()));
        return compound;
    }
}
