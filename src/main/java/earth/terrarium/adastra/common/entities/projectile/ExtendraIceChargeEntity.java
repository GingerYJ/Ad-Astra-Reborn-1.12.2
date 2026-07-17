package earth.terrarium.adastra.common.entities.projectile;

import earth.terrarium.adastra.common.entities.AdAstraPlaceholderProjectile;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.PotionEffect;
import net.minecraft.init.MobEffects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

/** Ice Charge projectile used by Freeze and by the player item. */
public class ExtendraIceChargeEntity extends AdAstraPlaceholderProjectile {

    public ExtendraIceChargeEntity(World world) {
        super(world);
        setSize(0.35F, 0.35F);
    }

    public ExtendraIceChargeEntity(World world, EntityLivingBase thrower) {
        super(world, thrower);
        setSize(0.35F, 0.35F);
    }

    public ExtendraIceChargeEntity(World world, double x, double y, double z) {
        super(world, x, y, z);
        setSize(0.35F, 0.35F);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (world.isRemote) {
            world.spawnParticle(net.minecraft.util.EnumParticleTypes.SNOWBALL,
                posX - motionX, posY - motionY, posZ - motionZ, 0.0D, 0.0D, 0.0D);
        }
    }

    @Override
    protected void onImpact(RayTraceResult result) {
        if (result.entityHit instanceof EntityLivingBase && result.entityHit != getThrower()) {
            EntityLivingBase target = (EntityLivingBase) result.entityHit;
            target.attackEntityFrom(DamageSource.causeThrownDamage(this, getThrower()), 2.0F);
            target.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 200, 1));
        }
        if (!world.isRemote) {
            setDead();
        }
    }
}
