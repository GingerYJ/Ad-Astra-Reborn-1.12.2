package earth.terrarium.adastra.common.entities.mob;

import earth.terrarium.adastra.common.entities.AdAstraPlaceholderMob;
import net.minecraft.world.World;

public class CorruptedLunarianEntity extends AdAstraPlaceholderMob {

    public CorruptedLunarianEntity(World world) {
        super(world);
        setSize(0.6f, 2.4f);
    }

    @Override
    protected double getMobMaxHealth() {
        return 28.0d;
    }

    @Override
    protected double getMobMovementSpeed() {
        return 0.28d;
    }

    @Override
    protected double getMobAttackDamage() {
        return 5.0d;
    }

    @Override
    protected double getMobFollowRange() {
        return 36.0d;
    }
}
