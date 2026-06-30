package earth.terrarium.adastra.common.entities.mob;

import earth.terrarium.adastra.common.entities.AdAstraPlaceholderMob;
import net.minecraft.world.World;

public class LunarianEntity extends AdAstraPlaceholderMob {

    public LunarianEntity(World world) {
        super(world);
        setSize(0.75f, 2.5f);
    }

    @Override
    protected double getMobMaxHealth() {
        return 24.0d;
    }

    @Override
    protected double getMobMovementSpeed() {
        return 0.27d;
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
