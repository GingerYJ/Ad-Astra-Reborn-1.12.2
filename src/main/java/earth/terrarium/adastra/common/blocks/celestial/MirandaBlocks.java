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

public class MirandaBlocks extends Block {
    public static final PropertyEnum<EnumMirandaBlocks> TYPE = PropertyEnum.create("type", EnumMirandaBlocks.class);

    public MirandaBlocks() {
        super(Material.ROCK);
        this.setSoundType(SoundType.STONE);
        this.setHarvestLevel("pickaxe", 2);
    }

    @Override @SideOnly(Side.CLIENT)
    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
        for (EnumMirandaBlocks t : EnumMirandaBlocks.values()) list.add(new ItemStack(this, 1, t.getMeta()));
    }

    @Override public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
        return new ItemStack(Item.getItemFromBlock(this), 1, getMetaFromState(state));
    }

    @Override public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        switch (getMetaFromState(state)) {
            case 3: return Item.getItemFromBlock(this);
            case 4: return ModItems.DOLOMITE_CRYSTAL;
            case 5: return Items.DIAMOND;
            case 6: return Items.QUARTZ;
            case 7: return ModItems.COBALT_INGOT;
            default: return Item.getItemFromBlock(this);
        }
    }

    @Override public int damageDropped(IBlockState state) {
        int meta = getMetaFromState(state);
        switch (meta) {
            case 4: // dolomite crystal
            case 5: // diamond
            case 6: // quartz
            case 7: // cobalt ingot
                return 0;
            default:
                return meta;
        }
    }

    public enum EnumMirandaBlocks implements IStringSerializable {
        MIRANDA_GRUNT_1(0, "miranda_grunt_1"), MIRANDA_SUBGRUNT_1(1, "miranda_subgrunt_1"),
        MIRANDA_STONE_1(2, "miranda_stone_1"), MIRANDA_IRON_ORE(3, "miranda_iron_ore"),
        MIRANDA_DOLOMITE_ORE(4, "miranda_dolomite_ore"), MIRANDA_DIAMOND_ORE(5, "miranda_diamond_ore"),
        MIRANDA_QUARTZ_ORE(6, "miranda_quartz_ore"), MIRANDA_COBALT_ORE(7, "miranda_cobalt_ore"),
        MIRANDA_NICKEL_ORE(8, "miranda_nickel_ore"), MIRANDA_GRUNT_2(9, "miranda_grunt_2"),
        MIRANDA_GRUNT_3(10, "miranda_grunt_3"), MIRANDA_SUBGRUNT_2(11, "miranda_subgrunt_2"),
        MIRANDA_SUBGRUNT_3(12, "miranda_subgrunt_3"), MIRANDA_STONE_2(13, "miranda_stone_2"),
        MIRANDA_STONE_3(14, "miranda_stone_3"), MIRANDA_ICE(15, "miranda_ice");
        private final int meta; private final String name;
        private static final EnumMirandaBlocks[] VALUES = values();
        EnumMirandaBlocks(int meta, String name) { this.meta = meta; this.name = name; }
        public int getMeta() { return meta; } @Override public String getName() { return name; }
        public static EnumMirandaBlocks byMetadata(int meta) { return VALUES[meta % VALUES.length]; }
    }

    @Override public IBlockState getStateFromMeta(int meta) { return getDefaultState().withProperty(TYPE, EnumMirandaBlocks.byMetadata(meta)); }
    @Override public int getMetaFromState(IBlockState state) { return state.getValue(TYPE).getMeta(); }
    @Override protected BlockStateContainer createBlockState() { return new BlockStateContainer(this, TYPE); }
}
