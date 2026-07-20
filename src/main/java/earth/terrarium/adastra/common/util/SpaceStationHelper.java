package earth.terrarium.adastra.common.util;

import earth.terrarium.adastra.AdAstraReborn;
import earth.terrarium.adastra.Reference;
import earth.terrarium.adastra.common.blocks.LaunchPadBlock;
import earth.terrarium.adastra.common.entities.vehicles.AdAstraVehicleEntity;
import earth.terrarium.adastra.common.network.NetworkHandler;
import earth.terrarium.adastra.common.network.packet.PacketOpenPlanetSelection;
import earth.terrarium.adastra.common.network.packet.PacketSyncSpaceStation;
import earth.terrarium.adastra.common.recipe.RecipeRegistry;
import earth.terrarium.adastra.common.recipe.SpaceStationRecipe;
import earth.terrarium.adastra.common.registry.ModBlocks;
import earth.terrarium.adastra.common.registry.ModDimensions;
import earth.terrarium.adastra.common.world.AdAstraSpaceStationWorldProvider;
import earth.terrarium.adastra.common.world.AdAstraStructureBlocks;
import earth.terrarium.adastra.common.world.PlanetDimensionProperties;
import earth.terrarium.adastra.common.world.data.GlobalSpaceStationData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
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

/** Server-side operations for the single shared space station. */
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

        MinecraftServer server = player.getServer();
        if (server == null) {
            return false;
        }

        GlobalSpaceStationData data = GlobalSpaceStationData.get(server.getWorld(0));
        if (data.isConstructed()) {
            syncToClient(player, data);
            player.sendStatusMessage(new TextComponentTranslation("message.ad_astra.space_station.exists"), true);
            return landOnSpaceStation(player);
        }

        if (!DimensionManager.isDimensionRegistered(ModDimensions.SPACE_STATION_ID)) {
            player.sendStatusMessage(new TextComponentTranslation("message.ad_astra.space_station.unavailable"), true);
            return false;
        }

        SpaceStationRecipe recipe = RecipeRegistry.findSpaceStationRecipe();
        if (recipe == null) {
            player.sendStatusMessage(new TextComponentTranslation("message.ad_astra.space_station.no_recipe"), true);
            return false;
        }
        if (!recipe.canCraft(player)) {
            player.sendStatusMessage(new TextComponentTranslation("message.ad_astra.space_station.missing_ingredients"), true);
            return false;
        }

        WorldServer targetWorld = server.getWorld(ModDimensions.SPACE_STATION_ID);
        if (targetWorld == null) {
            player.sendStatusMessage(new TextComponentTranslation("message.ad_astra.space_station.unavailable"), true);
            return false;
        }

        Template template = loadTemplate(targetWorld, recipe.getStructure());
        if (template == null) {
            player.sendStatusMessage(new TextComponentTranslation("message.ad_astra.space_station.template_missing"), true);
            return false;
        }

        BlockPos stationCenter = new BlockPos(0, AdAstraSpaceStationWorldProvider.STATION_Y, 0);
        BlockPos size = template.getSize();
        BlockPos origin = new BlockPos(
            stationCenter.getX() - size.getX() / 2,
            stationCenter.getY(),
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
        placeLaunchPad(targetWorld, new BlockPos(
            stationCenter.getX(), SPACE_STATION_LAUNCH_PAD_Y, stationCenter.getZ()));

        data.construct(stationCenter, "space_station");
        syncToAll(server, data);
        player.sendMessage(new TextComponentTranslation("message.ad_astra.space_station.constructed",
            new TextComponentTranslation("text.ad_astra.text.space_station").getUnformattedText()));
        return landOnSpaceStation(player);
    }

    public static boolean landOnSpaceStation(EntityPlayerMP player) {
        MinecraftServer server = player.getServer();
        if (server == null) {
            return false;
        }

        GlobalSpaceStationData data = GlobalSpaceStationData.get(server.getWorld(0));
        syncToClient(player, data);
        if (!data.isConstructed()) {
            player.sendStatusMessage(new TextComponentTranslation("message.ad_astra.space_station.unavailable"), true);
            return false;
        }

        BlockPos stationPos = data.getPosition();
        if (isPlayerInSpaceStationArea(player, stationPos)) {
            player.sendStatusMessage(new TextComponentTranslation("message.ad_astra.space_station.already_here"), true);
            reopenPlanetSelection(player, data);
            return false;
        }
        return PlanetTravelHelper.landPlayerAtSpaceStation(player, stationPos);
    }

    public static void syncToClient(EntityPlayerMP player) {
        MinecraftServer server = player == null ? null : player.getServer();
        if (server != null) {
            syncToClient(player, GlobalSpaceStationData.get(server.getWorld(0)));
        }
    }

    public static void syncToClient(EntityPlayerMP player, GlobalSpaceStationData data) {
        if (player != null && data != null) {
            NetworkHandler.CHANNEL.sendTo(new PacketSyncSpaceStation(data), player);
        }
    }

    private static void syncToAll(MinecraftServer server, GlobalSpaceStationData data) {
        for (EntityPlayerMP player : server.getPlayerList().getPlayers()) {
            syncToClient(player, data);
        }
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
                    if (!world.isAirBlock(origin.add(x, y, z))) {
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

    private static boolean isPlayerInSpaceStationArea(EntityPlayerMP player, BlockPos stationPos) {
        return player.dimension == ModDimensions.SPACE_STATION_ID
            && Math.abs(player.posX - (stationPos.getX() + 0.5D)) <= SPACE_STATION_AREA_RADIUS
            && Math.abs(player.posZ - (stationPos.getZ() + 0.5D)) <= SPACE_STATION_AREA_RADIUS;
    }

    private static void reopenPlanetSelection(EntityPlayerMP player, GlobalSpaceStationData data) {
        syncToClient(player, data);
        NetworkHandler.CHANNEL.sendTo(new PacketOpenPlanetSelection(Math.max(1, getRocketTier(player))), player);
    }

    private static int getRocketTier(EntityPlayerMP player) {
        Entity riding = player.getRidingEntity();
        if (riding instanceof AdAstraVehicleEntity) {
            return ((AdAstraVehicleEntity) riding).getRocketTier();
        }
        return 1;
    }
}
