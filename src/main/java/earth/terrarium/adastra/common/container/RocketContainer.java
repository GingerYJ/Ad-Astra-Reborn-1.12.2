package earth.terrarium.adastra.common.container;

import earth.terrarium.adastra.common.entities.vehicles.RocketEntity;
import earth.terrarium.adastra.common.util.RocketFuelHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

/**
 * Container for Rocket inventory GUI.
 * Provides 10 storage slots for rocket cargo.
 */
public class RocketContainer extends Container {

    private final RocketEntity rocket;
    private static final int ROCKET_SLOTS = 10;
    private static final int FUEL_INPUT_SLOT = 0;
    private static final int FUEL_OUTPUT_SLOT = 1;
    private static final int PLAYER_INVENTORY_ROWS = 3;
    private static final int PLAYER_INVENTORY_COLUMNS = 9;
    private static final int HOTBAR_SLOTS = 9;

    public RocketContainer(InventoryPlayer playerInventory, RocketEntity rocket) {
        this.rocket = rocket;

        this.addSlotToContainer(new VehicleSlot(rocket, FUEL_INPUT_SLOT, 12, 24));
        this.addSlotToContainer(new VehicleSlot(rocket, FUEL_OUTPUT_SLOT, 12, 54) {
            @Override
            public boolean isItemValid(ItemStack stack) {
                return false;
            }
        });

        for (int row = 0; row < 2; row++) {
            for (int col = 0; col < 4; col++) {
                int index = 2 + col + row * 4;
                this.addSlotToContainer(new VehicleSlot(rocket, index, 78 + col * 18, 31 + row * 18));
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
        return !rocket.isDead && playerIn.getDistanceSq(rocket) <= 64.0D;
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        if (!rocket.world.isRemote) {
            RocketFuelHelper.moveFuelItemToTank(rocket.getFuelTank(), rocket.getInventory(), FUEL_INPUT_SLOT, FUEL_OUTPUT_SLOT);
        }
        for (IContainerListener listener : this.listeners) {
            listener.sendWindowProperty(this, 0, rocket.getFluidFuelAmount());
        }
    }

    @Override
    public void updateProgressBar(int id, int data) {
        if (id == 0) {
            rocket.setClientFluidFuelAmount(data);
        }
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);

        if (slot != null && slot.getHasStack()) {
            ItemStack stackInSlot = slot.getStack();
            itemstack = stackInSlot.copy();

            if (index < ROCKET_SLOTS) {
                // Moving from rocket to player inventory
                if (!this.mergeItemStack(stackInSlot, ROCKET_SLOTS, this.inventorySlots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                // Moving from player to rocket inventory
                if (!this.mergeItemStack(stackInSlot, 0, ROCKET_SLOTS, false)) {
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

    /**
     * Custom slot for vehicle inventory.
     */
    private static class VehicleSlot extends Slot {
        private final RocketEntity rocket;
        private final int slotIndex;

        public VehicleSlot(RocketEntity rocket, int index, int xPosition, int yPosition) {
            super(null, index, xPosition, yPosition);
            this.rocket = rocket;
            this.slotIndex = index;
        }

        @Override
        public ItemStack getStack() {
            return rocket.getInventory().get(slotIndex);
        }

        @Override
        public void putStack(ItemStack stack) {
            rocket.getInventory().set(slotIndex, stack);
            this.onSlotChanged();
        }

        @Override
        public void onSlotChanged() {
            // Mark rocket as dirty (for saving)
        }

        @Override
        public int getSlotStackLimit() {
            return 64;
        }

        @Override
        public ItemStack decrStackSize(int amount) {
            ItemStack stack = getStack();
            if (stack.isEmpty()) {
                return ItemStack.EMPTY;
            }
            ItemStack result;
            if (stack.getCount() <= amount) {
                result = stack;
                putStack(ItemStack.EMPTY);
            } else {
                result = stack.splitStack(amount);
                if (stack.isEmpty()) {
                    putStack(ItemStack.EMPTY);
                }
            }
            this.onSlotChanged();
            return result;
        }

        @Override
        public boolean isItemValid(ItemStack stack) {
            return true; // Accept all items
        }

        @Override
        public boolean getHasStack() {
            return !getStack().isEmpty();
        }
    }

    public RocketEntity getRocket() {
        return rocket;
    }
}
