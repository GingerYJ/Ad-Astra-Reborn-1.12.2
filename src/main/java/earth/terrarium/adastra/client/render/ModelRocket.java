package earth.terrarium.adastra.client.render;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
class ModelRocket extends ModelBase {

    private final ModelRenderer root;

    ModelRocket(int tier) {
        textureWidth = 128;
        textureHeight = 128;

        RocketSpec spec = RocketSpec.forTier(tier);
        root = part();

        buildBody(spec);
        buildNose(spec);
        buildEngine(spec);
        buildFins(spec);
        if (spec.boosterHeight > 0) {
            buildBoosters(spec);
        }
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw,
                       float headPitch, float scale) {
        root.render(scale);
    }

    private void buildBody(RocketSpec spec) {
        int bodyHeight = spec.bodyTop - spec.bodyBottom;
        int radius = spec.radius;

        addBox(root, 0, 0, -radius, spec.bodyBottom, -radius - 1, radius * 2, bodyHeight, 1);
        addBox(root, 36, 0, -radius, spec.bodyBottom, radius, radius * 2, bodyHeight, 1);
        addBox(root, 36, 18, -radius - 1, spec.bodyBottom, -radius, 1, bodyHeight, radius * 2);
        addBox(root, 0, 18, radius, spec.bodyBottom, -radius, 1, bodyHeight, radius * 2);

        addBox(root, 0, 77, -radius, spec.bodyBottom, -radius, 2, bodyHeight, 2);
        addBox(root, 0, 77, radius - 2, spec.bodyBottom, -radius, 2, bodyHeight, 2);
        addBox(root, 0, 77, -radius, spec.bodyBottom, radius - 2, 2, bodyHeight, 2);
        addBox(root, 0, 77, radius - 2, spec.bodyBottom, radius - 2, 2, bodyHeight, 2);

        addBand(spec.bodyBottom, radius + 1, 5);
        addBand(spec.bodyTop - 2, radius + 1, 4);
        addBand(spec.bodyBottom + bodyHeight / 2, radius + 1, 2);

        int windowY = spec.bodyTop - Math.min(18, bodyHeight / 2);
        addBox(root, 36, 44, -6, windowY, -radius - 2, 12, 12, 1);
        addBox(root, 35, 58, -4, windowY + 10, -radius - 3, 8, 1, 1);
        addBox(root, 63, 43, -4, windowY + 2, -radius - 3, 1, 8, 1);
        addBox(root, 63, 43, 3, windowY + 2, -radius - 3, 1, 8, 1);
    }

    private void buildNose(RocketSpec spec) {
        int radius = spec.radius;
        int y = spec.bodyTop;

        addBand(y, radius + 1, 2);
        addBox(root, 65, 45, -7, y + 1, -7, 14, 8, 14);
        addBox(root, 104, 67, -5, y + 8, -5, 10, 7, 10);
        addBox(root, 88, 69, -3, y + 14, -3, 6, 6, 6);

        if (spec.tier >= 2) {
            addBox(root, 80, 69, -1, y + 19, -1, 2, spec.noseMastHeight, 2);
            addBox(root, 64, 69, -2, y + 18 + spec.noseMastHeight, -2, 4, 4, 4);
        } else {
            addBox(root, 80, 69, -1, y + 19, -1, 2, 8, 2);
            addBox(root, 64, 69, -2, y + 26, -2, 4, 4, 4);
        }

        if (spec.tier == 4) {
            addBox(root, 27, 77, -8, y + 1, -1, 16, 12, 1);
            addBox(root, 27, 89, -1, y + 1, -8, 1, 12, 16);
        }
    }

    private void buildEngine(RocketSpec spec) {
        int radius = spec.radius;
        addBand(spec.bottom, radius - 1, 4);
        addBox(root, 78, 22, -radius + 1, spec.bottom + 1, -radius + 1, radius * 2 - 2, 1, radius * 2 - 2);
        addBox(root, 94, 15, -6, spec.bottom, -6, 12, 3, 12);
        addBox(root, 80, 81, -5, spec.bottom - 2, -5, 10, 2, 10);
    }

    private void buildFins(RocketSpec spec) {
        int finHeight = spec.tier >= 3 ? 20 : 16;
        int finLength = spec.tier >= 3 ? 11 : 9;
        float[] rotations = {0.7854f, 2.3562f, -2.3562f, -0.7854f};
        for (float rotation : rotations) {
            ModelRenderer fin = part();
            fin.rotateAngleY = rotation;
            fin.setTextureOffset(72, 21).addBox(-1.0f, spec.bottom + 3, spec.radius - 1, 2, finHeight, finLength);
            fin.setTextureOffset(72, 0).addBox(-2.0f, spec.bottom + 1, spec.radius + finLength - 2, 4, finHeight + 1, 3);
            root.addChild(fin);
        }
    }

    private void buildBoosters(RocketSpec spec) {
        addBooster(spec, -1);
        addBooster(spec, 1);
    }

    private void addBooster(RocketSpec spec, int side) {
        int x = side < 0 ? -spec.radius - 10 : spec.radius + 2;
        int y = spec.bottom + 4;
        int height = spec.boosterHeight;

        addBox(root, 96, 49, x, y, -4, 8, height, 8);
        addBox(root, 104, 90, x + 1, y + height, -3, 6, 6, 6);
        addBox(root, 32, 79, x, y - 5, -4, 8, 5, 8);
        addBox(root, 72, 64, x + 1, y - 1, -3, 6, 2, 6);

        int strutX = side < 0 ? x + 8 : x - 2;
        addBox(root, 110, 32, strutX, y + 8, -2, 2, 4, 4);
        addBox(root, 110, 32, strutX, y + height - 10, -2, 2, 4, 4);
    }

    private void addBand(int y, int radius, int height) {
        addBox(root, 0, 62, -radius, y, -radius, radius * 2, height, 1);
        addBox(root, 0, 62, -radius, y, radius - 1, radius * 2, height, 1);
        addBox(root, 0, 42, -radius, y, -radius, 1, height, radius * 2);
        addBox(root, 0, 42, radius - 1, y, -radius, 1, height, radius * 2);
    }

    private void addBox(ModelRenderer parent, int textureX, int textureY, float x, float y, float z,
                        int width, int height, int depth) {
        parent.setTextureOffset(textureX, textureY).addBox(x, y, z, width, height, depth);
    }

    private ModelRenderer part() {
        return new ModelRenderer(this);
    }

    private static final class RocketSpec {

        private final int tier;
        private final int bottom;
        private final int bodyBottom;
        private final int bodyTop;
        private final int radius;
        private final int boosterHeight;
        private final int noseMastHeight;

        private RocketSpec(int tier, int totalHeight, int bottomHeight, int noseHeight, int radius,
                           int boosterHeight, int noseMastHeight) {
            this.tier = tier;
            this.bottom = -totalHeight / 2;
            this.bodyBottom = bottom + bottomHeight;
            this.bodyTop = totalHeight / 2 - noseHeight;
            this.radius = radius;
            this.boosterHeight = boosterHeight;
            this.noseMastHeight = noseMastHeight;
        }

        private static RocketSpec forTier(int tier) {
            switch (tier) {
                case 2:
                    return new RocketSpec(2, 77, 10, 22, 9, 0, 14);
                case 3:
                    return new RocketSpec(3, 88, 10, 24, 9, 26, 14);
                case 4:
                    return new RocketSpec(4, 112, 12, 32, 9, 35, 11);
                case 1:
                default:
                    return new RocketSpec(1, 74, 10, 22, 9, 0, 8);
            }
        }
    }
}
