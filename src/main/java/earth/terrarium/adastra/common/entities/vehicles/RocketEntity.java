package earth.terrarium.adastra.common.entities.vehicles;

import earth.terrarium.adastra.client.particle.ParticleHelper;
import earth.terrarium.adastra.api.planets.Planet;
import earth.terrarium.adastra.api.systems.OxygenApi;
import earth.terrarium.adastra.common.blocks.LaunchPadBlock;
import earth.terrarium.adastra.common.capability.AdAstraCapabilities;
import earth.terrarium.adastra.common.capability.AdAstraPlayer;
import earth.terrarium.adastra.common.capability.IAdAstraPlayer;
import earth.terrarium.adastra.common.config.AdAstraConfig;
import earth.terrarium.adastra.common.network.NetworkHandler;
import earth.terrarium.adastra.common.network.packet.PacketOpenPlanetSelection;
import earth.terrarium.adastra.common.network.packet.PacketSyncPlayerCapability;
import earth.terrarium.adastra.common.planets.PlanetApiImpl;
import earth.terrarium.adastra.common.registry.ModSounds;
import earth.terrarium.adastra.common.util.KeybindManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

/**
 * Base rocket entity with full fuel system, inventory, and launch control.
 */
public abstract class RocketEntity extends VehicleBase {

    private static final DataParameter<Boolean> DATA_IS_LAUNCHING = EntityDataManager.createKey(RocketEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Integer> DATA_LAUNCH_COUNTDOWN = EntityDataManager.createKey(RocketEntity.class, DataSerializers.VARINT);
    private static final DataParameter<Boolean> DATA_HAS_LAUNCHED = EntityDataManager.createKey(RocketEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> DATA_PLANET_SELECTION_OPENED = EntityDataManager.createKey(RocketEntity.class, DataSerializers.BOOLEAN);

    private static final int LAUNCH_COUNTDOWN = 200; // 10 seconds
    private static final int FUEL_PER_TICK = 10; // Fuel consumption per tick while flying
    private static final int LAUNCH_FUEL_COST = 1000; // 1 bucket to initiate launch
    private static final double PLANET_SELECTION_HEIGHT = 180.0D;
    private static final double PLANET_SELECTION_ASCENT = 120.0D;
    private static final double LAUNCH_ACCELERATION = 0.005D;
    private static final double MAX_FLIGHT_SPEED = 1.0D;
    private static final DamageSource ROCKET_FLAMES = new DamageSource("rocket_flames").setFireDamage();

    private boolean planetSelectionOpened = false;
    private float flightSpeed = 0.05f;
    private float angle = 0.0f;
    private boolean launchpadBound = false;
    private int particleTick = 0;
    private boolean launchKeyWasDown = false;
    private int lastCountdownMessageSecond = -1;
    private double launchStartY = Double.NaN;
    private boolean clientHasFlightTarget;
    private double clientTargetX;
    private double clientTargetY;
    private double clientTargetZ;
    private float clientTargetYaw;
    private float clientTargetPitch;

    public RocketEntity(World world, int rocketTier, int fuelCapacity, Predicate<FluidStack> fuelFilter) {
        super(world, VehicleType.ROCKET, 0, rocketTier, 10, fuelCapacity, fuelFilter);
        this.isImmuneToFire = true;
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        dataManager.register(DATA_IS_LAUNCHING, false);
        dataManager.register(DATA_LAUNCH_COUNTDOWN, -1);
        dataManager.register(DATA_HAS_LAUNCHED, false);
        dataManager.register(DATA_PLANET_SELECTION_OPENED, false);
    }

    @Override
    protected void openInventoryGUI(EntityPlayer player) {
        if (!world.isRemote && player instanceof EntityPlayerMP) {
            player.openGui(earth.terrarium.adastra.AdAstraReborn.instance,
                earth.terrarium.adastra.ModGuiHandler.ROCKET_GUI,
                world, getEntityId(), 0, 0);
        }
    }

    public void loadFromItemStack(ItemStack stack) {
        if (fuelTank != null && stack.hasTagCompound()) {
            NBTTagCompound tag = stack.getTagCompound();
            if (tag != null && tag.hasKey("AdAstraRocket")) {
                NBTTagCompound rocketTag = tag.getCompoundTag("AdAstraRocket");
                if (rocketTag.hasKey("FuelTank")) {
                    fuelTank.deserializeNBT(rocketTag.getCompoundTag("FuelTank"));
                }
            }
        }
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        syncLaunchStateFromDataManager();

        if (world.isRemote) {
            if (hasLaunched) {
                handleClientFlightPrediction();
            } else {
                motionX = 0.0D;
                motionY = 0.0D;
                motionZ = 0.0D;
            }

            for (Entity passenger : getPassengers()) {
                updatePassenger(passenger);
            }

            if (isLaunching) {
                spawnCountdownSmoke();
            }
            if (isLaunching || hasLaunched) {
                spawnRocketParticles();
            }
            return;
        }

        // Handle launch pad validation
        validateLaunchPad();
        handleLaunchInput();

        // Handle launch sequence
        if (isLaunching) {
            handleLaunchCountdown();
        } else if (hasLaunched) {
            handleFlight();
        }

        moveRocket();
    }

    @Override
    public void setPositionAndRotationDirect(double x, double y, double z, float yaw, float pitch, int increments, boolean teleport) {
        if (world.isRemote && hasLaunched && !teleport) {
            clientTargetX = x;
            clientTargetY = Math.max(y, posY);
            clientTargetZ = z;
            clientTargetYaw = yaw;
            clientTargetPitch = pitch;
            clientHasFlightTarget = true;
            return;
        }
        super.setPositionAndRotationDirect(x, y, z, yaw, pitch, increments, teleport);
    }

    private void handleClientFlightPrediction() {
        if (planetSelectionOpened) {
            motionX = 0.0D;
            motionY = 0.0D;
            motionZ = 0.0D;
            return;
        }

        if (flightSpeed < MAX_FLIGHT_SPEED) {
            flightSpeed += LAUNCH_ACCELERATION;
        }

        motionY = flightSpeed;
        double nextX = posX + motionX;
        double nextY = posY + motionY;
        double nextZ = posZ + motionZ;

        if (clientHasFlightTarget) {
            nextX += (clientTargetX - nextX) * 0.15D;
            nextZ += (clientTargetZ - nextZ) * 0.15D;

            double upwardCorrection = clientTargetY - nextY;
            if (upwardCorrection > 0.0D) {
                nextY += Math.min(upwardCorrection * 0.35D, 0.35D);
            }

            rotationYaw += MathHelper.wrapDegrees(clientTargetYaw - rotationYaw) * 0.25F;
            rotationPitch += (clientTargetPitch - rotationPitch) * 0.25F;
        }

        setPosition(nextX, nextY, nextZ);
        motionX *= 0.95D;
        motionZ *= 0.95D;
    }

    private void syncLaunchStateFromDataManager() {
        if (world.isRemote) {
            isLaunching = dataManager.get(DATA_IS_LAUNCHING);
            launchCountdown = dataManager.get(DATA_LAUNCH_COUNTDOWN);
            boolean launched = dataManager.get(DATA_HAS_LAUNCHED);
            if (launched && !hasLaunched) {
                flightSpeed = 0.05F;
                clientHasFlightTarget = false;
            }
            hasLaunched = launched;
            planetSelectionOpened = dataManager.get(DATA_PLANET_SELECTION_OPENED);
        }
    }

    private void setLaunching(boolean launching) {
        isLaunching = launching;
        if (!world.isRemote) {
            dataManager.set(DATA_IS_LAUNCHING, launching);
        }
    }

    private void setLaunchCountdown(int countdown) {
        launchCountdown = countdown;
        if (!world.isRemote) {
            dataManager.set(DATA_LAUNCH_COUNTDOWN, countdown);
        }
    }

    private void setHasLaunched(boolean launched) {
        hasLaunched = launched;
        if (!world.isRemote) {
            dataManager.set(DATA_HAS_LAUNCHED, launched);
        }
    }

    private void setPlanetSelectionOpened(boolean opened) {
        planetSelectionOpened = opened;
        if (!world.isRemote) {
            dataManager.set(DATA_PLANET_SELECTION_OPENED, opened);
        }
    }

    @Override
    protected boolean handlesOwnMotion() {
        return true;
    }

    @Override
    public double getMountedYOffset() {
        return Math.min(2.0D, height * 0.28D);
    }

    @Override
    public void updatePassenger(Entity passenger) {
        if (!isPassenger(passenger)) {
            return;
        }

        double yOffset = getMountedYOffset() + passenger.getYOffset();
        double previousY = prevPosY + yOffset;
        double currentY = posY + yOffset;

        passenger.prevPosX = prevPosX;
        passenger.prevPosY = previousY;
        passenger.prevPosZ = prevPosZ;
        passenger.lastTickPosX = prevPosX;
        passenger.lastTickPosY = previousY;
        passenger.lastTickPosZ = prevPosZ;
        passenger.setPosition(posX, currentY, posZ);
        passenger.fallDistance = 0.0F;
    }

    /**
     * Validate that rocket is on a launch pad structure.
     */
    private void validateLaunchPad() {
        if (world.isRemote || ticksExisted % 5 != 0) return;
        if (isLaunching || hasLaunched) return;

        BlockPos launchPadCenter = getLaunchPadCenter();
        if (launchPadCenter != null) {
            launchpadBound = true;
            if (isLaunchPadPowered(launchPadCenter) && canLaunchFromPoweredPad()) {
                beginLaunchSequence();
            }
        } else if (launchpadBound) {
            ItemStack drop = getDropStack();
            if (!drop.isEmpty()) {
                entityDropItem(drop, 0.0F);
            }
            world.playSound(null, posX, posY, posZ, SoundEvents.BLOCK_METAL_BREAK, SoundCategory.NEUTRAL, 1.0f, 1.0f);
            setDead();
        }
    }

    private boolean isOnValidLaunchPad() {
        return getLaunchPadCenter() != null;
    }

    private BlockPos getLaunchPadCenter() {
        BlockPos feet = getPosition();
        BlockPos center = LaunchPadBlock.findLaunchPadCenter(world, feet);
        return center != null ? center : LaunchPadBlock.findLaunchPadCenter(world, feet.down());
    }

    private boolean isLaunchPadPowered(BlockPos center) {
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                BlockPos partPos = center.add(dx, 0, dz);
                if (world.getBlockState(partPos).getBlock() instanceof LaunchPadBlock && world.isBlockPowered(partPos)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Check if the rocket can launch.
     */
    public boolean canLaunch() {
        if (isLaunching || hasLaunched) return false;
        if (!(getControllingPassenger() instanceof EntityPlayer)) return false;
        if (!canLaunchFromCurrentDimension()) return false;
        if (!launchpadBound && isOnValidLaunchPad()) {
            launchpadBound = true;
        }
        if (!launchpadBound) return false;
        if (!hasEnoughFuel(LAUNCH_FUEL_COST)) return false;
        return passengerHasSpaceDown();
    }

    private boolean canLaunchFromPoweredPad() {
        if (isLaunching || hasLaunched) return false;
        if (!(getControllingPassenger() instanceof EntityPlayer)) return false;
        if (!canLaunchFromCurrentDimension()) return false;
        return hasEnoughFuel(LAUNCH_FUEL_COST);
    }

    private boolean passengerHasSpaceDown() {
        Entity passenger = getControllingPassenger();
        return passenger instanceof EntityPlayer && KeybindManager.jumpDown((EntityPlayer) passenger);
    }

    private boolean canLaunchFromCurrentDimension() {
        if (AdAstraConfig.launchFromAnywhere) {
            return true;
        }

        int dimensionId = world.provider.getDimension();
        if (dimensionId == 0) {
            return true;
        }

        Map<Integer, Planet> planets = PlanetApiImpl.snapshotPlanets();
        if (planets.containsKey(dimensionId)) {
            return true;
        }

        for (Planet planet : planets.values()) {
            if (planet.getAdditionalLaunchDimensions().contains(dimensionId)) {
                return true;
            }
        }
        return false;
    }

    private void handleLaunchInput() {
        if (world.isRemote || isLaunching || hasLaunched) {
            if (!isLaunching) {
                launchKeyWasDown = false;
            }
            return;
        }
        Entity passenger = getControllingPassenger();
        boolean launchKeyDown = passenger instanceof EntityPlayer && KeybindManager.jumpDown((EntityPlayer) passenger);
        if (launchKeyDown && !launchKeyWasDown) {
            initiateLaunch();
        }
        launchKeyWasDown = launchKeyDown;
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (isLaunching || hasLaunched) {
            return false;
        }
        return super.attackEntityFrom(source, amount);
    }

    /**
     * Initiate the launch sequence.
     */
    public void initiateLaunch() {
        if (!canLaunch()) {
            sendLaunchFailure();
            return;
        }

        beginLaunchSequence();
    }

    private void beginLaunchSequence() {
        setLaunching(true);
        setLaunchCountdown(LAUNCH_COUNTDOWN);
        lastCountdownMessageSecond = -1;
        launchStartY = posY;

        // Play launch sound
        if (!world.isRemote) {
            world.playSound(null, posX, posY, posZ, ModSounds.ROCKET_LAUNCH, SoundCategory.NEUTRAL, 2.0f, 1.0f);
            Entity passenger = getControllingPassenger();
            if (passenger instanceof EntityPlayer) {
                ((EntityPlayer) passenger).sendStatusMessage(new TextComponentTranslation("message.ad_astra.rocket_launch_started"), true);
            }
        }

        // Consume launch fuel
        consumeFluidFuel(LAUNCH_FUEL_COST);
    }

    /**
     * Handle the launch countdown and ignition.
     */
    private void handleLaunchCountdown() {
        if (!world.isRemote && !(getControllingPassenger() instanceof EntityPlayer)) {
            cancelLaunch();
            return;
        }

        setLaunchCountdown(launchCountdown - 1);
        if (!world.isRemote) {
            int seconds = Math.max(1, (launchCountdown + 19) / 20);
            if (seconds <= 10 && seconds != lastCountdownMessageSecond) {
                lastCountdownMessageSecond = seconds;
                Entity passenger = getControllingPassenger();
                if (passenger instanceof EntityPlayer) {
                    ((EntityPlayer) passenger).sendStatusMessage(new TextComponentTranslation("message.ad_astra.rocket_countdown", seconds), true);
                }
            }
        }

        // Launch when countdown reaches zero
        if (launchCountdown <= 0) {
            launch();
        }
    }

    private void spawnCountdownSmoke() {
        if (launchCountdown % 20 != 0) {
            return;
        }
        world.spawnParticle(EnumParticleTypes.SMOKE_LARGE,
            posX + (rand.nextDouble() - 0.5) * width,
            posY,
            posZ + (rand.nextDouble() - 0.5) * width,
            0.0D, 0.04D, 0.0D);
    }

    private void cancelLaunch() {
        setLaunching(false);
        setLaunchCountdown(-1);
        launchKeyWasDown = false;
        lastCountdownMessageSecond = -1;
        if (fuelTank != null) {
            FluidStack current = fuelTank.getFluid();
            if (current != null) {
                fuelTank.fill(new FluidStack(current.getFluid(), Math.min(LAUNCH_FUEL_COST, fuelTank.getCapacity() - fuelTank.getFluidAmount())), true);
            } else {
                fuelTank.fill(new FluidStack(earth.terrarium.adastra.common.registry.ModFluids.FUEL, Math.min(LAUNCH_FUEL_COST, fuelTank.getCapacity())), true);
            }
        }
    }

    /**
     * Complete the launch and transition to flight mode.
     */
    private void launch() {
        setLaunching(false);
        setHasLaunched(true);
        setLaunchCountdown(-1);
        lastCountdownMessageSecond = -1;
        flightSpeed = 0.05f;
        if (Double.isNaN(launchStartY)) {
            launchStartY = posY;
        }

        // Play rocket sound
        if (!world.isRemote) {
            world.playSound(null, posX, posY, posZ, ModSounds.ROCKET, SoundCategory.NEUTRAL, 1.5f, 1.0f);
        }
    }

    /**
     * Handle rocket flight physics.
     */
    private void handleFlight() {
        if (planetSelectionOpened) {
            motionX = 0.0D;
            motionY = 0.0D;
            motionZ = 0.0D;
            return;
        }

        // Check for planet selection height
        if (posY >= getPlanetSelectionY() && !planetSelectionOpened) {
            if (!openPlanetSelection()) {
                explode();
            }
            return;
        }

        // Accelerate
        if (flightSpeed < MAX_FLIGHT_SPEED) {
            flightSpeed += LAUNCH_ACCELERATION;
        }

        // Apply upward motion
        motionY = flightSpeed;

        // Handle steering from passenger
        Entity passenger = getControllingPassenger();
        if (passenger != null) {
            float xxa = 0.0f;
            if (passenger instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) passenger;
                if (player.moveStrafing != 0.0f) {
                    xxa = -player.moveStrafing;
                }
            }

            if (xxa != 0) {
                angle += xxa * 1.0f;
            } else {
                angle *= 0.9f;
            }

            // Clamp turning
            angle = Math.max(-3, Math.min(3, angle));
            rotationYaw += angle;
        }

        // Burn entities below rocket
        if (!world.isRemote) {
            burnEntitiesBelow();
        }
    }

    private double getPlanetSelectionY() {
        if (Double.isNaN(launchStartY)) {
            return PLANET_SELECTION_HEIGHT;
        }
        return Math.max(PLANET_SELECTION_HEIGHT, launchStartY + PLANET_SELECTION_ASCENT);
    }

    private void sendLaunchFailure() {
        Entity passenger = getControllingPassenger();
        if (!(passenger instanceof EntityPlayer)) {
            return;
        }
        EntityPlayer player = (EntityPlayer) passenger;
        if (isLaunching || hasLaunched) {
            player.sendStatusMessage(new TextComponentTranslation("message.ad_astra.rocket_already_launching"), true);
        } else if (!canLaunchFromCurrentDimension()) {
            player.sendStatusMessage(new TextComponentTranslation("message.ad_astra.invalid_launching_dimension"), true);
        } else if (!launchpadBound && !isOnValidLaunchPad()) {
            player.sendStatusMessage(new TextComponentTranslation("message.ad_astra.rocket_requires_launch_pad"), true);
        } else if (!hasEnoughFuel(LAUNCH_FUEL_COST)) {
            player.sendStatusMessage(new TextComponentTranslation("message.ad_astra.rocket_not_enough_fuel"), true);
        } else {
            player.sendStatusMessage(new TextComponentTranslation("message.ad_astra.rocket_cannot_launch"), true);
        }
    }

    private void moveRocket() {
        if (!hasLaunched) {
            motionX = 0.0D;
            motionY = 0.0D;
            motionZ = 0.0D;
            return;
        }

        move(MoverType.SELF, motionX, motionY, motionZ);
        for (Entity passenger : getPassengers()) {
            updatePassenger(passenger);
        }
        motionX *= 0.95D;
        motionZ *= 0.95D;
    }

    /**
     * Open planet selection GUI for the pilot.
     */
    private boolean openPlanetSelection() {
        Entity passenger = getControllingPassenger();
        if (passenger instanceof EntityPlayerMP) {
            setPlanetSelectionOpened(true);
            syncPlayerCapability((EntityPlayerMP) passenger);
            NetworkHandler.CHANNEL.sendTo(
                new PacketOpenPlanetSelection(Math.max(1, getRocketTier()), getEntityId()),
                (EntityPlayerMP) passenger
            );
            return true;
        }
        return false;
    }

    private void syncPlayerCapability(EntityPlayerMP player) {
        IAdAstraPlayer capability = AdAstraCapabilities.getPlayer(player);
        if (capability instanceof AdAstraPlayer) {
            NetworkHandler.CHANNEL.sendTo(new PacketSyncPlayerCapability((AdAstraPlayer) capability), player);
        }
    }

    private void explode() {
        if (!world.isRemote) {
            world.createExplosion(this, posX, posY, posZ, 7.0F + getRocketTier() * 2.0F, OxygenApi.API.hasOxygen(world));
            setDead();
        }
    }

    /**
     * Spawn rocket exhaust particles.
     */
    private void spawnRocketParticles() {
        particleTick++;
        if (particleTick % 5 != 0) return;

        int flameCount = hasLaunched ? 3 : 1;
        int smokeCount = hasLaunched && particleTick % 15 == 0 ? 1 : 0;

        // Large flame particles for rocket exhaust
        for (int i = 0; i < flameCount; i++) {
            double offsetX = (rand.nextDouble() - 0.5) * 0.4;
            double offsetZ = (rand.nextDouble() - 0.5) * 0.4;
            ParticleHelper.spawnLargeFlame(world,
                posX + offsetX,
                posY - 0.5,
                posZ + offsetZ,
                (rand.nextDouble() - 0.5) * 0.15,
                -0.2 - rand.nextDouble() * 0.1,
                (rand.nextDouble() - 0.5) * 0.15);
        }

        // Large smoke particles
        for (int i = 0; i < smokeCount; i++) {
            double offsetX = (rand.nextDouble() - 0.5) * 0.5;
            double offsetZ = (rand.nextDouble() - 0.5) * 0.5;
            ParticleHelper.spawnLargeSmoke(world,
                posX + offsetX,
                posY - 0.5,
                posZ + offsetZ,
                (rand.nextDouble() - 0.5) * 0.08,
                -0.08 - rand.nextDouble() * 0.05,
                (rand.nextDouble() - 0.5) * 0.08);
        }
    }

    /**
     * Burn entities below the rocket during flight.
     */
    private void burnEntitiesBelow() {
        List<EntityLivingBase> entities = world.getEntitiesWithinAABB(
            EntityLivingBase.class,
            getEntityBoundingBox().grow(2.0D, 30.0D, 2.0D).offset(0.0D, -37.0D, 0.0D));
        Entity passenger = getControllingPassenger();
        for (EntityLivingBase entity : entities) {
            if (entity == passenger) {
                continue;
            }
            entity.setFire(10);
            entity.attackEntityFrom(ROCKET_FLAMES, 10.0F);
        }
    }

    @Override
    public ItemStack getDropStack() {
        ItemStack stack = super.getDropStack();
        if (!stack.isEmpty() && fuelTank != null && fuelTank.getFluidAmount() > 0) {
            NBTTagCompound tag = stack.hasTagCompound() ? stack.getTagCompound() : new NBTTagCompound();
            NBTTagCompound rocketTag = tag.hasKey("AdAstraRocket") ? tag.getCompoundTag("AdAstraRocket") : new NBTTagCompound();
            rocketTag.setTag("FuelTank", fuelTank.serializeNBT());
            tag.setTag("AdAstraRocket", rocketTag);
            stack.setTagCompound(tag);
        }
        return stack;
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        planetSelectionOpened = compound.getBoolean("PlanetSelectionOpened");
        flightSpeed = compound.getFloat("FlightSpeed");
        angle = compound.getFloat("Angle");
        launchpadBound = compound.getBoolean("LaunchpadBound");
        launchStartY = compound.hasKey("LaunchStartY") ? compound.getDouble("LaunchStartY") : Double.NaN;
        if (!world.isRemote) {
            dataManager.set(DATA_IS_LAUNCHING, isLaunching);
            dataManager.set(DATA_LAUNCH_COUNTDOWN, launchCountdown);
            dataManager.set(DATA_HAS_LAUNCHED, hasLaunched);
            dataManager.set(DATA_PLANET_SELECTION_OPENED, planetSelectionOpened);
        }
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setBoolean("PlanetSelectionOpened", planetSelectionOpened);
        compound.setFloat("FlightSpeed", flightSpeed);
        compound.setFloat("Angle", angle);
        compound.setBoolean("LaunchpadBound", launchpadBound);
        if (!Double.isNaN(launchStartY)) {
            compound.setDouble("LaunchStartY", launchStartY);
        }
    }

    @Override
    public boolean canPassengerSteer() {
        return !isLaunching && !hasLaunched && super.canPassengerSteer();
    }

    @Override
    public boolean shouldSit() {
        return false;
    }

    @Override
    public boolean zoomOutCameraInThirdPerson() {
        return true;
    }

    @Override
    public Vec3d getDismountPosition(EntityLivingBase passenger) {
        Vec3d offset = getHorizontalLookOffset(passenger, 2.0D);
        return lowerUntilSolid(new Vec3d(passenger.posX + offset.x, passenger.posY, passenger.posZ + offset.z), 6);
    }

    /**
     * Prevent dismounting during launch and flight.
     */
    @Override
    public boolean shouldDismountInWater(Entity rider) {
        return false;
    }
}
