package earth.terrarium.adastra.client.render;

import earth.terrarium.adastra.common.tile.GravityNormalizerTileEntity;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class TileGravityNormalizerRenderer extends TileEntitySpecialRenderer<GravityNormalizerTileEntity> {

    private static final float FIELD_RADIUS = 0.6f;
    private static final int RING_COUNT = 3;
    private static final int RING_SEGMENTS = 24;

    @Override
    public void render(GravityNormalizerTileEntity te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if (te == null || !te.isLit()) {
            return;
        }

        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5, y + 0.6, z + 0.5);
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
        GlStateManager.disableLighting();
        GlStateManager.depthMask(false);

        long time = te.getWorld().getTotalWorldTime();
        float animationTime = (time + partialTicks) * 0.02f;

        // Render rotating gravitational field rings
        for (int i = 0; i < RING_COUNT; i++) {
            float ringOffset = i * (2.0f / RING_COUNT) - 1.0f;
            float ringRotation = animationTime * (30.0f + i * 15.0f);
            float ringScale = 1.0f - Math.abs(ringOffset) * 0.3f;
            renderGravityRing(ringOffset, ringRotation, ringScale, animationTime + i * 0.5f);
        }

        // Render central energy core
        renderEnergyCore(animationTime);

        GlStateManager.depthMask(true);
        GlStateManager.enableLighting();
        GlStateManager.disableBlend();
        GlStateManager.enableTexture2D();
        GlStateManager.popMatrix();
    }

    private void renderGravityRing(float yOffset, float rotation, float scale, float animationTime) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();

        GlStateManager.pushMatrix();
        GlStateManager.rotate(rotation, 0, 1, 0);
        GlStateManager.scale(scale, 1.0f, scale);

        float pulseIntensity = (float) ((Math.sin(animationTime * 2.0) + 1.0) / 2.0);
        float ringAlpha = 0.2f + pulseIntensity * 0.3f;
        float ringThickness = 0.02f;

        buffer.begin(GL11.GL_TRIANGLE_STRIP, DefaultVertexFormats.POSITION_COLOR);

        for (int segment = 0; segment <= RING_SEGMENTS; segment++) {
            float angle = (float) (segment * 2.0 * Math.PI / RING_SEGMENTS);
            float x = (float) (FIELD_RADIUS * Math.cos(angle));
            float z = (float) (FIELD_RADIUS * Math.sin(angle));

            float brightness = 0.6f + pulseIntensity * 0.4f;
            int r = (int) (150 * brightness);
            int g = (int) (100 * brightness);
            int b = (int) (255 * brightness);
            int a = (int) (255 * ringAlpha);

            buffer.pos(x, yOffset - ringThickness, z).color(r, g, b, a).endVertex();
            buffer.pos(x, yOffset + ringThickness, z).color(r, g, b, a).endVertex();
        }

        tessellator.draw();
        GlStateManager.popMatrix();
    }

    private void renderEnergyCore(float animationTime) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();

        float pulseIntensity = (float) ((Math.sin(animationTime * 3.0) + 1.0) / 2.0);
        float coreSize = 0.08f + pulseIntensity * 0.04f;
        float coreAlpha = 0.5f + pulseIntensity * 0.3f;

        float brightness = 0.8f + pulseIntensity * 0.2f;
        int r = (int) (200 * brightness);
        int g = (int) (150 * brightness);
        int b = (int) (255 * brightness);
        int a = (int) (255 * coreAlpha);

        // Render core as a simple quad facing camera (billboard effect)
        GlStateManager.rotate(-rendererDispatcher.entityYaw, 0, 1, 0);
        GlStateManager.rotate(rendererDispatcher.entityPitch, 1, 0, 0);

        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        buffer.pos(-coreSize, -coreSize, 0).color(r, g, b, a).endVertex();
        buffer.pos(-coreSize, coreSize, 0).color(r, g, b, a).endVertex();
        buffer.pos(coreSize, coreSize, 0).color(r, g, b, a).endVertex();
        buffer.pos(coreSize, -coreSize, 0).color(r, g, b, a).endVertex();
        tessellator.draw();
    }
}
