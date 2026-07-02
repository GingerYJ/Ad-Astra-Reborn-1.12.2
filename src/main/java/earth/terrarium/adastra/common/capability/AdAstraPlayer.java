package earth.terrarium.adastra.common.capability;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.Constants;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class AdAstraPlayer implements IAdAstraPlayer {

    private int oxygenTicks;
    private float temperature;
    private float gravity;
    private Map<Integer, BlockPos> launchPositions;
    private Set<SpaceStation> spaceStations;

    public AdAstraPlayer() {
        this.oxygenTicks = 0;
        this.temperature = 20.0f;
        this.gravity = 1.0f;
        this.launchPositions = new HashMap<>();
        this.spaceStations = new HashSet<>();
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

    @Override
    public Set<SpaceStation> getSpaceStations() {
        return spaceStations;
    }

    @Override
    public void addSpaceStation(SpaceStation station) {
        if (station != null) {
            spaceStations.add(station);
        }
    }

    @Override
    public void removeSpaceStation(SpaceStation station) {
        spaceStations.remove(station);
    }

    @Override
    public boolean hasSpaceStation(SpaceStation station) {
        return spaceStations.contains(station);
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

        NBTTagList stationsList = new NBTTagList();
        for (SpaceStation station : spaceStations) {
            NBTTagCompound stationTag = new NBTTagCompound();
            station.writeToNBT(stationTag);
            stationsList.appendTag(stationTag);
        }
        nbt.setTag("SpaceStations", stationsList);

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

        spaceStations.clear();
        NBTTagList stationsList = nbt.getTagList("SpaceStations", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < stationsList.tagCount(); i++) {
            NBTTagCompound stationTag = stationsList.getCompoundTagAt(i);
            SpaceStation station = new SpaceStation(stationTag);
            spaceStations.add(station);
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
        this.spaceStations = new HashSet<>(other.spaceStations);
    }
}
