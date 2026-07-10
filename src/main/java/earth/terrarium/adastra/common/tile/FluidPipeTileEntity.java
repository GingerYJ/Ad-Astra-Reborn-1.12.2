package earth.terrarium.adastra.common.tile;

import earth.terrarium.adastra.common.config.AdAstraConfig;
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

    private static final int IDLE_THRESHOLD = 20;
    private static final int DESH_TRANSFER_RATE = 150;
    private static final int DUCT_TRANSFER_RATE = 250;
    private static final int OSTRUM_TRANSFER_RATE = 500;
    private int idleTicks;
    private int pullCursor;
    private int pushCursor;

    private final FluidTank buffer = new FluidTank(OSTRUM_TRANSFER_RATE) {
        @Override
        protected void onContentsChanged() {
            idleTicks = 0;
            markDirty();
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
        boolean transferred = pullFluid(attempts);
        transferred |= pushFluid(attempts);
        idleTicks = transferred ? 0 : idleTicks + 1;
    }

    private boolean pullFluid(int attempts) {
        int room = buffer.getCapacity() - buffer.getFluidAmount();
        if (room <= 0) {
            return false;
        }

        for (int attempt = 0; attempt < attempts; attempt++) {
            EnumFacing facing = nextTransferFacing(true);
            if (facing != null && pullFluid(facing, room)) {
                return true;
            }
        }
        return false;
    }

    private boolean pullFluid(EnumFacing facing, int room) {
        IFluidHandler source = getNeighborFluidHandler(facing);
        if (source == null) {
            return false;
        }
        FluidStack simulated = source.drain(Math.min(getTransferRate(), room), false);
        if (simulated == null || simulated.amount <= 0) {
            return false;
        }
        int fillable = buffer.fill(simulated, false);
        if (fillable <= 0) {
            return false;
        }
        FluidStack drained = source.drain(fillable, true);
        if (drained == null || drained.amount <= 0) {
            return false;
        }
        buffer.fill(drained, true);
        markDirty();
        return true;
    }

    private boolean pushFluid(int attempts) {
        FluidStack stored = buffer.getFluid();
        if (stored == null || stored.amount <= 0) {
            return false;
        }

        int remaining = Math.min(getTransferRate(), stored.amount);
        for (int attempt = 0; attempt < attempts; attempt++) {
            EnumFacing facing = nextTransferFacing(false);
            if (facing != null && pushFluid(facing, stored, remaining)) {
                return true;
            }
        }
        return false;
    }

    private boolean pushFluid(EnumFacing facing, FluidStack stored, int remaining) {
        IFluidHandler target = getNeighborFluidHandler(facing);
        if (target == null) {
            return false;
        }
        FluidStack offered = stored.copy();
        offered.amount = remaining;
        int accepted = target.fill(offered, true);
        if (accepted <= 0) {
            return false;
        }
        buffer.drain(accepted, true);
        markDirty();
        TileEntity tile = getCachedNeighbor(facing);
        if (tile != null) {
            tile.markDirty();
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

    private IFluidHandler getNeighborFluidHandler(EnumFacing facing) {
        TileEntity tile = getCachedNeighbor(facing);
        if (tile == null || !tile.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, facing.getOpposite())) {
            return null;
        }
        return tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, facing.getOpposite());
    }

    private int getTransferRate() {
        if (world != null && pos != null && world.getBlockState(pos).getBlock() == ModBlocks.FLUID_PIPE_DUCT) {
            return DUCT_TRANSFER_RATE;
        }
        if (world != null && pos != null && world.getBlockState(pos).getBlock() == ModBlocks.OSTRUM_FLUID_PIPE) {
            return OSTRUM_TRANSFER_RATE;
        }
        return DESH_TRANSFER_RATE;
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
