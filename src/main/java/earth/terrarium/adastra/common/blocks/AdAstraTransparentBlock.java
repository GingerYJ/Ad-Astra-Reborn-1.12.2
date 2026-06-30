package earth.terrarium.adastra.common.blocks;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;

public class AdAstraTransparentBlock extends AdAstraBlock {

    public AdAstraTransparentBlock(Material material, float hardness, float resistance) {
        super(material, hardness, resistance);
        setLightOpacity(0);
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }
}
