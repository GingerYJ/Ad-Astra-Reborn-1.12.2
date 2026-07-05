package earth.terrarium.adastra.mixin.common;

import earth.terrarium.adastra.common.entities.vehicles.AdAstraVehicleEntity;
import earth.terrarium.adastra.common.registry.ModItems;
import earth.terrarium.adastra.common.systems.GravitySystem;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.network.NetHandlerPlayServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NetHandlerPlayServer.class)
public abstract class NetHandlerPlayServerMixin {

    @Shadow
    public EntityPlayerMP player;

    @Shadow
    private int floatingTickCount;

    @Shadow
    private int vehicleFloatingTickCount;

    @Inject(method = "update", at = @At("HEAD"))
    private void adastra$resetAdAstraFloatingChecks(CallbackInfo ci) {
        if (player == null) {
            return;
        }

        if (!player.onGround && (adastra$hasJetSuitSet(player) || adastra$isLowGravity(player))) {
            floatingTickCount = 0;
        }

        Entity riding = player.getRidingEntity();
        if (riding instanceof AdAstraVehicleEntity) {
            vehicleFloatingTickCount = 0;
        }
    }

    private boolean adastra$hasJetSuitSet(EntityPlayerMP player) {
        return adastra$hasItem(player, EntityEquipmentSlot.HEAD, ModItems.JET_SUIT_HELMET)
            && adastra$hasItem(player, EntityEquipmentSlot.CHEST, ModItems.JET_SUIT)
            && adastra$hasItem(player, EntityEquipmentSlot.LEGS, ModItems.JET_SUIT_PANTS)
            && adastra$hasItem(player, EntityEquipmentSlot.FEET, ModItems.JET_SUIT_BOOTS);
    }

    private boolean adastra$hasItem(EntityPlayerMP player, EntityEquipmentSlot slot, Item item) {
        return !player.getItemStackFromSlot(slot).isEmpty() && player.getItemStackFromSlot(slot).getItem() == item;
    }

    private boolean adastra$isLowGravity(EntityPlayerMP player) {
        return player.world != null && GravitySystem.getGravityForEntity(player) < 1.0F;
    }
}
