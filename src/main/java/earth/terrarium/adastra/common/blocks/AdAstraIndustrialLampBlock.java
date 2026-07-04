package earth.terrarium.adastra.common.blocks;

import earth.terrarium.adastra.common.AdAstraCreativeTab;
import net.minecraft.block.Block;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class AdAstraIndustrialLampBlock extends Block {

    public static final PropertyDirection FACING = BlockHorizontal.FACING;
    public static final PropertyEnum<AttachFace> FACE = PropertyEnum.create("face", AttachFace.class);

    private static final AxisAlignedBB NORTH_AABB = new AxisAlignedBB(0.0d, 0.1875d, 0.5d, 1.0d, 0.8125d, 1.0d);
    private static final AxisAlignedBB EAST_AABB = new AxisAlignedBB(0.0d, 0.1875d, 0.0d, 0.5d, 0.8125d, 1.0d);
    private static final AxisAlignedBB SOUTH_AABB = new AxisAlignedBB(0.0d, 0.1875d, 0.0d, 1.0d, 0.8125d, 0.5d);
    private static final AxisAlignedBB WEST_AABB = new AxisAlignedBB(0.5d, 0.1875d, 0.0d, 1.0d, 0.8125d, 1.0d);
    private static final AxisAlignedBB FLOOR_X_AABB = new AxisAlignedBB(0.1875d, 0.0d, 0.0d, 0.8125d, 0.5d, 1.0d);
    private static final AxisAlignedBB FLOOR_Z_AABB = new AxisAlignedBB(0.0d, 0.0d, 0.1875d, 1.0d, 0.5d, 0.8125d);
    private static final AxisAlignedBB CEILING_X_AABB = new AxisAlignedBB(0.1875d, 0.5d, 0.0d, 0.8125d, 1.0d, 1.0d);
    private static final AxisAlignedBB CEILING_Z_AABB = new AxisAlignedBB(0.0d, 0.5d, 0.1875d, 1.0d, 1.0d, 0.8125d);
    private static final AxisAlignedBB NORTH_MOUNT_AABB = new AxisAlignedBB(0.0d, 3.0d / 16.0d, 14.0d / 16.0d, 1.0d, 13.0d / 16.0d, 1.0d);
    private static final AxisAlignedBB NORTH_BODY_AABB = new AxisAlignedBB(1.0d / 16.0d, 4.0d / 16.0d, 8.0d / 16.0d, 15.0d / 16.0d, 12.0d / 16.0d, 14.0d / 16.0d);
    private static final AxisAlignedBB EAST_MOUNT_AABB = new AxisAlignedBB(0.0d, 3.0d / 16.0d, 0.0d, 2.0d / 16.0d, 13.0d / 16.0d, 1.0d);
    private static final AxisAlignedBB EAST_BODY_AABB = new AxisAlignedBB(2.0d / 16.0d, 4.0d / 16.0d, 1.0d / 16.0d, 8.0d / 16.0d, 12.0d / 16.0d, 15.0d / 16.0d);
    private static final AxisAlignedBB SOUTH_MOUNT_AABB = new AxisAlignedBB(0.0d, 3.0d / 16.0d, 0.0d, 1.0d, 13.0d / 16.0d, 2.0d / 16.0d);
    private static final AxisAlignedBB SOUTH_BODY_AABB = new AxisAlignedBB(1.0d / 16.0d, 4.0d / 16.0d, 2.0d / 16.0d, 15.0d / 16.0d, 12.0d / 16.0d, 8.0d / 16.0d);
    private static final AxisAlignedBB WEST_MOUNT_AABB = new AxisAlignedBB(14.0d / 16.0d, 3.0d / 16.0d, 0.0d, 1.0d, 13.0d / 16.0d, 1.0d);
    private static final AxisAlignedBB WEST_BODY_AABB = new AxisAlignedBB(8.0d / 16.0d, 4.0d / 16.0d, 1.0d / 16.0d, 14.0d / 16.0d, 12.0d / 16.0d, 15.0d / 16.0d);
    private static final AxisAlignedBB FLOOR_X_MOUNT_AABB = new AxisAlignedBB(3.0d / 16.0d, 0.0d, 0.0d, 13.0d / 16.0d, 2.0d / 16.0d, 1.0d);
    private static final AxisAlignedBB FLOOR_X_BODY_AABB = new AxisAlignedBB(4.0d / 16.0d, 2.0d / 16.0d, 1.0d / 16.0d, 12.0d / 16.0d, 8.0d / 16.0d, 15.0d / 16.0d);
    private static final AxisAlignedBB FLOOR_Z_MOUNT_AABB = new AxisAlignedBB(0.0d, 0.0d, 3.0d / 16.0d, 1.0d, 2.0d / 16.0d, 13.0d / 16.0d);
    private static final AxisAlignedBB FLOOR_Z_BODY_AABB = new AxisAlignedBB(1.0d / 16.0d, 2.0d / 16.0d, 4.0d / 16.0d, 15.0d / 16.0d, 8.0d / 16.0d, 12.0d / 16.0d);
    private static final AxisAlignedBB CEILING_X_MOUNT_AABB = new AxisAlignedBB(3.0d / 16.0d, 14.0d / 16.0d, 0.0d, 13.0d / 16.0d, 1.0d, 1.0d);
    private static final AxisAlignedBB CEILING_X_BODY_AABB = new AxisAlignedBB(4.0d / 16.0d, 8.0d / 16.0d, 1.0d / 16.0d, 12.0d / 16.0d, 14.0d / 16.0d, 15.0d / 16.0d);
    private static final AxisAlignedBB CEILING_Z_MOUNT_AABB = new AxisAlignedBB(0.0d, 14.0d / 16.0d, 3.0d / 16.0d, 1.0d, 1.0d, 13.0d / 16.0d);
    private static final AxisAlignedBB CEILING_Z_BODY_AABB = new AxisAlignedBB(1.0d / 16.0d, 8.0d / 16.0d, 4.0d / 16.0d, 15.0d / 16.0d, 14.0d / 16.0d, 12.0d / 16.0d);

    private final boolean small;

    public AdAstraIndustrialLampBlock(boolean small, int lightValue) {
        super(Material.IRON);
        this.small = small;
        setCreativeTab(AdAstraCreativeTab.INSTANCE);
        setHardness(5.0f);
        setResistance(12.0f);
        setSoundType(SoundType.METAL);
        setHarvestLevel("pickaxe", 1);
        setLightLevel(lightValue / 15.0f);
        setDefaultState(blockState.getBaseState()
            .withProperty(FACING, EnumFacing.NORTH)
            .withProperty(FACE, AttachFace.WALL));
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        if (small) {
            return smallBox(state);
        }
        switch (state.getValue(FACE)) {
            case FLOOR:
                return state.getValue(FACING).getAxis() == EnumFacing.Axis.X ? FLOOR_X_AABB : FLOOR_Z_AABB;
            case CEILING:
                return state.getValue(FACING).getAxis() == EnumFacing.Axis.X ? CEILING_X_AABB : CEILING_Z_AABB;
            case WALL:
            default:
                switch (state.getValue(FACING)) {
                    case EAST:
                        return EAST_AABB;
                    case SOUTH:
                        return SOUTH_AABB;
                    case WEST:
                        return WEST_AABB;
                    case NORTH:
                    default:
                        return NORTH_AABB;
                }
        }
    }

    @Override
    public void addCollisionBoxToList(IBlockState state, World world, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entity, boolean isActualState) {
        if (small) {
            addCollisionBoxToList(pos, entityBox, collidingBoxes, smallBox(state));
            return;
        }
        for (AxisAlignedBB box : largeCollisionBoxes(state)) {
            addCollisionBoxToList(pos, entityBox, collidingBoxes, box);
        }
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        EnumFacing horizontal = placer.getHorizontalFacing().getOpposite();
        if (facing == EnumFacing.UP) {
            return getDefaultState().withProperty(FACE, AttachFace.FLOOR).withProperty(FACING, horizontal);
        }
        if (facing == EnumFacing.DOWN) {
            return getDefaultState().withProperty(FACE, AttachFace.CEILING).withProperty(FACING, horizontal);
        }
        return getDefaultState().withProperty(FACE, AttachFace.WALL).withProperty(FACING, facing);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        EnumFacing facing = EnumFacing.byHorizontalIndex(meta & 3);
        AttachFace face = AttachFace.byMetadata((meta >> 2) & 3);
        return getDefaultState().withProperty(FACING, facing).withProperty(FACE, face);
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
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING, FACE);
    }

    private AxisAlignedBB smallBox(IBlockState state) {
        switch (state.getValue(FACE)) {
            case FLOOR:
                return new AxisAlignedBB(0.1875d, 0.0d, 0.1875d, 0.8125d, 0.5d, 0.8125d);
            case CEILING:
                return new AxisAlignedBB(0.1875d, 0.5d, 0.1875d, 0.8125d, 1.0d, 0.8125d);
            case WALL:
            default:
                switch (state.getValue(FACING)) {
                    case EAST:
                        return new AxisAlignedBB(0.0d, 0.1875d, 0.1875d, 0.5d, 0.8125d, 0.8125d);
                    case SOUTH:
                        return new AxisAlignedBB(0.1875d, 0.1875d, 0.0d, 0.8125d, 0.8125d, 0.5d);
                    case WEST:
                        return new AxisAlignedBB(0.5d, 0.1875d, 0.1875d, 1.0d, 0.8125d, 0.8125d);
                    case NORTH:
                    default:
                        return new AxisAlignedBB(0.1875d, 0.1875d, 0.5d, 0.8125d, 0.8125d, 1.0d);
                }
        }
    }

    private AxisAlignedBB[] largeCollisionBoxes(IBlockState state) {
        switch (state.getValue(FACE)) {
            case FLOOR:
                return state.getValue(FACING).getAxis() == EnumFacing.Axis.X
                    ? new AxisAlignedBB[]{FLOOR_X_MOUNT_AABB, FLOOR_X_BODY_AABB}
                    : new AxisAlignedBB[]{FLOOR_Z_MOUNT_AABB, FLOOR_Z_BODY_AABB};
            case CEILING:
                return state.getValue(FACING).getAxis() == EnumFacing.Axis.X
                    ? new AxisAlignedBB[]{CEILING_X_MOUNT_AABB, CEILING_X_BODY_AABB}
                    : new AxisAlignedBB[]{CEILING_Z_MOUNT_AABB, CEILING_Z_BODY_AABB};
            case WALL:
            default:
                switch (state.getValue(FACING)) {
                    case EAST:
                        return new AxisAlignedBB[]{EAST_MOUNT_AABB, EAST_BODY_AABB};
                    case SOUTH:
                        return new AxisAlignedBB[]{SOUTH_MOUNT_AABB, SOUTH_BODY_AABB};
                    case WEST:
                        return new AxisAlignedBB[]{WEST_MOUNT_AABB, WEST_BODY_AABB};
                    case NORTH:
                    default:
                        return new AxisAlignedBB[]{NORTH_MOUNT_AABB, NORTH_BODY_AABB};
                }
        }
    }

    public enum AttachFace implements IStringSerializable {
        FLOOR(0, "floor"),
        WALL(1, "wall"),
        CEILING(2, "ceiling");

        private final int metadata;
        private final String name;

        AttachFace(int metadata, String name) {
            this.metadata = metadata;
            this.name = name;
        }

        public int getMetadata() {
            return metadata;
        }

        @Override
        public String getName() {
            return name;
        }

        public static AttachFace byMetadata(int metadata) {
            for (AttachFace face : values()) {
                if (face.metadata == metadata) {
                    return face;
                }
            }
            return WALL;
        }
    }
}
