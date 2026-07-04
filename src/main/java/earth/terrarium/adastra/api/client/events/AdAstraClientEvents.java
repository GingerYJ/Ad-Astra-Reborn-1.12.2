package earth.terrarium.adastra.api.client.events;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Client-side events for the Ad Astra API on Minecraft 1.12.2.
 * Mirrors the 1.20.x {@code AdAstraClientEvents} class.
 */
public final class AdAstraClientEvents {

    private static final List<RenderSolarSystemEvent> RENDER_SOLAR_SYSTEM_LISTENERS = new ArrayList<>();
    private static final List<LegacyRenderSolarSystemEvent> LEGACY_RENDER_SOLAR_SYSTEM_LISTENERS = new ArrayList<>();

    private AdAstraClientEvents() {
    }

    /**
     * Lightweight Minecraft 1.12.2 replacement for the 1.20.x GuiGraphics render context.
     */
    public static final class RenderContext {
        private final Minecraft minecraft;
        @Nullable
        private final GuiScreen screen;
        private final float partialTicks;

        public RenderContext(Minecraft minecraft, @Nullable GuiScreen screen, float partialTicks) {
            this.minecraft = minecraft;
            this.screen = screen;
            this.partialTicks = partialTicks;
        }

        public Minecraft getMinecraft() {
            return minecraft;
        }

        @Nullable
        public GuiScreen getScreen() {
            return screen;
        }

        public float getPartialTicks() {
            return partialTicks;
        }
    }

    @FunctionalInterface
    public interface RenderSolarSystemEvent {

        /**
         * Called when the solar system background is rendered in planet selection screens.
         *
         * @param context Minecraft 1.12.2 render context
         * @param solarSystem The solar system resource location, or null
         * @param width Screen width
         * @param height Screen height
         */
        void render(RenderContext context, @Nullable ResourceLocation solarSystem, int width, int height);

        static void register(RenderSolarSystemEvent listener) {
            RENDER_SOLAR_SYSTEM_LISTENERS.add(listener);
        }

        static void fire(RenderContext context, @Nullable ResourceLocation solarSystem, int width, int height) {
            for (RenderSolarSystemEvent listener : RENDER_SOLAR_SYSTEM_LISTENERS) {
                listener.render(context, solarSystem, width, height);
            }
            LegacyRenderSolarSystemEvent.fire(solarSystem, width, height);
        }
    }

    @FunctionalInterface
    public interface LegacyRenderSolarSystemEvent {

        /**
         * Legacy 1.12.2-port signature kept for already-ported listeners.
         */
        void render(@Nullable ResourceLocation solarSystem, int width, int height);

        static void register(LegacyRenderSolarSystemEvent listener) {
            LEGACY_RENDER_SOLAR_SYSTEM_LISTENERS.add(listener);
        }

        static void fire(@Nullable ResourceLocation solarSystem, int width, int height) {
            for (LegacyRenderSolarSystemEvent listener : LEGACY_RENDER_SOLAR_SYSTEM_LISTENERS) {
                listener.render(solarSystem, width, height);
            }
        }
    }
}
