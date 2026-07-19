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
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
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

        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);
        renderGlobe(te.getWorld().getBlockState(te.getPos()), te.getWorld(), te.getPos(), destroyStage, alpha);
        GlStateManager.popMatrix();
    }

    private static void renderGlobe(IBlockState state, IBlockAccess world, BlockPos pos, int destroyStage, float alpha) {
        GlStateManager.pushMatrix();
        GlStateManager.disableLighting();
        GlStateManager.disableCull();
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        boolean destroying = BlockDestroyStageRenderer.isDestroying(destroyStage);
        renderBakedStand(state, world, pos, destroyStage, alpha, destroying);

        GlStateManager.enableCull();
        GlStateManager.enableLighting();
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.popMatrix();
    }

    private static void renderBakedStand(
        IBlockState state,
        IBlockAccess world,
        BlockPos pos,
        int destroyStage,
        float alpha,
        boolean destroying) {
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
        if (!destroying) {
            minecraft.getBlockRendererDispatcher()
                .getBlockModelRenderer()
                .renderModelBrightnessColor(state, model, 1.0f, 1.0f, 1.0f, 1.0f);
        }
        BlockDestroyStageRenderer.renderDamageModelLocal(world, pos, state, model, destroyStage, alpha);
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
            renderGlobe(block.getDefaultState(), null, null, -1, 1.0f);
        }
    }
}
