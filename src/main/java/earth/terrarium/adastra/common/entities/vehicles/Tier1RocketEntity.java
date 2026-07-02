package earth.terrarium.adastra.common.entities.vehicles;

import earth.terrarium.adastra.common.util.RocketFuelHelper;
import net.minecraft.world.World;

public class Tier1RocketEntity extends RocketEntity {

    private static final int TIER_1_FUEL_CAPACITY = 3000; // 3 buckets

    public Tier1RocketEntity(World world) {
        super(world, 1, TIER_1_FUEL_CAPACITY, stack -> RocketFuelHelper.canFuelRocket(stack, 1));
        setSize(1.1f, 4.6f);
    }
}
