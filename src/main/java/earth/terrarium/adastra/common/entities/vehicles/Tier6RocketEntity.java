package earth.terrarium.adastra.common.entities.vehicles;

import earth.terrarium.adastra.common.util.RocketFuelHelper;
import net.minecraft.world.World;

public class Tier6RocketEntity extends RocketEntity {

    private static final int TIER_6_FUEL_CAPACITY = 8000;

    public Tier6RocketEntity(World world) {
        super(world, 6, TIER_6_FUEL_CAPACITY, stack -> RocketFuelHelper.canFuelRocket(stack, 6));
        setSize(1.1f, 7.0f);
    }
}
