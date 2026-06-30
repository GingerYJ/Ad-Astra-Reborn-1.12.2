package earth.terrarium.adastra.common.entities.mob;

import earth.terrarium.adastra.common.entities.AdAstraPlaceholderMob;
import net.minecraft.world.World;

public class PygroEntity extends AdAstraPlaceholderMob {

    public PygroEntity(World world) {
        super(world);
        setSize(0.6f, 1.8f);
    }

    @Override
    protected double getMobMaxHealth() {
        return 24.0d;
    }

    @Override
    protected double getMobMovementSpeed() {
        return 0.26d;
    }

    @Override
    protected double getMobAttackDamage() {
        return 4.0d;
    }

    @Override
    protected double getMobFollowRange() {
        return 32.0d;
    }
}
