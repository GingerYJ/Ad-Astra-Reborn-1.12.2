package earth.terrarium.adastra.client.render;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
class ModelPygroBrute extends ModelBiped {

    ModelPygroBrute() {
        super(0.0f, 0.0f, 64, 64);

        bipedHeadwear = part(0.0f, 0.0f, 0.0f);

        bipedHead = part(-0.5f, 0.0f, -1.0f);
        bipedHead.setTextureOffset(0, 0).addBox(-4.5f, -8.0f, -4.0f, 8, 8, 8);
        bipedHead.setTextureOffset(32, 44).addBox(-3.5f, -4.0f, -6.0f, 6, 3, 2);
        bipedHead.addChild(cube(0, 0, -6.4448f, -2.1679f, 0.0f, 0.0f, 0.0f, 0.4363f,
            -2.0f, -1.5f, -0.5f, 3, 3, 1));
        bipedHead.addChild(cube(0, 0, 6.2052f, -2.5179f, 2.0f, 0.2182f, 0.0f, -0.4363f,
            -2.0f, -1.5f, -0.5f, 3, 3, 1));
        bipedHead.addChild(cube(0, 0, 5.9787f, -2.4122f, -1.0f, -0.1745f, 0.0f, -0.4363f,
            -1.5f, -1.5f, -0.5f, 3, 3, 1));
        bipedHead.addChild(cube(32, 32, -5.2424f, -4.361f, 0.0f, 0.0f, 0.0f, 0.4363f,
            -1.0f, -3.0f, -3.0f, 2, 6, 6));
        bipedHead.addChild(cube(18, 17, -0.5f, -0.7679f, -4.999f, 1.5708f, 0.0f, 0.0f,
            -2.0f, 0.0f, -3.5f, 4, 0, 2));
        bipedHead.addChild(cube(22, 0, -0.5f, 0.2995f, -4.7491f, 1.0472f, 0.0f, 0.0f,
            -2.0f, 0.0f, -0.5f, 4, 0, 2));
        bipedHead.addChild(cube(42, 35, -0.5f, 0.5f, -4.0f, 1.0472f, 0.0f, 0.0f,
            -3.0f, -0.5f, -1.0f, 6, 1, 2));
        bipedHead.addChild(cube(0, 4, -2.0f, -9.25f, -1.5f, 2.4435f, -0.7854f, -3.1416f,
            -0.5f, 0.25f, -0.5f, 1, 2, 2));
        bipedHead.addChild(cube(0, 4, -2.0f, -9.25f, 0.5f, -0.6981f, -1.0036f, 0.0f,
            -0.5f, 0.25f, -0.5f, 1, 2, 2));
        bipedHead.addChild(cube(24, 2, 0.0f, -9.25f, 1.5f, -0.6981f, 0.0f, 0.0f,
            -0.5f, -0.75f, -0.5f, 1, 3, 2));
        bipedHead.addChild(cube(24, 2, 1.25f, -9.25f, -1.5f, 2.4435f, 1.0036f, 3.1416f,
            -0.5f, -0.75f, -0.5f, 1, 3, 2));
        bipedHead.addChild(cube(40, 16, 4.2424f, -4.361f, 0.0f, 0.0f, 0.0f, -0.4363f,
            -1.0f, -3.0f, -3.0f, 2, 6, 6));

        bipedRightLeg = part(1.0f, 12.0f, 0.0f);
        bipedRightLeg.setTextureOffset(42, 28).addBox(-2.5f, 4.0f, -3.5f, 5, 2, 5);
        bipedRightLeg.setTextureOffset(16, 32).addBox(-2.0f, 0.0f, -3.0f, 4, 12, 4);

        bipedLeftLeg = part(-3.0f, 12.0f, 0.0f);
        bipedLeftLeg.setTextureOffset(32, 0).addBox(-2.0f, 0.0f, -3.0f, 4, 12, 4);
        bipedLeftLeg.setTextureOffset(42, 28).addBox(-2.5f, 4.0f, -3.5f, 5, 2, 5);

        bipedRightArm = part(-5.0f, 2.0f, 0.0f);
        bipedRightArm.setTextureOffset(24, 16).addBox(-4.0f, -2.0f, -3.0f, 4, 12, 4);

        bipedLeftArm = part(5.0f, 2.0f, 0.0f);
        bipedLeftArm.setTextureOffset(0, 32).addBox(-2.0f, -2.0f, -3.0f, 4, 12, 4);

        bipedBody = part(-0.5f, 0.0f, -1.0f);
        bipedBody.setTextureOffset(0, 16).addBox(-4.5f, 0.0f, -2.0f, 8, 12, 4);
        bipedBody.setTextureOffset(0, 48).addBox(-2.5f, 7.0f, -3.0f, 4, 4, 1);
    }

    private ModelRenderer cube(int textureX, int textureY, float pointX, float pointY, float pointZ,
                               float rotateX, float rotateY, float rotateZ, float boxX, float boxY,
                               float boxZ, int width, int height, int depth) {
        ModelRenderer renderer = part(pointX, pointY, pointZ, rotateX, rotateY, rotateZ);
        renderer.setTextureOffset(textureX, textureY).addBox(boxX, boxY, boxZ, width, height, depth);
        return renderer;
    }

    private ModelRenderer part(float pointX, float pointY, float pointZ, float rotateX, float rotateY,
                               float rotateZ) {
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
