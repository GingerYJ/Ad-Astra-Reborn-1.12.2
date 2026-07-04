package earth.terrarium.adastra.common.util.radio;

import earth.terrarium.adastra.common.tile.RadioTileEntity;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;

public class StationInfo {

    private final String url;
    private final String title;
    private final String name;
    private final StationLocation location;

    public StationInfo(String url, String title, String name, StationLocation location) {
        this.url = RadioTileEntity.normalizeStation(url);
        this.title = normalize(title, "0.00 N/A");
        this.name = normalize(name, "Unknown Station");
        this.location = location == null ? StationLocation.UNKNOWN : location;
    }

    public String getUrl() {
        return url;
    }

    public String getTitle() {
        return title;
    }

    public String getName() {
        return name;
    }

    public StationLocation getLocation() {
        return location;
    }

    public void write(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, url);
        ByteBufUtils.writeUTF8String(buf, title);
        ByteBufUtils.writeUTF8String(buf, name);
        buf.writeByte(location.ordinal());
    }

    public static StationInfo read(ByteBuf buf) {
        String url = ByteBufUtils.readUTF8String(buf);
        String title = ByteBufUtils.readUTF8String(buf);
        String name = ByteBufUtils.readUTF8String(buf);
        int ordinal = buf.readUnsignedByte();
        StationLocation[] values = StationLocation.values();
        StationLocation location = ordinal >= 0 && ordinal < values.length ? values[ordinal] : StationLocation.UNKNOWN;
        return new StationInfo(url, title, name, location);
    }

    private static String normalize(String value, String fallback) {
        String normalized = value == null ? "" : value.trim();
        return normalized.isEmpty() ? fallback : normalized;
    }
}
