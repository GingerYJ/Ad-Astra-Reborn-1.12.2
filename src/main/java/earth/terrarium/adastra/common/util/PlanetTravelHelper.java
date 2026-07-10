package earth.terrarium.adastra.common.util;

import earth.terrarium.adastra.common.config.AdAstraConfig;
import earth.terrarium.adastra.common.config.ExternalDimensionConfig;
import earth.terrarium.adastra.common.registry.ModDimensions;
import earth.terrarium.adastra.common.blocks.LaunchPadBlock;
import earth.terrarium.adastra.common.entities.vehicles.LanderEntity;
import earth.terrarium.adastra.common.entities.vehicles.RocketEntity;
import earth.terrarium.adastra.common.registry.ModBlocks;
import earth.terrarium.adastra.common.world.PlanetDimensionProperties;
import earth.terrarium.adastra.common.world.custom.CustomPlanetDefinition;
import earth.terrarium.adastra.common.world.custom.CustomPlanetRegistry;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.Teleporter;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class PlanetTravelHelper {

    public static final int NETHER_DIMENSION_ID = -1;
    public static final int END_DIMENSION_ID = 1;
    public static final int LANDING_Y = 180;
    public static final int SPACE_STATION_LANDING_Y = 126;
    private static final int LANDING_CHUNK_PRELOAD_RADIUS = 2;
    private static final int SPACE_STATION_LANDING_SEARCH_RADIUS = 6;
    private static final ThreadLocal<RocketTravelContext> ROCKET_TRAVEL_CONTEXT = new ThreadLocal<>();

    public static final PlanetDimensionProperties EARTH_PROPERTIES = new PlanetDimensionProperties(
        "earth",
        0,
        "",
        Biomes.PLAINS,
        Blocks.GRASS.getDefaultState(),
        Blocks.DIRT.getDefaultState(),
        true,
        true,
        true,
        (short) 15,
        1.0F,
        16,
        0,
        24000,
        new net.minecraft.util.math.Vec3d(0.0D, 0.0D, 0.0D),
        new net.minecraft.util.math.Vec3d(0.5D, 0.7D, 1.0D));

    private static final PlanetDimensionProperties[] PLANETS = new PlanetDimensionProperties[]{
        ModDimensions.MOON_PROPERTIES,
        ModDimensions.MARS_PROPERTIES,
        ModDimensions.MERCURY_PROPERTIES,
        ModDimensions.VENUS_PROPERTIES,
        ModDimensions.GLACIO_PROPERTIES,
        ModDimensions.CERES_PROPERTIES,
        ModDimensions.PLUTO_PROPERTIES,
        ModDimensions.HAUMEA_PROPERTIES,
        ModDimensions.KUIPER_BELT_PROPERTIES,
        ModDimensions.IO_PROPERTIES,
        ModDimensions.EUROPA_PROPERTIES,
        ModDimensions.GANYMEDE_PROPERTIES,
        ModDimensions.CALLISTO_PROPERTIES,
        ModDimensions.ENCELADUS_PROPERTIES,
        ModDimensions.TITAN_PROPERTIES,
        ModDimensions.MIRANDA_PROPERTIES,
        ModDimensions.TRITON_PROPERTIES,
        ModDimensions.PHOBOS_PROPERTIES,
        ModDimensions.JUPITER_ORBIT_PROPERTIES,
        ModDimensions.BARNARDA_C_PROPERTIES,
        ModDimensions.BARNARDA_C1_PROPERTIES,
        ModDimensions.TAUCETI_F_PROPERTIES,
        ModDimensions.PROXIMA_B_PROPERTIES
    };

    private PlanetTravelHelper() {
    }

    public static PlanetDimensionProperties[] getPlanets() {
        List<PlanetDimensionProperties> planets = new ArrayList<>();
        for (PlanetDimensionProperties planet : PLANETS) {
            planets.add(planet);
        }
        planets.addAll(ExternalDimensionConfig.getPlanetProperties());
        planets.addAll(CustomPlanetRegistry.getPlanetProperties());
        return planets.toArray(new PlanetDimensionProperties[planets.size()]);
    }

    public static PlanetDimensionProperties getPlanetByDimensionId(int dimensionId) {
        if (dimensionId == EARTH_PROPERTIES.getDimensionId()) {
            return EARTH_PROPERTIES;
        }
        for (PlanetDimensionProperties planet : PLANETS) {
            if (planet.getDimensionId() == dimensionId) {
                return planet;
            }
        }
        ExternalDimensionConfig.ExternalDimensionEntry external = ExternalDimensionConfig.getEntry(dimensionId);
        if (external != null) {
            return external.toDimensionProperties();
        }
        PlanetDimensionProperties custom = CustomPlanetRegistry.getPlanetProperties(dimensionId);
        return custom;
    }

    public static PlanetDimensionProperties getPlanetByOrbitDimensionId(int orbitDimensionId) {
        if (orbitDimensionId == ModDimensions.EARTH_ORBIT_ID) {
            return EARTH_PROPERTIES;
        }
        for (PlanetDimensionProperties planet : getPlanets()) {
            if (getOrbitDimensionId(planet.getDimensionId()) == orbitDimensionId) {
                return planet;
            }
        }
        return null;
    }

    public static boolean canRocketTierReach(int rocketTier, PlanetDimensionProperties planet) {
        return planet != null && rocketTier >= getRequiredRocketTier(planet);
    }

    public static int getRequiredRocketTier(PlanetDimensionProperties planet) {
        if (planet == null) {
            return Integer.MAX_VALUE;
        }
        if (planet.getDimensionId() == 0) {
            return 0;
        }
        return PlanetTierOverrideRegistry.getPlanetTier(planet.getDimensionId(), planet.getTier());
    }

    public static boolean isRocketTravelInProgress(EntityPlayer player, int targetDimensionId) {
        RocketTravelContext context = ROCKET_TRAVEL_CONTEXT.get();
        return context != null
            && context.targetDimensionId == targetDimensionId
            && player != null
            && context.playerId.equals(player.getUniqueID());
    }

    public static boolean landPlayer(EntityPlayerMP player, int dimensionId, int rocketTier) {
        Entity vehicle = player.getRidingEntity();
        RocketEntity rocket = vehicle instanceof RocketEntity ? (RocketEntity) vehicle : null;
        ItemStack rocketStack = rocket == null ? ItemStack.EMPTY : rocket.getDropStack();
        return landPlayer(player, dimensionId, rocketTier, rocket, rocketStack);
    }

    public static boolean landPlayer(EntityPlayerMP player, int dimensionId, int rocketTier, RocketEntity rocket, ItemStack rocketStack) {
        PlanetDimensionProperties planet = getPlanetByDimensionId(dimensionId);
        if (!canRocketTierReach(rocketTier, planet)) {
            player.sendStatusMessage(new TextComponentTranslation("message.ad_astra.planets.unreachable"), true);
            return false;
        }
        if (dimensionId != 0 && !DimensionManager.isDimensionRegistered(dimensionId)) {
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

        BlockPos landingColumn = new BlockPos(player.posX, 0, player.posZ);
        preloadLandingChunks(targetWorld, landingColumn, LANDING_CHUNK_PRELOAD_RADIUS);
        BlockPos landingPos;
        if (dimensionId != 0) {
            landingPos = createReturnLaunchPad(targetWorld, landingColumn);
        } else {
            landingPos = findPlanetSurfaceLandingPos(targetWorld, landingColumn);
        }
        double x = landingPos.getX() + 0.5D;
        double y = landingPos.getY();
        double z = landingPos.getZ() + 0.5D;

        player.dismountRidingEntity();
        beginRocketTravel(player, dimensionId);
        try {
            player.changeDimension(dimensionId, new FixedTeleporter(targetWorld, x, y, z));
        } finally {
            clearRocketTravel();
        }
        player.setPositionAndUpdate(x, y, z);
        SpaceStationLandingProtection.protect(player, landingPos);
        player.fallDistance = 0.0F;
        player.motionX = 0.0D;
        player.motionY = 0.0D;
        player.motionZ = 0.0D;
        player.velocityChanged = true;

        LanderEntity lander = spawnLander(targetWorld, player, x, y, z);
        if (rocket != null) {
            transferRocketToLander(rocket, rocketStack, lander, player);
        }

        player.sendStatusMessage(new TextComponentTranslation("message.ad_astra.planets.landed", planet.getName()), true);
        if (dimensionId != 0) {
            player.sendMessage(new TextComponentTranslation("message.ad_astra.planets.return_hint"));
        }
        return true;
    }

    public static boolean landPlayerAtSpaceStation(EntityPlayerMP player, int dimensionId, BlockPos stationPos) {
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

        preloadLandingChunks(targetWorld, stationPos, LANDING_CHUNK_PRELOAD_RADIUS);
        BlockPos landingPos = findSpaceStationLandingPos(targetWorld, stationPos);
        double x = landingPos.getX() + 0.5D;
        double y = landingPos.getY();
        double z = landingPos.getZ() + 0.5D;

        Entity vehicle = player.getRidingEntity();
        RocketEntity rocket = vehicle instanceof RocketEntity ? (RocketEntity) vehicle : null;
        ItemStack rocketStack = rocket == null ? ItemStack.EMPTY : rocket.getDropStack();
        player.dismountRidingEntity();
        player.changeDimension(dimensionId, new FixedTeleporter(targetWorld, x, y, z));
        player.setPositionAndUpdate(x, y, z);
        SpaceStationLandingProtection.protect(player, landingPos);
        player.fallDistance = 0.0F;
        player.motionX = 0.0D;
        player.motionY = 0.0D;
        player.motionZ = 0.0D;
        player.velocityChanged = true;

        if (rocket != null) {
            transferRocketToLander(rocket, rocketStack, null, player);
        }

        player.sendStatusMessage(new TextComponentTranslation("message.ad_astra.space_station.landed"), true);
        return true;
    }

    public static int getOrbitDimensionId(int planetDimensionId) {
        if (planetDimensionId == 0) {
            return ModDimensions.EARTH_ORBIT_ID;
        }
        if (planetDimensionId == ModDimensions.MOON_ID) {
            return ModDimensions.MOON_ORBIT_ID;
        }
        if (planetDimensionId == ModDimensions.MARS_ID) {
            return ModDimensions.MARS_ORBIT_ID;
        }
        if (planetDimensionId == ModDimensions.MERCURY_ID) {
            return ModDimensions.MERCURY_ORBIT_ID;
        }
        if (planetDimensionId == ModDimensions.VENUS_ID) {
            return ModDimensions.VENUS_ORBIT_ID;
        }
        if (planetDimensionId == ModDimensions.GLACIO_ID) {
            return ModDimensions.GLACIO_ORBIT_ID;
        }
        if (planetDimensionId == ModDimensions.CERES_ID) {
            return ModDimensions.CERES_ORBIT_ID;
        }
        if (planetDimensionId == ModDimensions.PLUTO_ID) {
            return ModDimensions.PLUTO_ORBIT_ID;
        }
        if (planetDimensionId == ModDimensions.HAUMEA_ID) {
            return ModDimensions.HAUMEA_ORBIT_ID;
        }
        if (planetDimensionId == ModDimensions.IO_ID) {
            return ModDimensions.IO_ORBIT_ID;
        }
        if (planetDimensionId == ModDimensions.EUROPA_ID) {
            return ModDimensions.EUROPA_ORBIT_ID;
        }
        if (planetDimensionId == ModDimensions.GANYMEDE_ID) {
            return ModDimensions.GANYMEDE_ORBIT_ID;
        }
        if (planetDimensionId == ModDimensions.CALLISTO_ID) {
            return ModDimensions.CALLISTO_ORBIT_ID;
        }
        if (planetDimensionId == ModDimensions.ENCELADUS_ID) {
            return ModDimensions.ENCELADUS_ORBIT_ID;
        }
        if (planetDimensionId == ModDimensions.TITAN_ID) {
            return ModDimensions.TITAN_ORBIT_ID;
        }
        if (planetDimensionId == ModDimensions.MIRANDA_ID) {
            return ModDimensions.MIRANDA_ORBIT_ID;
        }
        if (planetDimensionId == ModDimensions.TRITON_ID) {
            return ModDimensions.TRITON_ORBIT_ID;
        }
        if (planetDimensionId == ModDimensions.PHOBOS_ID) {
            return ModDimensions.PHOBOS_ORBIT_ID;
        }
        if (planetDimensionId == ModDimensions.JUPITER_ORBIT_ID) {
            return ModDimensions.JUPITER_ORBIT_ID;
        }
        if (planetDimensionId == ModDimensions.BARNARDA_C_ID) {
            return ModDimensions.BARNARDA_C_ORBIT_ID;
        }
        if (planetDimensionId == ModDimensions.BARNARDA_C1_ID) {
            return ModDimensions.BARNARDA_C1_ORBIT_ID;
        }
        if (planetDimensionId == ModDimensions.TAUCETI_F_ID) {
            return ModDimensions.TAUCETI_F_ORBIT_ID;
        }
        if (planetDimensionId == ModDimensions.PROXIMA_B_ID) {
            return ModDimensions.PROXIMA_B_ORBIT_ID;
        }
        if (ExternalDimensionConfig.isExternalDimension(planetDimensionId)) {
            return Integer.MIN_VALUE;
        }
        CustomPlanetDefinition custom = CustomPlanetRegistry.getByDimensionId(planetDimensionId);
        if (custom != null) {
            return custom.getOrbitDimensionId();
        }
        return Integer.MIN_VALUE;
    }

    private static void beginRocketTravel(EntityPlayerMP player, int targetDimensionId) {
        ROCKET_TRAVEL_CONTEXT.set(new RocketTravelContext(player.getUniqueID(), targetDimensionId));
    }

    private static void clearRocketTravel() {
        ROCKET_TRAVEL_CONTEXT.remove();
    }

    public static ResourceLocation getOrbitDimensionLocation(int orbitDimensionId) {
        if (orbitDimensionId == ModDimensions.EARTH_ORBIT_ID) {
            return new ResourceLocation(earth.terrarium.adastra.Reference.MOD_ID, "earth_orbit");
        }
        if (orbitDimensionId == ModDimensions.MOON_ORBIT_ID) {
            return new ResourceLocation(earth.terrarium.adastra.Reference.MOD_ID, "moon_orbit");
        }
        if (orbitDimensionId == ModDimensions.MARS_ORBIT_ID) {
            return new ResourceLocation(earth.terrarium.adastra.Reference.MOD_ID, "mars_orbit");
        }
        if (orbitDimensionId == ModDimensions.MERCURY_ORBIT_ID) {
            return new ResourceLocation(earth.terrarium.adastra.Reference.MOD_ID, "mercury_orbit");
        }
        if (orbitDimensionId == ModDimensions.VENUS_ORBIT_ID) {
            return new ResourceLocation(earth.terrarium.adastra.Reference.MOD_ID, "venus_orbit");
        }
        if (orbitDimensionId == ModDimensions.GLACIO_ORBIT_ID) {
            return new ResourceLocation(earth.terrarium.adastra.Reference.MOD_ID, "glacio_orbit");
        }
        if (orbitDimensionId == ModDimensions.CERES_ORBIT_ID) {
            return new ResourceLocation(earth.terrarium.adastra.Reference.MOD_ID, "ceres_orbit");
        }
        if (orbitDimensionId == ModDimensions.PLUTO_ORBIT_ID) {
            return new ResourceLocation(earth.terrarium.adastra.Reference.MOD_ID, "pluto_orbit");
        }
        if (orbitDimensionId == ModDimensions.HAUMEA_ORBIT_ID) {
            return new ResourceLocation(earth.terrarium.adastra.Reference.MOD_ID, "haumea_orbit");
        }
        if (orbitDimensionId == ModDimensions.IO_ORBIT_ID) {
            return new ResourceLocation(earth.terrarium.adastra.Reference.MOD_ID, "io_orbit");
        }
        if (orbitDimensionId == ModDimensions.EUROPA_ORBIT_ID) {
            return new ResourceLocation(earth.terrarium.adastra.Reference.MOD_ID, "europa_orbit");
        }
        if (orbitDimensionId == ModDimensions.GANYMEDE_ORBIT_ID) {
            return new ResourceLocation(earth.terrarium.adastra.Reference.MOD_ID, "ganymede_orbit");
        }
        if (orbitDimensionId == ModDimensions.CALLISTO_ORBIT_ID) {
            return new ResourceLocation(earth.terrarium.adastra.Reference.MOD_ID, "callisto_orbit");
        }
        if (orbitDimensionId == ModDimensions.ENCELADUS_ORBIT_ID) {
            return new ResourceLocation(earth.terrarium.adastra.Reference.MOD_ID, "enceladus_orbit");
        }
        if (orbitDimensionId == ModDimensions.TITAN_ORBIT_ID) {
            return new ResourceLocation(earth.terrarium.adastra.Reference.MOD_ID, "titan_orbit");
        }
        if (orbitDimensionId == ModDimensions.MIRANDA_ORBIT_ID) {
            return new ResourceLocation(earth.terrarium.adastra.Reference.MOD_ID, "miranda_orbit");
        }
        if (orbitDimensionId == ModDimensions.TRITON_ORBIT_ID) {
            return new ResourceLocation(earth.terrarium.adastra.Reference.MOD_ID, "triton_orbit");
        }
        if (orbitDimensionId == ModDimensions.PHOBOS_ORBIT_ID) {
            return new ResourceLocation(earth.terrarium.adastra.Reference.MOD_ID, "phobos_orbit");
        }
        if (orbitDimensionId == ModDimensions.JUPITER_ORBIT_ID) {
            return new ResourceLocation(earth.terrarium.adastra.Reference.MOD_ID, "jupiter_orbit");
        }
        if (orbitDimensionId == ModDimensions.BARNARDA_C_ORBIT_ID) {
            return new ResourceLocation(earth.terrarium.adastra.Reference.MOD_ID, "barnarda_c_orbit");
        }
        if (orbitDimensionId == ModDimensions.BARNARDA_C1_ORBIT_ID) {
            return new ResourceLocation(earth.terrarium.adastra.Reference.MOD_ID, "barnarda_c1_orbit");
        }
        if (orbitDimensionId == ModDimensions.TAUCETI_F_ORBIT_ID) {
            return new ResourceLocation(earth.terrarium.adastra.Reference.MOD_ID, "tauceti_f_orbit");
        }
        if (orbitDimensionId == ModDimensions.PROXIMA_B_ORBIT_ID) {
            return new ResourceLocation(earth.terrarium.adastra.Reference.MOD_ID, "proxima_b_orbit");
        }
        for (CustomPlanetDefinition custom : CustomPlanetRegistry.getDefinitions()) {
            if (custom.getOrbitDimensionId() == orbitDimensionId) {
                return custom.getOrbitDimensionLocation();
            }
        }
        return null;
    }

    private static void preloadLandingChunks(WorldServer world, BlockPos center, int radius) {
        ChunkPos chunk = new ChunkPos(center);
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                world.getChunk(chunk.x + dx, chunk.z + dz);
            }
        }
    }

    private static BlockPos findSpaceStationLandingPos(WorldServer world, BlockPos stationPos) {
        for (int radius = 0; radius <= SPACE_STATION_LANDING_SEARCH_RADIUS; radius++) {
            for (int dx = -radius; dx <= radius; dx++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    if (radius > 0 && Math.abs(dx) != radius && Math.abs(dz) != radius) {
                        continue;
                    }
                    BlockPos standPos = findTopStandablePos(world, stationPos.getX() + dx, stationPos.getZ() + dz);
                    if (standPos != null) {
                        return standPos;
                    }
                }
            }
        }
        BlockPos platformCenter = new BlockPos(stationPos.getX(), SPACE_STATION_LANDING_Y - 1, stationPos.getZ());
        createEmergencyLandingPlatform(world, platformCenter);
        return platformCenter.up();
    }

    private static BlockPos findTopStandablePos(WorldServer world, int x, int z) {
        for (int y = 255; y >= 1; y--) {
            BlockPos surface = new BlockPos(x, y, z);
            BlockPos feet = surface.up();
            if (isLandingSurface(world, surface) && isOpenForPlayer(world, feet) && isOpenForPlayer(world, feet.up())) {
                return feet;
            }
        }
        return null;
    }

    private static boolean isLandingSurface(WorldServer world, BlockPos pos) {
        IBlockState state = world.getBlockState(pos);
        return !state.getBlock().isAir(state, world, pos) && state.getMaterial().blocksMovement();
    }

    private static boolean isOpenForPlayer(WorldServer world, BlockPos pos) {
        IBlockState state = world.getBlockState(pos);
        return state.getBlock().isAir(state, world, pos) || !state.getMaterial().blocksMovement();
    }

    private static BlockPos findPlanetSurfaceLandingPos(WorldServer world, BlockPos landingColumn) {
        BlockPos landingPos = world.getHeight(landingColumn);
        if (landingPos.getY() < 2) {
            landingPos = new BlockPos(landingPos.getX(), 2, landingPos.getZ());
        }
        clearLandingSpace(world, landingPos);
        return landingPos;
    }

    private static void createEmergencyLandingPlatform(WorldServer world, BlockPos center) {
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                BlockPos floor = center.add(dx, 0, dz);
                world.setBlockState(floor, ModBlocks.IRON_PLATING.getDefaultState(), 3);
                clearLandingSpace(world, floor.up());
            }
        }
    }

    private static BlockPos createReturnLaunchPad(WorldServer world, BlockPos landingColumn) {
        BlockPos center = world.getHeight(landingColumn);
        if (center.getY() < 2) {
            center = new BlockPos(center.getX(), 2, center.getZ());
        }
        IBlockState supportState = getLandingSupportState(world, center);

        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                BlockPos support = center.add(dx, -1, dz);
                world.setBlockState(support, supportState, 2);
                for (int dy = 0; dy <= 3; dy++) {
                    world.setBlockToAir(center.add(dx, dy, dz));
                }
            }
        }

        for (LaunchPadBlock.Part part : LaunchPadBlock.Part.values()) {
            world.setBlockState(getLaunchPadPartPos(center, part),
                ModBlocks.LAUNCH_PAD.getDefaultState().withProperty(LaunchPadBlock.PART, part), 3);
        }
        BlockPos landingPos = center.up();
        clearLandingSpace(world, landingPos);
        return landingPos;
    }

    private static void clearLandingSpace(WorldServer world, BlockPos feetPos) {
        world.setBlockToAir(feetPos);
        world.setBlockToAir(feetPos.up());
        world.setBlockToAir(feetPos.up(2));
    }

    private static IBlockState getLandingSupportState(WorldServer world, BlockPos center) {
        if (ExternalDimensionConfig.isExternalDimension(world.provider.getDimension())) {
            BlockPos below = center.down();
            if (below.getY() >= 0) {
                IBlockState state = world.getBlockState(below);
                if (!state.getBlock().isAir(state, world, below) && state.getMaterial().blocksMovement()) {
                    return state;
                }
            }
        }
        PlanetDimensionProperties properties = getPlanetByDimensionId(world.provider.getDimension());
        return properties == null ? Blocks.STONE.getDefaultState() : properties.getFillerBlock();
    }

    private static BlockPos getLaunchPadPartPos(BlockPos center, LaunchPadBlock.Part part) {
        switch (part) {
            case TOP_LEFT:
                return center.add(-1, 0, 1);
            case TOP:
                return center.add(0, 0, 1);
            case TOP_RIGHT:
                return center.add(1, 0, 1);
            case LEFT:
                return center.add(-1, 0, 0);
            case RIGHT:
                return center.add(1, 0, 0);
            case BOTTOM_LEFT:
                return center.add(-1, 0, -1);
            case BOTTOM:
                return center.add(0, 0, -1);
            case BOTTOM_RIGHT:
                return center.add(1, 0, -1);
            case CENTER:
            default:
                return center;
        }
    }

    private static LanderEntity spawnLander(WorldServer world, EntityPlayerMP player, double x, double y, double z) {
        LanderEntity lander = new LanderEntity(world);
        lander.setLocationAndAngles(x, y, z, player.rotationYaw, 0.0F);
        if (world.spawnEntity(lander)) {
            player.startRiding(lander, true);
            return lander;
        }
        return null;
    }

    private static void transferRocketToLander(RocketEntity rocket, ItemStack rocketStack, LanderEntity lander, EntityPlayerMP player) {
        if (lander != null) {
            if (!rocketStack.isEmpty()) {
                lander.getInventory().set(0, rocketStack);
            }
            for (int i = 0; i < rocket.getInventory().size() && i + 1 < lander.getInventory().size(); i++) {
                ItemStack stack = rocket.getInventory().get(i);
                if (!stack.isEmpty()) {
                    lander.getInventory().set(i + 1, stack.copy());
                    rocket.getInventory().set(i, ItemStack.EMPTY);
                }
            }
        }
        if (lander == null && !rocketStack.isEmpty() && !player.inventory.addItemStackToInventory(rocketStack)) {
            player.dropItem(rocketStack, false);
        }
        for (int i = 0; i < rocket.getInventory().size(); i++) {
            ItemStack stack = rocket.getInventory().get(i);
            if (!stack.isEmpty()) {
                ItemStack copy = stack.copy();
                if (!player.inventory.addItemStackToInventory(copy)) {
                    player.dropItem(copy, false);
                }
                rocket.getInventory().set(i, ItemStack.EMPTY);
            }
        }
        rocket.setDead();
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

    private static final class RocketTravelContext {

        private final UUID playerId;
        private final int targetDimensionId;

        private RocketTravelContext(UUID playerId, int targetDimensionId) {
            this.playerId = playerId;
            this.targetDimensionId = targetDimensionId;
        }
    }
}


