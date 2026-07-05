package earth.terrarium.adastra.common.items;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class NetheriteSpaceSuitItem extends SpaceSuitItem {

    public NetheriteSpaceSuitItem(String name, EntityEquipmentSlot slot) {
        super(name, SpaceSuitMaterial.NETHERITE_SPACE, slot);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag) {
        addOxygenTooltip(stack, tooltip);
        addSuitInfoTooltip(stack, tooltip, getSuitInfoTranslationKey());
    }

    @Override
    protected String getSuitInfoTranslationKey() {
        return "info.ad_astra.netherite_space_suit";
    }
}
