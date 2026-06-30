package earth.terrarium.adastra.common.entities.vehicles;

import net.minecraft.world.World;

public class Tier1RocketEntity extends AdAstraVehicleEntity {

    public Tier1RocketEntity(World world) {
        super(world, VehicleType.ROCKET, 1200);
        setSize(1.1f, 4.6f);
    }
}
