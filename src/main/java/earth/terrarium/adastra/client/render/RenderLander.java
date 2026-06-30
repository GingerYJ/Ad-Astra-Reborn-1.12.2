package earth.terrarium.adastra.client.render;

import earth.terrarium.adastra.common.entities.vehicles.LanderEntity;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
class RenderLander extends Render<LanderEntity> {

    private final ModelBase model = new ModelLander();
    private final ResourceLocation texture;

    RenderLander(RenderManager renderManager, ResourceLocation texture) {
        super(renderManager);
        this.texture = texture;
        this.shadowSize = 0.6f;
    }

    @Override
    public void doRender(LanderEntity entity, double x, double y, double z, float entityYaw, float partialTicks) {
        GlStateManager.pushMatrix();
        GlStateManager.translate((float) x, (float) y + entity.height * 0.5f, (float) z);
        GlStateManager.rotate(180.0f - entityYaw, 0.0f, 1.0f, 0.0f);
        float pitch = entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks;
        GlStateManager.rotate(-pitch, 0.0f, 0.0f, 1.0f);
        bindEntityTexture(entity);
        model.render(entity, 0.0f, 0.0f, partialTicks, 0.0f, 0.0f, 0.0625f);
        GlStateManager.popMatrix();
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

    @Override
    protected ResourceLocation getEntityTexture(LanderEntity entity) {
        return texture;
    }
}
