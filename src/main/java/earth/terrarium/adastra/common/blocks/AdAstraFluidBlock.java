package earth.terrarium.adastra.common.blocks;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;

public class AdAstraFluidBlock extends BlockFluidClassic {

    public AdAstraFluidBlock(Fluid fluid) {
        super(fluid, Material.WATER);
        setQuantaPerBlock(8);
        setLightOpacity(3);
    }

    @Override
    public boolean canDrain(World world, BlockPos pos) {
        return true;
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
