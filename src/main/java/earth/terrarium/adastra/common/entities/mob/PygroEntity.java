package earth.terrarium.adastra.common.entities.mob;

import earth.terrarium.adastra.common.entities.AdAstraPlaceholderMob;
import earth.terrarium.adastra.common.registry.ModItems;
import earth.terrarium.adastra.common.registry.ModDimensions;
import net.minecraft.init.MobEffects;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

public class PygroEntity extends AdAstraPlaceholderMob {

    private static final int CONVERSION_TIME = 300;

    private int conversionTime;

    public PygroEntity(World world) {
        super(world);
        setSize(0.6f, 1.8f);
        isImmuneToFire = true;
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        tickConversion();
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setInteger("ConversionTime", conversionTime);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        conversionTime = compound.getInteger("ConversionTime");
    }

    public boolean isConverting() {
        return conversionTime > 0;
    }

    @Override
    protected double getMobMaxHealth() {
        return 24.0d;
    }

    @Override
    protected double getMobMovementSpeed() {
        return 0.45d;
    }

    @Override
    protected double getMobAttackDamage() {
        return 6.0d;
    }

    @Override
    protected double getMobFollowRange() {
        return 32.0d;
    }

    @Override
    protected boolean canDespawnNaturally() {
        return false;
    }

    @Override
    protected void dropFewItems(boolean wasRecentlyHit, int lootingModifier) {
        super.dropFewItems(wasRecentlyHit, lootingModifier);
        // Drops calorite nuggets (0-2, +looting)
        int count = rand.nextInt(3) + rand.nextInt(1 + lootingModifier);
        for (int i = 0; i < count; i++) {
            dropItem(ModItems.CALORITE_NUGGET, 1);
        }
    }

    private void tickConversion() {
        if (world.isRemote) {
            return;
        }
        if (isPiglinSafeDimension()) {
            conversionTime = 0;
            return;
        }
        if (++conversionTime >= CONVERSION_TIME) {
            finishConversion();
        }
    }

    private boolean isPiglinSafeDimension() {
        if (world.provider == null) {
            return false;
        }
        int dimension = world.provider.getDimension();
        return dimension == -1 || dimension == ModDimensions.MERCURY_ID || dimension == ModDimensions.VENUS_ID;
    }

    private void finishConversion() {
        ZombifiedPygroEntity zombified = new ZombifiedPygroEntity(world);
        zombified.copyLocationAndAnglesFrom(this);
        if (hasCustomName()) {
            zombified.setCustomNameTag(getCustomNameTag());
        }
        zombified.addPotionEffect(new PotionEffect(MobEffects.NAUSEA, 200, 0));
        world.spawnEntity(zombified);
        setDead();
    }
}
