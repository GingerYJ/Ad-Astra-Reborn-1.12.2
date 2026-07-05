package earth.terrarium.adastra.common.menus.vehicles;

import earth.terrarium.adastra.common.container.slots.VehicleInventorySlot;
import earth.terrarium.adastra.common.entities.vehicles.LanderEntity;
import earth.terrarium.adastra.common.menus.base.BaseEntityContainerMenu;
import net.minecraft.entity.player.InventoryPlayer;

public class LanderMenu extends BaseEntityContainerMenu<LanderEntity> {

    private static final int LANDER_SLOTS = 11;

    private final LanderEntity lander;

    public LanderMenu(InventoryPlayer playerInventory, LanderEntity lander) {
        super(playerInventory, lander, LANDER_SLOTS);
        this.lander = lander;

        this.addSlotToContainer(VehicleInventorySlot.noInsert(lander, 0, 26, 27));
        this.addSlotToContainer(VehicleInventorySlot.noInsert(lander, 1, 11, 58));
        this.addSlotToContainer(VehicleInventorySlot.noInsert(lander, 2, 40, 58));

        for (int row = 0; row < 2; row++) {
            for (int col = 0; col < 4; col++) {
                int index = 3 + col + row * 4;
                this.addSlotToContainer(VehicleInventorySlot.noInsert(lander, index, 77 + col * 18, 31 + row * 18));
            }
        }

        addPlayerInvSlots();
    }

    @Override
    public int getPlayerInvXOffset() {
        return 0;
    }

    @Override
    public int getPlayerInvYOffset() {
        return 92;
    }

    public LanderEntity getLander() {
        return lander;
    }
}
