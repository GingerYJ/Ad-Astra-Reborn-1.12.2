package earth.terrarium.adastra.common.container.slots;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;

public class InventorySlot extends Slot {

    private boolean enabled = true;

    public InventorySlot(IInventory inventory, int index, int xPosition, int yPosition) {
        super(inventory, index, xPosition, yPosition);
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
