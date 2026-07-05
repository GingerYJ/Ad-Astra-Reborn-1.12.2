package earth.terrarium.adastra.common.items;

import earth.terrarium.adastra.common.registry.ModItems;
import earth.terrarium.adastra.common.systems.OxygenSystem;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class SpaceSuitItem extends AdAstraArmorItem {

    public SpaceSuitItem(String name, EntityEquipmentSlot slot) {
        this(name, SpaceSuitMaterial.SPACE, slot);
    }

    protected SpaceSuitItem(String name, SpaceSuitMaterial material, EntityEquipmentSlot slot) {
        super(name, material, slot);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag) {
        addOxygenTooltip(stack, tooltip);
        addSuitInfoTooltip(stack, tooltip, getSuitInfoTranslationKey());
    }

    @Override
    public void onArmorTick(World world, EntityPlayer player, ItemStack stack) {
        super.onArmorTick(world, player, stack);
        if (world.isRemote || !isOxygenChestPiece() || player.getItemStackFromSlot(EntityEquipmentSlot.CHEST) != stack) {
            return;
        }
        OxygenSystem.handleUnderwaterSpaceSuitBreathing(player);
    }

    @Override
    protected String getSuitInfoTranslationKey() {
        return "info.ad_astra.space_suit";
    }

    public static boolean hasFullSet(EntityLivingBase entity) {
        return isSpaceSuitItem(entity, EntityEquipmentSlot.HEAD)
            && isSpaceSuitItem(entity, EntityEquipmentSlot.CHEST)
            && isSpaceSuitItem(entity, EntityEquipmentSlot.LEGS)
            && isSpaceSuitItem(entity, EntityEquipmentSlot.FEET);
    }

    public static boolean hasFullNetheriteSet(EntityLivingBase entity) {
        return isNetheriteSpaceSuitItem(entity, EntityEquipmentSlot.HEAD)
            && isNetheriteSpaceSuitItem(entity, EntityEquipmentSlot.CHEST)
            && isNetheriteSpaceSuitItem(entity, EntityEquipmentSlot.LEGS)
            && isNetheriteSpaceSuitItem(entity, EntityEquipmentSlot.FEET);
    }

    public static boolean hasFullJetSuitSet(EntityLivingBase entity) {
        return isJetSuitItem(entity, EntityEquipmentSlot.HEAD)
            && isJetSuitItem(entity, EntityEquipmentSlot.CHEST)
            && isJetSuitItem(entity, EntityEquipmentSlot.LEGS)
            && isJetSuitItem(entity, EntityEquipmentSlot.FEET);
    }

    public static boolean hasFullHeatResistantSet(EntityLivingBase entity) {
        return hasFullNetheriteSet(entity) || hasFullJetSuitSet(entity);
    }

    private static boolean isSpaceSuitItem(EntityLivingBase entity, EntityEquipmentSlot slot) {
        Item item = entity.getItemStackFromSlot(slot).getItem();
        switch (slot) {
            case HEAD:
                return item == ModItems.SPACE_HELMET || item == ModItems.NETHERITE_SPACE_HELMET || item == ModItems.JET_SUIT_HELMET;
            case CHEST:
                return item == ModItems.SPACE_SUIT || item == ModItems.NETHERITE_SPACE_SUIT || item == ModItems.JET_SUIT;
            case LEGS:
                return item == ModItems.SPACE_PANTS || item == ModItems.NETHERITE_SPACE_PANTS || item == ModItems.JET_SUIT_PANTS;
            case FEET:
                return item == ModItems.SPACE_BOOTS || item == ModItems.NETHERITE_SPACE_BOOTS || item == ModItems.JET_SUIT_BOOTS;
            default:
                return false;
        }
    }

    private static boolean isNetheriteSpaceSuitItem(EntityLivingBase entity, EntityEquipmentSlot slot) {
        Item item = entity.getItemStackFromSlot(slot).getItem();
        switch (slot) {
            case HEAD:
                return item == ModItems.NETHERITE_SPACE_HELMET;
            case CHEST:
                return item == ModItems.NETHERITE_SPACE_SUIT;
            case LEGS:
                return item == ModItems.NETHERITE_SPACE_PANTS;
            case FEET:
                return item == ModItems.NETHERITE_SPACE_BOOTS;
            default:
                return false;
        }
    }

    private static boolean isJetSuitItem(EntityLivingBase entity, EntityEquipmentSlot slot) {
        Item item = entity.getItemStackFromSlot(slot).getItem();
        switch (slot) {
            case HEAD:
                return item == ModItems.JET_SUIT_HELMET;
            case CHEST:
                return item == ModItems.JET_SUIT;
            case LEGS:
                return item == ModItems.JET_SUIT_PANTS;
            case FEET:
                return item == ModItems.JET_SUIT_BOOTS;
            default:
                return false;
        }
    }
}
