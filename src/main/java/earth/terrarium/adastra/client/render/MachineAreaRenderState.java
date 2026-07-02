package earth.terrarium.adastra.client.render;

import earth.terrarium.adastra.common.tile.GravityNormalizerTileEntity;
import earth.terrarium.adastra.common.tile.OxygenDistributorTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import org.lwjgl.opengl.GL11;

public final class MachineAreaRenderState {

    private static final int SEGMENTS = 96;

    private static BlockPos gravityNormalizerPos;
    private static BlockPos oxygenDistributorPos;
    private static boolean showGravityNormalizerArea;
    private static boolean showOxygenDistributorArea;

    private MachineAreaRenderState() {
    }

    public static void toggleGravityNormalizer(BlockPos pos) {
        if (pos == null) {
            showGravityNormalizerArea = false;
            gravityNormalizerPos = null;
            return;
        }
        if (showGravityNormalizerArea && pos.equals(gravityNormalizerPos)) {
            showGravityNormalizerArea = false;
            gravityNormalizerPos = null;
        } else {
            showGravityNormalizerArea = true;
            gravityNormalizerPos = pos.toImmutable();
        }
    }

    public static boolean isShowingGravityNormalizer(BlockPos pos) {
        return showGravityNormalizerArea && pos != null && pos.equals(gravityNormalizerPos);
    }

    public static void toggleOxygenDistributor(BlockPos pos) {
        if (pos == null) {
            showOxygenDistributorArea = false;
            oxygenDistributorPos = null;
            return;
        }
        if (showOxygenDistributorArea && pos.equals(oxygenDistributorPos)) {
            showOxygenDistributorArea = false;
            oxygenDistributorPos = null;
        } else {
            showOxygenDistributorArea = true;
            oxygenDistributorPos = pos.toImmutable();
        }
    }

    public static boolean isShowingOxygenDistributor(BlockPos pos) {
        return showOxygenDistributorArea && pos != null && pos.equals(oxygenDistributorPos);
    }

    public static void clear() {
        showGravityNormalizerArea = false;
        showOxygenDistributorArea = false;
        gravityNormalizerPos = null;
        oxygenDistributorPos = null;
    }

    public static void render(RenderWorldLastEvent event) {
        Minecraft minecraft = Minecraft.getMinecraft();
        if (minecraft.world == null) {
            clear();
            return;
        }

        renderGravityNormalizer(event, minecraft);
        renderOxygenDistributor(event, minecraft);
    }

    private static void renderGravityNormalizer(RenderWorldLastEvent event, Minecraft minecraft) {
        if (!showGravityNormalizerArea || gravityNormalizerPos == null) {
            return;
        }

        TileEntity tile = minecraft.world.getTileEntity(gravityNormalizerPos);
        if (!(tile instanceof GravityNormalizerTileEntity)) {
            showGravityNormalizerArea = false;
            gravityNormalizerPos = null;
            return;
        }

        GravityNormalizerTileEntity normalizer = (GravityNormalizerTileEntity) tile;
        renderSphere(event.getPartialTicks(), gravityNormalizerPos, normalizer.getWorkingRadius(), 140, 105, 255, 170);
    }

    private static void renderOxygenDistributor(RenderWorldLastEvent event, Minecraft minecraft) {
        if (!showOxygenDistributorArea || oxygenDistributorPos == null) {
            return;
        }

        TileEntity tile = minecraft.world.getTileEntity(oxygenDistributorPos);
        if (!(tile instanceof OxygenDistributorTileEntity)) {
            showOxygenDistributorArea = false;
            oxygenDistributorPos = null;
            return;
        }

        OxygenDistributorTileEntity distributor = (OxygenDistributorTileEntity) tile;
        renderSphere(event.getPartialTicks(), oxygenDistributorPos, distributor.getWorkingRadius(), 80, 210, 255, 170);
    }

    private static void renderSphere(float partialTicks, BlockPos pos, int radius, int red, int green, int blue, int alpha) {
        Entity camera = Minecraft.getMinecraft().getRenderViewEntity();
        if (camera == null || radius <= 0) {
            return;
        }

        double cameraX = camera.lastTickPosX + (camera.posX - camera.lastTickPosX) * partialTicks;
        double cameraY = camera.lastTickPosY + (camera.posY - camera.lastTickPosY) * partialTicks;
        double cameraZ = camera.lastTickPosZ + (camera.posZ - camera.lastTickPosZ) * partialTicks;
        double centerX = pos.getX() + 0.5D - cameraX;
        double centerY = pos.getY() + 0.5D - cameraY;
        double centerZ = pos.getZ() + 0.5D - cameraZ;

        GlStateManager.pushMatrix();
        GlStateManager.disableTexture2D();
        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.depthMask(false);
        GlStateManager.glLineWidth(2.0F);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        drawCircle(buffer, tessellator, centerX, centerY, centerZ, radius, 0, red, green, blue, alpha);
        drawCircle(buffer, tessellator, centerX, centerY, centerZ, radius, 1, red, green, blue, alpha);
        drawCircle(buffer, tessellator, centerX, centerY, centerZ, radius, 2, red, green, blue, alpha);

        GlStateManager.glLineWidth(1.0F);
        GlStateManager.depthMask(true);
        GlStateManager.disableBlend();
        GlStateManager.enableLighting();
        GlStateManager.enableTexture2D();
        GlStateManager.popMatrix();
    }

    private static void drawCircle(BufferBuilder buffer, Tessellator tessellator,
                                   double centerX, double centerY, double centerZ,
                                   double radius, int plane,
                                   int red, int green, int blue, int alpha) {
        buffer.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);
        for (int i = 0; i <= SEGMENTS; i++) {
            double angle = i * Math.PI * 2.0D / SEGMENTS;
            double sin = Math.sin(angle) * radius;
            double cos = Math.cos(angle) * radius;
            double x = centerX;
            double y = centerY;
            double z = centerZ;
            if (plane == 0) {
                x += cos;
                z += sin;
            } else if (plane == 1) {
                x += cos;
                y += sin;
            } else {
                y += cos;
                z += sin;
            }
            buffer.pos(x, y, z).color(red, green, blue, alpha).endVertex();
        }
        tessellator.draw();
    }
}
