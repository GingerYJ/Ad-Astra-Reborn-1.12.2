package earth.terrarium.adastra.common.handler;

import earth.terrarium.adastra.api.events.AdAstraEvents;
import earth.terrarium.adastra.api.planets.PlanetApi;
import earth.terrarium.adastra.api.systems.GravityApi;
import earth.terrarium.adastra.api.systems.OxygenApi;
import earth.terrarium.adastra.api.systems.PlanetData;
import earth.terrarium.adastra.api.systems.TemperatureApi;
import earth.terrarium.adastra.common.config.AdAstraConfig;
import earth.terrarium.adastra.common.entities.mob.LunarianWanderingTraderEntity;
import earth.terrarium.adastra.common.entities.mob.MoglerEntity;
import earth.terrarium.adastra.common.entities.mob.PygroBruteEntity;
import earth.terrarium.adastra.common.entities.mob.PygroEntity;
import earth.terrarium.adastra.common.entities.mob.SulfurCreeperEntity;
import earth.terrarium.adastra.common.entities.mob.ZombifiedMoglerEntity;
import earth.terrarium.adastra.common.entities.mob.ZombifiedPygroEntity;
import earth.terrarium.adastra.common.entities.vehicles.LanderEntity;
import earth.terrarium.adastra.common.entities.vehicles.VehicleBase;
import earth.terrarium.adastra.common.items.GasTankItem;
import earth.terrarium.adastra.common.items.SpaceSuitItem;
import earth.terrarium.adastra.common.network.NetworkHandler;
import earth.terrarium.adastra.common.network.packet.PacketPlayRadioStation;
import earth.terrarium.adastra.common.network.packet.PacketSyncLocalPlanetData;
import earth.terrarium.adastra.common.network.packet.PacketSyncPlanetDefinitions;
import earth.terrarium.adastra.common.planets.PlanetApiImpl;
import earth.terrarium.adastra.common.registry.ModDimensions;
import earth.terrarium.adastra.common.systems.EntityEnvironmentSystem;
import earth.terrarium.adastra.common.systems.OxygenSystem;
import earth.terrarium.adastra.common.registry.ModItems;
import earth.terrarium.adastra.common.util.EnvironmentUtils;
import earth.terrarium.adastra.common.util.KeybindManager;
import earth.terrarium.adastra.common.util.PlanetTravelHelper;
import earth.terrarium.adastra.common.util.SpaceStationLandingProtection;
import earth.terrarium.adastra.common.util.radio.RadioHolder;
import earth.terrarium.adastra.common.world.PlanetMobSpawns;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.monster.AbstractSkeleton;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.entity.monster.EntityHusk;
import net.minecraft.entity.monster.EntityMagmaCube;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.monster.EntitySnowman;
import net.minecraft.entity.monster.EntityStray;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class CommonEventHandler {

    private static final int DAMAGE_INTERVAL = 40;
    private static final int UNSAFE_DISMOUNT_HOLD_TICKS = 40;
    private static final DamageSource EXTREME_COLD = new DamageSource("freeze").setDamageBypassesArmor();
    private static final DamageSource ACID_RAIN = new DamageSource("acidRain");
    private static final Map<UUID, Integer> DISMOUNT_HOLD_TICKS = new HashMap<>();
    private static final Map<UUID, PendingDismountPosition> PENDING_DISMOUNT_POSITIONS = new HashMap<>();
    private static final Set<Integer> SPACE_SLEEPING_DIMENSIONS = new HashSet<>();
    private static final Map<UUID, PlanetData> LAST_SYNCED_PLANET_DATA = new HashMap<>();

    @SubscribeEvent
    public void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
        net.minecraft.entity.EntityLivingBase living = event.getEntityLiving();

        // Gravity motion override is now handled by GravityEventHandler
        // to avoid conflicts and maintain proper event priority

        if (living.world.isRemote) {
            return;
        }

        EntityEnvironmentSystem.tick(living);
        tickAcidRain(living);

        if (!(living instanceof EntityPlayer)) {
            return;
        }

        EntityPlayer player = (EntityPlayer) living;

        SpaceStationLandingProtection.tick(player);
        tickVehicleDismountHold(player);
        applyPendingVehicleDismount(player);
        if (player.ticksExisted % 200 == 0) {
            SpaceStationLandingProtection.pruneExpired(player.ticksExisted);
        }

        syncLocalPlanetData(player);

        if (player.capabilities.isCreativeMode || player.isSpectator()) {
            return;
        }

        // Use centralized OxygenSystem for all oxygen handling
        earth.terrarium.adastra.common.systems.OxygenSystem.checkOxygen(player, player.world);

        tickTemperature(player);
    }

    @SubscribeEvent
    public void onWorldTick(TickEvent.WorldTickEvent event) {
        if (event.world == null || event.world.isRemote || !(event.world instanceof WorldServer)) {
            return;
        }

        WorldServer world = (WorldServer) event.world;
        int dimension = world.provider.getDimension();

        if (event.phase == TickEvent.Phase.START) {
            if (shouldAdvanceAllWorldsAfterSpaceSleep(world)) {
                SPACE_SLEEPING_DIMENSIONS.add(dimension);
            } else {
                SPACE_SLEEPING_DIMENSIONS.remove(dimension);
            }
            return;
        }

        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        PlanetMobSpawns.tickWorld(world);
        if (!SPACE_SLEEPING_DIMENSIONS.remove(dimension)) {
            return;
        }

        long dayTime = world.getWorldTime();
        long nextDayStart = dayTime - dayTime % 24000L;
        for (WorldServer serverWorld : world.getMinecraftServer().worlds) {
            if (serverWorld != null) {
                serverWorld.setWorldTime(nextDayStart);
            }
        }
    }

    private boolean shouldAdvanceAllWorldsAfterSpaceSleep(WorldServer world) {
        return world.provider.getDimension() != 0
            && PlanetApi.API.isExtraterrestrial(world)
            && world.getGameRules().getBoolean("doDaylightCycle")
            && world.areAllPlayersAsleep();
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onEntityMount(EntityMountEvent event) {
        if (!event.isDismounting() || !(event.getEntityMounting() instanceof EntityPlayer)) {
            return;
        }

        EntityPlayer player = (EntityPlayer) event.getEntityMounting();
        Entity mounted = event.getEntityBeingMounted();

        if (mounted instanceof LanderEntity && !mounted.onGround && mounted.posY > PlanetTravelHelper.LANDING_Y - 10.0D) {
            event.setCanceled(true);
            PENDING_DISMOUNT_POSITIONS.remove(player.getUniqueID());
            return;
        }

        if (mounted instanceof VehicleBase && !canUnsafeDismount(player, (VehicleBase) mounted)) {
            event.setCanceled(true);
            PENDING_DISMOUNT_POSITIONS.remove(player.getUniqueID());
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onVehicleDismountPosition(EntityMountEvent event) {
        if (!event.isDismounting() || !(event.getEntityMounting() instanceof EntityPlayer) || !(event.getEntityBeingMounted() instanceof VehicleBase)) {
            return;
        }

        EntityPlayer player = (EntityPlayer) event.getEntityMounting();
        if (event.isCanceled()) {
            PENDING_DISMOUNT_POSITIONS.remove(player.getUniqueID());
            return;
        }

        VehicleBase vehicle = (VehicleBase) event.getEntityBeingMounted();
        Vec3d position = vehicle.getDismountPosition(player);
        if (position == null) {
            PENDING_DISMOUNT_POSITIONS.remove(player.getUniqueID());
            return;
        }
        PENDING_DISMOUNT_POSITIONS.put(player.getUniqueID(), new PendingDismountPosition(player.dimension, position));
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onRadioHolderMount(EntityMountEvent event) {
        if (event.isCanceled() || event.isDismounting() || !(event.getEntityMounting() instanceof EntityPlayerMP)) {
            return;
        }
        Entity mounted = event.getEntityBeingMounted();
        if (mounted instanceof RadioHolder) {
            NetworkHandler.CHANNEL.sendTo(
                new PacketPlayRadioStation(((RadioHolder) mounted).getRadioUrl()),
                (EntityPlayerMP) event.getEntityMounting());
        }
    }

    private void tickTemperature(EntityPlayer player) {
        // Use the new TemperatureSystem which handles all temperature logic
        if (player.ticksExisted % DAMAGE_INTERVAL == 0) {
            earth.terrarium.adastra.common.systems.TemperatureSystem.applyTemperatureDamage(player);
        }
    }

    private void syncLocalPlanetData(EntityPlayer player) {
        if (!(player instanceof EntityPlayerMP) || player.ticksExisted % 5 != 0) {
            return;
        }
        boolean oxygen = OxygenApi.API.hasOxygen(player);
        short temperature = TemperatureApi.API.getTemperature(player);
        float gravity = GravityApi.API.getGravity(player);
        PlanetData data = new PlanetData(oxygen, temperature, gravity);
        UUID playerId = player.getUniqueID();
        if (!data.equals(LAST_SYNCED_PLANET_DATA.get(playerId))) {
            LAST_SYNCED_PLANET_DATA.put(playerId, data);
            NetworkHandler.CHANNEL.sendTo(new PacketSyncLocalPlanetData(data), (EntityPlayerMP) player);
        }
    }

    @SubscribeEvent
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.player instanceof EntityPlayerMP) {
            NetworkHandler.CHANNEL.sendTo(
                new PacketSyncPlanetDefinitions(PlanetApiImpl.snapshotPlanets()),
                (EntityPlayerMP) event.player);
        }
    }

    @SubscribeEvent
    public void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        KeybindManager.clear(event.player);
        clearDismountHold(event.player, false);
        PENDING_DISMOUNT_POSITIONS.remove(event.player.getUniqueID());
        LAST_SYNCED_PLANET_DATA.remove(event.player.getUniqueID());
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
            return;
        }
        if (shouldCancelProtectedFireDamage(event.getEntityLiving(), event.getSource())) {
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
            return;
        }
        if (shouldCancelProtectedFireDamage(event.getEntityLiving(), event.getSource())) {
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
            return;
        }
        if (shouldCancelProtectedFireDamage(event.getEntityLiving(), event.getSource())) {
            event.setAmount(0.0F);
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onLivingCheckSpawn(LivingSpawnEvent.CheckSpawn event) {
        if (event.getEntityLiving() instanceof EntityLiving
            && shouldBlockDisabledHostileMobSpawn((EntityLiving) event.getEntityLiving(), event.getWorld())) {
            event.setResult(Event.Result.DENY);
            return;
        }
        if (event.getEntityLiving() instanceof EntityLiving
            && shouldBlockNoOxygenSpawn((EntityLiving) event.getEntityLiving(), event.getWorld())) {
            event.setResult(Event.Result.DENY);
            return;
        }
        if (event.getEntityLiving() instanceof EntityLiving
            && !event.isSpawner()
            && PlanetMobSpawns.isRespawnOnCooldown((EntityLiving) event.getEntityLiving(), event.getWorld())) {
            event.setResult(Event.Result.DENY);
            return;
        }
        if (event.getEntityLiving() instanceof EntityLiving
            && !event.isSpawner()
            && shouldBlockPlanetWhitelistSpawn((EntityLiving) event.getEntityLiving(), event.getWorld())) {
            event.setResult(Event.Result.DENY);
            return;
        }
        if (event.getEntityLiving() instanceof EntityLiving
            && exceedsPlanetEntityTypeCap((EntityLiving) event.getEntityLiving(), event.getWorld())) {
            event.setResult(Event.Result.DENY);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onEntityJoinWorld(EntityJoinWorldEvent event) {
        if (event.getEntity() instanceof EntityLiving
            && exceedsPlanetEntityTypeCap((EntityLiving) event.getEntity(), event.getWorld())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onEntityJoinWorldTracking(EntityJoinWorldEvent event) {
        if (!event.isCanceled() && event.getEntity() instanceof EntityLiving) {
            PlanetMobSpawns.onEntityJoined((EntityLiving) event.getEntity(), event.getWorld());
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onLivingDeath(LivingDeathEvent event) {
        if (!event.isCanceled() && event.getEntityLiving() instanceof EntityLiving) {
            PlanetMobSpawns.onEntityDied((EntityLiving) event.getEntityLiving(), event.getEntityLiving().world);
        }
    }

    @SubscribeEvent
    public void onWorldUnload(WorldEvent.Unload event) {
        PlanetMobSpawns.onWorldUnload(event.getWorld());
    }

    private boolean shouldBlockDisabledHostileMobSpawn(EntityLiving entity, World world) {
        if (entity == null || world == null || world.isRemote || world.provider == null) {
            return false;
        }
        if (!entity.isCreatureType(EnumCreatureType.MONSTER, false)) {
            return false;
        }
        return !AdAstraConfig.canHostileMobsSpawn(world.provider.getDimension());
    }

    private boolean shouldBlockNoOxygenSpawn(EntityLiving entity, World world) {
        if (entity == null || world == null || world.isRemote || world.provider == null) {
            return false;
        }
        if (!PlanetApi.API.isPlanet(world) || OxygenApi.API.hasOxygen(world)) {
            return false;
        }
        return !EntityEnvironmentSystem.canLiveWithoutOxygen(entity);
    }

    private boolean shouldBlockPlanetWhitelistSpawn(EntityLiving entity, World world) {
        if (entity == null || world == null || world.isRemote || world.provider == null) {
            return false;
        }
        int dimensionId = world.provider.getDimension();
        return AdAstraConfig.isPlanetDimension(dimensionId)
            && !PlanetMobSpawns.isSpawnAllowed(entity, dimensionId);
    }

    private void tickAcidRain(EntityLivingBase entity) {
        if (entity.ticksExisted % 10 != 0 || !(entity.world instanceof WorldServer)) {
            return;
        }
        if (entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) entity;
            if (player.capabilities.isCreativeMode || player.isSpectator()) {
                return;
            }
        }
        if (!isInAcidRain(entity) || canSurviveAcidRain(entity) || isProtectedByAcidRainVehicle(entity)) {
            return;
        }

        WorldServer world = (WorldServer) entity.world;
        if (!AdAstraEvents.AcidRainTickEvent.fire(world, entity)) {
            return;
        }

        entity.attackEntityFrom(ACID_RAIN, 3.0F);
        world.playSound(null, entity.posX, entity.posY, entity.posZ,
            net.minecraft.init.SoundEvents.ENTITY_GENERIC_BURN,
            SoundCategory.NEUTRAL,
            0.4F,
            2.0F + entity.getRNG().nextFloat() * 0.4F);
    }

    private boolean isInAcidRain(EntityLivingBase entity) {
        World world = entity.world;
        if (world == null || world.provider == null || world.provider.getDimension() != ModDimensions.VENUS_ID) {
            return false;
        }
        return world.isRainingAt(entity.getPosition())
            || world.isRainingAt(new net.minecraft.util.math.BlockPos(
                entity.posX,
                entity.getEntityBoundingBox().maxY,
                entity.posZ));
    }

    private boolean canSurviveAcidRain(EntityLivingBase entity) {
        return canSurviveExtremeHeat(entity) || canSurviveInSpace(entity);
    }

    private boolean canSurviveExtremeHeat(EntityLivingBase entity) {
        return entity instanceof EntityBlaze
            || entity instanceof EntityMagmaCube
            || entity instanceof EntityPigZombie;
    }

    private boolean canSurviveInSpace(EntityLivingBase entity) {
        return entity instanceof EntityArmorStand
            || entity instanceof EntityIronGolem
            || entity instanceof EntityDragon
            || entity instanceof EntityWither
            || entity instanceof LunarianWanderingTraderEntity
            || entity instanceof PygroEntity
            || entity instanceof PygroBruteEntity
            || entity instanceof ZombifiedPygroEntity
            || entity instanceof MoglerEntity
            || entity instanceof ZombifiedMoglerEntity
            || entity instanceof SulfurCreeperEntity;
    }

    private boolean isProtectedByAcidRainVehicle(EntityLivingBase entity) {
        Entity riding = entity.getRidingEntity();
        return riding instanceof VehicleBase
            && (((VehicleBase) riding).getVehicleType() == VehicleBase.VehicleType.ROCKET
                || ((VehicleBase) riding).getVehicleType() == VehicleBase.VehicleType.LANDER);
    }

    private void tickVehicleDismountHold(EntityPlayer player) {
        Entity riding = player.getRidingEntity();
        if (!(riding instanceof VehicleBase) || ((VehicleBase) riding).isSafeToDismount(player) || !player.isSneaking()) {
            clearDismountHold(player, true);
            return;
        }

        UUID id = player.getUniqueID();
        int ticks = Math.min(UNSAFE_DISMOUNT_HOLD_TICKS, DISMOUNT_HOLD_TICKS.getOrDefault(id, 0) + 1);
        DISMOUNT_HOLD_TICKS.put(id, ticks);

        if (ticks < UNSAFE_DISMOUNT_HOLD_TICKS) {
            float seconds = Math.round((UNSAFE_DISMOUNT_HOLD_TICKS - ticks) / 20.0F * 10.0F) / 10.0F;
            player.sendStatusMessage(new TextComponentTranslation("message.ad_astra.hold_to_dismount", seconds), true);
        } else {
            player.sendStatusMessage(new TextComponentString(""), true);
        }
    }

    private boolean canUnsafeDismount(EntityPlayer player, VehicleBase vehicle) {
        if (vehicle.isSafeToDismount(player)) {
            clearDismountHold(player, true);
            return true;
        }
        if (DISMOUNT_HOLD_TICKS.getOrDefault(player.getUniqueID(), 0) >= UNSAFE_DISMOUNT_HOLD_TICKS) {
            clearDismountHold(player, true);
            return true;
        }
        return false;
    }

    private void clearDismountHold(EntityPlayer player, boolean clearMessage) {
        if (player == null) {
            return;
        }
        if (DISMOUNT_HOLD_TICKS.remove(player.getUniqueID()) != null && clearMessage) {
            player.sendStatusMessage(new TextComponentString(""), true);
        }
    }

    private void applyPendingVehicleDismount(EntityPlayer player) {
        PendingDismountPosition pending = PENDING_DISMOUNT_POSITIONS.get(player.getUniqueID());
        if (pending == null) {
            return;
        }
        if (player.getRidingEntity() != null) {
            return;
        }

        PENDING_DISMOUNT_POSITIONS.remove(player.getUniqueID());
        if (player.dimension != pending.dimension) {
            return;
        }

        Vec3d position = pending.position;
        if (player instanceof EntityPlayerMP) {
            ((EntityPlayerMP) player).connection.setPlayerLocation(position.x, position.y, position.z, player.rotationYaw, player.rotationPitch);
        } else {
            player.setPosition(position.x, position.y, position.z);
        }
        player.fallDistance = 0.0F;
    }

    private static class PendingDismountPosition {
        private final int dimension;
        private final Vec3d position;

        private PendingDismountPosition(int dimension, Vec3d position) {
            this.dimension = dimension;
            this.position = position;
        }
    }

    private boolean shouldCancelProtectedFireDamage(EntityLivingBase entity, DamageSource source) {
        if (entity == null || source == null || !isFireOrHotFloorDamage(source)) {
            return false;
        }
        if (!hasFullNetheriteSpaceSuit(entity)) {
            return false;
        }
        entity.extinguish();
        return true;
    }

    private boolean isFireOrHotFloorDamage(DamageSource source) {
        return source.isFireDamage() || "hotFloor".equals(source.getDamageType());
    }

    private boolean hasFullNetheriteSpaceSuit(EntityLivingBase entity) {
        return SpaceSuitItem.hasFullNetheriteSet(entity);
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

        return PlanetMobSpawns.exceedsEntityTypeCap(entity, world.provider.getDimension());
    }

    private boolean canUseSuitOxygen(EntityPlayer player) {
        return SpaceSuitItem.hasFullSet(player);
    }

    private boolean hasFreezeProtection(EntityPlayer player) {
        return canUseSuitOxygen(player);
    }

    private boolean hasHeatProtection(EntityPlayer player) {
        return SpaceSuitItem.hasFullHeatResistantSet(player);
    }

    /**
     * Invalidate nearby sealed-room caches when a block is placed.
     * This ensures oxygen distributor rooms are re-scanned after
     * a player builds/expands a room.
     */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onBlockPlace(BlockEvent.PlaceEvent event) {
        if (event.getWorld().isRemote) return;
        OxygenSystem.invalidateNearbyCache(
            (World) event.getWorld(), event.getPos(), 32);
    }

    /**
     * Invalidate nearby sealed-room caches when a block is broken.
     */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onBlockBreak(BlockEvent.BreakEvent event) {
        if (event.getWorld().isRemote) return;
        OxygenSystem.invalidateNearbyCache(
            (World) event.getWorld(), event.getPos(), 32);
    }
}
