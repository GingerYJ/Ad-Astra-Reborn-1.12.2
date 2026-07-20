package earth.terrarium.adastra.common.entities.vehicles;

import earth.terrarium.adastra.common.util.RocketFuelHelper;
import net.minecraft.world.World;

public class Tier8RocketEntity extends RocketEntity {

    private static final int TIER_8_FUEL_CAPACITY = 18000;

    public Tier8RocketEntity(World world) {
        super(world, 8, TIER_8_FUEL_CAPACITY, stack -> RocketFuelHelper.canFuelRocket(stack, 8));
        setSize(1.1f, 7.0f);
    }
}
