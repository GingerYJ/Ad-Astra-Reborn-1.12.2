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

public class GanymedeBlocks extends Block {
    public static final PropertyEnum<EnumGanymedeBlocks> TYPE = PropertyEnum.create("type", EnumGanymedeBlocks.class);

    public GanymedeBlocks() {
        super(Material.ROCK);
        this.setSoundType(SoundType.STONE);
        this.setHarvestLevel("pickaxe", 2);
    }

    @Override @SideOnly(Side.CLIENT)
    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
        for (EnumGanymedeBlocks t : EnumGanymedeBlocks.values()) list.add(new ItemStack(this, 1, t.getMeta()));
    }

    @Override public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
        return new ItemStack(Item.getItemFromBlock(this), 1, getMetaFromState(state));
    }

    @Override public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        switch (getMetaFromState(state)) {
            case 2: return ModItems.MAGNESIUM_INGOT;
            case 3: return ModItems.ILMENITE_RAW;
            default: return Item.getItemFromBlock(this);
        }
    }

    @Override public int damageDropped(IBlockState state) { return getMetaFromState(state) >= 2 ? 0 : getMetaFromState(state); }

    public enum EnumGanymedeBlocks implements IStringSerializable {
        GANYMEDE_GRUNT(0, "ganymede_grunt"), GANYMEDE_STONE(1, "ganymede_stone"),
        GANYMEDE_MAGNESIUM_ORE(2, "ganymede_magnesium_ore"), GANYMEDE_TITANIUM_ORE(3, "ganymede_titanium_ore");
        private final int meta; private final String name;
        private static final EnumGanymedeBlocks[] VALUES = values();
        EnumGanymedeBlocks(int meta, String name) { this.meta = meta; this.name = name; }
        public int getMeta() { return meta; } @Override public String getName() { return name; }
        public static EnumGanymedeBlocks byMetadata(int meta) { return VALUES[meta % VALUES.length]; }
    }

    @Override public IBlockState getStateFromMeta(int meta) { return getDefaultState().withProperty(TYPE, EnumGanymedeBlocks.byMetadata(meta)); }
    @Override public int getMetaFromState(IBlockState state) { return state.getValue(TYPE).getMeta(); }
    @Override protected BlockStateContainer createBlockState() { return new BlockStateContainer(this, TYPE); }
}
