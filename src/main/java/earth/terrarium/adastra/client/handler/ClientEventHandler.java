package earth.terrarium.adastra.client.handler;

import earth.terrarium.adastra.client.hud.AdAstraHudOverlay;
import earth.terrarium.adastra.client.render.MachineAreaRenderState;
import earth.terrarium.adastra.common.entities.vehicles.RocketEntity;
import earth.terrarium.adastra.common.network.NetworkHandler;
import earth.terrarium.adastra.common.network.packet.PacketSyncKeybinds;
import earth.terrarium.adastra.common.registry.ModItems;
import earth.terrarium.adastra.common.world.AdAstraWorldProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

public class ClientEventHandler {

    private static final KeyBinding TOGGLE_SUIT_FLIGHT = new KeyBinding("key.ad_astra.toggle_suit_flight", Keyboard.KEY_V, "key.categories.adastra");

    private boolean suitFlightEnabled;
    private boolean lastJumping;
    private boolean lastSprinting;
    private boolean lastSuitFlightEnabled;
    private int syncCooldown;

    public ClientEventHandler() {
        ClientRegistry.registerKeyBinding(TOGGLE_SUIT_FLIGHT);
    }

    @SubscribeEvent
    public void onRenderGameOverlay(RenderGameOverlayEvent.Post event) {
        AdAstraHudOverlay.render(event);
    }

    @SubscribeEvent
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        MachineAreaRenderState.render(event);
    }

    /**
     * When loading a world on the client, install custom sky renderers for Ad Astra dimensions.
     * For oxygen-less dimensions: black space sky with stars and celestial bodies.
     * For atmospheric dimensions: use enhanced sky renderer with appropriate tinting.
     */
    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load event) {
        if (!event.getWorld().isRemote) {
            return;
        }
        MachineAreaRenderState.clear();
        net.minecraft.world.WorldProvider provider = event.getWorld().provider;
        if (!(provider instanceof AdAstraWorldProvider)) {
            return;
        }
        // Install planet-specific sky renderers
        if (provider.getSkyRenderer() == null) {
            earth.terrarium.adastra.client.render.PlanetSkyRenderers.registerSkyRenderers(event.getWorld());
        }
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        Minecraft minecraft = Minecraft.getMinecraft();
        if (minecraft.player == null) {
            return;
        }

        if (isWearingJetSuit()) {
            while (TOGGLE_SUIT_FLIGHT.isPressed()) {
                suitFlightEnabled = !suitFlightEnabled;
                minecraft.player.sendStatusMessage(new TextComponentTranslation(suitFlightEnabled
                    ? "message.ad_astra.suit_flight_enabled"
                    : "message.ad_astra.suit_flight_disabled"), true);
            }
        } else {
            suitFlightEnabled = false;
        }
        syncKeybinds(minecraft);
    }

    private void syncKeybinds(Minecraft minecraft) {
        boolean jumping = minecraft.gameSettings.keyBindJump.isKeyDown();
        boolean sprinting = minecraft.gameSettings.keyBindSprint.isKeyDown();
        boolean ridingRocket = minecraft.player.getRidingEntity() instanceof RocketEntity;
        if (ridingRocket || jumping != lastJumping || sprinting != lastSprinting || suitFlightEnabled != lastSuitFlightEnabled || syncCooldown-- <= 0) {
            sendKeybinds(jumping, sprinting, suitFlightEnabled);
            syncCooldown = 10;
        }
    }

    private void sendKeybinds(boolean jumping, boolean sprinting, boolean enabled) {
        lastJumping = jumping;
        lastSprinting = sprinting;
        lastSuitFlightEnabled = enabled;
        NetworkHandler.CHANNEL.sendToServer(new PacketSyncKeybinds(jumping, sprinting, enabled));
    }

    private boolean isWearingJetSuit() {
        Minecraft minecraft = Minecraft.getMinecraft();
        ItemStack chest = minecraft.player.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
        return !chest.isEmpty() && chest.getItem() == ModItems.JET_SUIT;
    }
}
