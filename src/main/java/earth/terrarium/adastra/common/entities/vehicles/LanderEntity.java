package earth.terrarium.adastra.common.entities.vehicles;

import earth.terrarium.adastra.common.entities.AdAstraPlaceholderEntity;
import net.minecraft.world.World;

public class LanderEntity extends AdAstraPlaceholderEntity {

    public LanderEntity(World world) {
        super(world);
        setSize(1.2f, 2.0f);
    }
}
