package earth.terrarium.adastra.common.registry;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

/** Shared ore-dictionary names for the imported materials and ores. */
public final class ExtendraOreDictionary {

    private ExtendraOreDictionary() {
    }

    public static void register() {
        registerMaterial("juperium", ExtendraItems.JUPERIUM_INGOT, ExtendraItems.JUPERIUM_NUGGET,
            ExtendraItems.JUPERIUM_PLATE, ExtendraBlocks.getMaterialBlock("juperium"));
        registerMaterial("saturlyte", ExtendraItems.SATURLYTE_INGOT, ExtendraItems.SATURLYTE_NUGGET,
            ExtendraItems.SATURLYTE_PLATE, ExtendraBlocks.getMaterialBlock("saturlyte"));
        registerMaterial("uranium", ExtendraItems.URANIUM_INGOT, ExtendraItems.URANIUM_NUGGET,
            ExtendraItems.URANIUM_PLATE, ExtendraBlocks.getMaterialBlock("uranium"));
        registerMaterial("neptunium", ExtendraItems.NEPTUNIUM_INGOT, ExtendraItems.NEPTUNIUM_NUGGET,
            ExtendraItems.NEPTUNIUM_PLATE, ExtendraBlocks.getMaterialBlock("neptunium"));
        registerMaterial("radium", ExtendraItems.RADIUM_INGOT, ExtendraItems.RADIUM_NUGGET,
            ExtendraItems.RADIUM_PLATE, ExtendraBlocks.getMaterialBlock("radium"));
        registerMaterial("plutonium", ExtendraItems.PLUTONIUM_INGOT, ExtendraItems.PLUTONIUM_NUGGET,
            ExtendraItems.PLUTONIUM_PLATE, ExtendraBlocks.getMaterialBlock("plutonium"));
        registerMaterial("electrolyte", ExtendraItems.ELECTROLYTE_INGOT, ExtendraItems.ELECTROLYTE_NUGGET,
            ExtendraItems.ELECTROLYTE_PLATE, ExtendraBlocks.getMaterialBlock("electrolyte"));
        registerMaterial("aurorite", ExtendraItems.AURORITE_INGOT, ExtendraItems.AURORITE_NUGGET,
            ExtendraItems.AURORITE_PLATE, ExtendraBlocks.getMaterialBlock("aurorite"));

        register("rawJuperium", ExtendraItems.RAW_JUPERIUM);
        register("rawSaturlyte", ExtendraItems.RAW_SATURLYTE);
        register("rawUranium", ExtendraItems.RAW_URANIUM);
        register("rawNeptunium", ExtendraItems.RAW_NEPTUNIUM);
        register("rawRadium", ExtendraItems.RAW_RADIUM);
        register("rawPlutonium", ExtendraItems.RAW_PLUTONIUM);
        register("rawElectrolyte", ExtendraItems.RAW_ELECTROLYTE);
        register("rawAurorite", ExtendraItems.RAW_AURORITE);
        register("gemFreezeShard", ExtendraItems.FREEZE_SHARD);

        registerOreFamily("oreCopper",
            "ceres_copper_ore", "neptune_copper_ore", "orcus_copper_ore", "haumea_copper_ore",
            "quaoar_copper_ore", "makemake_copper_ore", "gonggong_copper_ore", "eris_copper_ore",
            "sedna_copper_ore");
        registerOreFamily("oreIron",
            "ceres_iron_ore", "uranus_iron_ore", "neptune_iron_ore", "orcus_iron_ore",
            "haumea_iron_ore", "quaoar_iron_ore", "makemake_iron_ore", "gonggong_iron_ore",
            "eris_iron_ore", "sedna_iron_ore", "b_iron_ore");
        registerOreFamily("oreCoal", "jupiter_coal_ore", "saturn_coal_ore", "neptune_coal_ore");
        registerOreFamily("oreGold", "jupiter_gold_ore", "saturn_gold_ore", "pluto_gold_ore");
        registerOreFamily("oreDiamond",
            "jupiter_diamond_ore", "saturn_diamond_ore", "uranus_diamond_ore",
            "pluto_diamond_ore", "b_diamond_ore");
        registerOreFamily("oreLapis", "uranus_lapis_ore");
        registerOreFamily("oreRedstone", "b_redstone_ore");
        registerOreFamily("oreEmerald", "b_emerald_ore");

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
        // Keep the old material name for recipes from earlier extension builds.
        registerOreFamily("oreFreezeShard",
            "uranus_ice_shard_ore", "neptune_ice_shard_ore", "pluto_ice_shard_ore");

        // The source wood tag is converted to the 1.12 OreDictionary name used
        // by the workbench recipe loader.
        register("logCentaurianOak", ExtendraBlocks.get("centaurian_oak_log"));
        register("logCentaurianOak", ExtendraBlocks.get("centaurian_oak_wood"));
        register("logCentaurianOak", ExtendraBlocks.get("stripped_centaurian_oak_log"));
        register("logCentaurianOak", ExtendraBlocks.get("stripped_centaurian_oak_wood"));
    }

    private static void registerMaterial(String name, Item ingot, Item nugget, Item plate, Block block) {
        register("ingot" + capitalize(name), ingot);
        register("nugget" + capitalize(name), nugget);
        register("plate" + capitalize(name), plate);
        register("block" + capitalize(name), block);
    }

    private static void registerOreFamily(String name, String... oreNames) {
        for (String oreName : oreNames) {
            register(name, ExtendraBlocks.getOre(oreName));
        }
    }

    private static String capitalize(String value) {
        return value.substring(0, 1).toUpperCase() + value.substring(1);
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
}
