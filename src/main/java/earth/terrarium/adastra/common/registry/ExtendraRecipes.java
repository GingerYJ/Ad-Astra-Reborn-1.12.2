package earth.terrarium.adastra.common.registry;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;

/** Smelting and basic processing recipes for Ad Extendra materials. */
public final class ExtendraRecipes {

    private static boolean registered;

    private ExtendraRecipes() {
    }

    public static void register() {
        if (registered) {
            return;
        }
        registered = true;

        material("juperium", ExtendraItems.RAW_JUPERIUM, ExtendraItems.JUPERIUM_INGOT, "jupiter_juperium_ore");
        material("saturlyte", ExtendraItems.RAW_SATURLYTE, ExtendraItems.SATURLYTE_INGOT, "saturn_saturlyte_ore");
        material("uranium", ExtendraItems.RAW_URANIUM, ExtendraItems.URANIUM_INGOT, "uranus_uranium_ore");
        material("neptunium", ExtendraItems.RAW_NEPTUNIUM, ExtendraItems.NEPTUNIUM_INGOT, "neptune_neptunium_ore");
        material("radium", ExtendraItems.RAW_RADIUM, ExtendraItems.RADIUM_INGOT, "orcus_radium_ore");
        material("plutonium", ExtendraItems.RAW_PLUTONIUM, ExtendraItems.PLUTONIUM_INGOT, "pluto_plutonium_ore");
        material("electrolyte", ExtendraItems.RAW_ELECTROLYTE, ExtendraItems.ELECTROLYTE_INGOT, "sedna_electrolyte_ore");
        material("aurorite", ExtendraItems.RAW_AURORITE, ExtendraItems.AURORITE_INGOT, "vicinus_aurorite_ore");

        smelt("ceres_copper_ore", ModItems.COPPER_INGOT);
        smelt("ceres_iron_ore", net.minecraft.init.Items.IRON_INGOT);
        smelt("jupiter_coal_ore", net.minecraft.init.Items.COAL);
        smelt("jupiter_diamond_ore", net.minecraft.init.Items.DIAMOND);
        smelt("jupiter_gold_ore", net.minecraft.init.Items.GOLD_INGOT);
        smelt("saturn_coal_ore", net.minecraft.init.Items.COAL);
        smelt("saturn_diamond_ore", net.minecraft.init.Items.DIAMOND);
        smelt("saturn_gold_ore", net.minecraft.init.Items.GOLD_INGOT);
        smelt("uranus_ice_shard_ore", ExtendraItems.FREEZE_SHARD);
        smelt("uranus_iron_ore", net.minecraft.init.Items.IRON_INGOT);
        smelt("uranus_lapis_ore", new ItemStack(net.minecraft.init.Items.DYE, 1, 4));
        smelt("uranus_diamond_ore", net.minecraft.init.Items.DIAMOND);
        smelt("neptune_ice_shard_ore", ExtendraItems.FREEZE_SHARD);
        smelt("neptune_iron_ore", net.minecraft.init.Items.IRON_INGOT);
        smelt("neptune_copper_ore", ModItems.COPPER_INGOT);
        smelt("neptune_coal_ore", net.minecraft.init.Items.COAL);
        smelt("orcus_copper_ore", ModItems.COPPER_INGOT);
        smelt("orcus_iron_ore", net.minecraft.init.Items.IRON_INGOT);
        smelt("pluto_ice_shard_ore", ExtendraItems.FREEZE_SHARD);
        smelt("pluto_gold_ore", net.minecraft.init.Items.GOLD_INGOT);
        smelt("pluto_diamond_ore", net.minecraft.init.Items.DIAMOND);
        smelt("haumea_copper_ore", ModItems.COPPER_INGOT);
        smelt("haumea_iron_ore", net.minecraft.init.Items.IRON_INGOT);
        smelt("quaoar_copper_ore", ModItems.COPPER_INGOT);
        smelt("quaoar_iron_ore", net.minecraft.init.Items.IRON_INGOT);
        smelt("makemake_copper_ore", ModItems.COPPER_INGOT);
        smelt("makemake_iron_ore", net.minecraft.init.Items.IRON_INGOT);
        smelt("gonggong_copper_ore", ModItems.COPPER_INGOT);
        smelt("gonggong_iron_ore", net.minecraft.init.Items.IRON_INGOT);
        smelt("eris_copper_ore", ModItems.COPPER_INGOT);
        smelt("eris_iron_ore", net.minecraft.init.Items.IRON_INGOT);
        smelt("sedna_copper_ore", ModItems.COPPER_INGOT);
        smelt("sedna_iron_ore", net.minecraft.init.Items.IRON_INGOT);
        smelt("b_iron_ore", net.minecraft.init.Items.IRON_INGOT);
        smelt("b_redstone_ore", net.minecraft.init.Items.REDSTONE);
        smelt("b_emerald_ore", net.minecraft.init.Items.EMERALD);
        smelt("b_diamond_ore", net.minecraft.init.Items.DIAMOND);

        for (String planet : ExtendraBlocks.PLANETS) {
            smelt(ExtendraBlocks.get(planet + "_cobblestone"), ExtendraBlocks.getPlanetStone(planet));
            smelt(ExtendraBlocks.get(planet + "_stone_bricks"),
                ExtendraBlocks.get("cracked_" + planet + "_stone_bricks"));
        }

        // Vicinus provides building blocks and an ore, but is not a separate
        // dimension in the source project.
        smelt(ExtendraBlocks.get("vicinus_cobblestone"), ExtendraBlocks.get("vicinus_stone"));
        smelt(ExtendraBlocks.get("vicinus_stone_bricks"),
            ExtendraBlocks.get("cracked_vicinus_stone_bricks"));
    }

    private static void material(String name, Item raw, Item ingot, String ore) {
        smelt(raw, ingot);
        smelt(ExtendraBlocks.getOre(ore), ingot);
    }

    private static void smelt(String blockName, Item output) {
        smelt(ExtendraBlocks.getOre(blockName), output);
    }

    private static void smelt(Block input, Item output) {
        if (input != null && output != null) {
            GameRegistry.addSmelting(input, new ItemStack(output), 0.1F);
        }
    }

    private static void smelt(Item input, Item output) {
        if (input != null && output != null) {
            GameRegistry.addSmelting(input, new ItemStack(output), 0.1F);
        }
    }

    private static void smelt(String blockName, ItemStack output) {
        Block input = ExtendraBlocks.getOre(blockName);
        if (input != null) {
            GameRegistry.addSmelting(input, output, 0.1F);
        }
    }

    private static void smelt(Block input, Block output) {
        if (input != null && output != null) {
            GameRegistry.addSmelting(input, new ItemStack(output), 0.1F);
        }
    }
}
