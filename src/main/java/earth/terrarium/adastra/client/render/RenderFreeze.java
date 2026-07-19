package earth.terrarium.adastra.client.render;

import earth.terrarium.adastra.Reference;
import earth.terrarium.adastra.client.model.ModelFreeze;
import earth.terrarium.adastra.common.entities.mob.FreezeEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderFreeze extends RenderLiving<FreezeEntity> {

    private static final ResourceLocation BODY = new ResourceLocation(
        Reference.MOD_ID, "textures/entity/mob/freeze/freeze.png");
    private static final ResourceLocation EYES = new ResourceLocation(
        Reference.MOD_ID, "textures/entity/mob/freeze/freeze_eyes.png");
    private final ModelFreeze model;

    public RenderFreeze(RenderManager manager) {
        this(manager, new ModelFreeze());
    }

    private RenderFreeze(RenderManager manager, ModelFreeze model) {
        super(manager, model, 0.6F);
        this.model = model;
        addLayer(new EyesLayer());
    }

    @Override
    protected ResourceLocation getEntityTexture(FreezeEntity entity) {
        return BODY;
    }

    private final class EyesLayer implements LayerRenderer<FreezeEntity> {
        @Override
        public void doRenderLayer(FreezeEntity entity, float limbSwing, float limbSwingAmount,
                                  float partialTicks, float ageInTicks, float netHeadYaw,
                                  float headPitch, float scale) {
            Minecraft.getMinecraft().getTextureManager().bindTexture(EYES);
            GlStateManager.pushMatrix();
            GlStateManager.disableLighting();
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
            model.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entity);
            model.renderEyes(scale);
            GlStateManager.disableBlend();
            GlStateManager.enableLighting();
            GlStateManager.popMatrix();
        }

        @Override
        public boolean shouldCombineTextures() {
            return false;
        }
    }
}
