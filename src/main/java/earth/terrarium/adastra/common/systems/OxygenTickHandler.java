package earth.terrarium.adastra.common.systems;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Event handler for the oxygen system.
 * Subscribes to LivingUpdateEvent and calls oxygen checks for players.
 *
 * This handler should be registered in the mod's event bus during initialization.
 * Usage:
 * <pre>
 * MinecraftForge.EVENT_BUS.register(new OxygenTickHandler());
 * </pre>
 */
public class OxygenTickHandler {

    /**
     * Interval for oxygen checks (in ticks).
     * Default: 20 ticks (1 second) for performance optimization.
     * The actual oxygen consumption happens at a different interval (12 ticks).
     */
    private static final int CHECK_INTERVAL = 20;

    /**
     * Handle living entity updates and check oxygen for players.
     * Called every tick for every living entity.
     *
     * @param event The living update event
     */
    @SubscribeEvent
    public void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
        // Only process players
        if (!(event.getEntityLiving() instanceof EntityPlayer)) {
            return;
        }

        EntityPlayer player = (EntityPlayer) event.getEntityLiving();

        // Skip client-side processing (oxygen is server-authoritative)
        if (player.world.isRemote) {
            return;
        }

        // Check oxygen every CHECK_INTERVAL ticks for performance
        // Note: The actual consumption interval is handled within OxygenSystem.checkOxygen()
        if (player.ticksExisted % CHECK_INTERVAL == 0) {
            OxygenSystem.checkOxygen(player, player.world);
        }
    }
}
