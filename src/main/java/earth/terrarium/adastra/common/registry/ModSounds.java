package earth.terrarium.adastra.common.registry;

import earth.terrarium.adastra.Reference;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID)
public final class ModSounds {

    public static SoundEvent ROCKET_LAUNCH;
    public static SoundEvent ROCKET;
    public static SoundEvent WRENCH;
    public static SoundEvent SLIDING_DOOR_CLOSE;
    public static SoundEvent SLIDING_DOOR_OPEN;
    public static SoundEvent OXYGEN_INTAKE;
    public static SoundEvent OXYGEN_OUTTAKE;
    public static SoundEvent GRAVITY_NORMALIZER_IDLE;

    private ModSounds() {
    }

    @SubscribeEvent
    public static void register(RegistryEvent.Register<SoundEvent> event) {
        ROCKET_LAUNCH = register(event, "rocket_launch");
        ROCKET = register(event, "rocket");
        WRENCH = register(event, "wrench");
        SLIDING_DOOR_CLOSE = register(event, "sliding_door_close");
        SLIDING_DOOR_OPEN = register(event, "sliding_door_open");
        OXYGEN_INTAKE = register(event, "oxygen_intake");
        OXYGEN_OUTTAKE = register(event, "oxygen_outtake");
        GRAVITY_NORMALIZER_IDLE = register(event, "gravity_normalizer_idle");
    }

    private static SoundEvent register(RegistryEvent.Register<SoundEvent> event, String name) {
        ResourceLocation id = new ResourceLocation(Reference.MOD_ID, name);
        SoundEvent sound = new SoundEvent(id).setRegistryName(id);
        event.getRegistry().register(sound);
        return sound;
    }
}
