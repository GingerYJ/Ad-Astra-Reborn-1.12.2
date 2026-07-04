package earth.terrarium.adastra.common.handler;

import earth.terrarium.adastra.api.planets.Planet;
import earth.terrarium.adastra.api.planets.PlanetApi;
import earth.terrarium.adastra.common.planets.PlanetApiImpl;
import earth.terrarium.adastra.common.util.PlanetTravelHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Teleporter;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Handles entities falling out of orbit dimensions before vanilla void handling kills them.
 */
public class SpaceBoundaryHandler {

    private static final double OUT_OF_WORLD_Y = -64.0D;

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onWorldTick(TickEvent.WorldTickEvent event) {
        if (event.phase != TickEvent.Phase.START || event.world == null || event.world.isRemote) {
            return;
        }

        World world = event.world;
        if (!(world instanceof WorldServer) || !PlanetApi.API.isSpace(world)) {
            return;
        }

        Integer targetDimension = getSurfaceDimensionForOrbit(world.provider.getDimension());
        if (targetDimension == null || !DimensionManager.isDimensionRegistered(targetDimension)) {
            return;
        }

        List<Entity> entities = new ArrayList<>(world.loadedEntityList);
        for (Entity entity : entities) {
            if (!shouldTeleportRootEntity(entity, world)) {
                continue;
            }
            teleportEntityFromOrbit(entity, targetDimension);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof EntityPlayerMP) || entity.world == null || entity.world.isRemote) {
            return;
        }
        Entity rootEntity = getRootEntity(entity);
        if (rootEntity.posY >= OUT_OF_WORLD_Y) {
            return;
        }

        World world = rootEntity.world;
        if (!PlanetApi.API.isSpace(world)) {
            return;
        }

        Integer targetDimension = getSurfaceDimensionForOrbit(world.provider.getDimension());
        if (targetDimension == null || !DimensionManager.isDimensionRegistered(targetDimension)) {
            return;
        }

        teleportEntityFromOrbit(rootEntity, targetDimension);
    }

    private boolean shouldTeleportRootEntity(Entity entity, World expectedWorld) {
        return entity != null
            && !entity.isDead
            && entity.world == expectedWorld
            && entity.getRidingEntity() == null
            && entity.posY < OUT_OF_WORLD_Y;
    }

    private Entity getRootEntity(Entity entity) {
        Entity root = entity;
        while (root.getRidingEntity() != null && root.getRidingEntity() != root) {
            root = root.getRidingEntity();
        }
        return root;
    }

    private Integer getSurfaceDimensionForOrbit(int orbitDimension) {
        for (Map.Entry<Integer, Planet> entry : PlanetApiImpl.snapshotPlanets().entrySet()) {
            Planet planet = entry.getValue();
            Integer planetOrbit = planet.getOrbitDimensionId();
            if (planetOrbit != null && planetOrbit == orbitDimension) {
                return planet.getDimensionId();
            }
        }
        return null;
    }

    private Entity teleportEntityFromOrbit(Entity entity, int targetDimension) {
        MinecraftServer server = entity instanceof EntityPlayerMP
            ? ((EntityPlayerMP) entity).getServer()
            : entity.world.getMinecraftServer();
        if (server == null) {
            return null;
        }

        WorldServer targetWorld = server.getWorld(targetDimension);
        if (targetWorld == null) {
            return null;
        }

        double x = entity.posX;
        double y = PlanetTravelHelper.LANDING_Y;
        double z = entity.posZ;
        List<Entity> passengers = new ArrayList<>(entity.getPassengers());
        for (Entity passenger : passengers) {
            passenger.dismountRidingEntity();
        }
        entity.dismountRidingEntity();
        entity.setPosition(x, y, z);

        Entity teleportedEntity = changeDimension(entity, targetDimension, targetWorld, x, y, z);
        if (teleportedEntity == null) {
            return null;
        }

        for (Entity passenger : passengers) {
            Entity teleportedPassenger = teleportEntityFromOrbit(passenger, targetDimension);
            if (teleportedPassenger != null) {
                teleportedPassenger.startRiding(teleportedEntity, true);
            }
        }
        return teleportedEntity;
    }

    private Entity changeDimension(Entity entity, int targetDimension, WorldServer targetWorld, double x, double y, double z) {
        Entity teleportedEntity;
        if (entity instanceof EntityPlayerMP) {
            EntityPlayerMP player = (EntityPlayerMP) entity;
            player.changeDimension(targetDimension, new FixedTeleporter(targetWorld, x, y, z));
            player.setPositionAndUpdate(x, y, z);
            teleportedEntity = player;
        } else {
            teleportedEntity = entity.changeDimension(targetDimension, new FixedTeleporter(targetWorld, x, y, z));
            if (teleportedEntity != null) {
                teleportedEntity.setLocationAndAngles(x, y, z, entity.rotationYaw, entity.rotationPitch);
            }
        }
        resetMotion(teleportedEntity);
        return teleportedEntity;
    }

    private void resetMotion(Entity entity) {
        if (entity == null) {
            return;
        }
        entity.fallDistance = 0.0F;
        entity.motionX = 0.0D;
        entity.motionY = 0.0D;
        entity.motionZ = 0.0D;
        entity.velocityChanged = true;
    }

    private static final class FixedTeleporter extends Teleporter {

        private final WorldServer world;
        private final double x;
        private final double y;
        private final double z;

        private FixedTeleporter(WorldServer world, double x, double y, double z) {
            super(world);
            this.world = world;
            this.x = x;
            this.y = y;
            this.z = z;
        }

        @Override
        public void placeInPortal(Entity entity, float rotationYaw) {
            entity.setLocationAndAngles(x, y, z, entity.rotationYaw, entity.rotationPitch);
            entity.motionX = 0.0D;
            entity.motionY = 0.0D;
            entity.motionZ = 0.0D;
            world.getChunk(new BlockPos(x, y, z));
        }
    }
}
