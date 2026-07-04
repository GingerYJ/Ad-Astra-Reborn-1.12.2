package earth.terrarium.adastra.client.render;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;

public interface Ti69App {

    void render(FontRenderer font, WorldClient world);

    int color();

    default void renderIcon(ResourceLocation texture, int x, int y, int u, int v, int width, int height, int texWidth, int texHeight) {
        float minU = (float) u / (float) texWidth;
        float maxU = (float) (u + width) / (float) texWidth;
        float minV = (float) v / (float) texHeight;
        float maxV = (float) (v + height) / (float) texHeight;

        net.minecraft.client.Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(7, DefaultVertexFormats.POSITION_TEX);
        buffer.pos(x, y + height, 0).tex(minU, maxV).endVertex();
        buffer.pos(x + width, y + height, 0).tex(maxU, maxV).endVertex();
        buffer.pos(x + width, y, 0).tex(maxU, minV).endVertex();
        buffer.pos(x, y, 0).tex(minU, minV).endVertex();
        tessellator.draw();
    }

    default void renderTime(FontRenderer font, WorldClient world) {
        renderTime(font, world, 0);
    }

    default void renderTime(FontRenderer font, WorldClient world, int x) {
        long dayTime = (world.getWorldTime() + 6000L) % 12000L;
        boolean isPm = (world.getWorldTime() + 6000L) % 24000L >= 12000;
        int hours = (int) (dayTime / 1000);
        if (hours == 0) hours = 12;
        int minutes = (int) ((dayTime % 1000) / (1000.0 / 60.0));
        String timeText = hours + ":" + (minutes < 10 ? "0" + minutes : minutes) + (isPm ? " PM" : " AM");
        font.drawString(timeText, x, 5, 0xFFFFFF);
    }
}
