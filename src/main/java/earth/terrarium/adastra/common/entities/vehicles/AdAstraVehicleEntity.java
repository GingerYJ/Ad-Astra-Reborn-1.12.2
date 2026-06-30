package earth.terrarium.adastra.common.entities.vehicles;

import earth.terrarium.adastra.common.entities.AdAstraPlaceholderEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class AdAstraVehicleEntity extends AdAstraPlaceholderEntity {

    private final VehicleType vehicleType;
    private final int maxFuel;

    private int fuel;
    private int launchTicks;

    public AdAstraVehicleEntity(World world, VehicleType vehicleType, int maxFuel) {
        super(world);
        this.vehicleType = vehicleType;
        this.maxFuel = maxFuel;
        this.fuel = maxFuel;
        this.preventEntitySpawning = true;
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        if (vehicleType == VehicleType.ROVER) {
            updateRoverMotion();
        } else {
            updateFlightMotion();
        }

        move(MoverType.SELF, motionX, motionY, motionZ);
        motionX *= 0.82D;
        motionZ *= 0.82D;
        if (onGround && motionY < 0.0D) {
            motionY = 0.0D;
        }
    }

    @Override
    public boolean processInitialInteract(EntityPlayer player, EnumHand hand) {
        if (!player.isSneaking() && !isBeingRidden()) {
            if (!world.isRemote) {
                player.startRiding(this);
            }
            return true;
        }
        return super.processInitialInteract(player, hand);
    }

    @Override
    public boolean canBeCollidedWith() {
        return !isDead;
    }

    @Override
    public boolean canBePushed() {
        return true;
    }

    @Override
    public double getMountedYOffset() {
        return vehicleType == VehicleType.ROVER ? height * 0.72D : height * 0.42D;
    }

    @Override
    public Entity getControllingPassenger() {
        return getPassengers().isEmpty() ? null : getPassengers().get(0);
    }

    @Override
    public boolean canPassengerSteer() {
        return getControllingPassenger() instanceof EntityPlayer;
    }

    @Override
    protected boolean canFitPassenger(Entity passenger) {
        return getPassengers().isEmpty();
    }

    @Override
    public void updatePassenger(Entity passenger) {
        super.updatePassenger(passenger);
        if (isPassenger(passenger)) {
            passenger.setPosition(posX, posY + getMountedYOffset() + passenger.getYOffset(), posZ);
        }
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        fuel = compound.hasKey("Fuel") ? compound.getInteger("Fuel") : maxFuel;
        launchTicks = compound.getInteger("LaunchTicks");
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setInteger("Fuel", fuel);
        compound.setInteger("MaxFuel", maxFuel);
        compound.setInteger("LaunchTicks", launchTicks);
    }

    public int getFuel() {
        return fuel;
    }

    public int getMaxFuel() {
        return maxFuel;
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }

    private void updateRoverMotion() {
        if (!hasNoGravity()) {
            motionY -= 0.08D;
        }

        Entity passenger = getControllingPassenger();
        if (!(passenger instanceof EntityLivingBase)) {
            return;
        }

        EntityLivingBase rider = (EntityLivingBase) passenger;
        rotationYaw = rider.rotationYaw;
        prevRotationYaw = rotationYaw;
        float forward = rider.moveForward;
        float strafe = rider.moveStrafing * 0.45F;

        if (forward <= 0.0F) {
            forward *= 0.35F;
        }

        float speed = onGround ? 0.22F : 0.08F;
        moveRelative(strafe, 0.0F, forward, speed);
    }

    private void updateFlightMotion() {
        Entity passenger = getControllingPassenger();
        boolean accelerating = passenger instanceof EntityLivingBase
            && ((EntityLivingBase) passenger).moveForward > 0.2F
            && consumeFuel();

        if (accelerating) {
            launchTicks++;
            motionY = Math.min(0.45D, motionY + 0.045D);
        } else if (!hasNoGravity()) {
            motionY -= 0.035D;
        }

        if (passenger != null) {
            rotationYaw = MathHelper.wrapDegrees(passenger.rotationYaw);
            prevRotationYaw = rotationYaw;
        }
    }

    private boolean consumeFuel() {
        if (fuel <= 0) {
            return false;
        }
        fuel--;
        return true;
    }

    public enum VehicleType {
        ROVER,
        ROCKET,
        LANDER
    }
}
