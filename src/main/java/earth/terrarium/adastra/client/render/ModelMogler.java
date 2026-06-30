package earth.terrarium.adastra.client.render;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
class ModelMogler extends ModelBase {

    private static final float PI = (float) Math.PI;

    private final ModelRenderer body;
    private final ModelRenderer head;
    private final ModelRenderer rightFrontLeg;
    private final ModelRenderer leftFrontLeg;
    private final ModelRenderer rightBackLeg;
    private final ModelRenderer leftBackLeg;

    ModelMogler() {
        textureWidth = 256;
        textureHeight = 256;

        head = part(0.0f, 10.1899f, -17.7795f);
        head.setTextureOffset(33, 127).addBox(-8.0f, 4.3819f, -6.3545f, 16, 6, 0);
        head.setTextureOffset(97, 68).addBox(-9.0f, -7.3501f, -12.2827f, 18, 6, 0);

        ModelRenderer skull = part(0.0f, 10.8101f, -11.2205f, -0.5236f, 0.0f, 0.0f);
        skull.setTextureOffset(57, 46).addBox(-9.0f, -25.0f, -8.0f, 18, 11, 11);
        skull.setTextureOffset(59, 106).addBox(-8.0f, -14.0f, 2.0f, 16, 6, 4);
        skull.setTextureOffset(56, 68).addBox(-8.0f, -14.0f, -7.0f, 16, 6, 9);
        head.addChild(skull);

        ModelRenderer rightTusk = part(4.4912f, 4.0149f, -10.184f, -0.4164f, -0.3272f, -0.6284f);
        rightTusk.mirror = true;
        rightTusk.setTextureOffset(0, 102).addBox(-2.0f, -2.5f, -2.5f, 7, 5, 9);
        rightTusk.mirror = false;
        head.addChild(rightTusk);

        ModelRenderer leftTusk = part(-4.4912f, 4.0149f, -10.184f, -0.4164f, 0.3272f, 0.6284f);
        leftTusk.setTextureOffset(0, 102).addBox(-5.0f, -2.5f, -2.5f, 7, 5, 9);
        head.addChild(leftTusk);

        body = part(0.0f, 11.4541f, -6.7261f);
        body.setTextureOffset(0, 0).addBox(-5.0f, -0.4541f, -13.2739f, 10, 6, 28);
        body.addChild(cube(0, 34, 0.0f, 0.0f, 0.0f, -1.1781f, 0.0f, 0.0f,
            -11.0f, 0.5f, -13.0f, 22, 11, 12));
        body.addChild(cube(48, 0, 0.0f, 0.0f, 0.0f, -1.3963f, 0.0f, 0.0f,
            -10.0f, -7.5f, -10.875f, 20, 13, 12));
        body.addChild(cube(0, 57, 0.0f, 0.0f, 0.0f, -1.4835f, 0.0f, 0.0f,
            -9.0f, -15.5f, -8.0f, 18, 14, 10));
        body.addChild(cube(66, 25, 0.0f, 0.0f, 0.0f, -1.8326f, 0.0f, 0.0f,
            -8.0f, -20.5f, -8.0f, 16, 10, 10));

        rightBackLeg = part(-7.5f, 13.0f, 2.5f);
        rightBackLeg.setTextureOffset(36, 83).addBox(-4.5f, -1.0f, -4.5f, 9, 12, 9);

        rightFrontLeg = part(-7.5f, 13.0f, -16.5f);
        rightFrontLeg.setTextureOffset(99, 95).addBox(-4.5f, -1.0f, -4.5f, 9, 12, 9);

        leftBackLeg = part(7.5f, 13.0f, 2.5f);
        leftBackLeg.setTextureOffset(0, 81).addBox(-4.5f, -1.0f, -4.5f, 9, 12, 9);

        leftFrontLeg = part(7.5f, 13.0f, -16.5f);
        leftFrontLeg.setTextureOffset(72, 83).addBox(-4.5f, -1.0f, -4.5f, 9, 12, 9);
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw,
                       float headPitch, float scale) {
        body.render(scale);
        head.render(scale);
        rightFrontLeg.render(scale);
        leftFrontLeg.render(scale);
        rightBackLeg.render(scale);
        leftBackLeg.render(scale);
    }

    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw,
                                  float headPitch, float scaleFactor, Entity entityIn) {
        head.rotateAngleY = netHeadYaw * ((float) Math.PI / 180.0f);
        head.rotateAngleX = headPitch * ((float) Math.PI / 180.0f);

        float swing = limbSwing * 0.6662f;
        rightFrontLeg.rotateAngleX = MathHelper.cos(swing) * 1.2f * limbSwingAmount;
        leftFrontLeg.rotateAngleX = MathHelper.cos(swing + PI) * 1.2f * limbSwingAmount;
        rightBackLeg.rotateAngleX = rightFrontLeg.rotateAngleX;
        leftBackLeg.rotateAngleX = leftFrontLeg.rotateAngleX;
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
