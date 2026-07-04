package earth.terrarium.adastra.common.systems;

import earth.terrarium.adastra.api.systems.OxygenApi;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;

import java.util.Collection;

/**
 * ServiceLoader implementation of {@link OxygenApi} for the 1.12.2 port.
 * Delegates all logic to the existing {@link OxygenSystem}, {@link OxygenUtils},
 * and {@link OxygenSystemExtended} helpers.
 */
public class OxygenApiImpl implements OxygenApi {

    @Override
    public boolean hasOxygen(World world) {
        return OxygenSystemExtended.hasOxygenInDimension(world);
    }

    @Override
    public boolean hasOxygen(int dimensionId) {
        World world = DimensionManager.getWorld(dimensionId);
        return world != null && hasOxygen(world);
    }

    @Override
    public boolean hasOxygen(World world, BlockPos pos) {
        return OxygenUtils.hasOxygenAtPosition(world, pos);
    }

    @Override
    public boolean hasOxygen(Entity entity) {
        if (entity == null || entity.world == null) {
            return true;
        }
        return OxygenUtils.hasOxygenAtPosition(entity.world, entity.getPosition());
    }

    @Override
    public void setOxygen(World world, BlockPos pos, boolean oxygen) {
        OxygenSystemExtended.setOxygen(world, pos, oxygen);
    }

    @Override
    public void setOxygen(World world, Collection<BlockPos> positions, boolean oxygen) {
        OxygenSystemExtended.setOxygen(world, positions, oxygen);
    }

    @Override
    public void removeOxygen(World world, BlockPos pos) {
        OxygenSystemExtended.removeOxygen(world, pos);
    }

    @Override
    public void removeOxygen(World world, Collection<BlockPos> positions) {
        OxygenSystemExtended.removeOxygen(world, positions);
    }

    @Override
    public void entityTick(WorldServer world, EntityLivingBase entity) {
        if (entity == null || world == null || world.isRemote) {
            return;
        }

        if (entity instanceof EntityPlayer) {
            OxygenSystem.checkOxygen((EntityPlayer) entity, world);
        } else if (!hasOxygen(entity)) {
            entity.setAir(-80);
            if (entity.ticksExisted % 20 == 0) {
                entity.attackEntityFrom(OxygenSystem.OXYGEN_DEPRIVATION, 2.0f);
            }
        }
    }
}
