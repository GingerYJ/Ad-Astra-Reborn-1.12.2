package earth.terrarium.adastra.common.items;

import earth.terrarium.adastra.Reference;
import earth.terrarium.adastra.client.render.ModelSpaceSuitArmor;
import earth.terrarium.adastra.common.AdAstraCreativeTab;
import earth.terrarium.adastra.common.registry.ModFluids;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
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
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.util.EnumFacing;

import javax.annotation.Nullable;
import java.util.List;

public class AdAstraArmorItem extends ItemArmor {

    private static final int OXYGEN_BAR_COLOR = 0x99ccff;
    private static final int ENERGY_BAR_COLOR = 0xffdd66;
    private static final int JET_SUIT_MAX_ENERGY_IN = 1000;

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
    @SideOnly(Side.CLIENT)
    public ModelBiped getArmorModel(EntityLivingBase entityLiving, ItemStack stack, EntityEquipmentSlot slot, ModelBiped defaultModel) {
        ModelBiped model = ArmorModels.get(suitMaterial, slot);
        model.setModelAttributes(defaultModel);
        return model;
    }

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt) {
        if (isJetSuitChestPiece()) {
            return new JetSuitCapabilityProvider(stack, suitMaterial.oxygenCapacity, suitMaterial.energyCapacity);
        }
        return isOxygenChestPiece() ? new OxygenSuitFluidHandler(stack, suitMaterial.oxygenCapacity) : super.initCapabilities(stack, nbt);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag) {
        if (isOxygenChestPiece()) {
            tooltip.add(new TextComponentTranslation(
                "tooltip.ad_astra.gas_tank.oxygen",
                GasTankItem.getStoredOxygen(stack),
                GasTankItem.getOxygenCapacity(stack)).getFormattedText());
        }
        if (hasEnergyStorage()) {
            tooltip.add(new TextComponentTranslation(
                "tooltip.ad_astra.energy_stored",
                getEnergyStored(stack),
                suitMaterial.energyCapacity).getFormattedText());
            tooltip.add(new TextComponentTranslation(
                "tooltip.ad_astra.max_energy_in",
                JET_SUIT_MAX_ENERGY_IN).getFormattedText());
        }
        if (isOxygenChestPiece()) {
            tooltip.add(TextFormatting.GRAY + new TextComponentTranslation("info.ad_astra.space_suit.oxygen").getFormattedText());
        }
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        return (isOxygenChestPiece() && GasTankItem.getOxygenCapacity(stack) > 0) || (hasEnergyStorage() && getEnergyStored(stack) > 0);
    }

    @Override
    public double getDurabilityForDisplay(ItemStack stack) {
        int stored = isOxygenChestPiece() ? GasTankItem.getStoredOxygen(stack) : getEnergyStored(stack);
        int capacity = isOxygenChestPiece() ? GasTankItem.getOxygenCapacity(stack) : suitMaterial.energyCapacity;
        return capacity <= 0 ? 1.0d : 1.0d - (stored / (double) capacity);
    }

    @Override
    public int getRGBDurabilityForDisplay(ItemStack stack) {
        return isOxygenChestPiece() ? OXYGEN_BAR_COLOR : ENERGY_BAR_COLOR;
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
        if (hasEnergyStorage()) {
            ItemStack charged = new ItemStack(this);
            receiveEnergy(charged, suitMaterial.energyCapacity, false);
            items.add(charged);

            if (isOxygenChestPiece()) {
                ItemStack ready = charged.copy();
                IFluidHandlerItem handler = FluidUtil.getFluidHandler(ready);
                if (handler != null) {
                    handler.fill(new FluidStack(ModFluids.OXYGEN, suitMaterial.oxygenCapacity), true);
                    items.add(handler.getContainer());
                }
            }
        }
    }

    public boolean isOxygenChestPiece() {
        return armorType == EntityEquipmentSlot.CHEST && suitMaterial.oxygenCapacity > 0;
    }

    public boolean isJetSuitChestPiece() {
        return hasEnergyStorage();
    }

    public boolean hasEnergyStorage() {
        return armorType == EntityEquipmentSlot.CHEST && suitMaterial.energyCapacity > 0;
    }

    public static boolean isJetSuitChest(ItemStack stack) {
        return !stack.isEmpty() && stack.getItem() instanceof AdAstraArmorItem && ((AdAstraArmorItem) stack.getItem()).isJetSuitChestPiece();
    }

    public static int getJetSuitEnergyStored(ItemStack stack) {
        if (!isJetSuitChest(stack)) {
            return 0;
        }
        return ((AdAstraArmorItem) stack.getItem()).getEnergyStored(stack);
    }

    public static int consumeJetSuitEnergy(ItemStack stack, int amount, boolean simulate) {
        if (!isJetSuitChest(stack) || amount <= 0) {
            return 0;
        }
        AdAstraArmorItem item = (AdAstraArmorItem) stack.getItem();
        return new ItemStackEnergyStorage(stack, item.suitMaterial.energyCapacity, JET_SUIT_MAX_ENERGY_IN, amount).extractEnergy(amount, simulate);
    }

    private int getEnergyStored(ItemStack stack) {
        return new ItemStackEnergyStorage(stack, suitMaterial.energyCapacity, JET_SUIT_MAX_ENERGY_IN, 0).getEnergyStored();
    }

    private int receiveEnergy(ItemStack stack, int amount, boolean simulate) {
        return new ItemStackEnergyStorage(stack, suitMaterial.energyCapacity, suitMaterial.energyCapacity, 0).receiveEnergy(amount, simulate);
    }

    public enum SuitMaterial {
        SPACE("space_suit", 37, new int[]{2, 6, 5, 2}, 14, SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, 0.0f, 1000, 0),
        NETHERITE_SPACE("netherite_space_suit", 37, new int[]{3, 8, 6, 3}, 15, SoundEvents.ITEM_ARMOR_EQUIP_IRON, 3.0f, 2000, 0),
        JET("jet_suit", 37, new int[]{4, 9, 7, 4}, 15, SoundEvents.ITEM_ARMOR_EQUIP_IRON, 5.0f, 3000, 1_000_000);

        private final ArmorMaterial armorMaterial;
        private final String texture;
        private final int oxygenCapacity;
        private final int energyCapacity;

        SuitMaterial(String texture, int durability, int[] reductions, int enchantability, SoundEvent equipSound, float toughness, int oxygenCapacity, int energyCapacity) {
            this.texture = texture;
            this.oxygenCapacity = oxygenCapacity;
            this.energyCapacity = energyCapacity;
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

    private static final class JetSuitCapabilityProvider implements ICapabilityProvider {

        private final OxygenSuitFluidHandler fluidHandler;
        private final IEnergyStorage energyStorage;

        private JetSuitCapabilityProvider(ItemStack stack, int oxygenCapacity, int energyCapacity) {
            this.fluidHandler = new OxygenSuitFluidHandler(stack, oxygenCapacity);
            this.energyStorage = new ItemStackEnergyStorage(stack, energyCapacity, JET_SUIT_MAX_ENERGY_IN, 0);
        }

        @Override
        public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
            return capability == CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY || capability == CapabilityEnergy.ENERGY;
        }

        @Override
        @SuppressWarnings("unchecked")
        public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
            if (capability == CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY) {
                return (T) fluidHandler;
            }
            if (capability == CapabilityEnergy.ENERGY) {
                return (T) energyStorage;
            }
            return null;
        }
    }

    @SideOnly(Side.CLIENT)
    private static final class ArmorModels {
        private static final ModelBiped SPACE_HEAD = new ModelSpaceSuitArmor(SuitMaterial.SPACE, EntityEquipmentSlot.HEAD);
        private static final ModelBiped SPACE_CHEST = new ModelSpaceSuitArmor(SuitMaterial.SPACE, EntityEquipmentSlot.CHEST);
        private static final ModelBiped SPACE_LEGS = new ModelSpaceSuitArmor(SuitMaterial.SPACE, EntityEquipmentSlot.LEGS);
        private static final ModelBiped SPACE_FEET = new ModelSpaceSuitArmor(SuitMaterial.SPACE, EntityEquipmentSlot.FEET);
        private static final ModelBiped NETHERITE_HEAD = new ModelSpaceSuitArmor(SuitMaterial.NETHERITE_SPACE, EntityEquipmentSlot.HEAD);
        private static final ModelBiped NETHERITE_CHEST = new ModelSpaceSuitArmor(SuitMaterial.NETHERITE_SPACE, EntityEquipmentSlot.CHEST);
        private static final ModelBiped NETHERITE_LEGS = new ModelSpaceSuitArmor(SuitMaterial.NETHERITE_SPACE, EntityEquipmentSlot.LEGS);
        private static final ModelBiped NETHERITE_FEET = new ModelSpaceSuitArmor(SuitMaterial.NETHERITE_SPACE, EntityEquipmentSlot.FEET);
        private static final ModelBiped JET_HEAD = new ModelSpaceSuitArmor(SuitMaterial.JET, EntityEquipmentSlot.HEAD);
        private static final ModelBiped JET_CHEST = new ModelSpaceSuitArmor(SuitMaterial.JET, EntityEquipmentSlot.CHEST);
        private static final ModelBiped JET_LEGS = new ModelSpaceSuitArmor(SuitMaterial.JET, EntityEquipmentSlot.LEGS);
        private static final ModelBiped JET_FEET = new ModelSpaceSuitArmor(SuitMaterial.JET, EntityEquipmentSlot.FEET);

        private ArmorModels() {
        }

        private static ModelBiped get(SuitMaterial material, EntityEquipmentSlot slot) {
            switch (material) {
                case NETHERITE_SPACE:
                    return bySlot(slot, NETHERITE_HEAD, NETHERITE_CHEST, NETHERITE_LEGS, NETHERITE_FEET);
                case JET:
                    return bySlot(slot, JET_HEAD, JET_CHEST, JET_LEGS, JET_FEET);
                case SPACE:
                default:
                    return bySlot(slot, SPACE_HEAD, SPACE_CHEST, SPACE_LEGS, SPACE_FEET);
            }
        }

        private static ModelBiped bySlot(EntityEquipmentSlot slot, ModelBiped head, ModelBiped chest, ModelBiped legs, ModelBiped feet) {
            switch (slot) {
                case HEAD:
                    return head;
                case CHEST:
                    return chest;
                case LEGS:
                    return legs;
                case FEET:
                    return feet;
                default:
                    return chest;
            }
        }
    }
}
