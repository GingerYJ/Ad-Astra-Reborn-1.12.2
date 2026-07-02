package earth.terrarium.adastra.client.sound;

import earth.terrarium.adastra.common.entities.vehicles.RocketEntity;
import earth.terrarium.adastra.common.registry.ModSounds;
import net.minecraft.client.audio.ITickableSound;
import net.minecraft.client.audio.PositionedSound;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Looping sound for rocket engines.
 * Plays while the rocket is in flight.
 */
@SideOnly(Side.CLIENT)
public class LoopingRocketSound extends PositionedSound implements ITickableSound {

    private final RocketEntity rocket;
    private boolean donePlaying = false;

    public LoopingRocketSound(RocketEntity rocket) {
        super(ModSounds.ROCKET, SoundCategory.NEUTRAL);
        this.rocket = rocket;
        this.repeat = true;
        this.repeatDelay = 0;
        this.volume = 1.5f;
        this.pitch = 1.0f;
        this.xPosF = (float) rocket.posX;
        this.yPosF = (float) rocket.posY;
        this.zPosF = (float) rocket.posZ;
    }

    @Override
    public void update() {
        // Stop if rocket is dead or no longer flying
        if (rocket.isDead || (!rocket.isLaunching() && !rocket.hasLaunched())) {
            donePlaying = true;
            return;
        }

        // Update position to follow rocket
        this.xPosF = (float) rocket.posX;
        this.yPosF = (float) rocket.posY;
        this.zPosF = (float) rocket.posZ;

        // Adjust volume based on flight state
        if (rocket.isLaunching()) {
            this.volume = 1.0f;
        } else if (rocket.hasLaunched()) {
            this.volume = 1.5f;
        }
    }

    @Override
    public boolean isDonePlaying() {
        return donePlaying;
    }
}
