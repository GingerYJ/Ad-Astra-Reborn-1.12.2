package earth.terrarium.adastra.mixin.common.environment;

import earth.terrarium.adastra.common.systems.TemperatureSystem;
import net.minecraft.block.BlockIce;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockIce.class)
public abstract class IceBlockMixin {

    @Inject(method = "turnIntoWater", at = @At("HEAD"), cancellable = true)
    private void adastra$turnIntoWater(World world, BlockPos pos, CallbackInfo ci) {
        if (world != null
            && !world.isRemote
            && TemperatureSystem.getTemperatureAtPos(world, pos) < TemperatureSystem.MIN_LIVEABLE_TEMPERATURE) {
            ci.cancel();
        }
    }
}
