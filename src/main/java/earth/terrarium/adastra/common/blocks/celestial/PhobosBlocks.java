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

public class PhobosBlocks extends Block {
    public static final PropertyEnum<EnumPhobosBlocks> TYPE = PropertyEnum.create("type", EnumPhobosBlocks.class);

    public PhobosBlocks() {
        super(Material.ROCK);
        this.setSoundType(SoundType.STONE);
        this.setHarvestLevel("pickaxe", 2);
    }

    @Override @SideOnly(Side.CLIENT)
    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
        for (EnumPhobosBlocks t : EnumPhobosBlocks.values()) list.add(new ItemStack(this, 1, t.getMeta()));
    }

    @Override public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
        return new ItemStack(Item.getItemFromBlock(this), 1, getMetaFromState(state));
    }

    @Override public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        int meta = getMetaFromState(state);
        if (meta == 3) return ModItems.METEORIC_IRON_FRAGMENTS;
        if (meta == 5) return ModItems.RAW_DESH;
        return Item.getItemFromBlock(this);
    }

    @Override public int damageDropped(IBlockState state) {
        int meta = getMetaFromState(state);
        return meta == 3 || meta == 5 ? 0 : meta;
    }

    @Override public int quantityDropped(IBlockState state, int fortune, Random random) {
        int meta = getMetaFromState(state);
        if (meta == 3 || meta == 5) {
            return 1 + random.nextInt(2);
        }
        return super.quantityDropped(state, fortune, random);
    }

    public enum EnumPhobosBlocks implements IStringSerializable {
        PHOBOS_REGOLITE(0, "phobos_regolite"), PHOBOS_STONE(1, "phobos_stone"),
        PHOBOS_IRON_ORE(2, "phobos_iron_ore"), PHOBOS_METEORICIRON_ORE(3, "phobos_meteoriciron_ore"),
        PHOBOS_NICKEL_ORE(4, "phobos_nickel_ore"), PHOBOS_DESH_ORE(5, "phobos_desh_ore");
        private final int meta; private final String name;
        private static final EnumPhobosBlocks[] VALUES = values();
        EnumPhobosBlocks(int meta, String name) { this.meta = meta; this.name = name; }
        public int getMeta() { return meta; } @Override public String getName() { return name; }
        public static EnumPhobosBlocks byMetadata(int meta) { return VALUES[meta % VALUES.length]; }
    }

    @Override public IBlockState getStateFromMeta(int meta) { return getDefaultState().withProperty(TYPE, EnumPhobosBlocks.byMetadata(meta)); }
    @Override public int getMetaFromState(IBlockState state) { return state.getValue(TYPE).getMeta(); }
    @Override protected BlockStateContainer createBlockState() { return new BlockStateContainer(this, TYPE); }
}
