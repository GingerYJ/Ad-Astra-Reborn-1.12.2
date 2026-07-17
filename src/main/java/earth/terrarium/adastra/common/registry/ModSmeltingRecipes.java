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
        ExtendraRecipes.register();
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
