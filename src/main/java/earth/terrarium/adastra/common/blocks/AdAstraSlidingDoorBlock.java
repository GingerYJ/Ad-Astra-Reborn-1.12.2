package earth.terrarium.adastra.common.blocks;

import earth.terrarium.adastra.common.items.AdAstraWrenchItem;
import earth.terrarium.adastra.common.registry.ModTileEntities;
import earth.terrarium.adastra.common.registry.ModSounds;
import earth.terrarium.adastra.common.tile.SlidingDoorTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class AdAstraSlidingDoorBlock extends AdAstraModelBlock implements ITileEntityProvider {

    public static final PropertyDirection FACING = BlockHorizontal.FACING;
    public static final PropertyBool LOCKED = PropertyBool.create("locked");
    public static final PropertyBool OPEN = PropertyBool.create("open");
    public static final PropertyEnum<AdAstraSlidingDoorPart> PART = PropertyEnum.create("part", AdAstraSlidingDoorPart.class);
    public static final PropertyBool POWERED = PropertyBool.create("powered");

    private static final AxisAlignedBB NORTH_SHAPE = new AxisAlignedBB(0.0D, 0.0D, 1.0D / 16.0D, 1.0D, 1.0D, 4.0D / 16.0D);
    private static final AxisAlignedBB EAST_SHAPE = new AxisAlignedBB(12.0D / 16.0D, 0.0D, 0.0D, 15.0D / 16.0D, 1.0D, 1.0D);
    private static final AxisAlignedBB SOUTH_SHAPE = new AxisAlignedBB(0.0D, 0.0D, 12.0D / 16.0D, 1.0D, 1.0D, 15.0D / 16.0D);
    private static final AxisAlignedBB WEST_SHAPE = new AxisAlignedBB(1.0D / 16.0D, 0.0D, 0.0D, 4.0D / 16.0D, 1.0D, 1.0D);

    private boolean destroyingDoor;

    public AdAstraSlidingDoorBlock(float hardness, float resistance) {
        super(Material.IRON, hardness, resistance);
        setSoundType(SoundType.METAL);
        setHarvestLevel("pickaxe", 1);
        setDefaultState(blockState.getBaseState()
            .withProperty(FACING, EnumFacing.NORTH)
            .withProperty(LOCKED, false)
            .withProperty(OPEN, false)
            .withProperty(PART, AdAstraSlidingDoorPart.BOTTOM)
            .withProperty(POWERED, false));
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        EnumFacing doorFacing = placer.getHorizontalFacing().getOpposite();
        boolean powered = isStructurePowered(world, pos, doorFacing);
        return getDefaultState()
            .withProperty(FACING, doorFacing)
            .withProperty(OPEN, powered)
            .withProperty(POWERED, powered);
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        EnumFacing doorFacing = placer.getHorizontalFacing().getOpposite();
        if (!canPlaceDoor(world, pos, doorFacing, true)) {
            boolean drop = !(placer instanceof EntityPlayer) || !((EntityPlayer) placer).capabilities.isCreativeMode;
            world.destroyBlock(pos, drop);
            return;
        }

        boolean powered = isStructurePowered(world, pos, doorFacing);
        IBlockState baseState = state
            .withProperty(FACING, doorFacing)
            .withProperty(LOCKED, false)
            .withProperty(OPEN, powered)
            .withProperty(POWERED, powered);

        for (AdAstraSlidingDoorPart part : AdAstraSlidingDoorPart.values()) {
            BlockPos partPos = getPartPos(pos, doorFacing, part);
            world.setBlockState(partPos, baseState.withProperty(PART, part), 3);
            configureTile(world, partPos, part, false, powered, powered, true);
        }
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        BlockPos controllerPos = getControllerPos(world, pos, state);
        if (controllerPos == null) {
            return false;
        }

        IBlockState controllerState = world.getBlockState(controllerPos);
        if (!(controllerState.getBlock() instanceof AdAstraSlidingDoorBlock)) {
            return false;
        }

        SlidingDoorTileEntity controller = getSlidingDoorTile(world, controllerPos);
        boolean locked = getLocked(controllerState, controller);
        boolean open = getOpen(controllerState, controller);
        boolean powered = getPowered(controllerState, controller);
        ItemStack held = player.getHeldItem(hand);

        if (!held.isEmpty() && held.getItem() instanceof AdAstraWrenchItem && player.isSneaking()) {
            if (!world.isRemote) {
                setDoorState(world, controllerPos, controllerState, !locked, open, powered);
                world.playSound(null, controllerPos, ModSounds.WRENCH, SoundCategory.BLOCKS, 1.0f, world.rand.nextFloat() * 0.2f + 0.9f);
                player.sendStatusMessage(new TextComponentString(!locked ? "Sliding door locked" : "Sliding door unlocked"), true);
            }
            return true;
        }

        if (locked) {
            return true;
        }

        if (!world.isRemote) {
            setDoorState(world, controllerPos, controllerState, locked, !open, powered);
        }
        return true;
    }

    @Override
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos fromPos) {
        if (world.isRemote) {
            return;
        }

        BlockPos controllerPos = getControllerPos(world, pos, state);
        if (controllerPos == null) {
            return;
        }

        IBlockState controllerState = world.getBlockState(controllerPos);
        if (!(controllerState.getBlock() instanceof AdAstraSlidingDoorBlock)) {
            return;
        }

        SlidingDoorTileEntity controller = getSlidingDoorTile(world, controllerPos);
        boolean powered = isStructurePowered(world, controllerPos, getFacing(controllerState));
        if (getPowered(controllerState, controller) != powered) {
            setDoorState(world, controllerPos, controllerState, getLocked(controllerState, controller), getOpen(controllerState, controller), powered);
        }
    }

    @Override
    public boolean canPlaceBlockAt(World world, BlockPos pos) {
        return canPlaceDoor(world, pos, EnumFacing.NORTH, false)
            || canPlaceDoor(world, pos, EnumFacing.EAST, false)
            || canPlaceDoor(world, pos, EnumFacing.SOUTH, false)
            || canPlaceDoor(world, pos, EnumFacing.WEST, false);
    }

    @Override
    public boolean canPlaceBlockOnSide(World world, BlockPos pos, EnumFacing side) {
        return canPlaceBlockAt(world, pos);
    }

    @Override
    public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
        if (!destroyingDoor && !world.isRemote) {
            if (!player.capabilities.isCreativeMode && willHarvest) {
                dropDoorItem(world, pos, state);
            }
            destroyDoor(world, pos, state, false);
            return true;
        }
        return super.removedByPlayer(state, world, pos, player, willHarvest);
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        if (!destroyingDoor && !world.isRemote) {
            destroyDoor(world, pos, state, false);
        }
        super.breakBlock(world, pos, state);
    }

    @Override
    public void harvestBlock(World world, EntityPlayer player, BlockPos pos, IBlockState state, TileEntity tile, ItemStack stack) {
        // Drops are handled in removedByPlayer so breaking any 3x3 part yields exactly one door item.
    }

    @Override
    public void dropBlockAsItemWithChance(World world, BlockPos pos, IBlockState state, float chance, int fortune) {
        TileEntity tile = world.getTileEntity(pos);
        if (getPart(state, tile).isController()) {
            super.dropBlockAsItemWithChance(world, pos, state, chance, fortune);
        }
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
        SlidingDoorTileEntity tile = getSlidingDoorTile(world, pos);
        if (tile == null) {
            return state;
        }
        return state
            .withProperty(PART, tile.getPart())
            .withProperty(LOCKED, tile.isLocked())
            .withProperty(OPEN, tile.isOpen())
            .withProperty(POWERED, tile.isPowered());
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
        switch (state.getValue(FACING)) {
            case EAST:
                return EAST_SHAPE;
            case SOUTH:
                return SOUTH_SHAPE;
            case WEST:
                return WEST_SHAPE;
            case NORTH:
            default:
                return NORTH_SHAPE;
        }
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
        BlockPos controllerPos = getControllerPos(world, pos, state);
        if (controllerPos != null) {
            SlidingDoorTileEntity controller = getSlidingDoorTile(world, controllerPos);
            if (controller != null && controller.isPassable()) {
                return NULL_AABB;
            }
            IBlockState controllerState = world.getBlockState(controllerPos);
            if (controllerState.getBlock() instanceof AdAstraSlidingDoorBlock
                && (controllerState.getValue(OPEN) || controllerState.getValue(POWERED))) {
                return NULL_AABB;
            }
        }
        return getBoundingBox(state, world, pos);
    }

    @Override
    public boolean isPassable(IBlockAccess world, BlockPos pos) {
        IBlockState state = world.getBlockState(pos);
        BlockPos controllerPos = getControllerPos(world, pos, state);
        if (controllerPos == null) {
            return false;
        }
        SlidingDoorTileEntity controller = getSlidingDoorTile(world, controllerPos);
        if (controller != null) {
            return controller.isPassable();
        }
        IBlockState controllerState = world.getBlockState(controllerPos);
        return controllerState.getBlock() instanceof AdAstraSlidingDoorBlock
            && (controllerState.getValue(OPEN) || controllerState.getValue(POWERED));
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState()
            .withProperty(FACING, EnumFacing.byHorizontalIndex(meta & 3))
            .withProperty(OPEN, (meta & 4) != 0)
            .withProperty(POWERED, (meta & 8) != 0);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        int meta = state.getValue(FACING).getHorizontalIndex();
        if (state.getValue(OPEN)) {
            meta |= 4;
        }
        if (state.getValue(POWERED)) {
            meta |= 8;
        }
        return meta;
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
        return new BlockStateContainer(this, FACING, LOCKED, OPEN, PART, POWERED);
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return ModTileEntities.createForBlock(this);
    }

    private boolean canPlaceDoor(World world, BlockPos controllerPos, EnumFacing facing, boolean allowExistingController) {
        for (AdAstraSlidingDoorPart part : AdAstraSlidingDoorPart.values()) {
            BlockPos partPos = getPartPos(controllerPos, facing, part);
            if (allowExistingController && partPos.equals(controllerPos) && world.getBlockState(partPos).getBlock() == this) {
                continue;
            }
            if (!world.isAirBlock(partPos) && !world.getBlockState(partPos).getBlock().isReplaceable(world, partPos)) {
                return false;
            }
        }
        return true;
    }

    private void setDoorState(World world, BlockPos controllerPos, IBlockState controllerState, boolean locked, boolean open, boolean powered) {
        EnumFacing facing = getFacing(controllerState);
        for (AdAstraSlidingDoorPart part : AdAstraSlidingDoorPart.values()) {
            BlockPos partPos = getPartPos(controllerPos, facing, part);
            IBlockState partState = world.getBlockState(partPos);
            if (!(partState.getBlock() instanceof AdAstraSlidingDoorBlock)) {
                continue;
            }

            IBlockState newState = partState
                .withProperty(FACING, facing)
                .withProperty(PART, part)
                .withProperty(LOCKED, locked)
                .withProperty(OPEN, open)
                .withProperty(POWERED, powered);
            world.setBlockState(partPos, newState, 3);
            configureTile(world, partPos, part, locked, open, powered);
        }
    }

    private void configureTile(World world, BlockPos pos, AdAstraSlidingDoorPart part, boolean locked, boolean open, boolean powered) {
        configureTile(world, pos, part, locked, open, powered, false);
    }

    private void configureTile(World world, BlockPos pos, AdAstraSlidingDoorPart part, boolean locked, boolean open, boolean powered, boolean resetSlide) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof SlidingDoorTileEntity) {
            ((SlidingDoorTileEntity) tile).configure(part, locked, open, powered, resetSlide);
        }
    }

    private void destroyDoor(World world, BlockPos pos, IBlockState state, boolean dropController) {
        BlockPos controllerPos = getControllerPos(world, pos, state);
        if (controllerPos == null) {
            controllerPos = pos;
        }

        IBlockState controllerState = world.getBlockState(controllerPos);
        if (!(controllerState.getBlock() instanceof AdAstraSlidingDoorBlock)) {
            destroyNearbyDoorParts(world, pos, state.getBlock());
            return;
        }

        EnumFacing facing = getFacing(controllerState);
        destroyingDoor = true;
        try {
            for (AdAstraSlidingDoorPart part : AdAstraSlidingDoorPart.values()) {
                BlockPos partPos = getPartPos(controllerPos, facing, part);
                IBlockState partState = world.getBlockState(partPos);
                if (partState.getBlock() instanceof AdAstraSlidingDoorBlock) {
                    world.destroyBlock(partPos, dropController && part.isController());
                }
            }
        } finally {
            destroyingDoor = false;
        }
    }

    private void dropDoorItem(World world, BlockPos pos, IBlockState state) {
        IBlockState dropState = state;
        BlockPos controllerPos = getControllerPos(world, pos, state);
        if (controllerPos != null) {
            IBlockState controllerState = world.getBlockState(controllerPos);
            if (controllerState.getBlock() instanceof AdAstraSlidingDoorBlock) {
                dropState = controllerState;
            }
        }
        super.dropBlockAsItemWithChance(world, pos, dropState, 1.0f, 0);
    }

    private void destroyNearbyDoorParts(World world, BlockPos origin, Block block) {
        destroyingDoor = true;
        try {
            for (int x = -2; x <= 2; x++) {
                for (int y = -2; y <= 2; y++) {
                    for (int z = -2; z <= 2; z++) {
                        BlockPos checkPos = origin.add(x, y, z);
                        if (world.getBlockState(checkPos).getBlock() == block) {
                            world.destroyBlock(checkPos, false);
                        }
                    }
                }
            }
        } finally {
            destroyingDoor = false;
        }
    }

    private boolean isStructurePowered(World world, BlockPos controllerPos, EnumFacing facing) {
        for (AdAstraSlidingDoorPart part : AdAstraSlidingDoorPart.values()) {
            if (world.isBlockPowered(getPartPos(controllerPos, facing, part))) {
                return true;
            }
        }
        return false;
    }

    private BlockPos getControllerPos(IBlockAccess world, BlockPos pos, IBlockState state) {
        TileEntity tile = world.getTileEntity(pos);
        AdAstraSlidingDoorPart part = getPart(state, tile);
        EnumFacing direction = getFacing(state).rotateY();
        return pos.offset(direction.getOpposite(), part.getXOffset()).down(part.getYOffset());
    }

    private BlockPos getPartPos(BlockPos controllerPos, EnumFacing facing, AdAstraSlidingDoorPart part) {
        EnumFacing direction = facing.rotateY();
        return controllerPos.offset(direction, part.getXOffset()).up(part.getYOffset());
    }

    private AdAstraSlidingDoorPart getPart(IBlockState state, TileEntity tile) {
        if (tile instanceof SlidingDoorTileEntity) {
            return ((SlidingDoorTileEntity) tile).getPart();
        }
        return state.getValue(PART);
    }

    private EnumFacing getFacing(IBlockState state) {
        return state.getValue(FACING);
    }

    private boolean getLocked(IBlockState state, SlidingDoorTileEntity tile) {
        return tile != null ? tile.isLocked() : state.getValue(LOCKED);
    }

    private boolean getOpen(IBlockState state, SlidingDoorTileEntity tile) {
        return tile != null ? tile.isOpen() : state.getValue(OPEN);
    }

    private boolean getPowered(IBlockState state, SlidingDoorTileEntity tile) {
        return tile != null ? tile.isPowered() : state.getValue(POWERED);
    }

    private SlidingDoorTileEntity getSlidingDoorTile(IBlockAccess world, BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);
        return tile instanceof SlidingDoorTileEntity ? (SlidingDoorTileEntity) tile : null;
    }
}
