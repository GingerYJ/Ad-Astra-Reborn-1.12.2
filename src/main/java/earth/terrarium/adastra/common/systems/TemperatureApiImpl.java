package earth.terrarium.adastra.common.systems;

import earth.terrarium.adastra.api.systems.TemperatureApi;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;

import java.util.Collection;

/**
 * ServiceLoader implementation of {@link TemperatureApi} for the 1.12.2 port.
 * Delegates all logic to the existing {@link TemperatureSystem}.
 */
public class TemperatureApiImpl implements TemperatureApi {

    @Override
    public short getTemperature(World world) {
        return TemperatureSystem.getTemperatureInDimension(world);
    }

    @Override
    public short getTemperature(int dimensionId) {
        World world = DimensionManager.getWorld(dimensionId);
        return world != null ? getTemperature(world) : TemperatureSystem.EARTH_TEMPERATURE;
    }

    @Override
    public short getTemperature(World world, BlockPos pos) {
        return TemperatureSystem.getTemperatureAtPos(world, pos);
    }

    @Override
    public short getTemperature(Entity entity) {
        if (entity == null || entity.world == null) {
            return TemperatureSystem.EARTH_TEMPERATURE;
        }
        return TemperatureSystem.getTemperatureAtPos(entity.world, entity.getPosition());
    }

    @Override
    public void setTemperature(World world, BlockPos pos, short temperature) {
        TemperatureSystem.setTemperature(world, pos, temperature);
    }

    @Override
    public void setTemperature(World world, Collection<BlockPos> positions, short temperature) {
        TemperatureSystem.setTemperature(world, positions, temperature);
    }

    @Override
    public void removeTemperature(World world, BlockPos pos) {
        TemperatureSystem.removeTemperature(world, pos);
    }

    @Override
    public void removeTemperature(World world, Collection<BlockPos> positions) {
        TemperatureSystem.removeTemperature(world, positions);
    }

    @Override
    public boolean isLiveable(World world, BlockPos pos) {
        return TemperatureSystem.isLiveable(world, pos);
    }

    @Override
    public boolean isHot(World world, BlockPos pos) {
        return TemperatureSystem.isHot(world, pos);
    }

    @Override
    public boolean isCold(World world, BlockPos pos) {
        return TemperatureSystem.isCold(world, pos);
    }

    @Override
    public void entityTick(WorldServer world, EntityLivingBase entity) {
        TemperatureSystem.applyTemperatureDamage(entity);
    }
}
