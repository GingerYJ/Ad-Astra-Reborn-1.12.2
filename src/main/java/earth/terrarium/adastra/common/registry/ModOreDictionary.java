package earth.terrarium.adastra.common.registry;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public final class ModOreDictionary {

    private ModOreDictionary() {
    }

    public static void register() {
        registerMetalSet("steel", ModItems.STEEL_INGOT, ModItems.STEEL_NUGGET, ModItems.STEEL_PLATE, ModItems.STEEL_ROD, ModBlocks.STEEL_BLOCK);
        registerMetalSet("etrium", ModItems.ETRIUM_INGOT, ModItems.ETRIUM_NUGGET, ModItems.ETRIUM_PLATE, ModItems.ETRIUM_ROD, ModBlocks.ETRIUM_BLOCK);
        registerMetalSet("desh", ModItems.DESH_INGOT, ModItems.DESH_NUGGET, ModItems.DESH_PLATE, null, ModBlocks.DESH_BLOCK);
        registerMetalSet("ostrum", ModItems.OSTRUM_INGOT, ModItems.OSTRUM_NUGGET, ModItems.OSTRUM_PLATE, null, ModBlocks.OSTRUM_BLOCK);
        registerMetalSet("calorite", ModItems.CALORITE_INGOT, ModItems.CALORITE_NUGGET, ModItems.CALORITE_PLATE, null, ModBlocks.CALORITE_BLOCK);
        register("ingotCopper", ModItems.COPPER_INGOT);
        register("blockCopper", ModBlocks.COPPER_BLOCK);

        register("plateIron", ModItems.IRON_PLATE);
        register("rodIron", ModItems.IRON_ROD);
        register("coreEtrionic", ModItems.ETRIONIC_CORE);
        register("toolWrench", ModItems.WRENCH);
        register("craftingToolWrench", ModItems.WRENCH);
        register("wrench", ModItems.WRENCH);

        register("rawDesh", ModItems.RAW_DESH);
        register("rawOstrum", ModItems.RAW_OSTRUM);
        register("rawCalorite", ModItems.RAW_CALORITE);
        register("blockRawDesh", ModBlocks.RAW_DESH_BLOCK);
        register("blockRawOstrum", ModBlocks.RAW_OSTRUM_BLOCK);
        register("blockRawCalorite", ModBlocks.RAW_CALORITE_BLOCK);

        register("oreCheese", ModBlocks.MOON_CHEESE_ORE);
        register("oreDesh", ModBlocks.MOON_DESH_ORE);
        register("oreDesh", ModBlocks.DEEPSLATE_DESH_ORE);
        register("oreOstrum", ModBlocks.MARS_OSTRUM_ORE);
        register("oreOstrum", ModBlocks.DEEPSLATE_OSTRUM_ORE);
        register("oreCalorite", ModBlocks.VENUS_CALORITE_ORE);
        register("oreCalorite", ModBlocks.DEEPSLATE_CALORITE_ORE);
        register("oreIceShard", ModBlocks.MOON_ICE_SHARD_ORE);
        register("oreIceShard", ModBlocks.DEEPSLATE_ICE_SHARD_ORE);
        register("oreIceShard", ModBlocks.MARS_ICE_SHARD_ORE);
        register("oreIceShard", ModBlocks.GLACIO_ICE_SHARD_ORE);

        register("oreIron", ModBlocks.MOON_IRON_ORE);
        register("oreIron", ModBlocks.MARS_IRON_ORE);
        register("oreIron", ModBlocks.MERCURY_IRON_ORE);
        register("oreIron", ModBlocks.GLACIO_IRON_ORE);
        register("oreGold", ModBlocks.VENUS_GOLD_ORE);
        register("oreCopper", ModBlocks.GLACIO_COPPER_ORE);
        register("oreCoal", ModBlocks.VENUS_COAL_ORE);
        register("oreCoal", ModBlocks.GLACIO_COAL_ORE);
        register("oreDiamond", ModBlocks.MARS_DIAMOND_ORE);
        register("oreDiamond", ModBlocks.VENUS_DIAMOND_ORE);
        register("oreLapis", ModBlocks.GLACIO_LAPIS_ORE);

        register("gemCheese", ModItems.CHEESE);
        register("gemIceShard", ModItems.ICE_SHARD);
        register("blockCheese", ModBlocks.CHEESE_BLOCK);

        registerMaterial("juperium", ModItems.JUPERIUM_INGOT, ModItems.JUPERIUM_NUGGET,
            ModItems.JUPERIUM_PLATE, ModBlocks.getMaterialBlock("juperium"));
        registerMaterial("saturlyte", ModItems.SATURLYTE_INGOT, ModItems.SATURLYTE_NUGGET,
            ModItems.SATURLYTE_PLATE, ModBlocks.getMaterialBlock("saturlyte"));
        registerMaterial("uranium", ModItems.URANIUM_INGOT, ModItems.URANIUM_NUGGET,
            ModItems.URANIUM_PLATE, ModBlocks.getMaterialBlock("uranium"));
        registerMaterial("neptunium", ModItems.NEPTUNIUM_INGOT, ModItems.NEPTUNIUM_NUGGET,
            ModItems.NEPTUNIUM_PLATE, ModBlocks.getMaterialBlock("neptunium"));
        registerMaterial("radium", ModItems.RADIUM_INGOT, ModItems.RADIUM_NUGGET,
            ModItems.RADIUM_PLATE, ModBlocks.getMaterialBlock("radium"));
        registerMaterial("plutonium", ModItems.PLUTONIUM_INGOT, ModItems.PLUTONIUM_NUGGET,
            ModItems.PLUTONIUM_PLATE, ModBlocks.getMaterialBlock("plutonium"));
        registerMaterial("electrolyte", ModItems.ELECTROLYTE_INGOT, ModItems.ELECTROLYTE_NUGGET,
            ModItems.ELECTROLYTE_PLATE, ModBlocks.getMaterialBlock("electrolyte"));
        registerMaterial("aurorite", ModItems.AURORITE_INGOT, ModItems.AURORITE_NUGGET,
            ModItems.AURORITE_PLATE, ModBlocks.getMaterialBlock("aurorite"));

        register("rawJuperium", ModItems.RAW_JUPERIUM);
        register("rawSaturlyte", ModItems.RAW_SATURLYTE);
        register("rawUranium", ModItems.RAW_URANIUM);
        register("rawNeptunium", ModItems.RAW_NEPTUNIUM);
        register("rawRadium", ModItems.RAW_RADIUM);
        register("rawPlutonium", ModItems.RAW_PLUTONIUM);
        register("rawElectrolyte", ModItems.RAW_ELECTROLYTE);
        register("rawAurorite", ModItems.RAW_AURORITE);
        register("gemFreezeShard", ModItems.FREEZE_SHARD);

        registerOreFamily("oreCopper",
            "ceres_copper_ore", "neptune_copper_ore", "orcus_copper_ore", "haumea_copper_ore",
            "quaoar_copper_ore", "makemake_copper_ore", "gonggong_copper_ore", "eris_copper_ore",
            "sedna_copper_ore");
        registerOreFamily("oreIron",
            "ceres_iron_ore", "uranus_iron_ore", "neptune_iron_ore", "orcus_iron_ore",
            "haumea_iron_ore", "quaoar_iron_ore", "makemake_iron_ore", "gonggong_iron_ore",
            "eris_iron_ore", "sedna_iron_ore", "proxima_centauri_b_iron_ore");
        registerOreFamily("oreCoal", "jupiter_coal_ore", "saturn_coal_ore", "neptune_coal_ore");
        registerOreFamily("oreGold", "jupiter_gold_ore", "saturn_gold_ore", "pluto_gold_ore");
        registerOreFamily("oreDiamond",
            "jupiter_diamond_ore", "saturn_diamond_ore", "uranus_diamond_ore",
            "pluto_diamond_ore", "proxima_centauri_b_diamond_ore");
        registerOreFamily("oreLapis", "uranus_lapis_ore");
        registerOreFamily("oreRedstone", "proxima_centauri_b_redstone_ore");
        registerOreFamily("oreEmerald", "proxima_centauri_b_emerald_ore");

        registerOreFamily("oreJuperium", "jupiter_juperium_ore");
        registerOreFamily("oreSaturlyte", "saturn_saturlyte_ore");
        registerOreFamily("oreUranium", "uranus_uranium_ore");
        registerOreFamily("oreNeptunium", "neptune_neptunium_ore");
        registerOreFamily("oreRadium", "orcus_radium_ore");
        registerOreFamily("orePlutonium", "pluto_plutonium_ore");
        registerOreFamily("oreElectrolyte", "sedna_electrolyte_ore");
        registerOreFamily("oreAurorite", "vicinus_aurorite_ore");
        registerOreFamily("oreIceShard",
            "uranus_ice_shard_ore", "neptune_ice_shard_ore", "pluto_ice_shard_ore");
        registerOreFamily("oreFreezeShard",
            "uranus_ice_shard_ore", "neptune_ice_shard_ore", "pluto_ice_shard_ore");

        register("logCentaurianOak", ModBlocks.get("centaurian_oak_log"));
        register("logCentaurianOak", ModBlocks.get("centaurian_oak_wood"));
        register("logCentaurianOak", ModBlocks.get("stripped_centaurian_oak_log"));
        register("logCentaurianOak", ModBlocks.get("stripped_centaurian_oak_wood"));
    }

    private static void registerMetalSet(String material, Item ingot, Item nugget, Item plate, Item rod, Block block) {
        String suffix = material.substring(0, 1).toUpperCase() + material.substring(1);
        register("ingot" + suffix, ingot);
        register("nugget" + suffix, nugget);
        register("plate" + suffix, plate);
        register("block" + suffix, block);
        if (rod != null) {
            register("rod" + suffix, rod);
        }
    }

    private static void register(String name, Item item) {
        if (item != null) {
            OreDictionary.registerOre(name, new ItemStack(item));
        }
    }

    private static void register(String name, Block block) {
        if (block != null) {
            OreDictionary.registerOre(name, new ItemStack(block));
        }
    }

    private static void register(String name, Block block, int meta) {
        if (block != null) {
            OreDictionary.registerOre(name, new ItemStack(block, 1, meta));
        }
    }

    private static void registerMaterial(String name, Item ingot, Item nugget, Item plate, Block block) {
        String suffix = Character.toUpperCase(name.charAt(0)) + name.substring(1);
        register("ingot" + suffix, ingot);
        register("nugget" + suffix, nugget);
        register("plate" + suffix, plate);
        register("block" + suffix, block);
    }

    private static void registerOreFamily(String name, String... oreNames) {
        for (String oreName : oreNames) {
            register(name, ModBlocks.getOre(oreName));
        }
    }
}
