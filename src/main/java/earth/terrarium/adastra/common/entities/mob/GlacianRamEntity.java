package earth.terrarium.adastra.common.entities.mob;

import earth.terrarium.adastra.common.entities.AdAstraPlaceholderMob;
import net.minecraft.world.World;

public class GlacianRamEntity extends AdAstraPlaceholderMob {

    public GlacianRamEntity(World world) {
        super(world);
        setSize(0.9f, 1.3f);
    }

    @Override
    protected boolean isHostileMob() {
        return false;
    }

    @Override
    protected boolean isNeutralMob() {
        return true;
    }

    @Override
    protected double getMobMaxHealth() {
        return 24.0d;
    }

    @Override
    protected double getMobMovementSpeed() {
        return 0.25d;
    }

    @Override
    protected double getMobAttackDamage() {
        return 3.0d;
    }

    @Override
    protected double getMobFollowRange() {
        return 20.0d;
    }
}
