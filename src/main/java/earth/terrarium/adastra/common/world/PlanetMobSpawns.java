package earth.terrarium.adastra.common.world;

import earth.terrarium.adastra.AdAstraReborn;
import earth.terrarium.adastra.common.config.AdAstraConfig;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/** Resolves planet spawn rows and keeps low-cost per-planet mob counts. */
public final class PlanetMobSpawns {

    private static final Map<Integer, Map<String, Integer>> ENTITY_COUNTS = new HashMap<>();
    private static final Map<Integer, Set<UUID>> TRACKED_ENTITIES = new HashMap<>();
    private static final Map<Integer, Long> LAST_COUNT_RESCANS = new HashMap<>();
    private static final Map<Integer, Map<String, Long>> RESPAWN_COOLDOWNS = new HashMap<>();
    private static final Map<String, EntityEntry> ENTITY_ENTRIES_BY_ID = new HashMap<>();
    private static final Set<String> UNRESOLVED_ENTITY_IDS = new HashSet<>();
    private static final Map<Class<?>, ResourceLocation> ENTITY_IDS_BY_CLASS = new HashMap<>();
    private static final Set<Class<?>> UNREGISTERED_ENTITY_CLASSES = new HashSet<>();
    private static final Set<String> WARNED_ENTITY_IDS = new HashSet<>();

    private PlanetMobSpawns() {
    }

    public static List<Biome.SpawnListEntry> getPossibleCreatures(
        PlanetDimensionProperties properties,
        EnumCreatureType creatureType) {
        if (properties == null) {
            return Collections.emptyList();
        }
        if (creatureType == EnumCreatureType.MONSTER
            && !AdAstraConfig.canHostileMobsSpawn(properties.getDimensionId())) {
            return Collections.emptyList();
        }

        String planetKey = AdAstraConfig.getPlanetKeyForDimension(properties.getDimensionId());
        if (planetKey == null) {
            planetKey = properties.getName();
        }
        List<AdAstraConfig.PlanetMobSpawnConfig> configured =
            AdAstraConfig.getPlanetMobSpawnWhitelist(planetKey);
        List<AdAstraConfig.PlanetMobSpawnConfig> validRows = getValidRows(configured);
        if (configured.isEmpty() || validRows.isEmpty()) {
            return properties.getBiome() == null
                ? Collections.<Biome.SpawnListEntry>emptyList()
                : properties.getBiome().getSpawnableList(creatureType);
        }

        List<Biome.SpawnListEntry> result = new ArrayList<>();
        for (AdAstraConfig.PlanetMobSpawnConfig row : validRows) {
            if (row.getMaxCount() == 0 || mapType(row.getSpawnType()) != creatureType) {
                continue;
            }
            EntityEntry entityEntry = resolve(row.getEntityId());
            if (entityEntry == null) {
                continue;
            }
            @SuppressWarnings("unchecked")
            Class<? extends EntityLiving> entityClass = (Class<? extends EntityLiving>) entityEntry.getEntityClass();
            result.add(new Biome.SpawnListEntry(entityClass, row.getWeight(), row.getMinGroup(), row.getMaxGroup()));
        }
        return result;
    }

    public static boolean isSpawnAllowed(EntityLiving entity, int dimensionId) {
        if (entity == null) {
            return false;
        }
        String planetKey = AdAstraConfig.getPlanetKeyForDimension(dimensionId);
        if (planetKey == null) {
            return true;
        }
        List<AdAstraConfig.PlanetMobSpawnConfig> configured =
            AdAstraConfig.getPlanetMobSpawnWhitelist(planetKey);
        List<AdAstraConfig.PlanetMobSpawnConfig> validRows = getValidRows(configured);
        if (configured.isEmpty() || validRows.isEmpty()) {
            return true;
        }
        ResourceLocation entityId = getEntityId(entity);
        if (entityId == null) {
            return false;
        }
        EnumCreatureType creatureType = getCreatureType(entity);
        for (AdAstraConfig.PlanetMobSpawnConfig row : validRows) {
            if (row.getMaxCount() > 0
                && row.getEntityId().equals(entityId.toString())
                && mapType(row.getSpawnType()) == creatureType) {
                return true;
            }
        }
        return false;
    }

    public static int getMaxCount(EntityLiving entity, int dimensionId) {
        int defaultLimit = Math.max(1, AdAstraConfig.planetEntityCapPerType);
        if (entity == null) {
            return defaultLimit;
        }
        String planetKey = AdAstraConfig.getPlanetKeyForDimension(dimensionId);
        if (planetKey == null) {
            return defaultLimit;
        }
        List<AdAstraConfig.PlanetMobSpawnConfig> configured =
            AdAstraConfig.getPlanetMobSpawnWhitelist(planetKey);
        if (configured.isEmpty()) {
            return defaultLimit;
        }
        ResourceLocation entityId = getEntityId(entity);
        if (entityId == null) {
            return 0;
        }
        EnumCreatureType creatureType = getCreatureType(entity);
        int limit = Integer.MAX_VALUE;
        for (AdAstraConfig.PlanetMobSpawnConfig row : configured) {
            if (row.getEntityId().equals(entityId.toString())
                && mapType(row.getSpawnType()) == creatureType) {
                limit = Math.min(limit, row.getMaxCount());
            }
        }
        return limit == Integer.MAX_VALUE ? defaultLimit : limit;
    }

    public static int getEntityCount(EntityLiving entity, int dimensionId) {
        ResourceLocation entityId = getEntityId(entity);
        if (entityId == null) {
            return 0;
        }
        Map<String, Integer> counts = ENTITY_COUNTS.get(dimensionId);
        if (counts == null) {
            return 0;
        }
        Integer count = counts.get(entityId.toString());
        return count == null ? 0 : count;
    }

    public static boolean exceedsEntityTypeCap(EntityLiving entity, int dimensionId) {
        int maxCount = getMaxCount(entity, dimensionId);
        return maxCount <= 0 || getEntityCount(entity, dimensionId) >= maxCount;
    }

    /** Records a living entity after a join event that was not canceled. */
    public static void onEntityJoined(EntityLiving entity, World world) {
        if (world == null || world.isRemote || world.provider == null || entity == null) {
            return;
        }
        ResourceLocation entityId = getEntityId(entity);
        if (entityId == null) {
            return;
        }
        int dimensionId = world.provider.getDimension();
        removeTrackedEntityFromOtherDimensions(entity, dimensionId, entityId.toString());
        if (!isTrackedPlanetWorld(world) || !entity.isEntityAlive()) {
            return;
        }
        Set<UUID> tracked = TRACKED_ENTITIES.computeIfAbsent(dimensionId, key -> new HashSet<>());
        if (tracked.add(entity.getUniqueID())) {
            incrementCount(dimensionId, entityId.toString());
        }
    }

    /** Removes a living entity from the incremental count. Safe to call more than once. */
    public static void onEntityLeft(EntityLiving entity, World world) {
        if (!isTrackedPlanetWorld(world) || entity == null) {
            return;
        }
        untrackEntity(world.provider.getDimension(), entity);
    }

    /** Removes a dead entity and starts the optional natural-spawn cooldown. */
    public static void onEntityDied(EntityLiving entity, World world) {
        if (!isTrackedPlanetWorld(world) || entity == null) {
            return;
        }
        int dimensionId = world.provider.getDimension();
        ResourceLocation entityId = getEntityId(entity);
        untrackEntity(dimensionId, entity);
        if (entityId == null) {
            return;
        }

        int interval = AdAstraConfig.planetMobRespawnIntervalTicks;
        Map<String, Long> cooldowns = RESPAWN_COOLDOWNS.get(dimensionId);
        if (interval <= 0) {
            if (cooldowns != null) {
                cooldowns.remove(entityId.toString());
                if (cooldowns.isEmpty()) {
                    RESPAWN_COOLDOWNS.remove(dimensionId);
                }
            }
            return;
        }

        if (cooldowns == null) {
            cooldowns = new HashMap<>();
            RESPAWN_COOLDOWNS.put(dimensionId, cooldowns);
        }
        long expiresAt = world.getTotalWorldTime() + interval;
        Long currentExpiry = cooldowns.get(entityId.toString());
        if (currentExpiry == null || expiresAt > currentExpiry) {
            cooldowns.put(entityId.toString(), expiresAt);
        }
    }

    /** Only natural spawn checks call this; summoned or manually added entities are unaffected. */
    public static boolean isRespawnOnCooldown(EntityLiving entity, World world) {
        if (!isTrackedPlanetWorld(world) || entity == null || AdAstraConfig.planetMobRespawnIntervalTicks <= 0) {
            return false;
        }
        ResourceLocation entityId = getEntityId(entity);
        if (entityId == null) {
            return false;
        }
        int dimensionId = world.provider.getDimension();
        Map<String, Long> cooldowns = RESPAWN_COOLDOWNS.get(dimensionId);
        if (cooldowns == null) {
            return false;
        }
        String id = entityId.toString();
        Long expiresAt = cooldowns.get(id);
        if (expiresAt == null) {
            return false;
        }
        if (world.getTotalWorldTime() >= expiresAt) {
            cooldowns.remove(id);
            if (cooldowns.isEmpty()) {
                RESPAWN_COOLDOWNS.remove(dimensionId);
            }
            return false;
        }
        return true;
    }

    /** Performs one complete count calibration for a player-active planet world. */
    public static void tickWorld(World world) {
        if (!isTrackedPlanetWorld(world) || world.playerEntities == null || world.playerEntities.isEmpty()) {
            return;
        }
        int dimensionId = world.provider.getDimension();
        long now = world.getTotalWorldTime();
        Long lastRescan = LAST_COUNT_RESCANS.get(dimensionId);
        int interval = AdAstraConfig.planetMobCountRescanIntervalTicks;
        if (lastRescan != null
            && (interval <= 0 || (now >= lastRescan && now - lastRescan < interval))) {
            return;
        }
        rescanWorld(world, dimensionId, now);
    }

    public static void onWorldUnload(World world) {
        if (world == null || world.isRemote || world.provider == null) {
            return;
        }
        int dimensionId = world.provider.getDimension();
        ENTITY_COUNTS.remove(dimensionId);
        TRACKED_ENTITIES.remove(dimensionId);
        LAST_COUNT_RESCANS.remove(dimensionId);
        RESPAWN_COOLDOWNS.remove(dimensionId);
    }

    private static void rescanWorld(World world, int dimensionId, long now) {
        Map<String, Integer> counts = new HashMap<>();
        Set<UUID> tracked = new HashSet<>();
        for (Entity loaded : world.loadedEntityList) {
            if (!(loaded instanceof EntityLiving) || !loaded.isEntityAlive()) {
                continue;
            }
            ResourceLocation entityId = getEntityId((EntityLiving) loaded);
            if (entityId == null) {
                continue;
            }
            tracked.add(loaded.getUniqueID());
            String id = entityId.toString();
            Integer count = counts.get(id);
            counts.put(id, count == null ? 1 : count + 1);
        }
        ENTITY_COUNTS.put(dimensionId, counts);
        TRACKED_ENTITIES.put(dimensionId, tracked);
        LAST_COUNT_RESCANS.put(dimensionId, now);
    }

    private static void incrementCount(int dimensionId, String entityId) {
        Map<String, Integer> counts = ENTITY_COUNTS.computeIfAbsent(dimensionId, key -> new HashMap<>());
        Integer count = counts.get(entityId);
        counts.put(entityId, count == null ? 1 : count + 1);
    }

    private static void untrackEntity(int dimensionId, EntityLiving entity) {
        Set<UUID> tracked = TRACKED_ENTITIES.get(dimensionId);
        if (tracked == null || !tracked.remove(entity.getUniqueID())) {
            return;
        }
        ResourceLocation entityId = getEntityId(entity);
        if (entityId == null) {
            return;
        }
        Map<String, Integer> counts = ENTITY_COUNTS.get(dimensionId);
        if (counts == null) {
            return;
        }
        String id = entityId.toString();
        Integer count = counts.get(id);
        if (count == null || count <= 1) {
            counts.remove(id);
        } else {
            counts.put(id, count - 1);
        }
    }

    private static void removeTrackedEntityFromOtherDimensions(
        EntityLiving entity,
        int currentDimensionId,
        String entityId) {
        UUID uuid = entity.getUniqueID();
        for (Map.Entry<Integer, Set<UUID>> entry : TRACKED_ENTITIES.entrySet()) {
            if (entry.getKey() == currentDimensionId || !entry.getValue().remove(uuid)) {
                continue;
            }
            decrementCount(entry.getKey(), entityId);
        }
    }

    private static void decrementCount(int dimensionId, String entityId) {
        Map<String, Integer> counts = ENTITY_COUNTS.get(dimensionId);
        if (counts == null) {
            return;
        }
        Integer count = counts.get(entityId);
        if (count == null || count <= 1) {
            counts.remove(entityId);
        } else {
            counts.put(entityId, count - 1);
        }
    }

    private static boolean isTrackedPlanetWorld(World world) {
        return world != null
            && !world.isRemote
            && world.provider != null
            && AdAstraConfig.isPlanetDimension(world.provider.getDimension());
    }

    private static List<AdAstraConfig.PlanetMobSpawnConfig> getValidRows(
        List<AdAstraConfig.PlanetMobSpawnConfig> configured) {
        if (configured.isEmpty()) {
            return Collections.emptyList();
        }

        List<AdAstraConfig.PlanetMobSpawnConfig> valid = new ArrayList<>();
        for (AdAstraConfig.PlanetMobSpawnConfig row : configured) {
            EntityEntry entry = resolve(row.getEntityId());
            if (entry != null && EntityLiving.class.isAssignableFrom(entry.getEntityClass())) {
                valid.add(row);
            }
        }
        return valid;
    }

    public static EnumCreatureType getCreatureType(EntityLiving entity) {
        if (entity.isCreatureType(EnumCreatureType.MONSTER, false)) return EnumCreatureType.MONSTER;
        if (entity.isCreatureType(EnumCreatureType.WATER_CREATURE, false)) return EnumCreatureType.WATER_CREATURE;
        if (entity.isCreatureType(EnumCreatureType.CREATURE, false)) return EnumCreatureType.CREATURE;
        return EnumCreatureType.AMBIENT;
    }

    private static EnumCreatureType mapType(String type) {
        if ("monster".equals(type)) return EnumCreatureType.MONSTER;
        if ("creature".equals(type)) return EnumCreatureType.CREATURE;
        if ("water".equals(type)) return EnumCreatureType.WATER_CREATURE;
        // 1.12.2 has no CAVE enum; AMBIENT is the vanilla cave-creature category.
        return EnumCreatureType.AMBIENT;
    }

    private static EntityEntry resolve(String id) {
        EntityEntry cached = ENTITY_ENTRIES_BY_ID.get(id);
        if (cached != null) {
            return cached;
        }
        if (UNRESOLVED_ENTITY_IDS.contains(id)) {
            return null;
        }
        try {
            EntityEntry entry = ForgeRegistries.ENTITIES.getValue(new ResourceLocation(id));
            if (entry == null) {
                UNRESOLVED_ENTITY_IDS.add(id);
                if (WARNED_ENTITY_IDS.add(id)) {
                    AdAstraReborn.LOGGER.warn("Ignored mob whitelist entity '{}': entity is not registered.", id);
                }
            } else {
                ENTITY_ENTRIES_BY_ID.put(id, entry);
                if (!EntityLiving.class.isAssignableFrom(entry.getEntityClass())
                    && WARNED_ENTITY_IDS.add(id + "#type")) {
                    AdAstraReborn.LOGGER.warn("Ignored mob whitelist entity '{}': entity is not living.", id);
                }
            }
            return entry;
        } catch (RuntimeException exception) {
            UNRESOLVED_ENTITY_IDS.add(id);
            if (WARNED_ENTITY_IDS.add(id)) {
                AdAstraReborn.LOGGER.warn("Ignored mob whitelist entity '{}': invalid resource ID.", id);
            }
            return null;
        }
    }

    public static ResourceLocation getEntityId(EntityLiving entity) {
        if (entity == null) {
            return null;
        }
        Class<?> entityClass = entity.getClass();
        ResourceLocation cached = ENTITY_IDS_BY_CLASS.get(entityClass);
        if (cached != null) {
            return cached;
        }
        if (UNREGISTERED_ENTITY_CLASSES.contains(entityClass)) {
            return null;
        }
        for (EntityEntry entry : ForgeRegistries.ENTITIES.getValuesCollection()) {
            if (entry.getEntityClass() == entityClass && entry.getRegistryName() != null) {
                ResourceLocation id = entry.getRegistryName();
                ENTITY_IDS_BY_CLASS.put(entityClass, id);
                return id;
            }
        }
        UNREGISTERED_ENTITY_CLASSES.add(entityClass);
        return null;
    }
}
