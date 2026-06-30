package earth.terrarium.adastra.common.tile;

import net.minecraft.item.Item;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityFurnace;

public class CoalGeneratorTileEntity extends AdAstraMachineTileEntity {

    private static final int FUEL_SLOT = 1;
    private static final int ENERGY_GENERATED_PER_TICK = 20;
    private static final int MAX_BURN_TIME = 20_000;

    protected int cookTime;
    protected int cookTimeTotal;

    public CoalGeneratorTileEntity() {
        super("coal_generator", 2, IRON_ENERGY, 0, IRON_IO, 0);
        setAllSideModes(SideConfigType.ENERGY, AdAstraSideMode.PUSH);
    }

    @Override
    protected void tickMachine() {
        if (energy == null || !canFunction()) {
            setLit(false);
            return;
        }

        if (energy.internalReceiveEnergy(ENERGY_GENERATED_PER_TICK, true) == 0) {
            setLit(false);
            return;
        }

        if (cookTime > 0) {
            cookTime--;
            energy.internalReceiveEnergy(ENERGY_GENERATED_PER_TICK, false);
            pushEnergyToSides();
            setLit(true);
            markDirty();
            return;
        }

        if (consumeFuel()) {
            setLit(true);
        } else {
            cookTimeTotal = 0;
            setLit(false);
        }
        pushEnergyToSides();
    }

    private boolean consumeFuel() {
        ItemStack fuel = items.getStackInSlot(FUEL_SLOT);
        if (fuel.isEmpty() || fuel.getItem() instanceof ItemBucket) {
            return false;
        }

        int burnTime = Math.min(MAX_BURN_TIME, TileEntityFurnace.getItemBurnTime(fuel));
        if (burnTime <= 0) {
            return false;
        }

        Item fuelItem = fuel.getItem();
        ItemStack container = fuelItem.getContainerItem(fuel);
        fuel.shrink(1);
        if (fuel.isEmpty()) {
            items.setStackInSlot(FUEL_SLOT, container);
        }

        cookTimeTotal = burnTime;
        cookTime = burnTime;
        markDirty();
        return true;
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return index == FUEL_SLOT && !stack.isEmpty() && !(stack.getItem() instanceof ItemBucket) && TileEntityFurnace.getItemBurnTime(stack) > 0;
    }

    @Override
    public int[] getSlotsForFace(net.minecraft.util.EnumFacing side) {
        return new int[]{FUEL_SLOT};
    }

    @Override
    public boolean canExtractItem(int index, ItemStack stack, net.minecraft.util.EnumFacing direction) {
        return false;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        cookTime = compound.getInteger("CookTime");
        cookTimeTotal = compound.getInteger("CookTimeTotal");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setInteger("CookTime", cookTime);
        compound.setInteger("CookTimeTotal", cookTimeTotal);
        return compound;
    }

    @Override
    public int getField(int id) {
        if (id == 4) {
            return cookTime;
        }
        if (id == 5) {
            return cookTimeTotal;
        }
        return super.getField(id);
    }

    @Override
    public void setField(int id, int value) {
        if (id == 4) {
            cookTime = value;
        } else if (id == 5) {
            cookTimeTotal = value;
        } else {
            super.setField(id, value);
        }
    }

    @Override
    public int getFieldCount() {
        return 6;
    }
}
