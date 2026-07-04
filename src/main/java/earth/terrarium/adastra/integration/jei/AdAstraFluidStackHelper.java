package earth.terrarium.adastra.integration.jei;

import earth.terrarium.adastra.common.registry.ModFluids;
import earth.terrarium.adastra.common.registry.ModItems;
import mezz.jei.plugins.vanilla.ingredients.fluid.FluidStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

public class AdAstraFluidStackHelper extends FluidStackHelper {

    @Override
    public ItemStack getCheatItemStack(FluidStack ingredient) {
        ItemStack bucket = getAdAstraBucket(ingredient);
        return bucket.isEmpty() ? super.getCheatItemStack(ingredient) : bucket;
    }

    @Override
    public ItemStack replaceWithCheatItemStack(FluidStack ingredient, ItemStack stack) {
        ItemStack bucket = getAdAstraBucket(ingredient);
        return bucket.isEmpty() ? super.replaceWithCheatItemStack(ingredient, stack) : bucket;
    }

    private static ItemStack getAdAstraBucket(FluidStack ingredient) {
        if (ingredient == null) {
            return ItemStack.EMPTY;
        }
        Fluid fluid = ingredient.getFluid();
        if (fluid == ModFluids.OXYGEN) {
            return new ItemStack(ModItems.OXYGEN_BUCKET);
        }
        if (fluid == ModFluids.HYDROGEN) {
            return new ItemStack(ModItems.HYDROGEN_BUCKET);
        }
        if (fluid == ModFluids.OIL) {
            return new ItemStack(ModItems.OIL_BUCKET);
        }
        if (fluid == ModFluids.FUEL) {
            return new ItemStack(ModItems.FUEL_BUCKET);
        }
        if (fluid == ModFluids.CRYO_FUEL) {
            return new ItemStack(ModItems.CRYO_FUEL_BUCKET);
        }
        return ItemStack.EMPTY;
    }
}
