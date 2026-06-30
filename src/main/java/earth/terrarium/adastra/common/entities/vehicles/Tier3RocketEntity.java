package earth.terrarium.adastra.common.entities.vehicles;

import net.minecraft.world.World;

public class Tier3RocketEntity extends AdAstraVehicleEntity {

    public Tier3RocketEntity(World world) {
        super(world, VehicleType.ROCKET, 2200, 3);
        setSize(1.1f, 5.5f);
    }
}
