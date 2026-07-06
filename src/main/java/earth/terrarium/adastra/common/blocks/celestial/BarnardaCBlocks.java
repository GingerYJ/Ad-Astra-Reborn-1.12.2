package earth.terrarium.adastra.common.blocks.celestial;

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

public class BarnardaCBlocks extends Block {
    public static final PropertyEnum<EnumBarnardaCBlocks> TYPE = PropertyEnum.create("type", EnumBarnardaCBlocks.class);

    public BarnardaCBlocks() {
        super(Material.ROCK);
        this.setSoundType(SoundType.STONE);
        this.setHarvestLevel("pickaxe", 2);
    }

    @Override @SideOnly(Side.CLIENT)
    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
        for (EnumBarnardaCBlocks t : EnumBarnardaCBlocks.values()) list.add(new ItemStack(this, 1, t.getMeta()));
    }

    @Override public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
        return new ItemStack(Item.getItemFromBlock(this), 1, getMetaFromState(state));
    }

    public enum EnumBarnardaCBlocks implements IStringSerializable {
        BARNARDA_C_GRUNT(0, "barnarda_c_grunt"), BARNARDA_C_SUBGRUNT(1, "barnarda_c_subgrunt"),
        BARNARDA_C_STONE(2, "barnarda_c_stone"), BARNARDA_C_DIRT(3, "barnarda_c_dirt"),
        BARNARDA_C_GRASS(4, "barnarda_c_grass"), BARNARDA_C_SAND(5, "barnarda_c_sand"),
        BARNARDA_C_COBBLESTONE(6, "barnarda_c_cobblestone"), BARNARDA_C_STONE_BRICKS(7, "barnarda_c_stone_bricks"),
        BARNARDA_C_WOOD_PLANKS(8, "barnarda_c_wood_planks"), BARNARDA_C_LOG(9, "barnarda_c_log"),
        BARNARDA_C_LEAVES(10, "barnarda_c_leaves"), BARNARDA_C_IRON_ORE(11, "barnarda_c_iron_ore"),
        BARNARDA_C_GOLD_ORE(12, "barnarda_c_gold_ore"), BARNARDA_C_COAL_ORE(13, "barnarda_c_coal_ore");
        private final int meta; private final String name;
        private static final EnumBarnardaCBlocks[] VALUES = values();
        EnumBarnardaCBlocks(int meta, String name) { this.meta = meta; this.name = name; }
        public int getMeta() { return meta; } @Override public String getName() { return name; }
        public static EnumBarnardaCBlocks byMetadata(int meta) { return VALUES[meta % VALUES.length]; }
    }

    @Override public int damageDropped(IBlockState state) { return getMetaFromState(state); }

    @Override public IBlockState getStateFromMeta(int meta) { return getDefaultState().withProperty(TYPE, EnumBarnardaCBlocks.byMetadata(meta)); }
    @Override public int getMetaFromState(IBlockState state) { return state.getValue(TYPE).getMeta(); }
    @Override protected BlockStateContainer createBlockState() { return new BlockStateContainer(this, TYPE); }
}
