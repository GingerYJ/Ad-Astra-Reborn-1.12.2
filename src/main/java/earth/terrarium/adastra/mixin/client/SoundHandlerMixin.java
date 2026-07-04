package earth.terrarium.adastra.mixin.client;

import earth.terrarium.adastra.api.planets.PlanetApi;
import earth.terrarium.adastra.api.systems.PlanetData;
import earth.terrarium.adastra.client.systems.ClientData;
import earth.terrarium.adastra.common.config.AdAstraConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.ITickableSound;
import net.minecraft.client.audio.Sound;
import net.minecraft.client.audio.SoundEventAccessor;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.audio.SoundManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SoundHandler.class)
public abstract class SoundHandlerMixin {

    @Shadow
    @Final
    private SoundManager sndManager;

    @Inject(method = "playSound", at = @At("HEAD"), cancellable = true)
    private void adastra$playSound(ISound sound, CallbackInfo ci) {
        if (adastra$playMuffled(sound, 0)) {
            ci.cancel();
        }
    }

    @Inject(method = "playDelayedSound", at = @At("HEAD"), cancellable = true)
    private void adastra$playDelayedSound(ISound sound, int delay, CallbackInfo ci) {
        if (adastra$playMuffled(sound, delay)) {
            ci.cancel();
        }
    }

    private boolean adastra$playMuffled(ISound sound, int delay) {
        if (!adastra$shouldMuffle(sound)) {
            return false;
        }

        MuffledSound muffled = new MuffledSound(sound);
        if (delay <= 0) {
            sndManager.playSound(muffled);
        } else {
            sndManager.playDelayedSound(muffled, delay);
        }
        return true;
    }

    private boolean adastra$shouldMuffle(ISound sound) {
        if (sound == null
            || sound instanceof MuffledSound
            || sound instanceof ITickableSound
            || sound.getCategory() == SoundCategory.MASTER
            || !AdAstraConfig.spaceMuffler) {
            return false;
        }

        Minecraft minecraft = Minecraft.getMinecraft();
        World world = minecraft.world;
        if (world == null || !PlanetApi.API.isSpace(world)) {
            return false;
        }

        PlanetData localData = ClientData.getLocalData();
        return sound.getCategory() == SoundCategory.MUSIC
            || sound.getCategory() == SoundCategory.RECORDS
            || localData == null
            || !localData.oxygen();
    }

    private static final class MuffledSound implements ISound {

        private final ISound delegate;

        private MuffledSound(ISound delegate) {
            this.delegate = delegate;
        }

        @Override
        public ResourceLocation getSoundLocation() {
            return delegate.getSoundLocation();
        }

        @Override
        public SoundEventAccessor createAccessor(SoundHandler handler) {
            return delegate.createAccessor(handler);
        }

        @Override
        public Sound getSound() {
            return delegate.getSound();
        }

        @Override
        public SoundCategory getCategory() {
            return delegate.getCategory();
        }

        @Override
        public boolean canRepeat() {
            return delegate.canRepeat();
        }

        @Override
        public int getRepeatDelay() {
            return delegate.getRepeatDelay();
        }

        @Override
        public float getVolume() {
            return getCategory() == SoundCategory.MUSIC || getCategory() == SoundCategory.RECORDS ? 1.0F : 0.1F;
        }

        @Override
        public float getPitch() {
            return 0.1F;
        }

        @Override
        public float getXPosF() {
            return delegate.getXPosF();
        }

        @Override
        public float getYPosF() {
            return delegate.getYPosF();
        }

        @Override
        public float getZPosF() {
            return delegate.getZPosF();
        }

        @Override
        public AttenuationType getAttenuationType() {
            return delegate.getAttenuationType();
        }
    }
}
