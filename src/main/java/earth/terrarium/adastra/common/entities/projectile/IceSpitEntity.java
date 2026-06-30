package earth.terrarium.adastra.common.entities.projectile;

import earth.terrarium.adastra.common.entities.AdAstraPlaceholderProjectile;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class IceSpitEntity extends AdAstraPlaceholderProjectile {

    private static final float DAMAGE = 4.0f;

    public IceSpitEntity(World world) {
        super(world);
        setSize(0.5f, 0.5f);
    }

    public IceSpitEntity(World world, EntityLivingBase thrower) {
        super(world, thrower);
        setSize(0.5f, 0.5f);
    }

    public IceSpitEntity(World world, double x, double y, double z) {
        super(world, x, y, z);
        setSize(0.5f, 0.5f);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        world.spawnParticle(EnumParticleTypes.SPIT, posX - motionX, posY - motionY, posZ - motionZ, 0.0d, 0.001d, 0.0d);
        world.spawnParticle(EnumParticleTypes.SNOWBALL, posX - motionX, posY - motionY, posZ - motionZ, 0.0d, 0.001d, 0.0d);
    }

    @Override
    protected void onImpact(RayTraceResult result) {
        if (result.entityHit != null) {
            result.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, getThrower()), DAMAGE);
        }
        if (!world.isRemote) {
            world.setEntityState(this, (byte) 3);
            setDead();
        }
    }
}
