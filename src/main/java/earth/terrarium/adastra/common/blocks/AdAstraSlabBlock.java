package earth.terrarium.adastra.common.blocks;

import earth.terrarium.adastra.common.AdAstraCreativeTab;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;

import java.util.Random;

public abstract class AdAstraSlabBlock extends BlockSlab {

    public static final PropertyEnum<SlabVariant> VARIANT = PropertyEnum.create("variant", SlabVariant.class);

    private final BlockSlab singleSlab;

    protected AdAstraSlabBlock(Material material, BlockSlab singleSlab, float hardness, float resistance, SoundType soundType) {
        super(material);
        this.singleSlab = singleSlab == null ? this : singleSlab;
        setHardness(hardness);
        setResistance(resistance);
        setSoundType(soundType);
        if (!isDouble()) {
            setCreativeTab(AdAstraCreativeTab.INSTANCE);
        }
        IBlockState state = blockState.getBaseState().withProperty(VARIANT, SlabVariant.DEFAULT);
        if (!isDouble()) {
            state = state.withProperty(HALF, EnumBlockHalf.BOTTOM);
        }
        setDefaultState(state);
    }

    @Override
    public String getTranslationKey(int meta) {
        return super.getTranslationKey();
    }

    @Override
    public IProperty<?> getVariantProperty() {
        return VARIANT;
    }

    @Override
    public Comparable<?> getTypeForItem(ItemStack stack) {
        return SlabVariant.DEFAULT;
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return Item.getItemFromBlock(singleSlab);
    }

    @Override
    public ItemStack getItem(net.minecraft.world.World world, net.minecraft.util.math.BlockPos pos, IBlockState state) {
        return new ItemStack(singleSlab);
    }

    @Override
    public int quantityDropped(Random random) {
        return isDouble() ? 2 : 1;
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        IBlockState state = getDefaultState();
        if (!isDouble()) {
            state = state.withProperty(HALF, (meta & 8) == 0 ? EnumBlockHalf.BOTTOM : EnumBlockHalf.TOP);
        }
        return state;
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        if (isDouble()) {
            return 0;
        }
        return state.getValue(HALF) == EnumBlockHalf.TOP ? 8 : 0;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return isDouble() ? new BlockStateContainer(this, VARIANT) : new BlockStateContainer(this, VARIANT, HALF);
    }

    public BlockSlab getSingleSlab() {
        return singleSlab;
    }

    public enum SlabVariant implements IStringSerializable {
        DEFAULT;

        @Override
        public String getName() {
            return "default";
        }
    }

    public static class Single extends AdAstraSlabBlock {

        public Single(Material material, float hardness, float resistance, SoundType soundType) {
            super(material, null, hardness, resistance, soundType);
        }

        @Override
        public boolean isDouble() {
            return false;
        }
    }

    public static class Double extends AdAstraSlabBlock {

        public Double(Material material, BlockSlab singleSlab, float hardness, float resistance, SoundType soundType) {
            super(material, singleSlab, hardness, resistance, soundType);
        }

        @Override
        public boolean isDouble() {
            return true;
        }
    }
}
