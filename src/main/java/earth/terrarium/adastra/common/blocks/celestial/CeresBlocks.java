package earth.terrarium.adastra.common.blocks.celestial;

import earth.terrarium.adastra.common.registry.ModItems;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

public class CeresBlocks extends Block {

    public static final PropertyEnum<EnumCeresBlocks> TYPE = PropertyEnum.create("type", EnumCeresBlocks.class);

    public CeresBlocks() {
        super(Material.ROCK);
        this.setSoundType(SoundType.STONE);
        this.setHarvestLevel("pickaxe", 2);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
        for (EnumCeresBlocks type : EnumCeresBlocks.values()) {
            list.add(new ItemStack(this, 1, type.getMeta()));
        }
    }

    @Override
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
        return new ItemStack(Item.getItemFromBlock(this), 1, this.getMetaFromState(state));
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        switch (getMetaFromState(state)) {
            case 2: return ModItems.DOLOMITE_CRYSTAL;
            case 3: return ModItems.METEORIC_IRON_FRAGMENTS;
            default: return Item.getItemFromBlock(this);
        }
    }

    @Override
    public int damageDropped(IBlockState state) {
        switch (getMetaFromState(state)) {
            case 2: return 0;
            case 3: return 0;
            default: return getMetaFromState(state);
        }
    }

    @Override
    public int quantityDropped(IBlockState state, int fortune, Random random) {
        if (this.getMetaFromState(state) == 3) {
            return 1 + random.nextInt(2);
        }
        return super.quantityDropped(state, fortune, random);
    }

    public enum EnumCeresBlocks implements IStringSerializable {
        CERES_GRUNT(0, "ceres_grunt"),
        CERES_SUBGRUNT(1, "ceres_subgrunt"),
        CERES_DOLOMITE_ORE(2, "ceres_dolomite_ore"),
        CERES_METEORICIRON_ORE(3, "ceres_meteoriciron_ore"),
        CERES_DUNGEON_TOP(4, "ceres_dungeon_top"),
        CERES_DUNGEON_FLOOR(5, "ceres_dungeon_floor");

        private final int meta;
        private final String name;
        private static final EnumCeresBlocks[] VALUES = values();

        EnumCeresBlocks(int meta, String name) { this.meta = meta; this.name = name; }
        public int getMeta() { return this.meta; }
        @Override
        public String getName() { return this.name; }
        public static EnumCeresBlocks byMetadata(int meta) { return VALUES[meta % VALUES.length]; }
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(TYPE, EnumCeresBlocks.byMetadata(meta));
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(TYPE).getMeta();
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, TYPE);
    }
}
