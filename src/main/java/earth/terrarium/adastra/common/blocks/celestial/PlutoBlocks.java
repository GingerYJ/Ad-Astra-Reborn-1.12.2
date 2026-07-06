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

public class PlutoBlocks extends Block {
    public static final PropertyEnum<EnumPlutoBlocks> TYPE = PropertyEnum.create("type", EnumPlutoBlocks.class);

    public PlutoBlocks() {
        super(Material.ROCK);
        this.setSoundType(SoundType.STONE);
        this.setHarvestLevel("pickaxe", 2);
    }

    @Override @SideOnly(Side.CLIENT)
    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
        for (EnumPlutoBlocks t : EnumPlutoBlocks.values()) list.add(new ItemStack(this, 1, t.getMeta()));
    }

    @Override public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
        return new ItemStack(Item.getItemFromBlock(this), 1, getMetaFromState(state));
    }

    @Override public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return getMetaFromState(state) == 4 ? ModItems.URANIUM_FRAGMENTS : Item.getItemFromBlock(this);
    }

    @Override public int damageDropped(IBlockState state) {
        return getMetaFromState(state) == 4 ? 0 : getMetaFromState(state);
    }

    @Override public int quantityDropped(IBlockState state, int fortune, Random random) {
        if (getMetaFromState(state) == 4) {
            return 1 + random.nextInt(2);
        }
        return super.quantityDropped(state, fortune, random);
    }

    public enum EnumPlutoBlocks implements IStringSerializable {
        PLUTO_GRUNT(0, "pluto_grunt"), PLUTO_SUBGRUNT(1, "pluto_subgrunt"),
        PLUTO_IRON_ORE(2, "pluto_iron_ore"), PLUTO_SULFUR_ORE(3, "pluto_sulfur_ore"),
        PLUTO_URANIUM_ORE(4, "pluto_uranium_ore"), PLUTO_FROZEN_CRUST(5, "pluto_frozen_crust");
        private final int meta; private final String name;
        private static final EnumPlutoBlocks[] VALUES = values();
        EnumPlutoBlocks(int meta, String name) { this.meta = meta; this.name = name; }
        public int getMeta() { return meta; } @Override public String getName() { return name; }
        public static EnumPlutoBlocks byMetadata(int meta) { return VALUES[meta % VALUES.length]; }
    }

    @Override public IBlockState getStateFromMeta(int meta) { return getDefaultState().withProperty(TYPE, EnumPlutoBlocks.byMetadata(meta)); }
    @Override public int getMetaFromState(IBlockState state) { return state.getValue(TYPE).getMeta(); }
    @Override protected BlockStateContainer createBlockState() { return new BlockStateContainer(this, TYPE); }
}
