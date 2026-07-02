package earth.terrarium.adastra.client.particle;

import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ParticleOxygenBubble extends Particle {

    private final float bubbleScale;

    protected ParticleOxygenBubble(World world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
        super(world, x, y, z, velocityX, velocityY, velocityZ);
        this.particleGravity = -0.2F; // Negative gravity for rising bubbles
        this.particleMaxAge = 20 + this.rand.nextInt(20);

        // Light blue/cyan bubble color
        this.particleRed = 0.7F + this.rand.nextFloat() * 0.3F;
        this.particleGreen = 0.9F + this.rand.nextFloat() * 0.1F;
        this.particleBlue = 1.0F;

        // Small to medium size
        this.bubbleScale = 0.2F + this.rand.nextFloat() * 0.3F;
        this.particleScale = this.bubbleScale;

        // Initial upward velocity
        this.motionX = velocityX + (this.rand.nextDouble() - 0.5D) * 0.05D;
        this.motionY = velocityY + 0.05D + this.rand.nextDouble() * 0.05D;
        this.motionZ = velocityZ + (this.rand.nextDouble() - 0.5D) * 0.05D;
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

        // Apply buoyancy (negative gravity)
        this.motionY += 0.003D * Math.abs(this.particleGravity);

        // Add wobble effect
        double wobbleStrength = 0.02D;
        this.motionX += Math.sin((this.particleAge + this.posY) * 0.3D) * wobbleStrength;
        this.motionZ += Math.cos((this.particleAge + this.posX) * 0.3D) * wobbleStrength;

        // Move particle
        this.move(this.motionX, this.motionY, this.motionZ);

        // Air/liquid resistance
        this.motionX *= 0.85D;
        this.motionY *= 0.95D;
        this.motionZ *= 0.85D;

        // Slight size oscillation
        float sizeOscillation = (float)Math.sin(this.particleAge * 0.5D) * 0.1F;
        this.particleScale = this.bubbleScale * (1.0F + sizeOscillation);

        // Fade based on age
        float ageRatio = (float)this.particleAge / (float)this.particleMaxAge;
        if (ageRatio > 0.7F) {
            this.particleAlpha = 1.0F - ((ageRatio - 0.7F) / 0.3F);
        } else {
            this.particleAlpha = 0.6F + (float)Math.sin(this.particleAge * 0.2D) * 0.2F;
        }

        // Pop if reaches surface or collides
        if (this.onGround) {
            this.setExpired();
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

        // Make bubbles slightly brighter
        lightmapX = Math.min(240, lightmapX + 40);
        lightmapY = Math.min(240, lightmapY + 40);

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
            return new ParticleOxygenBubble(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn);
        }
    }
}
