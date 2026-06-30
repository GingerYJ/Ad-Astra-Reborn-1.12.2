package earth.terrarium.adastra.common.items;

import earth.terrarium.adastra.Reference;
import earth.terrarium.adastra.common.AdAstraCreativeTab;
import earth.terrarium.adastra.common.registry.ModFluids;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStack;

import javax.annotation.Nullable;
import java.util.List;

public class AdAstraArmorItem extends ItemArmor {

    private static final int OXYGEN_BAR_COLOR = 0x99ccff;

    private final SuitMaterial suitMaterial;
    private final String texture;

    public AdAstraArmorItem(String name, SuitMaterial suitMaterial, EntityEquipmentSlot slot) {
        super(suitMaterial.armorMaterial, 0, slot);
        this.suitMaterial = suitMaterial;
        this.texture = suitMaterial.texture;
        setRegistryName(Reference.MOD_ID, name);
        setTranslationKey(Reference.MOD_ID + "." + name);
        setCreativeTab(AdAstraCreativeTab.INSTANCE);
        setMaxStackSize(1);
    }

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
        return Reference.MOD_ID + ":textures/entity/armor/" + texture + ".png";
    }

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt) {
        return isOxygenChestPiece() ? new OxygenSuitFluidHandler(stack, suitMaterial.oxygenCapacity) : super.initCapabilities(stack, nbt);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag) {
        if (isOxygenChestPiece()) {
            tooltip.add(new TextComponentTranslation(
                "tooltip.ad_astra.gas_tank.oxygen",
                GasTankItem.getStoredOxygen(stack),
                GasTankItem.getOxygenCapacity(stack)).getFormattedText());
            tooltip.add(TextFormatting.GRAY + new TextComponentTranslation("info.ad_astra.space_suit.oxygen").getFormattedText());
        }
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        return isOxygenChestPiece() && GasTankItem.getOxygenCapacity(stack) > 0;
    }

    @Override
    public double getDurabilityForDisplay(ItemStack stack) {
        int stored = GasTankItem.getStoredOxygen(stack);
        int capacity = GasTankItem.getOxygenCapacity(stack);
        return capacity <= 0 ? 1.0d : 1.0d - (stored / (double) capacity);
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
        if (isOxygenChestPiece()) {
            ItemStack filled = new ItemStack(this);
            IFluidHandlerItem handler = FluidUtil.getFluidHandler(filled);
            if (handler != null) {
                handler.fill(new FluidStack(ModFluids.OXYGEN, suitMaterial.oxygenCapacity), true);
                items.add(handler.getContainer());
            }
        }
    }

    public boolean isOxygenChestPiece() {
        return armorType == EntityEquipmentSlot.CHEST && suitMaterial.oxygenCapacity > 0;
    }

    public enum SuitMaterial {
        SPACE("space_suit", 37, new int[]{2, 6, 5, 2}, 14, SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, 0.0f, 1000),
        NETHERITE_SPACE("netherite_space_suit", 37, new int[]{3, 8, 6, 3}, 15, SoundEvents.ITEM_ARMOR_EQUIP_IRON, 3.0f, 2000),
        JET("jet_suit", 37, new int[]{4, 9, 7, 4}, 15, SoundEvents.ITEM_ARMOR_EQUIP_IRON, 5.0f, 3000);

        private final ArmorMaterial armorMaterial;
        private final String texture;
        private final int oxygenCapacity;

        SuitMaterial(String texture, int durability, int[] reductions, int enchantability, SoundEvent equipSound, float toughness, int oxygenCapacity) {
            this.texture = texture;
            this.oxygenCapacity = oxygenCapacity;
            this.armorMaterial = EnumHelper.addArmorMaterial(
                Reference.MOD_ID + "_" + texture,
                Reference.MOD_ID + ":" + texture,
                durability,
                reductions,
                enchantability,
                equipSound,
                toughness);
        }
    }

    private static final class OxygenSuitFluidHandler extends FluidHandlerItemStack {

        private OxygenSuitFluidHandler(ItemStack container, int capacity) {
            super(container, capacity);
        }

        @Override
        public boolean canFillFluidType(FluidStack fluid) {
            return fluid != null && fluid.getFluid() == ModFluids.OXYGEN;
        }

        @Override
        public boolean canDrainFluidType(FluidStack fluid) {
            return fluid != null && fluid.getFluid() == ModFluids.OXYGEN;
        }
    }
}
