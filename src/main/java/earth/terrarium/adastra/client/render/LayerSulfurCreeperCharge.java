package earth.terrarium.adastra.client.render;

import earth.terrarium.adastra.common.entities.mob.SulfurCreeperEntity;
import net.minecraft.client.model.ModelCreeper;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
class LayerSulfurCreeperCharge implements LayerRenderer<SulfurCreeperEntity> {

    private static final ResourceLocation TEXTURE = new ResourceLocation("textures/entity/creeper/creeper_armor.png");

    private final RenderSulfurCreeper renderer;
    private final ModelCreeper model = new ModelCreeper(2.0f);

    LayerSulfurCreeperCharge(RenderSulfurCreeper renderer) {
        this.renderer = renderer;
    }

    @Override
    public void doRenderLayer(SulfurCreeperEntity entity, float limbSwing, float limbSwingAmount, float partialTicks,
                              float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        if (!entity.getPowered()) {
            return;
        }

        GlStateManager.depthMask(!entity.isInvisible());
        renderer.bindTexture(TEXTURE);
        GlStateManager.matrixMode(5890);
        GlStateManager.loadIdentity();
        float offset = (float) entity.ticksExisted + partialTicks;
        GlStateManager.translate(offset * 0.01f, offset * 0.01f, 0.0f);
        GlStateManager.matrixMode(5888);
        GlStateManager.enableBlend();
        GlStateManager.color(0.5f, 0.5f, 0.5f, 1.0f);
        GlStateManager.disableLighting();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
        model.setModelAttributes(renderer.getMainModel());
        model.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
        GlStateManager.matrixMode(5890);
        GlStateManager.loadIdentity();
        GlStateManager.matrixMode(5888);
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.enableLighting();
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.depthMask(true);
        GlStateManager.disableBlend();
    }

    @Override
    public boolean shouldCombineTextures() {
        return false;
    }
}
