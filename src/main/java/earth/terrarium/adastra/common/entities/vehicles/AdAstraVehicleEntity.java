package earth.terrarium.adastra.common.entities.vehicles;

import earth.terrarium.adastra.common.entities.AdAstraPlaceholderEntity;
import earth.terrarium.adastra.common.network.NetworkHandler;
import earth.terrarium.adastra.common.network.packet.PacketOpenPlanetSelection;
import earth.terrarium.adastra.common.registry.ModItems;
import earth.terrarium.adastra.common.registry.ModSounds;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class AdAstraVehicleEntity extends AdAstraPlaceholderEntity {

    private static final double PLANET_SELECTION_HEIGHT = 180.0D;
    private static final int LAUNCH_SOUND_THRESHOLD = 5;
    private static final int ROCKET_SOUND_INTERVAL = 40;

    private final VehicleType vehicleType;
    private final int maxFuel;
    private final int rocketTier;

    private int fuel;
    private int launchTicks;
    private boolean planetSelectionOpened;
    private boolean launchSoundPlayed;
    private int rocketSoundCooldown;

    public AdAstraVehicleEntity(World world, VehicleType vehicleType, int maxFuel) {
        this(world, vehicleType, maxFuel, 0);
    }

    public AdAstraVehicleEntity(World world, VehicleType vehicleType, int maxFuel, int rocketTier) {
        super(world);
        this.vehicleType = vehicleType;
        this.maxFuel = maxFuel;
        this.rocketTier = rocketTier;
        this.fuel = maxFuel;
        this.preventEntitySpawning = true;
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        if (rocketSoundCooldown > 0) {
            rocketSoundCooldown--;
        }

        if (handlesOwnMotion()) {
            return;
        }

        if (vehicleType == VehicleType.ROVER) {
            updateRoverMotion();
        } else {
            updateFlightMotion();
        }

        // Apply motion
        move(MoverType.SELF, motionX, motionY, motionZ);

        // Apply friction/drag based on vehicle type
        if (vehicleType == VehicleType.ROVER) {
            // Rovers have more friction when on ground
            if (onGround) {
                motionX *= 0.75D;
                motionZ *= 0.75D;
                if (motionY < 0.0D) {
                    motionY = 0.0D;
                }
            } else {
                motionX *= 0.95D;
                motionZ *= 0.95D;
            }
        } else {
            // Rockets have less friction (air resistance)
            motionX *= 0.95D;
            motionZ *= 0.95D;
            if (onGround && motionY < 0.0D) {
                motionY = 0.0D;
            }
        }
    }

    protected boolean handlesOwnMotion() {
        return false;
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
    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (isEntityInvulnerable(source) || isDead) {
            return false;
        }

        Entity attacker = source.getTrueSource();
        if (!(attacker instanceof EntityPlayer)) {
            return false;
        }

        EntityPlayer player = (EntityPlayer) attacker;
        if (player.getRidingEntity() == this) {
            return false;
        }

        if (!world.isRemote) {
            world.playSound(null, posX, posY, posZ, SoundEvents.BLOCK_METAL_BREAK, SoundCategory.NEUTRAL, 1.0f, 1.0f);
            if (!player.capabilities.isCreativeMode) {
                ItemStack drop = getDropStack();
                if (!drop.isEmpty()) {
                    entityDropItem(drop, 0.0F);
                }
            }
            setDead();
        }
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
        if (isPassenger(passenger)) {
            // Position passenger at mount offset
            passenger.setPosition(posX, posY + getMountedYOffset() + passenger.getYOffset(), posZ);

            // Sync passenger rotation with vehicle for immersive control
            if (vehicleType == VehicleType.ROVER) {
                // Rover: passenger looks where vehicle is heading
                passenger.rotationYaw = this.rotationYaw;
                passenger.prevRotationYaw = this.prevRotationYaw;
            } else {
                // Rocket: passenger rotation is synced but can look around slightly
                passenger.rotationYaw = this.rotationYaw;
                passenger.prevRotationYaw = this.prevRotationYaw;
            }
        }
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        fuel = compound.hasKey("Fuel") ? compound.getInteger("Fuel") : maxFuel;
        launchTicks = compound.getInteger("LaunchTicks");
        planetSelectionOpened = compound.getBoolean("PlanetSelectionOpened");
        launchSoundPlayed = compound.getBoolean("LaunchSoundPlayed");
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setInteger("Fuel", fuel);
        compound.setInteger("MaxFuel", maxFuel);
        compound.setInteger("LaunchTicks", launchTicks);
        compound.setBoolean("PlanetSelectionOpened", planetSelectionOpened);
        compound.setBoolean("LaunchSoundPlayed", launchSoundPlayed);
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

    public int getRocketTier() {
        return rocketTier;
    }

    public ItemStack getDropStack() {
        if (vehicleType == VehicleType.ROVER) {
            return new ItemStack(ModItems.TIER_1_ROVER);
        }
        if (vehicleType == VehicleType.ROCKET) {
            switch (rocketTier) {
                case 1:
                    return new ItemStack(ModItems.TIER_1_ROCKET);
                case 2:
                    return new ItemStack(ModItems.TIER_2_ROCKET);
                case 3:
                    return new ItemStack(ModItems.TIER_3_ROCKET);
                case 4:
                    return new ItemStack(ModItems.TIER_4_ROCKET);
                case 5:
                    return new ItemStack(ModItems.TIER_5_ROCKET);
                case 6:
                    return new ItemStack(ModItems.TIER_6_ROCKET);
                case 7:
                    return new ItemStack(ModItems.TIER_7_ROCKET);
                default:
                    return ItemStack.EMPTY;
            }
        }
        return ItemStack.EMPTY;
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
        float forward = rider.moveForward;
        float strafe = rider.moveStrafing;

        // WASD ground driving control
        if (forward != 0.0F || strafe != 0.0F) {
            // Turning - A/D keys affect rotation
            rotationYaw -= strafe * 2.0F;
            prevRotationYaw = rotationYaw;

            // Forward/backward movement - W/S keys
            double yawRadians = Math.toRadians(rotationYaw);
            double speed = forward * 0.3D;
            motionX = -Math.sin(yawRadians) * speed;
            motionZ = Math.cos(yawRadians) * speed;
        } else {
            // Deceleration when no input
            motionX *= 0.82D;
            motionZ *= 0.82D;
        }

        // Apply friction more aggressively on ground
        if (onGround) {
            motionX *= 0.75D;
            motionZ *= 0.75D;
        }
    }

    private void updateFlightMotion() {
        Entity passenger = getControllingPassenger();

        if (passenger instanceof EntityLivingBase) {
            EntityLivingBase rider = (EntityLivingBase) passenger;
            float forward = rider.moveForward;
            float strafe = rider.moveStrafing;

            // W key = accelerate upward
            if (forward > 0.0F && consumeFuel()) {
                launchTicks++;

                // Play launch sound when starting
                if (!launchSoundPlayed && launchTicks >= LAUNCH_SOUND_THRESHOLD) {
                    playLaunchSound();
                    launchSoundPlayed = true;
                }

                // Play looping rocket sound while flying
                if (launchTicks > LAUNCH_SOUND_THRESHOLD) {
                    playRocketSound();
                }

                motionY += 0.1D;
                // Clamp maximum upward speed
                if (motionY > 0.45D) {
                    motionY = 0.45D;
                }
                openPlanetSelectionIfReady(passenger);
            }
            // S key = descend (slower than ascent)
            else if (forward < 0.0F) {
                motionY -= 0.05D;
                // Clamp maximum downward speed
                if (motionY < -0.3D) {
                    motionY = -0.3D;
                }
                // Reset launch state when descending
                resetLaunchState();
            }
            // No thrust = gravity takes over
            else {
                if (!hasNoGravity()) {
                    motionY -= 0.035D;
                }
                // Reset launch state when not thrusting
                resetLaunchState();
            }

            // A/D keys for horizontal steering while in flight
            if (strafe != 0.0F && launchTicks > 0) {
                rotationYaw -= strafe * 1.5F;
            }

            prevRotationYaw = rotationYaw;
        } else {
            // No passenger, gravity applies
            if (!hasNoGravity()) {
                motionY -= 0.035D;
            }
            // Reset launch state when no passenger
            resetLaunchState();
        }
    }

    private void playLaunchSound() {
        if (world != null && !world.isRemote && vehicleType == VehicleType.ROCKET) {
            world.playSound(null, posX, posY, posZ, ModSounds.ROCKET_LAUNCH, SoundCategory.NEUTRAL, 1.5f, 1.0f);
        }
    }

    private void playRocketSound() {
        if (world != null && !world.isRemote && vehicleType == VehicleType.ROCKET && rocketSoundCooldown <= 0) {
            // Adjust pitch based on vertical speed
            float pitch = 0.9f + (float) Math.min(motionY * 0.5f, 0.3f);
            world.playSound(null, posX, posY, posZ, ModSounds.ROCKET, SoundCategory.NEUTRAL, 0.6f, pitch);
            rocketSoundCooldown = ROCKET_SOUND_INTERVAL;
        }
    }

    private void resetLaunchState() {
        if (launchTicks > 0) {
            launchTicks = 0;
            launchSoundPlayed = false;
        }
    }

    private boolean consumeFuel() {
        if (fuel <= 0) {
            return false;
        }
        fuel--;
        return true;
    }

    private void openPlanetSelectionIfReady(Entity passenger) {
        if (world.isRemote || vehicleType != VehicleType.ROCKET || planetSelectionOpened || posY < PLANET_SELECTION_HEIGHT) {
            return;
        }
        if (passenger instanceof EntityPlayerMP) {
            planetSelectionOpened = true;
            NetworkHandler.CHANNEL.sendTo(new PacketOpenPlanetSelection(Math.max(1, rocketTier)), (EntityPlayerMP) passenger);
        }
    }

    // ===== Network Control Methods =====

    /**
     * Start the rocket launch sequence.
     * Called from PacketVehicleControl.
     */
    public void startLaunch() {
        if (vehicleType == VehicleType.ROCKET && fuel > 0) {
            launchTicks = 1;
        }
    }

    /**
     * Set rover control inputs.
     * Called from PacketVehicleControl.
     *
     * @param steer Steering value (-1.0 to 1.0)
     * @param accelerate Acceleration value (0.0 to 1.0)
     * @param boost Boost enabled
     */
    public void setRoverControl(float steer, float accelerate, boolean boost) {
        if (vehicleType != VehicleType.ROVER) {
            return;
        }

        // Apply steering
        if (steer != 0.0f) {
            rotationYaw -= steer * 2.0f;
            prevRotationYaw = rotationYaw;
        }

        // Apply acceleration
        if (accelerate > 0.0f && consumeFuel()) {
            double yawRadians = Math.toRadians(rotationYaw);
            double speed = accelerate * 0.3D * (boost ? 1.5D : 1.0D);
            motionX = -Math.sin(yawRadians) * speed;
            motionZ = Math.cos(yawRadians) * speed;
        }
    }

    /**
     * Set lander descent thrust.
     * Called from PacketVehicleControl.
     *
     * @param thrust Thrust value (0.0 to 1.0)
     * @param steer Horizontal steering (-1.0 to 1.0)
     */
    public void setLanderThrust(float thrust, float steer) {
        if (vehicleType != VehicleType.LANDER) {
            return;
        }

        if (thrust > 0.0f && consumeFuel()) {
            // Counteract gravity with thrust
            motionY += thrust * 0.08D;
            if (motionY > 0.2D) {
                motionY = 0.2D;
            }
        }

        // Horizontal steering for landing adjustment
        if (steer != 0.0f) {
            rotationYaw -= steer * 1.0f;
            prevRotationYaw = rotationYaw;
        }
    }

    /**
     * Stop all vehicle motion.
     * Called from PacketVehicleControl.
     */
    public void stopVehicle() {
        motionX = 0.0D;
        motionZ = 0.0D;
        if (onGround) {
            motionY = 0.0D;
        }
    }

    public enum VehicleType {
        ROVER,
        ROCKET,
        LANDER
    }
}
