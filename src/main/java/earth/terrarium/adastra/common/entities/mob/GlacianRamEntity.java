package earth.terrarium.adastra.common.entities.mob;

import earth.terrarium.adastra.common.entities.ai.EntityAIEatPermafrost;
import earth.terrarium.adastra.common.registry.ModBlocks;
import earth.terrarium.adastra.common.registry.ModItems;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIFollowParent;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMate;
import net.minecraft.entity.ai.EntityAIPanic;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAITempt;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

public class GlacianRamEntity extends EntityAnimal {

    private static final DataParameter<Boolean> SHEARED = EntityDataManager.createKey(GlacianRamEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Integer> EAT_TIMER = EntityDataManager.createKey(GlacianRamEntity.class, DataSerializers.VARINT);

    public GlacianRamEntity(World world) {
        super(world);
        setSize(0.9f, 1.3f);
    }

    @Override
    protected void initEntityAI() {
        tasks.addTask(0, new EntityAISwimming(this));
        tasks.addTask(1, new EntityAIPanic(this, 1.25d));
        tasks.addTask(2, new EntityAIMate(this, 1.0d));
        tasks.addTask(3, new EntityAITempt(this, 1.1d, ModItems.ICE_SHARD, false));
        tasks.addTask(4, new EntityAIFollowParent(this, 1.1d));
        tasks.addTask(5, new EntityAIEatPermafrost(this));
        tasks.addTask(6, new EntityAIWander(this, 1.0d));
        tasks.addTask(7, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0f));
        tasks.addTask(8, new EntityAILookIdle(this));
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();

        int eatTimer = getEatTimer();
        if (eatTimer > 0) {
            setEatTimer(eatTimer - 1);
        }
    }

    @Override
    public void handleStatusUpdate(byte status) {
        if (status == 10) {
            setEatTimer(40);
        } else {
            super.handleStatusUpdate(status);
        }
    }

    @Override
    public boolean processInteract(EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (stack.getItem() == Items.BUCKET && !isChild()) {
            player.playSound(SoundEvents.ENTITY_COW_MILK, 1.0f, 1.0f);
            if (!player.capabilities.isCreativeMode) {
                stack.shrink(1);
                ItemStack milk = new ItemStack(Items.MILK_BUCKET);
                if (stack.isEmpty()) {
                    player.setHeldItem(hand, milk);
                } else if (!player.inventory.addItemStackToInventory(milk)) {
                    player.dropItem(milk, false);
                }
            }
            return true;
        }

        if (stack.getItem() == Items.SHEARS) {
            if (!world.isRemote && readyForShearing()) {
                shear();
                stack.damageItem(1, player);
            }
            return true;
        }

        return super.processInteract(player, hand);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        dataManager.register(SHEARED, false);
        dataManager.register(EAT_TIMER, 0);
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(16.0d);
        getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.20d);
        getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(20.0d);
    }

    @Override
    public boolean isBreedingItem(ItemStack stack) {
        return !stack.isEmpty() && stack.getItem() == ModItems.ICE_SHARD;
    }

    @Override
    public GlacianRamEntity createChild(EntityAgeable ageable) {
        return new GlacianRamEntity(world);
    }

    @Override
    protected boolean canDespawn() {
        return false;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_SHEEP_AMBIENT; // 1.12 fallback for goat
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_SHEEP_HURT; // 1.12 fallback for goat
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_SHEEP_DEATH; // 1.12 fallback for goat
    }

    public boolean isSheared() {
        return dataManager.get(SHEARED);
    }

    public void setSheared(boolean sheared) {
        dataManager.set(SHEARED, sheared);
    }

    public int getEatTimer() {
        return dataManager.get(EAT_TIMER);
    }

    public void setEatTimer(int timer) {
        dataManager.set(EAT_TIMER, timer);
    }

    public void onEatPermafrost() {
        setSheared(false);
        if (isChild()) {
            ageUp(60, true);
        }
    }

    public float getNeckAngle(float partialTicks) {
        int timer = getEatTimer();
        if (timer <= 0) {
            return 0.0F;
        } else if (timer >= 4 && timer <= 36) {
            return 1.0F;
        } else {
            return timer < 4 ? ((float) timer - partialTicks) / 4.0F : -((float) (timer - 40) - partialTicks) / 4.0F;
        }
    }

    public float getHeadAngle(float partialTicks) {
        int timer = getEatTimer();
        if (timer > 4 && timer <= 36) {
            float f = ((float) (timer - 4) - partialTicks) / 32.0F;
            return (float) (Math.PI / 5) + 0.21991149F * (float) Math.sin(f * 28.7F);
        } else {
            return timer > 0 ? (float) (Math.PI / 5) : rotationPitch * (float) (Math.PI / 180.0);
        }
    }

    private boolean readyForShearing() {
        return isEntityAlive() && !isSheared() && !isChild();
    }

    private void shear() {
        world.playSound(null, posX, posY, posZ, SoundEvents.ENTITY_SHEEP_SHEAR, SoundCategory.PLAYERS, 1.0f, 1.0f);
        setSheared(true);
        int count = 1 + rand.nextInt(3);
        for (int i = 0; i < count; i++) {
            entityDropItem(new ItemStack(ModBlocks.GLACIAN_FUR), 0.0f);
        }
    }

    @Override
    protected void dropFewItems(boolean wasRecentlyHit, int lootingModifier) {
        super.dropFewItems(wasRecentlyHit, lootingModifier);
        // Drops glacian fur if not sheared (1-2)
        if (!isSheared()) {
            int furCount = 1 + rand.nextInt(2);
            for (int i = 0; i < furCount; i++) {
                entityDropItem(new ItemStack(ModBlocks.GLACIAN_FUR), 0.0f);
            }
        }
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setBoolean("Sheared", isSheared());
        compound.setInteger("EatTimer", getEatTimer());
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        setSheared(compound.getBoolean("Sheared"));
        setEatTimer(compound.getInteger("EatTimer"));
    }
}
