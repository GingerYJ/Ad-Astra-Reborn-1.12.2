package earth.terrarium.adastra.client.render;

import earth.terrarium.adastra.Reference;
import earth.terrarium.adastra.common.entities.misc.SpacePaintingEntity;
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
class RenderSpacePainting extends Render<SpacePaintingEntity> {

    RenderSpacePainting(RenderManager renderManager) {
        super(renderManager);
    }

    @Override
    public void doRender(SpacePaintingEntity entity, double x, double y, double z, float entityYaw, float partialTicks) {
        GlStateManager.pushMatrix();
        GlStateManager.translate((float) x, (float) y, (float) z);
        GlStateManager.rotate(180.0f - entityYaw, 0.0f, 1.0f, 0.0f);
        GlStateManager.scale(0.0625f, 0.0625f, 0.0625f);
        bindEntityTexture(entity);
        renderQuad(entity.getWidthPixels(), entity.getHeightPixels());
        GlStateManager.popMatrix();
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

    private void renderQuad(int width, int height) {
        float left = -width / 2.0f;
        float right = width / 2.0f;
        float bottom = -height / 2.0f;
        float top = height / 2.0f;

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_NORMAL);
        buffer.pos(right, top, -0.5f).tex(0.0f, 0.0f).normal(0.0f, 0.0f, -1.0f).endVertex();
        buffer.pos(left, top, -0.5f).tex(1.0f, 0.0f).normal(0.0f, 0.0f, -1.0f).endVertex();
        buffer.pos(left, bottom, -0.5f).tex(1.0f, 1.0f).normal(0.0f, 0.0f, -1.0f).endVertex();
        buffer.pos(right, bottom, -0.5f).tex(0.0f, 1.0f).normal(0.0f, 0.0f, -1.0f).endVertex();
        buffer.pos(left, top, 0.5f).tex(0.0f, 0.0f).normal(0.0f, 0.0f, 1.0f).endVertex();
        buffer.pos(right, top, 0.5f).tex(1.0f, 0.0f).normal(0.0f, 0.0f, 1.0f).endVertex();
        buffer.pos(right, bottom, 0.5f).tex(1.0f, 1.0f).normal(0.0f, 0.0f, 1.0f).endVertex();
        buffer.pos(left, bottom, 0.5f).tex(0.0f, 1.0f).normal(0.0f, 0.0f, 1.0f).endVertex();
        tessellator.draw();
    }

    @Override
    protected ResourceLocation getEntityTexture(SpacePaintingEntity entity) {
        return new ResourceLocation(Reference.MOD_ID, "textures/painting/" + entity.getVariant().getId() + ".png");
    }
}
