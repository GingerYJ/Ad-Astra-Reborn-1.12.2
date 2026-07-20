package earth.terrarium.adastra.common.blocks;

import earth.terrarium.adastra.common.AdAstraCreativeTab;
import earth.terrarium.adastra.common.registry.ModBlocks;
import net.minecraft.block.BlockBush;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;

public class AdAstraMushroomBlock extends BlockBush {

    public AdAstraMushroomBlock() {
        setCreativeTab(AdAstraCreativeTab.INSTANCE);
        setHardness(0.0f);
        setLightLevel(0.375f);
        setSoundType(SoundType.PLANT);
    }

    @Override
    protected boolean canSustainBush(IBlockState state) {
        return super.canSustainBush(state) || state.getBlock() == ModBlocks.MOON_MYCELIUM;
    }
}
