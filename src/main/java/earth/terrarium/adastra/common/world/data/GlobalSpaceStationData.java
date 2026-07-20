package earth.terrarium.adastra.common.world.data;

import earth.terrarium.adastra.common.registry.ModDimensions;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;

/** Server-wide state for the one shared Ad Astra space station. */
public class GlobalSpaceStationData extends WorldSavedData {

    public static final String DATA_NAME = "adastra_space_station";

    private boolean constructed;
    private BlockPos position = BlockPos.ORIGIN;
    private String name = "space_station";

    public GlobalSpaceStationData(String name) {
        super(name);
    }

    public GlobalSpaceStationData() {
        this(DATA_NAME);
    }

    public static GlobalSpaceStationData get(World world) {
        if (world == null || world.isRemote) {
            throw new IllegalArgumentException("GlobalSpaceStationData must be accessed on the server.");
        }

        WorldServer overworld = world instanceof WorldServer
            ? ((WorldServer) world).getMinecraftServer().getWorld(0)
            : null;
        if (overworld == null) {
            throw new IllegalArgumentException("The server overworld is unavailable.");
        }

        MapStorage storage = overworld.getMapStorage();
        GlobalSpaceStationData data = (GlobalSpaceStationData) storage.getOrLoadData(
            GlobalSpaceStationData.class, DATA_NAME);
        if (data == null) {
            data = new GlobalSpaceStationData();
            storage.setData(DATA_NAME, data);
            data.markDirty();
        }
        return data;
    }

    public boolean isConstructed() {
        return constructed;
    }

    public BlockPos getPosition() {
        return constructed ? position : null;
    }

    public String getName() {
        return name;
    }

    public int getDimension() {
        return ModDimensions.SPACE_STATION_ID;
    }

    public void construct(BlockPos position, String name) {
        if (constructed || position == null) {
            return;
        }
        this.position = position;
        this.name = name == null || name.trim().isEmpty() ? "space_station" : name;
        this.constructed = true;
        markDirty();
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        constructed = nbt.getBoolean("Constructed");
        position = new BlockPos(
            nbt.getInteger("PosX"),
            nbt.getInteger("PosY"),
            nbt.getInteger("PosZ"));
        name = nbt.hasKey("Name") ? nbt.getString("Name") : "space_station";
        if (name.trim().isEmpty()) {
            name = "space_station";
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        nbt.setBoolean("Constructed", constructed);
        nbt.setInteger("PosX", position.getX());
        nbt.setInteger("PosY", position.getY());
        nbt.setInteger("PosZ", position.getZ());
        nbt.setString("Name", name);
        return nbt;
    }
}
