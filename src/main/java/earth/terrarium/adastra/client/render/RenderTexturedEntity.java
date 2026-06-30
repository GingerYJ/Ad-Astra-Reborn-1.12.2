package earth.terrarium.adastra.client.render;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
class RenderTexturedEntity<T extends Entity> extends Render<T> {

    private final ModelBase model;
    private final ResourceLocation texture;

    RenderTexturedEntity(RenderManager renderManager, ResourceLocation texture, float width, float height, float depth, float shadowSize) {
        super(renderManager);
        this.texture = texture;
        this.model = new TexturedBoxModel(width, height, depth);
        this.shadowSize = shadowSize;
    }

    @Override
    public void doRender(T entity, double x, double y, double z, float entityYaw, float partialTicks) {
        GlStateManager.pushMatrix();
        GlStateManager.translate((float) x, (float) y + entity.height * 0.5f, (float) z);
        GlStateManager.rotate(180.0f - entityYaw, 0.0f, 1.0f, 0.0f);
        bindEntityTexture(entity);
        model.render(entity, 0.0f, 0.0f, partialTicks, 0.0f, 0.0f, 0.0625f);
        GlStateManager.popMatrix();
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

    @Override
    protected ResourceLocation getEntityTexture(T entity) {
        return texture;
    }
}
