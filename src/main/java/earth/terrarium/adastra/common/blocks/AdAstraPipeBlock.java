package earth.terrarium.adastra.common.blocks;

import earth.terrarium.adastra.common.AdAstraCreativeTab;
import earth.terrarium.adastra.common.registry.ModBlocks;
import earth.terrarium.adastra.common.registry.ModTileEntities;
import earth.terrarium.adastra.common.tile.AdAstraPipeTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;

import javax.annotation.Nullable;
import java.util.List;

public class AdAstraPipeBlock extends Block implements ITileEntityProvider {

    public static final PropertyEnum<AdAstraPipeConnection> CONNECTED_DOWN = PropertyEnum.create("connected_down", AdAstraPipeConnection.class);
    public static final PropertyEnum<AdAstraPipeConnection> CONNECTED_EAST = PropertyEnum.create("connected_east", AdAstraPipeConnection.class);
    public static final PropertyEnum<AdAstraPipeConnection> CONNECTED_NORTH = PropertyEnum.create("connected_north", AdAstraPipeConnection.class);
    public static final PropertyEnum<AdAstraPipeConnection> CONNECTED_SOUTH = PropertyEnum.create("connected_south", AdAstraPipeConnection.class);
    public static final PropertyEnum<AdAstraPipeConnection> CONNECTED_UP = PropertyEnum.create("connected_up", AdAstraPipeConnection.class);
    public static final PropertyEnum<AdAstraPipeConnection> CONNECTED_WEST = PropertyEnum.create("connected_west", AdAstraPipeConnection.class);

    private static final AxisAlignedBB CORE_AABB = new AxisAlignedBB(0.3125d, 0.3125d, 0.3125d, 0.6875d, 0.6875d, 0.6875d);
    private static final AxisAlignedBB[] CONNECTION_BOXES = {
        new AxisAlignedBB(0.3125d, 0.0d, 0.3125d, 0.6875d, 0.3125d, 0.6875d),
        new AxisAlignedBB(0.3125d, 0.6875d, 0.3125d, 0.6875d, 1.0d, 0.6875d),
        new AxisAlignedBB(0.3125d, 0.3125d, 0.0d, 0.6875d, 0.6875d, 0.3125d),
        new AxisAlignedBB(0.3125d, 0.3125d, 0.6875d, 0.6875d, 0.6875d, 1.0d),
        new AxisAlignedBB(0.0d, 0.3125d, 0.3125d, 0.3125d, 0.6875d, 0.6875d),
        new AxisAlignedBB(0.6875d, 0.3125d, 0.3125d, 1.0d, 0.6875d, 0.6875d)
    };

    public AdAstraPipeBlock(float hardness, float resistance) {
        super(Material.IRON);
        setCreativeTab(AdAstraCreativeTab.INSTANCE);
        setHardness(hardness);
        setResistance(resistance);
        setSoundType(SoundType.METAL);
        setHarvestLevel("pickaxe", 1);
        setDefaultState(blockState.getBaseState()
            .withProperty(CONNECTED_DOWN, AdAstraPipeConnection.NONE)
            .withProperty(CONNECTED_EAST, AdAstraPipeConnection.NONE)
            .withProperty(CONNECTED_NORTH, AdAstraPipeConnection.NONE)
            .withProperty(CONNECTED_SOUTH, AdAstraPipeConnection.NONE)
            .withProperty(CONNECTED_UP, AdAstraPipeConnection.NONE)
            .withProperty(CONNECTED_WEST, AdAstraPipeConnection.NONE));
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
        return state
            .withProperty(CONNECTED_DOWN, getConnection(world, pos, EnumFacing.DOWN))
            .withProperty(CONNECTED_EAST, getConnection(world, pos, EnumFacing.EAST))
            .withProperty(CONNECTED_NORTH, getConnection(world, pos, EnumFacing.NORTH))
            .withProperty(CONNECTED_SOUTH, getConnection(world, pos, EnumFacing.SOUTH))
            .withProperty(CONNECTED_UP, getConnection(world, pos, EnumFacing.UP))
            .withProperty(CONNECTED_WEST, getConnection(world, pos, EnumFacing.WEST));
    }

    @Override
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos fromPos) {
        world.markBlockRangeForRenderUpdate(pos, pos);
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        IBlockState actual = getActualState(state, source, pos);
        AxisAlignedBB box = CORE_AABB;
        for (EnumFacing facing : EnumFacing.values()) {
            if (getConnectionValue(actual, facing) != AdAstraPipeConnection.NONE) {
                box = box.union(CONNECTION_BOXES[facing.getIndex()]);
            }
        }
        return box;
    }

    @Override
    public void addCollisionBoxToList(IBlockState state, World world, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entity, boolean isActualState) {
        IBlockState actual = isActualState ? state : getActualState(state, world, pos);
        addCollisionBoxToList(pos, entityBox, collidingBoxes, CORE_AABB);
        for (EnumFacing facing : EnumFacing.values()) {
            if (getConnectionValue(actual, facing) != AdAstraPipeConnection.NONE) {
                addCollisionBoxToList(pos, entityBox, collidingBoxes, CONNECTION_BOXES[facing.getIndex()]);
            }
        }
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState();
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return 0;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this,
            CONNECTED_DOWN,
            CONNECTED_EAST,
            CONNECTED_NORTH,
            CONNECTED_SOUTH,
            CONNECTED_UP,
            CONNECTED_WEST);
    }

    private AdAstraPipeConnection getConnection(IBlockAccess world, BlockPos pos, EnumFacing facing) {
        BlockPos neighborPos = pos.offset(facing);
        Block neighbor = world.getBlockState(neighborPos).getBlock();
        if (neighbor instanceof AdAstraPipeBlock) {
            return AdAstraPipeConnection.NORMAL;
        }
        AdAstraPipeConnection configured = getConfiguredConnection(world, pos, facing);
        if (isEnergyCable()) {
            TileEntity tile = world.getTileEntity(neighborPos);
            if (tile != null && tile.hasCapability(CapabilityEnergy.ENERGY, facing.getOpposite())) {
                return configured;
            }
        }
        if (isFluidPipe()) {
            TileEntity tile = world.getTileEntity(neighborPos);
            if (tile != null && tile.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, facing.getOpposite())) {
                return configured;
            }
        }
        return AdAstraPipeConnection.NONE;
    }

    private AdAstraPipeConnection getConfiguredConnection(IBlockAccess world, BlockPos pos, EnumFacing facing) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof AdAstraPipeTileEntity) {
            return ((AdAstraPipeTileEntity) tile).getConnection(facing);
        }
        return AdAstraPipeConnection.NORMAL;
    }

    private boolean isEnergyCable() {
        return this == ModBlocks.STEEL_CABLE || this == ModBlocks.DESH_CABLE || this == ModBlocks.CABLE_DUCT;
    }

    private boolean isFluidPipe() {
        return this == ModBlocks.DESH_FLUID_PIPE || this == ModBlocks.OSTRUM_FLUID_PIPE || this == ModBlocks.FLUID_PIPE_DUCT;
    }

    private AdAstraPipeConnection getConnectionValue(IBlockState state, EnumFacing facing) {
        switch (facing) {
            case DOWN:
                return state.getValue(CONNECTED_DOWN);
            case EAST:
                return state.getValue(CONNECTED_EAST);
            case NORTH:
                return state.getValue(CONNECTED_NORTH);
            case SOUTH:
                return state.getValue(CONNECTED_SOUTH);
            case UP:
                return state.getValue(CONNECTED_UP);
            case WEST:
            default:
                return state.getValue(CONNECTED_WEST);
        }
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return ModTileEntities.createForBlock(this);
    }
}
