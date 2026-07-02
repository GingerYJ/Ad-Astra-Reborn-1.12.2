package earth.terrarium.adastra.client.particle;

import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Particle for oxygen venting effect - white/cyan vapor clouds.
 */
@SideOnly(Side.CLIENT)
public class ParticleOxygenVent extends Particle {

    private final float ventScale;

    protected ParticleOxygenVent(World world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
        super(world, x, y, z, velocityX, velocityY, velocityZ);
        this.particleGravity = -0.05F; // Slight upward drift
        this.particleMaxAge = 30 + this.rand.nextInt(20);

        // White/cyan oxygen vapor color
        this.particleRed = 0.9F + this.rand.nextFloat() * 0.1F;
        this.particleGreen = 0.95F + this.rand.nextFloat() * 0.05F;
        this.particleBlue = 1.0F;

        // Medium size, expands over time
        this.ventScale = 0.3F + this.rand.nextFloat() * 0.4F;
        this.particleScale = this.ventScale;

        // Initial velocity with spread
        this.motionX = velocityX + (this.rand.nextDouble() - 0.5D) * 0.15D;
        this.motionY = velocityY + 0.02D + this.rand.nextDouble() * 0.08D;
        this.motionZ = velocityZ + (this.rand.nextDouble() - 0.5D) * 0.15D;
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

        // Apply slight upward drift
        this.motionY += 0.002D * Math.abs(this.particleGravity);

        // Move particle
        this.move(this.motionX, this.motionY, this.motionZ);

        // Air resistance - slows down quickly
        this.motionX *= 0.92D;
        this.motionY *= 0.94D;
        this.motionZ *= 0.92D;

        // Expand and fade over time
        float ageRatio = (float)this.particleAge / (float)this.particleMaxAge;
        this.particleScale = this.ventScale * (1.0F + ageRatio * 1.5F);
        this.particleAlpha = Math.max(0.0F, 0.7F - ageRatio * 0.7F);

        // Slight color shift to more transparent white
        if (ageRatio > 0.5F) {
            float shift = (ageRatio - 0.5F) * 2.0F;
            this.particleRed = 1.0F;
            this.particleGreen = 0.95F + shift * 0.05F;
        }
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

        // Make oxygen vapor bright
        lightmapX = Math.min(240, lightmapX + 60);
        lightmapY = Math.min(240, lightmapY + 60);

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

    @Override
    public boolean shouldDisableDepth() {
        return true;
    }

    @SideOnly(Side.CLIENT)
    public static class Factory implements IParticleFactory {
        @Override
        public Particle createParticle(int particleID, World worldIn, double xCoordIn, double yCoordIn, double zCoordIn,
                                       double xSpeedIn, double ySpeedIn, double zSpeedIn, int... parameters) {
            return new ParticleOxygenVent(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn);
        }
    }
}
