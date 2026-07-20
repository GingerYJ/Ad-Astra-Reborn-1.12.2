package earth.terrarium.adastra.common.entities.vehicles;

import earth.terrarium.adastra.common.util.RocketFuelHelper;
import net.minecraft.world.World;

public class Tier15RocketEntity extends RocketEntity {

    private static final int TIER_15_FUEL_CAPACITY = 25000;

    public Tier15RocketEntity(World world) {
        super(world, 15, TIER_15_FUEL_CAPACITY, stack -> RocketFuelHelper.canFuelRocket(stack, 15));
        setSize(1.1f, 7.0f);
    }
}
