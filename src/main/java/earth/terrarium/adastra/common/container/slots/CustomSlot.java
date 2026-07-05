package earth.terrarium.adastra.common.container.slots;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class CustomSlot extends Slot {

    private final boolean canInsert;
    private final boolean canTake;

    public CustomSlot(IInventory inventory, int index, int xPosition, int yPosition, boolean canInsert, boolean canTake) {
        super(inventory, index, xPosition, yPosition);
        this.canInsert = canInsert;
        this.canTake = canTake;
    }

    public static CustomSlot noInsert(IInventory inventory, int index, int xPosition, int yPosition) {
        return new CustomSlot(inventory, index, xPosition, yPosition, false, true);
    }

    public static CustomSlot noInsertOrTake(IInventory inventory, int index, int xPosition, int yPosition) {
        return new CustomSlot(inventory, index, xPosition, yPosition, false, false);
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
        return canInsert;
    }

    @Override
    public boolean canTakeStack(EntityPlayer playerIn) {
        return canTake;
    }
}
