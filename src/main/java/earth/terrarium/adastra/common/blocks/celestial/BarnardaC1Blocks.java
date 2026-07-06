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

public class BarnardaC1Blocks extends Block {
    public static final PropertyEnum<EnumBarnardaC1Blocks> TYPE = PropertyEnum.create("type", EnumBarnardaC1Blocks.class);

    public BarnardaC1Blocks() {
        super(Material.ROCK);
        this.setSoundType(SoundType.STONE);
        this.setHarvestLevel("pickaxe", 2);
    }

    @Override @SideOnly(Side.CLIENT)
    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
        for (EnumBarnardaC1Blocks t : EnumBarnardaC1Blocks.values()) list.add(new ItemStack(this, 1, t.getMeta()));
    }

    @Override public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
        return new ItemStack(Item.getItemFromBlock(this), 1, getMetaFromState(state));
    }

    public enum EnumBarnardaC1Blocks implements IStringSerializable {
        BARNARDA_C1_GRUNT(0, "barnarda_c1_grunt"), BARNARDA_C1_STONE(1, "barnarda_c1_stone"),
        BARNARDA_C1_COBBLESTONE(2, "barnarda_c1_cobblestone"), BARNARDA_C1_IRON_ORE(3, "barnarda_c1_iron_ore");
        private final int meta; private final String name;
        private static final EnumBarnardaC1Blocks[] VALUES = values();
        EnumBarnardaC1Blocks(int meta, String name) { this.meta = meta; this.name = name; }
        public int getMeta() { return meta; } @Override public String getName() { return name; }
        public static EnumBarnardaC1Blocks byMetadata(int meta) { return VALUES[meta % VALUES.length]; }
    }

    @Override public int damageDropped(IBlockState state) { return getMetaFromState(state); }

    @Override public IBlockState getStateFromMeta(int meta) { return getDefaultState().withProperty(TYPE, EnumBarnardaC1Blocks.byMetadata(meta)); }
    @Override public int getMetaFromState(IBlockState state) { return state.getValue(TYPE).getMeta(); }
    @Override protected BlockStateContainer createBlockState() { return new BlockStateContainer(this, TYPE); }
}
