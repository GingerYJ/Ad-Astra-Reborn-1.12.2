package earth.terrarium.adastra.common.capability;

import earth.terrarium.adastra.AdAstraReborn;
import earth.terrarium.adastra.common.network.NetworkHandler;
import earth.terrarium.adastra.common.network.packet.PacketSyncPlayerCapability;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

public class AdAstraCapabilityHandler {

    @SubscribeEvent
    public void onPlayerClone(net.minecraftforge.event.entity.player.PlayerEvent.Clone event) {
        EntityPlayer player = event.getEntityPlayer();
        EntityPlayer oldPlayer = event.getOriginal();

        if (player == null || oldPlayer == null) {
            return;
        }

        IAdAstraPlayer newCap = AdAstraCapabilities.getPlayer(player);
        IAdAstraPlayer oldCap = AdAstraCapabilities.getPlayer(oldPlayer);

        if (newCap != null && oldCap != null) {
            if (newCap instanceof AdAstraPlayer && oldCap instanceof AdAstraPlayer) {
                ((AdAstraPlayer) newCap).copyFrom((AdAstraPlayer) oldCap);
            }
        }
    }

    @SubscribeEvent
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.player instanceof EntityPlayerMP) {
            syncToClient((EntityPlayerMP) event.player);
        }
    }

    @SubscribeEvent
    public void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.player instanceof EntityPlayerMP) {
            syncToClient((EntityPlayerMP) event.player);
        }
    }

    @SubscribeEvent
    public void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.player instanceof EntityPlayerMP) {
            syncToClient((EntityPlayerMP) event.player);
        }
    }

    private void syncToClient(EntityPlayerMP player) {
        IAdAstraPlayer cap = AdAstraCapabilities.getPlayer(player);
        if (cap instanceof AdAstraPlayer) {
            try {
                NetworkHandler.CHANNEL.sendTo(new PacketSyncPlayerCapability((AdAstraPlayer) cap), player);
            } catch (Exception e) {
                AdAstraReborn.LOGGER.error("Failed to sync player capability to client for player: " + player.getName(), e);
            }
        }
    }
}
