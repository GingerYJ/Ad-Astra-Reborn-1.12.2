package earth.terrarium.adastra.common.entities.mob;

import earth.terrarium.adastra.common.entities.AdAstraPlaceholderMob;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.world.World;

public class MartianRaptorEntity extends AdAstraPlaceholderMob {

    public MartianRaptorEntity(World world) {
        super(world);
        setSize(0.75f, 2.0f);
    }

    @Override
    protected double getMobMaxHealth() {
        return 26.0d;
    }

    @Override
    protected double getMobMovementSpeed() {
        return 0.30d;
    }

    @Override
    protected double getMobAttackDamage() {
        return 8.0d;
    }

    @Override
    protected double getMobFollowRange() {
        return 36.0d;
    }

    @Override
    protected double getMeleeAttackSpeed() {
        return 1.20d;
    }

    @Override
    public EnumCreatureAttribute getCreatureAttribute() {
        return EnumCreatureAttribute.UNDEAD;
    }
}
