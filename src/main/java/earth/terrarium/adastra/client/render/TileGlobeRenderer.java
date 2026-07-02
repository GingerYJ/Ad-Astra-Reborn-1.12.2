package earth.terrarium.adastra.client.render;

import earth.terrarium.adastra.Reference;
import earth.terrarium.adastra.common.tile.GlobeTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class TileGlobeRenderer extends TileEntitySpecialRenderer<GlobeTileEntity> {

    public static final ItemRenderer ITEM_RENDERER = new ItemRenderer();

    private static final float GLOBE_RADIUS = 0.32f;
    private static final float GLOBE_HEIGHT = 0.62f;
    private static final int SPHERE_SEGMENTS = 24;
    private static final float TEX_SIZE = 64.0f;
    private static final float TEX_INSET = 0.5f;
    private static final float DISC_UV_RADIUS = 0.92f;
    private static final int FACE_NORTH = 0;
    private static final int FACE_EAST = 1;
    private static final int FACE_SOUTH = 2;
    private static final int FACE_WEST = 3;
    private static final int FACE_UP = 4;
    private static final int FACE_DOWN = 5;
    private static final int GOLD = 0xffd4b142;
    private static final int DARK_GOLD = 0xff8d772f;
    private static final int METAL = 0xffb6aa80;

    @Override
    public void render(GlobeTileEntity te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if (te == null) {
            return;
        }

        float yRot = te.getLastYRot() + (te.getYRot() - te.getLastYRot()) * partialTicks;
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);
        renderGlobe(getGlobeTexture(te), yRot);
        GlStateManager.popMatrix();
    }

    private ResourceLocation getGlobeTexture(GlobeTileEntity te) {
        String name = "earth_globe";
        if (te.getWorld() != null && te.getWorld().getBlockState(te.getPos()).getBlock().getRegistryName() != null) {
            name = te.getWorld().getBlockState(te.getPos()).getBlock().getRegistryName().getPath();
        }
        return new ResourceLocation(Reference.MOD_ID, "textures/block/globe/" + name + ".png");
    }

    private static ResourceLocation getGlobeTexture(ItemStack stack) {
        String name = "earth_globe";
        if (!stack.isEmpty() && stack.getItem().getRegistryName() != null) {
            name = stack.getItem().getRegistryName().getPath();
        }
        return new ResourceLocation(Reference.MOD_ID, "textures/block/globe/" + name + ".png");
    }

    private static void renderGlobe(ResourceLocation texture, float yRot) {
        GlStateManager.pushMatrix();
        GlStateManager.disableLighting();
        GlStateManager.disableCull();
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        renderStand();
        renderFrame();

        GlStateManager.pushMatrix();
        GlStateManager.translate(0.5f, GLOBE_HEIGHT, 0.5f);
        GlStateManager.rotate(yRot, 0.0f, 1.0f, 0.0f);
        Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
        GlStateManager.enableTexture2D();
        GlStateManager.enableAlpha();
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1f);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        renderSphere(GLOBE_RADIUS);
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();

        GlStateManager.enableCull();
        GlStateManager.enableLighting();
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.popMatrix();
    }

    private static void renderStand() {
        GlStateManager.disableTexture2D();
        renderBox(0.25f, 0.00f, 0.25f, 0.75f, 0.07f, 0.75f, GOLD);
        renderBox(0.30f, 0.07f, 0.30f, 0.70f, 0.11f, 0.70f, DARK_GOLD);
        renderBox(0.44f, 0.11f, 0.44f, 0.56f, 0.39f, 0.56f, METAL);
        renderBox(0.37f, 0.36f, 0.37f, 0.63f, 0.43f, 0.63f, GOLD);
        GlStateManager.enableTexture2D();
    }

    private static void renderFrame() {
        GlStateManager.disableTexture2D();
        GL11.glLineWidth(3.0f);
        renderCircle(0.5f, GLOBE_HEIGHT, 0.5f, 0.38f, 25.0f, GOLD);
        renderAxis(0.5f, GLOBE_HEIGHT, 0.5f, 0.43f, METAL);
        GL11.glLineWidth(1.0f);
        GlStateManager.enableTexture2D();
    }

    private static void renderCircle(float cx, float cy, float cz, float radius, float zRot, int color) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        float radians = (float) Math.toRadians(zRot);
        float cos = (float) Math.cos(radians);
        float sin = (float) Math.sin(radians);
        int a = color >> 24 & 255;
        int r = color >> 16 & 255;
        int g = color >> 8 & 255;
        int b = color & 255;

        buffer.begin(GL11.GL_LINE_LOOP, DefaultVertexFormats.POSITION_COLOR);
        for (int i = 0; i < 64; i++) {
            double angle = Math.PI * 2.0d * i / 64.0d;
            float x = (float) Math.cos(angle) * radius;
            float y = (float) Math.sin(angle) * radius;
            float rx = x * cos - y * sin;
            float ry = x * sin + y * cos;
            buffer.pos(cx + rx, cy + ry, cz).color(r, g, b, a).endVertex();
        }
        tessellator.draw();
    }

    private static void renderAxis(float cx, float cy, float cz, float halfHeight, int color) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        int a = color >> 24 & 255;
        int r = color >> 16 & 255;
        int g = color >> 8 & 255;
        int b = color & 255;
        buffer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
        buffer.pos(cx, cy - halfHeight, cz).color(r, g, b, a).endVertex();
        buffer.pos(cx, cy + halfHeight, cz).color(r, g, b, a).endVertex();
        tessellator.draw();
    }

    private static void renderBox(float minX, float minY, float minZ, float maxX, float maxY, float maxZ, int color) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        int a = color >> 24 & 255;
        int r = color >> 16 & 255;
        int g = color >> 8 & 255;
        int b = color & 255;

        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        addColoredQuad(buffer, minX, minY, minZ, maxX, minY, minZ, maxX, maxY, minZ, minX, maxY, minZ, r, g, b, a);
        addColoredQuad(buffer, maxX, minY, maxZ, minX, minY, maxZ, minX, maxY, maxZ, maxX, maxY, maxZ, r, g, b, a);
        addColoredQuad(buffer, minX, minY, maxZ, minX, minY, minZ, minX, maxY, minZ, minX, maxY, maxZ, r, g, b, a);
        addColoredQuad(buffer, maxX, minY, minZ, maxX, minY, maxZ, maxX, maxY, maxZ, maxX, maxY, minZ, r, g, b, a);
        addColoredQuad(buffer, minX, maxY, minZ, maxX, maxY, minZ, maxX, maxY, maxZ, minX, maxY, maxZ, r, g, b, a);
        addColoredQuad(buffer, minX, minY, maxZ, maxX, minY, maxZ, maxX, minY, minZ, minX, minY, minZ, r, g, b, a);
        tessellator.draw();
    }

    private static void addColoredQuad(BufferBuilder buffer,
                                       float x1, float y1, float z1,
                                       float x2, float y2, float z2,
                                       float x3, float y3, float z3,
                                       float x4, float y4, float z4,
                                       int r, int g, int b, int a) {
        buffer.pos(x1, y1, z1).color(r, g, b, a).endVertex();
        buffer.pos(x2, y2, z2).color(r, g, b, a).endVertex();
        buffer.pos(x3, y3, z3).color(r, g, b, a).endVertex();
        buffer.pos(x4, y4, z4).color(r, g, b, a).endVertex();
    }

    private static void renderSphere(float radius) {
        renderCubeMappedSphere(radius);
    }

    private static void renderCubeMappedSphere(float radius) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);

        renderCubeMappedFace(buffer, FACE_NORTH, radius);
        renderCubeMappedFace(buffer, FACE_EAST, radius);
        renderCubeMappedFace(buffer, FACE_SOUTH, radius);
        renderCubeMappedFace(buffer, FACE_WEST, radius);
        renderCubeMappedFace(buffer, FACE_UP, radius);
        renderCubeMappedFace(buffer, FACE_DOWN, radius);

        tessellator.draw();
    }

    private static void renderCubeMappedFace(BufferBuilder buffer, int face, float radius) {
        for (int row = 0; row < SPHERE_SEGMENTS; row++) {
            float t1 = -1.0f + 2.0f * row / SPHERE_SEGMENTS;
            float t2 = -1.0f + 2.0f * (row + 1) / SPHERE_SEGMENTS;

            for (int col = 0; col < SPHERE_SEGMENTS; col++) {
                float s1 = -1.0f + 2.0f * col / SPHERE_SEGMENTS;
                float s2 = -1.0f + 2.0f * (col + 1) / SPHERE_SEGMENTS;

                addCubeMappedVertex(buffer, face, s1, t1, radius);
                addCubeMappedVertex(buffer, face, s2, t1, radius);
                addCubeMappedVertex(buffer, face, s2, t2, radius);
                addCubeMappedVertex(buffer, face, s1, t2, radius);
            }
        }
    }

    private static void addCubeMappedVertex(BufferBuilder buffer, int face, float s, float t, float radius) {
        float x;
        float y;
        float z;
        switch (face) {
            case FACE_EAST:
                x = 1.0f;
                y = t;
                z = s;
                break;
            case FACE_SOUTH:
                x = -s;
                y = t;
                z = 1.0f;
                break;
            case FACE_WEST:
                x = -1.0f;
                y = t;
                z = -s;
                break;
            case FACE_UP:
                x = s;
                y = 1.0f;
                z = -t;
                break;
            case FACE_DOWN:
                x = s;
                y = -1.0f;
                z = t;
                break;
            case FACE_NORTH:
            default:
                x = s;
                y = t;
                z = -1.0f;
                break;
        }

        float length = (float) Math.sqrt(x * x + y * y + z * z);
        float[] uv = getCubeFaceUv(face, s, t);
        buffer.pos(x / length * radius, y / length * radius, z / length * radius)
            .tex(uv[0], uv[1])
            .endVertex();
    }

    private static float[] getCubeFaceUv(int face, float s, float t) {
        float minU;
        float maxU;
        float minV;
        float maxV;
        boolean flipU = false;
        boolean flipV = false;

        switch (face) {
            case FACE_EAST:
                minU = 0.0f;
                maxU = 15.0f;
                minV = 15.0f;
                maxV = 30.0f;
                break;
            case FACE_SOUTH:
                minU = 45.0f;
                maxU = 60.0f;
                minV = 15.0f;
                maxV = 30.0f;
                break;
            case FACE_WEST:
                minU = 30.0f;
                maxU = 45.0f;
                minV = 15.0f;
                maxV = 30.0f;
                break;
            case FACE_UP:
                minU = 15.0f;
                maxU = 30.0f;
                minV = 0.0f;
                maxV = 15.0f;
                flipU = true;
                flipV = true;
                break;
            case FACE_DOWN:
                minU = 0.0f;
                maxU = 15.0f;
                minV = 30.0f;
                maxV = 45.0f;
                break;
            case FACE_NORTH:
            default:
                minU = 15.0f;
                maxU = 30.0f;
                minV = 15.0f;
                maxV = 30.0f;
                break;
        }

        float localU = (s + 1.0f) * 0.5f;
        float localV = 1.0f - (t + 1.0f) * 0.5f;
        if (flipU) {
            localU = 1.0f - localU;
        }
        if (flipV) {
            localV = 1.0f - localV;
        }

        float[] discUv = squareToDisc(localU * 2.0f - 1.0f, localV * 2.0f - 1.0f);
        localU = 0.5f + discUv[0] * 0.5f * DISC_UV_RADIUS;
        localV = 0.5f + discUv[1] * 0.5f * DISC_UV_RADIUS;

        minU += TEX_INSET;
        maxU -= TEX_INSET;
        minV += TEX_INSET;
        maxV -= TEX_INSET;
        return new float[]{
            (minU + (maxU - minU) * localU) / TEX_SIZE,
            (minV + (maxV - minV) * localV) / TEX_SIZE
        };
    }

    private static float[] squareToDisc(float x, float y) {
        if (x == 0.0f && y == 0.0f) {
            return new float[]{0.0f, 0.0f};
        }

        float r;
        float theta;
        float absX = Math.abs(x);
        float absY = Math.abs(y);
        if (absX > absY) {
            r = x;
            theta = (float) (Math.PI / 4.0d) * (y / x);
        } else {
            r = y;
            theta = (float) (Math.PI / 2.0d) - (float) (Math.PI / 4.0d) * (x / y);
        }

        return new float[]{r * (float) Math.cos(theta), r * (float) Math.sin(theta)};
    }

    @SideOnly(Side.CLIENT)
    public static class ItemRenderer extends TileEntityItemStackRenderer {

        private ItemRenderer() {
        }

        @Override
        public void renderByItem(ItemStack stack) {
            GlStateManager.pushMatrix();
            float yRot = (System.currentTimeMillis() / 20.0f) % 360.0f;
            renderGlobe(getGlobeTexture(stack), yRot);
            GlStateManager.popMatrix();
        }
    }
}
