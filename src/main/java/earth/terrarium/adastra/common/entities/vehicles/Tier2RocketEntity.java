package earth.terrarium.adastra.common.entities.vehicles;

import earth.terrarium.adastra.common.util.RocketFuelHelper;
import net.minecraft.world.World;

public class Tier2RocketEntity extends RocketEntity {

    private static final int TIER_2_FUEL_CAPACITY = 4000; // 4 buckets

    public Tier2RocketEntity(World world) {
        super(world, 2, TIER_2_FUEL_CAPACITY, stack -> RocketFuelHelper.canFuelRocket(stack, 2));
        setSize(1.1f, 4.8f);
    }
}
