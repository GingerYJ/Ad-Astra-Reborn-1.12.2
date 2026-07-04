package earth.terrarium.adastra.common.items;

import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class AdAstraPipeItemBlock extends ItemBlock {

    private final boolean energy;
    private final int transferRate;
    private final String descriptionKey;

    public AdAstraPipeItemBlock(Block block, boolean energy, int transferRate, String descriptionKey) {
        super(block);
        this.energy = energy;
        this.transferRate = transferRate;
        this.descriptionKey = descriptionKey;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag) {
        tooltip.add(new TextComponentTranslation(
            energy ? "tooltip.ad_astra.energy_transfer_tick" : "tooltip.ad_astra.fluid_transfer_tick",
            transferRate).getFormattedText());
        tooltip.add(TextFormatting.GRAY + new TextComponentTranslation(descriptionKey).getFormattedText());
    }
}
