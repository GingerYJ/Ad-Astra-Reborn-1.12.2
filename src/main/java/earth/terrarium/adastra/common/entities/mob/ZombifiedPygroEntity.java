package earth.terrarium.adastra.common.entities.mob;

import earth.terrarium.adastra.common.entities.AdAstraPlaceholderMob;
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
        return 0.25d;
    }

    @Override
    protected double getMobAttackDamage() {
        return 5.0d;
    }

    @Override
    protected double getMobFollowRange() {
        return 32.0d;
    }
}
