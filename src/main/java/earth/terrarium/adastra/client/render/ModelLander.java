package earth.terrarium.adastra.client.render;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
class ModelLander extends ModelBase {

    private final ModelRenderer root;

    ModelLander() {
        textureWidth = 128;
        textureHeight = 128;

        root = new ModelRenderer(this);
        buildRaft();
        buildCabin();
        buildLegs();
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw,
                       float headPitch, float scale) {
        root.render(scale);
    }

    private void buildRaft() {
        addBox(root, 0, 42, -6.0f, 13.0f, -18.0f, 12, 5, 10);
        addBox(root, 0, 42, -6.0f, 13.0f, 8.0f, 12, 5, 10);

        ModelRenderer leftFront = part(0.0f, 0.0f, 0.0f, 0.0f, 0.7854f, 0.0f);
        leftFront.setTextureOffset(0, 59).addBox(-15.0f, 13.0f, -10.0f, 18, 5, 8);
        root.addChild(leftFront);

        ModelRenderer rightFront = part(0.0f, 0.0f, 0.0f, 0.0f, -0.7854f, 0.0f);
        rightFront.setTextureOffset(0, 59).addBox(-3.0f, 13.0f, -10.0f, 18, 5, 8);
        root.addChild(rightFront);

        ModelRenderer leftBack = part(0.0f, 0.0f, 0.0f, 0.0f, -0.7854f, 0.0f);
        leftBack.setTextureOffset(0, 59).addBox(-15.0f, 13.0f, 2.0f, 18, 5, 8);
        root.addChild(leftBack);

        ModelRenderer rightBack = part(0.0f, 0.0f, 0.0f, 0.0f, 0.7854f, 0.0f);
        rightBack.setTextureOffset(0, 59).addBox(-3.0f, 13.0f, 2.0f, 18, 5, 8);
        root.addChild(rightBack);

        addBox(root, 74, 9, -8.0f, 11.0f, -8.0f, 16, 1, 16);
    }

    private void buildCabin() {
        addBox(root, 88, 11, -9.0f, -8.0f, -9.0f, 18, 20, 1);
        addBox(root, 88, 31, -9.0f, -8.0f, 8.0f, 18, 20, 1);
        addBox(root, 88, 11, -9.0f, -8.0f, -9.0f, 1, 20, 18);
        addBox(root, 88, 11, 8.0f, -8.0f, -9.0f, 1, 20, 18);

        addBox(root, 0, 24, -5.0f, -16.0f, -5.0f, 10, 8, 10);
        addBox(root, 49, 0, -3.0f, -22.0f, -3.0f, 6, 6, 6);
        addBox(root, 39, 0, -1.0f, -27.0f, -1.0f, 2, 6, 2);

        addConePanel(0.0f);
        addConePanel(1.5708f);
        addConePanel(3.1416f);
        addConePanel(-1.5708f);
    }

    private void buildLegs() {
        addLeg(-10.0f, -10.0f);
        addLeg(10.0f, -10.0f);
        addLeg(-10.0f, 10.0f);
        addLeg(10.0f, 10.0f);
    }

    private void addConePanel(float rotationY) {
        ModelRenderer panel = part(0.0f, -14.0f, 0.0f, -0.3491f, rotationY, 0.0f);
        panel.setTextureOffset(0, 12).addBox(-8.0f, -4.0f, 8.0f, 16, 10, 1);
        root.addChild(panel);
    }

    private void addLeg(float x, float z) {
        addBox(root, 39, 0, x - 1.0f, 7.0f, z - 1.0f, 2, 10, 2);

        ModelRenderer braceX = part(x, 12.0f, z, 0.0f, z < 0.0f ? 0.7854f : -0.7854f, 0.0f);
        braceX.setTextureOffset(39, 0).addBox(-1.0f, -7.0f, -1.0f, 2, 12, 2);
        root.addChild(braceX);

        addBox(root, 0, 42, x - 4.0f, 17.0f, z - 4.0f, 8, 2, 8);
    }

    private void addBox(ModelRenderer parent, int textureX, int textureY, float x, float y, float z,
                        int width, int height, int depth) {
        parent.setTextureOffset(textureX, textureY).addBox(x, y, z, width, height, depth);
    }

    private ModelRenderer part(float pointX, float pointY, float pointZ, float rotateX, float rotateY,
                               float rotateZ) {
        ModelRenderer renderer = new ModelRenderer(this);
        renderer.setRotationPoint(pointX, pointY, pointZ);
        renderer.rotateAngleX = rotateX;
        renderer.rotateAngleY = rotateY;
        renderer.rotateAngleZ = rotateZ;
        return renderer;
    }
}
