package earth.terrarium.adastra.common.util;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public final class SpaceStationLandingProtection {

    private static final int MAX_PROTECTION_TICKS = 1200;
    private static final int GROUNDED_GRACE_TICKS = 40;
    private static final double MAX_DESCENT_SPEED = -0.08D;
    private static final double RECOVERY_DROP_DISTANCE = 12.0D;
    private static final Map<UUID, ProtectionState> PROTECTED_PLAYERS = new HashMap<>();

    private SpaceStationLandingProtection() {
    }

    public static void protect(EntityPlayer player) {
        protect(player, player == null ? null : new BlockPos(player.posX, player.posY, player.posZ));
    }

    public static void protect(EntityPlayer player, BlockPos safePos) {
        if (player == null) {
            return;
        }
        BlockPos recoveryPos = safePos == null ? new BlockPos(player.posX, player.posY, player.posZ) : safePos;
        PROTECTED_PLAYERS.put(player.getUniqueID(),
            new ProtectionState(player.ticksExisted + MAX_PROTECTION_TICKS, player.dimension, recoveryPos));
        clearFallState(player);
    }

    public static boolean tick(EntityPlayer player) {
        ProtectionState state = getProtection(player);
        if (state == null) {
            return false;
        }
        clearFallState(player);
        if (state.shouldRecover(player)) {
            player.setPositionAndUpdate(state.safePos.getX() + 0.5D, state.safePos.getY(), state.safePos.getZ() + 0.5D);
            player.motionX = 0.0D;
            player.motionY = 0.0D;
            player.motionZ = 0.0D;
            player.velocityChanged = true;
            state.groundedTicks = 0;
            return true;
        }
        if (player.onGround) {
            state.groundedTicks++;
            if (state.groundedTicks >= GROUNDED_GRACE_TICKS) {
                clear(player);
                return false;
            }
        } else {
            state.groundedTicks = 0;
            if (player.motionY < MAX_DESCENT_SPEED) {
                player.motionY = MAX_DESCENT_SPEED;
                player.velocityChanged = true;
            }
        }
        return true;
    }

    public static boolean shouldCancelFall(EntityPlayer player) {
        if (getProtection(player) == null) {
            return false;
        }
        clearFallState(player);
        return true;
    }

    public static boolean isFallDamage(DamageSource source) {
        return source == DamageSource.FALL || source != null && "fall".equals(source.getDamageType());
    }

    public static void clear(EntityPlayer player) {
        if (player != null) {
            PROTECTED_PLAYERS.remove(player.getUniqueID());
        }
    }

    public static void pruneExpired(int currentTick) {
        Iterator<Map.Entry<UUID, ProtectionState>> iterator = PROTECTED_PLAYERS.entrySet().iterator();
        while (iterator.hasNext()) {
            if (iterator.next().getValue().expiresAt < currentTick) {
                iterator.remove();
            }
        }
    }

    private static ProtectionState getProtection(EntityPlayer player) {
        if (player == null) {
            return null;
        }
        ProtectionState state = PROTECTED_PLAYERS.get(player.getUniqueID());
        if (state == null) {
            return null;
        }
        if (state.expiresAt < player.ticksExisted) {
            PROTECTED_PLAYERS.remove(player.getUniqueID());
            return null;
        }
        return state;
    }

    private static void clearFallState(EntityPlayer player) {
        player.fallDistance = 0.0F;
    }

    private static final class ProtectionState {
        private final int expiresAt;
        private final int dimension;
        private final BlockPos safePos;
        private int groundedTicks;

        private ProtectionState(int expiresAt, int dimension, BlockPos safePos) {
            this.expiresAt = expiresAt;
            this.dimension = dimension;
            this.safePos = safePos;
        }

        private boolean shouldRecover(EntityPlayer player) {
            return player.dimension == dimension && player.posY < safePos.getY() - RECOVERY_DROP_DISTANCE;
        }
    }
}
