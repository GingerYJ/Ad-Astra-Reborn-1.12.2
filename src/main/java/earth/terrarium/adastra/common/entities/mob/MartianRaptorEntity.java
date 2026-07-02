package earth.terrarium.adastra.common.entities.mob;

import earth.terrarium.adastra.common.entities.AdAstraPlaceholderMob;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.ai.EntityAILeapAtTarget;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

public class MartianRaptorEntity extends AdAstraPlaceholderMob {

    private int movementCooldownTicks;

    public MartianRaptorEntity(World world) {
        super(world);
        setSize(0.75f, 2.0f);
    }

    @Override
    protected void initEntityAI() {
        super.initEntityAI();
        // Add leap attack behavior (after swimming, before melee)
        tasks.addTask(1, new EntityAILeapAtTarget(this, 0.2f));
    }

    @Override
    public boolean attackEntityAsMob(Entity target) {
        boolean result = super.attackEntityAsMob(target);
        if (result) {
            this.movementCooldownTicks = 10;
            this.world.setEntityState(this, (byte) 4);
        }
        return result;
    }

    @Override
    public void handleStatusUpdate(byte status) {
        if (status == 4) {
            this.movementCooldownTicks = 10;
        } else {
            super.handleStatusUpdate(status);
        }
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        if (movementCooldownTicks > 0) {
            movementCooldownTicks--;
        }
    }

    public int getMovementCooldownTicks() {
        return this.movementCooldownTicks;
    }

    @Override
    protected double getMobMaxHealth() {
        return 26.0d; // 1.20.x: 26
    }

    @Override
    protected double getMobMovementSpeed() {
        return 0.30d; // 1.20.x: 0.3
    }

    @Override
    protected double getMobAttackDamage() {
        return 8.0d; // 1.20.x: 8
    }

    @Override
    protected double getMobFollowRange() {
        return 35.0d;
    }

    @Override
    protected double getMeleeAttackSpeed() {
        return 1.20d;
    }

    @Override
    public EnumCreatureAttribute getCreatureAttribute() {
        return EnumCreatureAttribute.UNDEAD;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_HUSK_HURT; // 1.12 fallback for strider
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_HUSK_DEATH; // 1.12 fallback for strider
    }

    @Override
    protected void dropFewItems(boolean wasRecentlyHit, int lootingModifier) {
        super.dropFewItems(wasRecentlyHit, lootingModifier);
        // Drops raw ostrum (0-2, +looting)
        int count = rand.nextInt(3) + rand.nextInt(1 + lootingModifier);
        for (int i = 0; i < count; i++) {
            dropItem(earth.terrarium.adastra.common.registry.ModItems.RAW_OSTRUM, 1);
        }
    }
}
