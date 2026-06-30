package earth.terrarium.adastra.common.entities.mob;

import earth.terrarium.adastra.common.entities.AdAstraPlaceholderMob;
import net.minecraft.world.World;

public class StarCrawlerEntity extends AdAstraPlaceholderMob {

    public StarCrawlerEntity(World world) {
        super(world);
        setSize(1.3f, 1.0f);
    }

    @Override
    protected double getMobMaxHealth() {
        return 18.0d;
    }

    @Override
    protected double getMobMovementSpeed() {
        return 0.30d;
    }

    @Override
    protected double getMobAttackDamage() {
        return 4.0d;
    }

    @Override
    protected double getMobFollowRange() {
        return 30.0d;
    }
}
