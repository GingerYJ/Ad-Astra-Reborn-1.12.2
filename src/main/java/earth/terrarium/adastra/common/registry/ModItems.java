package earth.terrarium.adastra.common.registry;

import earth.terrarium.adastra.Reference;
import earth.terrarium.adastra.common.AdAstraCreativeTab;
import earth.terrarium.adastra.common.blocks.AdAstraFluidBlock;
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
import earth.terrarium.adastra.common.items.AdAstraEnergyItem;
import earth.terrarium.adastra.common.items.AdAstraBucketItem;
import earth.terrarium.adastra.common.items.AdAstraSpawnEggItem;
import earth.terrarium.adastra.common.items.ConfigurableRocketItem;
import earth.terrarium.adastra.common.items.IceChargeItem;
import earth.terrarium.adastra.common.items.AdAstraWrenchItem;
import earth.terrarium.adastra.common.items.GasTankItem;
import earth.terrarium.adastra.common.items.JetSuitItem;
import earth.terrarium.adastra.common.items.NetheriteSpaceSuitItem;
import earth.terrarium.adastra.common.items.SpacePaintingItem;
import earth.terrarium.adastra.common.items.SpaceSuitMaterial;
import earth.terrarium.adastra.common.items.SpaceSuitItem;
import earth.terrarium.adastra.common.items.Ti69Item;
import earth.terrarium.adastra.common.items.VehicleItem;
import earth.terrarium.adastra.common.rocket.ConfigurableRocketRegistry;
import earth.terrarium.adastra.common.rocket.ConfigurableRocketSpec;
import earth.terrarium.adastra.common.items.ZipGunItem;
import earth.terrarium.adastra.common.entities.vehicles.Tier1RocketEntity;
import earth.terrarium.adastra.common.entities.vehicles.Tier1RoverEntity;
import earth.terrarium.adastra.common.entities.vehicles.Tier2RocketEntity;
import earth.terrarium.adastra.common.entities.vehicles.Tier3RocketEntity;
import earth.terrarium.adastra.common.entities.vehicles.Tier4RocketEntity;
import earth.terrarium.adastra.common.entities.vehicles.Tier5RocketEntity;
import earth.terrarium.adastra.common.entities.vehicles.Tier6RocketEntity;
import earth.terrarium.adastra.common.entities.vehicles.Tier7RocketEntity;
import net.minecraft.block.Block;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.init.Items;
import net.minecraft.item.ItemFood;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID)
public final class ModItems {

    private static final List<Item> INTERNAL_ITEMS = new ArrayList<>();
    private static final Map<String, Item> BY_NAME = new LinkedHashMap<>();

    public static final List<Item> ITEMS = Collections.unmodifiableList(INTERNAL_ITEMS);

    public static final Item CHEESE = food("cheese", 4, 1.0f);

    public static final Item IRON_PLATE = item("iron_plate");
    public static final Item IRON_ROD = item("iron_rod");
    public static final Item STEEL_INGOT = item("steel_ingot");
    public static final Item STEEL_NUGGET = item("steel_nugget");
    public static final Item STEEL_PLATE = item("steel_plate");
    public static final Item STEEL_ROD = item("steel_rod");
    public static final Item ETRIUM_INGOT = item("etrium_ingot");
    public static final Item ETRIUM_NUGGET = item("etrium_nugget");
    public static final Item ETRIUM_PLATE = item("etrium_plate");
    public static final Item ETRIUM_ROD = item("etrium_rod");
    public static final Item ETRIONIC_CORE = item("etrionic_core");
    public static final Item DESH_INGOT = item("desh_ingot");
    public static final Item DESH_NUGGET = item("desh_nugget");
    public static final Item DESH_PLATE = item("desh_plate");
    public static final Item OSTRUM_INGOT = item("ostrum_ingot");
    public static final Item OSTRUM_NUGGET = item("ostrum_nugget");
    public static final Item OSTRUM_PLATE = item("ostrum_plate");
    public static final Item CALORITE_INGOT = item("calorite_ingot");
    public static final Item CALORITE_NUGGET = item("calorite_nugget");
    public static final Item CALORITE_PLATE = item("calorite_plate");
    public static final Item RAW_DESH = item("raw_desh");
    public static final Item RAW_OSTRUM = item("raw_ostrum");
    public static final Item RAW_CALORITE = item("raw_calorite");
    // Minecraft 1.12.2 has no vanilla copper item; this backs the core Glacio copper ore.
    public static final Item COPPER_INGOT = item("copper_ingot");
    public static final Item PHOTOVOLTAIC_ETRIUM_CELL = item("photovoltaic_etrium_cell");
    public static final Item PHOTOVOLTAIC_VESNIUM_CELL = item("photovoltaic_vesnium_cell");
    public static final Item OXYGEN_GEAR = item("oxygen_gear");
    public static final Item WHEEL = item("wheel");
    public static final Item ENGINE_FRAME = item("engine_frame");
    public static final Item FAN = item("fan");
    public static final Item ROCKET_NOSE_CONE = item("rocket_nose_cone");
    public static final Item STEEL_ENGINE = item("steel_engine");
    public static final Item DESH_ENGINE = item("desh_engine");
    public static final Item OSTRUM_ENGINE = item("ostrum_engine");
    public static final Item CALORITE_ENGINE = item("calorite_engine");
    public static final Item STEEL_TANK = item("steel_tank");
    public static final Item DESH_TANK = item("desh_tank");
    public static final Item OSTRUM_TANK = item("ostrum_tank");
    public static final Item CALORITE_TANK = item("calorite_tank");
    public static final Item ROCKET_FIN = item("rocket_fin");
    public static final Item ICE_SHARD = item("ice_shard");

    public static final Item JUPERIUM_INGOT = registerItem("juperium_ingot");
    public static final Item JUPERIUM_NUGGET = registerItem("juperium_nugget");
    public static final Item JUPERIUM_PLATE = registerItem("juperium_plate");
    public static final Item RAW_JUPERIUM = registerItem("raw_juperium");
    public static final Item JUPERIUM_ENGINE = registerItem("juperium_engine");
    public static final Item JUPERIUM_TANK = registerItem("juperium_tank");

    public static final Item SATURLYTE_INGOT = registerItem("saturlyte_ingot");
    public static final Item SATURLYTE_NUGGET = registerItem("saturlyte_nugget");
    public static final Item SATURLYTE_PLATE = registerItem("saturlyte_plate");
    public static final Item RAW_SATURLYTE = registerItem("raw_saturlyte");
    public static final Item SATURLYTE_ENGINE = registerItem("saturlyte_engine");
    public static final Item SATURLYTE_TANK = registerItem("saturlyte_tank");

    public static final Item URANIUM_INGOT = registerItem("uranium_ingot");
    public static final Item URANIUM_NUGGET = registerItem("uranium_nugget");
    public static final Item URANIUM_PLATE = registerItem("uranium_plate");
    public static final Item RAW_URANIUM = registerItem("raw_uranium");
    public static final Item URANIUM_ENGINE = registerItem("uranium_engine");
    public static final Item URANIUM_TANK = registerItem("uranium_tank");

    public static final Item NEPTUNIUM_INGOT = registerItem("neptunium_ingot");
    public static final Item NEPTUNIUM_NUGGET = registerItem("neptunium_nugget");
    public static final Item NEPTUNIUM_PLATE = registerItem("neptunium_plate");
    public static final Item RAW_NEPTUNIUM = registerItem("raw_neptunium");
    public static final Item NEPTUNIUM_ENGINE = registerItem("neptunium_engine");
    public static final Item NEPTUNIUM_TANK = registerItem("neptunium_tank");

    public static final Item RADIUM_INGOT = registerItem("radium_ingot");
    public static final Item RADIUM_NUGGET = registerItem("radium_nugget");
    public static final Item RADIUM_PLATE = registerItem("radium_plate");
    public static final Item RAW_RADIUM = registerItem("raw_radium");
    public static final Item RADIUM_ENGINE = registerItem("radium_engine");
    public static final Item RADIUM_TANK = registerItem("radium_tank");

    public static final Item PLUTONIUM_INGOT = registerItem("plutonium_ingot");
    public static final Item PLUTONIUM_NUGGET = registerItem("plutonium_nugget");
    public static final Item PLUTONIUM_PLATE = registerItem("plutonium_plate");
    public static final Item RAW_PLUTONIUM = registerItem("raw_plutonium");
    public static final Item PLUTONIUM_ENGINE = registerItem("plutonium_engine");
    public static final Item PLUTONIUM_TANK = registerItem("plutonium_tank");

    public static final Item ELECTROLYTE_INGOT = registerItem("electrolyte_ingot");
    public static final Item ELECTROLYTE_NUGGET = registerItem("electrolyte_nugget");
    public static final Item ELECTROLYTE_PLATE = registerItem("electrolyte_plate");
    public static final Item RAW_ELECTROLYTE = registerItem("raw_electrolyte");
    public static final Item ELECTROLYTE_ENGINE = registerItem("electrolyte_engine");
    public static final Item ELECTROLYTE_TANK = registerItem("electrolyte_tank");

    public static final Item AURORITE_INGOT = registerItem("aurorite_ingot");
    public static final Item AURORITE_NUGGET = registerItem("aurorite_nugget");
    public static final Item AURORITE_PLATE = registerItem("aurorite_plate");
    public static final Item RAW_AURORITE = registerItem("raw_aurorite");
    public static final Item AURORITE_ENGINE = registerItem("aurorite_engine");
    public static final Item AURORITE_TANK = registerItem("aurorite_tank");

    public static final Item FREEZE_SHARD = registerItem("freeze_shard");
    public static final Item ICE_CHARGE = registerIceCharge();

    public static final Item TIER_8_ROCKET = registerRocket(8, 18000);
    public static final Item TIER_9_ROCKET = registerRocket(9, 19000);
    public static final Item TIER_10_ROCKET = registerRocket(10, 20000);
    public static final Item TIER_11_ROCKET = registerRocket(11, 21000);
    public static final Item TIER_12_ROCKET = registerRocket(12, 22000);
    public static final Item TIER_13_ROCKET = registerRocket(13, 23000);
    public static final Item TIER_14_ROCKET = registerRocket(14, 24000);
    public static final Item TIER_15_ROCKET = registerRocket(15, 25000);

    public static final Item FREEZE_SPAWN_EGG = registerSpawnEgg();

    public static final Item SPACE_HELMET = armor("space_helmet", SpaceSuitMaterial.SPACE, EntityEquipmentSlot.HEAD);
    public static final Item SPACE_SUIT = armor("space_suit", SpaceSuitMaterial.SPACE, EntityEquipmentSlot.CHEST);
    public static final Item SPACE_PANTS = armor("space_pants", SpaceSuitMaterial.SPACE, EntityEquipmentSlot.LEGS);
    public static final Item SPACE_BOOTS = armor("space_boots", SpaceSuitMaterial.SPACE, EntityEquipmentSlot.FEET);
    public static final Item NETHERITE_SPACE_HELMET = armor("netherite_space_helmet", SpaceSuitMaterial.NETHERITE_SPACE, EntityEquipmentSlot.HEAD);
    public static final Item NETHERITE_SPACE_SUIT = armor("netherite_space_suit", SpaceSuitMaterial.NETHERITE_SPACE, EntityEquipmentSlot.CHEST);
    public static final Item NETHERITE_SPACE_PANTS = armor("netherite_space_pants", SpaceSuitMaterial.NETHERITE_SPACE, EntityEquipmentSlot.LEGS);
    public static final Item NETHERITE_SPACE_BOOTS = armor("netherite_space_boots", SpaceSuitMaterial.NETHERITE_SPACE, EntityEquipmentSlot.FEET);
    public static final Item JET_SUIT_HELMET = armor("jet_suit_helmet", SpaceSuitMaterial.JET, EntityEquipmentSlot.HEAD);
    public static final Item JET_SUIT = armor("jet_suit", SpaceSuitMaterial.JET, EntityEquipmentSlot.CHEST);
    public static final Item JET_SUIT_PANTS = armor("jet_suit_pants", SpaceSuitMaterial.JET, EntityEquipmentSlot.LEGS);
    public static final Item JET_SUIT_BOOTS = armor("jet_suit_boots", SpaceSuitMaterial.JET, EntityEquipmentSlot.FEET);

    public static final Item OXYGEN_BUCKET = bucket("oxygen_bucket", ModBlocks.OXYGEN, ModFluids.OXYGEN);
    public static final Item HYDROGEN_BUCKET = bucket("hydrogen_bucket", ModBlocks.HYDROGEN, ModFluids.HYDROGEN);
    public static final Item OIL_BUCKET = bucket("oil_bucket", ModBlocks.OIL, ModFluids.OIL);
    public static final Item FUEL_BUCKET = bucket("fuel_bucket", ModBlocks.FUEL, ModFluids.FUEL);
    public static final Item CRYO_FUEL_BUCKET = bucket("cryo_fuel_bucket", ModBlocks.CRYO_FUEL, ModFluids.CRYO_FUEL);

    public static final Item TI_69 = ti69("ti_69");
    public static final Item WRENCH = wrench("wrench");
    public static final Item ZIP_GUN = zipGun("zip_gun");
    public static final Item ETRIONIC_CAPACITOR = energyItem("etrionic_capacitor", 250_000, 250, 500);
    public static final Item GAS_TANK = gasTank("gas_tank", GasTankItem.GAS_TANK_CAPACITY, GasTankItem.GAS_TANK_DISTRIBUTION_AMOUNT);
    public static final Item LARGE_GAS_TANK = gasTank("large_gas_tank", GasTankItem.LARGE_GAS_TANK_CAPACITY, GasTankItem.LARGE_GAS_TANK_DISTRIBUTION_AMOUNT);
    public static final Item SPACE_PAINTING = spacePainting("space_painting");

    public static final Item TIER_1_ROCKET = vehicle("tier_1_rocket", Tier1RocketEntity::new);
    public static final Item TIER_2_ROCKET = vehicle("tier_2_rocket", Tier2RocketEntity::new);
    public static final Item TIER_3_ROCKET = vehicle("tier_3_rocket", Tier3RocketEntity::new);
    public static final Item TIER_4_ROCKET = vehicle("tier_4_rocket", Tier4RocketEntity::new);
    public static final Item TIER_5_ROCKET = vehicle("tier_5_rocket", Tier5RocketEntity::new);
    public static final Item TIER_6_ROCKET = vehicle("tier_6_rocket", Tier6RocketEntity::new);
    public static final Item TIER_7_ROCKET = vehicle("tier_7_rocket", Tier7RocketEntity::new);
    public static final Item TIER_1_ROVER = vehicle("tier_1_rover", Tier1RoverEntity::new);
    public static final List<ConfigurableRocketItem> CONFIGURABLE_ROCKETS = configurableRockets();

    public static final Item STAR_CRAWLER_SPAWN_EGG = spawnEgg("star_crawler_spawn_egg", StarCrawlerEntity::new, 0xff000000, 0xff444444);
    public static final Item PYGRO_SPAWN_EGG = spawnEgg("pygro_spawn_egg", PygroEntity::new, 0xffcc6600, 0xfffef978);
    public static final Item CORRUPTED_LUNARIAN_SPAWN_EGG = spawnEgg("corrupted_lunarian_spawn_egg", CorruptedLunarianEntity::new, 0xff5b73c7, 0xff8244d5);
    public static final Item LUNARIAN_SPAWN_EGG = spawnEgg("lunarian_spawn_egg", LunarianEntity::new, 0xff5b73c7, 0xff8244d5);
    public static final Item MARTIAN_RAPTOR_SPAWN_EGG = spawnEgg("martian_raptor_spawn_egg", MartianRaptorEntity::new, 0xffd48f30, 0xffac791c);
    public static final Item ZOMBIFIED_PYGRO_SPAWN_EGG = spawnEgg("zombified_pygro_spawn_egg", ZombifiedPygroEntity::new, 0xffcc6600, 0xff799c65);
    public static final Item PYGRO_BRUTE_SPAWN_EGG = spawnEgg("pygro_brute_spawn_egg", PygroBruteEntity::new, 0xffcc6600, 0xfffef978);
    public static final Item MOGLER_SPAWN_EGG = spawnEgg("mogler_spawn_egg", MoglerEntity::new, 0xffffcc00, 0xffcc0000);
    public static final Item ZOMBIFIED_MOGLER_SPAWN_EGG = spawnEgg("zombified_mogler_spawn_egg", ZombifiedMoglerEntity::new, 0xbf4e41, 0x79e655);
    public static final Item SULFUR_CREEPER_SPAWN_EGG = spawnEgg("sulfur_creeper_spawn_egg", SulfurCreeperEntity::new, 0xd48f30, 0xac791c);
    public static final Item GLACIAN_RAM_SPAWN_EGG = spawnEgg("glacian_ram_spawn_egg", GlacianRamEntity::new, 0xffe6ff, 0x433d3d);
    public static final Item LUNARIAN_WANDERING_TRADER_SPAWN_EGG = spawnEgg("lunarian_wandering_trader_spawn_egg", LunarianWanderingTraderEntity::new, 0x5b73c7, 0x8244d5);
    private ModItems() {
    }

    @SubscribeEvent
    public static void register(RegistryEvent.Register<Item> event) {
        List<Item> all = new ArrayList<>();
        for (Block block : ModBlocks.BLOCKS) {
            if (block instanceof AdAstraFluidBlock) {
                continue;
            }
            Item blockItem = ModBlocks.createItemBlock(block);
            if (blockItem != null) {
                all.add(blockItem);
            }
        }
        all.addAll(INTERNAL_ITEMS);
        event.getRegistry().registerAll(all.toArray(new Item[0]));
    }

    public static Item get(String name) {
        return BY_NAME.get(name);
    }

    private static Item item(String name) {
        return item(name, 64);
    }

    private static Item item(String name, int maxStackSize) {
        Item item = new Item();
        item.setRegistryName(Reference.MOD_ID, name);
        item.setTranslationKey(Reference.MOD_ID + "." + name);
        item.setCreativeTab(AdAstraCreativeTab.INSTANCE);
        item.setMaxStackSize(maxStackSize);
        INTERNAL_ITEMS.add(item);
        return item;
    }

    private static Item food(String name, int amount, float saturation) {
        Item item = new ItemFood(amount, saturation, false);
        item.setRegistryName(Reference.MOD_ID, name);
        item.setTranslationKey(Reference.MOD_ID + "." + name);
        item.setCreativeTab(AdAstraCreativeTab.INSTANCE);
        INTERNAL_ITEMS.add(item);
        return item;
    }

    private static Item armor(String name, SpaceSuitMaterial material, EntityEquipmentSlot slot) {
        Item item;
        switch (material) {
            case NETHERITE_SPACE:
                item = new NetheriteSpaceSuitItem(name, slot);
                break;
            case JET:
                item = new JetSuitItem(name, slot);
                break;
            case SPACE:
            default:
                item = new SpaceSuitItem(name, slot);
                break;
        }
        INTERNAL_ITEMS.add(item);
        return item;
    }

    private static Item wrench(String name) {
        Item item = new AdAstraWrenchItem(name);
        INTERNAL_ITEMS.add(item);
        return item;
    }

    private static Item energyItem(String name, int capacity, int maxReceive, int maxExtract) {
        Item item = new AdAstraEnergyItem(name, capacity, maxReceive, maxExtract);
        INTERNAL_ITEMS.add(item);
        return item;
    }

    private static Item gasTank(String name, int capacity, int distributionAmount) {
        Item item = new GasTankItem(name, capacity, distributionAmount);
        INTERNAL_ITEMS.add(item);
        return item;
    }

    private static Item ti69(String name) {
        Item item = new Ti69Item(name);
        INTERNAL_ITEMS.add(item);
        return item;
    }

    private static Item zipGun(String name) {
        Item item = new ZipGunItem(name);
        INTERNAL_ITEMS.add(item);
        return item;
    }

    private static Item spacePainting(String name) {
        Item item = new SpacePaintingItem(name);
        INTERNAL_ITEMS.add(item);
        return item;
    }

    private static List<ConfigurableRocketItem> configurableRockets() {
        List<ConfigurableRocketItem> rockets = new ArrayList<>();
        for (ConfigurableRocketSpec spec : ConfigurableRocketRegistry.getRockets()) {
            if (spec.getItem() != null) {
                rockets.add(spec.getItem());
                continue;
            }
            ConfigurableRocketItem item = new ConfigurableRocketItem(spec);
            spec.setItem(item);
            INTERNAL_ITEMS.add(item);
            rockets.add(item);
        }
        return Collections.unmodifiableList(rockets);
    }

    private static Item vehicle(String name, java.util.function.Function<net.minecraft.world.World, ? extends earth.terrarium.adastra.common.entities.vehicles.AdAstraVehicleEntity> factory) {
        Item item = new VehicleItem(name, factory);
        INTERNAL_ITEMS.add(item);
        return item;
    }

    private static Item spawnEgg(String name, java.util.function.Function<net.minecraft.world.World, ? extends net.minecraft.entity.EntityLivingBase> factory,
                                 int primaryColor, int secondaryColor) {
        Item item = new AdAstraSpawnEggItem(name, factory, primaryColor, secondaryColor);
        INTERNAL_ITEMS.add(item);
        return item;
    }

    private static Item bucket(String name, Block fluidBlock, Fluid fluid) {
        Item item = new AdAstraBucketItem(fluidBlock, fluid);
        item.setRegistryName(Reference.MOD_ID, name);
        item.setTranslationKey(Reference.MOD_ID + "." + name);
        item.setCreativeTab(AdAstraCreativeTab.INSTANCE);
        item.setContainerItem(Items.BUCKET);
        item.setMaxStackSize(1);
        INTERNAL_ITEMS.add(item);
        return item;
    }

    private static Item registerItem(String name) {
        return registerItem(new Item(), name);
    }

    private static Item registerItem(Item item, String name) {
        item.setRegistryName(ModResourceIds.item(name));
        item.setTranslationKey(Reference.MOD_ID + "." + ModResourceIds.itemPath(name));
        item.setCreativeTab(AdAstraCreativeTab.INSTANCE);
        INTERNAL_ITEMS.add(item);
        BY_NAME.put(name, item);
        return item;
    }

    private static Item registerIceCharge() {
        return registerItem(new IceChargeItem("ice_charge", false), "ice_charge");
    }

    private static Item registerRocket(int tier, int fuelCapacity) {
        String id = "tier_" + tier + "_rocket";
        int modelTier = Math.min(Math.max(tier - 3, 5), 12);
        ConfigurableRocketSpec spec = new ConfigurableRocketSpec(
            id,
            "Tier " + tier + " Rocket",
            tier,
            fuelCapacity,
            modelTier,
            new ResourceLocation(Reference.MOD_ID, "textures/entity/rocket/tier_" + tier + "_rocket.png"),
            null,
            "ad_astra:textures/entity/rocket/tier_" + tier + "_rocket.png",
            true,
            true);
        ConfigurableRocketItem item = new ConfigurableRocketItem(spec, false);
        registerItem(item, id);
        spec.setItem(item);
        ConfigurableRocketRegistry.registerBuiltIn(spec);
        return item;
    }

    private static Item registerSpawnEgg() {
        return registerItem(new AdAstraSpawnEggItem(
            "freeze_spawn_egg",
            earth.terrarium.adastra.common.entities.mob.FreezeEntity::new,
            0xCBF8FF,
            0x79B7D0,
            false), "freeze_spawn_egg");
    }
}
