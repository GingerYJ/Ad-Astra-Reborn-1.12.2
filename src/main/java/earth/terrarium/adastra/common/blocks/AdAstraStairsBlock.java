package earth.terrarium.adastra.common.blocks;

import earth.terrarium.adastra.common.AdAstraCreativeTab;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.state.IBlockState;

public class AdAstraStairsBlock extends BlockStairs {

    public AdAstraStairsBlock(IBlockState modelState) {
        super(modelState);
        setCreativeTab(AdAstraCreativeTab.INSTANCE);
        setHardness(modelState.getBlock().getBlockHardness(null, null, null));
        setResistance(modelState.getBlock().getExplosionResistance(null));
    }
}
