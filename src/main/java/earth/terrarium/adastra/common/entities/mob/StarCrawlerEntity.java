package earth.terrarium.adastra.common.entities.mob;

import earth.terrarium.adastra.common.entities.AdAstraPlaceholderMob;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

public class StarCrawlerEntity extends AdAstraPlaceholderMob {

    public StarCrawlerEntity(World world) {
        super(world);
        setSize(1.3f, 1.0f);
    }

    @Override
    protected double getMobMaxHealth() {
        return 40.0d;
    }

    @Override
    protected double getMobMovementSpeed() {
        return 0.40d;
    }

    @Override
    protected double getMobAttackDamage() {
        return 9.0d;
    }

    @Override
    protected double getMobFollowRange() {
        return 30.0d;
    }

    @Override
    protected double getMeleeAttackSpeed() {
        return 0.8d;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_SPIDER_HURT; // 1.12 fallback for turtle
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_SPIDER_DEATH; // 1.12 fallback for turtle
    }

    @Override
    protected void dropFewItems(boolean wasRecentlyHit, int lootingModifier) {
        super.dropFewItems(wasRecentlyHit, lootingModifier);
        // Drops nothing (hostile mob with no loot table drops in 1.20)
    }
}
