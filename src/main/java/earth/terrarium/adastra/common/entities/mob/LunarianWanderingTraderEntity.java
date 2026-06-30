package earth.terrarium.adastra.common.entities.mob;

import earth.terrarium.adastra.common.entities.AdAstraPlaceholderMob;
import net.minecraft.world.World;

public class LunarianWanderingTraderEntity extends AdAstraPlaceholderMob {

    public LunarianWanderingTraderEntity(World world) {
        super(world);
        setSize(0.6f, 1.95f);
        isImmuneToFire = true;
    }

    @Override
    protected boolean isHostileMob() {
        return false;
    }

    @Override
    protected double getMobMaxHealth() {
        return 20.0d;
    }

    @Override
    protected double getMobMovementSpeed() {
        return 0.23d;
    }

    @Override
    protected double getMobAttackDamage() {
        return 0.0d;
    }

    @Override
    protected double getMobFollowRange() {
        return 16.0d;
    }
}
