package earth.terrarium.adastra.common.entities.vehicles;

import net.minecraft.world.World;

public class Tier4RocketEntity extends AdAstraVehicleEntity {

    public Tier4RocketEntity(World world) {
        super(world, VehicleType.ROCKET, 3000);
        setSize(1.1f, 7.0f);
    }
}
