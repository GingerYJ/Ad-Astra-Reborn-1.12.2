package earth.terrarium.adastra.common.capability;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;

public class SpaceStation {

    private String name;
    private int dimension;
    private BlockPos position;

    public SpaceStation(String name, int dimension, BlockPos position) {
        this.name = name;
        this.dimension = dimension;
        this.position = position;
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

    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        nbt.setString("Name", name);
        nbt.setInteger("Dimension", dimension);
        nbt.setLong("PosX", position.getX());
        nbt.setLong("PosY", position.getY());
        nbt.setLong("PosZ", position.getZ());
        return nbt;
    }

    public void readFromNBT(NBTTagCompound nbt) {
        this.name = nbt.getString("Name");
        this.dimension = nbt.getInteger("Dimension");
        long x = nbt.getLong("PosX");
        long y = nbt.getLong("PosY");
        long z = nbt.getLong("PosZ");
        this.position = new BlockPos(x, y, z);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        SpaceStation that = (SpaceStation) obj;
        return dimension == that.dimension &&
            java.util.Objects.equals(name, that.name) &&
            java.util.Objects.equals(position, that.position);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(name, dimension, position);
    }

    @Override
    public String toString() {
        return "SpaceStation{" +
            "name='" + name + '\'' +
            ", dimension=" + dimension +
            ", position=" + position +
            '}';
    }
}
