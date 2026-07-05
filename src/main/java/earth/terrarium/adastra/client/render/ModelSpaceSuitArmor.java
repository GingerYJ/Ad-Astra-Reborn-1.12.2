package earth.terrarium.adastra.client.render;

import earth.terrarium.adastra.common.items.SpaceSuitMaterial;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelSpaceSuitArmor extends ModelBiped {

    public ModelSpaceSuitArmor(SpaceSuitMaterial material, EntityEquipmentSlot slot) {
        super(0.0f, 0.0f, 64, 64);
        textureWidth = 64;
        textureHeight = 64;

        bipedHead = emptyPart();
        bipedHeadwear = emptyPart();
        bipedBody = emptyPart();
        bipedRightArm = emptyPart();
        bipedLeftArm = emptyPart();
        bipedRightLeg = emptyPart();
        bipedLeftLeg = emptyPart();

        switch (slot) {
            case HEAD:
                buildHead(material);
                break;
            case CHEST:
                buildChest(material);
                break;
            case LEGS:
                buildLegs(material);
                break;
            case FEET:
                buildBoots(material);
                break;
            default:
                break;
        }
    }

    private void buildHead(SpaceSuitMaterial material) {
        float y = material == SpaceSuitMaterial.NETHERITE_SPACE ? -1.0f : 0.0f;
        bipedHead = part(0, 0);
        bipedHead.addBox(-4.0f, -8.0f + y, -4.0f, 8, 8, 8, 0.6f);

        bipedHeadwear = part(32, 0);
        bipedHeadwear.addBox(-4.0f, -8.0f + y, -4.0f, 8, 8, 8, 1.0f);
    }

    private void buildChest(SpaceSuitMaterial material) {
        bipedBody = part(24, 16);
        bipedBody.addBox(-4.0f, 0.0f, -2.0f, 8, 12, 4, 0.6f);

        if (material == SpaceSuitMaterial.JET) {
            bipedBody = part(24, 16);
            bipedBody.addBox(-4.0f, 0.0f, -2.0f, 8, 12, 4, 0.6f);
            bipedBody.setTextureOffset(32, 48).addBox(-4.0f, -1.0f, 3.0f, 8, 12, 4, 0.5f);
            bipedBody.setTextureOffset(32, 32).addBox(-4.0f, 0.0f, -2.0f, 8, 12, 4, 0.9f);
        } else if (material == SpaceSuitMaterial.NETHERITE_SPACE) {
            bipedBody.addChild(box(32, 46, -5.0f, -2.0f, 3.0f, 10, 14, 4, 0.5f));
        } else {
            bipedBody.addChild(box(32, 44, -6.0f, -3.0f, 3.0f, 12, 16, 4, 0.6f));
        }

        buildArms(material);
    }

    private void buildArms(SpaceSuitMaterial material) {
        bipedRightArm = part(16, 32);
        bipedRightArm.setRotationPoint(-5.0f, 2.0f, 0.0f);
        bipedLeftArm = part(16, 32);
        bipedLeftArm.mirror = true;
        bipedLeftArm.setRotationPoint(5.0f, 2.0f, 0.0f);

        if (material == SpaceSuitMaterial.SPACE) {
            bipedRightArm.addBox(-3.0f, -2.0f, -2.0f, 4, 12, 4, 0.6f);
            bipedLeftArm.addBox(-1.0f, -2.0f, -2.0f, 4, 12, 4, 0.6f);
            return;
        }

        bipedRightArm.addBox(-3.5f, -2.0f, -2.0f, 4, 12, 4, 1.0f);
        bipedRightArm.setTextureOffset(16, 48).addBox(-3.5f, -2.0f, -2.0f, 4, 12, 4, 0.6f);
        bipedLeftArm.addBox(-0.5f, -2.0f, -2.0f, 4, 12, 4, 1.0f);
        bipedLeftArm.setTextureOffset(16, 48).addBox(-0.5f, -2.0f, -2.0f, 4, 12, 4, 0.6f);
    }

    private void buildLegs(SpaceSuitMaterial material) {
        bipedBody = part(0, 16);
        bipedBody.addBox(-4.0f, -0.5f, -2.0f, 8, 12, 4, 0.5f);

        bipedRightLeg = part(0, 32);
        bipedRightLeg.setRotationPoint(-1.9f, 12.0f, 0.0f);
        bipedRightLeg.addBox(-2.0f, 0.0f, -2.0f, 4, 12, 4, 0.7f);

        bipedLeftLeg = part(0, 32);
        bipedLeftLeg.mirror = true;
        bipedLeftLeg.setRotationPoint(1.9f, 12.0f, 0.0f);
        bipedLeftLeg.addBox(-2.0f, 0.0f, -2.0f, 4, 12, 4, 0.7f);
    }

    private void buildBoots(SpaceSuitMaterial material) {
        bipedRightLeg = part(0, 48);
        bipedRightLeg.setRotationPoint(-1.9f, 12.0f, 0.0f);
        bipedLeftLeg = part(0, 48);
        bipedLeftLeg.mirror = true;
        bipedLeftLeg.setRotationPoint(1.9f, 12.0f, 0.0f);

        if (material == SpaceSuitMaterial.SPACE) {
            bipedRightLeg.addBox(-2.0f, 0.0f, -2.0f, 4, 12, 4, 0.6f);
            bipedRightLeg.setTextureOffset(16, 48).addBox(-2.0f, 0.0f, -2.0f, 4, 12, 4, 1.0f);
            bipedLeftLeg.addBox(-2.0f, 0.0f, -2.0f, 4, 12, 4, 0.6f);
            bipedLeftLeg.setTextureOffset(16, 48).addBox(-2.0f, 0.0f, -2.0f, 4, 12, 4, 1.0f);
            return;
        }

        bipedRightLeg.addBox(-2.0f, 0.0f, -2.0f, 4, 12, 4, 1.0f);
        bipedLeftLeg.addBox(-2.0f, 0.0f, -2.0f, 4, 12, 4, 1.0f);
    }

    private ModelRenderer box(int textureX, int textureY, float x, float y, float z, int width, int height, int depth, float inflate) {
        ModelRenderer renderer = part(textureX, textureY);
        renderer.addBox(x, y, z, width, height, depth, inflate);
        return renderer;
    }

    private ModelRenderer part(int textureX, int textureY) {
        return new ModelRenderer(this, textureX, textureY);
    }

    private ModelRenderer emptyPart() {
        return new ModelRenderer(this);
    }
}
