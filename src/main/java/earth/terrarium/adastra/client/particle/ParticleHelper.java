package earth.terrarium.adastra.client.particle;

import earth.terrarium.adastra.common.registry.ModParticles;
import net.minecraft.client.Minecraft;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Helper class for spawning Ad Astra particles.
 */
@SideOnly(Side.CLIENT)
public final class ParticleHelper {

    private ParticleHelper() {
    }

    /**
     * Spawns acid rain particles.
     * @param world The world
     * @param x X position
     * @param y Y position
     * @param z Z position
     * @param velocityX X velocity
     * @param velocityY Y velocity
     * @param velocityZ Z velocity
     */
    public static void spawnAcidRain(World world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
        if (world.isRemote) {
            Minecraft.getMinecraft().effectRenderer.spawnEffectParticle(
                ModParticles.ACID_RAIN, x, y, z, velocityX, velocityY, velocityZ);
        }
    }

    /**
     * Spawns large flame particles.
     * @param world The world
     * @param x X position
     * @param y Y position
     * @param z Z position
     * @param velocityX X velocity
     * @param velocityY Y velocity
     * @param velocityZ Z velocity
     */
    public static void spawnLargeFlame(World world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
        if (world.isRemote) {
            Minecraft.getMinecraft().effectRenderer.spawnEffectParticle(
                ModParticles.LARGE_FLAME, x, y, z, velocityX, velocityY, velocityZ);
        }
    }

    /**
     * Spawns large smoke particles.
     * @param world The world
     * @param x X position
     * @param y Y position
     * @param z Z position
     * @param velocityX X velocity
     * @param velocityY Y velocity
     * @param velocityZ Z velocity
     */
    public static void spawnLargeSmoke(World world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
        if (world.isRemote) {
            Minecraft.getMinecraft().effectRenderer.spawnEffectParticle(
                ModParticles.LARGE_SMOKE, x, y, z, velocityX, velocityY, velocityZ);
        }
    }

    /**
     * Spawns oxygen bubble particles.
     * @param world The world
     * @param x X position
     * @param y Y position
     * @param z Z position
     * @param velocityX X velocity
     * @param velocityY Y velocity
     * @param velocityZ Z velocity
     */
    public static void spawnOxygenBubble(World world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
        if (world.isRemote) {
            Minecraft.getMinecraft().effectRenderer.spawnEffectParticle(
                ModParticles.OXYGEN_BUBBLE, x, y, z, velocityX, velocityY, velocityZ);
        }
    }

    /**
     * Spawns oxygen venting particles.
     * @param world The world
     * @param x X position
     * @param y Y position
     * @param z Z position
     * @param velocityX X velocity
     * @param velocityY Y velocity
     * @param velocityZ Z velocity
     */
    public static void spawnOxygenVent(World world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
        if (world.isRemote) {
            Minecraft.getMinecraft().effectRenderer.spawnEffectParticle(
                ModParticles.OXYGEN_VENT, x, y, z, velocityX, velocityY, velocityZ);
        }
    }

    /**
     * Spawns cryo freezing particles.
     * @param world The world
     * @param x X position
     * @param y Y position
     * @param z Z position
     * @param velocityX X velocity
     * @param velocityY Y velocity
     * @param velocityZ Z velocity
     */
    public static void spawnCryoFreeze(World world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
        if (world.isRemote) {
            Minecraft.getMinecraft().effectRenderer.spawnEffectParticle(
                ModParticles.CRYO_FREEZE, x, y, z, velocityX, velocityY, velocityZ);
        }
    }

    public static void spawnWind(World world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
        if (world.isRemote) {
            Minecraft.getMinecraft().effectRenderer.spawnEffectParticle(
                ModParticles.WIND, x, y, z, velocityX, velocityY, velocityZ);
        }
    }
}
