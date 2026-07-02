package earth.terrarium.adastra.common.entities.mob;

import earth.terrarium.adastra.common.entities.AdAstraPlaceholderMob;
import earth.terrarium.adastra.common.registry.ModItems;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class LunarianEntity extends AdAstraPlaceholderMob implements IMerchant {

    private EntityPlayer customer;
    private MerchantRecipeList buyingList;

    public LunarianEntity(World world) {
        super(world);
        setSize(0.75f, 2.5f);
    }

    @Override
    protected void initEntityAI() {
        super.initEntityAI();
        tasks.addTask(1, new EntityAIAvoidEntity<CorruptedLunarianEntity>(this, CorruptedLunarianEntity.class, 15.0f, 0.5, 0.5));
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
        return 0.50d;
    }

    @Override
    protected double getMobAttackDamage() {
        return 0.0d;
    }

    @Override
    protected double getMobFollowRange() {
        return 48.0d;
    }

    @Override
    protected void dropFewItems(boolean wasRecentlyHit, int lootingModifier) {
        super.dropFewItems(wasRecentlyHit, lootingModifier);
        int emeraldCount = rand.nextInt(3) + rand.nextInt(1 + lootingModifier);
        for (int i = 0; i < emeraldCount; i++) {
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
        return new net.minecraft.util.text.TextComponentTranslation("entity.ad_astra.lunarian.name");
    }

    private void populateTradingList() {
        // Emerald -> Ice Shard (1-2)
        buyingList.add(new MerchantRecipe(
            new ItemStack(Items.EMERALD, 3),
            new ItemStack(ModItems.ICE_SHARD, 1 + rand.nextInt(2))
        ));

        // Emerald -> Cheese (1-3)
        buyingList.add(new MerchantRecipe(
            new ItemStack(Items.EMERALD, 2),
            new ItemStack(ModItems.CHEESE, 1 + rand.nextInt(3))
        ));

        // Moon resources -> Emerald
        buyingList.add(new MerchantRecipe(
            new ItemStack(ModItems.ICE_SHARD, 4),
            new ItemStack(Items.EMERALD, 1)
        ));

        // Desh Ingot <-> Emerald
        buyingList.add(new MerchantRecipe(
            new ItemStack(Items.EMERALD, 5),
            new ItemStack(ModItems.DESH_INGOT, 1)
        ));

        buyingList.add(new MerchantRecipe(
            new ItemStack(ModItems.DESH_INGOT, 1),
            new ItemStack(Items.EMERALD, 3)
        ));
    }
}
