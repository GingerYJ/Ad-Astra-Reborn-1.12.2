package earth.terrarium.adastra.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IRenderHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class PlanetCloudRenderer extends IRenderHandler {

    private static final float CLOUD_SIZE = 512.0F;
    private static final float DEFAULT_TEXTURE_SCALE = 96.0F;

    private final ResourceLocation texture;
    private final float height;
    private final float textureScale;
    private final float red;
    private final float green;
    private final float blue;
    private final float alpha;

    public PlanetCloudRenderer(ResourceLocation texture, float height, float red, float green, float blue, float alpha) {
        this(texture, height, DEFAULT_TEXTURE_SCALE, red, green, blue, alpha);
    }

    public PlanetCloudRenderer(ResourceLocation texture, float height, float textureScale, float red, float green, float blue, float alpha) {
        this.texture = texture;
        this.height = height;
        this.textureScale = textureScale;
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.alpha = alpha;
    }

    @Override
    public void render(float partialTicks, WorldClient world, Minecraft mc) {
        if (mc.gameSettings.shouldRenderClouds() == 0 || mc.getRenderViewEntity() == null) {
            return;
        }

        Entity view = mc.getRenderViewEntity();
        double cameraX = view.lastTickPosX + (view.posX - view.lastTickPosX) * partialTicks;
        double cameraY = view.lastTickPosY + (view.posY - view.lastTickPosY) * partialTicks;
        double cameraZ = view.lastTickPosZ + (view.posZ - view.lastTickPosZ) * partialTicks;
        double scroll = ((world.getTotalWorldTime() + partialTicks) * 0.02D);

        float minX = (float) (-CLOUD_SIZE);
        float maxX = (float) CLOUD_SIZE;
        float minZ = (float) (-CLOUD_SIZE);
        float maxZ = (float) CLOUD_SIZE;
        float y = (float) (height - cameraY);

        double texMinX = (cameraX - CLOUD_SIZE + scroll) / textureScale;
        double texMaxX = (cameraX + CLOUD_SIZE + scroll) / textureScale;
        double texMinZ = (cameraZ - CLOUD_SIZE) / textureScale;
        double texMaxZ = (cameraZ + CLOUD_SIZE) / textureScale;

        GlStateManager.pushMatrix();
        GlStateManager.disableCull();
        GlStateManager.enableBlend();
        GlStateManager.enableTexture2D();
        GlStateManager.depthMask(false);
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        mc.getTextureManager().bindTexture(texture);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
        buffer.pos(minX, y, minZ).tex(texMinX, texMinZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(minX, y, maxZ).tex(texMinX, texMaxZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(maxX, y, maxZ).tex(texMaxX, texMaxZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(maxX, y, minZ).tex(texMaxX, texMinZ).color(red, green, blue, alpha).endVertex();
        tessellator.draw();

        GlStateManager.depthMask(true);
        GlStateManager.disableBlend();
        GlStateManager.enableCull();
        GlStateManager.popMatrix();
    }
}
