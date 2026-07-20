package earth.terrarium.adastra.common.entities.vehicles;

import earth.terrarium.adastra.common.util.RocketFuelHelper;
import net.minecraft.world.World;

public class Tier9RocketEntity extends RocketEntity {

    private static final int TIER_9_FUEL_CAPACITY = 19000;

    public Tier9RocketEntity(World world) {
        super(world, 9, TIER_9_FUEL_CAPACITY, stack -> RocketFuelHelper.canFuelRocket(stack, 9));
        setSize(1.1f, 7.0f);
    }
}
