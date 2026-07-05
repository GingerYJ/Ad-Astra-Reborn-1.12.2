package earth.terrarium.adastra.common.container;

import earth.terrarium.adastra.common.container.slots.VehicleInventorySlot;
import earth.terrarium.adastra.common.entities.vehicles.LanderEntity;
import net.minecraft.entity.player.InventoryPlayer;

/**
 * Container for Lander inventory GUI.
 * Provides 11 retrieval slots for landed rocket cargo.
 */
public class LanderContainer extends BaseVehicleContainer<LanderEntity> {

    private final LanderEntity lander;
    private static final int LANDER_SLOTS = 11;

    public LanderContainer(InventoryPlayer playerInventory, LanderEntity lander) {
        super(lander, LANDER_SLOTS);
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

        addPlayerInventory(playerInventory, 0, 92);
    }

    public LanderEntity getLander() {
        return lander;
    }
}
