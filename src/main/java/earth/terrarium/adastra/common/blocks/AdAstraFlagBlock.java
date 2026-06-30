package earth.terrarium.adastra.common.blocks;

import earth.terrarium.adastra.common.registry.ModTileEntities;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Locale;

public class AdAstraFlagBlock extends AdAstraModelBlock implements ITileEntityProvider {

    public static final PropertyEnum<AdAstraEightDirection> FACING = PropertyEnum.create("facing", AdAstraEightDirection.class);
    public static final PropertyEnum<Half> HALF = PropertyEnum.create("half", Half.class);
    public static final PropertyBool WATERLOGGED = PropertyBool.create("waterlogged");

    public AdAstraFlagBlock() {
        super(Material.WOOD, 1.0f, 1.0f);
        setSoundType(SoundType.WOOD);
        setDefaultState(blockState.getBaseState()
            .withProperty(FACING, AdAstraEightDirection.NORTH)
            .withProperty(HALF, Half.LOWER)
            .withProperty(WATERLOGGED, false));
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState()
            .withProperty(FACING, AdAstraEightDirection.VALUES[meta & 7])
            .withProperty(HALF, (meta & 8) == 0 ? Half.LOWER : Half.UPPER)
            .withProperty(WATERLOGGED, false);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        int meta = state.getValue(FACING).ordinal();
        if (state.getValue(HALF) == Half.UPPER) {
            meta |= 8;
        }
        return meta;
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        return getDefaultState().withProperty(FACING, AdAstraEightDirection.fromYaw(placer.rotationYaw));
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        if (world.isAirBlock(pos.up())) {
            world.setBlockState(pos, state.withProperty(HALF, Half.LOWER), 2);
            world.setBlockState(pos.up(), state.withProperty(HALF, Half.UPPER), 2);
        }
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        BlockPos otherPos = state.getValue(HALF) == Half.LOWER ? pos.up() : pos.down();
        IBlockState otherState = world.getBlockState(otherPos);
        if (otherState.getBlock() == this) {
            world.setBlockState(otherPos, net.minecraft.init.Blocks.AIR.getDefaultState(), 2);
        }
        super.breakBlock(world, pos, state);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING, HALF, WATERLOGGED);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return ModTileEntities.createForBlock(this);
    }

    public enum Half implements IStringSerializable {
        LOWER,
        UPPER;

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
