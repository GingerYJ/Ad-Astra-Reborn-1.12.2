package earth.terrarium.adastra.client.render;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
class ModelZombifiedPygro extends ModelBiped {

    ModelZombifiedPygro() {
        super(0.0f, 0.0f, 64, 64);

        bipedHeadwear = part(0.0f, 0.0f, 0.0f);

        bipedHead = part(-0.5f, 0.0f, -1.0f);
        bipedHead.setTextureOffset(0, 0).addBox(-4.5f, -8.0f, -4.0f, 8, 8, 8);
        bipedHead.setTextureOffset(20, 51).addBox(-3.5f, -4.0f, -6.0f, 6, 3, 2);
        bipedHead.addChild(cube(32, 32, -0.5f, 0.0f, 0.0f, 0.0f, 0.0f, 0.4363f,
            -7.1411f, -4.9482f, -3.0f, 2, 6, 6));
        ModelRenderer snout = cube(33, 46, -0.5f, 0.0f, 0.0f, 1.0472f, 0.0f, 0.0f,
            -2.0f, -3.9631f, -4.134f, 4, 0, 3);
        snout.setTextureOffset(21, 57).addBox(-3.0f, -3.7141f, -3.433f, 6, 1, 2);
        bipedHead.addChild(snout);
        ModelRenderer rightEar = cube(43, 22, -0.5f, 0.0f, 0.0f, 0.0f, 0.0f, -0.4363f,
            5.1411f, -0.9482f, -1.0f, 2, 2, 4);
        rightEar.setTextureOffset(40, 16).addBox(5.1411f, -4.9482f, -3.0f, 2, 4, 6);
        bipedHead.addChild(rightEar);

        bipedRightLeg = part(1.0f, 12.0f, 0.0f);
        bipedRightLeg.setTextureOffset(0, 48).addBox(-2.5f, 5.0f, -3.5f, 5, 2, 5);
        bipedRightLeg.setTextureOffset(16, 32).addBox(-2.0f, 0.0f, -3.0f, 4, 12, 4);

        bipedLeftLeg = part(-3.5f, 12.0f, 0.0f);
        bipedLeftLeg.setTextureOffset(32, 0).addBox(-1.5f, 0.0f, -3.0f, 4, 12, 4);
        bipedLeftLeg.setTextureOffset(0, 48).addBox(-2.0f, 2.0f, -3.5f, 5, 2, 5);

        bipedRightArm = part(-5.0f, 2.0f, 0.0f);
        bipedRightArm.setTextureOffset(24, 16).addBox(-4.0f, -2.0f, -3.0f, 4, 6, 4);
        bipedRightArm.addChild(cube(38, 49, -2.0f, 7.0f, -1.0f, 0.0f, -2.3562f, 0.0f,
            0.0f, -3.0f, -3.0f, 0, 6, 6));
        bipedRightArm.addChild(cube(38, 49, -2.0f, 7.0f, -1.0f, 0.0f, -0.7854f, 0.0f,
            0.0f, -3.0f, -3.0f, 0, 6, 6));

        bipedLeftArm = part(5.0f, 2.0f, 0.0f);
        bipedLeftArm.setTextureOffset(0, 32).addBox(-2.0f, -2.0f, -3.0f, 4, 12, 4);

        bipedBody = part(-0.5f, 0.0f, -1.0f);
        bipedBody.setTextureOffset(0, 16).addBox(-4.5f, 0.0f, -2.0f, 8, 12, 4);
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
