package earth.terrarium.adastra.common.blocks;

import earth.terrarium.adastra.common.registry.ModTileEntities;
import earth.terrarium.adastra.common.tile.GravityNormalizerTileEntity;
import earth.terrarium.adastra.common.tile.OxygenDistributorTileEntity;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class AdAstraAttachedMachineBlock extends AdAstraModelBlock implements ITileEntityProvider {

    private static final AxisAlignedBB OXYGEN_DISTRIBUTOR_FLOOR_BASE = new AxisAlignedBB(1.0 / 16.0, 0.0, 1.0 / 16.0, 15.0 / 16.0, 5.0 / 16.0, 15.0 / 16.0);
    private static final AxisAlignedBB OXYGEN_DISTRIBUTOR_FLOOR_POST = new AxisAlignedBB(4.0 / 16.0, 5.0 / 16.0, 4.0 / 16.0, 12.0 / 16.0, 15.0 / 16.0, 12.0 / 16.0);
    private static final AxisAlignedBB OXYGEN_DISTRIBUTOR_CEILING_BASE = new AxisAlignedBB(1.0 / 16.0, 11.0 / 16.0, 1.0 / 16.0, 15.0 / 16.0, 1.0, 15.0 / 16.0);
    private static final AxisAlignedBB OXYGEN_DISTRIBUTOR_CEILING_POST = new AxisAlignedBB(4.0 / 16.0, 1.0 / 16.0, 4.0 / 16.0, 12.0 / 16.0, 11.0 / 16.0, 12.0 / 16.0);
    private static final AxisAlignedBB OXYGEN_DISTRIBUTOR_NORTH_BASE = new AxisAlignedBB(1.0 / 16.0, 1.0 / 16.0, 11.0 / 16.0, 15.0 / 16.0, 15.0 / 16.0, 1.0);
    private static final AxisAlignedBB OXYGEN_DISTRIBUTOR_NORTH_POST = new AxisAlignedBB(4.0 / 16.0, 4.0 / 16.0, 1.0 / 16.0, 12.0 / 16.0, 12.0 / 16.0, 11.0 / 16.0);
    private static final AxisAlignedBB OXYGEN_DISTRIBUTOR_EAST_BASE = new AxisAlignedBB(0.0, 1.0 / 16.0, 1.0 / 16.0, 5.0 / 16.0, 15.0 / 16.0, 15.0 / 16.0);
    private static final AxisAlignedBB OXYGEN_DISTRIBUTOR_EAST_POST = new AxisAlignedBB(5.0 / 16.0, 4.0 / 16.0, 4.0 / 16.0, 15.0 / 16.0, 12.0 / 16.0, 12.0 / 16.0);
    private static final AxisAlignedBB OXYGEN_DISTRIBUTOR_SOUTH_BASE = new AxisAlignedBB(1.0 / 16.0, 1.0 / 16.0, 0.0, 15.0 / 16.0, 15.0 / 16.0, 5.0 / 16.0);
    private static final AxisAlignedBB OXYGEN_DISTRIBUTOR_SOUTH_POST = new AxisAlignedBB(4.0 / 16.0, 4.0 / 16.0, 5.0 / 16.0, 12.0 / 16.0, 12.0 / 16.0, 15.0 / 16.0);
    private static final AxisAlignedBB OXYGEN_DISTRIBUTOR_WEST_BASE = new AxisAlignedBB(11.0 / 16.0, 1.0 / 16.0, 1.0 / 16.0, 1.0, 15.0 / 16.0, 15.0 / 16.0);
    private static final AxisAlignedBB OXYGEN_DISTRIBUTOR_WEST_POST = new AxisAlignedBB(1.0 / 16.0, 4.0 / 16.0, 4.0 / 16.0, 11.0 / 16.0, 12.0 / 16.0, 12.0 / 16.0);
    private static final AxisAlignedBB GRAVITY_NORMALIZER_FLOOR = new AxisAlignedBB(1.0 / 16.0, 0.0, 1.0 / 16.0, 15.0 / 16.0, 15.0 / 16.0, 15.0 / 16.0);
    private static final AxisAlignedBB GRAVITY_NORMALIZER_CEILING = new AxisAlignedBB(1.0 / 16.0, 1.0 / 16.0, 1.0 / 16.0, 15.0 / 16.0, 1.0, 15.0 / 16.0);
    private static final AxisAlignedBB GRAVITY_NORMALIZER_NORTH = new AxisAlignedBB(1.0 / 16.0, 1.0 / 16.0, 1.0 / 16.0, 15.0 / 16.0, 15.0 / 16.0, 1.0);
    private static final AxisAlignedBB GRAVITY_NORMALIZER_EAST = new AxisAlignedBB(0.0, 1.0 / 16.0, 1.0 / 16.0, 15.0 / 16.0, 15.0 / 16.0, 15.0 / 16.0);
    private static final AxisAlignedBB GRAVITY_NORMALIZER_SOUTH = new AxisAlignedBB(1.0 / 16.0, 1.0 / 16.0, 0.0, 15.0 / 16.0, 15.0 / 16.0, 15.0 / 16.0);
    private static final AxisAlignedBB GRAVITY_NORMALIZER_WEST = new AxisAlignedBB(1.0 / 16.0, 1.0 / 16.0, 1.0 / 16.0, 1.0, 15.0 / 16.0, 15.0 / 16.0);

    public static final PropertyDirection FACING = BlockHorizontal.FACING;
    public static final PropertyEnum<AdAstraIndustrialLampBlock.AttachFace> FACE = PropertyEnum.create("face", AdAstraIndustrialLampBlock.AttachFace.class);
    public static final PropertyBool LIT = PropertyBool.create("lit");
    public static final PropertyBool POWERED = PropertyBool.create("powered");

    public AdAstraAttachedMachineBlock(Material material, float hardness, float resistance) {
        super(material, hardness, resistance);
        setSoundType(SoundType.METAL);
        setHarvestLevel("pickaxe", 1);
        setDefaultState(blockState.getBaseState()
            .withProperty(FACE, AdAstraIndustrialLampBlock.AttachFace.FLOOR)
            .withProperty(FACING, EnumFacing.NORTH)
            .withProperty(LIT, false)
            .withProperty(POWERED, false));
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        EnumFacing horizontal = placer.getHorizontalFacing().getOpposite();
        AdAstraIndustrialLampBlock.AttachFace face = AdAstraIndustrialLampBlock.AttachFace.FLOOR;
        if (facing == EnumFacing.DOWN) {
            face = AdAstraIndustrialLampBlock.AttachFace.CEILING;
        } else if (facing != EnumFacing.UP) {
            face = AdAstraIndustrialLampBlock.AttachFace.WALL;
            horizontal = facing;
        }
        return getDefaultState()
            .withProperty(FACE, face)
            .withProperty(FACING, horizontal)
            .withProperty(POWERED, world.isBlockPowered(pos));
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        world.setBlockState(pos, state.withProperty(POWERED, world.isBlockPowered(pos)), 2);
    }

    @Override
    public void neighborChanged(IBlockState state, World world, BlockPos pos, net.minecraft.block.Block block, BlockPos fromPos) {
        boolean powered = world.isBlockPowered(pos);
        if (state.getValue(POWERED) != powered) {
            world.setBlockState(pos, state.withProperty(POWERED, powered), 2);
        }
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState()
            .withProperty(FACING, EnumFacing.byHorizontalIndex(meta & 3))
            .withProperty(FACE, AdAstraIndustrialLampBlock.AttachFace.byMetadata((meta >> 2) & 3));
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(FACING).getHorizontalIndex() | (state.getValue(FACE).getMetadata() << 2);
    }

    @Override
    public IBlockState withRotation(IBlockState state, Rotation rotation) {
        return state.withProperty(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public IBlockState withMirror(IBlockState state, Mirror mirror) {
        return state.withRotation(mirror.toRotation(state.getValue(FACING)));
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACE, FACING, LIT, POWERED);
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        return AdAstraMachineGuiHelper.openMachineGui(world, pos, player, hand);
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        if (isGravityNormalizer()) {
            return gravityNormalizerBox(state);
        }
        if (isOxygenDistributor()) {
            return union(oxygenDistributorBoxes(state));
        }
        return super.getBoundingBox(state, source, pos);
    }

    @Override
    public void addCollisionBoxToList(IBlockState state, World world, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entity, boolean isActualState) {
        if (isGravityNormalizer()) {
            addCollisionBoxToList(pos, entityBox, collidingBoxes, gravityNormalizerBox(state));
            return;
        }
        if (isOxygenDistributor()) {
            for (AxisAlignedBB box : oxygenDistributorBoxes(state)) {
                addCollisionBoxToList(pos, entityBox, collidingBoxes, box);
            }
            return;
        }
        super.addCollisionBoxToList(state, world, pos, entityBox, collidingBoxes, entity, isActualState);
    }

    @Override
    public boolean hasComparatorInputOverride(IBlockState state) {
        return true;
    }

    @Override
    public int getComparatorInputOverride(IBlockState blockState, World world, BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof OxygenDistributorTileEntity) {
            OxygenDistributorTileEntity distributor = (OxygenDistributorTileEntity) tile;
            return calculateDistributionSignal(distributor.distributedBlocksCount(), distributor.distributedBlocksLimit());
        }
        if (tile instanceof GravityNormalizerTileEntity) {
            GravityNormalizerTileEntity normalizer = (GravityNormalizerTileEntity) tile;
            return calculateDistributionSignal(normalizer.distributedBlocksCount(), normalizer.distributedBlocksLimit());
        }
        return 0;
    }

    private int calculateDistributionSignal(int count, int limit) {
        if (count <= 0 || limit <= 0) {
            return 0;
        }
        return Math.max(0, Math.min(15, count * 15 / limit));
    }

    private AxisAlignedBB[] oxygenDistributorBoxes(IBlockState state) {
        switch (state.getValue(FACE)) {
            case CEILING:
                return new AxisAlignedBB[]{OXYGEN_DISTRIBUTOR_CEILING_BASE, OXYGEN_DISTRIBUTOR_CEILING_POST};
            case WALL:
                switch (state.getValue(FACING)) {
                    case EAST:
                        return new AxisAlignedBB[]{OXYGEN_DISTRIBUTOR_EAST_BASE, OXYGEN_DISTRIBUTOR_EAST_POST};
                    case SOUTH:
                        return new AxisAlignedBB[]{OXYGEN_DISTRIBUTOR_SOUTH_BASE, OXYGEN_DISTRIBUTOR_SOUTH_POST};
                    case WEST:
                        return new AxisAlignedBB[]{OXYGEN_DISTRIBUTOR_WEST_BASE, OXYGEN_DISTRIBUTOR_WEST_POST};
                    case NORTH:
                    default:
                        return new AxisAlignedBB[]{OXYGEN_DISTRIBUTOR_NORTH_BASE, OXYGEN_DISTRIBUTOR_NORTH_POST};
                }
            case FLOOR:
            default:
                return new AxisAlignedBB[]{OXYGEN_DISTRIBUTOR_FLOOR_BASE, OXYGEN_DISTRIBUTOR_FLOOR_POST};
        }
    }

    private AxisAlignedBB gravityNormalizerBox(IBlockState state) {
        switch (state.getValue(FACE)) {
            case CEILING:
                return GRAVITY_NORMALIZER_CEILING;
            case WALL:
                switch (state.getValue(FACING)) {
                    case EAST:
                        return GRAVITY_NORMALIZER_EAST;
                    case SOUTH:
                        return GRAVITY_NORMALIZER_SOUTH;
                    case WEST:
                        return GRAVITY_NORMALIZER_WEST;
                    case NORTH:
                    default:
                        return GRAVITY_NORMALIZER_NORTH;
                }
            case FLOOR:
            default:
                return GRAVITY_NORMALIZER_FLOOR;
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

    private boolean isOxygenDistributor() {
        return getRegistryName() != null && "oxygen_distributor".equals(getRegistryName().getPath());
    }

    private boolean isGravityNormalizer() {
        return getRegistryName() != null && "gravity_normalizer".equals(getRegistryName().getPath());
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return ModTileEntities.createForBlock(this);
    }
}
