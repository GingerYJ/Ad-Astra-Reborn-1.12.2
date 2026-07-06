package earth.terrarium.adastra.common.commands;

import earth.terrarium.adastra.common.entities.vehicles.AdAstraVehicleEntity;
import earth.terrarium.adastra.common.network.NetworkHandler;
import earth.terrarium.adastra.common.network.packet.PacketOpenPlanetSelection;
import earth.terrarium.adastra.common.util.radio.StationLoader;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Teleporter;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Root command for Ad Astra: /adastra
 * Handles subcommands like tps, performance, etc.
 */
public class AdAstraCommand extends CommandBase {

    private final TpsCommand tpsCommand = new TpsCommand();

    @Override
    public String getName() {
        return "adastra";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/adastra <tps|planets|setdimension|tpdim|radio|help>";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length == 0) {
            throw new WrongUsageException(getUsage(sender));
        }

        String subCommand = args[0].toLowerCase();
        String[] subArgs = Arrays.copyOfRange(args, 1, args.length);

        switch (subCommand) {
            case "tps":
                requirePermission(sender, tpsCommand.getRequiredPermissionLevel());
                tpsCommand.execute(server, sender, subArgs);
                break;
            case "planets":
                openPlanets(sender, subArgs);
                break;
            case "setdimension":
            case "tpdim":
            case "dimtp":
                teleportToDimension(server, sender, subArgs);
                break;
            case "radio":
                handleRadio(sender, subArgs);
                break;
            case "help":
                showHelp(sender);
                break;
            default:
                throw new WrongUsageException(getUsage(sender));
        }
    }

    private void openPlanets(ICommandSender sender, String[] args) throws CommandException {
        if (args.length != 0) {
            throw new WrongUsageException("/adastra planets");
        }
        requirePermission(sender, 2);
        if (!(sender instanceof EntityPlayerMP)) {
            throw new CommandException("commands.generic.player.unspecified");
        }

        EntityPlayerMP player = (EntityPlayerMP) sender;
        NetworkHandler.CHANNEL.sendTo(new PacketOpenPlanetSelection(resolveRocketTier(player)), player);
    }


    private void teleportToDimension(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        requirePermission(sender, 2);
        if (!(sender instanceof EntityPlayerMP)) {
            throw new CommandException("commands.generic.player.unspecified");
        }
        if (args.length != 1 && args.length != 4) {
            throw new WrongUsageException("/adastra setdimension <dimensionId> [x y z]");
        }

        int dimensionId = parseInt(args[0]);
        if (!DimensionManager.isDimensionRegistered(dimensionId)) {
            throw new CommandException("Dimension %s is not registered.", dimensionId);
        }

        EntityPlayerMP player = (EntityPlayerMP) sender;
        WorldServer targetWorld = server.getWorld(dimensionId);
        if (targetWorld == null) {
            throw new CommandException("Dimension %s is registered but could not be loaded.", dimensionId);
        }

        double x = player.posX;
        double y;
        double z = player.posZ;
        if (args.length == 4) {
            x = parseCoordinateDouble(args[1]);
            y = parseCoordinateDouble(args[2]);
            z = parseCoordinateDouble(args[3]);
            clearPlayerSpace(targetWorld, new BlockPos(x, y, z));
        } else {
            BlockPos landing = findSafeLandingPos(targetWorld, x, z);
            x = landing.getX() + 0.5D;
            y = landing.getY();
            z = landing.getZ() + 0.5D;
        }

        player.dismountRidingEntity();
        if (player.dimension != dimensionId) {
            player.changeDimension(dimensionId, new FixedCommandTeleporter(targetWorld, x, y, z));
        }
        player.setPositionAndUpdate(x, y, z);
        player.fallDistance = 0.0F;
        player.motionX = 0.0D;
        player.motionY = 0.0D;
        player.motionZ = 0.0D;
        player.velocityChanged = true;

        sender.sendMessage(new TextComponentString(
            TextFormatting.GREEN + "Teleported to dimension " + dimensionId
                + TextFormatting.GRAY + " at "
                + String.format(java.util.Locale.ROOT, "%.1f %.1f %.1f", x, y, z)
        ));
    }

    private BlockPos findSafeLandingPos(WorldServer world, double x, double z) {
        BlockPos column = new BlockPos(x, 0, z);
        world.getChunk(column);
        BlockPos landing = world.getHeight(column);
        if (landing.getY() < 2) {
            landing = new BlockPos(column.getX(), 81, column.getZ());
            createEmergencyPlatform(world, landing.down());
        } else if (world.isAirBlock(landing.down())) {
            world.setBlockState(landing.down(), Blocks.STONE.getDefaultState(), 3);
        }
        clearPlayerSpace(world, landing);
        return landing;
    }

    private void createEmergencyPlatform(WorldServer world, BlockPos center) {
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                world.setBlockState(center.add(dx, 0, dz), Blocks.STONE.getDefaultState(), 3);
            }
        }
    }

    private void clearPlayerSpace(WorldServer world, BlockPos feet) {
        world.setBlockToAir(feet);
        world.setBlockToAir(feet.up());
        world.setBlockToAir(feet.up(2));
    }

    private double parseCoordinateDouble(String value) throws CommandException {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            throw new CommandException("Invalid coordinate: %s", value);
        }
    }

    private void handleRadio(ICommandSender sender, String[] args) throws CommandException {
        if (args.length != 1 || !"refresh".equalsIgnoreCase(args[0])) {
            throw new WrongUsageException("/adastra radio refresh");
        }

        if (System.getProperty("adastra.stations") == null) {
            sender.sendMessage(new TextComponentString(
                TextFormatting.YELLOW + "No adastra.stations system property is set; reloading bundled stations."
            ));
        }

        StationLoader.reload();
        int stationCount = StationLoader.stations().size();
        sender.sendMessage(new TextComponentString(
            TextFormatting.GREEN + "Reloaded " + stationCount + " Ad Astra radio station(s)."
        ));
    }

    private int resolveRocketTier(EntityPlayerMP player) {
        Entity riding = player.getRidingEntity();
        if (riding instanceof AdAstraVehicleEntity) {
            return Math.max(1, ((AdAstraVehicleEntity) riding).getRocketTier());
        }
        return 1;
    }

    private void showHelp(ICommandSender sender) {
        sender.sendMessage(new TextComponentString(
            TextFormatting.GOLD + "=== Ad Astra Commands ===" + TextFormatting.RESET
        ));
        sender.sendMessage(new TextComponentString(
            TextFormatting.AQUA + "/adastra planets" + TextFormatting.GRAY + " - Open planet selection"
        ));
        sender.sendMessage(new TextComponentString(
            TextFormatting.AQUA + "/adastra setdimension <id> [x y z]" + TextFormatting.GRAY + " - Teleport to a dimension for testing"
        ));
        sender.sendMessage(new TextComponentString(
            TextFormatting.AQUA + "/adastra radio refresh" + TextFormatting.GRAY + " - Reload radio stations"
        ));
        sender.sendMessage(new TextComponentString(
            TextFormatting.AQUA + "/adastra tps" + TextFormatting.GRAY + " - Show current TPS"
        ));
        sender.sendMessage(new TextComponentString(
            TextFormatting.AQUA + "/adastra tps detailed" + TextFormatting.GRAY + " - Show detailed performance stats"
        ));
    }

    private void requirePermission(ICommandSender sender, int permissionLevel) throws CommandException {
        if (!sender.canUseCommand(permissionLevel, getName())) {
            throw new CommandException("commands.generic.permission");
        }
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        if (args.length == 1) {
            return getListOfStringsMatchingLastWord(args, "tps", "planets", "setdimension", "tpdim", "dimtp", "radio", "help");
        } else if (args.length == 2 && "tps".equalsIgnoreCase(args[0])) {
            return tpsCommand.getTabCompletions(server, sender, new String[]{args[1]}, targetPos);
        } else if (args.length == 2 && "radio".equalsIgnoreCase(args[0])) {
            return getListOfStringsMatchingLastWord(args, "refresh");
        }
        return Collections.emptyList();
    }
    private static final class FixedCommandTeleporter extends Teleporter {

        private final WorldServer world;
        private final double x;
        private final double y;
        private final double z;

        private FixedCommandTeleporter(WorldServer world, double x, double y, double z) {
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
