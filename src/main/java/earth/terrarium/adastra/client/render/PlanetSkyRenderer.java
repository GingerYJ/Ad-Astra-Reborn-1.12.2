package earth.terrarium.adastra.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IRenderHandler;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Enhanced planet sky renderer for space dimensions.
 * Renders black sky background with stars, celestial bodies (planets, moons, suns),
 * and customizable atmosphere effects.
 *
 * Ported from 1.20.x ModSkyRenderer with 1.12.2 adaptations:
 * - Uses GlStateManager instead of RenderSystem
 * - Tessellator/BufferBuilder patterns instead of VertexBuffer
 * - IRenderHandler interface for dimension sky rendering
 */
public class PlanetSkyRenderer extends IRenderHandler {

    private static final int STAR_COUNT = 1500;
    private static final long STAR_SEED = 10842L;

    /** Pre-generated star geometry for performance. */
    private float[] starVertices;

    /** List of celestial bodies to render (planets, moons, sun). */
    private final List<CelestialBody> celestialBodies = new ArrayList<>();

    /** Whether to render sunrise/sunset effects. */
    private final boolean renderSunrise;

    /** Sky color tint (for atmospheric effects). */
    private final float skyRed, skyGreen, skyBlue;

    public PlanetSkyRenderer() {
        this(false, 0.0f, 0.0f, 0.0f);
    }

    public PlanetSkyRenderer(boolean renderSunrise, float skyRed, float skyGreen, float skyBlue) {
        this.renderSunrise = renderSunrise;
        this.skyRed = skyRed;
        this.skyGreen = skyGreen;
        this.skyBlue = skyBlue;
    }

    /**
     * Add a celestial body (planet, moon, sun) to render in the sky.
     */
    public PlanetSkyRenderer addCelestialBody(ResourceLocation texture, float scale,
                                               float localRotX, float localRotY, float localRotZ,
                                               float globalRotX, float globalRotY, float globalRotZ,
                                               boolean rotateWithTime, boolean blend) {
        celestialBodies.add(new CelestialBody(texture, scale,
            localRotX, localRotY, localRotZ,
            globalRotX, globalRotY, globalRotZ,
            rotateWithTime, blend));
        return this;
    }

    @Override
    public void render(float partialTicks, WorldClient world, Minecraft mc) {
        GlStateManager.disableTexture2D();
        GlStateManager.disableFog();
        GlStateManager.disableAlpha();
        GlStateManager.disableBlend();
        GlStateManager.depthMask(false);

        // Black space background
        GlStateManager.clearColor(skyRed, skyGreen, skyBlue, 1.0F);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);

        renderStars();
        renderCelestialBodies(world, partialTicks);

        GlStateManager.depthMask(true);
        GlStateManager.enableTexture2D();
        GlStateManager.enableAlpha();
        GlStateManager.enableFog();
    }

    private void renderStars() {
        if (starVertices == null) {
            starVertices = buildStars();
        }

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
        for (int i = 0; i < starVertices.length; i += 3) {
            buffer.pos(starVertices[i], starVertices[i + 1], starVertices[i + 2]).endVertex();
        }
        tessellator.draw();
    }

    private void renderCelestialBodies(WorldClient world, float partialTicks) {
        if (celestialBodies.isEmpty()) {
            return;
        }

        GlStateManager.enableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

        float timeOfDay = world.getCelestialAngle(partialTicks);

        for (CelestialBody body : celestialBodies) {
            renderCelestialBody(body, timeOfDay);
        }

        GlStateManager.disableBlend();
        GlStateManager.disableTexture2D();
    }

    private void renderCelestialBody(CelestialBody body, float timeOfDay) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();

        GlStateManager.pushMatrix();

        // Apply global rotation
        float globalRotZ = body.globalRotZ;
        if (body.rotateWithTime) {
            globalRotZ += timeOfDay * 360.0f;
        }

        GlStateManager.rotate(body.globalRotX, 1, 0, 0);
        GlStateManager.rotate(body.globalRotY, 0, 1, 0);
        GlStateManager.rotate(globalRotZ, 0, 0, 1);

        // Move to sky sphere distance
        GlStateManager.translate(0, 100, 0);

        // Apply local rotation
        GlStateManager.rotate(body.localRotX, 1, 0, 0);
        GlStateManager.rotate(body.localRotY, 0, 1, 0);
        GlStateManager.rotate(body.localRotZ, 0, 0, 1);

        GlStateManager.translate(0, -100, 0);

        // Bind texture
        Minecraft.getMinecraft().getTextureManager().bindTexture(body.texture);

        if (body.blend) {
            GlStateManager.enableBlend();
        } else {
            GlStateManager.disableBlend();
        }

        // Render quad
        float scale = body.scale;
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        buffer.pos(-scale, 100, -scale).tex(1, 0).endVertex();
        buffer.pos(scale, 100, -scale).tex(0, 0).endVertex();
        buffer.pos(scale, 100, scale).tex(0, 1).endVertex();
        buffer.pos(-scale, 100, scale).tex(1, 1).endVertex();
        tessellator.draw();

        GlStateManager.popMatrix();
    }

    /**
     * Generate star vertices based on the 1.20.x algorithm.
     * Each star is a quad billboard positioned on a sphere.
     */
    private float[] buildStars() {
        Random random = new Random(STAR_SEED);
        float[] vertices = new float[STAR_COUNT * 4 * 3];
        int index = 0;

        for (int i = 0; i < STAR_COUNT; i++) {
            double dx = random.nextFloat() * 2.0F - 1.0F;
            double dy = random.nextFloat() * 2.0F - 1.0F;
            double dz = random.nextFloat() * 2.0F - 1.0F;
            double size = 0.15F + random.nextFloat() * 0.1F;
            double distSq = dx * dx + dy * dy + dz * dz;

            // Only keep points within sphere shell
            if (distSq >= 1.0E-4D && distSq <= 1.0D) {
                double scale = 100.0D / Math.sqrt(distSq);
                dx *= scale;
                dy *= scale;
                dz *= scale;

                double cx = dx;
                double cy = dy;
                double cz = dz;

                // Build orthogonal basis for star quad
                double angle = Math.atan2(dx, dz);
                double sinA = Math.sin(angle);
                double cosA = Math.cos(angle);
                double tilt = Math.atan2(Math.sqrt(dx * dx + dz * dz), dy);
                double sinT = Math.sin(tilt);
                double cosT = Math.cos(tilt);
                double spin = random.nextDouble() * Math.PI * 2.0D;
                double sinS = Math.sin(spin);
                double cosS = Math.cos(spin);

                for (int corner = 0; corner < 4; corner++) {
                    double ox = ((corner & 2) - 1) * size;
                    double oy = ((corner + 1 & 2) - 1) * size;
                    double rx = ox * cosS - oy * sinS;
                    double ry = oy * cosS + ox * sinS;
                    double px = rx * cosT;
                    double pz = -rx * sinT;
                    double vx = px * sinA - ry * cosA;
                    double vz = ry * sinA + px * cosA;

                    vertices[index++] = (float) (cx + vx);
                    vertices[index++] = (float) (cy + pz);
                    vertices[index++] = (float) (cz + vz);
                }
            } else {
                // Degenerate quad for rejected points
                for (int corner = 0; corner < 4; corner++) {
                    vertices[index++] = 0.0F;
                    vertices[index++] = 0.0F;
                    vertices[index++] = 0.0F;
                }
            }
        }

        return vertices;
    }

    /**
     * Represents a celestial body (planet, moon, sun) in the sky.
     */
    private static class CelestialBody {
        final ResourceLocation texture;
        final float scale;
        final float localRotX, localRotY, localRotZ;
        final float globalRotX, globalRotY, globalRotZ;
        final boolean rotateWithTime;
        final boolean blend;

        CelestialBody(ResourceLocation texture, float scale,
                     float localRotX, float localRotY, float localRotZ,
                     float globalRotX, float globalRotY, float globalRotZ,
                     boolean rotateWithTime, boolean blend) {
            this.texture = texture;
            this.scale = scale;
            this.localRotX = localRotX;
            this.localRotY = localRotY;
            this.localRotZ = localRotZ;
            this.globalRotX = globalRotX;
            this.globalRotY = globalRotY;
            this.globalRotZ = globalRotZ;
            this.rotateWithTime = rotateWithTime;
            this.blend = blend;
        }
    }
}
