package earth.terrarium.adastra.common.entities.mob;

import earth.terrarium.adastra.common.entities.AdAstraPlaceholderMob;
import earth.terrarium.adastra.common.registry.ModItems;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class LunarianWanderingTraderEntity extends AdAstraPlaceholderMob implements IMerchant {

    private EntityPlayer customer;
    private MerchantRecipeList buyingList;
    private int despawnDelay = 48000;

    public LunarianWanderingTraderEntity(World world) {
        super(world);
        setSize(0.6f, 1.95f);
        isImmuneToFire = true;
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();

        if (!world.isRemote && --despawnDelay <= 0) {
            if (customer == null) {
                setDead();
            }
        }
    }

    @Override
    public boolean processInteract(EntityPlayer player, EnumHand hand) {
        if (!world.isRemote && isEntityAlive()) {
            setCustomer(player);
            player.displayVillagerTradeGui(this);
            return true;
        }
        return super.processInteract(player, hand);
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setInteger("DespawnDelay", despawnDelay);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        if (compound.hasKey("DespawnDelay")) {
            despawnDelay = compound.getInteger("DespawnDelay");
        }
    }

    @Override
    protected boolean isHostileMob() {
        return false;
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
        return 0.23d;
    }

    @Override
    protected double getMobAttackDamage() {
        return 0.0d;
    }

    @Override
    protected double getMobFollowRange() {
        return 16.0d;
    }

    @Override
    protected boolean canDespawnNaturally() {
        return false;
    }

    @Override
    protected void dropFewItems(boolean wasRecentlyHit, int lootingModifier) {
        super.dropFewItems(wasRecentlyHit, lootingModifier);
        int count = 1 + rand.nextInt(3) + rand.nextInt(1 + lootingModifier);
        for (int i = 0; i < count; i++) {
            dropItem(Items.EMERALD, 1);
        }
    }

    @Override
    public void setCustomer(@Nullable EntityPlayer player) {
        this.customer = player;
    }

    @Nullable
    @Override
    public EntityPlayer getCustomer() {
        return this.customer;
    }

    @Override
    public MerchantRecipeList getRecipes(EntityPlayer player) {
        if (buyingList == null) {
            buyingList = new MerchantRecipeList();
            populateTradingList();
        }
        return buyingList;
    }

    @Override
    public void setRecipes(@Nullable MerchantRecipeList recipeList) {
        this.buyingList = recipeList;
    }

    @Override
    public void useRecipe(MerchantRecipe recipe) {
        recipe.incrementToolUses();
    }

    @Override
    public void verifySellingItem(ItemStack stack) {
    }

    @Override
    public World getWorld() {
        return this.world;
    }

    @Override
    public net.minecraft.util.math.BlockPos getPos() {
        return new net.minecraft.util.math.BlockPos(this);
    }

    @Override
    public net.minecraft.util.text.ITextComponent getDisplayName() {
        return new net.minecraft.util.text.TextComponentTranslation("entity.ad_astra.lunarian_wandering_trader.name");
    }

    private void populateTradingList() {
        // Rare/expensive trades for wandering trader
        buyingList.add(new MerchantRecipe(
            new ItemStack(Items.EMERALD, 8),
            new ItemStack(ModItems.ETRIUM_INGOT, 1)
        ));

        buyingList.add(new MerchantRecipe(
            new ItemStack(Items.EMERALD, 6),
            new ItemStack(ModItems.OSTRUM_INGOT, 1)
        ));

        buyingList.add(new MerchantRecipe(
            new ItemStack(Items.EMERALD, 4),
            new ItemStack(ModItems.STEEL_INGOT, 2)
        ));

        buyingList.add(new MerchantRecipe(
            new ItemStack(Items.EMERALD, 10),
            new ItemStack(ModItems.OXYGEN_GEAR, 1)
        ));

        buyingList.add(new MerchantRecipe(
            new ItemStack(Items.EMERALD, 3),
            new ItemStack(ModItems.CHEESE, 4)
        ));
    }
}
