package earth.terrarium.adastra.common.menus.machines;

import earth.terrarium.adastra.common.container.AdAstraMachineContainer;
import earth.terrarium.adastra.common.menus.base.MachineMenu;
import earth.terrarium.adastra.common.registry.ModGuiIds;
import earth.terrarium.adastra.common.tile.GravityNormalizerTileEntity;
import net.minecraft.entity.player.InventoryPlayer;

public class GravityNormalizerMenu extends MachineMenu<GravityNormalizerTileEntity> {

    private static final int BATTERY_SLOT = 0;
    private static final int MACHINE_SLOT_COUNT = 1;

    public GravityNormalizerMenu(InventoryPlayer inventory, GravityNormalizerTileEntity entity) {
        super(inventory, entity, AdAstraMachineContainer.layoutFor(ModGuiIds.GRAVITY_NORMALIZER, entity));
        addSlotToContainer(createBatterySlot(entity, BATTERY_SLOT, 161, -25));
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
        return 12;
    }

    @Override
    public int getPlayerInvYOffset() {
        return 130;
    }
}
