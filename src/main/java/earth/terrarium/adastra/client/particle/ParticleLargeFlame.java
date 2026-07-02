package earth.terrarium.adastra.client.particle;

import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
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

        int brightness = 240; // Full brightness for flame
        int lightmapX = 240;
        int lightmapY = 240;

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
            return new ParticleLargeFlame(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn);
        }
    }
}
