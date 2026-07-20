package earth.terrarium.adastra.mixin.client;

import earth.terrarium.adastra.Reference;
import earth.terrarium.adastra.common.blocks.AdAstraFluidBlock;
import earth.terrarium.adastra.common.registry.ModDimensions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin {

    private static final ResourceLocation AD_ASTRA$RAIN = new ResourceLocation("textures/environment/rain.png");
    private static final ResourceLocation AD_ASTRA$ACID_RAIN = new ResourceLocation(Reference.MOD_ID, "textures/environment/acid_rain.png");

    @Redirect(
        method = "renderRainSnow",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/texture/TextureManager;bindTexture(Lnet/minecraft/util/ResourceLocation;)V"
        )
    )
    private void adastra$bindAcidRainTexture(TextureManager textureManager, ResourceLocation texture) {
        if (AD_ASTRA$RAIN.equals(texture) && adastra$hasVenusAcidRain()) {
            textureManager.bindTexture(AD_ASTRA$ACID_RAIN);
            return;
        }

        textureManager.bindTexture(texture);
    }

    @Redirect(
        method = "setupFog(IZ)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/block/state/IBlockState;getMaterial()Lnet/minecraft/block/material/Material;"
        )
    )
    private Material adastra$useLiquidFog(IBlockState state) {
        if (state.getBlock() instanceof AdAstraFluidBlock) {
            AdAstraFluidBlock fluid = (AdAstraFluidBlock) state.getBlock();
            if (fluid.usesWaterVisualEffects()) {
                return Material.WATER;
            }
        }
        return state.getMaterial();
    }

    private boolean adastra$hasVenusAcidRain() {
        Minecraft minecraft = Minecraft.getMinecraft();
        World world = minecraft.world;
        return world != null
            && world.provider != null
            && world.provider.getDimension() == ModDimensions.VENUS_ID
            && world.isRaining();
    }
}
