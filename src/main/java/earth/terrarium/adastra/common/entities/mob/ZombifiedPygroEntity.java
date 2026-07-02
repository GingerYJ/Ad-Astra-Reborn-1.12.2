package earth.terrarium.adastra.common.entities.mob;

import earth.terrarium.adastra.common.entities.AdAstraPlaceholderMob;
import earth.terrarium.adastra.common.registry.ModItems;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.init.Items;
import net.minecraft.world.World;

public class ZombifiedPygroEntity extends AdAstraPlaceholderMob {

    public ZombifiedPygroEntity(World world) {
        super(world);
        setSize(0.6f, 1.8f);
        isImmuneToFire = true;
    }

    @Override
    protected double getMobMaxHealth() {
        return 28.0d;
    }

    @Override
    protected double getMobMovementSpeed() {
        return 0.26d;
    }

    @Override
    protected double getMobAttackDamage() {
        return 8.0d;
    }

    @Override
    protected double getMobFollowRange() {
        return 32.0d;
    }

    @Override
    public EnumCreatureAttribute getCreatureAttribute() {
        return EnumCreatureAttribute.UNDEAD;
    }

    @Override
    protected void dropFewItems(boolean wasRecentlyHit, int lootingModifier) {
        super.dropFewItems(wasRecentlyHit, lootingModifier);
        // Drops rotten flesh (0-1, +looting) and rarely calorite nuggets
        int fleshCount = rand.nextInt(2) + rand.nextInt(1 + lootingModifier);
        for (int i = 0; i < fleshCount; i++) {
            dropItem(Items.ROTTEN_FLESH, 1);
        }

        if (rand.nextInt(3) == 0) { // 33% chance
            dropItem(ModItems.CALORITE_NUGGET, 1);
        }
    }
}
