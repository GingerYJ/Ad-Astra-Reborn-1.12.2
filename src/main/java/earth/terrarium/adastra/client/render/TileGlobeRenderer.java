package earth.terrarium.adastra.client.render;

import earth.terrarium.adastra.Reference;
import earth.terrarium.adastra.common.tile.GlobeTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
        renderGlobe(te.getWorld().getBlockState(te.getPos()), yRot, te.getWorld(), te.getPos(), destroyStage, alpha);
        GlStateManager.popMatrix();
    }

    private static void renderGlobe(
        IBlockState state,
        float yRot,
        IBlockAccess world,
        BlockPos pos,
        int destroyStage,
        float alpha) {
        GlStateManager.pushMatrix();
        GlStateManager.disableLighting();
        GlStateManager.disableCull();
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        boolean destroying = BlockDestroyStageRenderer.isDestroying(destroyStage);
        renderBakedGlobe(state, yRot, world, pos, destroyStage, alpha, destroying);

        GlStateManager.enableCull();
        GlStateManager.enableLighting();
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.popMatrix();
    }

    private static void renderBakedGlobe(
        IBlockState state,
        float yRot,
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

        IBakedModel staticModel = new FilteredBakedModel(model, false);
        IBakedModel rotatingModel = new FilteredBakedModel(model, true);
        minecraft.renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        GlStateManager.enableTexture2D();
        GlStateManager.enableAlpha();
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1f);
        if (!destroying) {
            minecraft.getBlockRendererDispatcher()
                .getBlockModelRenderer()
                .renderModelBrightnessColor(state, staticModel, 1.0f, 1.0f, 1.0f, 1.0f);
        }
        BlockDestroyStageRenderer.renderDamageModelLocal(world, pos, state, staticModel, destroyStage, alpha);

        GlStateManager.pushMatrix();
        GlStateManager.translate(0.5f, 0.0f, 0.5f);
        GlStateManager.rotate(-yRot, 0.0f, 1.0f, 0.0f);
        GlStateManager.translate(-0.5f, 0.0f, -0.5f);
        if (!destroying) {
            minecraft.getBlockRendererDispatcher()
                .getBlockModelRenderer()
                .renderModelBrightnessColor(state, rotatingModel, 1.0f, 1.0f, 1.0f, 1.0f);
        }
        BlockDestroyStageRenderer.renderDamageModelLocal(world, pos, state, rotatingModel, destroyStage, alpha);
        GlStateManager.popMatrix();
    }

    private static boolean isRotatingQuad(BakedQuad quad) {
        VertexFormat format = quad.getFormat();
        int positionElement = -1;
        for (int index = 0; index < format.getElementCount(); index++) {
            if (format.getElement(index).isPositionElement()) {
                positionElement = index;
                break;
            }
        }
        if (positionElement < 0) {
            return false;
        }

        int[] vertexData = quad.getVertexData();
        int vertexStride = format.getIntegerSize();
        int positionOffset = format.getOffset(positionElement) / 4;
        float minX = Float.MAX_VALUE;
        float minY = Float.MAX_VALUE;
        float minZ = Float.MAX_VALUE;
        float maxX = -Float.MAX_VALUE;
        float maxY = -Float.MAX_VALUE;
        float maxZ = -Float.MAX_VALUE;
        for (int vertex = 0; vertex < 4; vertex++) {
            int offset = vertex * vertexStride + positionOffset;
            float x = Float.intBitsToFloat(vertexData[offset]);
            float y = Float.intBitsToFloat(vertexData[offset + 1]);
            float z = Float.intBitsToFloat(vertexData[offset + 2]);
            minX = Math.min(minX, x);
            minY = Math.min(minY, y);
            minZ = Math.min(minZ, z);
            maxX = Math.max(maxX, x);
            maxY = Math.max(maxY, y);
            maxZ = Math.max(maxZ, z);
        }

        float horizontalSize = Math.max(maxX - minX, maxZ - minZ);
        float verticalSize = maxY - minY;
        // The fixed support axle is tall but narrow; planet and ring quads are broad.
        boolean supportAxle = verticalSize > 0.9f && horizontalSize < 0.8f;
        // Oval planets have side faces that are 0.45 by 0.45 blocks after baking.
        // The base is wider but too flat, while the support axle is excluded above.
        boolean planetFace = horizontalSize > 0.4f && verticalSize > 0.3f;
        boolean broadPlanetOrRing = horizontalSize > 0.6f;
        return !supportAxle && (planetFace || broadPlanetOrRing);
    }

    private static final class FilteredBakedModel implements IBakedModel {

        private final IBakedModel delegate;
        private final boolean rotating;

        private FilteredBakedModel(IBakedModel delegate, boolean rotating) {
            this.delegate = delegate;
            this.rotating = rotating;
        }

        @Override
        public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) {
            List<BakedQuad> quads = delegate.getQuads(state, side, rand);
            if (quads.isEmpty()) {
                return Collections.emptyList();
            }
            List<BakedQuad> filtered = new ArrayList<>();
            for (BakedQuad quad : quads) {
                if (isRotatingQuad(quad) == rotating) {
                    filtered.add(quad);
                }
            }
            return filtered;
        }

        @Override
        public boolean isAmbientOcclusion() {
            return delegate.isAmbientOcclusion();
        }

        @Override
        public boolean isGui3d() {
            return delegate.isGui3d();
        }

        @Override
        public boolean isBuiltInRenderer() {
            return delegate.isBuiltInRenderer();
        }

        @Override
        public net.minecraft.client.renderer.texture.TextureAtlasSprite getParticleTexture() {
            return delegate.getParticleTexture();
        }

        @Override
        public net.minecraft.client.renderer.block.model.ItemCameraTransforms getItemCameraTransforms() {
            return delegate.getItemCameraTransforms();
        }

        @Override
        public net.minecraft.client.renderer.block.model.ItemOverrideList getOverrides() {
            return delegate.getOverrides();
        }
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
            float yRot = (System.currentTimeMillis() / 20.0f) % 360.0f;
            renderGlobe(block.getDefaultState(), yRot, null, null, -1, 1.0f);
        }
    }
}
