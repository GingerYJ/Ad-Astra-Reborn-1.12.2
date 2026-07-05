package earth.terrarium.adastra.common.menus.machines;

import earth.terrarium.adastra.common.container.AdAstraMachineContainer;
import earth.terrarium.adastra.common.container.slots.PredicateSlot;
import earth.terrarium.adastra.common.menus.base.MachineMenu;
import earth.terrarium.adastra.common.registry.ModGuiIds;
import earth.terrarium.adastra.common.tile.OxygenLoaderTileEntity;
import net.minecraft.entity.player.InventoryPlayer;

public class OxygenLoaderMenu extends MachineMenu<OxygenLoaderTileEntity> {

    private static final int MACHINE_SLOT_COUNT = 5;

    public OxygenLoaderMenu(InventoryPlayer inventory, OxygenLoaderTileEntity entity) {
        super(inventory, entity, AdAstraMachineContainer.layoutFor(ModGuiIds.OXYGEN_LOADER, entity));
        addSlotToContainer(createBatterySlot(entity, 0, 154, -25));
        addSlotToContainer(new PredicateSlot(entity, 1, 12, 22, stack -> entity.isItemValidForSlot(1, stack)));
        addSlotToContainer(new PredicateSlot(entity, 2, 12, 52, stack -> false));
        addSlotToContainer(new PredicateSlot(entity, 3, 127, 22, stack -> entity.isItemValidForSlot(3, stack)));
        addSlotToContainer(new PredicateSlot(entity, 4, 127, 52, stack -> false));
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
        return 102;
    }
}
