package earth.terrarium.adastra.common.tile;

import earth.terrarium.adastra.common.blocks.AdAstraPipeConnection;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;

public class AdAstraPipeTileEntity extends AdAstraTileEntity {

    private final AdAstraPipeConnection[] connections = new AdAstraPipeConnection[EnumFacing.values().length];

    public AdAstraPipeTileEntity() {
        for (EnumFacing facing : EnumFacing.values()) {
            connections[facing.getIndex()] = AdAstraPipeConnection.NORMAL;
        }
    }

    public AdAstraPipeConnection getConnection(EnumFacing facing) {
        return connections[facing.getIndex()];
    }

    public AdAstraPipeConnection cycleConnection(EnumFacing facing, boolean backwards) {
        AdAstraPipeConnection current = getConnection(facing);
        AdAstraPipeConnection next = backwards ? current.previous() : current.next();
        connections[facing.getIndex()] = next;
        markDirty();
        if (world != null && pos != null) {
            world.markBlockRangeForRenderUpdate(pos, pos);
        }
        return next;
    }

    protected boolean canPull(EnumFacing facing) {
        AdAstraPipeConnection connection = getConnection(facing);
        return connection == AdAstraPipeConnection.NORMAL || connection == AdAstraPipeConnection.EXTRACT;
    }

    protected boolean canPush(EnumFacing facing) {
        AdAstraPipeConnection connection = getConnection(facing);
        return connection == AdAstraPipeConnection.NORMAL || connection == AdAstraPipeConnection.INSERT;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        if (compound.hasKey("Connections")) {
            NBTTagCompound connectionTag = compound.getCompoundTag("Connections");
            for (EnumFacing facing : EnumFacing.values()) {
                connections[facing.getIndex()] = AdAstraPipeConnection.byOrdinal(connectionTag.getByte(facing.getName()));
            }
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        NBTTagCompound connectionTag = new NBTTagCompound();
        for (EnumFacing facing : EnumFacing.values()) {
            connectionTag.setByte(facing.getName(), (byte) connections[facing.getIndex()].ordinal());
        }
        compound.setTag("Connections", connectionTag);
        return compound;
    }
}
