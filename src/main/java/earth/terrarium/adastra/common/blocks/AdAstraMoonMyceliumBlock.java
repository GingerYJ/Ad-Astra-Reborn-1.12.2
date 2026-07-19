package earth.terrarium.adastra.common.blocks;

import earth.terrarium.adastra.common.registry.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.IGrowable;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

/** The moon mycelium surface block. */
public final class AdAstraMoonMyceliumBlock extends Block implements IGrowable {

    public AdAstraMoonMyceliumBlock() {
        super(Material.GRASS);
        setCreativeTab(earth.terrarium.adastra.common.AdAstraCreativeTab.INSTANCE);
        setHardness(0.6F);
        setResistance(0.6F);
        setTickRandomly(true);
        setLightLevel(0.4F);
    }

    @Override
    public void updateTick(World world, BlockPos pos, IBlockState state, Random random) {
        if (!canRemainMycelium(world, pos)) {
            world.setBlockState(pos, ModBlocks.MOON_STONE.getDefaultState(), 3);
        }
    }

    private boolean canRemainMycelium(World world, BlockPos pos) {
        BlockPos above = pos.up();
        return world.getBlockLightOpacity(above) < 15;
    }

    @Override
    public boolean canGrow(World world, BlockPos pos, IBlockState state, boolean isClient) {
        return world.isAirBlock(pos.up());
    }

    @Override
    public boolean canUseBonemeal(World world, Random random, BlockPos pos, IBlockState state) {
        return true;
    }

    @Override
    public void grow(World world, Random random, BlockPos pos, IBlockState state) {
        // The source block exposes bonemeal support but delegates feature placement
        // to the configured world-generation features, so this remains intentional.
    }
}
