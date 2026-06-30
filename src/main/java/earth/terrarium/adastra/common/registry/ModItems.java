package earth.terrarium.adastra.common.registry;

import earth.terrarium.adastra.Reference;
import earth.terrarium.adastra.common.AdAstraCreativeTab;
import earth.terrarium.adastra.common.blocks.AdAstraFluidBlock;
import earth.terrarium.adastra.common.items.AdAstraArmorItem;
import earth.terrarium.adastra.common.items.AdAstraEnergyItem;
import earth.terrarium.adastra.common.items.AdAstraWrenchItem;
import earth.terrarium.adastra.common.items.GasTankItem;
import net.minecraft.block.Block;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.init.Items;
import net.minecraft.item.ItemFood;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBucket;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID)
public final class ModItems {

    private static final List<Item> INTERNAL_ITEMS = new ArrayList<>();

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

    public static final Item OXYGEN_BUCKET = bucket("oxygen_bucket", ModBlocks.OXYGEN);
    public static final Item HYDROGEN_BUCKET = bucket("hydrogen_bucket", ModBlocks.HYDROGEN);
    public static final Item OIL_BUCKET = bucket("oil_bucket", ModBlocks.OIL);
    public static final Item FUEL_BUCKET = bucket("fuel_bucket", ModBlocks.FUEL);
    public static final Item CRYO_FUEL_BUCKET = bucket("cryo_fuel_bucket", ModBlocks.CRYO_FUEL);

    public static final Item TI_69 = item("ti_69", 1);
    public static final Item WRENCH = wrench("wrench");
    public static final Item ZIP_GUN = item("zip_gun", 1);
    public static final Item ETRIONIC_CAPACITOR = energyItem("etrionic_capacitor", 250_000, 250, 500);
    public static final Item GAS_TANK = gasTank("gas_tank", GasTankItem.GAS_TANK_CAPACITY, GasTankItem.GAS_TANK_DISTRIBUTION_AMOUNT);
    public static final Item LARGE_GAS_TANK = gasTank("large_gas_tank", GasTankItem.LARGE_GAS_TANK_CAPACITY, GasTankItem.LARGE_GAS_TANK_DISTRIBUTION_AMOUNT);

    public static final Item SPACE_HELMET = armor("space_helmet", AdAstraArmorItem.SuitMaterial.SPACE, EntityEquipmentSlot.HEAD);
    public static final Item SPACE_SUIT = armor("space_suit", AdAstraArmorItem.SuitMaterial.SPACE, EntityEquipmentSlot.CHEST);
    public static final Item SPACE_PANTS = armor("space_pants", AdAstraArmorItem.SuitMaterial.SPACE, EntityEquipmentSlot.LEGS);
    public static final Item SPACE_BOOTS = armor("space_boots", AdAstraArmorItem.SuitMaterial.SPACE, EntityEquipmentSlot.FEET);

    public static final Item NETHERITE_SPACE_HELMET = armor("netherite_space_helmet", AdAstraArmorItem.SuitMaterial.NETHERITE_SPACE, EntityEquipmentSlot.HEAD);
    public static final Item NETHERITE_SPACE_SUIT = armor("netherite_space_suit", AdAstraArmorItem.SuitMaterial.NETHERITE_SPACE, EntityEquipmentSlot.CHEST);
    public static final Item NETHERITE_SPACE_PANTS = armor("netherite_space_pants", AdAstraArmorItem.SuitMaterial.NETHERITE_SPACE, EntityEquipmentSlot.LEGS);
    public static final Item NETHERITE_SPACE_BOOTS = armor("netherite_space_boots", AdAstraArmorItem.SuitMaterial.NETHERITE_SPACE, EntityEquipmentSlot.FEET);

    public static final Item JET_SUIT_HELMET = armor("jet_suit_helmet", AdAstraArmorItem.SuitMaterial.JET, EntityEquipmentSlot.HEAD);
    public static final Item JET_SUIT = armor("jet_suit", AdAstraArmorItem.SuitMaterial.JET, EntityEquipmentSlot.CHEST);
    public static final Item JET_SUIT_PANTS = armor("jet_suit_pants", AdAstraArmorItem.SuitMaterial.JET, EntityEquipmentSlot.LEGS);
    public static final Item JET_SUIT_BOOTS = armor("jet_suit_boots", AdAstraArmorItem.SuitMaterial.JET, EntityEquipmentSlot.FEET);

    public static final Item SPACE_PAINTING = item("space_painting");
    public static final Item TIER_1_ROCKET = item("tier_1_rocket", 1);
    public static final Item TIER_2_ROCKET = item("tier_2_rocket", 1);
    public static final Item TIER_3_ROCKET = item("tier_3_rocket", 1);
    public static final Item TIER_4_ROCKET = item("tier_4_rocket", 1);
    public static final Item TIER_1_ROVER = item("tier_1_rover", 1);

    public static final Item LUNARIAN_SPAWN_EGG = item("lunarian_spawn_egg", 1);
    public static final Item CORRUPTED_LUNARIAN_SPAWN_EGG = item("corrupted_lunarian_spawn_egg", 1);
    public static final Item STAR_CRAWLER_SPAWN_EGG = item("star_crawler_spawn_egg", 1);
    public static final Item MARTIAN_RAPTOR_SPAWN_EGG = item("martian_raptor_spawn_egg", 1);
    public static final Item PYGRO_SPAWN_EGG = item("pygro_spawn_egg", 1);
    public static final Item ZOMBIFIED_PYGRO_SPAWN_EGG = item("zombified_pygro_spawn_egg", 1);
    public static final Item PYGRO_BRUTE_SPAWN_EGG = item("pygro_brute_spawn_egg", 1);
    public static final Item MOGLER_SPAWN_EGG = item("mogler_spawn_egg", 1);
    public static final Item ZOMBIFIED_MOGLER_SPAWN_EGG = item("zombified_mogler_spawn_egg", 1);
    public static final Item SULFUR_CREEPER_SPAWN_EGG = item("sulfur_creeper_spawn_egg", 1);
    public static final Item GLACIAN_RAM_SPAWN_EGG = item("glacian_ram_spawn_egg", 1);
    public static final Item LUNARIAN_WANDERING_TRADER_SPAWN_EGG = item("lunarian_wandering_trader_spawn_egg", 1);

    private ModItems() {
    }

    @SubscribeEvent
    public static void register(RegistryEvent.Register<Item> event) {
        List<Item> all = new ArrayList<>();
        for (Block block : ModBlocks.BLOCKS) {
            if (block instanceof AdAstraFluidBlock) {
                continue;
            }
            all.add(ModBlocks.createItemBlock(block));
        }
        all.addAll(INTERNAL_ITEMS);
        event.getRegistry().registerAll(all.toArray(new Item[0]));
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

    private static Item armor(String name, AdAstraArmorItem.SuitMaterial material, EntityEquipmentSlot slot) {
        Item item = new AdAstraArmorItem(name, material, slot);
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

    private static Item bucket(String name, Block fluidBlock) {
        Item item = new ItemBucket(fluidBlock);
        item.setRegistryName(Reference.MOD_ID, name);
        item.setTranslationKey(Reference.MOD_ID + "." + name);
        item.setCreativeTab(AdAstraCreativeTab.INSTANCE);
        item.setContainerItem(Items.BUCKET);
        item.setMaxStackSize(1);
        INTERNAL_ITEMS.add(item);
        return item;
    }
}
