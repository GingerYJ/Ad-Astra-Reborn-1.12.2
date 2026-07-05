package earth.terrarium.adastra.common.container.slots;

import earth.terrarium.adastra.common.tile.NasaWorkbenchTileEntity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class NasaWorkbenchOutputSlot extends Slot {

    private final NasaWorkbenchTileEntity nasaWorkbench;

    public NasaWorkbenchOutputSlot(NasaWorkbenchTileEntity inventory, int index, int xPosition, int yPosition) {
        super(inventory, index, xPosition, yPosition);
        this.nasaWorkbench = inventory;
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
        return false;
    }

    @Override
    public boolean canTakeStack(EntityPlayer playerIn) {
        return nasaWorkbench.hasCraftingResult();
    }

    @Override
    public ItemStack onTake(EntityPlayer playerIn, ItemStack stack) {
        nasaWorkbench.craftActiveRecipe(playerIn);
        return super.onTake(playerIn, stack);
    }
}
