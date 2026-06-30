package earth.terrarium.adastra.common.items;

import earth.terrarium.adastra.Reference;
import earth.terrarium.adastra.common.AdAstraCreativeTab;
import net.minecraft.entity.Entity;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.common.util.EnumHelper;

public class AdAstraArmorItem extends ItemArmor {

    private final String texture;

    public AdAstraArmorItem(String name, SuitMaterial suitMaterial, EntityEquipmentSlot slot) {
        super(suitMaterial.armorMaterial, 0, slot);
        this.texture = suitMaterial.texture;
        setRegistryName(Reference.MOD_ID, name);
        setTranslationKey(Reference.MOD_ID + "." + name);
        setCreativeTab(AdAstraCreativeTab.INSTANCE);
    }

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
        return Reference.MOD_ID + ":textures/entity/armor/" + texture + ".png";
    }

    public enum SuitMaterial {
        SPACE("space_suit", 37, new int[]{2, 6, 5, 2}, 14, SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, 0.0f),
        NETHERITE_SPACE("netherite_space_suit", 37, new int[]{3, 8, 6, 3}, 15, SoundEvents.ITEM_ARMOR_EQUIP_IRON, 3.0f),
        JET("jet_suit", 37, new int[]{4, 9, 7, 4}, 15, SoundEvents.ITEM_ARMOR_EQUIP_IRON, 5.0f);

        private final ArmorMaterial armorMaterial;
        private final String texture;

        SuitMaterial(String texture, int durability, int[] reductions, int enchantability, SoundEvent equipSound, float toughness) {
            this.texture = texture;
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
}
