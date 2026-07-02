package earth.terrarium.adastra.common.container;

import earth.terrarium.adastra.common.entities.vehicles.RoverEntity;
import earth.terrarium.adastra.common.util.RocketFuelHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

/**
 * Container for Rover inventory GUI.
 * Provides 18 storage slots for rover cargo.
 */
public class RoverContainer extends Container {

    private final RoverEntity rover;
    private static final int ROVER_SLOTS = 18;
    private static final int FUEL_INPUT_SLOT = 0;
    private static final int FUEL_OUTPUT_SLOT = 1;
    private static final int PLAYER_INVENTORY_ROWS = 3;
    private static final int PLAYER_INVENTORY_COLUMNS = 9;
    private static final int HOTBAR_SLOTS = 9;

    public RoverContainer(InventoryPlayer playerInventory, RoverEntity rover) {
        this.rover = rover;

        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 4; col++) {
                int index = 2 + col + row * 4;
                this.addSlotToContainer(new VehicleSlot(rover, index, 78 + col * 18, 16 + row * 18));
            }
        }

        this.addSlotToContainer(new VehicleSlot(rover, FUEL_INPUT_SLOT, 12, 26));
        this.addSlotToContainer(new VehicleSlot(rover, FUEL_OUTPUT_SLOT, 12, 56) {
            @Override
            public boolean isItemValid(ItemStack stack) {
                return false;
            }
        });

        // Player inventory (3 rows)
        for (int row = 0; row < PLAYER_INVENTORY_ROWS; row++) {
            for (int col = 0; col < PLAYER_INVENTORY_COLUMNS; col++) {
                this.addSlotToContainer(new Slot(playerInventory, col + row * 9 + 9,
                    col * 18, 99 + row * 18));
            }
        }

        // Player hotbar
        for (int col = 0; col < HOTBAR_SLOTS; col++) {
            this.addSlotToContainer(new Slot(playerInventory, col, col * 18, 157));
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return !rover.isDead && playerIn.getDistanceSq(rover) <= 64.0D;
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        if (!rover.world.isRemote) {
            RocketFuelHelper.moveFuelItemToTank(rover.getFuelTank(), rover.getInventory(), FUEL_INPUT_SLOT, FUEL_OUTPUT_SLOT);
        }
        for (IContainerListener listener : this.listeners) {
            listener.sendWindowProperty(this, 0, rover.getFluidFuelAmount());
        }
    }

    @Override
    public void updateProgressBar(int id, int data) {
        if (id == 0) {
            rover.setClientFluidFuelAmount(data);
        }
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);

        if (slot != null && slot.getHasStack()) {
            ItemStack stackInSlot = slot.getStack();
            itemstack = stackInSlot.copy();

            if (index < ROVER_SLOTS) {
                if (!this.mergeItemStack(stackInSlot, ROVER_SLOTS, this.inventorySlots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (!this.mergeItemStack(stackInSlot, 0, ROVER_SLOTS, false)) {
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
        private final RoverEntity rover;
        private final int slotIndex;

        public VehicleSlot(RoverEntity rover, int index, int xPosition, int yPosition) {
            super(null, index, xPosition, yPosition);
            this.rover = rover;
            this.slotIndex = index;
        }

        @Override
        public ItemStack getStack() {
            return rover.getInventory().get(slotIndex);
        }

        @Override
        public void putStack(ItemStack stack) {
            rover.getInventory().set(slotIndex, stack);
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

    public RoverEntity getRover() {
        return rover;
    }
}
