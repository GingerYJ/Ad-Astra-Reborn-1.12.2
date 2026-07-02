package earth.terrarium.adastra.common.container;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Utility class for energy operations in Ad Astra machines.
 * Provides helper methods for energy handling with containers and items.
 */
public class EnergyUtils {

    /**
     * Try to charge an item from an energy container.
     * @param container The source energy container
     * @param item The item to charge
     * @param maxTransfer Maximum energy to transfer
     * @return Amount actually transferred
     */
    public static int chargeItem(@Nonnull AdAstraEnergyContainer container, @Nonnull ItemStack item, int maxTransfer) {
        if (item.isEmpty() || !item.hasCapability(CapabilityEnergy.ENERGY, null)) {
            return 0;
        }

        IEnergyStorage itemStorage = item.getCapability(CapabilityEnergy.ENERGY, null);
        if (itemStorage == null || !itemStorage.canReceive()) {
            return 0;
        }

        return container.transferTo(itemStorage, maxTransfer);
    }

    /**
     * Try to discharge an item into an energy container.
     * @param container The destination energy container
     * @param item The item to discharge
     * @param maxTransfer Maximum energy to transfer
     * @return Amount actually transferred
     */
    public static int dischargeItem(@Nonnull AdAstraEnergyContainer container, @Nonnull ItemStack item, int maxTransfer) {
        if (item.isEmpty() || !item.hasCapability(CapabilityEnergy.ENERGY, null)) {
            return 0;
        }

        IEnergyStorage itemStorage = item.getCapability(CapabilityEnergy.ENERGY, null);
        if (itemStorage == null || !itemStorage.canExtract()) {
            return 0;
        }

        return container.transferFrom(itemStorage, maxTransfer);
    }

    /**
     * Transfer energy from a tile entity to another.
     * @param from Source tile
     * @param fromSide Side to extract from (on source tile)
     * @param to Destination tile
     * @param toSide Side to insert to (on destination tile)
     * @param maxAmount Maximum energy to transfer
     * @return Amount actually transferred
     */
    public static int transferEnergy(@Nullable TileEntity from, @Nullable EnumFacing fromSide,
                                     @Nullable TileEntity to, @Nullable EnumFacing toSide,
                                     int maxAmount) {
        if (from == null || to == null) {
            return 0;
        }

        if (!from.hasCapability(CapabilityEnergy.ENERGY, fromSide)) {
            return 0;
        }
        if (!to.hasCapability(CapabilityEnergy.ENERGY, toSide)) {
            return 0;
        }

        IEnergyStorage source = from.getCapability(CapabilityEnergy.ENERGY, fromSide);
        IEnergyStorage target = to.getCapability(CapabilityEnergy.ENERGY, toSide);

        if (source == null || !source.canExtract() || target == null || !target.canReceive()) {
            return 0;
        }

        int extracted = source.extractEnergy(maxAmount, true);
        if (extracted <= 0) {
            return 0;
        }

        int accepted = target.receiveEnergy(extracted, false);
        if (accepted > 0) {
            source.extractEnergy(accepted, false);
        }

        return accepted;
    }

    /**
     * Get the energy stored in an item.
     */
    public static int getEnergyInItem(@Nonnull ItemStack stack) {
        if (stack.isEmpty() || !stack.hasCapability(CapabilityEnergy.ENERGY, null)) {
            return 0;
        }

        IEnergyStorage storage = stack.getCapability(CapabilityEnergy.ENERGY, null);
        return storage != null ? storage.getEnergyStored() : 0;
    }

    /**
     * Get the max energy capacity of an item.
     */
    public static int getMaxEnergyInItem(@Nonnull ItemStack stack) {
        if (stack.isEmpty() || !stack.hasCapability(CapabilityEnergy.ENERGY, null)) {
            return 0;
        }

        IEnergyStorage storage = stack.getCapability(CapabilityEnergy.ENERGY, null);
        return storage != null ? storage.getMaxEnergyStored() : 0;
    }

    /**
     * Check if an item can store energy.
     */
    public static boolean canStoreEnergy(@Nonnull ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }
        return stack.hasCapability(CapabilityEnergy.ENERGY, null);
    }

    /**
     * Check if an item can receive energy.
     */
    public static boolean canReceiveEnergy(@Nonnull ItemStack stack) {
        if (stack.isEmpty() || !stack.hasCapability(CapabilityEnergy.ENERGY, null)) {
            return false;
        }

        IEnergyStorage storage = stack.getCapability(CapabilityEnergy.ENERGY, null);
        return storage != null && storage.canReceive();
    }

    /**
     * Check if an item can extract energy.
     */
    public static boolean canExtractEnergy(@Nonnull ItemStack stack) {
        if (stack.isEmpty() || !stack.hasCapability(CapabilityEnergy.ENERGY, null)) {
            return false;
        }

        IEnergyStorage storage = stack.getCapability(CapabilityEnergy.ENERGY, null);
        return storage != null && storage.canExtract();
    }

    /**
     * Format energy for display (e.g., "10000 FE" or "10.0k FE").
     */
    @Nonnull
    public static String formatEnergy(int energy) {
        if (energy >= 1_000_000) {
            return String.format("%.1fM FE", energy / 1_000_000.0);
        } else if (energy >= 1000) {
            return String.format("%.1fk FE", energy / 1000.0);
        } else {
            return energy + " FE";
        }
    }

    /**
     * Format energy with capacity (e.g., "5000 / 10000 FE").
     */
    @Nonnull
    public static String formatEnergyWithCapacity(int energy, int capacity) {
        return formatEnergy(energy) + " / " + formatEnergy(capacity);
    }

    /**
     * Get energy fill percentage (0.0 to 1.0).
     */
    public static float getEnergyPercentage(int energy, int capacity) {
        if (capacity == 0) return 0f;
        return (float) energy / capacity;
    }

    /**
     * Calculate how many ticks of operation are possible with the given energy.
     * @param energy Current energy
     * @param costPerTick Energy cost per tick
     * @return Number of ticks possible
     */
    public static int calculateRemainingTicks(int energy, int costPerTick) {
        if (costPerTick <= 0) return Integer.MAX_VALUE;
        return energy / costPerTick;
    }

    /**
     * Calculate total energy cost for an operation.
     * @param costPerTick Energy cost per tick
     * @param ticks Total ticks
     * @return Total energy cost
     */
    public static int calculateTotalCost(int costPerTick, int ticks) {
        return costPerTick * ticks;
    }

    /**
     * Check if there's enough energy for an operation.
     * @param container The energy container
     * @param cost Required energy
     * @return true if sufficient energy
     */
    public static boolean hasEnergy(@Nonnull AdAstraEnergyContainer container, int cost) {
        return container.getEnergyStored() >= cost;
    }
}
