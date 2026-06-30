package earth.terrarium.adastra.common.entities;

import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class AdAstraPlaceholderProjectile extends EntityThrowable {

    public AdAstraPlaceholderProjectile(World world) {
        super(world);
    }

    @Override
    protected void onImpact(RayTraceResult result) {
        if (!world.isRemote) {
            setDead();
        }
    }
}
