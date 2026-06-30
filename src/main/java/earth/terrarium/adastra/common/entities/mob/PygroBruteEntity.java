package earth.terrarium.adastra.common.entities.mob;

import earth.terrarium.adastra.common.entities.AdAstraPlaceholderMob;
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
}
