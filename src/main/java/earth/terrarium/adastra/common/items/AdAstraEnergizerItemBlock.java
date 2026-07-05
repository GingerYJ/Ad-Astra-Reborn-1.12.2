package earth.terrarium.adastra.common.items;

import earth.terrarium.adastra.common.AdAstraCreativeTab;
import earth.terrarium.adastra.common.config.AdAstraConfig;
import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.energy.CapabilityEnergy;

import javax.annotation.Nullable;
import java.util.List;

public class AdAstraEnergizerItemBlock extends ItemBlock {

    private static final String ENERGY_TAG = "Energy";
    private static final int ITEM_ENERGY_IO = 1000;
    private static final int ENERGY_BAR_COLOR = 0x63dcc2;

    public AdAstraEnergizerItemBlock(Block block) {
        super(block);
        setCreativeTab(AdAstraCreativeTab.INSTANCE);
        setMaxStackSize(1);
    }

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt) {
        return new EnergyProvider(stack);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag) {
        ItemStackEnergyStorage storage = createStorage(stack);
        tooltip.add(new TextComponentTranslation(
            "tooltip.ad_astra.energy_stored",
            storage.getEnergyStored(),
            storage.getMaxEnergyStored()).getFormattedText());
        tooltip.add(new TextComponentTranslation("tooltip.ad_astra.max_energy_in", ITEM_ENERGY_IO).getFormattedText());
        tooltip.add(new TextComponentTranslation("tooltip.ad_astra.max_energy_out", ITEM_ENERGY_IO).getFormattedText());
        tooltip.add(TextFormatting.GRAY + new TextComponentTranslation("info.ad_astra.energizer").getFormattedText());
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        return getEnergyStored(stack) > 0;
    }

    @Override
    public double getDurabilityForDisplay(ItemStack stack) {
        int capacity = AdAstraConfig.energizerEnergyCapacity;
        return capacity <= 0 ? 1.0d : 1.0d - (getEnergyStored(stack) / (double) capacity);
    }

    @Override
    public int getRGBDurabilityForDisplay(ItemStack stack) {
        return ENERGY_BAR_COLOR;
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (!isInCreativeTab(tab)) {
            return;
        }

        items.add(new ItemStack(this));
        ItemStack charged = new ItemStack(this);
        setEnergyStored(charged, AdAstraConfig.energizerEnergyCapacity);
        items.add(charged);
    }

    public static int getEnergyStored(ItemStack stack) {
        return createStorage(stack).getEnergyStored();
    }

    public static void setEnergyStored(ItemStack stack, int energy) {
        NBTTagCompound tag = stack.getTagCompound();
        if (tag == null) {
            tag = new NBTTagCompound();
            stack.setTagCompound(tag);
        }
        tag.setInteger(ENERGY_TAG, Math.max(0, Math.min(AdAstraConfig.energizerEnergyCapacity, energy)));
    }

    private static ItemStackEnergyStorage createStorage(ItemStack stack) {
        return new ItemStackEnergyStorage(stack, AdAstraConfig.energizerEnergyCapacity, ITEM_ENERGY_IO, ITEM_ENERGY_IO);
    }

    private static class EnergyProvider implements ICapabilityProvider {

        private final ItemStackEnergyStorage storage;

        private EnergyProvider(ItemStack stack) {
            this.storage = createStorage(stack);
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
