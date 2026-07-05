package earth.terrarium.adastra.common.menus.machines;

import earth.terrarium.adastra.common.container.AdAstraMachineContainer;
import earth.terrarium.adastra.common.container.slots.NasaWorkbenchOutputSlot;
import earth.terrarium.adastra.common.container.slots.PredicateSlot;
import earth.terrarium.adastra.common.menus.base.MachineMenu;
import earth.terrarium.adastra.common.registry.ModGuiIds;
import earth.terrarium.adastra.common.tile.NasaWorkbenchTileEntity;
import net.minecraft.entity.player.InventoryPlayer;

public class NasaWorkbenchMenu extends MachineMenu<NasaWorkbenchTileEntity> {

    private static final int MACHINE_SLOT_COUNT = 15;

    public NasaWorkbenchMenu(InventoryPlayer inventory, NasaWorkbenchTileEntity entity) {
        super(inventory, entity, AdAstraMachineContainer.layoutFor(ModGuiIds.NASA_WORKBENCH, entity));
        addSlotToContainer(new PredicateSlot(entity, 0, 56, 20, stack -> entity.isItemValidForSlot(0, stack)));
        addSlotToContainer(new PredicateSlot(entity, 1, 47, 38, stack -> entity.isItemValidForSlot(1, stack)));
        addSlotToContainer(new PredicateSlot(entity, 2, 65, 38, stack -> entity.isItemValidForSlot(2, stack)));
        addSlotToContainer(new PredicateSlot(entity, 3, 47, 56, stack -> entity.isItemValidForSlot(3, stack)));
        addSlotToContainer(new PredicateSlot(entity, 4, 65, 56, stack -> entity.isItemValidForSlot(4, stack)));
        addSlotToContainer(new PredicateSlot(entity, 5, 47, 74, stack -> entity.isItemValidForSlot(5, stack)));
        addSlotToContainer(new PredicateSlot(entity, 6, 65, 74, stack -> entity.isItemValidForSlot(6, stack)));
        addSlotToContainer(new PredicateSlot(entity, 7, 29, 92, stack -> entity.isItemValidForSlot(7, stack)));
        addSlotToContainer(new PredicateSlot(entity, 8, 47, 92, stack -> entity.isItemValidForSlot(8, stack)));
        addSlotToContainer(new PredicateSlot(entity, 9, 65, 92, stack -> entity.isItemValidForSlot(9, stack)));
        addSlotToContainer(new PredicateSlot(entity, 10, 83, 92, stack -> entity.isItemValidForSlot(10, stack)));
        addSlotToContainer(new PredicateSlot(entity, 11, 29, 110, stack -> entity.isItemValidForSlot(11, stack)));
        addSlotToContainer(new PredicateSlot(entity, 12, 56, 110, stack -> entity.isItemValidForSlot(12, stack)));
        addSlotToContainer(new PredicateSlot(entity, 13, 83, 110, stack -> entity.isItemValidForSlot(13, stack)));
        addSlotToContainer(new NasaWorkbenchOutputSlot(entity, 14, 129, 56));
        addPlayerInvSlots();
    }

    @Override
    protected int getContainerInputEnd() {
        return MACHINE_SLOT_COUNT;
    }

    @Override
    protected int getInventoryStart() {
        return MACHINE_SLOT_COUNT;
    }

    @Override
    public int getPlayerInvXOffset() {
        return 8;
    }

    @Override
    public int getPlayerInvYOffset() {
        return 142;
    }

}
