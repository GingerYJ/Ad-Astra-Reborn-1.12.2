package earth.terrarium.adastra.common.menus.machines;

import earth.terrarium.adastra.common.container.AdAstraMachineContainer;
import earth.terrarium.adastra.common.container.slots.PredicateSlot;
import earth.terrarium.adastra.common.menus.base.MachineMenu;
import earth.terrarium.adastra.common.registry.ModGuiIds;
import earth.terrarium.adastra.common.tile.CoalGeneratorTileEntity;
import net.minecraft.entity.player.InventoryPlayer;

public class CoalGeneratorMenu extends MachineMenu<CoalGeneratorTileEntity> {

    private static final int FUEL_SLOT = 1;
    private static final int MACHINE_SLOT_COUNT = 1;

    public CoalGeneratorMenu(InventoryPlayer inventory, CoalGeneratorTileEntity entity) {
        super(inventory, entity, AdAstraMachineContainer.layoutFor(ModGuiIds.COAL_GENERATOR, entity));
        addSlotToContainer(new PredicateSlot(entity, FUEL_SLOT, 77, 71, stack -> entity.isItemValidForSlot(FUEL_SLOT, stack)));
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
        return 107;
    }
}
