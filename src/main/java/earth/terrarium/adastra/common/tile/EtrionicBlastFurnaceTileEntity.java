package earth.terrarium.adastra.common.tile;

import earth.terrarium.adastra.common.registry.ModItems;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.items.ItemHandlerHelper;

public class EtrionicBlastFurnaceTileEntity extends AdAstraMachineTileEntity {

    private static final int FIRST_INPUT_SLOT = 1;
    private static final int LAST_INPUT_SLOT = 4;
    private static final int FIRST_OUTPUT_SLOT = 5;
    private static final int LAST_OUTPUT_SLOT = 8;
    private static final int ALLOYING_ENERGY_PER_TICK = 20;
    private static final int ALLOYING_COOK_TIME = 100;

    private FurnaceMode mode = FurnaceMode.ALLOYING;
    private int cookTime;
    private int cookTimeTotal;

    public EtrionicBlastFurnaceTileEntity() {
        super("etrionic_blast_furnace", 9, STEEL_ENERGY, STEEL_IO, 0, 0);
        setAllSideModes(SideConfigType.ENERGY, AdAstraSideMode.PULL);
    }

    @Override
    protected void tickMachine() {
        if (energy == null || !canFunction()) {
            setLit(false);
            return;
        }

        if (mode == FurnaceMode.BLASTING) {
            tickBlastingPlaceholder();
            return;
        }

        tickAlloying();
    }

    private void tickAlloying() {
        if (!hasIronIngot() || !hasCoal() || !canAddOutput(new ItemStack(ModItems.STEEL_INGOT))) {
            cookTime = 0;
            cookTimeTotal = 0;
            setLit(false);
            return;
        }

        if (energy.extractEnergy(ALLOYING_ENERGY_PER_TICK, true) < ALLOYING_ENERGY_PER_TICK) {
            setLit(false);
            return;
        }

        cookTimeTotal = ALLOYING_COOK_TIME;
        energy.extractEnergy(ALLOYING_ENERGY_PER_TICK, false);
        cookTime++;
        setLit(true);

        if (cookTime >= cookTimeTotal) {
            craftSteelIngot();
            cookTime = 0;
        }
        markDirty();
    }

    private void tickBlastingPlaceholder() {
        cookTime = 0;
        cookTimeTotal = 0;
        setLit(false);
    }

    private boolean hasIronIngot() {
        for (int slot = FIRST_INPUT_SLOT; slot <= LAST_INPUT_SLOT; slot++) {
            ItemStack stack = items.getStackInSlot(slot);
            if (!stack.isEmpty() && stack.getItem() == Items.IRON_INGOT) {
                return true;
            }
        }
        return false;
    }

    private boolean hasCoal() {
        for (int slot = FIRST_INPUT_SLOT; slot <= LAST_INPUT_SLOT; slot++) {
            ItemStack stack = items.getStackInSlot(slot);
            if (!stack.isEmpty() && stack.getItem() == Items.COAL) {
                return true;
            }
        }
        return false;
    }

    private void craftSteelIngot() {
        consumeOne(stack -> stack.getItem() == Items.IRON_INGOT);
        consumeOne(stack -> stack.getItem() == Items.COAL);
        addOutput(new ItemStack(ModItems.STEEL_INGOT));
    }

    private void consumeOne(StackPredicate predicate) {
        for (int slot = FIRST_INPUT_SLOT; slot <= LAST_INPUT_SLOT; slot++) {
            ItemStack stack = items.getStackInSlot(slot);
            if (!stack.isEmpty() && predicate.test(stack)) {
                stack.shrink(1);
                if (stack.isEmpty()) {
                    items.setStackInSlot(slot, ItemStack.EMPTY);
                }
                return;
            }
        }
    }

    private boolean canAddOutput(ItemStack result) {
        for (int slot = FIRST_OUTPUT_SLOT; slot <= LAST_OUTPUT_SLOT; slot++) {
            ItemStack output = items.getStackInSlot(slot);
            if (output.isEmpty()) {
                return true;
            }
            if (ItemHandlerHelper.canItemStacksStack(output, result)
                && output.getCount() + result.getCount() <= Math.min(output.getMaxStackSize(), items.getSlotLimit(slot))) {
                return true;
            }
        }
        return false;
    }

    private void addOutput(ItemStack result) {
        for (int slot = FIRST_OUTPUT_SLOT; slot <= LAST_OUTPUT_SLOT; slot++) {
            ItemStack output = items.getStackInSlot(slot);
            if (output.isEmpty()) {
                items.setStackInSlot(slot, result.copy());
                return;
            }
            if (ItemHandlerHelper.canItemStacksStack(output, result)
                && output.getCount() + result.getCount() <= Math.min(output.getMaxStackSize(), items.getSlotLimit(slot))) {
                output.grow(result.getCount());
                return;
            }
        }
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        if (stack.isEmpty() || index < FIRST_INPUT_SLOT || index > LAST_INPUT_SLOT) {
            return false;
        }
        return stack.getItem() == Items.IRON_INGOT || stack.getItem() == Items.COAL;
    }

    @Override
    public int[] getSlotsForFace(EnumFacing side) {
        return new int[]{1, 2, 3, 4, 5, 6, 7, 8};
    }

    @Override
    public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
        return index >= FIRST_INPUT_SLOT && index <= LAST_INPUT_SLOT && isItemValidForSlot(index, itemStackIn);
    }

    @Override
    public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
        return index >= FIRST_OUTPUT_SLOT && index <= LAST_OUTPUT_SLOT;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        cookTime = compound.getInteger("CookTime");
        cookTimeTotal = compound.getInteger("CookTimeTotal");
        mode = FurnaceMode.byOrdinal(compound.getByte("Mode"));
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setInteger("CookTime", cookTime);
        compound.setInteger("CookTimeTotal", cookTimeTotal);
        compound.setByte("Mode", (byte) mode.ordinal());
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
        if (id == 6) {
            return mode.ordinal();
        }
        return super.getField(id);
    }

    @Override
    public void setField(int id, int value) {
        if (id == 4) {
            cookTime = value;
        } else if (id == 5) {
            cookTimeTotal = value;
        } else if (id == 6) {
            mode = FurnaceMode.byOrdinal(value);
        } else {
            super.setField(id, value);
        }
    }

    @Override
    public int getFieldCount() {
        return 7;
    }

    public FurnaceMode getMode() {
        return mode;
    }

    public void setMode(FurnaceMode mode) {
        this.mode = mode;
        cookTime = 0;
        cookTimeTotal = 0;
        markDirty();
    }

    public enum FurnaceMode {
        ALLOYING,
        BLASTING;

        public FurnaceMode next() {
            FurnaceMode[] values = values();
            return values[(ordinal() + 1) % values.length];
        }

        public FurnaceMode previous() {
            FurnaceMode[] values = values();
            return values[(ordinal() - 1 + values.length) % values.length];
        }

        public static FurnaceMode byOrdinal(int ordinal) {
            FurnaceMode[] values = values();
            return ordinal >= 0 && ordinal < values.length ? values[ordinal] : ALLOYING;
        }
    }

    private interface StackPredicate {
        boolean test(ItemStack stack);
    }
}
