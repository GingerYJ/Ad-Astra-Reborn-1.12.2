package earth.terrarium.adastra.common.blocks.celestial;

import earth.terrarium.adastra.common.tile.TileEntityEnceladusCrystal;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EnceladusCrystalBlock extends Block implements ITileEntityProvider {

    public static final PropertyDirection FACING = PropertyDirection.create("facing");
    private static final AxisAlignedBB CRYSTAL_AABB = new AxisAlignedBB(0.25D, 0.0D, 0.25D, 0.75D, 0.75D, 0.75D);

    public EnceladusCrystalBlock() {
        super(Material.ICE);
        this.setSoundType(SoundType.GLASS);
        this.setHarvestLevel("pickaxe", 2);
        this.setLightLevel(0.4F);
        this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.UP));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getRenderLayer() { return BlockRenderLayer.TRANSLUCENT; }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.INVISIBLE;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) { return false; }
    @Override public boolean isFullCube(IBlockState state) { return false; }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return CRYSTAL_AABB;
    }

    @Override public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(FACING, EnumFacing.byIndex(meta));
    }

    @Override public int getMetaFromState(IBlockState state) {
        return state.getValue(FACING).getIndex();
    }

    @Override protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING);
    }

    @Override
    public boolean canPlaceBlockAt(World world, BlockPos pos) {
        return super.canPlaceBlockAt(world, pos) && world.isSideSolid(pos.down(), EnumFacing.UP);
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityEnceladusCrystal();
    }
}
