package earth.terrarium.adastra.client.render;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
class ModelStarCrawler extends ModelBase {

    private final ModelRenderer body;
    private final ModelRenderer leg1;
    private final ModelRenderer leg2;
    private final ModelRenderer leg3;
    private final ModelRenderer leg4;

    ModelStarCrawler() {
        textureWidth = 128;
        textureHeight = 128;

        leg1 = part(0.0f, 24.0f, 0.0f);
        leg1.setTextureOffset(0, 27).addBox(-16.0f, -9.0f, -6.0f, 8, 9, 12);
        leg1.setTextureOffset(0, 51).addBox(-24.0f, -7.0f, -5.0f, 8, 7, 10);
        leg1.setTextureOffset(28, 61).addBox(-29.0f, -5.0f, -4.0f, 5, 5, 8);
        leg1.addChild(cube(51, 44, -16.0f, 0.804f, -5.8016f, 0.2618f, 0.0f, 0.0f, -7.0f, -2.5f, -3.5f, 7, 0, 5));
        leg1.addChild(cube(0, 0, -24.0f, 0.804f, -4.8016f, 0.2618f, 0.0f, 0.0f, -4.0f, -2.5f, -3.5f, 4, 0, 5));
        leg1.addChild(cube(23, 32, -24.0f, 1.3216f, 6.7334f, -0.2618f, 0.0f, 0.0f, -4.0f, -2.5f, -3.5f, 4, 0, 5));
        leg1.addChild(cube(65, 44, -16.0f, 1.3216f, 7.7334f, -0.2618f, 0.0f, 0.0f, -7.0f, -2.5f, -3.5f, 7, 0, 5));
        leg1.addChild(cube(21, 48, -9.0f, 1.3216f, 8.7334f, -0.2618f, 0.0f, 0.0f, -7.0f, -2.5f, -3.5f, 7, 0, 5));
        leg1.addChild(cube(23, 27, -9.0f, 0.804f, -6.8016f, 0.2618f, 0.0f, 0.0f, -7.0f, -2.5f, -3.5f, 7, 0, 5));

        leg2 = part(0.0f, 24.0f, 0.0f);
        leg2.setTextureOffset(40, 27).addBox(-6.0f, -9.0f, 8.0f, 12, 9, 8);
        leg2.setTextureOffset(58, 53).addBox(-5.0f, -7.0f, 16.0f, 10, 7, 8);
        leg2.setTextureOffset(54, 68).addBox(-4.0f, -5.0f, 24.0f, 8, 5, 5);
        leg2.addChild(cube(0, 23, -6.4146f, -1.352f, 24.5f, 0.0f, 0.0f, 1.309f, 0.0f, -2.5f, -0.5f, 0, 3, 4));
        leg2.addChild(cube(0, 26, 4.4827f, -1.8696f, 24.5f, 0.0f, 0.0f, 1.8326f, 0.0f, -2.5f, -0.5f, 0, 3, 4));
        leg2.addChild(cube(56, 42, 6.4486f, -1.6108f, 19.5f, 0.0f, 0.0f, 1.8326f, 0.0f, -2.5f, -3.5f, 0, 4, 7));
        leg2.addChild(cube(0, 5, -7.4146f, -1.352f, 19.5f, 0.0f, 0.0f, 1.309f, 0.0f, -2.5f, -3.5f, 0, 4, 7));
        leg2.addChild(cube(0, 0, 8.4146f, -1.352f, 12.5f, 0.0f, 0.0f, 1.8326f, 0.0f, -2.5f, -3.5f, 0, 5, 7));
        leg2.addChild(cube(26, 67, -8.4146f, -1.352f, 12.5f, 0.0f, 0.0f, 1.309f, 0.0f, -2.5f, -3.5f, 0, 5, 7));

        leg3 = part(0.0f, 24.0f, 0.0f);
        leg3.setTextureOffset(48, 0).addBox(-5.0f, -7.0f, -24.0f, 10, 7, 8);
        leg3.setTextureOffset(64, 15).addBox(-4.0f, -5.0f, -29.0f, 8, 5, 5);
        leg3.setTextureOffset(40, 27).addBox(-6.0f, -9.0f, -16.0f, 12, 9, 8);
        leg3.addChild(cube(0, 26, 4.4827f, -1.8696f, -27.5f, 0.0f, 0.0f, 1.8326f, 0.0f, -2.5f, -0.5f, 0, 3, 4));
        leg3.addChild(cube(56, 42, 6.4486f, -1.6108f, -19.5f, 0.0f, 0.0f, 1.8326f, 0.0f, -2.5f, -3.5f, 0, 4, 7));
        leg3.addChild(cube(26, 67, -8.4146f, -1.352f, -12.5f, 0.0f, 0.0f, 1.309f, 0.0f, -2.5f, -3.5f, 0, 5, 7));
        leg3.addChild(cube(0, 23, -6.4146f, -1.352f, -27.5f, 0.0f, 0.0f, 1.309f, 0.0f, -2.5f, -0.5f, 0, 3, 4));
        leg3.addChild(cube(0, 5, -7.4146f, -1.352f, -19.5f, 0.0f, 0.0f, 1.309f, 0.0f, -2.5f, -3.5f, 0, 4, 7));
        leg3.addChild(cube(0, 0, 8.4146f, -1.352f, -11.5f, 0.0f, 0.0f, 1.8326f, 0.0f, -2.5f, -3.5f, 0, 5, 7));

        leg4 = part(0.0f, 24.0f, 0.0f);
        leg4.setTextureOffset(30, 44).addBox(16.0f, -7.0f, -5.0f, 8, 7, 10);
        leg4.setTextureOffset(0, 68).addBox(24.0f, -5.0f, -4.0f, 5, 5, 8);
        leg4.setTextureOffset(0, 27).addBox(8.0f, -9.0f, -6.0f, 8, 9, 12);
        leg4.addChild(cube(23, 32, 28.0f, 1.3216f, 6.7334f, -0.2618f, 0.0f, 0.0f, -4.0f, -2.5f, -3.5f, 4, 0, 5));
        leg4.addChild(cube(0, 0, 28.0f, 0.804f, -4.8016f, 0.2618f, 0.0f, 0.0f, -4.0f, -2.5f, -3.5f, 4, 0, 5));
        leg4.addChild(cube(65, 44, 23.0f, 1.3216f, 7.7334f, -0.2618f, 0.0f, 0.0f, -7.0f, -2.5f, -3.5f, 7, 0, 5));
        leg4.addChild(cube(51, 44, 23.0f, 0.804f, -5.8016f, 0.2618f, 0.0f, 0.0f, -7.0f, -2.5f, -3.5f, 7, 0, 5));
        leg4.addChild(cube(21, 48, 16.0f, 1.3216f, 8.7334f, -0.2618f, 0.0f, 0.0f, -7.0f, -2.5f, -3.5f, 7, 0, 5));
        leg4.addChild(cube(23, 27, 16.0f, 0.804f, -6.8016f, 0.2618f, 0.0f, 0.0f, -7.0f, -2.5f, -3.5f, 7, 0, 5));

        body = part(0.0f, 24.0f, 0.0f);
        body.setTextureOffset(0, 0).addBox(-8.0f, -11.0f, -8.0f, 16, 11, 16);
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw,
                       float headPitch, float scale) {
        body.render(scale);
        leg1.render(scale);
        leg2.render(scale);
        leg3.render(scale);
        leg4.render(scale);
    }

    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw,
                                  float headPitch, float scaleFactor, Entity entityIn) {
        float swing = MathHelper.cos(limbSwing * 0.6662f) * limbSwingAmount;
        leg1.rotateAngleY = swing;
        leg2.rotateAngleY = swing;
        leg3.rotateAngleY = swing;
        leg4.rotateAngleY = swing;
    }

    private ModelRenderer cube(int textureX, int textureY, float pointX, float pointY, float pointZ,
                               float rotateX, float rotateY, float rotateZ, float boxX, float boxY, float boxZ,
                               int width, int height, int depth) {
        ModelRenderer renderer = part(pointX, pointY, pointZ, rotateX, rotateY, rotateZ);
        renderer.setTextureOffset(textureX, textureY).addBox(boxX, boxY, boxZ, width, height, depth);
        return renderer;
    }

    private ModelRenderer part(float pointX, float pointY, float pointZ, float rotateX, float rotateY, float rotateZ) {
        ModelRenderer renderer = part(pointX, pointY, pointZ);
        renderer.rotateAngleX = rotateX;
        renderer.rotateAngleY = rotateY;
        renderer.rotateAngleZ = rotateZ;
        return renderer;
    }

    private ModelRenderer part(float pointX, float pointY, float pointZ) {
        ModelRenderer renderer = new ModelRenderer(this);
        renderer.setRotationPoint(pointX, pointY, pointZ);
        return renderer;
    }
}
