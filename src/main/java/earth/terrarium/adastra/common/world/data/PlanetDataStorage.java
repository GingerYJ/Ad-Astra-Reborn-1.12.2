package earth.terrarium.adastra.common.world.data;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

/**
 * WorldSavedData that stores per-block position environmental data.
 * Uses Long-encoded BlockPos for efficient storage.
 */
public class PlanetDataStorage extends WorldSavedData {

    private static final String DATA_NAME = "adastra_planet_data";

    private final Map<Long, PlanetData> planetData = new HashMap<>();
    private final World world;

    private boolean isDirty = false;

    public PlanetDataStorage(String name, World world) {
        super(name);
        this.world = world;
    }

    public PlanetDataStorage(World world) {
        this(DATA_NAME, world);
    }

    /**
     * Gets or creates the PlanetDataStorage for the given world.
     */
    @Nonnull
    public static PlanetDataStorage get(World world) {
        if (world.isRemote) {
            throw new RuntimeException("PlanetDataStorage should only be accessed server-side");
        }

        MapStorage storage = world.getMapStorage();
        PlanetDataStorage instance = (PlanetDataStorage) storage.getOrLoadData(PlanetDataStorage.class, DATA_NAME);

        if (instance == null) {
            instance = new PlanetDataStorage(world);
            storage.setData(DATA_NAME, instance);
        }

        return instance;
    }

    /**
     * Sets oxygen presence at the given position.
     */
    public void setOxygen(BlockPos pos, boolean hasOxygen) {
        long key = pos.toLong();
        PlanetData data = planetData.computeIfAbsent(key, k ->
            new PlanetData(hasOxygen, getDefaultTemperature(), getDefaultGravity()));
        data.setOxygen(hasOxygen);
        isDirty = true;
        markDirty();
    }

    /**
     * Gets oxygen presence at the given position.
     * Returns dimension default if not set.
     */
    public boolean getOxygen(BlockPos pos) {
        PlanetData data = planetData.get(pos.toLong());
        return data != null ? data.hasOxygen() : getDefaultOxygen();
    }

    /**
     * Checks if oxygen data exists for the given position.
     */
    public boolean hasOxygen(BlockPos pos) {
        return getOxygen(pos);
    }

    /**
     * Sets temperature at the given position.
     */
    public void setTemperature(BlockPos pos, short temperature) {
        long key = pos.toLong();
        PlanetData data = planetData.computeIfAbsent(key, k ->
            new PlanetData(getDefaultOxygen(), temperature, getDefaultGravity()));
        data.setTemperature(temperature);
        isDirty = true;
        markDirty();
    }

    /**
     * Gets temperature at the given position.
     * Returns dimension default if not set.
     */
    public short getTemperature(BlockPos pos) {
        PlanetData data = planetData.get(pos.toLong());
        return data != null ? data.getTemperature() : getDefaultTemperature();
    }

    /**
     * Sets gravity multiplier at the given position.
     */
    public void setGravity(BlockPos pos, float gravity) {
        long key = pos.toLong();
        PlanetData data = planetData.computeIfAbsent(key, k ->
            new PlanetData(getDefaultOxygen(), getDefaultTemperature(), gravity));
        data.setGravity(gravity);
        isDirty = true;
        markDirty();
    }

    /**
     * Gets gravity multiplier at the given position.
     * Returns dimension default if not set.
     */
    public float getGravity(BlockPos pos) {
        PlanetData data = planetData.get(pos.toLong());
        return data != null ? data.getGravity() : getDefaultGravity();
    }

    /**
     * Gets complete planet data at the given position.
     * Returns null if no custom data exists (use dimension defaults).
     */
    public PlanetData getData(BlockPos pos) {
        return planetData.get(pos.toLong());
    }

    /**
     * Sets complete planet data at the given position.
     */
    public void setData(BlockPos pos, PlanetData data) {
        planetData.put(pos.toLong(), data);
        isDirty = true;
        markDirty();
    }

    /**
     * Clears data in a region. Useful for Oxygen/Gravity distributor removal.
     */
    public void clearRegion(Iterable<BlockPos> positions) {
        for (BlockPos pos : positions) {
            planetData.remove(pos.toLong());
        }
        isDirty = true;
        markDirty();
    }

    /**
     * Clears a single position's data.
     */
    public void clearPosition(BlockPos pos) {
        if (planetData.remove(pos.toLong()) != null) {
            isDirty = true;
            markDirty();
        }
    }

    /**
     * Gets all positions with custom data (for debugging/admin tools).
     */
    public Map<Long, PlanetData> getAllData() {
        return new HashMap<>(planetData);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        planetData.clear();

        // Read as int array for efficient storage
        int[] dataArray = nbt.getIntArray("data");

        if (dataArray.length % 3 != 0) {
            throw new RuntimeException("Invalid planet data array length: " + dataArray.length);
        }

        for (int i = 0; i < dataArray.length; i += 3) {
            // Reconstruct long position from two ints
            int firstPos = dataArray[i];
            int secondPos = dataArray[i + 1];
            long posLong = ((long) firstPos << 32) | (secondPos & 0xFFFFFFFFL);

            // Unpack planet data from int
            int packedData = dataArray[i + 2];
            PlanetData data = PlanetData.unpack(packedData);

            planetData.put(posLong, data);
        }

        isDirty = false;
    }

    @Override
    @Nonnull
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        // Get dimension defaults
        boolean defaultOxygen = getDefaultOxygen();
        short defaultTemperature = getDefaultTemperature();
        float defaultGravity = getDefaultGravity();

        // Count how many entries are non-default
        int nonDefaultCount = 0;
        for (PlanetData data : planetData.values()) {
            if (data.hasOxygen() != defaultOxygen ||
                data.getTemperature() != defaultTemperature ||
                data.getGravity() != defaultGravity) {
                nonDefaultCount++;
            }
        }

        // Create int array (3 ints per entry: posHigh, posLow, packedData)
        int[] dataArray = new int[nonDefaultCount * 3];
        int index = 0;

        for (Map.Entry<Long, PlanetData> entry : planetData.entrySet()) {
            PlanetData data = entry.getValue();

            // Only save positions that differ from dimension defaults
            if (data.hasOxygen() != defaultOxygen ||
                data.getTemperature() != defaultTemperature ||
                data.getGravity() != defaultGravity) {

                long pos = entry.getKey();
                dataArray[index++] = (int) (pos >> 32);
                dataArray[index++] = (int) pos;
                dataArray[index++] = data.pack();
            }
        }

        nbt.setIntArray("data", dataArray);

        isDirty = false;
        return nbt;
    }

    /**
     * Gets the dimension's default oxygen setting.
     */
    private boolean getDefaultOxygen() {
        if (world.provider instanceof earth.terrarium.adastra.common.world.AdAstraWorldProvider) {
            earth.terrarium.adastra.common.world.AdAstraWorldProvider provider =
                (earth.terrarium.adastra.common.world.AdAstraWorldProvider) world.provider;
            return provider.hasOxygen();
        }
        // Overworld has oxygen by default
        return world.provider.getDimension() == 0;
    }

    /**
     * Gets the dimension's default temperature.
     */
    private short getDefaultTemperature() {
        if (world.provider instanceof earth.terrarium.adastra.common.world.AdAstraWorldProvider) {
            earth.terrarium.adastra.common.world.AdAstraWorldProvider provider =
                (earth.terrarium.adastra.common.world.AdAstraWorldProvider) world.provider;
            return provider.getTemperature();
        }
        // Earth default temperature: 20°C
        return 20;
    }

    /**
     * Gets the dimension's default gravity multiplier.
     */
    private float getDefaultGravity() {
        if (world.provider instanceof earth.terrarium.adastra.common.world.AdAstraWorldProvider) {
            earth.terrarium.adastra.common.world.AdAstraWorldProvider provider =
                (earth.terrarium.adastra.common.world.AdAstraWorldProvider) world.provider;
            return provider.getGravity();
        }
        // Earth default gravity: 1.0
        return 1.0f;
    }

    public boolean isDirty() {
        return isDirty;
    }
}
