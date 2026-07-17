package earth.terrarium.adastra.client.particle;

import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/** Lightweight drifting particle used by the source project's windy biome. */
@SideOnly(Side.CLIENT)
public final class ParticleWind extends Particle {

    private ParticleWind(World world, double x, double y, double z,
                         double velocityX, double velocityY, double velocityZ) {
        super(world, x, y, z, velocityX, velocityY, velocityZ);
        setParticleTextureIndex(7); // minecraft:generic_7 in the 1.12 particle sheet
        setSize(0.02F, 0.02F);
        particleScale *= rand.nextFloat() * 0.6F + 0.2F;
        particleMaxAge = 40 + rand.nextInt(30);
        particleRed = 0.78F;
        particleGreen = 0.88F;
        particleBlue = 1.0F;
        particleAlpha = 0.65F;
        motionX = velocityX * 0.2D + (rand.nextDouble() * 2.0D - 1.0D) * 0.02D;
        motionY = velocityY * 0.2D + (rand.nextDouble() * 2.0D - 1.0D) * 0.02D;
        motionZ = velocityZ * 0.2D + (rand.nextDouble() * 2.0D - 1.0D) * 0.02D;
    }

    @Override
    public void onUpdate() {
        prevPosX = posX;
        prevPosY = posY;
        prevPosZ = posZ;
        if (particleAge++ >= particleMaxAge) {
            setExpired();
            return;
        }

        motionX -= 0.05D;
        move(motionX, motionY, motionZ);
        motionX *= 0.85D;
        motionY *= 0.85D;
        motionZ *= 0.85D;
        if (particleAge > particleMaxAge - 10) {
            particleAlpha = Math.max(0.0F, (particleMaxAge - particleAge) / 10.0F);
        }
    }

    @Override
    public int getFXLayer() {
        return 0;
    }

    @SideOnly(Side.CLIENT)
    public static final class Factory implements IParticleFactory {
        @Override
        public Particle createParticle(int particleID, World world, double x, double y, double z,
                                       double velocityX, double velocityY, double velocityZ, int... parameters) {
            return new ParticleWind(world, x, y, z, velocityX, velocityY, velocityZ);
        }
    }
}
