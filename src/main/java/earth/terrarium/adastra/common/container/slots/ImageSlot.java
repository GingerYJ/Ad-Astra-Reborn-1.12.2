package earth.terrarium.adastra.common.container.slots;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.util.function.Predicate;

public class ImageSlot extends PredicateSlot {

    private final ResourceLocation icon;

    public ImageSlot(IInventory inventory, int index, int xPosition, int yPosition, ResourceLocation icon, Predicate<ItemStack> predicate) {
        super(inventory, index, xPosition, yPosition, predicate);
        this.icon = icon;
    }

    public ResourceLocation getIcon() {
        return icon;
    }
}
