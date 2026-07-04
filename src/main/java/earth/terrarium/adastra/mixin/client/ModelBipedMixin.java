package earth.terrarium.adastra.mixin.client;

import earth.terrarium.adastra.common.entities.vehicles.VehicleBase;
import earth.terrarium.adastra.common.registry.ModBlocks;
import earth.terrarium.adastra.common.registry.ModItems;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ModelBiped.class)
public abstract class ModelBipedMixin extends ModelBase {

    @Shadow
    public ModelRenderer bipedRightArm;

    @Shadow
    public ModelRenderer bipedLeftArm;

    @Inject(method = "setRotationAngles", at = @At("HEAD"))
    private void adastra$setVehiclePose(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entity, CallbackInfo ci) {
        if (entity != null && entity.getRidingEntity() instanceof VehicleBase && !((VehicleBase) entity.getRidingEntity()).shouldSit()) {
            isRiding = false;
        }
    }

    @Inject(method = "setRotationAngles", at = @At("TAIL"))
    private void adastra$setHeldOverHeadPose(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entity, CallbackInfo ci) {
        if (!(entity instanceof EntityLivingBase)) {
            return;
        }

        EntityLivingBase living = (EntityLivingBase) entity;
        if (adastra$isHeldOverHead(living.getHeldItemMainhand())) {
            bipedRightArm.rotateAngleX = -2.8F;
            bipedLeftArm.rotateAngleX = bipedRightArm.rotateAngleX;
        } else if (adastra$isHeldOverHead(living.getHeldItemOffhand())) {
            bipedLeftArm.rotateAngleX = -2.8F;
            bipedRightArm.rotateAngleX = bipedLeftArm.rotateAngleX;
        }
    }

    private boolean adastra$isHeldOverHead(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }

        Item item = stack.getItem();
        return item == ModItems.TIER_1_ROCKET
            || item == ModItems.TIER_2_ROCKET
            || item == ModItems.TIER_3_ROCKET
            || item == ModItems.TIER_4_ROCKET
            || item == ModItems.TIER_1_ROVER
            || item == Item.getItemFromBlock(ModBlocks.LAUNCH_PAD);
    }
}
