package earth.terrarium.adastra.client.systems;

import earth.terrarium.adastra.api.systems.PlanetData;
import earth.terrarium.adastra.common.registry.ModSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

@SideOnly(Side.CLIENT)
public final class ClientData {

    @Nullable
    private static PlanetData localData;

    private ClientData() {
    }

    public static void updateLocalData(PlanetData data) {
        if (data == null) {
            return;
        }
        if (localData != null && localData.oxygen() != data.oxygen()) {
            SoundEvent sound = data.oxygen() ? ModSounds.OXYGEN_INTAKE : ModSounds.OXYGEN_OUTTAKE;
            if (sound != null) {
                Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getRecord(sound, 1.0F, 1.0F));
            }
        }
        localData = data;
    }

    @Nullable
    public static PlanetData getLocalData() {
        return localData;
    }

    public static void clear() {
        localData = null;
    }
}
