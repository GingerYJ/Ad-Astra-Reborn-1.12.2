package earth.terrarium.adastra.common.systems;

import earth.terrarium.adastra.api.systems.PlanetData;
import earth.terrarium.adastra.common.registry.ModDimensions;
import earth.terrarium.adastra.common.world.AdAstraWorldProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import java.util.Collection;

/**
 * Extended oxygen system managing oxygen presence at block positions with PlanetDataStorage.
 */
public class OxygenSystemExtended {

    public static boolean hasOxygenInDimension(World world) {
        if (world == null || world.provider == null) {
            return true;
        }
        if (world.provider instanceof AdAstraWorldProvider) {
            return ((AdAstraWorldProvider) world.provider).hasOxygen();
        }

        int dimension = world.provider.getDimension();

        switch (dimension) {
            case -1: // Nether
            case 1:  // End
                return false;
            case 0:  // Overworld
                return true;
            case ModDimensions.MOON_ID:
            case ModDimensions.MARS_ID:
            case ModDimensions.MERCURY_ID:
            case ModDimensions.VENUS_ID:
            case ModDimensions.EARTH_ORBIT_ID:
            case ModDimensions.MOON_ORBIT_ID:
            case ModDimensions.MARS_ORBIT_ID:
            case ModDimensions.MERCURY_ORBIT_ID:
            case ModDimensions.VENUS_ORBIT_ID:
            case ModDimensions.GLACIO_ORBIT_ID:
                return false;
            case ModDimensions.GLACIO_ID:
                return true;
            default:
                return true;
        }
    }

    public static boolean hasOxygenAtPos(World world, BlockPos pos) {
        if (world.isRemote) {
            return hasOxygenInDimension(world);
        }

        if (!(world instanceof WorldServer)) {
            return hasOxygenInDimension(world);
        }

        PlanetDataStorage storage = PlanetDataStorage.get(world);
        Boolean oxygen = storage.getOxygenOverride(pos);
        if (oxygen == null) {
            return hasOxygenInDimension(world);
        }
        return oxygen;
    }

    public static void setOxygen(World world, BlockPos pos, boolean oxygen) {
        if (world.isRemote || !(world instanceof WorldServer)) {
            return;
        }

        PlanetDataStorage storage = PlanetDataStorage.get(world);
        if (setOxygenWithoutMarking(world, storage, pos, oxygen)) {
            storage.markChanged();
        }
    }

    public static void setOxygen(World world, Collection<BlockPos> positions, boolean oxygen) {
        if (world.isRemote || !(world instanceof WorldServer)) {
            return;
        }

        PlanetDataStorage storage = PlanetDataStorage.get(world);
        boolean changed = false;
        for (BlockPos pos : positions) {
            changed |= setOxygenWithoutMarking(world, storage, pos, oxygen);
        }
        if (changed) {
            storage.markChanged();
        }
    }

    private static boolean setOxygenWithoutMarking(World world, PlanetDataStorage storage,
                                                    BlockPos pos, boolean oxygen) {
        Boolean previous = storage.getOxygenOverride(pos);
        if (oxygen == hasOxygenInDimension(world)) {
            return storage.clearOxygenOverrideWithoutMarking(pos);
        }
        return previous == null || previous != oxygen
            ? storage.setOxygenOverrideWithoutMarking(pos, oxygen)
            : false;
    }

    public static void removeOxygen(World world, BlockPos pos) {
        if (world.isRemote || !(world instanceof WorldServer)) {
            return;
        }
        PlanetDataStorage storage = PlanetDataStorage.get(world);
        if (storage.clearOxygenOverrideWithoutMarking(pos)) {
            storage.markChanged();
        }
    }

    public static void removeOxygen(World world, Collection<BlockPos> positions) {
        if (world.isRemote || !(world instanceof WorldServer)) {
            return;
        }
        PlanetDataStorage storage = PlanetDataStorage.get(world);
        boolean changed = false;
        for (BlockPos pos : positions) {
            changed |= storage.clearOxygenOverrideWithoutMarking(pos);
        }
        if (changed) {
            storage.markChanged();
        }
    }
}
