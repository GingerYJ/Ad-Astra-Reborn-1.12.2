package earth.terrarium.adastra.common.blocks;

import earth.terrarium.adastra.common.AdAstraCreativeTab;
import net.minecraft.block.BlockFence;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;

public class AdAstraFenceBlock extends BlockFence {

    public AdAstraFenceBlock(Material material, MapColor mapColor) {
        super(material, mapColor);
        setCreativeTab(AdAstraCreativeTab.INSTANCE);
        setHardness(2.0f);
        setResistance(3.0f);
    }
}
