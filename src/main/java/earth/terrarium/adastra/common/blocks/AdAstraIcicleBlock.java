package earth.terrarium.adastra.common.blocks;

import earth.terrarium.adastra.common.AdAstraCreativeTab;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

/** A small 1.12.2 equivalent of the pointed-dripstone icicle. */
public final class AdAstraIcicleBlock extends Block {

    public static final PropertyEnum<Thickness> THICKNESS = PropertyEnum.create("thickness", Thickness.class);
    public static final PropertyEnum<VerticalDirection> VERTICAL_DIRECTION =
        PropertyEnum.create("vertical_direction", VerticalDirection.class);

    public AdAstraIcicleBlock() {
        super(Material.ICE);
        setCreativeTab(AdAstraCreativeTab.INSTANCE);
        setHardness(0.5F);
        setResistance(0.5F);
        setSoundType(SoundType.GLASS);
        setDefaultState(blockState.getBaseState()
            .withProperty(THICKNESS, Thickness.TIP)
            .withProperty(VERTICAL_DIRECTION, VerticalDirection.UP));
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        Thickness[] thicknesses = Thickness.values();
        int thickness = Math.min(thicknesses.length - 1, meta >> 1);
        VerticalDirection direction = (meta & 1) == 0 ? VerticalDirection.UP : VerticalDirection.DOWN;
        return getDefaultState().withProperty(THICKNESS, thicknesses[thickness])
            .withProperty(VERTICAL_DIRECTION, direction);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(THICKNESS).ordinal() * 2
            + (state.getValue(VERTICAL_DIRECTION) == VerticalDirection.DOWN ? 1 : 0);
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing,
                                            float hitX, float hitY, float hitZ, int meta,
                                            EntityLivingBase placer) {
        EnumFacing direction = getPlacementDirection(facing, placer);
        if (!isValidPlacement(world, pos, direction)) {
            direction = direction.getOpposite();
            if (!isValidPlacement(world, pos, direction)) {
                return null;
            }
        }

        boolean mergeTip = placer == null || !placer.isSneaking();
        Thickness thickness = calculateThickness(world, pos, direction, mergeTip);
        return getDefaultState()
            .withProperty(THICKNESS, thickness)
            .withProperty(VERTICAL_DIRECTION, VerticalDirection.fromFacing(direction));
    }

    @Override
    public boolean canPlaceBlockOnSide(World world, BlockPos pos, EnumFacing side) {
        // ItemBlock in 1.12.2 does not handle a null placement state. Reject
        // unsupported side positions before getStateForPlacement is called.
        return isValidPlacement(world, pos, EnumFacing.UP)
            || isValidPlacement(world, pos, EnumFacing.DOWN);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, THICKNESS, VERTICAL_DIRECTION);
    }

    public boolean canSurvive(IBlockAccess world, BlockPos pos, IBlockState state) {
        return isValidPlacement(world, pos, state.getValue(VERTICAL_DIRECTION).toFacing());
    }

    @Override
    public BlockRenderLayer getRenderLayer() {
        // Icicle textures contain hard transparent pixels, like vanilla plants.
        return BlockRenderLayer.CUTOUT_MIPPED;
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
    public int getLightOpacity(IBlockState state) {
        return 0;
    }

    public static void grow(World world, AdAstraIcicleBlock block, BlockPos tipPos,
                            EnumFacing direction, int height, boolean mergeTip) {
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos(tipPos.getX(), tipPos.getY(), tipPos.getZ());
        BlockPos support = cursor.offset(direction.getOpposite());
        if (!world.getBlockState(support).getMaterial().isSolid()) {
            return;
        }

        for (int index = 0; index < Math.max(1, height); index++) {
            if (!world.isAirBlock(cursor)) {
                return;
            }
            Thickness thickness;
            if (height >= 3 && index == 0) {
                thickness = Thickness.BASE;
            } else if (height >= 3 && index < height - 2) {
                thickness = Thickness.MIDDLE;
            } else if (height >= 2 && index == height - 2) {
                thickness = Thickness.FRUSTUM;
            } else {
                thickness = index == height - 1 && mergeTip ? Thickness.TIP_MERGE : Thickness.TIP;
            }
            world.setBlockState(cursor, block.getDefaultState()
                .withProperty(THICKNESS, thickness)
                .withProperty(VERTICAL_DIRECTION, VerticalDirection.fromFacing(direction)), 2);
            cursor.move(direction);
        }
    }

    private static EnumFacing getPlacementDirection(EnumFacing clickedSide, EntityLivingBase placer) {
        if (clickedSide == EnumFacing.UP || clickedSide == EnumFacing.DOWN) {
            return clickedSide;
        }
        if (placer == null) {
            return EnumFacing.UP;
        }
        return placer.rotationPitch >= 0.0F ? EnumFacing.UP : EnumFacing.DOWN;
    }

    private static boolean isValidPlacement(IBlockAccess world, BlockPos pos, EnumFacing direction) {
        BlockPos support = pos.offset(direction.getOpposite());
        IBlockState supportState = world.getBlockState(support);
        return supportState.getMaterial().isSolid()
            || isPointedDripstoneWithDirection(supportState, direction);
    }

    private static Thickness calculateThickness(IBlockAccess world, BlockPos pos,
                                                 EnumFacing direction, boolean mergeTip) {
        EnumFacing opposite = direction.getOpposite();
        IBlockState tipSide = world.getBlockState(pos.offset(direction));
        if (isPointedDripstoneWithDirection(tipSide, opposite)) {
            return !mergeTip && tipSide.getValue(THICKNESS) != Thickness.TIP_MERGE
                ? Thickness.TIP
                : Thickness.TIP_MERGE;
        }
        if (!isPointedDripstoneWithDirection(tipSide, direction)) {
            return Thickness.TIP;
        }

        Thickness neighborThickness = tipSide.getValue(THICKNESS);
        if (neighborThickness == Thickness.TIP || neighborThickness == Thickness.TIP_MERGE) {
            return Thickness.FRUSTUM;
        }

        IBlockState baseSide = world.getBlockState(pos.offset(opposite));
        return isPointedDripstoneWithDirection(baseSide, direction)
            ? Thickness.MIDDLE
            : Thickness.BASE;
    }

    private static boolean isPointedDripstoneWithDirection(IBlockState state, EnumFacing direction) {
        return state.getBlock() instanceof AdAstraIcicleBlock
            && state.getValue(VERTICAL_DIRECTION).toFacing() == direction;
    }

    public enum Thickness implements IStringSerializable {
        BASE("base"), FRUSTUM("frustum"), MIDDLE("middle"), TIP("tip"), TIP_MERGE("tip_merge");

        private final String name;

        Thickness(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }

    public enum VerticalDirection implements IStringSerializable {
        UP("up", EnumFacing.UP), DOWN("down", EnumFacing.DOWN);

        private final String name;
        private final EnumFacing facing;

        VerticalDirection(String name, EnumFacing facing) {
            this.name = name;
            this.facing = facing;
        }

        public EnumFacing toFacing() {
            return facing;
        }

        public static VerticalDirection fromFacing(EnumFacing facing) {
            return facing == EnumFacing.DOWN ? DOWN : UP;
        }

        @Override
        public String getName() {
            return name;
        }
    }
}
