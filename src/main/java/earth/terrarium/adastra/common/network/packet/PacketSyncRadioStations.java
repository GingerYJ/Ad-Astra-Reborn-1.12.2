package earth.terrarium.adastra.common.network.packet;

import earth.terrarium.adastra.client.gui.RadioStationGui;
import earth.terrarium.adastra.client.gui.RoverRadioGui;
import earth.terrarium.adastra.common.util.radio.StationInfo;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.ArrayList;
import java.util.List;

public class PacketSyncRadioStations implements IMessage {

    private static final int MAX_STATIONS = 1024;

    private final List<StationInfo> stations = new ArrayList<>();

    public PacketSyncRadioStations() {
    }

    public PacketSyncRadioStations(List<StationInfo> stations) {
        if (stations != null) {
            this.stations.addAll(stations);
        }
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        stations.clear();
        int count = Math.min(buf.readInt(), MAX_STATIONS);
        for (int i = 0; i < count; i++) {
            stations.add(StationInfo.read(buf));
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        int count = Math.min(stations.size(), MAX_STATIONS);
        buf.writeInt(count);
        for (int i = 0; i < count; i++) {
            stations.get(i).write(buf);
        }
    }

    public static class Handler implements IMessageHandler<PacketSyncRadioStations, IMessage> {

        @Override
        public IMessage onMessage(PacketSyncRadioStations message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                RadioStationGui.handleStationUpdates(message.stations);
                RoverRadioGui.handleStationUpdates(message.stations);
            });
            return null;
        }
    }
}
