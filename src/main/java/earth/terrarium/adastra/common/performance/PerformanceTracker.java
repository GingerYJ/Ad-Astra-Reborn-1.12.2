package earth.terrarium.adastra.common.performance;

import earth.terrarium.adastra.AdAstraReborn;
import earth.terrarium.adastra.common.config.AdAstraConfig;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Tracks performance metrics for Ad Astra systems.
 * Monitors TPS, per-system timing, and machine activity.
 */
public class PerformanceTracker {

    private static final int TPS_SAMPLE_SIZE = 100;
    private static final long[] TICK_TIMES = new long[TPS_SAMPLE_SIZE];
    private static int tickIndex = 0;
    private static long lastTickTime = 0;

    // Per-system timing (system name -> total nanoseconds)
    private static final Map<String, Long> SYSTEM_TIMES = new HashMap<>();
    private static final Map<String, Long> SYSTEM_START_TIMES = new HashMap<>();

    // Machine tracking
    private static int activeMachineCount = 0;
    private static int totalMachineCount = 0;

    // TPS warning threshold
    private static final double TPS_WARNING_THRESHOLD = 18.0;
    private static long lastWarningTime = 0;
    private static final long WARNING_COOLDOWN = 60000; // 60 seconds

    /**
     * Call this at the start of each server tick.
     */
    public static void startTick() {
        lastTickTime = System.nanoTime();
    }

    /**
     * Call this at the end of each server tick.
     */
    public static void endTick() {
        long currentTime = System.nanoTime();
        long tickDuration = currentTime - lastTickTime;

        TICK_TIMES[tickIndex] = tickDuration;
        tickIndex = (tickIndex + 1) % TPS_SAMPLE_SIZE;

        // Check for low TPS and warn
        double tps = getCurrentTPS();
        if (getValidSampleCount() == TPS_SAMPLE_SIZE
            && tps < TPS_WARNING_THRESHOLD
            && currentTime - lastWarningTime > WARNING_COOLDOWN * 1_000_000L) {
            AdAstraReborn.LOGGER.warn("Ad Astra: Low TPS detected! Current TPS: {}", String.format("%.2f", tps));
            lastWarningTime = currentTime;
        }
    }

    /**
     * Get current TPS (ticks per second).
     * @return TPS value, capped at 20.0
     */
    public static double getCurrentTPS() {
        long totalTime = 0;
        int validSamples = 0;

        for (long time : TICK_TIMES) {
            if (time > 0) {
                totalTime += time;
                validSamples++;
            }
        }

        if (validSamples == 0) {
            return 20.0;
        }

        double avgTickTime = (double) totalTime / validSamples;
        double tps = 1_000_000_000.0 / avgTickTime;
        return Math.min(tps, 20.0);
    }

    /**
     * Get average tick time in milliseconds.
     */
    public static long getAverageTickTime() {
        long totalTime = 0;
        int validSamples = 0;

        for (long time : TICK_TIMES) {
            if (time > 0) {
                totalTime += time;
                validSamples++;
            }
        }

        if (validSamples == 0) {
            return 0;
        }

        return (totalTime / validSamples) / 1_000_000; // Convert to ms
    }

    /**
     * Start timing a system.
     * @param systemName Name of the system (e.g., "machines", "environment")
     */
    public static void startSystemTiming(String systemName) {
        if (!AdAstraConfig.debugLogging) {
            return;
        }
        SYSTEM_START_TIMES.put(systemName, System.nanoTime());
    }

    /**
     * End timing a system and accumulate the time.
     * @param systemName Name of the system
     */
    public static void endSystemTiming(String systemName) {
        if (!AdAstraConfig.debugLogging) {
            return;
        }
        Long startTime = SYSTEM_START_TIMES.get(systemName);
        if (startTime != null) {
            long duration = System.nanoTime() - startTime;
            SYSTEM_TIMES.merge(systemName, duration, Long::sum);
            SYSTEM_START_TIMES.remove(systemName);
        }
    }

    /**
     * Get total time spent in a system (in nanoseconds) since last reset.
     * @param systemName Name of the system
     * @return Total nanoseconds
     */
    public static long getSystemTime(String systemName) {
        return SYSTEM_TIMES.getOrDefault(systemName, 0L);
    }

    /**
     * Reset per-system timing counters. Call this periodically (e.g., every second).
     */
    public static void resetSystemTimes() {
        SYSTEM_TIMES.clear();
    }

    /**
     * Update machine statistics.
     * @param active Number of active machines (processing)
     * @param total Total number of loaded machines
     */
    public static void updateMachineStats(int active, int total) {
        activeMachineCount = active;
        totalMachineCount = total;
    }

    /**
     * Get number of currently active machines.
     */
    public static int getActiveMachineCount() {
        return activeMachineCount;
    }

    /**
     * Get total number of loaded machines.
     */
    public static int getTotalMachineCount() {
        return totalMachineCount;
    }

    /**
     * Get a performance report as a formatted string.
     */
    public static String getPerformanceReport() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Ad Astra Performance Report ===\n");
        sb.append(String.format("TPS: %.2f (%dms avg tick)\n", getCurrentTPS(), getAverageTickTime()));
        sb.append(String.format("Machines: %d active / %d total\n", activeMachineCount, totalMachineCount));
        sb.append("\nPer-System Timing:\n");

        for (Map.Entry<String, Long> entry : SYSTEM_TIMES.entrySet()) {
            double ms = entry.getValue() / 1_000_000.0;
            sb.append(String.format("  %s: %.2fms\n", entry.getKey(), ms));
        }

        return sb.toString();
    }

    private static int getValidSampleCount() {
        int validSamples = 0;
        for (long time : TICK_TIMES) {
            if (time > 0) {
                validSamples++;
            }
        }
        return validSamples;
    }

    /**
     * Clear all tracking data.
     */
    public static void reset() {
        Arrays.fill(TICK_TIMES, 0);
        tickIndex = 0;
        SYSTEM_TIMES.clear();
        SYSTEM_START_TIMES.clear();
        activeMachineCount = 0;
        totalMachineCount = 0;
    }
}
