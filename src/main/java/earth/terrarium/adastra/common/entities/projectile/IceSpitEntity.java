package earth.terrarium.adastra.common.entities.projectile;

import earth.terrarium.adastra.common.entities.AdAstraPlaceholderProjectile;
import net.minecraft.world.World;

public class IceSpitEntity extends AdAstraPlaceholderProjectile {

    public IceSpitEntity(World world) {
        super(world);
        setSize(0.5f, 0.5f);
    }
}
