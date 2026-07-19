package earth.terrarium.adastra.client.model;

import earth.terrarium.adastra.common.entities.mob.FreezeEntity;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

/** Blockbench Freeze model converted for the 1.12 renderer. */
public class ModelFreeze extends ModelBase {

    private final ModelRenderer head;
    private final ModelRenderer eyes;
    private final ModelRenderer[] rods = new ModelRenderer[3];
    private final ModelRenderer tornadoBottom;
    private final ModelRenderer tornadoMid;
    private final ModelRenderer tornadoTop;

    public ModelFreeze() {
        textureWidth = 128;
        textureHeight = 160;

        head = new ModelRenderer(this, 0, 0);
        head.setRotationPoint(0.0F, -20.0F, 0.0F);
        head.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8);

        eyes = new ModelRenderer(this, 4, 24);
        eyes.setRotationPoint(0.0F, 0.0F, 0.0F);
        eyes.addBox(-5.0F, -5.0F, -4.2F, 10, 3, 4);
        head.addChild(eyes);

        rods[0] = rod(2.5981F, -4.4609F, 1.5F, -2.7489F, -1.0472F, 3.1416F);
        rods[1] = rod(-2.5981F, -4.4609F, 1.5F, -2.7489F, 1.0472F, 3.1416F);
        rods[2] = rod(0.0F, -4.4609F, -3.0F, 0.3927F, 0.0F, 0.0F);

        tornadoBottom = new ModelRenderer(this, 1, 115);
        tornadoBottom.setRotationPoint(0.0F, 0.0F, 0.0F);
        tornadoBottom.addBox(-2.5F, -7.0F, -2.5F, 5, 7, 5);

        tornadoMid = new ModelRenderer(this, 74, 60);
        tornadoMid.setRotationPoint(0.0F, -7.0F, 0.0F);
        tornadoMid.addBox(-6.0F, -6.0F, -6.0F, 12, 6, 12);
        tornadoBottom.addChild(tornadoMid);

        tornadoTop = new ModelRenderer(this, 0, 32);
        tornadoTop.setRotationPoint(0.0F, -6.0F, 0.0F);
        tornadoTop.addBox(-9.0F, -8.0F, -9.0F, 18, 8, 18);
        tornadoMid.addChild(tornadoTop);
    }

    private ModelRenderer rod(float x, float y, float z, float rx, float ry, float rz) {
        ModelRenderer rod = new ModelRenderer(this, 0, 17);
        rod.setRotationPoint(x, y, z);
        rod.rotateAngleX = rx;
        rod.rotateAngleY = ry;
        rod.rotateAngleZ = rz;
        rod.addBox(-1.0F, 0.0F, -3.0F, 2, 8, 2);
        return rod;
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks,
                       float netHeadYaw, float headPitch, float scale) {
        setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entity);
        head.render(scale);
        for (ModelRenderer rod : rods) {
            rod.render(scale);
        }
        tornadoBottom.render(scale);
    }

    public void renderEyes(float scale) {
        eyes.render(scale);
    }

    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks,
                                  float netHeadYaw, float headPitch, float scaleFactor, Entity entity) {
        head.rotateAngleY = netHeadYaw * 0.017453292F;
        head.rotateAngleX = headPitch * 0.017453292F;
        float wobble = ((FreezeEntity) entity).ticksExisted * 0.05F;
        tornadoBottom.rotateAngleY = wobble;
        tornadoMid.rotateAngleY = -wobble * 1.3F;
        tornadoTop.rotateAngleY = wobble * 1.7F;
    }
}
