package earth.terrarium.adastra.common.entities.mob;

import earth.terrarium.adastra.common.entities.AdAstraPlaceholderMob;
import net.minecraft.world.World;

public class LunarianEntity extends AdAstraPlaceholderMob {

    public LunarianEntity(World world) {
        super(world);
        setSize(0.75f, 2.5f);
    }

    @Override
    protected boolean isHostileMob() {
        return false;
    }

    @Override
    protected boolean usesMeleeAttack() {
        return false;
    }

    @Override
    protected double getMobMaxHealth() {
        return 20.0d;
    }

    @Override
    protected double getMobMovementSpeed() {
        return 0.50d;
    }

    @Override
    protected double getMobAttackDamage() {
        return 0.0d;
    }

    @Override
    protected double getMobFollowRange() {
        return 48.0d;
    }
}
