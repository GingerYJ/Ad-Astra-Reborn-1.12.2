package earth.terrarium.adastra.common.tile;

import net.minecraft.nbt.NBTTagCompound;

public class RadioTileEntity extends AdAstraSyncedTileEntity {

    private String station = "";
    private boolean playing;

    public String getStation() {
        return station;
    }

    public void setStation(String station) {
        String value = station == null ? "" : station.trim();
        if (!this.station.equals(value)) {
            this.station = value;
            syncToClients();
        }
    }

    public boolean isPlaying() {
        return playing && !station.isEmpty();
    }

    public boolean hasStation() {
        return !station.isEmpty();
    }

    public boolean togglePlaying() {
        playing = !playing;
        if (station.isEmpty()) {
            playing = false;
        }
        syncToClients();
        return playing;
    }

    public void stop() {
        if (playing) {
            playing = false;
            syncToClients();
        }
    }

    public void clearStation() {
        boolean changed = !station.isEmpty() || playing;
        station = "";
        playing = false;
        if (changed) {
            syncToClients();
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        station = compound.getString("Station");
        playing = compound.getBoolean("Playing");
        if (station.isEmpty()) {
            playing = false;
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setString("Station", station);
        compound.setBoolean("Playing", playing);
        return compound;
    }
}
