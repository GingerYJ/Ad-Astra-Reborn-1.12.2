package earth.terrarium.adastra.common.handler;

import earth.terrarium.adastra.common.items.GasTankItem;
import earth.terrarium.adastra.common.registry.ModItems;
import earth.terrarium.adastra.common.util.EnvironmentUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class CommonEventHandler {

    private static final int OXYGEN_CHECK_INTERVAL = 12;
    private static final int OXYGEN_PER_CHECK = 1;
    private static final int DAMAGE_INTERVAL = 40;
    private static final float SUFFOCATION_DAMAGE = 2.0f;
    private static final DamageSource SPACE_SUFFOCATION = new DamageSource("oxygen").setDamageBypassesArmor();

    @SubscribeEvent
    public void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
        if (!(event.getEntityLiving() instanceof EntityPlayer)) {
            return;
        }

        EntityPlayer player = (EntityPlayer) event.getEntityLiving();
        if (player.world.isRemote || player.capabilities.isCreativeMode || player.isSpectator()) {
            return;
        }
        if (player.ticksExisted % OXYGEN_CHECK_INTERVAL != 0 || EnvironmentUtils.hasOxygen(player)) {
            return;
        }

        if (canUseSuitOxygen(player) && GasTankItem.drainOxygen(player.getItemStackFromSlot(EntityEquipmentSlot.CHEST), OXYGEN_PER_CHECK, false) >= OXYGEN_PER_CHECK) {
            player.inventory.markDirty();
            return;
        }

        if (player.ticksExisted % DAMAGE_INTERVAL == 0) {
            player.attackEntityFrom(SPACE_SUFFOCATION, SUFFOCATION_DAMAGE);
        }
    }

    private boolean canUseSuitOxygen(EntityPlayer player) {
        return isWearingSet(player, ModItems.SPACE_HELMET, ModItems.SPACE_SUIT, ModItems.SPACE_PANTS, ModItems.SPACE_BOOTS)
            || isWearingSet(player, ModItems.NETHERITE_SPACE_HELMET, ModItems.NETHERITE_SPACE_SUIT, ModItems.NETHERITE_SPACE_PANTS, ModItems.NETHERITE_SPACE_BOOTS)
            || isWearingSet(player, ModItems.JET_SUIT_HELMET, ModItems.JET_SUIT, ModItems.JET_SUIT_PANTS, ModItems.JET_SUIT_BOOTS);
    }

    private boolean isWearingSet(EntityPlayer player, Item helmet, Item chest, Item legs, Item boots) {
        return isWearing(player, EntityEquipmentSlot.HEAD, helmet)
            && isWearing(player, EntityEquipmentSlot.CHEST, chest)
            && isWearing(player, EntityEquipmentSlot.LEGS, legs)
            && isWearing(player, EntityEquipmentSlot.FEET, boots);
    }

    private boolean isWearing(EntityPlayer player, EntityEquipmentSlot slot, Item item) {
        ItemStack stack = player.getItemStackFromSlot(slot);
        return !stack.isEmpty() && stack.getItem() == item;
    }
}
