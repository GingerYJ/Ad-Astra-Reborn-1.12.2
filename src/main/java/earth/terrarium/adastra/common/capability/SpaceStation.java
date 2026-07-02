package earth.terrarium.adastra.common.capability;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;

import java.util.Objects;
import java.util.UUID;

public class SpaceStation {

    private String name;
    private int dimension;
    private BlockPos position;
    private UUID owner;

    public SpaceStation(String name, int dimension, BlockPos position, UUID owner) {
        this.name = name;
        this.dimension = dimension;
        this.position = position;
        this.owner = owner;
    }

    public SpaceStation(NBTTagCompound nbt) {
        readFromNBT(nbt);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDimension() {
        return dimension;
    }

    public void setDimension(int dimension) {
        this.dimension = dimension;
    }

    public BlockPos getPosition() {
        return position;
    }

    public void setPosition(BlockPos position) {
        this.position = position;
    }

    public UUID getOwner() {
        return owner;
    }

    public void setOwner(UUID owner) {
        this.owner = owner;
    }

    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        nbt.setString("Name", name);
        nbt.setInteger("Dimension", dimension);
        nbt.setLong("PosX", position.getX());
        nbt.setLong("PosY", position.getY());
        nbt.setLong("PosZ", position.getZ());
        nbt.setString("Owner", owner.toString());
        return nbt;
    }

    public void readFromNBT(NBTTagCompound nbt) {
        this.name = nbt.getString("Name");
        this.dimension = nbt.getInteger("Dimension");
        long x = nbt.getLong("PosX");
        long y = nbt.getLong("PosY");
        long z = nbt.getLong("PosZ");
        this.position = new BlockPos(x, y, z);
        this.owner = UUID.fromString(nbt.getString("Owner"));
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        SpaceStation that = (SpaceStation) obj;
        return dimension == that.dimension &&
            Objects.equals(name, that.name) &&
            Objects.equals(position, that.position) &&
            Objects.equals(owner, that.owner);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, dimension, position, owner);
    }

    @Override
    public String toString() {
        return "SpaceStation{" +
            "name='" + name + '\'' +
            ", dimension=" + dimension +
            ", position=" + position +
            ", owner=" + owner +
            '}';
    }
}
