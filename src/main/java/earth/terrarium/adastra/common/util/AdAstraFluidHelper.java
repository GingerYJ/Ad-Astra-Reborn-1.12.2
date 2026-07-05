package earth.terrarium.adastra.common.util;

import earth.terrarium.adastra.common.registry.ModFluids;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStack;

import javax.annotation.Nullable;

public final class AdAstraFluidHelper {

    private static final String FLUID_NAME_KEY = "FluidName";
    private static final String OLD_AD_ASTRA_OXYGEN_NAME = "ad_astra_oxygen";
    private static final String OLD_AD_ASTRA_HYDROGEN_NAME = "ad_astra_hydrogen";
    private static final String OXYGEN_BACKUP_KEY = "AdAstraOxygenAmount";

    private AdAstraFluidHelper() {
    }

    @Nullable
    public static FluidStack loadFluidStackFromNBT(@Nullable NBTTagCompound nbt) {
        if (nbt == null || nbt.hasKey("Empty")) {
            return null;
        }
        NBTTagCompound copy = nbt.copy();
        migrateFluidName(copy);
        return normalizeFluidStack(FluidStack.loadFluidStackFromNBT(copy));
    }

    @Nullable
    public static FluidStack normalizeFluidStack(@Nullable FluidStack stack) {
        if (stack == null || stack.getFluid() == null) {
            return stack;
        }
        Fluid replacement = getLegacyReplacement(stack.getFluid().getName());
        if (replacement == null) {
            return stack;
        }
        FluidStack migrated = new FluidStack(replacement, stack.amount);
        if (stack.tag != null) {
            migrated.tag = stack.tag.copy();
        }
        return migrated;
    }

    public static void migrateItemFluidTag(ItemStack stack) {
        if (stack.isEmpty() || !stack.hasTagCompound()) {
            return;
        }
        NBTTagCompound tag = stack.getTagCompound();
        if (tag != null && tag.hasKey(FluidHandlerItemStack.FLUID_NBT_KEY)) {
            migrateFluidName(tag.getCompoundTag(FluidHandlerItemStack.FLUID_NBT_KEY));
        }
    }

    public static void migrateFluidName(NBTTagCompound tag) {
        if (tag == null || !tag.hasKey(FLUID_NAME_KEY)) {
            return;
        }
        String fluidName = tag.getString(FLUID_NAME_KEY);
        if (OLD_AD_ASTRA_OXYGEN_NAME.equals(fluidName) || "ad_astra:oxygen".equals(fluidName)) {
            tag.setString(FLUID_NAME_KEY, ModFluids.OXYGEN_FLUID_NAME);
        } else if (OLD_AD_ASTRA_HYDROGEN_NAME.equals(fluidName) || "ad_astra:hydrogen".equals(fluidName)) {
            tag.setString(FLUID_NAME_KEY, ModFluids.HYDROGEN_FLUID_NAME);
        }
    }

    public static int getOxygenAmountWithBackup(ItemStack stack, @Nullable FluidStack contained) {
        FluidStack normalized = normalizeFluidStack(contained);
        if (isOxygen(normalized)) {
            int amount = Math.max(0, normalized.amount);
            setOxygenBackupAmount(stack, amount);
            return amount;
        }
        return getOxygenBackupAmount(stack);
    }

    public static void setOxygenBackupAmount(ItemStack stack, int amount) {
        if (stack.isEmpty()) {
            return;
        }
        if (amount <= 0) {
            if (stack.hasTagCompound()) {
                stack.getTagCompound().removeTag(OXYGEN_BACKUP_KEY);
            }
            return;
        }
        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
        }
        stack.getTagCompound().setInteger(OXYGEN_BACKUP_KEY, amount);
    }

    @Nullable
    public static FluidStack restoreOxygenFromBackup(ItemStack stack, int capacity) {
        int amount = getOxygenBackupAmount(stack);
        if (amount <= 0) {
            return null;
        }
        return new FluidStack(ModFluids.OXYGEN, Math.min(amount, capacity));
    }

    public static boolean isOxygen(@Nullable FluidStack stack) {
        return stack != null && isOxygen(stack.getFluid());
    }

    public static boolean isOxygen(@Nullable Fluid fluid) {
        return fluid == ModFluids.OXYGEN || (fluid != null && ModFluids.OXYGEN_FLUID_NAME.equals(fluid.getName()));
    }

    public static boolean isHydrogen(@Nullable FluidStack stack) {
        return stack != null && isHydrogen(stack.getFluid());
    }

    public static boolean isHydrogen(@Nullable Fluid fluid) {
        return fluid == ModFluids.HYDROGEN || (fluid != null && ModFluids.HYDROGEN_FLUID_NAME.equals(fluid.getName()));
    }

    public static boolean isAdAstraGas(@Nullable FluidStack stack) {
        return stack != null && (isOxygen(stack.getFluid()) || isHydrogen(stack.getFluid()));
    }

    @Nullable
    private static Fluid getLegacyReplacement(String fluidName) {
        if (isLegacyOxygenName(fluidName)) {
            return ModFluids.OXYGEN;
        }
        if (isLegacyHydrogenName(fluidName)) {
            return ModFluids.HYDROGEN;
        }
        return null;
    }

    private static boolean isLegacyOxygenName(String fluidName) {
        return OLD_AD_ASTRA_OXYGEN_NAME.equals(fluidName) || "ad_astra:oxygen".equals(fluidName);
    }

    private static boolean isLegacyHydrogenName(String fluidName) {
        return OLD_AD_ASTRA_HYDROGEN_NAME.equals(fluidName) || "ad_astra:hydrogen".equals(fluidName);
    }

    private static int getOxygenBackupAmount(ItemStack stack) {
        if (stack.isEmpty() || !stack.hasTagCompound()) {
            return 0;
        }
        return Math.max(0, stack.getTagCompound().getInteger(OXYGEN_BACKUP_KEY));
    }
}
