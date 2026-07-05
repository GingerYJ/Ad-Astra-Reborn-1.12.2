package earth.terrarium.adastra.common.util;

import earth.terrarium.adastra.AdAstraReborn;
import earth.terrarium.adastra.Reference;
import earth.terrarium.adastra.common.blocks.LaunchPadBlock;
import earth.terrarium.adastra.common.capability.AdAstraCapabilities;
import earth.terrarium.adastra.common.capability.AdAstraPlayer;
import earth.terrarium.adastra.common.capability.IAdAstraPlayer;
import earth.terrarium.adastra.common.capability.SpaceStation;
import earth.terrarium.adastra.common.entities.vehicles.AdAstraVehicleEntity;
import earth.terrarium.adastra.common.network.NetworkHandler;
import earth.terrarium.adastra.common.network.packet.PacketOpenPlanetSelection;
import earth.terrarium.adastra.common.network.packet.PacketSyncPlayerCapability;
import earth.terrarium.adastra.common.recipe.RecipeRegistry;
import earth.terrarium.adastra.common.recipe.SpaceStationRecipe;
import earth.terrarium.adastra.common.registry.ModBlocks;
import earth.terrarium.adastra.common.registry.ModDimensions;
import earth.terrarium.adastra.common.world.AdAstraOrbitWorldProvider;
import earth.terrarium.adastra.common.world.AdAstraStructureBlocks;
import earth.terrarium.adastra.common.world.PlanetDimensionProperties;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.datafix.FixTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.WorldServer;
import net.minecraft.world.gen.structure.template.PlacementSettings;
import net.minecraft.world.gen.structure.template.Template;
import net.minecraftforge.common.DimensionManager;

import java.io.InputStream;
import java.util.Set;

public final class SpaceStationHelper {

    private static final int CLEARANCE_Y = 14;
    private static final int SPACE_STATION_LAUNCH_PAD_Y = 124;
    private static final int SPACE_STATION_AREA_RADIUS = 40;

    private SpaceStationHelper() {
    }

    public static boolean constructSpaceStation(EntityPlayerMP player, int planetDimensionId, int rocketTier) {
        PlanetDimensionProperties planet = PlanetTravelHelper.getPlanetByDimensionId(planetDimensionId);
        if (!PlanetTravelHelper.canRocketTierReach(rocketTier, planet)) {
            player.sendStatusMessage(new TextComponentTranslation("message.ad_astra.planets.unreachable"), true);
            return false;
        }

        int orbitDimensionId = PlanetTravelHelper.getOrbitDimensionId(planetDimensionId);
        ResourceLocation orbitLocation = PlanetTravelHelper.getOrbitDimensionLocation(orbitDimensionId);
        if (orbitLocation == null || !DimensionManager.isDimensionRegistered(orbitDimensionId)) {
            player.sendStatusMessage(new TextComponentTranslation("message.ad_astra.space_station.unavailable"), true);
            return false;
        }
        SpaceStation existingStation = getOwnedSpaceStationInOrbit(player, orbitDimensionId);
        if (existingStation != null) {
            player.sendStatusMessage(new TextComponentTranslation("message.ad_astra.space_station.orbit_exists"), true);
            IAdAstraPlayer capability = AdAstraCapabilities.getPlayer(player);
            if (capability != null) {
                syncCapability(player, capability);
            }
            if (isPlayerInSpaceStationArea(player, orbitDimensionId, existingStation.getPosition())) {
                reopenPlanetSelection(player);
                return false;
            }
            return PlanetTravelHelper.landPlayerAtSpaceStation(player, orbitDimensionId, existingStation.getPosition());
        }

        MinecraftServer server = player.getServer();
        if (server == null) {
            return false;
        }
        WorldServer targetWorld = server.getWorld(orbitDimensionId);
        if (targetWorld == null) {
            player.sendStatusMessage(new TextComponentTranslation("message.ad_astra.space_station.unavailable"), true);
            return false;
        }

        SpaceStationRecipe recipe = RecipeRegistry.findSpaceStationRecipe(orbitLocation);
        if (recipe == null) {
            player.sendStatusMessage(new TextComponentTranslation("message.ad_astra.space_station.no_recipe"), true);
            return false;
        }
        if (!recipe.canCraft(player)) {
            player.sendStatusMessage(new TextComponentTranslation("message.ad_astra.space_station.missing_ingredients"), true);
            return false;
        }

        Template template = loadTemplate(targetWorld, recipe.getStructure());
        if (template == null) {
            player.sendStatusMessage(new TextComponentTranslation("message.ad_astra.space_station.template_missing"), true);
            return false;
        }

        int chunkX = MathHelper.floor(player.posX) >> 4;
        int chunkZ = MathHelper.floor(player.posZ) >> 4;
        BlockPos stationCenter = new BlockPos(chunkX * 16 + 8, AdAstraOrbitWorldProvider.STATION_Y, chunkZ * 16 + 8);
        BlockPos size = template.getSize();
        BlockPos origin = new BlockPos(
            stationCenter.getX() - size.getX() / 2,
            AdAstraOrbitWorldProvider.STATION_Y,
            stationCenter.getZ() - size.getZ() / 2);

        if (!isClear(targetWorld, origin, size)) {
            player.sendStatusMessage(new TextComponentTranslation("text.ad_astra.space_station.already_exists"), true);
            return false;
        }
        if (!recipe.consumeIngredients(player)) {
            player.sendStatusMessage(new TextComponentTranslation("message.ad_astra.space_station.missing_ingredients"), true);
            return false;
        }

        targetWorld.getChunk(stationCenter);
        PlacementSettings settings = new PlacementSettings()
            .setIgnoreEntities(false)
            .setIgnoreStructureBlock(true)
            .setReplacedBlock(Blocks.STRUCTURE_VOID);
        template.addBlocksToWorld(targetWorld, origin, settings, 2);
        BlockPos launchPadCenter = new BlockPos(stationCenter.getX(), SPACE_STATION_LAUNCH_PAD_Y, stationCenter.getZ());
        placeLaunchPad(targetWorld, launchPadCenter);

        String name = new TextComponentTranslation("text.ad_astra.text.space_station_name",
            getNextStationIndex(player, orbitDimensionId)).getUnformattedText();
        SpaceStation station = new SpaceStation(name, orbitDimensionId, stationCenter, player.getUniqueID());
        IAdAstraPlayer capability = AdAstraCapabilities.getPlayer(player);
        if (capability != null) {
            capability.addSpaceStation(station);
            syncCapability(player, capability);
        }

        player.sendMessage(new TextComponentTranslation("message.ad_astra.space_station.constructed", name));
        return PlanetTravelHelper.landPlayerAtSpaceStation(player, orbitDimensionId, stationCenter);
    }

    public static boolean landOnSpaceStation(EntityPlayerMP player, int orbitDimensionId, BlockPos stationPos) {
        if (!ownsSpaceStation(player, orbitDimensionId, stationPos)) {
            player.sendStatusMessage(new TextComponentTranslation("message.ad_astra.space_station.not_owned"), true);
            return false;
        }
        if (isPlayerInSpaceStationArea(player, orbitDimensionId, stationPos)) {
            player.sendStatusMessage(new TextComponentTranslation("message.ad_astra.space_station.already_here"), true);
            reopenPlanetSelection(player);
            return false;
        }
        if (!DimensionManager.isDimensionRegistered(orbitDimensionId)) {
            player.sendStatusMessage(new TextComponentTranslation("message.ad_astra.space_station.unavailable"), true);
            return false;
        }
        return PlanetTravelHelper.landPlayerAtSpaceStation(player, orbitDimensionId, stationPos);
    }

    private static Template loadTemplate(WorldServer world, String structure) {
        ResourceLocation location = new ResourceLocation(structure);
        String path = "/data/" + location.getNamespace() + "/structures/" + location.getPath() + ".nbt";
        try (InputStream stream = SpaceStationHelper.class.getResourceAsStream(path)) {
            if (stream == null) {
                return null;
            }

            NBTTagCompound tag = CompressedStreamTools.readCompressed(stream);
            tag = world.getMinecraftServer().getDataFixer().process(FixTypes.STRUCTURE, tag);
            if (Reference.MOD_ID.equals(location.getNamespace())) {
                AdAstraStructureBlocks.remapPalette(tag);
            }

            Template template = new Template();
            template.read(tag);
            return template;
        } catch (Exception exception) {
            AdAstraReborn.LOGGER.error("Failed to load space station template {}", structure, exception);
            return null;
        }
    }

    private static boolean isClear(WorldServer world, BlockPos origin, BlockPos size) {
        for (int x = 0; x < size.getX(); x++) {
            for (int z = 0; z < size.getZ(); z++) {
                for (int y = 0; y < Math.min(size.getY() + CLEARANCE_Y, 32); y++) {
                    BlockPos pos = origin.add(x, y, z);
                    if (!world.isAirBlock(pos)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private static void placeLaunchPad(WorldServer world, BlockPos center) {
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                BlockPos support = center.add(dx, -1, dz);
                world.setBlockState(support, ModBlocks.IRON_PLATING.getDefaultState(), 2);
                world.setBlockToAir(center.add(dx, 0, dz));
                world.setBlockToAir(center.add(dx, 1, dz));
            }
        }
        for (LaunchPadBlock.Part part : LaunchPadBlock.Part.values()) {
            world.setBlockState(getLaunchPadPartPos(center, part),
                ModBlocks.LAUNCH_PAD.getDefaultState().withProperty(LaunchPadBlock.PART, part), 3);
        }
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

    private static int getNextStationIndex(EntityPlayerMP player, int orbitDimensionId) {
        IAdAstraPlayer capability = AdAstraCapabilities.getPlayer(player);
        if (capability == null) {
            return 1;
        }
        int count = 0;
        for (SpaceStation station : capability.getSpaceStations()) {
            if (station.getDimension() == orbitDimensionId) {
                count++;
            }
        }
        return count + 1;
    }

    private static SpaceStation getOwnedSpaceStationInOrbit(EntityPlayerMP player, int orbitDimensionId) {
        IAdAstraPlayer capability = AdAstraCapabilities.getPlayer(player);
        if (capability == null) {
            return null;
        }
        for (SpaceStation station : capability.getSpaceStations()) {
            if (station.getDimension() == orbitDimensionId && station.getOwner().equals(player.getUniqueID())) {
                return station;
            }
        }
        return null;
    }

    private static boolean ownsSpaceStation(EntityPlayerMP player, int orbitDimensionId, BlockPos stationPos) {
        IAdAstraPlayer capability = AdAstraCapabilities.getPlayer(player);
        if (capability == null) {
            return false;
        }
        Set<SpaceStation> stations = capability.getSpaceStations();
        for (SpaceStation station : stations) {
            if (station.getDimension() == orbitDimensionId
                && station.getOwner().equals(player.getUniqueID())
                && sameColumn(station.getPosition(), stationPos)) {
                return true;
            }
        }
        return false;
    }

    private static boolean sameColumn(BlockPos first, BlockPos second) {
        return first != null
            && second != null
            && first.getX() == second.getX()
            && first.getZ() == second.getZ();
    }

    private static boolean isPlayerInSpaceStationArea(EntityPlayerMP player, int orbitDimensionId, BlockPos stationPos) {
        return player.dimension == orbitDimensionId
            && Math.abs(player.posX - (stationPos.getX() + 0.5D)) <= SPACE_STATION_AREA_RADIUS
            && Math.abs(player.posZ - (stationPos.getZ() + 0.5D)) <= SPACE_STATION_AREA_RADIUS;
    }

    private static void reopenPlanetSelection(EntityPlayerMP player) {
        IAdAstraPlayer capability = AdAstraCapabilities.getPlayer(player);
        if (capability != null) {
            syncCapability(player, capability);
        }
        NetworkHandler.CHANNEL.sendTo(new PacketOpenPlanetSelection(Math.max(1, getRocketTier(player))), player);
    }

    private static int getRocketTier(EntityPlayerMP player) {
        Entity riding = player.getRidingEntity();
        if (riding instanceof AdAstraVehicleEntity) {
            return ((AdAstraVehicleEntity) riding).getRocketTier();
        }
        return 1;
    }

    private static void syncCapability(EntityPlayerMP player, IAdAstraPlayer capability) {
        if (capability instanceof AdAstraPlayer) {
            NetworkHandler.CHANNEL.sendTo(new PacketSyncPlayerCapability((AdAstraPlayer) capability), player);
        }
    }
}
