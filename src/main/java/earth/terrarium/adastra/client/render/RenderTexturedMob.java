package earth.terrarium.adastra.client.render;

import earth.terrarium.adastra.common.entities.AdAstraPlaceholderMob;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
class RenderTexturedMob<T extends AdAstraPlaceholderMob> extends RenderLiving<T> {

    private final ResourceLocation texture;

    RenderTexturedMob(RenderManager renderManager, ModelBase model, ResourceLocation texture, float shadowSize) {
        super(renderManager, model, shadowSize);
        this.texture = texture;
    }

    @Override
    protected ResourceLocation getEntityTexture(T entity) {
        return texture;
    }
}
