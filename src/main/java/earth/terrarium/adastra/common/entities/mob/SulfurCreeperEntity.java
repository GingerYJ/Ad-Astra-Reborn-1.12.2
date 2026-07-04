package earth.terrarium.adastra.common.entities.mob;

import earth.terrarium.adastra.common.entities.AdAstraPlaceholderMob;
import earth.terrarium.adastra.common.items.GasTankItem;
import earth.terrarium.adastra.common.registry.ModItems;
import net.minecraft.entity.EntityAreaEffectCloud;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import java.util.Collection;
import java.util.List;

public class SulfurCreeperEntity extends AdAstraPlaceholderMob {

    private static final DataParameter<Integer> FUSE = EntityDataManager.createKey(SulfurCreeperEntity.class, DataSerializers.VARINT);
    private static final DataParameter<Boolean> POWERED = EntityDataManager.createKey(SulfurCreeperEntity.class, DataSerializers.BOOLEAN);
    private static final int MAX_FUSE = 30;
    private static final double START_FUSE_DISTANCE_SQ = 9.0d;
    private static final double STOP_FUSE_DISTANCE_SQ = 49.0d;
    private static final float EXPLOSION_STRENGTH = 3.0f;
    private static final double OXYGEN_DRAIN_RADIUS = 7.0d;
    private static final int OXYGEN_DRAIN_PER_BLOCK = 125;

    private int lastFuse;
    private int fuse;

    public SulfurCreeperEntity(World world) {
        super(world);
        setSize(0.6f, 1.7f);
        isImmuneToFire = true;
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        dataManager.register(FUSE, 0);
        dataManager.register(POWERED, false);
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
        lastFuse = getFuse();
        super.onLivingUpdate();

        if (!world.isRemote) {
            updateFuse();
        } else {
            fuse = dataManager.get(FUSE);
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
        return 0.35d;
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
        compound.setInteger("Fuse", getFuse());
        compound.setBoolean("powered", getPowered());
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        setFuse(compound.getInteger("Fuse"));
        setPowered(compound.getBoolean("powered") || compound.getBoolean("Powered"));
    }

    @Override
    public void onStruckByLightning(EntityLightningBolt lightningBolt) {
        super.onStruckByLightning(lightningBolt);
        setPowered(true);
    }

    public int getFuse() {
        return world.isRemote ? dataManager.get(FUSE) : fuse;
    }

    public boolean getPowered() {
        return dataManager.get(POWERED);
    }

    public float getCreeperFlashIntensity(float partialTicks) {
        return ((float) lastFuse + (float) (getFuse() - lastFuse) * partialTicks) / (float) (MAX_FUSE - 2);
    }

    private void setFuse(int fuse) {
        this.fuse = MathHelper.clamp(fuse, 0, MAX_FUSE);
        dataManager.set(FUSE, this.fuse);
    }

    private void setPowered(boolean powered) {
        dataManager.set(POWERED, powered);
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
            setFuse(fuse + 1);

            if (fuse >= MAX_FUSE) {
                explode();
            }
        } else if (target == null || !target.isEntityAlive() || getDistanceSq(target) > STOP_FUSE_DISTANCE_SQ) {
            setFuse(fuse - 1);
        }
    }

    private void explode() {
        if (!isDead) {
            float power = getPowered() ? 2.0f : 1.0f;
            drainOxygenFromNearbySpaceSuits();
            world.createExplosion(this, posX, posY, posZ, EXPLOSION_STRENGTH * power, true);
            spawnEffectCloud();
            setDead();
        }
    }

    private void drainOxygenFromNearbySpaceSuits() {
        AxisAlignedBB area = getEntityBoundingBox().grow(OXYGEN_DRAIN_RADIUS);
        List<EntityPlayer> players = world.getEntitiesWithinAABB(EntityPlayer.class, area);
        for (EntityPlayer player : players) {
            if (!hasFullSpaceSuit(player)) {
                continue;
            }

            double distance = getDistance(player);
            if (distance > OXYGEN_DRAIN_RADIUS) {
                continue;
            }

            int amount = Math.max(0, (int) ((OXYGEN_DRAIN_RADIUS - distance) * OXYGEN_DRAIN_PER_BLOCK));
            if (amount <= 0) {
                continue;
            }

            ItemStack chest = player.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
            if (GasTankItem.drainOxygen(chest, amount, false) > 0) {
                player.inventory.markDirty();
            }
        }
    }

    private boolean hasFullSpaceSuit(EntityPlayer player) {
        return hasArmorSet(player, ModItems.SPACE_HELMET, ModItems.SPACE_SUIT, ModItems.SPACE_PANTS, ModItems.SPACE_BOOTS)
            || hasArmorSet(player, ModItems.NETHERITE_SPACE_HELMET, ModItems.NETHERITE_SPACE_SUIT, ModItems.NETHERITE_SPACE_PANTS, ModItems.NETHERITE_SPACE_BOOTS)
            || hasArmorSet(player, ModItems.JET_SUIT_HELMET, ModItems.JET_SUIT, ModItems.JET_SUIT_PANTS, ModItems.JET_SUIT_BOOTS);
    }

    private boolean hasArmorSet(EntityPlayer player, Item helmet, Item chest, Item legs, Item boots) {
        return player.getItemStackFromSlot(EntityEquipmentSlot.HEAD).getItem() == helmet
            && player.getItemStackFromSlot(EntityEquipmentSlot.CHEST).getItem() == chest
            && player.getItemStackFromSlot(EntityEquipmentSlot.LEGS).getItem() == legs
            && player.getItemStackFromSlot(EntityEquipmentSlot.FEET).getItem() == boots;
    }

    private void spawnEffectCloud() {
        Collection<PotionEffect> effects = getActivePotionEffects();
        if (effects.isEmpty()) {
            return;
        }

        EntityAreaEffectCloud cloud = new EntityAreaEffectCloud(world, posX, posY, posZ);
        cloud.setRadius(2.5f);
        cloud.setRadiusOnUse(-0.5f);
        cloud.setWaitTime(10);
        cloud.setDuration(cloud.getDuration() / 2);
        cloud.setRadiusPerTick(-cloud.getRadius() / (float) cloud.getDuration());
        for (PotionEffect effect : effects) {
            cloud.addEffect(new PotionEffect(effect));
        }
        world.spawnEntity(cloud);
    }

    @Override
    protected void dropFewItems(boolean wasRecentlyHit, int lootingModifier) {
        super.dropFewItems(wasRecentlyHit, lootingModifier);
        // Drops sulfur (gunpowder) (0-2, +looting)
        int count = rand.nextInt(3) + rand.nextInt(1 + lootingModifier);
        for (int i = 0; i < count; i++) {
            dropItem(net.minecraft.init.Items.GUNPOWDER, 1);
        }
    }
}
