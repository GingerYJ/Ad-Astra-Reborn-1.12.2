package earth.terrarium.adastra.common.menus.vehicles;

import earth.terrarium.adastra.common.container.slots.VehicleInventorySlot;
import earth.terrarium.adastra.common.entities.vehicles.RoverEntity;
import earth.terrarium.adastra.common.menus.base.BaseEntityContainerMenu;
import earth.terrarium.adastra.common.util.RocketFuelHelper;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IContainerListener;

/**
 * Menu for Rover inventory GUI.
 */
public class RoverMenu extends BaseEntityContainerMenu<RoverEntity> {

    private static final int ROVER_SLOTS = 18;
    private static final int FUEL_INPUT_SLOT = 0;
    private static final int FUEL_OUTPUT_SLOT = 1;

    private final RoverEntity rover;

    public RoverMenu(InventoryPlayer playerInventory, RoverEntity rover) {
        super(playerInventory, rover, ROVER_SLOTS);
        this.rover = rover;

        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 4; col++) {
                int index = 2 + col + row * 4;
                this.addSlotToContainer(new VehicleInventorySlot(rover, index, 78 + col * 18, 16 + row * 18));
            }
        }

        this.addSlotToContainer(new VehicleInventorySlot(rover, FUEL_INPUT_SLOT, 12, 26,
            stack -> RocketFuelHelper.canDrainFuelContainer(rover.getFuelTank(), stack), true));
        this.addSlotToContainer(VehicleInventorySlot.noInsert(rover, FUEL_OUTPUT_SLOT, 12, 56));

        addPlayerInvSlots();
    }

    @Override
    public int getPlayerInvXOffset() {
        return 0;
    }

    @Override
    public int getPlayerInvYOffset() {
        return 99;
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        if (!rover.world.isRemote) {
            RocketFuelHelper.moveFuelItemToTank(rover.getFuelTank(), rover.getInventory(), FUEL_INPUT_SLOT, FUEL_OUTPUT_SLOT);
        }
        for (IContainerListener listener : this.listeners) {
            listener.sendWindowProperty(this, 0, rover.getFluidFuelAmount());
        }
    }

    @Override
    public void updateProgressBar(int id, int data) {
        if (id == 0) {
            rover.setClientFluidFuelAmount(data);
        }
    }

    public RoverEntity getRover() {
        return rover;
    }
}
