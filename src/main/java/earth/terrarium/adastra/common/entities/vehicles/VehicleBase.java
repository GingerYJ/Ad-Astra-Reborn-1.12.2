package earth.terrarium.adastra.common.entities.vehicles;

import earth.terrarium.adastra.common.container.AdAstraFluidTank;
import earth.terrarium.adastra.common.registry.ModFluids;
import earth.terrarium.adastra.common.util.RocketFuelHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;
import java.util.function.Predicate;

/**
 * Extended vehicle base with fuel tanks and inventory systems.
 * Provides abstraction for rockets, rovers, and landers.
 */
public abstract class VehicleBase extends AdAstraVehicleEntity {

    protected AdAstraFluidTank fuelTank;
    protected NonNullList<ItemStack> inventory;
    protected final int inventorySize;
    protected final int fuelCapacity;

    // Launch control state
    protected boolean isLaunching = false;
    protected int launchCountdown = -1;
    protected boolean hasLaunched = false;

    public VehicleBase(World world, VehicleType vehicleType, int maxFuel, int rocketTier,
                      int inventorySize, int fuelCapacity, @Nullable Predicate<FluidStack> fuelFilter) {
        super(world, vehicleType, maxFuel, rocketTier);
        this.inventorySize = inventorySize;
        this.fuelCapacity = fuelCapacity;

        // Initialize fluid tank for liquid fuel
        if (fuelCapacity > 0) {
            this.fuelTank = new AdAstraFluidTank(fuelCapacity, fuelFilter);
        }

        // Initialize inventory
        this.inventory = NonNullList.withSize(inventorySize, ItemStack.EMPTY);
    }

    @Override
    public boolean processInitialInteract(EntityPlayer player, EnumHand hand) {
        if (fuelTank != null) {
            if (getVehicleType() == VehicleType.ROCKET
                && RocketFuelHelper.handleRocketFuelInteraction(fuelTank, player, hand, getRocketTier())) {
                return true;
            }
            if (getVehicleType() != VehicleType.ROCKET && RocketFuelHelper.fillTankFromHeldItem(fuelTank, player, hand)) {
                return true;
            }
        }
        if (player.isSneaking() && !world.isRemote) {
            // Open inventory GUI when sneaking
            openInventoryGUI(player);
            return true;
        }
        return super.processInitialInteract(player, hand);
    }

    /**
 * Open the vehicle's inventory GUI for the player.
     * Should be overridden by subclasses to open specific GUI.
     */
    protected abstract void openInventoryGUI(EntityPlayer player);

    @Override
    protected void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);

        // Load inventory
        if (compound.hasKey("Inventory", Constants.NBT.TAG_LIST)) {
            NBTTagList list = compound.getTagList("Inventory", Constants.NBT.TAG_COMPOUND);
            inventory = NonNullList.withSize(inventorySize, ItemStack.EMPTY);
            for (int i = 0; i < list.tagCount(); i++) {
                NBTTagCompound itemTag = list.getCompoundTagAt(i);
                int slot = itemTag.getByte("Slot") & 255;
                if (slot >= 0 && slot < inventory.size()) {
                    inventory.set(slot, new ItemStack(itemTag));
                }
            }
        }

        // Load fluid tank
        if (fuelTank != null && compound.hasKey("FuelTank", Constants.NBT.TAG_COMPOUND)) {
            fuelTank.deserializeNBT(compound.getCompoundTag("FuelTank"));
        }

        // Load launch state
        isLaunching = compound.getBoolean("IsLaunching");
        launchCountdown = compound.getInteger("LaunchCountdown");
        hasLaunched = compound.getBoolean("HasLaunched");
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);

        // Save inventory
        NBTTagList list = new NBTTagList();
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.get(i);
            if (!stack.isEmpty()) {
                NBTTagCompound itemTag = new NBTTagCompound();
                itemTag.setByte("Slot", (byte) i);
                stack.writeToNBT(itemTag);
                list.appendTag(itemTag);
            }
        }
        compound.setTag("Inventory", list);

        // Save fluid tank
        if (fuelTank != null) {
            compound.setTag("FuelTank", fuelTank.serializeNBT());
        }

        // Save launch state
        compound.setBoolean("IsLaunching", isLaunching);
        compound.setInteger("LaunchCountdown", launchCountdown);
        compound.setBoolean("HasLaunched", hasLaunched);
    }

    /**
     * Get the vehicle's inventory.
     */
    public NonNullList<ItemStack> getInventory() {
        return inventory;
    }

    /**
     * Get the vehicle's fuel tank.
     */
    @Nullable
    public AdAstraFluidTank getFuelTank() {
        return fuelTank;
    }

    /**
     * Get the current fuel amount in millibuckets.
     */
    public int getFluidFuelAmount() {
        return fuelTank != null ? fuelTank.getFluidAmount() : 0;
    }

    /**
     * Get the fuel tank capacity in millibuckets.
     */
    public int getFluidFuelCapacity() {
        return fuelCapacity;
    }

    public void setClientFluidFuelAmount(int amount) {
        if (fuelTank == null) {
            return;
        }
        if (amount <= 0) {
            fuelTank.clear();
            return;
        }
        FluidStack current = fuelTank.getFluid();
        FluidStack synced = current == null ? new FluidStack(ModFluids.FUEL, amount) : current.copy();
        synced.amount = Math.min(amount, fuelCapacity);
        fuelTank.setFluidDirect(synced);
    }

    /**
     * Check if the vehicle has enough fuel for an operation.
     */
    public boolean hasEnoughFuel(int requiredAmount) {
        if (fuelTank == null) {
            return getFuel() > 0; // Fallback to integer fuel
        }
        return fuelTank.getFluidAmount() >= requiredAmount;
    }

    /**
     * Consume fuel from the tank.
     * @param amount Amount in millibuckets to consume
     * @return true if fuel was consumed, false if not enough fuel
     */
    public boolean consumeFluidFuel(int amount) {
        if (fuelTank == null || fuelTank.getFluidAmount() < amount) {
            return false;
        }
        FluidStack drained = fuelTank.drain(amount, true);
        return drained != null && drained.amount >= amount;
    }

    /**
     * Drop all inventory contents when vehicle is destroyed.
     */
    @Override
    public void setDead() {
        if (!world.isRemote) {
            dropInventory();
        }
        super.setDead();
    }

    /**
     * Drop all inventory items at the vehicle's position.
     */
    protected void dropInventory() {
        for (ItemStack stack : inventory) {
            if (!stack.isEmpty()) {
                entityDropItem(stack, 0.0F);
            }
        }
    }

    /**
     * Get the inventory size.
     */
    public int getInventorySize() {
        return inventorySize;
    }

    /**
     * Check if the vehicle is currently launching.
     */
    public boolean isLaunching() {
        return isLaunching;
    }

    /**
     * Get the current launch countdown.
     */
    public int getLaunchCountdown() {
        return launchCountdown;
    }

    /**
     * Check if the vehicle has completed launch.
     */
    public boolean hasLaunched() {
        return hasLaunched;
    }
}
