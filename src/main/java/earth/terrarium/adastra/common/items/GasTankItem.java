package earth.terrarium.adastra.common.items;

import earth.terrarium.adastra.Reference;
import earth.terrarium.adastra.common.AdAstraCreativeTab;
import earth.terrarium.adastra.common.registry.ModFluids;
import earth.terrarium.adastra.common.util.AdAstraFluidHelper;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStack;

import javax.annotation.Nullable;
import java.util.List;

public class GasTankItem extends Item {

    public static final int GAS_TANK_CAPACITY = 1000;
    public static final int LARGE_GAS_TANK_CAPACITY = 3000;
    public static final int GAS_TANK_DISTRIBUTION_AMOUNT = 10;
    public static final int LARGE_GAS_TANK_DISTRIBUTION_AMOUNT = 50;

    private static final int USE_DURATION = 72_000;
    private static final int OXYGEN_BAR_COLOR = 0x99ccff;

    private final int capacity;
    private final int distributionAmount;

    public GasTankItem(String name, int capacity, int distributionAmount) {
        this.capacity = capacity;
        this.distributionAmount = distributionAmount;
        setRegistryName(Reference.MOD_ID, name);
        setTranslationKey(Reference.MOD_ID + "." + name);
        setCreativeTab(AdAstraCreativeTab.INSTANCE);
        setMaxStackSize(1);
    }

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt) {
        return new OxygenFluidHandler(stack, capacity);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag) {
        tooltip.add(new TextComponentTranslation(
            "tooltip.ad_astra.gas_tank.oxygen",
            getStoredOxygen(stack),
            getOxygenCapacity(stack)).getFormattedText());
        tooltip.add(new TextComponentTranslation(
            "tooltip.ad_astra.gas_tank.max_out",
            distributionAmount).getFormattedText());
        tooltip.add(TextFormatting.GRAY + new TextComponentTranslation("info.ad_astra.gas_tank").getFormattedText());
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (getStoredOxygen(stack) <= 0) {
            return new ActionResult<>(EnumActionResult.PASS, stack);
        }
        player.setActiveHand(hand);
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }

    @Override
    public void onUsingTick(ItemStack stack, EntityLivingBase entity, int count) {
        if (entity.world.isRemote || !(entity instanceof EntityPlayer) || getStoredOxygen(stack) <= 0) {
            return;
        }

        EntityPlayer player = (EntityPlayer) entity;
        if (distributeSequential(stack, player) && entity.ticksExisted % 4 == 0) {
            entity.playSound(SoundEvents.ENTITY_GENERIC_DRINK, 0.4f, 1.6f);
        }
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return USE_DURATION;
    }

    @Override
    public EnumAction getItemUseAction(ItemStack stack) {
        return getStoredOxygen(stack) > 0 ? EnumAction.DRINK : EnumAction.NONE;
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        return getStoredOxygen(stack) > 0;
    }

    @Override
    public double getDurabilityForDisplay(ItemStack stack) {
        int stored = getStoredOxygen(stack);
        int tankCapacity = getOxygenCapacity(stack);
        return tankCapacity <= 0 ? 1.0d : 1.0d - (stored / (double) tankCapacity);
    }

    @Override
    public int getRGBDurabilityForDisplay(ItemStack stack) {
        return OXYGEN_BAR_COLOR;
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (!isInCreativeTab(tab)) {
            return;
        }
        items.add(new ItemStack(this));
        ItemStack filled = new ItemStack(this);
        IFluidHandlerItem handler = FluidUtil.getFluidHandler(filled);
        if (handler != null) {
            handler.fill(new FluidStack(ModFluids.OXYGEN, capacity), true);
            items.add(handler.getContainer());
        }
    }

    public int getCapacity() {
        return capacity;
    }

    public int getDistributionAmount() {
        return distributionAmount;
    }

    public static boolean canSupplyOxygen(ItemStack stack) {
        return getStoredOxygen(stack) > 0;
    }

    public static int getStoredOxygen(ItemStack stack) {
        return AdAstraFluidHelper.getOxygenAmountWithBackup(stack, FluidUtil.getFluidContained(stack));
    }

    public static int getOxygenCapacity(ItemStack stack) {
        ItemStack single = stack.copy();
        single.setCount(1);
        IFluidHandlerItem handler = FluidUtil.getFluidHandler(single);
        if (handler == null) {
            return 0;
        }
        IFluidTankProperties[] properties = handler.getTankProperties();
        return properties.length == 0 ? 0 : properties[0].getCapacity();
    }

    public static int drainOxygen(ItemStack stack, int maxAmount, boolean simulate) {
        if (stack.isEmpty() || maxAmount <= 0) {
            return 0;
        }
        IFluidHandlerItem handler = FluidUtil.getFluidHandler(stack);
        if (handler == null) {
            return 0;
        }
        FluidStack drained = handler.drain(new FluidStack(ModFluids.OXYGEN, maxAmount), !simulate);
        return drained == null ? 0 : drained.amount;
    }

    private boolean distributeSequential(ItemStack sourceStack, EntityPlayer player) {
        IFluidHandlerItem source = FluidUtil.getFluidHandler(sourceStack);
        if (source == null) {
            return false;
        }

        FluidStack request = new FluidStack(ModFluids.OXYGEN, distributionAmount);
        for (int i = player.inventory.getSizeInventory() - 1; i >= 0; i--) {
            ItemStack targetStack = player.inventory.getStackInSlot(i);
            if (!canDistributeTo(sourceStack, targetStack)) {
                continue;
            }

            IFluidHandlerItem target = FluidUtil.getFluidHandler(targetStack);
            if (target == null) {
                continue;
            }

            FluidStack moved = FluidUtil.tryFluidTransfer(target, source, request, true);
            if (moved != null && moved.amount > 0) {
                player.inventory.setInventorySlotContents(i, target.getContainer());
                player.inventory.markDirty();
                return true;
            }
        }
        return false;
    }

    private boolean canDistributeTo(ItemStack sourceStack, ItemStack targetStack) {
        return !targetStack.isEmpty()
            && targetStack != sourceStack
            && targetStack.getItem() != this
            && targetStack.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
    }

    private static class OxygenFluidHandler extends FluidHandlerItemStack {

        private OxygenFluidHandler(ItemStack container, int capacity) {
            super(container, capacity);
            AdAstraFluidHelper.migrateItemFluidTag(container);
        }

        @Override
        public FluidStack getFluid() {
            FluidStack fluid = AdAstraFluidHelper.normalizeFluidStack(super.getFluid());
            if (AdAstraFluidHelper.isOxygen(fluid)) {
                AdAstraFluidHelper.setOxygenBackupAmount(container, fluid.amount);
                return fluid;
            }
            FluidStack restored = AdAstraFluidHelper.restoreOxygenFromBackup(container, capacity);
            if (restored != null) {
                super.setFluid(restored);
            }
            return restored;
        }

        @Override
        protected void setFluid(FluidStack fluid) {
            if (fluid == null) {
                if (container.hasTagCompound()) {
                    container.getTagCompound().removeTag(FLUID_NBT_KEY);
                }
                AdAstraFluidHelper.setOxygenBackupAmount(container, 0);
                return;
            }
            FluidStack normalized = AdAstraFluidHelper.normalizeFluidStack(fluid);
            super.setFluid(normalized);
            AdAstraFluidHelper.setOxygenBackupAmount(container, AdAstraFluidHelper.isOxygen(normalized) ? normalized.amount : 0);
        }

        @Override
        public boolean canFillFluidType(FluidStack fluid) {
            return AdAstraFluidHelper.isOxygen(fluid);
        }

        @Override
        public boolean canDrainFluidType(FluidStack fluid) {
            return AdAstraFluidHelper.isOxygen(fluid);
        }
    }
}
