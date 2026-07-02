package earth.terrarium.adastra.common.util;

import earth.terrarium.adastra.common.container.AdAstraFluidTank;
import earth.terrarium.adastra.common.registry.ModFluids;
import earth.terrarium.adastra.common.registry.ModItems;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class RocketFuelHelper {

    private static final Map<String, Integer> FUEL_TIERS = new ConcurrentHashMap<>();

    static {
        registerDefaultFuel("fuel", 1);
        registerDefaultFuel("cryo_fuel", 2);
        registerDefaultFuel("oil", 1);
        registerDefaultFuel("lava", 1);
    }

    private RocketFuelHelper() {
    }

    public static boolean addFuel(String fluidName, int fuelTier) {
        String resolvedName = resolveFluidName(fluidName);
        if (resolvedName == null) {
            return false;
        }
        FUEL_TIERS.put(resolvedName, Math.max(1, fuelTier));
        return true;
    }

    public static boolean removeFuel(String fluidName) {
        String resolvedName = resolveFluidName(fluidName);
        if (resolvedName == null) {
            resolvedName = normalizeFluidName(fluidName);
        }
        return resolvedName != null && FUEL_TIERS.remove(resolvedName) != null;
    }

    public static int getFuelTier(FluidStack stack) {
        if (stack == null || stack.getFluid() == null || stack.amount <= 0) {
            return 0;
        }
        Integer tier = FUEL_TIERS.get(stack.getFluid().getName());
        return tier == null ? 0 : tier;
    }

    public static int getFuelTier(String fluidName) {
        String resolvedName = resolveFluidName(fluidName);
        if (resolvedName == null) {
            resolvedName = normalizeFluidName(fluidName);
        }
        if (resolvedName == null) {
            return 0;
        }
        Integer tier = FUEL_TIERS.get(resolvedName);
        return tier == null ? 0 : tier;
    }

    public static boolean isRocketFuel(FluidStack stack) {
        return getFuelTier(stack) > 0;
    }

    public static boolean canFuelRocket(FluidStack stack, int rocketTier) {
        return getFuelTier(stack) >= Math.max(1, rocketTier);
    }

    public static Map<String, Integer> getFuelTiers() {
        return Collections.unmodifiableMap(FUEL_TIERS);
    }

    public static boolean moveFuelItemToTank(AdAstraFluidTank tank, NonNullList<ItemStack> inventory, int inputSlot, int outputSlot) {
        if (tank == null || inputSlot < 0 || inputSlot >= inventory.size() || outputSlot < 0 || outputSlot >= inventory.size()) {
            return false;
        }

        ItemStack input = inventory.get(inputSlot);
        if (input.isEmpty()) {
            return false;
        }

        ItemStack output = inventory.get(outputSlot);
        if (!output.isEmpty()) {
            return false;
        }

        ItemStack single = input.copy();
        single.setCount(1);
        ItemStack result = drainFuelContainer(tank, single);
        if (ItemStack.areItemStacksEqual(single, result)) {
            return false;
        }

        if (!canMergeOutput(output, result)) {
            return false;
        }

        input.shrink(1);
        if (input.isEmpty()) {
            inventory.set(inputSlot, ItemStack.EMPTY);
        }

        if (output.isEmpty()) {
            inventory.set(outputSlot, result);
        } else {
            output.grow(result.getCount());
        }
        return true;
    }

    public static boolean handleRocketFuelInteraction(AdAstraFluidTank tank, EntityPlayer player, EnumHand hand, int rocketTier) {
        if (tank == null || player == null) {
            return false;
        }

        ItemStack held = player.getHeldItem(hand);
        FluidStack heldFuel = getContainedFluid(held);
        if (heldFuel == null || !isRocketFuel(heldFuel)) {
            return false;
        }

        int requiredTier = Math.max(1, rocketTier);
        int heldTier = getFuelTier(heldFuel);
        if (heldTier < requiredTier) {
            if (!player.world.isRemote) {
                player.sendStatusMessage(new TextComponentTranslation(
                    "message.ad_astra.fuel_tier_too_low",
                    heldTier,
                    requiredTier), true);
            }
            return true;
        }

        if (fillTankFromHeldItem(tank, player, hand)) {
            return true;
        }

        if (!player.world.isRemote) {
            String message = tank.isFull()
                ? "message.ad_astra.fuel_tank_full"
                : "message.ad_astra.fuel_not_accepted";
            player.sendStatusMessage(new TextComponentTranslation(message), true);
        }
        return true;
    }

    public static boolean fillTankFromHeldItem(AdAstraFluidTank tank, EntityPlayer player, EnumHand hand) {
        ItemStack held = player.getHeldItem(hand);
        if (held.isEmpty()) {
            return false;
        }

        if (player.world.isRemote) {
            return canDrainFuelContainer(tank, held);
        }

        ItemStack single = held.copy();
        single.setCount(1);
        int before = tank.getFluidAmount();
        ItemStack result = drainFuelContainer(tank, single);
        int added = tank.getFluidAmount() - before;
        if (added <= 0 && ItemStack.areItemStacksEqual(single, result)) {
            return false;
        }

        if (!player.capabilities.isCreativeMode) {
            held.shrink(1);
            if (held.isEmpty()) {
                player.setHeldItem(hand, result);
            } else if (!player.inventory.addItemStackToInventory(result)) {
                player.dropItem(result, false);
            }
        }
        player.world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ITEM_BUCKET_EMPTY, SoundCategory.PLAYERS, 0.8F, 1.0F);
        player.sendStatusMessage(new TextComponentTranslation(
            "message.ad_astra.fuel_added",
            added,
            tank.getFluidAmount(),
            tank.getCapacity()), true);
        return true;
    }

    public static boolean canDrainFuelContainer(AdAstraFluidTank tank, @Nonnull ItemStack stack) {
        if (tank == null || stack.isEmpty()) {
            return false;
        }

        FluidStack contained = getContainedFluid(stack);
        return contained != null && contained.amount > 0 && tank.fill(contained, false) > 0;
    }

    @Nonnull
    public static ItemStack drainFuelContainer(AdAstraFluidTank tank, @Nonnull ItemStack stack) {
        if (tank == null || stack.isEmpty()) {
            return stack;
        }

        ItemStack bucketResult = drainKnownBucket(tank, stack);
        if (!ItemStack.areItemStacksEqual(stack, bucketResult)) {
            return bucketResult;
        }

        if (!stack.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null)) {
            return stack;
        }

        ItemStack working = stack.copy();
        IFluidHandlerItem handler = working.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
        if (handler == null) {
            return stack;
        }

        FluidStack simulated = handler.drain(Integer.MAX_VALUE, false);
        if (simulated == null || simulated.amount <= 0 || tank.fill(simulated, false) <= 0) {
            return stack;
        }

        int accepted = tank.fill(simulated, false);
        FluidStack drained = handler.drain(accepted, true);
        if (drained == null || drained.amount <= 0) {
            return stack;
        }

        tank.fill(drained, true);
        return handler.getContainer();
    }

    private static ItemStack drainKnownBucket(AdAstraFluidTank tank, ItemStack stack) {
        Fluid fluid = getBucketFluid(stack.getItem());
        if (fluid == null) {
            return stack;
        }

        FluidStack fuel = new FluidStack(fluid, Fluid.BUCKET_VOLUME);
        if (tank.fill(fuel, false) < Fluid.BUCKET_VOLUME) {
            return stack;
        }

        tank.fill(fuel, true);
        return new ItemStack(Items.BUCKET);
    }

    @Nullable
    private static FluidStack getContainedFluid(@Nonnull ItemStack stack) {
        if (stack.isEmpty()) {
            return null;
        }

        Fluid fluid = getBucketFluid(stack.getItem());
        if (fluid != null) {
            return new FluidStack(fluid, Fluid.BUCKET_VOLUME);
        }

        if (!stack.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null)) {
            return null;
        }

        ItemStack working = stack.copy();
        working.setCount(1);
        IFluidHandlerItem handler = working.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
        if (handler == null) {
            return null;
        }
        FluidStack simulated = handler.drain(Integer.MAX_VALUE, false);
        return simulated == null ? null : simulated.copy();
    }

    @Nullable
    private static Fluid getBucketFluid(Item item) {
        if (item == ModItems.FUEL_BUCKET) {
            return ModFluids.FUEL;
        }
        if (item == ModItems.CRYO_FUEL_BUCKET) {
            return ModFluids.CRYO_FUEL;
        }
        if (item == ModItems.OIL_BUCKET) {
            return ModFluids.OIL;
        }
        if (item == Items.LAVA_BUCKET) {
            return FluidRegistry.LAVA;
        }
        return null;
    }

    @Nullable
    public static String resolveFluidName(String fluidName) {
        String normalized = normalizeFluidName(fluidName);
        if (normalized == null) {
            return null;
        }

        Fluid fluid = FluidRegistry.getFluid(normalized);
        if (fluid == null && normalized.indexOf(':') >= 0) {
            fluid = FluidRegistry.getFluid(normalized.substring(normalized.indexOf(':') + 1));
        }
        return fluid == null ? null : fluid.getName();
    }

    @Nullable
    private static String normalizeFluidName(String fluidName) {
        if (fluidName == null) {
            return null;
        }
        String trimmed = fluidName.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private static void registerDefaultFuel(String fluidName, int fuelTier) {
        FUEL_TIERS.put(fluidName, Math.max(1, fuelTier));
    }

    private static boolean canMergeOutput(ItemStack output, ItemStack result) {
        if (result.isEmpty()) {
            return true;
        }
        if (output.isEmpty()) {
            return true;
        }
        return ItemStack.areItemsEqual(output, result)
            && ItemStack.areItemStackTagsEqual(output, result)
            && output.getCount() + result.getCount() <= output.getMaxStackSize();
    }
}
