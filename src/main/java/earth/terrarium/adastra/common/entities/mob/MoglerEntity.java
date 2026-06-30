package earth.terrarium.adastra.common.entities.mob;

import earth.terrarium.adastra.common.entities.AdAstraPlaceholderMob;
import net.minecraft.world.World;

public class MoglerEntity extends AdAstraPlaceholderMob {

    public MoglerEntity(World world) {
        super(world);
        setSize(1.4f, 1.4f);
        isImmuneToFire = true;
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
}
