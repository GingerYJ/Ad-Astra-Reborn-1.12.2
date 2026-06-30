package earth.terrarium.adastra.common.entities.mob;

import earth.terrarium.adastra.common.entities.AdAstraPlaceholderMob;
import net.minecraft.world.World;

public class ZombifiedMoglerEntity extends AdAstraPlaceholderMob {

    public ZombifiedMoglerEntity(World world) {
        super(world);
        setSize(1.4f, 1.4f);
        isImmuneToFire = true;
    }

    @Override
    protected double getMobMaxHealth() {
        return 34.0d;
    }

    @Override
    protected double getMobMovementSpeed() {
        return 0.24d;
    }

    @Override
    protected double getMobAttackDamage() {
        return 6.0d;
    }

    @Override
    protected double getMobFollowRange() {
        return 32.0d;
    }
}
