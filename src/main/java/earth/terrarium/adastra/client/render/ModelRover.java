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

    private final ModelRenderer root;
    private final ModelRenderer frontLeftWheel;
    private final ModelRenderer frontRightWheel;
    private final ModelRenderer rearLeftWheel;
    private final ModelRenderer rearRightWheel;

    ModelRover() {
        textureWidth = 256;
        textureHeight = 256;

        root = new ModelRenderer(this);

        buildFrame();
        buildCabin();
        buildAntenna();
        frontLeftWheel = wheel(18.0f, 3.0f, -22.0f);
        frontRightWheel = wheel(-18.0f, 3.0f, -22.0f);
        rearLeftWheel = wheel(18.0f, 3.0f, 20.0f);
        rearRightWheel = wheel(-18.0f, 3.0f, 20.0f);
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw,
                       float headPitch, float scale) {
        root.render(scale);
        frontLeftWheel.render(scale);
        frontRightWheel.render(scale);
        rearLeftWheel.render(scale);
        rearRightWheel.render(scale);
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
        frontLeftWheel.rotateAngleX = wheelSpin;
        frontRightWheel.rotateAngleX = wheelSpin;
        rearLeftWheel.rotateAngleX = wheelSpin;
        rearRightWheel.rotateAngleX = wheelSpin;

        float steering = MathHelper.clamp(rover.rotationYaw - rover.prevRotationYaw, -12.0f, 12.0f) * 0.02f;
        frontLeftWheel.rotateAngleY = steering;
        frontRightWheel.rotateAngleY = steering;
    }

    private void buildFrame() {
        addBox(root, 0, 0, -15.0f, -5.0f, -18.0f, 30, 3, 43);
        addBox(root, 88, 64, -18.0f, -5.5f, -19.0f, 36, 3, 3);
        addBox(root, 88, 64, -18.0f, -5.5f, 23.0f, 36, 3, 3);
        addBox(root, 0, 46, -11.0f, -8.0f, -31.0f, 22, 3, 22);
        addBox(root, 103, 24, -14.0f, -9.0f, 1.0f, 12, 2, 12);
        addBox(root, 103, 24, 2.0f, -9.0f, 1.0f, 12, 2, 12);
        addBox(root, 66, 46, -15.0f, -8.0f, 21.0f, 30, 2, 16);
    }

    private void buildCabin() {
        addBox(root, 0, 0, -15.0f, -17.0f, -19.0f, 8, 12, 12);
        addBox(root, 0, 0, 7.0f, -17.0f, -19.0f, 8, 12, 12);
        addBox(root, 139, 28, -16.0f, -15.0f, -12.0f, 10, 3, 3);
        addBox(root, 139, 28, 6.0f, -15.0f, -12.0f, 10, 3, 3);

        addBox(root, 0, 71, -15.0f, -28.0f, 19.0f, 30, 23, 2);
        addBox(root, 64, 71, -9.0f, -17.0f, 22.0f, 18, 10, 14);
        addBox(root, 0, 96, -9.0f, -19.0f, 22.0f, 18, 2, 14);

        ModelRenderer seatBack = part(-0.5f, -18.0f, 16.0f, 1.2217f, 0.0f, 0.0f);
        seatBack.setTextureOffset(50, 101).addBox(-13.5f, -1.0f, -10.0f, 12, 2, 14);
        seatBack.setTextureOffset(50, 101).addBox(2.5f, -1.0f, -10.0f, 12, 2, 14);
        root.addChild(seatBack);
    }

    private void buildAntenna() {
        addBox(root, 103, 0, -9.0f, -30.0f, -25.0f, 2, 22, 2);

        ModelRenderer antenna = part(-12.0f, -30.0f, -24.0f, 0.0f, 0.6545f, -0.3927f);
        antenna.setTextureOffset(0, 40).addBox(-6.0f, 0.0f, 1.0f, 17, 1, 1);
        antenna.setTextureOffset(0, 8).addBox(5.0f, -7.0f, -6.0f, 1, 16, 16);
        antenna.setTextureOffset(66, 46).addBox(1.0f, 0.0f, 0.0f, 4, 3, 4);
        root.addChild(antenna);
    }

    private ModelRenderer wheel(float x, float y, float z) {
        ModelRenderer wheel = new ModelRenderer(this);
        wheel.setRotationPoint(x, y, z);
        wheel.setTextureOffset(111, 0).addBox(-2.0f, -6.0f, -4.0f, 4, 12, 8);

        ModelRenderer cross = part(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.7854f);
        cross.setTextureOffset(111, 0).addBox(-2.0f, -6.0f, -4.0f, 4, 12, 8);
        wheel.addChild(cross);

        ModelRenderer hub = part(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.5708f);
        hub.setTextureOffset(111, 0).addBox(-2.0f, -6.0f, -4.0f, 4, 12, 8);
        wheel.addChild(hub);
        return wheel;
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
