package earth.terrarium.adastra.client.render;

import earth.terrarium.adastra.Reference;
import earth.terrarium.adastra.common.tile.GlobeTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class TileGlobeRenderer extends TileEntitySpecialRenderer<GlobeTileEntity> {

    public static final ItemRenderer ITEM_RENDERER = new ItemRenderer();

    @Override
    public void render(GlobeTileEntity te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if (te == null || te.getWorld() == null) {
            return;
        }

        float yRot = te.getLastYRot() + (te.getYRot() - te.getLastYRot()) * partialTicks;
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);
        renderGlobe(te.getWorld().getBlockState(te.getPos()), yRot);
        GlStateManager.popMatrix();
    }

    private static void renderGlobe(IBlockState state, float yRot) {
        GlStateManager.pushMatrix();
        GlStateManager.disableLighting();
        GlStateManager.disableCull();
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        renderBakedStand(state);
        renderBakedGlobe(state, yRot);

        GlStateManager.enableCull();
        GlStateManager.enableLighting();
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.popMatrix();
    }

    private static void renderBakedStand(IBlockState state) {
        if (state == null || state.getBlock().getRegistryName() == null) {
            return;
        }

        Minecraft minecraft = Minecraft.getMinecraft();
        String path = state.getBlock().getRegistryName().getPath();
        ModelResourceLocation modelLocation = new ModelResourceLocation(
            new ResourceLocation(Reference.MOD_ID, path), "normal");
        IBakedModel model = minecraft.getBlockRendererDispatcher()
            .getBlockModelShapes()
            .getModelManager()
            .getModel(modelLocation);
        if (model == minecraft.getBlockRendererDispatcher().getBlockModelShapes().getModelManager().getMissingModel()) {
            return;
        }

        minecraft.renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        GlStateManager.enableTexture2D();
        GlStateManager.enableAlpha();
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1f);
        minecraft.getBlockRendererDispatcher()
            .getBlockModelRenderer()
            .renderModelBrightnessColor(state, model, 1.0f, 1.0f, 1.0f, 1.0f);
    }

    private static void renderBakedGlobe(IBlockState state, float yRot) {
        if (state == null || state.getBlock().getRegistryName() == null) {
            return;
        }

        Minecraft minecraft = Minecraft.getMinecraft();
        // Globe block registry names already end in "_globe" (for example,
        // "earth_globe"); the cube model adds only the "_cube" suffix.
        String path = state.getBlock().getRegistryName().getPath() + "_cube";
        ModelResourceLocation modelLocation = new ModelResourceLocation(
            new ResourceLocation(Reference.MOD_ID, path), "normal");
        IBakedModel model = minecraft.getBlockRendererDispatcher()
            .getBlockModelShapes()
            .getModelManager()
            .getModel(modelLocation);
        if (model == minecraft.getBlockRendererDispatcher().getBlockModelShapes().getModelManager().getMissingModel()) {
            return;
        }

        minecraft.renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        GlStateManager.pushMatrix();
        GlStateManager.enableTexture2D();
        GlStateManager.enableAlpha();
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1f);
        GlStateManager.translate(0.5f, 0.0f, 0.5f);
        GlStateManager.rotate(-yRot, 0.0f, 1.0f, 0.0f);
        GlStateManager.translate(-0.5f, 0.0f, -0.5f);
        minecraft.getBlockRendererDispatcher()
            .getBlockModelRenderer()
            .renderModelBrightnessColor(state, model, 1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.popMatrix();
    }

    @SideOnly(Side.CLIENT)
    public static class ItemRenderer extends TileEntityItemStackRenderer {

        private ItemRenderer() {
        }

        @Override
        public void renderByItem(ItemStack stack) {
            Block block = Block.getBlockFromItem(stack.getItem());
            if (block == null || block.getRegistryName() == null) {
                return;
            }
            GlStateManager.pushMatrix();
            float yRot = (System.currentTimeMillis() / 20.0f) % 360.0f;
            renderGlobe(block.getDefaultState(), yRot);
            GlStateManager.popMatrix();
        }
    }
}
