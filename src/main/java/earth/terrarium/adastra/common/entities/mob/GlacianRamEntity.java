package earth.terrarium.adastra.common.entities.mob;

import earth.terrarium.adastra.common.entities.AdAstraPlaceholderMob;
import earth.terrarium.adastra.common.registry.ModBlocks;
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
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class GlacianRamEntity extends AdAstraPlaceholderMob {

    private static final DataParameter<Boolean> SHEARED = EntityDataManager.createKey(GlacianRamEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Integer> EAT_TIMER = EntityDataManager.createKey(GlacianRamEntity.class, DataSerializers.VARINT);

    public GlacianRamEntity(World world) {
        super(world);
        setSize(0.9f, 1.3f);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        dataManager.register(SHEARED, false);
        dataManager.register(EAT_TIMER, 0);
    }

    @Override
    protected boolean isHostileMob() {
        return false;
    }

    @Override
    protected boolean isNeutralMob() {
        return false;
    }

    @Override
    protected boolean usesMeleeAttack() {
        return false;
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();

        // Eating animation timer
        int eatTimer = getEatTimer();
        if (eatTimer > 0) {
            setEatTimer(eatTimer - 1);
        }

        // Occasionally attempt to eat permafrost blocks
        if (!world.isRemote && !isSheared() && rand.nextInt(1000) == 0) {
            BlockPos pos = getPosition();
            BlockPos belowPos = pos.down();

            if (world.getBlockState(belowPos).getBlock() == ModBlocks.PERMAFROST) {
                if (world.getGameRules().getBoolean("mobGriefing")) {
                    world.setBlockToAir(belowPos);
                    setEatTimer(40);
                    world.setEntityState(this, (byte) 10);
                    // Regrow fur after eating
                    setSheared(false);
                }
            }
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
    protected double getMobMaxHealth() {
        return 16.0d;
    }

    @Override
    protected double getMobMovementSpeed() {
        return 0.20d;
    }

    @Override
    protected double getMobAttackDamage() {
        return 0.0d;
    }

    @Override
    protected double getMobFollowRange() {
        return 20.0d;
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
}
