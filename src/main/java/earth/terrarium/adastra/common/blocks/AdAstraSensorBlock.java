package earth.terrarium.adastra.common.blocks;

import earth.terrarium.adastra.common.items.AdAstraWrenchItem;
import earth.terrarium.adastra.common.registry.ModTileEntities;
import earth.terrarium.adastra.common.registry.ModSounds;
import earth.terrarium.adastra.common.tile.DetectorTileEntity;
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
import net.minecraft.item.ItemStack;
import net.minecraft.world.IBlockAccess;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

public class AdAstraSensorBlock extends AdAstraModelBlock implements ITileEntityProvider {

    public static final PropertyEnum<DetectionType> DETECTION_TYPE = PropertyEnum.create("detection_type", DetectionType.class);
    public static final PropertyDirection FACING = BlockHorizontal.FACING;
    public static final PropertyBool INVERTED = PropertyBool.create("inverted");
    public static final PropertyBool LIT = PropertyBool.create("lit");
    public static final PropertyBool POWERED = PropertyBool.create("powered");

    public AdAstraSensorBlock(Material material, float hardness, float resistance) {
        super(material, hardness, resistance);
        setSoundType(SoundType.METAL);
        setHarvestLevel("pickaxe", 1);
        setDefaultState(blockState.getBaseState()
            .withProperty(DETECTION_TYPE, DetectionType.OXYGEN)
            .withProperty(FACING, EnumFacing.NORTH)
            .withProperty(INVERTED, false)
            .withProperty(LIT, false)
            .withProperty(POWERED, false));
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        return getDefaultState()
            .withProperty(FACING, placer.getHorizontalFacing().getOpposite())
            .withProperty(DETECTION_TYPE, DetectionType.byMetadata((meta >> 2) & 3));
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        world.setBlockState(pos, state.withProperty(FACING, placer.getHorizontalFacing().getOpposite()), 2);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState()
            .withProperty(FACING, EnumFacing.byHorizontalIndex(meta & 3))
            .withProperty(DETECTION_TYPE, DetectionType.byMetadata((meta >> 2) & 3));
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(FACING).getHorizontalIndex() | (state.getValue(DETECTION_TYPE).getMetadata() << 2);
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
        return new BlockStateContainer(this, DETECTION_TYPE, FACING, INVERTED, LIT, POWERED);
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        ItemStack held = player.getHeldItem(hand);
        if (held.isEmpty() || !(held.getItem() instanceof AdAstraWrenchItem)) {
            return false;
        }

        if (!world.isRemote) {
            IBlockState newState;
            TextComponentTranslation message;
            if (player.isSneaking()) {
                newState = state.withProperty(INVERTED, !state.getValue(INVERTED));
                message = new TextComponentTranslation(newState.getValue(INVERTED)
                    ? "text.ad_astra.detector.inverted_true"
                    : "text.ad_astra.detector.inverted_false");
            } else {
                DetectionType newType = state.getValue(DETECTION_TYPE).next();
                newState = state.withProperty(DETECTION_TYPE, newType);
                message = new TextComponentTranslation(newType.getMessageKey());
            }

            world.setBlockState(pos, newState, 3);
            TileEntity tile = world.getTileEntity(pos);
            if (tile instanceof DetectorTileEntity) {
                DetectorTileEntity detector = (DetectorTileEntity) tile;
                detector.setInverted(newState.getValue(INVERTED));
                detector.requestImmediateScan();
            }
            world.playSound(null, pos, ModSounds.WRENCH, SoundCategory.BLOCKS, 1.0f, world.rand.nextFloat() * 0.2f + 0.9f);
            player.sendStatusMessage(message, true);
        }
        return true;
    }

    @Override
    public boolean canProvidePower(IBlockState state) {
        return true;
    }

    @Override
    public int getWeakPower(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        return state.getValue(POWERED) ? 15 : 0;
    }

    @Override
    public int getStrongPower(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        return state.getValue(POWERED) ? 15 : 0;
    }

    public enum DetectionType implements IStringSerializable {
        OXYGEN(0, "oxygen"),
        TEMPERATURE(1, "temperature"),
        GRAVITY(2, "gravity");

        private final int metadata;
        private final String name;

        DetectionType(int metadata, String name) {
            this.metadata = metadata;
            this.name = name;
        }

        public int getMetadata() {
            return metadata;
        }

        public DetectionType next() {
            switch (this) {
                case OXYGEN:
                    return GRAVITY;
                case GRAVITY:
                    return TEMPERATURE;
                case TEMPERATURE:
                default:
                    return OXYGEN;
            }
        }

        public String getMessageKey() {
            return "text.ad_astra.detector." + name + "_mode";
        }

        @Override
        public String getName() {
            return name;
        }

        public static DetectionType byMetadata(int metadata) {
            for (DetectionType type : values()) {
                if (type.metadata == metadata) {
                    return type;
                }
            }
            return OXYGEN;
        }
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return ModTileEntities.createForBlock(this);
    }
}
