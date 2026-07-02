package earth.terrarium.adastra.common.entities.mob;

import earth.terrarium.adastra.common.entities.AdAstraPlaceholderMob;
import earth.terrarium.adastra.common.registry.ModItems;
import net.minecraft.world.World;

public class PygroBruteEntity extends AdAstraPlaceholderMob {

    public PygroBruteEntity(World world) {
        super(world);
        setSize(0.6f, 1.8f);
        isImmuneToFire = true;
    }

    @Override
    protected double getMobMaxHealth() {
        return 60.0d;
    }

    @Override
    protected double getMobMovementSpeed() {
        return 0.45d;
    }

    @Override
    protected double getMobAttackDamage() {
        return 9.0d;
    }

    @Override
    protected double getMobFollowRange() {
        return 36.0d;
    }

    @Override
    protected boolean canDespawnNaturally() {
        return false;
    }

    @Override
    protected void dropFewItems(boolean wasRecentlyHit, int lootingModifier) {
        super.dropFewItems(wasRecentlyHit, lootingModifier);
        // Drops calorite nuggets (1-3, +looting) - more than regular Pygro
        int count = 1 + rand.nextInt(3) + rand.nextInt(1 + lootingModifier);
        for (int i = 0; i < count; i++) {
            dropItem(ModItems.CALORITE_NUGGET, 1);
        }
    }
}
