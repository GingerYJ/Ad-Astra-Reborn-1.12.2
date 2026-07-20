package earth.terrarium.adastra.common.blocks;

import earth.terrarium.adastra.common.registry.ModBlocks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Bootstrap;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AdAstraFluidBlockTest {

    @BeforeAll
    static void initializeVanillaBlockRegistry() {
        Bootstrap.register();
    }

    @Test
    void recognizesBothVanillaWaterStates() {
        assertTrue(AdAstraFluidBlock.isVanillaWater(Blocks.WATER.getDefaultState()));
        assertTrue(AdAstraFluidBlock.isVanillaWater(Blocks.FLOWING_WATER.getDefaultState()));
        assertFalse(AdAstraFluidBlock.isVanillaWater(Blocks.LAVA.getDefaultState()));
    }

    @Test
    void customFluidCannotDisplaceVanillaWater() {
        AdAstraFluidBlock oil = (AdAstraFluidBlock) ModBlocks.OIL;
        BlockPos pos = BlockPos.ORIGIN;

        assertFalse(oil.canDisplace(singleState(Blocks.WATER.getDefaultState()), pos));
        assertFalse(oil.canDisplace(singleState(Blocks.FLOWING_WATER.getDefaultState()), pos));
    }

    @Test
    void onlyNonGaseousFluidsUseWaterVisualEffects() {
        assertTrue(((AdAstraFluidBlock) ModBlocks.OIL).usesWaterVisualEffects());
        assertFalse(((AdAstraFluidBlock) ModBlocks.OXYGEN).usesWaterVisualEffects());
        assertFalse(((AdAstraFluidBlock) ModBlocks.HYDROGEN).usesWaterVisualEffects());
    }

    private static IBlockAccess singleState(IBlockState state) {
        return new IBlockAccess() {
            @Override
            public TileEntity getTileEntity(BlockPos pos) {
                return null;
            }

            @Override
            public int getCombinedLight(BlockPos pos, int lightValue) {
                return 0;
            }

            @Override
            public IBlockState getBlockState(BlockPos pos) {
                return state;
            }

            @Override
            public boolean isAirBlock(BlockPos pos) {
                return state.getBlock() == Blocks.AIR;
            }

            @Override
            public Biome getBiome(BlockPos pos) {
                return Biome.getBiome(1);
            }

            @Override
            public int getStrongPower(BlockPos pos, EnumFacing direction) {
                return 0;
            }

            @Override
            public WorldType getWorldType() {
                return WorldType.DEFAULT;
            }

            @Override
            public boolean isSideSolid(BlockPos pos, EnumFacing side, boolean _default) {
                return false;
            }
        };
    }
}
