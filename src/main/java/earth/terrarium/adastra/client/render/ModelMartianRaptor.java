package earth.terrarium.adastra.client.render;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
class ModelMartianRaptor extends ModelBase {

    private final ModelRenderer body;
    private final ModelRenderer head;
    private final ModelRenderer leftLeg;
    private final ModelRenderer rightLeg;

    ModelMartianRaptor() {
        textureWidth = 128;
        textureHeight = 128;

        leftLeg = part(6.5f, 13.267f, 3.3841f, 0.0f, 0.0f, 0.0f);
        leftLeg.setTextureOffset(0, 65).addBox(-2.5f, 7.733f, -5.1341f, 5, 3, 5);
        leftLeg.addChild(cube(0, 0, -7.75f, -0.3977f, 4.635f, -0.3491f, 0.0f, 0.0f,
            5.75f, 2.0f, -2.0f, 4, 0, 5));
        ModelRenderer leftThigh = part(-7.75f, -1.7831f, 3.2889f, -0.7418f, 0.0f, 0.0f);
        leftThigh.setTextureOffset(68, 35).addBox(5.75f, 7.5f, 0.9f, 4, 5, 3);
        leftThigh.setTextureOffset(50, 39).addBox(5.25f, 2.5f, -4.0f, 5, 5, 8);
        leftLeg.addChild(leftThigh);

        rightLeg = part(-6.5f, 13.267f, 3.3841f, 0.0f, 0.0f, 0.0f);
        rightLeg.setTextureOffset(0, 65).addBox(-2.5f, 7.733f, -5.1341f, 5, 3, 5);
        rightLeg.addChild(cube(0, 0, -7.75f, -0.3977f, 4.635f, -0.3491f, 0.0f, 0.0f,
            5.75f, 2.0f, -2.0f, 4, 0, 5));
        ModelRenderer rightThigh = part(-7.75f, -1.7831f, 3.2889f, -0.7418f, 0.0f, 0.0f);
        rightThigh.setTextureOffset(68, 35).addBox(5.75f, 7.5f, 0.9f, 4, 5, 3);
        rightThigh.setTextureOffset(50, 39).addBox(5.25f, 2.5f, -4.0f, 5, 5, 8);
        rightLeg.addChild(rightThigh);

        body = part(0.0f, 12.0f, 0.0f, 0.0f, 0.0f, 0.0f);
        body.setTextureOffset(0, 0).addBox(-4.0f, -5.0f, -10.0f, 8, 9, 14);
        ModelRenderer chest = part(3.5f, -4.5f, 0.0f, 0.2519f, -0.2443f, 0.7543f);
        chest.setTextureOffset(28, 29).addBox(-9.5f, 1.5f, -7.0f, 5, 9, 9);
        chest.setTextureOffset(1, 24).addBox(-3.5f, -4.5f, -7.0f, 9, 5, 9);
        body.addChild(chest);

        ModelRenderer tail = part(-0.75f, -0.3318f, 6.5041f, 0.0f, 0.0f, 0.0f);
        tail.setTextureOffset(42, 52).addBox(-1.25f, 3.3318f, 6.4959f, 4, 4, 8);
        tail.setTextureOffset(0, 25).addBox(0.75f, -0.6682f, 9.4959f, 0, 4, 4);
        ModelRenderer tailBase = part(0.0f, 0.0f, 0.0f, -0.7418f, 0.0f, 0.0f);
        tailBase.setTextureOffset(0, 18).addBox(0.75f, -7.5f, -4.0f, 0, 6, 5);
        tailBase.setTextureOffset(0, 39).addBox(-2.75f, -1.5f, -5.0f, 7, 6, 8);
        tail.addChild(tailBase);
        ModelRenderer tailMid = part(0.0f, 0.0f, 0.0f, -0.3491f, 0.0f, 0.0f);
        tailMid.setTextureOffset(28, 19).addBox(0.75f, -5.0f, 4.0f, 0, 5, 4);
        tailMid.setTextureOffset(22, 47).addBox(-2.25f, 0.0f, 1.0f, 6, 5, 8);
        tail.addChild(tailMid);
        ModelRenderer tailTip = part(0.75f, 5.3318f, 18.4959f, 0.1745f, 0.0f, 0.0f);
        tailTip.setTextureOffset(0, 4).addBox(0.0f, -5.2f, -3.25f, 0, 3, 6);
        tailTip.setTextureOffset(36, 14).addBox(-4.5f, -0.2f, 1.75f, 9, 0, 8);
        tailTip.setTextureOffset(0, 53).addBox(-1.5f, -2.2f, -4.25f, 3, 3, 8);
        tail.addChild(tailTip);
        body.addChild(tail);

        head = part(0.0f, 7.0f, -4.0f, 0.0f, 0.0f, 0.0f);
        ModelRenderer skull = part(0.0f, 0.0f, 0.0f, 0.7854f, 0.0f, 0.0f);
        skull.setTextureOffset(16, 60).addBox(1.5f, -14.0f, -2.0f, 4, 4, 6);
        skull.setTextureOffset(60, 58).addBox(-5.5f, -14.0f, -2.0f, 4, 4, 6);
        skull.setTextureOffset(30, 0).addBox(-4.5f, -13.0f, -3.0f, 9, 6, 8);
        head.addChild(skull);
        head.addChild(cube(36, 64, 0.0f, 0.25f, 0.25f, 0.7974f, 0.1536f, 0.1555f,
            -3.5f, -13.0f, -7.75f, 4, 6, 5));
        head.addChild(cube(62, 9, 0.0f, 0.25f, 0.25f, 0.7974f, -0.1536f, -0.1555f,
            -0.5f, -13.0f, -7.75f, 4, 6, 5));
        body.addChild(head);
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw,
                       float headPitch, float scale) {
        body.render(scale);
        leftLeg.render(scale);
        rightLeg.render(scale);
    }

    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw,
                                  float headPitch, float scaleFactor, Entity entityIn) {
        leftLeg.rotateAngleX = MathHelper.cos(limbSwing) * -1.0f * limbSwingAmount;
        rightLeg.rotateAngleX = MathHelper.cos(limbSwing) * limbSwingAmount;
        head.rotateAngleY = netHeadYaw * ((float) Math.PI / 180.0f);
        head.rotateAngleX = headPitch * ((float) Math.PI / 180.0f);
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
