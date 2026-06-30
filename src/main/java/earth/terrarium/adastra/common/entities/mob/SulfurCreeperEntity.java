package earth.terrarium.adastra.common.entities.mob;

import earth.terrarium.adastra.common.entities.AdAstraPlaceholderMob;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class SulfurCreeperEntity extends AdAstraPlaceholderMob {

    private static final int MAX_FUSE = 30;
    private static final double START_FUSE_DISTANCE_SQ = 9.0d;
    private static final double STOP_FUSE_DISTANCE_SQ = 49.0d;
    private static final float EXPLOSION_STRENGTH = 3.0f;

    private int fuse;

    public SulfurCreeperEntity(World world) {
        super(world);
        setSize(0.6f, 1.7f);
        isImmuneToFire = true;
    }

    @Override
    protected void initEntityAI() {
        tasks.addTask(0, new EntityAISwimming(this));
        tasks.addTask(5, new EntityAIWander(this, getWanderSpeed()));
        tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, getWatchDistance()));
        targetTasks.addTask(1, new EntityAINearestAttackableTarget<EntityPlayer>(this, EntityPlayer.class, true));
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();

        if (!world.isRemote) {
            updateFuse();
        }
    }

    @Override
    protected boolean usesMeleeAttack() {
        return false;
    }

    @Override
    protected double getMobMaxHealth() {
        return 20.0d;
    }

    @Override
    protected double getMobMovementSpeed() {
        return 0.25d;
    }

    @Override
    protected double getMobAttackDamage() {
        return 0.0d;
    }

    @Override
    protected double getMobFollowRange() {
        return 32.0d;
    }

    @Override
    protected double getWanderSpeed() {
        return 0.75d;
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setInteger("Fuse", fuse);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        fuse = compound.getInteger("Fuse");
    }

    public int getFuse() {
        return fuse;
    }

    private void updateFuse() {
        EntityLivingBase target = getAttackTarget();
        if (target == null) {
            target = world.getNearestAttackablePlayer(this, getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).getAttributeValue(), 0.0d);
            if (target != null) {
                setAttackTarget(target);
            }
        }

        if (target != null && target.isEntityAlive() && getDistanceSq(target) <= START_FUSE_DISTANCE_SQ) {
            getNavigator().clearPath();
            getLookHelper().setLookPositionWithEntity(target, 30.0f, 30.0f);
            fuse++;

            if (fuse >= MAX_FUSE) {
                explode();
            }
        } else if (target == null || !target.isEntityAlive() || getDistanceSq(target) > STOP_FUSE_DISTANCE_SQ) {
            fuse = Math.max(0, fuse - 1);
        }
    }

    private void explode() {
        if (!isDead) {
            world.createExplosion(this, posX, posY, posZ, EXPLOSION_STRENGTH, true);
            setDead();
        }
    }
}
