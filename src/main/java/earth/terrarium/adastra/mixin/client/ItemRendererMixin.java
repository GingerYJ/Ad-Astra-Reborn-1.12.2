package earth.terrarium.adastra.mixin.client;

import earth.terrarium.adastra.client.render.Ti69Renderer;
import earth.terrarium.adastra.common.items.AdAstraArmorItem;
import earth.terrarium.adastra.common.registry.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemRenderer.class)
public abstract class ItemRendererMixin {

    @Shadow
    @Final
    private Minecraft mc;

    @Shadow
    protected abstract void renderArmFirstPerson(float equipProgress, float swingProgress, EnumHandSide hand);

    @Shadow
    public abstract void renderItemSide(EntityLivingBase entity, ItemStack stack, ItemCameraTransforms.TransformType transform, boolean leftHanded);

    @Inject(method = "renderItemInFirstPerson(Lnet/minecraft/client/entity/AbstractClientPlayer;FFLnet/minecraft/util/EnumHand;FLnet/minecraft/item/ItemStack;F)V", at = @At("HEAD"), cancellable = true)
    private void adastra$renderTi69InFirstPerson(AbstractClientPlayer player, float partialTicks, float pitch, EnumHand hand, float swingProgress, ItemStack stack, float equipProgress, CallbackInfo ci) {
        if (stack.isEmpty() || stack.getItem() != ModItems.TI_69) {
            return;
        }

        boolean mainHand = hand == EnumHand.MAIN_HAND;
        EnumHandSide side = mainHand ? player.getPrimaryHand() : player.getPrimaryHand().opposite();
        boolean rightHanded = side == EnumHandSide.RIGHT;
        float handed = rightHanded ? 1.0F : -1.0F;

        GlStateManager.pushMatrix();
        GlStateManager.translate(handed * 0.125F, -0.125F, 0.0F);
        if (!player.isInvisible()) {
            GlStateManager.pushMatrix();
            GlStateManager.rotate(handed * 10.0F, 0.0F, 0.0F, 1.0F);
            renderArmFirstPerson(equipProgress, swingProgress, side);
            GlStateManager.popMatrix();
        }

        GlStateManager.translate(handed * 0.51F, -0.1F + equipProgress * -1.2F, -0.75F);
        float swingRoot = MathHelper.sqrt(swingProgress);
        float swingSin = MathHelper.sin(swingRoot * (float) Math.PI);
        float offsetX = -0.5F * swingSin;
        float offsetY = 0.4F * MathHelper.sin(swingRoot * ((float) Math.PI * 2.0F));
        float offsetZ = -0.3F * MathHelper.sin(swingProgress * (float) Math.PI);
        GlStateManager.translate(handed * offsetX, offsetY * swingSin, offsetZ);
        GlStateManager.rotate(swingSin * -45.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(handed * swingSin * -30.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.translate(rightHanded ? 0.1F : 0.09F, 0.0F, 0.0F);
        GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.scale(0.5F, 0.5F, 0.5F);
        GlStateManager.translate(-0.5F, -0.75F, 0.0F);
        renderItemSide(player, stack, ItemCameraTransforms.TransformType.NONE, !rightHanded);

        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        GlStateManager.disableCull();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        Ti69Renderer.renderTi69OnScreen();
        GlStateManager.enableCull();
        GlStateManager.disableBlend();
        GlStateManager.enableLighting();

        GlStateManager.popMatrix();
        ci.cancel();
    }

    @Inject(method = "renderArmFirstPerson", at = @At("TAIL"))
    private void adastra$renderSpaceSuitArm(float equipProgress, float swingProgress, EnumHandSide hand, CallbackInfo ci) {
        AbstractClientPlayer player = mc.player;
        if (player == null) {
            return;
        }

        ItemStack chest = player.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
        if (chest.isEmpty() || !(chest.getItem() instanceof AdAstraArmorItem)) {
            return;
        }

        AdAstraArmorItem armor = (AdAstraArmorItem) chest.getItem();
        ModelBiped model = armor.getArmorModel(player, chest, EntityEquipmentSlot.CHEST, new ModelBiped());
        if (model == null) {
            return;
        }

        ResourceLocation texture = new ResourceLocation(armor.getArmorTexture(chest, player, EntityEquipmentSlot.CHEST, null));
        mc.getTextureManager().bindTexture(texture);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.disableCull();
        if (hand == EnumHandSide.RIGHT) {
            model.bipedRightArm.render(0.0625F);
        } else {
            model.bipedLeftArm.render(0.0625F);
        }
        GlStateManager.enableCull();
        GlStateManager.disableBlend();
    }
}