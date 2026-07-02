package earth.terrarium.adastra.common.handler;

import earth.terrarium.adastra.common.performance.PerformanceTracker;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

/**
 * Handles server tick events for performance monitoring.
 */
public class PerformanceTickHandler {

    private int tickCounter = 0;

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            PerformanceTracker.startTick();
        } else {
            PerformanceTracker.endTick();

            // Reset per-system counters every second for accurate per-second measurements
            tickCounter++;
            if (tickCounter >= 20) {
                PerformanceTracker.resetSystemTimes();
                tickCounter = 0;
            }
        }
    }
}
