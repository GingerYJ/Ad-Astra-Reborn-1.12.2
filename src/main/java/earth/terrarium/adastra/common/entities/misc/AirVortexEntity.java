package earth.terrarium.adastra.common.entities.misc;

import earth.terrarium.adastra.common.entities.AdAstraPlaceholderEntity;
import net.minecraft.world.World;

public class AirVortexEntity extends AdAstraPlaceholderEntity {

    public AirVortexEntity(World world) {
        super(world);
        setSize(0.5f, 0.5f);
    }
}
