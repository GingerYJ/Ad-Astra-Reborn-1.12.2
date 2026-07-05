package earth.terrarium.adastra.common.container.slots;

import earth.terrarium.adastra.common.entities.vehicles.VehicleBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import java.util.function.Predicate;

public class VehicleInventorySlot extends Slot {

    private final VehicleBase vehicle;
    private final int slotIndex;
    private final Predicate<ItemStack> canInsert;
    private final boolean canTake;

    public VehicleInventorySlot(VehicleBase vehicle, int index, int xPosition, int yPosition) {
        this(vehicle, index, xPosition, yPosition, true, true);
    }

    public VehicleInventorySlot(VehicleBase vehicle, int index, int xPosition, int yPosition, boolean canInsert, boolean canTake) {
        this(vehicle, index, xPosition, yPosition, stack -> canInsert, canTake);
    }

    public VehicleInventorySlot(VehicleBase vehicle, int index, int xPosition, int yPosition, Predicate<ItemStack> canInsert, boolean canTake) {
        super(null, index, xPosition, yPosition);
        this.vehicle = vehicle;
        this.slotIndex = index;
        this.canInsert = canInsert;
        this.canTake = canTake;
    }

    public static VehicleInventorySlot noInsert(VehicleBase vehicle, int index, int xPosition, int yPosition) {
        return new VehicleInventorySlot(vehicle, index, xPosition, yPosition, false, true);
    }

    @Override
    public ItemStack getStack() {
        return vehicle.getInventory().get(slotIndex);
    }

    @Override
    public void putStack(ItemStack stack) {
        vehicle.getInventory().set(slotIndex, stack == null ? ItemStack.EMPTY : stack);
        onSlotChanged();
    }

    @Override
    public void onSlotChanged() {
        // Vehicle inventory is stored on the entity and saved with the entity NBT.
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
        onSlotChanged();
        return result;
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
        return canInsert.test(stack);
    }

    @Override
    public boolean canTakeStack(EntityPlayer playerIn) {
        return canTake;
    }

    @Override
    public boolean getHasStack() {
        return !getStack().isEmpty();
    }
}
