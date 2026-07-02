package earth.terrarium.adastra.common.entities.mob;

import earth.terrarium.adastra.common.entities.AdAstraPlaceholderMob;
import earth.terrarium.adastra.common.registry.ModItems;
import net.minecraft.world.World;

public class PygroEntity extends AdAstraPlaceholderMob {

    public PygroEntity(World world) {
        super(world);
        setSize(0.6f, 1.8f);
        isImmuneToFire = true;
    }

    @Override
    protected double getMobMaxHealth() {
        return 24.0d;
    }

    @Override
    protected double getMobMovementSpeed() {
        return 0.45d;
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
    protected boolean canDespawnNaturally() {
        return false;
    }

    @Override
    protected void dropFewItems(boolean wasRecentlyHit, int lootingModifier) {
        super.dropFewItems(wasRecentlyHit, lootingModifier);
        // Drops calorite nuggets (0-2, +looting)
        int count = rand.nextInt(3) + rand.nextInt(1 + lootingModifier);
        for (int i = 0; i < count; i++) {
            dropItem(ModItems.CALORITE_NUGGET, 1);
        }
    }
}
