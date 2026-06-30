package earth.terrarium.adastra.common.entities;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public class AdAstraPlaceholderMob extends EntityCreature {

    public AdAstraPlaceholderMob(World world) {
        super(world);
    }

    @Override
    protected void initEntityAI() {
        tasks.addTask(0, new EntityAISwimming(this));

        if (usesMeleeAttack()) {
            tasks.addTask(2, new EntityAIAttackMelee(this, getMeleeAttackSpeed(), false));
        }

        tasks.addTask(6, new EntityAIWander(this, getWanderSpeed()));
        tasks.addTask(7, new EntityAIWatchClosest(this, EntityPlayer.class, getWatchDistance()));
        tasks.addTask(8, new EntityAILookIdle(this));

        if (isHostileMob()) {
            targetTasks.addTask(1, new EntityAIHurtByTarget(this, true));
            targetTasks.addTask(2, new EntityAINearestAttackableTarget<EntityPlayer>(this, EntityPlayer.class, true));
        } else if (isNeutralMob()) {
            targetTasks.addTask(1, new EntityAIHurtByTarget(this, true));
        }
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        setAttribute(SharedMonsterAttributes.MAX_HEALTH, getMobMaxHealth());
        setAttribute(SharedMonsterAttributes.MOVEMENT_SPEED, getMobMovementSpeed());
        setAttribute(SharedMonsterAttributes.FOLLOW_RANGE, getMobFollowRange());
        setAttribute(SharedMonsterAttributes.ATTACK_DAMAGE, getMobAttackDamage());
        setAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE, getMobKnockbackResistance());
    }

    @Override
    public boolean attackEntityAsMob(net.minecraft.entity.Entity entity) {
        IAttributeInstance attackDamage = getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
        float damage = attackDamage == null ? 0.0f : (float) attackDamage.getAttributeValue();
        return damage > 0.0f && entity.attackEntityFrom(DamageSource.causeMobDamage(this), damage);
    }

    @Override
    protected boolean canDespawn() {
        return canDespawnNaturally();
    }

    protected void setAttribute(IAttribute attribute, double value) {
        IAttributeInstance instance = getEntityAttribute(attribute);
        if (instance == null) {
            instance = getAttributeMap().registerAttribute(attribute);
        }

        instance.setBaseValue(value);
    }

    protected boolean isHostileMob() {
        return true;
    }

    protected boolean isNeutralMob() {
        return false;
    }

    protected boolean usesMeleeAttack() {
        return isHostileMob() || isNeutralMob();
    }

    protected double getMobMaxHealth() {
        return 20.0d;
    }

    protected double getMobMovementSpeed() {
        return 0.25d;
    }

    protected double getMobAttackDamage() {
        return 3.0d;
    }

    protected double getMobKnockbackResistance() {
        return 0.0d;
    }

    protected double getMobFollowRange() {
        return isHostileMob() ? 32.0d : 16.0d;
    }

    protected double getMeleeAttackSpeed() {
        return 1.0d;
    }

    protected double getWanderSpeed() {
        return 0.8d;
    }

    protected float getWatchDistance() {
        return 8.0f;
    }

    protected boolean canDespawnNaturally() {
        return true;
    }
}
