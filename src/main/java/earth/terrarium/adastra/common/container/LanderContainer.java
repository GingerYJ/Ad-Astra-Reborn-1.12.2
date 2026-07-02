package earth.terrarium.adastra.common.container;

import earth.terrarium.adastra.common.entities.vehicles.LanderEntity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

/**
 * Container for Lander inventory GUI.
 * Provides 6 storage slots for lander cargo.
 */
public class LanderContainer extends Container {

    private final LanderEntity lander;
    private static final int LANDER_SLOTS = 6;
    private static final int PLAYER_INVENTORY_ROWS = 3;
    private static final int PLAYER_INVENTORY_COLUMNS = 9;
    private static final int HOTBAR_SLOTS = 9;

    public LanderContainer(InventoryPlayer playerInventory, LanderEntity lander) {
        this.lander = lander;

        // Lander inventory slots (1 row of 6)
        for (int col = 0; col < 6; col++) {
            this.addSlotToContainer(new VehicleSlot(lander, col, 53 + col * 18, 18));
        }

        // Player inventory (3 rows)
        for (int row = 0; row < PLAYER_INVENTORY_ROWS; row++) {
            for (int col = 0; col < PLAYER_INVENTORY_COLUMNS; col++) {
                this.addSlotToContainer(new Slot(playerInventory, col + row * 9 + 9,
                    8 + col * 18, 84 + row * 18));
            }
        }

        // Player hotbar
        for (int col = 0; col < HOTBAR_SLOTS; col++) {
            this.addSlotToContainer(new Slot(playerInventory, col, 8 + col * 18, 142));
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return !lander.isDead && playerIn.getDistanceSq(lander) <= 64.0D;
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);

        if (slot != null && slot.getHasStack()) {
            ItemStack stackInSlot = slot.getStack();
            itemstack = stackInSlot.copy();

            if (index < LANDER_SLOTS) {
                if (!this.mergeItemStack(stackInSlot, LANDER_SLOTS, this.inventorySlots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (!this.mergeItemStack(stackInSlot, 0, LANDER_SLOTS, false)) {
                    return ItemStack.EMPTY;
                }
            }

            if (stackInSlot.isEmpty()) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }
        }

        return itemstack;
    }

    private static class VehicleSlot extends Slot {
        private final LanderEntity lander;
        private final int slotIndex;

        public VehicleSlot(LanderEntity lander, int index, int xPosition, int yPosition) {
            super(null, index, xPosition, yPosition);
            this.lander = lander;
            this.slotIndex = index;
        }

        @Override
        public ItemStack getStack() {
            return lander.getInventory().get(slotIndex);
        }

        @Override
        public void putStack(ItemStack stack) {
            lander.getInventory().set(slotIndex, stack);
            this.onSlotChanged();
        }

        @Override
        public int getSlotStackLimit() {
            return 64;
        }

        @Override
        public ItemStack decrStackSize(int amount) {
            ItemStack stack = getStack();
            if (stack.isEmpty()) return ItemStack.EMPTY;
            ItemStack result;
            if (stack.getCount() <= amount) {
                result = stack;
                putStack(ItemStack.EMPTY);
            } else {
                result = stack.splitStack(amount);
                if (stack.isEmpty()) putStack(ItemStack.EMPTY);
            }
            this.onSlotChanged();
            return result;
        }

        @Override
        public boolean isItemValid(ItemStack stack) {
            return true;
        }

        @Override
        public boolean getHasStack() {
            return !getStack().isEmpty();
        }
    }

    public LanderEntity getLander() {
        return lander;
    }
}
