package earth.terrarium.adastra.common.menus.base;

import earth.terrarium.adastra.common.container.AdAstraMachineContainer;
import earth.terrarium.adastra.common.container.slots.BatterySlot;
import earth.terrarium.adastra.common.tile.AdAstraMachineTileEntity;
import earth.terrarium.adastra.common.tile.EtrionicBlastFurnaceTileEntity;
import earth.terrarium.adastra.common.tile.GravityNormalizerTileEntity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;

public abstract class MachineMenu<T extends AdAstraMachineTileEntity> extends BaseConfigurableContainerMenu<T> {

    public static final int TOGGLE_FURNACE_MODE = 0;
    public static final int PREVIOUS_FURNACE_MODE = 1;
    public static final int GRAVITY_ID_BASE = 1000;

    private final AdAstraMachineContainer.Layout layout;
    private Slot batterySlot;
    private int[] cachedFields;

    protected MachineMenu(InventoryPlayer inventory, T entity, AdAstraMachineContainer.Layout layout) {
        super(inventory, entity);
        this.layout = layout;
        this.cachedFields = new int[entity.getFieldCount()];
    }

    public T getMachine() {
        return getEntity();
    }

    public AdAstraMachineContainer.Layout getLayout() {
        return layout;
    }

    public int getMachineSlotCount() {
        return getInventoryStart();
    }

    public int getSyncedField(int id) {
        if (id >= 0 && id < cachedFields.length) {
            return cachedFields[id];
        }
        return entity.getField(id);
    }

    public Slot getBatterySlot() {
        return batterySlot;
    }

    protected Slot createBatterySlot(T machine, int index, int xPosition, int yPosition) {
        batterySlot = new BatterySlot(machine, index, xPosition, yPosition);
        return batterySlot;
    }

    @Override
    public boolean enchantItem(EntityPlayer playerIn, int id) {
        if (!canInteractWith(playerIn)) {
            return false;
        }
        if ((id == TOGGLE_FURNACE_MODE || id == PREVIOUS_FURNACE_MODE) && entity instanceof EtrionicBlastFurnaceTileEntity) {
            EtrionicBlastFurnaceTileEntity furnace = (EtrionicBlastFurnaceTileEntity) entity;
            furnace.setMode(id == PREVIOUS_FURNACE_MODE ? furnace.getMode().previous() : furnace.getMode().next());
            furnace.markDirty();
            detectAndSendChanges();
            return true;
        }
        if (id >= GRAVITY_ID_BASE && id <= GRAVITY_ID_BASE + 200 && entity instanceof GravityNormalizerTileEntity) {
            GravityNormalizerTileEntity normalizer = (GravityNormalizerTileEntity) entity;
            normalizer.setTargetGravity((id - GRAVITY_ID_BASE) / 100.0f);
            normalizer.markDirty();
            detectAndSendChanges();
            return true;
        }
        return false;
    }

    @Override
    public void addListener(IContainerListener listener) {
        super.addListener(listener);
        for (int i = 0; i < entity.getFieldCount(); i++) {
            listener.sendWindowProperty(this, i, entity.getField(i));
            cachedFields[i] = entity.getField(i);
        }
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        for (int i = 0; i < entity.getFieldCount(); i++) {
            int value = entity.getField(i);
            if (i >= cachedFields.length || cachedFields[i] != value) {
                for (IContainerListener listener : listeners) {
                    listener.sendWindowProperty(this, i, value);
                }
                if (i < cachedFields.length) {
                    cachedFields[i] = value;
                }
            }
        }
    }

    @Override
    public void updateProgressBar(int id, int data) {
        if (id >= 0 && id < cachedFields.length) {
            cachedFields[id] = data;
        }
        entity.setField(id, data);
    }
}
