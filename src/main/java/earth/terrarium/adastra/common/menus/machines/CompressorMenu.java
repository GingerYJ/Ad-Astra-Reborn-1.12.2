package earth.terrarium.adastra.common.menus.machines;

import earth.terrarium.adastra.common.container.AdAstraMachineContainer;
import earth.terrarium.adastra.common.container.slots.PredicateSlot;
import earth.terrarium.adastra.common.menus.base.MachineMenu;
import earth.terrarium.adastra.common.registry.ModGuiIds;
import earth.terrarium.adastra.common.tile.CompressorTileEntity;
import net.minecraft.entity.player.InventoryPlayer;

public class CompressorMenu extends MachineMenu<CompressorTileEntity> {

    private static final int MACHINE_SLOT_COUNT = 3;

    public CompressorMenu(InventoryPlayer inventory, CompressorTileEntity entity) {
        super(inventory, entity, AdAstraMachineContainer.layoutFor(ModGuiIds.COMPRESSOR, entity));
        addSlotToContainer(createBatterySlot(entity, 0, 161, -25));
        addSlotToContainer(new PredicateSlot(entity, 1, 47, 58, stack -> entity.isItemValidForSlot(1, stack)));
        addSlotToContainer(new PredicateSlot(entity, 2, 95, 58, stack -> false));
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
