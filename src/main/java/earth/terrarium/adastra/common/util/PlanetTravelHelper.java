package earth.terrarium.adastra.common.util;

import earth.terrarium.adastra.common.registry.ModDimensions;
import earth.terrarium.adastra.common.world.PlanetDimensionProperties;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.Teleporter;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;

public final class PlanetTravelHelper {

    public static final int LANDING_Y = 180;

    private static final PlanetDimensionProperties[] PLANETS = new PlanetDimensionProperties[]{
        ModDimensions.MOON_PROPERTIES,
        ModDimensions.MARS_PROPERTIES,
        ModDimensions.MERCURY_PROPERTIES,
        ModDimensions.VENUS_PROPERTIES,
        ModDimensions.GLACIO_PROPERTIES
    };

    private PlanetTravelHelper() {
    }

    public static PlanetDimensionProperties[] getPlanets() {
        return PLANETS.clone();
    }

    public static PlanetDimensionProperties getPlanetByDimensionId(int dimensionId) {
        for (PlanetDimensionProperties planet : PLANETS) {
            if (planet.getDimensionId() == dimensionId) {
                return planet;
            }
        }
        return null;
    }

    public static boolean canRocketTierReach(int rocketTier, PlanetDimensionProperties planet) {
        return planet != null && rocketTier >= planet.getTier();
    }

    public static boolean landPlayer(EntityPlayerMP player, int dimensionId, int rocketTier) {
        PlanetDimensionProperties planet = getPlanetByDimensionId(dimensionId);
        if (!canRocketTierReach(rocketTier, planet)) {
            player.sendStatusMessage(new TextComponentTranslation("message.ad_astra.planets.unreachable"), true);
            return false;
        }
        if (!DimensionManager.isDimensionRegistered(dimensionId)) {
            player.sendStatusMessage(new TextComponentTranslation("message.ad_astra.planets.unregistered"), true);
            return false;
        }

        MinecraftServer server = player.getServer();
        if (server == null) {
            return false;
        }

        WorldServer targetWorld = server.getWorld(dimensionId);
        if (targetWorld == null) {
            player.sendStatusMessage(new TextComponentTranslation("message.ad_astra.planets.unregistered"), true);
            return false;
        }

        double x = player.posX;
        double z = player.posZ;
        player.dismountRidingEntity();
        player.changeDimension(dimensionId, new FixedTeleporter(targetWorld, x, LANDING_Y, z));
        player.setPositionAndUpdate(x, LANDING_Y, z);
        player.fallDistance = 0.0F;
        player.sendStatusMessage(new TextComponentTranslation("message.ad_astra.planets.landed", planet.getName()), true);
        return true;
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
            BlockPos pos = new BlockPos(x, y, z);
            world.getChunk(pos);
        }
    }
}
