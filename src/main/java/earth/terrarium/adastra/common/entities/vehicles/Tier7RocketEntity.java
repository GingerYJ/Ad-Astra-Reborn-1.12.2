package earth.terrarium.adastra.common.entities.vehicles;

import earth.terrarium.adastra.common.util.RocketFuelHelper;
import net.minecraft.world.World;

public class Tier7RocketEntity extends RocketEntity {

    private static final int TIER_7_FUEL_CAPACITY = 9000;

    public Tier7RocketEntity(World world) {
        super(world, 7, TIER_7_FUEL_CAPACITY, stack -> RocketFuelHelper.canFuelRocket(stack, 7));
        setSize(1.1f, 7.0f);
    }
}
