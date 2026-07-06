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

public class EuropaBlocks extends Block {
    public static final PropertyEnum<EnumEuropaBlocks> TYPE = PropertyEnum.create("type", EnumEuropaBlocks.class);

    public EuropaBlocks() {
        super(Material.ROCK);
        this.setSoundType(SoundType.STONE);
        this.setHarvestLevel("pickaxe", 2);
    }

    @Override @SideOnly(Side.CLIENT)
    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
        for (EnumEuropaBlocks t : EnumEuropaBlocks.values()) list.add(new ItemStack(this, 1, t.getMeta()));
    }

    @Override public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
        return new ItemStack(Item.getItemFromBlock(this), 1, getMetaFromState(state));
    }

    @Override public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        switch (getMetaFromState(state)) {
            case 5: return ModItems.RAW_SILICON;
            case 6: return Item.getItemFromBlock(this);
            default: return Item.getItemFromBlock(this);
        }
    }

    @Override public int damageDropped(IBlockState state) { return getMetaFromState(state) == 5 ? 0 : getMetaFromState(state); }

    public enum EnumEuropaBlocks implements IStringSerializable {
        EUROPA_GRUNT(0, "europa_grunt"), EUROPA_SUBGRUNT(1, "europa_subgrunt"),
        EUROPA_STONE(2, "europa_stone"), EUROPA_ICE(3, "europa_ice"),
        EUROPA_FROZEN_CRUST(4, "europa_frozen_crust"), EUROPA_SILICON_ORE(5, "europa_silicon_ore"),
        EUROPA_IRON_ORE(6, "europa_iron_ore");
        private final int meta; private final String name;
        private static final EnumEuropaBlocks[] VALUES = values();
        EnumEuropaBlocks(int meta, String name) { this.meta = meta; this.name = name; }
        public int getMeta() { return meta; } @Override public String getName() { return name; }
        public static EnumEuropaBlocks byMetadata(int meta) { return VALUES[meta % VALUES.length]; }
    }

    @Override public IBlockState getStateFromMeta(int meta) { return getDefaultState().withProperty(TYPE, EnumEuropaBlocks.byMetadata(meta)); }
    @Override public int getMetaFromState(IBlockState state) { return state.getValue(TYPE).getMeta(); }
    @Override protected BlockStateContainer createBlockState() { return new BlockStateContainer(this, TYPE); }
}
