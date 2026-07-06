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

public class ProximaBBlocks extends Block {
    public static final PropertyEnum<EnumProximaBBlocks> TYPE = PropertyEnum.create("type", EnumProximaBBlocks.class);

    public ProximaBBlocks() {
        super(Material.ROCK);
        this.setSoundType(SoundType.STONE);
        this.setHarvestLevel("pickaxe", 2);
    }

    @Override @SideOnly(Side.CLIENT)
    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
        for (EnumProximaBBlocks t : EnumProximaBBlocks.values()) list.add(new ItemStack(this, 1, t.getMeta()));
    }

    @Override public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
        return new ItemStack(Item.getItemFromBlock(this), 1, getMetaFromState(state));
    }

    public enum EnumProximaBBlocks implements IStringSerializable {
        PROXIMA_B_GRUNT(0, "proxima_b_grunt"), PROXIMA_B_SUBGRUNT(1, "proxima_b_subgrunt"),
        PROXIMA_B_STONE(2, "proxima_b_stone"), PROXIMA_B_GRASS(3, "proxima_b_grass"),
        PROXIMA_B_DIRT(4, "proxima_b_dirt"), PROXIMA_B_LOG(5, "proxima_b_log"),
        PROXIMA_B_LEAVES(6, "proxima_b_leaves"), PROXIMA_B_COBBLESTONE(7, "proxima_b_cobblestone");
        private final int meta; private final String name;
        private static final EnumProximaBBlocks[] VALUES = values();
        EnumProximaBBlocks(int meta, String name) { this.meta = meta; this.name = name; }
        public int getMeta() { return meta; } @Override public String getName() { return name; }
        public static EnumProximaBBlocks byMetadata(int meta) { return VALUES[meta % VALUES.length]; }
    }

    @Override public int damageDropped(IBlockState state) { return getMetaFromState(state); }

    @Override public IBlockState getStateFromMeta(int meta) { return getDefaultState().withProperty(TYPE, EnumProximaBBlocks.byMetadata(meta)); }
    @Override public int getMetaFromState(IBlockState state) { return state.getValue(TYPE).getMeta(); }
    @Override protected BlockStateContainer createBlockState() { return new BlockStateContainer(this, TYPE); }
}
