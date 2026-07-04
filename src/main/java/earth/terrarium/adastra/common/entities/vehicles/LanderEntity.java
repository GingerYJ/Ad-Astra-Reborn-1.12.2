package earth.terrarium.adastra.common.entities.vehicles;

import earth.terrarium.adastra.api.systems.OxygenApi;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

/**
 * Lander entity with controlled descent, thrust control, and landing safety.
 */
public class LanderEntity extends VehicleBase {

    private static final int LANDER_INVENTORY_SIZE = 11;
    private static final int LANDER_FUEL_CAPACITY = 1000; // 1 bucket
    private static final int FUEL_PER_THRUST = 5;
    private static final double THRUST_POWER = 0.08D;
    private static final double SAFE_LANDING_SPEED = 0.5D;
    private static final double HARD_LANDING_SPEED = 1.5D;

    private boolean thrusting = false;
    private int thrustTick = 0;
    private double lastLandingSpeed = 0.0D;

    public LanderEntity(World world) {
        super(world, VehicleType.LANDER, 0, 0, LANDER_INVENTORY_SIZE, LANDER_FUEL_CAPACITY, stack -> {
            if (stack == null) return false;
            return stack.getFluid().getName().contains("fuel");
        });
        setSize(1.2f, 2.0f);
        this.isImmuneToFire = true;
    }

    @Override
    protected void openInventoryGUI(EntityPlayer player) {
        if (!world.isRemote && player instanceof EntityPlayerMP) {
            player.openGui(earth.terrarium.adastra.AdAstraReborn.instance,
                earth.terrarium.adastra.ModGuiHandler.LANDER_GUI,
                world, getEntityId(), 0, 0);
        }
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (isDead) {
            return;
        }
        handleDescentControl();
        if (onGround && Math.abs(motionY) > 0.01D) {
            handleLanding();
        }
        if (thrusting && world.isRemote) {
            spawnThrustParticles();
        }
    }

    private void handleDescentControl() {
        Entity pilot = getControllingPassenger();
        if (!hasNoGravity() && !onGround) {
            motionY -= 0.08D;
        }
        if (pilot instanceof EntityLivingBase) {
            EntityLivingBase rider = (EntityLivingBase) pilot;
            float forward = rider.moveForward;
            float strafe = rider.moveStrafing;
            if (forward > 0.0F && !onGround) {
                if (hasFuel()) {
                    thrusting = true;
                    thrustTick++;
                    motionY += THRUST_POWER;
                    if (motionY > 0.2D) motionY = 0.2D;
                    if (thrustTick % 5 == 0) consumeFluidFuel(FUEL_PER_THRUST);
                    if (!world.isRemote && thrustTick % 20 == 0) {
                        world.playSound(null, posX, posY, posZ, SoundEvents.BLOCK_FIRE_AMBIENT, SoundCategory.NEUTRAL, 0.5f, 1.2f);
                    }
                } else {
                    thrusting = false;
                    if (!world.isRemote && ticksExisted % 40 == 0 && pilot instanceof EntityPlayer) {
                        ((EntityPlayer) pilot).sendMessage(new TextComponentString("Lander is out of fuel!"));
                    }
                }
            } else {
                thrusting = false;
                thrustTick = 0;
            }
            if (strafe != 0.0F && !onGround) {
                double yawRadians = Math.toRadians(rotationYaw + 90);
                double strafeSpeed = strafe * 0.05D;
                motionX += Math.cos(yawRadians) * strafeSpeed;
                motionZ += Math.sin(yawRadians) * strafeSpeed;
                double horizontalSpeed = Math.sqrt(motionX * motionX + motionZ * motionZ);
                if (horizontalSpeed > 0.3D) {
                    double scale = 0.3D / horizontalSpeed;
                    motionX *= scale;
                    motionZ *= scale;
                }
            }
            if (!onGround) rotationYaw += strafe * 1.0f;
        } else {
            thrusting = false;
        }
        if (isInWater()) {
            motionY = Math.min(0.06D, motionY + 0.15D);
            motionX *= 0.9D;
            motionZ *= 0.9D;
        }
        if (!onGround) lastLandingSpeed = Math.abs(motionY);
    }

    private void handleLanding() {
        if (world.isRemote) return;
        if (lastLandingSpeed < SAFE_LANDING_SPEED) {
            world.playSound(null, posX, posY, posZ, SoundEvents.BLOCK_METAL_PLACE, SoundCategory.NEUTRAL, 1.0f, 0.8f);
            Entity pilot = getControllingPassenger();
            if (pilot instanceof EntityPlayer) {
                ((EntityPlayer) pilot).sendMessage(new TextComponentString("Safe landing!"));
            }
        } else if (lastLandingSpeed < HARD_LANDING_SPEED) {
            world.playSound(null, posX, posY, posZ, SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.NEUTRAL, 0.5f, 1.2f);
            for (Entity passenger : getPassengers()) {
                if (passenger instanceof EntityLivingBase) {
                    passenger.attackEntityFrom(net.minecraft.util.DamageSource.FLY_INTO_WALL, 5.0f);
                }
            }
            Entity pilot = getControllingPassenger();
            if (pilot instanceof EntityPlayer) {
                ((EntityPlayer) pilot).sendMessage(new TextComponentString("Hard landing! Damage taken."));
            }
        } else {
            explode();
            Entity pilot = getControllingPassenger();
            if (pilot instanceof EntityPlayer) {
                ((EntityPlayer) pilot).sendMessage(new TextComponentString("CRASH!"));
            }
        }
        lastLandingSpeed = 0.0D;
    }

    @Override
    public void fall(float distance, float damageMultiplier) {
        super.fall(distance, damageMultiplier);
        if (!world.isRemote && distance > 40.0F && onGround && !isDead) {
            explode();
        }
    }

    private void explode() {
        if (!world.isRemote) {
            world.createExplosion(this, posX, posY, posZ, 10.0f, OxygenApi.API.hasOxygen(world));
            setDead();
        }
    }

    private boolean hasFuel() {
        return fuelTank != null && fuelTank.getFluidAmount() > 0;
    }

    private void spawnThrustParticles() {
        for (int i = 0; i < 5; i++) {
            double offsetX = (rand.nextDouble() - 0.5) * 0.3;
            double offsetZ = (rand.nextDouble() - 0.5) * 0.3;
            world.spawnParticle(EnumParticleTypes.FLAME, posX + offsetX, posY - 0.5, posZ + offsetZ,
                (rand.nextDouble() - 0.5) * 0.05, -0.1, (rand.nextDouble() - 0.5) * 0.05);
        }
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        thrusting = compound.getBoolean("Thrusting");
        thrustTick = compound.getInteger("ThrustTick");
        lastLandingSpeed = compound.getDouble("LastLandingSpeed");
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setBoolean("Thrusting", thrusting);
        compound.setInteger("ThrustTick", thrustTick);
        compound.setDouble("LastLandingSpeed", lastLandingSpeed);
    }

    public boolean isThrusting() {
        return thrusting;
    }

    public double getLastLandingSpeed() {
        return lastLandingSpeed;
    }

    @Override
    public boolean hideRider() {
        return true;
    }

    @Override
    public boolean zoomOutCameraInThirdPerson() {
        return true;
    }

    @Override
    public Vec3d getDismountPosition(EntityLivingBase passenger) {
        Vec3d offset = getHorizontalLookOffset(passenger, 1.0D);
        return new Vec3d(passenger.posX + offset.x, passenger.posY - 2.0D, passenger.posZ + offset.z);
    }
}
