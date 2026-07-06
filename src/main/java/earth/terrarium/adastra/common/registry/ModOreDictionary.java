package earth.terrarium.adastra.common.registry;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
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
        register("ingotNickel", ModItems.NICKEL_INGOT);
        register("ingotCopper", ModItems.COPPER_INGOT);
        register("blockCopper", ModBlocks.COPPER_BLOCK);
        register("ingotMagnesium", ModItems.MAGNESIUM_INGOT);
        register("blockMagnesium", ModBlocks.MAGNESIUM_BLOCK);
        register("ingotCobalt", ModItems.COBALT_INGOT);
        register("blockCobalt", ModBlocks.COBALT_BLOCK);
        register("blockNickel", ModBlocks.NICKEL_BLOCK);
        register("blockUranium", ModBlocks.URANIUM_BLOCK);
        register("blockMeteoricIron", ModBlocks.METEORIC_IRON_BLOCK);
        register("ingotUranium", ModItems.URANIUM_INGOT);
        register("rawMeteoricIron", ModItems.METEORIC_IRON_FRAGMENTS);
        register("dustMeteoricIron", ModItems.METEORIC_IRON_FRAGMENTS);
        register("rawUranium", ModItems.URANIUM_FRAGMENTS);
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

        register("oreDolomite", ModBlocks.CERES_BLOCKS, 2);
        register("oreMeteoricIron", ModBlocks.CERES_BLOCKS, 3);
        register("oreIron", ModBlocks.PLUTO_BLOCKS, 2);
        register("oreSulfur", ModBlocks.PLUTO_BLOCKS, 3);
        register("oreUranium", ModBlocks.PLUTO_BLOCKS, 4);
        register("oreDolomite", ModBlocks.HAUMEA_BLOCKS, 2);
        register("oreSulfur", ModBlocks.IO_BLOCKS, 8);
        register("oreVolcanic", ModBlocks.IO_BLOCKS, 9);
        register("oreSilicon", ModBlocks.EUROPA_BLOCKS, 5);
        register("oreIron", ModBlocks.EUROPA_BLOCKS, 6);
        register("oreMagnesium", ModBlocks.GANYMEDE_BLOCKS, 2);
        register("oreIlmenite", ModBlocks.GANYMEDE_BLOCKS, 3);
        register("oreCoal", ModBlocks.ENCELADUS_BLOCKS, 2);
        register("oreSapphire", ModBlocks.TITAN_BLOCKS, 3);
        register("oreEmerald", ModBlocks.TITAN_BLOCKS, 4);
        register("oreDiamond", ModBlocks.TITAN_BLOCKS, 5);
        register("oreCoal", ModBlocks.TITAN_BLOCKS, 6);
        register("oreLapis", ModBlocks.TITAN_BLOCKS, 7);
        register("oreRedstone", ModBlocks.TITAN_BLOCKS, 8);
        register("oreIron", ModBlocks.MIRANDA_BLOCKS, 3);
        register("oreDolomite", ModBlocks.MIRANDA_BLOCKS, 4);
        register("oreDiamond", ModBlocks.MIRANDA_BLOCKS, 5);
        register("oreQuartz", ModBlocks.MIRANDA_BLOCKS, 6);
        register("oreCobalt", ModBlocks.MIRANDA_BLOCKS, 7);
        register("oreNickel", ModBlocks.MIRANDA_BLOCKS, 8);
        register("oreIron", ModBlocks.PHOBOS_BLOCKS, 2);
        register("oreMeteoricIron", ModBlocks.PHOBOS_BLOCKS, 3);
        register("oreNickel", ModBlocks.PHOBOS_BLOCKS, 4);
        register("oreDesh", ModBlocks.PHOBOS_BLOCKS, 5);
        register("oreIron", ModBlocks.BARNARDA_C_BLOCKS, 11);
        register("oreGold", ModBlocks.BARNARDA_C_BLOCKS, 12);
        register("oreCoal", ModBlocks.BARNARDA_C_BLOCKS, 13);
        register("oreIron", ModBlocks.BARNARDA_C1_BLOCKS, 3);
        register("oreIron", ModBlocks.TAUCETI_F_BLOCKS, 5);
        register("oreCoal", ModBlocks.TAUCETI_F_BLOCKS, 6);
        register("oreGold", ModBlocks.TAUCETI_F_BLOCKS, 7);
        register("oreDiamond", ModBlocks.TAUCETI_F_BLOCKS, 8);
        register("oreLapis", ModBlocks.TAUCETI_F_BLOCKS, 9);

        register("gemCheese", ModItems.CHEESE);
        register("gemIceShard", ModItems.ICE_SHARD);
        register("blockCheese", ModBlocks.CHEESE_BLOCK);
        registerPlanetaryExclusiveResourceOreDictionary();
    }

    private static void registerPlanetaryExclusiveResourceOreDictionary() {
        registerPlanetaryResource("mercury", "hermium");
        registerPlanetaryResource("glacio", "cryonite");
        registerPlanetaryResource("ceres", "cerium");
        registerPlanetaryResource("pluto", "plutonium");
        registerPlanetaryResource("haumea", "haumeite");
        registerPlanetaryResource("kuiper_belt", "kuiperite");
        registerPlanetaryResource("io", "ionite");
        registerPlanetaryResource("europa", "europium");
        registerPlanetaryResource("ganymede", "ganymedite");
        registerPlanetaryResource("callisto", "callistite");
        registerPlanetaryResource("enceladus", "enceladite");
        registerPlanetaryResource("titan", "titanite");
        registerPlanetaryResource("miranda", "mirandium");
        registerPlanetaryResource("triton", "tritonium");
        registerPlanetaryResource("phobos", "phobium");
        registerPlanetaryResource("barnarda_c", "barnardium");
        registerPlanetaryResource("barnarda_c1", "c1_barnardium");
        registerPlanetaryResource("tauceti_f", "taucetite");
        registerPlanetaryResource("proxima_b", "proximite");
    }

    private static void registerPlanetaryResource(String planet, String resource) {
        String suffix = toOreDictSuffix(resource);
        Item raw = Item.REGISTRY.getObject(new ResourceLocation("ad_astra", "raw_" + resource));
        Item ingot = Item.REGISTRY.getObject(new ResourceLocation("ad_astra", resource + "_ingot"));
        Block block = Block.REGISTRY.getObject(new ResourceLocation("ad_astra", resource + "_block"));
        Block ore = Block.REGISTRY.getObject(new ResourceLocation("ad_astra", planet + "_" + resource + "_ore"));
        register("raw" + suffix, raw);
        register("ingot" + suffix, ingot);
        register("block" + suffix, block);
        register("ore" + suffix, ore);
    }

    private static String toOreDictSuffix(String name) {
        StringBuilder builder = new StringBuilder();
        boolean upper = true;
        for (int i = 0; i < name.length(); i++) {
            char c = name.charAt(i);
            if (c == '_') {
                upper = true;
                continue;
            }
            builder.append(upper ? Character.toUpperCase(c) : c);
            upper = false;
        }
        return builder.toString();
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
}
