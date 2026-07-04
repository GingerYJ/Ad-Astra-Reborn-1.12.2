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
            case ModDimensions.NETHER_ORBIT_ID:
            case ModDimensions.END_ORBIT_ID:
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
        PlanetData data = storage.getData(pos);

        if (data == null) {
            return hasOxygenInDimension(world);
        }

        return data.oxygen();
    }

    public static void setOxygen(World world, BlockPos pos, boolean oxygen) {
        if (world.isRemote || !(world instanceof WorldServer)) {
            return;
        }

        PlanetDataStorage storage = PlanetDataStorage.get(world);
        PlanetData data = storage.getData(pos);

        if (data == null) {
            short temperature = TemperatureSystem.getTemperatureAtPos(world, pos);
            float gravity = GravitySystem.getGravityAtPos(world, pos);
            data = new PlanetData(oxygen, temperature, gravity);
        } else {
            data.setOxygen(oxygen);
        }

        storage.setData(pos, data);
    }

    public static void setOxygen(World world, Collection<BlockPos> positions, boolean oxygen) {
        if (world.isRemote || !(world instanceof WorldServer)) {
            return;
        }

        for (BlockPos pos : positions) {
            setOxygen(world, pos, oxygen);
        }
    }

    public static void removeOxygen(World world, BlockPos pos) {
        setOxygen(world, pos, hasOxygenInDimension(world));
    }

    public static void removeOxygen(World world, Collection<BlockPos> positions) {
        setOxygen(world, positions, hasOxygenInDimension(world));
    }
}
