package earth.terrarium.adastra.common.container;

import earth.terrarium.adastra.common.entities.vehicles.LanderEntity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

/**
 * Container for Lander inventory GUI.
 * Provides 11 retrieval slots for landed rocket cargo.
 */
public class LanderContainer extends Container {

    private final LanderEntity lander;
    private static final int LANDER_SLOTS = 11;
    private static final int PLAYER_INVENTORY_ROWS = 3;
    private static final int PLAYER_INVENTORY_COLUMNS = 9;
    private static final int HOTBAR_SLOTS = 9;

    public LanderContainer(InventoryPlayer playerInventory, LanderEntity lander) {
        this.lander = lander;

        this.addSlotToContainer(new VehicleSlot(lander, 0, 26, 27));
        this.addSlotToContainer(new VehicleSlot(lander, 1, 11, 58));
        this.addSlotToContainer(new VehicleSlot(lander, 2, 40, 58));

        for (int row = 0; row < 2; row++) {
            for (int col = 0; col < 4; col++) {
                int index = 3 + col + row * 4;
                this.addSlotToContainer(new VehicleSlot(lander, index, 77 + col * 18, 31 + row * 18));
            }
        }

        // Player inventory (3 rows)
        for (int row = 0; row < PLAYER_INVENTORY_ROWS; row++) {
            for (int col = 0; col < PLAYER_INVENTORY_COLUMNS; col++) {
                this.addSlotToContainer(new Slot(playerInventory, col + row * 9 + 9,
                    col * 18, 92 + row * 18));
            }
        }

        // Player hotbar
        for (int col = 0; col < HOTBAR_SLOTS; col++) {
            this.addSlotToContainer(new Slot(playerInventory, col, col * 18, 150));
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
            lander.getInventory().set(slotIndex, stack == null ? ItemStack.EMPTY : stack);
            this.onSlotChanged();
        }

        @Override
        public void onSlotChanged() {
            // Vehicle inventory is stored on the entity, so there is no backing IInventory to mark dirty.
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
            return false;
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
