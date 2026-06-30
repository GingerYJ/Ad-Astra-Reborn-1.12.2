package earth.terrarium.adastra.common.blocks;

import earth.terrarium.adastra.common.AdAstraCreativeTab;
import net.minecraft.block.Block;
import net.minecraft.block.BlockWall;

public class AdAstraWallBlock extends BlockWall {

    public AdAstraWallBlock(Block modelBlock) {
        super(modelBlock);
        setCreativeTab(AdAstraCreativeTab.INSTANCE);
    }
}
