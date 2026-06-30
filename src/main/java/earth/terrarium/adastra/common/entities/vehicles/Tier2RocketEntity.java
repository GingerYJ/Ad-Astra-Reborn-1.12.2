package earth.terrarium.adastra.common.entities.vehicles;

import net.minecraft.world.World;

public class Tier2RocketEntity extends AdAstraVehicleEntity {

    public Tier2RocketEntity(World world) {
        super(world, VehicleType.ROCKET, 1600, 2);
        setSize(1.1f, 4.8f);
    }
}
