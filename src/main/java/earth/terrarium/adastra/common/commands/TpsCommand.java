package earth.terrarium.adastra.common.commands;

import earth.terrarium.adastra.common.performance.PerformanceTracker;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

/**
 * /adastra tps command for monitoring server performance.
 * Shows current TPS, per-system timing, and warns when TPS drops below threshold.
 */
public class TpsCommand extends CommandBase {

    @Override
    public String getName() {
        return "tps";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/adastra tps [detailed]";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2; // Op level 2
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        boolean detailed = args.length > 0 && "detailed".equalsIgnoreCase(args[0]);

        // Get current TPS
        double tps = PerformanceTracker.getCurrentTPS();
        long averageTickTime = PerformanceTracker.getAverageTickTime();

        // Format TPS with color based on performance
        TextFormatting tpsColor;
        if (tps >= 19.5) {
            tpsColor = TextFormatting.GREEN;
        } else if (tps >= 18.0) {
            tpsColor = TextFormatting.YELLOW;
        } else {
            tpsColor = TextFormatting.RED;
        }

        sender.sendMessage(new TextComponentString(
            TextFormatting.GOLD + "=== Ad Astra Performance ===" + TextFormatting.RESET
        ));

        sender.sendMessage(new TextComponentString(
            TextFormatting.AQUA + "TPS: " + tpsColor + String.format("%.2f", tps) +
            TextFormatting.GRAY + " (" + averageTickTime + "ms avg tick)"
        ));

        if (detailed) {
            sender.sendMessage(new TextComponentString(
                TextFormatting.GOLD + "\nPer-System Timing:" + TextFormatting.RESET
            ));

            // Get timing data for each system
            long machineTime = PerformanceTracker.getSystemTime("machines");
            long environmentTime = PerformanceTracker.getSystemTime("environment");
            long networkTime = PerformanceTracker.getSystemTime("network");
            long mobTime = PerformanceTracker.getSystemTime("mobs");

            sender.sendMessage(new TextComponentString(
                TextFormatting.GRAY + "  Machines: " + formatTime(machineTime)
            ));
            sender.sendMessage(new TextComponentString(
                TextFormatting.GRAY + "  Environment: " + formatTime(environmentTime)
            ));
            sender.sendMessage(new TextComponentString(
                TextFormatting.GRAY + "  Network: " + formatTime(networkTime)
            ));
            sender.sendMessage(new TextComponentString(
                TextFormatting.GRAY + "  Mobs: " + formatTime(mobTime)
            ));

            // Show active machine count
            int activeMachines = PerformanceTracker.getActiveMachineCount();
            sender.sendMessage(new TextComponentString(
                TextFormatting.AQUA + "\nActive Machines: " + TextFormatting.WHITE + activeMachines
            ));
        }

        // Warn if TPS is low
        if (tps < 18.0) {
            sender.sendMessage(new TextComponentString(
                TextFormatting.RED + "⚠ Warning: Low TPS detected! Performance degradation may occur."
            ));
        }
    }

    private String formatTime(long nanos) {
        if (nanos == 0) {
            return TextFormatting.GRAY + "0.00ms";
        }

        double ms = nanos / 1_000_000.0;
        TextFormatting color;
        if (ms < 5) {
            color = TextFormatting.GREEN;
        } else if (ms < 15) {
            color = TextFormatting.YELLOW;
        } else {
            color = TextFormatting.RED;
        }

        return color + String.format("%.2fms", ms) + TextFormatting.RESET;
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        if (args.length == 1) {
            return getListOfStringsMatchingLastWord(args, "detailed");
        }
        return Collections.emptyList();
    }
}
