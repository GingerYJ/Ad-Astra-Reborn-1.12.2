package earth.terrarium.adastra.common.tile;

import earth.terrarium.adastra.AdAstraReborn;
import earth.terrarium.adastra.common.network.NetworkHandler;
import earth.terrarium.adastra.common.network.packet.PacketPlayRadioStation;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraftforge.fml.common.network.NetworkRegistry;

public class RadioTileEntity extends AdAstraSyncedTileEntity {

    public static final int MAX_STATION_LENGTH = 512;
    private static final double PLAYBACK_RANGE = Math.sqrt(3072.0D);

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
            syncPlaybackToClients();
        }
    }

    public boolean isPlaying() {
        return playing && !station.isEmpty();
    }

    @Override
    public void handleUpdateTag(NBTTagCompound tag) {
        super.handleUpdateTag(tag);
        syncPlaybackFromClientUpdate();
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        super.onDataPacket(net, pkt);
        syncPlaybackFromClientUpdate();
    }

    private void syncPlaybackToClients() {
        if (world == null || pos == null || world.isRemote) {
            return;
        }
        String url = isPlaying() ? station : "";
        NetworkHandler.CHANNEL.sendToAllAround(
            new PacketPlayRadioStation(url, pos),
            new NetworkRegistry.TargetPoint(
                world.provider.getDimension(),
                pos.getX() + 0.5D,
                pos.getY() + 0.5D,
                pos.getZ() + 0.5D,
                PLAYBACK_RANGE
            )
        );
    }

    private void syncPlaybackFromClientUpdate() {
        if (world != null && world.isRemote) {
            AdAstraReborn.proxy.syncRadioPlayback(pos, station, isPlaying());
        }
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
