package earth.terrarium.adastra.client.render;

import earth.terrarium.adastra.common.entities.vehicles.AdAstraVehicleEntity;
import earth.terrarium.adastra.common.entities.vehicles.ConfigurableRocketEntity;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
class RenderRocket<T extends AdAstraVehicleEntity> extends Render<T> {

    private final ModelBase model;
    private final java.util.Map<Integer, ModelBase> modelCache = new java.util.HashMap<>();
    private final ResourceLocation texture;

    RenderRocket(RenderManager renderManager, int tier, ResourceLocation texture, float shadowSize) {
        super(renderManager);
        this.model = new ModelRocket(tier);
        this.texture = texture;
        this.shadowSize = shadowSize;
    }

    @Override
    public void doRender(T entity, double x, double y, double z, float entityYaw, float partialTicks) {
        GlStateManager.pushMatrix();
        GlStateManager.translate((float) x, (float) y + 1.55f, (float) z);
        GlStateManager.rotate(180.0f - entityYaw, 0.0f, 1.0f, 0.0f);
        float pitch = entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks;
        GlStateManager.rotate(-pitch, 0.0f, 0.0f, 1.0f);
        GlStateManager.scale(-1.0f, -1.0f, 1.0f);
        bindEntityTexture(entity);
        GlStateManager.disableCull();
        ModelBase renderModel = modelFor(entity);
        renderModel.setRotationAngles(0.0f, 0.0f, partialTicks, 0.0f, 0.0f, 0.0625f, entity);
        renderModel.render(entity, 0.0f, 0.0f, partialTicks, 0.0f, 0.0f, 0.0625f);
        GlStateManager.enableCull();
        GlStateManager.popMatrix();
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

    private ModelBase modelFor(T entity) {
        if (entity instanceof ConfigurableRocketEntity) {
            ConfigurableRocketEntity rocket = (ConfigurableRocketEntity) entity;
            int modelTier = rocket.getModelTier();
            boolean extendra = rocket.getRocketSpec().usesExtendraModel();
            int cacheKey = extendra ? 100 + modelTier : modelTier;
            ModelBase cached = modelCache.get(cacheKey);
            if (cached == null) {
                cached = extendra
                    ? new ModelExtendraRocket(modelTier)
                    : new ModelRocket(modelTier);
                modelCache.put(cacheKey, cached);
            }
            return cached;
        }
        return model;
    }

    @Override
    protected ResourceLocation getEntityTexture(T entity) {
        if (entity instanceof ConfigurableRocketEntity) {
            return ConfigurableRocketTextureManager.textureFor(((ConfigurableRocketEntity) entity).getRocketSpec());
        }
        return texture;
    }
}
