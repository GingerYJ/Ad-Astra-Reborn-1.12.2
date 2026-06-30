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
        return 30.0d;
    }

    @Override
    protected double getMobMovementSpeed() {
        return 0.23d;
    }

    @Override
    protected double getMobAttackDamage() {
        return 5.0d;
    }

    @Override
    protected double getMobFollowRange() {
        return 28.0d;
    }
}
