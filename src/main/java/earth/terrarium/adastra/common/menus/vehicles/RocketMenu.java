package earth.terrarium.adastra.common.menus.vehicles;

import earth.terrarium.adastra.common.container.slots.VehicleInventorySlot;
import earth.terrarium.adastra.common.entities.vehicles.RocketEntity;
import earth.terrarium.adastra.common.menus.base.BaseEntityContainerMenu;
import earth.terrarium.adastra.common.util.RocketFuelHelper;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IContainerListener;

/**
 * Menu for Rocket inventory GUI.
 * Provides fuel input/output slots and 8 storage slots for rocket cargo.
 */
public class RocketMenu extends BaseEntityContainerMenu<RocketEntity> {

    private static final int ROCKET_SLOTS = 10;
    private static final int FUEL_INPUT_SLOT = 0;
    private static final int FUEL_OUTPUT_SLOT = 1;

    private final RocketEntity rocket;

    public RocketMenu(InventoryPlayer playerInventory, RocketEntity rocket) {
        super(playerInventory, rocket, ROCKET_SLOTS);
        this.rocket = rocket;

        this.addSlotToContainer(new VehicleInventorySlot(rocket, FUEL_INPUT_SLOT, 12, 24,
            stack -> RocketFuelHelper.canDrainFuelContainer(rocket.getFuelTank(), stack), true));
        this.addSlotToContainer(VehicleInventorySlot.noInsert(rocket, FUEL_OUTPUT_SLOT, 12, 54));

        for (int row = 0; row < 2; row++) {
            for (int col = 0; col < 4; col++) {
                int index = 2 + col + row * 4;
                this.addSlotToContainer(new VehicleInventorySlot(rocket, index, 78 + col * 18, 31 + row * 18));
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

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        if (!rocket.world.isRemote) {
            RocketFuelHelper.moveFuelItemToTank(rocket.getFuelTank(), rocket.getInventory(), FUEL_INPUT_SLOT, FUEL_OUTPUT_SLOT);
        }
        for (IContainerListener listener : this.listeners) {
            listener.sendWindowProperty(this, 0, rocket.getFluidFuelAmount());
        }
    }

    @Override
    public void updateProgressBar(int id, int data) {
        if (id == 0) {
            rocket.setClientFluidFuelAmount(data);
        }
    }

    public RocketEntity getRocket() {
        return rocket;
    }
}
