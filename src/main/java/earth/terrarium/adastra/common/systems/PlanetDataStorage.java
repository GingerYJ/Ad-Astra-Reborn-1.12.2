package earth.terrarium.adastra.common.systems;

import earth.terrarium.adastra.api.systems.PlanetData;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.longs.Long2ByteOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;

import javax.annotation.Nonnull;

/**
 * WorldSavedData for storing planet environmental data per block position.
 * Tracks oxygen, temperature, and gravity overrides for specific locations.
 */
public class PlanetDataStorage extends WorldSavedData {

    private static final String DATA_NAME = "adastra_planet_data";
    private static final byte OXYGEN_OVERRIDE = 1;
    private static final byte TEMPERATURE_OVERRIDE = 1 << 1;
    private static final byte GRAVITY_OVERRIDE = 1 << 2;
    private static final byte ALL_OVERRIDES = OXYGEN_OVERRIDE | TEMPERATURE_OVERRIDE | GRAVITY_OVERRIDE;

    private final Long2ObjectOpenHashMap<PlanetData> planetData = new Long2ObjectOpenHashMap<>();
    private final Long2ByteOpenHashMap overrideMasks = new Long2ByteOpenHashMap();
    private World world;
    private boolean legacyDataNeedsMigration;

    public PlanetDataStorage(String name) {
        super(name);
    }

    private PlanetDataStorage(String name, World world) {
        this(name);
        attachWorld(world);
    }

    public PlanetDataStorage(World world) {
        this(DATA_NAME, world);
    }

    public static PlanetDataStorage get(World world) {
        if (world.isRemote || !(world instanceof WorldServer)) {
            throw new IllegalStateException("PlanetDataStorage can only be accessed on the server side");
        }

        MapStorage storage = world.getPerWorldStorage();
        PlanetDataStorage data = (PlanetDataStorage) storage.getOrLoadData(PlanetDataStorage.class, DATA_NAME);

        if (data == null) {
            data = new PlanetDataStorage(world);
            storage.setData(DATA_NAME, data);
        } else {
            data.attachWorld(world);
        }

        return data;
    }

    @Override
    public void readFromNBT(@Nonnull NBTTagCompound nbt) {
        planetData.clear();
        overrideMasks.clear();
        int[] data = nbt.getIntArray("Data");
        int[] masks = nbt.getIntArray("OverrideMasks");
        legacyDataNeedsMigration = data.length > 0 && masks.length == 0;

        if (data.length % 3 != 0) {
            throw new RuntimeException("Invalid planet data length: " + data.length);
        }

        int entryIndex = 0;
        for (int i = 0; i < data.length; i += 3, entryIndex++) {
            int firstPos = data[i];
            int secondPos = data[i + 1];
            long packedPos = ((long) firstPos << 32) | (secondPos & 0xFFFFFFFFL);
            this.planetData.put(packedPos, PlanetData.unpack(data[i + 2]));
            byte mask = entryIndex < masks.length ? (byte) masks[entryIndex] : ALL_OVERRIDES;
            this.overrideMasks.put(packedPos, (byte) (mask & ALL_OVERRIDES));
        }
    }

    @Override
    @Nonnull
    public NBTTagCompound writeToNBT(@Nonnull NBTTagCompound nbt) {
        IntArrayList dataArray = new IntArrayList(this.planetData.size() * 3);
        IntArrayList maskArray = new IntArrayList(this.planetData.size());

        for (Long2ObjectMap.Entry<PlanetData> entry : planetData.long2ObjectEntrySet()) {
            long packedPos = entry.getLongKey();
            byte mask = overrideMasks.get(packedPos);
            if (mask == 0) {
                continue;
            }
            dataArray.add((int) (packedPos >> 32));
            dataArray.add((int) packedPos);
            dataArray.add(entry.getValue().pack());
            maskArray.add(mask);
        }

        nbt.setIntArray("Data", dataArray.toIntArray());
        nbt.setIntArray("OverrideMasks", maskArray.toIntArray());
        return nbt;
    }

    public PlanetData getData(BlockPos pos) {
        return pos == null ? null : planetData.get(pos.toLong());
    }

    public void setData(BlockPos pos, PlanetData data) {
        if (pos != null && data != null && setDataWithoutMarking(pos, data, ALL_OVERRIDES)) {
            markDirty();
        }
    }

    public void removeData(BlockPos pos) {
        if (removeDataWithoutMarking(pos)) {
            markDirty();
        }
    }

    boolean removeDataWithoutMarking(BlockPos pos) {
        if (pos == null) {
            return false;
        }
        long packedPos = pos.toLong();
        overrideMasks.remove(packedPos);
        return planetData.remove(packedPos) != null;
    }

    Boolean getOxygenOverride(BlockPos pos) {
        PlanetData data = getOverrideData(pos, OXYGEN_OVERRIDE);
        return data == null ? null : data.oxygen();
    }

    Short getTemperatureOverride(BlockPos pos) {
        PlanetData data = getOverrideData(pos, TEMPERATURE_OVERRIDE);
        return data == null ? null : data.temperature();
    }

    Float getGravityOverride(BlockPos pos) {
        PlanetData data = getOverrideData(pos, GRAVITY_OVERRIDE);
        return data == null ? null : data.gravity();
    }

    boolean hasGravityOverride(BlockPos pos) {
        return hasOverride(pos, GRAVITY_OVERRIDE);
    }

    boolean setOxygenOverrideWithoutMarking(BlockPos pos, boolean oxygen) {
        PlanetData data = getOrCreateData(pos);
        return data != null
            && setDataWithoutMarking(
                pos,
                new PlanetData(oxygen, data.temperature(), data.gravity()),
                (byte) (getMask(pos) | OXYGEN_OVERRIDE));
    }

    boolean setTemperatureOverrideWithoutMarking(BlockPos pos, short temperature) {
        PlanetData data = getOrCreateData(pos);
        return data != null
            && setDataWithoutMarking(
                pos,
                new PlanetData(data.oxygen(), temperature, data.gravity()),
                (byte) (getMask(pos) | TEMPERATURE_OVERRIDE));
    }

    boolean setGravityOverrideWithoutMarking(BlockPos pos, float gravity) {
        PlanetData data = getOrCreateData(pos);
        return data != null
            && setDataWithoutMarking(
                pos,
                new PlanetData(data.oxygen(), data.temperature(), gravity),
                (byte) (getMask(pos) | GRAVITY_OVERRIDE));
    }

    boolean clearOxygenOverrideWithoutMarking(BlockPos pos) {
        return clearOverrideWithoutMarking(pos, OXYGEN_OVERRIDE);
    }

    boolean clearTemperatureOverrideWithoutMarking(BlockPos pos) {
        return clearOverrideWithoutMarking(pos, TEMPERATURE_OVERRIDE);
    }

    boolean clearGravityOverrideWithoutMarking(BlockPos pos) {
        return clearOverrideWithoutMarking(pos, GRAVITY_OVERRIDE);
    }

    private PlanetData getOverrideData(BlockPos pos, byte override) {
        if (pos == null || !hasOverride(pos, override)) {
            return null;
        }
        return planetData.get(pos.toLong());
    }

    private boolean hasOverride(BlockPos pos, byte override) {
        return pos != null && (overrideMasks.get(pos.toLong()) & override) != 0;
    }

    private byte getMask(BlockPos pos) {
        return pos == null ? 0 : overrideMasks.get(pos.toLong());
    }

    private PlanetData getOrCreateData(BlockPos pos) {
        if (pos == null || world == null) {
            return null;
        }
        PlanetData existing = planetData.get(pos.toLong());
        if (existing != null) {
            return existing;
        }
        return new PlanetData(
            OxygenSystemExtended.hasOxygenInDimension(world),
            TemperatureSystem.getTemperatureInDimension(world),
            GravitySystem.getGravityInDimension(world));
    }

    private boolean setDataWithoutMarking(BlockPos pos, PlanetData data, byte mask) {
        if (pos == null || data == null) {
            return false;
        }
        long packedPos = pos.toLong();
        byte normalizedMask = (byte) (mask & ALL_OVERRIDES);
        if (normalizedMask == 0) {
            return removeDataWithoutMarking(pos);
        }
        PlanetData previous = planetData.get(packedPos);
        byte previousMask = overrideMasks.get(packedPos);
        if (data.equals(previous) && previousMask == normalizedMask) {
            return false;
        }
        planetData.put(packedPos, data);
        overrideMasks.put(packedPos, normalizedMask);
        return true;
    }

    private boolean clearOverrideWithoutMarking(BlockPos pos, byte override) {
        if (pos == null) {
            return false;
        }
        long packedPos = pos.toLong();
        byte previousMask = overrideMasks.get(packedPos);
        if ((previousMask & override) == 0) {
            return false;
        }
        byte newMask = (byte) (previousMask & ~override);
        if (newMask == 0) {
            planetData.remove(packedPos);
            overrideMasks.remove(packedPos);
        } else {
            overrideMasks.put(packedPos, newMask);
        }
        return true;
    }

    private void attachWorld(World world) {
        if (world == null || this.world == world) {
            return;
        }
        this.world = world;
        migrateLegacyData();
    }

    private void migrateLegacyData() {
        if (!legacyDataNeedsMigration || world == null) {
            return;
        }
        boolean changed = false;
        long[] positions = planetData.keySet().toLongArray();
        for (long packedPos : positions) {
            PlanetData data = planetData.get(packedPos);
            BlockPos pos = BlockPos.fromLong(packedPos);
            byte mask = 0;
            if (data.oxygen() != OxygenSystemExtended.hasOxygenInDimension(world)) {
                mask |= OXYGEN_OVERRIDE;
            }
            if (data.temperature() != TemperatureSystem.getEnvironmentTemperature(world, pos)) {
                mask |= TEMPERATURE_OVERRIDE;
            }
            if (!sameStoredGravity(data.gravity(), GravitySystem.getGravityInDimension(world))) {
                mask |= GRAVITY_OVERRIDE;
            }
            if (mask == 0) {
                planetData.remove(packedPos);
                overrideMasks.remove(packedPos);
            } else {
                overrideMasks.put(packedPos, mask);
            }
            changed = true;
        }
        legacyDataNeedsMigration = false;
        if (changed) {
            markDirty();
        }
    }

    private static boolean sameStoredGravity(float first, float second) {
        return (int) (first * 100.0F) == (int) (second * 100.0F);
    }

    void markChanged() {
        markDirty();
    }

    public boolean hasData(BlockPos pos) {
        return pos != null && overrideMasks.get(pos.toLong()) != 0;
    }

    public void clear() {
        planetData.clear();
        overrideMasks.clear();
        markDirty();
    }
}
