package earth.terrarium.adastra.client.render;

import earth.terrarium.adastra.Reference;
import earth.terrarium.adastra.common.registry.ModItems;
import earth.terrarium.adastra.common.items.ConfigurableRocketItem;
import earth.terrarium.adastra.common.rocket.ConfigurableRocketSpec;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class VehicleItemStackRenderer extends TileEntityItemStackRenderer {

    public static final VehicleItemStackRenderer INSTANCE = new VehicleItemStackRenderer();

    private final ModelBase tier1Rocket = new ModelRocket(1);
    private final ModelBase tier2Rocket = new ModelRocket(2);
    private final ModelBase tier3Rocket = new ModelRocket(3);
    private final ModelBase tier4Rocket = new ModelRocket(4);
    private final ModelBase tier5Rocket = new ModelRocket(5);
    private final ModelBase tier6Rocket = new ModelRocket(6);
    private final ModelBase tier7Rocket = new ModelRocket(7);
    private final ModelBase rover = new ModelRover();
    private final java.util.Map<Integer, ModelBase> configurableRocketModels = new java.util.HashMap<>();

    private VehicleItemStackRenderer() {
    }

    @Override
    public void renderByItem(ItemStack stack) {
        Item item = stack.getItem();
        if (item == ModItems.TIER_1_ROCKET) {
            renderRocket(tier1Rocket, texture("rocket/tier_1_rocket"), 0.11f, -0.05f);
        } else if (item == ModItems.TIER_2_ROCKET) {
            renderRocket(tier2Rocket, texture("rocket/tier_2_rocket"), 0.11f, -0.05f);
        } else if (item == ModItems.TIER_3_ROCKET) {
            renderRocket(tier3Rocket, texture("rocket/tier_3_rocket"), 0.1f, -0.05f);
        } else if (item == ModItems.TIER_4_ROCKET) {
            renderRocket(tier4Rocket, texture("rocket/tier_4_rocket"), 0.08f, -0.05f);
        } else if (item == ModItems.TIER_5_ROCKET) {
            renderRocket(tier5Rocket, texture("rocket/tier_5_rocket"), 0.08f, -0.05f);
        } else if (item == ModItems.TIER_6_ROCKET) {
            renderRocket(tier6Rocket, texture("rocket/tier_6_rocket"), 0.08f, -0.05f);
        } else if (item == ModItems.TIER_7_ROCKET) {
            renderRocket(tier7Rocket, texture("rocket/tier_7_rocket"), 0.08f, -0.05f);
        } else if (item instanceof ConfigurableRocketItem) {
            ConfigurableRocketSpec spec = ((ConfigurableRocketItem) item).getSpec();
            renderRocket(configurableModel(spec), ConfigurableRocketTextureManager.textureFor(spec), spec.getModelTier() >= 4 ? 0.08f : 0.11f, -0.05f);
        } else if (item == ModItems.TIER_1_ROVER) {
            renderRover();
        }
    }

    private ModelBase configurableModel(ConfigurableRocketSpec spec) {
        int modelTier = spec.getModelTier();
        int cacheKey = spec.usesExtendraModel()
            ? 100 + modelTier
            : modelTier;
        ModelBase model = configurableRocketModels.get(cacheKey);
        if (model == null) {
            model = spec.usesExtendraModel()
                ? new ModelExtendraRocket(modelTier)
                : new ModelRocket(modelTier);
            configurableRocketModels.put(cacheKey, model);
        }
        return model;
    }

    private void renderRocket(ModelBase model, ResourceLocation texture, float scale, float yOffset) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(0.5f, 0.55f + yOffset, 0.5f);
        GlStateManager.rotate(180.0f, 0.0f, 0.0f, 1.0f);
        GlStateManager.rotate(-35.0f, 1.0f, 0.0f, 0.0f);
        GlStateManager.rotate(35.0f, 0.0f, 1.0f, 0.0f);
        GlStateManager.scale(scale, scale, scale);
        Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
        GlStateManager.disableCull();
        model.render(null, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0625f);
        GlStateManager.enableCull();
        GlStateManager.popMatrix();
    }

    private void renderRover() {
        GlStateManager.pushMatrix();
        GlStateManager.translate(0.5f, 0.55f, 0.5f);
        GlStateManager.rotate(180.0f, 0.0f, 0.0f, 1.0f);
        GlStateManager.rotate(-25.0f, 1.0f, 0.0f, 0.0f);
        GlStateManager.rotate(45.0f, 0.0f, 1.0f, 0.0f);
        GlStateManager.scale(0.08f, 0.08f, 0.08f);
        Minecraft.getMinecraft().getTextureManager().bindTexture(texture("rover/tier_1_rover"));
        GlStateManager.disableCull();
        rover.render(null, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0625f);
        GlStateManager.enableCull();
        GlStateManager.popMatrix();
    }

    private static ResourceLocation texture(String path) {
        return new ResourceLocation(Reference.MOD_ID, "textures/entity/" + path + ".png");
    }
}

