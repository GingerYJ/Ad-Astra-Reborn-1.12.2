package earth.terrarium.adastra.common.entities.mob;

import earth.terrarium.adastra.common.entities.AdAstraPlaceholderMob;
import earth.terrarium.adastra.common.registry.ModDimensions;
import earth.terrarium.adastra.common.registry.ModItems;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class MoglerEntity extends AdAstraPlaceholderMob {

    private static final int CONVERSION_TIME = 300;

    private int attackCooldown;
    private int conversionTime;

    public MoglerEntity(World world) {
        super(world);
        setSize(1.4f, 1.4f);
        isImmuneToFire = true;
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        if (attackCooldown > 0) {
            attackCooldown--;
        }
        tickConversion();
    }

    @Override
    public boolean attackEntityAsMob(Entity target) {
        if (attackCooldown > 0) {
            return false;
        }

        boolean result = super.attackEntityAsMob(target);
        if (result && target instanceof EntityLivingBase) {
            attackCooldown = 10;

            double knockbackStrength = 1.0;
            double dx = posX - target.posX;
            double dz = posZ - target.posZ;
            double magnitude = MathHelper.sqrt(dx * dx + dz * dz);

            if (magnitude > 0.0) {
                target.motionX += (dx / magnitude) * knockbackStrength;
                target.motionY += 0.4;
                target.motionZ += (dz / magnitude) * knockbackStrength;
                target.velocityChanged = true;
            }

            playSound(SoundEvents.ENTITY_ZOMBIE_ATTACK_IRON_DOOR, 1.0f, 0.8f);
        }
        return result;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_PIG_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_PIG_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_PIG_DEATH;
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setInteger("ConversionTime", conversionTime);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        conversionTime = compound.getInteger("ConversionTime");
    }

    public boolean isConverting() {
        return conversionTime > 0;
    }

    @Override
    protected double getMobMaxHealth() {
        return 50.0d;
    }

    @Override
    protected double getMobMovementSpeed() {
        return 0.40d;
    }

    @Override
    protected double getMobAttackDamage() {
        return 8.0d;
    }

    @Override
    protected double getMobFollowRange() {
        return 28.0d;
    }

    @Override
    protected double getMobKnockbackResistance() {
        return 0.60d;
    }

    @Override
    protected boolean canDespawnNaturally() {
        return false;
    }

    @Override
    protected void dropFewItems(boolean wasRecentlyHit, int lootingModifier) {
        super.dropFewItems(wasRecentlyHit, lootingModifier);
        int caloriteCount = 1 + rand.nextInt(3) + rand.nextInt(1 + lootingModifier);
        for (int i = 0; i < caloriteCount; i++) {
            dropItem(ModItems.RAW_CALORITE, 1);
        }

        int leatherCount = rand.nextInt(2) + rand.nextInt(1 + lootingModifier);
        for (int i = 0; i < leatherCount; i++) {
            dropItem(Items.LEATHER, 1);
        }
    }

    private void tickConversion() {
        if (world.isRemote) {
            return;
        }
        if (isPiglinSafeDimension()) {
            conversionTime = 0;
            return;
        }
        if (++conversionTime >= CONVERSION_TIME) {
            finishConversion();
        }
    }

    private boolean isPiglinSafeDimension() {
        if (world.provider == null) {
            return false;
        }
        int dimension = world.provider.getDimension();
        return dimension == -1 || dimension == ModDimensions.MERCURY_ID || dimension == ModDimensions.VENUS_ID;
    }

    private void finishConversion() {
        ZombifiedMoglerEntity zombified = new ZombifiedMoglerEntity(world);
        zombified.copyLocationAndAnglesFrom(this);
        if (hasCustomName()) {
            zombified.setCustomNameTag(getCustomNameTag());
        }
        zombified.addPotionEffect(new PotionEffect(MobEffects.NAUSEA, 200, 0));
        world.spawnEntity(zombified);
        setDead();
    }
}
