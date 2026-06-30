package earth.terrarium.adastra.client.render;

import earth.terrarium.adastra.common.entities.misc.AirVortexEntity;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
class RenderAirVortex extends Render<AirVortexEntity> {

    private static final ResourceLocation TEXTURE = new ResourceLocation("minecraft", "textures/blocks/ice.png");

    RenderAirVortex(RenderManager renderManager) {
        super(renderManager);
        shadowSize = 0.25f;
    }

    @Override
    public void doRender(AirVortexEntity entity, double x, double y, double z, float entityYaw, float partialTicks) {
        GlStateManager.pushMatrix();
        GlStateManager.translate((float) x, (float) y + 0.25f, (float) z);
        GlStateManager.rotate((entity.ticksExisted + partialTicks) * 12.0f, 0.0f, 1.0f, 0.0f);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.color(0.65f, 0.9f, 1.0f, 0.55f);
        bindEntityTexture(entity);
        drawCube(0.35f);
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

    @Override
    protected ResourceLocation getEntityTexture(AirVortexEntity entity) {
        return TEXTURE;
    }

    private static void drawCube(float size) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        face(buffer, -size, -size, -size, size, size, -size);
        face(buffer, size, -size, size, -size, size, size);
        face(buffer, -size, -size, size, -size, size, -size);
        face(buffer, size, -size, -size, size, size, size);
        face(buffer, -size, size, -size, size, size, size);
        face(buffer, -size, -size, size, size, -size, -size);
        tessellator.draw();
    }

    private static void face(BufferBuilder buffer, float x1, float y1, float z1, float x2, float y2, float z2) {
        buffer.pos(x1, y1, z1).tex(0.0d, 0.0d).endVertex();
        buffer.pos(x2, y1, z1).tex(1.0d, 0.0d).endVertex();
        buffer.pos(x2, y2, z2).tex(1.0d, 1.0d).endVertex();
        buffer.pos(x1, y2, z2).tex(0.0d, 1.0d).endVertex();
    }
}
