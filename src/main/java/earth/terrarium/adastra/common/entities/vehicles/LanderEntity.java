package earth.terrarium.adastra.common.entities.vehicles;

import net.minecraft.world.World;

public class LanderEntity extends AdAstraVehicleEntity {

    public LanderEntity(World world) {
        super(world, VehicleType.LANDER, 0);
        setSize(1.2f, 2.0f);
    }
}
