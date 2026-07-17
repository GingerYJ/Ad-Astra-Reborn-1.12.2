package earth.terrarium.adastra.common.registry;

import earth.terrarium.adastra.Reference;
import earth.terrarium.adastra.common.AdAstraCreativeTab;
import earth.terrarium.adastra.common.items.AdAstraSpawnEggItem;
import earth.terrarium.adastra.common.items.ConfigurableRocketItem;
import earth.terrarium.adastra.common.items.ExtendraIceChargeItem;
import earth.terrarium.adastra.common.rocket.ConfigurableRocketSpec;
import earth.terrarium.adastra.common.rocket.ConfigurableRocketRegistry;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Items introduced by the Ad Extendra content port. */
public final class ExtendraItems {

    private static final List<Item> INTERNAL_ITEMS = new ArrayList<>();
    private static final Map<String, Item> BY_NAME = new LinkedHashMap<>();

    public static final List<Item> ITEMS = Collections.unmodifiableList(INTERNAL_ITEMS);

    public static final Item JUPERIUM_INGOT = item("juperium_ingot");
    public static final Item JUPERIUM_NUGGET = item("juperium_nugget");
    public static final Item JUPERIUM_PLATE = item("juperium_plate");
    public static final Item RAW_JUPERIUM = item("raw_juperium");
    public static final Item JUPERIUM_ENGINE = item("juperium_engine");
    public static final Item JUPERIUM_TANK = item("juperium_tank");

    public static final Item SATURLYTE_INGOT = item("saturlyte_ingot");
    public static final Item SATURLYTE_NUGGET = item("saturlyte_nugget");
    public static final Item SATURLYTE_PLATE = item("saturlyte_plate");
    public static final Item RAW_SATURLYTE = item("raw_saturlyte");
    public static final Item SATURLYTE_ENGINE = item("saturlyte_engine");
    public static final Item SATURLYTE_TANK = item("saturlyte_tank");

    public static final Item URANIUM_INGOT = item("uranium_ingot");
    public static final Item URANIUM_NUGGET = item("uranium_nugget");
    public static final Item URANIUM_PLATE = item("uranium_plate");
    public static final Item RAW_URANIUM = item("raw_uranium");
    public static final Item URANIUM_ENGINE = item("uranium_engine");
    public static final Item URANIUM_TANK = item("uranium_tank");

    public static final Item NEPTUNIUM_INGOT = item("neptunium_ingot");
    public static final Item NEPTUNIUM_NUGGET = item("neptunium_nugget");
    public static final Item NEPTUNIUM_PLATE = item("neptunium_plate");
    public static final Item RAW_NEPTUNIUM = item("raw_neptunium");
    public static final Item NEPTUNIUM_ENGINE = item("neptunium_engine");
    public static final Item NEPTUNIUM_TANK = item("neptunium_tank");

    public static final Item RADIUM_INGOT = item("radium_ingot");
    public static final Item RADIUM_NUGGET = item("radium_nugget");
    public static final Item RADIUM_PLATE = item("radium_plate");
    public static final Item RAW_RADIUM = item("raw_radium");
    public static final Item RADIUM_ENGINE = item("radium_engine");
    public static final Item RADIUM_TANK = item("radium_tank");

    public static final Item PLUTONIUM_INGOT = item("plutonium_ingot");
    public static final Item PLUTONIUM_NUGGET = item("plutonium_nugget");
    public static final Item PLUTONIUM_PLATE = item("plutonium_plate");
    public static final Item RAW_PLUTONIUM = item("raw_plutonium");
    public static final Item PLUTONIUM_ENGINE = item("plutonium_engine");
    public static final Item PLUTONIUM_TANK = item("plutonium_tank");

    public static final Item ELECTROLYTE_INGOT = item("electrolyte_ingot");
    public static final Item ELECTROLYTE_NUGGET = item("electrolyte_nugget");
    public static final Item ELECTROLYTE_PLATE = item("electrolyte_plate");
    public static final Item RAW_ELECTROLYTE = item("raw_electrolyte");
    public static final Item ELECTROLYTE_ENGINE = item("electrolyte_engine");
    public static final Item ELECTROLYTE_TANK = item("electrolyte_tank");

    public static final Item AURORITE_INGOT = item("aurorite_ingot");
    public static final Item AURORITE_NUGGET = item("aurorite_nugget");
    public static final Item AURORITE_PLATE = item("aurorite_plate");
    public static final Item RAW_AURORITE = item("raw_aurorite");
    public static final Item AURORITE_ENGINE = item("aurorite_engine");
    public static final Item AURORITE_TANK = item("aurorite_tank");

    public static final Item FREEZE_SHARD = item("freeze_shard");
    public static final Item ICE_CHARGE = iceCharge();

    // The imported extension continues the core rocket progression after tier 7.
    public static final Item TIER_8_ROCKET = rocket(8, 18000);
    public static final Item TIER_9_ROCKET = rocket(9, 19000);
    public static final Item TIER_10_ROCKET = rocket(10, 20000);
    public static final Item TIER_11_ROCKET = rocket(11, 21000);
    public static final Item TIER_12_ROCKET = rocket(12, 22000);
    public static final Item TIER_13_ROCKET = rocket(13, 23000);
    public static final Item TIER_14_ROCKET = rocket(14, 24000);
    public static final Item TIER_15_ROCKET = rocket(15, 25000);

    public static final Item FREEZE_SPAWN_EGG = new AdAstraSpawnEggItem(
        "freeze_spawn_egg", earth.terrarium.adastra.common.entities.mob.ExtendraFreezeEntity::new,
        0xCBF8FF, 0x79B7D0);

    static {
        INTERNAL_ITEMS.add(FREEZE_SPAWN_EGG);
        BY_NAME.put("freeze_spawn_egg", FREEZE_SPAWN_EGG);
    }

    private ExtendraItems() {
    }

    public static Item get(String name) {
      return BY_NAME.get(name);
    }

    public static void register(net.minecraftforge.event.RegistryEvent.Register<Item> event) {
        event.getRegistry().registerAll(INTERNAL_ITEMS.toArray(new Item[0]));
    }

    private static Item item(String name) {
        Item item = new Item();
        item.setRegistryName(Reference.MOD_ID, name);
        item.setTranslationKey(Reference.MOD_ID + "." + name);
        item.setCreativeTab(AdAstraCreativeTab.INSTANCE);
        INTERNAL_ITEMS.add(item);
        BY_NAME.put(name, item);
        return item;
    }

    private static Item iceCharge() {
        Item item = new ExtendraIceChargeItem("ice_charge");
        INTERNAL_ITEMS.add(item);
        BY_NAME.put("ice_charge", item);
        return item;
    }

    private static Item rocket(int tier, int fuelCapacity) {
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
        ConfigurableRocketItem item = new ConfigurableRocketItem(spec);
        spec.setItem(item);
        ConfigurableRocketRegistry.registerBuiltIn(spec);
        INTERNAL_ITEMS.add(item);
        BY_NAME.put(id, item);
        return item;
    }
}
