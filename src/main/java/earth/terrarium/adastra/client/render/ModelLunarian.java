package earth.terrarium.adastra.client.render;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
class ModelLunarian extends ModelBase {

    private final ModelRenderer head;
    private final ModelRenderer body;
    private final ModelRenderer leftLeg;
    private final ModelRenderer rightLeg;
    private final ModelRenderer arms;

    ModelLunarian() {
        textureWidth = 128;
        textureHeight = 128;

        head = part(0.0f, 0.0f, 0.0f);
        head.mirror = true;
        head.setTextureOffset(0, 19).addBox(-4.0f, -9.0f, -4.0f, 8, 9, 8);
        head.setTextureOffset(0, 0).addBox(-4.5f, -18.0f, -4.5f, 9, 10, 9);
        head.setTextureOffset(80, 18).addBox(-8.0f, -14.0f, -8.0f, 16, 0, 16);
        head.mirror = false;
        head.setTextureOffset(36, 0).addBox(-4.5f, -18.0f, -4.5f, 9, 10, 9, 0.5f);
        head.setTextureOffset(32, 19).addBox(-4.0f, -9.0f, -4.0f, 8, 9, 8, 0.5f);
        head.mirror = true;
        head.setTextureOffset(0, 20).addBox(-1.0f, -3.0f, -6.0f, 2, 4, 2);
        head.mirror = false;

        body = part(-4.0f, 12.0f, 2.0f);
        body.mirror = true;
        body.setTextureOffset(100, 0).addBox(0.0f, -12.0f, -5.0f, 8, 12, 6);
        body.setTextureOffset(0, 36).addBox(0.0f, -12.0f, -5.0f, 8, 19, 6, 0.5f);
        body.setTextureOffset(28, 36).addBox(0.0f, -12.0f, -5.0f, 8, 19, 6, 0.7f);
        body.mirror = false;

        leftLeg = part(2.0f, 12.0f, 0.0f);
        leftLeg.mirror = true;
        leftLeg.setTextureOffset(0, 81).addBox(-2.0f, 0.0f, -2.0f, 4, 12, 4);
        leftLeg.setTextureOffset(16, 81).addBox(-2.0f, 0.0f, -2.0f, 4, 12, 4, 0.5f);
        leftLeg.mirror = false;

        rightLeg = part(-2.0f, 12.0f, 0.0f);
        rightLeg.setTextureOffset(0, 81).addBox(-2.0f, 0.0f, -2.0f, 4, 12, 4);
        rightLeg.setTextureOffset(16, 81).addBox(-2.0f, 0.0f, -2.0f, 4, 12, 4, 0.5f);

        arms = part(0.0f, 2.0f, 0.0f, -0.9599f, 0.0f, 0.0f);
        arms.mirror = true;
        arms.setTextureOffset(0, 73).addBox(-4.0f, 2.1434f, -1.7952f, 8, 4, 4);
        arms.setTextureOffset(0, 61).addBox(4.0f, -1.8566f, -1.7952f, 4, 8, 4);
        arms.mirror = false;
        arms.setTextureOffset(0, 61).addBox(-8.0f, -1.8566f, -1.7952f, 4, 8, 4);
        arms.mirror = true;
        arms.setTextureOffset(16, 61).addBox(4.0f, -1.8566f, -1.7952f, 4, 8, 4, 0.5f);
        arms.mirror = false;
        arms.setTextureOffset(16, 61).addBox(-8.0f, -1.8566f, -1.7952f, 4, 8, 4, 0.5f);
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
        leftLeg.rotateAngleX = MathHelper.cos(limbSwing * 0.6662f) * 1.4f * limbSwingAmount * 0.5f;
        rightLeg.rotateAngleX = MathHelper.cos(limbSwing * 0.6662f + (float) Math.PI) * 1.4f * limbSwingAmount * 0.5f;
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
