package earth.terrarium.adastra.client.particle;

import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.Particle;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ParticleLargeFlame extends Particle {

    private final float flameScale;

    protected ParticleLargeFlame(World world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
        super(world, x, y, z, velocityX, velocityY, velocityZ);
        this.particleGravity = -0.1F; // Negative gravity for rising flame
        this.particleMaxAge = 10 + this.rand.nextInt(15);

        // Orange-red flame colors
        this.particleRed = 1.0F;
        this.particleGreen = 0.5F + this.rand.nextFloat() * 0.3F;
        this.particleBlue = 0.1F;

        // Larger size
        this.flameScale = 0.5F + this.rand.nextFloat() * 0.5F;
        this.particleScale = this.flameScale;
        this.setParticleTextureIndex(48);

        // Initial upward velocity
        this.motionX = velocityX + (this.rand.nextDouble() - 0.5D) * 0.1D;
        this.motionY = velocityY + 0.1D + this.rand.nextDouble() * 0.1D;
        this.motionZ = velocityZ + (this.rand.nextDouble() - 0.5D) * 0.1D;
    }

    @Override
    public void onUpdate() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;

        if (this.particleAge++ >= this.particleMaxAge) {
            this.setExpired();
            return;
        }

        // Apply negative gravity (rises)
        this.motionY += 0.004D * Math.abs(this.particleGravity);

        // Move particle
        this.move(this.motionX, this.motionY, this.motionZ);

        // Slow down motion
        this.motionX *= 0.96D;
        this.motionY *= 0.96D;
        this.motionZ *= 0.96D;

        // Expand and fade
        float ageRatio = (float)this.particleAge / (float)this.particleMaxAge;
        this.particleScale = this.flameScale * (1.0F + ageRatio * 0.5F);
        this.particleAlpha = 1.0F - ageRatio;

        // Transition from orange to yellow to white
        if (ageRatio < 0.3F) {
            this.particleGreen = 0.5F + ageRatio * 1.5F;
        } else {
            this.particleGreen = Math.min(1.0F, 0.95F + (ageRatio - 0.3F) * 0.15F);
            this.particleBlue = Math.min(0.5F, (ageRatio - 0.3F) * 0.7F);
        }
    }

    @Override
    public int getFXLayer() {
        return 0;
    }

    @Override
    public int getBrightnessForRender(float partialTick) {
        return 0xF000F0;
    }

    @Override
    public boolean shouldDisableDepth() {
        return true;
    }

    @SideOnly(Side.CLIENT)
    public static class Factory implements IParticleFactory {
        @Override
        public Particle createParticle(int particleID, World worldIn, double xCoordIn, double yCoordIn, double zCoordIn,
                                       double xSpeedIn, double ySpeedIn, double zSpeedIn, int... parameters) {
            return new ParticleLargeFlame(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn);
        }
    }
}
