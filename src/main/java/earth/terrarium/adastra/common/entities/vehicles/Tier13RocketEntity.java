package earth.terrarium.adastra.common.entities.vehicles;

import earth.terrarium.adastra.common.util.RocketFuelHelper;
import net.minecraft.world.World;

public class Tier13RocketEntity extends RocketEntity {

    private static final int TIER_13_FUEL_CAPACITY = 23000;

    public Tier13RocketEntity(World world) {
        super(world, 13, TIER_13_FUEL_CAPACITY, stack -> RocketFuelHelper.canFuelRocket(stack, 13));
        setSize(1.1f, 7.0f);
    }
}
