package earth.terrarium.adastra.common.entities.mob;

import earth.terrarium.adastra.common.entities.AdAstraPlaceholderMob;
import net.minecraft.world.World;

public class MartianRaptorEntity extends AdAstraPlaceholderMob {

    public MartianRaptorEntity(World world) {
        super(world);
        setSize(0.75f, 2.0f);
    }

    @Override
    protected double getMobMaxHealth() {
        return 22.0d;
    }

    @Override
    protected double getMobMovementSpeed() {
        return 0.32d;
    }

    @Override
    protected double getMobAttackDamage() {
        return 5.0d;
    }

    @Override
    protected double getMobFollowRange() {
        return 36.0d;
    }

    @Override
    protected double getMeleeAttackSpeed() {
        return 1.15d;
    }
}
