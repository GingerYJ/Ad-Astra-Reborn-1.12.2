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
import net.minecraft.init.Items;
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

public class TitanBlocks extends Block {
    public static final PropertyEnum<EnumTitanBlocks> TYPE = PropertyEnum.create("type", EnumTitanBlocks.class);

    public TitanBlocks() {
        super(Material.ROCK);
        this.setSoundType(SoundType.STONE);
        this.setHarvestLevel("pickaxe", 2);
    }

    @Override @SideOnly(Side.CLIENT)
    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
        for (EnumTitanBlocks t : EnumTitanBlocks.values()) list.add(new ItemStack(this, 1, t.getMeta()));
    }

    @Override public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
        return new ItemStack(Item.getItemFromBlock(this), 1, getMetaFromState(state));
    }

    @Override public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        switch (getMetaFromState(state)) {
            case 3: return ModItems.SAPPHIRE;
            case 4: return Items.EMERALD;
            case 5: return Items.DIAMOND;
            case 6: return Items.COAL;
            case 7: return Items.DYE;
            case 8: return Items.REDSTONE;
            default: return Item.getItemFromBlock(this);
        }
    }

    @Override public int damageDropped(IBlockState state) {
        if (getMetaFromState(state) == 7) return 4;
        return 0;
    }

    @Override public int quantityDropped(IBlockState state, int fortune, Random random) {
        int meta = getMetaFromState(state);
        if (meta == 3) return 1 + random.nextInt(2);
        if (meta == 7) return 8 + random.nextInt(5);
        if (meta == 8) return 4 + random.nextInt(3);
        return super.quantityDropped(state, fortune, random);
    }

    public enum EnumTitanBlocks implements IStringSerializable {
        TITAN_GRUNT(0, "titan_grunt"), TITAN_SUBGRUNT(1, "titan_subgrunt"), TITAN_STONE(2, "titan_stone"),
        TITAN_SAPPHIRE_ORE(3, "titan_sapphire_ore"), TITAN_EMERALD_ORE(4, "titan_emerald_ore"),
        TITAN_DIAMOND_ORE(5, "titan_diamond_ore"), TITAN_COAL_ORE(6, "titan_coal_ore"),
        TITAN_LAPIS_ORE(7, "titan_lapis_ore"), TITAN_REDSTONE_ORE(8, "titan_redstone_ore");
        private final int meta; private final String name;
        private static final EnumTitanBlocks[] VALUES = values();
        EnumTitanBlocks(int meta, String name) { this.meta = meta; this.name = name; }
        public int getMeta() { return meta; } @Override public String getName() { return name; }
        public static EnumTitanBlocks byMetadata(int meta) { return VALUES[meta % VALUES.length]; }
    }

    @Override public IBlockState getStateFromMeta(int meta) { return getDefaultState().withProperty(TYPE, EnumTitanBlocks.byMetadata(meta)); }
    @Override public int getMetaFromState(IBlockState state) { return state.getValue(TYPE).getMeta(); }
    @Override protected BlockStateContainer createBlockState() { return new BlockStateContainer(this, TYPE); }
}
