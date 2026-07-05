package earth.terrarium.adastra.common.items;

import earth.terrarium.adastra.common.systems.OxygenUtils;
import earth.terrarium.adastra.common.util.KeybindManager;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class JetSuitItem extends SpaceSuitItem {

    private static final int UPWARD_ENERGY_PER_TICK = 50;
    private static final int FORWARD_ENERGY_PER_TICK = 100;
    private static final double FORWARD_FORCE = 0.075D;
    private static final double MAX_FORWARD_SPEED = 2.0D;

    public JetSuitItem(String name, EntityEquipmentSlot slot) {
        super(name, SpaceSuitMaterial.JET, slot);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag) {
        addOxygenTooltip(stack, tooltip);
        addEnergyTooltip(stack, tooltip);
        addSuitInfoTooltip(stack, tooltip, getSuitInfoTranslationKey());
    }

    @Override
    public void onArmorTick(World world, EntityPlayer player, ItemStack stack) {
        super.onArmorTick(world, player, stack);
        if (world.isRemote || player.capabilities.isFlying || player.isSpectator()) {
            return;
        }
        if (!isJetSuitChestPiece() || player.getItemStackFromSlot(EntityEquipmentSlot.CHEST) != stack) {
            return;
        }
        if (!SpaceSuitItem.hasFullJetSuitSet(player)) {
            return;
        }
        if (!KeybindManager.suitFlightEnabled(player) || !KeybindManager.jumpDown(player)) {
            return;
        }

        int energyCost = KeybindManager.sprintDown(player) ? FORWARD_ENERGY_PER_TICK : UPWARD_ENERGY_PER_TICK;
        if (!canUseEnergy(player, stack, energyCost)) {
            return;
        }

        if (KeybindManager.sprintDown(player)) {
            propelForward(player);
        } else {
            propelUpward(player);
        }

        consumeEnergy(player, stack, energyCost);
    }

    @Override
    protected String getSuitInfoTranslationKey() {
        return "info.ad_astra.jet_suit";
    }

    private boolean canUseEnergy(EntityPlayer player, ItemStack stack, int energyCost) {
        return player.capabilities.isCreativeMode || AdAstraArmorItem.consumeJetSuitEnergy(stack, energyCost, true) >= energyCost;
    }

    private void consumeEnergy(EntityPlayer player, ItemStack stack, int energyCost) {
        if (player.capabilities.isCreativeMode) {
            return;
        }
        AdAstraArmorItem.consumeJetSuitEnergy(stack, energyCost, false);
        player.inventory.markDirty();
        OxygenUtils.syncInventory(player);
    }

    private void propelUpward(EntityPlayer player) {
        player.motionY += Math.max(0.0025D, upwardAcceleration(player.ticksExisted));
        player.fallDistance = Math.max(player.fallDistance / 1.5F, 0.0F);
        player.velocityChanged = true;
    }

    private void propelForward(EntityPlayer player) {
        Vec3d look = player.getLookVec().normalize();
        double speed = Math.sqrt(player.motionX * player.motionX + player.motionY * player.motionY + player.motionZ * player.motionZ);
        if (speed > MAX_FORWARD_SPEED) {
            return;
        }
        player.motionX += look.x * FORWARD_FORCE;
        player.motionY += look.y * FORWARD_FORCE;
        player.motionZ += look.z * FORWARD_FORCE;
        player.fallDistance = Math.max(player.fallDistance / 1.5F, 0.0F);
        player.velocityChanged = true;
    }

    private double upwardAcceleration(int ticksExisted) {
        return sigmoidAcceleration(ticksExisted, 5.0D, 1.0D, 2.0D) / 25.0D;
    }

    public static double sigmoidAcceleration(double time, double peakTime, double peakAcceleration, double initialAcceleration) {
        return ((2.0D * peakAcceleration) / (1.0D + Math.exp(-time / peakTime)) - peakAcceleration) + initialAcceleration;
    }

}
