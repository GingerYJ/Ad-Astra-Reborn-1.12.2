package earth.terrarium.adastra.common.registry;

import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;

public final class ModSmeltingRecipes {

    private static final float SOURCE_EXPERIENCE = 0.1f;

    private ModSmeltingRecipes() {
    }

    public static void register() {
        registerAdAstraMetalSmelting();
        registerPlanetaryOreSmelting();
        registerCelestialOreSmelting();
        registerPlanetaryExclusiveResourceSmelting();
        registerStoneSmelting();
    }

    private static void registerAdAstraMetalSmelting() {
        smelt(ModItems.RAW_DESH, ModItems.DESH_INGOT);
        smelt(ModItems.RAW_OSTRUM, ModItems.OSTRUM_INGOT);
        smelt(ModItems.RAW_CALORITE, ModItems.CALORITE_INGOT);
        smelt(ModItems.URANIUM_FRAGMENTS, ModItems.URANIUM_INGOT);

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

    private static void registerCelestialOreSmelting() {
        // Dwarf planets and Kuiper-system resources
        smelt(ModBlocks.CERES_BLOCKS, 2, ModItems.DOLOMITE_CRYSTAL);
        smelt(ModBlocks.CERES_BLOCKS, 3, ModItems.METEORIC_IRON_FRAGMENTS);
        smelt(ModBlocks.PLUTO_BLOCKS, 2, Items.IRON_INGOT);
        smelt(ModBlocks.PLUTO_BLOCKS, 3, ModItems.SULFUR_DUST);
        smelt(ModBlocks.PLUTO_BLOCKS, 4, ModItems.URANIUM_FRAGMENTS);
        smelt(ModBlocks.HAUMEA_BLOCKS, 2, ModItems.DOLOMITE_CRYSTAL);

        // Jupiter moons
        smelt(ModBlocks.IO_BLOCKS, 8, ModItems.SULFUR_DUST);
        smelt(ModBlocks.IO_BLOCKS, 9, ModItems.VOLCANIC_SHARD);
        smelt(ModBlocks.EUROPA_BLOCKS, 5, ModItems.RAW_SILICON);
        smelt(ModBlocks.EUROPA_BLOCKS, 6, Items.IRON_INGOT);
        smelt(ModBlocks.GANYMEDE_BLOCKS, 2, ModItems.MAGNESIUM_INGOT);
        smelt(ModBlocks.GANYMEDE_BLOCKS, 3, ModItems.ILMENITE_RAW);

        // Saturn / Uranus / Neptune / Mars moons
        smelt(ModBlocks.ENCELADUS_BLOCKS, 2, Items.COAL);
        smelt(ModBlocks.TITAN_BLOCKS, 3, ModItems.SAPPHIRE);
        smelt(ModBlocks.TITAN_BLOCKS, 4, Items.EMERALD);
        smelt(ModBlocks.TITAN_BLOCKS, 5, Items.DIAMOND);
        smelt(ModBlocks.TITAN_BLOCKS, 6, Items.COAL);
        smelt(ModBlocks.TITAN_BLOCKS, 7, new ItemStack(Items.DYE, 1, EnumDyeColor.BLUE.getDyeDamage()));
        smelt(ModBlocks.TITAN_BLOCKS, 8, Items.REDSTONE);
        smelt(ModBlocks.MIRANDA_BLOCKS, 3, Items.IRON_INGOT);
        smelt(ModBlocks.MIRANDA_BLOCKS, 4, ModItems.DOLOMITE_CRYSTAL);
        smelt(ModBlocks.MIRANDA_BLOCKS, 5, Items.DIAMOND);
        smelt(ModBlocks.MIRANDA_BLOCKS, 6, Items.QUARTZ);
        smelt(ModBlocks.MIRANDA_BLOCKS, 7, ModItems.COBALT_INGOT);
        smelt(ModBlocks.MIRANDA_BLOCKS, 8, ModItems.NICKEL_INGOT);
        smelt(ModBlocks.PHOBOS_BLOCKS, 2, Items.IRON_INGOT);
        smelt(ModBlocks.PHOBOS_BLOCKS, 3, ModItems.METEORIC_IRON_FRAGMENTS);
        smelt(ModBlocks.PHOBOS_BLOCKS, 4, ModItems.NICKEL_INGOT);
        smelt(ModBlocks.PHOBOS_BLOCKS, 5, ModItems.RAW_DESH);

        // Exoplanet systems
        smelt(ModBlocks.BARNARDA_C_BLOCKS, 11, Items.IRON_INGOT);
        smelt(ModBlocks.BARNARDA_C_BLOCKS, 12, Items.GOLD_INGOT);
        smelt(ModBlocks.BARNARDA_C_BLOCKS, 13, Items.COAL);
        smelt(ModBlocks.BARNARDA_C1_BLOCKS, 3, Items.IRON_INGOT);
        smelt(ModBlocks.TAUCETI_F_BLOCKS, 5, Items.IRON_INGOT);
        smelt(ModBlocks.TAUCETI_F_BLOCKS, 6, Items.COAL);
        smelt(ModBlocks.TAUCETI_F_BLOCKS, 7, Items.GOLD_INGOT);
        smelt(ModBlocks.TAUCETI_F_BLOCKS, 8, Items.DIAMOND);
        smelt(ModBlocks.TAUCETI_F_BLOCKS, 9, new ItemStack(Items.DYE, 1, EnumDyeColor.BLUE.getDyeDamage()));

        // Raw/material convenience smelting is intentionally conservative until titanium/uranium chains are integrated.
    }

    private static void registerPlanetaryExclusiveResourceSmelting() {
        smeltPlanetaryResource("mercury", "hermium");
        smeltPlanetaryResource("glacio", "cryonite");
        smeltPlanetaryResource("ceres", "cerium");
        smeltPlanetaryResource("pluto", "plutonium");
        smeltPlanetaryResource("haumea", "haumeite");
        smeltPlanetaryResource("kuiper_belt", "kuiperite");
        smeltPlanetaryResource("io", "ionite");
        smeltPlanetaryResource("europa", "europium");
        smeltPlanetaryResource("ganymede", "ganymedite");
        smeltPlanetaryResource("callisto", "callistite");
        smeltPlanetaryResource("enceladus", "enceladite");
        smeltPlanetaryResource("titan", "titanite");
        smeltPlanetaryResource("miranda", "mirandium");
        smeltPlanetaryResource("triton", "tritonium");
        smeltPlanetaryResource("phobos", "phobium");
        smeltPlanetaryResource("barnarda_c", "barnardium");
        smeltPlanetaryResource("barnarda_c1", "c1_barnardium");
        smeltPlanetaryResource("tauceti_f", "taucetite");
        smeltPlanetaryResource("proxima_b", "proximite");
    }

    private static void smeltPlanetaryResource(String planet, String resource) {
        Item raw = Item.REGISTRY.getObject(new ResourceLocation("ad_astra", "raw_" + resource));
        Item ingot = Item.REGISTRY.getObject(new ResourceLocation("ad_astra", resource + "_ingot"));
        Block ore = Block.REGISTRY.getObject(new ResourceLocation("ad_astra", planet + "_" + resource + "_ore"));
        if (ingot == null || ingot == Items.AIR) {
            return;
        }
        if (raw != null && raw != Items.AIR) {
            smelt(raw, ingot);
        }
        if (ore != null && ore != net.minecraft.init.Blocks.AIR) {
            smelt(ore, ingot);
        }
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
        smelt(input, meta, new ItemStack(output));
    }

    private static void smelt(Block input, int meta, ItemStack output) {
        GameRegistry.addSmelting(new ItemStack(input, 1, meta), output, SOURCE_EXPERIENCE);
    }

    private static void smelt(Item input, Item output) {
        smelt(input, new ItemStack(output));
    }

    private static void smelt(Item input, ItemStack output) {
        GameRegistry.addSmelting(input, output, SOURCE_EXPERIENCE);
    }

    private static void smelt(Block input, Item output) {
        smelt(input, new ItemStack(output));
    }

    private static void smelt(Block input, Block output) {
        smelt(input, new ItemStack(output));
    }

    private static void smelt(Block input, ItemStack output) {
        GameRegistry.addSmelting(input, output, SOURCE_EXPERIENCE);
    }
}
