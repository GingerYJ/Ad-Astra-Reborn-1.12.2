package earth.terrarium.adastra.client.handler;

import earth.terrarium.adastra.client.hud.AdAstraHudOverlay;
import earth.terrarium.adastra.client.particle.ParticleHelper;
import earth.terrarium.adastra.client.radio.audio.RadioHandler;
import earth.terrarium.adastra.client.render.MachineAreaRenderState;
import earth.terrarium.adastra.client.systems.ClientData;
import earth.terrarium.adastra.common.config.AdAstraConfig;
import earth.terrarium.adastra.common.entities.vehicles.RocketEntity;
import earth.terrarium.adastra.common.items.AdAstraArmorItem;
import earth.terrarium.adastra.common.network.NetworkHandler;
import earth.terrarium.adastra.common.network.packet.PacketSyncKeybinds;
import earth.terrarium.adastra.common.registry.ModDimensions;
import earth.terrarium.adastra.common.registry.ModItems;
import earth.terrarium.adastra.common.util.radio.RadioHolder;
import earth.terrarium.adastra.common.world.AdAstraWorldProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

import java.util.Random;

public class ClientEventHandler {

    private static final KeyBinding TOGGLE_SUIT_FLIGHT = new KeyBinding("key.ad_astra.toggle_suit_flight", Keyboard.KEY_V, "key.categories.adastra");
    private static final int ACID_RAIN_RADIUS = 10;
    private static final int JET_SUIT_UPWARD_ENERGY_PER_TICK = 50;
    private static final int JET_SUIT_FORWARD_ENERGY_PER_TICK = 100;

    private boolean lastJumping;
    private boolean lastSprinting;
    private boolean lastSuitFlightEnabled;
    private boolean wasRidingRadioHolder;
    private int acidRainSoundCooldown;
    private int syncCooldown;

    public ClientEventHandler() {
        ClientRegistry.registerKeyBinding(TOGGLE_SUIT_FLIGHT);
    }

    @SubscribeEvent
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        MachineAreaRenderState.render(event);
    }

    @SubscribeEvent
    public void onRenderGameOverlay(RenderGameOverlayEvent.Post event) {
        AdAstraHudOverlay.render(event);
    }

    @SubscribeEvent
    public void onFogColors(EntityViewRenderEvent.FogColors event) {
        Minecraft minecraft = Minecraft.getMinecraft();
        if (!hasVenusAcidRain(minecraft)) {
            return;
        }

        float intensity = 0.45F;
        event.setRed(blend(event.getRed(), 0.72F, intensity));
        event.setGreen(blend(event.getGreen(), 0.66F, intensity));
        event.setBlue(blend(event.getBlue(), 0.34F, intensity));
    }

    @SubscribeEvent
    public void onFogDensity(EntityViewRenderEvent.FogDensity event) {
        Minecraft minecraft = Minecraft.getMinecraft();
        if (!hasVenusAcidRain(minecraft) || event.getState().getMaterial().isLiquid()) {
            return;
        }

        event.setDensity(0.035F);
        event.setCanceled(true);
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
        ClientData.clear();
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
    public void onWorldUnload(WorldEvent.Unload event) {
        if (event.getWorld().isRemote) {
            RadioHandler.stop();
            ClientData.clear();
            wasRidingRadioHolder = false;
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

        RadioHandler.tick();
        tickRadioHolderDismount(minecraft);
        tickAcidRainParticles(minecraft);
        tickAcidRainSound(minecraft);
        tickJetSuitParticles(minecraft);

        if (isWearingJetSuit()) {
            while (TOGGLE_SUIT_FLIGHT.isPressed()) {
                AdAstraConfig.setJetSuitEnabled(!AdAstraConfig.jetSuitEnabled);
                minecraft.player.sendStatusMessage(new TextComponentTranslation(AdAstraConfig.jetSuitEnabled
                    ? "message.ad_astra.suit_flight_enabled"
                    : "message.ad_astra.suit_flight_disabled"), true);
            }
        }
        syncKeybinds(minecraft);
    }

    private void syncKeybinds(Minecraft minecraft) {
        boolean jumping = minecraft.gameSettings.keyBindJump.isKeyDown();
        boolean sprinting = minecraft.gameSettings.keyBindSprint.isKeyDown();
        boolean suitFlightEnabled = isWearingJetSuit() && AdAstraConfig.jetSuitEnabled;
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

    private void tickRadioHolderDismount(Minecraft minecraft) {
        boolean ridingRadioHolder = minecraft.player.getRidingEntity() instanceof RadioHolder;
        if (wasRidingRadioHolder && !ridingRadioHolder) {
            RadioHandler.stop();
        }
        wasRidingRadioHolder = ridingRadioHolder;
    }

    private void tickAcidRainParticles(Minecraft minecraft) {
        World world = minecraft.world;
        if (!hasVenusAcidRain(minecraft)
            || minecraft.gameSettings.particleSetting >= 2) {
            return;
        }

        int count = minecraft.gameSettings.particleSetting == 1 ? 4 : 10;
        Random random = world.rand;
        BlockPos playerPos = minecraft.player.getPosition();
        for (int i = 0; i < count; i++) {
            int xOffset = random.nextInt(ACID_RAIN_RADIUS * 2 + 1) - ACID_RAIN_RADIUS;
            int zOffset = random.nextInt(ACID_RAIN_RADIUS * 2 + 1) - ACID_RAIN_RADIUS;
            double x = playerPos.getX() + xOffset + random.nextDouble();
            double y = minecraft.player.posY + 6.0D + random.nextDouble() * 6.0D;
            double z = playerPos.getZ() + zOffset + random.nextDouble();
            BlockPos spawnPos = new BlockPos(x, y, z);
            if (world.isRainingAt(spawnPos)) {
                ParticleHelper.spawnAcidRain(world, x, y, z, 0.0D, -0.2D, 0.0D);
            }
        }
    }

    private void tickAcidRainSound(Minecraft minecraft) {
        World world = minecraft.world;
        if (!hasVenusAcidRain(minecraft)) {
            acidRainSoundCooldown = 0;
            return;
        }

        if (acidRainSoundCooldown > 0) {
            acidRainSoundCooldown--;
            return;
        }

        Random random = world.rand;
        BlockPos playerPos = minecraft.player.getPosition();
        BlockPos soundPos = null;
        for (int i = 0; i < 20; i++) {
            int xOffset = random.nextInt(ACID_RAIN_RADIUS * 2 + 1) - ACID_RAIN_RADIUS;
            int zOffset = random.nextInt(ACID_RAIN_RADIUS * 2 + 1) - ACID_RAIN_RADIUS;
            BlockPos rainPos = world.getPrecipitationHeight(playerPos.add(xOffset, 0, zOffset));
            if (rainPos.getY() <= playerPos.getY() + 10 && rainPos.getY() >= playerPos.getY() - 10 && world.isRainingAt(rainPos)) {
                soundPos = rainPos.down();
                break;
            }
        }

        if (soundPos == null) {
            acidRainSoundCooldown = 10;
            return;
        }

        boolean above = soundPos.getY() > playerPos.getY() + 1
            && world.getPrecipitationHeight(playerPos).getY() > playerPos.getY();
        world.playSound(
            minecraft.player,
            soundPos,
            above ? SoundEvents.WEATHER_RAIN_ABOVE : SoundEvents.WEATHER_RAIN,
            SoundCategory.WEATHER,
            above ? 0.1F : 0.2F,
            above ? 0.5F : 1.0F);
        acidRainSoundCooldown = 20 + random.nextInt(30);
    }

    private boolean hasVenusAcidRain(Minecraft minecraft) {
        World world = minecraft.world;
        return world != null
            && world.provider != null
            && world.provider.getDimension() == ModDimensions.VENUS_ID
            && world.isRaining();
    }

    private float blend(float original, float target, float intensity) {
        return original * (1.0F - intensity) + target * intensity;
    }

    private void tickJetSuitParticles(Minecraft minecraft) {
        if (minecraft.gameSettings.particleSetting >= 2
            || !AdAstraConfig.jetSuitEnabled
            || !minecraft.gameSettings.keyBindJump.isKeyDown()
            || !isWearingJetSuitSet()) {
            return;
        }

        ItemStack chest = minecraft.player.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
        int energyCost = minecraft.gameSettings.keyBindSprint.isKeyDown()
            ? JET_SUIT_FORWARD_ENERGY_PER_TICK
            : JET_SUIT_UPWARD_ENERGY_PER_TICK;
        if (!minecraft.player.capabilities.isCreativeMode
            && AdAstraArmorItem.consumeJetSuitEnergy(chest, energyCost, true) < energyCost) {
            return;
        }

        spawnJetSuitParticle(minecraft.world, 0.05D, 0.8D, -0.45D);
        spawnJetSuitParticle(minecraft.world, 0.05D, 0.8D, 0.45D);
        spawnJetSuitParticle(minecraft.world, 0.05D, 0.0D, -0.1D);
        spawnJetSuitParticle(minecraft.world, 0.05D, 0.0D, 0.1D);
    }

    private void spawnJetSuitParticle(World world, double sideOffset, double yOffset, double forwardOffset) {
        Minecraft minecraft = Minecraft.getMinecraft();
        double bodyYaw = minecraft.player.renderYawOffset;
        double forwardOffsetX = Math.cos(bodyYaw * Math.PI / 180.0D) * forwardOffset;
        double forwardOffsetZ = Math.sin(bodyYaw * Math.PI / 180.0D) * forwardOffset;
        double sideOffsetX = Math.cos((bodyYaw - 90.0D) * Math.PI / 180.0D) * sideOffset;
        double sideOffsetZ = Math.sin((bodyYaw - 90.0D) * Math.PI / 180.0D) * sideOffset;

        ParticleHelper.spawnLargeFlame(
            world,
            minecraft.player.posX + forwardOffsetX + sideOffsetX,
            minecraft.player.posY + yOffset,
            minecraft.player.posZ + forwardOffsetZ + sideOffsetZ,
            0.0D,
            0.0D,
            0.0D);
    }

    private boolean isWearingJetSuit() {
        Minecraft minecraft = Minecraft.getMinecraft();
        ItemStack chest = minecraft.player.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
        return !chest.isEmpty() && chest.getItem() == ModItems.JET_SUIT;
    }

    private boolean isWearingJetSuitSet() {
        Minecraft minecraft = Minecraft.getMinecraft();
        return isWearing(EntityEquipmentSlot.HEAD, ModItems.JET_SUIT_HELMET)
            && isWearing(EntityEquipmentSlot.CHEST, ModItems.JET_SUIT)
            && isWearing(EntityEquipmentSlot.LEGS, ModItems.JET_SUIT_PANTS)
            && isWearing(EntityEquipmentSlot.FEET, ModItems.JET_SUIT_BOOTS);
    }

    private boolean isWearing(EntityEquipmentSlot slot, net.minecraft.item.Item item) {
        Minecraft minecraft = Minecraft.getMinecraft();
        ItemStack stack = minecraft.player.getItemStackFromSlot(slot);
        return !stack.isEmpty() && stack.getItem() == item;
    }
}
