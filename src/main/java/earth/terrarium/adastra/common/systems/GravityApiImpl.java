package earth.terrarium.adastra.common.systems;

import earth.terrarium.adastra.api.systems.GravityApi;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;

import java.util.Collection;

/**
 * ServiceLoader implementation of {@link GravityApi} for the 1.12.2 port.
 * Delegates all query and mutation operations to the existing {@link GravitySystem}.
 */
public class GravityApiImpl implements GravityApi {

    @Override
    public float getGravity(World world) {
        return GravitySystem.getGravityInDimension(world);
    }

    @Override
    public float getGravity(int dimensionId) {
        World world = DimensionManager.getWorld(dimensionId);
        return world != null ? getGravity(world) : 1.0f;
    }

    @Override
    public float getGravity(World world, BlockPos pos) {
        return GravitySystem.getGravityAtPos(world, pos);
    }

    @Override
    public float getGravity(Entity entity) {
        if (entity == null) {
            return 1.0f;
        }
        return GravitySystem.getGravityForEntity(entity);
    }

    @Override
    public void setGravity(World world, BlockPos pos, float gravity) {
        GravitySystem.setGravity(world, pos, gravity);
    }

    @Override
    public void setGravity(World world, Collection<BlockPos> positions, float gravity) {
        GravitySystem.setGravity(world, positions, gravity);
    }

    @Override
    public void removeGravity(World world, BlockPos pos) {
        GravitySystem.removeGravity(world, pos);
    }

    @Override
    public void removeGravity(World world, Collection<BlockPos> positions) {
        GravitySystem.removeGravity(world, positions);
    }

    @Override
    public void entityTick(World world, EntityLivingBase entity, Vec3d travelVector, BlockPos movementAffectingPos) {
        // Gravity application is handled by GravitySystem's Forge event handlers in this port.
        // This method is retained for API compatibility.
    }
}
