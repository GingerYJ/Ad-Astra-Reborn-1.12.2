package earth.terrarium.adastra.common.entities;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class AdAstraPlaceholderProjectile extends EntityThrowable {

    public AdAstraPlaceholderProjectile(World world) {
        super(world);
    }

    public AdAstraPlaceholderProjectile(World world, EntityLivingBase thrower) {
        super(world, thrower);
    }

    public AdAstraPlaceholderProjectile(World world, double x, double y, double z) {
        super(world, x, y, z);
    }

    @Override
    protected void onImpact(RayTraceResult result) {
        if (!world.isRemote) {
            setDead();
        }
    }
}
