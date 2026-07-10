package earth.terrarium.adastra.common.tile;

import earth.terrarium.adastra.common.config.AdAstraConfig;
import earth.terrarium.adastra.common.registry.ModSounds;
import earth.terrarium.adastra.common.systems.GravitySystem;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;

import java.util.HashSet;
import java.util.Set;

public class GravityNormalizerTileEntity extends AdAstraMachineTileEntity {

    private static final int MAX_DISTRIBUTION_BLOCKS = AdAstraConfig.maxDistributionBlocks;
    private static final int TARGET_GRAVITY_SCALE = 1000;
    private static final int MAX_WORKING_RADIUS = calculateMaxRadius(MAX_DISTRIBUTION_BLOCKS);
    private static final int DEFAULT_WORKING_RADIUS = MAX_WORKING_RADIUS;
    private static final int SOUND_INTERVAL = 120;

    private boolean normalizingGravity;
    private int workingRadius = DEFAULT_WORKING_RADIUS;
    private int plannedBlocksCount;
    private int distributedBlocksCount;
    private int energyPerTick;
    private int ticksUntilRefresh;
    private int soundCooldown;
    private float targetGravity = 1.0f;
    private float animation;
    private float lastAnimation;
    private final Set<BlockPos> lastDistributedBlocks = new HashSet<>();
    private boolean coverageDirty = true;

    public GravityNormalizerTileEntity() {
        super("gravity_normalizer", 1, DESH_ENERGY, DESH_IO, 0, 0);
        setAllSideModes(SideConfigType.ENERGY, AdAstraSideMode.PULL);
    }

    @Override
    public void update() {
        if (world != null && world.isRemote) {
            lastAnimation = animation;
            if (isLit()) {
                animation = (animation + 10.0f) % 360.0f;
            }
            return;
        }
        super.update();
    }

    @Override
    protected void tickMachine() {
        if (soundCooldown > 0) {
            soundCooldown--;
        }
        if (energy == null || !canFunction()) {
            stopNormalizing();
            return;
        }

        refreshDistributionEstimateIfNeeded();
        int requiredEnergy = calculateEnergyPerTick(plannedBlocksCount);
        if (!canMaintainDistribution(requiredEnergy)) {
            stopNormalizing();
            return;
        }

        energy.internalExtractEnergy(requiredEnergy, false);

        // Clear gravity cache when normalizer starts working
        if (!normalizingGravity) {
            GravitySystem.clearCache();
        }

        normalizingGravity = true;
        distributedBlocksCount = plannedBlocksCount;
        energyPerTick = requiredEnergy;
        setLit(true);
        playWorkSound();
        if (tickCounter % 20 == 0) {
            markDirty();
        }
    }

    private void playWorkSound() {
        if (soundCooldown <= 0) {
            playMachineSound(ModSounds.GRAVITY_NORMALIZER_IDLE, 0.35f, 0.9f + world.rand.nextFloat() * 0.2f);
            soundCooldown = SOUND_INTERVAL;
        }
    }

    private void refreshDistributionEstimateIfNeeded() {
        if (!coverageDirty) {
            return;
        }
        updateGravityPositionsSpherical();
        plannedBlocksCount = lastDistributedBlocks.size();
        ticksUntilRefresh = Math.max(1, AdAstraConfig.distributionRefreshRate);
        coverageDirty = false;
    }

    private void updateGravityPositionsSpherical() {
        if (world == null || world.isRemote || pos == null) {
            return;
        }

        Set<BlockPos> positions = new HashSet<>();
        int radiusSq = workingRadius * workingRadius;

        for (int x = -workingRadius; x <= workingRadius; x++) {
            for (int y = -workingRadius; y <= workingRadius; y++) {
                for (int z = -workingRadius; z <= workingRadius; z++) {
                    if (x * x + y * y + z * z <= radiusSq) {
                        positions.add(pos.add(x, y, z));
                        if (positions.size() >= MAX_DISTRIBUTION_BLOCKS) {
                            break;
                        }
                    }
                }
                if (positions.size() >= MAX_DISTRIBUTION_BLOCKS) {
                    break;
                }
            }
            if (positions.size() >= MAX_DISTRIBUTION_BLOCKS) {
                break;
            }
        }

        updateGravityPositions(positions);
    }

    private void updateGravityPositions(Set<BlockPos> newPositions) {
        if (world == null || world.isRemote) {
            return;
        }

        Set<BlockPos> toRemove = new HashSet<>(lastDistributedBlocks);
        toRemove.removeAll(newPositions);
        boolean changed = !toRemove.isEmpty();
        if (!toRemove.isEmpty()) {
            GravitySystem.removeGravity(world, toRemove);
        }

        Set<BlockPos> toAdd = new HashSet<>(newPositions);
        toAdd.removeAll(lastDistributedBlocks);
        changed |= !toAdd.isEmpty();
        if (!toAdd.isEmpty()) {
            GravitySystem.setGravity(world, toAdd, targetGravity);
        }

        lastDistributedBlocks.clear();
        lastDistributedBlocks.addAll(newPositions);
        if (changed) {
            GravitySystem.clearCache();
        }
    }

    private boolean canMaintainDistribution(int requiredEnergy) {
        return plannedBlocksCount > 0 && energy.internalExtractEnergy(requiredEnergy, true) >= requiredEnergy;
    }

    @Override
    protected boolean hasOngoingWork() {
        return normalizingGravity;
    }

    private void stopNormalizing() {
        if (normalizingGravity || distributedBlocksCount != 0 || energyPerTick != 0 || !lastDistributedBlocks.isEmpty()) {
            normalizingGravity = false;
            distributedBlocksCount = 0;
            energyPerTick = 0;
            clearGravityBlocks();
            markDirty();

            // Clear gravity cache when normalizer stops working
            GravitySystem.clearCache();
        }
        setLit(false);
    }

    private void clearGravityBlocks() {
        if (world != null && !world.isRemote && !lastDistributedBlocks.isEmpty()) {
            GravitySystem.removeGravity(world, lastDistributedBlocks);
            lastDistributedBlocks.clear();
        }
        coverageDirty = true;
    }

    private int calculateEnergyPerTick(int blocksCount) {
        if (blocksCount <= 0) {
            return 0;
        }
        int baseEnergy = Math.max(1, blocksCount / 24);
        return (int) (baseEnergy * AdAstraConfig.gravityNormalizerEnergyMultiplier);
    }

    public boolean isNormalizingGravity() {
        return normalizingGravity && canFunction();
    }

    public boolean isNormalizingGravity(BlockPos target) {
        if (!isNormalizingGravity() || pos == null || target == null) {
            return false;
        }

        long dx = target.getX() - pos.getX();
        long dy = target.getY() - pos.getY();
        long dz = target.getZ() - pos.getZ();
        long radius = workingRadius;
        return dx * dx + dy * dy + dz * dz <= radius * radius;
    }

    public int getWorkingRadius() {
        return workingRadius;
    }

    public void setWorkingRadius(int radius) {
        int clamped = clamp(radius, 1, MAX_WORKING_RADIUS);
        if (workingRadius != clamped) {
            workingRadius = clamped;
            ticksUntilRefresh = 0;
            coverageDirty = true;
            markDirty();
        }
    }

    public int getDistributedBlocksCount() {
        return isNormalizingGravity() ? distributedBlocksCount : 0;
    }

    public int distributedBlocksCount() {
        return getDistributedBlocksCount();
    }

    public int getDistributedBlocksLimit() {
        return MAX_DISTRIBUTION_BLOCKS;
    }

    public int distributedBlocksLimit() {
        return getDistributedBlocksLimit();
    }

    public int getEnergyPerTick() {
        return isNormalizingGravity() ? energyPerTick : 0;
    }

    public int energyPerTick() {
        return getEnergyPerTick();
    }

    public float getTargetGravity() {
        return targetGravity;
    }

    public float targetGravity() {
        return targetGravity;
    }

    public void setTargetGravity(float targetGravity) {
        float clamped = Math.max(0.0f, Math.min(2.0f, targetGravity));
        if (this.targetGravity != clamped) {
            this.targetGravity = clamped;
            if (world != null && !world.isRemote && !lastDistributedBlocks.isEmpty()) {
                GravitySystem.setGravity(world, lastDistributedBlocks, clamped);
                GravitySystem.clearCache();
            }
            markDirty();
        }
    }

    @Override
    public void invalidate() {
        super.invalidate();
        clearGravityBlocks();
    }

    @Override
    public void onChunkUnload() {
        super.onChunkUnload();
        clearGravityBlocks();
    }

    public float getAnimation() {
        return animation;
    }

    public float getLastAnimation() {
        return lastAnimation;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        normalizingGravity = compound.getBoolean("NormalizingGravity");
        workingRadius = compound.hasKey("WorkingRadius") ? clamp(compound.getInteger("WorkingRadius"), 1, MAX_WORKING_RADIUS) : DEFAULT_WORKING_RADIUS;
        plannedBlocksCount = compound.hasKey("PlannedBlocksCount") ? clamp(compound.getInteger("PlannedBlocksCount"), 0, MAX_DISTRIBUTION_BLOCKS) : countBlocksInRadius(workingRadius);
        distributedBlocksCount = compound.hasKey("DistributedBlocksCount") ? clamp(compound.getInteger("DistributedBlocksCount"), 0, MAX_DISTRIBUTION_BLOCKS) : 0;
        energyPerTick = Math.max(0, compound.getInteger("EnergyPerTick"));
        ticksUntilRefresh = 0;
        coverageDirty = true;
        targetGravity = compound.hasKey("TargetGravity") ? compound.getFloat("TargetGravity") : 1.0f;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setBoolean("NormalizingGravity", normalizingGravity);
        compound.setInteger("WorkingRadius", workingRadius);
        compound.setInteger("PlannedBlocksCount", plannedBlocksCount);
        compound.setInteger("DistributedBlocksCount", distributedBlocksCount);
        compound.setInteger("EnergyPerTick", energyPerTick);
        compound.setInteger("DistributionRefreshTicks", ticksUntilRefresh);
        compound.setFloat("TargetGravity", targetGravity);
        return compound;
    }

    @Override
    public int getField(int id) {
        if (id == 4) {
            return isNormalizingGravity() ? 1 : 0;
        }
        if (id == 5) {
            return workingRadius;
        }
        if (id == 6) {
            return getDistributedBlocksCount();
        }
        if (id == 7) {
            return MAX_DISTRIBUTION_BLOCKS;
        }
        if (id == 8) {
            return getEnergyPerTick();
        }
        if (id == 9) {
            return Math.round(targetGravity * TARGET_GRAVITY_SCALE);
        }
        return super.getField(id);
    }

    @Override
    public void setField(int id, int value) {
        if (id == 5) {
            setWorkingRadius(value);
            return;
        }
        if (id == 9) {
            setTargetGravity(value / (float) TARGET_GRAVITY_SCALE);
            return;
        }
        super.setField(id, value);
    }

    @Override
    public int getFieldCount() {
        return 10;
    }

    private static int calculateMaxRadius(int blockLimit) {
        int radius = 1;
        while (countBlocksInRadius(radius + 1) <= blockLimit) {
            radius++;
        }
        return radius;
    }

    private static int countBlocksInRadius(int radius) {
        int radiusSq = radius * radius;
        int count = 0;
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    if (x * x + y * y + z * z <= radiusSq) {
                        count++;
                    }
                }
            }
        }
        return count;
    }

    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }
}
