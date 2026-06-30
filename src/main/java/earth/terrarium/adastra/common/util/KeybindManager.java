package earth.terrarium.adastra.common.util;

import net.minecraft.entity.player.EntityPlayer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class KeybindManager {

    private static final Map<UUID, KeyState> PLAYER_KEYS = new HashMap<>();

    private KeybindManager() {
    }

    public static boolean jumpDown(EntityPlayer player) {
        return player != null && get(player.getUniqueID()).jumpDown;
    }

    public static boolean sprintDown(EntityPlayer player) {
        return player != null && get(player.getUniqueID()).sprintDown;
    }

    public static boolean suitFlightEnabled(EntityPlayer player) {
        return player != null && get(player.getUniqueID()).suitFlightEnabled;
    }

    public static void set(EntityPlayer player, boolean jumpDown, boolean sprintDown, boolean suitFlightEnabled) {
        if (player != null) {
            PLAYER_KEYS.put(player.getUniqueID(), new KeyState(jumpDown, sprintDown, suitFlightEnabled));
        }
    }

    public static void clear(EntityPlayer player) {
        if (player != null) {
            PLAYER_KEYS.remove(player.getUniqueID());
        }
    }

    private static KeyState get(UUID player) {
        return PLAYER_KEYS.getOrDefault(player, KeyState.EMPTY);
    }

    private static final class KeyState {

        private static final KeyState EMPTY = new KeyState(false, false, false);

        private final boolean jumpDown;
        private final boolean sprintDown;
        private final boolean suitFlightEnabled;

        private KeyState(boolean jumpDown, boolean sprintDown, boolean suitFlightEnabled) {
            this.jumpDown = jumpDown;
            this.sprintDown = sprintDown;
            this.suitFlightEnabled = suitFlightEnabled;
        }
    }
}
