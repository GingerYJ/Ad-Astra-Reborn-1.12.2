package earth.terrarium.adastra.common.entities.vehicles;

import earth.terrarium.adastra.common.util.RocketFuelHelper;
import net.minecraft.world.World;

public class Tier5RocketEntity extends RocketEntity {

    private static final int TIER_5_FUEL_CAPACITY = 7000;

    public Tier5RocketEntity(World world) {
        super(world, 5, TIER_5_FUEL_CAPACITY, stack -> RocketFuelHelper.canFuelRocket(stack, 5));
        setSize(1.1f, 7.0f);
    }
}
