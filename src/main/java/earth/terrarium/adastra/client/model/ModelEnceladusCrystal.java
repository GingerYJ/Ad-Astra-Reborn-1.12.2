package earth.terrarium.adastra.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelEnceladusCrystal extends ModelBase {
    private ModelRenderer shape1;
    private ModelRenderer shape2;
    private ModelRenderer shape3;
    private ModelRenderer shape5;
    private ModelRenderer shape6;
    private ModelRenderer shape7;
    private ModelRenderer shape8;
    private ModelRenderer shape9;
    private ModelRenderer shape10;
    private ModelRenderer shape11;
    private ModelRenderer shape12;
    private ModelRenderer shape13;
    private ModelRenderer shape14;
    private ModelRenderer shape15;

    public ModelEnceladusCrystal() {
        textureWidth = 64;
        textureHeight = 32;

        shape1 = new ModelRenderer(this, 20, 0);
        shape1.addBox(0F, -16F, 0F, 3, 16, 3);
        shape1.setRotationPoint(-1.5F, 24F, 0F);
        shape1.setTextureSize(64, 32);
        shape1.mirror = true;
        setRotation(shape1, 0.2845197F, -0.2792527F, 0.122173F);

        shape2 = new ModelRenderer(this, 0, 0);
        shape2.addBox(0F, -7F, 0F, 1, 7, 1);
        shape2.setRotationPoint(0F, 24F, 3F);
        shape2.setTextureSize(64, 32);
        shape2.mirror = true;
        setRotation(shape2, -0.1858931F, 0.3141593F, 0.0698132F);

        shape3 = new ModelRenderer(this, 0, 0);
        shape3.addBox(0F, -7F, 0F, 2, 7, 2);
        shape3.setRotationPoint(-2F, 24F, 3F);
        shape3.setTextureSize(64, 32);
        shape3.mirror = true;
        setRotation(shape3, -0.122173F, 0.1570796F, -0.2094395F);

        shape5 = new ModelRenderer(this, 0, 0);
        shape5.addBox(0F, -5F, 0F, 2, 5, 2);
        shape5.setRotationPoint(0F, 24F, 0F);
        shape5.setTextureSize(64, 32);
        shape5.mirror = true;
        setRotation(shape5, -0.4833219F, 2.044824F, -0.0371786F);

        shape6 = new ModelRenderer(this, 0, 0);
        shape6.addBox(0F, -9F, 0F, 2, 9, 2);
        shape6.setRotationPoint(-2F, 24F, 3F);
        shape6.setTextureSize(64, 32);
        shape6.mirror = true;
        setRotation(shape6, -0.0607251F, 3.141593F, 0.2997009F);

        shape7 = new ModelRenderer(this, 0, 0);
        shape7.addBox(0F, -4F, 0F, 3, 4, 3);
        shape7.setRotationPoint(-2F, 24F, 1F);
        shape7.setTextureSize(64, 32);
        shape7.mirror = true;
        setRotation(shape7, 0F, 2.700407F, 0.1396263F);

        shape8 = new ModelRenderer(this, 0, 0);
        shape8.addBox(0F, -6F, 0F, 1, 6, 1);
        shape8.setRotationPoint(-1F, 24F, -2F);
        shape8.setTextureSize(64, 32);
        shape8.mirror = true;
        setRotation(shape8, 0.2443461F, 0.2792527F, 0.1919862F);

        shape9 = new ModelRenderer(this, 0, 0);
        shape9.addBox(0F, -9F, 0F, 1, 9, 1);
        shape9.setRotationPoint(1F, 24F, 1F);
        shape9.setTextureSize(64, 32);
        shape9.mirror = true;
        setRotation(shape9, 0F, 0.2094395F, 0.3665191F);

        shape10 = new ModelRenderer(this, 0, 0);
        shape10.addBox(-1F, -5F, 0F, 1, 5, 1);
        shape10.setRotationPoint(2F, 24F, 0F);
        shape10.setTextureSize(64, 32);
        shape10.mirror = true;
        setRotation(shape10, 0F, 0.4537856F, 0.7679449F);

        shape11 = new ModelRenderer(this, 0, 0);
        shape11.addBox(0F, -6F, 0F, 1, 6, 1);
        shape11.setRotationPoint(-3F, 24F, 3F);
        shape11.setTextureSize(64, 32);
        shape11.mirror = true;
        setRotation(shape11, -0.1919862F, 0F, -0.3316126F);

        shape12 = new ModelRenderer(this, 0, 0);
        shape12.addBox(0F, -2F, 0F, 2, 2, 2);
        shape12.setRotationPoint(1.6F, 24F, 1F);
        shape12.setTextureSize(64, 32);
        shape12.mirror = true;
        setRotation(shape12, 0.122173F, 0.122173F, 0.4712389F);

        shape13 = new ModelRenderer(this, 0, 0);
        shape13.addBox(0F, -3F, 0F, 1, 3, 1);
        shape13.setRotationPoint(1F, 24F, 1.8F);
        shape13.setTextureSize(64, 32);
        shape13.mirror = true;
        setRotation(shape13, -0.4886922F, 0.1047198F, 0.1919862F);

        shape14 = new ModelRenderer(this, 0, 0);
        shape14.addBox(-1F, -3F, -1F, 1, 3, 1);
        shape14.setRotationPoint(-0.5F, 24F, -2F);
        shape14.setTextureSize(64, 32);
        shape14.mirror = true;
        setRotation(shape14, 0.8028515F, -0.1396263F, -0.0174533F);

        shape15 = new ModelRenderer(this, 0, 0);
        shape15.addBox(0F, -7F, 0F, 1, 7, 1);
        shape15.setRotationPoint(-3F, 24F, 0F);
        shape15.setTextureSize(64, 32);
        shape15.mirror = true;
        setRotation(shape15, 0F, -0.3839724F, -0.2974289F);

        shape1 = new ModelRenderer(this, 33, 0);
        shape1.addBox(0F, -16F, 0F, 4, 16, 4);
        shape1.setRotationPoint(-2F, 24F, -1F);
        shape1.setTextureSize(64, 32);
        shape1.mirror = true;
        setRotation(shape1, 0.2496131F, -0.2792527F, 0.122173F);
    }

    public void render(Entity entity, float scale) {
        shape1.render(scale);
        shape2.render(scale);
        shape3.render(scale);
        shape5.render(scale);
        shape6.render(scale);
        shape7.render(scale);
        shape8.render(scale);
        shape9.render(scale);
        shape10.render(scale);
        shape11.render(scale);
        shape12.render(scale);
        shape13.render(scale);
        shape14.render(scale);
        shape15.render(scale);
    }

    private void setRotation(ModelRenderer model, float x, float y, float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }
}
