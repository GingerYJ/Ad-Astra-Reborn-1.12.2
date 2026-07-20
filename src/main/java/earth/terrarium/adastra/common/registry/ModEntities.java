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
import earth.terrarium.adastra.common.entities.projectile.IceChargeEntity;
import earth.terrarium.adastra.common.entities.mob.FreezeEntity;
import earth.terrarium.adastra.common.entities.vehicles.LanderEntity;
import earth.terrarium.adastra.common.entities.vehicles.ConfigurableRocketEntity;
import earth.terrarium.adastra.common.entities.vehicles.Tier1RocketEntity;
import earth.terrarium.adastra.common.entities.vehicles.Tier1RoverEntity;
import earth.terrarium.adastra.common.entities.vehicles.Tier2RocketEntity;
import earth.terrarium.adastra.common.entities.vehicles.Tier3RocketEntity;
import earth.terrarium.adastra.common.entities.vehicles.Tier4RocketEntity;
import earth.terrarium.adastra.common.entities.vehicles.Tier5RocketEntity;
import earth.terrarium.adastra.common.entities.vehicles.Tier6RocketEntity;
import earth.terrarium.adastra.common.entities.vehicles.Tier7RocketEntity;
import earth.terrarium.adastra.common.entities.vehicles.Tier8RocketEntity;
import earth.terrarium.adastra.common.entities.vehicles.Tier9RocketEntity;
import earth.terrarium.adastra.common.entities.vehicles.Tier10RocketEntity;
import earth.terrarium.adastra.common.entities.vehicles.Tier11RocketEntity;
import earth.terrarium.adastra.common.entities.vehicles.Tier12RocketEntity;
import earth.terrarium.adastra.common.entities.vehicles.Tier13RocketEntity;
import earth.terrarium.adastra.common.entities.vehicles.Tier14RocketEntity;
import earth.terrarium.adastra.common.entities.vehicles.Tier15RocketEntity;
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
    public static final EntityEntry TIER_1_ROCKET = entity("tier_1_rocket", Tier1RocketEntity.class, 160, 1, true);
    public static final EntityEntry TIER_2_ROCKET = entity("tier_2_rocket", Tier2RocketEntity.class, 160, 1, true);
    public static final EntityEntry TIER_3_ROCKET = entity("tier_3_rocket", Tier3RocketEntity.class, 160, 1, true);
    public static final EntityEntry TIER_4_ROCKET = entity("tier_4_rocket", Tier4RocketEntity.class, 160, 1, true);
    public static final EntityEntry TIER_5_ROCKET = entity("tier_5_rocket", Tier5RocketEntity.class, 160, 1, true);
    public static final EntityEntry TIER_6_ROCKET = entity("tier_6_rocket", Tier6RocketEntity.class, 160, 1, true);
    public static final EntityEntry TIER_7_ROCKET = entity("tier_7_rocket", Tier7RocketEntity.class, 160, 1, true);
    public static final EntityEntry TIER_8_ROCKET = entity("tier_8_rocket", Tier8RocketEntity.class, 160, 1, true);
    public static final EntityEntry TIER_9_ROCKET = entity("tier_9_rocket", Tier9RocketEntity.class, 160, 1, true);
    public static final EntityEntry TIER_10_ROCKET = entity("tier_10_rocket", Tier10RocketEntity.class, 160, 1, true);
    public static final EntityEntry TIER_11_ROCKET = entity("tier_11_rocket", Tier11RocketEntity.class, 160, 1, true);
    public static final EntityEntry TIER_12_ROCKET = entity("tier_12_rocket", Tier12RocketEntity.class, 160, 1, true);
    public static final EntityEntry TIER_13_ROCKET = entity("tier_13_rocket", Tier13RocketEntity.class, 160, 1, true);
    public static final EntityEntry TIER_14_ROCKET = entity("tier_14_rocket", Tier14RocketEntity.class, 160, 1, true);
    public static final EntityEntry TIER_15_ROCKET = entity("tier_15_rocket", Tier15RocketEntity.class, 160, 1, true);
    public static final EntityEntry CONFIGURABLE_ROCKET = entity("configurable_rocket", ConfigurableRocketEntity.class, 160, 1, true);
    public static final EntityEntry LANDER = entity("lander", LanderEntity.class, 160, 1, true);
    public static final EntityEntry LUNARIAN = entity("lunarian", LunarianEntity.class, 80, 3, true);
    public static final EntityEntry CORRUPTED_LUNARIAN = entity("corrupted_lunarian", CorruptedLunarianEntity.class, 80, 3, true);
    public static final EntityEntry STAR_CRAWLER = entity("star_crawler", StarCrawlerEntity.class, 80, 3, true);
    public static final EntityEntry MARTIAN_RAPTOR = entity("martian_raptor", MartianRaptorEntity.class, 80, 3, true);
    public static final EntityEntry PYGRO = entity("pygro", PygroEntity.class, 80, 3, true);
    public static final EntityEntry ZOMBIFIED_PYGRO = entity("zombified_pygro", ZombifiedPygroEntity.class, 80, 3, true);
    public static final EntityEntry PYGRO_BRUTE = entity("pygro_brute", PygroBruteEntity.class, 80, 3, true);
    public static final EntityEntry MOGLER = entity("mogler", MoglerEntity.class, 80, 3, true);
    public static final EntityEntry ZOMBIFIED_MOGLER = entity("zombified_mogler", ZombifiedMoglerEntity.class, 80, 3, true);
    public static final EntityEntry LUNARIAN_WANDERING_TRADER = entity("lunarian_wandering_trader", LunarianWanderingTraderEntity.class, 80, 3, true);
    public static final EntityEntry SULFUR_CREEPER = entity("sulfur_creeper", SulfurCreeperEntity.class, 128, 3, true);
    public static final EntityEntry GLACIAN_RAM = entity("glacian_ram", GlacianRamEntity.class, 160, 3, true);
    public static final EntityEntry ICE_SPIT = entity("ice_spit", IceSpitEntity.class, 64, 10, true);
    public static final EntityEntry ICE_CHARGE = prefixedEntity("ice_charge", IceChargeEntity.class, 64, 10, true);
    public static final EntityEntry FREEZE = prefixedEntity("freeze", FreezeEntity.class, 80, 3, true);
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

    private static <T extends Entity> EntityEntry prefixedEntity(String name, Class<T> entityClass,
                                                                  int trackingRange, int updateFrequency,
                                                                  boolean sendVelocityUpdates) {
        EntityEntry entry = EntityEntryBuilder.create()
            .entity(entityClass)
            .id(ModResourceIds.entity(name), nextId++)
            .name(Reference.MOD_ID + "." + ModResourceIds.entityPath(name))
            .tracker(trackingRange, updateFrequency, sendVelocityUpdates)
            .build();
        INTERNAL_ENTITIES.add(entry);
        return entry;
    }

}
