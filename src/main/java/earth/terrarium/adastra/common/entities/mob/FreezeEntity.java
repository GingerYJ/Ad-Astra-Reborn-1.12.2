package earth.terrarium.adastra.common.entities.mob;

import earth.terrarium.adastra.common.entities.AdAstraPlaceholderMob;
import earth.terrarium.adastra.common.entities.projectile.IceChargeEntity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

/** A ranged ice monster matching the Freeze encounter. */
public class FreezeEntity extends AdAstraPlaceholderMob {

    public FreezeEntity(World world) {
        super(world);
        setSize(0.8F, 1.9F);
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        if (!world.isRemote && ticksExisted % 40 == 0) {
            EntityLivingBase target = getAttackTarget();
            if (target instanceof EntityPlayer && getDistanceSq(target) <= 20.0D * 20.0D) {
                IceChargeEntity charge = new IceChargeEntity(world, this);
                double dx = target.posX - posX;
                double dy = target.posY + target.getEyeHeight() - charge.posY;
                double dz = target.posZ - posZ;
                double horizontal = Math.sqrt(dx * dx + dz * dz);
                charge.shoot(dx, dy + horizontal * 0.2D, dz, 1.25F, 6.0F);
                world.spawnEntity(charge);
                playSound(SoundEvents.ENTITY_SNOWBALL_THROW, 1.0F, 0.5F);
            }
        }
    }

    @Override
    protected double getMobMaxHealth() {
        return 30.0D;
    }

    @Override
    protected double getMobMovementSpeed() {
        return 0.23D;
    }

    @Override
    protected double getMobAttackDamage() {
        return 6.0D;
    }

    @Override
    protected double getMobFollowRange() {
        return 48.0D;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_BLAZE_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_BLAZE_DEATH;
    }
}
