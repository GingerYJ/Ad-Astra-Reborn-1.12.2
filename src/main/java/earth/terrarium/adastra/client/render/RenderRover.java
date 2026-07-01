package earth.terrarium.adastra.client.render;

import earth.terrarium.adastra.common.entities.vehicles.Tier1RoverEntity;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
class RenderRover extends Render<Tier1RoverEntity> {

    private final ModelBase model = new ModelRover();
    private final ResourceLocation texture;

    RenderRover(RenderManager renderManager, ResourceLocation texture) {
        super(renderManager);
        this.texture = texture;
        this.shadowSize = 1.0f;
    }

    @Override
    public void doRender(Tier1RoverEntity entity, double x, double y, double z, float entityYaw, float partialTicks) {
        GlStateManager.pushMatrix();
        GlStateManager.translate((float) x, (float) y + 1.55f, (float) z);
        GlStateManager.rotate(180.0f - entityYaw, 0.0f, 1.0f, 0.0f);
        float pitch = entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks;
        GlStateManager.rotate(-pitch, 0.0f, 0.0f, 1.0f);
        GlStateManager.scale(-1.0f, -1.0f, 1.0f);
        bindEntityTexture(entity);
        GlStateManager.disableCull();
        model.setRotationAngles(0.0f, 0.0f, partialTicks, 0.0f, 0.0f, 0.0625f, entity);
        model.render(entity, 0.0f, 0.0f, partialTicks, 0.0f, 0.0f, 0.0625f);
        GlStateManager.enableCull();
        GlStateManager.popMatrix();
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

    @Override
    protected ResourceLocation getEntityTexture(Tier1RoverEntity entity) {
        return texture;
    }
}
