package earth.terrarium.adastra.common.entities.vehicles;

import earth.terrarium.adastra.common.network.NetworkHandler;
import earth.terrarium.adastra.common.network.packet.PacketOpenVehicleGui;
import earth.terrarium.adastra.common.network.packet.PacketPlayRadioStation;
import earth.terrarium.adastra.common.tile.RadioTileEntity;
import earth.terrarium.adastra.common.util.radio.RadioHolder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Rover entity with dual-passenger support, 18-slot inventory, and fuel consumption.
 */
public class RoverEntity extends VehicleBase implements RadioHolder {

    private static final int ROVER_INVENTORY_SIZE = 18;
    private static final int ROVER_FUEL_CAPACITY = 2000; // 2 buckets
    private static final int FUEL_PER_MOVEMENT = 1; // Fuel consumed per movement tick
    private static final double RUN_OVER_MIN_SPEED = 0.15D;
    private static final DataParameter<String> RADIO_URL = EntityDataManager.createKey(RoverEntity.class, DataSerializers.STRING);

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
        addVehiclePart("radio", 0.6f, 0.7f, 0.6D, 1.0D, 0.5D, (player, hand) -> {
            if (player.getRidingEntity() != this) {
                return false;
            }
            if (!world.isRemote && player instanceof EntityPlayerMP) {
                NetworkHandler.CHANNEL.sendTo(
                    new PacketOpenVehicleGui(getEntityId(), PacketOpenVehicleGui.VehicleGuiType.ROVER_RADIO),
                    (EntityPlayerMP) player);
            }
            return true;
        });
        addVehiclePart("cargo", 1.1f, 0.7f, 0.15D, 0.8D, -1.7D, (player, hand) -> {
            if (!world.isRemote) {
                openInventoryGUI(player);
            }
            return true;
        });
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        dataManager.register(RADIO_URL, "");
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
            runOverEntities();
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

    private void runOverEntities() {
        double horizontalSpeed = Math.sqrt(motionX * motionX + motionZ * motionZ);
        if (horizontalSpeed <= RUN_OVER_MIN_SPEED) {
            return;
        }

        List<EntityLivingBase> entities = world.getEntitiesWithinAABB(
            EntityLivingBase.class,
            getEntityBoundingBox().grow(1.001D));
        if (entities.isEmpty()) {
            return;
        }

        Entity driver = getControllingPassenger();
        DamageSource source = driver instanceof EntityLivingBase
            ? DamageSource.causeMobDamage((EntityLivingBase) driver)
            : DamageSource.GENERIC;
        double power = horizontalSpeed * 0.4D;
        float damage = (float) (power * 50.0D);
        double yaw = Math.toRadians(rotationYaw);
        double knockbackX = -Math.sin(yaw) * 0.1D;
        double knockbackZ = Math.cos(yaw) * 0.1D;

        for (EntityLivingBase entity : entities) {
            if (getPassengers().contains(entity)) {
                continue;
            }
            entity.motionX += knockbackX;
            entity.motionY += power;
            entity.motionZ += knockbackZ;
            entity.velocityChanged = true;
            entity.attackEntityFrom(source, damage);
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

    }

    @Override
    public double getMountedYOffset() {
        return height * 0.72D;
    }

    @Override
    public Vec3d getDismountPosition(EntityLivingBase passenger) {
        double zOffset = getControllingPassenger() == passenger ? 1.75D : -1.75D;
        double angle = -Math.toRadians(rotationYaw) - Math.PI / 2.0D;
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        double localX = -0.5D;
        double localZ = zOffset;
        double x = localX * cos + localZ * sin;
        double z = localZ * cos - localX * sin;
        return new Vec3d(posX + x, posY, posZ + z);
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        acceleration = compound.getFloat("Acceleration");
        steering = compound.getFloat("Steering");
        fuelConsumptionTick = compound.getInteger("FuelConsumptionTick");
        setRadioUrl(compound.getString("RadioUrl"));
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setFloat("Acceleration", acceleration);
        compound.setFloat("Steering", steering);
        compound.setInteger("FuelConsumptionTick", fuelConsumptionTick);
        compound.setString("RadioUrl", getRadioUrl());
    }

    @Override
    public String getRadioUrl() {
        return dataManager.get(RADIO_URL);
    }

    @Override
    public void setRadioUrl(String url) {
        String normalized = RadioTileEntity.normalizeStation(url);
        dataManager.set(RADIO_URL, normalized);
        if (!world.isRemote) {
            syncRadioToPassengers(normalized);
        }
    }

    private void syncRadioToPassengers(String url) {
        for (Entity passenger : getPassengers()) {
            if (passenger instanceof EntityPlayerMP) {
                NetworkHandler.CHANNEL.sendTo(new PacketPlayRadioStation(url), (EntityPlayerMP) passenger);
            }
        }
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
