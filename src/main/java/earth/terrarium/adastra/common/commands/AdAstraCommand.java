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
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
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
        return "/adastra <tps|planets|radio|help>";
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
            return getListOfStringsMatchingLastWord(args, "tps", "planets", "radio", "help");
        } else if (args.length == 2 && "tps".equalsIgnoreCase(args[0])) {
            return tpsCommand.getTabCompletions(server, sender, new String[]{args[1]}, targetPos);
        } else if (args.length == 2 && "radio".equalsIgnoreCase(args[0])) {
            return getListOfStringsMatchingLastWord(args, "refresh");
        }
        return Collections.emptyList();
    }
}
