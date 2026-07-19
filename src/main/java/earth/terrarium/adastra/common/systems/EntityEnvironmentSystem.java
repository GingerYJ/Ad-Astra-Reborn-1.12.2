package earth.terrarium.adastra.common.systems;

import earth.terrarium.adastra.api.events.AdAstraEvents;
import earth.terrarium.adastra.common.config.AdAstraConfig;
import earth.terrarium.adastra.common.entities.mob.LunarianWanderingTraderEntity;
import earth.terrarium.adastra.common.entities.mob.MoglerEntity;
import earth.terrarium.adastra.common.entities.mob.PygroBruteEntity;
import earth.terrarium.adastra.common.entities.mob.PygroEntity;
import earth.terrarium.adastra.common.entities.mob.SulfurCreeperEntity;
import earth.terrarium.adastra.common.entities.mob.ZombifiedMoglerEntity;
import earth.terrarium.adastra.common.entities.mob.ZombifiedPygroEntity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
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
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

/**
 * Server-side environment tick entry point for living entities.
 */
public final class EntityEnvironmentSystem {

    private EntityEnvironmentSystem() {
    }

    public static void tick(EntityLivingBase entity) {
        if (entity == null || entity.world == null || entity.world.isRemote || !(entity.world instanceof WorldServer)) {
            return;
        }
        if (entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) entity;
            if (player.capabilities.isCreativeMode || player.isSpectator()) {
                return;
            }
            return;
        }

        tickOxygen(entity, (WorldServer) entity.world);
    }

    public static boolean canLiveWithoutOxygen(EntityLivingBase entity) {
        if (entity == null) {
            return true;
        }
        if (isConfiguredNoOxygenEntity(entity)) {
            return true;
        }
        if (entity.getClass().getName().startsWith("earth.terrarium.adastra.common.entities.")) {
            return true;
        }
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
            || entity instanceof SulfurCreeperEntity
            || entity instanceof EntityZombie
            || entity instanceof EntityHusk
            || entity instanceof AbstractSkeleton
            || entity instanceof EntityStray
            || entity instanceof EntitySnowman
            || entity instanceof EntityBlaze
            || entity instanceof EntityMagmaCube
            || entity instanceof EntityPigZombie;
    }

    private static boolean isConfiguredNoOxygenEntity(EntityLivingBase entity) {
        String[] whitelist = AdAstraConfig.noOxygenEntityWhitelist;
        if (whitelist == null || whitelist.length == 0) {
            return false;
        }

        ResourceLocation registryName = EntityList.getKey(entity);
        String registryId = registryName == null ? "" : registryName.toString();
        if (AdAstraConfig.isPlanetMobSpawnEntity(registryId)) {
            return true;
        }
        String namespaceWildcard = registryName == null ? "" : registryName.getNamespace() + ":*";
        String className = entity.getClass().getName();
        String simpleClassName = entity.getClass().getSimpleName();

        for (String entry : whitelist) {
            if (entry == null) {
                continue;
            }
            String normalized = entry.trim();
            if (normalized.isEmpty()) {
                continue;
            }
            if (normalized.equalsIgnoreCase(registryId)
                || normalized.equalsIgnoreCase(namespaceWildcard)
                || normalized.equals(className)
                || normalized.equals(simpleClassName)) {
                return true;
            }
        }
        return false;
    }

    private static void tickOxygen(EntityLivingBase entity, WorldServer world) {
        if (AdAstraConfig.disableOxygen || canLiveWithoutOxygen(entity)) {
            return;
        }

        boolean hasOxygen = AdAstraEvents.EntityOxygenEvent.fire(entity, hasOxygen(entity, world));
        if (hasOxygen) {
            if (entity.getAir() < OxygenSystem.MAX_AIR) {
                entity.setAir(OxygenSystem.MAX_AIR);
            }
            return;
        }

        entity.setAir(OxygenSystem.SUFFOCATION_AIR_LEVEL);
        if (entity.ticksExisted % OxygenSystem.getOxygenDamageInterval() != 0) {
            return;
        }
        if (AdAstraEvents.OxygenTickEvent.fire(world, entity)) {
            entity.attackEntityFrom(OxygenSystem.OXYGEN_DEPRIVATION, OxygenSystem.getOxygenDamageAmount());
        }
    }

    private static boolean hasOxygen(EntityLivingBase entity, World world) {
        return OxygenUtils.hasOxygenInDimension(world)
            || OxygenUtils.hasOxygenAtPosition(world, entity.getPosition());
    }
}
