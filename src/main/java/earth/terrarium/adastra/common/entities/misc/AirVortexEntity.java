package earth.terrarium.adastra.common.entities.misc;

import earth.terrarium.adastra.common.entities.AdAstraPlaceholderEntity;
import earth.terrarium.adastra.common.registry.ModBlocks;
import earth.terrarium.adastra.common.tile.OxygenDistributorTileEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;

public class AirVortexEntity extends AdAstraPlaceholderEntity {

    public static final int LIFE = 600;

    private static final String TAG_HAS_SOURCE = "HasSource";
    private static final String TAG_SOURCE_X = "SourceX";
    private static final String TAG_SOURCE_Y = "SourceY";
    private static final String TAG_SOURCE_Z = "SourceZ";
    private static final String TAG_RADIUS = "Radius";
    private static final int DEFAULT_RADIUS = 8;

    private BlockPos source;
    private int radius = DEFAULT_RADIUS;

    public AirVortexEntity(World world) {
        super(world);
        setSize(0.5f, 0.5f);
    }

    public AirVortexEntity(World world, BlockPos source, int radius) {
        this(world);
        this.source = source;
        this.radius = Math.max(1, radius);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        if (world.isRemote) {
            return;
        }

        OxygenDistributorTileEntity distributor = getSourceDistributor();
        if (ticksExisted >= LIFE
            || distributor == null
            || !distributor.isProvidingOxygen(getPosition())
            || distributor.getDistributedBlocksCount() < distributor.getDistributedBlocksLimit()) {
            setDead();
            return;
        }

        AxisAlignedBB bounds = new AxisAlignedBB(source).grow(radius);
        List<Entity> entities = world.getEntitiesWithinAABBExcludingEntity(this, bounds);
        for (Entity entity : entities) {
            if (canPull(entity) && distributor.isProvidingOxygen(entity.getPosition())) {
                applyForce(entity);
            }
        }
    }

    @Override
    public boolean isEntityInvulnerable(DamageSource source) {
        return true;
    }

    @Override
    public boolean canBeCollidedWith() {
        return false;
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        if (compound.getBoolean(TAG_HAS_SOURCE)) {
            source = new BlockPos(
                compound.getInteger(TAG_SOURCE_X),
                compound.getInteger(TAG_SOURCE_Y),
                compound.getInteger(TAG_SOURCE_Z));
        } else {
            source = null;
        }
        radius = Math.max(1, compound.hasKey(TAG_RADIUS) ? compound.getInteger(TAG_RADIUS) : DEFAULT_RADIUS);
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setBoolean(TAG_HAS_SOURCE, source != null);
        if (source != null) {
            compound.setInteger(TAG_SOURCE_X, source.getX());
            compound.setInteger(TAG_SOURCE_Y, source.getY());
            compound.setInteger(TAG_SOURCE_Z, source.getZ());
        }
        compound.setInteger(TAG_RADIUS, radius);
    }

    public BlockPos getSourcePos() {
        return source;
    }

    private OxygenDistributorTileEntity getSourceDistributor() {
        if (source == null || !world.isBlockLoaded(source) || world.getBlockState(source).getBlock() != ModBlocks.OXYGEN_DISTRIBUTOR) {
            return null;
        }

        TileEntity tile = world.getTileEntity(source);
        return tile instanceof OxygenDistributorTileEntity ? (OxygenDistributorTileEntity) tile : null;
    }

    private boolean canPull(Entity entity) {
        if (entity == null || entity.isDead || entity.noClip) {
            return false;
        }
        if (entity instanceof AirVortexEntity) {
            return false;
        }
        return !(entity instanceof EntityPlayer) || !((EntityPlayer) entity).capabilities.isFlying;
    }

    private void applyForce(Entity entity) {
        BlockPos targetPos = getPosition().down(2);
        Vec3d target = new Vec3d(targetPos.getX() + 0.5d, targetPos.getY() + 0.5d, targetPos.getZ() + 0.5d);
        Vec3d delta = target.subtract(entity.getPositionVector());
        double length = Math.sqrt(delta.x * delta.x + delta.y * delta.y + delta.z * delta.z);
        if (length < 0.0001d) {
            return;
        }

        int time = MathHelper.clamp(ticksExisted, 0, LIFE);
        double altitude = 1000.0d * Math.exp(-0.005d * time) + LIFE;
        double power = Math.max(0.30d, altitude * altitude / (double) (LIFE * LIFE));
        Vec3d force = delta.normalize().scale(power);
        entity.addVelocity(force.x, force.y, force.z);
        entity.velocityChanged = true;
    }
}
