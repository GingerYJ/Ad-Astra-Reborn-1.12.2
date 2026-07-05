package earth.terrarium.adastra.common.menus.machines;

import earth.terrarium.adastra.common.container.AdAstraMachineContainer;
import earth.terrarium.adastra.common.menus.base.MachineMenu;
import earth.terrarium.adastra.common.registry.ModGuiIds;
import earth.terrarium.adastra.common.tile.SolarPanelTileEntity;
import net.minecraft.entity.player.InventoryPlayer;

public class SolarPanelMenu extends MachineMenu<SolarPanelTileEntity> {

    private static final int BATTERY_SLOT = 0;
    private static final int MACHINE_SLOT_COUNT = 1;

    public SolarPanelMenu(InventoryPlayer inventory, SolarPanelTileEntity entity) {
        super(inventory, entity, AdAstraMachineContainer.layoutFor(ModGuiIds.SOLAR_PANEL, entity));
        addSlotToContainer(createBatterySlot(entity, BATTERY_SLOT, 154, -25));
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
        return 148;
    }
}
