package earth.terrarium.adastra.client.render;

import earth.terrarium.adastra.common.entities.vehicles.Tier1RoverEntity;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
class ModelRover extends ModelBase {

    private ModelRenderer root;
    private ModelRenderer wheel1;
    private ModelRenderer wheel2;
    private ModelRenderer wheel3;
    private ModelRenderer wheel4;

    ModelRover() {
        textureWidth = 256;
        textureHeight = 256;
        buildBodyLayer();
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw,
                       float headPitch, float scale) {
        root.render(scale);
    }

    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw,
                                  float headPitch, float scaleFactor, Entity entityIn) {
        if (!(entityIn instanceof Tier1RoverEntity)) {
            return;
        }

        Tier1RoverEntity rover = (Tier1RoverEntity) entityIn;
        double speed = Math.sqrt(rover.motionX * rover.motionX + rover.motionZ * rover.motionZ);
        float wheelSpin = rover.ticksExisted * (float) speed * 3.5f;
        wheel1.rotateAngleX = wheelSpin;
        wheel2.rotateAngleX = wheelSpin;
        wheel3.rotateAngleX = wheelSpin;
        wheel4.rotateAngleX = wheelSpin;

        float steering = MathHelper.clamp(rover.rotationYaw - rover.prevRotationYaw, -12.0f, 12.0f) * 0.02f;
        wheel1.rotateAngleY = steering;
        wheel2.rotateAngleY = steering;
    }

    private void buildBodyLayer() {
        root = part(2.0f, 24.0f, -4.0f, 0.0f, 0.0f, 0.0f);
        addBox(root, 0, 0, -15.0f, -10.0f, -16.0f, 30, 3, 43);
        addBox(root, 88, 64, -18.0f, -9.6f, -17.0f, 36, 3, 3);
        addBox(root, 88, 64, -18.0f, -9.6f, 25.0f, 36, 3, 3);
        addBox(root, 0, 46, -11.0f, -13.0f, -29.0f, 22, 3, 22);
        addBox(root, 66, 53, 6.0f, -24.0f, -3.0f, 4, 3, 4);
        addBox(root, 0, 0, 7.0f, -22.0f, -17.0f, 8, 12, 12);
        addBox(root, 139, 28, 6.0f, -20.0f, -10.0f, 10, 3, 3);
        addBox(root, 139, 28, -16.0f, -20.0f, -10.0f, 10, 3, 3);
        addBox(root, 0, 0, -15.0f, -22.0f, -17.0f, 8, 12, 12);
        addBox(root, 103, 0, -9.0f, -35.0f, -23.0f, 2, 22, 2);
        addBox(root, 103, 24, -14.0f, -14.0f, 4.0f, 12, 2, 12);
        addBox(root, 32, 24, -9.0f, -23.0f, 17.0f, 2, 13, 2);
        addBox(root, 0, 71, -15.0f, -33.0f, 21.0f, 30, 23, 2);
        addBox(root, 66, 46, -15.0f, -12.0f, 23.0f, 30, 2, 16);
        addBox(root, 64, 71, -9.0f, -22.0f, 24.0f, 18, 10, 14);
        addBox(root, 0, 96, -9.0f, -24.0f, 24.0f, 18, 2, 14);
        addBox(root, 32, 24, 7.0f, -23.0f, 17.0f, 2, 13, 2);
        addBox(root, 103, 24, 2.0f, -14.0f, 4.0f, 12, 2, 12);
        ModelRenderer cube_r1 = part(-0.5f, -23.0f, 18.0f, 1.2217f, 0.0f, 0.0f);
        addBox(cube_r1, 50, 101, 2.5f, -1.0f, -10.0f, 12, 2, 14);
        addBox(cube_r1, 50, 101, -13.5f, -1.0f, -10.0f, 12, 2, 14);
        root.addChild(cube_r1);
        ModelRenderer cube_r2 = part(8.0f, -21.0f, 1.5f, 0.2182f, 0.0f, 0.0f);
        addBox(cube_r2, 0, 55, -5.0f, -5.0f, -0.5f, 10, 6, 1);
        root.addChild(cube_r2);
        ModelRenderer cube_r3 = part(8.0f, -17.5f, 0.0f, -0.2618f, 0.0f, 0.0f);
        addBox(cube_r3, 64, 71, -1.0f, -3.5f, -3.0f, 2, 12, 2);
        root.addChild(cube_r3);
        ModelRenderer wheels = part(-16.0f, 0.0f, 21.0f, 0.0f, 0.0f, 0.0f);
        root.addChild(wheels);
        ModelRenderer wheelframe = part(-2.0f, -8.0f, 5.5f, 0.0f, 0.0f, 0.0f);
        wheels.addChild(wheelframe);
        ModelRenderer bone18 = part(-0.5f, 0.0f, -4.0f, -0.7854f, 0.0f, 0.0f);
        addBox(bone18, 126, 0, -1.5f, -11.9355f, -1.3787f, 5, 0, 7);
        addBox(bone18, 126, 0, -1.5f, 17.763f, -31.0772f, 5, 0, 7);
        addBox(bone18, 126, 0, 33.5f, 17.763f, -31.0772f, 5, 0, 7);
        addBox(bone18, 126, 0, 33.5f, -11.9355f, -1.3787f, 5, 0, 7);
        wheelframe.addChild(bone18);
        ModelRenderer bone19 = part(-0.5f, 0.0f, -4.0f, -2.3562f, 0.0f, 0.0f);
        addBox(bone19, 126, 0, -1.5f, 6.2787f, -7.0355f, 5, 0, 7);
        addBox(bone19, 126, 0, -1.5f, 35.9772f, 22.663f, 5, 0, 7);
        addBox(bone19, 126, 0, 33.5f, 35.9772f, 22.663f, 5, 0, 7);
        addBox(bone19, 126, 0, 33.5f, 6.2787f, -7.0355f, 5, 0, 7);
        wheelframe.addChild(bone19);
        ModelRenderer bone20 = part(-0.5f, 0.0f, -4.0f, -1.5708f, 0.0f, 0.0f);
        wheelframe.addChild(bone20);
        ModelRenderer bone21 = part(-0.5f, 0.5f, -4.0f, 0.0f, 0.0f, 0.0f);
        addBox(bone21, 126, 0, -1.5f, -9.9f, 0.5f, 5, 0, 7);
        addBox(bone21, 126, 0, -1.5f, -9.9f, -41.5f, 5, 0, 7);
        addBox(bone21, 126, 0, 33.5f, -9.9f, -41.5f, 5, 0, 7);
        addBox(bone21, 126, 0, 33.5f, -9.9f, 0.5f, 5, 0, 7);
        wheelframe.addChild(bone21);
        ModelRenderer wheel1 = part(34.0f, -8.0f, -36.5f, 0.0f, 0.0f, 0.0f);
        wheels.addChild(wheel1);
        this.wheel1 = wheel1;
        ModelRenderer bone10 = part(-2.5f, 0.0f, 0.0f, -0.7854f, 0.0f, 0.0f);
        addBox(bone10, 111, 0, -0.5f, -8.4f, -3.5f, 4, 4, 7);
        addBox(bone10, 111, 0, -0.5f, 4.4f, -3.5f, 4, 4, 7);
        wheel1.addChild(bone10);
        ModelRenderer bone9 = part(-2.5f, 0.0f, 0.0f, -2.3562f, 0.0f, 0.0f);
        addBox(bone9, 111, 0, -0.5f, -8.4f, -3.5f, 4, 4, 7);
        addBox(bone9, 111, 0, -0.5f, 4.4f, -3.5f, 4, 4, 7);
        wheel1.addChild(bone9);
        ModelRenderer bone8 = part(-2.5f, 0.0f, 0.0f, -1.5708f, 0.0f, 0.0f);
        addBox(bone8, 111, 0, -0.5f, -8.4f, -3.5f, 4, 4, 7);
        addBox(bone8, 111, 0, -0.5f, 4.4f, -3.5f, 4, 4, 7);
        wheel1.addChild(bone8);
        ModelRenderer bone7 = part(-2.5f, 0.5f, 0.0f, 0.0f, 0.0f, 0.0f);
        addBox(bone7, 111, 0, -0.5f, -8.9f, -3.5f, 4, 4, 7);
        addBox(bone7, 111, 0, -0.5f, 3.9f, -3.5f, 4, 4, 7);
        addBox(bone7, 0, 37, 1.5f, -5.1f, -4.5f, 0, 9, 9);
        wheel1.addChild(bone7);
        ModelRenderer wheel2 = part(-2.0f, -8.0f, -36.5f, 0.0f, 0.0f, 0.0f);
        wheels.addChild(wheel2);
        this.wheel2 = wheel2;
        ModelRenderer bone2 = part(-0.5f, 0.0f, 0.0f, -0.7854f, 0.0f, 0.0f);
        addBox(bone2, 111, 0, -0.5f, -8.4f, -3.5f, 4, 4, 7);
        addBox(bone2, 111, 0, -0.5f, 4.4f, -3.5f, 4, 4, 7);
        wheel2.addChild(bone2);
        ModelRenderer bone3 = part(-0.5f, 0.0f, 0.0f, -2.3562f, 0.0f, 0.0f);
        addBox(bone3, 111, 0, -0.5f, -8.4f, -3.5f, 4, 4, 7);
        addBox(bone3, 111, 0, -0.5f, 4.4f, -3.5f, 4, 4, 7);
        wheel2.addChild(bone3);
        ModelRenderer bone4 = part(-0.5f, 0.0f, 0.0f, -1.5708f, 0.0f, 0.0f);
        addBox(bone4, 111, 0, -0.5f, -8.4f, -3.5f, 4, 4, 7);
        addBox(bone4, 111, 0, -0.5f, 4.4f, -3.5f, 4, 4, 7);
        wheel2.addChild(bone4);
        ModelRenderer bone5 = part(-0.5f, 0.5f, 0.0f, 0.0f, 0.0f, 0.0f);
        addBox(bone5, 111, 0, -0.5f, -8.9f, -3.5f, 4, 4, 7);
        addBox(bone5, 111, 0, -0.5f, 3.9f, -3.5f, 4, 4, 7);
        addBox(bone5, 0, 37, 1.5f, -5.1f, -4.5f, 0, 9, 9);
        wheel2.addChild(bone5);
        ModelRenderer wheel3 = part(34.0f, -8.0f, 5.5f, 0.0f, 0.0f, 0.0f);
        wheels.addChild(wheel3);
        this.wheel3 = wheel3;
        ModelRenderer bone6 = part(-2.5f, 0.0f, -4.0f, -0.7854f, 0.0f, 0.0f);
        addBox(bone6, 111, 0, -0.5f, -11.2284f, -0.6716f, 4, 4, 7);
        addBox(bone6, 111, 0, -0.5f, 1.5716f, -0.6716f, 4, 4, 7);
        wheel3.addChild(bone6);
        ModelRenderer bone11 = part(-2.5f, 0.0f, -4.0f, -2.3562f, 0.0f, 0.0f);
        addBox(bone11, 111, 0, -0.5f, -11.2284f, -6.3284f, 4, 4, 7);
        addBox(bone11, 111, 0, -0.5f, 1.5716f, -6.3284f, 4, 4, 7);
        wheel3.addChild(bone11);
        ModelRenderer bone12 = part(-2.5f, 0.0f, -4.0f, -1.5708f, 0.0f, 0.0f);
        addBox(bone12, 111, 0, -0.5f, -12.4f, -3.5f, 4, 4, 7);
        addBox(bone12, 111, 0, -0.5f, 0.4f, -3.5f, 4, 4, 7);
        wheel3.addChild(bone12);
        ModelRenderer bone13 = part(-2.5f, 0.5f, -4.0f, 0.0f, 0.0f, 0.0f);
        addBox(bone13, 111, 0, -0.5f, -8.9f, 0.5f, 4, 4, 7);
        addBox(bone13, 111, 0, -0.5f, 3.9f, 0.5f, 4, 4, 7);
        addBox(bone13, 0, 37, 1.5f, -5.1f, -0.5f, 0, 9, 9);
        wheel3.addChild(bone13);
        ModelRenderer wheel4 = part(-2.0f, -8.0f, 5.5f, 0.0f, 0.0f, 0.0f);
        wheels.addChild(wheel4);
        this.wheel4 = wheel4;
        ModelRenderer bone14 = part(-0.5f, 0.0f, -4.0f, -0.7854f, 0.0f, 0.0f);
        addBox(bone14, 111, 0, -0.5f, -11.2284f, -0.6716f, 4, 4, 7);
        addBox(bone14, 111, 0, -0.5f, 1.5716f, -0.6716f, 4, 4, 7);
        wheel4.addChild(bone14);
        ModelRenderer bone15 = part(-0.5f, 0.0f, -4.0f, -2.3562f, 0.0f, 0.0f);
        addBox(bone15, 111, 0, -0.5f, -11.2284f, -6.3284f, 4, 4, 7);
        addBox(bone15, 111, 0, -0.5f, 1.5716f, -6.3284f, 4, 4, 7);
        wheel4.addChild(bone15);
        ModelRenderer bone16 = part(-0.5f, 0.0f, -4.0f, -1.5708f, 0.0f, 0.0f);
        addBox(bone16, 111, 0, -0.5f, -12.4f, -3.5f, 4, 4, 7);
        addBox(bone16, 111, 0, -0.5f, 0.4f, -3.5f, 4, 4, 7);
        wheel4.addChild(bone16);
        ModelRenderer bone17 = part(-0.5f, 0.5f, -4.0f, 0.0f, 0.0f, 0.0f);
        addBox(bone17, 111, 0, -0.5f, -8.9f, 0.5f, 4, 4, 7);
        addBox(bone17, 111, 0, -0.5f, 3.9f, 0.5f, 4, 4, 7);
        addBox(bone17, 0, 37, 1.5f, -5.1f, -0.5f, 0, 9, 9);
        wheel4.addChild(bone17);
        ModelRenderer antenna = part(-12.0f, -35.275f, -22.0f, 0.0f, 0.6545f, -0.3927f);
        addBox(antenna, 28, 0, 10.9319f, -0.6943f, 0.7497f, 1, 3, 3);
        addBox(antenna, 0, 0, 4.9319f, -1.1943f, 0.2497f, 1, 4, 4);
        addBox(antenna, 0, 40, -6.0681f, 0.8057f, 1.7497f, 17, 0, 1);
        addBox(antenna, 66, 46, 0.9319f, -0.1943f, 0.2497f, 4, 3, 4);
        addBox(antenna, 0, 8, 4.9319f, -7.1943f, -5.7503f, 0, 16, 16);
        root.addChild(antenna);
        ModelRenderer cube_r4 = part(2.4319f, 0.8057f, 2.2497f, -1.6144f, 0.0f, 0.0f);
        addBox(cube_r4, 0, 40, -8.5f, 0.0f, -0.5f, 17, 0, 1);
        antenna.addChild(cube_r4);
    }

    private void addBox(ModelRenderer parent, int textureX, int textureY, float x, float y, float z,
                        int width, int height, int depth) {
        parent.setTextureOffset(textureX, textureY).addBox(x, y, z, width, height, depth);
    }

    private void setMirror(ModelRenderer renderer, boolean mirror) {
        renderer.mirror = mirror;
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
