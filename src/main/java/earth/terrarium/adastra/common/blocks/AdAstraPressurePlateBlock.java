package earth.terrarium.adastra.common.blocks;

import earth.terrarium.adastra.common.AdAstraCreativeTab;
import net.minecraft.block.BlockPressurePlate;
import net.minecraft.block.material.Material;

public class AdAstraPressurePlateBlock extends BlockPressurePlate {

    public AdAstraPressurePlateBlock(Material material, Sensitivity sensitivity) {
        super(material, sensitivity);
        setCreativeTab(AdAstraCreativeTab.INSTANCE);
        setHardness(0.5f);
    }
}
