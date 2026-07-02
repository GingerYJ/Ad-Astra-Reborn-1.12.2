package earth.terrarium.adastra.common.handler;

import earth.terrarium.adastra.common.config.AdAstraConfig;
import earth.terrarium.adastra.common.items.AdAstraArmorItem;
import earth.terrarium.adastra.common.items.GasTankItem;
import earth.terrarium.adastra.common.registry.ModItems;
import earth.terrarium.adastra.common.util.EnvironmentUtils;
import earth.terrarium.adastra.common.util.KeybindManager;
import earth.terrarium.adastra.common.util.PlanetTravelHelper;
import earth.terrarium.adastra.common.util.SpaceStationLandingProtection;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.EntityTravelToDimensionEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class CommonEventHandler {

    private static final int DAMAGE_INTERVAL = 40;
    private static final DamageSource EXTREME_COLD = new DamageSource("freeze").setDamageBypassesArmor();
    private static final int JET_SUIT_UPWARD_ENERGY_PER_TICK = 50;
    private static final int JET_SUIT_FORWARD_ENERGY_PER_TICK = 100;
    private static final double JET_SUIT_UPWARD_FORCE = 0.075D;
    private static final double JET_SUIT_FORWARD_FORCE = 0.075D;
    private static final double JET_SUIT_MAX_FORWARD_SPEED = 2.0D;

    @SubscribeEvent
    public void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
        net.minecraft.entity.EntityLivingBase living = event.getEntityLiving();

        // Gravity motion override is now handled by GravityEventHandler
        // to avoid conflicts and maintain proper event priority

        if (!(living instanceof EntityPlayer)) {
            return;
        }

        EntityPlayer player = (EntityPlayer) living;
        if (player.world.isRemote) {
            return;
        }

        SpaceStationLandingProtection.tick(player);
        if (player.ticksExisted % 200 == 0) {
            SpaceStationLandingProtection.pruneExpired(player.ticksExisted);
        }

        tickJetSuit(player);

        if (player.capabilities.isCreativeMode || player.isSpectator()) {
            return;
        }

        // Use centralized OxygenSystem for all oxygen handling
        earth.terrarium.adastra.common.systems.OxygenSystem.checkOxygen(player, player.world);

        tickTemperature(player);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onEntityTravelToDimension(EntityTravelToDimensionEvent event) {
        if (!(event.getEntity() instanceof EntityPlayer)) {
            return;
        }

        EntityPlayer player = (EntityPlayer) event.getEntity();
        if (player.world == null || player.world.isRemote) {
            return;
        }

        int targetDimension = event.getDimension();
        int currentDimension = player.dimension;
        if (shouldBlockVanillaPlanetTravel(player, currentDimension, targetDimension)) {
            event.setCanceled(true);
            player.sendStatusMessage(new TextComponentTranslation("message.ad_astra.planets.vanilla_travel_blocked"), true);
        }
    }

    private void tickTemperature(EntityPlayer player) {
        // Use the new TemperatureSystem which handles all temperature logic
        if (player.ticksExisted % DAMAGE_INTERVAL == 0) {
            earth.terrarium.adastra.common.systems.TemperatureSystem.applyTemperatureDamage(player);
        }
    }

    private boolean shouldBlockVanillaPlanetTravel(EntityPlayer player, int currentDimension, int targetDimension) {
        if (PlanetTravelHelper.isRocketTravelInProgress(player, targetDimension)) {
            return false;
        }
        return AdAstraConfig.shouldBlockVanillaTravelForDimension(targetDimension)
            || AdAstraConfig.shouldBlockVanillaTravelForDimension(currentDimension);
    }

    @SubscribeEvent
    public void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        KeybindManager.clear(event.player);
        SpaceStationLandingProtection.clear(event.player);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onLivingFall(LivingFallEvent event) {
        if (event.getEntityLiving() instanceof EntityPlayer
            && SpaceStationLandingProtection.shouldCancelFall((EntityPlayer) event.getEntityLiving())) {
            event.setDistance(0.0F);
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onLivingAttack(LivingAttackEvent event) {
        if (event.getEntityLiving() instanceof EntityPlayer
            && SpaceStationLandingProtection.isFallDamage(event.getSource())
            && SpaceStationLandingProtection.shouldCancelFall((EntityPlayer) event.getEntityLiving())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onLivingHurt(LivingHurtEvent event) {
        if (event.getEntityLiving() instanceof EntityPlayer
            && SpaceStationLandingProtection.isFallDamage(event.getSource())
            && SpaceStationLandingProtection.shouldCancelFall((EntityPlayer) event.getEntityLiving())) {
            event.setAmount(0.0F);
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onLivingDamage(LivingDamageEvent event) {
        if (event.getEntityLiving() instanceof EntityPlayer
            && SpaceStationLandingProtection.isFallDamage(event.getSource())
            && SpaceStationLandingProtection.shouldCancelFall((EntityPlayer) event.getEntityLiving())) {
            event.setAmount(0.0F);
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onLivingCheckSpawn(LivingSpawnEvent.CheckSpawn event) {
        if (event.getEntityLiving() instanceof EntityLiving
            && exceedsPlanetEntityTypeCap((EntityLiving) event.getEntityLiving(), event.getWorld())) {
            event.setResult(Event.Result.DENY);
        }
    }

    @SubscribeEvent
    public void onEntityJoinWorld(EntityJoinWorldEvent event) {
        if (event.getEntity() instanceof EntityLiving
            && exceedsPlanetEntityTypeCap((EntityLiving) event.getEntity(), event.getWorld())) {
            event.setCanceled(true);
        }
    }

    private boolean exceedsPlanetEntityTypeCap(EntityLiving entity, World world) {
        if (world == null || world.isRemote || world.provider == null) {
            return false;
        }
        if (!AdAstraConfig.isPlanetDimension(world.provider.getDimension())) {
            return false;
        }
        if (AdAstraConfig.planetMobSpawnRateMultiplier <= 0.0F) {
            return true;
        }

        int count = 0;
        Class<?> entityClass = entity.getClass();
        for (Entity loaded : world.loadedEntityList) {
            if (loaded == entity || !loaded.isEntityAlive()) {
                continue;
            }
            if (loaded.getClass() == entityClass && ++count >= AdAstraConfig.planetEntityCapPerType) {
                return true;
            }
        }
        return false;
    }

    private void tickJetSuit(EntityPlayer player) {
        if (player.capabilities.isFlying || player.isSpectator()) {
            return;
        }
        if (!isWearingSet(player, ModItems.JET_SUIT_HELMET, ModItems.JET_SUIT, ModItems.JET_SUIT_PANTS, ModItems.JET_SUIT_BOOTS)) {
            return;
        }
        if (!KeybindManager.suitFlightEnabled(player) || !KeybindManager.jumpDown(player)) {
            return;
        }

        ItemStack chest = player.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
        int energyCost = KeybindManager.sprintDown(player) ? JET_SUIT_FORWARD_ENERGY_PER_TICK : JET_SUIT_UPWARD_ENERGY_PER_TICK;
        if (!player.capabilities.isCreativeMode && AdAstraArmorItem.consumeJetSuitEnergy(chest, energyCost, true) < energyCost) {
            return;
        }

        if (KeybindManager.sprintDown(player)) {
            propelForward(player);
        } else {
            propelUpward(player);
        }

        if (!player.capabilities.isCreativeMode) {
            AdAstraArmorItem.consumeJetSuitEnergy(chest, energyCost, false);
            player.inventory.markDirty();
        }
    }

    private void propelUpward(EntityPlayer player) {
        player.motionY += Math.max(0.0025D, JET_SUIT_UPWARD_FORCE);
        player.fallDistance = Math.max(player.fallDistance / 1.5F, 0.0F);
        player.velocityChanged = true;
    }

    private void propelForward(EntityPlayer player) {
        Vec3d look = player.getLookVec().normalize();
        double speed = Math.sqrt(player.motionX * player.motionX + player.motionY * player.motionY + player.motionZ * player.motionZ);
        if (speed <= JET_SUIT_MAX_FORWARD_SPEED) {
            player.motionX += look.x * JET_SUIT_FORWARD_FORCE;
            player.motionY += look.y * JET_SUIT_FORWARD_FORCE;
            player.motionZ += look.z * JET_SUIT_FORWARD_FORCE;
            player.fallDistance = Math.max(player.fallDistance / 1.5F, 0.0F);
            player.velocityChanged = true;
        }
    }

    private boolean canUseSuitOxygen(EntityPlayer player) {
        return isWearingSet(player, ModItems.SPACE_HELMET, ModItems.SPACE_SUIT, ModItems.SPACE_PANTS, ModItems.SPACE_BOOTS)
            || isWearingSet(player, ModItems.NETHERITE_SPACE_HELMET, ModItems.NETHERITE_SPACE_SUIT, ModItems.NETHERITE_SPACE_PANTS, ModItems.NETHERITE_SPACE_BOOTS)
            || isWearingSet(player, ModItems.JET_SUIT_HELMET, ModItems.JET_SUIT, ModItems.JET_SUIT_PANTS, ModItems.JET_SUIT_BOOTS);
    }

    private boolean hasFreezeProtection(EntityPlayer player) {
        return canUseSuitOxygen(player);
    }

    private boolean hasHeatProtection(EntityPlayer player) {
        return isWearingSet(player, ModItems.NETHERITE_SPACE_HELMET, ModItems.NETHERITE_SPACE_SUIT, ModItems.NETHERITE_SPACE_PANTS, ModItems.NETHERITE_SPACE_BOOTS)
            || isWearingSet(player, ModItems.JET_SUIT_HELMET, ModItems.JET_SUIT, ModItems.JET_SUIT_PANTS, ModItems.JET_SUIT_BOOTS);
    }

    private boolean isWearingSet(EntityPlayer player, Item helmet, Item chest, Item legs, Item boots) {
        return isWearing(player, EntityEquipmentSlot.HEAD, helmet)
            && isWearing(player, EntityEquipmentSlot.CHEST, chest)
            && isWearing(player, EntityEquipmentSlot.LEGS, legs)
            && isWearing(player, EntityEquipmentSlot.FEET, boots);
    }

    private boolean isWearing(EntityPlayer player, EntityEquipmentSlot slot, Item item) {
        ItemStack stack = player.getItemStackFromSlot(slot);
        return !stack.isEmpty() && stack.getItem() == item;
    }
}
