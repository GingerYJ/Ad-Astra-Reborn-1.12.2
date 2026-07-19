package earth.terrarium.adastra.common.blocks;

import earth.terrarium.adastra.common.AdAstraCreativeTab;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFlowerPot;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFlowerPot;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * A named, pre-filled flower pot for plants that vanilla 1.12.2 does not
 * know how to render in the shared flower-pot block.
 */
public final class AdAstraFlowerPotBlock extends BlockFlowerPot {

    private final Block plant;

    public AdAstraFlowerPotBlock(Block plant) {
        super();
        this.plant = plant;
        setCreativeTab(AdAstraCreativeTab.INSTANCE);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        Item plantItem = Item.getItemFromBlock(plant);
        return new TileEntityFlowerPot(plantItem, 0);
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player,
                                    EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        TileEntity tile = world.getTileEntity(pos);
        if (!(tile instanceof TileEntityFlowerPot)) {
            return false;
        }

        TileEntityFlowerPot pot = (TileEntityFlowerPot) tile;
        ItemStack potted = pot.getFlowerItemStack();
        ItemStack held = player.getHeldItem(hand);
        if (!potted.isEmpty()) {
            if (held.isEmpty()) {
                player.setHeldItem(hand, potted);
            } else if (!player.addItemStackToInventory(potted)) {
                player.dropItem(potted, false);
            }
            pot.setItemStack(ItemStack.EMPTY);
            pot.markDirty();
            world.setBlockState(pos, Blocks.FLOWER_POT.getDefaultState(), 3);
            return true;
        }

        if (held.isEmpty() || Block.getBlockFromItem(held.getItem()) != plant) {
            return false;
        }
        pot.setItemStack(new ItemStack(held.getItem(), 1, held.getMetadata()));
        if (!player.capabilities.isCreativeMode) {
            held.shrink(1);
        }
        pot.markDirty();
        world.notifyBlockUpdate(pos, state, state, 3);
        return true;
    }
}
