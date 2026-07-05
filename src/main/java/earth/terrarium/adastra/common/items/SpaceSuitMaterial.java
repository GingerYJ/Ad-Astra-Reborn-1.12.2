package earth.terrarium.adastra.common.items;

import earth.terrarium.adastra.Reference;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemArmor;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.common.util.EnumHelper;

public enum SpaceSuitMaterial {
    SPACE("space_suit", 37, new int[]{2, 6, 5, 2}, 14, SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, 0.0f, 1000, 0),
    NETHERITE_SPACE("netherite_space_suit", 37, new int[]{3, 8, 6, 3}, 15, SoundEvents.ITEM_ARMOR_EQUIP_IRON, 3.0f, 2000, 0),
    JET("jet_suit", 37, new int[]{4, 9, 7, 4}, 15, SoundEvents.ITEM_ARMOR_EQUIP_IRON, 5.0f, 3000, 1_000_000);

    private final ItemArmor.ArmorMaterial armorMaterial;
    private final String texture;
    private final int oxygenCapacity;
    private final int energyCapacity;

    SpaceSuitMaterial(String texture, int durability, int[] reductions, int enchantability, SoundEvent equipSound, float toughness, int oxygenCapacity, int energyCapacity) {
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

    public ItemArmor.ArmorMaterial armorMaterial() {
        return armorMaterial;
    }

    public String texture() {
        return texture;
    }

    public int oxygenCapacity() {
        return oxygenCapacity;
    }

    public int energyCapacity() {
        return energyCapacity;
    }
}
