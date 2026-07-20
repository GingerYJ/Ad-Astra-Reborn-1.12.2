package earth.terrarium.adastra.common.entities.vehicles;

import earth.terrarium.adastra.common.util.RocketFuelHelper;
import net.minecraft.world.World;

public class Tier11RocketEntity extends RocketEntity {

    private static final int TIER_11_FUEL_CAPACITY = 21000;

    public Tier11RocketEntity(World world) {
        super(world, 11, TIER_11_FUEL_CAPACITY, stack -> RocketFuelHelper.canFuelRocket(stack, 11));
        setSize(1.1f, 7.0f);
    }
}
