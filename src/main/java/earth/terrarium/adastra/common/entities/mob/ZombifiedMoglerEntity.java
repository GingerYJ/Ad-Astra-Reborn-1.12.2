package earth.terrarium.adastra.common.entities.mob;

import earth.terrarium.adastra.common.entities.AdAstraPlaceholderMob;
import net.minecraft.world.World;

public class ZombifiedMoglerEntity extends AdAstraPlaceholderMob {

    public ZombifiedMoglerEntity(World world) {
        super(world);
        setSize(1.4f, 1.4f);
        isImmuneToFire = true;
    }
}
