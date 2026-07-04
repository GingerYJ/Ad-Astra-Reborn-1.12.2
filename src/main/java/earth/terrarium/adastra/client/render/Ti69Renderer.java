package earth.terrarium.adastra.client.render;

import earth.terrarium.adastra.Reference;
import earth.terrarium.adastra.client.render.apps.SensorApp;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;

public final class Ti69Renderer {

    public static final ResourceLocation TEXTURE = new ResourceLocation(Reference.MOD_ID, "textures/ti-69/ti-69.png");
    public static final ResourceLocation SCREEN = new ResourceLocation(Reference.MOD_ID, "textures/ti-69/screen.png");
    public static final ResourceLocation OVERLAY = new ResourceLocation(Reference.MOD_ID, "textures/ti-69/overlay.png");
    public static final ResourceLocation ICONS = new ResourceLocation(Reference.MOD_ID, "textures/ti-69/icons.png");
    private static final float TEXEL = 1.0F / 128.0F;

    public static Ti69App app = new SensorApp();

    private Ti69Renderer() {
    }

    public static void renderTi69OnScreen() {
        Minecraft minecraft = Minecraft.getMinecraft();
        FontRenderer font = minecraft.fontRenderer;
        WorldClient world = minecraft.world;
        if (world == null) {
            return;
        }

        GlStateManager.pushMatrix();
        GlStateManager.scale(TEXEL, TEXEL, TEXEL);
        drawTexture(TEXTURE, 1.0F, 1.0F, 1.0F, 1.0F, 0.0F, -7.0F, -7.0F, 135.0F, 135.0F);
        GlStateManager.popMatrix();

        Ti69App currentApp = app;
        int backgroundColor = currentApp != null ? currentApp.color() : 0xff000000;
        float red = ((backgroundColor >> 16) & 0xFF) / 255.0F;
        float green = ((backgroundColor >> 8) & 0xFF) / 255.0F;
        float blue = (backgroundColor & 0xFF) / 255.0F;

        GlStateManager.pushMatrix();
        GlStateManager.scale(TEXEL, TEXEL, TEXEL);
        GlStateManager.scale(0.95F, 0.392F, 1.0F);
        GlStateManager.translate(21.0F, 48.0F, 0.01F);
        drawTexture(SCREEN, red, green, blue, 1.0F, 0.0F, -7.0F, -7.0F, 100.0F, 100.0F);
        GlStateManager.popMatrix();

        if (currentApp != null) {
            GlStateManager.pushMatrix();
            GlStateManager.disableDepth();
            GlStateManager.depthMask(false);
            GlStateManager.scale(TEXEL, TEXEL, TEXEL);
            GlStateManager.scale(1.2F, 0.7F, 1.0F);
            GlStateManager.translate(30.0F, 25.0F, 0.03F);
            currentApp.render(font, world);
            GlStateManager.depthMask(true);
            GlStateManager.enableDepth();
            GlStateManager.popMatrix();
        }

        GlStateManager.pushMatrix();
        GlStateManager.scale(TEXEL, TEXEL, TEXEL);
        GlStateManager.scale(0.95F, 0.392F, 1.0F);
        GlStateManager.translate(21.0F, 48.0F, -0.01F);
        drawTexture(OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F, 0.0F, -7.0F, -7.0F, 100.0F, 100.0F);
        GlStateManager.popMatrix();
    }

    private static void drawTexture(ResourceLocation texture, float red, float green, float blue, float alpha, float z) {
        drawTexture(texture, red, green, blue, alpha, z, 0.0F, 0.0F, 1.0F, 1.0F);
    }

    private static void drawTexture(ResourceLocation texture, float red, float green, float blue, float alpha, float z, float minX, float minY, float maxX, float maxY) {
        Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
        GlStateManager.color(red, green, blue, alpha);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.disableCull();

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(7, DefaultVertexFormats.POSITION_TEX);
        buffer.pos(minX, maxY, z).tex(0.0D, 1.0D).endVertex();
        buffer.pos(maxX, maxY, z).tex(1.0D, 1.0D).endVertex();
        buffer.pos(maxX, minY, z).tex(1.0D, 0.0D).endVertex();
        buffer.pos(minX, minY, z).tex(0.0D, 0.0D).endVertex();
        tessellator.draw();

        GlStateManager.enableCull();
        GlStateManager.disableBlend();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }
}
