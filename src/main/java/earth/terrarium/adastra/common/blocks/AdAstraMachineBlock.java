package earth.terrarium.adastra.common.blocks;

import earth.terrarium.adastra.common.registry.ModTileEntities;
import earth.terrarium.adastra.common.tile.AdAstraMachineTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.entity.player.EntityPlayer;

import javax.annotation.Nullable;
import java.util.List;

public class AdAstraMachineBlock extends AdAstraBlock implements ITileEntityProvider {

    private static final AxisAlignedBB WATER_PUMP_BASE = new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 4.0 / 16.0, 1.0);
    private static final AxisAlignedBB WATER_PUMP_NORTH_BODY_LEFT = new AxisAlignedBB(0.0, 4.0 / 16.0, 0.0, 9.0 / 16.0, 1.0, 1.0);
    private static final AxisAlignedBB WATER_PUMP_NORTH_BODY_RIGHT = new AxisAlignedBB(9.0 / 16.0, 4.0 / 16.0, 5.0 / 16.0, 1.0, 15.0 / 16.0, 15.0 / 16.0);
    private static final AxisAlignedBB WATER_PUMP_NORTH_PIPE_LOW = new AxisAlignedBB(11.0 / 16.0, 4.0 / 16.0, 1.0 / 16.0, 14.0 / 16.0, 9.0 / 16.0, 4.0 / 16.0);
    private static final AxisAlignedBB WATER_PUMP_NORTH_PIPE_HIGH = new AxisAlignedBB(11.0 / 16.0, 9.0 / 16.0, 1.0 / 16.0, 14.0 / 16.0, 12.0 / 16.0, 5.0 / 16.0);
    private static final AxisAlignedBB WATER_PUMP_EAST_BODY_BACK = new AxisAlignedBB(0.0, 4.0 / 16.0, 0.0, 1.0, 1.0, 9.0 / 16.0);
    private static final AxisAlignedBB WATER_PUMP_EAST_BODY_FRONT = new AxisAlignedBB(1.0 / 16.0, 4.0 / 16.0, 9.0 / 16.0, 11.0 / 16.0, 15.0 / 16.0, 1.0);
    private static final AxisAlignedBB WATER_PUMP_EAST_PIPE_LOW = new AxisAlignedBB(12.0 / 16.0, 4.0 / 16.0, 11.0 / 16.0, 15.0 / 16.0, 9.0 / 16.0, 14.0 / 16.0);
    private static final AxisAlignedBB WATER_PUMP_EAST_PIPE_HIGH = new AxisAlignedBB(11.0 / 16.0, 9.0 / 16.0, 11.0 / 16.0, 15.0 / 16.0, 12.0 / 16.0, 14.0 / 16.0);
    private static final AxisAlignedBB WATER_PUMP_SOUTH_BODY_RIGHT = new AxisAlignedBB(7.0 / 16.0, 4.0 / 16.0, 0.0, 1.0, 1.0, 1.0);
    private static final AxisAlignedBB WATER_PUMP_SOUTH_BODY_LEFT = new AxisAlignedBB(0.0, 4.0 / 16.0, 1.0 / 16.0, 7.0 / 16.0, 15.0 / 16.0, 11.0 / 16.0);
    private static final AxisAlignedBB WATER_PUMP_SOUTH_PIPE_LOW = new AxisAlignedBB(2.0 / 16.0, 4.0 / 16.0, 12.0 / 16.0, 5.0 / 16.0, 9.0 / 16.0, 15.0 / 16.0);
    private static final AxisAlignedBB WATER_PUMP_SOUTH_PIPE_HIGH = new AxisAlignedBB(2.0 / 16.0, 9.0 / 16.0, 11.0 / 16.0, 5.0 / 16.0, 12.0 / 16.0, 15.0 / 16.0);
    private static final AxisAlignedBB WATER_PUMP_WEST_BODY_BACK = new AxisAlignedBB(0.0, 4.0 / 16.0, 7.0 / 16.0, 1.0, 1.0, 1.0);
    private static final AxisAlignedBB WATER_PUMP_WEST_BODY_FRONT = new AxisAlignedBB(5.0 / 16.0, 4.0 / 16.0, 0.0, 15.0 / 16.0, 15.0 / 16.0, 7.0 / 16.0);
    private static final AxisAlignedBB WATER_PUMP_WEST_PIPE_LOW = new AxisAlignedBB(1.0 / 16.0, 4.0 / 16.0, 2.0 / 16.0, 4.0 / 16.0, 9.0 / 16.0, 5.0 / 16.0);
    private static final AxisAlignedBB WATER_PUMP_WEST_PIPE_HIGH = new AxisAlignedBB(1.0 / 16.0, 9.0 / 16.0, 2.0 / 16.0, 5.0 / 16.0, 12.0 / 16.0, 5.0 / 16.0);
    private static final AxisAlignedBB ETRIONIC_BLAST_FURNACE_BASE = new AxisAlignedBB(1.0 / 16.0, 0.0, 1.0 / 16.0, 15.0 / 16.0, 5.0 / 16.0, 15.0 / 16.0);
    private static final AxisAlignedBB ETRIONIC_BLAST_FURNACE_BACK_WALL = new AxisAlignedBB(5.0 / 16.0, 5.0 / 16.0, 11.0 / 16.0, 13.0 / 16.0, 1.0, 13.0 / 16.0);
    private static final AxisAlignedBB ETRIONIC_BLAST_FURNACE_FRONT_WALL = new AxisAlignedBB(3.0 / 16.0, 5.0 / 16.0, 3.0 / 16.0, 11.0 / 16.0, 1.0, 5.0 / 16.0);
    private static final AxisAlignedBB ETRIONIC_BLAST_FURNACE_LEFT_WALL = new AxisAlignedBB(3.0 / 16.0, 5.0 / 16.0, 5.0 / 16.0, 5.0 / 16.0, 1.0, 13.0 / 16.0);
    private static final AxisAlignedBB ETRIONIC_BLAST_FURNACE_RIGHT_WALL = new AxisAlignedBB(11.0 / 16.0, 5.0 / 16.0, 3.0 / 16.0, 13.0 / 16.0, 1.0, 11.0 / 16.0);
    private static final AxisAlignedBB ETRIONIC_BLAST_FURNACE_NORTH_STACK = new AxisAlignedBB(3.0 / 16.0, 0.0, 0.0, 13.0 / 16.0, 14.0 / 16.0, 3.0 / 16.0);
    private static final AxisAlignedBB ETRIONIC_BLAST_FURNACE_EAST_STACK = new AxisAlignedBB(13.0 / 16.0, 0.0, 3.0 / 16.0, 1.0, 14.0 / 16.0, 13.0 / 16.0);
    private static final AxisAlignedBB ETRIONIC_BLAST_FURNACE_SOUTH_STACK = new AxisAlignedBB(3.0 / 16.0, 0.0, 13.0 / 16.0, 13.0 / 16.0, 14.0 / 16.0, 1.0);
    private static final AxisAlignedBB ETRIONIC_BLAST_FURNACE_WEST_STACK = new AxisAlignedBB(0.0, 0.0, 3.0 / 16.0, 3.0 / 16.0, 14.0 / 16.0, 13.0 / 16.0);

    public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
    public static final PropertyBool LIT = PropertyBool.create("lit");
    public static final PropertyBool POWERED = PropertyBool.create("powered");

    public AdAstraMachineBlock(Material material, float hardness, float resistance) {
        super(material, hardness, resistance);
        setSoundType(SoundType.METAL);
        setHarvestLevel("pickaxe", 1);
        setDefaultState(blockState.getBaseState()
            .withProperty(FACING, EnumFacing.NORTH)
            .withProperty(LIT, false)
            .withProperty(POWERED, false));
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        EnumFacing facing = EnumFacing.byHorizontalIndex(meta & 3);
        boolean lit = (meta & 4) != 0;
        boolean powered = (meta & 8) != 0;
        return getDefaultState()
            .withProperty(FACING, facing)
            .withProperty(LIT, lit)
            .withProperty(POWERED, powered);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        int meta = state.getValue(FACING).getHorizontalIndex();
        if (state.getValue(LIT)) {
            meta |= 4;
        }
        if (state.getValue(POWERED)) {
            meta |= 8;
        }
        return meta;
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        return getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        world.setBlockState(pos, state.withProperty(FACING, placer.getHorizontalFacing().getOpposite()), 2);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING, LIT, POWERED);
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        return AdAstraMachineGuiHelper.openMachineGui(world, pos, player, hand);
    }

    @Override
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos fromPos) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof AdAstraMachineTileEntity) {
            ((AdAstraMachineTileEntity) tile).invalidateNeighborCache();
        }
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        if (isWaterPump()) {
            return union(waterPumpBoxes(state));
        }
        if (isEtrionicBlastFurnace()) {
            return union(etrionicBlastFurnaceBoxes(state));
        }
        return super.getBoundingBox(state, source, pos);
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return !isNonFullModelMachine();
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return !isNonFullModelMachine();
    }

    @Override
    public BlockRenderLayer getRenderLayer() {
        return isCutoutModelMachine() ? BlockRenderLayer.CUTOUT : super.getRenderLayer();
    }

    @Override
    public int getLightOpacity(IBlockState state) {
        return isNonFullModelMachine() ? 0 : super.getLightOpacity(state);
    }

    @Override
    public void addCollisionBoxToList(IBlockState state, World world, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entity, boolean isActualState) {
        if (isWaterPump()) {
            for (AxisAlignedBB box : waterPumpBoxes(state)) {
                addCollisionBoxToList(pos, entityBox, collidingBoxes, box);
            }
            return;
        }
        if (isEtrionicBlastFurnace()) {
            for (AxisAlignedBB box : etrionicBlastFurnaceBoxes(state)) {
                addCollisionBoxToList(pos, entityBox, collidingBoxes, box);
            }
            return;
        }
        super.addCollisionBoxToList(state, world, pos, entityBox, collidingBoxes, entity, isActualState);
    }

    private AxisAlignedBB[] waterPumpBoxes(IBlockState state) {
        switch (state.getValue(FACING)) {
            case EAST:
                return new AxisAlignedBB[]{WATER_PUMP_EAST_PIPE_HIGH, WATER_PUMP_BASE, WATER_PUMP_EAST_BODY_BACK, WATER_PUMP_EAST_BODY_FRONT, WATER_PUMP_EAST_PIPE_LOW};
            case SOUTH:
                return new AxisAlignedBB[]{WATER_PUMP_SOUTH_PIPE_HIGH, WATER_PUMP_BASE, WATER_PUMP_SOUTH_BODY_RIGHT, WATER_PUMP_SOUTH_BODY_LEFT, WATER_PUMP_SOUTH_PIPE_LOW};
            case WEST:
                return new AxisAlignedBB[]{WATER_PUMP_WEST_PIPE_HIGH, WATER_PUMP_BASE, WATER_PUMP_WEST_BODY_BACK, WATER_PUMP_WEST_BODY_FRONT, WATER_PUMP_WEST_PIPE_LOW};
            case NORTH:
            default:
                return new AxisAlignedBB[]{WATER_PUMP_BASE, WATER_PUMP_NORTH_BODY_LEFT, WATER_PUMP_NORTH_BODY_RIGHT, WATER_PUMP_NORTH_PIPE_LOW, WATER_PUMP_NORTH_PIPE_HIGH};
        }
    }

    private AxisAlignedBB[] etrionicBlastFurnaceBoxes(IBlockState state) {
        switch (state.getValue(FACING)) {
            case EAST:
                return new AxisAlignedBB[]{ETRIONIC_BLAST_FURNACE_BASE, ETRIONIC_BLAST_FURNACE_LEFT_WALL, ETRIONIC_BLAST_FURNACE_RIGHT_WALL, ETRIONIC_BLAST_FURNACE_FRONT_WALL, ETRIONIC_BLAST_FURNACE_BACK_WALL, ETRIONIC_BLAST_FURNACE_EAST_STACK};
            case SOUTH:
                return new AxisAlignedBB[]{ETRIONIC_BLAST_FURNACE_BASE, ETRIONIC_BLAST_FURNACE_RIGHT_WALL, ETRIONIC_BLAST_FURNACE_FRONT_WALL, ETRIONIC_BLAST_FURNACE_LEFT_WALL, ETRIONIC_BLAST_FURNACE_BACK_WALL, ETRIONIC_BLAST_FURNACE_SOUTH_STACK};
            case WEST:
                return new AxisAlignedBB[]{ETRIONIC_BLAST_FURNACE_BASE, ETRIONIC_BLAST_FURNACE_FRONT_WALL, ETRIONIC_BLAST_FURNACE_LEFT_WALL, ETRIONIC_BLAST_FURNACE_RIGHT_WALL, ETRIONIC_BLAST_FURNACE_BACK_WALL, ETRIONIC_BLAST_FURNACE_WEST_STACK};
            case NORTH:
            default:
                return new AxisAlignedBB[]{ETRIONIC_BLAST_FURNACE_BASE, ETRIONIC_BLAST_FURNACE_BACK_WALL, ETRIONIC_BLAST_FURNACE_FRONT_WALL, ETRIONIC_BLAST_FURNACE_LEFT_WALL, ETRIONIC_BLAST_FURNACE_RIGHT_WALL, ETRIONIC_BLAST_FURNACE_NORTH_STACK};
        }
    }

    private AxisAlignedBB union(AxisAlignedBB[] boxes) {
        AxisAlignedBB result = boxes[0];
        for (int i = 1; i < boxes.length; i++) {
            AxisAlignedBB box = boxes[i];
            result = new AxisAlignedBB(
                Math.min(result.minX, box.minX),
                Math.min(result.minY, box.minY),
                Math.min(result.minZ, box.minZ),
                Math.max(result.maxX, box.maxX),
                Math.max(result.maxY, box.maxY),
                Math.max(result.maxZ, box.maxZ));
        }
        return result;
    }

    private boolean isWaterPump() {
        return getRegistryName() != null && "water_pump".equals(getRegistryName().getPath());
    }

    private boolean isEtrionicBlastFurnace() {
        return getRegistryName() != null && "etrionic_blast_furnace".equals(getRegistryName().getPath());
    }

    private boolean isNonFullModelMachine() {
        if (getRegistryName() == null) {
            return false;
        }
        String path = getRegistryName().getPath();
        return "nasa_workbench".equals(path)
            || "solar_panel".equals(path)
            || "water_pump".equals(path)
            || "etrionic_blast_furnace".equals(path);
    }

    private boolean isCutoutModelMachine() {
        if (getRegistryName() == null) {
            return false;
        }
        String path = getRegistryName().getPath();
        return "nasa_workbench".equals(path)
            || "solar_panel".equals(path);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return ModTileEntities.createForBlock(this);
    }
}
