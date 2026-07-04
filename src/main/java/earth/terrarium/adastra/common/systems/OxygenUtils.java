package earth.terrarium.adastra.common.systems;

import earth.terrarium.adastra.common.items.GasTankItem;
import earth.terrarium.adastra.common.registry.ModItems;
import earth.terrarium.adastra.common.util.EnvironmentUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
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

        boolean hasPositionOxygen = OxygenSystemExtended.hasOxygenAtPos(world, pos);
        if (hasPositionOxygen) {
            return true;
        }

        return EnvironmentUtils.hasOxygen(world, pos, EnvironmentUtils.DEFAULT_ENVIRONMENT_SCAN_RADIUS);
    }

    /**
     * Check if the player is wearing a complete space suit.
     * All four armor pieces must be worn for the suit to function.
     *
     * @param player The player to check
     * @return true if wearing complete space suit, false otherwise
     */
    public static boolean isWearingSpaceSuit(EntityPlayer player) {
        return isWearingSet(player, ModItems.SPACE_HELMET, ModItems.SPACE_SUIT, ModItems.SPACE_PANTS, ModItems.SPACE_BOOTS)
            || isWearingSet(player, ModItems.NETHERITE_SPACE_HELMET, ModItems.NETHERITE_SPACE_SUIT, ModItems.NETHERITE_SPACE_PANTS, ModItems.NETHERITE_SPACE_BOOTS)
            || isWearingSet(player, ModItems.JET_SUIT_HELMET, ModItems.JET_SUIT, ModItems.JET_SUIT_PANTS, ModItems.JET_SUIT_BOOTS);
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

    /**
     * Check if a player is wearing a complete armor set.
     *
     * @param player The player to check
     * @param helmet Helmet item
     * @param chest Chest item
     * @param legs Leggings item
     * @param boots Boots item
     * @return true if wearing complete set, false otherwise
     */
    private static boolean isWearingSet(EntityPlayer player, Item helmet, Item chest, Item legs, Item boots) {
        return isWearing(player, EntityEquipmentSlot.HEAD, helmet)
            && isWearing(player, EntityEquipmentSlot.CHEST, chest)
            && isWearing(player, EntityEquipmentSlot.LEGS, legs)
            && isWearing(player, EntityEquipmentSlot.FEET, boots);
    }

    /**
     * Check if a player is wearing a specific item in a specific slot.
     *
     * @param player The player to check
     * @param slot Equipment slot
     * @param item Item to check for
     * @return true if wearing the item in the slot, false otherwise
     */
    private static boolean isWearing(EntityPlayer player, EntityEquipmentSlot slot, Item item) {
        ItemStack stack = player.getItemStackFromSlot(slot);
        return !stack.isEmpty() && stack.getItem() == item;
    }
}
