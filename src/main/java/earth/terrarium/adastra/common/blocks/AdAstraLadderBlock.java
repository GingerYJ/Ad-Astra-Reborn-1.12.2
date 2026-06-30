package earth.terrarium.adastra.common.blocks;

import earth.terrarium.adastra.common.AdAstraCreativeTab;
import net.minecraft.block.BlockLadder;

public class AdAstraLadderBlock extends BlockLadder {

    public AdAstraLadderBlock() {
        setCreativeTab(AdAstraCreativeTab.INSTANCE);
        setHardness(0.4f);
    }
}
