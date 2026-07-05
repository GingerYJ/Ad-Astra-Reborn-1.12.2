package earth.terrarium.adastra.common.menus.machines;

import earth.terrarium.adastra.common.container.AdAstraMachineContainer;
import earth.terrarium.adastra.common.container.slots.PredicateSlot;
import earth.terrarium.adastra.common.menus.base.MachineMenu;
import earth.terrarium.adastra.common.registry.ModGuiIds;
import earth.terrarium.adastra.common.tile.CryoFreezerTileEntity;
import net.minecraft.entity.player.InventoryPlayer;

public class CryoFreezerMenu extends MachineMenu<CryoFreezerTileEntity> {

    private static final int MACHINE_SLOT_COUNT = 4;

    public CryoFreezerMenu(InventoryPlayer inventory, CryoFreezerTileEntity entity) {
        super(inventory, entity, AdAstraMachineContainer.layoutFor(ModGuiIds.CRYO_FREEZER, entity));
        addSlotToContainer(createBatterySlot(entity, 0, 154, -25));
        addSlotToContainer(new PredicateSlot(entity, 1, 26, 70, stack -> entity.isItemValidForSlot(1, stack)));
        addSlotToContainer(new PredicateSlot(entity, 2, 113, 42, stack -> entity.isItemValidForSlot(2, stack)));
        addSlotToContainer(new PredicateSlot(entity, 3, 113, 70, stack -> false));
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
    protected int startIndex() {
        return START_INDEX;
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
