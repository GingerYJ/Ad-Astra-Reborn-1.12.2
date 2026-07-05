package earth.terrarium.adastra.common.menus.machines;

import earth.terrarium.adastra.common.container.AdAstraMachineContainer;
import earth.terrarium.adastra.common.container.slots.PredicateSlot;
import earth.terrarium.adastra.common.menus.base.MachineMenu;
import earth.terrarium.adastra.common.registry.ModGuiIds;
import earth.terrarium.adastra.common.tile.EtrionicBlastFurnaceTileEntity;
import net.minecraft.entity.player.InventoryPlayer;

public class EtrionicBlastFurnaceMenu extends MachineMenu<EtrionicBlastFurnaceTileEntity> {

    private static final int MACHINE_SLOT_COUNT = 9;

    public EtrionicBlastFurnaceMenu(InventoryPlayer inventory, EtrionicBlastFurnaceTileEntity entity) {
        super(inventory, entity, AdAstraMachineContainer.layoutFor(ModGuiIds.ETRIONIC_BLAST_FURNACE, entity));
        addSlotToContainer(createBatterySlot(entity, 0, 161, -25));
        addSlotToContainer(new PredicateSlot(entity, 1, 29, 38, stack -> entity.isItemValidForSlot(1, stack)));
        addSlotToContainer(new PredicateSlot(entity, 2, 47, 38, stack -> entity.isItemValidForSlot(2, stack)));
        addSlotToContainer(new PredicateSlot(entity, 3, 29, 58, stack -> entity.isItemValidForSlot(3, stack)));
        addSlotToContainer(new PredicateSlot(entity, 4, 47, 58, stack -> entity.isItemValidForSlot(4, stack)));
        addSlotToContainer(new PredicateSlot(entity, 5, 101, 38, stack -> false));
        addSlotToContainer(new PredicateSlot(entity, 6, 119, 38, stack -> false));
        addSlotToContainer(new PredicateSlot(entity, 7, 101, 58, stack -> false));
        addSlotToContainer(new PredicateSlot(entity, 8, 119, 58, stack -> false));
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
        return 12;
    }

    @Override
    public int getPlayerInvYOffset() {
        return 114;
    }
}
