package earth.terrarium.adastra.common.entities.vehicles;

import earth.terrarium.adastra.common.util.RocketFuelHelper;
import net.minecraft.world.World;

public class Tier14RocketEntity extends RocketEntity {

    private static final int TIER_14_FUEL_CAPACITY = 24000;

    public Tier14RocketEntity(World world) {
        super(world, 14, TIER_14_FUEL_CAPACITY, stack -> RocketFuelHelper.canFuelRocket(stack, 14));
        setSize(1.1f, 7.0f);
    }
}
