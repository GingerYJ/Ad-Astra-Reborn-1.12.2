package earth.terrarium.adastra.common.registry;

import earth.terrarium.adastra.AdAstraReborn;
import earth.terrarium.adastra.Reference;
import earth.terrarium.adastra.common.entities.misc.AirVortexEntity;
import earth.terrarium.adastra.common.entities.misc.SpacePaintingEntity;
import earth.terrarium.adastra.common.entities.mob.CorruptedLunarianEntity;
import earth.terrarium.adastra.common.entities.mob.GlacianRamEntity;
import earth.terrarium.adastra.common.entities.mob.LunarianEntity;
import earth.terrarium.adastra.common.entities.mob.LunarianWanderingTraderEntity;
import earth.terrarium.adastra.common.entities.mob.MartianRaptorEntity;
import earth.terrarium.adastra.common.entities.mob.MoglerEntity;
import earth.terrarium.adastra.common.entities.mob.PygroBruteEntity;
import earth.terrarium.adastra.common.entities.mob.PygroEntity;
import earth.terrarium.adastra.common.entities.mob.StarCrawlerEntity;
import earth.terrarium.adastra.common.entities.mob.SulfurCreeperEntity;
import earth.terrarium.adastra.common.entities.mob.ZombifiedMoglerEntity;
import earth.terrarium.adastra.common.entities.mob.ZombifiedPygroEntity;
import earth.terrarium.adastra.common.entities.projectile.IceSpitEntity;
import earth.terrarium.adastra.common.entities.vehicles.LanderEntity;
import earth.terrarium.adastra.common.entities.vehicles.Tier1RocketEntity;
import earth.terrarium.adastra.common.entities.vehicles.Tier1RoverEntity;
import earth.terrarium.adastra.common.entities.vehicles.Tier2RocketEntity;
import earth.terrarium.adastra.common.entities.vehicles.Tier3RocketEntity;
import earth.terrarium.adastra.common.entities.vehicles.Tier4RocketEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID)
public final class ModEntities {

    private static final List<EntityEntry> INTERNAL_ENTITIES = new ArrayList<>();

    public static final List<EntityEntry> ENTITIES = Collections.unmodifiableList(INTERNAL_ENTITIES);

    public static final EntityEntry AIR_VORTEX = entity("air_vortex", AirVortexEntity.class, 64, 3, false);
    public static final EntityEntry TIER_1_ROVER = entity("tier_1_rover", Tier1RoverEntity.class, 160, 3, true);
    public static final EntityEntry TIER_1_ROCKET = entity("tier_1_rocket", Tier1RocketEntity.class, 160, 3, true);
    public static final EntityEntry TIER_2_ROCKET = entity("tier_2_rocket", Tier2RocketEntity.class, 160, 3, true);
    public static final EntityEntry TIER_3_ROCKET = entity("tier_3_rocket", Tier3RocketEntity.class, 160, 3, true);
    public static final EntityEntry TIER_4_ROCKET = entity("tier_4_rocket", Tier4RocketEntity.class, 160, 3, true);
    public static final EntityEntry LANDER = entity("lander", LanderEntity.class, 160, 3, true);
    public static final EntityEntry LUNARIAN = egg("lunarian", LunarianEntity.class, 0xff33ccff, 0xff4e3923);
    public static final EntityEntry CORRUPTED_LUNARIAN = egg("corrupted_lunarian", CorruptedLunarianEntity.class, 0xff1e1b19, 0xff0090c1);
    public static final EntityEntry STAR_CRAWLER = egg("star_crawler", StarCrawlerEntity.class, 0xff333333, 0xff00cccc);
    public static final EntityEntry MARTIAN_RAPTOR = egg("martian_raptor", MartianRaptorEntity.class, 0x51a03e, 0xffffcc00);
    public static final EntityEntry PYGRO = egg("pygro", PygroEntity.class, 0xffcc6600, 0xff990000);
    public static final EntityEntry ZOMBIFIED_PYGRO = egg("zombified_pygro", ZombifiedPygroEntity.class, 0x814a25, 0x5d8e47);
    public static final EntityEntry PYGRO_BRUTE = egg("pygro_brute", PygroBruteEntity.class, 0xffcc6600, 0xfffef978);
    public static final EntityEntry MOGLER = egg("mogler", MoglerEntity.class, 0xffffcc00, 0xffcc0000);
    public static final EntityEntry ZOMBIFIED_MOGLER = egg("zombified_mogler", ZombifiedMoglerEntity.class, 0xbf4e41, 0x79e655);
    public static final EntityEntry LUNARIAN_WANDERING_TRADER = egg("lunarian_wandering_trader", LunarianWanderingTraderEntity.class, 0x5b73c7, 0x8244d5);
    public static final EntityEntry SULFUR_CREEPER = egg("sulfur_creeper", SulfurCreeperEntity.class, 128, 0xd48f30, 0xac791c);
    public static final EntityEntry GLACIAN_RAM = egg("glacian_ram", GlacianRamEntity.class, 160, 0xffe6ff, 0x433d3d);
    public static final EntityEntry ICE_SPIT = entity("ice_spit", IceSpitEntity.class, 64, 10, true);
    public static final EntityEntry SPACE_PAINTING = entity("space_painting", SpacePaintingEntity.class, 160, Integer.MAX_VALUE, false);

    private static int nextId;

    private ModEntities() {
    }

    @SubscribeEvent
    public static void register(RegistryEvent.Register<EntityEntry> event) {
        event.getRegistry().registerAll(INTERNAL_ENTITIES.toArray(new EntityEntry[0]));
    }

    private static <T extends Entity> EntityEntry entity(String name, Class<T> entityClass, int trackingRange, int updateFrequency, boolean sendVelocityUpdates) {
        EntityEntry entry = EntityEntryBuilder.create()
            .entity(entityClass)
            .id(new ResourceLocation(Reference.MOD_ID, name), nextId++)
            .name(Reference.MOD_ID + "." + name)
            .tracker(trackingRange, updateFrequency, sendVelocityUpdates)
            .build();
        INTERNAL_ENTITIES.add(entry);
        return entry;
    }

    private static <T extends Entity> EntityEntry egg(String name, Class<T> entityClass, int primaryColor, int secondaryColor) {
        return egg(name, entityClass, 80, primaryColor, secondaryColor);
    }

    private static <T extends Entity> EntityEntry egg(String name, Class<T> entityClass, int trackingRange, int primaryColor, int secondaryColor) {
        EntityEntry entry = EntityEntryBuilder.create()
            .entity(entityClass)
            .id(new ResourceLocation(Reference.MOD_ID, name), nextId++)
            .name(Reference.MOD_ID + "." + name)
            .tracker(trackingRange, 3, true)
            .egg(primaryColor, secondaryColor)
            .build();
        INTERNAL_ENTITIES.add(entry);
        return entry;
    }
}
