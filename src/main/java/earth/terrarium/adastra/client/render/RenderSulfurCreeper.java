package earth.terrarium.adastra.client.render;

import earth.terrarium.adastra.Reference;
import earth.terrarium.adastra.common.entities.mob.SulfurCreeperEntity;
import net.minecraft.client.model.ModelCreeper;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
class RenderSulfurCreeper extends RenderLiving<SulfurCreeperEntity> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(Reference.MOD_ID, "textures/entity/mob/sulfur_creeper.png");

    RenderSulfurCreeper(RenderManager renderManager) {
        super(renderManager, createModel(), 0.5f);
        addLayer(new LayerSulfurCreeperCharge(this));
    }

    private static ModelCreeper createModel() {
        ModelCreeper model = new ModelCreeper();
        model.textureWidth = 64;
        model.textureHeight = 64;
        return model;
    }

    @Override
    protected void preRenderCallback(SulfurCreeperEntity entity, float partialTickTime) {
        float flash = entity.getCreeperFlashIntensity(partialTickTime);
        float pulse = 1.0f + MathHelper.sin(flash * 100.0f) * flash * 0.01f;
        flash = MathHelper.clamp(flash, 0.0f, 1.0f);
        flash *= flash;
        flash *= flash;
        float horizontalScale = (1.0f + flash * 0.4f) * pulse;
        float verticalScale = (1.0f + flash * 0.1f) / pulse;
        GlStateManager.scale(horizontalScale, verticalScale, horizontalScale);
    }

    @Override
    protected int getColorMultiplier(SulfurCreeperEntity entity, float lightBrightness, float partialTickTime) {
        float flash = entity.getCreeperFlashIntensity(partialTickTime);
        if ((int) (flash * 10.0f) % 2 == 0) {
            return 0;
        }

        int alpha = MathHelper.clamp((int) (flash * 0.2f * 255.0f), 0, 255);
        return alpha << 24 | 0x30FFFFFF;
    }

    @Override
    protected ResourceLocation getEntityTexture(SulfurCreeperEntity entity) {
        return TEXTURE;
    }
}
