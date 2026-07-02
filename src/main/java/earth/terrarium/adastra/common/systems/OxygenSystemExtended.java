package earth.terrarium.adastra.common.systems;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import java.util.Collection;

/**
 * Extended oxygen system managing oxygen presence at block positions with PlanetDataStorage.
 */
public class OxygenSystemExtended {

    public static boolean hasOxygenInDimension(World world) {
        int dimension = world.provider.getDimension();

        switch (dimension) {
            case -1: // Nether
            case 1:  // End
                return false;
            case 0:  // Overworld
                return true;
            case -28: // Moon
            case -29: // Mars
            case -30: // Venus
            case -31: // Mercury
            case -32: // Glacio
                return false;
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
