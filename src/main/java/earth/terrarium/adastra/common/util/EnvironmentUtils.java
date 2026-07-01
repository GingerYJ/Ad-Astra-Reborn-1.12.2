package earth.terrarium.adastra.common.util;

import earth.terrarium.adastra.common.blocks.AdAstraMachineBlock;
import earth.terrarium.adastra.common.registry.ModBlocks;
import earth.terrarium.adastra.common.tile.OxygenDistributorTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public final class EnvironmentUtils {

    public static final int DEFAULT_ENVIRONMENT_SCAN_RADIUS = 16;
    public static final short EARTH_TEMPERATURE = 15;
    public static final short MIN_LIVEABLE_TEMPERATURE = -50;
    public static final short MAX_LIVEABLE_TEMPERATURE = 70;

    private EnvironmentUtils() {
    }

    public static boolean hasOxygen(Entity entity) {
        return entity != null && hasOxygen(entity.world, entity.getPosition(), DEFAULT_ENVIRONMENT_SCAN_RADIUS);
    }

    public static boolean hasOxygen(World world, BlockPos pos, int scanRadius) {
        if (world == null || pos == null) {
            return true;
        }
        if (worldProviderHasOxygen(world)) {
            return true;
        }
        return isCoveredByOxygenDistributor(world, pos, scanRadius);
    }

    public static boolean worldProviderHasOxygen(World world) {
        if (world == null || world.provider == null) {
            return true;
        }
        Object value = invokeNoArg(world.provider, "hasOxygen");
        return value instanceof Boolean ? (Boolean) value : true;
    }

    public static float getGravity(Entity entity) {
        return entity == null ? 9.80665F : getGravity(entity.world);
    }

    public static float getGravity(World world) {
        if (world == null || world.provider == null) {
            return 9.80665F;
        }
        Object value = invokeNoArg(world.provider, "getGravity");
        return value instanceof Number ? ((Number) value).floatValue() : 9.80665F;
    }

    public static short getTemperature(Entity entity) {
        return entity == null ? EARTH_TEMPERATURE : getTemperature(entity.world);
    }

    public static short getTemperature(World world) {
        if (world == null || world.provider == null) {
            return EARTH_TEMPERATURE;
        }
        Object value = invokeNoArg(world.provider, "getTemperature");
        return value instanceof Number ? ((Number) value).shortValue() : EARTH_TEMPERATURE;
    }

    public static boolean isTemperatureLiveable(Entity entity) {
        short temperature = getTemperature(entity);
        return temperature >= MIN_LIVEABLE_TEMPERATURE && temperature <= MAX_LIVEABLE_TEMPERATURE;
    }

    private static boolean isCoveredByOxygenDistributor(World world, BlockPos target, int scanRadius) {
        BlockPos min = target.add(-scanRadius, -scanRadius, -scanRadius);
        BlockPos max = target.add(scanRadius, scanRadius, scanRadius);

        for (BlockPos mutablePos : BlockPos.getAllInBoxMutable(min, max)) {
            if (!world.isBlockLoaded(mutablePos)) {
                continue;
            }
            IBlockState state = world.getBlockState(mutablePos);
            Block block = state.getBlock();
            if (block != ModBlocks.OXYGEN_DISTRIBUTOR || !isMachineLit(state)) {
                continue;
            }

            TileEntity tile = world.getTileEntity(mutablePos);
            if (tile instanceof OxygenDistributorTileEntity) {
                if (((OxygenDistributorTileEntity) tile).isProvidingOxygen(target)) {
                    return true;
                }
            } else {
                return true;
            }
        }
        return false;
    }

    private static boolean isMachineLit(IBlockState state) {
        return state.getPropertyKeys().contains(AdAstraMachineBlock.LIT) && state.getValue(AdAstraMachineBlock.LIT);
    }

    private static Object invokeNoArg(Object target, String methodName) {
        try {
            Method method = target.getClass().getMethod(methodName);
            return method.invoke(target);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | SecurityException e) {
            return null;
        }
    }
}
