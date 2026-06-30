package earth.terrarium.adastra.common.blocks;

import earth.terrarium.adastra.common.AdAstraCreativeTab;
import net.minecraft.block.BlockTrapDoor;
import net.minecraft.block.material.Material;

public class AdAstraTrapDoorBlock extends BlockTrapDoor {

    public AdAstraTrapDoorBlock(Material material) {
        super(material);
        setCreativeTab(AdAstraCreativeTab.INSTANCE);
        setHardness(material == Material.IRON ? 5.0f : 3.0f);
        setResistance(material == Material.IRON ? 12.0f : 5.0f);
    }
}
