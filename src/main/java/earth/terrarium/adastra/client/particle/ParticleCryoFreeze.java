package earth.terrarium.adastra.client.particle;

import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Particle for cryo freezing effect - icy blue crystalline particles.
 */
@SideOnly(Side.CLIENT)
public class ParticleCryoFreeze extends Particle {

    private final float cryoScale;
    private final float spinSpeed;

    protected ParticleCryoFreeze(World world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
        super(world, x, y, z, velocityX, velocityY, velocityZ);
        this.particleGravity = 0.3F; // Falls down like snow/ice
        this.particleMaxAge = 40 + this.rand.nextInt(30);

        // Icy blue/cyan crystalline color
        this.particleRed = 0.6F + this.rand.nextFloat() * 0.2F;
        this.particleGreen = 0.8F + this.rand.nextFloat() * 0.2F;
        this.particleBlue = 1.0F;

        // Small crystalline particles
        this.cryoScale = 0.15F + this.rand.nextFloat() * 0.25F;
        this.particleScale = this.cryoScale;

        // Spinning effect
        this.spinSpeed = (this.rand.nextFloat() - 0.5F) * 0.2F;

        // Initial velocity - slow falling with spread
        this.motionX = velocityX + (this.rand.nextDouble() - 0.5D) * 0.08D;
        this.motionY = velocityY - 0.02D - this.rand.nextDouble() * 0.05D;
        this.motionZ = velocityZ + (this.rand.nextDouble() - 0.5D) * 0.08D;
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
        this.motionY -= 0.003D * this.particleGravity;

        // Add slight swirling motion
        double swirl = this.particleAge * 0.1D;
        this.motionX += Math.sin(swirl) * 0.001D;
        this.motionZ += Math.cos(swirl) * 0.001D;

        // Move particle
        this.move(this.motionX, this.motionY, this.motionZ);

        // Air resistance
        this.motionX *= 0.95D;
        this.motionY *= 0.98D;
        this.motionZ *= 0.95D;

        // Sparkle effect - vary brightness
        float ageRatio = (float)this.particleAge / (float)this.particleMaxAge;
        float sparkle = (float)Math.abs(Math.sin(this.particleAge * 0.5D)) * 0.3F;

        // Fade out near end
        if (ageRatio > 0.8F) {
            this.particleAlpha = Math.max(0.0F, 1.0F - ((ageRatio - 0.8F) / 0.2F));
        } else {
            this.particleAlpha = 0.8F + sparkle;
        }

        // Slight size variation for crystalline effect
        this.particleScale = this.cryoScale * (1.0F + (float)Math.sin(this.particleAge * 0.3D) * 0.15F);

        // Despawn on ground
        if (this.onGround && this.particleAge > 10) {
            this.particleMaxAge = this.particleAge + 5; // Quick fade
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

        // Enhanced brightness for icy sparkle
        int lightmapX = 240;
        int lightmapY = 240;

        // Add sparkle/shimmer effect to color
        float sparkle = (float)Math.abs(Math.sin((this.particleAge + partialTicks) * 0.5D)) * 0.2F;
        float r = Math.min(1.0F, this.particleRed + sparkle);
        float g = Math.min(1.0F, this.particleGreen + sparkle);
        float b = this.particleBlue;

        buffer.pos(x - rotationX * scale - rotationXY * scale, y - rotationZ * scale, z - rotationYZ * scale - rotationXZ * scale)
            .tex(maxU, maxV).color(r, g, b, this.particleAlpha)
            .lightmap(lightmapX, lightmapY).endVertex();
        buffer.pos(x - rotationX * scale + rotationXY * scale, y + rotationZ * scale, z - rotationYZ * scale + rotationXZ * scale)
            .tex(maxU, minV).color(r, g, b, this.particleAlpha)
            .lightmap(lightmapX, lightmapY).endVertex();
        buffer.pos(x + rotationX * scale + rotationXY * scale, y + rotationZ * scale, z + rotationYZ * scale + rotationXZ * scale)
            .tex(minU, minV).color(r, g, b, this.particleAlpha)
            .lightmap(lightmapX, lightmapY).endVertex();
        buffer.pos(x + rotationX * scale - rotationXY * scale, y - rotationZ * scale, z + rotationYZ * scale - rotationXZ * scale)
            .tex(minU, maxV).color(r, g, b, this.particleAlpha)
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
            return new ParticleCryoFreeze(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn);
        }
    }
}
