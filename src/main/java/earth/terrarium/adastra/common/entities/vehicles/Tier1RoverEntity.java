package earth.terrarium.adastra.common.entities.vehicles;

import net.minecraft.world.World;

public class Tier1RoverEntity extends AdAstraVehicleEntity {

    public Tier1RoverEntity(World world) {
        super(world, VehicleType.ROVER, 0);
        setSize(2.2f, 0.9f);
    }
}
