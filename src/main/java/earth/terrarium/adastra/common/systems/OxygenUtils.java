package earth.terrarium.adastra.common.systems;

import earth.terrarium.adastra.common.items.GasTankItem;
import earth.terrarium.adastra.common.items.SpaceSuitItem;
import earth.terrarium.adastra.common.util.EnvironmentUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Helper methods for oxygen system operations.
 * Provides utilities for checking oxygen availability, space suit status, and oxygen consumption.
 */
public final class OxygenUtils {

    private OxygenUtils() {
    }

    /**
     * Check if the given world dimension has oxygen by default.
     *
     * @param world The world to check
     * @return true if dimension has oxygen, false otherwise
     */
    public static boolean hasOxygenInDimension(World world) {
        return EnvironmentUtils.worldProviderHasOxygen(world);
    }

    /**
     * Check if oxygen is available at the given position.
     * This checks dimension-level oxygen, position overrides, and local oxygen sources.
     *
     * @param world The world
     * @param pos The block position to check
     * @return true if oxygen is available, false otherwise
     */
    public static boolean hasOxygenAtPosition(World world, BlockPos pos) {
        if (world.isRemote) {
            return EnvironmentUtils.hasOxygen(world, pos, EnvironmentUtils.DEFAULT_ENVIRONMENT_SCAN_RADIUS);
        }
        return OxygenSystemExtended.hasOxygenAtPos(world, pos);
    }

    /**
     * Check if the player is wearing a complete space suit.
     * All four armor pieces must be worn for the suit to function.
     *
     * @param player The player to check
     * @return true if wearing complete space suit, false otherwise
     */
    public static boolean isWearingSpaceSuit(EntityPlayer player) {
        return player != null && SpaceSuitItem.hasFullSet(player);
    }

    /**
     * Get the amount of oxygen stored in the player's space suit chest piece.
     *
     * @param player The player to check
     * @return Amount of oxygen in millibuckets (mB), or 0 if no suit or no oxygen
     */
    public static int getSpaceSuitOxygen(EntityPlayer player) {
        if (player == null || !isWearingSpaceSuit(player)) {
            return 0;
        }
        ItemStack chest = player.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
        return GasTankItem.getStoredOxygen(chest);
    }

    /**
     * Consume oxygen from the player's space suit chest piece.
     *
     * @param player The player whose suit oxygen should be consumed
     * @param amount Amount of oxygen to consume in millibuckets (mB)
     * @return Amount actually consumed (may be less than requested if insufficient oxygen)
     */
    public static int consumeSpaceSuitOxygen(EntityPlayer player, int amount) {
        if (player == null || !isWearingSpaceSuit(player) || amount <= 0) {
            return 0;
        }
        ItemStack chest = player.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
        int consumed = GasTankItem.drainOxygen(chest, amount, false);
        if (consumed > 0) {
            player.inventory.markDirty();
            syncInventory(player);
        }
        return consumed;
    }

    public static void syncInventory(EntityPlayer player) {
        if (player instanceof EntityPlayerMP) {
            ((EntityPlayerMP) player).inventoryContainer.detectAndSendChanges();
        }
    }

}
