package earth.terrarium.adastra.client.render;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
class ModelLander extends ModelBase {

    private ModelRenderer root;

    ModelLander() {
        textureWidth = 128;
        textureHeight = 128;

        root = new ModelRenderer(this);
        root.setRotationPoint(0.0f, 20.0f, 0.0f);
        root.rotateAngleX = 0.0f;
        root.rotateAngleY = 0.0f;
        root.rotateAngleZ = 0.0f;
        buildBody();
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw,
                       float headPitch, float scale) {
        root.render(scale);
    }

    private void buildBody() {
        ModelRenderer raft = part(root, -7.0f, 1.5f, -13.0f, 0.0f, 0.0f, 0.0f);
        addBox(part(raft, 7.0f, -0.5f, 0.0f, 0.0f, 3.1416f, 0.0f), 0, 42, -6.0f, -3.5f, -5.0f, 12, 7, 10);
        addBox(part(raft, 7.0f, 71.5f, 13.0f, 0.0f, 0.0f, 0.0f), 0, 42, -6.0f, -75.5f, 8.0f, 12, 7, 10);
        addBox(part(raft, -0.8839f, -0.5f, 5.1265f, 0.0f, -2.3562f, 0.0f), 0, 59, -9.0f, -3.0f, -5.0f, 18, 6, 10);
        ModelRenderer cube_r4 = part(raft, 7.0f, 0.0f, 13.0f, 0.0f, 1.5708f, 0.0f);
        setMirror(cube_r4, true);
        addBox(cube_r4, 0, 42, -6.0f, -4.0f, 8.0f, 12, 7, 10);
        ModelRenderer cube_r5 = part(raft, 14.8839f, -0.5f, 5.1265f, 0.0f, 2.3562f, 0.0f);
        setMirror(cube_r5, true);
        addBox(cube_r5, 0, 59, -9.0f, -3.0f, -5.0f, 18, 6, 10);
        ModelRenderer cube_r6 = part(raft, 14.0f, 0.0f, 26.0f, 0.0f, 0.7854f, 0.0f);
        setMirror(cube_r6, true);
        addBox(cube_r6, 0, 59, -4.75f, -3.5f, -8.0f, 18, 6, 10);
        addBox(part(raft, 0.0f, 0.0f, 26.0f, 0.0f, -0.7854f, 0.0f), 0, 59, -13.25f, -3.5f, -8.0f, 18, 6, 10);
        addBox(part(raft, 7.0f, 0.0f, 13.0f, 0.0f, -1.5708f, 0.0f), 0, 42, -6.0f, -4.0f, 8.0f, 12, 7, 10);

        ModelRenderer main = part(root, 0.0f, 73.0f, 0.0f, 0.0f, 0.0f, 0.0f);
        addBox(main, 74, 9, -9.0f, -73.0f, -9.0f, 18, 0, 18);

        ModelRenderer fins = part(main, -2.0f, -8.0f, -2.0f, 0.0f, 0.0f, 0.0f);

        ModelRenderer pyramid = part(main, 0.0f, -17.0f, 0.0f, 0.0f, 0.0f, 0.0f);
        addBox(pyramid, 75, -10, -6.0f, -77.5f, -5.0f, 0, 9, 10);
        addBox(pyramid, 75, -10, 6.0f, -77.5f, -5.0f, 0, 9, 10);
        addBox(pyramid, 88, 11, 10.0f, -58.0f, -10.0f, 0, 2, 20);
        addBox(pyramid, 88, 11, -10.0f, -58.0f, -10.0f, 0, 2, 20);
        addBox(pyramid, 88, 31, -10.0f, -58.0f, 10.0f, 20, 2, 0);
        addBox(pyramid, 88, 31, -10.0f, -58.0f, -10.0f, 20, 2, 0);

        addBox(part(pyramid, 0.0f, -93.0f, 0.0f, -0.3491f, 0.7854f, 0.0f), 39, 0, -1.0f, 2.6076f, -3.171f, 2, 15, 2);
        addBox(part(pyramid, 0.0f, -66.0f, 0.0f, -2.8798f, -1.5708f, 3.1416f), 0, 12, -8.0f, -21.5488f, 8.7536f, 16, 12, 0);
        addBox(part(pyramid, 0.0f, -93.0f, 0.0f, -0.3491f, 2.3562f, 0.0f), 39, 0, -1.0f, 2.6076f, -3.171f, 2, 15, 2);
        addBox(part(pyramid, 0.0f, -66.0f, 0.0f, -2.8798f, 3.1416f, 3.1416f), 0, 12, -8.0f, -21.5488f, 8.7536f, 16, 12, 0);
        addBox(part(pyramid, 0.0f, -93.0f, 0.0f, -0.3491f, -2.3562f, 0.0f), 39, 0, -1.0f, 2.6076f, -3.171f, 2, 15, 2);
        addBox(part(pyramid, 0.0f, -66.0f, 0.0f, -2.8798f, 1.5708f, 3.1416f), 0, 12, -8.0f, -21.5488f, 8.7536f, 16, 12, 0);
        addBox(part(pyramid, 6.3647f, -77.5491f, 4.9353f, 0.0f, -0.7854f, 0.0f), 39, 0, -1.0f, 0.01f, 0.0027f, 2, 10, 2);
        addBox(part(pyramid, 6.3647f, -77.5491f, -6.3647f, 0.0f, -0.7854f, 0.0f), 39, 0, -1.0f, 0.01f, 0.0027f, 2, 10, 2);
        addBox(part(pyramid, -4.9353f, -77.5491f, -6.3647f, 0.0f, -0.7854f, 0.0f), 39, 0, -1.02f, 0.01f, 0.0027f, 2, 10, 2);
        ModelRenderer cube_r18 = part(pyramid, 0.0f, -73.0f, 0.0f, 0.0f, -1.5708f, 0.0f);
        addBox(cube_r18, 75, -10, -6.0f, -4.5f, -5.0f, 0, 9, 10);
        addBox(cube_r18, 75, -10, 6.0f, -4.5f, -5.0f, 0, 9, 10);
        addBox(part(pyramid, -4.9353f, -77.5491f, 4.9353f, 0.0f, -0.7854f, 0.0f), 39, 0, -1.02f, 0.01f, 0.0027f, 2, 10, 2);
        addBox(part(pyramid, 0.0f, -93.0f, 0.0f, -0.3491f, -0.7854f, 0.0f), 39, 0, -1.0f, 2.6076f, -3.171f, 2, 15, 2);
        addBox(part(pyramid, 0.0f, -66.0f, 0.0f, -2.8798f, 0.0f, 3.1416f), 0, 12, -8.0f, -21.5488f, 8.7536f, 16, 12, 0);
        addBox(part(pyramid, 0.0f, -84.0f, 0.0f, -0.3491f, -2.3562f, 0.0f), 39, 0, -1.0f, 17.6076f, -3.171f, 2, 12, 2);
        addBox(part(pyramid, 0.0f, -84.0f, 0.0f, -0.3491f, 2.3562f, 0.0f), 39, 0, -1.0f, 17.6076f, -3.171f, 2, 12, 2);
        addBox(part(pyramid, 0.0f, -84.0f, 0.0f, -0.3491f, -0.7854f, 0.0f), 39, 0, -1.0f, 17.6076f, -3.171f, 2, 12, 2);
        addBox(part(pyramid, 0.0f, -84.0f, 0.0f, -0.3491f, 0.7854f, 0.0f), 39, 0, -1.0f, 17.6076f, -3.171f, 2, 12, 2);
        addBox(part(pyramid, 0.0f, -57.0f, 0.0f, -2.8798f, -1.5708f, 3.1416f), 0, 0, -8.0f, -9.5488f, 8.7536f, 16, 12, 0);
        addBox(part(pyramid, 0.0f, -57.0f, 0.0f, -2.8798f, 1.5708f, 3.1416f), 0, 0, -8.0f, -9.5488f, 8.7536f, 16, 12, 0);
        addBox(part(pyramid, 0.0f, -57.0f, 0.0f, -2.8798f, 3.1416f, 3.1416f), 0, 0, -8.0f, -9.5488f, 8.7536f, 16, 12, 0);
        addBox(part(pyramid, 0.0f, -57.0f, 0.0f, -2.8798f, 0.0f, 3.1416f), 0, 0, -8.0f, -9.5488f, 8.7536f, 16, 12, 0);

        ModelRenderer booster = part(main, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f);

        ModelRenderer tip = part(main, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f);
        addBox(tip, 0, 24, -4.0f, -110.0f, -4.0f, 8, 8, 8);
        addBox(tip, 49, 0, -3.0f, -118.0f, -3.0f, 6, 8, 6);
    }

    private void addBox(ModelRenderer parent, int textureX, int textureY, float x, float y, float z,
                        int width, int height, int depth) {
        parent.setTextureOffset(textureX, textureY).addBox(x, y, z, width, height, depth);
    }

    private void setMirror(ModelRenderer renderer, boolean mirror) {
        renderer.mirror = mirror;
    }

    private ModelRenderer part(ModelRenderer parent, float pointX, float pointY, float pointZ, float rotateX, float rotateY,
                               float rotateZ) {
        ModelRenderer renderer = new ModelRenderer(this);
        renderer.setRotationPoint(pointX, pointY, pointZ);
        renderer.rotateAngleX = rotateX;
        renderer.rotateAngleY = rotateY;
        renderer.rotateAngleZ = rotateZ;
        if (parent != null) {
            parent.addChild(renderer);
        }
        return renderer;
    }
}
