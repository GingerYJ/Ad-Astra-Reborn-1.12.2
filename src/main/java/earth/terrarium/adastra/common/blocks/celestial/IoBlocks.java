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

public class IoBlocks extends Block {
    public static final PropertyEnum<EnumIoBlocks> TYPE = PropertyEnum.create("type", EnumIoBlocks.class);

    public IoBlocks() {
        super(Material.ROCK);
        this.setSoundType(SoundType.STONE);
        this.setHarvestLevel("pickaxe", 2);
    }

    @Override @SideOnly(Side.CLIENT)
    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
        for (EnumIoBlocks t : EnumIoBlocks.values()) list.add(new ItemStack(this, 1, t.getMeta()));
    }

    @Override public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
        return new ItemStack(Item.getItemFromBlock(this), 1, getMetaFromState(state));
    }

    @Override public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        switch (getMetaFromState(state)) {
            case 8: return ModItems.SULFUR_DUST;
            case 9: return ModItems.VOLCANIC_SHARD;
            default: return Item.getItemFromBlock(this);
        }
    }

    @Override public int damageDropped(IBlockState state) { return getMetaFromState(state) >= 8 ? 0 : getMetaFromState(state); }

    public enum EnumIoBlocks implements IStringSerializable {
        IO_GRUNT(0, "io_grunt"), IO_SUBGRUNT(1, "io_subgrunt"), IO_STONE(2, "io_stone"),
        IO_VOLCANIC_ASH(3, "io_volcanic_ash"), IO_SULFUR_SURFACE(4, "io_sulfur_surface"),
        IO_MAGMA_CRUST(5, "io_magma_crust"), IO_COBBLESTONE(6, "io_cobblestone"),
        IO_STONE_BRICKS(7, "io_stone_bricks"), IO_SULFUR_ORE(8, "io_sulfur_ore"),
        IO_VOLCANIC_ORE(9, "io_volcanic_ore"), IO_BASALT(10, "io_basalt");
        private final int meta; private final String name;
        private static final EnumIoBlocks[] VALUES = values();
        EnumIoBlocks(int meta, String name) { this.meta = meta; this.name = name; }
        public int getMeta() { return meta; } @Override public String getName() { return name; }
        public static EnumIoBlocks byMetadata(int meta) { return VALUES[meta % VALUES.length]; }
    }

    @Override public IBlockState getStateFromMeta(int meta) { return getDefaultState().withProperty(TYPE, EnumIoBlocks.byMetadata(meta)); }
    @Override public int getMetaFromState(IBlockState state) { return state.getValue(TYPE).getMeta(); }
    @Override protected BlockStateContainer createBlockState() { return new BlockStateContainer(this, TYPE); }
}
