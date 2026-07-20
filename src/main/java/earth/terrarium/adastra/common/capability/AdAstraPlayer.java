package earth.terrarium.adastra.common.capability;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.Constants;

import java.util.HashMap;
import java.util.Map;

public class AdAstraPlayer implements IAdAstraPlayer {

    private int oxygenTicks;
    private float temperature;
    private float gravity;
    private Map<Integer, BlockPos> launchPositions;

    public AdAstraPlayer() {
        this.oxygenTicks = 0;
        this.temperature = 20.0f;
        this.gravity = 1.0f;
        this.launchPositions = new HashMap<>();
    }

    @Override
    public int getOxygenTicks() {
        return oxygenTicks;
    }

    @Override
    public void setOxygenTicks(int ticks) {
        this.oxygenTicks = ticks;
    }

    @Override
    public float getTemperature() {
        return temperature;
    }

    @Override
    public void setTemperature(float temperature) {
        this.temperature = temperature;
    }

    @Override
    public float getGravity() {
        return gravity;
    }

    @Override
    public void setGravity(float gravity) {
        this.gravity = gravity;
    }

    @Override
    public Map<Integer, BlockPos> getLaunchPositions() {
        return launchPositions;
    }

    @Override
    public void setLaunchPosition(int dimension, BlockPos pos) {
        if (pos != null) {
            launchPositions.put(dimension, pos);
        } else {
            launchPositions.remove(dimension);
        }
    }

    @Override
    public BlockPos getLaunchPosition(int dimension) {
        return launchPositions.get(dimension);
    }

    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        nbt.setInteger("OxygenTicks", oxygenTicks);
        nbt.setFloat("Temperature", temperature);
        nbt.setFloat("Gravity", gravity);

        NBTTagList launchPosList = new NBTTagList();
        for (Map.Entry<Integer, BlockPos> entry : launchPositions.entrySet()) {
            NBTTagCompound posTag = new NBTTagCompound();
            posTag.setInteger("Dimension", entry.getKey());
            posTag.setLong("X", entry.getValue().getX());
            posTag.setLong("Y", entry.getValue().getY());
            posTag.setLong("Z", entry.getValue().getZ());
            launchPosList.appendTag(posTag);
        }
        nbt.setTag("LaunchPositions", launchPosList);

        return nbt;
    }

    public void readFromNBT(NBTTagCompound nbt) {
        oxygenTicks = nbt.getInteger("OxygenTicks");
        temperature = nbt.getFloat("Temperature");
        gravity = nbt.getFloat("Gravity");

        launchPositions.clear();
        NBTTagList launchPosList = nbt.getTagList("LaunchPositions", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < launchPosList.tagCount(); i++) {
            NBTTagCompound posTag = launchPosList.getCompoundTagAt(i);
            int dimension = posTag.getInteger("Dimension");
            long x = posTag.getLong("X");
            long y = posTag.getLong("Y");
            long z = posTag.getLong("Z");
            launchPositions.put(dimension, new BlockPos(x, y, z));
        }

    }

    public void copyFrom(AdAstraPlayer other) {
        if (other == null) {
            return;
        }
        this.oxygenTicks = other.oxygenTicks;
        this.temperature = other.temperature;
        this.gravity = other.gravity;
        this.launchPositions = new HashMap<>(other.launchPositions);
    }
}
