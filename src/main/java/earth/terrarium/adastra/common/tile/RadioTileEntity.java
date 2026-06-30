package earth.terrarium.adastra.common.tile;

import net.minecraft.nbt.NBTTagCompound;

public class RadioTileEntity extends AdAstraSyncedTileEntity {

    public static final int MAX_STATION_LENGTH = 512;

    private String station = "";
    private boolean playing;

    public static String normalizeStation(String station) {
        String value = station == null ? "" : station.trim();
        return value.length() > MAX_STATION_LENGTH ? value.substring(0, MAX_STATION_LENGTH) : value;
    }

    public String getStation() {
        return station;
    }

    public void setStation(String station) {
        setStationAndPlaying(station, playing);
    }

    public void setPlaying(boolean playing) {
        setStationAndPlaying(station, playing);
    }

    public void setStationAndPlaying(String station, boolean playing) {
        String value = normalizeStation(station);
        boolean valuePlaying = playing && !value.isEmpty();
        if (!this.station.equals(value) || this.playing != valuePlaying) {
            this.station = value;
            this.playing = valuePlaying;
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
        setPlaying(!playing);
        return isPlaying();
    }

    public void stop() {
        setPlaying(false);
    }

    public void clearStation() {
        setStationAndPlaying("", false);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        station = normalizeStation(compound.getString("Station"));
        playing = compound.getBoolean("Playing") && !station.isEmpty();
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setString("Station", station);
        compound.setBoolean("Playing", playing);
        return compound;
    }
}
