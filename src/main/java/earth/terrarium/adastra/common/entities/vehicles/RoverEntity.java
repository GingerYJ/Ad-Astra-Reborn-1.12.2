package earth.terrarium.adastra.common.entities.vehicles;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Rover entity with dual-passenger support, 18-slot inventory, and fuel consumption.
 */
public class RoverEntity extends VehicleBase {

    private static final int ROVER_INVENTORY_SIZE = 18;
    private static final int ROVER_FUEL_CAPACITY = 2000; // 2 buckets
    private static final int FUEL_PER_MOVEMENT = 1; // Fuel consumed per movement tick

    private float acceleration = 0.0f;
    private float steering = 0.0f;
    private int fuelConsumptionTick = 0;

    public RoverEntity(World world) {
        super(world, VehicleType.ROVER, 0, 0, ROVER_INVENTORY_SIZE, ROVER_FUEL_CAPACITY, stack -> {
            // Accept fuel for rovers
            if (stack == null) return false;
            return stack.getFluid().getName().contains("fuel") ||
                   stack.getFluid().getName().contains("gasoline");
        });
        setSize(2.2f, 0.9f);
    }

    @Override
    protected void openInventoryGUI(EntityPlayer player) {
        if (!world.isRemote && player instanceof EntityPlayerMP) {
            player.openGui(earth.terrarium.adastra.AdAstraReborn.instance,
                earth.terrarium.adastra.ModGuiHandler.ROVER_GUI,
                world, getEntityId(), 0, 0);
        }
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        // Handle rover movement
        if (!world.isRemote) {
            handleRoverMovement();
        }
    }

    /**
     * Handle rover ground movement with fuel consumption.
     */
    private void handleRoverMovement() {
        Entity driver = getControllingPassenger();
        if (!(driver instanceof EntityLivingBase)) {
            // No driver, apply friction
            motionX *= 0.85D;
            motionZ *= 0.85D;
            acceleration *= 0.9f;
            return;
        }

        EntityLivingBase rider = (EntityLivingBase) driver;
        float forward = rider.moveForward;
        float strafe = rider.moveStrafing;

        // Check if movement input exists
        boolean hasInput = forward != 0.0F || strafe != 0.0F;

        if (hasInput) {
            // Check for fuel
            if (fuelTank != null && fuelTank.getFluidAmount() > 0) {
                // Consume fuel periodically
                fuelConsumptionTick++;
                if (fuelConsumptionTick >= 20) { // Every second
                    consumeFluidFuel(FUEL_PER_MOVEMENT);
                    fuelConsumptionTick = 0;
                }

                // Apply steering
                steering = strafe * 2.5f;
                rotationYaw -= steering;

                // Apply acceleration
                acceleration = forward * 0.35f;

                // Calculate movement vector
                double yawRadians = Math.toRadians(rotationYaw);
                motionX = -Math.sin(yawRadians) * acceleration;
                motionZ = Math.cos(yawRadians) * acceleration;
            } else {
                // Out of fuel
                motionX *= 0.95D;
                motionZ *= 0.95D;
                acceleration *= 0.8f;

                if (ticksExisted % 60 == 0 && driver instanceof EntityPlayer) {
                    ((EntityPlayer) driver).sendMessage(new TextComponentString("Rover is out of fuel!"));
                }
            }
        } else {
            // No input, decelerate
            motionX *= 0.85D;
            motionZ *= 0.85D;
            acceleration *= 0.9f;
        }

        // Apply additional friction on ground
        if (onGround) {
            motionX *= 0.75D;
            motionZ *= 0.75D;
        }
    }

    @Override
    protected boolean canFitPassenger(Entity passenger) {
        // Allow 2 passengers: driver + passenger
        return getPassengers().size() < 2;
    }

    @Override
    public Entity getControllingPassenger() {
        // First passenger is always the driver
        List<Entity> passengers = getPassengers();
        return passengers.isEmpty() ? null : passengers.get(0);
    }

    @Override
    public void updatePassenger(Entity passenger) {
        if (!isPassenger(passenger)) return;

        List<Entity> passengers = getPassengers();
        int passengerIndex = passengers.indexOf(passenger);

        if (passengerIndex == 0) {
            // Driver position (front)
            double offsetX = -Math.sin(Math.toRadians(rotationYaw)) * 0.3;
            double offsetZ = Math.cos(Math.toRadians(rotationYaw)) * 0.3;
            passenger.setPosition(
                posX + offsetX,
                posY + getMountedYOffset() + passenger.getYOffset(),
                posZ + offsetZ
            );
        } else if (passengerIndex == 1) {
            // Passenger position (back)
            double offsetX = -Math.sin(Math.toRadians(rotationYaw)) * -0.3;
            double offsetZ = Math.cos(Math.toRadians(rotationYaw)) * -0.3;
            passenger.setPosition(
                posX + offsetX,
                posY + getMountedYOffset() + passenger.getYOffset(),
                posZ + offsetZ
            );
        }

        // Sync rotation
        passenger.rotationYaw = this.rotationYaw;
        passenger.prevRotationYaw = this.prevRotationYaw;
    }

    @Override
    public double getMountedYOffset() {
        return height * 0.72D;
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        acceleration = compound.getFloat("Acceleration");
        steering = compound.getFloat("Steering");
        fuelConsumptionTick = compound.getInteger("FuelConsumptionTick");
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setFloat("Acceleration", acceleration);
        compound.setFloat("Steering", steering);
        compound.setInteger("FuelConsumptionTick", fuelConsumptionTick);
    }

    /**
     * Get the current acceleration value.
     */
    public float getAcceleration() {
        return acceleration;
    }

    /**
     * Get the current steering value.
     */
    public float getSteering() {
        return steering;
    }
}
