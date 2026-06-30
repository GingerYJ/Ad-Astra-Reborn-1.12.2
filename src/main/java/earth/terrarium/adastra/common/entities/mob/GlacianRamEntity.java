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
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;

public class GlacianRamEntity extends AdAstraPlaceholderMob {

    private static final DataParameter<Boolean> SHEARED = EntityDataManager.createKey(GlacianRamEntity.class, DataSerializers.BOOLEAN);

    public GlacianRamEntity(World world) {
        super(world);
        setSize(0.9f, 1.3f);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        dataManager.register(SHEARED, false);
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
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        setSheared(compound.getBoolean("Sheared"));
    }

    public boolean isSheared() {
        return dataManager.get(SHEARED);
    }

    public void setSheared(boolean sheared) {
        dataManager.set(SHEARED, sheared);
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
}
