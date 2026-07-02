package earth.terrarium.adastra.client.particle;

import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ParticleLargeSmoke extends Particle {

    private final float smokeScale;

    protected ParticleLargeSmoke(World world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
        super(world, x, y, z, velocityX, velocityY, velocityZ);
        this.particleGravity = -0.05F; // Slight upward bias
        this.particleMaxAge = 30 + this.rand.nextInt(20);

        // Gray smoke colors
        float shade = 0.3F + this.rand.nextFloat() * 0.3F;
        this.particleRed = shade;
        this.particleGreen = shade;
        this.particleBlue = shade;

        // Large size
        this.smokeScale = 0.8F + this.rand.nextFloat() * 0.7F;
        this.particleScale = this.smokeScale;

        // Initial velocity with some randomness
        this.motionX = velocityX + (this.rand.nextDouble() - 0.5D) * 0.1D;
        this.motionY = velocityY + 0.05D + this.rand.nextDouble() * 0.05D;
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

        // Apply upward drift
        this.motionY += 0.002D * Math.abs(this.particleGravity);

        // Move particle
        this.move(this.motionX, this.motionY, this.motionZ);

        // Air resistance
        this.motionX *= 0.92D;
        this.motionY *= 0.92D;
        this.motionZ *= 0.92D;

        // Add some horizontal drift
        if (this.particleAge % 3 == 0) {
            this.motionX += (this.rand.nextDouble() - 0.5D) * 0.01D;
            this.motionZ += (this.rand.nextDouble() - 0.5D) * 0.01D;
        }

        // Expand and fade
        float ageRatio = (float)this.particleAge / (float)this.particleMaxAge;
        this.particleScale = this.smokeScale * (1.0F + ageRatio * 2.0F);

        // Fade out gradually, more quickly near the end
        if (ageRatio < 0.6F) {
            this.particleAlpha = 0.8F - ageRatio * 0.5F;
        } else {
            this.particleAlpha = 0.5F - (ageRatio - 0.6F) * 1.25F;
        }

        // Lighten color over time
        float lightening = ageRatio * 0.3F;
        this.particleRed = Math.min(1.0F, this.particleRed + lightening);
        this.particleGreen = Math.min(1.0F, this.particleGreen + lightening);
        this.particleBlue = Math.min(1.0F, this.particleBlue + lightening);
    }

    @Override
    public void renderParticle(BufferBuilder buffer, Entity entityIn, float partialTicks,
                               float rotationX, float rotationZ, float rotationYZ,
                               float rotationXY, float rotationXZ) {
        float minU = 0.0F;
        float maxU = 1.0F;
        float minV = 0.0F;
        float maxV = 1.0F;

        float x = (float)(this.prevPosX + (this.posX - this.prevPosX) * partialTicks - interpPosX);
        float y = (float)(this.prevPosY + (this.posY - this.prevPosY) * partialTicks - interpPosY);
        float z = (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * partialTicks - interpPosZ);

        float scale = this.particleScale;

        int brightness = this.getBrightnessForRender(partialTicks);
        int lightmapX = brightness >> 16 & 65535;
        int lightmapY = brightness & 65535;

        buffer.pos(x - rotationX * scale - rotationXY * scale, y - rotationZ * scale, z - rotationYZ * scale - rotationXZ * scale)
            .tex(maxU, maxV).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha)
            .lightmap(lightmapX, lightmapY).endVertex();
        buffer.pos(x - rotationX * scale + rotationXY * scale, y + rotationZ * scale, z - rotationYZ * scale + rotationXZ * scale)
            .tex(maxU, minV).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha)
            .lightmap(lightmapX, lightmapY).endVertex();
        buffer.pos(x + rotationX * scale + rotationXY * scale, y + rotationZ * scale, z + rotationYZ * scale + rotationXZ * scale)
            .tex(minU, minV).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha)
            .lightmap(lightmapX, lightmapY).endVertex();
        buffer.pos(x + rotationX * scale - rotationXY * scale, y - rotationZ * scale, z + rotationYZ * scale - rotationXZ * scale)
            .tex(minU, maxV).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha)
            .lightmap(lightmapX, lightmapY).endVertex();
    }

    @Override
    public int getFXLayer() {
        return 0;
    }

    @SideOnly(Side.CLIENT)
    public static class Factory implements IParticleFactory {
        @Override
        public Particle createParticle(int particleID, World worldIn, double xCoordIn, double yCoordIn, double zCoordIn,
                                       double xSpeedIn, double ySpeedIn, double zSpeedIn, int... parameters) {
            return new ParticleLargeSmoke(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn);
        }
    }
}
