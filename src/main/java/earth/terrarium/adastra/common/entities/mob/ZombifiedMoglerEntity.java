package earth.terrarium.adastra.common.entities.mob;

import earth.terrarium.adastra.common.entities.AdAstraPlaceholderMob;
import earth.terrarium.adastra.common.registry.ModItems;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.init.Items;
import net.minecraft.world.World;

public class ZombifiedMoglerEntity extends AdAstraPlaceholderMob {

    public ZombifiedMoglerEntity(World world) {
        super(world);
        setSize(1.4f, 1.4f);
        isImmuneToFire = true;
    }

    @Override
    protected double getMobMaxHealth() {
        return 40.0d;
    }

    @Override
    protected double getMobMovementSpeed() {
        return 0.30d;
    }

    @Override
    protected double getMobAttackDamage() {
        return 6.0d;
    }

    @Override
    protected double getMobFollowRange() {
        return 32.0d;
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
    public EnumCreatureAttribute getCreatureAttribute() {
        return EnumCreatureAttribute.UNDEAD;
    }

    @Override
    protected void dropFewItems(boolean wasRecentlyHit, int lootingModifier) {
        super.dropFewItems(wasRecentlyHit, lootingModifier);
        // Drops rotten flesh (1-2, +looting) and rarely raw calorite
        int fleshCount = 1 + rand.nextInt(2) + rand.nextInt(1 + lootingModifier);
        for (int i = 0; i < fleshCount; i++) {
            dropItem(Items.ROTTEN_FLESH, 1);
        }

        if (rand.nextInt(3) == 0) { // 33% chance
            dropItem(ModItems.RAW_CALORITE, 1);
        }
    }
}
