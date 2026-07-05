package earth.terrarium.adastra.mixin.client;

import earth.terrarium.adastra.client.radio.audio.RadioHandler;
import earth.terrarium.adastra.common.util.radio.RadioHolder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class RadioHolderDismountMixin {

    @Shadow
    public World world;

    @Shadow
    public abstract Entity getRidingEntity();

    @Inject(method = "dismountRidingEntity", at = @At("HEAD"))
    private void adastra$stopRadioWhenDismounting(CallbackInfo ci) {
        Entity entity = (Entity) (Object) this;
        if (world != null && world.isRemote && entity instanceof EntityPlayer && getRidingEntity() instanceof RadioHolder) {
            RadioHandler.stop();
        }
    }
}
