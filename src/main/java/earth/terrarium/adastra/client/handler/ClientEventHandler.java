package earth.terrarium.adastra.client.handler;

import earth.terrarium.adastra.client.hud.AdAstraHudOverlay;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ClientEventHandler {

    @SubscribeEvent
    public void onRenderGameOverlay(RenderGameOverlayEvent.Post event) {
        AdAstraHudOverlay.render(event);
    }
}
