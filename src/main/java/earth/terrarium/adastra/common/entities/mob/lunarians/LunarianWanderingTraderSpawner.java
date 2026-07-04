package earth.terrarium.adastra.common.entities.mob.lunarians;

import earth.terrarium.adastra.api.planets.PlanetApi;
import earth.terrarium.adastra.common.entities.mob.LunarianWanderingTraderEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.List;
import java.util.Random;

public class LunarianWanderingTraderSpawner {

    private static final String DATA_NAME = "adastra_lunarian_wandering_trader";
    private static final int DEFAULT_SPAWN_DELAY = 24000;
    private static final int DEFAULT_SPAWN_TIMER = 1200;
    private static final int SPAWN_RANGE = 48;
    private static final int WANDER_RANGE = 16;
    private static final int DESPAWN_DELAY = 48000;

    @SubscribeEvent
    public void onWorldTick(TickEvent.WorldTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.world.isRemote || !(event.world instanceof WorldServer)) {
            return;
        }

        WorldServer world = (WorldServer) event.world;
        if (!shouldTick(world)) {
            return;
        }

        SpawnState.tick(world);
    }

    private boolean shouldTick(WorldServer world) {
        return world.provider != null
            && world.provider.getDimension() != 0
            && PlanetApi.API.isPlanet(world)
            && world.getGameRules().getBoolean("doMobSpawning");
    }

    private static class SpawnState {

        private static final Random RANDOM = new Random();

        private static void tick(WorldServer world) {
            SpawnData data = SpawnData.get(world);
            if (--data.spawnTimer > 0) {
                data.markDirty();
                return;
            }

            data.spawnTimer = DEFAULT_SPAWN_TIMER;
            data.spawnDelay -= DEFAULT_SPAWN_TIMER;
            data.markDirty();
            if (data.spawnDelay > 0) {
                return;
            }

            data.spawnDelay = DEFAULT_SPAWN_DELAY;
            int chance = data.spawnChance;
            data.spawnChance = Math.min(75, Math.max(25, data.spawnChance + 25));
            data.markDirty();
            if (RANDOM.nextInt(100) > chance) {
                return;
            }

            if (trySpawn(world)) {
                data.spawnChance = 25;
                data.markDirty();
            }
        }

        private static boolean trySpawn(WorldServer world) {
            if (hasExistingTrader(world)) {
                return false;
            }

            EntityPlayer player = randomPlayer(world);
            if (player == null) {
                return false;
            }
            if (RANDOM.nextInt(10) != 0) {
                return false;
            }

            BlockPos target = player.getPosition();
            BlockPos spawnPos = nearbySpawnPos(world, target);
            if (spawnPos == null) {
                return false;
            }

            LunarianWanderingTraderEntity trader = new LunarianWanderingTraderEntity(world);
            trader.setLocationAndAngles(
                spawnPos.getX() + 0.5D,
                spawnPos.getY(),
                spawnPos.getZ() + 0.5D,
                RANDOM.nextFloat() * 360.0F,
                0.0F);
            trader.onInitialSpawn(world.getDifficultyForLocation(spawnPos), null);
            trader.setHomePosAndDistance(target, WANDER_RANGE);
            trader.setDespawnDelay(DESPAWN_DELAY);
            return world.spawnEntity(trader);
        }

        private static boolean hasExistingTrader(WorldServer world) {
            for (Entity entity : world.loadedEntityList) {
                if (entity instanceof LunarianWanderingTraderEntity && entity.isEntityAlive()) {
                    return true;
                }
            }
            return false;
        }

        private static EntityPlayer randomPlayer(WorldServer world) {
            List<EntityPlayer> players = world.playerEntities;
            return players.isEmpty() ? null : players.get(RANDOM.nextInt(players.size()));
        }

        private static BlockPos nearbySpawnPos(WorldServer world, BlockPos center) {
            for (int i = 0; i < 10; i++) {
                int x = center.getX() + RANDOM.nextInt(SPAWN_RANGE * 2 + 1) - SPAWN_RANGE;
                int z = center.getZ() + RANDOM.nextInt(SPAWN_RANGE * 2 + 1) - SPAWN_RANGE;
                BlockPos top = world.getTopSolidOrLiquidBlock(new BlockPos(x, 0, z));
                if (canSpawnAt(world, top)) {
                    return top;
                }
            }
            return null;
        }

        private static boolean canSpawnAt(WorldServer world, BlockPos pos) {
            return world.isBlockLoaded(pos)
                && world.isAirBlock(pos)
                && world.isAirBlock(pos.up())
                && world.getBlockState(pos.down()).isSideSolid(world, pos.down(), EnumFacing.UP);
        }
    }

    public static class SpawnData extends WorldSavedData {

        private int spawnDelay = DEFAULT_SPAWN_DELAY;
        private int spawnChance = 25;
        private int spawnTimer = DEFAULT_SPAWN_TIMER;

        public SpawnData(String name) {
            super(name);
        }

        private static SpawnData get(WorldServer world) {
            String name = DATA_NAME + "_" + world.provider.getDimension();
            MapStorage storage = world.getMapStorage();
            SpawnData data = (SpawnData) storage.getOrLoadData(SpawnData.class, name);
            if (data == null) {
                data = new SpawnData(name);
                storage.setData(name, data);
                data.markDirty();
            }
            return data;
        }

        @Override
        public void readFromNBT(NBTTagCompound nbt) {
            spawnDelay = nbt.hasKey("SpawnDelay") ? nbt.getInteger("SpawnDelay") : DEFAULT_SPAWN_DELAY;
            spawnChance = nbt.hasKey("SpawnChance") ? nbt.getInteger("SpawnChance") : 25;
            spawnTimer = nbt.hasKey("SpawnTimer") ? nbt.getInteger("SpawnTimer") : DEFAULT_SPAWN_TIMER;
            spawnChance = Math.min(75, Math.max(25, spawnChance));
            spawnTimer = Math.min(DEFAULT_SPAWN_TIMER, Math.max(1, spawnTimer));
        }

        @Override
        public NBTTagCompound writeToNBT(NBTTagCompound compound) {
            compound.setInteger("SpawnDelay", spawnDelay);
            compound.setInteger("SpawnChance", spawnChance);
            compound.setInteger("SpawnTimer", spawnTimer);
            return compound;
        }
    }
}
