package earth.terrarium.adastra.common;

import earth.terrarium.adastra.Reference;
import earth.terrarium.adastra.common.registry.ModItems;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public final class AdAstraCreativeTab extends CreativeTabs {

    public static final AdAstraCreativeTab INSTANCE = new AdAstraCreativeTab();

    private AdAstraCreativeTab() {
        super(Reference.MOD_ID);
    }

    @Override
    public ItemStack createIcon() {
        if (ModItems.STEEL_INGOT != null) {
            return new ItemStack(ModItems.STEEL_INGOT);
        }
        return ItemStack.EMPTY;
    }

    public ItemStack getIconItemStack() {
        return createIcon();
    }

    public Item getTabIconItem() {
        return ModItems.STEEL_INGOT;
    }
}
