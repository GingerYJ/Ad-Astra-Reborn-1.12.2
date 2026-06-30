package earth.terrarium.adastra.common.blocks;

import earth.terrarium.adastra.common.registry.ModTileEntities;
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
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class AdAstraAttachedMachineBlock extends AdAstraModelBlock implements ITileEntityProvider {

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
    public TileEntity createNewTileEntity(World world, int meta) {
        return ModTileEntities.createForBlock(this);
    }
}
