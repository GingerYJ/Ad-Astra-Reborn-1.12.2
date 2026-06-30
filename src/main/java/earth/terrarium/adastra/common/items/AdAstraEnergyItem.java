package earth.terrarium.adastra.common.items;

import earth.terrarium.adastra.Reference;
import earth.terrarium.adastra.common.AdAstraCreativeTab;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

import javax.annotation.Nullable;
import java.util.List;

public class AdAstraEnergyItem extends Item {

    private static final String ACTIVE_TAG = "Active";
    private static final String MODE_TAG = "Mode";

    private final int capacity;
    private final int maxReceive;
    private final int maxExtract;

    public AdAstraEnergyItem(String name, int capacity, int maxReceive, int maxExtract) {
        this.capacity = capacity;
        this.maxReceive = maxReceive;
        this.maxExtract = maxExtract;
        setRegistryName(Reference.MOD_ID, name);
        setTranslationKey(Reference.MOD_ID + "." + name);
        setCreativeTab(AdAstraCreativeTab.INSTANCE);
        setMaxStackSize(1);
    }

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt) {
        return new EnergyProvider(stack, capacity, maxReceive, maxExtract);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag) {
        ItemStackEnergyStorage storage = new ItemStackEnergyStorage(stack, capacity, maxReceive, maxExtract);
        tooltip.add(String.format("%d / %d FE", storage.getEnergyStored(), storage.getMaxEnergyStored()));
        tooltip.add(new TextComponentTranslation(isActive(stack) ? "tooltip.ad_astra.capacitor.enabled" : "tooltip.ad_astra.capacitor.disabled").getFormattedText());
        tooltip.add(new TextComponentTranslation(getMode(stack) == DistributionMode.SEQUENTIAL ? "tooltip.ad_astra.distribution_mode.sequential" : "tooltip.ad_astra.distribution_mode.round_robin").getFormattedText());
        tooltip.add(String.format("Max In: %d FE/t", maxReceive));
        tooltip.add(String.format("Max Out: %d FE/t", maxExtract));
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (!world.isRemote) {
            if (player.isSneaking()) {
                DistributionMode mode = toggleMode(stack);
                player.sendStatusMessage(new TextComponentTranslation(mode == DistributionMode.SEQUENTIAL
                    ? "tooltip.ad_astra.distribution_mode.sequential"
                    : "tooltip.ad_astra.distribution_mode.round_robin"), true);
            } else {
                boolean active = toggleActive(stack);
                player.sendStatusMessage(new TextComponentTranslation(active
                    ? "tooltip.ad_astra.capacitor.enabled"
                    : "tooltip.ad_astra.capacitor.disabled"), true);
            }
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected) {
        if (world.isRemote || world.getTotalWorldTime() % 5 != 0 || !isActive(stack) || !(entity instanceof EntityPlayer)) {
            return;
        }
        IEnergyStorage capacitor = stack.getCapability(CapabilityEnergy.ENERGY, null);
        if (capacitor == null || capacitor.getEnergyStored() <= 0) {
            return;
        }

        if (getMode(stack) == DistributionMode.ROUND_ROBIN) {
            distributeRoundRobin(stack, (EntityPlayer) entity, capacitor);
        } else {
            distributeSequential(stack, (EntityPlayer) entity, capacitor);
        }
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        return getEnergyStored(stack) > 0;
    }

    @Override
    public double getDurabilityForDisplay(ItemStack stack) {
        return 1.0d - (getEnergyStored(stack) / (double) capacity);
    }

    private int getEnergyStored(ItemStack stack) {
        return new ItemStackEnergyStorage(stack, capacity, maxReceive, maxExtract).getEnergyStored();
    }

    private boolean isActive(ItemStack stack) {
        NBTTagCompound tag = stack.getTagCompound();
        return tag == null || !tag.hasKey(ACTIVE_TAG) || tag.getBoolean(ACTIVE_TAG);
    }

    private boolean toggleActive(ItemStack stack) {
        NBTTagCompound tag = getOrCreateTag(stack);
        boolean active = !isActive(stack);
        tag.setBoolean(ACTIVE_TAG, active);
        return active;
    }

    private DistributionMode getMode(ItemStack stack) {
        NBTTagCompound tag = stack.getTagCompound();
        if (tag == null || !tag.hasKey(MODE_TAG)) {
            return DistributionMode.SEQUENTIAL;
        }
        return DistributionMode.byOrdinal(tag.getByte(MODE_TAG));
    }

    private DistributionMode toggleMode(ItemStack stack) {
        DistributionMode next = getMode(stack) == DistributionMode.SEQUENTIAL ? DistributionMode.ROUND_ROBIN : DistributionMode.SEQUENTIAL;
        getOrCreateTag(stack).setByte(MODE_TAG, (byte) next.ordinal());
        return next;
    }

    private NBTTagCompound getOrCreateTag(ItemStack stack) {
        NBTTagCompound tag = stack.getTagCompound();
        if (tag == null) {
            tag = new NBTTagCompound();
            stack.setTagCompound(tag);
        }
        return tag;
    }

    private void distributeSequential(ItemStack capacitorStack, EntityPlayer player, IEnergyStorage capacitor) {
        for (int i = player.inventory.getSizeInventory() - 1; i >= 0; i--) {
            ItemStack target = player.inventory.getStackInSlot(i);
            if (canChargeTarget(capacitorStack, target)) {
                if (moveEnergy(capacitor, target, maxExtract * 5) > 0) {
                    player.inventory.markDirty();
                    return;
                }
            }
        }
    }

    private void distributeRoundRobin(ItemStack capacitorStack, EntityPlayer player, IEnergyStorage capacitor) {
        int targets = 0;
        for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
            if (canChargeTarget(capacitorStack, player.inventory.getStackInSlot(i))) {
                targets++;
            }
        }
        if (targets <= 0) {
            return;
        }

        int amountPerTarget = Math.max(1, (maxExtract * 5) / targets);
        for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
            ItemStack target = player.inventory.getStackInSlot(i);
            if (canChargeTarget(capacitorStack, target)) {
                moveEnergy(capacitor, target, amountPerTarget);
            }
        }
        player.inventory.markDirty();
    }

    private boolean canChargeTarget(ItemStack capacitorStack, ItemStack target) {
        return !target.isEmpty() && target != capacitorStack && target.hasCapability(CapabilityEnergy.ENERGY, null);
    }

    private int moveEnergy(IEnergyStorage source, ItemStack target, int maxAmount) {
        IEnergyStorage targetEnergy = target.getCapability(CapabilityEnergy.ENERGY, null);
        if (targetEnergy == null || !targetEnergy.canReceive()) {
            return 0;
        }
        int extracted = source.extractEnergy(maxAmount, true);
        if (extracted <= 0) {
            return 0;
        }
        int accepted = targetEnergy.receiveEnergy(extracted, false);
        if (accepted > 0) {
            source.extractEnergy(accepted, false);
        }
        return accepted;
    }

    private enum DistributionMode {
        SEQUENTIAL,
        ROUND_ROBIN;

        private static DistributionMode byOrdinal(int ordinal) {
            DistributionMode[] values = values();
            return ordinal >= 0 && ordinal < values.length ? values[ordinal] : SEQUENTIAL;
        }
    }

    private static class EnergyProvider implements ICapabilityProvider {

        private final ItemStackEnergyStorage storage;

        private EnergyProvider(ItemStack stack, int capacity, int maxReceive, int maxExtract) {
            this.storage = new ItemStackEnergyStorage(stack, capacity, maxReceive, maxExtract);
        }

        @Override
        public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
            return capability == CapabilityEnergy.ENERGY;
        }

        @Override
        @SuppressWarnings("unchecked")
        public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
            return capability == CapabilityEnergy.ENERGY ? (T) storage : null;
        }
    }
}
