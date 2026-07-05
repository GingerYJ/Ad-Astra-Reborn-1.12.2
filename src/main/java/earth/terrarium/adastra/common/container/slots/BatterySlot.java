package earth.terrarium.adastra.common.container.slots;

import earth.terrarium.adastra.Reference;
import earth.terrarium.adastra.common.tile.AdAstraMachineTileEntity;
import net.minecraft.util.ResourceLocation;

public class BatterySlot extends ImageSlot {

    private static final ResourceLocation BATTERY_SLOT_ICON = new ResourceLocation(Reference.MOD_ID, "textures/item/icons/battery_slot_icon.png");

    public BatterySlot(AdAstraMachineTileEntity machine, int index, int xPosition, int yPosition) {
        super(machine, index, xPosition, yPosition, BATTERY_SLOT_ICON, stack -> machine.isItemValidForSlot(index, stack));
    }
}
