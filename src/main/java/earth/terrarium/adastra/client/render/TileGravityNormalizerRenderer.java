package earth.terrarium.adastra.client.render;

import earth.terrarium.adastra.Reference;
import earth.terrarium.adastra.common.blocks.AdAstraAttachedMachineBlock;
import earth.terrarium.adastra.common.blocks.AdAstraIndustrialLampBlock;
import earth.terrarium.adastra.common.registry.ModBlocks;
import earth.terrarium.adastra.common.tile.GravityNormalizerTileEntity;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class TileGravityNormalizerRenderer extends TileEntitySpecialRenderer<GravityNormalizerTileEntity> {

    public static final ItemRenderer ITEM_RENDERER = new ItemRenderer();
    public static final ModelResourceLocation TOP_MODEL = new ModelResourceLocation(Reference.MOD_ID + ":gravity_normalizer_top", "normal");
    public static final ModelResourceLocation TOE_MODEL = new ModelResourceLocation(Reference.MOD_ID + ":gravity_normalizer_toe", "normal");

    private static final float SIN_45 = (float) Math.sin(Math.PI / 4.0d);
    private static final float FIELD_RADIUS = 0.6f;
    private static final float FIELD_CENTER_Y = 1.10f;
    private static final float FIELD_VERTICAL_SPACING = 0.45f;
    private static final int RING_COUNT = 3;
    private static final int RING_SEGMENTS = 24;

    @Override
    public void render(GravityNormalizerTileEntity te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if (te == null || te.getWorld() == null) {
            return;
        }

        IBlockState state = te.getWorld().getBlockState(te.getPos());
        float animation = te.getLastAnimation() + (te.getAnimation() - te.getLastAnimation()) * partialTicks;
        renderAnimatedModels(state, x, y, z, animation, alpha);
        if (!te.isLit()) {
            return;
        }

        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5, y + FIELD_CENTER_Y, z + 0.5);
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
        GlStateManager.disableLighting();
        GlStateManager.depthMask(false);

        long time = te.getWorld().getTotalWorldTime();
        float animationTime = (time + partialTicks) * 0.02f;

        // Render rotating gravitational field rings
        for (int i = 0; i < RING_COUNT; i++) {
            float ringOffset = (i * (2.0f / RING_COUNT) - 1.0f) * FIELD_VERTICAL_SPACING;
            float ringRotation = animationTime * (30.0f + i * 15.0f);
            float normalizedOffset = Math.abs(ringOffset) / FIELD_VERTICAL_SPACING;
            float ringScale = 1.0f - normalizedOffset * 0.3f;
            renderGravityRing(ringOffset, ringRotation, ringScale, animationTime + i * 0.5f);
        }

        // Render central energy core
        renderEnergyCore(animationTime);

        GlStateManager.depthMask(true);
        GlStateManager.enableLighting();
        GlStateManager.disableBlend();
        GlStateManager.enableTexture2D();
        GlStateManager.popMatrix();
    }

    private static void renderAnimatedModels(IBlockState state, double x, double y, double z, float animation, float alpha) {
        Minecraft minecraft = Minecraft.getMinecraft();
        IBakedModel topModel = minecraft.getBlockRendererDispatcher()
            .getBlockModelShapes()
            .getModelManager()
            .getModel(TOP_MODEL);
        IBakedModel toeModel = minecraft.getBlockRendererDispatcher()
            .getBlockModelShapes()
            .getModelManager()
            .getModel(TOE_MODEL);

        if (topModel == minecraft.getBlockRendererDispatcher().getBlockModelShapes().getModelManager().getMissingModel()
            || toeModel == minecraft.getBlockRendererDispatcher().getBlockModelShapes().getModelManager().getMissingModel()) {
            return;
        }

        minecraft.renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);
        GlStateManager.enableTexture2D();
        GlStateManager.disableCull();
        GlStateManager.color(1.0f, 1.0f, 1.0f, alpha);

        applyAttachmentTransform(state);
        renderTopModel(minecraft, state, topModel, animation, alpha);
        renderToeModels(minecraft, state, toeModel, animation, alpha);

        GlStateManager.enableCull();
        GlStateManager.popMatrix();
    }

    private static void renderBaseModel(IBlockState state, float alpha) {
        Minecraft minecraft = Minecraft.getMinecraft();
        IBakedModel model = minecraft.getBlockRendererDispatcher()
            .getBlockModelShapes()
            .getModelForState(state);

        minecraft.renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        GlStateManager.enableTexture2D();
        GlStateManager.disableCull();
        GlStateManager.color(1.0f, 1.0f, 1.0f, alpha);
        renderModel(minecraft, state, model, alpha);
        GlStateManager.enableCull();
    }

    private static void renderTopModel(Minecraft minecraft, IBlockState state, IBakedModel model, float animation, float alpha) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(0.5f, 0.7f, 0.5f);
        GlStateManager.rotate(animation, 1.0f, 0.0f, 0.0f);
        GlStateManager.rotate(animation, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotate(animation, 0.0f, 0.0f, 1.0f);

        float yRot = animation / 1.2f;
        GlStateManager.rotate(yRot, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotate(60.0f, SIN_45, 0.0f, SIN_45);
        GlStateManager.rotate(60.0f, SIN_45, 0.0f, SIN_45);
        GlStateManager.rotate(yRot, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotate(60.0f, SIN_45, 0.0f, SIN_45);
        GlStateManager.rotate(yRot, 0.0f, 1.0f, 0.0f);

        GlStateManager.translate(-0.5f, -0.7f, -0.5f);
        renderModel(minecraft, state, model, alpha);
        GlStateManager.popMatrix();
    }

    private static void renderToeModels(Minecraft minecraft, IBlockState state, IBakedModel model, float animation, float alpha) {
        for (int i = 0; i < 4; i++) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(0.5f, 0.0f, 0.5f);
            GlStateManager.rotate(90.0f * i, 0.0f, 1.0f, 0.0f);
            GlStateManager.translate(-0.5f, 0.0f, -0.5f);

            GlStateManager.translate(0.27f, 0.27f, 0.27f);
            GlStateManager.rotate((float) Math.sin(animation / 50.0f + i) * 10.0f, 1.0f, 0.0f, 0.0f);
            GlStateManager.translate(-0.27f, -0.27f, -0.27f);

            renderModel(minecraft, state, model, alpha);
            GlStateManager.popMatrix();
        }
    }

    private static void renderModel(Minecraft minecraft, IBlockState state, IBakedModel model, float alpha) {
        minecraft.getBlockRendererDispatcher()
            .getBlockModelRenderer()
            .renderModelBrightnessColor(state, model, 1.0f, 1.0f, 1.0f, alpha);
    }

    private static void applyAttachmentTransform(IBlockState state) {
        if (!state.getPropertyKeys().contains(AdAstraAttachedMachineBlock.FACE)
            || !state.getPropertyKeys().contains(AdAstraAttachedMachineBlock.FACING)) {
            return;
        }

        AdAstraIndustrialLampBlock.AttachFace face = state.getValue(AdAstraAttachedMachineBlock.FACE);
        EnumFacing direction = state.getValue(AdAstraAttachedMachineBlock.FACING);
        if (face == AdAstraIndustrialLampBlock.AttachFace.CEILING) {
            GlStateManager.translate(0.0f, 1.0f, 1.0f);
            GlStateManager.rotate(180.0f, 1.0f, 0.0f, 0.0f);
        } else if (face == AdAstraIndustrialLampBlock.AttachFace.WALL) {
            if (direction == EnumFacing.NORTH) {
                GlStateManager.translate(0.0f, 0.0f, 1.0f);
            } else if (direction == EnumFacing.SOUTH) {
                GlStateManager.translate(1.0f, 0.0f, 0.0f);
            } else if (direction == EnumFacing.WEST) {
                GlStateManager.translate(1.0f, 0.0f, 1.0f);
            }
            rotateBlock(direction);
            GlStateManager.rotate(-90.0f, 1.0f, 0.0f, 0.0f);
        }
    }

    private static void rotateBlock(EnumFacing facing) {
        switch (facing) {
            case SOUTH:
                GlStateManager.rotate(180.0f, 0.0f, 1.0f, 0.0f);
                break;
            case WEST:
                GlStateManager.rotate(90.0f, 0.0f, 1.0f, 0.0f);
                break;
            case EAST:
                GlStateManager.rotate(270.0f, 0.0f, 1.0f, 0.0f);
                break;
            case UP:
                GlStateManager.rotate(90.0f, 1.0f, 0.0f, 0.0f);
                break;
            case DOWN:
                GlStateManager.rotate(90.0f, -1.0f, 0.0f, 0.0f);
                break;
            case NORTH:
            default:
                break;
        }
    }

    private void renderGravityRing(float yOffset, float rotation, float scale, float animationTime) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();

        GlStateManager.pushMatrix();
        GlStateManager.rotate(rotation, 0, 1, 0);
        GlStateManager.scale(scale, 1.0f, scale);

        float pulseIntensity = (float) ((Math.sin(animationTime * 2.0) + 1.0) / 2.0);
        float ringAlpha = 0.2f + pulseIntensity * 0.3f;
        float ringThickness = 0.02f;

        buffer.begin(GL11.GL_TRIANGLE_STRIP, DefaultVertexFormats.POSITION_COLOR);

        for (int segment = 0; segment <= RING_SEGMENTS; segment++) {
            float angle = (float) (segment * 2.0 * Math.PI / RING_SEGMENTS);
            float x = (float) (FIELD_RADIUS * Math.cos(angle));
            float z = (float) (FIELD_RADIUS * Math.sin(angle));

            float brightness = 0.6f + pulseIntensity * 0.4f;
            int r = (int) (150 * brightness);
            int g = (int) (100 * brightness);
            int b = (int) (255 * brightness);
            int a = (int) (255 * ringAlpha);

            buffer.pos(x, yOffset - ringThickness, z).color(r, g, b, a).endVertex();
            buffer.pos(x, yOffset + ringThickness, z).color(r, g, b, a).endVertex();
        }

        tessellator.draw();
        GlStateManager.popMatrix();
    }

    private void renderEnergyCore(float animationTime) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();

        float pulseIntensity = (float) ((Math.sin(animationTime * 3.0) + 1.0) / 2.0);
        float coreSize = 0.08f + pulseIntensity * 0.04f;
        float coreAlpha = 0.5f + pulseIntensity * 0.3f;

        float brightness = 0.8f + pulseIntensity * 0.2f;
        int r = (int) (200 * brightness);
        int g = (int) (150 * brightness);
        int b = (int) (255 * brightness);
        int a = (int) (255 * coreAlpha);

        // Render core as a simple quad facing camera (billboard effect)
        GlStateManager.rotate(-rendererDispatcher.entityYaw, 0, 1, 0);
        GlStateManager.rotate(rendererDispatcher.entityPitch, 1, 0, 0);

        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        buffer.pos(-coreSize, -coreSize, 0).color(r, g, b, a).endVertex();
        buffer.pos(-coreSize, coreSize, 0).color(r, g, b, a).endVertex();
        buffer.pos(coreSize, coreSize, 0).color(r, g, b, a).endVertex();
        buffer.pos(coreSize, -coreSize, 0).color(r, g, b, a).endVertex();
        tessellator.draw();
    }

    @SideOnly(Side.CLIENT)
    public static class ItemRenderer extends TileEntityItemStackRenderer {

        private ItemRenderer() {
        }

        @Override
        public void renderByItem(ItemStack stack) {
            IBlockState state = ModBlocks.GRAVITY_NORMALIZER.getDefaultState();
            GlStateManager.pushMatrix();
            renderBaseModel(state, 1.0f);
            float animation = System.currentTimeMillis() / 5.0f;
            renderAnimatedModels(state, 0.0d, 0.0d, 0.0d, animation, 1.0f);
            GlStateManager.popMatrix();
        }
    }
}
