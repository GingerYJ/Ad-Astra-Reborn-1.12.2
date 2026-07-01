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
}
