package earth.terrarium.adastra.common.systems;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

/**
 * WorldSavedData for storing planet environmental data per block position.
 * Tracks oxygen, temperature, and gravity overrides for specific locations.
 */
public class PlanetDataStorage extends WorldSavedData {

    private static final String DATA_NAME = "adastra_planet_data";

    private final Map<BlockPos, PlanetData> planetData = new HashMap<>();
    private final World world;

    public PlanetDataStorage(String name, World world) {
        super(name);
        this.world = world;
    }

    public PlanetDataStorage(World world) {
        this(DATA_NAME, world);
    }

    public static PlanetDataStorage get(World world) {
        if (world.isRemote || !(world instanceof WorldServer)) {
            throw new IllegalStateException("PlanetDataStorage can only be accessed on the server side");
        }

        MapStorage storage = world.getMapStorage();
        PlanetDataStorage data = (PlanetDataStorage) storage.getOrLoadData(PlanetDataStorage.class, DATA_NAME);

        if (data == null) {
            data = new PlanetDataStorage(world);
            storage.setData(DATA_NAME, data);
        }

        return data;
    }

    @Override
    public void readFromNBT(@Nonnull NBTTagCompound nbt) {
        planetData.clear();
        int[] data = nbt.getIntArray("Data");

        if (data.length % 3 != 0) {
            throw new RuntimeException("Invalid planet data length: " + data.length);
        }

        for (int i = 0; i < data.length; i += 3) {
            int firstPos = data[i];
            int secondPos = data[i + 1];
            long packedPos = ((long) firstPos << 32) | (secondPos & 0xFFFFFFFFL);
            BlockPos pos = BlockPos.fromLong(packedPos);
            PlanetData planetData = PlanetData.unpack(data[i + 2]);
            this.planetData.put(pos, planetData);
        }
    }

    @Override
    @Nonnull
    public NBTTagCompound writeToNBT(@Nonnull NBTTagCompound nbt) {
        boolean defaultOxygen = OxygenSystemExtended.hasOxygenInDimension(world);
        short defaultTemperature = TemperatureSystem.getTemperatureInDimension(world);
        float defaultGravity = GravitySystem.getGravityInDimension(world);

        IntArrayList dataArray = new IntArrayList(this.planetData.size() * 3);

        for (Map.Entry<BlockPos, PlanetData> entry : planetData.entrySet()) {
            PlanetData data = entry.getValue();

            if (data.oxygen() != defaultOxygen ||
                data.temperature() != defaultTemperature ||
                data.gravity() != defaultGravity) {

                long packedPos = entry.getKey().toLong();
                dataArray.add((int) (packedPos >> 32));
                dataArray.add((int) packedPos);
                dataArray.add(data.pack());
            }
        }

        nbt.setIntArray("Data", dataArray.toIntArray());
        return nbt;
    }

    public PlanetData getData(BlockPos pos) {
        return planetData.get(pos);
    }

    public void setData(BlockPos pos, PlanetData data) {
        planetData.put(pos, data);
        markDirty();
    }

    public void removeData(BlockPos pos) {
        if (planetData.remove(pos) != null) {
            markDirty();
        }
    }

    public boolean hasData(BlockPos pos) {
        return planetData.containsKey(pos);
    }

    public void clear() {
        planetData.clear();
        markDirty();
    }
}
