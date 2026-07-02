package earth.terrarium.adastra.client.render;

import earth.terrarium.adastra.common.tile.EnergizerTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class TileEnergizerRenderer extends TileEntitySpecialRenderer<EnergizerTileEntity> {

    private static final float BEAM_WIDTH = 0.0625f;
    private static final float BEAM_HEIGHT = 0.5f;
    private static final int BEAM_SEGMENTS = 8;

    @Override
    public void render(EnergizerTileEntity te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if (te == null) {
            return;
        }

        ItemStack stack = te.getStackInSlot(0);

        // Render floating item if present and machine is lit
        if (!stack.isEmpty() && te.isLit()) {
            renderFloatingItem(te, x, y, z, partialTicks, stack);
        }

        // Render energy beams if machine is lit
        if (te.isLit()) {
            renderEnergyBeams(te, x, y, z, partialTicks);
        }
    }

    private void renderFloatingItem(EnergizerTileEntity te, double x, double y, double z, float partialTicks, ItemStack stack) {
        GlStateManager.pushMatrix();

        long time = te.getWorld().getTotalWorldTime();
        double offset = Math.sin((time + partialTicks) / 8.0) / 8.0;

        GlStateManager.translate(x + 0.5, y + 1.6 + offset, z + 0.5);
        GlStateManager.rotate((time + partialTicks) * 4.0f, 0, 1, 0);

        GlStateManager.scale(0.5f, 0.5f, 0.5f);

        RenderHelper.enableStandardItemLighting();
        Minecraft.getMinecraft().getRenderItem().renderItem(stack, ItemCameraTransforms.TransformType.GROUND);
        RenderHelper.disableStandardItemLighting();

        GlStateManager.popMatrix();
    }

    private void renderEnergyBeams(EnergizerTileEntity te, double x, double y, double z, float partialTicks) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5, y + 0.4, z + 0.5);
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
        GlStateManager.disableLighting();
        GlStateManager.depthMask(false);

        long time = te.getWorld().getTotalWorldTime();
        float animationTime = (time + partialTicks) * 0.05f;

        // Render multiple energy beams
        for (int i = 0; i < 4; i++) {
            float angle = (i * 90.0f) + animationTime * 20.0f;
            renderEnergyBeam(angle, animationTime + i * 0.25f);
        }

        GlStateManager.depthMask(true);
        GlStateManager.enableLighting();
        GlStateManager.disableBlend();
        GlStateManager.enableTexture2D();
        GlStateManager.popMatrix();
    }

    private void renderEnergyBeam(float angleOffset, float animationTime) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();

        GlStateManager.pushMatrix();
        GlStateManager.rotate(angleOffset, 0, 1, 0);

        float pulseIntensity = (float) ((Math.sin(animationTime * 2.0) + 1.0) / 2.0);
        float beamAlpha = 0.3f + pulseIntensity * 0.4f;

        buffer.begin(GL11.GL_TRIANGLE_STRIP, DefaultVertexFormats.POSITION_COLOR);

        for (int segment = 0; segment <= BEAM_SEGMENTS; segment++) {
            float heightRatio = (float) segment / BEAM_SEGMENTS;
            float currentHeight = heightRatio * BEAM_HEIGHT;
            float widthScale = 1.0f - heightRatio * 0.5f;
            float currentWidth = BEAM_WIDTH * widthScale;

            float segmentPulse = (float) Math.sin((animationTime + heightRatio * 3.0) * 3.0) * 0.2f + 0.8f;
            float brightness = segmentPulse * (1.0f - heightRatio * 0.3f);

            int r = (int) (100 * brightness);
            int g = (int) (200 * brightness);
            int b = (int) (255 * brightness);
            int a = (int) (255 * beamAlpha * (1.0f - heightRatio * 0.5f));

            buffer.pos(-currentWidth, currentHeight, 0).color(r, g, b, a).endVertex();
            buffer.pos(currentWidth, currentHeight, 0).color(r, g, b, a).endVertex();
        }

        tessellator.draw();
        GlStateManager.popMatrix();
    }
}
