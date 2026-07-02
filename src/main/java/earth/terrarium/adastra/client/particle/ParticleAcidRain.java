package earth.terrarium.adastra.client.particle;

import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ParticleAcidRain extends Particle {

    protected ParticleAcidRain(World world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
        super(world, x, y, z, velocityX, velocityY, velocityZ);
        this.particleGravity = 0.3F;
        this.particleMaxAge = 20 + this.rand.nextInt(10);

        // Green-yellow tint for acid rain
        this.particleRed = 0.6F + this.rand.nextFloat() * 0.2F;
        this.particleGreen = 0.8F + this.rand.nextFloat() * 0.2F;
        this.particleBlue = 0.2F;

        // Small to medium size
        this.particleScale = 0.1F + this.rand.nextFloat() * 0.15F;

        // Initial downward velocity
        this.motionY = velocityY - 0.1D;
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

        // Apply gravity
        this.motionY -= 0.04D * this.particleGravity;

        // Move particle
        this.move(this.motionX, this.motionY, this.motionZ);

        // Slight air resistance
        this.motionX *= 0.98D;
        this.motionZ *= 0.98D;

        // Fade out near end of life
        if (this.particleAge > this.particleMaxAge - 5) {
            this.particleAlpha = 1.0F - ((float)(this.particleAge - (this.particleMaxAge - 5)) / 5.0F);
        }

        // Die on collision with ground
        if (this.onGround) {
            this.motionX *= 0.7D;
            this.motionZ *= 0.7D;
            this.setExpired();
        }
    }

    @Override
    public void renderParticle(BufferBuilder buffer, Entity entityIn, float partialTicks,
                               float rotationX, float rotationZ, float rotationYZ,
                               float rotationXY, float rotationXZ) {
        // Stretch the particle vertically for rain effect
        float minU = 0.0F;
        float maxU = 1.0F;
        float minV = 0.0F;
        float maxV = 1.0F;

        float x = (float)(this.prevPosX + (this.posX - this.prevPosX) * partialTicks - interpPosX);
        float y = (float)(this.prevPosY + (this.posY - this.prevPosY) * partialTicks - interpPosY);
        float z = (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * partialTicks - interpPosZ);

        float scale = this.particleScale * 0.5F;
        float heightScale = this.particleScale * 2.0F;

        int brightness = this.getBrightnessForRender(partialTicks);
        int lightmapX = brightness >> 16 & 65535;
        int lightmapY = brightness & 65535;

        buffer.pos(x - rotationX * scale - rotationXY * scale, y - rotationZ * heightScale, z - rotationYZ * scale - rotationXZ * scale)
            .tex(maxU, maxV).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha)
            .lightmap(lightmapX, lightmapY).endVertex();
        buffer.pos(x - rotationX * scale + rotationXY * scale, y + rotationZ * heightScale, z - rotationYZ * scale + rotationXZ * scale)
            .tex(maxU, minV).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha)
            .lightmap(lightmapX, lightmapY).endVertex();
        buffer.pos(x + rotationX * scale + rotationXY * scale, y + rotationZ * heightScale, z + rotationYZ * scale + rotationXZ * scale)
            .tex(minU, minV).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha)
            .lightmap(lightmapX, lightmapY).endVertex();
        buffer.pos(x + rotationX * scale - rotationXY * scale, y - rotationZ * heightScale, z + rotationYZ * scale - rotationXZ * scale)
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
            return new ParticleAcidRain(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn);
        }
    }
}
