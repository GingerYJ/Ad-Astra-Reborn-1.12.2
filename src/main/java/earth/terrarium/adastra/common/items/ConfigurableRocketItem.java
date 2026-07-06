package earth.terrarium.adastra.common.items;

import earth.terrarium.adastra.common.entities.vehicles.ConfigurableRocketEntity;
import earth.terrarium.adastra.common.rocket.ConfigurableRocketSpec;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class ConfigurableRocketItem extends VehicleItem {

    private final ConfigurableRocketSpec spec;

    public ConfigurableRocketItem(ConfigurableRocketSpec spec) {
        super(spec.getId(), world -> new ConfigurableRocketEntity(world, spec));
        this.spec = spec;
    }

    public ConfigurableRocketSpec getSpec() {
        return spec;
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        return spec.getDisplayName();
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(I18n.translateToLocalFormatted("tooltip.ad_astra.configurable_rocket.tier", spec.getTier()));
        tooltip.add(I18n.translateToLocalFormatted("tooltip.ad_astra.configurable_rocket.texture", spec.getTextureDisplayName()));
    }
}
