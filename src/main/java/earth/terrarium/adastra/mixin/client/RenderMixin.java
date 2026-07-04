package earth.terrarium.adastra.mixin.client;

import earth.terrarium.adastra.common.entities.vehicles.RocketEntity;
import earth.terrarium.adastra.common.entities.vehicles.VehicleBase;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Render.class)
public abstract class RenderMixin {

    private static final double AD_ASTRA$PLANET_SELECTION_HEIGHT = 180.0D;

    @Inject(method = "shouldRender", at = @At("HEAD"), cancellable = true)
    private void adastra$hideRocketAndRiderInPlanetSelection(Entity entity, ICamera camera, double camX, double camY, double camZ, CallbackInfoReturnable<Boolean> cir) {
        if (adastra$isRocketInPlanetSelection(entity) || adastra$isRidingRocketInPlanetSelection(entity) || adastra$isHiddenVehicleRider(entity)) {
            cir.setReturnValue(false);
        }
    }

    private boolean adastra$isRocketInPlanetSelection(Entity entity) {
        return entity instanceof RocketEntity && entity.posY >= AD_ASTRA$PLANET_SELECTION_HEIGHT;
    }

    private boolean adastra$isRidingRocketInPlanetSelection(Entity entity) {
        Entity vehicle = entity.getRidingEntity();
        return vehicle instanceof RocketEntity && vehicle.posY >= AD_ASTRA$PLANET_SELECTION_HEIGHT;
    }

    private boolean adastra$isHiddenVehicleRider(Entity entity) {
        Entity vehicle = entity.getRidingEntity();
        return vehicle instanceof VehicleBase && ((VehicleBase) vehicle).hideRider();
    }
}
