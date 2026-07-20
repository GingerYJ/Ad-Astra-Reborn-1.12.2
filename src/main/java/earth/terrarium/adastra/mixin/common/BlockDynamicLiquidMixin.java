package earth.terrarium.adastra.mixin.common;

import earth.terrarium.adastra.common.blocks.AdAstraFluidBlock;
import net.minecraft.block.BlockDynamicLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockDynamicLiquid.class)
public abstract class BlockDynamicLiquidMixin {

    @Inject(method = "canFlowInto", at = @At("HEAD"), cancellable = true)
    private void adastra$preventCustomFluidReplacement(World world, BlockPos pos, IBlockState state,
                                                       CallbackInfoReturnable<Boolean> cir) {
        if (state.getBlock() instanceof AdAstraFluidBlock) {
            cir.setReturnValue(false);
        }
    }
}
