package earth.terrarium.adastra.common.tile;

import earth.terrarium.adastra.common.blocks.AdAstraSensorBlock;
import earth.terrarium.adastra.common.blocks.AdAstraSensorBlock.DetectionType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

public class DetectorTileEntity extends AdAstraMachineTileEntity {

    public static final int SCAN_INTERVAL_TICKS = 40;
    public static final int DEFAULT_SCAN_RADIUS = 16;
    public static final int MAX_SCAN_RADIUS = 32;

    public static final int STATUS_UNKNOWN = 0;
    public static final int STATUS_OXYGEN_FOUND = 1;
    public static final int STATUS_OXYGEN_MISSING = 2;
    public static final int STATUS_NO_DISTRIBUTOR = 3;
    public static final int STATUS_TEMPERATURE_PLACEHOLDER = 4;
    public static final int STATUS_GRAVITY_PLACEHOLDER = 5;

    private boolean detected;
    private boolean powered;
    private boolean inverted;
    private int scanRadius = DEFAULT_SCAN_RADIUS;
    private int ticksUntilScan;
    private int detectionStatus = STATUS_UNKNOWN;
    private int nearbyDistributors;
    private int activeDistributors;

    public DetectorTileEntity() {
        super("detector", 0, 0, 0, 0, 0);
    }

    @Override
    protected void tickMachine() {
        if (world == null || pos == null) {
            return;
        }

        syncInvertedBlockState();
        syncBlockState(powered);
        if (ticksUntilScan > 0) {
            ticksUntilScan--;
            return;
        }

        DetectionResult result = scanForDetection();
        setDetectionState(result.detected, result.status, result.nearbyDistributors, result.activeDistributors);
        ticksUntilScan = SCAN_INTERVAL_TICKS;
        markDirty();
    }

    private DetectionResult scanForDetection() {
        IBlockState state = world.getBlockState(pos);
        if (!(state.getBlock() instanceof AdAstraSensorBlock)) {
            return DetectionResult.empty(STATUS_UNKNOWN);
        }

        DetectionType type = state.getValue(AdAstraSensorBlock.DETECTION_TYPE);
        if (type == DetectionType.OXYGEN) {
            return scanForOxygen();
        }
        if (type == DetectionType.TEMPERATURE) {
            return DetectionResult.empty(STATUS_TEMPERATURE_PLACEHOLDER);
        }
        if (type == DetectionType.GRAVITY) {
            return DetectionResult.empty(STATUS_GRAVITY_PLACEHOLDER);
        }
        return DetectionResult.empty(STATUS_UNKNOWN);
    }

    private DetectionResult scanForOxygen() {
        int radiusSq = scanRadius * scanRadius;
        int nearby = 0;
        int active = 0;
        boolean found = false;
        BlockPos.MutableBlockPos scanPos = new BlockPos.MutableBlockPos();

        for (int x = -scanRadius; x <= scanRadius; x++) {
            for (int y = -scanRadius; y <= scanRadius; y++) {
                for (int z = -scanRadius; z <= scanRadius; z++) {
                    if (x * x + y * y + z * z > radiusSq) {
                        continue;
                    }

                    scanPos.setPos(pos.getX() + x, pos.getY() + y, pos.getZ() + z);
                    if (!world.isBlockLoaded(scanPos)) {
                        continue;
                    }

                    TileEntity tile = world.getTileEntity(scanPos);
                    if (!(tile instanceof OxygenDistributorTileEntity)) {
                        continue;
                    }

                    nearby++;
                    OxygenDistributorTileEntity distributor = (OxygenDistributorTileEntity) tile;
                    if (distributor.isProvidingOxygen()) {
                        active++;
                    }
                    if (providesOxygenToSensor(distributor)) {
                        found = true;
                    }
                }
            }
        }

        if (found) {
            return new DetectionResult(true, STATUS_OXYGEN_FOUND, nearby, active);
        }
        return new DetectionResult(false, nearby > 0 ? STATUS_OXYGEN_MISSING : STATUS_NO_DISTRIBUTOR, nearby, active);
    }

    private boolean providesOxygenToSensor(OxygenDistributorTileEntity distributor) {
        if (distributor.isProvidingOxygen(pos)) {
            return true;
        }
        for (EnumFacing facing : EnumFacing.values()) {
            if (distributor.isProvidingOxygen(pos.offset(facing))) {
                return true;
            }
        }
        return false;
    }

    private void setDetectionState(boolean detected, int status, int nearbyDistributors, int activeDistributors) {
        boolean output = applyInversion(detected);
        boolean changed = this.detected != detected
            || this.powered != output
            || this.detectionStatus != status
            || this.nearbyDistributors != nearbyDistributors
            || this.activeDistributors != activeDistributors;

        this.detected = detected;
        this.powered = output;
        this.detectionStatus = status;
        this.nearbyDistributors = nearbyDistributors;
        this.activeDistributors = activeDistributors;

        syncBlockState(output);
        if (changed) {
            markDirty();
        }
    }

    private boolean applyInversion(boolean value) {
        return inverted ? !value : value;
    }

    private void syncInvertedBlockState() {
        IBlockState state = world.getBlockState(pos);
        if (state.getBlock() instanceof AdAstraSensorBlock && state.getValue(AdAstraSensorBlock.INVERTED) != inverted) {
            world.setBlockState(pos, state.withProperty(AdAstraSensorBlock.INVERTED, inverted), 3);
        }
    }

    private void syncBlockState(boolean output) {
        IBlockState state = world.getBlockState(pos);
        if (!(state.getBlock() instanceof AdAstraSensorBlock)) {
            return;
        }

        boolean needsLit = state.getValue(AdAstraSensorBlock.LIT) != output;
        boolean needsPowered = state.getValue(AdAstraSensorBlock.POWERED) != output;
        if (!needsLit && !needsPowered) {
            return;
        }

        IBlockState newState = state
            .withProperty(AdAstraSensorBlock.LIT, output)
            .withProperty(AdAstraSensorBlock.POWERED, output);
        world.setBlockState(pos, newState, 3);
        world.notifyNeighborsOfStateChange(pos, newState.getBlock(), false);
    }

    public boolean isDetected() {
        return detected;
    }

    public boolean isPowered() {
        return powered;
    }

    public boolean isInverted() {
        return inverted;
    }

    public void setInverted(boolean inverted) {
        if (this.inverted != inverted) {
            this.inverted = inverted;
            ticksUntilScan = 0;
            markDirty();
        }
    }

    public int getScanRadius() {
        return scanRadius;
    }

    public void setScanRadius(int radius) {
        int clamped = clamp(radius, 1, MAX_SCAN_RADIUS);
        if (scanRadius != clamped) {
            scanRadius = clamped;
            ticksUntilScan = 0;
            markDirty();
        }
    }

    public int getTicksUntilScan() {
        return ticksUntilScan;
    }

    public int getDetectionStatus() {
        return detectionStatus;
    }

    public int getNearbyDistributors() {
        return nearbyDistributors;
    }

    public int getActiveDistributors() {
        return activeDistributors;
    }

    public void requestImmediateScan() {
        ticksUntilScan = 0;
        markDirty();
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        detected = compound.getBoolean("Detected");
        powered = compound.getBoolean("Powered");
        inverted = compound.getBoolean("Inverted");
        scanRadius = compound.hasKey("ScanRadius") ? clamp(compound.getInteger("ScanRadius"), 1, MAX_SCAN_RADIUS) : DEFAULT_SCAN_RADIUS;
        ticksUntilScan = clamp(compound.getInteger("ScanCooldown"), 0, SCAN_INTERVAL_TICKS);
        detectionStatus = Math.max(0, compound.getInteger("DetectionStatus"));
        nearbyDistributors = Math.max(0, compound.getInteger("NearbyDistributors"));
        activeDistributors = Math.max(0, compound.getInteger("ActiveDistributors"));
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setBoolean("Detected", detected);
        compound.setBoolean("Powered", powered);
        compound.setBoolean("Inverted", inverted);
        compound.setInteger("ScanRadius", scanRadius);
        compound.setInteger("ScanCooldown", ticksUntilScan);
        compound.setInteger("DetectionStatus", detectionStatus);
        compound.setInteger("NearbyDistributors", nearbyDistributors);
        compound.setInteger("ActiveDistributors", activeDistributors);
        return compound;
    }

    @Override
    public int getField(int id) {
        if (id == 4) {
            return detected ? 1 : 0;
        }
        if (id == 5) {
            return powered ? 1 : 0;
        }
        if (id == 6) {
            return scanRadius;
        }
        if (id == 7) {
            return ticksUntilScan;
        }
        if (id == 8) {
            return getDetectionTypeMetadata();
        }
        if (id == 9) {
            return detectionStatus;
        }
        if (id == 10) {
            return nearbyDistributors;
        }
        if (id == 11) {
            return activeDistributors;
        }
        if (id == 12) {
            return inverted ? 1 : 0;
        }
        return super.getField(id);
    }

    @Override
    public void setField(int id, int value) {
        if (id == 6) {
            setScanRadius(value);
            return;
        }
        if (id == 12) {
            setInverted(value != 0);
            return;
        }
        super.setField(id, value);
    }

    @Override
    public int getFieldCount() {
        return 13;
    }

    private int getDetectionTypeMetadata() {
        if (world == null || pos == null) {
            return DetectionType.OXYGEN.getMetadata();
        }
        IBlockState state = world.getBlockState(pos);
        if (state.getBlock() instanceof AdAstraSensorBlock) {
            return state.getValue(AdAstraSensorBlock.DETECTION_TYPE).getMetadata();
        }
        return DetectionType.OXYGEN.getMetadata();
    }

    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    private static class DetectionResult {
        private final boolean detected;
        private final int status;
        private final int nearbyDistributors;
        private final int activeDistributors;

        private DetectionResult(boolean detected, int status, int nearbyDistributors, int activeDistributors) {
            this.detected = detected;
            this.status = status;
            this.nearbyDistributors = nearbyDistributors;
            this.activeDistributors = activeDistributors;
        }

        private static DetectionResult empty(int status) {
            return new DetectionResult(false, status, 0, 0);
        }
    }
}
