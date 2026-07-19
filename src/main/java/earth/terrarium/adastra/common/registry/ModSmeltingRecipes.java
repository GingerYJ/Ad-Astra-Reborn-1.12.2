package earth.terrarium.adastra.common.registry;

import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;

public final class ModSmeltingRecipes {

    private static final float SOURCE_EXPERIENCE = 0.1f;

    private ModSmeltingRecipes() {
    }

    public static void register() {
        registerAdAstraMetalSmelting();
        registerPlanetaryOreSmelting();
        registerStoneSmelting();
        registerAdditionalMaterialSmelting();
        registerOreSmelting();
        registerAdditionalStoneSmelting();
    }

    private static void registerAdditionalMaterialSmelting() {
        material("juperium", ModItems.RAW_JUPERIUM, ModItems.JUPERIUM_INGOT, "jupiter_juperium_ore");
        material("saturlyte", ModItems.RAW_SATURLYTE, ModItems.SATURLYTE_INGOT, "saturn_saturlyte_ore");
        material("uranium", ModItems.RAW_URANIUM, ModItems.URANIUM_INGOT, "uranus_uranium_ore");
        material("neptunium", ModItems.RAW_NEPTUNIUM, ModItems.NEPTUNIUM_INGOT, "neptune_neptunium_ore");
        material("radium", ModItems.RAW_RADIUM, ModItems.RADIUM_INGOT, "orcus_radium_ore");
        material("plutonium", ModItems.RAW_PLUTONIUM, ModItems.PLUTONIUM_INGOT, "pluto_plutonium_ore");
        material("electrolyte", ModItems.RAW_ELECTROLYTE, ModItems.ELECTROLYTE_INGOT, "sedna_electrolyte_ore");
        material("aurorite", ModItems.RAW_AURORITE, ModItems.AURORITE_INGOT, "vicinus_aurorite_ore");
    }

    private static void registerOreSmelting() {
        smeltOre("ceres_copper_ore", ModItems.COPPER_INGOT);
        smeltOre("ceres_iron_ore", Items.IRON_INGOT);
        smeltOre("jupiter_coal_ore", Items.COAL);
        smeltOre("jupiter_diamond_ore", Items.DIAMOND);
        smeltOre("jupiter_gold_ore", Items.GOLD_INGOT);
        smeltOre("saturn_coal_ore", Items.COAL);
        smeltOre("saturn_diamond_ore", Items.DIAMOND);
        smeltOre("saturn_gold_ore", Items.GOLD_INGOT);
        smeltOre("uranus_ice_shard_ore", ModItems.FREEZE_SHARD);
        smeltOre("uranus_iron_ore", Items.IRON_INGOT);
        smeltOre("uranus_lapis_ore", new ItemStack(Items.DYE, 1, 4));
        smeltOre("uranus_diamond_ore", Items.DIAMOND);
        smeltOre("neptune_ice_shard_ore", ModItems.FREEZE_SHARD);
        smeltOre("neptune_iron_ore", Items.IRON_INGOT);
        smeltOre("neptune_copper_ore", ModItems.COPPER_INGOT);
        smeltOre("neptune_coal_ore", Items.COAL);
        smeltOre("orcus_copper_ore", ModItems.COPPER_INGOT);
        smeltOre("orcus_iron_ore", Items.IRON_INGOT);
        smeltOre("pluto_ice_shard_ore", ModItems.FREEZE_SHARD);
        smeltOre("pluto_gold_ore", Items.GOLD_INGOT);
        smeltOre("pluto_diamond_ore", Items.DIAMOND);
        smeltOre("haumea_copper_ore", ModItems.COPPER_INGOT);
        smeltOre("haumea_iron_ore", Items.IRON_INGOT);
        smeltOre("quaoar_copper_ore", ModItems.COPPER_INGOT);
        smeltOre("quaoar_iron_ore", Items.IRON_INGOT);
        smeltOre("makemake_copper_ore", ModItems.COPPER_INGOT);
        smeltOre("makemake_iron_ore", Items.IRON_INGOT);
        smeltOre("gonggong_copper_ore", ModItems.COPPER_INGOT);
        smeltOre("gonggong_iron_ore", Items.IRON_INGOT);
        smeltOre("eris_copper_ore", ModItems.COPPER_INGOT);
        smeltOre("eris_iron_ore", Items.IRON_INGOT);
        smeltOre("sedna_copper_ore", ModItems.COPPER_INGOT);
        smeltOre("sedna_iron_ore", Items.IRON_INGOT);
        smeltOre("proxima_centauri_b_iron_ore", Items.IRON_INGOT);
        smeltOre("proxima_centauri_b_redstone_ore", Items.REDSTONE);
        smeltOre("proxima_centauri_b_emerald_ore", Items.EMERALD);
        smeltOre("proxima_centauri_b_diamond_ore", Items.DIAMOND);
    }

    private static void registerAdditionalStoneSmelting() {
        for (String planet : ModBlocks.PLANETS) {
            smelt(ModBlocks.get(planet + "_cobblestone"), ModBlocks.getPlanetStone(planet));
            smelt(ModBlocks.get(planet + "_stone_bricks"),
                ModBlocks.get("cracked_" + planet + "_stone_bricks"));
        }
        smelt(ModBlocks.get("vicinus_cobblestone"), ModBlocks.get("vicinus_stone"));
        smelt(ModBlocks.get("vicinus_stone_bricks"),
            ModBlocks.get("cracked_vicinus_stone_bricks"));
    }

    private static void registerAdAstraMetalSmelting() {
        smelt(ModItems.RAW_DESH, ModItems.DESH_INGOT);
        smelt(ModItems.RAW_OSTRUM, ModItems.OSTRUM_INGOT);
        smelt(ModItems.RAW_CALORITE, ModItems.CALORITE_INGOT);

        smelt(ModBlocks.MOON_DESH_ORE, ModItems.DESH_INGOT);
        smelt(ModBlocks.DEEPSLATE_DESH_ORE, ModItems.DESH_INGOT);
        smelt(ModBlocks.MARS_OSTRUM_ORE, ModItems.OSTRUM_INGOT);
        smelt(ModBlocks.DEEPSLATE_OSTRUM_ORE, ModItems.OSTRUM_INGOT);
        smelt(ModBlocks.VENUS_CALORITE_ORE, ModItems.CALORITE_INGOT);
        smelt(ModBlocks.DEEPSLATE_CALORITE_ORE, ModItems.CALORITE_INGOT);
    }

    private static void registerPlanetaryOreSmelting() {
        smelt(ModBlocks.MOON_CHEESE_ORE, ModItems.CHEESE);

        smelt(ModBlocks.MOON_ICE_SHARD_ORE, ModItems.ICE_SHARD);
        smelt(ModBlocks.DEEPSLATE_ICE_SHARD_ORE, ModItems.ICE_SHARD);
        smelt(ModBlocks.MARS_ICE_SHARD_ORE, ModItems.ICE_SHARD);
        smelt(ModBlocks.GLACIO_ICE_SHARD_ORE, ModItems.ICE_SHARD);

        smelt(ModBlocks.VENUS_COAL_ORE, Items.COAL);
        smelt(ModBlocks.GLACIO_COAL_ORE, Items.COAL);
        smelt(ModBlocks.MARS_DIAMOND_ORE, Items.DIAMOND);
        smelt(ModBlocks.VENUS_DIAMOND_ORE, Items.DIAMOND);
        smelt(ModBlocks.MOON_IRON_ORE, Items.IRON_INGOT);
        smelt(ModBlocks.MARS_IRON_ORE, Items.IRON_INGOT);
        smelt(ModBlocks.MERCURY_IRON_ORE, Items.IRON_INGOT);
        smelt(ModBlocks.GLACIO_IRON_ORE, Items.IRON_INGOT);
        smelt(ModBlocks.GLACIO_COPPER_ORE, ModItems.COPPER_INGOT);
        smelt(ModBlocks.VENUS_GOLD_ORE, Items.GOLD_INGOT);
        smelt(ModBlocks.GLACIO_LAPIS_ORE, new ItemStack(Items.DYE, 1, EnumDyeColor.BLUE.getDyeDamage()));
    }

    private static void registerStoneSmelting() {
        smelt(ModBlocks.MOON_COBBLESTONE, ModBlocks.MOON_STONE);
        smelt(ModBlocks.MARS_COBBLESTONE, ModBlocks.MARS_STONE);
        smelt(ModBlocks.MERCURY_COBBLESTONE, ModBlocks.MERCURY_STONE);
        smelt(ModBlocks.VENUS_COBBLESTONE, ModBlocks.VENUS_STONE);
        smelt(ModBlocks.GLACIO_COBBLESTONE, ModBlocks.GLACIO_STONE);

        smelt(ModBlocks.MOON_STONE_BRICKS, ModBlocks.CRACKED_MOON_STONE_BRICKS);
        smelt(ModBlocks.MARS_STONE_BRICKS, ModBlocks.CRACKED_MARS_STONE_BRICKS);
        smelt(ModBlocks.MERCURY_STONE_BRICKS, ModBlocks.CRACKED_MERCURY_STONE_BRICKS);
        smelt(ModBlocks.VENUS_STONE_BRICKS, ModBlocks.CRACKED_VENUS_STONE_BRICKS);
        smelt(ModBlocks.GLACIO_STONE_BRICKS, ModBlocks.CRACKED_GLACIO_STONE_BRICKS);
        smelt(ModBlocks.PERMAFROST_BRICKS, ModBlocks.CRACKED_PERMAFROST_BRICKS);
        smelt(ModBlocks.VENUS_SANDSTONE_BRICKS, ModBlocks.CRACKED_VENUS_SANDSTONE_BRICKS);
    }

    private static void smelt(Block input, int meta, Item output) {
        if (input == null || output == null) {
            return;
        }
        smelt(input, meta, new ItemStack(output));
    }

    private static void smelt(Block input, int meta, ItemStack output) {
        if (input == null || output == null || output.isEmpty()) {
            return;
        }
        GameRegistry.addSmelting(new ItemStack(input, 1, meta), output, SOURCE_EXPERIENCE);
    }

    private static void smelt(Item input, Item output) {
        if (input == null || output == null) {
            return;
        }
        smelt(input, new ItemStack(output));
    }

    private static void smelt(Item input, ItemStack output) {
        if (input == null || output == null || output.isEmpty()) {
            return;
        }
        GameRegistry.addSmelting(input, output, SOURCE_EXPERIENCE);
    }

    private static void smelt(Block input, Item output) {
        if (input == null || output == null) {
            return;
        }
        smelt(input, new ItemStack(output));
    }

    private static void smelt(Block input, Block output) {
        if (input == null || output == null) {
            return;
        }
        smelt(input, new ItemStack(output));
    }

    private static void smelt(Block input, ItemStack output) {
        if (input == null || output == null || output.isEmpty()) {
            return;
        }
        GameRegistry.addSmelting(input, output, SOURCE_EXPERIENCE);
    }

    private static void material(String name, Item raw, Item ingot, String ore) {
        smelt(raw, ingot);
        smeltOre(ore, ingot);
    }

    private static void smeltOre(String blockName, Item output) {
        Block input = ModBlocks.getOre(blockName);
        if (input != null && output != null) {
            smelt(input, output);
        }
    }

    private static void smeltOre(String blockName, ItemStack output) {
        Block input = ModBlocks.getOre(blockName);
        if (input != null) {
            GameRegistry.addSmelting(input, output, SOURCE_EXPERIENCE);
        }
    }
}
