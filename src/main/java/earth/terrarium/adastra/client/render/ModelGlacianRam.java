package earth.terrarium.adastra.client.render;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
class ModelGlacianRam extends ModelBase {

    private static final float PI = (float) Math.PI;

    private final ModelRenderer head;
    private final ModelRenderer body;
    private final ModelRenderer leftFrontLeg;
    private final ModelRenderer rightFrontLeg;
    private final ModelRenderer leftHindLeg;
    private final ModelRenderer rightHindLeg;

    ModelGlacianRam() {
        textureWidth = 64;
        textureHeight = 64;

        head = part(0.0f, 12.8f, -1.9f, 0.0f, PI, 0.0f);
        head.setTextureOffset(3, 19).addBox(-3.5f, -3.8f, -2.9f, 7, 6, 7);
        head.setTextureOffset(50, 53).addBox(1.5f, -5.8f, -0.9f, 4, 2, 2);
        head.setTextureOffset(32, 52).addBox(3.5f, -5.8f, 1.1f, 2, 2, 2);
        head.setTextureOffset(51, 56).addBox(-5.5f, -5.8f, 1.1f, 2, 2, 2);
        head.setTextureOffset(52, 18).addBox(-5.5f, -5.8f, -0.9f, 4, 2, 2);

        ModelRenderer leftEar = part(-2.9417f, -2.791f, 1.35f, 0.0f, 0.0f, -0.9599f);
        leftEar.setTextureOffset(21, 38).addBox(-3.3927f, -0.7456f, -1.5f, 4, 1, 3);
        head.addChild(leftEar);

        ModelRenderer rightEar = part(2.9417f, -2.791f, 1.35f, 0.0f, 0.0f, 0.9599f);
        rightEar.mirror = true;
        rightEar.setTextureOffset(21, 38).addBox(-0.6073f, -0.7456f, -1.5f, 4, 1, 3);
        rightEar.mirror = false;
        head.addChild(rightEar);

        leftFrontLeg = part(2.5f, 20.0f, 0.5f);
        leftFrontLeg.setTextureOffset(0, 54).addBox(-1.5f, -2.0f, -1.5f, 3, 7, 3);

        rightFrontLeg = part(-2.5f, 20.0f, 0.5f);
        rightFrontLeg.setTextureOffset(12, 30).addBox(-1.5f, -2.0f, -1.5f, 3, 7, 3);

        leftHindLeg = part(3.0f, 20.0f, 9.5f);
        leftHindLeg.setTextureOffset(0, 54).addBox(-1.5f, -2.0f, -1.5f, 3, 7, 3);

        rightHindLeg = part(-3.0f, 20.0f, 9.5f);
        rightHindLeg.setTextureOffset(12, 30).addBox(-1.5f, -2.0f, -1.5f, 3, 7, 3);

        body = part(0.25f, 18.8333f, 5.0333f, 0.0f, PI, 0.0f);
        body.setTextureOffset(0, 40).addBox(-1.25f, -2.8333f, -9.1667f, 3, 2, 3);
        body.setTextureOffset(0, 40).addBox(5.75f, 1.1667f, -2.1667f, 0, 2, 6);
        body.setTextureOffset(0, 0).addBox(-5.25f, -6.8333f, -2.1667f, 11, 8, 10);
        body.setTextureOffset(27, 27).addBox(-5.25f, -4.8333f, -7.1667f, 11, 6, 5);
        body.setTextureOffset(0, 40).addBox(-5.25f, 1.1667f, -2.1667f, 0, 2, 6);
        body.addChild(cube(0, 40, 4.75f, 2.1667f, -5.1667f, 0.0f, -1.5708f, 0.0f,
            13.0f, -1.0f, 3.0f, 0, 2, 6));
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw,
                       float headPitch, float scale) {
        body.render(scale);
        head.render(scale);
        leftFrontLeg.render(scale);
        rightFrontLeg.render(scale);
        leftHindLeg.render(scale);
        rightHindLeg.render(scale);
    }

    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw,
                                  float headPitch, float scaleFactor, Entity entityIn) {
        head.rotateAngleY = PI + netHeadYaw * ((float) Math.PI / 180.0f);
        head.rotateAngleX = headPitch * ((float) Math.PI / 180.0f);

        float swing = limbSwing * 0.6662f;
        rightHindLeg.rotateAngleX = MathHelper.cos(swing) * 1.4f * limbSwingAmount;
        leftHindLeg.rotateAngleX = MathHelper.cos(swing + PI) * 1.4f * limbSwingAmount;
        rightFrontLeg.rotateAngleX = MathHelper.cos(swing + PI) * 1.4f * limbSwingAmount;
        leftFrontLeg.rotateAngleX = MathHelper.cos(swing) * 1.4f * limbSwingAmount;
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
