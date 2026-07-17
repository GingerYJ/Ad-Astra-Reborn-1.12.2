package earth.terrarium.adastra.common.items;

import earth.terrarium.adastra.Reference;
import earth.terrarium.adastra.common.AdAstraCreativeTab;
import earth.terrarium.adastra.common.entities.projectile.ExtendraIceChargeEntity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.init.SoundEvents;
import net.minecraft.world.World;

public class ExtendraIceChargeItem extends Item {

    public ExtendraIceChargeItem(String name) {
        setRegistryName(Reference.MOD_ID, name);
        setTranslationKey(Reference.MOD_ID + "." + name);
        setCreativeTab(AdAstraCreativeTab.INSTANCE);
        setMaxStackSize(16);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (!world.isRemote) {
            ExtendraIceChargeEntity charge = new ExtendraIceChargeEntity(world, player);
            net.minecraft.util.math.Vec3d look = player.getLookVec();
            charge.shoot(look.x, look.y, look.z, 1.6F, 1.0F);
            world.spawnEntity(charge);
            world.playSound(null, player.posX, player.posY, player.posZ,
                SoundEvents.ENTITY_SNOWBALL_THROW, SoundCategory.PLAYERS, 0.5F,
                0.4F / (world.rand.nextFloat() * 0.4F + 0.8F));
            if (!player.capabilities.isCreativeMode) {
                stack.shrink(1);
            }
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }
}
