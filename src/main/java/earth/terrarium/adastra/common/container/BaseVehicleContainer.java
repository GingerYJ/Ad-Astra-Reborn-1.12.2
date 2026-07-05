package earth.terrarium.adastra.common.container;

import earth.terrarium.adastra.common.entities.vehicles.VehicleBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public abstract class BaseVehicleContainer<T extends VehicleBase> extends Container {

    private static final int PLAYER_INVENTORY_ROWS = 3;
    private static final int PLAYER_INVENTORY_COLUMNS = 9;
    private static final int HOTBAR_SLOTS = 9;

    protected final T vehicle;
    private final int vehicleSlotCount;

    protected BaseVehicleContainer(T vehicle, int vehicleSlotCount) {
        this.vehicle = vehicle;
        this.vehicleSlotCount = vehicleSlotCount;
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return !vehicle.isDead && playerIn.getDistanceSq(vehicle) <= 64.0D;
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
        ItemStack original = ItemStack.EMPTY;
        Slot slot = inventorySlots.get(index);
        if (slot == null || !slot.getHasStack()) {
            return original;
        }

        ItemStack stack = slot.getStack();
        original = stack.copy();
        if (index < vehicleSlotCount) {
            if (!mergeItemStack(stack, vehicleSlotCount, inventorySlots.size(), true)) {
                return ItemStack.EMPTY;
            }
        } else if (!mergeItemStack(stack, 0, vehicleSlotCount, false)) {
            return ItemStack.EMPTY;
        }

        if (stack.isEmpty()) {
            slot.putStack(ItemStack.EMPTY);
        } else {
            slot.onSlotChanged();
        }
        return original;
    }

    protected void addPlayerInventory(InventoryPlayer playerInventory, int x, int y) {
        for (int row = 0; row < PLAYER_INVENTORY_ROWS; row++) {
            for (int col = 0; col < PLAYER_INVENTORY_COLUMNS; col++) {
                addSlotToContainer(new Slot(playerInventory, col + row * 9 + 9, x + col * 18, y + row * 18));
            }
        }

        for (int col = 0; col < HOTBAR_SLOTS; col++) {
            addSlotToContainer(new Slot(playerInventory, col, x + col * 18, y + 58));
        }
    }
}
