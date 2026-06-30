package earth.terrarium.adastra.common.blocks;

import earth.terrarium.adastra.common.AdAstraCreativeTab;
import net.minecraft.block.BlockRotatedPillar;
import net.minecraft.block.material.Material;

public class AdAstraAxisBlock extends BlockRotatedPillar {

    public AdAstraAxisBlock(Material material, float hardness, float resistance) {
        super(material);
        setCreativeTab(AdAstraCreativeTab.INSTANCE);
        setHardness(hardness);
        setResistance(resistance);
    }
}
