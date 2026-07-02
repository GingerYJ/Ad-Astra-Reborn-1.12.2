package earth.terrarium.adastra.common.container;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Utility class for fluid operations in Ad Astra machines.
 * Provides helper methods for fluid handling with tanks and items.
 */
public class FluidUtils {

    /**
     * Try to fill a fluid container item from a tank.
     * @param tank The source tank
     * @param container The container item to fill
     * @return The filled container, or the original if operation failed
     */
    @Nonnull
    public static ItemStack fillContainer(@Nonnull AdAstraFluidTank tank, @Nonnull ItemStack container) {
        if (container.isEmpty() || !container.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null)) {
            return container;
        }

        IFluidHandlerItem handler = container.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
        if (handler == null) {
            return container;
        }

        FluidStack toFill = tank.drain(Fluid.BUCKET_VOLUME, false);
        if (toFill == null || toFill.amount <= 0) {
            return container;
        }

        int filled = handler.fill(toFill, true);
        if (filled > 0) {
            tank.drain(filled, true);
            return handler.getContainer();
        }

        return container;
    }

    /**
     * Try to drain a fluid container item into a tank.
     * @param tank The destination tank
     * @param container The container item to drain
     * @return The drained container, or the original if operation failed
     */
    @Nonnull
    public static ItemStack drainContainer(@Nonnull AdAstraFluidTank tank, @Nonnull ItemStack container) {
        if (container.isEmpty() || !container.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null)) {
            return container;
        }

        IFluidHandlerItem handler = container.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
        if (handler == null) {
            return container;
        }

        FluidStack toDrain = handler.drain(Fluid.BUCKET_VOLUME, false);
        if (toDrain == null || toDrain.amount <= 0) {
            return container;
        }

        int accepted = tank.fill(toDrain, false);
        if (accepted > 0) {
            FluidStack drained = handler.drain(accepted, true);
            if (drained != null && drained.amount > 0) {
                tank.fill(drained, true);
                return handler.getContainer();
            }
        }

        return container;
    }

    /**
     * Transfer fluid between two tanks.
     * @param from Source tank
     * @param to Destination tank
     * @param maxAmount Maximum amount to transfer
     * @return Amount actually transferred
     */
    public static int transferFluid(@Nonnull AdAstraFluidTank from, @Nonnull AdAstraFluidTank to, int maxAmount) {
        FluidStack drained = from.drain(maxAmount, false);
        if (drained == null || drained.amount <= 0) {
            return 0;
        }

        int filled = to.fill(drained, false);
        if (filled <= 0) {
            return 0;
        }

        from.drain(filled, true);
        to.fill(drained.copy(), true);
        return filled;
    }

    /**
     * Check if two fluid stacks are compatible (same fluid or one is null).
     */
    public static boolean fluidsMatch(@Nullable FluidStack a, @Nullable FluidStack b) {
        if (a == null || b == null) {
            return true;
        }
        return a.isFluidEqual(b);
    }

    /**
     * Get the fluid from a tank, or null if empty.
     */
    @Nullable
    public static FluidStack getFluidInTank(@Nonnull AdAstraFluidTank tank) {
        return tank.getFluid();
    }

    /**
     * Get the fluid type from a tank, or null if empty.
     */
    @Nullable
    public static Fluid getFluidType(@Nonnull AdAstraFluidTank tank) {
        FluidStack fluid = tank.getFluid();
        return fluid != null ? fluid.getFluid() : null;
    }

    /**
     * Get the amount of fluid in a tank.
     */
    public static int getFluidAmount(@Nonnull AdAstraFluidTank tank) {
        FluidStack fluid = tank.getFluid();
        return fluid != null ? fluid.amount : 0;
    }

    /**
     * Check if a tank contains a specific fluid type.
     */
    public static boolean hasFluidType(@Nonnull AdAstraFluidTank tank, @Nonnull Fluid fluid) {
        FluidStack stack = tank.getFluid();
        return stack != null && stack.getFluid() == fluid;
    }

    /**
     * Check if a tank has at least the specified amount of fluid.
     */
    public static boolean hasFluidAmount(@Nonnull AdAstraFluidTank tank, int amount) {
        return getFluidAmount(tank) >= amount;
    }

    /**
     * Check if a tank can accept the specified fluid stack.
     */
    public static boolean canFill(@Nonnull AdAstraFluidTank tank, @Nonnull FluidStack fluid) {
        return tank.fill(fluid, false) > 0;
    }

    /**
     * Check if a tank can drain the specified amount.
     */
    public static boolean canDrain(@Nonnull AdAstraFluidTank tank, int amount) {
        FluidStack drained = tank.drain(amount, false);
        return drained != null && drained.amount >= amount;
    }

    /**
     * Get the fluid contained in an item stack.
     */
    @Nullable
    public static FluidStack getFluidInItem(@Nonnull ItemStack stack) {
        if (stack.isEmpty() || !stack.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null)) {
            return null;
        }

        IFluidHandlerItem handler = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
        if (handler == null) {
            return null;
        }

        IFluidTankProperties[] properties = handler.getTankProperties();
        if (properties.length > 0 && properties[0] != null) {
            return properties[0].getContents();
        }

        return null;
    }

    /**
     * Format a fluid amount for display (e.g., "1000 mB" or "1 B").
     */
    @Nonnull
    public static String formatFluidAmount(int amount) {
        if (amount >= 1000 && amount % 1000 == 0) {
            return (amount / 1000) + " B";
        }
        return amount + " mB";
    }

    /**
     * Format a fluid stack for display (e.g., "Water: 1000 mB").
     */
    @Nonnull
    public static String formatFluidStack(@Nullable FluidStack stack) {
        if (stack == null || stack.amount <= 0) {
            return "Empty";
        }
        return stack.getLocalizedName() + ": " + formatFluidAmount(stack.amount);
    }
}
