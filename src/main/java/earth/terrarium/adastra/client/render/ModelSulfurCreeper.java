package earth.terrarium.adastra.client.render;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
class ModelSulfurCreeper extends ModelBase {

    private final ModelRenderer head;
    private final ModelRenderer body;
    private final ModelRenderer frontRightLeg;
    private final ModelRenderer frontLeftLeg;
    private final ModelRenderer rearRightLeg;
    private final ModelRenderer rearLeftLeg;

    ModelSulfurCreeper() {
        textureWidth = 64;
        textureHeight = 64;

        head = new ModelRenderer(this, 0, 0);
        head.addBox(-4.0f, -8.0f, -4.0f, 8, 8, 8);
        head.setRotationPoint(0.0f, 6.0f, 0.0f);

        body = new ModelRenderer(this, 16, 16);
        body.addBox(-4.0f, 0.0f, -2.0f, 8, 12, 4);
        body.setRotationPoint(0.0f, 6.0f, 0.0f);

        frontRightLeg = leg(-2.0f, 18.0f, 4.0f);
        frontLeftLeg = leg(2.0f, 18.0f, 4.0f);
        rearRightLeg = leg(-2.0f, 18.0f, -4.0f);
        rearLeftLeg = leg(2.0f, 18.0f, -4.0f);
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw,
                       float headPitch, float scale) {
        setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entity);
        head.render(scale);
        body.render(scale);
        frontRightLeg.render(scale);
        frontLeftLeg.render(scale);
        rearRightLeg.render(scale);
        rearLeftLeg.render(scale);
    }

    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw,
                                  float headPitch, float scaleFactor, Entity entityIn) {
        head.rotateAngleY = netHeadYaw * ((float) Math.PI / 180.0f);
        head.rotateAngleX = headPitch * ((float) Math.PI / 180.0f);
        frontRightLeg.rotateAngleX = MathHelper.cos(limbSwing * 0.6662f) * 1.4f * limbSwingAmount;
        frontLeftLeg.rotateAngleX = MathHelper.cos(limbSwing * 0.6662f + (float) Math.PI) * 1.4f * limbSwingAmount;
        rearRightLeg.rotateAngleX = MathHelper.cos(limbSwing * 0.6662f + (float) Math.PI) * 1.4f * limbSwingAmount;
        rearLeftLeg.rotateAngleX = MathHelper.cos(limbSwing * 0.6662f) * 1.4f * limbSwingAmount;
    }

    private ModelRenderer leg(float x, float y, float z) {
        ModelRenderer leg = new ModelRenderer(this, 0, 16);
        leg.addBox(-2.0f, 0.0f, -2.0f, 4, 6, 4);
        leg.setRotationPoint(x, y, z);
        return leg;
    }
}
