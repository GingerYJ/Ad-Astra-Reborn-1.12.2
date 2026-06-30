package earth.terrarium.adastra.common.entities.mob;

import earth.terrarium.adastra.common.entities.AdAstraPlaceholderMob;
import net.minecraft.world.World;

public class SulfurCreeperEntity extends AdAstraPlaceholderMob {

    public SulfurCreeperEntity(World world) {
        super(world);
        setSize(0.6f, 1.7f);
        isImmuneToFire = true;
    }
}
