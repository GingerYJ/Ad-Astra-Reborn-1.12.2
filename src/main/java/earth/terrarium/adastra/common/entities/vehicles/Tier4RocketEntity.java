package earth.terrarium.adastra.common.entities.vehicles;

import earth.terrarium.adastra.common.util.RocketFuelHelper;
import net.minecraft.world.World;

public class Tier4RocketEntity extends RocketEntity {

    private static final int TIER_4_FUEL_CAPACITY = 6000; // 6 buckets

    public Tier4RocketEntity(World world) {
        super(world, 4, TIER_4_FUEL_CAPACITY, stack -> RocketFuelHelper.canFuelRocket(stack, 4));
        setSize(1.1f, 7.0f);
    }
}
