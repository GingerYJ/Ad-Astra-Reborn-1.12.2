package earth.terrarium.adastra.common.menus.machines;

import earth.terrarium.adastra.common.container.AdAstraMachineContainer;
import earth.terrarium.adastra.common.container.slots.PredicateSlot;
import earth.terrarium.adastra.common.menus.base.MachineMenu;
import earth.terrarium.adastra.common.registry.ModGuiIds;
import earth.terrarium.adastra.common.tile.OxygenDistributorTileEntity;
import net.minecraft.entity.player.InventoryPlayer;

public class OxygenDistributorMenu extends MachineMenu<OxygenDistributorTileEntity> {

    private static final int MACHINE_SLOT_COUNT = 3;

    public OxygenDistributorMenu(InventoryPlayer inventory, OxygenDistributorTileEntity entity) {
        super(inventory, entity, AdAstraMachineContainer.layoutFor(ModGuiIds.OXYGEN_DISTRIBUTOR, entity));
        addSlotToContainer(createBatterySlot(entity, 0, 168, 7));
        addSlotToContainer(new PredicateSlot(entity, 1, 17, 82, stack -> entity.isItemValidForSlot(1, stack)));
        addSlotToContainer(new PredicateSlot(entity, 2, 17, 112, stack -> false));
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
        return 162;
    }
}
