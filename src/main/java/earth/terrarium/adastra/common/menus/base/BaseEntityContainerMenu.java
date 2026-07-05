package earth.terrarium.adastra.common.menus.base;

import earth.terrarium.adastra.common.container.slots.InventorySlot;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public abstract class BaseEntityContainerMenu<T extends Entity> extends Container {

    protected static final int START_INDEX = 0;

    protected final T entity;
    protected final InventoryPlayer inventory;
    private final int entitySlotCount;

    protected BaseEntityContainerMenu(InventoryPlayer inventory, T entity, int entitySlotCount) {
        this.inventory = inventory;
        this.entity = entity;
        this.entitySlotCount = entitySlotCount;
    }

    public T getEntity() {
        return entity;
    }

    protected int getContainerInputEnd() {
        return entitySlotCount;
    }

    protected int getInventoryStart() {
        return entitySlotCount;
    }

    protected int startIndex() {
        return START_INDEX;
    }

    public abstract int getPlayerInvXOffset();

    public abstract int getPlayerInvYOffset();

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return !entity.isDead && playerIn.getDistanceSq(entity) <= 64.0D;
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
        if (index < getInventoryStart()) {
            if (!mergeItemStack(stack, getInventoryStart(), inventorySlots.size(), true)) {
                return ItemStack.EMPTY;
            }
        } else if (!mergeIntoEntitySlots(stack)) {
            return ItemStack.EMPTY;
        }

        if (stack.isEmpty()) {
            slot.putStack(ItemStack.EMPTY);
        } else {
            slot.onSlotChanged();
        }

        if (stack.getCount() == original.getCount()) {
            return ItemStack.EMPTY;
        }
        slot.onTake(playerIn, stack);
        return original;
    }

    protected boolean mergeIntoEntitySlots(ItemStack stack) {
        for (int i = startIndex(); i < getContainerInputEnd(); i++) {
            Slot slot = inventorySlots.get(i);
            if (!slot.isItemValid(stack)) {
                continue;
            }
            if (mergeItemStack(stack, i, i + 1, false)) {
                return true;
            }
        }
        return false;
    }

    protected void addPlayerInvSlots() {
        int x = getPlayerInvXOffset();
        int y = getPlayerInvYOffset();
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                addSlotToContainer(new InventorySlot(inventory, column + row * 9 + 9, x + column * 18, y + row * 18));
            }
        }
        for (int column = 0; column < 9; column++) {
            addSlotToContainer(new InventorySlot(inventory, column, x + column * 18, y + 58));
        }
    }
}
