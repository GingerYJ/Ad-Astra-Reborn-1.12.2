package earth.terrarium.adastra.common.blocks;

import earth.terrarium.adastra.common.AdAstraCreativeTab;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public class AdAstraBlock extends Block {

    public AdAstraBlock(Material material, float hardness, float resistance) {
        super(material);
        setCreativeTab(AdAstraCreativeTab.INSTANCE);
        setHardness(hardness);
        setResistance(resistance);
    }
}
