package earth.terrarium.adastra.client.render;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
class TexturedBoxModel extends ModelBase {

    private final ModelRenderer body;

    TexturedBoxModel(float width, float height, float depth) {
        textureWidth = 64;
        textureHeight = 64;
        body = new ModelRenderer(this, 0, 0);
        body.addBox(width * -8.0f, height * -8.0f, depth * -8.0f, (int) (width * 16.0f), (int) (height * 16.0f), (int) (depth * 16.0f));
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        body.render(scale);
    }
}
