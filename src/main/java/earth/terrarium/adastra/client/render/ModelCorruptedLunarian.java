package earth.terrarium.adastra.client.render;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
class ModelCorruptedLunarian extends ModelBase {

    private final ModelRenderer head;
    private final ModelRenderer body;
    private final ModelRenderer leftLeg;
    private final ModelRenderer rightLeg;
    private final ModelRenderer arms;
    private final ModelRenderer leftArm;
    private final ModelRenderer rightArm;
    private final ModelRenderer extraArm1;
    private final ModelRenderer extraArm2;
    private final ModelRenderer extraArm3;
    private final ModelRenderer extraArm4;

    ModelCorruptedLunarian() {
        textureWidth = 128;
        textureHeight = 128;

        head = part(0.0f, 0.0f, 0.0f);
        head.mirror = true;
        head.setTextureOffset(0, 19).addBox(-4.0f, -9.0f, -4.0f, 8, 9, 8);
        head.setTextureOffset(0, 0).addBox(-4.5f, -18.0f, -4.5f, 9, 10, 9);
        head.setTextureOffset(0, 20).addBox(-1.0f, -3.0f, -6.0f, 2, 4, 2);
        head.mirror = false;

        body = part(-4.0f, 12.0f, 2.0f);
        body.mirror = true;
        body.setTextureOffset(100, 0).addBox(0.0f, -12.0f, -5.0f, 8, 12, 6);
        body.setTextureOffset(0, 36).addBox(0.0f, -12.0f, -5.0f, 8, 19, 6, 0.5f);
        body.mirror = false;

        leftLeg = part(2.0f, 12.0f, 0.0f);
        leftLeg.mirror = true;
        leftLeg.setTextureOffset(0, 81).addBox(-2.0f, 0.0f, -2.0f, 4, 12, 4);
        leftLeg.mirror = false;

        rightLeg = part(-2.0f, 12.0f, 0.0f);
        rightLeg.setTextureOffset(0, 81).addBox(-2.0f, 0.0f, -2.0f, 4, 12, 4);

        arms = part(0.0f, 2.0f, 0.0f, -1.5708f, 0.0f, 0.0f);
        leftArm = part(6.0f, 0.1434f, 0.2048f);
        leftArm.mirror = true;
        leftArm.setTextureOffset(30, 61).addBox(-2.0f, -2.0f, -2.0f, 4, 12, 4);
        leftArm.mirror = false;
        arms.addChild(leftArm);
        rightArm = part(-6.0f, 0.1434f, 0.2048f);
        rightArm.setTextureOffset(30, 61).addBox(-2.0f, -2.0f, -2.0f, 4, 12, 4);
        arms.addChild(rightArm);

        ModelRenderer extraArms = part(0.0f, 0.0f, 3.0f);
        extraArm1 = part(-1.2968f, 6.4078f, 0.3907f);
        ModelRenderer arm1 = cube(52, 0, 0.2968f, -0.4078f, 0.1093f, -0.6109f, -0.6109f, 0.0f,
            -11.5f, 0.0f, 9.5f, 10, 2, 2);
        arm1.setTextureOffset(36, 0).addBox(-1.5f, 0.0f, -0.5f, 2, 2, 12);
        extraArm1.addChild(arm1);
        extraArms.addChild(extraArm1);

        extraArm2 = part(1.0f, 6.0f, 1.0f);
        ModelRenderer arm2 = part(-0.5f, 0.0f, -0.5f, -0.6109f, 0.6981f, 0.0f);
        arm2.mirror = true;
        arm2.setTextureOffset(52, 0).addBox(2.0f, 0.0f, 9.5f, 10, 2, 2);
        arm2.mirror = false;
        arm2.setTextureOffset(36, 0).addBox(0.0f, 0.0f, -0.5f, 2, 2, 12);
        extraArm2.addChild(arm2);
        extraArms.addChild(extraArm2);

        extraArm3 = part(-1.0f, 4.0f, 1.0f);
        ModelRenderer arm3 = cube(52, 0, 0.0f, 1.0f, -0.5f, 0.6109f, -0.6109f, 0.0f,
            -11.5f, -2.0f, 9.5f, 10, 2, 2);
        arm3.setTextureOffset(36, 0).addBox(-1.5f, -2.0f, -0.5f, 2, 2, 12);
        extraArm3.addChild(arm3);
        extraArms.addChild(extraArm3);

        extraArm4 = part(1.0f, 4.0f, 0.0f);
        ModelRenderer arm4 = part(-0.5f, 1.0f, 0.5f, 0.6109f, 0.6109f, 0.0f);
        arm4.mirror = true;
        arm4.setTextureOffset(52, 0).addBox(2.0f, -2.0f, 9.5f, 10, 2, 2);
        arm4.mirror = false;
        arm4.setTextureOffset(36, 0).addBox(0.0f, -2.0f, -0.5f, 2, 2, 12);
        extraArm4.addChild(arm4);
        extraArms.addChild(extraArm4);
        body.addChild(extraArms);
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw,
                       float headPitch, float scale) {
        head.render(scale);
        body.render(scale);
        leftLeg.render(scale);
        rightLeg.render(scale);
        arms.render(scale);
    }

    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw,
                                  float headPitch, float scaleFactor, Entity entityIn) {
        head.rotateAngleY = netHeadYaw * ((float) Math.PI / 180.0f);
        head.rotateAngleX = headPitch * ((float) Math.PI / 180.0f);
        leftLeg.rotateAngleX = MathHelper.cos(limbSwing * 0.6662f) * -1.0f * limbSwingAmount;
        rightLeg.rotateAngleX = MathHelper.cos(limbSwing * 0.6662f) * limbSwingAmount;

        leftArm.rotateAngleZ = MathHelper.cos(ageInTicks * 0.04f) * 0.04f + 0.04f;
        rightArm.rotateAngleZ = -leftArm.rotateAngleZ;
        leftArm.rotateAngleX = 0.5236f + leftArm.rotateAngleZ;
        rightArm.rotateAngleX = 0.5236f - leftArm.rotateAngleZ;

        float extraSwing = MathHelper.cos(limbSwing * 0.3662f + (float) Math.PI) * limbSwingAmount * 0.5f;
        extraArm1.rotateAngleY = extraSwing;
        extraArm2.rotateAngleY = extraSwing;
        extraArm3.rotateAngleY = extraSwing;
        extraArm4.rotateAngleY = extraSwing;
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
