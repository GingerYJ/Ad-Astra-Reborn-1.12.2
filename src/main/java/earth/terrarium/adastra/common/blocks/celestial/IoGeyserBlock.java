package earth.terrarium.adastra.common.blocks.celestial;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

public class IoGeyserBlock extends Block {

    public static final PropertyBool ACTIVE = PropertyBool.create("active");

    public IoGeyserBlock() {
        super(Material.ROCK);
        this.setSoundType(SoundType.STONE);
        this.setHarvestLevel("pickaxe", 2);
        this.setTickRandomly(true);
        this.setDefaultState(this.blockState.getBaseState().withProperty(ACTIVE, false));
    }

    @Override
    public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
        if (!world.isRemote) {
            boolean isActive = state.getValue(ACTIVE);
            if (isActive) {
                world.setBlockState(pos, state.withProperty(ACTIVE, false), 3);
            } else {
                world.setBlockState(pos, state.withProperty(ACTIVE, true), 3);
            }
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random rand) {
        if (state.getValue(ACTIVE)) {
            for (int i = 0; i < 3; i++) {
                world.spawnParticle(EnumParticleTypes.FLAME,
                    pos.getX() + 0.5D + (rand.nextDouble() - 0.5D) * 0.5D,
                    pos.getY() + 1.0D,
                    pos.getZ() + 0.5D + (rand.nextDouble() - 0.5D) * 0.5D,
                    0.0D, 0.1D, 0.0D);
                world.spawnParticle(EnumParticleTypes.SMOKE_LARGE,
                    pos.getX() + 0.5D + (rand.nextDouble() - 0.5D) * 0.8D,
                    pos.getY() + 1.5D,
                    pos.getZ() + 0.5D + (rand.nextDouble() - 0.5D) * 0.8D,
                    0.0D, 0.05D, 0.0D);
            }
        }
    }

    @Override public IBlockState getStateFromMeta(int meta) { return getDefaultState().withProperty(ACTIVE, meta == 1); }
    @Override public int getMetaFromState(IBlockState state) { return state.getValue(ACTIVE) ? 1 : 0; }
    @Override protected BlockStateContainer createBlockState() { return new BlockStateContainer(this, ACTIVE); }
}
