package earth.terrarium.adastra.common.commands;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
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
        return "/adastra <tps|help>";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
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
                tpsCommand.execute(server, sender, subArgs);
                break;
            case "help":
                showHelp(sender);
                break;
            default:
                throw new WrongUsageException(getUsage(sender));
        }
    }

    private void showHelp(ICommandSender sender) {
        sender.sendMessage(new TextComponentString(
            TextFormatting.GOLD + "=== Ad Astra Commands ===" + TextFormatting.RESET
        ));
        sender.sendMessage(new TextComponentString(
            TextFormatting.AQUA + "/adastra tps" + TextFormatting.GRAY + " - Show current TPS"
        ));
        sender.sendMessage(new TextComponentString(
            TextFormatting.AQUA + "/adastra tps detailed" + TextFormatting.GRAY + " - Show detailed performance stats"
        ));
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        if (args.length == 1) {
            return getListOfStringsMatchingLastWord(args, "tps", "help");
        } else if (args.length == 2 && "tps".equalsIgnoreCase(args[0])) {
            return tpsCommand.getTabCompletions(server, sender, new String[]{args[1]}, targetPos);
        }
        return Collections.emptyList();
    }
}
