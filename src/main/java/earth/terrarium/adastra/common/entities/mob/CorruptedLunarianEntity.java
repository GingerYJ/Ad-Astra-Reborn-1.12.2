package earth.terrarium.adastra.common.entities.mob;

import earth.terrarium.adastra.common.entities.AdAstraPlaceholderMob;
import earth.terrarium.adastra.common.entities.projectile.IceSpitEntity;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.ai.EntityAIAttackRanged;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.monster.EntityGolem;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class CorruptedLunarianEntity extends AdAstraPlaceholderMob implements IRangedAttackMob {

    private static final double RANGED_ATTACK_SPEED = 1.25d;
    private static final int RANGED_ATTACK_INTERVAL = 20;
    private static final float RANGED_ATTACK_DISTANCE = 15.0f;

    public CorruptedLunarianEntity(World world) {
        super(world);
        setSize(0.6f, 2.4f);
    }

    @Override
    protected void initEntityAI() {
        super.initEntityAI();
        tasks.addTask(1, new EntityAIAttackRanged(this, RANGED_ATTACK_SPEED, RANGED_ATTACK_INTERVAL, RANGED_ATTACK_DISTANCE));
        targetTasks.addTask(3, new EntityAINearestAttackableTarget<EntityVillager>(this, EntityVillager.class, true));
        targetTasks.addTask(4, new EntityAINearestAttackableTarget<LunarianWanderingTraderEntity>(this, LunarianWanderingTraderEntity.class, true));
        targetTasks.addTask(5, new EntityAINearestAttackableTarget<EntityGolem>(this, EntityGolem.class, true));
    }

    @Override
    public void attackEntityWithRangedAttack(EntityLivingBase target, float distanceFactor) {
        IceSpitEntity projectile = new IceSpitEntity(world, this);
        double targetX = target.posX - posX;
        double targetY = target.getEntityBoundingBox().minY + target.height * 0.33d - projectile.posY - 1.1d;
        double targetZ = target.posZ - posZ;
        double horizontalDistance = MathHelper.sqrt(targetX * targetX + targetZ * targetZ);
        projectile.shoot(targetX, targetY + horizontalDistance * 0.2d, targetZ, 1.6f, 14 - world.getDifficulty().getId() * 4);
        projectile.setSilent(true);
        world.spawnEntity(projectile);
        world.playSound(null, posX, posY, posZ, SoundEvents.ENTITY_BLAZE_SHOOT, SoundCategory.PLAYERS, 1.0f, 1.0f);
    }

    @Override
    public void setSwingingArms(boolean swingingArms) {
    }

    @Override
    public EnumCreatureAttribute getCreatureAttribute() {
        return EnumCreatureAttribute.UNDEAD;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_ZOMBIE_HURT; // 1.12 fallback for pillager
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_ZOMBIE_DEATH; // 1.12 fallback for pillager
    }

    @Override
    protected double getMobMaxHealth() {
        return 20.0d; // 1.20.x: 20
    }

    @Override
    protected double getMobMovementSpeed() {
        return 0.30d; // 1.20.x: 0.3
    }

    @Override
    protected double getMobAttackDamage() {
        return 2.0d; // 1.20.x: 2
    }

    @Override
    protected double getMobFollowRange() {
        return 35.0d;
    }

    @Override
    protected void dropFewItems(boolean wasRecentlyHit, int lootingModifier) {
        super.dropFewItems(wasRecentlyHit, lootingModifier);
        // Drops ice_shard (0-2)
        int count = rand.nextInt(3); // 0-2
        for (int i = 0; i < count; i++) {
            dropItem(earth.terrarium.adastra.common.registry.ModItems.ICE_SHARD, 1);
        }
    }
}
