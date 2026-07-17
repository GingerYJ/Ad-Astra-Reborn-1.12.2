package earth.terrarium.adastra.common.blocks;

import earth.terrarium.adastra.common.registry.ModTileEntities;
import earth.terrarium.adastra.common.tile.GlobeTileEntity;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraft.world.IBlockAccess;
import net.minecraft.util.math.BlockPos;

public class AdAstraGlobeBlock extends AdAstraModelBlock implements ITileEntityProvider {

    private static final String PROJECTILE_HIT_TAG = "AdAstraGlobeHit";
    public static final PropertyBool POWERED = PropertyBool.create("powered");
    public static final PropertyBool WATERLOGGED = PropertyBool.create("waterlogged");
    private static final AxisAlignedBB GLOBE_BOX = new AxisAlignedBB(0.2, 0.0, 0.2, 0.8, 1.0, 0.8);

    public AdAstraGlobeBlock() {
        super(Material.IRON, 5.0f, 6.0f);
        setSoundType(SoundType.METAL);
        setHarvestLevel("pickaxe", 1);
        setDefaultState(blockState.getBaseState()
            .withProperty(POWERED, false)
            .withProperty(WATERLOGGED, false));
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState()
            .withProperty(POWERED, (meta & 1) != 0)
            .withProperty(WATERLOGGED, false);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(POWERED) ? 1 : 0;
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof GlobeTileEntity) {
            if (!world.isRemote) {
                ((GlobeTileEntity) tile).rotateGlobe();
            }
            return true;
        }
        return false;
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return GLOBE_BOX;
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
        return GLOBE_BOX;
    }

    @Override
    public void onEntityCollision(World world, BlockPos pos, IBlockState state, Entity entity) {
        if (!world.isRemote && entity instanceof IProjectile && !entity.getEntityData().getBoolean(PROJECTILE_HIT_TAG)) {
            TileEntity tile = world.getTileEntity(pos);
            if (tile instanceof GlobeTileEntity) {
                ((GlobeTileEntity) tile).rotateGlobe();
                entity.getEntityData().setBoolean(PROJECTILE_HIT_TAG, true);
            }
        }
    }

    @Override
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos fromPos) {
        if (!world.isRemote) {
            boolean powered = world.isBlockPowered(pos);
            if (state.getValue(POWERED) != powered) {
                world.setBlockState(pos, state.withProperty(POWERED, powered), 3);
            }
        }
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, POWERED, WATERLOGGED);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return ModTileEntities.createForBlock(this);
    }
}
