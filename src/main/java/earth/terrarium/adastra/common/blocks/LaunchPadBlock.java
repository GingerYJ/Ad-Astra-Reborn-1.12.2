package earth.terrarium.adastra.common.blocks;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Locale;

/**
 * Launch pad block for rocket placement.
 * Forms a 3x3 multi-block structure that provides a launch platform.
 */
public class LaunchPadBlock extends AdAstraModelBlock {

    public static final PropertyEnum<Part> PART = PropertyEnum.create("part", Part.class);
    private static final AxisAlignedBB COLLISION_BOX = new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.0625, 1.0);
    private boolean removingStructure;

    public LaunchPadBlock(Material material, float hardness, float resistance) {
        super(material, hardness, resistance);
        setDefaultState(blockState.getBaseState().withProperty(PART, Part.CENTER));
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, net.minecraft.world.IBlockAccess source, BlockPos pos) {
        return COLLISION_BOX;
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, net.minecraft.world.IBlockAccess worldIn, BlockPos pos) {
        return COLLISION_BOX;
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return state.getValue(PART).isController() ? EnumBlockRenderType.MODEL : EnumBlockRenderType.INVISIBLE;
    }

    @Override
    public boolean canPlaceBlockAt(World world, BlockPos pos) {
        return canPlaceStructureAt(world, pos);
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        if (world.isRemote) {
            return;
        }
        for (Part part : Part.values()) {
            BlockPos partPos = getPartPos(pos, part);
            world.setBlockState(partPos, state.withProperty(PART, part), 3);
        }
    }

    @Override
    public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
        if (!world.isRemote && !removingStructure) {
            BlockPos center = findLaunchPadCenter(world, pos);
            if (center != null) {
                if (!player.capabilities.isCreativeMode && willHarvest) {
                    spawnAsEntity(world, center, new ItemStack(this));
                }
                destroyStructure(world, center, false);
                return true;
            }
        }
        return super.removedByPlayer(state, world, pos, player, willHarvest);
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        if (!world.isRemote && !removingStructure) {
            BlockPos center = findLaunchPadCenter(world, pos);
            if (center != null) {
                destroyStructure(world, center, false);
            }
        }
        super.breakBlock(world, pos, state);
    }

    @Override
    public void harvestBlock(World world, EntityPlayer player, BlockPos pos, IBlockState state, TileEntity tile, ItemStack stack) {
        // The 3x3 structure drops exactly one item from removedByPlayer.
    }

    @Override
    public void dropBlockAsItemWithChance(World world, BlockPos pos, IBlockState state, float chance, int fortune) {
        if (state.getValue(PART).isController()) {
            super.dropBlockAsItemWithChance(world, pos, state, chance, fortune);
        }
    }

    private boolean canPlaceStructureAt(World world, BlockPos centerPos) {
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                BlockPos partPos = centerPos.add(dx, 0, dz);
                IBlockState state = world.getBlockState(partPos);
                if (!state.getBlock().isReplaceable(world, partPos)) {
                    return false;
                }
                if (!world.getBlockState(partPos.down()).isSideSolid(world, partPos.down(), net.minecraft.util.EnumFacing.UP)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Check if a valid 3x3 launch pad structure exists at the given position.
     * @param world The world
     * @param centerPos Center position of the potential launch pad
     * @return true if a valid 3x3 structure exists
     */
    public static boolean isValidLaunchPadStructure(World world, BlockPos centerPos) {
        for (Part part : Part.values()) {
            BlockPos checkPos = getPartPos(centerPos, part);
            IBlockState state = world.getBlockState(checkPos);
            if (!(state.getBlock() instanceof LaunchPadBlock)) {
                return false;
            }
            if (state.getValue(PART) != part) {
                return false;
            }
        }
        return true;
    }

    private static boolean hasLaunchPadBlocksIn3x3(World world, BlockPos centerPos) {
        for (Part part : Part.values()) {
            if (!(world.getBlockState(getPartPos(centerPos, part)).getBlock() instanceof LaunchPadBlock)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Find the center of the launch pad structure from any position within it.
     * @param world The world
     * @param startPos A position that might be part of a launch pad
     * @return The center position if a valid structure exists, null otherwise
     */
    public static BlockPos findLaunchPadCenter(World world, BlockPos startPos) {
        IBlockState startState = world.getBlockState(startPos);
        if (startState.getBlock() instanceof LaunchPadBlock) {
            BlockPos center = getControllerPos(startPos, startState.getValue(PART));
            if (isValidLaunchPadStructure(world, center)) {
                return center;
            }
        }

        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                BlockPos testCenter = startPos.add(dx, 0, dz);
                if (isValidLaunchPadStructure(world, testCenter)) {
                    return testCenter;
                }
            }
        }
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                BlockPos testCenter = startPos.add(dx, 0, dz);
                if (hasLaunchPadBlocksIn3x3(world, testCenter)) {
                    return testCenter;
                }
            }
        }
        return null;
    }

    /**
     * Check if a rocket can launch from this position.
     * @param world The world
     * @param rocketPos The rocket's position
     * @return true if the rocket is positioned on a valid launch pad
     */
    public static boolean canRocketLaunchFrom(World world, BlockPos rocketPos) {
        BlockPos below = rocketPos.down();
        BlockPos center = findLaunchPadCenter(world, below);
        return center != null;
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        Part[] values = Part.values();
        return getDefaultState().withProperty(PART, values[Math.max(0, Math.min(meta, values.length - 1))]);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(PART).ordinal();
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, PART);
    }

    private static BlockPos getPartPos(BlockPos centerPos, Part part) {
        return centerPos.add(part.xOffset, 0, part.zOffset);
    }

    private static BlockPos getControllerPos(BlockPos pos, Part part) {
        return pos.add(-part.xOffset, 0, -part.zOffset);
    }

    private void destroyStructure(World world, BlockPos center, boolean dropController) {
        removingStructure = true;
        try {
            for (Part part : Part.values()) {
                BlockPos partPos = getPartPos(center, part);
                IBlockState partState = world.getBlockState(partPos);
                if (partState.getBlock() instanceof LaunchPadBlock) {
                    world.destroyBlock(partPos, dropController && part.isController());
                }
            }
        } finally {
            removingStructure = false;
        }
    }

    /**
     * Called when a player right-clicks the launch pad.
     * Can be used to check for nearby rockets and initiate launch.
     */
    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
                                   net.minecraft.util.EnumHand hand, net.minecraft.util.EnumFacing facing,
                                   float hitX, float hitY, float hitZ) {
        return super.onBlockActivated(worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ);
    }

    public enum Part implements IStringSerializable {
        TOP_LEFT(-1, 1),
        TOP(0, 1),
        TOP_RIGHT(1, 1),
        LEFT(-1, 0),
        CENTER(0, 0),
        RIGHT(1, 0),
        BOTTOM_LEFT(-1, -1),
        BOTTOM(0, -1),
        BOTTOM_RIGHT(1, -1);

        private final int xOffset;
        private final int zOffset;

        Part(int xOffset, int zOffset) {
            this.xOffset = xOffset;
            this.zOffset = zOffset;
        }

        public boolean isController() {
            return this == CENTER;
        }

        @Override
        public String getName() {
            return name().toLowerCase(Locale.ROOT);
        }

        @Override
        public String toString() {
            return getName();
        }
    }
}
