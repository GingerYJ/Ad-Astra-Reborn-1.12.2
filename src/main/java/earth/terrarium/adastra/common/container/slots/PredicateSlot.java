package earth.terrarium.adastra.common.container.slots;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import java.util.function.Predicate;

public class PredicateSlot extends Slot {

    private final Predicate<ItemStack> predicate;

    public PredicateSlot(IInventory inventory, int index, int xPosition, int yPosition, Predicate<ItemStack> predicate) {
        super(inventory, index, xPosition, yPosition);
        this.predicate = predicate;
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
        return predicate.test(stack);
    }
}
