package earth.terrarium.adastra.common.entities.vehicles;

import earth.terrarium.adastra.common.util.RocketFuelHelper;
import net.minecraft.world.World;

public class Tier3RocketEntity extends RocketEntity {

    private static final int TIER_3_FUEL_CAPACITY = 5000; // 5 buckets

    public Tier3RocketEntity(World world) {
        super(world, 3, TIER_3_FUEL_CAPACITY, stack -> RocketFuelHelper.canFuelRocket(stack, 3));
        setSize(1.1f, 5.5f);
    }
}
