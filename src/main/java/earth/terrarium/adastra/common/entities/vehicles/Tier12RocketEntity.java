package earth.terrarium.adastra.common.entities.vehicles;

import earth.terrarium.adastra.common.util.RocketFuelHelper;
import net.minecraft.world.World;

public class Tier12RocketEntity extends RocketEntity {

    private static final int TIER_12_FUEL_CAPACITY = 22000;

    public Tier12RocketEntity(World world) {
        super(world, 12, TIER_12_FUEL_CAPACITY, stack -> RocketFuelHelper.canFuelRocket(stack, 12));
        setSize(1.1f, 7.0f);
    }
}
