package earth.terrarium.adastra.common.items;

import earth.terrarium.adastra.Reference;
import earth.terrarium.adastra.common.AdAstraCreativeTab;
import earth.terrarium.adastra.common.registry.ModFluids;
import earth.terrarium.adastra.common.util.AdAstraFluidHelper;
import earth.terrarium.adastra.common.util.EnvironmentUtils;
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
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStack;

import javax.annotation.Nullable;
import java.util.List;

public class ZipGunItem extends Item {

    public static final int CAPACITY = 3000;

    private static final int USE_DURATION = 72_000;
    private static final int FUEL_PER_TICK = 1;
    private static final int BAR_COLOR = 0x99ccff;
    /** 重力倍率低于此值视为“低重力”，压缩枪推进大幅增强。地球=1.0，月球≈0.165、火星≈0.38。 */
    private static final float LOW_GRAVITY_RATIO_THRESHOLD = 0.5F;

    public ZipGunItem(String name) {
        setRegistryName(Reference.MOD_ID, name);
        setTranslationKey(Reference.MOD_ID + "." + name);
        setCreativeTab(AdAstraCreativeTab.INSTANCE);
        setMaxStackSize(1);
    }

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt) {
        return new PropellantHandler(stack);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag) {
        FluidStack propellant = FluidUtil.getFluidContained(stack);
        int amount = propellant == null ? 0 : propellant.amount;
        String fluidName = propellant == null
            ? new TextComponentTranslation("hud.ad_astra.none").getFormattedText()
            : propellant.getLocalizedName();
        tooltip.add(new TextComponentTranslation("tooltip.ad_astra.zip_gun.propellant", amount, CAPACITY, fluidName).getFormattedText());
        tooltip.add(TextFormatting.GRAY + new TextComponentTranslation("info.ad_astra.zip_gun").getFormattedText());
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (player.capabilities.isCreativeMode || hasPropellant(stack)) {
            player.setActiveHand(hand);
            return new ActionResult<>(EnumActionResult.SUCCESS, stack);
        }
        return new ActionResult<>(EnumActionResult.PASS, stack);
    }

    @Override
    public void onUsingTick(ItemStack stack, EntityLivingBase entity, int count) {
        if (!(entity instanceof EntityPlayer)) {
            return;
        }

        EntityPlayer player = (EntityPlayer) entity;
        boolean mainHandBoost = consumeOrPreview(player, player.getHeldItemMainhand(), FUEL_PER_TICK);
        boolean offHandBoost = consumeOrPreview(player, player.getHeldItemOffhand(), FUEL_PER_TICK);
        if (!mainHandBoost && !offHandBoost && !player.capabilities.isCreativeMode) {
            player.stopActiveHand();
            return;
        }

        propel(player, mainHandBoost, offHandBoost);
        if (!player.world.isRemote && player.ticksExisted % 8 == 0) {
            player.world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_BLAZE_SHOOT, SoundCategory.PLAYERS, 0.25F, 1.85F);
        }
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return USE_DURATION;
    }

    @Override
    public EnumAction getItemUseAction(ItemStack stack) {
        return EnumAction.BLOCK;
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        return getStoredPropellant(stack) > 0;
    }

    @Override
    public double getDurabilityForDisplay(ItemStack stack) {
        return 1.0D - (getStoredPropellant(stack) / (double) CAPACITY);
    }

    @Override
    public int getRGBDurabilityForDisplay(ItemStack stack) {
        return BAR_COLOR;
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (!isInCreativeTab(tab)) {
            return;
        }
        items.add(new ItemStack(this));
        addFilledVariant(items, ModFluids.OXYGEN);
        addFilledVariant(items, ModFluids.HYDROGEN);
    }

    public static int getStoredPropellant(ItemStack stack) {
        FluidStack contained = AdAstraFluidHelper.normalizeFluidStack(FluidUtil.getFluidContained(stack));
        return isValidPropellant(contained) ? contained.amount : 0;
    }

    public static boolean isValidPropellant(FluidStack fluid) {
        return fluid != null && isValidPropellant(fluid.getFluid());
    }

    private static boolean isValidPropellant(Fluid fluid) {
        return AdAstraFluidHelper.isOxygen(fluid) || AdAstraFluidHelper.isHydrogen(fluid);
    }

    private static boolean hasPropellant(ItemStack stack) {
        return getStoredPropellant(stack) > 0;
    }

    private static boolean consumeOrPreview(EntityPlayer player, ItemStack stack, int amount) {
        if (stack.isEmpty() || !(stack.getItem() instanceof ZipGunItem)) {
            return false;
        }
        if (player.capabilities.isCreativeMode) {
            return true;
        }

        FluidStack contained = FluidUtil.getFluidContained(stack);
        if (!isValidPropellant(contained)) {
            return false;
        }
        if (player.world.isRemote) {
            return contained.amount >= amount;
        }

        IFluidHandlerItem handler = FluidUtil.getFluidHandler(stack);
        if (handler == null) {
            return false;
        }
        FluidStack drained = handler.drain(new FluidStack(contained.getFluid(), amount), true);
        return drained != null && drained.amount > 0;
    }

    private static void propel(EntityPlayer player, boolean mainHandBoost, boolean offHandBoost) {
        double maxSpeed = 0.35D;
        double propelForce = 0.2D;
        double propelYForce = 0.2D;

        boolean lowGravity = EnvironmentUtils.getGravityRatio(player) <= LOW_GRAVITY_RATIO_THRESHOLD;
        if (lowGravity) {
            propelForce *= 0.1D;
            propelYForce *= 0.1D;
            maxSpeed *= 20.0D;
            player.fallDistance *= 0.9F;
        } else {
            propelYForce *= 0.2D;
            propelYForce *= 1.0D - Math.min(1.0D, player.posY / 90.0D);
        }

        if (mainHandBoost && offHandBoost) {
            propelForce *= 1.4D;
            propelYForce *= 1.25D;
            maxSpeed *= 1.5D;
            player.fallDistance *= 0.9F;
        }

        Vec3d look = player.getLookVec();
        double currentSpeed = Math.sqrt(player.motionX * player.motionX + player.motionY * player.motionY + player.motionZ * player.motionZ);
        if (currentSpeed < maxSpeed) {
            player.motionX += look.x * propelForce;
            player.motionY += look.y * propelYForce;
            player.motionZ += look.z * propelForce;
            player.velocityChanged = true;
        }
    }

    private static void addFilledVariant(NonNullList<ItemStack> items, Fluid fluid) {
        ItemStack filled = new ItemStack(items.get(0).getItem());
        IFluidHandlerItem handler = FluidUtil.getFluidHandler(filled);
        if (handler != null) {
            handler.fill(new FluidStack(fluid, CAPACITY), true);
            items.add(handler.getContainer());
        }
    }

    private static class PropellantHandler extends FluidHandlerItemStack {

        private PropellantHandler(ItemStack container) {
            super(container, CAPACITY);
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
            return restored == null ? fluid : restored;
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
            return isValidPropellant(fluid);
        }

        @Override
        public boolean canDrainFluidType(FluidStack fluid) {
            return isValidPropellant(fluid);
        }
    }
}
