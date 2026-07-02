package earth.terrarium.adastra.client.sound;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

/**
 * Manages looping sounds for machines and entities.
 * Prevents duplicate sounds from playing at the same location.
 */
@SideOnly(Side.CLIENT)
public class SoundManager {

    private static final Map<BlockPos, LoopingMachineSound> activeMachineSounds = new HashMap<>();

    /**
     * Play a looping sound for a machine at the given position.
     * If a sound is already playing at this position, does nothing.
     *
     * @param sound The sound event to play
     * @param pos The position of the machine
     * @param volume Sound volume (0.0 - 1.0)
     * @param pitch Sound pitch
     * @param shouldContinue Predicate to check if sound should continue playing
     */
    public static void playMachineLoopingSound(SoundEvent sound, BlockPos pos, float volume, float pitch, Predicate<TileEntity> shouldContinue) {
        if (Minecraft.getMinecraft().world == null) {
            return;
        }

        // Check if sound is already playing at this position
        LoopingMachineSound existing = activeMachineSounds.get(pos);
        if (existing != null && !existing.isDonePlaying()) {
            return; // Sound already playing
        }

        // Create and play new sound
        LoopingMachineSound machineSound = new LoopingMachineSound(sound, pos, volume, pitch, shouldContinue);
        Minecraft.getMinecraft().getSoundHandler().playSound(machineSound);
        activeMachineSounds.put(pos, machineSound);
    }

    /**
     * Stop the looping sound at the given position.
     *
     * @param pos The position of the machine
     */
    public static void stopMachineLoopingSound(BlockPos pos) {
        LoopingMachineSound sound = activeMachineSounds.remove(pos);
        if (sound != null) {
            Minecraft.getMinecraft().getSoundHandler().stopSound(sound);
        }
    }

    /**
     * Clean up finished sounds.
     * Should be called periodically.
     */
    public static void cleanup() {
        activeMachineSounds.entrySet().removeIf(entry -> entry.getValue().isDonePlaying());
    }

    /**
     * Stop all sounds.
     * Should be called when leaving a world.
     */
    public static void stopAll() {
        for (ISound sound : activeMachineSounds.values()) {
            Minecraft.getMinecraft().getSoundHandler().stopSound(sound);
        }
        activeMachineSounds.clear();
    }
}
