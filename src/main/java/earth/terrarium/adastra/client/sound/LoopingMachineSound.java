package earth.terrarium.adastra.client.sound;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ITickableSound;
import net.minecraft.client.audio.PositionedSound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.function.Predicate;

/**
 * Looping sound for machines.
 * Plays while a machine is active.
 */
@SideOnly(Side.CLIENT)
public class LoopingMachineSound extends PositionedSound implements ITickableSound {

    private final BlockPos pos;
    private final Predicate<TileEntity> shouldContinue;
    private boolean donePlaying = false;
    private int ticksExisted = 0;

    public LoopingMachineSound(SoundEvent sound, BlockPos pos, float volume, float pitch, Predicate<TileEntity> shouldContinue) {
        super(sound, SoundCategory.BLOCKS);
        this.pos = pos;
        this.shouldContinue = shouldContinue;
        this.repeat = true;
        this.repeatDelay = 0;
        this.volume = volume;
        this.pitch = pitch;
        this.xPosF = pos.getX() + 0.5f;
        this.yPosF = pos.getY() + 0.5f;
        this.zPosF = pos.getZ() + 0.5f;
    }

    @Override
    public void update() {
        ticksExisted++;

        // Check every 20 ticks if the machine is still active
        if (ticksExisted % 20 == 0) {
            TileEntity tile = Minecraft.getMinecraft().world.getTileEntity(pos);
            if (tile == null || tile.isInvalid() || !shouldContinue.test(tile)) {
                donePlaying = true;
            }
        }
    }

    @Override
    public boolean isDonePlaying() {
        return donePlaying;
    }

    public BlockPos getPos() {
        return pos;
    }
}
