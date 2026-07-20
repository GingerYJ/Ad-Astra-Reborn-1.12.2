package earth.terrarium.adastra.common.entities.vehicles;

import earth.terrarium.adastra.common.util.RocketFuelHelper;
import net.minecraft.world.World;

public class Tier10RocketEntity extends RocketEntity {

    private static final int TIER_10_FUEL_CAPACITY = 20000;

    public Tier10RocketEntity(World world) {
        super(world, 10, TIER_10_FUEL_CAPACITY, stack -> RocketFuelHelper.canFuelRocket(stack, 10));
        setSize(1.1f, 7.0f);
    }
}
