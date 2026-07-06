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

public class TauCetiFBlocks extends Block {
    public static final PropertyEnum<EnumTauCetiFBlocks> TYPE = PropertyEnum.create("type", EnumTauCetiFBlocks.class);

    public TauCetiFBlocks() {
        super(Material.ROCK);
        this.setSoundType(SoundType.STONE);
        this.setHarvestLevel("pickaxe", 2);
    }

    @Override @SideOnly(Side.CLIENT)
    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
        for (EnumTauCetiFBlocks t : EnumTauCetiFBlocks.values()) list.add(new ItemStack(this, 1, t.getMeta()));
    }

    @Override public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
        return new ItemStack(Item.getItemFromBlock(this), 1, getMetaFromState(state));
    }

    public enum EnumTauCetiFBlocks implements IStringSerializable {
        TAUCETI_F_GRUNT(0, "tauceti_f_grunt"), TAUCETI_F_SUBGRUNT(1, "tauceti_f_subgrunt"),
        TAUCETI_F_STONE(2, "tauceti_f_stone"), TAUCETI_F_SAND(3, "tauceti_f_sand"),
        TAUCETI_F_COBBLESTONE(4, "tauceti_f_cobblestone"), TAUCETI_F_IRON_ORE(5, "tauceti_f_iron_ore"),
        TAUCETI_F_COAL_ORE(6, "tauceti_f_coal_ore"), TAUCETI_F_GOLD_ORE(7, "tauceti_f_gold_ore"),
        TAUCETI_F_DIAMOND_ORE(8, "tauceti_f_diamond_ore"), TAUCETI_F_LAPIS_ORE(9, "tauceti_f_lapis_ore");
        private final int meta; private final String name;
        private static final EnumTauCetiFBlocks[] VALUES = values();
        EnumTauCetiFBlocks(int meta, String name) { this.meta = meta; this.name = name; }
        public int getMeta() { return meta; } @Override public String getName() { return name; }
        public static EnumTauCetiFBlocks byMetadata(int meta) { return VALUES[meta % VALUES.length]; }
    }

    @Override public int damageDropped(IBlockState state) { return getMetaFromState(state); }

    @Override public IBlockState getStateFromMeta(int meta) { return getDefaultState().withProperty(TYPE, EnumTauCetiFBlocks.byMetadata(meta)); }
    @Override public int getMetaFromState(IBlockState state) { return state.getValue(TYPE).getMeta(); }
    @Override protected BlockStateContainer createBlockState() { return new BlockStateContainer(this, TYPE); }
}
