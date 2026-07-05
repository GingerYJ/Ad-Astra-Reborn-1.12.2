package earth.terrarium.adastra.client.gui.widget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class GravitySliderWidget extends Gui {

    public static final int TRACK_WIDTH = 108;
    public static final int TRACK_HEIGHT = 11;
    private static final int HANDLE_WIDTH = 5;
    private static final int HANDLE_HEIGHT = 21;
    private static final int HANDLE_TRAVEL = TRACK_WIDTH - HANDLE_WIDTH;
    private static final int MAX_STEPS = 200;

    private final ResourceLocation texture;

    public GravitySliderWidget(ResourceLocation texture) {
        this.texture = texture;
    }

    public void render(Minecraft minecraft, int x, int y, float targetGravity) {
        int handleX = x + Math.round(clamp(targetGravity, 0.0f, 2.0f) / 2.0f * HANDLE_TRAVEL);
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        minecraft.getTextureManager().bindTexture(texture);
        drawModalRectWithCustomSizedTexture(handleX, y - 5, 0.0f, 0.0f, HANDLE_WIDTH, HANDLE_HEIGHT, HANDLE_WIDTH, HANDLE_HEIGHT);
    }

    public boolean isMouseOver(int mouseX, int mouseY, int x, int y) {
        return mouseX >= x && mouseX < x + TRACK_WIDTH && mouseY >= y && mouseY < y + TRACK_HEIGHT;
    }

    public int getStepsFromMouse(int mouseX, int x) {
        int relative = mouseX - x;
        if (relative < -6 || relative > TRACK_WIDTH + 6) {
            return -1;
        }
        float fraction = clamp(relative / (float) TRACK_WIDTH, 0.0f, 1.0f);
        return Math.round(fraction * MAX_STEPS);
    }

    private float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }
}
