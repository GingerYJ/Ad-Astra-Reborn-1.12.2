package earth.terrarium.adastra.common.blocks;

import earth.terrarium.adastra.common.registry.ModTileEntities;
import earth.terrarium.adastra.common.tile.RadioTileEntity;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

public class AdAstraRadioBlock extends AdAstraModelBlock implements ITileEntityProvider {

    public static final PropertyEnum<AdAstraEightDirection> FACING = PropertyEnum.create("facing", AdAstraEightDirection.class);

    public AdAstraRadioBlock() {
        super(Material.WOOD, 1.0f, 1.0f);
        setSoundType(SoundType.WOOD);
        setDefaultState(blockState.getBaseState().withProperty(FACING, AdAstraEightDirection.NORTH));
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(FACING, AdAstraEightDirection.VALUES[meta & 7]);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(FACING).ordinal();
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, net.minecraft.util.EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        return getDefaultState().withProperty(FACING, AdAstraEightDirection.fromYaw(placer.rotationYaw));
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        world.setBlockState(pos, state.withProperty(FACING, AdAstraEightDirection.fromYaw(placer.rotationYaw)), 2);
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        TileEntity tile = world.getTileEntity(pos);
        if (!(tile instanceof RadioTileEntity)) {
            return false;
        }

        RadioTileEntity radio = (RadioTileEntity) tile;
        if (!world.isRemote) {
            if (player.isSneaking()) {
                radio.clearStation();
                player.sendStatusMessage(new TextComponentString("Radio station cleared"), true);
            } else if (radio.hasStation()) {
                boolean playing = radio.togglePlaying();
                player.sendStatusMessage(new TextComponentString(playing ? "Radio on: " + radio.getStation() : "Radio off"), true);
            } else {
                radio.stop();
                player.sendStatusMessage(new TextComponentString("Radio station not set"), true);
            }
        }
        return true;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return ModTileEntities.createForBlock(this);
    }
}
