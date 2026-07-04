package earth.terrarium.adastra.client.render;

import earth.terrarium.adastra.Reference;
import earth.terrarium.adastra.common.blocks.AdAstraAttachedMachineBlock;
import earth.terrarium.adastra.common.blocks.AdAstraIndustrialLampBlock;
import earth.terrarium.adastra.common.registry.ModBlocks;
import earth.terrarium.adastra.common.tile.OxygenDistributorTileEntity;
import earth.terrarium.adastra.common.registry.ModParticles;
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

import java.util.Random;

@SideOnly(Side.CLIENT)
public class TileOxygenDistributorRenderer extends TileEntitySpecialRenderer<OxygenDistributorTileEntity> {

    public static final ItemRenderer ITEM_RENDERER = new ItemRenderer();
    public static final ModelResourceLocation TOP_MODEL = new ModelResourceLocation(Reference.MOD_ID + ":oxygen_distributor_top", "normal");

    private static final int BUBBLE_SPAWN_CHANCE = 20;
    private static final float BUBBLE_SPAWN_RADIUS = 0.4f;
    private static final float BUBBLE_SPAWN_HEIGHT = 0.8f;
    private static final int RING_SEGMENTS = 16;
    private static final float FAN_RADIUS = 0.35f;

    @Override
    public void render(OxygenDistributorTileEntity te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if (te == null || te.getWorld() == null) {
            return;
        }

        IBlockState state = te.getWorld().getBlockState(te.getPos());
        float yRot = te.getLastYRot() + (te.getYRot() - te.getLastYRot()) * partialTicks;
        renderRotatingTop(state, x, y, z, yRot, alpha);

        // Spawn oxygen bubble particles around the distributor when active
        if (te.isLit()) {
            Random random = te.getWorld().rand;
            if (random.nextInt(BUBBLE_SPAWN_CHANCE) == 0) {
                spawnOxygenBubble(te, x, y, z, random);
            }

            // Render spinning fan/vent effect
            renderSpinningFan(te, x, y, z, partialTicks);
        }
    }

    private static void renderRotatingTop(IBlockState state, double x, double y, double z, float yRot, float alpha) {
        Minecraft minecraft = Minecraft.getMinecraft();
        IBakedModel model = minecraft.getBlockRendererDispatcher()
            .getBlockModelShapes()
            .getModelManager()
            .getModel(TOP_MODEL);

        if (model == minecraft.getBlockRendererDispatcher().getBlockModelShapes().getModelManager().getMissingModel()) {
            return;
        }

        minecraft.renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);
        GlStateManager.enableTexture2D();
        GlStateManager.disableCull();
        GlStateManager.color(1.0f, 1.0f, 1.0f, alpha);

        applyAttachmentTransform(state);
        GlStateManager.translate(0.5f, 0.0f, 0.5f);
        GlStateManager.rotate(-yRot, 0.0f, 1.0f, 0.0f);
        GlStateManager.translate(-0.5f, 0.0f, -0.5f);

        renderModel(minecraft, state, model, alpha);

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

    private void spawnOxygenBubble(OxygenDistributorTileEntity te, double x, double y, double z, Random random) {
        double worldX = te.getPos().getX() + x + 0.5;
        double worldY = te.getPos().getY() + y + BUBBLE_SPAWN_HEIGHT;
        double worldZ = te.getPos().getZ() + z + 0.5;

        // Random offset within spawn radius
        double offsetX = (random.nextDouble() - 0.5) * BUBBLE_SPAWN_RADIUS * 2.0;
        double offsetZ = (random.nextDouble() - 0.5) * BUBBLE_SPAWN_RADIUS * 2.0;

        // Upward velocity with slight random horizontal drift
        double velocityX = (random.nextDouble() - 0.5) * 0.02;
        double velocityY = 0.05 + random.nextDouble() * 0.03;
        double velocityZ = (random.nextDouble() - 0.5) * 0.02;

        Minecraft.getMinecraft().effectRenderer.spawnEffectParticle(
            ModParticles.OXYGEN_BUBBLE,
            worldX + offsetX,
            worldY,
            worldZ + offsetZ,
            velocityX,
            velocityY,
            velocityZ
        );
    }

    /**
     * Render a spinning fan/vent effect at the top of the oxygen distributor.
     * Ported from 1.20.x style with rotation animation.
     */
    private void renderSpinningFan(OxygenDistributorTileEntity te, double x, double y, double z, float partialTicks) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5, y + 0.7, z + 0.5);
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.disableLighting();
        GlStateManager.depthMask(false);

        long time = te.getWorld().getTotalWorldTime();
        float rotation = ((time + partialTicks) * 8.0f) % 360.0f;

        // Render spinning blades
        for (int blade = 0; blade < 4; blade++) {
            float bladeAngle = rotation + (blade * 90.0f);
            renderFanBlade(bladeAngle);
        }

        // Render central hub
        renderFanHub();

        GlStateManager.depthMask(true);
        GlStateManager.enableLighting();
        GlStateManager.disableBlend();
        GlStateManager.enableTexture2D();
        GlStateManager.popMatrix();
    }

    private void renderFanBlade(float angle) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();

        GlStateManager.pushMatrix();
        GlStateManager.rotate(angle, 0, 1, 0);

        // Cyan/blue color for oxygen effect
        int r = 100;
        int g = 200;
        int b = 255;
        int a = 180;

        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);

        // Blade is a thin rectangle extending from center
        float width = 0.05f;
        float length = FAN_RADIUS;

        buffer.pos(-width, 0, 0).color(r, g, b, a).endVertex();
        buffer.pos(-width, 0, length).color(r, g, b, a / 2).endVertex();
        buffer.pos(width, 0, length).color(r, g, b, a / 2).endVertex();
        buffer.pos(width, 0, 0).color(r, g, b, a).endVertex();

        tessellator.draw();
        GlStateManager.popMatrix();
    }

    private void renderFanHub() {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();

        // Brighter center hub
        int r = 150;
        int g = 220;
        int b = 255;
        int a = 220;

        buffer.begin(GL11.GL_TRIANGLE_FAN, DefaultVertexFormats.POSITION_COLOR);

        // Center point
        buffer.pos(0, 0, 0).color(r, g, b, a).endVertex();

        // Circle around center
        float hubRadius = 0.08f;
        for (int i = 0; i <= RING_SEGMENTS; i++) {
            float angle = (float) (i * 2.0 * Math.PI / RING_SEGMENTS);
            float xOffset = (float) (hubRadius * Math.cos(angle));
            float zOffset = (float) (hubRadius * Math.sin(angle));
            buffer.pos(xOffset, 0, zOffset).color(r, g, b, a).endVertex();
        }

        tessellator.draw();
    }

    @SideOnly(Side.CLIENT)
    public static class ItemRenderer extends TileEntityItemStackRenderer {

        private ItemRenderer() {
        }

        @Override
        public void renderByItem(ItemStack stack) {
            IBlockState state = ModBlocks.OXYGEN_DISTRIBUTOR.getDefaultState();
            GlStateManager.pushMatrix();
            renderBaseModel(state, 1.0f);
            float yRot = (System.currentTimeMillis() / 5.0f) % 360.0f;
            renderRotatingTop(state, 0.0d, 0.0d, 0.0d, yRot, 1.0f);
            GlStateManager.popMatrix();
        }
    }
}
