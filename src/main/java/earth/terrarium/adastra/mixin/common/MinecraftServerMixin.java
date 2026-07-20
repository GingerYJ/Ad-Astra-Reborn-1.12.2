package earth.terrarium.adastra.mixin.common;

import earth.terrarium.adastra.common.world.AdAstraSpaceStationWorldProvider;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {

    @Inject(method = "isBlockProtected", at = @At("HEAD"), cancellable = true)
    private void adastra$allowSpaceStationEditing(World world, BlockPos pos, EntityPlayer player, CallbackInfoReturnable<Boolean> cir) {
        if (world != null && world.provider instanceof AdAstraSpaceStationWorldProvider) {
            cir.setReturnValue(false);
        }
    }
}
